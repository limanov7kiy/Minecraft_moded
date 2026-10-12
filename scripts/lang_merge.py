#!/usr/bin/env python3
"""Сборка переведённых пачек в ресурспак. Использование: lang_merge.py <папка с исходными пачками> <папка с переводами> <папка пака assets-корень>
Каждый перевод NNN.json = {ns: {ключ: русская строка}}; проверяются набор ключей и плейсхолдеры. Хорошие ключи
добавляются в <пак>/assets/<ns>/lang/ru_ru.json, плохие выводятся списком (пачку/ключи перегнать)."""
import json, pathlib, sys
sys.path.insert(0, str(pathlib.Path(__file__).parent))
from lang_validate import ph  # noqa: E402
src, tr, pack = map(pathlib.Path, sys.argv[1:4])
ok = bad = 0
for f in sorted(tr.glob("[0-9][0-9][0-9].json")):
    try:
        t = json.loads(f.read_text("utf-8"))
    except Exception as e:
        print(f"{f.name}: JSON не разбирается: {e}"); continue
    s = json.loads((src / f.name).read_text("utf-8"))
    for ns, keys in s.items():
        tt = t.get(ns, {}); out = pack / "assets" / ns / "lang" / "ru_ru.json"
        cur = json.loads(out.read_text("utf-8")) if out.exists() else {}
        for k, en in keys.items():
            v = tt.get(k)
            if not isinstance(v, str) or not v.strip() or ph(en) != ph(v) or (v == en and len(en) > 3):
                bad += 1; print(f"{f.name} {ns} {k}: " + ("нет" if v is None else f"плейсхолдеры/пусто: {v[:60]!r} (en={en[:60]!r})")); continue
            cur[k] = v; ok += 1
        out.parent.mkdir(parents=True, exist_ok=True)
        out.write_text(json.dumps(dict(sorted(cur.items())), ensure_ascii=False, indent=2) + "\n", "utf-8")
print(f"принято {ok}, отклонено {bad}")
