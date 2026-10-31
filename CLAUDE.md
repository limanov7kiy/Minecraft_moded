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
- **Основная ветка сборки `claude/affectionate-davinci-4gddoh` меняется ТОЛЬКО через PR и проверку Ревью** (решение владельца
  2026-10-24). Прямой пуш (в т.ч. `git push` в эту ветку, force-push, merge локально и пуш) запрещён всем потокам и чатам — старым и новым.
  Исключений нет, кроме явного разрешения владельца; срочное тоже идёт через PR (Ревью сливает его быстро). Работа — в своей ветке
  от свежей основной, затем PR с базой `claude/affectionate-davinci-4gddoh`. Если в основную ветку попало что-то мимо PR — историю
  не переписывать, сообщить координатору; Ревью проверяет постфактум, исправление — отдельным PR через revert.

## Назначение и лицензии (решение пользователя)
Сборка некоммерческая: только для своей компании друзей и РП-отыгрыша. Это не продукт — не продаётся,
не публикуется, денег и рекламы нет. Поэтому лицензионные ограничения модов (All Rights Reserved,
«форки только на GitHub», запрет распространять изменённые jar и т.п.) **не являются причиной отказываться**
от задачи: можно переносить и править код и ассеты чужих модов, пересобирать и патчить jar.
Отвечает за это пользователь; само ограничение лицензии формально никуда не девается — мы на него
сознательно не обращаем внимания.
**Решение пользователя (2026-10-12): репозиторий публичный** — чтобы друзья ставили сборку по ссылке
(packwiz-installer). Условия:
- сборка по-прежнему некоммерческая: не продаём, не монетизируем, не выкладываем на Modrinth/CurseForge;
- авторство сохраняем: в изменённых файлах и в `docs/` указываем, чей это мод/код;
- моды CurseForge с запретом стороннего скачивания (нет `url` в `.pw.toml`) лежат в репозитории как jar в
  `pack/mods/` и раздаются по raw-ссылке GitHub; если правообладатель попросит — убираем (решение пользователя
  2026-10-12, подтверждено в чате ревью-потока);
- при претензии правообладателя пользователь снова делает репозиторий приватным
  (тогда раздача — только файлом `.mrpack`, см. `docs/friends.md`);
- никаких секретов в репозитории (токены, ключи, `FAL_KEY`, логи с личными данными) — он открыт всем.

Сравнение версий и доступности модов — `docs/version-choice.md`.
Что сделано, что в работе и разделение задач по чатам (потокам) — `docs/roadmap.md`.

Новый файл с инструкцией или подсказками → строка в «Справочнике файлов» README.md.

## Сборка (packwiz)
Связка зафиксирована: **1.20.1 + Forge 47.4.26**. Сборка лежит в `pack/` в формате packwiz.
- `pack/modlist.txt` — источник истины: какие моды и почему. Новый мод сначала вписывается туда.
- `scripts/add-mods.sh` — добавляет в packwiz всё из modlist (packwiz: `go install github.com/packwiz/packwiz@latest`,
  путь передаётся через `PACKWIZ=`).
- `scripts/test-server.sh <папка вне репо>` — ставит Forge-сервер, качает серверные моды, запускает до `Done`.
  После каждого изменения набора модов прогоняй его и записывай находки в `docs/compat.md`.
- `scripts/test-client.sh <папка вне репо>` — клиент из `dist/*.mrpack` под Xvfb + llvmpipe до главного меню
  (`CLIENT OK`); вход на сервер и что смотреть глазами — `docs/client-checklist.md`, находки — compat.md «Проверка клиента».
- **Версия сборки**: `pack/pack.toml` (`version`) и `pack/config/mm_pack_version.txt` — одна и та же строка вида `ГГГГ.ММ.ДД-N`
  (дата + номер за день: `2026.10.19-1`, следующий PR того же дня — `-2`). **Каждый PR, меняющий `pack/` (моды, конфиги, скрипты KubeJS),
  поднимает версию в обоих местах** (потом `packwiz refresh`); PR только с docs/scripts/mods-src версию не трогает. Файл версии — в `config/`,
  потому что `pack.toml` до игрока не доходит (packwiz-installer его не кладёт в экземпляр, в `.mrpack` его нет), а `config/` идёт и клиенту, и серверу.
  Клиентский мод mm-perf показывает версию в главном меню, в заголовке окна и в логе (`[mm_perf] версия сборки`); `scripts/check-pack-version.py`
  (его зовут `export-pack.sh` и `test-server.sh`) падает, если версии разошлись или не вида `ГГГГ.ММ.ДД-N`.
- **Индекс после `packwiz refresh`**: пересобирай через `scripts/refresh.sh` (не голым refresh) — он возвращает `preserve = true` обязательным файлам
  (список `REQUIRED_PRESERVE` в `scripts/check-pack-index.py`, сейчас порт голоса `voicechat-server.properties`) и убирает записи по `.packwizignore`;
  `check-pack-index.py` зовут `export-pack.sh` и `test-server.sh`. Причина — поток 77, `docs/compat.md`.
