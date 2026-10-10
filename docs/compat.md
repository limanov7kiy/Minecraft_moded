# Совместимость: журнал

Связка: **Minecraft 1.20.1 + Forge 47.4.26** (Java 17 или 21).
Проверка — `scripts/test-server.sh <папка>`: настоящий запуск выделенного сервера со всеми серверными модами.

## Решения по запрошенным модам

| Мод | Состояние на 1.20.1 Forge | Что сделали |
|---|---|---|
| Electroblob's Wizardry | Оригинал есть только для 1.12.2. На 1.20.1 есть **Electroblob's Wizardry Redux** — пересоздание с нуля (GPL-3.0), статус alpha, заклинания ещё добавляются | Взяли Redux |
| Lycanites Mobs | Официальная версия для 1.20.1 вышла 2026-08-01 (LGPL-3.0) | Взяли официальную |
| Galacticraft | Galacticraft Legacy — только 1.12.2. Galacticraft 5 — пре-альфа, только Fabric 1.21.1, без релиза. Официального порта на 1.20.1 Forge нет | Пока в сборке Ad Astra + Northstar Redux + Creating Space (ракеты, кислород, планеты). Порт Galacticraft — отдельный большой проект, см. ниже |
| Create: Northstar | Оригинал заброшен в 2024 г., собран под Create 0.5.1 и **роняет сервер** с Create 6 (`NoClassDefFoundError: MovementBehaviour`) | Заменили на **Northstar Redux** (MIT, поддерживается) + его дополнения |

## Найдено тестовыми запусками

1. Iron's Spells требует `playeranimator` и `irons_lib` (>=1.20.1-2, <1.20.1-3), Origins (apoli) — `caelus`,
   Originium Arts — `ldlib`. На Modrinth эти зависимости не объявлены — добавлены вручную.
2. Create: Northstar (оригинал) несовместим с Create 6 — заменён (см. выше).
3. Origin Furs (unofficial port) на выделенном сервере падает: в общей инициализации обращается к
   клиентскому классу `GeoObjectRenderer` из AzureLib. Это чисто визуальный мод (шерсть/уши origins),
   поэтому `origin-furs-unofficial-forge-port` и `arknights-furs` вручную помечены
   `side = "client"` (AzureLib остаётся на обеих сторонах — он нужен Cataclysm Spellbooks на сервере). **После `packwiz update` проверить, что пометка не слетела.**
4. Lycanites Mobs 0.1.0-alpha: в jar нет `lycanitesmobs.refmap.json`, хотя конфиг миксинов на него ссылается —
   миксины к ванильным классам (`StructureAccessor`, `ReloadCommandMixin`, клиентский `LightTextureMixin`) в
   настоящей игре не применялись (`/reload` не перечитывал данные Lycanites, на клиенте ломался эффект освещения).
   **Патч:** `patches/lycanites-refmap` — маленькая библиотека (GAMELIBRARY) с этим refmap (имена → SRG из
   маппингов 1.20.1), сам мод (120 МБ) не трогаем. Проверено: предупреждение `Mixin apply failed` исчезло,
   миксины применяются. Когда автор выпустит исправленный jar — убрать `pack/mods/lycanites-refmap-fix-*.jar`.
5. Create: Northstar - Structures 1.0.0 (MIT) — совместимой версии под Forge 1.20.1 нет (на 2026-10-10 последний
   Forge-релиз 1.0.0). Аддон переопределяет 7 лунных биомов Northstar копиями под старый Northstar:
   фичи `northstar:moon_<камень>_blob` в Redux 0.6 переименованы в `moon_blob_<камень>` — мир не создавался
   (`Unbound values in registry ... placed_feature`). **Патч:** `patches/northstar_structures_fix.py <папка с jar>`
   собирает биомы заново (основа — биом Redux: спавны, эффекты; фичи — аддона, имена переведены; добавлены
   фичи Redux, которых у аддона нет, кроме старых `moon_crater*`) и разрешает кратеры на Луне Ad Astra
   (`ad_astra:lunar_wastelands`, дно/вал — песок и камень Луны Ad Astra) — туда летают ракеты сборки.
   → `pack/kubejs/data/northstar/worldgen/biome`, `pack/kubejs/data/northstar_structures`. Мод возвращён.
   Проверено (`scripts/tests/northstar_structures_commands.txt`): мир создаётся, `locate structure` находит
   малый и старый большой кратер на `ad_astra:moon` и кратер на `northstar:moon`, ошибок генерации нет.
   После обновления аддона или Northstar перезапустить скрипт (или удалить патч, если автор исправил).
