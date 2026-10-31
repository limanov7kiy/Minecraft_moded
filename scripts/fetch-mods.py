#!/usr/bin/env python3
"""Скачивает jar-файлы модов из pack/mods/*.pw.toml в указанную папку.
Моды с [option] optional=true, default=false кладутся не в <папка>, а в <папка>/optional/ (Forge вложенные папки не читает):
включить = перенести jar на уровень выше, в mods/.
Свой мод с .pw.toml и url на raw GitHub (mm-autoupdate, side=server): если jar лежит в pack/mods/ и хэш совпал — берётся оттуда,
без сети (до слияния PR ссылка на ветку сборки ещё не работает).
Использование: fetch-mods.py <server|client> <папка>"""
import hashlib, http.client, pathlib, sys, time, tomllib, urllib.request
side, out = sys.argv[1], pathlib.Path(sys.argv[2])
out.mkdir(parents=True, exist_ok=True)
pack = pathlib.Path(__file__).resolve().parent.parent / "pack" / "mods"
want = {"server": {"server", "both"}, "client": {"client", "both"}}[side]
keep = set()
metas = {f: tomllib.loads(f.read_text()) for f in sorted(pack.glob("*.pw.toml"))}
via_meta = {m["filename"] for m in metas.values()}   # jar, у которых сторона задана метафайлом
for f, m in metas.items():
    if m.get("side", "both") not in want:
        continue
    o = m.get("option", {})
    off = bool(o.get("optional")) and not o.get("default", True)
    d = m["download"]; dst = (out / "optional" if off else out) / m["filename"]; keep.add(dst)
    if off: dst.parent.mkdir(exist_ok=True)
    if dst.exists() and hashlib.new(d["hash-format"], dst.read_bytes()).hexdigest() == d["hash"]:
        continue
    local = pack / m["filename"]
    if local.is_file() and hashlib.new(d["hash-format"], local.read_bytes()).hexdigest() == d["hash"]:
        dst.write_bytes(local.read_bytes()); print("copied", dst.name); continue
    url = d.get("url")
    if not url:  # CurseForge: прямая ссылка по file-id
        fid = str(m["update"]["curseforge"]["file-id"])
        url = f"https://mediafilez.forgecdn.net/files/{fid[:4]}/{int(fid[4:])}/{m['filename']}"
    for attempt in range(4):  # большие файлы иногда обрываются — повторяем
        try:
            data = urllib.request.urlopen(urllib.request.Request(url, headers={"User-Agent": "Minecraft_moded"})).read()
            break
        except (http.client.IncompleteRead, OSError) as e:
            print(f"retry {m['filename']}: {e}"); time.sleep(2 ** attempt)
    else:
        sys.exit(f"download failed: {m['filename']}")
    if hashlib.new(d["hash-format"], data).hexdigest() != d["hash"]:
        sys.exit(f"hash mismatch: {m['filename']}")
    dst.write_bytes(data); print("got", dst.name)
for j in pack.glob("*.jar"):  # собственные jar сборки (патчи, библиотеки-замены) — на обе стороны,
    if side == "server" and j.name.endswith("-client.jar"):  # кроме клиентских (*-client.jar)
        continue
    if j.name in via_meta:  # сторона задана в .pw.toml — обработан выше
        continue
    keep.add(out / j.name)
    if not (out / j.name).exists() or (out / j.name).read_bytes() != j.read_bytes():
        (out / j.name).write_bytes(j.read_bytes()); print("copied", j.name)
for j in [*out.glob("*.jar"), *(out / "optional").glob("*.jar")]:  # лишнее (и устаревшее в optional/) удаляем
    if j not in keep:
        j.unlink(); print("removed", j.name)
