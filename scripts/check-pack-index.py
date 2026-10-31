#!/usr/bin/env python3
"""Проверка pack/index.toml после `packwiz refresh` (причина: поток 77, Millida).
1) у файлов из REQUIRED_PRESERVE должен стоять `preserve = true` — иначе packwiz-installer при автообновлении сервера
   затрёт локальный файл значением сборки (так был сбит порт голосового чата хостинга 13021 -> 24454);
2) в индексе не должно быть файлов, подпадающих под шаблоны pack/.packwizignore (например mods/mm-autoupdate-*.jar).
Лечится: scripts/refresh.sh (делает refresh и чинит оба случая). Вызывается из export-pack.sh и test-server.sh.
Использование: check-pack-index.py (код 1 при проблемах)."""
import fnmatch, pathlib, re, sys

# Единственное место списка «обязательно preserve» — дополняйте здесь (пути как в index.toml).
REQUIRED_PRESERVE = [
    "config/voicechat/voicechat-server.properties",  # порт голоса выдаёт хостинг (Millida: 13021), сборка держит 24454
]

pack = pathlib.Path(__file__).resolve().parent.parent / "pack"


def read_index():
    """[{'file':..., 'preserve':bool}] из [[files]] индекса (без зависимости от tomllib: Python может быть <3.11)."""
    out, cur = [], None
    for line in (pack / "index.toml").read_text(encoding="utf-8").splitlines():
        if line.strip() == "[[files]]":
            cur = {"file": None, "preserve": False}
            out.append(cur)
        elif cur is not None:
            m = re.match(r'\s*file\s*=\s*"(.*)"\s*$', line)
            if m:
                cur["file"] = m.group(1)
            elif re.match(r"\s*preserve\s*=\s*true\s*$", line):
                cur["preserve"] = True
    return out


def ignore_patterns():
    p = pack / ".packwizignore"
    if not p.is_file():
        return []
    return [l.strip() for l in p.read_text(encoding="utf-8").splitlines() if l.strip() and not l.lstrip().startswith("#")]


def ignored(path, pat):
    pat = pat.rstrip("/")
    if fnmatch.fnmatch(path, pat) or path.startswith(pat + "/"):
        return True
    return "/" not in pat and fnmatch.fnmatch(path.rsplit("/", 1)[-1], pat)


def problems():
    files = read_index()
    by_path = {f["file"]: f for f in files}
    bad = []
    for path in REQUIRED_PRESERVE:
        f = by_path.get(path)
        if f is None:
            bad.append(f"{path}: нет в pack/index.toml (должен быть с preserve = true)")
        elif not f["preserve"]:
            bad.append(f"{path}: нет `preserve = true` — автообновление затрёт файл значением сборки")
    pats = ignore_patterns()
    for f in files:
        for pat in pats:
            if ignored(f["file"], pat):
                bad.append(f"{f['file']}: есть в индексе, но подпадает под pack/.packwizignore ({pat})")
                break
    return bad


if __name__ == "__main__":
    bad = problems()
    if bad:
        sys.exit("pack/index.toml не прошёл проверку:\n  " + "\n  ".join(bad) +
                 "\nИсправление: scripts/refresh.sh (вместо голого `packwiz refresh`).")
    print(f"индекс сборки ок: preserve у {len(REQUIRED_PRESERVE)} обязательных файлов, игнорируемых записей нет")
