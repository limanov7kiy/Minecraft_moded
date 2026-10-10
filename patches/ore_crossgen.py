#!/usr/bin/env python3
"""Ресурсы Northstar и Creating Space на планетах Ad Astra (одна Солнечная система — см. space_unify.py).

Миры Northstar и Creating Space больше не посещаются, поэтому всё, что их рецепты добывают на планетах,
должно генерироваться на планетах Ad Astra:
  * руды Northstar/Creating Space — кроме материалов, которые на этой планете уже даёт Ad Astra или
    Mekanism (мод mekanism-ad-astra-ores): так нет двух руд одного материала на одной планете;
    руда ставится и в камень Ad Astra, и в камень Northstar (исходные цели сохранены);
  * пласты камня Northstar (обычного и глубинного) — их рецепты используют сам камень, а внутри
    пластов появляются руды Northstar в «родной» текстуре;
  * жеоды лунного сапфира, гравий, гигантские грибы Венеры — готовые размещения Northstar.
Результат — pack/kubejs/data/mm_space/... (модификаторы биомов Forge + размещения).

Использование: ore_crossgen.py <папка с jar модов>
"""
import copy, glob, json, pathlib, re, sys, zipfile

MODS = pathlib.Path(sys.argv[1])
OUT = pathlib.Path(__file__).resolve().parent.parent / "pack/kubejs/data"
NS = "mm_space"

def jar(pattern):
    return zipfile.ZipFile(glob.glob(str(MODS / pattern))[0])

def write(rel, data):
    p = OUT / rel
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n")

# Планеты Ad Astra: биомы и тег заменяемого камня
PLANETS = {
    "moon": {"biomes": ["ad_astra:lunar_wastelands"], "stone": "ad_astra:moon_stone_replaceables"},
    "mars": {"biomes": ["ad_astra:martian_canyon_creek", "ad_astra:martian_polar_caps", "ad_astra:martian_wastelands"],
             "stone": "ad_astra:mars_stone_replaceables"},
    "venus": {"biomes": ["ad_astra:venus_wastelands", "ad_astra:infernal_venus_barrens"],
              "stone": "ad_astra:venus_stone_replaceables"},
    "mercury": {"biomes": ["ad_astra:mercury_deltas"], "stone": "ad_astra:mercury_stone_replaceables"},
}
# Материалы, которые на планете уже есть (Ad Astra + Mekanism Ad Astra Ores) — руды-дубли не добавляем
ALREADY = {"moon": {"iron", "osmium", "tin"}, "mars": {"iron", "diamond", "fluorite", "tin", "uranium"},
           "venus": {"coal", "diamond", "gold", "fluorite", "lead", "uranium"},
           "mercury": {"iron", "lead", "osmium", "uranium"}}

def ore_material(state):              # northstar:moon_deep_zinc_ore -> zinc
    return re.sub(r"^(moon|mars|venus|mercury)_(deep_)?", "", state.split(":")[1]).removesuffix("_ore")

def retarget(feature, stone_tag):
    """Добавляет к целям руды камень Ad Astra (ставится «поверхностный» вариант руды)."""
    f = copy.deepcopy(feature)
    targets = f["config"]["targets"]
    targets.append({"target": {"predicate_type": "minecraft:tag_match", "tag": stone_tag},
                    "state": copy.deepcopy(targets[0]["state"])})
    return f

added = {p: {"underground_ores": [], "underground_decoration": [], "vegetal_decoration": [],
             "local_modifications": [], "surface_structures": []} for p in PLANETS}
report = {p: [] for p in PLANETS}

# ---------- Northstar: руды (встроенные в placed_feature) ----------
ns = jar("Northstar-*.jar")
for name in ns.namelist():
    m = re.match(r"data/northstar/worldgen/placed_feature/((moon|mars|venus|mercury)_ore_.+)\.json$", name)
    if not m:
        continue
    pf, planet = json.loads(ns.read(name)), m.group(2)
    feat = pf["feature"]
    if not isinstance(feat, dict) or feat.get("type") != "minecraft:ore":
        continue
    material = ore_material(feat["config"]["targets"][0]["state"]["Name"])
    if material in ALREADY[planet]:
        report[planet].append(f"- {material} (Northstar, пропущено: уже есть)")
        continue
    out = {"feature": retarget(feat, PLANETS[planet]["stone"]), "placement": pf["placement"]}
    write(f"{NS}/worldgen/placed_feature/{planet}/northstar_{m.group(1)}.json", out)
    added[planet]["underground_ores"].append(f"{NS}:{planet}/northstar_{m.group(1)}")
    report[planet].append(f"+ {material} (Northstar, {m.group(1)})")

