#!/usr/bin/env python3
"""Модель Yes Steve Model «Vivi» (авторский персонаж владельца сборки: девушка-волчица) — генератор.

Собирает папку модели pack/config/yes_steve_model/custom/vivi/ (формат YSM 2.x, папка):
  ysm.json, models/main.json + arm.json, textures/vivi.png (256×256, пиксель-арт),
  animations/*.animation.json.

Способ тот же, что у Priestess (mods-src/ysm-priestess/build.py; оттуда берутся класс Geo — кубы и упаковка
текстуры — и чтение jar YSM): скелет (имена/иерархия/pivot костей) и базовые анимации (ходьба, бег, присед,
плавание, полёт, сон, езда, лодка, удары, Iron's Spells, TACZ) — от встроенной модели YSM «default»
(CC 0; авторы: 哥斯拉, 端木一动不动, 甜粽子, 星屑海螺, 蓝玫瑰, 艾雷克亚). Геометрия, текстура, уши, хвост,
пальто, меч, позы — свои (Minecraft_moded, поток 69).

Использование: python3 build.py [--out <папка>]
"""
import argparse, copy, json, math, pathlib, sys
import numpy as np

HERE = pathlib.Path(__file__).resolve().parent
ROOT = HERE.parent.parent
sys.path.insert(0, str(ROOT / "mods-src" / "ysm-priestess"))
import build as PB  # noqa: E402  (Geo, ysm_default, kf — общий движок; модель Priestess не меняется)

OUT = ROOT / "pack" / "config" / "yes_steve_model" / "custom" / "vivi"
TEX = PB.TEX
C, fill, noise = PB.C, PB.fill, PB.noise
SIDE = PB.SIDE

# ======================================================================= палитра (по описанию референса)
P = {
    "skin": C("fbe9df"), "skin_s": C("efd2c6"), "skin_d": C("d9aea2"), "mouth": C("b0646a"),
    # волосы: верхний слой тёмно-красный, нижний слой и кончики — светлые кремово-розовые
    "hair": C("7c1f2c"), "hair_l": C("942a39"), "hair_hi": C("b3434f"), "hair_d": C("561421"),
    "cream": C("f4dcd2"), "cream_l": C("fcece4"), "cream_d": C("e2bfb4"), "cream_p": C("e7b3b3"),
    # глаза красно-бордовые
    "eye": C("9c2033"), "eye_l": C("d0485a"), "eye_d": C("4e0c18"), "pupil": C("2a060c"),
    "white": C("f6f3f1"), "white_s": C("e3dfdd"), "white_d": C("c8c2c0"),
    "black": C("1c1a1f"), "black_l": C("34313a"), "black_hi": C("4d4955"), "black_d": C("0e0d11"),
    # пальто: бордовое (винное), золотая окантовка
    "coat": C("6b1a29"), "coat_l": C("832536"), "coat_d": C("4b0f1b"), "coat_in": C("3d0c16"),
    "gold": C("d8a83c"), "gold_l": C("f3d47e"), "gold_d": C("94701f"),
    "silver": C("cfd3da"), "silver_d": C("8d929c"),
    # меч в ножнах: серо-стальной
    "steel": C("8a919a"), "steel_l": C("b8bfc7"), "steel_hi": C("dde2e7"), "steel_d": C("5a6068"), "steel_dd": C("3e434a"),
}


# ======================================================================= материалы (painter(w, h, face, seed) → RGBA h×w)
def edge_cols(face):
    """какой стороне мира соответствует левый (u=0) и правый столбец грани (соглашения ysmrender/Bedrock)"""
    return {"north": ("xlo", "xhi"), "south": ("xhi", "xlo"), "east": ("zhi", "zlo"), "west": ("zlo", "zhi")}.get(face, (None, None))


def m_skin(w, h, face, s):
    a = fill(w, h, P["skin"]) + noise(w, h, s, 3)
    if face == "down": a[:] = P["skin_s"]
    if face in SIDE and h > 3: a[-1] = P["skin_s"]
    return a


def m_hand(w, h, face, s):
    a = m_skin(w, h, face, s)
    if face in SIDE and h > 2: a[0] = P["skin_s"]
    return a


def m_head(w, h, face, s):
    """лицо: тень от чёлки сверху, светлые щёки; остальные грани — волосы"""
    a = fill(w, h, P["skin"]) + noise(w, h, s, 2)
    if face == "north":
        a[0:2] = P["skin_s"]
        a[-1, :] = P["skin_s"]
    else:
        a[:] = P["hair_d"]
    return a


def m_eye_white(w, h, face, s):
    """спокойный, чуть прикрытый глаз: тяжёлое верхнее веко с ресницами, белок, нижняя тонкая линия"""
    a = fill(w, h, P["white"])
    if face == "north":
        k = max(3, int(h * 0.36))
        a[:k - 1, :] = P["skin_d"]          # верхнее веко (прикрытый взгляд)
        a[k - 1:k + 1, :] = P["black"]      # линия ресниц
        a[k - 1:k + 3, -1] = P["black"]      # «хвостик» к виску
        a[k + 1:, 0] = P["black_l"]
        a[-1, 1:-1] = P["eye_d"]
    else:
        a[:] = P["skin_d"]
    return a


def m_iris(w, h, face, s):
    """красно-бордовая радужка: тёмная сверху (тень века), светлее книзу, тёмный ободок"""
    a = fill(w, h, P["eye"])
    if face == "north":
        for y in range(h):
            f = y / max(1, h - 1)
            a[y] = P["eye_d"] * (1 - f) + P["eye_l"] * f
        a[:, 0] = P["eye_d"]; a[:, -1] = P["eye_d"]
    return a


def m_pupil(w, h, face, s):
    return fill(w, h, P["pupil"])


def m_sparkle(w, h, face, s):
    return fill(w, h, P["white"])


def m_mouth(w, h, face, s):
    return fill(w, h, P["skin_d"] * 0.6 + P["mouth"] * 0.4)


