#!/usr/bin/env python3
"""Одна Солнечная система на три космических мода: планеты и орбиты — от Ad Astra.

Northstar и Creating Space описывают пункты назначения ракет данными. Скрипт берёт их исходные файлы
из jar, перенаправляет в измерения Ad Astra и пишет переопределения в pack/kubejs/data
(KubeJS загружает их поверх данных модов). Окружающая среда (кислород, гравитация) — одна, Ad Astra:
  * у Northstar в перенаправленных записях убрана атмосфера (по умолчанию — пригодная для дыхания):
    кислород — забота Ad Astra; скафандры Northstar/Creating Space тратят воздух из своего баллона
    (мод mods-src/mm-compat);
  * гравитация — реальная, от Northstar (по ней же он считает тягу ракет); у Ad Astra на тех же
    планетах и орбитах гравитация нейтральная (9.807 — множитель 1), иначе миксины двух модов
    перемножили бы её. Глацио (до неё Northstar не летает) — гравитация Ad Astra;
  * температура Northstar сохранена (от неё зависят его механизмы), поэтому скафандры всех трёх
    модов связаны тегами: любой полный космический комплект защищает в обеих системах;
  * Creating Space сам не трогает гравитацию в мирах ad_astra:*, а кислород проверяет только в своих
    биомах (тег creatingspace:no_oxygen), так что в мирах Ad Astra действует одна система.

Использование: space_unify.py <папка с jar модов>
"""
import glob, json, pathlib, sys, zipfile

MODS = pathlib.Path(sys.argv[1])
OUT = pathlib.Path(__file__).resolve().parent.parent / "pack/kubejs/data"

def jar(pattern):
    return zipfile.ZipFile(glob.glob(str(MODS / pattern))[0])

def write(rel, data):
    p = OUT / rel
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n")
    print("wrote", rel)

# ---------- Northstar: planet_dimension -> измерения Ad Astra ----------
NS_DIMS = {f"northstar:{n}": f"ad_astra:{n}" for n in
           ["earth_orbit", "moon", "mars", "mars_orbit", "mercury", "mercury_orbit", "venus", "venus_orbit"]}

def remap(v, table):
    if isinstance(v, str):
        return table.get(v, v)
    if isinstance(v, list):
        return [remap(x, table) for x in v]
    if isinstance(v, dict):
        return {table.get(k, k): remap(x, table) for k, x in v.items()}
    return v

ns = jar("Northstar-*.jar")
for name in ["earth", "earth_orbit", "moon", "mars", "mars_orbit", "mercury", "mercury_orbit", "venus", "venus_orbit"]:
    d = json.loads(ns.read(f"data/northstar/northstar/planet_dimension/{name}.json"))
    for key in ("dimension", "dimension_above", "dimension_below"):
        if key in d:
            d[key] = NS_DIMS.get(d[key], d[key])
    d.pop("atmosphere", None)                 # кислород — забота Ad Astra
    if name == "moon":
        d["dimension_above"] = "ad_astra:moon_orbit"
    write(f"northstar/northstar/planet_dimension/{name}.json", d)

# Орбиты Луны у Northstar нет, у Ad Astra есть — описываем её для Northstar по образцу земной орбиты
moon_orbit = json.loads(ns.read("data/northstar/northstar/planet_dimension/earth_orbit.json"))
moon_orbit.pop("atmosphere", None)
moon_orbit.update(dimension="ad_astra:moon_orbit", dimension_below="ad_astra:moon", planet="northstar:the_moon")
write("northstar/northstar/planet_dimension/moon_orbit.json", moon_orbit)

# ---------- Ad Astra: нейтральная гравитация там, где её задаёт Northstar ----------
aa = jar("ad_astra-forge-*.jar")
for name in ["moon", "mars", "venus", "mercury", "earth_orbit", "moon_orbit", "mars_orbit", "venus_orbit", "mercury_orbit"]:
    d = json.loads(aa.read(f"data/ad_astra/planets/{name}.json"))
    d["gravity"] = 9.807
    write(f"ad_astra/planets/{name}.json", d)

