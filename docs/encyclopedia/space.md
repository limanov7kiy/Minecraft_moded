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

_Обзор категории — вручную, следующие этапы._

---

<!-- cards:start -->
## Ad Astra

<!-- auto:start card:ad-astra -->
<a id="ad-astra"></a>
- **Версия:** 1.15.21 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ad-astra)
- **Что это:** Live long and prosper, Ad Astra! _(источник: Modrinth)_
- **В наших docs:** [galacticraft.md](../galacticraft.md) (25), [compat.md](../compat.md) (20), [roadmap.md](../roadmap.md) (7), [performance.md](../performance.md) (3), [client-checklist.md](../client-checklist.md) (2), [russification.md](../russification.md) (2)
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

### Команды/гамерулы

Собственных команд у Ad Astra не найдено. Отключать механики можно только настройками (ниже).

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
- **Что это:** Add things be more convenience, compatibility for Ad Astra! _(источник: Modrinth)_
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
- **Что это:** Adds recipes to make Create and Ad Astra more compatible _(источник: Modrinth)_
<!-- auto:end card:create-ad-astra-recipes -->

### Как играть

_Пока пусто — заполнит один из следующих этапов._

### Предметы/блоки

_Пока пусто — заполнит один из следующих этапов._

### Мобы

_Пока пусто — заполнит один из следующих этапов._

### Эффекты

_Пока пусто — заполнит один из следующих этапов._

### События

_Пока пусто — заполнит один из следующих этапов._

### Данжи/структуры (/locate)

_Пока пусто — заполнит один из следующих этапов._

### Чары

_Пока пусто — заполнит один из следующих этапов._

### Команды/гамерулы

_Пока пусто — заполнит один из следующих этапов._

### Конфиги

_Пока пусто — заполнит один из следующих этапов._

## Create: Northstar - Redux

<!-- auto:start card:northstar-redux -->
<a id="northstar-redux"></a>
- **Версия:** 0.6.6+1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/northstar-redux)
- **Что это:** Space exploration addon for Create. _(источник: Modrinth)_
- **В сборке:** порт Create: Northstar под Create 6 (оригинал несовместим)
- **В наших docs:** [compat.md](../compat.md) (52), [galacticraft.md](../galacticraft.md) (15), [roadmap.md](../roadmap.md) (11), [version-choice.md](../version-choice.md) (10), [russification.md](../russification.md) (8), [performance.md](../performance.md) (7)
<!-- auto:end card:northstar-redux -->

### Как играть

_Пока пусто — заполнит один из следующих этапов._

### Предметы/блоки

_Пока пусто — заполнит один из следующих этапов._

### Мобы

_Пока пусто — заполнит один из следующих этапов._

### Эффекты

_Пока пусто — заполнит один из следующих этапов._

### События

_Пока пусто — заполнит один из следующих этапов._

### Данжи/структуры (/locate)

_Пока пусто — заполнит один из следующих этапов._

### Чары

_Пока пусто — заполнит один из следующих этапов._

### Команды/гамерулы

_Пока пусто — заполнит один из следующих этапов._

### Конфиги

_Пока пусто — заполнит один из следующих этапов._

## Create: Northstar - Structures

<!-- auto:start card:create-northstar-structures -->
<a id="create-northstar-structures"></a>
- **Версия:** 1.0.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/create-northstar-structures)
- **Что это:** An addon that adds structures to the planets of Northstar Redux. _(источник: Modrinth)_
- **В сборке:** биомы Луны под Redux 0.6+ и кратеры на Луне Ad Astra: patches/northstar_structures_fix.py
- **В наших docs:** [compat.md](../compat.md) (27), [version-choice.md](../version-choice.md) (9), [performance.md](../performance.md) (7), [russification.md](../russification.md) (6), [roadmap.md](../roadmap.md) (5), [russification-glossary.md](../russification-glossary.md) (4)
<!-- auto:end card:create-northstar-structures -->

### Как играть

_Пока пусто — заполнит один из следующих этапов._

### Предметы/блоки

_Пока пусто — заполнит один из следующих этапов._

### Мобы

_Пока пусто — заполнит один из следующих этапов._

### Эффекты