def m_hair(w, h, face, s, tip=0.0, under=False, band=False):
    """каре: пряди тремя тонами тёмно-красного; tip — доля высоты, переходящая в кремово-розовый кончик;
    under — нижний слой (весь кремово-розовый)"""
    r = np.random.default_rng(s)
    base, lt, dk = ("cream", "cream_l", "cream_d") if under else ("hair", "hair_l", "hair_d")
    a = fill(w, h, P[base])
    if face in ("up", "down"):
        a[:, ::2] = P[lt]
        return a
    for x in range(w):
        v = r.random()
        if v < 0.35: a[:, x] = P[lt]
        elif v < 0.5: a[:, x] = P[dk]
    if band and h > 6 and not under:
        y0 = int(h * 0.22)
        for x in range(w):
            k = 1 + (x * 7 + s) % 2
            a[y0:y0 + k, x] = a[y0:y0 + k, x] * 0.4 + P["hair_hi"] * 0.6
    if tip > 0 and not under:
        n = max(1, int(round(h * tip)))
        jag = r.integers(0, max(1, n // 2) + 1, w)  # неровная граница перехода — пряди разной длины
        for x in range(w):
            for y in range(h - n + jag[x], h):
                f = min(1.0, (y - (h - n + jag[x]) + 1) / max(1, n - jag[x]) * 1.6)
                a[y, x] = a[y, x] * (1 - f) + (P["cream"] if (x + y) % 3 else P["cream_d"]) * f
    return a


def hair(tip=0.0, under=False, band=False):
    return lambda w, h, f, s: m_hair(w, h, f, s, tip, under, band)


def m_ear(w, h, face, s):
    """волчье ухо снаружи: тёмно-красное, к кончику темнее"""
    a = fill(w, h, P["hair"]) + noise(w, h, s, 5)
    if face in SIDE:
        for y in range(h):
            f = 1 - y / max(1, h - 1)
            a[y] = a[y] * (1 - 0.35 * f) + P["hair_d"] * 0.35 * f
    return a


def m_ear_in(w, h, face, s):
    """внутренняя сторона уха: розово-кремовый мех, пушистая кромка"""
    r = np.random.default_rng(s)
    a = fill(w, h, P["cream_p"])
    for x in range(w):
        if r.random() < 0.4: a[:, x] = P["cream_l"]
    if face in SIDE and h > 2:
        a[-1, ::2] = P["cream_d"]
    return a


def m_tail(w, h, face, s, t0=0.0, t1=1.0):
    """хвост: тёмно-красный мех, по длине (t0→t1) переходит в кремово-розовый кончик; пряди меха полосами"""
    r = np.random.default_rng(s)
    a = np.zeros((h, w, 4), np.float32)
    strand = r.random(w)
    for y in range(h):
        t = t0 + (t1 - t0) * (y + 0.5) / h
        k = float(np.clip((t - 0.7) / 0.25, 0, 1))  # светлый только конец
        for x in range(w):
            jitter = (strand[x] - 0.5) * 0.25
            kk = float(np.clip(k + jitter, 0, 1))
            dark = P["hair_d"] if strand[x] < 0.3 else (P["hair_l"] if strand[x] > 0.75 else P["hair"])
            light = P["cream_d"] if strand[x] < 0.3 else (P["cream_l"] if strand[x] > 0.75 else P["cream"])
            a[y, x] = dark * (1 - kk) + light * kk
    if face in ("up", "down"):
        a[:] = a.mean(axis=(0, 1))
    return a


def tail(t0, t1):
    return lambda w, h, f, s: m_tail(w, h, f, s, t0, t1)


def m_white(w, h, face, s):
    a = fill(w, h, P["white"]) + noise(w, h, s, 3)
    if face in SIDE and h > 2: a[-1] = P["white_s"]
    return a


def m_collar(w, h, face, s):
    a = fill(w, h, P["white"])
    if face in SIDE: a[-1] = P["white_d"]; a[0] = P["white_s"]
    return a


def m_tie(w, h, face, s):
    a = fill(w, h, P["black"])
    if face == "north" and w > 1:
        a[:, 0] = P["black_l"]
    if h > 3: a[-1] = P["black_d"]
    return a


def m_vest(w, h, face, s):
    """чёрный жилет: на передней грани — ряд пуговиц по центру и тонкая линия застёжки"""
    a = fill(w, h, P["black"]) + noise(w, h, s, 4)
    if face == "north" and w > 6:
        c = w // 2
        a[:, c] = P["black_d"]
        for y in range(1, h - 1, 3):
            a[y, c - 1] = P["gold_d"]; a[y, c] = P["gold"]
    if face in SIDE:
        a[-1] = P["black_l"]
    return a


def m_belt(w, h, face, s):
    a = fill(w, h, P["black"]); a[0, :] = P["black_l"]; a[-1, :] = P["black_d"]
    return a


def m_silver(w, h, face, s):
    a = fill(w, h, P["silver"]); a[-1:, :] = P["silver_d"]; a[:, -1:] = P["silver_d"]
    if w > 1 and h > 1: a[0, 0] = P["white"]
    return a


def m_gold(w, h, face, s):
    a = fill(w, h, P["gold"]); a[-1:, :] = P["gold_d"]; a[:, -1:] = P["gold_d"]
    if w > 1 and h > 1: a[0, :] = P["gold_l"]
    return a


def m_brooch(w, h, face, s):
    """брошь-медальон: золотой круг с гербом (тёмно-красное поле, золотая фигура)"""
    a = fill(w, h, P["gold"])
    if face == "north" and w >= 5 and h >= 5:
        yy, xx = np.mgrid[0:h, 0:w]
        cx, cy = (w - 1) / 2, (h - 1) / 2
        d = np.sqrt((xx - cx) ** 2 + (yy - cy) ** 2)
        R = min(w, h) / 2
        a[d > R] = 0
        a[(d <= R) & (d > R - 1)] = P["gold_d"]
        inner = d <= R - 1.6
        a[inner] = P["coat_d"]
        a[inner & (np.abs(xx - cx) < 0.8)] = P["gold_l"]          # герб: вертикаль
        a[inner & (np.abs(yy - cy + 0.5) < 0.8) & (np.abs(xx - cx) < R * 0.55)] = P["gold_l"]  # и перекладина
        a[0, int(cx)] = P["gold_l"]
    return a


def m_tassel(w, h, face, s):
    a = fill(w, h, P["gold"])
    a[:, ::2] = P["gold_d"]
    if h > 2: a[0] = P["gold_l"]
    return a


def m_pants(w, h, face, s, knee=False):
    """чёрные прямые брюки: стрелка спереди, тень по бокам"""
    a = fill(w, h, P["black"]) + noise(w, h, s, 3)
    if face in SIDE:
        a[:, 0] = P["black_d"]; a[:, -1] = P["black_d"]
        if face == "north" and w > 4:
            a[:, w // 2] = P["black_l"]
        if knee and face == "north":
            a[0:max(1, h // 10), :] = P["black_l"]
    return a


def pants(knee=False):
    return lambda w, h, f, s: m_pants(w, h, f, s, knee)


def m_boot(w, h, face, s):
    """чёрные ботинки: блик сверху, тёмная подошва"""
    a = fill(w, h, P["black_d"]) + noise(w, h, s, 3)
    if face in SIDE:
        a[-1] = P["black"] * 0.5
        if h > 2: a[0] = P["black_l"]
    if face == "up": a[:] = P["black_l"]
    if face == "down": a[:] = P["black_d"] * 0.6
    return a


def m_coat(w, h, face, s, gold=(), inner=False):
    """винное сукно: мягкие вертикальные складки; gold — края мира с золотой окантовкой
    ('xlo','xhi','zlo','zhi','top','bottom'); у 'bottom' — кайма подола с тёмной кромкой"""
    a = fill(w, h, P["coat_in"] if inner else P["coat"]) + noise(w, h, s, 4)
    if face in ("up", "down"):
        a[:] = P["coat_d"]
        return a
    if not inner:
        a[:, 2::5] = a[:, 2::5] * 0.6 + P["coat_d"] * 0.4
        a[:, 4::7] = a[:, 4::7] * 0.6 + P["coat_l"] * 0.4
    lo, hi = edge_cols(face)
    for side, k in gold if isinstance(gold, (list, tuple)) else ():
        if side == "top":
            a[:k] = P["gold"]; a[k:k + 1] = P["gold_d"]
        elif side == "bottom":
            a[h - k:] = P["gold"]; a[h - k - 1:h - k] = P["gold_d"]; a[-1] = P["gold_d"]
        elif side == lo:
            a[:, :k] = P["gold"]; a[:, k:k + 1] = P["gold_d"]
        elif side == hi:
            a[:, w - k:] = P["gold"]; a[:, w - k - 1:w - k] = P["gold_d"]
    return a


def coat(*gold, inner=False):
    """coat(('xlo', 1), ('bottom', 2)) — список (край, ширина px)"""
    return lambda w, h, f, s: m_coat(w, h, f, s, gold, inner)


def m_sleeve(w, h, face, s):
    a = fill(w, h, P["coat"]) + noise(w, h, s, 4)
    if face in SIDE:
        a[:, 1::4] = a[:, 1::4] * 0.7 + P["coat_d"] * 0.3
    else:
        a[:] = P["coat_d"]
    return a


def m_cuff(w, h, face, s):
    """отворот рукава: винный с золотой каймой сверху и снизу"""
    a = fill(w, h, P["coat_l"])
    if face in SIDE:
        a[0] = P["gold"]; a[-1] = P["gold"]
        if h > 3: a[1] = P["gold_d"]
    else:
        a[:] = P["coat_d"]
    return a


def m_epaulet(w, h, face, s):
    """эполет: винный погон с золотой каймой по краю"""
    a = fill(w, h, P["coat_l"]) + noise(w, h, s, 3)
    if face == "up":
        a[0, :] = P["gold"]; a[-1, :] = P["gold"]; a[:, 0] = P["gold"]; a[:, -1] = P["gold"]
    elif face in SIDE:
        a[:] = P["gold"]; a[-1] = P["gold_d"]
    return a


def m_sheath(w, h, face, s):
    """ножны: тёмная сталь, продольный блик и риски"""
    a = fill(w, h, P["steel_d"]) + noise(w, h, s, 4)
    if face in SIDE and w > 2:
        a[:, 1] = P["steel"]; a[:, 0] = P["steel_dd"]; a[:, -1] = P["steel_dd"]
        if face == "north" or face == "south":
            a[:, w // 2] = P["steel"]
    return a


def m_steel(w, h, face, s):
    a = fill(w, h, P["steel"]); a[0] = P["steel_hi"]; a[-1] = P["steel_d"]
    if w > 2: a[:, -1] = P["steel_d"]
    return a


def m_grip(w, h, face, s):
    """рукоять: тёмная оплётка ромбиками"""
    a = fill(w, h, P["black"])
    for y in range(h):
        a[y, (y % 3)::3] = P["steel_dd"]
    return a


MATS = {k[2:]: v for k, v in globals().items() if k.startswith("m_") and callable(v)}


# ======================================================================= геометрия
class Geo(PB.Geo):
    """Geo Priestess, но материалы-строки — из палитры Vivi (подставляем функцию до упаковки)."""

    def cube(self, bone, origin, size, mat, **kw):
        if isinstance(mat, str):
            mat = MATS[mat]
        elif isinstance(mat, dict):
            mat = {k: (MATS[v] if isinstance(v, str) else v) for k, v in mat.items()}
        return super().cube(bone, origin, size, mat, **kw)


def strand(G, bone, x0, x1, ytop, ybot, z0, z1, tip=0.35, under=False, band=False, faces=None, seed=None):
    """прядь каре: основной куб + сужающийся кончик (кончик — кремово-розовый)"""
    h = ytop - ybot
    k = h * 0.2
    G.cube(bone, (x0, ybot + k, z0), (x1 - x0, h - k, z1 - z0), hair(tip, under, band), faces=faces, seed=seed)
    w = (x1 - x0) * 0.6
    cx = (x0 + x1) / 2
    G.cube(bone, (cx - w / 2, ybot, z0 + (z1 - z0) * 0.15), (w, k, (z1 - z0) * 0.7), hair(1.0, under), faces=faces,
           seed=None if seed is None else seed + 500)


def build_main(default_geo):
    G = Geo()
    for b in default_geo["minecraft:geometry"][0]["bones"]:
        rot = b.get("rotation")
        if b["name"] in ("Collar", "Ribbon"):
            rot = None
        G.bone(b["name"], b.get("parent"), b.get("pivot", [0, 0, 0]), rot)
    cb = G.cube

    # ================= голова, лицо: спокойный, чуть холодный взгляд, красно-бордовые глаза
    cb("Head", (-3.5, 33.05, -3.5), (7, 7, 7), "head", dens=4, faces=["north"])
    cb("Head", (-3.5, 33.05, -3.5), (7, 7, 7), "head", faces=["south", "east", "west", "up", "down"])
    cb("AllHead", (-1.4, 31.9, -1.4), (2.8, 2.0, 2.8), "skin")
    for side in ("Right", "Left"):
        sg = -1 if side == "Right" else 1
        ex0 = -3.4 if side == "Right" else 1.0
        cb(f"{side}Eyelid", (ex0, 34.55, -3.53), (2.4, 2.3, 0.1), "eye_white", dens=5, faces=["north"])
        cx = ex0 + 1.2 + 0.1 * sg
        cb(f"{side}EyesBase", (cx - 0.8, 34.6, -3.58), (1.6, 1.45, 0.1), "iris", dens=5, faces=["north"])
        cb(f"{side}EyesBase", (cx - 0.3, 34.9, -3.62), (0.6, 0.62, 0.04), "pupil", dens=5, faces=["north"])  # круглый зрачок
        cb(f"{side}EyesBase", (cx - 0.62, 35.55, -3.66), (0.34, 0.34, 0.04), "sparkle", dens=2, faces=["north"], share="swatch_sparkle")
    G.bone("Mouth", "Head", (0, 33.9, -3.5))
    cb("Mouth", (-0.42, 33.85, -3.54), (0.84, 0.14, 0.05), "mouth", faces=["north"], share="swatch_mouth")

    # ================= волосы: каре с прямой чёлкой; верх тёмно-красный, нижний слой и кончики кремово-розовые
    cb("Hair", (-4.1, 36.6, -4.1), (8.2, 4.6, 8.2), hair(0, band=True), dens=2, faces=["south", "east", "west", "up"])
    cb("Hair", (-4.1, 36.6, -4.1), (8.2, 4.6, 8.2), hair(0, band=True), dens=4, faces=["north"])
    cb("Hair", (-3.6, 41.15, -3.6), (7.2, 0.5, 7.2), hair(), dens=2)
    cb("Hair", (-2.4, 41.6, -2.4), (4.8, 0.3, 4.8), hair(), dens=2)
    # нижний (светлый) слой каре — внутри, торчит из-под верхнего по низу боков и затылка
    cb("Hair", (-4.25, 31.3, -2.4), (0.5, 6.0, 6.6), hair(under=True), faces=["east", "west", "north", "south", "down"])
    cb("Hair", (3.75, 31.3, -2.4), (0.5, 6.0, 6.6), hair(under=True), faces=["east", "west", "north", "south", "down"])
    cb("Hair", (-4.0, 31.0, 3.7), (8.0, 6.0, 0.55), hair(under=True), faces=["south", "east", "west", "down", "north"])
    # верхний слой: пряди по бокам и сзади, длиной чуть выше подбородка нижнего слоя, кончики светлые
    for sg in (-1, 1):
        zs = [-3.9, -2.75, -1.6, -0.4, 0.8, 2.0, 3.2, 4.3]
        for i in range(len(zs) - 1):
            x0 = 4.25 if i % 2 else 4.35
            yb = 31.9 + (0.0 if i < 2 else 0.25 * (i % 3))
            lo, hi = (-(x0 + 0.42), -x0) if sg < 0 else (x0, x0 + 0.42)
            strand(G, "corner_h1", lo, hi, 40.8, yb, zs[i], zs[i + 1], tip=0.3, band=True,
                   faces=["east", "west", "north", "south", "down"], seed=60 + i + (20 if sg > 0 else 0))
    xs = [-4.4, -3.3, -2.2, -1.1, 0.0, 1.1, 2.2, 3.3, 4.4]
    for i in range(len(xs) - 1):
        yb = 32.2 - (0.3 if i % 2 else 0) - (0.2 if i in (3, 4) else 0)
        strand(G, "Hair", xs[i], xs[i + 1], 40.9, yb, 4.25 + (0.12 if i % 2 else 0), 4.75 + (0.12 if i % 2 else 0), tip=0.3,
               band=True, faces=["south", "east", "west", "down", "north"], seed=80 + i)
    # чёлка: прямой ровный срез над глазами, кончики чуть светлее
    bang_x = [-4.15, -3.25, -2.35, -1.4, -0.47, 0.47, 1.4, 2.35, 3.25, 4.15]
    for i in range(len(bang_x) - 1):
        z0 = -4.44 if i % 2 else -4.32
        yb = 37.0 + (0.06 if i % 3 == 1 else 0)
        cb("front_h1", (bang_x[i], yb, z0), (bang_x[i + 1] - bang_x[i], 41.1 - yb, 0.34), hair(0.12, band=True), dens=4,
           faces=["north", "east", "west", "down", "up"], seed=40 + i)
    # пряди у щёк (каре спереди чуть длиннее) — светлые кончики
    for sg in (-1, 1):
        x0, x1 = (-4.6, -3.55) if sg < 0 else (3.55, 4.6)
        strand(G, "corner_h1", x0, x1, 38.8, 31.4, -4.35, -3.2, tip=0.32, seed=30 + (sg > 0))

    # ================= волчьи уши: высокие, острые, стоячие; снаружи тёмно-красные, внутри розово-кремовые
    for side, sg in (("L", 1), ("R", -1)):
        bn = f"Ear{side}"
        px = 2.45 * sg
        G.bone(bn, "Head", (px, 41.2, 0.2), (0, 0, -8 * sg))
        # узкий высокий треугольник: стопка ромбов (кубы, повёрнутые на 45° вокруг Z) всё меньше кверху, низ утоплен
        # в волосы; поворот ромба 45° + наклон кости — в мире ромбы стоят ровно
        for i in range(10):  # мелкий шаг — ровные края треугольника
            k, cy, d = 3.0 - 0.29 * i, 41.4 + 0.5 * i, 0.9 - 0.04 * i
            cb(bn, (px - k / 2, cy - k / 2, 0.2 - d / 2), (k, k, d), "ear", rot=(0, 0, 45 + 8 * sg), pivot=(px, cy, 0.2), seed=900 + i)
        for i in range(6):  # внутренняя сторона — розово-кремовая
            k, cy = 1.9 - 0.27 * i, 41.75 + 0.5 * i
            cb(bn, (px - k / 2, cy - k / 2, -0.34), (k, k, 0.08), "ear_in", rot=(0, 0, 45 + 8 * sg), pivot=(px, cy, -0.3),
               faces=["north"], seed=920 + i)

    # ================= корпус: белая рубашка, чёрный галстук, жилет на пуговицах, ремень с пряжкой и кисточкой
    cb("UpperBody", (-3.5, 23.9, -2.025), (7, 7.85, 4.05), "vest")
    cb("UpperBody", (-2.7, 30.15, -2.125), (5.4, 2, 4.2), "vest")
    cb("UpperBody", (-4, 31.2, -1.9), (8, 1, 4.2), "vest")
    G.bone("Hips", "UpperBody", (0, 20.65, 0))  # верх брюк (отдельная кость — его пересечение с бёдрами задумано)
    cb("Hips", (-3.6, 20.6, -2.12), (7.2, 3.4, 4.24), pants())
    cb("UpperBody", (-3.75, 23.35, -2.25), (7.5, 0.85, 4.5), "belt")
    G.bone("Buckle", "UpperBody", (0, 23.78, -2.35))
    cb("Buckle", (-0.7, 23.25, -2.42), (1.4, 1.05, 0.2), "silver")
    cb("Buckle", (-0.4, 23.5, -2.46), (0.8, 0.55, 0.06), "belt", faces=["north"])
    G.bone("Tassel", "Buckle", (0.95, 23.4, -2.45))
    cb("Tassel", (0.85, 23.1, -2.55), (0.3, 0.3, 0.2), "gold")
    cb("Tassel", (0.75, 21.9, -2.52), (0.5, 1.25, 0.25), "tassel", dens=4)
    # грудь: жилет, белый вырез рубашки и галстук
    cb("Breast", (-3.4, 27.3, -3.05), (6.8, 2.9, 2.7), "vest")
    G.bone("Shirt", "UpperBody", (0, 31.2, -2.2))
    cb("Shirt", (-1.0, 28.4, -3.18), (2.0, 3.0, 0.15), "white", faces=["north", "east", "west", "up"])
    cb("Shirt", (-0.4, 26.2, -3.3), (0.8, 5.2, 0.12), "tie", dens=4, faces=["north", "east", "west", "down"])
    cb("Shirt", (-0.55, 30.6, -3.38), (1.1, 0.8, 0.22), "tie", dens=4)
    # воротник рубашки
    G.bone("Neck", "UpperBody", (0, 31.5, 0))
    cb("Neck", (-1.9, 31.2, -1.9), (3.8, 1.9, 3.8), "skin")
    cb("Neck", (-2.15, 31.6, -2.15), (4.3, 1.0, 4.3), "collar", dens=3)
    for sg in (-1, 1):
        cb("Neck", (0.05 * sg - (1.3 if sg < 0 else 0), 30.9, -2.35), (1.3, 1.1, 0.14), "collar", dens=4,
           rot=(0, 0, 32 * sg), pivot=(0.0, 31.6, -2.3))

    # ================= руки в рукавах пальто: золотой отворот, белая манжета рубашки
    cb("LeftArm", (2.45, 24.9, -1.75), (3.3, 7.3, 3.5), "sleeve")
    cb("LeftArm", (2.8, 24.125, -1.15), (2.7, 2.05, 2.3), "sleeve")
    cb("LeftArm", (2.2, 31.25, -1.95), (3.75, 0.7, 3.9), "epaulet", dens=3)
    cb("LeftArm", (5.75, 29.6, -1.95), (0.25, 1.7, 3.9), "gold", faces=["west", "east", "north", "south", "down"])
    cb("LeftForeArm", (2.5, 20.6, -1.6), (3.25, 4.8, 3.25), "sleeve")
    cb("LeftForeArm", (2.3, 20.2, -1.8), (3.65, 1.3, 3.65), "cuff", dens=3)
    cb("LeftForeArm", (2.55, 19.6, -1.55), (3.15, 0.65, 3.15), "collar", dens=3)
    cb("LeftForeArm", (2.65, 18.075, -1.45), (2.95, 2.4, 2.95), "hand")
    G.mirror("LeftArm", "RightArm"); G.mirror("LeftForeArm", "RightForeArm")

    # ================= ноги: чёрные прямые брюки, чёрные ботинки на низком каблуке
    cb("LeftLeg", (0.25, 11.65, -2.0), (4.1, 10, 3.95), pants())
    cb("LeftLowerLeg", (0.4, 2.2, -1.85), (3.8, 9.8, 3.7), pants(knee=True))
    cb("LeftLowerLeg", (0.45, 11.0, -1.8), (3.7, 2.6, 3.6), pants())  # сустав колена (как у Priestess R-011)
    cb("LeftLowerLeg", (0.32, 2.1, -1.95), (3.96, 1.1, 3.9), pants())  # низ штанины поверх ботинка
    cb("LeftFoot", (0.55, 0, -2.0), (3.4, 2.3, 4.0), "boot")
    cb("LeftFoot", (0.65, 0, -4.1), (3.2, 1.5, 2.2), "boot")
    cb("LeftFoot", (0.95, 0, -4.4), (2.6, 1.15, 0.35), "boot")
    cb("LeftFoot", (0.85, 0, 1.75), (2.8, 0.85, 0.55), "boot")  # низкий каблук
    G.mirror("LeftLeg", "RightLeg"); G.mirror("LeftLowerLeg", "RightLowerLeg"); G.mirror("LeftFoot", "RightFoot")

    # ================= пальто нараспашку до щиколоток (кости Coat*, корпус UpperBody)
    G.bone("Coat", "UpperBody", (0, 32, 0))
    cb("Coat", (-4.75, 31.4, -2.75), (9.5, 0.7, 5.6), coat())
    # перед: полочки, лацканы с золотой окантовкой по внутреннему краю, золотые пуговицы
    cb("Coat", (1.55, 24.3, -3.08), (3.45, 7.2, 0.2), coat(("xlo", 1)), faces=["north", "south"])
    cb("Coat", (-5.0, 24.3, -3.08), (3.45, 7.2, 0.2), coat(("xhi", 1)), faces=["north", "south"])
    for sg in (-1, 1):  # лацкан: отогнутый треугольником клин у груди
        x0 = 1.55 if sg > 0 else -2.95
        cb("Coat", (x0, 27.4, -3.22), (1.4, 3.9, 0.12), coat(("xlo" if sg > 0 else "xhi", 1), ("top", 1)),
           faces=["north"], rot=(0, 0, 14 * sg), pivot=(1.55 * sg, 31.3, -3.2))
        for y in (28.7, 26.9, 25.1):
            cb("Coat", (2.15 * sg - 0.3, y, -3.28), (0.6, 0.6, 0.18), "gold", share="swatch_gold")
    cb("Coat", (5.0, 23.0, -3.08), (0.2, 8.5, 5.9), coat(), faces=["east", "west"])
    cb("Coat", (-5.2, 23.0, -3.08), (0.2, 8.5, 5.9), coat(), faces=["east", "west"])
    cb("Coat", (-5.2, 23.0, 2.75), (10.4, 8.5, 0.2), coat(), faces=["north", "south"])
    # брошь-медальон с гербом на груди (слева у персонажа)
    G.bone("Brooch", "Coat", (2.35, 29.0, -3.3))
    cb("Brooch", (1.6, 28.25, -3.36), (1.5, 1.5, 0.16), "brooch", dens=6)
    # полы спереди (два сегмента, качаются): внутренний край с золотом, по подолу золотая кайма
    for side, sg in (("L", 1), ("R", -1)):
        xi, xo = 3.0 * sg, 6.0 * sg
        x0, x1 = min(xi, xo), max(xi, xo)
        edge = "xlo" if sg > 0 else "xhi"
        G.bone(f"CoatFront{side}", "Coat", ((x0 + x1) / 2, 24.3, -3.0), (0, 0, -4 * sg))
        cb(f"CoatFront{side}", (x0, 12.5, -3.08), (x1 - x0, 11.8, 0.2), coat((edge, 1)), faces=["north", "south"])
        cb(f"CoatFront{side}", (x0 + (0.3 if sg > 0 else 0.8), 17.0, -3.2), (2.2, 0.55, 0.14), coat(("bottom", 1)), faces=["north"])  # клапан кармана
        G.bone(f"CoatFront{side}Low", f"CoatFront{side}", ((x0 + x1) / 2, 12.5, -3.0))
        cb(f"CoatFront{side}Low", (x0, 2.2, -3.08), (x1 - x0, 10.3, 0.2), coat((edge, 1), ("bottom", 2)), faces=["north", "south"])
    # бока
    for side, x in (("L", 6.1), ("R", -6.3)):
        G.bone(f"CoatSide{side}", "Coat", (x + 0.1, 23.0, 0))
        cb(f"CoatSide{side}", (x, 2.4, -2.95), (0.2, 20.6, 5.8), coat(("bottom", 2)), faces=["east", "west"])
    # спинка: две половины с разрезом под хвост, по два сегмента
    for side, sg in (("L", 1), ("R", -1)):
        xi, xo = 1.9 * sg, 6.3 * sg
        x0, x1 = min(xi, xo), max(xi, xo)
        edge = "xlo" if sg > 0 else "xhi"
        G.bone(f"CoatBack{side}", "Coat", ((x0 + x1) / 2, 23.0, 2.85))
        cb(f"CoatBack{side}", (x0, 12.0, 2.75), (x1 - x0, 11.0, 0.2), coat((edge, 1)), faces=["north", "south"])
        G.bone(f"CoatBack{side}Low", f"CoatBack{side}", ((x0 + x1) / 2, 12.0, 2.85))
        cb(f"CoatBack{side}Low", (x0, 2.2, 2.75), (x1 - x0, 9.8, 0.2), coat((edge, 1), ("bottom", 2)), faces=["north", "south"])
    # ремень-хлястик на спине
    cb("Coat", (-3.6, 23.1, 2.95), (7.2, 0.8, 0.15), coat(("top", 1), ("bottom", 1)), faces=["south"])

    # ================= хвост волчий: пушистый, ровной толщины (без лисьей «кисточки»), висит низко; тёмно-красный →
    # кремово-розовый конец; из разреза пальто, чуть вправо
    segs = [  # (кость, длина, ширина, глубина, t0, t1)
        ("Tail0", 2.6, 2.6, 2.6, 0.00, 0.12),
        ("Tail1", 3.8, 3.6, 3.4, 0.12, 0.33),
        ("Tail2", 4.0, 4.3, 4.0, 0.33, 0.55),
        ("Tail3", 4.0, 4.4, 4.1, 0.55, 0.77),
        ("Tail4", 3.6, 4.2, 3.9, 0.77, 0.95),
    ]
    parent, py, pz = "UpperBody", 20.2, 3.0
    zc = pz + 3.3  # ось хвоста позади разреза пальто и позади ножен меча
    for i, (bn, ln, w, d, t0, t1) in enumerate(segs):
        G.bone(bn, parent, (0, py, pz + (1.6 if i == 0 else 3.3)))  # корень вращается позади спины, остальные — по оси
        cb(bn, (-w / 2, py - ln - 0.7, zc - d / 2), (w, ln + 1.4, d), tail(t0, t1), seed=1100 + i * 10)
        # тот же объём, повёрнутый на 45° вокруг оси хвоста — восьмигранный пушистый силуэт
        k = 0.84
        cb(bn, (-w * k / 2, py - ln - 0.4, zc - d * k / 2), (w * k, ln + 0.8, d * k), tail(t0, t1),
           rot=(0, 45, 0), pivot=(0, py, zc), seed=1105 + i * 10)
        if i == 0:  # корень: из разреза пальто к оси хвоста
            cb(bn, (-0.9, py - 2.4, pz), (1.8, 2.4, zc - pz), tail(0, 0.05), seed=1099)
        parent, py = bn, py - ln
    # тупой конец (волчий хвост к кончику не сужается)
    cb("Tail4", (-1.8, py - 1.7, zc - 1.65), (3.6, 1.2, 3.3), tail(0.95, 1.0), seed=1190)
    cb("Tail4", (-1.3, py - 2.2, zc - 1.2), (2.6, 0.6, 2.4), tail(1.0, 1.0), seed=1191)

    # ================= меч в ножнах за спиной по диагонали: рукоять над правым плечом (сбоку от головы), наконечник —
    # у левого бока на уровне пояса, над хвостом. Ниже пояса не опускается: там качаются полы и шагают ноги.
    sword(G, SWORD_LONG)
    return G


SWORD_LONG = True  # В71 «б» (владелец): ножны до колен; вариант «а» (до пояса) — флаг --sword-short


def sword(G, long=False):
    """Кость Sword: pivot — наконечник ножен; ось меча — локальная +Y, затем поворот (наклон верха назад, диагональ).
    Родитель — SwordSwing с осью у гарды: на ходьбе, беге и т. п. низ длинных ножен отходит назад от ног
    и пол пальто (угол подбирает tune.py)."""
    if long:
        px, py, pz, ang, back, L = 8.6, 9.5, 3.6, -26, -6, 33.0
    else:
        px, py, pz, ang, back, L = 3.0, 24.4, 3.42, -32, -8, 24.0
    # ось качания — у гарды (рукоять над плечом остаётся на месте, отходит только низ ножен)
    G.bone("SwordSwing", "UpperBody", (px - math.sin(math.radians(-ang)) * L * 0.72, py + math.cos(math.radians(-ang)) * L * 0.72, pz))
    G.bone("Sword", "SwordSwing", (px, py, pz), (back, 0, ang))
    cb = G.cube
    sh = L * 0.72                       # ножны
    cb("Sword", (px - 0.6, py, pz - 0.36), (1.2, 0.7, 0.72), "steel")                 # наконечник
    cb("Sword", (px - 0.95, py + 0.5, pz - 0.38), (1.9, 1.3, 0.76), "steel")
    cb("Sword", (px - 1.15, py + 1.6, pz - 0.42), (2.3, sh - 1.6, 0.84), "sheath")
    for k in (0.32, 0.62, 0.95):
        cb("Sword", (px - 1.25, py + sh * k - 0.5, pz - 0.5), (2.5, 0.5, 1.0), "steel", share="swatch_steel")  # обоймы
    cb("Sword", (px - 0.5, py + sh, pz - 0.4), (1.0, 0.5, 0.8), "steel")               # устье
    cb("Sword", (px - 1.6, py + sh + 0.5, pz - 0.5), (3.2, 0.6, 1.0), "steel")         # гарда
    gl = L - sh - 1.9
    cb("Sword", (px - 0.3, py + sh + 1.1, pz - 0.3), (0.6, gl, 0.6), "grip", dens=4)   # рукоять
    cb("Sword", (px - 0.38, py + sh + 1.1 + gl, pz - 0.38), (0.76, 0.75, 0.76), "steel")  # навершие


# ======================================================================= анимации и логика
def parallels():
    A = {}
    sp = "math.clamp(q.ground_speed + (v.hg ?? 0), 0, 20)"
    vs = "math.clamp(-3 * q.vertical_speed, -10, 90)"
    ln = "ysm.second_order('vv_lean', (q.is_sneaking ?? 0) ? 1 : 0, 3, 1, 0)"
    # parallel1: уши — изредка подёргиваются; в приседе прижимаются назад. Хвост — покачивание, отлёт при беге и
    # прыжке, отклик на поворот. Меч — скрывается настройкой.
    twitch = "(math.mod(math.floor(q.anim_time * 2), 9) == 0 ? math.sin(q.anim_time * 1440) * 6 : 0)"
    A["parallel1"] = {"loop": True, "animation_length": 18, "bones": {
        "EarL": {"rotation": [f"-{ln} * 25", 0, f"-{twitch} - {ln} * 12"]},
        "EarR": {"rotation": [f"-{ln} * 25", 0, f"{twitch} + {ln} * 12"]},
        "Tail0": {"rotation": [f"24 + ysm.second_order('vv_tail_h', {sp}, 2, 0.5, 0) * 2.2 + ysm.second_order('vv_tail_v', {vs}, 2, 0.5, 0) * 0.3 - {ln} * 10",
                               "ysm.second_order('vv_tail_y', math.clamp(0.03 * q.yaw_speed, -30, 30), 2, 0.5, 0)",
                               "-7 + math.sin(q.anim_time * 40) * 3"]},
        "Tail1": {"rotation": ["-5 + math.sin(q.anim_time * 40 - 40) * 2", 0, "-4 + math.sin(q.anim_time * 40 - 50) * 3"]},
        "Tail2": {"rotation": [f"-4 - ysm.second_order('vv_tail_h2', {sp}, 2, 0.5, 0) * 0.8", 0, "-3 + math.sin(q.anim_time * 40 - 90) * 4"]},
        "Tail3": {"rotation": [f"-3 - ysm.second_order('vv_tail_h2', {sp}, 2, 0.5, 0) * 0.6", 0, "math.sin(q.anim_time * 40 - 130) * 4"]},
        "Tail4": {"rotation": ["-2", 0, "math.sin(q.anim_time * 40 - 170) * 5"]},
        "Sword": {"scale": "(v.roaming.vv_sword ?? 1) ? 1 : 0"},
    }}
    # parallel4: пальто — «физика» по скорости, вертикали и повороту (как у плаща Priestess);
    # постоянные слагаемые — лёгкий А-силуэт; в приседе корпус наклоняется, полы держим ближе к вертикали
    A["parallel4"] = {"loop": True, "bones": {
        "CoatFrontL": {"rotation": [f"-ysm.second_order('vv_cf', {sp}, 2, 0.6, 0) * 1.2 - 4 - {ln} * 32", 0, 0]},
        "CoatFrontR": {"rotation": [f"-ysm.second_order('vv_cf', {sp}, 2, 0.6, 0) * 1.2 - 4 - {ln} * 32", 0, 0]},
        "CoatFrontLLow": {"rotation": [f"-ysm.second_order('vv_cfl', {sp}, 2, 0.6, 0) * 1.0 - 2 - {ln} * 12", 0, 0]},
        "CoatFrontRLow": {"rotation": [f"-ysm.second_order('vv_cfl', {sp}, 2, 0.6, 0) * 1.0 - 2 - {ln} * 12", 0, 0]},
        "CoatBackL": {"rotation": [f"ysm.second_order('vv_cb', {sp}, 2, 0.6, 0) * 1.4 + ysm.second_order('vv_cv', {vs}, 2, 0.6, 0) * 0.4 + 1 + {ln} * 16", 0, "2"]},
        "CoatBackR": {"rotation": [f"ysm.second_order('vv_cb', {sp}, 2, 0.6, 0) * 1.4 + ysm.second_order('vv_cv', {vs}, 2, 0.6, 0) * 0.4 + 1 + {ln} * 16", 0, "-2"]},
        "CoatBackLLow": {"rotation": [f"ysm.second_order('vv_cbl', {sp}, 2, 0.6, 0) * 1.0 + {ln} * 18", 0,
                                      "ysm.second_order('vv_cy', math.clamp(0.02 * q.yaw_speed, -30, 30), 2, 0.6, 0)"]},
        "CoatBackRLow": {"rotation": [f"ysm.second_order('vv_cbl', {sp}, 2, 0.6, 0) * 1.0 + {ln} * 18", 0,
                                      "ysm.second_order('vv_cy', math.clamp(0.02 * q.yaw_speed, -30, 30), 2, 0.6, 0)"]},
        "CoatSideL": {"rotation": [f"ysm.second_order('vv_cb', {sp}, 2, 0.6, 0) * 0.8 + {ln} * 28", 0, f"-4 - {ln} * 10"]},
        "CoatSideR": {"rotation": [f"ysm.second_order('vv_cb', {sp}, 2, 0.6, 0) * 0.8 + {ln} * 28", 0, f"4 + {ln} * 10"]},
        "Tassel": {"rotation": [f"ysm.second_order('vv_tassel', {sp}, 3, 0.3, 0) * 2.5", 0, "math.sin(q.life_time * 100) * 4"]},
    }}
    return A


POCKETS = {  # руки в карманах пальто: кисти уходят под полы у бёдер
    "LeftArm": {"rotation": [8, 6, 12]}, "LeftForeArm": {"rotation": [-22, 0, 0]},
    "RightArm": {"rotation": [8, -6, -12]}, "RightForeArm": {"rotation": [-22, 0, 0]},
}


def extras():
    E = {}
    E["vv_pockets"] = {"loop": True, "animation_length": 4, "bones": dict(copy.deepcopy(POCKETS), **{
        "Head": {"rotation": kf({0: [2, 0, 0], 2: [3, -4, 0], 4: [2, 0, 0]})},
    })}
    E["vv_glance"] = {"loop": "hold_on_last_frame", "animation_length": 1.2, "bones": dict(copy.deepcopy(POCKETS), **{
        "Head": {"rotation": kf({0: [0, 0, 0], 0.6: [4, 38, 0], 1.2: [4, 42, 0]})},
        "UpBody": {"rotation": kf({0: [0, 0, 0], 0.6: [0, 10, 0], 1.2: [0, 12, 0]})},
        "EarL": {"rotation": kf({0: [0, 0, 0], 0.6: [-10, 0, -6], 1.2: [-8, 0, -4]})},
    })}
    E["vv_hilt"] = {"loop": True, "animation_length": 4, "bones": {  # рука на рукояти меча за плечом
        "RightArm": {"rotation": [-194, -14, -28]}, "RightForeArm": {"rotation": [-32, 0, 0]},  # подобрано: кисть на рукояти
        "LeftArm": {"rotation": [8, 6, 12]}, "LeftForeArm": {"rotation": [-22, 0, 0]},
        "Head": {"rotation": [4, -12, 0]},
    }}
    E["vv_bow"] = {"loop": "hold_on_last_frame", "animation_length": 2.0, "bones": {
        "UpBody": {"rotation": kf({0: [0, 0, 0], 0.7: [28, 0, 0], 1.5: [28, 0, 0], 2.0: [22, 0, 0]})},
        "Head": {"rotation": kf({0: [0, 0, 0], 0.7: [10, 0, 0], 2.0: [8, 0, 0]})},
        "RightArm": {"rotation": kf({0: [0, 0, 0], 0.7: [-40, -30, 0], 2.0: [-36, -30, 0]})},
        "RightForeArm": {"rotation": kf({0: [0, 0, 0], 0.7: [-60, 0, 0], 2.0: [-60, 0, 0]})},
        "LeftArm": {"rotation": kf({0: [0, 0, 0], 0.7: [20, 0, 6], 2.0: [18, 0, 6]})},
        "EarL": {"rotation": kf({0: [0, 0, 0], 0.7: [-14, 0, 0], 2.0: [-10, 0, 0]})},
        "EarR": {"rotation": kf({0: [0, 0, 0], 0.7: [-14, 0, 0], 2.0: [-10, 0, 0]})},
    }}
    return E


kf = PB.kf


def build_arm(default_arm):
    """Руки от первого лица: рукава пальто, золотой отворот, манжета рубашки, кисть."""
    A = Geo()
    for b in default_arm["minecraft:geometry"][0]["bones"]:
        A.bone(b["name"], b.get("parent"), b.get("pivot", [0, 0, 0]), b.get("rotation"))
    dx = 1.15 - 3.4
    dy = 29.775 - 31.025
    cb = A.cube
    cb("LeftArm", (2.45 + dx, 24.9 + dy, -1.75), (3.3, 7.3, 3.5), "sleeve")
    cb("LeftForeArm", (2.5 + dx, 20.6 + dy, -1.6), (3.25, 4.8, 3.25), "sleeve")
    cb("LeftForeArm", (2.3 + dx, 20.2 + dy, -1.8), (3.65, 1.3, 3.65), "cuff", dens=2)
    cb("LeftForeArm", (2.55 + dx, 19.6 + dy, -1.55), (3.15, 0.65, 3.15), "collar", dens=2)
    cb("LeftForeArm", (2.65 + dx, 18.075 + dy, -1.45), (2.95, 2.4, 2.95), "skin")
    A.mirror("LeftArm", "RightArm"); A.mirror("LeftForeArm", "RightForeArm")
    for loc in ("LeftHandLocator", "RightHandLocator"):
        x, y, z = A.byname[loc]["pivot"]
        cb(loc, (x - 0.5, y - 0.5, z - 0.5), (1, 1, 1), "skin")
    return A


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--out", default=str(OUT))
    ap.add_argument("--no-tune", action="store_true", help="без подбора углов (быстрая проба геометрии)")
    ap.add_argument("--sword-short", action="store_true", help="вариант «а»: ножны до пояса (для сравнения; В71 — «б»)")
    a = ap.parse_args()
    global SWORD_LONG
    SWORD_LONG = not a.sword_short
    out = pathlib.Path(a.out)
    rd = PB.ysm_default()
    dgeo = json.loads(rd("models/main.json"))
    darm = json.loads(rd("models/arm.json"))
    G = build_main(dgeo)
    A = build_arm(darm)
    allf = Geo(); allf.faces = G.faces + A.faces
    img, used = allf.pack()
    ncubes = sum(len(b.get("cubes", [])) for b in G.bones)
    print(f"текстура: занято строк {used}/{TEX}, граней {len(allf.faces)}, кубов {ncubes}")
    for d in ("models", "textures", "animations"):
        (out / d).mkdir(parents=True, exist_ok=True)
    (out / "models" / "main.json").write_text(json.dumps(G.json("geometry.vivi"), ensure_ascii=False, separators=(",", ":")))
    (out / "models" / "arm.json").write_text(json.dumps(A.json("geometry.vivi_arm", (3, 4.5, [0, 1.75, 0])), ensure_ascii=False, separators=(",", ":")))
    img.save(out / "textures" / "vivi.png")
    main_an = json.loads(rd("animations/main.animation.json"))
    an = main_an["animations"]
    for k in ("parallel1", "parallel4"):
        an.pop(k, None)
    an.update(parallels())
    # меню (gui): руки в карманах пальто, спокойная стойка
    an["gui"] = copy.deepcopy(an["gui"])
    an["gui"]["bones"].update(copy.deepcopy(POCKETS))
    seat_poses(an)
    sneak_fix(an)
    if not a.no_tune:
        tune_poses(out, an)
    (out / "animations" / "main.animation.json").write_text(json.dumps(main_an, ensure_ascii=False, separators=(",", ":")))
    extra = {"format_version": "1.8.0", "animations": extras()}
    dex = json.loads(rd("animations/extra.animation.json"))["animations"]
    for k, v in dex.items():
        extra["animations"][f"classic_{k}"] = v
    (out / "animations" / "extra.animation.json").write_text(json.dumps(extra, ensure_ascii=False, separators=(",", ":")))
    for f in ("arm", "iss", "tac", "fp.arm"):
        (out / "animations" / f"{f}.animation.json").write_bytes(rd(f"animations/{f}.animation.json"))
    dysm = json.loads(rd("ysm.json"))
    ysm = {
        "spec": 2,
        "metadata": {
            "name": "Vivi",
            "tips": "Vivi — девушка-волчица в бордовом пальто, меч в ножнах за спиной. Авторский персонаж. Модель Minecraft_moded.",
            "license": {"type": "CC BY-NC 4.0", "desc": "Персонаж — авторский (владелец сборки Minecraft_moded). Скелет и базовые анимации — встроенная модель YSM «default» (CC 0)."},
            "authors": [
                {"name": "Minecraft_moded (Поток 69, Claude)", "role": "модель, текстура, уши, хвост, пальто, меч, позы",
                 "comment": "Сборка для друзей, некоммерческая"},
            ] + [{"name": x["name"], "role": f"{x['role']} (default, CC 0)"} for x in dysm["metadata"]["authors"]],
            "link": {"home": "https://github.com/limanov7kiy/minecraft_moded"},
        },
        "properties": {
            "height_scale": 0.7, "width_scale": 0.7,
            "extra_animation": {
                "extra0": "#vv_config", "extra1": "vv_pockets", "extra2": "vv_glance", "extra3": "vv_hilt", "extra4": "vv_bow",
                "#classic": "Ещё позы (default)",
            },
            "extra_animation_buttons": [{
                "id": "vv_config", "name": "Настройки",
                "config_forms": [
                    {"type": "checkbox", "title": "Меч за спиной", "description": "Показывать меч в ножнах за спиной", "value": "v.roaming.vv_sword"},
                ]}],
            "extra_animation_classify": [{"id": "classic", "extra_animation": {f"extra{i}": f"classic_extra{i}" for i in range(8)}}],
            "preview_animation": "gui",
            "default_texture": "vivi",
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
            "texture": ["textures/vivi.png"]}},
    }
    (out / "ysm.json").write_text(json.dumps(ysm, ensure_ascii=False, indent=2) + "\n")
    print("готово:", out)


def seat_poses(an):
    """Посадки default «колени к груди» (sit) и «колени у подбородка» (boat) с длинным пальто дают сквозные колени
    в груди при любом положении пол — у Vivi сидит прямо: бёдра вперёд, голени вниз, руки на коленях."""
    b = an["sit"]["bones"]
    b["UpBody"]["rotation"] = [2, 0, 0]
    for s, sg in (("Left", 1), ("Right", -1)):
        b[f"{s}Leg"]["rotation"] = [-86, 0, -5 * sg]
        b[f"{s}LowerLeg"]["rotation"] = [84, 0, 0]
        b[f"{s}Arm"]["rotation"] = [-38, 0, -4 * sg]
        b[f"{s}ForeArm"]["rotation"] = [-34, 0, 0]
    b = an["boat"]["bones"]
    b["UpBody"]["rotation"] = [8, 0, 0]
    for s, sg in (("Left", 1), ("Right", -1)):
        b[f"{s}Leg"]["rotation"] = [-84, -2 * sg, -10 * sg]
        b[f"{s}LowerLeg"]["rotation"] = [50, 0, 0]
    b = an["ride_pig"]["bones"]
    b["UpBody"]["rotation"] = [6, 0, 0]


def add_rot(an, pose, bone, d):
    """прибавить d = [rx, ry, rz] ко всем ключам поворота кости в позе (числа, dict-ключи pre/post)"""
    tr = an[pose]["bones"][bone]
    def f(v):
        if isinstance(v, list):
            return [round(a + b, 3) if isinstance(a, (int, float)) else f"({a}) + {b}" for a, b in zip(v, d)]
        if isinstance(v, dict) and ("post" in v or "pre" in v):
            return {k: (f(x) if k in ("pre", "post") else x) for k, x in v.items()}
        return v
    r = tr["rotation"]
    tr["rotation"] = {t: f(v) for t, v in r.items()} if isinstance(r, dict) and not ("post" in r or "pre" in r) else f(r)


SNEAK_FIX = {"UpBody": [-20, 0, 0]}
SNEAK_KNEE = -70  # бедро в приседе поднимается не выше (у default — до −103°)


def sneak_fix(an):
    """Присед default: колено поднятой ноги уходит в грудь (и без пальто — ~1000 точек ног внутри корпуса);
    у Vivi корпус наклонён меньше, бедро поднимается не выше SNEAK_KNEE — полы пальто остаются снаружи ног."""
    for pose in ("sneak", "sneaking"):
        for bone, d in SNEAK_FIX.items():
            add_rot(an, pose, bone, d)
        for bone in ("LeftLeg", "RightLeg"):
            tr = an[pose]["bones"][bone]
            r = tr["rotation"]
            cl = lambda v: [max(v[0], SNEAK_KNEE)] + list(v[1:]) if isinstance(v, list) and isinstance(v[0], (int, float)) else v
            tr["rotation"] = {t: cl(v) for t, v in r.items()} if isinstance(r, dict) else cl(r)


TUNE = [("walk", {"q.ground_speed": 4}, True), ("run", {"q.ground_speed": 6}, True), ("sneak", {"q.is_sneaking": 1}, True),
        ("sneaking", {"q.is_sneaking": 1, "q.ground_speed": 2}, True), ("jump", {}, False), ("fly", {}, False),
        ("sit", {}, False), ("ride", {}, False), ("ride_pig", {}, False), ("boat", {}, False), ("climb", {}, False),
        ("climbing", {}, False), ("ladder_up", {}, False), ("ladder_down", {}, False), ("ladder_stillness", {}, False)]


def tune_poses(out, an):
    """Позы default под длинное пальто и хвост: углы пол и хвоста подбираются по пересечениям (tune.py)."""
    import tune
    pre = [dict(an[k], _parallel=True) for k in ("pre_parallel0", "pre_parallel1", "pre_parallel2")]
    par = [dict(an[k], _parallel=True) for k in ("parallel0", "parallel1", "parallel2", "parallel3", "parallel4")]
    rep = tune.tune(out, an, TUNE, pre, par)
    for pose, r in rep.items():
        print(f"  {pose:16s} " + ", ".join(f"{b} {v}" for b, v in r.items()))


if __name__ == "__main__":
    main()
