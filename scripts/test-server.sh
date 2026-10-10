#!/usr/bin/env bash
# Тестовый запуск сервера со сборкой: ставит Forge, качает серверные моды, запускает до "Done" и
# останавливает. Лог: <папка>/logs/latest.log. Использование: test-server.sh <папка> [таймаут_сек]
# TEST_COMMANDS=<файл> — команды консоли, которые выполнить после запуска (строка «#sleep N» — пауза).
# EXTRA_SERVER_SCRIPTS=<папка> — тестовые KubeJS-скрипты (например scripts/tests), только для этого запуска.
set -eu
dir="$1"; tmo="${2:-900}"
root="$(cd "$(dirname "$0")/.." && pwd)"
forge="$(sed -n 's/^forge = "\(.*\)"/\1/p' "$root/pack/pack.toml")"
mkdir -p "$dir" && cd "$dir"
if [ ! -f run.sh ]; then
  curl -sSLo forge-installer.jar "https://maven.minecraftforge.net/net/minecraftforge/forge/1.20.1-$forge/forge-1.20.1-$forge-installer.jar"
  java -jar forge-installer.jar --installServer >/dev/null
fi
python3 "$root/scripts/fetch-mods.py" server mods | grep -v '^got' || true
# конфиги сборки поверх сгенерированных
for d in config kubejs; do [ -d "$root/pack/$d" ] && mkdir -p "$d" && cp -r "$root/pack/$d/." "$d/"; done
[ -n "${EXTRA_SERVER_SCRIPTS:-}" ] && mkdir -p kubejs/server_scripts && cp "$EXTRA_SERVER_SCRIPTS"/*.js kubejs/server_scripts/
echo eula=true > eula.txt
[ -f server.properties ] || printf 'online-mode=false\nlevel-seed=12345\n' > server.properties
echo "-Xmx8G" > user_jvm_args.txt
rm -rf crash-reports
rm -f logs/latest.log
# "stop" отправляем, как только сервер написал Done; выходим сразу, если сервер упал
mkfifo -m 600 .stdin 2>/dev/null || true
timeout "$tmo" ./run.sh nogui < .stdin > console.log 2>&1 & pid=$!
exec 3>.stdin
last=0; still=0
while kill -0 $pid 2>/dev/null; do
  # лог не растёт 3 минуты — сервер мёртв или завис
  size=$(stat -c %s logs/latest.log 2>/dev/null || echo 0)
  if [ "$size" = "$last" ]; then still=$((still+5)); else still=0; last=$size; fi
  if [ $still -ge 180 ]; then echo "log stalled"; pkill -9 -f unix_args.txt || true; break; fi
  if grep -qs 'Done (' logs/latest.log; then
    # проверочные команды консоли (по одной в строке), затем stop
    [ -n "${TEST_COMMANDS:-}" ] && { while read -r c; do case "$c" in "#sleep "*) sleep "${c#\#sleep }" ;; ''|\#*) ;; *) echo "$c" >&3; sleep 2 ;; esac; done < "$TEST_COMMANDS"; sleep 5; }
    echo stop >&3; break
  fi
  # после падения сторонние потоки могут держать JVM живой — добиваем
  if grep -qs 'Failed to start the minecraft server\|Crash report saved\|crash report has been saved' logs/latest.log; then sleep 5; pkill -9 -f unix_args.txt || true; break; fi
  sleep 5
done
wait $pid || true; exec 3>&-
if grep -q 'Done (' logs/latest.log; then echo "SERVER OK: $(grep -o 'Done ([^)]*)' logs/latest.log)"; else echo "SERVER FAILED"; exit 1; fi
