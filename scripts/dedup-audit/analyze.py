import json, re, collections, sys

import os
D = os.environ.get('MM_AUDIT', '.').rstrip('/') + '/'
items = {i['id']: i for i in json.load(open(D + 'mm_dump_items.json'))['items']}
recipes = [r for r in json.load(open(D + 'mm_dump_recipes.json'))['recipes'] if r['out'] not in ('?', 'minecraft:air')]
item_tags = json.load(open(D + 'item_tags.json'))
for i in items.values():
    i['tags'] = item_tags.get(i['id'], [])
tagmembers = collections.defaultdict(list)
for i in items.values():
    for t in i['tags']:
        tagmembers[t].append(i['id'])
# базовые ресурсы: слитки, самоцветы, пыль, руды, краски, стекло, камень, дерево, шерсть… — их цену не раскручиваем
BASE = re.compile(r'^(forge:(ingots|gems|raw_materials|dusts|ores|dyes|glass|glass_panes|stone|cobblestone|sand|gravel|slimeballs|string|leather|feathers|rods/wooden|obsidian|gunpowder|bones|ender_pearls|crops|seeds|eggs|mushrooms)(/.*)?|minecraft:(logs|planks|wool|coals|sand|stone_crafting_materials|leaves))$')
NUGGET = re.compile(r'^forge:nuggets(/.*)?$'); BLOCK = re.compile(r'^forge:storage_blocks(/.*)?$')
def base_cost(i):
    ts = items[i]['tags'] if i in items else []
    if any(NUGGET.match(t) for t in ts): return 0.11
    if any(BLOCK.match(t) for t in ts): return 9.0
    if any(BASE.match(t) for t in ts): return 1.0
    return None

def parse_ing(s):
    """→ ('item', id) | ('tag', tag) | ('alt', [ids]) | None"""
    if s.startswith('['):
        ids = [x for x in s[1:-1].split('|') if x]
        return ('alt', ids) if ids else None
    try:
        j = json.loads(s)
    except Exception:
        return None
    if isinstance(j, list):
        ids = []
        for e in j:
            if isinstance(e, dict) and 'item' in e: ids.append(e['item'])
            elif isinstance(e, dict) and 'tag' in e: ids += tagmembers.get(e['tag'], [])
        return ('alt', ids) if ids else None
    if isinstance(j, dict):
        if 'item' in j: return ('item', j['item'])
        if 'tag' in j: return ('tag', j['tag'])
        for k in ('ingredient', 'base'):
            if k in j: return parse_ing(json.dumps(j[k]))
    return None

for r in recipes:
    r['ings'] = [p for p in (parse_ing(s) for s in r['in']) if p]

by_out = collections.defaultdict(list)
for r in recipes:
    by_out[r['out']].append(r)

# сложность: «сырьё» (нет рецепта) = 1; предмет = 1 этап + сумма ингредиентов / выход
INF = 1e9
cost = {}
for i in items:
    b = base_cost(i)
    cost[i] = b if b is not None else (INF if i in by_out else 1.0)
fixed = {i for i in items if base_cost(i) is not None}
best = {}
has_known = {o: any(r['ings'] for r in rs) for o, rs in by_out.items()}
SKIP_TYPES = re.compile(r'painting|pigment|almostunified|unpacking|decompact|recycl|disassembl')

def ing_cost(p):
    kind, v = p
    if kind == 'item': return cost.get(v, 1.0)
    ids = tagmembers.get(v, []) if kind == 'tag' else v
    return min((cost.get(x, 1.0) for x in ids), default=1.0)

for _ in range(40):
    changed = False
    for out, rs in by_out.items():
        if out in fixed: continue
        for r in rs:
            if SKIP_TYPES.search(r['type']):
                continue
            if not r['ings'] and has_known[out]:
                continue
            if any(p[0] == 'item' and p[1] == out for p in r['ings']):
                continue
            # ингредиенты не выгрузились (sequenced assembly, часть машин) — условно 5 единиц, помечаем «≈»
            c = 1 + (sum(ing_cost(p) for p in r['ings']) if r['ings'] else 5.0) / max(1, r.get('n', 1))
            if c < cost.get(out, INF) - 1e-9:
                cost[out] = c; best[out] = r; changed = True
    if not changed:
        break

def tree(item, seen=None, depth=0):
    """→ (глубина, множество типов рецептов) по лучшему рецепту"""
    seen = seen or set()
    r = None if item in fixed else best.get(item)
    if not r or item in seen or depth > 12:
        return 0, set()
    seen = seen | {item}
    d, types = 0, {r['type']}
    for kind, v in r['ings']:
        if kind == 'item': sub = v
        else:
            ids = tagmembers.get(v, []) if kind == 'tag' else v
            sub = min(ids, key=lambda x: cost.get(x, 1.0), default=None)
        if sub:
            sd, st = tree(sub, seen, depth + 1); d = max(d, sd); types |= st
    return d + 1, types

# использование в чужих рецептах (по id и по тегу)
uses_id = collections.Counter(); uses_tag = collections.Counter()
for r in recipes:
    seen = set()
    for kind, v in r['ings']:
        if kind == 'item' and v not in seen: uses_id[v] += 1; seen.add(v)
        elif kind == 'tag':
            for x in tagmembers.get(v, []):
                if x not in seen: uses_tag[x] += 1; seen.add(x)

def info(i):
    c = cost.get(i, 1.0)
    d, types = tree(i)
    st = sorted({t.split(':')[0] + ':' + t.split(':')[1].split('/')[0] for t in types if not t.startswith('minecraft:crafting')})
    approx = bool(best.get(i) and not best[i]['ings'])
    return {'id': i, 'name': items[i]['name'], 'approx': approx, 'cost': None if c >= INF else round(c, 1), 'depth': d, 'stations': st,
            'recipes': len(by_out.get(i, [])), 'uses_id': uses_id[i], 'uses_tag': uses_tag[i]}

json.dump({'cost': {k: v for k, v in cost.items() if v < INF}}, open(D + 'cost.json', 'w'))
if __name__ == '__main__':
    for q in sys.argv[1:]:
        print(info(q))
