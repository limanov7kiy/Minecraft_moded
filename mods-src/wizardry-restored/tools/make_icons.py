#!/usr/bin/env python3
"""Рисует иконки заклинаний 32x32 (свои, не из оригинального мода): фон-руна цвета стихии + символ."""
import math, pathlib
from PIL import Image, ImageDraw

OUT = pathlib.Path(__file__).resolve().parent.parent / "src/main/resources/assets/wizardry_restored/textures/spells"
ELEMENT = {"lightning": (70, 200, 255), "earth": (90, 190, 70), "sorcery": (110, 255, 160),
           "fire": (255, 120, 30), "ice": (170, 230, 255), "healing": (255, 240, 120),
           "necromancy": (170, 60, 230), "magic": (220, 220, 255)}

def base(color):
    im = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    dark = tuple(c // 5 for c in color)
    d.ellipse((1, 1, 30, 30), fill=dark + (255,), outline=color + (255,), width=2)
    return im, d

def lightning_bolt():
    im, d = base(ELEMENT["lightning"])
    d.line([(19, 4), (12, 16), (18, 16), (11, 28)], fill=(255, 255, 255, 255), width=3)
    d.line([(19, 4), (12, 16), (18, 16), (11, 28)], fill=ELEMENT["lightning"] + (255,), width=1)
    return im

def sixth_sense():
    im, d = base(ELEMENT["earth"])
    d.ellipse((6, 11, 26, 21), outline=(230, 255, 210, 255), width=2)
    d.ellipse((13, 12, 19, 20), fill=(255, 255, 255, 255))
    d.ellipse((15, 14, 17, 18), fill=(20, 40, 10, 255))
    return im

def shockwave():
    im, d = base(ELEMENT["sorcery"])
    for r, a in ((4, 255), (8, 200), (12, 140)):
        d.ellipse((16 - r, 16 - r, 16 + r, 16 + r), outline=(220, 255, 230, a), width=2)
    return im

def snowball():
    im, d = base(ELEMENT["ice"]); d.ellipse((10, 10, 22, 22), fill=(250, 252, 255, 255), outline=(150, 200, 240, 255)); return im

def mine():
    im, d = base(ELEMENT["earth"])
    d.line([(9, 23), (21, 11)], fill=(150, 100, 60, 255), width=3)
    d.arc((10, 6, 28, 20), 200, 340, fill=(200, 200, 210, 255), width=3); return im

def mind_trick():
    im, d = base(ELEMENT["necromancy"])
    for r in (3, 6, 9): d.arc((16 - r, 16 - r, 16 + r, 16 + r), 0, 300, fill=(230, 200, 255, 255), width=1)
    return im

def blink():
    im, d = base(ELEMENT["sorcery"])
    d.line([(7, 16), (24, 16)], fill=(230, 255, 240, 255), width=2); d.polygon([(25, 16), (19, 11), (19, 21)], fill=(230, 255, 240, 255))
    for x in (9, 13): d.point((x, 12), fill=(200, 160, 255, 255)); d.point((x, 20), fill=(200, 160, 255, 255))
    return im

def intimidate():
    im, d = base(ELEMENT["necromancy"])
    d.polygon([(8, 12), (24, 12), (16, 25)], outline=(255, 255, 255, 255))
    for x in (11, 15, 19): d.polygon([(x, 12), (x + 2, 12), (x + 1, 16)], fill=(255, 255, 255, 255))
    return im

def glide():
    im, d = base(ELEMENT["earth"])
    d.polygon([(6, 18), (16, 10), (26, 18), (16, 15)], fill=(230, 255, 210, 255)); return im

def ironflesh():
    im, d = base(ELEMENT["healing"]); d.polygon([(16, 6), (25, 10), (23, 22), (16, 27), (9, 22), (7, 10)], fill=(190, 190, 195, 255)); return im

def diamondflesh():
    im, d = base(ELEMENT["healing"]); d.polygon([(16, 6), (25, 10), (23, 22), (16, 27), (9, 22), (7, 10)], fill=(120, 240, 230, 255)); return im

def greater_fireball():
    im, d = base(ELEMENT["fire"])
    d.ellipse((8, 8, 24, 24), fill=(255, 150, 40, 255)); d.ellipse((12, 12, 20, 20), fill=(255, 240, 120, 255)); return im

def chain_lightning():
    im, d = base(ELEMENT["lightning"])
    d.line([(6, 8), (13, 16), (10, 16), (18, 25)], fill=(255, 255, 255, 255), width=2)
    d.line([(15, 9), (22, 16), (19, 16), (26, 23)], fill=(200, 240, 255, 255), width=1); return im

def paralysis():
    im, d = base(ELEMENT["lightning"])
    d.rectangle((10, 9, 22, 23), outline=(255, 255, 255, 255), width=2)
    d.line([(17, 10), (14, 16), (18, 16), (15, 22)], fill=ELEMENT["lightning"] + (255,), width=2); return im

def summon_iron_golem():
    im, d = base(ELEMENT["sorcery"])
    d.rectangle((11, 8, 21, 16), fill=(200, 200, 200, 255)); d.rectangle((9, 16, 23, 26), fill=(170, 170, 170, 255))
    d.point((14, 11), fill=(200, 30, 30, 255)); d.point((18, 11), fill=(200, 30, 30, 255)); return im

def _ray(color, inner):
    im, d = base(color); d.line([(6, 26), (26, 6)], fill=inner + (255,), width=3); d.line([(6, 26), (26, 6)], fill=(255, 255, 255, 255), width=1); return im

def lightning_ray(): return _ray(ELEMENT["lightning"], (120, 220, 255))
def disintegration(): return _ray(ELEMENT["fire"], (255, 120, 20))

def transience():
    im, d = base(ELEMENT["healing"]); d.ellipse((10, 6, 22, 18), outline=(255, 255, 255, 150), width=2); d.rectangle((11, 17, 21, 27), outline=(255, 255, 255, 150), width=2); return im

def mind_control():
    im, d = base(ELEMENT["necromancy"]); d.ellipse((9, 9, 23, 23), outline=(255, 255, 255, 255), width=2); d.ellipse((14, 14, 18, 18), fill=(200, 60, 255, 255)); return im

def metamorphosis():
    im, d = base(ELEMENT["necromancy"]); d.arc((7, 7, 25, 25), 30, 330, fill=(240, 220, 255, 255), width=2); d.polygon([(24, 10), (27, 16), (21, 15)], fill=(240, 220, 255, 255)); return im

def frost_barrier():
    im, d = base(ELEMENT["ice"])
    for x in (8, 14, 20): d.rectangle((x, 9, x + 5, 24), fill=(200, 235, 255, 255), outline=(120, 180, 230, 255))
    return im

def wall_of_frost():
    im, d = base(ELEMENT["ice"])
    for y in (8, 14, 20): d.rectangle((8, y, 24, y + 5), fill=(220, 245, 255, 255), outline=(120, 180, 230, 255))
    return im

def thunderstorm():
    im, d = base(ELEMENT["lightning"]); d.ellipse((6, 6, 26, 15), fill=(150, 160, 180, 255))
    for x in (10, 17, 23): d.line([(x, 15), (x - 3, 21), (x, 21), (x - 3, 27)], fill=(255, 255, 160, 255), width=1)
    return im

def earthquake():
    im, d = base(ELEMENT["earth"]); d.line([(6, 18), (11, 14), (15, 20), (20, 13), (26, 18)], fill=(240, 220, 170, 255), width=2); return im

def celestial_smite():
    im, d = base(ELEMENT["healing"]); d.rectangle((14, 4, 18, 26), fill=(255, 250, 200, 255)); d.ellipse((9, 22, 23, 28), fill=(255, 240, 150, 255)); return im

def slow_time():
    im, d = base(ELEMENT["sorcery"]); d.ellipse((7, 7, 25, 25), outline=(230, 255, 240, 255), width=2)
    d.line([(16, 16), (16, 9)], fill=(230, 255, 240, 255), width=2); d.line([(16, 16), (21, 18)], fill=(230, 255, 240, 255), width=2); return im

def light():
    im, d = base(ELEMENT["sorcery"]); d.ellipse((11, 11, 21, 21), fill=(255, 255, 220, 255))
    for a in range(0, 360, 45):
        import math
        x, y = 16 + 10 * math.cos(math.radians(a)), 16 + 10 * math.sin(math.radians(a))
        d.point((x, y), fill=(255, 255, 200, 255))
    return im

def shadow_ward():
    im, d = base(ELEMENT["necromancy"]); d.polygon([(16, 6), (25, 10), (23, 22), (16, 27), (9, 22), (7, 10)], fill=(25, 15, 35, 255), outline=(170, 90, 230, 255)); return im

def ice_shroud():
    im, d = base(ELEMENT["ice"])
    for r in (6, 10): d.ellipse((16 - r, 16 - r, 16 + r, 16 + r), outline=(230, 250, 255, 255), width=1)
    d.line([(16, 8), (16, 24)], fill=(255, 255, 255, 255)); d.line([(8, 16), (24, 16)], fill=(255, 255, 255, 255)); return im

def summon_spirit_wolf():
    im, d = base(ELEMENT["earth"]); d.polygon([(9, 22), (12, 10), (15, 15), (17, 15), (20, 10), (23, 22), (16, 26)], fill=(200, 230, 255, 200)); return im

def summon_spirit_horse():
    im, d = base(ELEMENT["earth"]); d.polygon([(10, 26), (12, 12), (18, 7), (23, 9), (20, 14), (18, 26)], fill=(200, 230, 255, 200)); return im

def freezing_weapon():
    im, d = base(ELEMENT["ice"]); d.line([(9, 23), (22, 10)], fill=(220, 240, 255, 255), width=3); d.line([(8, 18), (14, 24)], fill=(120, 90, 60, 255), width=2); return im

def empowering_presence():
    im, d = base(ELEMENT["healing"]); d.polygon([(16, 5), (19, 13), (27, 13), (21, 18), (23, 26), (16, 21), (9, 26), (11, 18), (5, 13), (13, 13)], fill=(255, 240, 150, 255)); return im

def muffle():
    im, d = base(ELEMENT["sorcery"]); d.polygon([(8, 13), (13, 13), (19, 8), (19, 24), (13, 19), (8, 19)], fill=(220, 255, 230, 255)); d.line([(22, 11), (27, 21)], fill=(255, 90, 90, 255), width=2); return im

def grapple():
    im, d = base(ELEMENT["earth"]); d.line([(7, 25), (14, 18), (12, 14), (20, 8)], fill=(120, 200, 80, 255), width=2); d.arc((18, 5, 26, 13), 180, 90, fill=(200, 200, 200, 255), width=2); return im

def transportation():
    im, d = base(ELEMENT["sorcery"])
    for i in range(6):
        import math
        a = math.radians(i * 60); x, y = 16 + 9 * math.cos(a), 16 + 9 * math.sin(a); d.rectangle((x - 2, y - 2, x + 2, y + 2), fill=(200, 200, 210, 255))
    d.ellipse((13, 13, 19, 19), fill=(190, 120, 255, 255)); return im

def ice_statue():
    im, d = base(ELEMENT["ice"]); d.rectangle((10, 7, 22, 26), fill=(190, 230, 255, 200), outline=(255, 255, 255, 255)); d.ellipse((13, 9, 19, 15), fill=(120, 160, 200, 255)); return im

def petrify():
    im, d = base(ELEMENT["sorcery"]); d.ellipse((11, 6, 21, 15), fill=(150, 150, 150, 255)); d.rectangle((11, 15, 21, 26), fill=(130, 130, 130, 255)); return im

def conjure_armor():
    im, d = base(ELEMENT["sorcery"]); d.polygon([(9, 9), (13, 7), (16, 10), (19, 7), (23, 9), (22, 25), (10, 25)], fill=(170, 255, 220, 170), outline=(230, 255, 240, 255)); return im

def forcefield():
    im, d = base(ELEMENT["healing"]); d.ellipse((6, 6, 26, 26), outline=(255, 255, 220, 255), width=2); d.ellipse((9, 9, 23, 23), fill=(255, 250, 190, 90)); return im

def radiant_totem():
    im, d = base(ELEMENT["healing"]); d.rectangle((13, 12, 19, 27), fill=(235, 230, 215, 255)); d.rectangle((12, 6, 20, 12), fill=(255, 220, 90, 255))
    for x, y in ((6, 9), (26, 9), (16, 2)): d.line([(16, 9), (x, y)], fill=(255, 255, 200, 255), width=1)
    return im

def withering_totem():
    im, d = base(ELEMENT["necromancy"]); d.rectangle((13, 12, 19, 27), fill=(45, 40, 50, 255)); d.rectangle((12, 6, 20, 12), fill=(110, 40, 170, 255))
    d.point([(9, 14), (23, 16), (10, 21), (22, 22)], fill=(200, 160, 255, 255)); return im

def black_hole():
    im, d = base(ELEMENT["sorcery"])
    for r, a in ((12, 90), (9, 150)): d.arc((16 - r, 16 - r, 16 + r, 16 + r), 20, 300, fill=(180, 120, 255, a), width=2)
    d.ellipse((11, 11, 21, 21), fill=(0, 0, 0, 255)); return im

def stormcloud():
    im, d = base(ELEMENT["lightning"]); d.ellipse((6, 8, 18, 18), fill=(120, 130, 150, 255)); d.ellipse((13, 6, 26, 18), fill=(140, 150, 170, 255))
    d.line([(16, 18), (13, 23), (17, 23), (14, 28)], fill=(255, 255, 140, 255), width=2); return im

def decoy():
    im, d = base(ELEMENT["sorcery"])
    for x, a in ((11, 255), (19, 110)): d.ellipse((x - 3, 7, x + 3, 13), fill=(220, 255, 230, a)); d.rectangle((x - 3, 14, x + 3, 25), fill=(220, 255, 230, a))
    return im

def _wraith(color, eye):
    im, d = base(color); d.polygon([(10, 26), (12, 10), (16, 6), (20, 10), (22, 26), (19, 22), (16, 26), (13, 22)], fill=tuple(min(255, c + 40) for c in color) + (220,))
    d.point([(14, 12), (18, 12)], fill=eye); return im

def summon_ice_wraith():
    return _wraith(ELEMENT["ice"], (40, 90, 200, 255))

def summon_shadow_wraith():
    return _wraith((90, 30, 140), (255, 60, 60, 255))

def summon_storm_elemental():
    im = _wraith(ELEMENT["lightning"], (255, 255, 255, 255)); d = ImageDraw.Draw(im)
    d.line([(24, 5), (21, 11), (25, 11), (22, 17)], fill=(255, 255, 140, 255), width=1); return im

def shield():
    im, d = base(ELEMENT["healing"]); d.polygon([(16, 5), (25, 9), (23, 20), (16, 27), (9, 20), (7, 9)], fill=(255, 245, 170, 160), outline=(255, 255, 230, 255)); return im

def lightning_web():
    im, d = base(ELEMENT["lightning"]); pts = [(8, 9), (24, 8), (16, 16), (7, 23), (25, 24)]
    for a in pts:
        for b in pts:
            if a < b: d.line([a, b], fill=(200, 240, 255, 200), width=1)
    for x, y in pts: d.ellipse((x - 2, y - 2, x + 2, y + 2), fill=(255, 255, 255, 255))
    return im

def imbue_weapon():
    im, d = base(ELEMENT["sorcery"]); d.line([(9, 24), (23, 8)], fill=(220, 230, 240, 255), width=3); d.line([(8, 19), (14, 25)], fill=(140, 90, 40, 255), width=2)
    d.point([(20, 18), (24, 14), (12, 10)], fill=(200, 140, 255, 255)); return im

def mark_sacrifice():
    im, d = base(ELEMENT["healing"]); d.ellipse((9, 9, 23, 23), outline=(255, 220, 90, 255), width=2); d.line([(16, 6), (16, 26)], fill=(255, 220, 90, 255), width=2); d.line([(6, 16), (26, 16)], fill=(255, 220, 90, 255), width=2); return im

def mirage():
    im, d = base(ELEMENT["sorcery"])
    for x, a in ((10, 70), (16, 140), (22, 255)): d.ellipse((x - 3, 7, x + 3, 13), fill=(220, 255, 230, a)); d.rectangle((x - 3, 14, x + 3, 25), fill=(220, 255, 230, a))
    return im

def snare():
    im, d = base(ELEMENT["earth"]); d.ellipse((7, 14, 25, 24), outline=(170, 120, 70, 255), width=2); d.line([(16, 19), (16, 6)], fill=(170, 120, 70, 255), width=2); return im

def forest_of_thorns():
    im, d = base(ELEMENT["earth"])
    for x in (9, 16, 23): d.polygon([(x - 4, 26), (x, 8 + (x % 3) * 2), (x + 4, 26)], fill=(60, 140, 50, 255)); d.point([(x - 1, 16), (x + 1, 20)], fill=(220, 60, 60, 255))
    return im

def lightning_disc():
    im, d = base(ELEMENT["lightning"]); d.ellipse((6, 12, 26, 20), fill=(200, 240, 255, 220), outline=(255, 255, 255, 255)); d.line([(10, 16), (22, 16)], fill=(255, 255, 160, 255), width=1); return im

def lightning_hammer():
    im, d = base(ELEMENT["lightning"]); d.rectangle((9, 7, 23, 14), fill=(190, 195, 205, 255)); d.rectangle((15, 14, 17, 27), fill=(150, 100, 60, 255))
    d.line([(24, 16), (21, 21), (25, 21), (22, 26)], fill=(255, 255, 140, 255), width=1); return im

def clairvoyance():
    im, d = base(ELEMENT["sorcery"]); d.line([(7, 25), (12, 20), (12, 14), (19, 12), (24, 7)], fill=(220, 255, 230, 255), width=2); d.ellipse((21, 4, 27, 10), fill=(255, 255, 255, 255)); return im

def divination():
    im, d = base(ELEMENT["sorcery"]); d.polygon([(16, 6), (24, 16), (16, 26), (8, 16)], fill=(90, 230, 230, 255), outline=(220, 255, 255, 255)); d.line([(16, 6), (16, 26)], fill=(200, 255, 255, 255)); return im

def effect_mark_sacrifice():
    im = Image.new("RGBA", (18, 18), (0, 0, 0, 0)); d = ImageDraw.Draw(im)
    d.ellipse((3, 3, 14, 14), outline=(255, 220, 90, 255), width=2); d.line([(9, 1), (9, 16)], fill=(255, 220, 90, 255), width=2); d.line([(1, 9), (16, 9)], fill=(255, 220, 90, 255), width=2)
    return im

def summon_phoenix():
    im, d = base(ELEMENT["fire"]); d.polygon([(16, 8), (27, 12), (19, 16), (16, 26), (13, 16), (5, 12)], fill=(255, 170, 40, 255), outline=(255, 240, 120, 255))
    d.ellipse((14, 6, 18, 10), fill=(255, 230, 90, 255)); return im

def resurrection():
    im, d = base(ELEMENT["healing"]); d.rectangle((14, 6, 18, 26), fill=(255, 250, 210, 255)); d.rectangle((9, 11, 23, 15), fill=(255, 250, 210, 255))
    d.arc((5, 3, 27, 13), 200, 340, fill=(255, 230, 100, 255), width=2); return im

def possession():
    im, d = base(ELEMENT["necromancy"]); d.ellipse((8, 10, 24, 26), fill=(60, 120, 60, 255)); d.point([(13, 16), (19, 16)], fill=(255, 60, 60, 255))
    d.polygon([(12, 4), (20, 4), (17, 11), (15, 11)], fill=(200, 160, 255, 200)); return im

def entity_phoenix():
    import random
    rnd = random.Random(7); im = Image.new("RGBA", (64, 32)); px = im.load()
    for y in range(32):
        for x in range(64):
            t = (y % 16) / 16 + rnd.uniform(-0.2, 0.2)
            r = 255; g = int(max(60, min(230, 230 - t * 170))); b = int(max(0, min(90, 70 - t * 60)))
            if rnd.random() < 0.08: g, b = 245, 160
            px[x, y] = (r, g, b, 255)
    for x, y in ((9, 12), (13, 12)): px[x, y] = px[x + 1, y] = (255, 255, 230, 255)
    return im

def block_transportation_stone(top=False):
    import random
    rnd = random.Random(11 if top else 12); im = Image.new("RGBA", (16, 16)); px = im.load()
    for y in range(16):
        for x in range(16):
            v = rnd.randint(105, 135); px[x, y] = (v, v, v + 6, 255)
    d = ImageDraw.Draw(im)
    if top: d.ellipse((6, 6, 9, 9), fill=(190, 120, 255, 255))
    else:
        d.line([(8, 6), (8, 13)], fill=(180, 110, 255, 255)); d.line([(6, 9), (10, 9)], fill=(180, 110, 255, 255))
    return im

if __name__ == "__main__":
    OUT.mkdir(parents=True, exist_ok=True)
    for f in (lightning_bolt, sixth_sense, shockwave, snowball, mine, mind_trick, blink, intimidate, glide, ironflesh,
              diamondflesh, greater_fireball, chain_lightning, paralysis, summon_iron_golem, lightning_ray, disintegration,
              transience, mind_control, metamorphosis, frost_barrier, wall_of_frost, thunderstorm, earthquake, celestial_smite,
              slow_time, light, shadow_ward, ice_shroud, summon_spirit_wolf, summon_spirit_horse, freezing_weapon,
              empowering_presence, muffle, grapple, transportation, ice_statue, petrify, conjure_armor,
              forcefield, radiant_totem, withering_totem, black_hole, stormcloud, decoy, summon_ice_wraith,
              summon_shadow_wraith, summon_storm_elemental, shield, lightning_web, imbue_weapon, mark_sacrifice, mirage,
              snare, forest_of_thorns, lightning_disc, lightning_hammer, clairvoyance, divination, summon_phoenix, resurrection, possession):
        f().save(OUT / f"{f.__name__}.png"); print("icon", f.__name__)
    eff = OUT.parent / "mob_effect"; eff.mkdir(parents=True, exist_ok=True)
    effect_mark_sacrifice().save(eff / "mark_sacrifice.png"); print("effect mark_sacrifice")
    ent = OUT.parent / "entity"; ent.mkdir(parents=True, exist_ok=True)
    entity_phoenix().save(ent / "phoenix.png"); print("entity phoenix")
    blk = OUT.parent / "block"; blk.mkdir(parents=True, exist_ok=True)
    block_transportation_stone().save(blk / "transportation_stone.png")
    block_transportation_stone(True).save(blk / "transportation_stone_top.png"); print("block transportation_stone")
