#!/usr/bin/env python3
"""Моды с [option] optional=true, default=false (pack/mods/*.pw.toml) в .mrpack помечаем env.client=unsupported:
формат .mrpack не знает «выключен по умолчанию», а Prism/Modrinth App ставят и optional-моды. Так клиент их не получает
(packwiz-installer при этом спрашивает пользователя, сервер берёт мод как обычно — fetch-mods.py).
Использование: mrpack-optional.py <файл.mrpack> (переписывается на месте)"""
import json, pathlib, shutil, sys, tomllib, zipfile
mrpack = pathlib.Path(sys.argv[1])
mods = pathlib.Path(__file__).resolve().parent.parent / "pack" / "mods"
off = set()
for f in mods.glob("*.pw.toml"):
    m = tomllib.loads(f.read_text())
    o = m.get("option", {})
    if o.get("optional") and not o.get("default", True):
        off.add("mods/" + m["filename"])
tmp = mrpack.with_suffix(".tmp")
n = 0
with zipfile.ZipFile(mrpack) as zi, zipfile.ZipFile(tmp, "w", zipfile.ZIP_DEFLATED) as zo:
    for it in zi.infolist():
        data = zi.read(it.filename)
        if it.filename == "modrinth.index.json":
            d = json.loads(data)
            for f in d["files"]:
                if f["path"] in off:
                    f.setdefault("env", {})["client"] = "unsupported"; n += 1
            data = json.dumps(d, ensure_ascii=False, indent=2).encode()
        zo.writestr(it, data)
shutil.move(tmp, mrpack)
print(f"mrpack-optional: {n} модов по умолчанию выключены на клиенте")
