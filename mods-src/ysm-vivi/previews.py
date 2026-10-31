"""Листы превью Vivi без игры (ysmrender.py из ysm-priestess). Использование: previews.py [папка модели] [папка вывода]."""
import sys, json, pathlib
HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE.parent / 'ysm-priestess'))
import ysmrender as R
from PIL import Image, ImageDraw, ImageFont
FONT = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 18)
D = pathlib.Path(sys.argv[1]) if len(sys.argv) > 1 else HERE.parent.parent / 'pack/config/yes_steve_model/custom/vivi'
OUT = pathlib.Path(sys.argv[2]) if len(sys.argv) > 2 else HERE.parent.parent / 'docs/img/ysm-vivi'
OUT.mkdir(parents=True, exist_ok=True)
m = R.Model(D / 'models/main.json', D / 'textures/vivi.png')
P = 'main:pre_parallel0,main:pre_parallel1,main:pre_parallel2,main:parallel0,main:parallel1,main:parallel2,main:parallel3,main:parallel4'


def shot(anim, t, view, ctx={}, **kw):
    return R.render(m, R.load_anims(D, anim + ',' + P), t, view, dict(ctx), **kw)


def sheet(ims, labels, out, cols=None):
    cols = cols or len(ims); rows = (len(ims) + cols - 1) // cols
    w, h = ims[0].size
    S = Image.new('RGBA', (w * cols, (h + 28) * rows), (30, 28, 38, 255)); d = ImageDraw.Draw(S)
    for i, (im, l) in enumerate(zip(ims, labels)):
        x, y = (i % cols) * w, (i // cols) * (h + 28)
        S.paste(im, (x, y + 28)); d.text((x + 10, y + 4), l, fill=(235, 230, 245, 255), font=FONT)
    S.save(OUT / out)


ex = json.loads((D / 'animations/extra.animation.json').read_text())['animations']


def xs(name, t, view, **kw):
    an = dict(ex[name]); an['_parallel'] = False
    return R.render(m, [*R.load_anims(D, 'main:pre_parallel0,main:pre_parallel1,main:pre_parallel2'), *R.load_anims(D, 'main:idle'), an,
                        *R.load_anims(D, 'main:parallel0,main:parallel1,main:parallel2,main:parallel3,main:parallel4')], t, view,
                    {'ctrl.playing_extra_animation': 1}, **kw)


if __name__ == '__main__':
    v = ['front', '34', 'left', 'back', '34b']
    sheet([shot('main:idle', 1, x) for x in v], ['спереди', '3/4', 'сбоку', 'сзади', 'сзади 3/4'], '01_views.png')
    sheet([shot('main:gui', 1, 'front', scale=6, center=(0, 0, 0))], ['меню (gui): руки в карманах'], '02_menu.png')
    sheet([shot('main:walk', 0.25, '34'), shot('main:run', 0.3, '34'), shot('main:sneak', 0.3, '34'), shot('main:sneaking', 0.3, 'left', {'q.is_sneaking': 1}),
           shot('main:sit', 0.3, '34'), shot('main:ride', 0.3, '34'), shot('main:fly', 0.3, '34'), shot('main:boat', 0.3, '34'),
           shot('main:swim', 0.3, 'left'), shot('main:elytra_fly', 0.3, 'left'), shot('main:jump', 0.3, 'left'), shot('main:sleep', 0.3, 'left')],
          ['ходьба', 'бег', 'присед', 'присед в движении', 'сидит', 'езда', 'полёт', 'лодка', 'плавание', 'элитры', 'прыжок', 'сон'], '03_states.png', cols=4)
    sheet([shot('main:idle', 1, 'front', {}, size=(340, 380), scale=24, center=(0, 38.5, 0)),
           shot('main:idle', 1, '34', {}, size=(340, 380), scale=24, center=(0, 38.5, 0)),
           shot('main:idle', 1, 'left', {}, size=(340, 380), scale=24, center=(0, 38.5, 0)),
           shot('main:idle', 1, 'back', {}, size=(340, 380), scale=24, center=(0, 38.5, 0))],
          ['лицо', 'голова 3/4', 'голова сбоку', 'затылок'], '04_head.png')
    sheet([xs('vv_pockets', 1, 'front'), xs('vv_pockets', 1, '34'), xs('vv_glance', 1.2, '34'), xs('vv_hilt', 1, '34'), xs('vv_bow', 2, 'left')],
          ['поза: руки в карманах', '…3/4', 'поза: взгляд через плечо', 'поза: рука на рукояти', 'поза: поклон'], '05_poses.png')
