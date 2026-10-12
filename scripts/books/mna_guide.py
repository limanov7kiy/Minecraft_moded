#!/usr/bin/env python3
"""Кодекс Mana and Artifice (assets/mna/guide/<lang>.json — собственный формат мода, не Patchouli).

  extract <en_us.json> <units.json>        — разложить кодекс на единицы перевода (id -> строка с разметкой)
  build   <en_us.json> <units_ru.json> <ru_ru.json> — собрать ru_ru.json из перевода
  check   <en_us.json> <ru_ru.json> [units_en.json units_ru.json] — проверить структуру и разметку (код 1 при ошибках)

Единица: `<запись>#<номер секции>`. Заголовок (title) — простая строка. Текст — строка с разметкой:
цветные/курсивные куски `⟦blue|слова⟧`, `⟦dark_purple,i|слова⟧` (i — курсив, u — подчёркивание); всё вне ⟦⟧ — без стиля;
переводы переставляют куски как нужно по-русски, но набор стилей должен совпасть. `\\n` — настоящие переводы строк, сохранять.
"""
import copy, json, re, sys, collections

MK = re.compile(r'⟦([a-z_,]+)\|(.*?)⟧', re.S)

def runs_to_markup(runs):
    out = []
    for r in runs:
        st = []
        if 'color' in r: st.append(r['color'])
        if r.get('italic'): st.append('i')
        if r.get('underlined'): st.append('u')
        out.append('⟦%s|%s⟧' % (','.join(st), r['text']) if st else r['text'])
    return ''.join(out)

def markup_to_runs(s):
    runs, pos = [], 0
    def plain(t):
        if t: runs.append({'text': t})
    for m in MK.finditer(s):
        plain(s[pos:m.start()])
        r = {'text': m.group(2)}
        for tok in m.group(1).split(','):
            if tok == 'i': r['italic'] = True
            elif tok == 'u': r['underlined'] = True
            else: r['color'] = tok
        runs.append(r); pos = m.end()
    plain(s[pos:])
    # порядок ключей как в оригинале: color, italic/underlined, text — не важен для игры
    return runs

def style_sig(s):
    return sorted(m.group(1) for m in MK.finditer(s))

def load(p):
    return json.load(open(p, encoding='utf-8'))

def units(en):
    u = {}
    for name, e in en.items():
        if not isinstance(e, dict): continue
        for i, s in enumerate(e['sections']):
            if s['type'] == 'title': u['%s#%d' % (name, i)] = s['value']
            elif s['type'] == 'text':
                u['%s#%d' % (name, i)] = runs_to_markup(s['json']) if 'json' in s else s['value']
    return u

def build(en, tr):
    ru = copy.deepcopy(en)
    for name, e in ru.items():
        if not isinstance(e, dict): continue
        for i, s in enumerate(e['sections']):
            k = '%s#%d' % (name, i)
            if k not in tr: continue
            t = tr[k]
            if s['type'] == 'title': s['value'] = t
            elif s['type'] == 'text':
                if 'json' in s:
                    s['json'] = markup_to_runs(t)
                    if 'value' in s: s['value'] = MK.sub(lambda m: m.group(2), t)
                else: s['value'] = t
    return ru

CYR = re.compile('[А-Яа-яЁё]')
def check(en, ru, uen=None, uru=None):
    err = []
    if set(en) != set(ru): err.append('наборы записей различаются')
    for name, e in en.items():
        if not isinstance(e, dict): continue
        r = ru.get(name)
        if not isinstance(r, dict): continue
        for k in e:
            if k != 'sections' and e[k] != r.get(k): err.append('%s: поле %s изменено' % (name, k))
        if len(e['sections']) != len(r['sections']): err.append('%s: число секций'% name); continue
        for i, (a, b) in enumerate(zip(e['sections'], r['sections'])):
            if a['type'] != b['type']: err.append('%s#%d: тип секции' % (name, i)); continue
            for k in a:
                if k in ('json', 'value') and a['type'] in ('title', 'text'): continue
                if a[k] != b.get(k): err.append('%s#%d: поле %s изменено' % (name, i, k))
    if uen is not None:
        for k, v in uen.items():
            t = uru.get(k)
            if t is None: err.append('%s: нет перевода' % k); continue
            if not t.strip(): err.append('%s: пусто' % k)
            if style_sig(v) != style_sig(t): err.append('%s: стили %s != %s' % (k, style_sig(v), style_sig(t)))
            if '⟦' in MK.sub('', t) or '⟧' in MK.sub('', t): err.append('%s: сломана разметка' % k)
            if v.count('\n') != t.count('\n'): err.append('%s: число переводов строк %d != %d' % (k, v.count('\n'), t.count('\n')))
            if re.search('[A-Za-z]{4,}', MK.sub(lambda m: m.group(2), v)) and not CYR.search(t): err.append('%s: нет кириллицы' % k)
    return err

if __name__ == '__main__':
    c = sys.argv[1]
    if c == 'extract':
        u = units(load(sys.argv[2])); json.dump(u, open(sys.argv[3], 'w', encoding='utf-8'), ensure_ascii=False, indent=0)
        print(len(u), 'единиц,', sum(len(v) for v in u.values()), 'знаков')
    elif c == 'build':
        en, tr = load(sys.argv[2]), load(sys.argv[3])
        json.dump(build(en, tr), open(sys.argv[4], 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
    elif c == 'check':
        en, ru = load(sys.argv[2]), load(sys.argv[3])
        uen = load(sys.argv[4]) if len(sys.argv) > 5 else None
        uru = load(sys.argv[5]) if len(sys.argv) > 5 else None
        e = check(en, ru, uen, uru)
        print('\n'.join(e[:50])); print(len(e), 'ошибок'); sys.exit(1 if e else 0)
