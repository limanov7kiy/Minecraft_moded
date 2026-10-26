# Теги для global.mmDedupTags: каких item-тегов убранного предмета нет у оставшегося (по статической сборке тегов tags.py).
# Использование: MM_AUDIT=<папка с item_tags.json> python3 -I scripts/dedup-audit/tagcopy.py → вывод вставить в dedup_list.js
import json, os, re
D = os.environ.get('MM_AUDIT', '.')
t = json.load(open(D + '/item_tags.json'))
src = open(os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../pack/kubejs/startup_scripts/dedup_list.js'), encoding='utf-8').read()
body = src.split('global.mmDedup = {', 1)[1].split('\n}', 1)[0]
need = {}
for gone, kept in re.findall(r"'([a-z0-9_.-]+:[a-z0-9_/.-]+)': '([a-z0-9_.-]+:[a-z0-9_/.-]+)'", body):
    miss = sorted(set(t.get(gone, [])) - set(t.get(kept, [])))
    if miss:
        need.setdefault(kept, set()).update(miss)
print('global.mmDedupTags = {')
print(',\n'.join(f"  '{k}': [{', '.join(repr(x) for x in sorted(v))}]" for k, v in sorted(need.items())))
print('}')
