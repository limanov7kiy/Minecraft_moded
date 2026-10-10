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

if __name__ == "__main__":
    OUT.mkdir(parents=True, exist_ok=True)
    for f in (lightning_bolt, sixth_sense, shockwave, snowball, mine, mind_trick, blink, intimidate, glide, ironflesh,
              diamondflesh, greater_fireball, chain_lightning, paralysis, summon_iron_golem):
        f().save(OUT / f"{f.__name__}.png"); print("icon", f.__name__)
