# Minecraft_moded

Модовая сборка Minecraft для игры с друзьями по сети: **Minecraft 1.20.1 + Forge 47.4.26**.
Магия, ракеты и космос, оружие, мобы, Arknights, измерения, умеренный реализм, оптимизация.

- Список модов с пояснениями: [pack/modlist.txt](pack/modlist.txt)
- Журнал совместимости: [docs/compat.md](docs/compat.md)
- Почему 1.20.1: [docs/version-choice.md](docs/version-choice.md)
- Где поднимать сервер: [docs/hosting.md](docs/hosting.md)

## Установка для игроков
Репозиторий **приватный** (в сборке переделанные чужие моды — см. CLAUDE.md), поэтому ссылки вида
`raw.githubusercontent.com/...` и packwiz-installer у игроков не работают (GitHub отдаёт 404).
Сборка раздаётся файлом: хост собирает `scripts/export-pack.sh` → `dist/*.mrpack` и лично передаёт друзьям;
игрок импортирует его в Prism Launcher / Modrinth App. Пошагово — [docs/friends.md](docs/friends.md).

## Сервер
`dist/*-server.zip` из того же `scripts/export-pack.sh` (моды both+server, конфиги, установщик Forge,
скрипты запуска), либо `scripts/fetch-mods.py server <папка>/mods`. Хостинг — [docs/hosting.md](docs/hosting.md).

## Claude Code
Проект подключает плагин `universal-modder` через `.claude/settings.json`. Вручную:
```
/plugin marketplace add rehan-remade/universal-modder
/plugin install universal-modder@universal-modder
```
