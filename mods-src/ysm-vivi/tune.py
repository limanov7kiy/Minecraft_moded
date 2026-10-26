"""Подбор углов пальто и хвоста по позам default (вызывается из build.py после записи геометрии).

Для каждой позы по очереди для костей CoatFront*, CoatBack*, CoatSide*, затем Tail0 ищется наименьший постоянный
добавочный поворот (сначала 0, потом всё больше; у хвоста — ещё и вбок), при котором за весь цикл анимации (кадры через
1/24 с — шаг ключей default) точки панели (кость с потомками) не попадают внутрь ног и меча (хвост — внутрь ног, меча и пальто), а в стоячих позах — не уходят ниже стоп;
если без пересечений нельзя — берётся поворот с наименьшей суммой. Нижний сегмент передней полы сначала пробует висеть
отвесно (когда верх полы лёг на бёдра — сидя, в лодке, — он свисает с колен, а не торчит вперёд). Постоянный (а не по кадрам) — чтобы полы не дёргались
и между ключевыми кадрами ничего не проскакивало. Так полы «отходят» от шагающей ноги, ложатся на бёдра сидя и не
проходят сквозь землю в приседе. Результат — rotation этих костей в самой позе (parallel4 добавляет физику
сверху). Проверка результата — vivicheck.py.
"""
import pathlib, sys
import numpy as np
HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE.parent / "ysm-priestess"))
import ysmrender as R

STEP_T = 1 / 24
LEGS = ("LeftLeg", "LeftLowerLeg", "LeftFoot", "RightLeg", "RightLowerLeg", "RightFoot")


class Fast(R.Model):
    """Model с кэшем каналов: мировые матрицы пересчитываются с добавочными поворотами без повторного molang."""

    def channels(self, anims, t, ctx):
        ev = R.Molang(ctx).run
        chan = {}
        for an in anims:
            at = t % an["animation_length"] if an.get("loop") is True and an.get("animation_length") else t
            if an.get("loop") == "hold_on_last_frame" and an.get("animation_length"):
                at = min(t, an["animation_length"])
            ctx["q.anim_time"] = at
            for bn, tr in an.get("bones", {}).items():
                c = chan.setdefault(bn, {})
                for k in ("rotation", "position", "scale"):
                    if k in tr:
                        try:
                            v = R.interp(tr[k], at, ev)
                        except Exception:  # noqa
                            continue
                        if k == "rotation" and an.get("_parallel") and "rotation" in c:
                            c[k] = [a + b for a, b in zip(c[k], v)]
                        else:
                            c[k] = v
        return chan

    def world_from(self, chan, extra):
        W = {}
        for name in self.order:
            b = self.bones[name]
            p = np.array(b.get("pivot", [0, 0, 0]), float)
            c = chan.get(name, {})
            r = np.array(b.get("rotation", [0, 0, 0]), float) + np.array(c.get("rotation", [0, 0, 0]), float)
            if name in extra:
                r = r + np.array([extra[name][0], 0, extra[name][1]])
            pos = np.array(c.get("position", [0, 0, 0]), float)
            sc = np.array(c.get("scale", [1, 1, 1]), float)
            M = R.affine(t=p + pos) @ R.affine(R.rot_matrix(r)) @ R.affine(np.diag(sc)) @ R.affine(t=-p)
            par = b.get("parent")
            W[name] = (W[par] @ M) if par in W else M
        return W

    def cubes_of(self, names):
        out = []
        for n in names:
            for c in self.bones[n].get("cubes", []):
                o = np.array(c["origin"], float); s = np.array(c["size"], float)
                C = R.affine()
                if "rotation" in c:
                    cp = np.array(c.get("pivot", [0, 0, 0]), float)
                    C = R.affine(t=cp) @ R.affine(R.rot_matrix(c["rotation"])) @ R.affine(t=-cp)
                out.append((n, C, o, o + s))
        return out


def surf(cubes, W, step=0.45):
    """точки на гранях кубов (мир)"""
    pts = []
    for n, C, lo, hi in cubes:
        M = W[n] @ C
        s = hi - lo
        k = [max(2, int(abs(x) / step) + 1) for x in s]
        g = [np.linspace(lo[i], hi[i], k[i]) for i in range(3)]
        for ax in range(3):
            for v in (lo[ax], hi[ax]):
                o = [g[i] if i != ax else np.array([v]) for i in range(3)]
                X, Y, Z = np.meshgrid(*o, indexing="ij")
                P = np.c_[X.ravel(), Y.ravel(), Z.ravel(), np.ones(X.size)]
                pts.append((P @ M.T)[:, :3])
    return np.concatenate(pts) if pts else np.zeros((0, 3))


