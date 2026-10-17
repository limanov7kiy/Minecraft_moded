#!/usr/bin/env python3
"""Карта заражения мобов наших модов для Fungal Infection: Spore и SRP System (поток 22, docs/compat.md «Паразиты»).
Вход — выгрузка реестра существ с тестового сервера: строки «EDUMP <id> <категория> boss=<true|false>»
(KubeJS-скрипт в docs/compat.md, «Паразиты»: ServerEvents.loaded → ForgeRegistries.ENTITY_TYPES).
Выход:
- pack/kubejs/data/spore/spore_mob_conversion/mm_moded.json — Spore превращает умершего с мицелием по тегам;
- pack/kubejs/data/disaster_ward/tags/entity_types/spore_to_{licker,claw}.json — «животные» → spore:licker, «монстры» → spore:claw
  (идея карты — Fungal Infection Spore: MINI additions, Minigato668, MIT; сам аддон не ставим);
- pack/kubejs/data/disaster_ward/tags/entity_types/infection_immune.json — драконы и мини-боссы без тега forge:bosses (не заражаются, см. disaster-ward);
- строка infection_mappings для pack/config/srpsystem-common.toml (SRP понимает только id, не теги) — печатается в stdout.
Использование: spore_infection_map.py <файл EDUMP> [--write-srp]"""
import json, pathlib, re, sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
MODS = {"iceandfire", "lycanitesmobs", "cataclysm", "mowziesmobs", "aether", "aether_redux", "deep_aether", "ancient_aether",
        "lost_aether_content", "alexsmobs", "alexscaves", "twilightforest", "undergarden", "blue_skies", "quark", "the_bumblezone",
        "deeperdarker", "caerula_arbor", "torchesbecomesunlight", "bobsoriginiumdream", "sarkazs_furnace_side_fables", "minecraft"}
ANIMAL = {"CREATURE", "WATER_CREATURE", "UNDERGROUND_WATER_CREATURE", "AXOLOTLS", "AMBIENT", "WATER_AMBIENT", "AETHER_AERWHALE",
          "cave_creature", "deep_sea_creature"}
MONSTER = {"MONSTER", "AETHER_SURFACE_MONSTER", "AETHER_DARKNESS_MONSTER", "AETHER_SKY_MONSTER"}
# крупные существа и мини-боссы без тега forge:bosses: не превращаются и не заражаются
BIG = re.compile(r"dragon|serpent|hydra|leviathan|cyclops|wroughtnaut|frostmaw|umvuthi$|mowziesmobs:naga|myrmex_queen|monstrosity|"
                 r"amalgalich|asmodeus|rahovart|lycanitesmobs:(behemophet|astaroth)|gorgon|dread_lich|alexsmobs:(void_worm|warped_mosco)|"
                 r"alexscaves:(tremorzilla|atlatitan)|aether:(aerwhale)|twilightforest:(yeti$|giant_miner|armored_giant)|cosmic_crystal|"
                 r"the_watcher|coralssus|ignited_revenant|cataclysm:(the_prowler|ender_golem)|minecraft:(warden|elder_guardian)")
SKIP = re.compile(r"egg|statue|multipart|_part|segment|skull|minion|follower|_player$|lantern|wisp|dummy|corpse|_doll")
# ваниль: люди и големы уже в списке Spore («Mobs and their infected counterparts»)
VANILLA_SKIP = {"minecraft:villager", "minecraft:zombie", "minecraft:zombie_villager", "minecraft:husk", "minecraft:drowned", "minecraft:pillager",
                "minecraft:witch", "minecraft:wandering_trader", "minecraft:evoker", "minecraft:vindicator", "minecraft:iron_golem",
                "minecraft:snow_golem", "minecraft:mooshroom", "minecraft:skeleton_horse", "minecraft:player", "minecraft:armor_stand"}

licker, claw, immune = [], [], []
for line in open(sys.argv[1], encoding="utf-8"):
    p = line.split()
    if len(p) < 4 or p[0] != "EDUMP":
        continue
    eid, cat, boss = p[1], p[2], p[3] == "boss=true"
    mod = eid.split(":")[0]
    if mod not in MODS or boss or eid in VANILLA_SKIP:
        continue
    if BIG.search(eid):
        if cat in ANIMAL | MONSTER:
            immune.append(eid)
        continue
    if SKIP.search(eid):
        continue
    if mod == "minecraft" and cat not in ANIMAL:
        continue   # ванильные монстры — по правилам самого Spore
    if cat in ANIMAL:
        licker.append(eid)
    elif cat in MONSTER:
        claw.append(eid)

def tag(name, ids):
    d = ROOT / "pack/kubejs/data/disaster_ward/tags/entity_types"
    d.mkdir(parents=True, exist_ok=True)
    (d / f"{name}.json").write_text(json.dumps({"replace": False, "values": [{"id": i, "required": False} for i in sorted(ids)]},
                                               ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

tag("spore_to_licker", licker)
tag("spore_to_claw", claw)
tag("infection_immune", immune)
conv = ROOT / "pack/kubejs/data/spore/spore_mob_conversion"
conv.mkdir(parents=True, exist_ok=True)
(conv / "mm_moded.json").write_text(json.dumps({"#disaster_ward:spore_to_licker": "spore:licker", "#disaster_ward:spore_to_claw": "spore:claw"},
                                               indent=2) + "\n", encoding="utf-8")
srp = [f"{i};spore:licker" for i in sorted(licker)] + [f"{i};spore:claw" for i in sorted(claw)]
print(f"licker={len(licker)} claw={len(claw)} immune={len(immune)}", file=sys.stderr)
if "--write-srp" in sys.argv:
    cfg = ROOT / "pack/config/srpsystem-common.toml"
    s = cfg.read_text(encoding="utf-8")
    s, n = re.subn(r"^(\s*infection_mappings\s*=\s*).*$", lambda m: m.group(1) + json.dumps(srp, ensure_ascii=False), s, flags=re.M)
    assert n == 1
    cfg.write_text(s, encoding="utf-8")
