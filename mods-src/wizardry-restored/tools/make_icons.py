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

if __name__ == "__main__":
    OUT.mkdir(parents=True, exist_ok=True)
    for f in (lightning_bolt, sixth_sense, shockwave, snowball, mine, mind_trick, blink, intimidate, glide, ironflesh,
              diamondflesh, greater_fireball, chain_lightning, paralysis, summon_iron_golem, lightning_ray, disintegration,
              transience, mind_control, metamorphosis, frost_barrier, wall_of_frost, thunderstorm, earthquake, celestial_smite,
              slow_time, light, shadow_ward, ice_shroud, summon_spirit_wolf, summon_spirit_horse):
        f().save(OUT / f"{f.__name__}.png"); print("icon", f.__name__)
