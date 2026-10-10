#!/usr/bin/env bash
# Какие классы/методы/поля других модов jar использует, а их нет в сборке (мод собран под другую версию).
# Использование: linkcheck.sh <mod.jar> <папка тестового сервера> [префикс пакета ...]
# LINKCHECK_JAVA_OPTS=-Dlinkcheck.members=true — и члены отсутствующих классов.
# Пример: linkcheck.sh traveloptics.jar /srv/test com/github/L_Ender/cataclysm io/redspace
set -eu
jar="$1"; srv="$2"; shift 2
asm="$(ls "$srv"/libraries/org/ow2/asm/asm/9.*/asm-9.*.jar | sort -V | tail -1)"
exec java ${LINKCHECK_JAVA_OPTS:-} -cp "$asm" "$(dirname "$0")/LinkCheck.java" "$jar" "$srv/mods" "$srv/libraries" "$@"
