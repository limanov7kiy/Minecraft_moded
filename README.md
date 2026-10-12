# Minecraft_moded

Модовая сборка Minecraft для игры с друзьями по сети: **Minecraft 1.20.1 + Forge 47.4.26**.
Магия, ракеты и космос, оружие, мобы, Arknights, измерения, умеренный реализм, оптимизация.

- Список модов с пояснениями: [pack/modlist.txt](pack/modlist.txt)
- Журнал совместимости: [docs/compat.md](docs/compat.md)
- Почему 1.20.1: [docs/version-choice.md](docs/version-choice.md)
- Где поднимать сервер: [docs/hosting.md](docs/hosting.md)

## Установка для игроков
Основной способ — файл `.mrpack` (импорт в Prism Launcher / Modrinth App), пошагово — [docs/friends.md](docs/friends.md).
Хост собирает его `scripts/export-pack.sh` → `dist/*.mrpack` и передаёт друзьям.

Альтернатива с автообновлением при каждом запуске — packwiz-installer (работает, пока репозиторий публичный):
1. Prism: создать экземпляр Minecraft 1.20.1 + Forge 47.4.26, памяти 8 ГБ.
2. Скачать [packwiz-installer-bootstrap.jar](https://github.com/packwiz/packwiz-installer-bootstrap/releases)
   в папку `.minecraft` экземпляра.
3. Настройки экземпляра → «Пользовательские команды» → «Команда перед запуском»:
   ```
   "$INST_JAVA" -jar packwiz-installer-bootstrap.jar https://raw.githubusercontent.com/limanov7kiy/Minecraft_moded/refs/heads/claude/affectionate-davinci-4gddoh/pack/pack.toml
   ```
   Ветки `main` нет — сборка живёт в `claude/affectionate-davinci-4gddoh`, отсюда `refs/heads/...` в ссылке
   (со ссылкой на `main` будет 404 «Failed to download pack.toml»).
   Около 24 модов CurseForge запрещают стороннее скачивание — при первом запуске установщик попросит скачать
   их вручную по показанным ссылкам. С `.mrpack` этого шага нет.

## Сервер
`dist/*-server.zip` из того же `scripts/export-pack.sh` (моды both+server, конфиги, установщик Forge,
скрипты запуска), либо `scripts/fetch-mods.py server <папка>/mods`. Хостинг — [docs/hosting.md](docs/hosting.md).

## Claude Code
Проект подключает плагин `universal-modder` через `.claude/settings.json`. Вручную:
```
/plugin marketplace add rehan-remade/universal-modder
/plugin install universal-modder@universal-modder
```
