# Переопределения лута и достижений для убранных логических дублей (docs/dedup.md): ищет в jar сервера таблицы лута
# (data/*/loot_tables/**, кроме blocks/) и достижения (data/*/advancements/**, кроме recipes/), где встречается убранный предмет из
# global.mmDedup, и кладёт копию с заменой на оставшийся в pack/kubejs/data/<мод>/... (датапак KubeJS важнее jar).
# Использование: MM_SERVER=<папка тестового сервера> python3 -I scripts/dedup-audit/overrides.py [--dry]
import glob, os, re, sys, zipfile
root = os.path.join(os.path.dirname(os.path.abspath(__file__)), '../..')
src = open(os.path.join(root, 'pack/kubejs/startup_scripts/dedup_list.js'), encoding='utf-8').read()
body = src.split('global.mmDedup = {', 1)[1].split('\n}', 1)[0]
mapping = dict(re.findall(r"'([a-z0-9_.-]+:[a-z0-9_/.-]+)': '([a-z0-9_.-]+:[a-z0-9_/.-]+)'", body))
pat = re.compile('"(' + '|'.join(re.escape(k) for k in mapping) + ')"')
want = re.compile(r'^data/[^/]+/(loot_tables|advancements)/.+\.json$')
dry = '--dry' in sys.argv
for j in sorted(glob.glob(os.environ['MM_SERVER'] + '/mods/*.jar')):
    try: z = zipfile.ZipFile(j)
    except zipfile.BadZipFile: continue
    for n in z.namelist():
        # выпадение самого блока не трогаем: уже стоящий в мире блок должен выпадать собой
        if not want.match(n) or '/advancements/recipes/' in n or '/loot_tables/blocks/' in n: continue
        txt = z.read(n).decode('utf-8', 'replace')
        if not pat.search(txt): continue
        new = pat.sub(lambda m: '"' + mapping[m.group(1)] + '"', txt)
        out = os.path.join(root, 'pack/kubejs', n)
        print(('[dry] ' if dry else '') + os.path.relpath(out, root), sorted(set(pat.findall(txt))))
        if not dry:
            os.makedirs(os.path.dirname(out), exist_ok=True)
            open(out, 'w', encoding='utf-8').write(new)
