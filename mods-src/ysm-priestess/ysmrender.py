#!/usr/bin/env python3
"""Офлайн-превью модели Yes Steve Model (Bedrock-геометрия + анимации) без игры.
Нужен потому, что клиент YSM в облаке не запускается (детект ВМ, docs/compat.md К7).

Рисует модель ортографически с z-буфером и текстурой (per-face UV, как Blockbench), применяя
анимации на момент времени t: ключевые кадры (linear/catmullrom ~ linear) и molang-выражения
(свой мини-интерпретатор; ysm.second_order и т.п. — установившееся значение).

Соглашения файла (проверено на встроенной модели default): лицо смотрит в −Z, левая рука игрока — +X;
поворот кости/куба [rx, ry, rz] действует как Rz(−rz)·Ry(ry)·Rx(−rx) вокруг pivot; «east» — грань −X.

Использование как модуль: render(model_dir, анимации, t, вид, vars) → PIL.Image.
CLI: ysmrender.py <папка модели> <out.png> [--anim main:idle,main:parallel0] [--t 0.5] [--view front|back|left|right|34]
"""
import json, math, pathlib, re, sys
import numpy as np
from PIL import Image

# ---------------------------------------------------------------- molang
TOK = re.compile(r"\s*(?:(\d+\.\d*|\.\d+|\d+)|('[^']*')|([A-Za-z_][\w.]*)|(\?\?|&&|\|\||[<>=!]=|[-+*/()<>?:,;!=\[\]{}]))")


