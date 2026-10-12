#!/usr/bin/env python3
"""Проверка перевода. Использование:
  lang_validate.py lang <ru_ru.json|папка пака> <папка jar с en_us>   — ключи, плейсхолдеры, §-коды, пустые/нерусские значения
  lang_validate.py book <папка en_us книги> <папка ru_ru книги>        — те же файлы и ключи, теги $(...) и ссылки совпадают
Код возврата 1, если есть ошибки (предупреждения не считаются)."""
import collections, json, pathlib, re, sys, zipfile
sys.path.insert(0, str(pathlib.Path(__file__).parent))
from lang_audit import LANG_RE, load_json, has_letters  # noqa: E402

PH = re.compile(r"%(?:\d+\$)?[-#+0,(]*\d*(?:\.\d+)?[a-zA-Z%]|\{[^{}]*\}|\\n|\$\([^)]*\)")
CYR = re.compile(r"[А-Яа-яЁё]")


def ph(s):  # %1$s == %s по составу (порядок слов в русском может меняться); §-коды — отдельно, как предупреждение
    return collections.Counter(re.sub(r"%\d+\$", "%", x) for x in PH.findall(s))


def check(en, ru, label, errs, warns):
    for k, v in ru.items():
        if k not in en:
            warns.append(f"{label}: лишний ключ {k}"); continue
        if not v.strip() and en[k].strip():
            errs.append(f"{label}: пустое значение {k}"); continue
        if ph(en[k]) != ph(v):
            errs.append(f"{label}: плейсхолдеры {k}: en={dict(ph(en[k]))} ru={dict(ph(v))}")
        if collections.Counter(re.findall("§.", en[k])) != collections.Counter(re.findall("§.", v)):
            warns.append(f"{label}: §-коды отличаются {k}")
        if v == en[k] and has_letters(v) and len(v) > 3:
            warns.append(f"{label}: не переведено {k}")
        elif has_letters(v) and not CYR.search(v) and len(re.sub(PH, "", v)) > 12 and re.search(r"[A-Za-z]{4}", v):
            warns.append(f"{label}: нет кириллицы {k}: {v[:50]}")


def cmd_lang(ru_path, jars):
    en = collections.defaultdict(dict)
    for j in pathlib.Path(jars).glob("*.jar"):
        try:
            z = zipfile.ZipFile(j)
        except zipfile.BadZipFile:
            continue
        for n in z.namelist():
            m = LANG_RE.match(n)
            if m and m.group(2).lower() == "en_us" and m.group(3) == "json":
                d = load_json(z.read(n))
                if d:
                    en[m.group(1)].update(d)
    p = pathlib.Path(ru_path)
    files = {}
    if p.is_dir():
        for f in p.glob("assets/*/lang/ru_ru.json"):
            files[f.parts[-3]] = f
    else:
        files[p.parts[-3] if len(p.parts) > 2 and p.parts[-2] == "lang" else p.stem] = p
    errs, warns, total = [], [], 0
    for ns, f in files.items():
        raw = f.read_bytes()
        try:
            json.loads(raw.decode("utf-8-sig"), strict=False)
        except Exception as e:
            errs.append(f"{f}: JSON не разбирается: {e}"); continue
        ru = load_json(raw) or {}
        total += len(ru)
        if ns not in en:
            warns.append(f"{ns}: нет en_us в jar"); continue
        check(en[ns], ru, ns, errs, warns)
    print(f"файлов {len(files)}, ключей {total}, ошибок {len(errs)}, предупреждений {len(warns)}")
    for e in errs[:200]: print("ERR ", e)
    for w in warns[:100]: print("warn", w)
    return 1 if errs else 0


def tags(s):
    return collections.Counter(re.findall(r"\$\([^)]*\)|\$\{[^}]*\}", s))


def walk(o, path=""):
    if isinstance(o, str):
        yield path, o
    elif isinstance(o, dict):
        for k, v in o.items():
            yield from walk(v, f"{path}/{k}")
    elif isinstance(o, list):
        for i, v in enumerate(o):
            yield from walk(v, f"{path}[{i}]")


def cmd_book(en_dir, ru_dir):
    en_dir, ru_dir = pathlib.Path(en_dir), pathlib.Path(ru_dir)
    errs = []
    for f in sorted(en_dir.rglob("*.json")):
        r = ru_dir / f.relative_to(en_dir)
        if not r.exists():
            errs.append(f"нет файла {r}"); continue
        try:
            a = dict(walk(json.loads(f.read_text("utf-8-sig"), strict=False)))
            b = dict(walk(json.loads(r.read_text("utf-8-sig"), strict=False)))
        except Exception as e:
            errs.append(f"{r}: JSON: {e}"); continue
        for k, v in a.items():
            if k not in b:
                errs.append(f"{r}: нет {k}")
            elif tags(v) != tags(b[k]) and not k.endswith(("/name", "/title")):
                errs.append(f"{r}{k}: теги en={dict(tags(v))} ru={dict(tags(b[k]))}")
            elif k.endswith(("/type", "/anchor", "/item", "/recipe", "/entity", "/icon", "/category", "/parent", "/advancement")) and v != b[k]:
                errs.append(f"{r}{k}: служебное поле изменено: {v!r} → {b[k]!r}")
    print(f"ошибок {len(errs)}")
    for e in errs[:200]: print("ERR ", e)
    return 1 if errs else 0


if __name__ == "__main__":
    if len(sys.argv) == 4 and sys.argv[1] == "lang": sys.exit(cmd_lang(sys.argv[2], sys.argv[3]))
    if len(sys.argv) == 4 and sys.argv[1] == "book": sys.exit(cmd_book(sys.argv[2], sys.argv[3]))
    sys.exit(__doc__)
