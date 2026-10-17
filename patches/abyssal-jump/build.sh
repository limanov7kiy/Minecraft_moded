#!/usr/bin/env bash
# Применяет AbyssalJumpPatch к pack/mods/arknights_weapon-1.5.6-mmport.jar (на месте). Идемпотентно.
# Использование: build.sh <тестовый сервер> — нужен ради библиотек ASM (libraries/org/ow2/asm). Усиление — третьим аргументом (по умолчанию 2).
set -eu
here="$(cd "$(dirname "$0")" && pwd)"; jar="$here/../../pack/mods/arknights_weapon-1.5.6-mmport.jar"
cp=$(for a in asm asm-tree; do ls "$1"/libraries/org/ow2/asm/$a/9.*/$a-9.*.jar | sort -V | tail -1; done | paste -sd:)
tmp=$(mktemp); trap 'rm -f "$tmp"' EXIT
java -cp "$cp" "$here/AbyssalJumpPatch.java" "$jar" "$tmp" "${2:-2}"
mv "$tmp" "$jar"; echo "patched $jar"
