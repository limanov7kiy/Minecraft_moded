#!/usr/bin/env python3
"""Решение владельца (2026-10-12): названия МОДОВ не переводим — остаются английскими везде, где видны (вкладки творческого режима,
экраны настроек, заголовки книг и т.п.). Скрипт берёт displayName из mods.toml всех jar и находит lang-ключи, значение которых —
название мода (точное совпадение) либо вкладка творческого режима/настройки, содержащие название мода; в наш пак пишется английское
значение (наш пак выше MPLOC/RTF+ и родного ru). Названия предметов/блоков/сущностей не трогаются (ключи item./block./entity. и т.п.).
Использование: lang_keep_en_modnames.py <папка jar> [--packs a.zip ...] [--pack <корень нашего пака>] [--dry]
Выводит число переопределённых ключей (там, где итоговое русское значение отличалось от английского)."""
import argparse, collections, io, json, pathlib, re, sys, tomllib, zipfile
sys.path.insert(0, str(pathlib.Path(__file__).parent))
from lang_packs_cover import read_langs  # noqa: E402
from lang_modnames import collect_names, classify, norm  # noqa: E402

ap = argparse.ArgumentParser()
ap.add_argument("jars"); ap.add_argument("--packs", nargs="*", default=[]); ap.add_argument("--dry", action="store_true")
ap.add_argument("--pack", default=str(pathlib.Path(__file__).resolve().parent.parent / "pack/resourcepacks-src/russian-moded"))
a = ap.parse_args()
pack = pathlib.Path(a.pack)
names = {}


en, ru, where = {}, {}, {}
for j in sorted(pathlib.Path(a.jars).glob("*.jar")):
    try:
        z = zipfile.ZipFile(j)
    except zipfile.BadZipFile:
        continue
    collect_names(z, names)
    for (ns, c), d in read_langs(z).items():
        if c == "en_us":
            for k, v in d.items():
                en[k] = v; where[k] = ns
        elif c == "ru_ru":
            ru.update(d)
for p in a.packs:
    for (ns, c), d in read_langs(zipfile.ZipFile(p)).items():
        if c == "ru_ru":
            ru.update(d)
for f in pack.glob("assets/*/lang/ru_ru.json"):
    ru.update(json.loads(f.read_text("utf-8")))  # уже переведённое нами
keys = {}
for k, v in en.items():
    why = classify(k, v, names)
    if why == "tab" and any(n in norm(ru.get(k, "")).casefold() for n in names):
        continue  # вкладка «Название мода: Предметы» уже содержит английское название — оставляем перевод хвоста
    if why:
        keys[k] = why
changed = collections.Counter(); by_ns = collections.defaultdict(dict)
for k, why in keys.items():
    if ru.get(k, v := en[k]) != en[k]:
        changed[why] += 1
    by_ns[where[k]][k] = en[k]
print(f"mods.toml: {len(names)} названий; ключей-названий найдено {len(keys)}; были переведены (переопределены): {sum(changed.values())} {dict(changed)}")
if not a.dry:
    for ns, d in by_ns.items():
        f = pack / "assets" / ns / "lang" / "ru_ru.json"
        cur = json.loads(f.read_text("utf-8")) if f.exists() else {}
        cur.update(d)
        f.parent.mkdir(parents=True, exist_ok=True)
        f.write_text(json.dumps(dict(sorted(cur.items())), ensure_ascii=False, indent=2) + "\n", "utf-8")
sample = [(k, en[k], ru.get(k)) for k in keys if ru.get(k, en[k]) != en[k]][:25]
for s in sample: print("  ", s)
