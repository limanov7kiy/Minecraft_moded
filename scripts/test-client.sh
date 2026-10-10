#!/usr/bin/env bash
# Тестовый клиент сборки под Xvfb (облако без GPU: программный рендер Mesa llvmpipe, ~1 FPS с шейдерами).
# Ставит Minecraft 1.20.1 + Forge через portablemc (pip install portablemc), качает клиентские моды,
# включает шейдерпак Complementary в Oculus и запускает игру с подключением к серверу.
# Использование: test-client.sh <папка вне репо> [сервер, по умолчанию localhost]
# Сервер — scripts/test-server.sh (или он же, оставленный запущенным). Памяти нужно ~9 ГБ клиенту + ~4 ГБ серверу:
# в облачном контейнере (лимит 13,4 ГБ) сервер запускать с -Xmx3G, иначе клиент убивает OOM.
# Экран предупреждения YSM («cannot run under a virtual machine») и выбор Origin проходятся вручную
# (xdotool: окно 854x480 в центре экрана 1280x720), скриншоты — `DISPLAY=:99 import -window root shot.png`.
set -eu
dir="$1"; server="${2:-localhost}"
root="$(cd "$(dirname "$0")/.." && pwd)"
forge="$(sed -n 's/^forge = "\(.*\)"/\1/p' "$root/pack/pack.toml")"
mkdir -p "$dir/game/config" "$dir/game/shaderpacks" "$dir/pyfix" && cd "$dir"
# portablemc берёт сертификаты из certifi — за прокси с собственным CA подсовываем системный набор
if [ -n "${SSL_CERT_FILE:-}" ]; then
  printf 'import certifi\ncertifi.where = lambda: "%s"\n' "$SSL_CERT_FILE" > pyfix/sitecustomize.py
fi
export PYTHONPATH="$dir/pyfix"
python3 "$root/scripts/fetch-mods.py" client game/mods | grep -v '^got' || true
cp -r "$root/pack/config/." game/config/
sp=game/shaderpacks/ComplementaryReimagined.zip
[ -f "$sp" ] || curl -sSLo "$sp" "$(curl -sS 'https://api.modrinth.com/v2/project/complementary-reimagined/version?loaders=%5B%22iris%22%5D&game_versions=%5B%221.20.1%22%5D' \
  | python3 -c 'import json,sys;print(json.load(sys.stdin)[0]["files"][0]["url"])')"
printf 'enableShaders=true\nshaderPack=ComplementaryReimagined.zip\n' > game/config/oculus.properties
pgrep -x Xvfb >/dev/null || { setsid Xvfb :99 -screen 0 1280x720x24 >/dev/null 2>&1 < /dev/null & sleep 2; }
export DISPLAY=:99 LIBGL_ALWAYS_SOFTWARE=1
exec portablemc --main-dir "$dir/main" --work-dir "$dir/game" start "forge:1.20.1-$forge" -u Tester -s "$server" \
  --jvm-args="-Xmx4500M -XX:MaxDirectMemorySize=1G -XX:+UseG1GC ${JVMX:-}"
