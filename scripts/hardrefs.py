#!/usr/bin/env python3
"""Ищет рецепты, которые требуют конкретный предмет по id (а не по тегу).
Нужен перед объединением дублей: если рецепт требует mod:steel_ingot по имени, а Almost Unified
перестанет выдавать этот предмет, рецепт станет невыполнимым.
Использование: hardrefs.py <папка с jar> <item_id> [item_id ...]"""
import collections, glob, json, re, sys, zipfile

pat = re.compile(r'"item":\s*"([a-z_0-9.]+:[a-z_0-9/.]+)"')
targets = set(sys.argv[2:])
hits = collections.defaultdict(set)
for j in glob.glob(sys.argv[1] + "/*.jar"):
    try:
        z = zipfile.ZipFile(j)
    except zipfile.BadZipFile:
        continue
    for n in z.namelist():
        if "/recipes/" not in n or not n.endswith(".json"):
            continue
        try:
            d = json.loads(z.read(n))
        except (ValueError, UnicodeDecodeError):
            continue
        if not isinstance(d, dict):
            continue
        ing = {k: v for k, v in d.items() if k not in ("result", "results", "output", "outputs")}
        for i in pat.findall(json.dumps(ing)):
            if i in targets:
                hits[i].add(n)
for k, v in sorted(hits.items()):
    print(f"{k}  <- {len(v)} рецепт(ов): " + ", ".join(sorted(v)[:4]))
