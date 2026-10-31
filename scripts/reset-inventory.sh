#!/usr/bin/env bash
# Опись прогресса на сервере перед «чистым сбросом» (docs/reset.md, поток 67). НИЧЕГО НЕ УДАЛЯЕТ — только показывает,
# что есть, сколько весит и к какой группе относится. Использование: reset-inventory.sh <папка сервера> [имя мира, по умолчанию из level-name]
set -u
dir="${1:?папка сервера}"; cd "$dir" || exit 1
world="${2:-$(sed -n 's/^level-name=//p' server.properties 2>/dev/null)}"; world="${world:-world}"
show() { # группа путь пояснение
  if [ -e "$2" ]; then printf '%-10s %8s  %s — %s\n' "$1" "$(du -sh "$2" 2>/dev/null | cut -f1)" "$2" "$3"; else printf '%-10s %8s  %s — %s\n' "$1" "нет" "$2" "$3"; fi
}
echo "Сервер: $(pwd); мир: $world; seed: $(sed -n 's/^level-seed=//p' server.properties 2>/dev/null)"
echo "== СБРОС: удаляется при чистом сбросе (docs/reset.md, шаг 4)"
show СБРОС "$world" "мир целиком: регионы всех измерений, игроки (playerdata — инвентарь, расы Origins/Arknights, магия, навыки, ESO), advancements, stats, data/ (рюкзаки, waystones, Puffish Skills, сезоны, корабли VS, LOD Distant Horizons, звуки AudioPlayer)"
show СБРОС "data/tbs" "кэш карт измерения Torches Become Sunlight (пересоздаётся при запуске)"
show СБРОС "config/chunky/tasks" "незаконченные задачи прегенерации Chunky старого мира"
show СБРОС "solsauthlogin/return_positions.json" "позиции возврата после входа (координаты старого мира, + .bak)"
show СБРОС "solsauthlogin/credentials.json" "регистрации Sol's AuthLogin (хэши паролей): удаляются, все регистрируются заново (+ .bak)"
show СБРОС "ops.json" "операторы (владелец заново выдаёт себе оператора командой op в консоли панели)"; show СБРОС "whitelist.json" "белый список"
show СБРОС "banned-players.json" "баны игроков"; show СБРОС "banned-ips.json" "баны по IP"
echo "== ОСТАВИТЬ: не прогресс (решения В73)"
show ОСТАВИТЬ "config/enhancedgroups/persistent-groups.json" "постоянные группы голосового чата"
show ОСТАВИТЬ "config/enhancedgroups/auto-join-groups.json" "автовход в группы голосового чата"
show ОСТАВИТЬ "audioplayer_uploads" "загруженные игроками звуки AudioPlayer (привязки к пластинкам — в мире, удалятся с ним)"
show ОСТАВИТЬ "usercache.json" "кэш ников"; show ОСТАВИТЬ "server.properties" "настройки сервера (seed меняется отдельно)"
show ОСТАВИТЬ "config" "конфиги сборки"; show ОСТАВИТЬ "mods" "моды"; show ОСТАВИТЬ "kubejs" "скрипты сборки"
show ОСТАВИТЬ "modernfix" "кэш шаблонов структур ModernFix (не зависит от мира)"; show ОСТАВИТЬ "local" "кэш KubeJS"
