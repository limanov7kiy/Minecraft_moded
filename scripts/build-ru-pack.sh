#!/usr/bin/env bash
# Собирает наш ресурспак «Русификация сборки» (pack/resourcepacks-src/russian-moded) в pack/resourcepacks/russian-moded.zip
# и обновляет индекс packwiz. Запускать после scripts/lang_merge.py. Нужны zip и packwiz (PACKWIZ=путь).
set -eu
root="$(cd "$(dirname "$0")/.." && pwd)"
rm -f "$root/pack/resourcepacks/russian-moded.zip"
(cd "$root/pack/resourcepacks-src/russian-moded" && zip -qr -X "$root/pack/resourcepacks/russian-moded.zip" pack.mcmeta assets)
(cd "$root/pack" && "${PACKWIZ:-packwiz}" refresh >/dev/null)
echo "собран pack/resourcepacks/russian-moded.zip ($(du -h "$root/pack/resourcepacks/russian-moded.zip" | cut -f1))"
