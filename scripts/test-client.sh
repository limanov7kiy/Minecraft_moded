#!/usr/bin/env bash
# Тестовый запуск КЛИЕНТА без лаунчера: Forge через portablemc, моды из .mrpack (как ставит Prism:
# env.client != unsupported + overrides), Xvfb + программный OpenGL (Mesa llvmpipe), без звука.
# Ждёт главного меню («Game took N seconds to start») или падения, делает скриншот.
# Использование: test-client.sh <папка вне репо> [pack.mrpack] [таймаут_сек]
#   .mrpack по умолчанию — свежий dist/*.mrpack (scripts/export-pack.sh).
#   CLIENT_KEEP=1      — не закрывать клиент после меню (дальше: вход на сервер руками, см. ниже).
#   CLIENT_XMX=5G      — куча клиента (вместе с тестовым сервером на 15 ГБ: клиент 5G, сервер 6G + swap).
#   CLIENT_GAME_ARGS="--quickPlaySingleplayer perf1" — сразу создать/открыть одиночный мир (замер, docs/performance.md);
#   CLIENT_JVM_EXTRA="-Dfoo=bar" — доп. аргументы JVM (до основных).
#   CLIENT_ARGS="-s 127.0.0.1" — сразу подключиться к серверу (экран предупреждений Forge это сбрасывает).
# Вход на сервер руками: DISPLAY=:99 xdotool mousemove 170 217 click 1 (Multiplayer), затем
#   «Direct Connection» (800 767), ctrl+a, адрес, «Join Server» (800 578). Скриншот: DISPLAY=:99 import -window root x.png
# Нужны: python3, java (для агента), Xvfb, imagemagick (import); xdotool — для кликов.
# Особенности облака (у игроков их нет): YSM отказывается работать под ВМ и потом роняет JVM
# нативным SIGSEGV при входе в мир — для теста его jar убирают из <папка>/game/mods и с сервера.
set -eu
dir="$(mkdir -p "$1" && cd "$1" && pwd)"; root="$(cd "$(dirname "$0")/.." && pwd)"
pack="${2:-$(ls -t "$root"/dist/*.mrpack | head -1)}"; tmo="${3:-1500}"
forge="$(sed -n 's/^forge = "\(.*\)"/\1/p' "$root/pack/pack.toml")"
mc="$(sed -n 's/^minecraft = "\(.*\)"/\1/p' "$root/pack/pack.toml")"
cd "$dir"
# portablemc в своём venv: системный certifi не знает CA прокси, а без certifi берётся SSL_CERT_FILE
[ -x venv/bin/portablemc ] || { python3 -m venv venv && venv/bin/pip install -q --no-deps portablemc; }
pmc() { venv/bin/portablemc --main-dir "$dir/.mc" --work-dir "$dir/game" "$@"; }
pmc start --dry "forge:$mc-$forge" >/dev/null
python3 -I "$root/scripts/mrpack-install.py" "$pack" "$dir/game" client
[ -f game/options.txt ] || printf 'version:3465\nrenderDistance:4\nsimulationDistance:5\nmaxFps:30\ngraphicsMode:0\nonboardAccessibility:false\nskipMultiplayerWarning:true\njoinedFirstServer:true\ntutorialStep:none\nsoundCategory_master:0.0\n' > game/options.txt
# агент, печатающий стек System.exit (моды, закрывающие игру без краш-репорта)
mkdir -p agent
[ -f agent/exittrace.jar ] || { javac -nowarn --release 17 -d agent "$root/scripts/clienttest/ExitTrace.java" 2>/dev/null; printf 'Premain-Class: ExitTrace\n' > agent/m.txt; (cd agent && jar cfm exittrace.jar m.txt ExitTrace*.class); }
java="$(find "$dir/.mc/jvm" -path '*bin/java' -type f | head -1)"
cat > javawrap.sh <<EOF
#!/bin/sh
"$java" -Djava.security.manager=allow -javaagent:$dir/agent/exittrace.jar -XX:ErrorFile=$dir/hs_err_%p.log \${CLIENT_JVM_EXTRA:-} "\$@" ${CLIENT_GAME_ARGS:-}
echo "\$?" > $dir/java-exit.txt
EOF
chmod +x javawrap.sh
[ -e /tmp/.X11-unix/X99 ] || { setsid Xvfb :99 -screen 0 1600x900x24 +extension GLX -nolisten tcp >xvfb.log 2>&1 & sleep 2; }
rm -rf game/logs/latest.log game/crash-reports java-exit.txt
export DISPLAY=:99 LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=llvmpipe LP_NUM_THREADS=2 ALSOFT_DRIVERS=null
setsid venv/bin/portablemc --main-dir "$dir/.mc" --work-dir "$dir/game" start --jvm "$dir/javawrap.sh" "forge:$mc-$forge" \
  -u Tester --resolution 1600x900 --jvm-args "-Xms2G -Xmx${CLIENT_XMX:-5G} -XX:+UseG1GC" ${CLIENT_ARGS:-} > console.log 2>&1 &
t=0
until [ -f java-exit.txt ] || grep -qs 'Game took' game/logs/latest.log || [ $t -ge "$tmo" ]; do sleep 10; t=$((t+10)); done
sleep 20; import -window root "$dir/menu.png" 2>/dev/null || true
grep -h 'Mixin apply failed' game/logs/latest.log | sed 's/.*Mixin apply failed/Mixin apply failed/' | cut -c1-200 | sort -u
grep -h -A8 'EXIT TRACE' game/logs/latest.log | head -12 || true
# модели/текстуры своих модов-портов (missing model рисуется фиолетово-чёрным кубом)
grep -hE "Unable to load model|Missing textures in model|Unable to resolve texture" -A3 game/logs/latest.log | grep -E "mod_lavacow|necromancersdelight|wizardry_restored|wizardrygolems" | cut -c1-200 | sort -u || true
if [ -f java-exit.txt ]; then
  cr="$(ls -t game/crash-reports/*.txt 2>/dev/null | head -1)"
  [ -n "$cr" ] && grep -m3 -E 'Exception|Error' "$cr" | cut -c1-200
  echo "CLIENT FAILED: JVM exit $(cat java-exit.txt); crash-reports: $(ls -t game/crash-reports 2>/dev/null | head -1); hs_err: $(ls -t hs_err_* 2>/dev/null | head -1)"; exit 1
fi
grep -q 'Game took' game/logs/latest.log || { echo "CLIENT FAILED: no main menu in ${tmo}s"; exit 1; }
echo "CLIENT OK: $(grep -o 'Game took [0-9.]* seconds' game/logs/latest.log); screenshot $dir/menu.png"
[ -n "${CLIENT_KEEP:-}" ] || pkill -f "[j]ava-runtime.*$dir" || true
