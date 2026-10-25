"""Проверка R-011 без игры: точки юбки снаружи плаща по позам (ysmrender.py).

Точки — сетка по граням костей юбки (Skirt*), шаг ~0,25 px. Для каждой точки берётся слой плаща (кости Cape*) на той же высоте
(±0,3) и его выпуклая оболочка в плоскости XZ; точка снаружи оболочки (запас 0,05) — «юбка сквозь плащ». Слои, где плаща нет
(ниже подола), не считаются. Лежачие состояния (сон, плавание, смерть) не меряются — слой по высоте у них не имеет смысла.
Использование: skirtcheck.py [папка модели] (по умолчанию pack/config/yes_steve_model/custom/priestess).
"""
import sys, pathlib
import numpy as np
HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import ysmrender as R

D = pathlib.Path(sys.argv[1]) if len(sys.argv) > 1 else HERE.parent.parent / "pack/config/yes_steve_model/custom/priestess"
PAR = "main:pre_parallel0,main:pre_parallel1,main:pre_parallel2,main:parallel0,main:parallel1,main:parallel2,main:parallel3,main:parallel4"
POSES = [("idle", "main:idle", {}), ("gui", "main:gui", {}), ("walk", "main:walk", {"q.ground_speed": 4}),
         ("jump", "main:jump", {}), ("fly", "main:fly", {}), ("run", "main:run", {"q.ground_speed": 6}),
         ("boat", "main:boat", {}), ("sneak", "main:sneak", {"q.is_sneaking": 1}),
         ("sneaking", "main:sneaking", {"q.is_sneaking": 1, "q.ground_speed": 2}), ("ride", "main:ride", {}),
         ("sit", "main:sit", {})]


def samples(P, step=0.25):
    a, b, d = P[0], P[1], P[3]
    nu = max(2, int(np.linalg.norm(b - a) / step) + 1); nv = max(2, int(np.linalg.norm(d - a) / step) + 1)
    u, v = np.meshgrid(np.linspace(0, 1, nu), np.linspace(0, 1, nv))
    return a + u.reshape(-1, 1) * (b - a) + v.reshape(-1, 1) * (d - a)


def hull(pts):
    pts = sorted(set(map(tuple, np.round(pts, 4))))
    if len(pts) < 3:
        return None
    def cross(o, a, b):
        return (a[0] - o[0]) * (b[1] - o[1]) - (a[1] - o[1]) * (b[0] - o[0])

    def half(seq):
        h = []
        for p in seq:
            while len(h) >= 2 and cross(h[-2], h[-1], p) <= 0:
                h.pop()
            h.append(p)
        return h
    lo, up = half(pts), half(reversed(pts))
    return np.array(lo[:-1] + up[:-1])


def outside(h, p, eps=0.05):
    for i in range(len(h)):
        a, b = h[i], h[(i + 1) % len(h)]
        e = b - a
        if (e[0] * (p[1] - a[1]) - e[1] * (p[0] - a[0])) < -eps * np.linalg.norm(e):
            return True
    return False


def check(model, spec, ctx, ts=(0.0, 0.2, 0.4, 0.6)):
    worst = 0
    for t in ts:
        W = model.world(R.load_anims(D, spec + "," + PAR), t, dict(ctx))
        qs = model.quads(W)
        cape = np.concatenate([samples(P, 0.3) for P, _, b in qs if b.startswith("Cape")])
        skirt = np.concatenate([samples(P) for P, _, b in qs if b.startswith("Skirt")])
        n = 0
        cache = {}
        for p in skirt:
            k = round(p[1] / 0.3)
            if k not in cache:
                lay = cape[np.abs(cape[:, 1] - p[1]) < 0.3]
                cache[k] = hull(lay[:, [0, 2]]) if len(lay) else None
            h = cache[k]
            if h is not None and outside(h, p[[0, 2]]):
                n += 1
        worst = max(worst, n)
    return worst


if __name__ == "__main__":
    m = R.Model(D / "models/main.json", D / "textures/priestess.png")
    for name, spec, ctx in POSES:
        print(f"{name:9s} {check(m, spec, ctx)}")
