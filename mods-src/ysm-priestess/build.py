#!/usr/bin/env python3
"""Модель Yes Steve Model «Priestess» (Arknights) — генератор.

Собирает папку модели pack/config/yes_steve_model/custom/priestess/ (формат YSM 2.x, папка):
  ysm.json, models/main.json + arm.json, textures/priestess.png (128×128, пиксель-арт),
  animations/*.animation.json, functions/*.molang.

Скелет (имена/иерархия/pivot костей) и базовые анимации (ходьба, бег, присед, плавание, полёт, сон,
езда, удары, удержание предметов, Iron's Spells, TACZ) — от встроенной модели YSM «default»
(лицензия CC 0; авторы: 哥斯拉, 端木一动不动, 甜粽子, 星屑海螺, 蓝玫瑰, 艾雷克亚) — так работают все состояния.
Геометрия, текстура, осколок Originium, формы предметов, глаза, плащ, позы колеса — свои (Minecraft_moded).

Использование: python3 build.py [--out <папка>] [--preview <png>]
Нужен jar YSM (ссылка — pack/mods/yes-steve-model.pw.toml), кэш — build/.
"""
import argparse, copy, hashlib, io, json, math, pathlib, re, sys, tomllib, urllib.request, zipfile
import numpy as np
from PIL import Image

HERE = pathlib.Path(__file__).resolve().parent
ROOT = HERE.parent.parent
OUT = ROOT / "pack" / "config" / "yes_steve_model" / "custom" / "priestess"
TEX = 128


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
    "skin": C("f6dfd4"), "skin_s": C("e8c3b6"), "skin_d": C("d5a597"), "blush": C("f2b1b0"), "mouth": C("a35c66"),
    "hair": C("2a2024"), "hair_l": C("3b2e33"), "hair_d": C("191316"), "hair_tip": C("1f2a55"), "hair_tip_l": C("2f3d78"),
    "white": C("f3f1f5"), "white_s": C("dcd8e2"), "white_d": C("bdb7c8"),
    "black": C("1d1b22"), "black_l": C("34313d"), "black_d": C("0f0e13"),
    "graphite": C("3b3a44"), "graph_l": C("55535f"),
    "purple": C("6d3fa8"), "purple_l": C("9467d0"), "purple_d": C("4a2a78"),
    "tights": C("3a2652"), "tights_l": C("4d3369"), "tights_d": C("2a1b3c"),
    "red": C("d8243f"), "red_l": C("ff5a6e"), "red_d": C("8e1028"), "core": C("fff3f5"),
    "violet": C("b06bff"), "violet_l": C("e2c4ff"), "eye": C("8d84a6"), "eye_d": C("5b5274"),
    "yellow": C("f1d04a"), "silver": C("c9c6d6"), "wood": C("3a2e2a"),
}


def fill(w, h, c):
    a = np.zeros((h, w, 4), np.float32); a[:] = c; return a


def noise(w, h, seed, amp=6.0):
    r = np.random.default_rng(seed)
    return (r.random((h, w, 1)) - 0.5) * amp


def shade_edges(a, top=None, bottom=None, left=None, right=None):
    if top is not None: a[0, :] = top
    if bottom is not None: a[-1, :] = bottom
    if left is not None: a[:, 0] = left
    if right is not None: a[:, -1] = right
    return a


def m_skin(w, h, face, s):
    a = fill(w, h, P["skin"]) + noise(w, h, s, 3)
    if face in ("down",): a[:] = P["skin_s"]
    return a


def m_sweater(w, h, face, s):
    """белый вязаный свитер в рубчик: вертикальные рубчики через пиксель"""
    a = fill(w, h, P["white"])
    if face in ("north", "south", "east", "west"):
        a[:, 1::2] = P["white_s"]
        a[-1, :] = P["white_d"]
    else:
        a[:] = P["white_s"]
    return a + noise(w, h, s, 4)


def m_cuff(w, h, face, s):
    """манжета-рюша: белая, нижний край зубчиками (прозрачные пиксели)"""
    a = fill(w, h, P["white"])
    if face in ("north", "south", "east", "west"):
        a[:, ::2] = P["white_s"]
        a[-1, 1::2, 3] = 0
        a[0, :] = P["white_d"]
    else:
        a[:] = P["white_s"]
    return a


def m_black(w, h, face, s):
    a = fill(w, h, P["black"]) + noise(w, h, s, 5)
    if face == "up": a[:] = P["black_l"]
    return a


def m_belt(w, h, face, s):
    a = fill(w, h, P["black"]); a[0, :] = P["black_l"]; return a


def m_silver(w, h, face, s):
    a = fill(w, h, P["silver"]); a[-1:, :] = P["white_d"]; return a


def m_skirt(w, h, face, s):
    """чёрная плиссировка: складки — светлые линии через 2 пикселя, к низу темнее"""
    a = fill(w, h, P["black"])
    if face in ("north", "south", "east", "west"):
        a[:, ::3] = P["black_l"]
        a[:, 2::3] = P["black_d"]
    return a + noise(w, h, s, 3)


def m_frill(w, h, face, s):
    """фиолетовая нижняя оборка с зубчатым краем"""
    a = fill(w, h, P["purple"])
    if face in ("north", "south", "east", "west"):
        a[:, ::2] = P["purple_l"]
        a[-1, ::2, 3] = 0
        a[0, :] = P["purple_d"]
    return a


