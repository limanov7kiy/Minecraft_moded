"""Проверка Vivi без игры (как skirtcheck.py у Priestess): пересечения по позам через ysmrender.py.

Сетка точек (шаг ~0,3 px) по граням одних костей проверяется на попадание внутрь кубов других костей (куб сжат на 0,08,
чтобы касание не считалось). Считается максимум по моментам анимации t = 0; 0,1; … 0,9 с:
  ноги→пальто — точки пальто (Coat*) внутри ног (бедро, голень, стопа), т. е. нога прошла сквозь полу;
  хвост→ноги — точки хвоста (Tail*) внутри ног;   хвост→пальто — точки пальто внутри хвоста;
  меч→хвост — точки меча внутри хвоста;  меч→пальто — точки пальто внутри меча;  меч→ноги — точки меча внутри ног;            уши→волосы — точки ушей ниже макушки внутри головы не считаются (так задумано);
  ноги→тело — точки корпуса (UpperBody, Breast) внутри ног (у default в приседе ~1000 — поза поправлена);
  земля — точки пальто и хвоста ниже стоп (y < −0,3) в стоячих позах.
Лежачие состояния (сон, плавание, элитры, смерть) не меряются.
Использование: vivicheck.py [папка модели] (по умолчанию pack/config/yes_steve_model/custom/vivi).
"""
import sys, pathlib
import numpy as np
HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE.parent / "ysm-priestess"))
import ysmrender as R

D = pathlib.Path(sys.argv[1]) if len(sys.argv) > 1 else HERE.parent.parent / "pack/config/yes_steve_model/custom/vivi"
PAR = "main:pre_parallel0,main:pre_parallel1,main:pre_parallel2,main:parallel0,main:parallel1,main:parallel2,main:parallel3,main:parallel4"
POSES = [("idle", "main:idle", {}), ("gui", "main:gui", {}), ("walk", "main:walk", {"q.ground_speed": 4}),
         ("run", "main:run", {"q.ground_speed": 6}), ("jump", "main:jump", {}), ("fly", "main:fly", {}),
         ("sneak", "main:sneak", {"q.is_sneaking": 1}), ("sneaking", "main:sneaking", {"q.is_sneaking": 1, "q.ground_speed": 2}),
         ("sit", "main:sit", {}), ("ride", "main:ride", {}), ("ride_pig", "main:ride_pig", {}), ("boat", "main:boat", {}),
         ("climb", "main:climb", {}), ("climbing", "main:climbing", {}), ("ladder_up", "main:ladder_up", {}),
         ("ladder_down", "main:ladder_down", {})]
LEGS = ("LeftLeg", "LeftLowerLeg", "LeftFoot", "RightLeg", "RightLowerLeg", "RightFoot")
GROUND = {"idle", "walk", "run", "sneak", "sneaking"}


def boxes(model, W, pred):
    out = []
    for name in model.order:
        if not pred(name):
            continue
        for c in model.bones[name].get("cubes", []):
            o = np.array(c["origin"], float); s = np.array(c["size"], float)
            M = W[name]
            if "rotation" in c:
                cp = np.array(c.get("pivot", [0, 0, 0]), float)
                M = M @ R.affine(t=cp) @ R.affine(R.rot_matrix(c["rotation"])) @ R.affine(t=-cp)
            out.append((np.linalg.inv(M), o, o + s))
    return out


def inside(bx, pts, eps=0.08):
    if not len(pts) or not bx:
        return np.zeros(len(pts), bool)
    H = np.c_[pts, np.ones(len(pts))]
    hit = np.zeros(len(pts), bool)
    for Mi, lo, hi in bx:
        L = (H @ Mi.T)[:, :3]
        hit |= np.all((L > lo + eps) & (L < hi - eps), axis=1)
    return hit


def samples(qs, pred, step=0.3):
    pts = []
    for P, _, b in qs:
        if not pred(b):
            continue
        a, bb, d = P[0], P[1], P[3]
        nu = max(2, int(np.linalg.norm(bb - a) / step) + 1); nv = max(2, int(np.linalg.norm(d - a) / step) + 1)
        u, v = np.meshgrid(np.linspace(0, 1, nu), np.linspace(0, 1, nv))
        pts.append(a + u.reshape(-1, 1) * (bb - a) + v.reshape(-1, 1) * (d - a))
    return np.concatenate(pts) if pts else np.zeros((0, 3))


is_coat = lambda b: b.startswith("Coat")
is_tail = lambda b: b.startswith("Tail")
is_leg = lambda b: b in LEGS
is_sword = lambda b: b == "Sword"


def check(model, name, spec, ctx):
    ts = [i / 10 for i in range(10)]
    res = {"ноги→пальто": 0, "хвост→ноги": 0, "хвост→пальто": 0, "меч→хвост": 0, "меч→пальто": 0, "меч→ноги": 0, "ноги→тело": 0, "земля": 0}
    for t in ts:
        W = model.world(R.load_anims(D, spec + "," + PAR), t, dict(ctx))
        qs = model.quads(W)
        legs, tailb = boxes(model, W, is_leg), boxes(model, W, is_tail)
        coat, tail, sword = samples(qs, is_coat), samples(qs, is_tail), samples(qs, is_sword)
        r = {"ноги→пальто": int(inside(legs, coat).sum()), "хвост→ноги": int(inside(legs, tail).sum()),
             "хвост→пальто": int(inside(tailb, coat).sum()), "меч→хвост": int(inside(tailb, sword).sum()),
             "меч→пальто": int(inside(boxes(model, W, is_sword), coat).sum()), "меч→ноги": int(inside(legs, sword).sum()),
             "ноги→тело": int(inside(legs, samples(qs, lambda b: b in ("UpperBody", "Breast"))).sum()),
             "земля": int((np.r_[coat[:, 1], tail[:, 1]] < -0.3).sum()) if name in GROUND else 0}
        for k, v in r.items():
            res[k] = max(res[k], v)
    return res


def detail(m, pose, t=0.4):
    """какие кости пальто/хвоста задеты в позе (для отладки): vivicheck.py <папка> --detail <поза>"""
    spec, ctx = next((s, c) for n, s, c in POSES if n == pose)
    W = m.world(R.load_anims(D, spec + "," + PAR), t, dict(ctx))
    qs = m.quads(W)
    legs, tailb = boxes(m, W, is_leg), boxes(m, W, is_tail)
    out = {}
    for q in qs:
        if q[2].startswith(("Coat", "Tail", "Sword")):
            s = samples([q], lambda b: True)
            n = int(inside(legs, s).sum()) + (int(inside(tailb, s).sum()) if not q[2].startswith("Tail") else 0)
            if n:
                out[q[2]] = out.get(q[2], 0) + n
    print(pose, t, out)


if __name__ == "__main__" and "--detail" in sys.argv:
    m = R.Model(D / "models/main.json", D / "textures/vivi.png")
    for p in sys.argv[sys.argv.index("--detail") + 1:]:
        for t in (0.0, 0.2, 0.4, 0.6):
            detail(m, p, t)
elif __name__ == "__main__":
    m = R.Model(D / "models/main.json", D / "textures/vivi.png")
    keys = ["ноги→пальто", "хвост→ноги", "хвост→пальто", "меч→хвост", "меч→пальто", "меч→ноги", "ноги→тело", "земля"]
    print(f"{'поза':9s} " + " ".join(f"{k:>12s}" for k in keys))
    tot = 0
    for name, spec, ctx in POSES:
        r = check(m, name, spec, ctx)
        tot += sum(r.values())
        print(f"{name:9s} " + " ".join(f"{r[k]:12d}" for k in keys))
    print("всего", tot)
