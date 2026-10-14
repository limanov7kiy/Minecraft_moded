#!/usr/bin/env bash
# Наш русский ресурспак «Русификация сборки» (pack/resourcepacks-src/russian-moded) раздаётся как мод ru-lang (клиентский):
# он подключает пак всегда включённым и самым верхним (AddPackFindersEvent, required + fixed TOP), независимо от options.txt игрока.
# Запускать после scripts/lang_merge.py / lang_keep_en_*.py и при слиянии веток, где менялись переводы:
# собирает mods-src/ru-lang, кладёт pack/mods/ru-lang-<версия>-client.jar и обновляет индекс packwiz (PACKWIZ=путь).
set -eu
root="$(cd "$(dirname "$0")/.." && pwd)"
(cd "$root/mods-src/ru-lang" && ./gradlew build --no-daemon -q)
rm -f "$root"/pack/mods/ru-lang-*-client.jar
cp "$root"/mods-src/ru-lang/build/libs/ru-lang-*.jar "$root/pack/mods/$(cd "$root/mods-src/ru-lang/build/libs" && ls ru-lang-*.jar | sed 's/\.jar$/-client.jar/')"
(cd "$root/pack" && "${PACKWIZ:-packwiz}" refresh >/dev/null)
echo "собран $(ls "$root"/pack/mods/ru-lang-*-client.jar)"
