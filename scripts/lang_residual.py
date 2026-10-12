#!/usr/bin/env python3
"""Недостающие русские строки после родных ru_ru модов и ресурспаков-русификаторов -> пачки для перевода.
Использование: lang_residual.py <папка jar> <выход> [--packs a.zip b.zip ...] [--size 250] [--skip-done <папка нашего пака assets>]
Пишет <выход>/NNN.json ({"_ns": ..., ключ: английская строка}) — пачки по модам (мелкие ns склеиваются), и index.json.
Ключи, уже переведённые в нашем паке (--skip-done), пропускаются."""
import argparse, collections, json, pathlib, sys, zipfile
sys.path.insert(0, str(pathlib.Path(__file__).parent))
from lang_audit import has_letters  # noqa: E402
from lang_packs_cover import read_langs  # noqa: E402

ap = argparse.ArgumentParser()
ap.add_argument("jars"); ap.add_argument("out")
ap.add_argument("--packs", nargs="*", default=[]); ap.add_argument("--size", type=int, default=250)
ap.add_argument("--skip-done"); a = ap.parse_args()
en, ru = collections.defaultdict(dict), collections.defaultdict(dict)
for j in sorted(pathlib.Path(a.jars).glob("*.jar")):
    try:
        z = zipfile.ZipFile(j)
    except zipfile.BadZipFile:
        continue
    for (ns, c), d in read_langs(z).items():
        if c == "en_us": en[ns].update(d)
        elif c == "ru_ru": ru[ns].update(d)
for p in a.packs:
    for (ns, c), d in read_langs(zipfile.ZipFile(p)).items():
        if c == "ru_ru": ru[ns].update({k: v for k, v in d.items() if v != en[ns].get(k)})
done = {}
if a.skip_done:
    for f in pathlib.Path(a.skip_done).glob("*/lang/ru_ru.json"):
        done[f.parts[-3]] = json.loads(f.read_text("utf-8"))
need = {}
for ns, d in en.items():
    r = {k: v for k, v in d.items() if has_letters(v) and k not in done.get(ns, {}) and (k not in ru[ns] or (ru[ns][k] == v and len(v) > 3))}
    if r: need[ns] = r
out = pathlib.Path(a.out); out.mkdir(parents=True, exist_ok=True)
batches, cur, cur_n = [], {}, 0
def flush():
    global cur, cur_n
    if cur: batches.append(cur); cur, cur_n = {}, 0
for ns, r in sorted(need.items(), key=lambda x: -len(x[1])):
    items = list(r.items())
    for i in range(0, len(items), a.size):
        chunk = items[i:i + a.size]
        if len(chunk) >= a.size * 0.6:
            batches.append({ns: dict(chunk)}); continue
        if cur_n + len(chunk) > a.size: flush()
        cur[ns] = dict(chunk); cur_n += len(chunk)
flush()
idx = []
for i, b in enumerate(batches, 1):
    (out / f"{i:03d}.json").write_text(json.dumps(b, ensure_ascii=False, indent=0))
    idx.append({"n": i, "ns": {k: len(v) for k, v in b.items()}, "keys": sum(map(len, b.values()))})
(out / "index.json").write_text(json.dumps(idx, ensure_ascii=False, indent=0))
print(f"ns {len(need)}, ключей {sum(map(len, need.values()))}, пачек {len(batches)}")