# ---------- Northstar: пласты родного камня ----------
for planet, p in PLANETS.items():
    for kind, state, count, lo, hi in (("stone", f"northstar:{planet}_stone", 6, 0, 160),
                                        ("deep_stone", f"northstar:{planet}_deep_stone", 6, -64, 16)):
        write(f"{NS}/worldgen/placed_feature/{planet}/northstar_{kind}_layer.json", {
            "feature": {"type": "minecraft:ore", "config": {"size": 64, "discard_chance_on_air_exposure": 0.0,
                        "targets": [{"target": {"predicate_type": "minecraft:tag_match", "tag": p["stone"]},
                                     "state": {"Name": state}}]}},
            "placement": [{"type": "minecraft:count", "count": count}, {"type": "minecraft:in_square"},
                          {"type": "minecraft:height_range", "height": {"type": "minecraft:uniform",
                           "min_inclusive": {"absolute": lo}, "max_inclusive": {"absolute": hi}}},
                          {"type": "minecraft:biome"}]})
        # пласты должны лечь раньше руд, чтобы руды Northstar находили свой камень
        added[planet]["underground_ores"].insert(0, f"{NS}:{planet}/northstar_{kind}_layer")
    report[planet].append(f"+ пласты northstar:{planet}_stone / {planet}_deep_stone")

# ---------- Northstar: готовые размещения (жеоды, гравий, грибы Венеры) ----------
DECOR = {"moon": {"local_modifications": ["northstar:moon_lunar_sapphire_geode"]},
         "mars": {"surface_structures": ["northstar:mars_gravel_pile"]},
         "venus": {"vegetal_decoration": ["northstar:huge_bloom_fungus_surface", "northstar:huge_spike_fungus_surface",
                                          "northstar:huge_plate_fungus", "northstar:huge_tower_fungus",
                                          "northstar:bloom_fungus", "northstar:plate_fungus", "northstar:spike_fungus",
                                          "northstar:tower_fungus"],
                   "surface_structures": ["northstar:venus_gravel_cluster"]}}
for planet, steps in DECOR.items():
    for step, feats in steps.items():
        added[planet][step] += feats
        report[planet] += [f"+ {f}" for f in feats]

# ---------- Creating Space: руды Луны ----------
cs = jar("creatingspace-*.jar")
for name in cs.namelist():
    m = re.match(r"data/creatingspace/worldgen/placed_feature/(moon_(.+)_ore)\.json$", name)
    if not m:
        continue
    pf = json.loads(cs.read(name))
    cf_id = pf["feature"].split(":")[1]
    cf = json.loads(cs.read(f"data/creatingspace/worldgen/configured_feature/{cf_id}.json"))
    out = {"feature": retarget(cf, PLANETS["moon"]["stone"]), "placement": pf["placement"]}
    if not any(x.get("type") == "minecraft:biome" for x in out["placement"]):
        out["placement"].append({"type": "minecraft:biome"})
    write(f"{NS}/worldgen/placed_feature/moon/creatingspace_{m.group(1)}.json", out)
    added["moon"]["underground_ores"].append(f"{NS}:moon/creatingspace_{m.group(1)}")
    report["moon"].append(f"+ {m.group(2)} (Creating Space)")

# ---------- Модификаторы биомов ----------
for planet, steps in added.items():
    for step, feats in steps.items():
        if feats:
            write(f"{NS}/forge/biome_modifier/{planet}_{step}.json", {
                "type": "forge:add_features", "biomes": PLANETS[planet]["biomes"],
                "features": feats, "step": step})

for planet, lines in report.items():
    print(f"[{planet}]"); print("\n".join("  " + l for l in lines))
