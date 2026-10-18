"""Листы превью Priestess без игры (ysmrender.py). Использование: previews.py [папка вывода] (по умолчанию docs/img/ysm-priestess)."""
import os, sys, json, pathlib
HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import ysmrender as R
from PIL import Image, ImageDraw, ImageFont
FONT = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 18)
D = HERE.parent.parent / 'pack/config/yes_steve_model/custom/priestess'
OUT = pathlib.Path(sys.argv[1]) if len(sys.argv) > 1 else HERE.parent.parent / 'docs/img/ysm-priestess'
OUT.mkdir(parents=True, exist_ok=True); os.chdir(OUT)
m = R.Model(D/'models/main.json', D/'textures/priestess.png')
P = 'main:pre_parallel0,main:pre_parallel1,main:pre_parallel2,main:parallel0,main:parallel1,main:parallel2,main:parallel3,main:parallel4'
def shot(anim, t, view, ctx={}, **kw):
    return R.render(m, R.load_anims(D, anim + ',' + P), t, view, dict(ctx), **kw)
def sheet(ims, labels, out, cols=None):
    cols = cols or len(ims); rows = (len(ims)+cols-1)//cols
    w, h = ims[0].size
    S = Image.new('RGBA', (w*cols, (h+28)*rows), (30,28,38,255)); d = ImageDraw.Draw(S)
    for i,(im,l) in enumerate(zip(ims,labels)):
        x, y = (i%cols)*w, (i//cols)*(h+28)
        S.paste(im, (x, y+28)); d.text((x+10, y+4), l, fill=(235,230,245,255), font=FONT)
    S.save(out)
v = ['front','34','left','back']
sheet([shot('main:idle',1,x) for x in v], ['спереди','3/4','сбоку','сзади'], '01_views.png')
sheet([shot('main:walk',0.25,'34'), shot('main:run',0.3,'34'), shot('main:sneak',0.3,'34'), shot('main:swim',0.3,'left'), shot('main:elytra_fly',0.3,'left'), shot('main:ride',0.3,'34'), shot('main:sit',0.3,'34')],
      ['ходьба','бег','присед','плавание','элитры','езда','сидит'], '02_states.png', cols=4)
names = ['меч','топор','кирка','лопата','мотыга','лук','арбалет','щит','копьё','посох','фолиант','еда/зелье','прочее']
holds = {1:'arm:hold_mainhand:sword',2:'arm:hold_mainhand:axe',3:'arm:hold_mainhand:pickaxe',4:'arm:hold_mainhand:shovel',5:'arm:hold_mainhand:hoe',6:'arm:hold_mainhand:bow',7:'arm:hold_mainhand:crossbow',8:'arm:hold_mainhand:shield',9:'arm:hold_mainhand:spear'}
ims=[]
for i,n in enumerate(names,1):
    a = 'main:idle' + (','+holds[i] if i in holds else '')
    ims.append(shot(a, 0.6, -40, {'v.form_r': i}, size=(420,560), scale=10))
sheet(ims, names, '03_forms.png', cols=7)
ims=[shot('main:idle',1,'front',{}, size=(300,260), scale=34, center=(0,35.5,0)), shot('main:idle',1,'front',{'v.roaming.pr_eyes':1}, size=(300,260), scale=34, center=(0,35.5,0))]
sheet(ims, ['глаза: обычно','глаза: вспышка (атака/чат)'], '04_eyes.png')
ex = json.loads((D/'animations/extra.animation.json').read_text())['animations']
def xs(name,t,view):
    an=dict(ex[name]); an['_parallel']=False
    return R.render(m, [ *R.load_anims(D,'main:pre_parallel0,main:pre_parallel1,main:pre_parallel2'), *R.load_anims(D,'main:idle'), an, *R.load_anims(D,'main:parallel0,main:parallel1,main:parallel2,main:parallel3,main:parallel4')], t, view, {'ctrl.playing_extra_animation':1})
sheet([xs('pr_notes',1,'34'), xs('pr_offer',1,'34'), xs('pr_curtsy',2,'34'), xs('pr_glitch',1,'front')], ['поза: записи','поза: осколок на ладони','поза: реверанс','поза: глитч'], '05_poses.png')
