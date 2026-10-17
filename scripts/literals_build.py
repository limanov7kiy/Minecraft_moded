#!/usr/bin/env python3
"""Сборка словаря литералов ru-lang из пачек перевода. Использование: literals_build.py <папка пачек harvest> <папка переводов> [словарь.json]
Для каждой строки проверяет: ключ есть в пачке, перевод — непустая строка без иероглифов/каны, те же §-коды, плейсхолдеры (%s/%d/{0}) и
управляющие символы (\\u0001), что в исходнике. Хорошие пары добавляются в mods-src/ru-lang/.../literals_ru.json (по умолчанию), плохие — списком."""
import collections, json, pathlib, re, sys
sys.path.insert(0, str(pathlib.Path(__file__).parent))
from cjk_nonlang_audit_lib import CJK_STRICT  # noqa: E402
src_dir, tr_dir = map(pathlib.Path, sys.argv[1:3])
dst = pathlib.Path(sys.argv[3] if len(sys.argv) > 3 else pathlib.Path(__file__).resolve().parent.parent / "mods-src/ru-lang/src/main/resources/assets/ru_lang/literals_ru.json")
cur = json.loads(dst.read_text("utf-8")) if dst.exists() else {}
TOK = re.compile(r"§.|%(?:\d+\$)?[-#+0]*\d*(?:\.\d+)?[a-zA-Z%]|\{[^{}]*\}|[\x00-\x08\x0b-\x1f]")
ok = bad = 0
for f in sorted(tr_dir.glob("[0-9][0-9][0-9].json")):
    s = json.loads((src_dir / f.name).read_text("utf-8"))["literals"]
    try: t = json.loads(f.read_text("utf-8"))["literals"]
    except Exception as e: print(f"{f.name}: не разобран: {e}"); continue
    for k in s:
        v = t.get(k)
        if not isinstance(v, str) or not v.strip(): bad += 1; print(f"{f.name}: нет перевода {k[:40]!r}"); continue
        if CJK_STRICT.search(v): bad += 1; print(f"{f.name}: остались иероглифы {k[:30]!r} -> {v[:40]!r}"); continue
        if collections.Counter(TOK.findall(k)) != collections.Counter(TOK.findall(v)):
            bad += 1; print(f"{f.name}: коды/плейсхолдеры {k[:30]!r} {TOK.findall(k)} -> {TOK.findall(v)}"); continue
        cur[k] = v; ok += 1
dst.parent.mkdir(parents=True, exist_ok=True)
dst.write_text(json.dumps(cur, ensure_ascii=False, indent=0, sort_keys=True) + "\n", "utf-8")
print(f"принято {ok}, отклонено {bad}, в словаре {len(cur)}")
