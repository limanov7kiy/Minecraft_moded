#!/usr/bin/env python3
"""Единая терминология в нашем пак-источнике (pack/resourcepacks-src/russian-moded). Правила (решение владельца 2026-10-12):
  Originium → «ориджиниум» (все падежи и производные, любой регистр): оригиниум/оригинум/ориджинум → ориджиниум.
Использование: lang_term_fix.py [корень пака] [--dry]. Печатает число исправленных строк. Запускать после lang_merge.py (идемпотентно)."""
import json, pathlib, re, sys
RULES = [(re.compile(r"ори(?:г|дж)и?ни?ум", re.I), "ориджиниум")]


def fix(s):
    def sub(rx, rep):
        def f(m):
            t = m.group(0)
            return rep.upper() if len(t) > 1 and t.isupper() else (rep[0].upper() + rep[1:] if t[0].isupper() else rep)
        return rx.sub(f, s)
    for rx, rep in RULES:
        s = sub(rx, rep)
    return s


if __name__ == "__main__":
    args = [a for a in sys.argv[1:] if a != "--dry"]
    root = pathlib.Path(args[0] if args else pathlib.Path(__file__).resolve().parent.parent / "pack/resourcepacks-src/russian-moded")
    n = files = 0
    for f in sorted(root.glob("assets/*/lang/ru_ru.json")):
        d = json.loads(f.read_text("utf-8")); ch = 0
        for k, v in d.items():
            nv = fix(v)
            if nv != v:
                d[k] = nv; ch += 1
        if ch:
            n += ch; files += 1
            if "--dry" not in sys.argv:
                f.write_text(json.dumps(d, ensure_ascii=False, indent=2) + "\n", "utf-8")
    print(f"исправлено строк: {n} в {files} файлах")
