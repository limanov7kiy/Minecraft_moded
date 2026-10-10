#!/usr/bin/env bash
# Zombie Have Ears 1.1.2 (автор SKALKMine, All Rights Reserved; правим только для своей сборки — см. CLAUDE.md,
# «Назначение и лицензии»). В mods.toml зависимости записаны в формате NeoForge (type = "required"), без поля
# mandatory — Forge 47 считает файл битым и запускает сервер вообще без модов. Перепаковываем jar с
# исправленным mods.toml, код мода не трогаем. Использование: build.sh [папка для jar] (по умолчанию pack/mods)
set -eu
here="$(cd "$(dirname "$0")" && pwd)"; out="${1:-$here/../../pack/mods}"
url=https://cdn.modrinth.com/data/GEjGqQ62/versions/2mId8IAl/zombiehaveears-1.1.2.jar
sha=$(curl -sS "https://api.modrinth.com/v2/version/2mId8IAl" | python3 -c "import json,sys;print(json.load(sys.stdin)['files'][0]['hashes']['sha512'])")
tmp=$(mktemp -d); trap 'rm -rf "$tmp"' EXIT
curl -sSLo "$tmp/orig.jar" "$url"
echo "$sha  $tmp/orig.jar" | sha512sum -c --quiet
mkdir "$tmp/x" && (cd "$tmp/x" && unzip -q ../orig.jar)
sed -i -e 's/^type = "required"/mandatory = true/' -e 's/^type = "optional"/mandatory = false/' "$tmp/x/META-INF/mods.toml"
sed -i 's/^description = .*/&\n[Пересобрано для Minecraft_moded: mods.toml в формате Forge 47, код без изменений.]/' "$tmp/x/META-INF/mods.toml"
grep -q '^type = ' "$tmp/x/META-INF/mods.toml" && { echo "mods.toml: остались поля type"; exit 1; }
# без pack.mcmeta сервер пишет «Missing metadata in pack mod:zombiehaveears»
[ -f "$tmp/x/pack.mcmeta" ] || echo '{"pack":{"description":"zombiehaveears resources","pack_format":15}}' > "$tmp/x/pack.mcmeta"
(cd "$tmp/x" && rm -f "$out/zombiehaveears-1.1.2-forgefix.jar" && zip -qrX "$out/zombiehaveears-1.1.2-forgefix.jar" META-INF/MANIFEST.MF . -x '.*')
echo "built $out/zombiehaveears-1.1.2-forgefix.jar"
