#!/usr/bin/env python3
"""Ставит .mrpack в папку игры так же, как Prism Launcher / Modrinth App: файлы из
modrinth.index.json, у которых env.<сторона> != "unsupported", проверка sha1/sha512,
затем overrides/ и <сторона>-overrides/ поверх. Нужен для проверки клиента без лаунчера
(scripts/test-client.sh). Использование: mrpack-install.py <pack.mrpack> <папка игры> [client|server]
Печатает зависимости пакета (minecraft, forge) и число поставленных файлов.
Кэш загрузок — <папка игры>/../.mrpack-cache: повторная установка качает только изменившееся."""
import hashlib, http.client, json, pathlib, shutil, sys, time, urllib.request, zipfile

pack, game = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2])
side = sys.argv[3] if len(sys.argv) > 3 else "client"
cache = game.resolve().parent / ".mrpack-cache"
cache.mkdir(parents=True, exist_ok=True)
z = zipfile.ZipFile(pack)
idx = json.loads(z.read("modrinth.index.json"))
print("deps:", json.dumps(idx["dependencies"]))

# моды из прошлой установки убираем: набор должен быть ровно как в пакете
for d in ("mods", "resourcepacks", "shaderpacks"):
    if (game / d).is_dir():
        shutil.rmtree(game / d)
n = skipped = 0
for f in idx["files"]:
    if f.get("env", {}).get(side) == "unsupported":
        skipped += 1
        continue
    rel = pathlib.PurePosixPath(f["path"])
    assert not rel.is_absolute() and ".." not in rel.parts, rel
    sha = f["hashes"]["sha1"]
    c = cache / sha
    if not c.exists():
        for attempt in range(4):
            try:
                url = f["downloads"][attempt % len(f["downloads"])]
                data = urllib.request.urlopen(urllib.request.Request(url, headers={"User-Agent": "Minecraft_moded"})).read()
                break
            except (http.client.IncompleteRead, OSError) as e:
                print(f"retry {rel}: {e}"); time.sleep(2 ** attempt)
        else:
            sys.exit(f"download failed: {rel}")
        if hashlib.sha1(data).hexdigest() != sha or hashlib.sha512(data).hexdigest() != f["hashes"]["sha512"]:
            sys.exit(f"hash mismatch: {rel}")
        c.write_bytes(data)
    dst = game / rel
    dst.parent.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(c, dst)
    n += 1
o = 0
for pre in ("overrides/", f"{side}-overrides/"):
    for name in z.namelist():
        if not name.startswith(pre) or name.endswith("/"):
            continue
        rel = pathlib.PurePosixPath(name[len(pre):])
        assert ".." not in rel.parts, rel
        dst = game / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        dst.write_bytes(z.read(name))
        o += 1
print(f"{side}: {n} files from index, {skipped} skipped (env.{side}=unsupported), {o} from overrides")
