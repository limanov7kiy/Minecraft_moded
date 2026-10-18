#!/usr/bin/env python3
"""Пересчёт хэшей pack/index.toml и pack/pack.toml по файлам pack/config/yes_steve_model/custom/** без packwiz
(для случаев, когда менялись только метафайлы моделей YSM; в остальных — `packwiz refresh`)."""
import hashlib, pathlib, re
pack = pathlib.Path(__file__).resolve().parent.parent / "pack"
idx = pack / "index.toml"
text = idx.read_text()
head, *blocks = re.split(r"\n\[\[files\]\]\n", text)
entries = {}
for b in blocks:
    f = re.search(r'file = "(.*)"', b).group(1)
    entries[f] = b.strip("\n")
prefix = "config/yes_steve_model/custom/"
for f in list(entries):
    if f.startswith(prefix):
        del entries[f]
for p in (pack / prefix).rglob("*"):
    if p.is_file():
        rel = p.relative_to(pack).as_posix()
        h = hashlib.sha256(p.read_bytes()).hexdigest()
        e = f'file = "{rel}"\nhash = "{h}"'
        if rel.endswith(".pw.toml"):
            e += "\nmetafile = true"
        entries[rel] = e
for f in list(entries):
    if f.startswith(prefix) is False:  # прочие записи: пересчитать хэш, если файл есть
        p = pack / f
        if p.is_file() and "metafile = true" not in entries[f]:
            entries[f] = re.sub(r'hash = ".*"', f'hash = "{hashlib.sha256(p.read_bytes()).hexdigest()}"', entries[f])
new = head.rstrip("\n") + "\n" + "".join(f"\n[[files]]\n{entries[f]}\n" for f in sorted(entries))
idx.write_text(new)
pt = pack / "pack.toml"
pt.write_text(re.sub(r'(\[index\][^\[]*?hash = ")[0-9a-f]+(")', lambda m: m.group(1) + hashlib.sha256(idx.read_bytes()).hexdigest() + m.group(2), pt.read_text(), flags=re.S))