class Molang:
    """Мини-интерпретатор molang: числа, строки, переменные (q./v./t./ysm./ctrl.), вызовы, ?:, ??, логика."""

    def __init__(self, ctx):
        self.ctx = ctx  # dict: полное имя переменной → значение; функции — callable

    def tokens(self, s):
        out, i = [], 0
        s = re.sub(r"//[^\n]*", "", s)
        while i < len(s):
            m = TOK.match(s, i)
            if not m:
                if s[i:].strip() == "":
                    break
                raise SyntaxError(f"molang: {s[i:i+20]!r}")
            i = m.end()
            num, st, name, op = m.groups()
            if num: out.append(("n", float(num)))
            elif st: out.append(("s", st[1:-1]))
            elif name: out.append(("id", name))
            elif op: out.append(("op", op))
        out.append(("end", None))
        return out

    def run(self, s):
        if isinstance(s, (int, float)):
            return float(s)
        self.t, self.p = self.tokens(str(s)), 0
        val = 0.0
        while self.t[self.p][0] != "end":
            if self.t[self.p] == ("op", ";"):
                self.p += 1; continue
            if self.t[self.p] == ("id", "return"):
                self.p += 1
                return self.expr()
            val = self.statement()
        return val

    def peek(self):
        return self.t[self.p]

    def eat(self, op=None):
        tk = self.t[self.p]
        if op and tk != ("op", op):
            raise SyntaxError(f"ожидалось {op}, получено {tk}")
        self.p += 1
        return tk

    def statement(self):
        # присваивание var = expr
        tk = self.peek()
        if tk[0] == "id" and self.t[self.p + 1] == ("op", "="):
            self.p += 2
            v = self.expr()
            self.ctx[self.norm(tk[1])] = v
            return v
        if tk == ("op", "{"):
            self.eat("{")
            v = 0.0
            while self.peek() != ("op", "}"):
                if self.peek() == ("op", ";"):
                    self.p += 1; continue
                v = self.statement()
            self.eat("}")
            return v
        return self.expr()

    @staticmethod
    def norm(name):
        name = name.lower()
        for a, b in (("query.", "q."), ("variable.", "v."), ("temp.", "t."), ("context.", "c.")):
            if name.startswith(a):
                name = b + name[len(a):]
        return name

    def expr(self):
        return self.ternary()

    def ternary(self):
        c = self.nullc()
        if self.peek() == ("op", "?"):
            self.eat("?")
            if self.peek() == ("op", "{"):
                a = self.statement()
            else:
                a = self.ternary()
            if self.peek() == ("op", ":"):
                self.eat(":")
                b = self.statement() if self.peek() == ("op", "{") else self.ternary()
            else:
                b = 0.0
            return a if truth(c) else b
        return c

    def nullc(self):
        a = self.orr()
        while self.peek() == ("op", "??"):
            self.eat()
            b = self.orr()
            a = b if a is None else a
        return a

    def orr(self):
        a = self.andd()
        while self.peek() == ("op", "||"):
            self.eat(); b = self.andd(); a = 1.0 if (truth(a) or truth(b)) else 0.0
        return a

    def andd(self):
        a = self.cmp()
        while self.peek() == ("op", "&&"):
            self.eat(); b = self.cmp(); a = 1.0 if (truth(a) and truth(b)) else 0.0
        return a

    def cmp(self):
        a = self.add()
        while self.peek()[0] == "op" and self.peek()[1] in ("<", ">", "<=", ">=", "==", "!="):
            op = self.eat()[1]; b = self.add()
            x, y = num(a), num(b)
            if op == "==" and (isinstance(a, str) or isinstance(b, str)):
                a = 1.0 if a == b else 0.0; continue
            if op == "!=" and (isinstance(a, str) or isinstance(b, str)):
                a = 1.0 if a != b else 0.0; continue
            a = 1.0 if {"<": x < y, ">": x > y, "<=": x <= y, ">=": x >= y, "==": x == y, "!=": x != y}[op] else 0.0
        return a

    def add(self):
        a = self.mul()
        while self.peek() in (("op", "+"), ("op", "-")):
            op = self.eat()[1]; b = self.mul()
            a = num(a) + num(b) if op == "+" else num(a) - num(b)
        return a

    def mul(self):
        a = self.unary()
        while self.peek() in (("op", "*"), ("op", "/")):
            op = self.eat()[1]; b = self.unary()
            a = num(a) * num(b) if op == "*" else (num(a) / num(b) if num(b) else 0.0)
        return a

    def unary(self):
        if self.peek() == ("op", "-"):
            self.eat(); return -num(self.unary())
        if self.peek() == ("op", "!"):
            self.eat(); return 0.0 if truth(self.unary()) else 1.0
        return self.atom()

    def atom(self):
        tk = self.eat()
        if tk[0] in ("n", "s"):
            return tk[1]
        if tk == ("op", "("):
            v = self.expr(); self.eat(")"); return v
        if tk[0] == "id":
            name = self.norm(tk[1])
            if self.peek() == ("op", "("):
                self.eat("(")
                args = []
                while self.peek() != ("op", ")"):
                    args.append(self.expr())
                    if self.peek() == ("op", ","):
                        self.eat(",")
                self.eat(")")
                v = call(name, args, self.ctx)
            else:
                v = self.ctx.get(name)
                if v is None and not name.startswith("v."):
                    v = 0.0
            # доступ к полю .x/.y/.z у результата (ysm.bone_rot(...).x) — не поддерживаем, 0
            return v
        raise SyntaxError(f"molang: неожиданный {tk}")


def truth(v):
    return v is not None and v != 0 and v != ""


def num(v):
    if v is None or isinstance(v, str):
        return 0.0
    return float(v)


