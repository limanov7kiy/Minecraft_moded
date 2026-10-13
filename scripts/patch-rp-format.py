#!/usr/bin/env python3
"""Ресурспак с чужим pack_format (MPLOC — 88, JustSomeTranslations — 34) 1.20.1 выкидывает из options.txt как «несовместимый».
Скачивает пак по url из pack/resourcepacks-src/upstream/<имя>.pw.toml, проверяет хеш и пишет копию с pack_format 15
(остальное содержимое не меняется; лицензии MIT / CC-BY-SA разрешают с указанием авторов — см. docs/russification.md)
в pack/resourcepacks/<filename>. Использование: patch-rp-format.py <pw.toml> [...]"""
import hashlib, io, json, pathlib, sys, tomllib, urllib.request, zipfile
root = pathlib.Path(__file__).resolve().parent.parent
for arg in sys.argv[1:]:
    m = tomllib.loads(pathlib.Path(arg).read_text()); d = m["download"]
    data = urllib.request.urlopen(urllib.request.Request(d["url"], headers={"User-Agent": "Minecraft_moded"})).read()
    assert hashlib.new(d["hash-format"], data).hexdigest() == d["hash"], "хеш не совпал"
    zin = zipfile.ZipFile(io.BytesIO(data)); out = root / "pack" / "resourcepacks" / m["filename"]
    with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as zout:
        for i in zin.infolist():
            # рекламные картинки MPLOC (assets/minecraft/textures/item/*.png, до 2000x2000) попадают в атлас блоков и предметов:
            # на видеокартах с лимитом текстуры 16384 (и в облачном test-client на llvmpipe) атлас не помещается — убираем их
            if m["filename"].startswith("mploc") and i.filename.startswith("assets/minecraft/textures/"):
                continue
            b = zin.read(i.filename)
            if i.filename == "pack.mcmeta":
                j = json.loads(b.decode("utf-8-sig")); j["pack"]["pack_format"] = 15
                j.pop("supported_formats", None); b = json.dumps(j, ensure_ascii=False, indent=1).encode()
            zout.writestr(i, b)
    print(out, out.stat().st_size)