6. Canary × Valkyrien Skies: миксин Canary `ai.poi.PoiManagerMixin` перезаписывает
   `PoiManager.getInRange`, после чего миксин VS `feature.poi.MixinPOIManager` не может встроиться —
   сервер падает при создании мира. Фикс: `pack/config/canary.properties` → `mixin.ai.poi=false`
   (отключена одна оптимизация, остальные работают).
7. Iron's Spells 1.20.1-3.16.3 поставляет таблицы добычи `chests/catacombs/crypt_loot` и
   `chests/citadel/citadel_tomes` в формате 1.21 (`"value"` вместо `"name"`, функция
   `set_written_book_pages`) — на 1.20.1 они не загружаются, сундуки пустые. **Патч:**
   `patches/irons_loot_1_20_1.py` переписывает их в формат 1.20.1 → `pack/kubejs/data/...`
   (KubeJS перекрывает данные модов). Проверено командой `/loot spawn`: книги и добыча выпадают.
   После обновления Iron's Spells перезапустить скрипт или удалить патч, если автор исправил.
8. Serene Seasons Plus требует библиотеку GabouLibs, которую автор удалил отовсюду (ни Modrinth, ни
   CurseForge). **Патч:** своя замена `patches/gaboulibs-shim` — 7 классов с тем же API (снежные бури,
   защита от двойной инициализации, совместимость с Project Atmosphere), восстановлены по декомпиляции
   SS+. Собирается `patches/gaboulibs-shim/build.sh <тестовый сервер>` → `pack/mods/gaboulibs-shim-1.0.0.jar`
   (FMLModType: GAMELIBRARY). Включено накопление снега (`pack/config/sereneseasonsplus-common.toml`).
   Проверено: зима + дождь → буря записана в `ssp_snow_history.dat`.
   Замечание: первая смена сезона командой `/season set` после запуска сервера не шлёт событие
   (особенность Serene Seasons) — при естественной смене сезонов не мешает.
9. Шум в логе сервера — разобран (поток 4), проверено `scripts/tests/noisecheck.js`:
   - **Теги со ссылками на отсутствующие предметы выбрасывались целиком** (это не шум, а потеря данных):
     `farmersdelight:snacks/sweets`, `diet:sugars` (мороженое и торты Twilight Delight, которые мод не
     регистрирует), `aether_redux:swet_jelly` (Aether Genesis), `forge:nuggets/reinforced_copper`,
     `aether_villages:collections/ancient_aether_biomes`. `pack/kubejs/server_scripts/missing_mod_refs.js`
     убирает только недостающие ссылки; список генерирует `scripts/missing_tag_refs.py <latest.log>` —
     после обновления модов прогнать сервер и запустить его снова. Теперь в тегах 20/18/25/3 предмета.
   - Рецепты для отсутствующих модов (результат пустой): `ancient_aether:ancient_rune`,
     `aether_redux:genesis_dark_gummy_swet`, три шаблона `aether_treasure_reforging`, плюс опечатка Cataclysm
     `azure_seastone_brick_stair_from_stonecutting` (дубль правильного `..._stairs_...`) — отключены
     условием `forge:false` в `pack/kubejs/data/*/recipes`.
   - `cataclysm_spellbooks:extended_(death_)laser_beam has no attributes` (368 строк): мод регистрирует лучи
     с категорией `MONSTER` — они ещё и занимали лимит враждебных мобов. `mods-src/mm-compat` 0.2.0 ставит
     им `MISC` → 0 строк.
   - Силы `arks:*/thought` рас Arknights (атрибут `the_massestravels:thought_load_limit` из закрытого мода):
     `mm-compat` регистрирует пустой атрибут, только если The Masses Travels нет; сами силы заменены скрытой
     заглушкой (`pack/kubejs/data/arks/powers/*/thought.json`, `loading_priority` 10) — у рас не висит
     «Thought Load +7», который ни на что не влияет.
   - Остаётся (безвредно): KubeJS не разбирает `deeperdarker:resonarium_*_smithing` (шаблон намеренно пустой)
     и `northstar:electrolysis/*` — рецепты загружает ванилла, они работают; попытки модов загрузить
     клиентские классы на сервере (`RuntimeDistCleaner`); внутренний миксин Valkyrien Skies
     `MixinVibrationSystemTicker`; Lycanites пишет «getEntityType() registry lookup failed» для 9 существ
     (alpha-версия мода).