def inside(cubes, W, pts, eps=0.08):
    hit = np.zeros(len(pts), bool)
    if not len(pts):
        return hit
    H = np.c_[pts, np.ones(len(pts))]
    for n, C, lo, hi in cubes:
        L = (H @ np.linalg.inv(W[n] @ C).T)[:, :3]
        hit |= np.all((L > lo + eps) & (L < hi - eps), axis=1)
    return hit


def subtree(model, root):
    out = [root]
    for n in model.order:
        if model.bones[n].get("parent") in out:
            out.append(n)
    return out


def tune(model_dir, an, poses, pre, par):
    """an — словарь анимаций main (меняется на месте); poses — [(имя, ctx, стоячая?)]; pre/par — списки dict-анимаций."""
    d = pathlib.Path(model_dir)
    m = Fast(d / "models/main.json", d / "textures/vivi.png")
    legs = m.cubes_of(LEGS)
    tails = m.cubes_of([n for n in m.order if n.startswith("Tail")])
    sword = m.cubes_of(["Sword"])
    one = lambda rng: [(a, 0) for a in rng]
    both = lambda n: one([x * k for x in range(0, n, 4) for k in (1, -1)][1:])
    # хвост: вверх-вниз и вбок (z), сначала маленькие отклонения
    tail_c = sorted({(x, z) for x in range(-24, 104, 4) for z in range(-60, 64, 6)}, key=lambda v: (v[0] ** 2 + (1.3 * v[1]) ** 2, v))
    ORDER = []
    for s in ("L", "R"):
        ORDER += [(f"CoatFront{s}", one(range(0, -96, -4))), (f"CoatFront{s}Low", both(64)),
                  (f"CoatBack{s}", one(range(0, 56, 4))), (f"CoatBack{s}Low", both(64)),
                  (f"CoatSide{s}", one(range(0, 64, 4)))]
    ORDER.append(("Tail0", tail_c))  # хвост — после пол: у него больше свободы (вверх-вниз и вбок)
    own = {b: m.cubes_of(subtree(m, b)) for b, _ in ORDER}
    rigid = m.cubes_of(["Coat", "Hips", "UpperBody"])
    report = {}

    def score(bone, W, ground):
        if bone == "Tail0":
            tp = surf(tails, W)
            coat = surf([c for b, _ in ORDER if b != "Tail0" for c in own[b]] + rigid, W)
            sc = inside(legs, W, tp).sum() + inside(tails, W, coat).sum() + inside(tails, W, surf(sword, W)).sum()
            return sc + ((tp[:, 1] < -0.3).sum() if ground else 0)
        pts = surf(own[bone], W)
        sc = inside(legs, W, pts).sum() + inside(sword, W, pts).sum()
        return sc + ((pts[:, 1] < -0.3).sum() if ground else 0)

    for pose, ctx, ground in poses:
        A = an[pose]
        L = A.get("animation_length") if A.get("loop") is not False else None
        times = [0.0] if not L else list(np.arange(0, L, STEP_T))
        anims = pre + [dict(A, _parallel=False)] + par
        chans = [m.channels(anims, t, dict(ctx)) for t in times]
        extra = {}
        for bone, cands in ORDER:
            best, bs = (0, 0), None
            if bone.startswith("CoatFront") and bone.endswith("Low"):
                # нижний сегмент полы висит отвесно (как под тяжестью), если это не даёт пересечений
                up = -extra.get(bone[:-3], (0, 0))[0]
                cands = sorted(one(range(-96, 100, 4)), key=lambda v: (abs(v[0] - up), v))
            for ang in cands:
                extra[bone] = ang
                sc = 0
                for ch in chans:
                    sc += score(bone, m.world_from(ch, extra), ground)
                    if bs is not None and sc >= bs:
                        break
                if bs is None or sc < bs:
                    best, bs = ang, sc
                if sc == 0:
                    break
            extra[bone] = best
        rep = {}
        for bone, v in extra.items():
            if v != (0, 0):
                A["bones"][bone] = {"rotation": [v[0], 0, v[1]]}
                rep[bone] = v
        report[pose] = rep
    return report
