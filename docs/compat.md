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
5. Create: Northstar - Structures 1.0.0 ссылается на `northstar:moon_*_blob`, которых нет в
   Northstar Redux 0.6+ (автор сам пишет: «not compatible with Northstar 0.6.0 yet») — генерация мира
   падает с `Unbound values in registry ... placed_feature`. Отключён; вернуть, когда выйдет совместимая версия.
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
9. Некритичное (в логе, игру не ломает): рецепты/теги для модов, которых нет в сборке
   (Ancient Aether, Aether Genesis и т.п.); спам `cataclysm_spellbooks:*laser_beam has no attributes`;
   способности рас Arknights `arks:*/thought` ссылаются на атрибут `the_massestravels:thought_load_limit`
   из мода, которого нет в открытом доступе, — эта способность у рас не работает.
10. После падения загрузки сторонние потоки держат JVM живой, сервер «висит» без вывода.
   `test-server.sh` теперь это распознаёт.
11. `.gitignore` правилом `world*/` прятал `pack/kubejs/data/mm_space/worldgen/` — размещения руд из
   `patches/ore_crossgen.py` не попадали в git, и на чистой копии мир не создавался
   (`Unbound values in registry ... placed_feature: [mm_space:mars/...]`). Правило привязано к корню (`/world*/`),
   файлы перегенерированы и закоммичены.

### Дополнения, волна 2 (поток 3)
Пачка 1 — измерения: Ancient Aether, Aether's Delight, Protect Your Moa, Treasure Reforging, Enhanced Extinguishing,
Aether Emissivity (клиент), Umbral Skies (TF × Aether), TF Dungeons & Villages, TF Lost Blocks, TF Bosses Resurrection,
Twilight Cave Fix (сервер), Create Blue Skies Compat, Blue Skies Tweaks, Farmer's Cutting: Blue Skies,
Blue Skies Gravestones Compat (сервер), Undergarden Delight, Deeper and Darker: Spellbooks, Deeper and Darker → Cataclysm,
Dungeons and Villages: Deeper and Darker, Create: Alex's Caves Compat. Сервер — `Done`.
- CBC: Nuclear 2.1.1 (и все прежние версии) — `NoSuchMethodError CBCBuilderTransformersImpl.safeNbt()` с
  Create Big Cannons 5.11.4, мод не грузится. Отключён (задача в потоке 4).
- Create: Alex's Caves Compat: часть рецептов и тегов для Create: New Age (его нет в сборке) не загружается — некритично.
- При добавлении с CurseForge packwiz перезаписывает уже стоящие зависимости (Aether, Structure Gel) CF-метаданными —
  такие изменения откатывать (`git checkout pack/mods/<мод>.pw.toml`).
- Almost Unified: новые моды не дают предметов в объединяемые теги; изменений не нужно.

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
- Legendary Survival Overhaul и Serene Seasons: проверить интеграцию температуры с сезонами.
- Серверный тест не проверяет клиент (рендер, шейдеры Oculus + Embeddium): нужен запуск клиента у игрока.

## Порт Galacticraft (если решим делать)
Galacticraft Legacy (1.12.2) → 1.20.1 — это фактически переписывание: между версиями сменились
система блоков (flattening), рендер, сеть, генерация мира и capabilities. Объём — десятки тысяч строк.
Перед началом проверить лицензию исходников Galacticraft Legacy: она должна разрешать распространять
изменённую версию друзьям.
