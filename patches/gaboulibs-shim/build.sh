#!/usr/bin/env bash
# Собирает gaboulibs-shim.jar против jar'ов тестового сервера (scripts/test-server.sh).
# Использование: build.sh <папка тестового сервера>
set -eu
here="$(cd "$(dirname "$0")" && pwd)"; srv="$1"
cp="$(ls "$srv"/libraries/net/minecraft/server/*/server-*-srg.jar):$(ls "$srv"/libraries/net/minecraftforge/fmlloader/*/fmlloader-*.jar):$(ls "$srv"/mods/SereneSeasons-*.jar)"
out="$(mktemp -d)"
javac --release 17 -nowarn -cp "$cp" -d "$out" $(find "$here/src" -name '*.java')
jar --create --file "$here/../../pack/mods/gaboulibs-shim-1.0.0.jar" --manifest "$here/src/META-INF/MANIFEST.MF" -C "$out" .
rm -rf "$out"
echo "built pack/mods/gaboulibs-shim-1.0.0.jar"
