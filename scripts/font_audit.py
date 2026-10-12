#!/usr/bin/env python3
"""Кастомные шрифты модов и ресурспаков: есть ли в них кириллица.
Использование: font_audit.py <папка с jar> [...zip]. bitmap — проверяет символы chars на U+0400–04FF;
ttf/unihex/legacy_unicode — отмечает «проверить глазами» (глифы внутри файла не разбираем)."""
import json, pathlib, re, sys, zipfile
FONT = re.compile(r"^assets/([^/]+)/font/([^/]+)\.json$")
for arg in sys.argv[1:]:
    p = pathlib.Path(arg)
    for j in sorted(p.glob("*.jar")) if p.is_dir() else [p]:
        try:
            z = zipfile.ZipFile(j)
        except zipfile.BadZipFile:
            continue
        for n in z.namelist():
            m = FONT.match(n)
            if not m:
                continue
            try:
                prov = json.loads(z.read(n).decode("utf-8-sig"), strict=False).get("providers", [])
            except Exception:
                print(f"{j.name}\t{m.group(1)}:{m.group(2)}\tне разобран json"); continue
            kinds = sorted({x.get("type") for x in prov})
            chars = "".join("".join(x.get("chars", [])) for x in prov if x.get("type") == "bitmap")
            cyr = any("Ѐ" <= c <= "ӿ" for c in chars)
            verdict = "кириллица ЕСТЬ" if cyr else ("проверить (ttf/unihex)" if set(kinds) - {"bitmap", "space", "reference"} else "нет кириллицы (bitmap)")
            print(f"{j.name}\t{m.group(1)}:{m.group(2)}\t{','.join(k or '?' for k in kinds)}\t{verdict}")
