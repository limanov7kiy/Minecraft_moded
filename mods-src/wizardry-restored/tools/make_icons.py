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

if __name__ == "__main__":
    OUT.mkdir(parents=True, exist_ok=True)
    for f in (lightning_bolt, sixth_sense, shockwave):
        f().save(OUT / f"{f.__name__}.png"); print("icon", f.__name__)
