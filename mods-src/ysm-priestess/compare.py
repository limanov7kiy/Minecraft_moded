"""Сравнение Priestess с открытыми моделями Arknights (Elaina69) при освещении сущностей Minecraft, вид 3-го лица.
Использование: compare.py <папка вывода> <папка модели-референса> [ещё референсы...]"""
import contextlib, io, json, pathlib, re, sys
HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import ysmrender as R
from PIL import Image, ImageDraw, ImageFont
FONT = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 20)


def lj(p):
    t = pathlib.Path(p).read_text(encoding='utf-8-sig'); t = re.sub(r'(?m)^\s*//.*$', '', t)
    return json.loads(re.sub(r',(\s*[}\]])', r'\1', t))


def load(d):
    d = pathlib.Path(d); y = lj(d / 'ysm.json'); pl = y['files']['player']
    tex = pl['texture'][0]; tex = tex if isinstance(tex, str) else tex['uv']
    m = R.Model(d / pl['model']['main'], d / tex)
    an = lj(d / pl['animation']['main'])['animations']
    seq = [dict(an[k], _parallel=False) for k in sorted(an) if re.match(r'^pre_parallel\d$', k)]
    seq += [dict(an['idle'], _parallel=False)] if 'idle' in an else []
    seq += [dict(an[k], _parallel=True) for k in sorted(an) if re.match(r'^parallel\d$', k)]
    return m, seq


def shot(d, view, **kw):
    m, seq = load(d)
    with contextlib.redirect_stderr(io.StringIO()):
        return R.render(m, seq, 1.0, view, {}, world=0.82, pitch=12, bg=(118, 150, 104, 255), **kw)


def row(items, out):
    w, h = items[0][0].size
    S = Image.new('RGBA', (w * len(items), h + 34), (30, 28, 38, 255)); d = ImageDraw.Draw(S)
    for i, (im, label) in enumerate(items):
        S.paste(im, (i * w, 34)); d.text((i * w + 10, 6), label, fill=(240, 236, 248, 255), font=FONT)
    S.save(out)


def forms_sheet(out):
    """13 форм осколка в правой руке, крупно, игровое освещение; позы удержания из arm.animation default."""
    me = HERE.parent.parent / 'pack/config/yes_steve_model/custom/priestess'
    m, seq = load(me)
    arm = lj(me / 'animations/arm.animation.json')['animations']
    holds = {1: 'sword', 2: 'axe', 3: 'pickaxe', 4: 'shovel', 5: 'hoe', 6: 'bow', 7: 'crossbow', 8: 'shield', 9: 'spear'}
    names = ['меч', 'топор', 'кирка', 'лопата', 'мотыга', 'лук', 'арбалет', 'щит', 'копьё/трезубец', 'посох/жезл', 'фолиант', 'еда/зелье', 'прочее']
    items = []
    for i, n in enumerate(names, 1):
        s2 = list(seq)
        k = f'hold_mainhand:{holds[i]}' if i in holds else None
        # нейтральная поза: в позах удержания default (инструмент «на плече») форма, как и обычный предмет, уходит за плечо
        ctx = {'v.form_r': i}
        W = m.world(s2, 0.6, dict(ctx))
        c = (W['RightHand'] @ [*m.bones['RightHand']['pivot'], 1])[:3]
        with contextlib.redirect_stderr(io.StringIO()):
            im = R.render(m, s2, 0.6, -55, ctx, world=0.82, pitch=12, bg=(118, 150, 104, 255), scale=10, size=(400, 520),
                          center=(c[0] - 2, c[1] + 5, c[2]))
        items.append((im, n))
    w, h = items[0][0].size; cols = 7
    S = Image.new('RGBA', (w * cols, (h + 34) * 2), (30, 28, 38, 255)); d = ImageDraw.Draw(S)
    for i, (im, label) in enumerate(items):
        x, y = (i % cols) * w, (i // cols) * (h + 34)
        S.paste(im, (x, y + 34)); d.text((x + 10, y + 6), label, fill=(240, 236, 248, 255), font=FONT)
    S.save(out)


def face_sheet(out):
    me = HERE.parent.parent / 'pack/config/yes_steve_model/custom/priestess'
    m, seq = load(me)
    items = []
    for v, ctx, n in ((0, {}, 'обычно'), (0, {'v.roaming.pr_eyes': 1}, 'вспышка: атака/использование/чат'), (30, {}, '3/4')):
        with contextlib.redirect_stderr(io.StringIO()):
            items.append((R.render(m, seq, 1.0, v, ctx, world=0.82, bg=(118, 150, 104, 255), scale=34, size=(460, 420),
                                   center=(0, 35.5, 0)), f'лицо: {n}'))
    row(items, out)


def companion_sheet(out):
    """осколок-спутник крупно с разных сторон"""
    me = HERE.parent.parent / 'pack/config/yes_steve_model/custom/priestess'
    m, seq = load(me)
    items = []
    for t, v in ((0.0, 0), (1.5, 30), (3.0, -30), (4.5, 0)):
        W = m.world(seq, t, {})
        c = (W['Shard'] @ [*m.bones['Shard']['pivot'], 1])[:3]
        with contextlib.redirect_stderr(io.StringIO()):
            items.append((R.render(m, seq, t, v, {}, world=0.82, pitch=8, bg=(118, 150, 104, 255), scale=26, size=(360, 420),
                                   center=tuple(c)), f'осколок, t={t:g} с'))
    row(items, out)


if __name__ == '__main__':
    out = pathlib.Path(sys.argv[1]); out.mkdir(parents=True, exist_ok=True)
    me = HERE.parent.parent / 'pack/config/yes_steve_model/custom/priestess'
    refs = sys.argv[2:]
    for view, tag in ((35, '34'), (0, 'front'), (200, 'back')):
        row([(shot(me, view), 'Priestess')] + [(shot(r, view), pathlib.Path(r).name) for r in refs], out / f'cmp_{tag}.png')
    face_sheet(out / 'cmp_face.png'); forms_sheet(out / 'cmp_forms.png'); companion_sheet(out / 'cmp_shard.png')
