# Minecraft_moded

Модовая сборка Minecraft для игры с друзьями по сети: **Minecraft 1.20.1 + Forge 47.4.26**.
Магия, ракеты и космос, оружие, мобы, Arknights, измерения, умеренный реализм, оптимизация.

- Список модов с пояснениями: [pack/modlist.txt](pack/modlist.txt)
- Журнал совместимости: [docs/compat.md](docs/compat.md)
- Почему 1.20.1: [docs/version-choice.md](docs/version-choice.md)
- Где поднимать сервер: [docs/hosting.md](docs/hosting.md)

## Установка для игроков (Prism Launcher)
1. Создать инстанс: Minecraft 1.20.1, загрузчик Forge 47.4.26. Памяти выделить 8 ГБ.
2. Скачать [packwiz-installer-bootstrap.jar](https://github.com/packwiz/packwiz-installer-bootstrap/releases)
   и положить в папку `.minecraft` инстанса.
3. В настройках инстанса → «Пользовательские команды» → «Команда перед запуском»:
   ```
   "$INST_JAVA" -jar packwiz-installer-bootstrap.jar https://raw.githubusercontent.com/limanov7kiy/Minecraft_moded/main/pack/pack.toml
   ```
   При каждом запуске сборка обновится сама. Ссылка заработает, когда сборка будет в ветке `main`
   публичного репозитория.

## Сервер
Тот же `pack.toml` с ключом `-s server` у packwiz-installer, либо `scripts/fetch-mods.py server <папка>/mods`.

## Claude Code
Проект подключает плагин `universal-modder` через `.claude/settings.json`. Вручную:
```
/plugin marketplace add rehan-remade/universal-modder
/plugin install universal-modder@universal-modder
```
