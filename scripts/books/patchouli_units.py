#!/usr/bin/env python3
"""Единицы перевода для Patchouli-книг с «сырым» текстом в файлах en_us (не в lang-ключах).

  extract <en_us папка книги> <units.json>                 — id (`файл::путь`) -> строка
  apply   <en_us папка> <units_ru.json> <ru_ru папка>      — копия всех json книги с подставленным переводом
  check   <units_en.json> <units_ru.json>                  — теги $(..), #плейсхолдеры#, кириллица (код 1 при ошибках)
Переводятся строки под ключами text/title/heading/description/subtitle/tooltip/header, кроме служебных
(`#heading`, `#auto_text`, `#item`: начинаются с # и без пробелов) и значений, которые являются lang-ключами.
"""
import json, pathlib, re, sys, collections

KEYS = {'name', 'text', 'title', 'heading', 'description', 'subtitle', 'tooltip', 'header', 'caption'}
TAG = re.compile(r'\$\([^)]*\)|#\w+#|%[sd]|\{\d\}')
CYR = re.compile('[А-Яа-яЁё]')

def translatable(k, v):
    return k in KEYS and isinstance(v, str) and not re.fullmatch(r'#\S*', v) and re.search('[A-Za-z]{2}', v) \
        and not re.fullmatch(r'[a-z0-9_]+(\.[a-z0-9_]+)+', v)

def walk(o, path, out):
    if isinstance(o, dict):
        for k, v in o.items():
            if translatable(k, v): out[path + '/' + k] = v
            else: walk(v, path + '/' + k, out)
    elif isinstance(o, list):
        for i, v in enumerate(o): walk(v, '%s/%d' % (path, i), out)

def extract(root):
    root = pathlib.Path(root); u = {}
    for f in sorted(root.rglob('*.json')):
        walk(json.loads(f.read_text(encoding='utf-8')), '', d := {})
        for p, v in d.items(): u['%s::%s' % (f.relative_to(root).as_posix(), p)] = v
    return u

def apply(root, tr, out):
    root, out = pathlib.Path(root), pathlib.Path(out)
    for f in sorted(root.rglob('*.json')):
        rel = f.relative_to(root).as_posix()
        o = json.loads(f.read_text(encoding='utf-8'))
        for k, v in tr.items():
            fr, _, p = k.partition('::')
            if fr != rel: continue
            parts = p.strip('/').split('/'); t = o
            for s in parts[:-1]: t = t[int(s)] if isinstance(t, list) else t[s]
            t[parts[-1]] = v
        dst = out / rel; dst.parent.mkdir(parents=True, exist_ok=True)
        dst.write_text(json.dumps(o, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')

def check(uen, uru):
    err = []
    for k, v in uen.items():
        t = uru.get(k)
        if t is None: err.append('%s: нет перевода' % k); continue
        if collections.Counter(TAG.findall(v)) != collections.Counter(TAG.findall(t)):
            err.append('%s: теги %s != %s' % (k, sorted(TAG.findall(v)), sorted(TAG.findall(t))))
        if re.search('[A-Za-z]{4,}', TAG.sub('', v)) and not CYR.search(t): err.append('%s: нет кириллицы' % k)
    return err

if __name__ == '__main__':
    c = sys.argv[1]
    if c == 'extract':
        u = extract(sys.argv[2]); json.dump(u, open(sys.argv[3], 'w', encoding='utf-8'), ensure_ascii=False, indent=0)
        print(len(u), 'единиц,', sum(map(len, u.values())), 'знаков')
    elif c == 'apply':
        apply(sys.argv[2], json.load(open(sys.argv[3], encoding='utf-8')), sys.argv[4])
    elif c == 'check':
        e = check(json.load(open(sys.argv[2], encoding='utf-8')), json.load(open(sys.argv[3], encoding='utf-8')))
        print('\n'.join(e[:60])); print(len(e), 'ошибок'); sys.exit(1 if e else 0)
