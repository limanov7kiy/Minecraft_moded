#!/usr/bin/env python3
"""Модель Yes Steve Model «Priestess» (Arknights) — генератор.

Собирает папку модели pack/config/yes_steve_model/custom/priestess/ (формат YSM 2.x, папка):
  ysm.json, models/main.json + arm.json, textures/priestess.png (256×256, пиксель-арт),
  animations/*.animation.json, functions/*.molang.

Скелет (имена/иерархия/pivot костей) и базовые анимации (ходьба, бег, присед, плавание, полёт, сон,
езда, удары, удержание предметов, Iron's Spells, TACZ) — от встроенной модели YSM «default»
(лицензия CC 0; авторы: 哥斯拉, 端木一动不动, 甜粽子, 星屑海螺, 蓝玫瑰, 艾雷克亚) — так работают все состояния.
Геометрия, текстура, осколок Originium, формы предметов, глаза, плащ, позы колеса — свои (Minecraft_moded).

Использование: python3 build.py [--out <папка>] [--preview <png>]
Нужен jar YSM (ссылка — pack/mods/yes-steve-model.pw.toml), кэш — build/.
"""
import argparse, copy, hashlib, io, json, math, pathlib, re, sys, tomllib, urllib.request, zipfile, zlib
import numpy as np
from PIL import Image

HERE = pathlib.Path(__file__).resolve().parent
ROOT = HERE.parent.parent
OUT = ROOT / "pack" / "config" / "yes_steve_model" / "custom" / "priestess"
TEX = 256


# ======================================================================= база default из jar YSM
def ysm_default():
    meta = tomllib.loads((ROOT / "pack" / "mods" / "yes-steve-model.pw.toml").read_text())
    cache = HERE / "build" / meta["filename"]
    if not cache.exists():
        cache.parent.mkdir(exist_ok=True)
        data = urllib.request.urlopen(meta["download"]["url"]).read()
        assert hashlib.sha512(data).hexdigest() == meta["download"]["hash"], "hash YSM jar"
        cache.write_bytes(data)
    z = zipfile.ZipFile(cache)
    pre = "assets/yes_steve_model/builtin/default/"
    return lambda p: z.read(pre + p)


# ======================================================================= палитра и материалы
def C(h, a=255):
    h = h.lstrip("#")
    return np.array([int(h[i:i + 2], 16) for i in (0, 2, 4)] + [a], np.float32)


P = {
    "skin": C("ffece4"), "skin_s": C("f4d3c8"), "skin_d": C("e3b3a6"), "blush": C("f7aeb0"), "mouth": C("b35a6a"),
    "hair": C("2c2226"), "hair_l": C("44363c"), "hair_hi": C("62505a"), "hair_d": C("1a1316"),
    "hair_tip": C("1d2b68"), "hair_tip_l": C("3a4fa6"),
    "white": C("f7f5fa"), "white_s": C("e4e0ea"), "white_d": C("c7c1d2"),
    "black": C("1e1c24"), "black_l": C("3a3744"), "black_hi": C("57535f"), "black_d": C("0f0e13"),
    "graphite": C("3b3a44"), "graph_l": C("5a5866"),
    "purple": C("7446b4"), "purple_l": C("a07ad8"), "purple_d": C("4a2a7c"),
    "tights": C("3e2a58"), "tights_l": C("5d437e"), "tights_hi": C("7a5d9c"), "tights_d": C("261836"), "tights_skin": C("7a6178"),
    "red": C("e0243f"), "red_l": C("ff6378"), "red_d": C("8e1028"), "core": C("fff4f6"),
    "violet": C("b36cff"), "violet_l": C("ead2ff"), "eye": C("9a90b6"), "eye_d": C("4f4668"), "eye_l": C("c9c0e0"),
    "obs": C("17151c"), "obs_l": C("3a3644"), "obs_hi": C("5a5468"), "obs_d": C("0a090d"), "obs_glint": C("4b3f63"),
    "ember": C("e8401c"), "ember_l": C("ff7a3a"), "ember_hi": C("ffd2a8"), "smoke": C("2e2b33"),
    "yellow": C("f1d04a"), "silver": C("d4d1e0"), "silver_d": C("8f8b9e"),
}


def fill(w, h, c):
    a = np.zeros((h, w, 4), np.float32); a[:] = c; return a


def noise(w, h, seed, amp=6.0):
    r = np.random.default_rng(seed)
    n = (r.random((h, w, 1)) - 0.5) * amp
    return np.concatenate([n, n, n, np.zeros((h, w, 1))], axis=2)


SIDE = ("north", "south", "east", "west")


def m_skin(w, h, face, s):
    a = fill(w, h, P["skin"]) + noise(w, h, s, 3)
    if face == "down": a[:] = P["skin_s"]
    if face in SIDE and h > 3: a[-1] = P["skin_s"]
    return a


def m_hand(w, h, face, s):
    a = m_skin(w, h, face, s)
    if face in SIDE and h > 2: a[0] = P["skin_s"]  # тень от манжеты
    return a


def m_sweater(w, h, face, s):
    """белый вязаный свитер в рубчик: вертикальные рубчики (2 px), мягкая тень к низу и по краям"""
    a = fill(w, h, P["white"])
    if face in SIDE:
        a[:, 1::3] = P["white_s"]
        if w > 4: a[:, 0] = P["white_s"]; a[:, -1] = P["white_d"]
        for y in range(h):
            f = y / max(1, h - 1)
            a[y, :, :3] = a[y, :, :3] * (1 - 0.08 * f)
    else:
        a[:] = P["white_s"]
    return a + noise(w, h, s, 4)


def m_cuff(w, h, face, s):
    """рюша: белая, нижний край зубчиками, складки"""
    a = fill(w, h, P["white"])
    if face in SIDE:
        a[:, ::2] = P["white_s"]
        a[-1, 1::2, 3] = 0
        a[0, :] = P["white_d"]
    else:
        a[:] = P["white_s"]
    return a


def m_black(w, h, face, s):
    a = fill(w, h, P["black"]) + noise(w, h, s, 5)
    if face == "up": a[:] = P["black_l"]
    if face in SIDE and h > 3: a[0] = P["black_l"]
    return a


def m_satin(w, h, face, s):
    """чёрный атлас (бант): блик по диагонали"""
    a = fill(w, h, P["black"])
    for y in range(h):
        x = int((y / max(1, h - 1)) * (w - 1) * 0.6 + w * 0.2)
        if 0 <= x < w: a[y, x] = P["black_hi"]
    if h > 2: a[-1] = P["black_d"]
    return a


def m_belt(w, h, face, s):
    a = fill(w, h, P["black"]); a[0, :] = P["black_l"]
    if w > 6: a[:, ::6] = P["black_l"]
    return a


def m_silver(w, h, face, s):
    a = fill(w, h, P["silver"]); a[-1:, :] = P["silver_d"]; a[:, -1:] = P["silver_d"]
    if w > 1 and h > 1: a[0, 0] = P["white"]
    return a


def m_pleat(w, h, face, s):
    """панель плиссировки: левый край светлый, правый тёмный, к подолу темнее, тонкий кант снизу"""
    a = fill(w, h, P["black"])
    if face in SIDE:
        a[:, 0] = P["black_hi"]; a[:, 1:2] = P["black_l"]; a[:, -1] = P["black_d"]
        for y in range(h):
            f = y / max(1, h - 1)
            a[y, :, :3] = a[y, :, :3] * (1 - 0.25 * f)
        a[-1] = P["black_l"]
    return a + noise(w, h, s, 3)


def m_frill(w, h, face, s):
    """фиолетовая нижняя оборка: волны-фестоны по низу"""
    a = fill(w, h, P["purple"])
    if face in SIDE:
        a[:, ::3] = P["purple_l"]; a[:, 2::3] = P["purple_d"]
        a[0] = P["purple_d"]
        a[-1, 1::3, 3] = 0
    return a


