#!/usr/bin/env bash
# Следит за кучей запущенного сервера (поток 34, поиск утечек): каждые N с пишет занятость old gen (jstat),
# каждые M с — гистограмму живых объектов (jcmd GC.class_histogram = полная сборка, top 40).
# Использование: memwatch.sh <папка вывода> [N=30] [M=300]; останавливается, когда сервер завершился.
# Heap dump при росте — вручную: jcmd <pid> GC.heap_dump <файл вне репо>.
set -u
out="$1"; n="${2:-30}"; m="${3:-300}"
mkdir -p "$out"
pid=""
until [ -n "$pid" ]; do pid=$(for p in $(pgrep -f unix_args.txt); do [ "$(cat /proc/$p/comm 2>/dev/null)" = java ] && echo $p; done | head -1); sleep 2; done
echo "pid $pid" > "$out/jstat.log"
t0=$(date +%s); next=$((t0 + m))
while kill -0 "$pid" 2>/dev/null; do
  now=$(date +%s)
  # OU — занято в old gen (КБ), OC — её размер; YGC/FGC — число сборок
  echo "$((now - t0)) $(jstat -gc "$pid" 2>/dev/null | tail -1)" >> "$out/jstat.log"
  if [ "$now" -ge "$next" ]; then
    jcmd "$pid" GC.class_histogram 2>/dev/null | head -45 > "$out/histo-$((now - t0)).txt"
    next=$((now + m))
  fi
  sleep "$n"
done