def call(name, a, ctx):
    f = ctx.get(name)
    if callable(f):
        return f(*a)
    d = lambda i: num(a[i]) if i < len(a) else 0.0
    if name == "math.sin": return math.sin(math.radians(d(0)))
    if name == "math.cos": return math.cos(math.radians(d(0)))
    if name == "math.abs": return abs(d(0))
    if name == "math.clamp": return min(max(d(0), d(1)), d(2))
    if name == "math.min": return min(d(0), d(1))
    if name == "math.max": return max(d(0), d(1))
    if name == "math.lerp": return d(0) + (d(1) - d(0)) * d(2)
    if name == "math.pow": return d(0) ** d(1)
    if name == "math.sqrt": return math.sqrt(max(0.0, d(0)))
    if name == "math.floor": return math.floor(d(0))
    if name == "math.ceil": return math.ceil(d(0))
    if name == "math.round": return round(d(0))
    if name == "math.mod": return math.fmod(d(0), d(1)) if d(1) else 0.0
    if name == "math.pi": return math.pi
    if name in ("math.random", "math.random_integer"): return (d(0) + d(1)) / 2
    if name == "math.hermite_blend": x = d(0); return 3 * x * x - 2 * x ** 3
    if name == "ysm.second_order": return a[1] if len(a) > 1 else 0.0  # установившееся значение
    if name in ("q.equipped_item_any_tag", "q.equipped_item_all_tags", "q.is_item_name_any"):
        slot, items = a[0], set(a[1:])
        return 1.0 if items & set(ctx.get(f"_held.{slot}", [])) else 0.0
    if name.startswith("ctrl."):
        slot = a[0] if a else ""
        return 1.0 if len(a) > 1 and a[1] in ctx.get(f"_held.{slot}", []) else 0.0
    return 0.0


# ---------------------------------------------------------------- геометрия
def rot_matrix(r):
    rx, ry, rz = (math.radians(x) for x in r)
    rx, rz = -rx, -rz
    cx, sx, cy, sy, cz, sz = math.cos(rx), math.sin(rx), math.cos(ry), math.sin(ry), math.cos(rz), math.sin(rz)
    Rx = np.array([[1, 0, 0], [0, cx, -sx], [0, sx, cx]])
    Ry = np.array([[cy, 0, sy], [0, 1, 0], [-sy, 0, cy]])
    Rz = np.array([[cz, -sz, 0], [sz, cz, 0], [0, 0, 1]])
    return Rz @ Ry @ Rx


def affine(R=None, t=(0, 0, 0)):
    M = np.eye(4)
    if R is not None:
        M[:3, :3] = R
    M[:3, 3] = t
    return M


def interp(track, t, ev):
    """Значение канала анимации (rotation/position/scale) в момент t."""
    def val(v):
        if isinstance(v, dict):
            v = v.get("post", v.get("pre", v.get("vector", [0, 0, 0])))
        def f(x):
            r = num(ev(x))
            return r if math.isfinite(r) else 0.0
        if isinstance(v, (int, float, str)):
            x = f(v); return [x, x, x]
        return [f(x) for x in v]
    if isinstance(track, dict) and track and all(re.match(r"^\d+(\.\d+)?$", k) for k in track):
        keys = sorted(track, key=float)
        ts = [float(k) for k in keys]
        if t <= ts[0]:
            return val(track[keys[0]])
        for i in range(len(ts) - 1):
            if ts[i] <= t <= ts[i + 1]:
                a, b = val(track[keys[i]]), val(track[keys[i + 1]])
                f = (t - ts[i]) / (ts[i + 1] - ts[i]) if ts[i + 1] > ts[i] else 0
                return [x + (y - x) * f for x, y in zip(a, b)]
        return val(track[keys[-1]])
    return val(track)


