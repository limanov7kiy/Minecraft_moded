# Космос и ракеты

Ракеты, планеты, скафандры, кислород и связки космических модов.

<!-- auto:start toc -->
Модов в категории: **8**.

- [Ad Astra](#ad-astra)
- [Ad-Astra: Giselle Addon](#ad-astra-giselle-addon)
- [Create Ad Astra Recipes](#create-ad-astra-recipes)
- [Create: Northstar - Redux](#create-northstar---redux)
- [Create: Northstar - Structures](#create-northstar---structures)
- [Creating Space](#creating-space)
- [Mekanism x Create: Northstar](#mekanism-x-create-northstar)
- [Mekanism: Ad Astra Ores](#mekanism-ad-astra-ores)

Вернуться: [оглавление справочника](INDEX.md).
<!-- auto:end toc -->

## Общее по категории

В сборке **одна Солнечная система из трёх модов** ([compat.md](../compat.md), «Одна Солнечная система»): **планеты, орбиты, воздух, температура — Ad Astra; ракеты, топливо и Δv — Northstar Redux и Creating Space; реальная гравитация — Northstar**. Крафтовые ракеты Ad Astra убраны; машины, скафандры, ровер и реактивный костюм остались. Разведка и план «Galacticraft»-геймплея (данжи с боссами, схемы) — [galacticraft.md](../galacticraft.md).

**Путь игрока**
1. **Create** — база: шестерни, жидкости, контрапции. Космос строится на ней.
2. **Ad Astra**: сталь (Компрессор, Этрионная печь) → энергия (генератор, панели) → Кислородный загрузчик и **скафандр**; нефть → топливо; книга-руководство «Астродукс». Отсюда же ровер и кислородное снабжение баз (Распылитель кислорода, вентиляция, шлюзы).
3. **Northstar Redux** (проще): электролиз → водород/кислород, Combustion Engine; Telescope + Astronomy Table открывают планеты (Луна 2, Марс 5, Венера и Меркурий 6 «науки»); Ракетная станция, двигатели, баки, Interplanetary Navigator, теплозащита. Дополнения: **Northstar Structures** (кратеры и бассейн Луны, Лунный компас), **Mekanism x Create: Northstar** (модули МекаСкафандра).
4. **Creating Space** (сложнее): стол инженера, чертёж двигателя, пара топлива, расчёт Δv до Венеры, Меркурия и Глацио (самый дорогой перелёт — 20 000 Δv от орбиты Земли).
5. Кислород скафандров Northstar/Creating Space в мирах Ad Astra связывает наш мод `mm-compat` (воздух из баллона, 1 ед./с). Защиту от условий планет усиливает **Giselle Addon** (чары и модули МекаСкафандра). Руды и растения на планетах — `patches/ore_crossgen.py` (+ Mekanism: Ad Astra Ores, Create Ad Astra Recipes — соседние карточки).

**Порядок планет** (Ad Astra): Земля → Луна (первая цель) → Марс → Венера и Меркурий (жара, нужен жаростойкий скафандр) → Глацио (другая система, воздух есть, холодно). Подробные таблицы — в карточках Ad Astra и Creating Space. Структуры планет — в карточках: Ad Astra (подземелье Луны, деревни, храм Марса, Венера), Northstar Structures (кратеры).

Проверки: `scripts/tests/gravitycheck.js`, `routecheck.js`, `rocketcheck.js`, `oxygen_commands.txt`, `spacecheck.js`, `northstar_structures_commands.txt` ([compat.md](../compat.md)); что смотреть глазами на клиенте — [client-checklist.md](../client-checklist.md); производительность Ad Astra — [performance.md](../performance.md).

---

<!-- cards:start -->
## Ad Astra

<!-- auto:start card:ad-astra -->
<a id="ad-astra"></a>
- **Версия:** 1.15.21 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ad-astra)
- **Что это:** Космическое расширение: планеты как отдельные измерения (Луна, Марс, Венера, Меркурий, Глацио) со своим воздухом, температурой и гравитацией, скафандры, кислородные машины, ровер и реактивный костюм. В сборке отвечает за планеты и орбиты единой Солнечной системы; его крафтовые ракеты убраны. _(источник: docs сборки)_
- **В наших docs:** [galacticraft.md](../galacticraft.md) (25), [compat.md](../compat.md) (20), [dedup.md](../dedup.md) (11), [performance.md](../performance.md) (11), [roadmap.md](../roadmap.md) (8), [client-checklist.md](../client-checklist.md) (2)
<!-- auto:end card:ad-astra -->

### Как играть

Ad Astra (Terrarium, лицензия Terrarium License) даёт **планеты как отдельные измерения** со своим воздухом, температурой и гравитацией, а также машины, скафандры и ровер. В сборке действует «одна Солнечная система» ([compat.md](../compat.md), раздел «Одна Солнечная система»; [galacticraft.md](../galacticraft.md)): **планеты и орбиты — только Ad Astra, ракеты — только Northstar и Creating Space**. Крафтовые ракеты Ad Astra (T1–T4), Верстак НАСА (и его автоматическая версия из Giselle Addon), конус, стабилизаторы и стартовая площадка **убраны** (`pack/kubejs/server_scripts/space_rockets.js`, спрятаны в JEI); двигатели и баки остались — из них делают реактивный костюм и ровер.

**Порядок планет** (данные `ad_astra:planets/*`, поле `tier` — тир ракеты Ad Astra; в сборке ракет Ad Astra нет, поэтому тир справочный, а границу задают тяга и Δv ракет Northstar/Creating Space, см. их карточки):

| Тело (измерение) | Тир | Воздух | Температура, °C | Гравитация в данных Ad Astra, м/с² |
|---|---|---|---|---|
| Земля `minecraft:overworld` → орбита `ad_astra:earth_orbit` | 1 | есть | 15 (орбита −270) | 9,807 (орбита 0) |
| Луна `ad_astra:moon` (+ `moon_orbit`) | 1 | нет | −173 | 1,622 |
| Марс `ad_astra:mars` (+ `mars_orbit`) | 2 | нет | −65 | 3,72 |
| Венера `ad_astra:venus` (+ `venus_orbit`) | 3 | нет | 464 | 8,87 |
| Меркурий `ad_astra:mercury` (+ `mercury_orbit`) | 3 | нет | 167 | 3,7 |
| Глацио `ad_astra:glacio` (+ `glacio_orbit`), система Proxima Centauri | 4 | **есть** | −20 | 3,721 |

В нашей сборке гравитацию Луны, Марса, Венеры, Меркурия и орбит **считает Northstar** (Луна 1,6; Марс 3,71; Венера 8,87; Меркурий 3,7; орбиты 0), а в данных Ad Astra для этих тел стоит нейтральные 9,807 (множитель 1) — иначе миксины двух модов перемножили бы её; Глацио — гравитация Ad Astra (`patches/space_unify.py`).

**Кислород и скафандры**
- Кислородный загрузчик (`ad_astra:oxygen_loader`) превращает воду в кислород и заряжает им скафандр, газовый баллон (`ad_astra:gas_tank`, `large_gas_tank`) или ведро. Водяная помпа (`water_pump`) даёт воду кислородному распылителю бесконечно.
- Распылитель кислорода (`ad_astra:oxygen_distributor`) наполняет герметичное помещение (по руководству Астродукса — до 6000 блоков на один распылитель), показ области — кнопка в интерфейсе. Вентиляция `vent`, шлюз `airlock`, детектор кислорода `oxygen_sensor` — для дверей, перетока воздуха и редстоуна. Дыра в стене даёт **воздушный вихрь**: всё внутри высасывает наружу.
- Холод (Луна, Марс, орбиты) переносит обычный скафандр (`space_helmet`, `space_suit`, `space_pants`, `space_boots`); жару Венеры и Меркурия — только **незеритовый скафандр** и реактивный костюм. Скафандры Northstar и Creating Space в мирах Ad Astra работают благодаря `mm-compat` (см. «Конфиги» ниже).
- Реактивный костюм (`jet_suit*`): взлёт и ускоренный полёт, питается энергией (заряжается в Энергизаторе), включение/выключение полёта — клавиша мода. Воздушный пистолет `zip_gun` — перемещение в невесомости за счёт кислорода. TI-69 показывает кислород, температуру и гравитацию на месте. Ровер `ad_astra:tier_1_rover` — наземный транспорт с радио, 16 слотов, 2 места; заправляется топливом из `#ad_astra:fuel`.

**Топливо и энергия.** Нефть (`ad_astra:oil`) добывают из океанских «нефтяных скважин» (структура `oil_well`) и перерабатывают на Топливном заводе (`fuel_refinery`) в `ad_astra:fuel`. Криотопливо (`ad_astra:cryo_fuel`) делает Криогенератор из осколков льда (`ice_shard`) — втрое эффективнее (`#ad_astra:efficient_fuel`); при размещении ранит и замораживает воду. Энергия: Угольный генератор, Солнечная панель, Энергизатор, кабели (стальной, дешевый). Сплавы и пластины: Компрессор (пластины из слитков), Этрионная доменная печь (сталь, сплавы).

Руководство — книга **Астродукс** (`ad_astra:astrodux`, Patchouli). Космическая станция Ad Astra создаётся в экране планет ракеты Ad Astra, которых в сборке нет; орбиты (`*_orbit`) — обычные измерения, куда летят ракеты Northstar/Creating Space, и строить там можно.

### Предметы/блоки

| Что | Id |
|---|---|
| Материалы | сталь `steel_ingot`, деш `desh_ingot`, острум `ostrum_ingot`, калорит `calorite_ingot` (и сырьё `raw_*`, пластины `*_plate`, стержни, листовые блоки `*_plateblock`), `etrium_ingot`, осколок льда `ice_shard` |
| Детали | двигатели (`steel_engine`, `desh_engine`, `ostrum_engine`, `calorite_engine`), баки (`*_tank`), рама двигателя `engine_frame`, колесо `wheel`, вентилятор `fan` |
| Машины | `coal_generator`, `compressor`, `etrionic_blast_furnace`, `energizer`, `fuel_refinery`, `cryo_freezer`, `oxygen_loader`, `oxygen_distributor`, `oxygen_sensor`, `water_pump`, `gravity_normalizer`, `radio` |
| Снаряжение | скафандр `space_*`, незеритовый `netherite_space_*`, реактивный `jet_suit*`, `zip_gun`, `ti_69`, `wrench` (ключ), `gas_tank`, `large_gas_tank` |
| Транспорт | ровер `tier_1_rover`, посадочный модуль (сущность `ad_astra:lander`) |
| Жидкости | `ad_astra:oil`, `fuel`, `cryo_fuel`, `hydrogen`, `oxygen` |
| Прочее | сыр `cheese` (лунный), флаги и глобусы, индустриальные лампы 16 цветов, раздвижные двери, лестницы/плиты планетных камней, грибы аэроноса/строфара и гласианская древесина (Глацио) |

Из рецептов убраны (в сборке не крафтятся): `tier_1..4_rocket`, `nasa_workbench`, `rocket_nose_cone`, `rocket_fin`, `launch_pad`.

### Мобы

Естественные спавны (категория `monster`, если не указано иное; данные биомов):

| Моб | Id | Где |
|---|---|---|
| Искажённый лунатик, Звёздный ползун | `ad_astra:corrupted_lunarian`, `star_crawler` | Луна, `ad_astra:lunar_wastelands` |
| Лунатик (житель), странствующий торговец-лунатик | `ad_astra:lunarian`, `lunarian_wandering_trader` | деревни лунатиков на Луне |
| Марсианский ящер | `ad_astra:martian_raptor` | три биома Марса |
| Моглер, Серный крипер, Зомби-пигро | `ad_astra:mogler`, `sulfur_creeper`, `zombified_pygro` | `venus_wastelands`, `infernal_venus_barrens` |
| Пигро, Пигро-громила | `ad_astra:pygro`, `pygro_brute` | в биомах не спавнятся; живут в структурах Венеры (деревня, башня пигро — по названиям структур) |
| Магмовый куб | `minecraft:magma_cube` | Меркурий, `mercury_deltas` |
| Гласианский баран (`creature`) | `ad_astra:glacian_ram` | Глацио |

Теги `#ad_astra:lives_without_oxygen` и `#ad_astra:can_survive_in_space` перечисляют существ, которым воздух не нужен; к ним мы добавляем мобов других модов (данж Galacticraft Revival, см. [galacticraft.md](../galacticraft.md)). Боссов у Ad Astra нет.

### События

- **Удушье**: без воздуха — урон `ad_astra:oxygen`; **кислотный дождь** (урон `ad_astra:acid_rain`), перегрев и обморожение по температуре планеты; **воздушный вихрь** при разгерметизации (сущность `ad_astra:air_vortex`; отключается настройкой `disableAirVortexes`).
- Криотопливо в мире наносит урон `ad_astra:cryo_fuel`; факел ракеты — `rocket_flames`; ровер сбивает — `ran_over`.
- Достижения ветки `ad_astra:*`: «Ad Astra», «One Small Step» (высадка на Луну), «The Martian», «Venusian», «Quite the Sun Tan» (Меркурий), «Interstellar» (новая система), «Astronaut» (скафандр), «Doom Slayer» (незеритовый скафандр), «Rocket Man» (реактивный костюм), «Take a Deep Breath» (распылитель) и др. Часть достижений про ракеты Ad Astra недостижима — их рецептов нет.

### Данжи/структуры (/locate)

| Структура | Id для `/locate structure` | Где (биом) | Шаг / отступ (чанки) |
|---|---|---|---|
| Лунное подземелье | `ad_astra:moon_dungeon` | `lunar_wastelands` | 32 / 14 |
| Деревня лунатиков | `ad_astra:lunarian_village` | `lunar_wastelands` | 30 / 14 |
| Лунная башня | `ad_astra:lunar_tower` | вместе с деревней лунатиков | 34 / 13 |
| Марсианский храм | `ad_astra:mars_temple` | `martian_wastelands`, `martian_canyon_creek` | 40 / 27 |
| Деревня пигро | `ad_astra:pygro_village` | `venus_wastelands` | 24 / 17 |
| Башня пигро | `ad_astra:pygro_tower` | `venus_wastelands` | 29 / 19 |
| Венерианская «пуля» | `ad_astra:venus_bullet` | `venus_wastelands` | 27 / 14 |
| Нефтяная скважина | `ad_astra:oil_well` | океаны Земли | 20 / 14 |

Искать нужно в нужном измерении: `/execute in ad_astra:moon run locate structure ad_astra:moon_dungeon`. Ванильный `/locate` в сборке асинхронный (Async Locator) — ответ приходит позже, а не сразу ([compat.md](../compat.md)). Биомы: `ad_astra:lunar_wastelands`, `martian_canyon_creek`, `martian_polar_caps`, `martian_wastelands`, `venus_wastelands`, `infernal_venus_barrens`, `mercury_deltas`, `glacio_snowy_barrens`, `glacio_ice_peaks`, `orbit`. Лут: `ad_astra:chests/dungeon/moon`, `chests/temple/mars`, `chests/village/moon`. В Лунное подземелье сборка добавляет данж Galacticraft Revival с боссом (`gcrevival`, [galacticraft.md](../galacticraft.md)).

### Конфиги

Настройки (мод на Resourceful Config, файла в `pack/config` у нас нет — действуют значения по умолчанию): `disableOxygen`, `disableTemperature`, `disableGravity`, `disableAirVortexes`, `disabledPlanets` (скрыть планеты в экране планет), `launchFromAnywhere`, `atmosphereLeave` (высота выхода в космос), `planetRandomTickSpeed`, `forcePlanetTick` (тики на планетах: замерзание воды, увядание растений; чуть тяжелее для производительности), `allowFlagImages`, а также параметры машин (ёмкости и скорость по тирам, генератор, помпа, радиус распределения).

**Наши правки** ([compat.md](../compat.md), «Одна Солнечная система»; генераторы — `patches/space_unify.py`, `patches/ore_crossgen.py`):
- `pack/kubejs/data/ad_astra/planets/*.json` — гравитация 9,807 (нейтральная) для Луны, Марса, Венеры, Меркурия и орбит; реальную даёт Northstar.
- `pack/kubejs/data/ad_astra/tags/items/freeze_resistant_armor.json` и `heat_resistant_armor.json` — скафандры Northstar и Creating Space защищают от холода и жары и в системе Ad Astra.
- `pack/kubejs/data/ad_astra/creatingspace/rocket_accessible_dimension/*` — маршруты ракет Creating Space по измерениям Ad Astra (Меркурий и Глацио — новые узлы).
- `pack/kubejs/server_scripts/space_rockets.js` + `startup_scripts/space_rockets_list.js` — удаление рецептов ракет Ad Astra.
- `pack/kubejs/data/mm_space/*` — руды и растения Northstar/Creating Space на планетах Ad Astra; `pack/config/almostunified/unify.json` — `ad_astra` в списке объединяемых модов.
- `mods-src/mm-compat` — кислород скафандров Northstar/Creating Space (см. карточки Northstar и Creating Space); в зависимостях — Ad Astra по id версии Forge-сборки.

## Ad-Astra: Giselle Addon

<!-- auto:start card:ad-astra-giselle-addon -->
<a id="ad-astra-giselle-addon"></a>
- **Версия:** 6.20 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ad-astra-giselle-addon)
- **Что это:** Дополнение к Ad Astra: кислородные канистры, топливный погрузчик, датчик ракеты и четыре чары защиты в мирах Ad Astra (дыхание, огонь, кислотный дождь, гравитация), а также модули МекаСкафандра. _(источник: docs сборки)_
- **В сборке:** Ad Astra × Mekanism/Create (кислород, энергия)
- **В наших docs:** [galacticraft.md](../galacticraft.md) (4), [russification-audit.md](../russification-audit.md) (3), [jei.md](../jei.md) (2)
<!-- auto:end card:ad-astra-giselle-addon -->

### Как играть

Аддон (автор — gisellevonbingen, MIT; нужен Botarium) добавляет к Ad Astra удобства и защиту от условий планет. **Важно для сборки:** ракеты Ad Astra убраны ([compat.md](../compat.md)), поэтому блоки аддона, привязанные к ним, почти бесполезны, а Автоматический верстак НАСА **не крафтится** (рецепт удалён).
- **Кислородная канистра** (`oxygen_can`; `netherite_oxygen_can` — улучшение с острумом и незеритом; `creative_oxygen_can`) носится в инвентаре и снабжает кислородом; нужна чаре «Дыхание в космосе» и модулю MekaSuit. Режим подзарядки одежды (выкл / вся броня / весь инвентарь) переключается в интерфейсе.
- **Топливный погрузчик** (`fuel_loader`) подаёт топливо ближайшему ровёру или ракете в радиусе работы — для нас полезен только с ровером.
- **Датчик ракеты** (`rocket_sensor`) через компаратор выдаёт состояние ближайшей ракеты (найдена, летит вверх, заправлена, обратный отсчёт, найден посадочный модуль) — относится к ракетам Ad Astra.
- **Нормализатор гравитации** (`gravity_normalizer`, помечен устаревшим) выравнивает гравитацию живых существ в заданном объёме; полноценная замена — чара «Нормализация гравитации».
- Совместимость (работает, если установлен соответствующий мод): модули **MekaSuit** — дыхание в космосе, защита от огня, защита от кислотного дождя, нормализация гравитации; улучшения PneumaticCraft и модификаторы Tinkers' Construct. В сборке есть Mekanism и AE2 (рецепты дробления «Венерианский песчаник → песок Венеры» и «небесная пыль»); Tinkers, PneumaticCraft, Thermal и Industrial Foregoing в сборке нет — их рецепты аддона не действуют ([jei.md](../jei.md)).

### Предметы/блоки

| Что | Id |
|---|---|
| Кислородные канистры | `ad_astra_giselle_addon:oxygen_can`, `netherite_oxygen_can`, `creative_oxygen_can` |
| Блоки | `fuel_loader`, `rocket_sensor`, `gravity_normalizer` (устарел), `automation_nasa_workbench` (в сборке без рецепта) |
| Модули MekaSuit | `space_breathing_unit`, `space_fire_proof_unit`, `acid_rain_proof_unit`, `gravity_normalizing_unit` |

### Чары

Работают в измерениях Ad Astra (то есть на всех планетах и орбитах нашей Солнечной системы); тратят энергию или прочность предмета.

| Чара (id) | Действие |
|---|---|
| `ad_astra_giselle_addon:space_breathing` — Дыхание в космосе | можно дышать в измерениях Ad Astra; в инвентаре нужна кислородная канистра |
| `ad_astra_giselle_addon:space_fire_proof` — Защита от космического огня | не загораешься в измерениях Ad Astra |
| `ad_astra_giselle_addon:acid_rain_proof` — Защита от кислотного дождя | нет урона от кислотного дождя |
| `ad_astra_giselle_addon:gravity_normalizing` — Нормализация гравитации | гравитация как в обычном мире |

Зачарованные книги крафтятся (`recipes/crafting/enchanted_book_*`), например книга «Дыхание в космосе»: книга + скафандр Ad Astra + 7 лунного камня (`moon_stone`).

### Конфиги

Параметры (файл конфига мода, в `pack/config` отсутствует — значения по умолчанию): ёмкость и скорость канистр, радиус и ёмкость погрузчика, ёмкость и расход нормализатора, радиус датчика, расход энергии/прочности/воздуха для каждой защиты (общие, MekaSuit и PneumaticCraft отдельно), показ подсказок в книгах. Наших правок нет; удаление рецепта автоверстака — в `pack/kubejs/server_scripts/space_rockets.js`.

## Create Ad Astra Recipes

<!-- auto:start card:create-ad-astra-recipes -->
<a id="create-ad-astra-recipes"></a>
- **Версия:** 1.0.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/create-ad-astra-recipes)
- **Что это:** Рецепты, повышающие совместимость Create и Ad Astra. _(источник: docs сборки)_
<!-- auto:end card:create-ad-astra-recipes -->

### Что даёт игроку

Create Ad Astra Recipes (1.0.0) — мини-мод только с рецептами (9 JSON, ни предметов, ни блоков):
- **Рецепты Create** (`create:mechanical_crafting`, механический крафтер): ракеты Ad Astra `tier_1_rocket` … `tier_4_rocket`. **В нашей сборке эти ракеты убраны** — рецепты на них вырезает `pack/kubejs/server_scripts/space_rockets.js` (список в `startup_scripts/space_rockets_list.js`), ракеты строятся из блоков Northstar/Creating Space ([compat.md](../compat.md), «Одна Солнечная система»).
- **Крафт-рецепты материалов планеты Глацио**: 4 ледяных осколка (`ad_astra:ice_shard`) → `ad_astra:permafrost`; вечная мерзлота + шерсть/листья/бревно/камень → `glacian_fur`, `glacian_leaves`, `glacian_log`, `glacio_stone`. Эти рецепты работают.
В JEI мод не даёт отдельных категорий ([jei.md](../jei.md)). Конфигов нет.

## Create: Northstar - Redux

<!-- auto:start card:northstar-redux -->
<a id="northstar-redux"></a>
- **Версия:** 0.6.6+1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/northstar-redux)
- **Что это:** Космос для Create в духе Galacticraft: ракеты собираются из блоков, нужны двигатели, топливо, навигатор и теплозащита; планеты открываются телескопом, есть электролиз, герметизаторы и скафандры. В сборке даёт ракеты и гравитацию, а сами планеты — Ad Astra. _(источник: docs сборки)_
- **В сборке:** порт Create: Northstar под Create 6 (оригинал несовместим)
- **В наших docs:** [compat.md](../compat.md) (68), [dedup.md](../dedup.md) (32), [galacticraft.md](../galacticraft.md) (15), [roadmap.md](../roadmap.md) (11), [version-choice.md](../version-choice.md) (10), [enchanting-table.md](../enchanting-table.md) (8)
<!-- auto:end card:northstar-redux -->

### Как играть

Northstar Redux (MIT; авторы Redstonneur1256, lightning2401) — космос в духе Galacticraft на механике **Create**: ракета собирается из блоков как контрапция, топливо — жидкости в баках, воздух и тепло — машины. В сборке он даёт **ракеты, расчёт топлива, гравитацию, механизмы кислорода/температуры и материалы**, а сами планеты — Ad Astra ([compat.md](../compat.md), «Одна Солнечная система»): маршруты Northstar (`northstar:planet_dimension`) перенаправлены в измерения `ad_astra:*`, мир Northstar в полётах не используется.

**Постройка и полёт ракеты** (подсказки Ponder «Building a rocket»):
1. Нужны **Rocket Thruster** (`rocket_thruster`; сколько — зависит от массы и гравитации), баки с топливом (`hydrocarbon`, `hydrogen` или `methane`; топливо хранится в баках Create), хотя бы одно сиденье, **Rocket Controls** (`rocket_controls`) и **Rocket Station** (`rocket_station`). Всё склеивается Super Glue (подойдут строительные леса или куски клея).
2. В Ракетной станции кладётся **Space Atlas** (`space_atlas`) — выбор цели; станция показывает топливо (взлёт/путь/посадка), число двигателей и навигационных компьютеров, **теплозащиту**. «Assemble Rocket» собирает ракету, «Disassemble» — разбирает.
3. Для других планет нужен **Interplanetary Navigator** (`interplanetary_navigator`; для лун не нужен). **Targeting Computer** в **Computer Rack** (`computer_rack`) снижает расход топлива.
4. Точка посадки — блок **Rocket Waypoint** (ПКМ атласом записывает координаты); **Auto-Landing Station** (`auto_lander`) сажает ракету безопасно.
5. Запуск начинается и отменяется клавишей, которую показывает подсказка на экране; при посадке торможение — удержанием клавиши из подсказки; без Rocket Controls ракета не соберётся («No rocket controls attached»). Теплозащита набирается металлическими блоками на корпусе: теги `northstar:tier_1/2/3_heat_resistance` — листовой металл/обшивка/колонны титана и железный блок (3), мартианской стали (8), вольфрама (20); тяжёлые блоки (`heavy_blocks`, 5; `super_heavy_blocks`, 10) утяжеляют ракету.

**Открытие планет.** Планета открывается «наукой»: **Telescope** (`telescope`, только ночью, тратит бумагу) делает **Astronomical Reading**, **Astronomy Table** (`astronomy_table`) вносит его в Space Atlas. Нужная наука: Земля 1, Луна 2, Марс 5, Венера 6, Меркурий 6 (`required_science` в данных). Чтение той же планеты в тот же день не засчитывается; чтения разных планет ценнее.

**Планеты в сборке** (данные Northstar после `space_unify.py`; измерения — Ad Astra):

| Тело | Измерение | Гравитация | Температура, °C |
|---|---|---|---|
| Земля (орбита `earth_orbit`) | `minecraft:overworld`, `ad_astra:earth_orbit` | орбита 0 | орбита −270 |
| Луна (+ орбита; орбиту для Northstar добавили мы) | `ad_astra:moon`, `moon_orbit` | 1,6 | −183 |
| Марс | `ad_astra:mars`, `mars_orbit` | 3,71 | −100 |
| Венера | `ad_astra:venus`, `venus_orbit` | 8,87 | 464 |
| Меркурий | `ad_astra:mercury`, `mercury_orbit` | 3,7 | от −200 до 434 (меняется) |

Глацио Northstar не знает. Данные описывают и другие тела Солнечной системы (Юпитер, Европа, Титан и т.д.), но посадочных измерений у них нет.

**Воздух и тепло.** Электролизная машина (`electrolysis_machine`) расщепляет воду на водород и кислород; Атмосферный концентратор (`atmospheric_concentrator`) собирает газ из атмосферы; **Oxygen Sealer** (`oxygen_sealer`, кислород сзади + вращение снизу) наполняет помещение; **Large Fan** (`large_fan`) увеличивает область; **Oxygen Filler** (`oxygen_filler`) заправляет скафандры; **Temperature Regulator** (`temperature_regulator`) меняет температуру в области; `vent`/`insulated_vent`; **Oxygen Detector** (`oxygen_detector`) — редстоун-сигнал. Скафандры: железный (`iron_space_suit_*`) и из мартианской стали (`martian_steel_space_suit_*`) — с баллоном воздуха (NBT `Oxygen`). В нашей сборке **кислород, которым дышит игрок на планетах, считает Ad Astra**: из данных Northstar убрана атмосфера, а скафандр Northstar тратит воздух из баллона через мод `mm-compat` (1 ед./с; пустой баллон — удушье). Следствие по данным, не проверено в игре: Атмосферный концентратор на этих планетах газ не берёт («нет атмосферы»). **Combustion Engine** (`combustion_engine`) — источник вращения из топлива (типы: водород, жидкий водород, метан, углеводород, биотопливо; есть типы для Create Diesel Generators и TFMG).

### Предметы/блоки

| Группа | Id (`northstar:`) |
|---|---|
| Ракета | `rocket_station`, `rocket_controls`, `rocket_thruster`, `rocket_waypoint`, `interplanetary_navigator`, `auto_lander`, `computer_rack`, `targeting_computer`, `space_atlas`, `return_ticket` |
| Наука | `telescope`, `astronomy_table`, `astronomical_reading` |
| Машины | `combustion_engine`, `electrolysis_machine`, `atmospheric_concentrator`, `oxygen_sealer`, `oxygen_filler`, `oxygen_detector`, `temperature_regulator`, `large_fan` + `fan_blade`, `ice_box`, `circuit_engraver`, `solar_panel`, `vent`, `insulated_vent` |
| Материалы | титан `titanium_ingot`, вольфрам `tungsten_ingot`, мартианская сталь `martian_steel_ingot`, `lunar_sapphire_shard`/`polished_lunar_sapphire`, `circuit`, `advanced_circuit`, `hardened_precision_mechanism`, `durable_fabric`, `rocket_combustion_chamber`, `oxygen_separator` |
| Броня и инструмент | скафандры (железный, из мартианской стали), броня и инструмент из мартианской стали (`martian_*`), `broken_iron_space_suit_*` |
| Камень планет | `moon_stone`, `mars_stone`, `venus_stone`, `mercury_stone` (+ глубинные, кирпичи, плиты, колонны, лампы), руды Луны/Марса/Венеры/Меркурия (титан, цинк, вольфрам, свечение, и др.) |
| Растения | деревья `argyre_*`, `calorian_*`, `coiler_*`, `wilter_*`, грибы Венеры, марсианские цветы/пальма, марсианская клубника |
| Жидкости | `oxygen`, `hydrogen`, `liquid_hydrogen`, `liquid_oxygen`, `methane`, `hydrocarbon`, `biofuel`, `sulfuric_acid`, `brine`, `chlorine`, `sodium`, `titanium_tetrachloride` |

Ресурсы Northstar в сборке размещены и на планетах Ad Astra (генератор `patches/ore_crossgen.py`, подробности — в «Конфигах»).

### Мобы

Есть яйца призыва (`northstar:<id>_spawn_egg`). Естественные спавны привязаны к биомам `northstar:*`, а в сборке игроки живут на планетах Ad Astra; в их биомы мы добавляли только руды, пласты камня, жеоды и растения — так что этих мобов в Ad Astra получают яйцами или `/summon` (вывод по патчам, игрой не проверено). Боссов нет.

| Планета | Мобы (`northstar:`) |
|---|---|
| Луна | `moon_lunargrade` (Луноходка, плюёт), `moon_snail`, `moon_eel` |
| Марс | `mars_cobra`, `mars_moth`, `mars_toad`, `mars_worm` (Эхо-червь, гнёзда `mars_worm_nest`) |
| Венера | `venus_mimic`, `venus_scorpion`, `venus_stone_bull`, `venus_vulture` |
| Меркурий | `mercury_raptor`, `mercury_roach`, `mercury_tortoise` |
| Холод | `frozen_zombie` |

Все они в теге `#northstar:doesnt_require_oxygen` (вместе с зомби, скелетом, странником и др.).

### События

- Урон: `northstar:suffocation_no_oxygen` («кончился кислород»), `suffocation_no_spacesuit` («забыл скафандр»), `acid` (серная кислота).
- Достижения: «Welcome to Northstar!» (`northstar:root`), «One Small Step» (ступить на Луну), «One Giant Leap» (ступить на Марс). Статистика: запуски, полёты и крушения ракет.
- Ракета при взлёте/посадке выводит сообщения (скорость, высота, отмена запуска); о причинах отказа взлёта — в Ракетной станции.

### Данжи/структуры (/locate)

| Структура | Id | Биомы |
|---|---|---|
| Лунная база | `northstar:lunar_base` | `#northstar:moon_biomes` (только `northstar:lunar_*`) |
| Марсианская база | `northstar:martian_base` | `martian_dunes`, `martian_highlands` (тег `has_mars_base`) |

Обе — шаг 40 / отступ 16 чанков. Лут: `northstar:chests/lunar_base_chest`, `martian_base_chest`, `martian_base_seed_chest` (кислород, золотые яблоки, книги, лунный сапфир, схемы, мартианская сталь, в сундуке с семенами — «спящие» марсианские семена, клубника). Так как биомы Northstar в сборке не посещаются, базы **на планетах Ad Astra не генерируются** (их биомы — только `northstar:*`) — вывод по данным, игрой не проверялся. Кратеры Луны — в карточке Northstar Structures. Биомы Northstar для `/locate biome`: `northstar:lunar_plains`, `lunar_hills`, `lunar_ice_caves`, `lunar_crater_fields`, `martian_dunes`, `mercury_basins`, `venus_fungal_forest` и другие.

### Чары

| Id | Действие |
|---|---|
| `northstar:frostbite` — Обморожение | наносит врагам урон холодом; книги лежат в сундуках баз (лут-таблицы) |

### Конфиги

Наших правок конфига нет (`pack/config` не содержит Northstar). Правки данными ([compat.md](../compat.md)):
- `patches/space_unify.py` → `pack/kubejs/data/northstar/northstar/planet_dimension/*.json` (измерения Ad Astra, без атмосферы, добавлена орбита Луны `moon_orbit`), `tags/items/insulating.json` и `heat_resistant.json` (скафандры Ad Astra и Creating Space считаются изолирующими/жаростойкими).
- `patches/ore_crossgen.py` → `pack/kubejs/data/mm_space/*`: руды Northstar (кроме материалов, которые на планете уже есть у Ad Astra и Mekanism), пласты `northstar:<планета>_stone`/`_deep_stone`, жеоды лунного сапфира, гравий Марса/Венеры и гигантские грибы Венеры — в биомы Ad Astra.
- `mods-src/mm-compat` (`SuitOxygen`): Ad Astra считает, что существо дышит, пока на нём полный комплект скафандра Northstar (теги `northstar:oxygen_sealing`/`oxygen_sources`) с непустым баллоном; воздух списывается 1 ед./с, креатив и спектатор не тратят. Проверка — `scripts/tests/oxygen_commands.txt`.
- `pack/config/almostunified/unify.json` — `northstar` в списке объединяемых модов.
- Известные причины замены оригинала: он собран под Create 0.5 и роняет сервер на Create 6.

## Create: Northstar - Structures

<!-- auto:start card:create-northstar-structures -->
<a id="create-northstar-structures"></a>
- **Версия:** 1.0.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/create-northstar-structures)
- **Что это:** Лунные кратеры всех размеров и гигантский бассейн Южный полюс — Эйткен для Northstar, плюс Лунный компас и команда поиска. В сборке работает и на Луне Ad Astra (патч), с исправлением удаления мобов. _(источник: docs сборки)_
- **В сборке:** биомы Луны под Redux 0.6+ и кратеры на Луне Ad Astra: patches/northstar_structures_fix.py
- **В наших docs:** [compat.md](../compat.md) (43), [dedup.md](../dedup.md) (29), [version-choice.md](../version-choice.md) (9), [enchanting-table.md](../enchanting-table.md) (8), [performance.md](../performance.md) (7), [friends.md](../friends.md) (6)
<!-- auto:end card:create-northstar-structures -->

### Как играть

Аддон (лицензия в `mods.toml` — All Rights Reserved) делает Луну рельефной: **кратеры разных размеров, «старые» кратеры с валом и гигантский бассейн Южный полюс — Эйткен** (радиус 2400 блоков), плюс лунные пещеры с кристаллами (аметист-асурин, голубой лёд, сосульки, обсидиан, глоустоун) и жеоды лунного сапфира. Играть тут особо нечем: кратеры — ландшафт, который находят с помощью **Лунного компаса** (`northstar_structures:moon_compass`) или команды `/findmoonstructure`. В сборке кратеры стоят на Луне Ad Astra (`ad_astra:moon`, биом `ad_astra:lunar_wastelands`, дно и вал — песок и камень Луны Ad Astra) и на Луне Northstar (`northstar:moon`) — патч `patches/northstar_structures_fix.py` ([compat.md](../compat.md), раздел «Create: Northstar - Structures»).

### Предметы/блоки

- `northstar_structures:moon_compass` — **Лунный компас**: ищет кратеры рядом, экран с вкладками «Huge», «Old Huge», «Aitken», сортировкой по диаметру, глубине и расстоянию; выбранная цель привязывается (стрелка указывает на неё). Рецепт аддона: полированный лунный сапфир ×4 + Targeting Computer Northstar в центре. Нюанс: файл рецепта лежит в папке `data/northstar_structures/recipe/` (так называется папка в более новых версиях игры; в 1.20.1 рецепты читаются из `recipes/`) — работает ли рецепт в игре, **не проверено**; выдать предмет можно командой `/give @s northstar_structures:moon_compass`.
### Данжи/структуры (/locate)

Структуры-кратеры:

| Id | Размер | Примечание |
|---|---|---|
| `northstar_structures:small_crater` | малый, радиус 10–16 | шаг 8 / отступ 3 чанка (набор `craters`) |
| `northstar_structures:medium_crater` | средний | набор `craters` |
| `northstar_structures:large_crater` | большой | шаг 24 / отступ 8 (набор `large_craters`) |
| `northstar_structures:huge_crater` | огромный, радиус 150–250 | ячейки 64×64 чанка |
| `northstar_structures:old_small_crater`, `old_medium_crater`, `old_large_crater` | «старые» (с валом, дно — песок) | в наборах малых/больших |
| `northstar_structures:old_huge_crater` | огромный старый | |
| `northstar_structures:south_pole_aitken_basin` | бассейн радиусом 2400 | одна ячейка на 5000 чанков |

Примеры (Луна, измерение указываем явно): `/execute in ad_astra:moon run locate structure northstar_structures:small_crater` и `.../old_large_crater`; `/execute in northstar:moon run locate structure northstar_structures:small_crater`. Тест — `scripts/tests/northstar_structures_commands.txt` (находит малый и старый большой кратер на `ad_astra:moon`, малый на `northstar:moon`). Ванильный `/locate` в сборке асинхронный (Async Locator) — ответ приходит позже.

Биомы кратеров: тег `#northstar_structures:crater_moon_biomes` — пять лунных биомов Northstar (`lunar_asurine_caves`, `lunar_cooled_lava_cave`, `lunar_crater_fields`, `lunar_hills`, `lunar_plains`) **и добавленный нами `ad_astra:lunar_wastelands`**. Лунные биомы с пещерами (`northstar:lunar_ice_caves`, `lunar_glowstone_cavern` и др.) переопределены патчем под Redux 0.6.

### Команды/гамерулы

- `/findmoonstructure <тип>` — поиск кратеров рядом: `small`, `old_small`, `medium`, `old_medium`, `large`, `old_large`, `huge`, `old_huge`, `south_pole_aitken_basin`. Выводит список с диаметром, глубиной, расстоянием; клик телепортирует (`/tp @s x ~ z`). Если тип выключен в конфиге — сообщение «generation is disabled». Права и точная форма вызова не проверялись.

### Конфиги

Файл конфига аддона (`northstar_structures-common.toml`, раздел `moon`) в `pack/config` не лежит — действуют значения по умолчанию: `enableCraters`, `enableSmallMediumCraters`, `enableLargeCraters`, `enableHugeCraters` и по отдельному включателю на каждый тип (`enableSmallCrater` … `enableOldHugeCrater`, `enableSouthPoleAitkenBasin`), `craterRarity` (по умолчанию 20, диапазон 1–1000; «больше — реже»).

**Наши правки** ([compat.md](../compat.md)):
- `patches/northstar_structures_fix.py <папка с jar>` → `pack/kubejs/data/northstar/worldgen/biome/*` (7 лунных биомов под Redux 0.6: фичи `northstar:moon_<камень>_blob` переименованы в `moon_blob_<камень>`, иначе мир не создавался — `Unbound values in registry placed_feature`) и `pack/kubejs/data/northstar_structures/*` (`old_small/medium/large/huge_crater`, `south_pole_aitken_basin` с песком и камнем Ad Astra; тег биомов с `ad_astra:lunar_wastelands`). После обновления аддона или Northstar скрипт перезапускают.
- `mods-src/mm-compat` 0.5.3, миксин `NorthstarCraterJoinMixin`: аддон на каждом событии `EntityJoinLevelEvent` удалял любое не-игровое существо в «защищённых кратерах» (круги радиусом 150–250 блоков и бассейн Эйткена) **без проверки измерения**, поэтому в Обычном мире мобы не появлялись. Теперь обработчик пропускается везде, кроме Луны (`northstar:moon`, `ad_astra:moon`). Подробности — [compat.md](../compat.md), «Northstar Structures удаляло всех существ…».

## Creating Space

<!-- auto:start card:creating-space -->
<a id="creating-space"></a>
- **Версия:** 1.20.1_1.7.13 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/creating-space)
- **Что это:** Ракеты на Create с настоящей инженерией: двигатель проектируется по чертежу, топливные пары и дельта-V ограничивают маршрут. Даёт двигатели, ракеты и скафандры; в сборке маршруты ведут по планетам Ad Astra. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (10), [galacticraft.md](../galacticraft.md) (3), [client-checklist.md](../client-checklist.md) (2), [dedup.md](../dedup.md) (1), [roadmap.md](../roadmap.md) (1), [version-choice.md](../version-choice.md) (1)
<!-- auto:end card:creating-space -->

### Как играть

Creating Space (MIT; автор Real Ant Engineer) — ракеты на Create, где важна «настоящая» инженерия: **двигатель проектируется** (тяга, смесь топлива, тип насосной и выхлопной части), а полёт ограничен **дельта-V (Δv)** — запасом скорости. В сборке он даёт ракеты, двигатели и скафандры; планеты — Ad Astra ([compat.md](../compat.md), «Одна Солнечная система»): граф перелётов Creating Space перенаправлен в измерения `ad_astra:*`.

**Путь ракеты**
1. Стол инженера ракет (`rocket_engineer_table`) создаёт **чертёж двигателя** (`engine_blueprint`): выбираются топливная пара, тип силового и выхлопного блока, размер и тяга; в окне видны ISP, масса, температура, давление, уровень материала. Чертёж дублируется (`engine_recipes/duplicate_blueprint`) и по нему собирается двигатель (`rocket_engine`, малый `small_rocket_engine`, большой `big_rocket_engine`) из деталей — турбина, инжектор, сетка инжектора, камера сгорания, рёбра, каналы, трубы: чем лучше топливо, тем более жаропрочный металл нужен.
2. Корпус: `rocket_casing`, баки (Create, `cryogenic_tank` — переносной бак/ведро для холодных жидкостей), топливо сжижается (`air_liquefier`, `mechanical_electrolyzer`) и подаётся в двигатель. `starter_charge` — взрывной заряд, нужный для запуска `rocket_generator`; `clamps` удерживают контрапцию, `flight_recorder` после неудачи показывает причину (нужны очки инженера): «not enough thrust», «wrong ratio of propellant», «not enough propellant».
3. В **Rocket Controls** (`rocket_controls`) выбирается цель и координаты входа, показывается **«ΔV needed»**; «Assemble» собирает ракету, «Launch» запускает. Нужно: тяга больше веса и хватает топлива на Δv маршрута.

**Топливные пары** (данные `propellant_type`): метан/кислород `methalox` (ISP до 460 с; жидкий кислород 2 : метан 1), водород/кислород `lh2lox` (до 540 с; кислород 6 : водород 1), металлический водород `metalic_hydrogen` (до 6000 с, «задел на будущее»). Силовые блоки: открытый цикл, ступенчатый с избытком топлива/окислителя, полный поток; выхлопные: колокольное сопло, aerospike.

**Маршруты и Δv** (наши данные после `patches/space_unify.py`; узел → куда, Δv; гравитация узла в скобках):

| Из | В (Δv) |
|---|---|
| Земля `minecraft:overworld` (9,81) | орбита Земли 1500 |
| Орбита Земли `ad_astra:earth_orbit` (0) | Земля 200 · орбита Луны 600 · орбита Марса 1000 · Венера 2200 · орбита Меркурия 6500 · орбита Глацио 20000 |
| Орбита Луны `ad_astra:moon_orbit` | Луна 300 · орбита Земли 200 · орбита Марса 400 · Венера 1600 |
| Луна `ad_astra:moon` (1,6) | орбита Луны 300 |
| Орбита Марса `ad_astra:mars_orbit` | Марс 300 · орбита Луны 300 · орбита Земли 500 |
| Марс `ad_astra:mars` (3,71) | орбита Марса 400 |
| Венера `ad_astra:venus` (8,87) | орбита Луны 1800 · орбита Земли 2000 |
| Орбита Меркурия `ad_astra:mercury_orbit` | Меркурий 1500 · орбита Земли 6500 |
| Меркурий `ad_astra:mercury` (3,7) | орбита Меркурия 1500 |
| Орбита Глацио `ad_astra:glacio_orbit` | Глацио 1200 · орбита Земли 20000 |
| Глацио `ad_astra:glacio` (3,721) | орбита Глацио 1200 |

Меркурий и Глацио (межзвёздный перелёт — самый дорогой по Δv) мы добавили сами; у Венеры исправлена гравитация (в оригинале 1,6 → 8,87). Узлы Creating Space `creatingspace:*` оставлены пустыми.

**Кислород.** Мод проверяет воздух только в своих биомах (тег `creatingspace:no_oxygen`: луна, Марс, Венера Creating Space и `creatingspace:space`), в мирах Ad Astra действует **одна система — Ad Astra**. Скафандр Creating Space (обычный: шлем/штаны/ботинки + `copper_oxygen_backtank`; улучшенный: `advanced_spacesuit_*` + `netherite_oxygen_backtank`) работает там через `mm-compat`: воздух из баллона (NBT `Oxygen`) тратится 1 ед./с, пустой — удушье. **Oxygen Sealer** (`creatingspace:oxygen_sealer`) наполняет помещение, но в мирах Ad Astra воздух определяет Ad Astra, поэтому надёжнее его распылитель кислорода (поведение герметизатора Creating Space в этих мирах не проверялось). Температуру скафандры дают через теги (`freeze_resistant_armor`, `heat_resistant_armor`: улучшенный — и от жары).

### Предметы/блоки

| Группа | Id (`creatingspace:`) |
|---|---|
| Двигатель | `rocket_engineer_table`, `design_blueprint`, `engine_blueprint`, `rocket_engine`, `small_rocket_engine`, `big_rocket_engine`, `power_pack`, `exhaust_pack`, `injector`, `reinforced_injector`, `combustion_chamber`, `bell_nozzle`, `aerospike_plug`, `copper_coil`, `basic_catalyst`, `sturdy_propeller` |
| Ракета | `rocket_controls`, `rocket_casing`, `rocket_generator`, `starter_charge`, `clamps`, `flight_recorder`, `catalyst_carrier`, `cryogenic_tank` |
| Химия | `air_liquefier`, `mechanical_electrolyzer`, `chemical_synthesizer` (устарел), `flow_meter`, `oxygen_sealer` |
| Скафандры | `basic_spacesuit_*`, `copper_oxygen_backtank`, `advanced_spacesuit_*`, `netherite_oxygen_backtank`, ткани `basic_spacesuit_fabric`/`advanced_spacesuit_fabric` |
| Металлы деталей | андезит, железо, медь, латунь, усиленная медь, мельхиор (`copronickel`), монель, инконель, хастеллой — слитки, листы, турбины, лопатки (`blisk`), трубы |
| Руды Луны | никель (`nickel_*`), кобальт (`cobalt_*`), алюминий (`aluminum_*`), сульфат никеля (кристаллы `crystal_*`), реголит Луны/Марса |
| Жидкости | `liquid_methane`, `liquid_oxygen`, `liquid_hydrogen`, `liquid_co2` |
| Прочее | `space_food` |

### Мобы

Своих мобов нет. Тег `#creatingspace:space_creatures` содержит скелета (`minecraft:skeleton`) — ему вакуум нипочём.

### События

- Смерть без кислорода в мире Creating Space: «`%s` didn't find oxygen in space» (урон `creatingspace:no_oxygen`).
- Падение ракеты и неудачный запуск разбираются по `flight_recorder` (причины выше).

### Данжи/структуры (/locate)

| Структура | Id | Биом |
|---|---|---|
| Заброшенный лунный аванпост | `creatingspace:abandoned_moon_outpost` | `creatingspace:moon_plains`, шаг 80 / отступ 16 |
| Разбившаяся ракета | `creatingspace:crashed_rocket` | `creatingspace:moon_plains`, шаг 20 / отступ 6 |

Лут разбившейся ракеты (`creatingspace:crashed_rocket_loot`) — чертежи дизайна (`design_blueprint`) и никель; аванпост — `abandoned_moon_base_loot`. Биом `creatingspace:moon_plains` есть только в собственном измерении Creating Space, а все полёты в сборке ведут на Луну Ad Astra (`lunar_wastelands`), поэтому **эти структуры в игре, скорее всего, не встречаются** — вывод по данным, игрой не проверялся.

### Конфиги

Собственного конфига в `pack/config` нет. Наши правки ([compat.md](../compat.md)):
- `patches/space_unify.py` → `pack/kubejs/data/ad_astra/creatingspace/rocket_accessible_dimension/*` (узлы Ad Astra, включая новые Меркурий и Глацио), `pack/kubejs/data/creatingspace/creatingspace/rocket_accessible_dimension/{moon_orbit,mars_orbit}.json` и аналоги для старых узлов (пустые связи), `pack/kubejs/data/minecraft/creatingspace/rocket_accessible_dimension/overworld.json`.
- `patches/ore_crossgen.py` → никель, кобальт и алюминий Creating Space генерируются на Луне Ad Astra (`pack/kubejs/data/mm_space/worldgen/placed_feature/moon/creatingspace_*`).
- `pack/config/almostunified/unify.json` — три никелевые руды Creating Space исключены из объединения (ошибочные теги); `pack/kubejs/server_scripts/missing_mod_refs.js` — убрана недостающая ссылка `creatingspace:reinforced_copper_nickel` из тега `forge:nuggets/reinforced_copper`.
- `mods-src/mm-compat` (`SuitOxygen`) — воздух скафандров (теги `creatingspace:spacesuit`, `creatingspace:oxygen_sources`) в системе Ad Astra.
- Проверки: `scripts/tests/routecheck.js`, `rocketcheck.js`, `gravitycheck.js`, `oxygen_commands.txt`.

## Mekanism x Create: Northstar

<!-- auto:start card:mekanism-x-create-northstar -->
<a id="mekanism-x-create-northstar"></a>
- **Версия:** 1.20.1-1.4.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-x-create-northstar)
- **Что это:** Мост между Mekanism и Northstar: модули МекаСкафандра (космический костюм, кислородный баллон, утепление, защита от жары) и материалы к ним (изоляция, дюрасталь). _(источник: docs сборки)_
<!-- auto:end card:mekanism-x-create-northstar -->

### Как играть

Мост Mekanism ↔ Create: Northstar (автор Wolfieboy09, All Rights Reserved): добавляет **модули для МекаСкафандра**, которые превращают его в космический скафандр Northstar, и материалы для них. Модули ставятся в МекаСкафандр как обычные модули Mekanism (`mekanism:module_base` в основе рецептов). Что именно работает на планетах Ad Astra (кислород там считает Ad Astra, а скафандр Northstar — через `mm-compat` по тегам `northstar:oxygen_sealing`/`oxygen_sources`), в игре **не проверялось**.

| Модуль (`mek_x_star:`) | Что делает (описание мода) | Рецепт |
|---|---|---|
| `module_space_suit` — Space Suit Unit | превращает МекаСкафандр в космический скафандр | механическая крафт-сборка Create 5×4 (атомные сплавы, изоляция, лист HDPE, цепи ultimate, сапфир, железные пластины) |
| `module_oxygen_tank` — Oxygen Tank Unit | кислородный баллон в нагруднике | основа — незеритовый баллон Create (`create:netherite_backtank`), железные пластины, изоляция |
| `module_oxygen_tank_expansion` — Oxygen Tank Expansion Unit | увеличивает ёмкость баллона | собирается через последовательную сборку Create (`incomplete_oxygen_tank_expansion`) |
| `module_space_suit_insulation` — Space Suit Insulation Unit | утепление, чтобы не замёрзнуть | изоляция, лист HDPE, железные пластины |
| `module_heat_resistive` — Heat Resistant Unit | защита от жары горячих планет | `heat_insulation`, лист HDPE, железные пластины |

Материалы: `insulation` (смешивание Create: 2 дроблёной шерсти + 2 дроблёной сырой меди + 1 дроблёного сырого железа), `crushed_wool` (дробление шерсти и ковров), `heat_insulation` (лист мартианской стали Northstar + изоляция + огненный порошок), `durasteel` (Mekanism: стальной слиток + углерод, 40), `pressurized_durasteel` (реакция: титантетрахлорид 50 + литий 100 + дюрасталь).

### Предметы/блоки

Предметы `mek_x_star:`: `insulation`, `heat_insulation`, `crushed_wool`, `durasteel`, `pressurized_durasteel`, `incomplete_oxygen_tank_expansion`, пять модулей (таблица выше). Блоков и мобов нет. Теги: `mek_x_star:insulating` (изоляция), `mek_x_star:heat_resistive` (термоизоляция), `forge:ingot/durasteel`.

### Конфиги

Конфига и наших правок нет: мод в сборке «как есть». Отдельной совместимости с Ad Astra у него нет — для неё сборка использует Giselle Addon (модули МекаСкафандра «дыхание в космосе» и др.) и `mm-compat`.

## Mekanism: Ad Astra Ores

<!-- auto:start card:mekanism-ad-astra-ores -->
<a id="mekanism-ad-astra-ores"></a>
- **Версия:** 1.1.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-ad-astra-ores)
- **Что это:** Руды Mekanism на планетах Ad Astra. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (32), [dedup.md](../dedup.md) (23), [jei.md](../jei.md) (10), [ntm.md](../ntm.md) (7), [galacticraft.md](../galacticraft.md) (4), [friends.md](../friends.md) (3)
<!-- auto:end card:mekanism-ad-astra-ores -->

