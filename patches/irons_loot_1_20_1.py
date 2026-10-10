#!/usr/bin/env python3
"""Iron's Spells 1.20.1-3.16.x поставляет две таблицы добычи в формате 1.21 — на 1.20.1 они не
загружаются (сундуки катакомб и цитадели пустеют). Скрипт берёт оригиналы из jar и переписывает их
в формат 1.20.1, результат кладётся в pack/kubejs/data (KubeJS перекрывает данные модов).

Использование: irons_loot_1_20_1.py <irons_spellbooks-*.jar>
"""
import json, pathlib, sys, zipfile

TABLES = ["chests/catacombs/crypt_loot", "chests/citadel/citadel_tomes"]
OUT = pathlib.Path(__file__).resolve().parent.parent / "pack/kubejs/data/irons_spellbooks/loot_tables"

def snbt_str(s):
    return "'" + s.replace("\\", "\\\\").replace("'", "\\'") + "'"

def fix(node):
    if isinstance(node, list):
        return [fix(x) for x in node]
    if not isinstance(node, dict):
        return node
    node = {k: fix(v) for k, v in node.items()}
    # 1.21: {"type": "minecraft:loot_table", "value": id}  ->  1.20.1: "name"
    if node.get("type") == "minecraft:loot_table" and "value" in node and "name" not in node:
        node["name"] = node.pop("value")
    # 1.21: set_written_book_pages  ->  1.20.1: set_nbt со страницами книги
    if node.get("function") == "minecraft:set_written_book_pages":
        pages = ",".join(snbt_str(json.dumps(p, ensure_ascii=False)) for p in node["pages"])
        node = {"function": "minecraft:set_nbt",
                "tag": f'{{title:"Citadel",author:"???",resolved:0b,pages:[{pages}]}}'}
    return node

with zipfile.ZipFile(sys.argv[1]) as jar:
    for t in TABLES:
        data = fix(json.loads(jar.read(f"data/irons_spellbooks/loot_tables/{t}.json")))
        dst = OUT / f"{t}.json"
        dst.parent.mkdir(parents=True, exist_ok=True)
        dst.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n")
        print("wrote", dst)