def m_tights(w, h, face, s, knee=False, top=False):
    """плотные тёмно-фиолетовые колготки: блик по центру спереди, тень по бокам, у бедра просвечивает кожа"""
    a = fill(w, h, P["tights"])
    if face in SIDE:
        c = w // 2
        if face in ("north", "south") and w > 3:
            a[:, c - 1:c + 1] = P["tights_l"]
            a[:, c - 1] = P["tights_hi"] if face == "north" else P["tights_l"]
        a[:, 0] = P["tights_d"]; a[:, -1] = P["tights_d"]
        if top:
            k = max(1, h // 6)
            for y in range(k):
                f = 1 - y / k
                a[y] = a[y] * (1 - 0.45 * f) + P["tights_skin"] * 0.45 * f
        if knee and face == "north":
            a[0:max(1, h // 8), :] = P["tights_l"]
    return a + noise(w, h, s, 3)


def tights(knee=False, top=False):
    return lambda w, h, f, s: m_tights(w, h, f, s, knee, top)


def m_shoe(w, h, face, s):
    a = fill(w, h, P["white"]) + noise(w, h, s, 3)
    if face in SIDE:
        a[-1] = P["white_d"]; a[-2:-1] = P["white_s"]
        if h > 3: a[0] = P["white_s"]
    if face == "down": a[:] = P["white_d"]
    return a


def m_hair(w, h, face, s, tip=0.0, band=False):
    """прямые волосы: пряди тремя тонами, «нимб-блик» band, концы (tip — доля высоты) — тёмно-синие"""
    r = np.random.default_rng(s)
    a = fill(w, h, P["hair"])
    if face in ("up", "down"):
        a[:, ::2] = P["hair_l"]
        return a
    for x in range(w):
        v = r.random()
        if v < 0.35: a[:, x] = P["hair_l"]
        elif v < 0.5: a[:, x] = P["hair_d"]
    if band and h > 6:
        y0 = int(h * 0.2)
        for x in range(w):
            k = 1 + (x * 7 + s) % 3  # «нимб»-блик: сплошная полоса неровной толщины
            a[y0:y0 + k, x] = a[y0:y0 + k, x] * 0.35 + P["hair_hi"] * 0.65
    if tip > 0:
        n = max(1, int(round(h * tip)))
        for y in range(h - n, h):
            f = (y - (h - n) + 1) / n
            a[y] = a[y] * (1 - f) + P["hair_tip"] * f
            a[y, (y % 3)::3] = a[y, (y % 3)::3] * (1 - f) + P["hair_tip_l"] * f
    return a


def hair(tip=0.0, band=False):
    return lambda w, h, f, s: m_hair(w, h, f, s, tip, band)


def m_head(w, h, face, s):
    """голова: лицо (4 px на блок): улыбка, румянец, тень от чёлки; остальное — волосы"""
    a = fill(w, h, P["skin"]) + noise(w, h, s, 2)
    if face == "north":
        a[0:2] = P["skin_s"]  # тень от чёлки; на лице только глаза (решение владельца)
        a[-1, :] = P["skin_s"]
    elif face in ("east", "west", "south", "up"):
        a[:] = P["hair"]
    return a


def m_eye_white(w, h, face, s):
    """глаз: густые ресницы сверху с «хвостиком», контур, белок, нижняя линия"""
    a = fill(w, h, P["white"])
    if face == "north":
        k = max(2, h // 4)
        a[:k, :] = P["black"]
        a[k, :] = P["black_l"]
        a[:k + 3, 0] = P["black"]; a[:k + 3, -1] = P["black"]
        a[-1, 2:-2] = P["eye_d"]
        a[-2, 1] = P["black_l"]; a[-2, -2] = P["black_l"]
    else:
        a[:] = P["black"]
    return a


def m_iris(w, h, face, s):
    """серо-фиолетовая радужка: тень века сверху, светлеет книзу, ободок"""
    a = fill(w, h, P["eye"])
    if face == "north":
        for y in range(h):
            f = y / max(1, h - 1)
            a[y] = P["eye_d"] * (1 - f) + P["eye_l"] * f
        a[0, :] = P["black"]; a[:, 0] = P["eye_d"]; a[:, -1] = P["eye_d"]
    return a


def m_violet(w, h, face, s):
    a = fill(w, h, P["violet"])
    if w > 2 and h > 2:
        a[0, :] = P["violet_l"]; a[:, 0] = P["violet_l"]
    return a


def m_rim(w, h, face, s):
    return fill(w, h, P["white"])


def m_hot(w, h, face, s):
    return fill(w, h, P["violet_l"])


def m_sparkle(w, h, face, s):
    return fill(w, h, P["white"])


def m_mouthflat(w, h, face, s):
    return fill(w, h, P["skin_d"] * 0.6 + P["mouth"] * 0.4)


def m_brow(w, h, face, s):
    return fill(w, h, P["hair_d"])


def m_crystal(w, h, face, s):
    """осколок Originium по референсу владельца: чёрный обсидиан, светлые рёбра сверху-слева, тёмные снизу-справа"""
    a = fill(w, h, P["obs"])
    a[0, :] = P["obs_l"]; a[:, 0] = P["obs_l"]
    a[-1, :] = P["obs_d"]; a[:, -1] = P["obs_d"]
    if w >= 4 and h >= 4:
        a[1, 1] = P["obs_hi"]; a[h // 2, 1:w // 2] = P["obs_l"]
        a[h - 2, w - 3] = P["obs_glint"]
    return a


def m_core(w, h, face, s):
    """светящееся ядро: оранжево-красное, к центру светлее"""
    a = fill(w, h, P["ember"])
    if w >= 3 and h >= 3:
        a[1:-1, 1:-1] = P["ember_l"]
        a[h // 2, w // 2] = P["ember_hi"]
    return a


def m_smoke(w, h, face, s):
    """дымчатые края внизу кристалла: тёмно-серый полупрозрачный"""
    a = fill(w, h, P["smoke"])
    a[..., 3] = 120
    a[::2, 1::2, 3] = 70
    return a


def m_glitch(w, h, face, s):
    a = fill(w, h, P["ember_l"]); a[::2, :] = P["ember_hi"]; return a


def m_cape(w, h, face, s, trim=(True, True, True), hem=None, inner=False):
    """плащ: белый, почти непрозрачный, складки; чёрная отделка (верх, бока) и по краю подола; hem — профиль клиньев"""
    a = fill(w, h, P["white_s"] if inner else P["white"]); a[..., 3] = 238
    a[:, ::5, :3] = P["white_d"][:3] if inner else P["white_s"][:3]
    if face in ("up", "down"):
        a[:] = P["black"]; return a
    t, l, r = trim
    if t: a[0:3, :, :] = P["black"]
    if l: a[:, 0:2, :] = P["black"]
    if r: a[:, -2:, :] = P["black"]
    if hem is not None:
        for x in range(w):
            u = x / max(1, w - 1)
            if face in ("south", "east"):
                u = 1 - u
            cut = int(round(hem(u) * h))
            if cut > 0:
                a[h - cut:, x, 3] = 0
            y = h - cut - 1
            for k in range(3):
                if 0 <= y - k < h: a[y - k, x] = P["black"]
    return a


def cape(hem=None, trim=(False, True, True), inner=False):
    return lambda w, h, f, s: m_cape(w, h, f, s, trim, hem, inner)


def wedges(n, depth, phase=0.0, slope=0.0):
    """профиль подола: n острых клиньев глубиной depth (доля высоты) + наклон slope (асимметрия)"""
    def f(u):
        t = (u * n + phase) % 1.0
        tri = abs(t - 0.5) * 2
        return max(0.0, min(0.95, depth * tri + slope * u))
    return f


def m_badge(w, h, face, s):
    """бейдж: белая карточка, чёрная полоса сверху, строки текста, фиолетовый ромб-логотип"""
    a = fill(w, h, P["white"])
    if face == "north" and w > 4 and h > 6:
        a[0:2, :] = P["black"]
        a[3, 1:-1] = P["graphite"]; a[5, 1:w - 2] = P["graph_l"]; a[7 if h > 8 else 6, 1:w // 2] = P["graph_l"]
        a[h - 3:h - 1, w - 3:w - 1] = P["purple"]
        a[-1, :] = P["white_d"]
    return a


def m_clip_board(w, h, face, s):
    a = fill(w, h, P["black"])
    if face == "up" and w > 4 and h > 4:
        a[1:-1, 1:-1] = P["graphite"]
        a[1:5, w - 6:w - 1] = P["yellow"]
        a[h // 2:, 2:w - 2:2] = P["graph_l"]
    return a


def m_pencil(w, h, face, s):
    a = fill(w, h, P["yellow"]); a[0] = P["black"]; return a


def m_strap(w, h, face, s):
    a = fill(w, h, P["black"]); a[:, 0] = P["black_l"]
    if h > 3: a[-2:] = P["silver"]  # металлический наконечник
    return a


def m_pouch(w, h, face, s):
    """подсумок: чёрная кожа, клапан, серебряная кнопка, шов"""
    a = fill(w, h, P["black"]) + noise(w, h, s, 6)
    if face in SIDE and h > 4:
        k = max(2, h // 3)
        a[0:k] = P["black_l"]; a[k] = P["black_d"]
        if w > 2: a[k - 1, w // 2] = P["silver"]
        a[-1] = P["black_d"]
    return a


def m_lace(w, h, face, s):
    """белое кружево: ажур из прозрачных точек, зубчатый край"""
    a = fill(w, h, P["white"])
    a[1::2, 1::2, 3] = 0
    a[-1, ::2, 3] = 0
    return a


MATS = {k[2:]: v for k, v in globals().items() if k.startswith("m_") and callable(v)}


# ======================================================================= геометрия
class Geo:
    def __init__(self):
        self.bones = []          # dict как в json
        self.byname = {}
        self.faces = []          # (cube, face, w_px, h_px, painter, seed, share_key, flip)

    def bone(self, name, parent=None, pivot=(0, 0, 0), rotation=None):
        if name in self.byname:
            return self.byname[name]
        b = {"name": name, "pivot": [round(x, 4) for x in pivot]}
        if parent: b["parent"] = parent
        if rotation: b["rotation"] = list(rotation)
        self.bones.append(b); self.byname[name] = b
        return b

    def cube(self, bone, origin, size, mat, rot=None, pivot=None, dens=2.0, inflate=0.0, faces=None, share=None,
             seed=None, mirror_of=None):
        c = {"origin": [round(x, 4) for x in origin], "size": [round(x, 4) for x in size], "uv": {}}
        if rot:
            c["rotation"] = list(rot); c["pivot"] = [round(x, 4) for x in pivot]
        if inflate:
            c["inflate"] = inflate
        self.byname[bone].setdefault("cubes", []).append(c)
        sx, sy, sz = size
        dims = {"north": (sx, sy), "south": (sx, sy), "east": (sz, sy), "west": (sz, sy), "up": (sx, sz), "down": (sx, sz)}
        seed = seed if seed is not None else len(self.faces) * 7 + 1
        for f, (fw, fh) in dims.items():
            if faces is not None and f not in faces:
                continue
            if fw <= 0.001 or fh <= 0.001:
                continue
            m = mat[f] if isinstance(mat, dict) else mat
            if m is None:
                continue
            pw, ph = max(1, int(round(fw * dens))), max(1, int(round(fh * dens)))
            key = None
            if share:  # общий участок текстуры (кристаллы и т.п.)
                key = (share, f if share.endswith("@face") else "", pw, ph) if not share.startswith("swatch") else (share,)
            self.faces.append([c, f, pw, ph, m, seed + zlib.crc32(f.encode()) % 97, key, False])
        return c

    def mirror(self, src_bone, dst_bone):
        """Копия кубов кости с отражением по X; текстура общая (u зеркально)."""
        b = self.byname[src_bone]
        start = len(self.faces)
        for c in b.get("cubes", []):
            o, s = c["origin"], c["size"]
            n = {"origin": [round(-(o[0] + s[0]), 4), o[1], o[2]], "size": list(s), "uv": {}}
            if "rotation" in c:
                r = c["rotation"]; n["rotation"] = [r[0], -r[1], -r[2]]
                n["pivot"] = [-c["pivot"][0], c["pivot"][1], c["pivot"][2]]
            if "inflate" in c: n["inflate"] = c["inflate"]
            self.byname[dst_bone].setdefault("cubes", []).append(n)
            for fr in [fr for fr in self.faces[:start] if fr[0] is c]:
                f = {"east": "west", "west": "east"}.get(fr[1], fr[1])
                self.faces.append([n, f, fr[2], fr[3], fr[4], fr[5], ("mirror", id(c), fr[1]), True])

    # ---- упаковка текстуры
    def pack(self):
        img = np.zeros((TEX, TEX, 4), np.float32)
        placed = {}
        order = sorted(range(len(self.faces)), key=lambda i: (-self.faces[i][3], -self.faces[i][2]))
        x = y = rowh = 0
        def alloc(w, h):
            nonlocal x, y, rowh
            if x + w > TEX:
                x, y, rowh = 0, y + rowh, 0
            if y + h > TEX:
                raise SystemExit(f"текстура {TEX}² переполнена (нужен {w}×{h} в строке y={y})")
            r = (x, y); x += w; rowh = max(rowh, h)
            return r
        mirrors = []
        for i in order:
            c, f, w, h, m, seed, key, flip = self.faces[i]
            if flip:
                mirrors.append(i); continue
            if key and key in placed:
                (px, py), pw, ph = placed[key]
            else:
                if key and str(key[0]).startswith("swatch"):
                    w = h = 8  # общий образец материала: все грани растягивают один квадрат 8×8
                px, py = alloc(w, h)
                img[py:py + h, px:px + w] = (MATS[m] if isinstance(m, str) else m)(w, h, f, seed)
                pw, ph = w, h
                if key: placed[key] = ((px, py), pw, ph)
            placed[("face", id(c), f)] = ((px, py), pw, ph)
            if f == "down":
                c["uv"][f] = {"uv": [px, py + ph], "uv_size": [pw, -ph]}
            else:
                c["uv"][f] = {"uv": [px, py], "uv_size": [pw, ph]}
        for i in mirrors:
            c, f, w, h, m, seed, key, flip = self.faces[i]
            _, src_id, src_f = key
            (px, py), pw, ph = placed[("face", src_id, src_f)]
            if f == "down":
                c["uv"][f] = {"uv": [px + pw, py + ph], "uv_size": [-pw, -ph]}
            else:
                c["uv"][f] = {"uv": [px + pw, py], "uv_size": [-pw, ph]}
        used = y + rowh
        img = np.clip(img, 0, 255).astype(np.uint8)
        return Image.fromarray(img, "RGBA"), used

    def json(self, ident="geometry.priestess", bounds=(7, 9, [0, 1.5, 0])):
        return {"format_version": "1.12.0", "minecraft:geometry": [{
            "description": {"identifier": ident, "texture_width": TEX, "texture_height": TEX,
                            "visible_bounds_width": bounds[0], "visible_bounds_height": bounds[1], "visible_bounds_offset": bounds[2]},
            "bones": self.bones}]}


def box(lo, hi):
    """origin/size из двух углов"""
    a = [min(l, h) for l, h in zip(lo, hi)]
    return a, [abs(h - l) for l, h in zip(lo, hi)]


# ======================================================================= модель Priestess
FORMS = ["sword", "axe", "pickaxe", "shovel", "hoe", "bow", "crossbow", "shield", "spear", "staff", "tome", "food", "other"]


def strand(G, bone, x0, x1, ytop, ybot, z0, z1, tip=0.35, band=False, rot=None, piv=None, taper=True, dens=2):
    """прядь: основной куб + сужающийся кончик (тёмно-синий)"""
    h = ytop - ybot
    k = h * 0.22 if taper else 0
    G.cube(bone, (x0, ybot + k, z0), (x1 - x0, h - k, z1 - z0), hair(tip * 0.6, band), rot=rot, pivot=piv, dens=dens)
    if taper:
        w = (x1 - x0) * 0.55
        cx = (x0 + x1) / 2
        G.cube(bone, (cx - w / 2, ybot, z0 + (z1 - z0) * 0.15), (w, k, (z1 - z0) * 0.7), hair(1.0), rot=rot, pivot=piv, dens=dens)


def ring(G, parent, name, n, top, height, rx, rz, width, thick, tilt, mat, zc=0.1, dens=2, phase=0.0):
    """кольцо панелей (юбка/оборка): n костей с поворотом по Y, в каждой панель с наклоном наружу"""
    for i in range(n):
        th = 360.0 * (i + phase) / n
        a = math.radians(th)
        r = rx * rz / math.sqrt((rx * math.cos(a)) ** 2 + (rz * math.sin(a)) ** 2)  # 0° — спереди (полуось rz), 90° — бок (rx)
        bn = f"{name}{i}"
        G.bone(bn, parent, (0, top, zc), (0, round(th, 3), 0))
        G.cube(bn, (-width / 2, top - height, zc - r - thick), (width, height, thick), mat,
               rot=(-tilt, 0, 0), pivot=(0, top, zc - r), dens=dens, faces=["north", "south", "east", "west", "down"])


def build_main(default_geo):
    G = Geo()
    for b in default_geo["minecraft:geometry"][0]["bones"]:
        rot = b.get("rotation")
        if b["name"] in ("Collar", "Ribbon"):
            rot = None
        G.bone(b["name"], b.get("parent"), b.get("pivot", [0, 0, 0]), rot)
    cb = G.cube

    # ================= голова, лицо
    cb("Head", (-3.5, 33.05, -3.5), (7, 7, 7), "head", dens=4, faces=["north"])
    cb("Head", (-3.5, 33.05, -3.5), (7, 7, 7), "head", faces=["south", "east", "west", "up", "down"])
    cb("AllHead", (-1.4, 31.9, -1.4), (2.8, 2.0, 2.8), "skin")
    for side in ("Right", "Left"):
        sg = -1 if side == "Right" else 1
        ex0 = -3.45 if side == "Right" else 1.05
        cb(f"{side}Eyelid", (ex0, 34.6, -3.53), (2.4, 2.4, 0.1), "eye_white", dens=5, faces=["north"])  # высота как у Irene
        cx = ex0 + 1.2 + 0.1 * sg   # зрачок чуть к носу
        cb(f"{side}EyesBase", (cx - 0.85, 34.65, -3.58), (1.7, 1.95, 0.1), "iris", dens=5, faces=["north"])
        cb(f"{side}EyesBase", (cx - 0.75, 36.05, -3.66), (0.4, 0.4, 0.05), "sparkle", dens=2, faces=["north"], share="swatch_sparkle")
        pv = (cx, 35.55, -3.64)
        for nm, sz, z, mat in (("Rim", 0.66, -3.63, "rim"), ("Pupil", 0.46, -3.65, "violet"), ("Hot", 0.2, -3.67, "hot")):
            G.bone(f"ysmGlow{side}{nm}", f"{side}EyesBase", pv)
            cb(f"ysmGlow{side}{nm}", (cx - sz / 2, 35.55 - sz / 2, z), (sz, sz, 0.02), mat, rot=(0, 0, 45), pivot=pv,
               faces=["north"], share=f"swatch_{mat}")

    # ================= волосы: «шапка», чёлка-химэ прядями, боковые пряди, длинные пряди сзади
    cb("Hair", (-4.1, 37.2, -4.1), (8.2, 4.0, 8.2), hair(0, True), dens=2, faces=["south", "east", "west", "up"])
    cb("Hair", (-4.1, 37.2, -4.1), (8.2, 4.0, 8.2), hair(0, True), dens=4, faces=["north"])
    cb("Hair", (-3.6, 41.15, -3.6), (7.2, 0.5, 7.2), hair(), dens=2)
    cb("Hair", (-2.5, 41.6, -2.5), (5.0, 0.35, 5.0), hair(), dens=2)
    cb("Hair", (-4.3, 32.4, -1.6), (0.6, 5.0, 5.9), hair(0.3))
    cb("Hair", (3.7, 32.4, -1.6), (0.6, 5.0, 5.9), hair(0.3))
    cb("Hair", (-4.2, 31.2, 3.9), (8.4, 10.0, 0.6), hair(0.15, True))
    # пряди поверх «шапки» по бокам и на затылке — рваный силуэт вместо коробки
    for sg in (-1, 1):
        zs = [-2.7, -1.35, 0.0, 1.35, 2.7, 4.1]
        for i in range(len(zs) - 1):
            x0 = 4.08 if i % 2 else 4.16
            yb = 32.8 - (i % 3) * 0.5
            lo, hi = (-(x0 + 0.4), -x0) if sg < 0 else (x0, x0 + 0.4)
            cb("Hair", (lo, yb, zs[i]), (0.4, 41.0 - yb, zs[i + 1] - zs[i]), hair(0.25, True), dens=2,
               faces=["east", "west", "north", "south", "down"], seed=60 + i + (10 if sg > 0 else 0))
    for i, x in enumerate([-4.1, -2.75, -1.4, -0.05, 1.3, 2.65]):
        yb = 31.0 - (i % 2) * 0.6
        cb("Hair", (x, yb, 4.5 + (0.15 if i % 2 else 0)), (1.45, 41.0 - yb, 0.4), hair(0.2, True), dens=2,
           faces=["south", "east", "west", "down"], seed=80 + i)
    # чёлка: ровный срез, пряди разной глубины
    bang_x = [-4.15, -3.2, -2.3, -1.38, -0.46, 0.46, 1.38, 2.3, 3.2, 4.15]
    for i in range(len(bang_x) - 1):
        z0 = -4.42 if i % 2 else -4.3
        yb = 37.12 + (0.08 if i % 3 == 1 else 0)
        cb("front_h1", (bang_x[i], yb, z0), (bang_x[i + 1] - bang_x[i], 41.1 - yb, 0.32), hair(0.0, True), dens=4,
           faces=["north", "east", "west", "down", "up"], seed=40 + i)
    # боковые пряди (химэ): до груди, три слоя на сторону
    for sg in (-1, 1):
        def X(a, b):
            return (min(sg * a, sg * b), max(sg * a, sg * b))
        for (xa, xb), (yt, yb), (z0, z1), tip in ((X(3.7, 4.55), (38.4, 28.6), (-4.25, -3.05), 0.4),
                                                  (X(3.75, 4.6), (38.2, 29.8), (-3.05, -1.85), 0.38),
                                                  (X(3.8, 4.65), (37.8, 31.0), (-1.85, -0.45), 0.35)):
            strand(G, "corner_h1", xa, xb, yt, yb, z0, z1, tip)
    # пряди за плечами
    for sg in (-1, 1):
        lo, hi = (-4.95, -3.95) if sg < 0 else (3.95, 4.95)
        strand(G, "hl_side", lo, hi, 33.0, 25.8, 0.4, 1.9, 0.4)
        strand(G, "hl_side", lo + 0.1 * sg, hi + 0.1 * sg, 33.0, 26.6, 1.9, 3.5, 0.4)
    # длинная масса сзади: 8 прядей разной длины, в два слоя
    xs = [-4.45, -3.35, -2.2, -1.1, 0.0, 1.1, 2.2, 3.35, 4.45]
    lens = [24.6, 23.6, 23.0, 22.6, 22.8, 23.2, 23.8, 24.8]
    for i in range(8):
        z0 = 4.1 + (0.18 if i % 2 else 0)
        strand(G, "hl_m", xs[i], xs[i + 1], 31.6, lens[i], z0, z0 + 0.55, 0.4, band=False)
    for i in range(0, 8, 2):
        strand(G, "hl_m", xs[i] + 0.3, xs[i + 1] + 0.6, 31.4, lens[i] + 1.4, 4.75, 5.15, 0.45)
    # ободок: чёрная лента через макушку, белое кружево по переднему краю
    G.bone("Headband", "Hair", (0, 39, -1))
    cb("Headband", (-3.7, 41.65, -1.3), (7.4, 0.35, 1.0), "black")
    cb("Headband", (-4.85, 37.6, -1.3), (0.35, 4.1, 1.0), "black")
    cb("Headband", (4.5, 37.6, -1.3), (0.35, 4.1, 1.0), "black")
    cb("Headband", (-3.7, 41.7, -1.65), (7.4, 0.4, 0.35), "lace", dens=4)
    cb("Headband", (-4.9, 37.6, -1.65), (0.45, 4.1, 0.35), "lace", dens=4)
    cb("Headband", (4.45, 37.6, -1.65), (0.45, 4.1, 0.35), "lace", dens=4)

    # ================= корпус: свитер, высокий пояс юбки, ремень с ромбовой пряжкой
    cb("UpperBody", (-3.5, 23.9, -2.025), (7, 7.85, 4.05), "sweater")
    cb("UpperBody", (-2.7, 30.15, -2.125), (5.4, 2, 4.2), "sweater")
    cb("UpperBody", (-4, 31.575, -1.9), (8, 1, 4.2), "sweater")
    cb("UpperBody", (-3.6, 20.6, -2.12), (7.2, 3.4, 4.24), "pleat")
    cb("UpperBody", (-3.75, 23.55, -2.25), (7.5, 0.85, 4.5), "belt")
    G.bone("Buckle", "UpperBody", (0, 23.98, -2.35))
    cb("Buckle", (-0.75, 23.23, -2.5), (1.5, 1.5, 0.3), "silver", rot=(0, 0, 45), pivot=(0, 23.98, -2.35))
    cb("Buckle", (-0.4, 23.58, -2.6), (0.8, 0.8, 0.25), "violet", rot=(0, 0, 45), pivot=(0, 23.98, -2.35), share="swatch_violet")
    cb("Breast", (-3.4, 27.3, -3.05), (6.8, 2.9, 2.7), "sweater")
    # бейдж на клипсе (левая сторона груди)
    G.bone("Badge", "Breast", (1.7, 29.2, -3.2))
    cb("Badge", (0.95, 26.7, -3.3), (1.6, 2.3, 0.18), "badge", dens=5, faces=["north", "south", "east", "west", "up", "down"])
    cb("Badge", (1.5, 28.85, -3.4), (0.5, 0.75, 0.3), "silver")
    # высокий ворот и бант
    G.bone("Neck", "UpperBody", (0, 31.5, 0))
    cb("Neck", (-2.05, 31.4, -2.05), (4.1, 2.45, 4.0), "sweater")
    cb("Neck", (-2.2, 33.3, -2.2), (4.4, 0.6, 4.3), "cuff", dens=3)
    G.bone("Bow", "Neck", (0, 31.4, -2.3))
    cb("Bow", (-0.5, 30.9, -2.65), (1.0, 1.0, 0.5), "satin", dens=4)
    cb("Bow", (-2.4, 30.8, -2.5), (1.95, 1.3, 0.35), "satin", rot=(0, 0, -14), pivot=(-0.5, 31.4, -2.4), dens=4)
    cb("Bow", (0.45, 30.8, -2.5), (1.95, 1.3, 0.35), "satin", rot=(0, 0, 14), pivot=(0.5, 31.4, -2.4), dens=4)
    cb("Bow", (-1.0, 28.0, -2.55), (0.7, 2.9, 0.15), "satin", rot=(0, 0, -12), pivot=(-0.35, 30.9, -2.5), dens=4)
    cb("Bow", (0.3, 28.2, -2.55), (0.7, 2.7, 0.15), "satin", rot=(0, 0, 14), pivot=(0.35, 30.9, -2.5), dens=4)

    # ================= руки
    cb("LeftArm", (2.45, 24.9, -1.7), (3.25, 7.3, 3.4), "sweater")
    cb("LeftArm", (2.8, 24.125, -1.125), (2.7, 2.05, 2.3), "sweater")
    cb("LeftForeArm", (2.525, 20.375, -1.575), (3.2, 5, 3.2), "sweater")
    cb("LeftForeArm", (2.625, 18.075, -1.475), (3, 2.4, 3), "hand")
    cb("LeftForeArm", (2.35, 19.85, -1.75), (3.55, 0.9, 3.55), "cuff", dens=3)
    cb("LeftForeArm", (2.22, 19.15, -1.88), (3.81, 0.85, 3.81), "cuff", dens=3)
    G.mirror("LeftArm", "RightArm"); G.mirror("LeftForeArm", "RightForeArm")

    # ================= ноги: стройные, колготки с бликом, туфли с бантом и ремешком-крестом
    cb("LeftLeg", (0.6, 11.65, -1.6), (3.1, 10, 3.2), tights(top=True))
    cb("LeftLowerLeg", (0.72, 2.0, -1.45), (2.86, 10.0, 2.9), tights(knee=True))
    cb("LeftLowerLeg", (1.25, 10.7, -1.62), (1.8, 1.5, 0.2), "tights", faces=["north", "east", "west", "up", "down"])
    cb("LeftFoot", (0.55, 0, -1.95), (3.3, 2.1, 3.9), "shoe")
    cb("LeftFoot", (0.62, 0, -4.05), (3.16, 1.55, 2.2), "shoe")
    cb("LeftFoot", (0.95, 0, -4.35), (2.5, 1.25, 0.35), "shoe")
    cb("LeftFoot", (0.9, 0, 1.75), (2.7, 0.9, 0.5), "black")
    cb("LeftFoot", (0.55, 1.5, -3.6), (3.3, 0.3, 0.3), "black", rot=(0, 28, 0), pivot=(2.2, 1.65, -3.45))
    cb("LeftFoot", (0.55, 1.5, -3.6), (3.3, 0.3, 0.3), "black", rot=(0, -28, 0), pivot=(2.2, 1.65, -3.45))
    cb("LeftFoot", (1.3, 1.25, -4.55), (0.8, 0.75, 0.3), "satin", rot=(0, 0, 15), pivot=(2.1, 1.6, -4.4))
    cb("LeftFoot", (2.1, 1.25, -4.55), (0.8, 0.75, 0.3), "satin", rot=(0, 0, -15), pivot=(2.1, 1.6, -4.4))
    cb("LeftFoot", (1.85, 1.4, -4.62), (0.5, 0.45, 0.3), "black")
    G.mirror("LeftLeg", "RightLeg"); G.mirror("LeftLowerLeg", "RightLowerLeg"); G.mirror("LeftFoot", "RightFoot")

    # ================= юбка: 18 панелей плиссировки + фиолетовая оборка
    ring(G, "Skirt", "SkirtP", 20, 22.6, 6.8, 3.95, 2.55, 1.65, 0.18, 21, "pleat", dens=3)
    ring(G, "Skirt", "SkirtF", 20, 17.6, 2.6, 5.85, 4.3, 2.1, 0.14, 26, "frill", dens=3, phase=0.5)

    # ================= подсумки и ремешки
    G.bone("Gear", "UpperBody", (0, 23, 0))
    cb("Gear", (3.65, 20.3, -2.0), (1.15, 1.9, 1.35), "pouch", dens=3)
    cb("Gear", (3.75, 20.6, -0.1), (1.0, 1.6, 1.25), "pouch", dens=3)
    cb("Gear", (-5.4, 19.0, -1.85), (1.8, 3.8, 3.4), "pouch", dens=3)
    cb("Gear", (-5.55, 21.9, -0.4), (0.25, 1.4, 0.5), "silver")
    G.bone("Straps", "Gear", (0, 23.6, 0))
    for i, (x, z, ln) in enumerate([(-4.75, -2.05, 5.0), (-3.85, 2.3, 6.2), (4.25, 1.55, 4.6), (2.85, -2.5, 3.6),
                                    (-2.6, -2.45, 2.8), (1.2, 2.35, 5.4)]):
        cb("Straps", (x, 23.6 - ln, z), (0.35, ln, 0.15), "strap", dens=3)

    # ================= плащ: накидка на плечах (чёрная отделка) + длинный плащ сегментами с качанием
    G.bone("Cape", "UpperBody", (0, 32, 0))
    cb("Cape", (-4.75, 31.7, -2.7), (9.5, 0.6, 5.45), "black")
    H4 = wedges(5, 0.28)
    cb("Cape", (-5.5, 27.2, 2.95), (11.0, 4.6, 0.18), cape(H4, (True, True, True)), faces=["north", "south"])
    cb("Cape", (1.9, 27.2, -2.95), (3.65, 4.6, 0.18), cape(wedges(2, 0.3, 0.25), (True, False, True)), faces=["north", "south"])
    cb("Cape", (-5.55, 27.2, -2.95), (3.65, 4.6, 0.18), cape(wedges(2, 0.3, 0.75), (True, True, False)), faces=["north", "south"])
    cb("Cape", (5.5, 27.2, -2.95), (0.18, 4.6, 5.9), cape(wedges(3, 0.3), (True, True, True)), faces=["east", "west"])
    cb("Cape", (-5.68, 27.2, -2.95), (0.18, 4.6, 5.9), cape(wedges(3, 0.3, 0.5), (True, True, True)), faces=["east", "west"])
    # спинка: три сегмента
    G.bone("CapeBack", "Cape", (0, 31.8, 2.8))
    cb("CapeBack", (-5.7, 22.0, 2.75), (11.4, 9.8, 0.16), cape(None, (False, True, True)), faces=["north", "south"])
    G.bone("CapeBackMid", "CapeBack", (0, 22.0, 2.83))
    cb("CapeBackMid", (-6.2, 14.0, 2.75), (12.4, 8.0, 0.16), cape(None, (False, True, True)), faces=["north", "south"])
    G.bone("CapeBackLow", "CapeBackMid", (0, 14.0, 2.83))
    cb("CapeBackLow", (-6.7, 4.5, 2.75), (13.4, 9.5, 0.16), cape(wedges(3, 0.42, 0.1, 0.22), (False, True, True)), faces=["north", "south"])
    # полы спереди: левая длиннее
    for side, x0, x1, ln, ph in (("L", 2.7, 6.0, 15.5, 0.15), ("R", -6.0, -2.7, 10.5, 0.7)):
        G.bone(f"CapeFront{side}", "Cape", ((x0 + x1) / 2, 31.8, -2.9))
        cb(f"CapeFront{side}", (x0, 22.0, -2.95), (x1 - x0, 9.8, 0.16), cape(None, (False, side == "R", side == "L")), faces=["north", "south"])
        G.bone(f"CapeFront{side}Low", f"CapeFront{side}", ((x0 + x1) / 2, 22.0, -2.9))
        cb(f"CapeFront{side}Low", (x0 - 0.3, 22.0 - ln, -2.95), (x1 - x0 + 0.6, ln, 0.16),
           cape(wedges(1, 0.5, ph), (False, side == "R", side == "L")), faces=["north", "south"])
    for side, x, ln in (("L", 6.05, 17.0), ("R", -6.21, 14.0)):
        G.bone(f"CapeSide{side}", "Cape", (x, 27.2, 0))
        cb(f"CapeSide{side}", (x, 27.2 - ln, -2.9), (0.16, ln, 5.75), cape(wedges(2, 0.4, 0.3 if side == "L" else 0.8)), faces=["east", "west"])

    # ================= нимб-ромб над головой (глитч) и осколок Originium у плеча
    G.bone("ysmGlowHalo", "Head", (0, 45.2, 0.6))
    halo(G, "ysmGlowHalo", (0, 45.2, 0.6), 1.9)
    G.bone("ysmGlowHaloGlitch", "ysmGlowHalo", (0, 45.2, 0.6))
    cb("ysmGlowHaloGlitch", (-2.9, 45.9, 0.5), (1.5, 0.3, 0.2), "glitch", share="swatch_glitch")
    cb("ysmGlowHaloGlitch", (1.3, 44.0, 0.5), (1.9, 0.25, 0.2), "glitch", share="swatch_glitch")
    cb("ysmGlowHaloGlitch", (-0.6, 47.7, 0.5), (1.1, 0.25, 0.2), "glitch", share="swatch_glitch")
    # спутник: чёрный осколок с маленьким светящимся ядром, рой чёрных обломков вокруг; летит по наклонной орбите
    G.bone("ShardOrbit", "Root", (0, 30, 0))
    G.bone("Shard", "ShardOrbit", (-11, 30, 1.5))
    shard(G, "Shard", (-11, 30, 1.5), 1.3)
    swarm(G, "Shard", (-11, 30, 1.5), 1.0)

    # ================= формы осколка в руках
    for side in ("R", "L"):
        hand = "RightHand" if side == "R" else "LeftHand"
        hp = G.byname[hand]["pivot"]
        root = f"Forms{side}"
        G.bone(root, hand, hp)
        for f in FORMS:
            G.bone(f"Pr{side}_{f}", root, hp)
            G.bone(f"ysmGlow{side}_{f}Core", f"Pr{side}_{f}", hp)
        if side == "R":
            forms(G, "R", hp)
        else:
            for f in FORMS:
                G.mirror(f"PrR_{f}", f"PrL_{f}"); G.mirror(f"ysmGlowR_{f}Core", f"ysmGlowL_{f}Core")

    # ================= реквизит поз
    lp = G.byname["LeftHand"]["pivot"]; rp = G.byname["RightHand"]["pivot"]
    G.bone("Clipboard", "LeftHand", lp)
    cb("Clipboard", (lp[0] - 2.2, lp[1] - 1.2, lp[2] - 3.6), (3.4, 0.3, 4.4), "clip_board", dens=3)
    G.bone("OfferShard", "RightHand", (rp[0], rp[1] + 3.2, rp[2] - 2.6))
    shard(G, "OfferShard", (rp[0], rp[1] + 3.2, rp[2] - 2.6), 0.7)
    G.bone("Pencil", "RightHand", rp)
    cb("Pencil", (rp[0] - 0.15, rp[1] - 1.0, rp[2] - 3.2), (0.3, 0.3, 3.0), "pencil", share="swatch_pencil")
    return G


def diamond(G, bone, c, w, h, d, mat="crystal", share="swatch_crystal", tilt=0):
    """ромб (куб, повёрнутый на 45° вокруг Z), центр c, «ширина» w, глубина d; вытянутость — двумя кубами"""
    x, y, z = c
    G.cube(bone, (x - w / 2, y - w / 2, z - d / 2), (w, w, d), mat, rot=(0, tilt, 45), pivot=c, share=share)
    if h > w:  # вытянутый кристалл: верх и низ — ещё по ромбу поменьше
        k = (h - w) / 2
        s = w * 0.7
        G.cube(bone, (x - s / 2, y + k * 0.8 - s / 2, z - d / 2 * 0.8), (s, s, d * 0.8), mat, rot=(0, tilt, 45), pivot=(x, y + k * 0.8, z), share=share)
        G.cube(bone, (x - s / 2, y - k * 0.8 - s / 2, z - d / 2 * 0.8), (s, s, d * 0.8), mat, rot=(0, tilt, 45), pivot=(x, y - k * 0.8, z), share=share)


def core(G, parent, c, k, name=None):
    """светящееся ядро: белый ромб-контур и оранжево-красное свечение внутри (кость ysmGlow…)"""
    x, y, z = c
    bn = name or f"ysmGlow{parent}Core"
    G.bone(bn, parent, c)
    G.cube(bn, (x - 0.5 * k, y - 0.5 * k, z - 0.04), (k, k, 0.08), "rim", rot=(0, 0, 45), pivot=c, share="swatch_rim")
    G.cube(bn, (x - 0.33 * k, y - 0.33 * k, z - 0.08), (0.66 * k, 0.66 * k, 0.16), "core", rot=(0, 0, 45), pivot=c, share="swatch_core")
    return bn


def halo(G, bone, c, r):
    """нимб: ромб-контур с оранжево-красным свечением (глитч — в parallel-анимации)"""
    x, y, z = c
    G.cube(bone, (x - 0.6 * r, y - 0.6 * r, z - 0.05), (1.2 * r, 1.2 * r, 0.1), "rim", rot=(0, 0, 45), pivot=c, share="swatch_rim")
    G.cube(bone, (x - 0.45 * r, y - 0.45 * r, z - 0.1), (0.9 * r, 0.9 * r, 0.2), "crystal", rot=(0, 0, 45), pivot=c, share="swatch_crystal")
    G.cube(bone, (x - 0.22 * r, y - 0.22 * r, z - 0.15), (0.44 * r, 0.44 * r, 0.3), "core", rot=(0, 0, 45), pivot=c, share="swatch_core")


def shard(G, bone, c, k):
    """осколок Originium: чёрный угловатый кристалл (высота ≈2× ширины, зазубренный силуэт), дымка внизу,
    маленькое светящееся ядро спереди и сзади; сам кристалл не светится"""
    x, y, z = c
    S = "swatch_crystal"
    G.cube(bone, (x - 0.75 * k, y - 1.6 * k, z - 0.75 * k), (1.5 * k, 3.2 * k, 1.5 * k), "crystal", rot=(0, 45, 6), pivot=c, share=S)
    G.cube(bone, (x - 0.45 * k, y + 1.2 * k, z - 0.45 * k), (0.9 * k, 0.9 * k, 0.9 * k), "crystal", rot=(35, 45, 0), pivot=(x, y + 1.6 * k, z), share=S)
    G.cube(bone, (x - 1.2 * k, y + 0.2 * k, z - 0.35 * k), (0.7 * k, 1.9 * k, 0.7 * k), "crystal", rot=(0, 45, 28), pivot=(x - 0.6 * k, y + 0.4 * k, z), share=S)
    G.cube(bone, (x + 0.5 * k, y - 1.0 * k, z - 0.3 * k), (0.6 * k, 1.5 * k, 0.6 * k), "crystal", rot=(0, 45, -32), pivot=(x + 0.6 * k, y - 0.6 * k, z), share=S)
    G.cube(bone, (x - 0.2 * k, y - 2.2 * k, z - 0.2 * k), (0.4 * k, 0.9 * k, 0.4 * k), "crystal", rot=(0, 45, 12), pivot=(x, y - 1.6 * k, z), share=S)
    G.cube(bone, (x - 0.9 * k, y - 2.3 * k, z - 0.9 * k), (1.8 * k, 0.8 * k, 1.8 * k), "smoke", share="swatch_smoke")
    G.cube(bone, (x - 0.6 * k, y - 2.8 * k, z - 0.6 * k), (1.2 * k, 0.6 * k, 1.2 * k), "smoke", share="swatch_smoke")
    core(G, bone, (x, y + 0.1 * k, z - 1.1 * k), 0.75 * k, f"ysmGlow{bone}CoreF")
    core(G, bone, (x, y + 0.1 * k, z + 1.1 * k), 0.75 * k, f"ysmGlow{bone}CoreB")


def swarm(G, parent, c, k):
    """рой мелких чёрных обломков вокруг осколка"""
    x, y, z = c
    G.bone("ShardSwarm", parent, c)
    for i, (r, a, dy, sz) in enumerate(((2.6, 0, 0.8, 0.5), (3.0, 50, -0.6, 0.35), (2.3, 105, 1.6, 0.3), (3.3, 160, -1.4, 0.45),
                                        (2.8, 215, 0.2, 0.28), (3.4, 260, 1.1, 0.38), (2.4, 305, -1.0, 0.25), (3.1, 340, -2.0, 0.3))):
        px, pz = x + r * k * math.cos(math.radians(a)), z + r * k * math.sin(math.radians(a))
        s2 = sz * k
        G.cube("ShardSwarm", (px - s2 / 2, y + dy * k - s2, pz - s2 / 2), (s2, s2 * 2, s2), "crystal",
               rot=(20 * (i % 3), 45, 15 * (i % 4)), pivot=(px, y + dy * k, pz), share="swatch_crystal")


def forms(G, side, hp):
    """Формы осколка в правой руке. Локальная система: рукоять в кулаке, предмет вперёд (−Z) и вверх (+Y)."""
    hx, hy, hz = hp
    cr = "crystal"; S = "swatch_crystal"
    def B(f, mat):  # тёмные грани — в кости формы, светящееся ядро — в её ysmGlow-кости
        return f"ysmGlow{side}_{f}Core" if mat == "core" else f"Pr{side}_{f}"
    def cube(f, lo, hi, mat=cr, share=S, rot=None, piv=None):
        o, s = box((hx + lo[0], hy + lo[1], hz + lo[2]), (hx + hi[0], hy + hi[1], hz + hi[2]))
        G.cube(B(f, mat), o, s, mat, share=share, rot=rot,
               pivot=(hx + piv[0], hy + piv[1], hz + piv[2]) if piv else None)
    def dia(f, c, w, d, tilt=0):
        G.cube(B(f, cr), (hx + c[0] - w / 2, hy + c[1] - w / 2, hz + c[2] - d / 2), (w, w, d), cr, share=S,
               rot=(0, tilt, 45), pivot=(hx + c[0], hy + c[1], hz + c[2]))
    def rhx(f, c, w, d):  # ромб в плоскости YZ (поворот вокруг X)
        G.cube(B(f, cr), (hx + c[0] - d / 2, hy + c[1] - w / 2, hz + c[2] - w / 2), (d, w, w), cr, share=S,
               rot=(45, 0, 0), pivot=(hx + c[0], hy + c[1], hz + c[2]))
    g = (-0.3, -1.4, -0.3)  # рукоять сквозь кулак
    # меч: рукоять, гарда, клинок вдоль −Z, острие-ромб
    cube("sword", (-0.3, -1.1, 1.0), (0.3, -0.5, -1.2), "black", "swatch_grip")
    cube("sword", (-0.35, -1.9, -1.2), (0.35, 0.3, -1.7))
    cube("sword", (-0.18, -1.3, -1.7), (0.18, 0.0, -8.8))
    cube("sword", (-0.1, -1.05, -1.7), (0.1, -0.25, -8.6), "core", "swatch_core")
    rhx("sword", (0, -0.65, -8.9), 0.9, 0.3)
    # топор: древко + широкое лезвие сверху
    cube("axe", (-0.25, -1.05, 1.2), (0.25, -0.55, -6.2), "black", "swatch_grip")
    cube("axe", (-0.2, -0.6, -4.2), (0.2, 1.9, -6.6))
    rhx("axe", (0, 1.2, -6.4), 1.6, 0.35)
    cube("axe", (-0.25, 0.0, -4.6), (0.25, 1.5, -5.6), "core", "swatch_core")
    cube("axe", (-0.25, -1.4, -5.0), (0.25, -0.5, -6.0))
    # кирка: древко + изогнутая поперечина
    cube("pickaxe", (-0.25, -1.05, 1.2), (0.25, -0.55, -6.2), "black", "swatch_grip")
    cube("pickaxe", (-0.25, -1.3, -5.6), (0.25, 1.6, -6.6))
    cube("pickaxe", (-0.2, 1.4, -6.4), (0.2, 2.1, -4.6))
    cube("pickaxe", (-0.2, -3.6, -6.4), (0.2, -1.2, -5.8))
    rhx("pickaxe", (0, 2.0, -4.3), 0.7, 0.3); rhx("pickaxe", (0, -3.6, -6.0), 0.7, 0.3)
    cube("pickaxe", (-0.28, -0.6, -5.8), (0.28, 0.9, -6.4), "core", "swatch_core")
    # лопата
    cube("shovel", (-0.25, -1.05, 1.2), (0.25, -0.55, -5.4), "black", "swatch_grip")
    cube("shovel", (-0.15, -2.0, -5.4), (0.15, 0.4, -8.0))
    rhx("shovel", (0, -0.8, -8.0), 1.7, 0.3)
    cube("shovel", (-0.2, -1.3, -5.9), (0.2, -0.3, -7.6), "core", "swatch_core")
    # мотыга
    cube("hoe", (-0.25, -1.05, 1.2), (0.25, -0.55, -6.4), "black", "swatch_grip")
    cube("hoe", (-0.2, -0.6, -5.9), (0.2, 1.8, -6.6))
    cube("hoe", (-0.2, 1.3, -6.6), (0.2, 1.8, -4.9))
    cube("hoe", (-0.25, 0.4, -6.0), (0.25, 1.2, -6.5), "core", "swatch_core")
    # лук: вертикальная дуга перед кулаком + тетива
    for y0, y1, z in ((-1.4, 1.4, -2.9), (1.4, 3.6, -2.4), (-3.6, -1.4, -2.4), (3.6, 5.0, -1.8), (-5.0, -3.6, -1.8)):
        cube("bow", (-0.25, y0 - 0.9, z - 0.25), (0.25, y1 - 0.9, z + 0.25))
    cube("bow", (-0.06, -5.9, -1.5), (0.06, 4.1, -1.4), "core", "swatch_core")
    rhx("bow", (0, -0.9, -3.2), 0.8, 0.4)
    # арбалет: ложе вдоль −Z, плечи поперёк
    cube("crossbow", (-0.35, -1.3, 1.0), (0.35, -0.5, -5.2), "black", "swatch_grip")
    cube("crossbow", (-3.2, -0.9, -4.4), (3.2, -0.5, -4.9))
    cube("crossbow", (-0.25, -0.5, -0.5), (0.25, 0.0, -5.0))
    dia("crossbow", (0, -0.7, -5.4), 1.0, 0.4)
    cube("crossbow", (-2.6, -0.85, -4.55), (2.6, -0.55, -4.75), "core", "swatch_core")
    # щит: ромб-кристалл перед рукой
    G.cube(B("shield", cr), (hx - 0.4 - 1.0, hy - 0.5 - 2.6, hz - 2.6 - 2.6), (0.5, 5.2, 5.2), cr, share=S, rot=(45, 0, 0),
           pivot=(hx - 0.9, hy - 0.5, hz - 2.6))
    G.cube(B("shield", "core"), (hx - 0.45 - 1.1, hy - 0.5 - 1.2, hz - 2.6 - 1.2), (0.3, 2.4, 2.4), "core", share="swatch_core",
           rot=(45, 0, 0), pivot=(hx - 0.9, hy - 0.5, hz - 2.6))
    # копьё
    cube("spear", (-0.22, -1.0, 4.0), (0.22, -0.56, -11.0), "black", "swatch_grip")
    rhx("spear", (0, -0.78, -11.6), 1.4, 0.35)
    cube("spear", (-0.15, 0.0, -10.4), (0.15, 0.4, -12.2)); cube("spear", (-0.15, -1.9, -10.4), (0.15, -1.5, -12.2))
    G.cube(B("spear", "core"), (hx - 0.3, hy - 0.78 - 0.3, hz - 11.6 - 0.3), (0.6, 0.6, 0.6), "core", share="swatch_core", rot=(45, 0, 0),
           pivot=(hx, hy - 0.78, hz - 11.6))
    # посох/жезл: вертикальное древко, большой ромб наверху и два спутника
    cube("staff", (-1.85, -8.0, -2.1), (-1.35, 9.5, -1.6), "black", "swatch_grip")
    G.cube(B("staff", cr), (hx - 1.6 - 1.2, hy + 11.4 - 1.2, hz - 1.85 - 0.4), (2.4, 2.4, 0.8), cr, share=S, rot=(0, 0, 45),
           pivot=(hx - 1.6, hy + 11.4, hz - 1.85))
    G.cube(B("staff", "core"), (hx - 1.6 - 0.5, hy + 11.4 - 0.5, hz - 1.85 - 0.5), (1.0, 1.0, 1.0), "core", share="swatch_core", rot=(0, 0, 45),
           pivot=(hx - 1.6, hy + 11.4, hz - 1.85))
    cube("staff", (-2.4, 9.3, -2.25), (-0.8, 9.7, -1.45))
    dia("staff", (-3.6, 12.8, -1.85), 0.7, 0.3); dia("staff", (0.4, 10.0, -1.85), 0.7, 0.3)
    # фолиант: две кристальные пластины-«обложки» над ладонью
    cube("tome", (-1.6, 0.4, -1.0), (-0.1, 0.65, -3.6), cr, S, rot=(0, 0, -12), piv=(-0.1, 0.5, -2.3))
    cube("tome", (0.1, 0.4, -1.0), (1.6, 0.65, -3.6), cr, S, rot=(0, 0, 12), piv=(0.1, 0.5, -2.3))
    cube("tome", (-0.1, 0.3, -1.0), (0.1, 0.9, -3.6), "core", "swatch_core")
    dia("tome", (0, 2.0, -2.3), 0.6, 0.3)
    # еда/зелье: маленький кристальный флакон
    cube("food", (-0.8, -1.4, -1.6), (0.8, 0.9, -3.2))
    cube("food", (-0.35, 0.9, -2.05), (0.35, 1.6, -2.75), "core", "swatch_core")
    dia("food", (0, 2.3, -2.4), 0.6, 0.3)
    # прочее: парящая над ладонью гроздь из трёх ромбов
    dia("other", (0, 1.8, -2.2), 1.3, 0.6); dia("other", (-1.1, 3.0, -2.0), 0.8, 0.4); dia("other", (1.0, 3.4, -2.6), 0.65, 0.35)


# ======================================================================= анимации и логика
GLOW = "ysm.second_order('pr_glow', (ysm.swinging || q.is_using_item || (v.chat_glow ?? 0) > 0 || (v.roaming.pr_eyes ?? 0)) ? 1 : 0, 1.6, 1, 0)"


def form_vis(side, i):
    """масштаб кости формы i: плавно 0→1, когда активна; ванильный предмет скрыт, пока форма показана"""
    v = f"v.form_{side.lower()}"
    return f"ysm.second_order('pr_f{side}{i}', ({v} ?? 0) == {i} ? 1 : 0, 3, 1, 0)"


def parallels():
    A = {}
    # parallel1: нимб и осколок-компаньон (орбита/покачивание/вращение), глитч
    T = 12  # длина цикла (с): все периоды делят 12
    shard_on = "((v.form_r ?? 0) == 0 && (v.form_l ?? 0) == 0 && (v.roaming.pr_shard ?? 1) && !ctrl.playing_extra_animation) ? 1 : 0"
    A["parallel1"] = {"loop": True, "animation_length": T, "bones": {
        "ysmGlowHalo": {
            "rotation": [0, "q.anim_time * 30", 0],
            "position": [0, "math.sin(q.anim_time * 90) * 0.25", 0],
            "scale": f"(v.roaming.pr_halo ?? 1) * (1 + 0.25 * {GLOW})"},
        "ysmGlowHaloGlitch": {
            "position": ["math.mod(math.floor(q.anim_time * 10), 7) == 0 ? 0.6 : (math.mod(math.floor(q.anim_time * 10), 11) == 0 ? -0.5 : 0)", 0, 0],
            "scale": "math.mod(math.floor(q.anim_time * 10), 5) < 2 ? 1 : 0"},
        "ShardOrbit": {
            "rotation": ["math.sin(q.anim_time * 60) * 14", "90 + math.sin(q.anim_time * 30) * 95", 0],  # дуга: правое плечо → спина → левое, не перед лицом
            "position": [0, "math.sin(q.anim_time * 120) * 1.4", 0]},
        "Shard": {
            "rotation": ["math.sin(q.anim_time * 90) * 8", "q.anim_time * -45", 0],
            "scale": f"ysm.second_order('pr_shard', {shard_on}, 3, 1, 0)"},
        "ShardSwarm": {
            "rotation": ["math.sin(q.anim_time * 60) * 10", "q.anim_time * 120", 0],
            "position": [0, "math.sin(q.anim_time * 180) * 0.3", 0]},
    }}
    # parallel2: формы осколка в руках вместо ванильного предмета
    bones = {}
    for side, loc in (("R", "RightHandLocator"), ("L", "LeftHandLocator")):
        v = f"v.form_{side.lower()}"
        bones[loc] = {"scale": f"(({v} ?? 0) > 0 && !(v.roaming.pr_vanilla ?? 0)) ? 0 : 1"}
        for i, f in enumerate(FORMS, 1):
            bones[f"Pr{side}_{f}"] = {"scale": f"(v.roaming.pr_vanilla ?? 0) ? 0 : {form_vis(side, i)}"}
        bones[f"Forms{side}"] = {"position": [0, "math.sin(q.anim_time * 120) * 0.15", 0], "scale": 1.25}
    # лук: при натяжении «тетива» отходит назад
    bones["PrR_bow"]["position"] = [0, 0, "q.is_using_item ? math.min(q.item_in_use_duration, 1) * 0.8 : 0"]
    A["parallel2"] = {"loop": True, "animation_length": T, "bones": bones}
    # parallel3: глаза-ромбы — ярче/крупнее при атаке, использовании, ломании блока, сообщении в чат
    g = GLOW
    eye = {}
    for s in ("Right", "Left"):
        eye[f"ysmGlow{s}Pupil"] = {"scale": f"1 + 0.3 * {g}"}
        eye[f"ysmGlow{s}Rim"] = {"scale": f"1 + 0.3 * {g}"}  # тонкая белая обводка ромба видна всегда, при вспышке чуть шире
        eye[f"ysmGlow{s}Hot"] = {"scale": f"{g} > 0.05 ? (0.5 + 0.7 * {g}) : 0"}
    A["parallel3"] = {"loop": True, "bones": eye}
    # parallel4: физика плаща (как у волос default: скорость, вертикаль, поворот), ремешки
    sp = "math.clamp(q.ground_speed + (v.hg ?? 0), 0, 20)"
    vs = "math.clamp(-3 * q.vertical_speed, -10, 90)"
    A["parallel4"] = {"loop": True, "bones": {
        "CapeBackLow": {"rotation": [f"ysm.second_order('pr_cape_h', {sp}, 2, 0.6, 0) * 1.6 + ysm.second_order('pr_cape_v', {vs}, 2, 0.6, 0) * 0.6", 0,
                                     "ysm.second_order('pr_cape_y', math.clamp(0.02 * q.yaw_speed, -40, 40), 2, 0.6, 0)"]},
        "CapeBack": {"rotation": [f"ysm.second_order('pr_cape_h2', {sp}, 2, 0.6, 0) * 0.4", 0, 0]},
        "CapeBackMid": {"rotation": [f"ysm.second_order('pr_cape_m', {sp}, 2, 0.5, 0) * 1.0", 0, 0]},
        "CapeFrontLLow": {"rotation": [f"-ysm.second_order('pr_capef', {sp}, 2, 0.6, 0) * 0.9 - 4", 0, 0]},
        "CapeFrontRLow": {"rotation": [f"-ysm.second_order('pr_capef', {sp}, 2, 0.6, 0) * 0.9 - 4", 0, 0]},
        "CapeSideL": {"rotation": [f"ysm.second_order('pr_cape_h', {sp}, 2, 0.6, 0) * 0.8", 0, -4]},
        "CapeSideR": {"rotation": [f"ysm.second_order('pr_cape_h', {sp}, 2, 0.6, 0) * 0.8", 0, 4]},
        "Straps": {"rotation": [f"ysm.second_order('pr_strap', {sp}, 3, 0.4, 0) * 1.2", 0, 0]},
    }}
    return A


def pre_parallels():
    # реквизит позы «записи» скрыт везде, кроме своей позы (pre_parallel ниже приоритетом, чем поза колеса)
    return {"pre_parallel2": {"loop": True, "bones": {"Clipboard": {"scale": 0}, "Pencil": {"scale": 0}, "OfferShard": {"scale": 0}}}}


def kf(d):
    return {f"{t:g}" if t != int(t) else f"{t:.1f}": v for t, v in d.items()}


def extras():
    """Позы колеса (extra): записи с планшетом, протянутая ладонь с осколком, реверанс, глитч-явление."""
    E = {}
    E["pr_notes"] = {"loop": True, "animation_length": 4, "bones": {
        "LeftArm": {"rotation": [-38, 18, -6]}, "LeftForeArm": {"rotation": [-62, 0, 0]},
        "RightArm": {"rotation": [-30, -22, 8]},
        "RightForeArm": {"rotation": kf({0: [-58, 0, 0], 1: [-62, -6, 0], 2: [-58, 4, 0], 3: [-64, -3, 0], 4: [-58, 0, 0]})},
        "Head": {"rotation": [18, 6, 0]},
        "Clipboard": {"scale": 1}, "Pencil": {"scale": 1},
        "UpBody": {"rotation": [4, 0, 0]},
    }}
    E["pr_offer"] = {"loop": True, "animation_length": 4, "bones": {
        "RightArm": {"rotation": [-72, -12, 6]}, "RightForeArm": {"rotation": [-12, 0, 0]},
        "LeftArm": {"rotation": [0, 0, -6]},
        "Head": {"rotation": [6, -10, 0]},
        "OfferShard": {"scale": 1, "position": kf({0: [0, 0, 0], 2: [0, 0.8, 0], 4: [0, 0, 0]}),
                              "rotation": [0, "q.anim_time * 90", 0]},
    }}
    E["pr_curtsy"] = {"loop": "hold_on_last_frame", "animation_length": 2.0, "bones": {
        "UpBody": {"rotation": kf({0: [0, 0, 0], 0.6: [14, 0, 0], 1.6: [14, 0, 0], 2.0: [8, 0, 0]})},
        "Head": {"rotation": kf({0: [0, 0, 0], 0.6: [16, 0, 0], 2.0: [10, 0, 0]})},
        "LeftArm": {"rotation": kf({0: [0, 0, 0], 0.6: [-14, 0, -24], 2.0: [-10, 0, -20]})},
        "RightArm": {"rotation": kf({0: [0, 0, 0], 0.6: [-14, 0, 24], 2.0: [-10, 0, 20]})},
        "LeftLeg": {"rotation": kf({0: [0, 0, 0], 0.6: [-12, 0, 0], 2.0: [-8, 0, 0]})},
        "LeftLowerLeg": {"rotation": kf({0: [0, 0, 0], 0.6: [30, 0, 0], 2.0: [20, 0, 0]})},
        "RightLeg": {"rotation": kf({0: [0, 0, 0], 0.6: [22, 0, 0], 2.0: [14, 0, 0]})},
        "RightLowerLeg": {"rotation": kf({0: [0, 0, 0], 0.6: [24, 0, 0], 2.0: [16, 0, 0]})},
        "Root": {"position": kf({0: [0, 0, 0], 0.6: [0, -1.4, 0], 2.0: [0, -0.9, 0]})},
    }}
    E["pr_glitch"] = {"loop": True, "animation_length": 3, "bones": {
        "LeftArm": {"rotation": [-10, 0, -62]}, "RightArm": {"rotation": [-10, 0, 62]},
        "LeftForeArm": {"rotation": [-18, 0, 0]}, "RightForeArm": {"rotation": [-18, 0, 0]},
        "Head": {"rotation": [-12, 0, "math.mod(math.floor(q.anim_time * 8), 6) == 0 ? 6 : 0"]},
        "ysmGlowHalo": {"scale": 1.8, "position": [0, 1.2, 0]},
        "ysmGlowRightRim": {"scale": 1.2}, "ysmGlowLeftRim": {"scale": 1.2},
        "ysmGlowRightHot": {"scale": 1.4}, "ysmGlowLeftHot": {"scale": 1.4},
        "Root": {"position": [0, "0.6 + math.sin(q.anim_time * 120) * 0.3", 0]},
        "LeftFoot": {"rotation": [30, 0, 0]}, "RightFoot": {"rotation": [30, 0, 0]},
    }}
    return E


def form_functions():
    """functions/*.molang: определение формы по тегам предмета (свои теги mm:ysm_form/* — KubeJS) и ванильным/forge."""
    tags = {
        "sword": ["mm:ysm_form/sword", "minecraft:swords", "forge:tools/swords"],
        "axe": ["mm:ysm_form/axe", "minecraft:axes", "forge:tools/axes"],
        "pickaxe": ["mm:ysm_form/pickaxe", "minecraft:pickaxes", "forge:tools/pickaxes"],
        "shovel": ["mm:ysm_form/shovel", "minecraft:shovels", "forge:tools/shovels"],
        "hoe": ["mm:ysm_form/hoe", "minecraft:hoes", "forge:tools/hoes"],
        "bow": ["mm:ysm_form/bow", "forge:tools/bows"],
        "crossbow": ["mm:ysm_form/crossbow", "forge:tools/crossbows"],
        "shield": ["mm:ysm_form/shield", "forge:tools/shields"],
        "spear": ["mm:ysm_form/spear", "forge:tools/tridents"],
        "staff": ["mm:ysm_form/staff"],
        "tome": ["mm:ysm_form/tome"],
        "food": ["mm:ysm_form/food"],
    }
    ids = {"bow": ["minecraft:bow"], "crossbow": ["minecraft:crossbow"], "shield": ["minecraft:shield"], "spear": ["minecraft:trident"],
           "food": ["minecraft:potion", "minecraft:milk_bucket", "minecraft:honey_bottle"]}
    out = {}
    for slot, var in (("mainhand", "r"), ("offhand", "l")):
        # результат: 0 — пусто, 1..N — FORMS по порядку
        lines = [f"!ysm.has_{slot} ? {{ return 0; }};"]
        for i, f in enumerate(FORMS, 1):
            if f == "other":
                continue
            conds = []
            if tags.get(f):
                conds.append("q.equipped_item_any_tag('%s', %s)" % (slot, ", ".join(f"'{t}'" for t in tags[f])))
            if ids.get(f):
                conds.append("q.is_item_name_any('%s', %s)" % (slot, ", ".join(f"'{t}'" for t in ids[f])))
            lines.append(f"({' || '.join(conds)}) ? {{ return {i}; }};")
        lines.append(f"return {FORMS.index('other') + 1};")
        out[f"pr_form_{var}.molang"] = "\n".join(lines) + "\n"
    out["pr_update@player_update.molang"] = (
        "v.form_r = fn.pr_form_r;\n"
        "v.form_l = fn.pr_form_l;\n")
    return out


# ======================================================================= arm.json (первое лицо)
def build_arm(default_arm, G):
    """Руки от первого лица: рукава свитера, манжеты, кисти. Предмет в первом лице — обычный (ванильный)."""
    A = Geo()
    for b in default_arm["minecraft:geometry"][0]["bones"]:
        A.bone(b["name"], b.get("parent"), b.get("pivot", [0, 0, 0]), b.get("rotation"))
    dx = 1.15 - 3.4  # смещение рук arm.json относительно main.json по X
    dy = 29.775 - 31.025
    cb = A.cube
    cb("LeftArm", (2.45 + dx, 24.9 + dy, -1.7), (3.25, 7.3, 3.4), "sweater")
    cb("LeftForeArm", (2.525 + dx, 20.375 + dy, -1.575), (3.2, 5, 3.2), "sweater")
    cb("LeftForeArm", (2.625 + dx, 18.075 + dy, -1.475), (3, 2.4, 3), "skin")
    cb("LeftForeArm", (2.35 + dx, 19.75 + dy, -1.75), (3.55, 0.9, 3.55), "cuff", dens=2)
    A.mirror("LeftArm", "RightArm"); A.mirror("LeftForeArm", "RightForeArm")
    # куб 1×1×1 в локаторах рук и границы — как в arm.json модели default
    for loc in ("LeftHandLocator", "RightHandLocator"):
        x, y, z = A.byname[loc]["pivot"]
        cb(loc, (x - 0.5, y - 0.5, z - 0.5), (1, 1, 1), "skin")
    return A


# ======================================================================= сборка
def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--out", default=str(OUT))
    ap.add_argument("--preview", default=None)
    a = ap.parse_args()
    out = pathlib.Path(a.out)
    rd = ysm_default()
    dgeo = json.loads(rd("models/main.json"))
    darm = json.loads(rd("models/arm.json"))
    G = build_main(dgeo)
    A = build_arm(darm, G)
    # общая текстура: грани руки первого лица — те же материалы; упаковываем вместе
    allf = Geo(); allf.faces = G.faces + A.faces
    img, used = allf.pack()
    print(f"текстура: занято строк {used}/{TEX}, граней {len(allf.faces)}")
    (out / "models").mkdir(parents=True, exist_ok=True)
    (out / "textures").mkdir(exist_ok=True); (out / "animations").mkdir(exist_ok=True); (out / "functions").mkdir(exist_ok=True)
    (out / "models" / "main.json").write_text(json.dumps(G.json(), ensure_ascii=False, separators=(",", ":")))
    (out / "models" / "arm.json").write_text(json.dumps(A.json("geometry.priestess_arm", (3, 4.5, [0, 1.75, 0])), ensure_ascii=False, separators=(",", ":")))
    img.save(out / "textures" / "priestess.png")
    # анимации: default (CC 0) + свои parallel/pre_parallel/extra
    main_an = json.loads(rd("animations/main.animation.json"))
    an = main_an["animations"]
    for k in ("parallel1", "parallel2", "parallel3", "parallel4"):
        an.pop(k, None)
    an.update(parallels()); an.update(pre_parallels())
    an["gui"] = copy.deepcopy(an["gui"])
    (out / "animations" / "main.animation.json").write_text(json.dumps(main_an, ensure_ascii=False, separators=(",", ":")))
    extra = {"format_version": "1.8.0", "animations": extras()}
    dex = json.loads(rd("animations/extra.animation.json"))["animations"]
    for k, v in dex.items():
        extra["animations"][f"classic_{k}"] = v
    (out / "animations" / "extra.animation.json").write_text(json.dumps(extra, ensure_ascii=False, separators=(",", ":")))
    for f in ("arm", "iss", "tac", "fp.arm"):
        (out / "animations" / f"{f}.animation.json").write_bytes(rd(f"animations/{f}.animation.json"))
    for name, text in form_functions().items():
        (out / "functions" / name).write_text(text)
    dysm = json.loads(rd("ysm.json"))
    ysm = {
        "spec": 2,
        "metadata": {
            "name": "Priestess",
            "tips": "Priestess (Arknights). Осколок Originium принимает форму предмета в руке; глаза-ромбы вспыхивают при атаке, использовании и сообщении в чат. Модель Minecraft_moded.",
            "license": {"type": "CC BY-NC 4.0", "desc": "Персонаж — Arknights (Hypergryph). Скелет и базовые анимации — встроенная модель YSM «default» (CC 0)."},
            "authors": [
                {"name": "Minecraft_moded (Поток 32, Claude)", "role": "модель, текстура, осколок, формы, позы",
                 "comment": "Сборка для друзей, некоммерческая"},
            ] + [{"name": x["name"], "role": f"{x['role']} (default, CC 0)"} for x in dysm["metadata"]["authors"]],
            "link": {"home": "https://github.com/limanov7kiy/minecraft_moded"},
        },
        "properties": {
            "height_scale": 0.7, "width_scale": 0.7,
            "extra_animation": {
                "extra0": "#pr_config", "extra1": "pr_notes", "extra2": "pr_offer", "extra3": "pr_curtsy", "extra4": "pr_glitch",
                "#classic": "Ещё позы (default)",
            },
            "extra_animation_buttons": [{
                "id": "pr_config", "name": "Настройки",
                "config_forms": [
                    {"type": "checkbox", "title": "Глаза светятся всегда", "description": "Ромбы в глазах горят постоянно, а не только при действии", "value": "v.roaming.pr_eyes"},
                    {"type": "checkbox", "title": "Обычные предметы", "description": "Показывать ванильный предмет в руке вместо формы осколка", "value": "v.roaming.pr_vanilla"},
                    {"type": "checkbox", "title": "Нимб", "description": "Светящийся ромб над головой", "value": "v.roaming.pr_halo"},
                    {"type": "checkbox", "title": "Осколок рядом", "description": "Осколок Originium парит рядом, пока руки пусты", "value": "v.roaming.pr_shard"},
                ]}],
            "extra_animation_classify": [{"id": "classic", "extra_animation": {f"extra{i}": f"classic_extra{i}" for i in range(8)}}],
            "preview_animation": "gui",
            "default_texture": "priestess",
            "merge_multiline_expr": True,
            "free": True,
        },
        "files": {"player": {
            "model": {"main": "models/main.json", "arm": "models/arm.json"},
            "animation": {"main": "animations/main.animation.json", "arm": "animations/arm.animation.json",
                          "extra": "animations/extra.animation.json", "irons_spell_books": "animations/iss.animation.json",
                          "tac": "animations/tac.animation.json",
                          "fp_arm": "animations/fp.arm.animation.json"},
            "animation_controllers": [],
            "texture": ["textures/priestess.png"]}},
    }
    (out / "ysm.json").write_text(json.dumps(ysm, ensure_ascii=False, indent=2) + "\n")
    print("готово:", out)


if __name__ == "__main__":
    main()