### Что даёт игроку

Mekanism: Ad Astra Ores (`mekanismaaa` 1.1.0; зависит от Ad Astra и Mekanism) — 16 блоков руд Mekanism в камне планет Ad Astra: `mekanismaaa:<планета>_<металл>_ore`.

| Планета (камень) | Руды |
|---|---|
| Луна (`ad_astra:moon_stone`) | осмий, олово |
| Марс (`ad_astra:mars_stone`) | флюорит, олово, уран |
| Венера | флюорит, свинец, уран |
| Меркурий | осмий, свинец, уран |
| Глацио (`ad_astra:glacio_stone`) | флюорит, свинец, осмий, олово, уран |

Руды встают в камень соответствующей планеты (признак — совпадение блока `…_stone`); генерация задана 10 `placed_feature` (`ore_osmium_upper/middle/small`, `ore_tin_large/small`, `ore_lead_normal`, `ore_uranium_small/buried`, `ore_fluorite_normal/buried`) и биом-модификаторами `gen_ores` / `gen_ores_deepslate`; количество и высоты берутся из настроек руд Mekanism (`configurable`). Добыча и переработка — как у обычных руд [Mekanism](tech.md#mekanism); в Almost Unified (`pack/config/almostunified/`) металлы Mekanism объединяются с остальными ([compat.md](../compat.md)). Конфигов мода в `pack/config` нет.

<!-- cards:end -->
