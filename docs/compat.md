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
4. Lycanites Mobs: предупреждение `Mixin apply failed lycanitesmobs.mixins.json:StructureAccessor`
   (не находит поле `settings` в `Structure`). Причина: в jar `lycanitesmobs-1.20.1` нет
   `lycanitesmobs.refmap.json`, хотя конфиг миксинов на него ссылается, — миксины к ванильным классам
   (`StructureAccessor`, `ReloadCommandMixin`, клиентский `LightTextureMixin`) в настоящей игре не
   применяются. `StructureAccessor` кодом не используется (сервер не падает); теряются перезагрузка
   JSON Lycanites по `/reload` и, вероятно, эффект освещения на клиенте. Патч на будущее: собрать
   refmap (LGPL-3.0 позволяет распространять изменённый jar) и сообщить автору.
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
Планеты и орбиты — **только Ad Astra**. Скрипты-генераторы (берут исходные данные из jar):
- `patches/space_unify.py` → маршруты: `planet_dimension` Northstar и `rocket_accessible_dimension`
  Creating Space ведут в измерения `ad_astra:*`; старые узлы Creating Space пустые (недостижимы).
  Окружающая среда одна — Ad Astra: у Northstar в этих записях убрана атмосфера и гравитация поверхности
  (иначе гравитация двух модов перемножилась бы), орбиты невесомые у обоих, температура Northstar сохранена
  (на ней работают его машины). Creating Space сам не трогает гравитацию в `ad_astra:*`, а кислород проверяет
  только в своих биомах.
  Скафандры связаны тегами: комплекты Northstar и Creating Space засчитываются Ad Astra (кислород, холод,
  жар у термостойких), скафандры Ad Astra — Northstar (утепление; незеритовый — жар).
  Цена: скафандры Northstar/CS в системе Ad Astra не тратят кислород из бака (тег «снабжающая
  кислородом броня»); ракетам Northstar на всех планетах нужна тяга как на Земле.
- `patches/ore_crossgen.py` → ресурсы на планетах Ad Astra (`pack/kubejs/data/mm_space/`): руды Northstar
  и Creating Space кроме уже имеющихся на планете материалов (Ad Astra, Mekanism Ad Astra Ores); пласты
  камня Northstar (его рецепты используют камень; руды Northstar в них — в родной текстуре); жеоды лунного
  сапфира, марсианский гравий, гравий и гигантские грибы Венеры.
  Проверено (`scripts/tests/spacecheck.js`, `routecheck.js`): на 4 чанках Луны/Марса/Венеры/Меркурия есть
  все добавленные руды (вольфрам, титан, цинк, алюминий, кобальт, никель…), на 64 чанках — жеоды сапфира
  и марсианский гравий; маршруты обоих модов ведут в `ad_astra:*`.

## Открытые вопросы
- Legendary Survival Overhaul и Serene Seasons: проверить интеграцию температуры с сезонами.
- Серверный тест не проверяет клиент (рендер, шейдеры Oculus + Embeddium): нужен запуск клиента у игрока.

## Порт Galacticraft (если решим делать)
Galacticraft Legacy (1.12.2) → 1.20.1 — это фактически переписывание: между версиями сменились
система блоков (flattening), рендер, сеть, генерация мира и capabilities. Объём — десятки тысяч строк.
Перед началом проверить лицензию исходников Galacticraft Legacy: она должна разрешать распространять
изменённую версию друзьям.