_Пока пусто — заполнит один из следующих этапов._

### События

_Пока пусто — заполнит один из следующих этапов._

### Данжи/структуры (/locate)

_Пока пусто — заполнит один из следующих этапов._

### Чары

_Пока пусто — заполнит один из следующих этапов._

### Команды/гамерулы

_Пока пусто — заполнит один из следующих этапов._

### Конфиги

_Пока пусто — заполнит один из следующих этапов._

## Creating Space

<!-- auto:start card:creating-space -->
<a id="creating-space"></a>
- **Версия:** 1.20.1_1.7.13 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/creating-space)
- **Что это:** Creating Space is a mod that allow you to create rocket using contraption and go to other planets. _(источник: Modrinth)_
- **В наших docs:** [compat.md](../compat.md) (10), [galacticraft.md](../galacticraft.md) (3), [client-checklist.md](../client-checklist.md) (2), [roadmap.md](../roadmap.md) (1), [version-choice.md](../version-choice.md) (1)
<!-- auto:end card:creating-space -->

### Как играть

_Пока пусто — заполнит один из следующих этапов._

### Предметы/блоки

_Пока пусто — заполнит один из следующих этапов._

### Мобы

_Пока пусто — заполнит один из следующих этапов._

### Эффекты

_Пока пусто — заполнит один из следующих этапов._

### События

_Пока пусто — заполнит один из следующих этапов._

### Данжи/структуры (/locate)

_Пока пусто — заполнит один из следующих этапов._

### Чары

_Пока пусто — заполнит один из следующих этапов._

### Команды/гамерулы

_Пока пусто — заполнит один из следующих этапов._

### Конфиги

_Пока пусто — заполнит один из следующих этапов._

## Mekanism x Create: Northstar

<!-- auto:start card:mekanism-x-create-northstar -->
<a id="mekanism-x-create-northstar"></a>
- **Версия:** 1.20.1-1.4.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-x-create-northstar)
- **Что это:** Compatibility between Mekanism and Create: Northstar _(источник: Modrinth)_
<!-- auto:end card:mekanism-x-create-northstar -->

### Как играть

_Пока пусто — заполнит один из следующих этапов._

### Предметы/блоки

_Пока пусто — заполнит один из следующих этапов._

### Мобы

_Пока пусто — заполнит один из следующих этапов._

### Эффекты

_Пока пусто — заполнит один из следующих этапов._

### События

_Пока пусто — заполнит один из следующих этапов._

### Данжи/структуры (/locate)

_Пока пусто — заполнит один из следующих этапов._

### Чары

_Пока пусто — заполнит один из следующих этапов._

### Команды/гамерулы

_Пока пусто — заполнит один из следующих этапов._

### Конфиги

_Пока пусто — заполнит один из следующих этапов._

## Mekanism: Ad Astra Ores

<!-- auto:start card:mekanism-ad-astra-ores -->
<a id="mekanism-ad-astra-ores"></a>
- **Версия:** 1.1.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-ad-astra-ores)
- **Что это:** Adds Mekanism ores on Ad Astra planets. _(источник: Modrinth)_
- **В наших docs:** [compat.md](../compat.md) (16), [jei.md](../jei.md) (10), [galacticraft.md](../galacticraft.md) (4), [orespawn.md](../orespawn.md) (3), [roadmap.md](../roadmap.md) (3), [russification-audit.md](../russification-audit.md) (3)
<!-- auto:end card:mekanism-ad-astra-ores -->

### Как играть

_Пока пусто — заполнит один из следующих этапов._

### Предметы/блоки

_Пока пусто — заполнит один из следующих этапов._

### Мобы

_Пока пусто — заполнит один из следующих этапов._

### Эффекты

_Пока пусто — заполнит один из следующих этапов._

### События

_Пока пусто — заполнит один из следующих этапов._

### Данжи/структуры (/locate)

_Пока пусто — заполнит один из следующих этапов._

### Чары

_Пока пусто — заполнит один из следующих этапов._

### Команды/гамерулы

_Пока пусто — заполнит один из следующих этапов._

### Конфиги

_Пока пусто — заполнит один из следующих этапов._

<!-- cards:end -->
