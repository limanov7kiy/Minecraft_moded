#!/usr/bin/env python3
"""Собирает все китайские/японские СТРОКИ-ЛИТЕРАЛЫ, которые мод может показать игроку не через lang: константы в .class (Component.literal),
текст компонентов в NBT (структуры, лут: sign/lore/CustomName) и в data-JSON (лут-таблицы, рецепты). Результат — пачки для перевода
(тот же формат, что у lang_residual.py: {"ns": {строка: строка}}) и общий словарь. Использование:
  cjk_literals_harvest.py <папка с jar> <выход-папка> [--size 150]
Ключ словаря — точная строка, как она придёт в LiteralContents на клиенте (миксин ru-lang подставит перевод)."""
import collections, gzip, io, json, pathlib, re, struct, sys, zipfile
sys.path.insert(0, str(pathlib.Path(__file__).parent))
from cjk_nonlang_audit_lib import class_strings, nbt_json_strings, CJK_STRICT  # noqa: E402

SKIP_PATH = re.compile(r"(zh_cn|zh_tw|zh_hk|ja_jp|ko_kr|/lang/|\.animation\.|patchouli_books|/models/|/textures/|/sounds|/particles|/geo/|/font/|/atlases/)")
SKIP_JARS = re.compile(r"^(ysm|tacz|jei|mna-forge)", re.I)   # ysm: модели игроков; tacz: гунпаки со своими lang; mna: данные без показа
size = int(sys.argv[sys.argv.index("--size") + 1]) if "--size" in sys.argv else 150
jars, out = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2]); out.mkdir(parents=True, exist_ok=True)
found = collections.OrderedDict()   # строка -> {"jars": set}


def add(s, jar):
    s = s.strip("\x00")
    if s and CJK_STRICT.search(s):
        found.setdefault(s, set()).add(jar)


def walk_json(o, jar):
    if isinstance(o, str):
        # строка может быть JSON-компонентом ('{"text":"..."}')
        if o.startswith(("{", "[")) and '"' in o:
            try: walk_json(json.loads(o), jar); return
            except Exception: pass
        add(o, jar)
    elif isinstance(o, dict):
        for k, v in o.items(): walk_json(v, jar)
    elif isinstance(o, list):
        for v in o: walk_json(v, jar)


for j in sorted(jars.glob("*.jar")):
    if SKIP_JARS.match(j.name): continue
    try: z = zipfile.ZipFile(j)
    except zipfile.BadZipFile: continue
    for n in z.namelist():
        if n.endswith("/") or SKIP_PATH.search(n): continue
        ext = n.rsplit(".", 1)[-1].lower()
        try:
            if ext == "class":
                for s in class_strings(z.read(n)): add(s, j.name)
            elif ext == "nbt":
                for s in nbt_json_strings(z.read(n)): add(s, j.name)
            elif ext == "json" and n.startswith("data/"):
                walk_json(json.loads(z.read(n).decode("utf-8-sig", "ignore"), strict=False), j.name)
            elif ext == "mcfunction":
                for s in re.findall(r'"([^"\\\\]*[\\u3040-\\u9fff\\uac00-\\ud7af][^"\\\\]*)"', z.read(n).decode("utf-8", "ignore")): add(s, j.name)
        except Exception:
            pass
items = list(found.items())
# пачки по размеру; ключ внутри пачки — сама строка (значение — она же: исходник для переводчика)
batches = [items[i:i + size] for i in range(0, len(items), size)]
for i, b in enumerate(batches, 1):
    (out / f"{i:03d}.json").write_text(json.dumps({"literals": {s: s for s, _ in b}}, ensure_ascii=False, indent=0))
(out / "sources.json").write_text(json.dumps({s: sorted(js) for s, js in items}, ensure_ascii=False, indent=0))
by = collections.Counter(j for _, js in items for j in js)
print(f"уникальных строк: {len(items)}, пачек: {len(batches)}; по jar: {by.most_common(8)}")