class Model:
    def __init__(self, geo_path, tex_path):
        g = json.loads(pathlib.Path(geo_path).read_text())["minecraft:geometry"][0]
        self.tw = g["description"]["texture_width"]; self.th = g["description"]["texture_height"]
        self.bones = {b["name"]: b for b in g["bones"]}
        self.order = [b["name"] for b in g["bones"]]
        self.tex = np.asarray(Image.open(tex_path).convert("RGBA")).astype(np.float32) / 255.0
        # PNG может быть крупнее UV-сетки (например 512 px при texture_width 256 — 2 px на единицу UV)
        self.ks = (self.tex.shape[1] / self.tw, self.tex.shape[0] / self.th)

    def world(self, anims, t, ctx):
        """Мировые матрицы костей с учётом анимаций (список dict-анимаций, применяются по порядку)."""
        ev = Molang(ctx).run
        chan = {}
        for an in anims:
            at = t % an["animation_length"] if an.get("loop") is True and an.get("animation_length") else t
            ctx["q.anim_time"] = at
            for bn, tr in an.get("bones", {}).items():
                c = chan.setdefault(bn, {})
                for k in ("rotation", "position", "scale"):
                    if k in tr:
                        try:
                            v = interp(tr[k], at, ev)
                        except Exception as e:  # noqa
                            print(f"molang {bn}.{k}: {e}", file=sys.stderr); continue
                        if k == "rotation" and an.get("_parallel") and "rotation" in c:
                            c[k] = [a + b for a, b in zip(c[k], v)]  # parallel: повороты складываются
                        else:
                            c[k] = v
        W = {}
        for name in self.order:
            b = self.bones[name]
            p = np.array(b.get("pivot", [0, 0, 0]), float)
            r0 = np.array(b.get("rotation", [0, 0, 0]), float)
            c = chan.get(name, {})
            r = r0 + np.array(c.get("rotation", [0, 0, 0]), float)
            pos = np.array(c.get("position", [0, 0, 0]), float)
            sc = np.array(c.get("scale", [1, 1, 1]), float)
            M = affine(t=p + pos) @ affine(rot_matrix(r)) @ affine(np.diag(sc)) @ affine(t=-p)
            par = b.get("parent")
            W[name] = (W[par] @ M) if par in W else M
        return W

    def quads(self, W):
        """Все грани: (4 вершины в мире, 4 uv в пикселях текстуры)."""
        out = []
        for name in self.order:
            b = self.bones[name]
            for c in b.get("cubes", []):
                o = np.array(c["origin"], float); s = np.array(c["size"], float)
                inf = c.get("inflate", 0)
                lo, hi = o - inf, o + s + inf
                x0, y0, z0 = lo; x1, y1, z1 = hi
                C = affine()
                if "rotation" in c:
                    cp = np.array(c.get("pivot", [0, 0, 0]), float)
                    C = affine(t=cp) @ affine(rot_matrix(c["rotation"])) @ affine(t=-cp)
                M = W[name] @ C
                # углы граней в порядке: верх-лево, верх-право, низ-право, низ-лево (как текстура)
                F = {
                    "north": [(x0, y1, z0), (x1, y1, z0), (x1, y0, z0), (x0, y0, z0)],
                    "south": [(x1, y1, z1), (x0, y1, z1), (x0, y0, z1), (x1, y0, z1)],
                    "east": [(x0, y1, z1), (x0, y1, z0), (x0, y0, z0), (x0, y0, z1)],
                    "west": [(x1, y1, z0), (x1, y1, z1), (x1, y0, z1), (x1, y0, z0)],
                    "up": [(x0, y1, z1), (x1, y1, z1), (x1, y1, z0), (x0, y1, z0)],
                    "down": [(x0, y0, z0), (x1, y0, z0), (x1, y0, z1), (x0, y0, z1)],
                }
                uvs = c.get("uv", {})
                if isinstance(uvs, list):  # box UV (раскладка Blockbench)
                    bu, bv = uvs; w_, h_, d_ = s
                    m_ = c.get("mirror", False)
                    e_, w2 = ("west", "east") if m_ else ("east", "west")
                    uvs = {e_: {"uv": [bu, bv + d_], "uv_size": [d_, h_]},
                           "north": {"uv": [bu + d_, bv + d_], "uv_size": [w_, h_]},
                           w2: {"uv": [bu + d_ + w_, bv + d_], "uv_size": [d_, h_]},
                           "south": {"uv": [bu + 2 * d_ + w_, bv + d_], "uv_size": [w_, h_]},
                           "up": {"uv": [bu + d_, bv], "uv_size": [w_, d_]},
                           "down": {"uv": [bu + d_ + w_, bv + d_], "uv_size": [w_, -d_]}}
                    if m_:
                        for f_ in uvs.values():
                            f_["uv"] = [f_["uv"][0] + f_["uv_size"][0], f_["uv"][1]]; f_["uv_size"] = [-f_["uv_size"][0], f_["uv_size"][1]]
                for f, corners in F.items():
                    u = uvs.get(f)
                    if not u or u.get("uv_size", [0, 0]) == [0, 0]:
                        continue
                    (u0, v0), (du, dv) = u["uv"], u["uv_size"]
                    tc = [(u0, v0), (u0 + du, v0), (u0 + du, v0 + dv), (u0, v0 + dv)]
                    P = np.array([list(p) + [1] for p in corners]) @ M.T
                    out.append((P[:, :3], np.array(tc, float), name))
        return out


