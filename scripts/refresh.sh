#!/usr/bin/env bash
# Обёртка над `packwiz refresh`: после пересборки индекса возвращает `preserve = true` обязательным файлам
# (список — REQUIRED_PRESERVE в scripts/check-pack-index.py), вычищает записи по шаблонам pack/.packwizignore
# и запускает проверки. Использование: PACKWIZ=~/go/bin/packwiz scripts/refresh.sh
set -euo pipefail
root="$(cd "$(dirname "$0")/.." && pwd)"
PACKWIZ="${PACKWIZ:-packwiz}"
(cd "$root/pack" && "$PACKWIZ" refresh)
python3 - "$root" <<'PY'
import importlib.util, re, sys, pathlib
root = pathlib.Path(sys.argv[1])
spec = importlib.util.spec_from_file_location("cpi", root / "scripts" / "check-pack-index.py")
cpi = importlib.util.module_from_spec(spec); spec.loader.exec_module(cpi)
idx = root / "pack" / "index.toml"
pats = cpi.ignore_patterns()
blocks = re.split(r"(?m)^(?=\[\[files\]\])", idx.read_text(encoding="utf-8"))
out = [blocks[0]]
for b in blocks[1:]:
    m = re.search(r'(?m)^file = "(.*)"$', b)
    path = m.group(1) if m else ""
    if any(cpi.ignored(path, p) for p in pats):
        print(f"refresh.sh: убрана игнорируемая запись {path}")
        continue
    if path in cpi.REQUIRED_PRESERVE and not re.search(r"(?m)^preserve = true$", b):
        b = b.rstrip("\n") + "\npreserve = true\n\n"
        print(f"refresh.sh: возвращён preserve = true для {path}")
    out.append(b)
idx.write_text("".join(out), encoding="utf-8")
PY
# хэш index.toml в pack.toml изменился -> второй refresh его пересчитает, не трогая наши правки
(cd "$root/pack" && "$PACKWIZ" refresh >/dev/null)
python3 "$root/scripts/check-pack-index.py"
python3 "$root/scripts/check-pack-version.py"
