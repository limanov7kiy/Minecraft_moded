#!/usr/bin/env bash
# Порт чужого jar под версии зависимостей сборки (без исходников): качает оригинал (проверка хеша),
# переписывает ссылки по <мод>/rules.txt (scripts/jarport/JarPort.java), добавляет классы-переходники из
# mods-src/jar-ports и ресурсы <мод>/res (data/, assets/). Строка `embed <url> <sha512>` в source.txt — встроить
# классы из другого jar (список — <мод>/embed.txt, переносятся правилом prefix; плюс файлы LICENSE* того jar).
# Использование: build.sh <мод> <тестовый сервер> [папка для jar]
# Тестовый сервер нужен ради библиотек ASM (libraries/org/ow2/asm); по умолчанию jar кладётся в pack/mods.
set -eu
here="$(cd "$(dirname "$0")" && pwd)"; root="$here/../.."
mod="$1"; srv="$2"; out="${3:-$root/pack/mods}"; dir="$here/$mod"
val() { sed -n "s/^$1 //p" "$dir/source.txt"; }
tmp=$(mktemp -d); trap 'rm -rf "$tmp"' EXIT
curl -sSLo "$tmp/orig.jar" "$(val url)"
if [ -n "$(val sha512)" ]; then echo "$(val sha512)  $tmp/orig.jar" | sha512sum -c --quiet
else echo "$(val sha1)  $tmp/orig.jar" | sha1sum -c --quiet; fi
(cd "$root/mods-src/jar-ports" && ./gradlew build -q >/dev/null)
mkdir "$tmp/shims" && (cd "$tmp/shims" && unzip -q "$root/mods-src/jar-ports/build/libs/jar-ports-1.jar" "$(val shims)/*")
cp=$(for a in asm asm-tree asm-commons; do ls "$srv"/libraries/org/ow2/asm/$a/9.*/$a-9.*.jar | sort -V | tail -1; done | paste -sd:)
extra=("$tmp/shims"); [ -d "$dir/res" ] && extra+=("$dir/res")
emb="$(val embed)"
if [ -n "$emb" ]; then
  curl -sSLo "$tmp/embed.jar" "${emb%% *}"
  echo "${emb##* }  $tmp/embed.jar" | sha512sum -c --quiet
  mkdir "$tmp/embed" && (cd "$tmp/embed" && unzip -q "$tmp/embed.jar" $(grep -v '^#' "$dir/embed.txt" | sed 's/$/.class/') 'LICENSE*')
  extra+=("$tmp/embed")
fi
java -cp "$cp" "$root/scripts/jarport/JarPort.java" "$tmp/orig.jar" "$out/$(val out)" "$dir/rules.txt" "${extra[@]}"
echo "built $out/$(val out)"
