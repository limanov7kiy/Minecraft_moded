# Minecraft_moded

Сборка модов Minecraft (Java Edition) для совместной сетевой игры с друзьями.
Общаемся с пользователем на русском.

## Инструменты
- Плагин `universal-modder` подключён на уровне проекта (`.claude/settings.json`):
  маркетплейс `rehan-remade/universal-modder`, плагин `universal-modder@universal-modder`.
  Для любых задач по модам начинай со скилла `mod-any-game`; справка по Minecraft —
  `skills/mod-any-game/references/engines/minecraft.md` внутри плагина.
- Генерация ассетов через fal требует переменную окружения `FAL_KEY` (необязательно).

## Правила сборки
- Версии жёстко связаны: версия Minecraft + загрузчик (Fabric/NeoForge/Forge) + версия API
  должны совпадать у сервера и всех клиентов. Любой мод добавляй только под зафиксированную тройку.
- Для каждого мода помечай сторону: `both` (клиент и сервер), `client` (только клиент —
  шейдеры, миникарты, UI) или `server` (только сервер). Клиентские моды на сервер не кладём.
- Никаких читов/клиентских хаков для чужих публичных серверов.

Сравнение версий и доступности модов — `docs/version-choice.md`.

## Сборка (packwiz)
Связка зафиксирована: **1.20.1 + Forge 47.4.26**. Сборка лежит в `pack/` в формате packwiz.
- `pack/modlist.txt` — источник истины: какие моды и почему. Новый мод сначала вписывается туда.
- `scripts/add-mods.sh` — добавляет в packwiz всё из modlist (packwiz: `go install github.com/packwiz/packwiz@latest`,
  путь передаётся через `PACKWIZ=`).
- `scripts/test-server.sh <папка вне репо>` — ставит Forge-сервер, качает серверные моды, запускает до `Done`.
  После каждого изменения набора модов прогоняй его и записывай находки в `docs/compat.md`.
- Сторону мода (`side` в `pack/mods/*.pw.toml`) packwiz берёт с Modrinth; если она неверна — поправь вручную.
- Свои моды и патчи: `mods-src/` (Gradle, ModDevGradle legacyforge + Parchment, как у Redux) и `patches/`.
  Готовые jar кладутся в `pack/mods/*.jar` и раздаются вместе со сборкой; `fetch-mods.py` их копирует.
  - `mods-src/wizardry-restored` — недостающие заклинания Electroblob's Wizardry поверх Redux
    (план и опись — `docs/wizardry-port.md`; код оригинала не копировать — лицензия Electroblob).
    Сборка: `cd mods-src/wizardry-restored && ./gradlew build`, затем `cp build/libs/*.jar ../../pack/mods/`.
  - `mods-src/mm-compat` — кодовые связки сборки (кислород скафандров Northstar/Creating Space в Ad Astra).
    Ad Astra в зависимостях — по Modrinth version id Forge-сборки (номер версии у Forge и Fabric одинаковый).
  - `patches/gaboulibs-shim` — замена удалённой библиотеки для Serene Seasons Plus.
  - `patches/space_unify.py`, `patches/ore_crossgen.py` — одна Солнечная система (см. docs/compat.md).
- Объединение дублей: Almost Unified (`pack/config/almostunified/`), перед изменениями проверять
  `scripts/hardrefs.py`.
- Пользователь хочет: для каждого мода — все дополнения и моды-связки; где совместимость плохая —
  дописывать патчи (отдельным модом-патчем или KubeJS/датапаком).

## Хостинг
Варианты разворачивания сервера и выбор — в `docs/hosting.md`.
