#!/usr/bin/env python3
"""Разворачивает packwiz-метафайлы вне mods/ (сейчас — модели YSM в config/yes_steve_model/custom/**/*.pw.toml):
качает файл по [download] url рядом с метафайлом, проверяет хэш и удаляет сам .pw.toml.
Клиенту это делает packwiz-installer / .mrpack; серверу — этот скрипт (test-server.sh, export-pack.sh).
Использование: fetch-config-files.py <папка сервера или стадии> (ищет в её config/)
Кэш: <папка>/../.config-files-cache — повторный запуск качает только новое."""
import hashlib, http.client, pathlib, sys, time, tomllib, urllib.request

base = pathlib.Path(sys.argv[1]).resolve()
cache = base.parent / ".config-files-cache"
cache.mkdir(exist_ok=True)
n = got = 0
for meta in sorted((base / "config").rglob("*.pw.toml")):
    m = tomllib.loads(meta.read_text())
    d = m["download"]
    if m.get("side", "both") == "client":
        meta.unlink(); continue
    c = cache / d["hash"]
    if not c.exists():
        for attempt in range(4):
            try:
                data = urllib.request.urlopen(urllib.request.Request(d["url"], headers={"User-Agent": "Minecraft_moded"})).read()
                break
            except (http.client.IncompleteRead, OSError) as e:
                print(f"retry {m['filename']}: {e}"); time.sleep(2 ** attempt)
        else:
            sys.exit(f"download failed: {m['filename']}")
        if hashlib.new(d["hash-format"], data).hexdigest() != d["hash"]:
            sys.exit(f"hash mismatch: {m['filename']}")
        c.write_bytes(data); got += 1
    (meta.parent / m["filename"]).write_bytes(c.read_bytes())
    meta.unlink(); n += 1
print(f"fetch-config-files: {n} файлов ({got} скачано)")