# ---------- Creating Space: граф перелётов -> измерения Ad Astra ----------
CS_DIMS = {"creatingspace:earth_orbit": "ad_astra:earth_orbit", "creatingspace:moon_orbit": "ad_astra:moon_orbit",
           "creatingspace:the_moon": "ad_astra:moon", "creatingspace:mars_orbit": "ad_astra:mars_orbit",
           "creatingspace:mars": "ad_astra:mars", "creatingspace:venus": "ad_astra:venus"}
cs = jar("creatingspace-*.jar")
base = "creatingspace/rocket_accessible_dimension"
over = json.loads(cs.read(f"data/minecraft/{base}/overworld.json"))
write(f"minecraft/{base}/overworld.json", remap(over, CS_DIMS))
nodes = {}
for old, new in CS_DIMS.items():
    ns_, path = old.split(":")
    d = json.loads(cs.read(f"data/{ns_}/{base}/{path}.json"))
    nodes[new] = remap(d, CS_DIMS)
    # старый узел оставляем без связей — Creating Space сам выкинет его как недостижимый
    write(f"{ns_}/{base}/{path}.json", dict(d, adjacentDimensions={}))

# Исправления физики: у Creating Space Венера с гравитацией Луны (1.6) — ставим реальные 8.87
nodes["ad_astra:venus"]["gravity"] = 8.87

def link(a, b, dv):
    nodes[a]["adjacentDimensions"][b] = {"deltaV": dv}
    nodes[b]["adjacentDimensions"][a] = {"deltaV": dv}

# Новые узлы: Меркурий и Глацио (их у Creating Space не было). Δv — в масштабе самого Creating Space:
# Земная орбита→Венера у него 2200, до Меркурия по-настоящему примерно втрое дороже, межзвёздный перелёт — дороже всего.
for dim, grav, body, dist, height in (("ad_astra:mercury_orbit", 0, "ad_astra:mercury", 100, 64),
                                      ("ad_astra:mercury", 3.7, "sun", 600, 200),
                                      ("ad_astra:glacio_orbit", 0, "ad_astra:glacio", 100, 64),
                                      ("ad_astra:glacio", 3.721, "proxima_centauri", 50000, 200)):
    nodes[dim] = {"adjacentDimensions": {}, "arrivalHeight": height, "gravity": grav,
                  "orbitedBody": body, "distanceToOrbitingBody": dist}
link("ad_astra:earth_orbit", "ad_astra:mercury_orbit", 6500)
link("ad_astra:mercury_orbit", "ad_astra:mercury", 1500)
link("ad_astra:earth_orbit", "ad_astra:glacio_orbit", 20000)
link("ad_astra:glacio_orbit", "ad_astra:glacio", 1200)
for dim, d in nodes.items():
    new_ns, new_path = dim.split(":")
    write(f"{new_ns}/{base}/{new_path}.json", d)

# ---------- Скафандры: любой полный комплект защищает в обеих системах ----------
NS_IRON = [f"northstar:iron_space_suit_{p}" for p in ("helmet", "chestpiece", "leggings", "boots")]
NS_MARTIAN = [f"northstar:martian_steel_space_suit_{p}" for p in ("helmet", "chestpiece", "leggings", "boots")]
CS_BASIC = ["creatingspace:basic_spacesuit_helmet", "creatingspace:copper_oxygen_backtank",
            "creatingspace:basic_spacesuit_leggings", "creatingspace:basic_spacesuit_boots"]
CS_ADV = ["creatingspace:advanced_spacesuit_helmet", "creatingspace:netherite_oxygen_backtank",
          "creatingspace:advanced_spacesuit_leggings", "creatingspace:advanced_spacesuit_boots"]

def tag(rel, values):
    write(f"{rel}.json", {"replace": False, "values": [{"id": v, "required": False} for v in values]})

# Ad Astra: кислород чужих скафандров считает мод mm_compat (тратится из их баллонов), здесь — только температура
tag("ad_astra/tags/items/freeze_resistant_armor", NS_IRON + NS_MARTIAN + CS_BASIC + CS_ADV)
tag("ad_astra/tags/items/heat_resistant_armor", NS_MARTIAN + CS_ADV)
# Northstar: температура (холод/жар)
tag("northstar/tags/items/insulating", ["#ad_astra:space_suit_items"] + CS_BASIC + CS_ADV)
tag("northstar/tags/items/heat_resistant", ["#ad_astra:netherite_space_suit_items"] + CS_ADV)
