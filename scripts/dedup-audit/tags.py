# Статическая сборка item-тегов: data/*/tags/items из jar модов, Minecraft и Forge, + pack/kubejs/data. Вложенные #теги раскрываются.
import zipfile, glob, json, collections, os, re
S = os.environ['MM_SERVER']   # папка тестового сервера (scripts/test-server.sh)
srcs = sorted(glob.glob(S + '/mods/*.jar')) + glob.glob(S + '/libraries/net/minecraft/server/*/server-*-extra.jar') + \
    glob.glob(S + '/libraries/net/minecraftforge/forge/*/forge-*-universal.jar')
raw = collections.defaultdict(list)
pat = re.compile(r'^data/([^/]+)/tags/items/(.+)\.json$')
def add(name, txt):
    try: j = json.loads(txt)
    except Exception: return
    if j.get('replace'): raw[name] = []
    for v in j.get('values', []):
        raw[name].append(v['id'] if isinstance(v, dict) else v)
for j in srcs:
    try: z = zipfile.ZipFile(j)
    except Exception: continue
    for n in z.namelist():
        m = pat.match(n)
        if m: add(m.group(1) + ':' + m.group(2), z.read(n))
kd = os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../pack/kubejs/data')
for root, _, fs in os.walk(kd):
    for f in fs:
        p = os.path.join(root, f); m = pat.match(os.path.relpath(p, kd + '/..').replace('\\', '/'))
        if m: add(m.group(1) + ':' + m.group(2), open(p, encoding='utf-8').read())
res = {}
def expand(t, stack=()):
    if t in res: return res[t]
    if t in stack: return set()
    out = set()
    for v in raw.get(t, []):
        out |= expand(v[1:], stack + (t,)) if v.startswith('#') else {v}
    res[t] = out; return out
for t in list(raw): expand(t)
item_tags = collections.defaultdict(list)
for t, ids in res.items():
    for i in ids: item_tags[i].append(t)
json.dump(item_tags, open(os.environ.get('MM_AUDIT', '.') + '/item_tags.json', 'w'))
print(len(res), 'tags,', len(item_tags), 'items with tags; steel:', sorted(item_tags.get('mekanism:ingot_steel', []))[:6])