VIEWS = {"front": 0, "left": 90, "back": 180, "right": 270, "34": 35, "34b": 215}


L0 = np.array([0.2, 1.0, -0.7]) / np.linalg.norm([0.2, 1.0, -0.7])  # два «солнца» освещения сущностей Minecraft
L1 = np.array([-0.2, 1.0, 0.7]) / np.linalg.norm([-0.2, 1.0, 0.7])


def render(model, anims, t=0.0, view="front", ctx=None, scale=12, size=(520, 640), pitch=0.0, bg=(46, 44, 56, 255),
           center=(0, 20, 0), light="mc", world=1.0):
    """light="mc": затенение граней как у сущностей в Minecraft (0.4 + 0.6·(n·L0 + n·L1)), world — освещённость места
    (1 — день на открытом месте, ~0.7 — тень леса); ysmGlow… — всегда полная яркость."""
    ctx = dict(ctx or {})
    W = model.world(anims, t, ctx)
    yaw = math.radians(VIEWS.get(view, view) if not isinstance(view, (int, float)) else view)
    pr = math.radians(pitch)
    # камера: поворот модели вокруг Y на yaw, затем наклон; экран x = +X файла (см. docstring)
    Ry = np.array([[math.cos(yaw), 0, -math.sin(yaw)], [0, 1, 0], [math.sin(yaw), 0, math.cos(yaw)]])
    Rx = np.array([[1, 0, 0], [0, math.cos(pr), -math.sin(pr)], [0, math.sin(pr), math.cos(pr)]])
    V = Rx @ Ry
    w, h = size
    img = np.zeros((h, w, 4), np.float32); img[:] = np.array(bg, np.float32) / 255
    zb = np.full((h, w), np.inf, np.float32)
    if light != "mc":
        light = np.array([0.35, 0.8, -0.5]); light /= np.linalg.norm(light)
    for P, tc, bone in model.quads(W):
        if not np.isfinite(P).all():
            continue
        Q = (P - np.array(center)) @ V.T
        sx = w / 2 + Q[:, 0] * scale; sy = h / 2 - Q[:, 1] * scale; sz = Q[:, 2]
        n = np.cross(Q[1] - Q[0], Q[3] - Q[0])
        nl = np.linalg.norm(n)
        if nl < 1e-9:
            continue
        n /= nl
        # грани рисуем с обеих сторон (плоскости толщиной 0), освещение по модулю
        if light == "mc":
            nw = np.cross(P[1] - P[0], P[3] - P[0]); nw /= max(np.linalg.norm(nw), 1e-9)
            if float(nw @ (np.array(center) - P.mean(0))) > 0:
                nw = -nw  # нормаль наружу (грань смотрит от центра модели)
            shade = min(1.0, 0.4 + 0.6 * (max(0.0, nw @ L0) + max(0.0, nw @ L1))) * world
        else:
            shade = 0.62 + 0.38 * abs(float(n @ (V @ light)))
        if bone.lower().startswith("ysmglow"):
            shade = 1.0
        for tri in ((0, 1, 2), (0, 2, 3)):
            raster(img, zb, sx[list(tri)], sy[list(tri)], sz[list(tri)], tc[list(tri)], model, shade)
    return Image.fromarray((np.clip(img, 0, 1) * 255).astype(np.uint8))


