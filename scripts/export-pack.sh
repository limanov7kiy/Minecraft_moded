#!/usr/bin/env bash
# Экспорт сборки для раздачи. Использование: export-pack.sh [папка, по умолчанию dist/]
#   <имя>-<версия>.mrpack         — клиент: импорт в Prism Launcher / Modrinth App (моды side=server
#                                    помечены env.client=unsupported, лаунчер их не ставит).
#   <имя>-<версия>-server.zip     — сервер: моды both+server, config/, kubejs/, установщик Forge и
#                                    скрипты запуска start.sh / start.bat.
# Нужен packwiz (PACKWIZ=путь, как в add-mods.sh), python3, curl, zip.
set -eu
root="$(cd "$(dirname "$0")/.." && pwd)"
out="$(mkdir -p "${1:-$root/dist}" && cd "${1:-$root/dist}" && pwd)"
PACKWIZ="${PACKWIZ:-packwiz}"
toml() { sed -n "s/^$1 = \"\(.*\)\"/\1/p" "$root/pack/pack.toml" | head -1; }
name="$(toml name)"; ver="$(toml version)"; mc="$(toml minecraft)"; forge="$(toml forge)"
rev="$(git -C "$root" rev-parse --short HEAD 2>/dev/null || echo local)"
base="$name-$ver+$rev"

# моды кэшируются в <папка>/.server-mods: повторный экспорт качает только изменившиеся
python3 "$root/scripts/fetch-mods.py" server "$out/.server-mods" | grep -v '^got' || true

# --- клиент ---
# Часть модов CurseForge запрещает стороннее скачивание: packwiz просит положить их в свой кэш
# import вручную. Все такие моды у нас both — подкладываем их из уже скачанных серверных.
case "$(uname)" in Darwin) cache="$HOME/Library/Caches" ;; *) cache="${XDG_CACHE_HOME:-$HOME/.cache}" ;; esac
mkdir -p "$cache/packwiz/cache/import"
for f in $(grep -L '^url' "$root"/pack/mods/*.pw.toml); do
  fn="$(sed -n 's/^filename = "\(.*\)"/\1/p' "$f")"
  [ -f "$out/.server-mods/$fn" ] && cp "$out/.server-mods/$fn" "$cache/packwiz/cache/import/"
done
rm -f "$out/$base.mrpack"
(cd "$root/pack" && "$PACKWIZ" refresh >/dev/null && "$PACKWIZ" modrinth export -o "$out/$base.mrpack" | tail -1)
python3 "$root/scripts/mrpack-optional.py" "$out/$base.mrpack"   # необязательные моды (default=false) клиенту в .mrpack не ставим
[ -f "$out/$base.mrpack" ] || { echo "mrpack не собран: см. вывод packwiz modrinth export"; exit 1; }

# --- сервер ---
stage="$out/$base-server"
rm -rf "$stage" && mkdir -p "$stage"
cp -r "$out/.server-mods" "$stage/mods"
for d in config defaultconfigs kubejs; do [ -d "$root/pack/$d" ] && cp -r "$root/pack/$d" "$stage/"; done
python3 "$root/scripts/fetch-config-files.py" "$stage"   # config/**/*.pw.toml (модели YSM) → сами файлы
curl -sSLo "$stage/forge-installer.jar" \
  "https://maven.minecraftforge.net/net/minecraftforge/forge/$mc-$forge/forge-$mc-$forge-installer.jar"
echo "-Xms4G
-Xmx10G" > "$stage/user_jvm_args.txt"
# online-mode=false: игроки с офлайн-аккаунтов (защита ников — мод sols-authlogin, /register и /login).
# Кладём как server.properties.default: start.sh/start.bat копируют его, только если server.properties ещё нет,
# так что обновление пакета поверх сервера не затирает настройки хоста.
# max-tick-time=-1: WorldEdit при старте считает все состояния блоков одним тиком дольше 60 с; без этого сторож убивает сервер.
printf 'motd=%s\nmax-players=10\nview-distance=10\nsimulation-distance=8\nallow-flight=true\nonline-mode=false\nmax-tick-time=-1\n' "$name" > "$stage/server.properties.default"
cat > "$stage/start.sh" <<'EOF'
#!/usr/bin/env sh
# Первый запуск ставит Forge; дальше просто запускает сервер. ОЗУ — в user_jvm_args.txt.
cd "$(dirname "$0")"
[ -f server.properties ] || cp server.properties.default server.properties
[ -f run.sh ] || java -jar forge-installer.jar --installServer || exit 1
[ -f eula.txt ] || { echo "Прочитайте https://aka.ms/MinecraftEULA и создайте eula.txt со строкой eula=true"; exit 1; }
exec sh ./run.sh nogui
EOF
cat > "$stage/start.bat" <<'EOF'
@echo off
rem Первый запуск ставит Forge; дальше просто запускает сервер. ОЗУ — в user_jvm_args.txt.
cd /d "%~dp0"
if not exist server.properties copy server.properties.default server.properties >nul
if not exist run.bat java -jar forge-installer.jar --installServer || exit /b 1
if not exist eula.txt (echo Read https://aka.ms/MinecraftEULA and create eula.txt with eula=true & pause & exit /b 1)
call run.bat nogui
pause
EOF
chmod +x "$stage/start.sh"
rm -f "$out/$base-server.zip"
(cd "$stage" && zip -qr "$out/$base-server.zip" .)
rm -rf "$stage"
echo "client: $out/$base.mrpack"
echo "server: $out/$base-server.zip ($(unzip -l "$out/$base-server.zip" | grep -c 'mods/.*\.jar') модов)"