def m_tights(w, h, face, s):
    a = fill(w, h, P["tights"]) + noise(w, h, s, 4)
    if face in ("north",):
        a[:, w // 2 - 0: w // 2 + 1] = P["tights_l"]  # блик по центру
    if face in ("east", "west"):
        a[:, 0] = P["tights_d"]
    return a


def m_shoe(w, h, face, s):
    """туфли: белые, верхняя часть — колготки (кубы стопы выше лодыжки)"""
    a = fill(w, h, P["white"]) + noise(w, h, s, 3)
    if face in ("north", "south", "east", "west"):
        k = max(1, int(round(h * 0.6)))
        a[:h - k] = P["tights"]
        a[h - k] = P["black"]  # кант
        a[-1] = P["white_d"]
    if face == "up":
        a[:] = P["tights"]
    if face == "down":
        a[:] = P["white_d"]
    return a


def m_toe(w, h, face, s):
    a = fill(w, h, P["white"]) + noise(w, h, s, 3)
    if face in ("north", "south", "east", "west"):
        a[-1] = P["white_d"]
    return a


def m_hair(w, h, face, s, tip_rows=0):
    """прямые тёмные волосы: вертикальные пряди, к концам — тёмно-синие"""
    r = np.random.default_rng(s)
    a = fill(w, h, P["hair"])
    for x in range(w):
        if r.random() < 0.45: a[:, x] = P["hair_l"]
        if r.random() < 0.2: a[:, x] = P["hair_d"]
    if tip_rows:
        for y in range(h - tip_rows, h):
            f = (y - (h - tip_rows) + 1) / tip_rows
            a[y] = a[y] * (1 - f) + P["hair_tip"] * f
            a[y, 1::3] = P["hair_tip_l"] * f + a[y, 1::3] * (1 - f)
    return a


def hair_tip(n):
    return lambda w, h, f, s: m_hair(w, h, f, s, tip_rows=(min(n, h) if f not in ("up", "down") else 0))


def m_hair_ends(w, h, face, s):
    """концы длинных прядей: нижние строки синие, низ рваный"""
    a = m_hair(w, h, face, s, tip_rows=max(2, h // 2) if face not in ("up", "down") else 0)
    if face in ("north", "south", "east", "west") and w > 2:
        r = np.random.default_rng(s + 7)
        for x in range(w):
            if r.random() < 0.4: a[-1, x, 3] = 0
    if face == "down":
        a[:] = P["hair_tip"]
    return a


def m_bangs_front(w, h, face, s):
    """передняя грань «шапки» волос: ровная чёлка-химэ, низ — прямая линия с лёгкими зубцами"""
    a = m_hair(w, h, face, s)
    if face == "north":
        a[-1, :] = P["hair_d"]
        a[-1, 2::5, 3] = 0
    return a


def m_head(w, h, face, s):
    """голова: лицо спереди (рот, румянец), остальное — под волосами"""
    a = fill(w, h, P["skin"]) + noise(w, h, s, 2)
    if face == "north":
        # 2 px на блок: лицо 14×14; рот — маленькая улыбка
        cx = w // 2
        my = int(h * 0.80)
        a[my, cx - 1:cx + 1] = P["mouth"]
        a[my - 1, cx - 2] = P["mouth"] * 0.6 + P["skin"] * 0.4
        a[my - 1, cx + 1] = P["mouth"] * 0.6 + P["skin"] * 0.4
        by = int(h * 0.62)
        a[by, 2:4] = P["blush"]; a[by, w - 4:w - 2] = P["blush"]
        a[h - 1, :] = P["skin_s"]
    elif face in ("east", "west", "south", "up"):
        a[:] = P["hair"]
    return a


def m_eye_white(w, h, face, s):
    """веко/белок: сверху густые ресницы, по бокам тёмный контур, снизу тонкая линия"""
    a = fill(w, h, P["white"])
    if face == "north":
        k = max(1, h // 4)
        a[:k, :] = P["black"]
        a[k:k + 1, :] = P["black_l"]
        a[:k + 2, 0] = P["black"]; a[:k + 2, -1] = P["black"]
        a[-1, 1:-1] = P["eye_d"] * 0.5 + P["white"] * 0.5
    else:
        a[:] = P["black"]
    return a


def m_iris(w, h, face, s):
    """серо-фиолетовая радужка: тёмный верх (тень века), светлеет книзу, блик"""
    a = fill(w, h, P["eye"])
    if face == "north":
        for y in range(h):
            f = y / max(1, h - 1)
            a[y] = P["eye_d"] * (1 - f) + (P["eye"] * 0.6 + P["violet_l"] * 0.4) * f
        a[0, :] = P["black"]
        if w > 2 and h > 3:
            a[1, 0] = P["white"]
    return a


def m_violet(w, h, face, s):
    a = fill(w, h, P["violet"])
    if face == "north" and w > 1 and h > 1:
        a[0, 0] = P["violet_l"]
    return a


def m_rim(w, h, face, s):
    a = fill(w, h, P["white"]); return a


def m_hot(w, h, face, s):
    return fill(w, h, P["violet_l"])


def m_crystal(w, h, face, s):
    """Originium: красный кристалл, белая сердцевина-блик, тёмные края"""
    a = fill(w, h, P["red"])
    a[0, :] = P["red_l"]; a[:, 0] = P["red_l"]
    a[-1, :] = P["red_d"]; a[:, -1] = P["red_d"]
    if w >= 3 and h >= 3:
        a[1:-1, 1:-1] = P["red"]
        a[h // 2, w // 2] = P["core"]
        a[1, 1] = P["core"]
    return a


def m_core(w, h, face, s):
    a = fill(w, h, P["core"]); a[-1, :] = P["red_l"]; return a


def m_glitch(w, h, face, s):
    a = fill(w, h, P["red_l"])
    a[::2, :] = P["white"]
    return a


def m_cape(w, h, face, s, trim_top=True, hem=None):
    """плащ: полупрозрачный белый, чёрная отделка по краям; hem — профиль подола (асимметричные клинья)"""
    a = fill(w, h, P["white"]); a[..., 3] = 205
    a[:, ::4, :3] = P["white_s"][:3]
    if face in ("up", "down"):
        a[:] = P["black"]; return a
    a[:, 0, :] = P["black"]; a[:, -1, :] = P["black"]
    if trim_top:
        a[0:2, :, :] = P["black"]
    if hem is not None:
        for x in range(w):
            u = x / max(1, w - 1)
            if face in ("south", "east"):
                u = 1 - u
            cut = int(round(hem(u) * h))  # сколько строк снизу срезано
            if cut > 0:
                a[h - cut:, x, 3] = 0
            y = h - cut - 1
            if 0 <= y < h:
                a[y, x] = P["black"]
                if y - 1 >= 0: a[y - 1, x] = P["black"]
    return a


def cape_mat(hem=None, trim_top=False):
    """материал плаща (плотность 2 px/блок задаётся у куба)"""
    return lambda w, h, f, s: m_cape(w, h, f, s, trim_top=trim_top, hem=hem)


def wedges(n, depth, phase=0.0, slope=0.0):
    """профиль подола: n острых клиньев глубиной depth (доля высоты), плюс наклон (асимметрия)"""
    def f(u):
        t = (u * n + phase) % 1.0
        tri = abs(t - 0.5) * 2  # 1 у краёв клина, 0 в острие
        return max(0.0, min(0.95, depth * tri + slope * u))
    return f


def m_badge(w, h, face, s):
    a = fill(w, h, P["white"])
    if face == "north":
        a[0, :] = P["black"]
        if h > 3:
            a[2, 1:-1] = P["graphite"]; a[3, 1:max(2, w - 2)] = P["graph_l"]
        a[-1, :] = P["white_d"]
    return a


def m_clip_board(w, h, face, s):
    a = fill(w, h, P["black"])
    if face == "up" and w > 3 and h > 3:
        a[1:-1, 1:-1] = P["graphite"]
        a[1:4, w - 4:w - 1] = P["yellow"]  # жёлтый стикер
        a[h // 2:, 2:w - 2:2] = P["graph_l"]
    return a


def m_pencil(w, h, face, s):
    a = fill(w, h, P["yellow"]); a[0] = P["black"]; return a


def m_strap(w, h, face, s):
    a = fill(w, h, P["black"]); a[:, 0] = P["black_l"]; return a


def m_pouch(w, h, face, s):
    a = fill(w, h, P["black"]) + noise(w, h, s, 6)
    if face in ("north", "south", "east", "west") and h > 2:
        a[0:2] = P["black_l"]  # клапан
        a[1, w // 2] = P["silver"]
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

    def cube(self, bone, origin, size, mat, rot=None, pivot=None, dens=1.0, inflate=0.0, faces=None, share=None,
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
            self.faces.append([c, f, pw, ph, m, seed + hash(f) % 97, key, False])
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
                    w = h = 6  # общий образец материала: все грани растягивают один квадрат 6×6
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

    def json(self, ident="geometry.priestess"):
        return {"format_version": "1.12.0", "minecraft:geometry": [{
            "description": {"identifier": ident, "texture_width": TEX, "texture_height": TEX,
                            "visible_bounds_width": 7, "visible_bounds_height": 9, "visible_bounds_offset": [0, 1.5, 0]},
            "bones": self.bones}]}


def box(lo, hi):
    """origin/size из двух углов"""
    a = [min(l, h) for l, h in zip(lo, hi)]
    return a, [abs(h - l) for l, h in zip(lo, hi)]


# ======================================================================= модель Priestess
FORMS = ["sword", "axe", "pickaxe", "shovel", "hoe", "bow", "crossbow", "shield", "spear", "staff", "tome", "food", "other"]


def build_main(default_geo):
    G = Geo()
    # скелет default: все кости с теми же именами/pivot/rotation, без кубов
    for b in default_geo["minecraft:geometry"][0]["bones"]:
        rot = b.get("rotation")
        if b["name"] in ("Collar", "Ribbon"):
            rot = None  # свои воротник/ободок без наклона
        G.bone(b["name"], b.get("parent"), b.get("pivot", [0, 0, 0]), rot)
    cb = G.cube

    # ---------------- голова и лицо
    cb("Head", (-3.5, 33.05, -3.5), (7, 7, 7), {"north": "head", "south": "head", "east": "head", "west": "head", "up": "head", "down": "skin"},
       dens=2, faces=["north"])
    cb("Head", (-3.5, 33.05, -3.5), (7, 7, 7), "head", faces=["south", "east", "west", "up", "down"])
    cb("AllHead", (-1.5, 31.925, -1.525), (3, 2, 3), "skin")
    # глаза: веко (белок+ресницы) → радужка → ромб-зрачок (ysmGlow) + белая обводка + «горячая» сердцевина
    for side, sx in (("Right", -1), ("Left", 1)):
        x0 = -3.5 if side == "Right" else 1.5
        cb(f"{side}Eyelid", (x0, 34.05, -3.53), (2, 2, 0.1), "eye_white", dens=4, faces=["north"])
        ix = -3.025 if side == "Right" else 1.625
        cb(f"{side}EyesBase", (ix, 34.05, -3.58), (1.4, 1.75, 0.1), "iris", dens=4, faces=["north"])
        cx = ix + 0.7
        pv = (cx, 34.9, -3.64)
        G.bone(f"ysmGlow{side}Rim", f"{side}EyesBase", pv)
        cb(f"ysmGlow{side}Rim", (cx - 0.42, 34.48, -3.63), (0.84, 0.84, 0.02), "rim", rot=(0, 0, 45), pivot=pv, faces=["north"], share="swatch_rim")
        G.bone(f"ysmGlow{side}Pupil", f"{side}EyesBase", pv)
        cb(f"ysmGlow{side}Pupil", (cx - 0.3, 34.6, -3.66), (0.6, 0.6, 0.02), "violet", rot=(0, 0, 45), pivot=pv, faces=["north"], share="swatch_violet")
        G.bone(f"ysmGlow{side}Hot", f"{side}EyesBase", pv)
        cb(f"ysmGlow{side}Hot", (cx - 0.14, 34.76, -3.68), (0.28, 0.28, 0.02), "hot", rot=(0, 0, 45), pivot=pv, faces=["north"], share="swatch_hot")
    # брови прячутся под чёлкой — кубы не нужны

    # ---------------- волосы (ровная чёлка-химэ, боковые пряди, прямые до груди, концы тёмно-синие)
    cb("Hair", (-3.85, 36.3, -3.85), (7.7, 4.25, 7.7), {"north": "bangs_front", "south": "hair", "east": "hair", "west": "hair", "up": "hair", "down": None},
       dens=2, faces=["north"])
    cb("Hair", (-3.85, 36.3, -3.85), (7.7, 4.25, 7.7), "hair", faces=["south", "east", "west", "up"])
    cb("Hair", (-3.0, 40.5, -3.0), (6, 0.4, 6), "hair", faces=["up", "north", "south", "east", "west"])
    # бока головы под «шапкой» (за боковыми прядями)
    cb("Hair", (-3.95, 32.2, -2.2), (0.45, 4.2, 6.1), "hair")
    cb("Hair", (3.5, 32.2, -2.2), (0.45, 4.2, 6.1), "hair")
    # затылок
    cb("Hair", (-3.9, 31.0, 3.5), (7.8, 5.4, 0.5), hair_tip(0))
    # боковые пряди химэ (corner_h1): до плеч, концы синие
    cb("corner_h1", (-4.35, 29.4, -3.95), (0.9, 7.0, 1.7), "hair_ends", seed=11)
    cb("corner_h1", (-4.15, 30.4, -2.3), (0.7, 6.0, 1.1), "hair_ends", seed=12)
    cb("corner_h1", (3.45, 29.4, -3.95), (0.9, 7.0, 1.7), "hair_ends", seed=13)
    cb("corner_h1", (3.45, 30.4, -2.3), (0.7, 6.0, 1.1), "hair_ends", seed=14)
    # пряди за плечами (hl_side)
    cb("hl_side", (-4.4, 26.2, 0.2), (1.0, 6.2, 3.2), "hair_ends", seed=21)
    cb("hl_side", (3.4, 26.2, 0.2), (1.0, 6.2, 3.2), "hair_ends", seed=22)
    # длинная прямая масса за спиной (hl_m, физика default)
    cb("hl_m", (-4.2, 24.0, 3.1), (8.4, 7.4, 1.0), "hair_ends", dens=1, seed=31)
    cb("hl_m", (-3.9, 30.6, 3.4), (7.8, 6.0, 0.9), "hair", seed=32)
    # ободок: чёрная лента через макушку с тонким кружевом
    G.bone("Headband", "Hair", (0, 38.5, -1))
    cb("Headband", (-3.95, 40.55, -1.7), (7.9, 0.35, 1.0), "black")
    cb("Headband", (-4.1, 36.8, -1.7), (0.3, 3.9, 1.0), "black")
    cb("Headband", (3.8, 36.8, -1.7), (0.3, 3.9, 1.0), "black")
    cb("Headband", (-3.95, 40.6, -2.0), (7.9, 0.3, 0.3), "cuff", dens=2)

    # ---------------- корпус: свитер, ремень с ромбовой пряжкой, высокий ворот, бант
    cb("UpperBody", (-3.5, 20.75, -2.025), (7, 11, 4.05), {"north": "sweater", "south": "sweater", "east": "sweater", "west": "sweater", "up": "sweater", "down": "black"})
    cb("UpperBody", (-2.7, 30.15, -2.125), (5.4, 2, 4.2), "sweater")
    cb("UpperBody", (-4, 31.575, -1.9), (8, 1, 4.2), "sweater")
    cb("UpperBody", (-3.7, 20.65, -2.15), (7.4, 3.3, 4.3), "skirt")       # пояс юбки поверх низа свитера
    cb("UpperBody", (-3.85, 23.7, -2.25), (7.7, 0.8, 4.5), "belt")        # ремень
    G.bone("Buckle", "UpperBody", (0, 24.1, -2.4))
    cb("Buckle", (-0.55, 23.55, -2.5), (1.1, 1.1, 0.25), "silver", rot=(0, 0, 45), pivot=(0, 24.1, -2.4))
    cb("Buckle", (-0.28, 23.82, -2.6), (0.56, 0.56, 0.2), "violet", rot=(0, 0, 45), pivot=(0, 24.1, -2.4))
    cb("Breast", (-3.4, 27.35, -3.05), (6.8, 2.8, 2.6), "sweater")
    # бейдж на клипсе (на груди слева)
    cb("Breast", (1.0, 27.3, -3.3), (1.3, 1.7, 0.2), "badge", dens=2)
    cb("Breast", (1.45, 28.9, -3.35), (0.4, 0.5, 0.3), "silver")
    # высокий ворот + чёрная лента-бант
    G.bone("Neck", "UpperBody", (0, 31.5, 0))
    cb("Neck", (-2.0, 31.4, -2.0), (4.0, 2.3, 3.9), "sweater")
    cb("Neck", (-2.15, 33.3, -2.15), (4.3, 0.5, 4.2), "cuff")
    G.bone("Bow", "Neck", (0, 31.5, -2.2))
    cb("Bow", (-0.4, 31.1, -2.45), (0.8, 0.8, 0.4), "black")
    cb("Bow", (-1.7, 31.2, -2.35), (1.3, 0.9, 0.3), "black", rot=(0, 0, -12), pivot=(-0.4, 31.5, -2.3))
    cb("Bow", (0.4, 31.2, -2.35), (1.3, 0.9, 0.3), "black", rot=(0, 0, 12), pivot=(0.4, 31.5, -2.3))
    cb("Bow", (-0.75, 29.2, -2.4), (0.5, 1.9, 0.15), "black", rot=(0, 0, -10), pivot=(-0.3, 31.1, -2.3))
    cb("Bow", (0.25, 29.3, -2.4), (0.5, 1.8, 0.15), "black", rot=(0, 0, 12), pivot=(0.3, 31.1, -2.3))

    # ---------------- руки: рукава свитера, манжеты-рюши, кисти
    cb("LeftArm", (2.45, 24.9, -1.7), (3.25, 7.3, 3.4), "sweater")
    cb("LeftArm", (2.8, 24.125, -1.125), (2.7, 2.05, 2.3), "sweater")
    cb("LeftForeArm", (2.525, 20.375, -1.575), (3.2, 5, 3.2), "sweater")
    cb("LeftForeArm", (2.625, 18.075, -1.475), (3, 2.4, 3), "skin")
    cb("LeftForeArm", (2.35, 19.75, -1.75), (3.55, 0.9, 3.55), "cuff", dens=2)
    G.mirror("LeftArm", "RightArm"); G.mirror("LeftForeArm", "RightForeArm")

    # ---------------- ноги: колготки, белые туфли с бантом и ремешком-крестом
    cb("LeftLeg", (0.375, 11.65, -2), (3.65, 10, 4), "tights")
    cb("LeftLowerLeg", (0.5, 2, -1.9), (3.4, 10, 4), "tights")
    cb("LeftFoot", (0.25, 0, -2.1), (3.875, 2.6, 4.375), "shoe")
    cb("LeftFoot", (0.35, 0, -4.4), (3.675, 1.9, 3), "toe")
    cb("LeftFoot", (0.5, 1.75, -3.75), (3.375, 0.35, 0.35), "black", rot=(0, 25, 0), pivot=(2.19, 1.9, -3.6))
    cb("LeftFoot", (0.5, 1.75, -3.75), (3.375, 0.35, 0.35), "black", rot=(0, -25, 0), pivot=(2.19, 1.9, -3.6))
    cb("LeftFoot", (1.55, 1.6, -4.6), (1.3, 0.7, 0.3), "black")
    G.mirror("LeftLeg", "RightLeg"); G.mirror("LeftLowerLeg", "RightLowerLeg"); G.mirror("LeftFoot", "RightFoot")

    # ---------------- юбка: чёрная плиссированная + фиолетовая нижняя оборка
    cb("Skirt", (-4.0, 19.0, -2.55), (8.0, 1.8, 5.1), "skirt")
    cb("Skirt", (-4.45, 17.3, -2.95), (8.9, 1.9, 5.9), "skirt")
    cb("Skirt", (-4.9, 15.7, -3.35), (9.8, 1.8, 6.7), "skirt", dens=1.0)
    cb("Skirt", (-4.75, 15.05, -3.2), (9.5, 0.8, 6.4), "frill", dens=1.0)

    # ---------------- подсумки и ремешки (на поясе: слева маленькие, справа большая)
    G.bone("Gear", "UpperBody", (0, 23, 0))
    cb("Gear", (3.65, 21.0, -1.9), (1.0, 1.6, 1.3), "pouch")
    cb("Gear", (3.75, 21.2, 0.0), (0.9, 1.4, 1.2), "pouch")
    cb("Gear", (-5.05, 19.8, -1.6), (1.5, 2.9, 2.9), "pouch")
    G.bone("Straps", "Gear", (0, 23.6, 0))
    for i, (x, z, ln) in enumerate([(-4.6, -1.9, 4.5), (-3.9, 2.3, 5.5), (4.2, 1.5, 4.0), (2.8, -2.45, 3.2)]):
        cb("Straps", (x, 23.6 - ln, z), (0.3, ln, 0.15), "strap", share="swatch_strap")
    cb("Straps", (-4.95, 19.0, -1.75), (0.25, 2.4, 0.15), "strap", share="swatch_strap")

    # ---------------- плащ-накидка: полупрозрачный белый, чёрная отделка, асимметричный подол с клиньями
    G.bone("Cape", "UpperBody", (0, 32, 0))
    # накидка на плечах (с чёрной отделкой)
    cb("Cape", (-4.6, 31.6, -2.5), (9.2, 0.6, 5.2), "black")
    cb("Cape", (-5.9, 27.6, -2.5), (1.3, 4.6, 5.2), {"north": "black", "south": "black", "east": cape_mat(wedges(2, 0.45), True),
                                                     "west": cape_mat(wedges(2, 0.45), True), "up": "black", "down": None})
    cb("Cape", (4.6, 27.6, -2.5), (1.3, 4.6, 5.2), {"north": "black", "south": "black", "east": cape_mat(wedges(2, 0.45, 0.5), True),
                                                    "west": cape_mat(wedges(2, 0.45, 0.5), True), "up": "black", "down": None})
    # спинка плаща: верх жёсткий, низ на «физике»
    G.bone("CapeBack", "Cape", (0, 31.8, 2.6))
    cb("CapeBack", (-5.6, 21.0, 2.55), (11.2, 10.9, 0.15), cape_mat(None, True), faces=["north", "south"], dens=2)
    G.bone("CapeBackLow", "CapeBack", (0, 21.0, 2.65))
    cb("CapeBackLow", (-6.2, 6.0, 2.6), (12.4, 15.0, 0.15), cape_mat(wedges(3, 0.35, 0.0, 0.25)), faces=["north", "south"], dens=2)
    # полы спереди (открытый перед): правая короче, левая длиннее — асимметрия
    for side, x0, x1, ln, ph in (("L", 2.9, 6.1, 20.5, 0.2), ("R", -6.1, -2.9, 17.5, 0.7)):
        G.bone(f"CapeFront{side}", "Cape", ((x0 + x1) / 2, 31.8, -2.5))
        cb(f"CapeFront{side}", (x0, 21.5, -2.62), (x1 - x0, 10.2, 0.12), cape_mat(None, True), faces=["north", "south"], dens=2)
        G.bone(f"CapeFront{side}Low", f"CapeFront{side}", ((x0 + x1) / 2, 21.5, -2.6))
        cb(f"CapeFront{side}Low", (x0 - 0.3, 21.5 - ln + 10.5, -2.62), (x1 - x0 + 0.6, ln - 10.5, 0.12),
           cape_mat(wedges(1, 0.55, ph)), faces=["north", "south"], dens=2)
    # бока плаща (под рукой), низ на физике вместе со спинкой
    for side, x in (("L", 5.95), ("R", -6.1)):
        G.bone(f"CapeSide{side}", "Cape", (x, 27.6, 0))
        cb(f"CapeSide{side}", (x, 13.0, -2.3), (0.15, 14.6, 4.9), cape_mat(wedges(2, 0.4, 0.3 if side == "L" else 0.8)), faces=["east", "west"], dens=2)

    # ---------------- нимб-ромб над головой (глитч) и осколок Originium рядом
    G.bone("ysmGlowHalo", "Head", (0, 43.3, 0.6))
    halo(G, "ysmGlowHalo", (0, 43.3, 0.6), 1.5)
    G.bone("ysmGlowHaloGlitch", "ysmGlowHalo", (0, 43.3, 0.6))
    cb("ysmGlowHaloGlitch", (-2.1, 43.8, 0.5), (1.1, 0.25, 0.2), "glitch", share="swatch_glitch")
    cb("ysmGlowHaloGlitch", (1.0, 42.5, 0.5), (1.4, 0.2, 0.2), "glitch", share="swatch_glitch")
    cb("ysmGlowHaloGlitch", (-0.5, 45.2, 0.5), (0.8, 0.2, 0.2), "glitch", share="swatch_glitch")
    G.bone("ShardOrbit", "Root", (0, 30, 0))
    G.bone("ysmGlowShard", "ShardOrbit", (-8, 30, 1))
    shard(G, "ysmGlowShard", (-8, 30, 1), 1.0)

    # ---------------- формы осколка в руках (правая — основная, левая — зеркальная)
    for side in ("R", "L"):
        hand = "RightHand" if side == "R" else "LeftHand"
        hp = G.byname[hand]["pivot"]
        root = f"Forms{side}"
        G.bone(root, hand, hp)
        for f in FORMS:
            G.bone(f"ysmGlow{side}_{f}", root, hp)
        if side == "R":
            forms(G, "R", hp)
        else:
            for f in FORMS:
                G.mirror(f"ysmGlowR_{f}", f"ysmGlowL_{f}")

    # ---------------- реквизит позы «записи»: планшет с жёлтым стикером и карандаш
    lp = G.byname["LeftHand"]["pivot"]; rp = G.byname["RightHand"]["pivot"]
    G.bone("Clipboard", "LeftHand", lp)
    cb("Clipboard", (lp[0] - 2.2, lp[1] - 1.2, lp[2] - 3.6), (3.4, 0.3, 4.4), "clip_board", dens=2)
    G.bone("ysmGlowOfferShard", "RightHand", (rp[0], rp[1] + 3.2, rp[2] - 2.6))
    shard(G, "ysmGlowOfferShard", (rp[0], rp[1] + 3.2, rp[2] - 2.6), 0.8)
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


def halo(G, bone, c, r):
    x, y, z = c
    diamond(G, bone, c, r * 1.4, r * 1.4, 0.35)
    G.cube(bone, (x - 0.35, y - 0.35, z - 0.3), (0.7, 0.7, 0.6), "core", rot=(0, 0, 45), pivot=c, share="swatch_core")


def shard(G, bone, c, k):
    x, y, z = c
    diamond(G, bone, c, 1.5 * k, 3.4 * k, 1.5 * k)
    diamond(G, bone, c, 1.5 * k, 1.5 * k, 1.5 * k, tilt=45)
    G.cube(bone, (x - 0.3, y - 0.3, z - 0.95), (0.6, 0.6, 1.9), "core", rot=(0, 0, 45), pivot=c, share="swatch_core")


def forms(G, side, hp):
    """Формы осколка в правой руке. Локальная система: рукоять в кулаке, предмет вперёд (−Z) и вверх (+Y)."""
    hx, hy, hz = hp
    cr = "crystal"; S = "swatch_crystal"
    def cube(f, lo, hi, mat=cr, share=S, rot=None, piv=None):
        o, s = box((hx + lo[0], hy + lo[1], hz + lo[2]), (hx + hi[0], hy + hi[1], hz + hi[2]))
        G.cube(f"ysmGlow{side}_{f}", o, s, mat, share=share, rot=rot,
               pivot=(hx + piv[0], hy + piv[1], hz + piv[2]) if piv else None)
    def dia(f, c, w, d, tilt=0):
        G.cube(f"ysmGlow{side}_{f}", (hx + c[0] - w / 2, hy + c[1] - w / 2, hz + c[2] - d / 2), (w, w, d), cr, share=S,
               rot=(0, tilt, 45), pivot=(hx + c[0], hy + c[1], hz + c[2]))
    def rhx(f, c, w, d):  # ромб в плоскости YZ (поворот вокруг X)
        G.cube(f"ysmGlow{side}_{f}", (hx + c[0] - d / 2, hy + c[1] - w / 2, hz + c[2] - w / 2), (d, w, w), cr, share=S,
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
    cube("axe", (-0.25, -1.4, -5.0), (0.25, -0.5, -6.0))
    # кирка: древко + изогнутая поперечина
    cube("pickaxe", (-0.25, -1.05, 1.2), (0.25, -0.55, -6.2), "black", "swatch_grip")
    cube("pickaxe", (-0.25, -1.3, -5.6), (0.25, 1.6, -6.6))
    cube("pickaxe", (-0.2, 1.4, -6.4), (0.2, 2.1, -4.6))
    cube("pickaxe", (-0.2, -3.6, -6.4), (0.2, -1.2, -5.8))
    rhx("pickaxe", (0, 2.0, -4.3), 0.7, 0.3); rhx("pickaxe", (0, -3.6, -6.0), 0.7, 0.3)
    # лопата
    cube("shovel", (-0.25, -1.05, 1.2), (0.25, -0.55, -5.4), "black", "swatch_grip")
    cube("shovel", (-0.15, -2.0, -5.4), (0.15, 0.4, -8.0))
    rhx("shovel", (0, -0.8, -8.0), 1.7, 0.3)
    # мотыга
    cube("hoe", (-0.25, -1.05, 1.2), (0.25, -0.55, -6.4), "black", "swatch_grip")
    cube("hoe", (-0.2, -0.6, -5.9), (0.2, 1.8, -6.6))
    cube("hoe", (-0.2, 1.3, -6.6), (0.2, 1.8, -4.9))
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
    # щит: ромб-кристалл перед рукой
    G.cube(f"ysmGlow{side}_shield", (hx - 0.4 - 1.0, hy - 0.5 - 2.6, hz - 2.6 - 2.6), (0.5, 5.2, 5.2), cr, share=S, rot=(45, 0, 0),
           pivot=(hx - 0.9, hy - 0.5, hz - 2.6))
    G.cube(f"ysmGlow{side}_shield", (hx - 0.45 - 1.1, hy - 0.5 - 1.2, hz - 2.6 - 1.2), (0.3, 2.4, 2.4), "core", share="swatch_core",
           rot=(45, 0, 0), pivot=(hx - 0.9, hy - 0.5, hz - 2.6))
    # копьё
    cube("spear", (-0.22, -1.0, 4.0), (0.22, -0.56, -11.0), "black", "swatch_grip")
    rhx("spear", (0, -0.78, -11.6), 1.4, 0.35)
    cube("spear", (-0.15, 0.0, -10.4), (0.15, 0.4, -12.2)); cube("spear", (-0.15, -1.9, -10.4), (0.15, -1.5, -12.2))
    # посох/жезл: вертикальное древко, большой ромб наверху и два спутника
    cube("staff", (-0.25, -8.0, -2.1), (0.25, 9.5, -1.6), "black", "swatch_grip")
    G.cube(f"ysmGlow{side}_staff", (hx - 1.2, hy + 11.4 - 1.2, hz - 1.85 - 0.4), (2.4, 2.4, 0.8), cr, share=S, rot=(0, 0, 45),
           pivot=(hx, hy + 11.4, hz - 1.85))
    G.cube(f"ysmGlow{side}_staff", (hx - 0.5, hy + 11.4 - 0.5, hz - 1.85 - 0.5), (1.0, 1.0, 1.0), "core", share="swatch_core", rot=(0, 0, 45),
           pivot=(hx, hy + 11.4, hz - 1.85))
    cube("staff", (-0.8, 9.3, -2.25), (0.8, 9.7, -1.45))
    dia("staff", (-2.0, 12.8, -1.85), 0.7, 0.3); dia("staff", (2.0, 10.0, -1.85), 0.7, 0.3)
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
            "rotation": [0, "q.anim_time * 60", 0],
            "position": [0, "math.sin(q.anim_time * 120) * 0.6", 0]},
        "ysmGlowShard": {
            "rotation": ["math.sin(q.anim_time * 90) * 10", "q.anim_time * -90", 0],
            "scale": f"ysm.second_order('pr_shard', {shard_on}, 3, 1, 0)"},
    }}
    # parallel2: формы осколка в руках вместо ванильного предмета
    bones = {}
    for side, loc in (("R", "RightHandLocator"), ("L", "LeftHandLocator")):
        v = f"v.form_{side.lower()}"
        bones[loc] = {"scale": f"(({v} ?? 0) > 0 && !(v.roaming.pr_vanilla ?? 0)) ? 0 : 1"}
        for i, f in enumerate(FORMS, 1):
            bones[f"ysmGlow{side}_{f}"] = {"scale": f"(v.roaming.pr_vanilla ?? 0) ? 0 : {form_vis(side, i)}"}
        bones[f"Forms{side}"] = {"position": [0, "math.sin(q.anim_time * 120) * 0.15", 0]}
    # лук: при натяжении «тетива» отходит назад
    bones["ysmGlowR_bow"]["position"] = [0, 0, "q.is_using_item ? math.min(q.item_in_use_duration, 1) * 0.8 : 0"]
    A["parallel2"] = {"loop": True, "animation_length": T, "bones": bones}
    # parallel3: глаза-ромбы — ярче/крупнее при атаке, использовании, ломании блока, сообщении в чат
    g = GLOW
    eye = {}
    for s in ("Right", "Left"):
        eye[f"ysmGlow{s}Pupil"] = {"scale": f"1 + 0.45 * {g}"}
        eye[f"ysmGlow{s}Rim"] = {"scale": f"{g} > 0.05 ? (0.55 + 0.75 * {g}) : 0"}
        eye[f"ysmGlow{s}Hot"] = {"scale": f"{g} > 0.05 ? (0.6 + 0.9 * {g}) : 0"}
    A["parallel3"] = {"loop": True, "bones": eye}
    # parallel4: физика плаща (как у волос default: скорость, вертикаль, поворот), ремешки
    sp = "math.clamp(q.ground_speed + (v.hg ?? 0), 0, 20)"
    vs = "math.clamp(-3 * q.vertical_speed, -10, 90)"
    A["parallel4"] = {"loop": True, "bones": {
        "CapeBackLow": {"rotation": [f"ysm.second_order('pr_cape_h', {sp}, 2, 0.6, 0) * 1.6 + ysm.second_order('pr_cape_v', {vs}, 2, 0.6, 0) * 0.6", 0,
                                     "ysm.second_order('pr_cape_y', math.clamp(0.02 * q.yaw_speed, -40, 40), 2, 0.6, 0)"]},
        "CapeBack": {"rotation": [f"ysm.second_order('pr_cape_h2', {sp}, 2, 0.6, 0) * 0.4", 0, 0]},
        "CapeFrontLLow": {"rotation": [f"-ysm.second_order('pr_capef', {sp}, 2, 0.6, 0) * 0.9 - 4", 0, 0]},
        "CapeFrontRLow": {"rotation": [f"-ysm.second_order('pr_capef', {sp}, 2, 0.6, 0) * 0.9 - 4", 0, 0]},
        "CapeSideL": {"rotation": [f"ysm.second_order('pr_cape_h', {sp}, 2, 0.6, 0) * 0.8", 0, -4]},
        "CapeSideR": {"rotation": [f"ysm.second_order('pr_cape_h', {sp}, 2, 0.6, 0) * 0.8", 0, 4]},
        "Straps": {"rotation": [f"ysm.second_order('pr_strap', {sp}, 3, 0.4, 0) * 1.2", 0, 0]},
    }}
    return A


def pre_parallels():
    # реквизит позы «записи» скрыт везде, кроме своей позы (pre_parallel ниже приоритетом, чем поза колеса)
    return {"pre_parallel2": {"loop": True, "bones": {"Clipboard": {"scale": 0}, "Pencil": {"scale": 0}, "ysmGlowOfferShard": {"scale": 0}}}}


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
        "ysmGlowOfferShard": {"scale": 1, "position": kf({0: [0, 0, 0], 2: [0, 0.8, 0], 4: [0, 0, 0]}),
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
        lines = [f"// форма осколка для {slot}: 0 — пусто, 1..{len(FORMS)} — {', '.join(FORMS)}",
                 f"!ysm.has_{slot} ? {{ return 0; }};"]
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
        "// каждый кадр перед анимацией: формы осколка в руках\n"
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
    (out / "models" / "arm.json").write_text(json.dumps(A.json("geometry.priestess_arm"), ensure_ascii=False, separators=(",", ":")))
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