10. После падения загрузки сторонние потоки держат JVM живой, сервер «висит» без вывода.
   `test-server.sh` теперь это распознаёт.
11. Legendary Survival Overhaul × Serene Seasons (+ Serene Seasons Plus): связка встроена в сам LSO
   (`[temperature.integration.serene-seasons]`, включена по умолчанию), патч не нужен. Проверено
   `scripts/tests/seasontemp.js` + `season_commands.txt`: у точки появления (тёмный лес, полдень) вклад
   сезона в температуру мира — середина лета +8, осени +1.5, зимы −9.5, весны −1.5 (итог 22 / 15.5 / 4.5 / 12.5).
   Serene Seasons Plus температуру не трогает (только снег/бури и длина дня).
12. `.gitignore` с правилом `world*/` скрывал все папки `worldgen/`: размещения руд из `patches/ore_crossgen.py`
   (`pack/kubejs/data/mm_space/worldgen`) не попали в git, и на чистой копии мир не создавался
   (`Unbound values ... mm_space:*`). Правило сделано корневым (`/world*/`), данные добавлены.
13. **Битый мод-файл выключает все моды.** Zombie Have Ears 1.1.2 (дополнение к голосовому чату) объявляет
   зависимость без поля `mandatory` (формат NeoForge) — Forge 47 пишет «not a valid mod file», «Mods loaded
   with 1 warnings» и запускает сервер **без единого мода** (а `Done` в логе есть). Мод убран;
   `test-server.sh` теперь считает предупреждения загрузки модов провалом.

## Голосовой чат (Simple Voice Chat 2.6.24)
Состав (раздел «Удобство и мультиплеер» в `pack/modlist.txt`):
| Мод | Сторона | Что даёт |
|---|---|---|
| Simple Voice Chat | both | голос по близости, группы; сервер слушает **UDP 24454** |
| Simple Radio (+ Lexiconfig) | both | рации, радио, микрофоны/динамики; связки с Create, Valkyrien Skies, Curios, Vibrative Voice включились сами |
| Sound Physics Remastered | client | реверберация и заглушение звуков и голосов стенами |
| Talking Heads, Let Them Talk, VoiceChatProfiles | client | анимация говорящего, приглушение музыки при речи, профили настроек |
| Vibrative Voice | server | голос — вибрация для скалк-сенсоров/Хранителя |
| VC Admin, SimpleVoiceChat Broadcast | server | модерация групп и вещание на всех для OP |

Нет под Forge 1.20.1 (только Fabric/Quilt, проверено на Modrinth и CurseForge): Voice Chat Interaction,
Audio Player, Enhanced Groups. Не взяты: связки с модами, которых нет в сборке (CPM, Figura, Plasmo Voice).
Серверные моды проверены на отсутствие своих сетевых каналов — клиенту без них подключаться можно.
Sound Physics на Modrinth помечен `client_only_server_optional` — packwiz ставит `both`, исправлено на `client`.
Тест: SVC загрузил 3 плагина (vibrativevoice, simplevoicechat_broadcast, simpleradio), `Voice chat server
started at port 24454`. Конфиг сервера — `pack/config/voicechat/voicechat-server.properties` (по умолчанию;
`voice_host` — для туннеля playit.gg, см. `docs/hosting.md`).

## Экспорт сборки
`scripts/export-pack.sh` → `dist/`: `.mrpack` (packwiz modrinth export; моды `side=server` в индексе с
`env.client=unsupported`, Twilight Forest с CurseForge и свои jar — в `overrides/`) и `*-server.zip`
(моды both+server, config, kubejs, установщик Forge, `start.sh`/`start.bat`). Серверный пакет проверен:
распаковка → `start.sh` ставит Forge, просит EULA → `Done (114 s)`, голосовой сервер поднят.
Клиентский `.mrpack` в лаунчере пока не проверен (поток 5). `pack/modlist.txt` исключён из раздачи
(`pack/.packwizignore`).

