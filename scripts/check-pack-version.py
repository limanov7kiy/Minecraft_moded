#!/usr/bin/env python3
"""Проверка версии сборки: pack/pack.toml (version) совпадает с pack/config/mm_pack_version.txt и имеет вид ГГГГ.ММ.ДД-N.
Её читает клиентский мод mm-perf (меню, заголовок окна, лог) и форма отчётов. Правило поднятия — CLAUDE.md, «Сборка (packwiz)».
Использование: check-pack-version.py (код 1 при расхождении). Вызывается из export-pack.sh и test-server.sh."""
import pathlib, re, sys
pack = pathlib.Path(__file__).resolve().parent.parent / "pack"
m = re.search(r'^version = "(.*)"', (pack / "pack.toml").read_text(encoding="utf-8"), re.M)
toml_v = m.group(1) if m else None
f = pack / "config" / "mm_pack_version.txt"
file_v = f.read_text(encoding="utf-8").strip() if f.is_file() else None
if not (toml_v and re.fullmatch(r"\d{4}\.\d{2}\.\d{2}-\d+", toml_v)):
    sys.exit(f"версия в pack/pack.toml {toml_v!r} не вида ГГГГ.ММ.ДД-N (например 2026.10.19-1)")
if toml_v != file_v:
    sys.exit(f"версии расходятся: pack/pack.toml = {toml_v}, pack/config/mm_pack_version.txt = {file_v}; поднимайте обе")
print(f"версия сборки {toml_v}: pack.toml и mm_pack_version.txt совпадают")
