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

## Проверка клиента (поток 5, 2026-10-10)
Клиент 1.20.1 Forge 47.4.26 запущен в облаке под Xvfb (Mesa llvmpipe, OpenGL 4.6, ~1 FPS) и подключён к
серверу из `scripts/test-server.sh`. Повторить: `scripts/test-client.sh <папка>` (подробности в шапке скрипта).
**С текущей сборкой клиент не запускается вообще** — три блокера подряд, все только на клиенте
(серверный тест их не видит). Дальше проверка шла с локальными обходами (в репо не вносились).

Блокеры (задачи — в `docs/roadmap.md`, потоки 3 и 4):
1. **`.gitignore`: правило `world*/` отрезает все папки `worldgen/`.** Размещения руд, которые генерирует
   `patches/ore_crossgen.py` (`pack/kubejs/data/mm_space/worldgen/placed_feature/...`), не попали в git —
   сервер из чистой ветки падает: `Unbound values in registry ... placed_feature: [mm_space:mars/northstar_...]`.
   Для теста сгенерированы локально (`ore_crossgen.py` + `space_unify.py` по папке модов сервера).
2. **ImmediatelyFast 1.2.7+1.20.2 × Oculus 1.8.0** — молча закрывает игру (`System.exit` в `IrisCompat.init`,
   ищет класс старого Iris `net.coderbot.iris.vertices.ImmediateState`; краш-репорта нет). packwiz выбрал
   старую версию; ImmediatelyFast **1.5.5+1.20.4-forge** (заявлена и для 1.20.1) с Oculus 1.8.0 работает.
3. **Origin Furs (`origin_visuals` 1.0.5) × AzureLib 3.1.20** — краш при загрузке ресурсов:
   `ClassMetadataNotFoundException: mod.azure.azurelib.model.GeoModel`. В AzureLib 3.1.0+ старый API
   `Geo*` удалён; последняя версия с ним — 3.0.27. А Cataclysm Spellbooks 1.2.6+ требует новый API `Az*`
   (3.1). Spellbooks 1.2.5 работает с AzureLib 3.0.27, но не с L_Ender's Cataclysm 3.31. Общей версии нет.
   Arknights furs — это ресурс-пак для Origin Furs, без него бесполезен.
4. **Cataclysm Spellbooks 1.2.9 × L_Ender's Cataclysm 3.31** — краш в `ClientSetup.registerEntityRenderers`:
   `NoClassDefFoundError: com/github/L_Ender/cataclysm/client/render/entity/Ancient_Remnant_Rework_Renderer`.
   Класс есть в Cataclysm 3.16, убран в 3.24+. Откат Cataclysm до 3.16 запускает игру, но модели Cataclysm 3.16
   падают с LionfishAPI 3.0 из сборки (`AbstractMethodError ... Koboleton_Model ... root()` при рендере
   кобольда) — откатывать пришлось бы и LionfishAPI, и проверять остальные дополнения Cataclysm.

Что проверено и работает (с обходами: ImmediatelyFast 1.5.5, без Origin Furs/Arknights furs, Cataclysm 3.16):
- **Oculus 1.8.0 + Embeddium 0.3.31 + шейдеры** Complementary Reimagined r5.9.3 — шейдер грузится
  (`Using shaderpack`, тени в F3), картинка есть. Предупреждения шейдера `Unknown variable: BIOME_PALE_GARDEN`
  и т.п. — переменные новых версий Iris, не мешают. Embeddium пишет «tainted by valkyrienskies, oculus» — норма.
- **Вход на сервер**, экран выбора Origin и класса (Origins + Arknights races), **миникарта и карта мира Xaero**,
  подсказки Jade/Valkyrien Skies.
- **Рендер AzureLib** (Cataclysm Spellbooks): дрон наблюдения, ледяной блок, маска Abyssal Warlock — без ошибок.
- **Distant Horizons**: загружается, миксин в Oculus применяется (`MixinOculusFrameBuffer`). Предупреждения:
  G1 GC (советует ZGC на Java 21 — у 1.20.1 Forge Java 17, можно Shenandoah), Alex's Caves «частично
  несовместим, может потребовать настройки». Сами LOD при 1 FPS визуально не проверить.

Новые находки:
5. **Lycanites `LightTextureMixin` по-прежнему не применяется на клиенте**: `@Shadow field lightTexture was not
   located` — в `patches/lycanites-refmap` нет имени поля (`LightTexture.lightTexture` → SRG `f_109870_`,
   сверить по маппингам). Серверные миксины патч починил, клиентский — нет.
6. **Конфиг Serene Seasons Plus в старом формате**: при входе мод пишет «This update renamed the configuration
   settings», переносит `sereneseasonsplus-common.toml` в `.pre-descriptive-config-update.bak` и создаёт новый —
   значит, настройка накопления снега из сборки **не действует** (так же и на сервере).
7. **Yes Steve Model** под виртуальной машиной не работает («YSM client cannot run under a virtual machine») —
   в облаке не проверить; у игроков на обычном ПК должно быть нормально (Windows/Linux x64, не macOS).
8. **Память**: клиент со сборкой занимает ~9 ГБ (куча 4,5–5 ГБ + ~4 ГБ вне кучи: Mesa, DH, шейдеры).
   Игрокам — `-Xmx6G` и не меньше 12–16 ГБ ОЗУ в системе. Старт клиента — 2–2,5 мин (на 4 ядрах).
9. Simple Voice Chat в сборке пока нет (поток 2) — не проверялся. Wizardry Restored в тесте была 0.3.0
   (0.6.0 вышла во время проверки) — рендер новых сущностей 0.6.0 не смотрели.

## Открытые вопросы
- Legendary Survival Overhaul и Serene Seasons: проверить интеграцию температуры с сезонами.
- Клиент на настоящей видеокарте: FPS с шейдерами и DH, вид LOD, YSM, голосовой чат — чек-лист у потока 5
  в `docs/roadmap.md`.

## Порт Galacticraft (если решим делать)
Galacticraft Legacy (1.12.2) → 1.20.1 — это фактически переписывание: между версиями сменились
система блоков (flattening), рендер, сеть, генерация мира и capabilities. Объём — десятки тысяч строк.
Лицензия не препятствует: сборка частная и некоммерческая (CLAUDE.md «Назначение и лицензии») —
код и ассеты Galacticraft Legacy можно переносить с указанием авторства, не публикуя результат.