def raster(img, zb, xs, ys, zs, tc, model, shade):
    h, w = zb.shape
    x0, x1 = int(max(0, math.floor(xs.min()))), int(min(w - 1, math.ceil(xs.max())))
    y0, y1 = int(max(0, math.floor(ys.min()))), int(min(h - 1, math.ceil(ys.max())))
    if x0 > x1 or y0 > y1:
        return
    X, Y = np.meshgrid(np.arange(x0, x1 + 1) + 0.5, np.arange(y0, y1 + 1) + 0.5)
    (ax, bx, cx), (ay, by, cy) = xs, ys
    den = (by - cy) * (ax - cx) + (cx - bx) * (ay - cy)
    if abs(den) < 1e-9:
        return
    l1 = ((by - cy) * (X - cx) + (cx - bx) * (Y - cy)) / den
    l2 = ((cy - ay) * (X - cx) + (ax - cx) * (Y - cy)) / den
    l3 = 1 - l1 - l2
    m = (l1 >= -1e-4) & (l2 >= -1e-4) & (l3 >= -1e-4)
    if not m.any():
        return
    z = l1 * zs[0] + l2 * zs[1] + l3 * zs[2]
    u = l1 * tc[0, 0] + l2 * tc[1, 0] + l3 * tc[2, 0]
    v = l1 * tc[0, 1] + l2 * tc[1, 1] + l3 * tc[2, 1]
    kx, ky = model.ks
    ui = np.clip(np.floor(u * kx - 1e-4 * np.sign(tc[1, 0] - tc[0, 0] + 1e-9)).astype(int), 0, model.tex.shape[1] - 1)
    vi = np.clip(np.floor(v * ky - 1e-4 * np.sign(tc[2, 1] - tc[1, 1] + 1e-9)).astype(int), 0, model.tex.shape[0] - 1)
    col = model.tex[vi, ui]
    sub = zb[y0:y1 + 1, x0:x1 + 1]
    m &= (col[..., 3] > 0.05) & (z < sub)
    if not m.any():
        return
    a = col[..., 3:4]
    rgb = col[..., :3] * shade
    dst = img[y0:y1 + 1, x0:x1 + 1]
    blend = rgb * a + dst[..., :3] * (1 - a)
    dst[..., :3] = np.where(m[..., None], blend, dst[..., :3])
    # полупрозрачное не пишет z (чтобы было видно то, что за плащом, если оно нарисовано позже — приблизительно)
    sub[m & (col[..., 3] > 0.9)] = z[m & (col[..., 3] > 0.9)]


def load_anims(model_dir, spec):
    """spec: 'main:idle,main:parallel0' → список dict (имя файла из ysm.json files.player.animation)."""
    d = pathlib.Path(model_dir)
    ysm = json.loads((d / "ysm.json").read_text())
    files = ysm["files"]["player"]["animation"]
    out = []
    for item in filter(None, spec.split(",")):
        f, a = item.split(":", 1)
        an = json.loads((d / files[f]).read_text())["animations"][a]
        an = dict(an); an["_parallel"] = "parallel" in a
        out.append(an)
    return out


if __name__ == "__main__":
    import argparse
    ap = argparse.ArgumentParser()
    ap.add_argument("model"); ap.add_argument("out")
    ap.add_argument("--anim", default=""); ap.add_argument("--t", type=float, default=0.0)
    ap.add_argument("--view", default="front"); ap.add_argument("--tex", default=None)
    ap.add_argument("--var", action="append", default=[], help="имя=значение (v.form_main=1)")
    a = ap.parse_args()
    d = pathlib.Path(a.model)
    ysm = json.loads((d / "ysm.json").read_text())
    tex = a.tex or ysm["files"]["player"]["texture"][0]
    tex = tex if isinstance(tex, str) else tex.get("uv", tex.get("path"))
    m = Model(d / ysm["files"]["player"]["model"]["main"], d / tex)
    ctx = {}
    for kv in a.var:
        k, v = kv.split("="); ctx[k] = float(v)
    views = [float(v) if re.match(r"^-?\d+(\.\d+)?$", v) else v for v in a.view.split(",")]
    ims = [render(m, load_anims(d, a.anim), a.t, v, ctx) for v in views]
    W = sum(i.width for i in ims); H = max(i.height for i in ims)
    sheet = Image.new("RGBA", (W, H)); x = 0
    for i in ims:
        sheet.paste(i, (x, 0)); x += i.width
    sheet.save(a.out)
