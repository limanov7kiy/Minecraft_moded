#!/usr/bin/env python3
"""Create: Northstar - Structures 1.0.0 (Forge) × Northstar Redux 0.6+.

Аддон переопределяет 7 лунных биомов Northstar своими копиями, сделанными под старый Northstar:
размещённые фичи `northstar:moon_<камень>_blob` в Redux 0.6 переименованы в `northstar:moon_blob_<камень>`,
и мир не создаётся (`Unbound values in registry ... placed_feature`). Скрипт собирает биомы заново:
  * основа — биом из самого Northstar Redux (актуальные спавны мобов, эффекты, осадки);
  * фичи — списки аддона (его «защищённые» от бассейна Эйткена кристаллы, гейзеры и т.п. и
    `aitken_basin_cleanup`); старые имена переводятся в имена Redux, отсутствующих нет;
  * добавляются фичи Redux, которых у аддона нет совсем (иней, малые ледяные залежи), кроме старых
    кратеров `moon_crater*` — их аддон заменил структурами.
Кроме того, кратеры (структуры аддона) разрешены и на Луне Ad Astra (`ad_astra:lunar_wastelands`) —
туда летают ракеты сборки (patches/space_unify.py); дно, вал и засыпка кратеров — песок и камень
Луны Ad Astra.

Результат — pack/kubejs/data (KubeJS загружает поверх данных модов).
Использование: northstar_structures_fix.py <папка с jar модов>
"""
import glob, json, pathlib, re, sys, zipfile

MODS = pathlib.Path(sys.argv[1])
OUT = pathlib.Path(__file__).resolve().parent.parent / "pack/kubejs/data"

def jar(pattern):
    return zipfile.ZipFile(glob.glob(str(MODS / pattern))[0])

def write(rel, data):
    p = OUT / rel
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n")
    print("wrote", rel)

ns, nss = jar("Northstar-*.jar"), jar("northstar_structures-*.jar")

def ids(z, ns_name, kind):
    pre = f"data/{ns_name}/worldgen/{kind}/"
    return {f"{ns_name}:{n[len(pre):-5]}" for n in z.namelist() if n.startswith(pre) and n.endswith(".json")}

placed = ids(ns, "northstar", "placed_feature") | ids(nss, "northstar_structures", "placed_feature") \
       | ids(nss, "northstar", "placed_feature")

def key(f):
    """Общее имя фичи у аддона и Redux: moon_X_blob == moon_blob_X, moon_lunar_sapphire_geode == lunar_sapphire_geode."""
    n = f.split(":", 1)[1]
    n = re.sub(r"^moon_(.+)_blob(_large|_small)?$", r"moon_blob_\1\2", n)
    return n.removeprefix("moon_") if n.endswith("geode") else n

def fix(f):
    if f in placed:
        return f
    alt = "northstar:" + key(f)
    if alt in placed:
        return alt
    print("  drop", f)

pre = "data/northstar/worldgen/biome/"
for name in sorted(n for n in nss.namelist() if n.startswith(pre) and n.endswith(".json")):
    addon, base = json.loads(nss.read(name)), json.loads(ns.read(name))
    steps = [[g for g in map(fix, step) if g] for step in addon["features"]]
    steps += [[] for _ in range(len(base["features"]) - len(steps))]
    have = {key(f) for step in steps for f in step}
    for i, step in enumerate(base["features"]):
        steps[i] += [f for f in step if key(f) not in have and "crater" not in key(f)]
    base["features"] = steps
    write(name[len("data/"):], base)

# Кратеры — и на Луне Ad Astra
tag = "northstar_structures/tags/worldgen/biome/crater_moon_biomes.json"
write(tag, {"replace": False, "values": ["ad_astra:lunar_wastelands"]})
pre = "data/northstar_structures/worldgen/structure/"
for name in sorted(n for n in nss.namelist() if n.startswith(pre) and n.endswith(".json")):
    s = json.loads(nss.read(name))
    blocks = {k: v.replace("northstar:moon_", "ad_astra:moon_") for k, v in s.items()
              if k.endswith("_block") and v in ("northstar:moon_sand", "northstar:moon_stone")}
    if blocks:
        s.update(blocks)
        write(name[len("data/"):], s)
