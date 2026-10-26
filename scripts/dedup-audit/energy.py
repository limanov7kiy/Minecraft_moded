import zipfile, glob, collections, json, os
M = {
 'FE': [b'net/minecraftforge/energy/IEnergyStorage'],
 'Create SU': [b'com/simibubi/create/content/kinetics/base/KineticBlockEntity', b'com/simibubi/create/content/kinetics/base/GeneratingKineticBlockEntity'],
 'Mekanism J': [b'mekanism/api/energy/IStrictEnergyHandler'],
 'AE2 AE': [b'appeng/api/networking/energy/IAEPowerStorage', b'appeng/api/networking/energy/IEnergyService'],
 'Botarium (Ad Astra)': [b'earth/terrarium/botarium/common/energy'],
 'Create Addition (FE↔SU)': [b'com/mrh0/createaddition/energy'],
}
res = collections.defaultdict(set)
for j in sorted(glob.glob(os.environ['MM_SERVER'] + '/mods/*.jar')):
    name = j.split('/')[-1]
    try: z = zipfile.ZipFile(j)
    except Exception: continue
    for n in z.namelist():
        if not n.endswith('.class'): continue
        b = z.read(n)
        for k, pats in M.items():
            if k in res[name]: continue
            if any(p in b for p in pats): res[name].add(k)
out = {n: sorted(v) for n, v in res.items() if v}
json.dump(out, open(os.environ.get('MM_AUDIT', '.') + '/energy.json', 'w'), ensure_ascii=False, indent=1)
for n, v in sorted(out.items()): print(f'{n[:60]:60} {", ".join(v)}')
