#!/usr/bin/env python3
"""Статическая проверка моделей мода из jar: blockstates -> models -> textures (missing model = фиолетово-чёрный куб).
Использование: check_mod_models.py <mod.jar> <namespace>. Код возврата 1, если что-то не находится.
Не ловит проблемы рантайма (ModernFix dynamic_resources, RegisterAdditional) — только отсутствие файлов."""
import json, re, sys, zipfile

jar, ns = sys.argv[1], sys.argv[2]
z = zipfile.ZipFile(jar)
names = set(z.namelist())
bad = []

def check_model(ref, src, seen):
    mns, _, path = ref.rpartition(':')
    mns = mns or 'minecraft'
    if mns != ns or ref in seen:
        return
    seen.add(ref)
    f = f'assets/{ns}/models/{path}.json'
    if f not in names:
        bad.append((src, 'model', ref)); return
    md = json.loads(z.read(f))
    if 'parent' in md:
        check_model(md['parent'], f, seen)
    for t in md.get('textures', {}).values():
        if t.startswith('#'): continue
        tns, _, tp = t.rpartition(':')
        if (tns or 'minecraft') == ns and f'assets/{ns}/textures/{tp}.png' not in names:
            bad.append((f, 'texture', t))

n = 0
for name in sorted(names):
    if re.fullmatch(rf'assets/{ns}/blockstates/.*\.json', name):
        n += 1
        d = json.loads(z.read(name))
        refs = []
        for v in d.get('variants', {}).values():
            refs += [m['model'] for m in (v if isinstance(v, list) else [v])]
        for part in d.get('multipart', []):
            ap = part['apply']
            refs += [m['model'] for m in (ap if isinstance(ap, list) else [ap])]
        for r in refs:
            check_model(r, name, set())
    elif re.fullmatch(rf'assets/{ns}/models/item/.*\.json', name):
        check_model(f'{ns}:item/' + name.rsplit('/', 1)[1][:-5], name, set())
print(f'{jar}: blockstates {n}, проблем {len(bad)}')
for b in bad: print('  ', *b)
sys.exit(1 if bad else 0)