- `scripts/export-pack.sh` — раздача: `dist/*.mrpack` (клиент) и `dist/*-server.zip` (сервер); инструкция для друзей — `docs/friends.md`.
- Сторону мода (`side` в `pack/mods/*.pw.toml`) packwiz берёт с Modrinth; если она неверна — поправь вручную.
- Свои моды и патчи: `mods-src/` (Gradle, ModDevGradle legacyforge + Parchment, как у Redux) и `patches/`.
  Готовые jar кладутся в `pack/mods/*.jar` и раздаются вместе со сборкой; `fetch-mods.py` их копирует
  (`*-client.jar` — только клиенту, на сервер не кладётся).
  - `mods-src/wizardry-restored` — недостающие заклинания Electroblob's Wizardry поверх Redux
    (план и опись — `docs/wizardry-port.md`; код и ассеты оригинала 1.12.2 переносить можно — см. «Назначение и лицензии»).
    Сборка: `cd mods-src/wizardry-restored && ./gradlew build`, затем `cp build/libs/*.jar ../../pack/mods/`.
  - `mods-src/mm-compat` — кодовые связки и исправления сборки (кислород скафандров Northstar/Creating Space в Ad Astra,
    категория лучей Cataclysm Spellbooks, недостающий атрибут рас Arknights, JEI-категория Imbuement Altar (Redux), блок попадания Superb Warfare по кораблям VS, фриз сервера от Mana and Artifice (блокирующий getChunk в onWorldTick),
    затемнение от страха Lycanites — их миксин в собранной игре не применяется; утечка кучи The Lost Cities `BuildingInfo` и копирование
    огромных шаблонов структур в каждый чанк — OOM при полёте, поток 34;
    падение сервера от подписки Distant Horizons на Chunky до его запуска, поток 64;
    костюм Create Nuclear защищает и от радиации Mekanism и от облучения Alex's Caves, противогаз Spore — и от газа Create Big Cannons
    (логические дубли, поток 66); противогаз Spore засчитывается охраной SCP: Ultimate и от SCP-008 (поток 73);
    блокирующая догрузка чанков на главном потоке у Serene Seasons Plus, Falling Tree и Weather2 — тик до 54 с при входе в новое место;
    Lost Cities `City.isCityCenter` без new Random на каждый вызов — генерация +47–56 %, поток 67).
    Ad Astra в зависимостях — по Modrinth version id Forge-сборки (номер версии у Forge и Fabric одинаковый).
  - `mods-src/mm-autoupdate` — серверный мод автообновления сборки с GitHub (side=server, свой `.pw.toml` с url на raw GitHub,
    jar в `.packwizignore`): источник — ветка `claude/autoupdate-stable` (двигает координатор fast-forward на проверенные сборки), окно `update_window` с 0.5.0 по умолчанию пусто (обновление при каждом перезапуске); на старте сервера (mode=on_start, по умолчанию; после обновления выход с кодом 0) или при остановке (on_stop) запускает packwiz-installer-bootstrap; с 0.5.0 пустой сервер при новой сборке сам перезапускается (idle_restart, в любое время, выход 0); `docs/hosting.md`, «Автообновление сервера с git».
    Версии/sha256 packwiz-installer зашиты в `Updater.java` — их же читает `export-pack.sh`.
  - `mods-src/voicechat-addons` — порт на Forge аддонов Simple Voice Chat от Max Henkel (Voice Chat Interaction,
    AudioPlayer, Enhanced Groups; оригиналы только Fabric). Сборка: `./gradlew build`, в `pack/mods/` — jar без `-reobf`.
  - `patches/gaboulibs-shim` — замена удалённой библиотеки для Serene Seasons Plus.
  - `patches/lycanites-refmap` — недостающий refmap миксинов Lycanites Mobs (без пересборки 120-МБ jar).
  - Тесты на сервере: `scripts/tests/*.js` (через `EXTRA_SERVER_SCRIPTS`), `castcheck.js` кастует все заклинания
    Wizardry Restored фейковым игроком и проверяет результат в мире.
  - `patches/space_unify.py`, `patches/ore_crossgen.py` — одна Солнечная система (см. docs/compat.md).
  - `patches/northstar_structures_fix.py` — биомы Northstar Structures под Northstar Redux 0.6 и кратеры на Луне Ad Astra.
  - `scripts/missing_tag_refs.py <latest.log> <mods>` — убрать из тегов ссылки на отсутствующие предметы (иначе тег
    выбрасывается целиком) → `pack/kubejs/server_scripts/missing_mod_refs.js`.
  - Мод собран под другую версию зависимости: `scripts/linkcheck/linkcheck.sh` (что не связывается) → порт
    `patches/jar-ports/<мод>` (правила для `scripts/jarport/JarPort.java` + переходники `mods-src/jar-ports`),
    сборка `patches/jar-ports/build.sh <мод> <тестовый сервер>` → `pack/mods/*-mmport.jar`. Если старого API в новой
    версии нет целиком — встроить копию старой библиотеки (`embed` + `prefix`, пример `patches/jar-ports/origin-furs`).
  - `patches/zombie-have-ears/build.sh` — Zombie Have Ears с `mods.toml` в формате Forge.
  - `scripts/test-server.sh`: `EXTRA_MODS=<папка>` — лишние jar только на этот запуск.
- Объединение дублей: Almost Unified (`pack/config/almostunified/`), перед изменениями проверять
  `scripts/hardrefs.py`.
- Пользователь хочет: для каждого мода — все дополнения и моды-связки; где совместимость плохая —
  дописывать патчи (отдельным модом-патчем или KubeJS/датапаком).

## Хостинг
Варианты разворачивания сервера и выбор — в `docs/hosting.md`.