## Объединение дублей (Almost Unified)
Один ресурс — один предмет: `pack/config/almostunified/unify.json`. Приоритет модов:
minecraft → kubejs → create → mekanism → createaddition → ad_astra → northstar. Например, вся сталь
(Ad Astra, Create Big Cannons, Mekanism, Superb Warfare) выдаётся как `mekanism:ingot_steel`, свинец —
Mekanism, листы — Create. Не объединяются (`ignoredItems`): уран Alex's Caves (своя механика радиации),
изумруд Caerula Arbor и никелевая руда Creating Space (у них ошибочные теги).
Генерация серебра и свинца Superb Warfare в Верхнем мире отключена (дубль Occultism/Mekanism):
`pack/kubejs/data/superbwarfare/forge/biome_modifier/`. Проверка рецептов, завязанных на конкретный
предмет: `scripts/hardrefs.py`.

## Одна Солнечная система (Ad Astra + Northstar + Creating Space)
Планеты и орбиты — **только Ad Astra**; ракеты — **только Northstar и Creating Space** (строятся из блоков,
требуют расчёта тяги/топлива/Δv, планеты Northstar открываются телескопом). Простые крафтовые ракеты Ad Astra
убраны: `pack/kubejs/server_scripts/space_rockets.js` (рецепты ракет T1–T4, верстак NASA и его автоматическая
версия из Giselle Addon, конус, стабилизаторы, стартовая площадка) и `client_scripts/space_rockets.js` (скрыты в
JEI). Двигатели и баки Ad Astra оставлены — из них делаются реактивный костюм и ровер.

Генераторы данных (берут исходные файлы из jar):
- `patches/space_unify.py` → маршруты: `planet_dimension` Northstar и `rocket_accessible_dimension`
  Creating Space ведут в `ad_astra:*`; старые узлы Creating Space пустые. Добавлены: орбита Луны для Northstar;
  Меркурий и Глацио (межзвёздный перелёт, самый дорогой по Δv) для Creating Space; исправлена гравитация Венеры
  у Creating Space (было 1.6 → 8.87).
  **Гравитация** — реальная, от Northstar (по ней он считает тягу ракет); у Ad Astra на тех же планетах и
  орбитах — нейтральная, иначе миксины двух модов перемножили бы её. Глацио — гравитация Ad Astra.
  **Кислород** — система Ad Astra. Скафандры Northstar и Creating Space в ней работают через мод
  `mods-src/mm-compat`: Ad Astra считает, что существо дышит, пока в баллоне скафандра есть воздух, и воздух
  тратится 1 ед./с (как в самих модах). Пустой баллон — удушье.
  **Температура** — Northstar сохраняет свою (на ней работают его машины); скафандры связаны тегами.
- `patches/ore_crossgen.py` → ресурсы Northstar и Creating Space на планетах Ad Astra (см. ниже в журнале).

Проверено на сервере (`scripts/tests/`): `gravitycheck.js` (Луна 1.6, Марс 3.71, Венера 8.87, Меркурий 3.7,
орбиты 0 — Northstar; у Ad Astra множитель 1; Глацио 0.38 — Ad Astra), `routecheck.js`, `rocketcheck.js`
(рецептов ракет Ad Astra нет, детали ракет Northstar/CS есть), `oxygen_commands.txt` (моб в полном скафандре
Northstar/CS выживает на Луне, воздух тратится ~1/с; при 5 ед. воздуха — через 5 с удушье и гибель),
`spacecheck.js` (руды на планетах).

## Открытые вопросы
- Серверный тест не проверяет клиент (рендер, шейдеры Oculus + Embeddium): нужен запуск клиента у игрока.

## Порт Galacticraft (если решим делать)
Galacticraft Legacy (1.12.2) → 1.20.1 — это фактически переписывание: между версиями сменились
система блоков (flattening), рендер, сеть, генерация мира и capabilities. Объём — десятки тысяч строк.
Лицензия не препятствует: сборка частная и некоммерческая (CLAUDE.md «Назначение и лицензии») —
код и ассеты Galacticraft Legacy можно переносить с указанием авторства, не публикуя результат.
