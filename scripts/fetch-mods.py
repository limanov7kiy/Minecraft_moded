#!/usr/bin/env python3
"""Скачивает jar-файлы модов из pack/mods/*.pw.toml в указанную папку.
Использование: fetch-mods.py <server|client> <папка>"""
import hashlib, http.client, pathlib, sys, time, tomllib, urllib.request
side, out = sys.argv[1], pathlib.Path(sys.argv[2])
out.mkdir(parents=True, exist_ok=True)
pack = pathlib.Path(__file__).resolve().parent.parent / "pack" / "mods"
want = {"server": {"server", "both"}, "client": {"client", "both"}}[side]
keep = set()
for f in sorted(pack.glob("*.pw.toml")):
    m = tomllib.loads(f.read_text())
    if m.get("side", "both") not in want:
        continue
    d = m["download"]; dst = out / m["filename"]; keep.add(dst.name)
    if dst.exists() and hashlib.new(d["hash-format"], dst.read_bytes()).hexdigest() == d["hash"]:
        continue
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
for j in out.glob("*.jar"):
    if j.name not in keep:
        j.unlink(); print("removed", j.name)
