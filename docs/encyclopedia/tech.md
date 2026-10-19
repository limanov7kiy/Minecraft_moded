# Техника и автоматизация

Create, Mekanism, AE2, Refined Storage, корабли Valkyrien Skies.

<!-- auto:start toc -->
Модов в категории: **20**.

- [Applied Energistics 2](#applied-energistics-2)
- [Create](#create)
- [Create Crafts & Additions](#create-crafts--additions)
- [Create Deco](#create-deco)
- [Create Stuff 'N Additions](#create-stuff-n-additions)
- [Create: Copycats+](#create-copycats)
- [Create: Propulsion](#create-propulsion)
- [Create: Steam 'n' Rails](#create-steam-n-rails)
- [Eureka! Ships! for Valkyrien Skies (Forge/Fabric)](#eureka-ships-for-valkyrien-skies-forgefabric)
- [Mekanism](#mekanism)
- [Mekanism Additions](#mekanism-additions)
- [Mekanism Covers](#mekanism-covers)
- [Mekanism Curios](#mekanism-curios)
- [Mekanism Generators](#mekanism-generators)
- [Mekanism Tools](#mekanism-tools)
- [Mekanism:More Machine](#mekanismmore-machine)
- [Refined Storage](#refined-storage)
- [Valkyrien Sails](#valkyrien-sails)
- [Valkyrien Skies](#valkyrien-skies)
- [Valkyrien Skies: Tournament Reforged](#valkyrien-skies-tournament-reforged)

Вернуться: [оглавление справочника](INDEX.md).
<!-- auto:end toc -->

## Общее по категории

_Обзор категории — вручную, следующие этапы._

---

<!-- cards:start -->
## Applied Energistics 2

<!-- auto:start card:ae2 -->
<a id="ae2"></a>
- **Версия:** 15.4.11 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ae2)
- **Что это:** Популярная автоматизация и хранение: сеть, МЭ-диски, автокрафт. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
- **В наших docs:** [compat.md](../compat.md) (2), [roadmap.md](../roadmap.md) (2)
<!-- auto:end card:ae2 -->

### Как играть

Applied Energistics 2 15.4.11 (TeamAppliedEnergistics) — «цифровое» хранение и автокрафт: все предметы живут в сети, которой управляют с терминала. **Требует GuideME** (не объявлен на Modrinth, добавлен вручную — [compat.md](../compat.md)). Руководство по предметам — внутриигровой `ae2:guide` (клавиша «Open Guide for Items»); достижения (ветка «Root») ведут по шагам.
1. **Добыть кварц**: метеориты (см. структуры) дают небесный камень (`sky_stone_block`) и Certus Quartz; кристаллы растут на `flawless/flawed/chipped/damaged_budding_quartz` (почки `small/medium/large_quartz_bud`, `quartz_cluster`); `charger` (+ `crank`) заряжает `charged_certus_quartz_crystal`; `fluix_crystal` и `fluix_dust` — материалы для кабелей и терминалов.
2. **Процессоры**: `inscriber` с прессами (`calculation_processor_press`, `engineering_processor_press`, …) → `calculation_processor`, `engineering_processor`.
3. **Сеть**: `controller` (ME Controller) питает сеть, кабели `*_glass_cable`, `*_covered_cable`, `*_smart_cable`, `*_dense_cable` (16 цветов + флюикс), энергия — `energy_cell`, `dense_energy_cell`, `vibration_chamber`.
4. **Хранение**: `drive` (ME Drive) + ячейки `item_storage_cell_1k…256k`, `fluid_storage_cell_*`, `cell_workbench`; терминалы (`crafting_terminal` и др.), портативные ячейки (клавиши «Open Portable Item/Fluid Cell»), беспроводной терминал (`wireless_access_point`, клавиша «Open Wireless Terminal»).
5. **Автокрафт**: `crafting_unit` + `crafting_storage` (1k…256k) + `crafting_accelerator` (ко-процессор), `pattern_provider`, `molecular_assembler`, `blank_pattern` → `crafting_pattern`; шины `import_bus`, `export_bus`, `storage_bus`, `annihilation_plane`, `formation_plane`.
6. **Телепорт и пространство**: `quantum_link` + `quantum_ring` (квантовый мост), `spatial_io_port` + `spatial_pylon` (пространственные ячейки, отдельное измерение).

### Предметы/блоки

| id (`ae2:<id>`) | Что делает |
|---|---|
| `controller` | Контроллер сети (питание, ограничение размера) |
| `drive`, `chest` (ME Chest), `io_port` | Подключение и перенос ячеек |
| `interface`, `pattern_provider`, `molecular_assembler` | Подача предметов в машины и автосборка |
| `crafting_terminal`, `conversion_monitor`, `crafting_monitor` | Терминалы и мониторы |
| `annihilation_plane`, `formation_plane` | Разрушение/размещение блоков и предметов |
| `quantum_link`, `quantum_ring`, `spatial_io_port`, `spatial_pylon` | Квантовый мост и пространственное хранилище |
| `entropy_manipulator` | Энтропийный манипулятор: меняет состояние блоков |
| `network_tool`, `certus_quartz_wrench`, `color_applicator` | Инструменты: сетевой тулкит, ключ, окраска кабелей (`*_paint_ball`) |
| `facade`, `cable_anchor`, `*_p2p_tunnel` | Фасады и P2P-туннели (предметы, жидкости, FE) |
| `fluix_*` инструменты, `certus_quartz_*` инструменты | Набор инструментов из кварца и флюикса |
| `tiny_tnt` | Крошечный динамит (сущность `tiny_tnt_primed`) |

Предметы «Dev.*» (`debug_*`) — отладочные, игрокам не нужны.

### Мобы

Профессия жителя «Fluix Researcher» (`ae2:fluix_researcher`) — единственная «живая» добавка AE2; сущность `ae2:tiny_tnt_primed` — взведённый крошечный динамит.

### Данжи/структуры (/locate)

- `ae2:meteorite` — метеорит; `random_spread`, spacing 32, separation 8 чанков: `/locate structure ae2:meteorite`. Метеорит несёт небесный камень (`sky_stone_block`), из которого делают часть блоков AE2.
- Измерение `ae2:spatial_storage` (биом `ae2:spatial_storage`) — отдельное пространство под пространственные ячейки (`spatial_io_port`).

### Команды/гамерулы

Корневая команда `/ae2` (`/ae2 list`, `/ae2 help <команда>`). Среди подкоманд — включение/выключение журнала чанков (`ChunkLogger`, для отладки загрузки).

### Конфиги

Файла `ae2/…` в `pack/config` нет — настройки по умолчанию. Истинный кварц и флюикс AE2 дают только новые материалы, Almost Unified их не трогает ([compat.md](../compat.md)); в JEI виден `ae2:charger` через ванильную книгу рецептов ([jei.md](../jei.md)). Botanical Machinery Extra хотели добавить, но он тянет AE2 как обязательную зависимость ([roadmap.md](../roadmap.md)).

## Create

<!-- auto:start card:create -->
<a id="create"></a>
- **Версия:** mc1.20.1-6.0.8 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/create)
- **Что это:** Эстетичная техника: механизмы на оборотах, шестерни, конвейеры, поезда. _(источник: docs сборки)_
<!-- auto:end card:create -->

### Как играть

Create 6.0.8 (автор simibubi; зависимости по `mods.toml` — Flywheel и Ponder) — основа всей «техники» сборки: большинство аддонов этой страницы и страницы [Космос и ракеты](space.md) строятся на нём. Внутриигровые анимации-подсказки (Ponder) есть почти у каждого блока; в подсказке предмета есть строка «Hold [клавиша] for Summary» (краткое описание), рецепты показывает JEI ([jei.md](../jei.md)).
Порядок освоения:
1. **Вращение.** Источник (ручной `hand_crank`, `water_wheel` / `large_water_wheel`, ветряк `windmill_bearing` + `sail_frame`, `steam_engine`, `creative_motor`) передаёт обороты по `shaft`, `cogwheel`, `large_cogwheel`, `gearbox`, `clutch`, `gearshift`. Скорость и нагрузку показывают `speedometer` и `stressometer`; очки `goggles` выводят эти числа на блок. Базовый материал — `andesite_alloy`, затем латунь (`brass_ingot`) и `precision_mechanism`.
2. **Обработка.** `crushing_wheel`, `millstone`, `mechanical_press`, `mechanical_mixer` + `basin`, `encased_fan` (обработка потоком), `spout`, `deployer`, `mechanical_saw`; сборка по шагам — «сборочная линия» (предметы `incomplete_*`, `sand_paper`), автокрафт — `mechanical_crafter`, роботизированная рука — `mechanical_arm`.
3. **Жидкости и склад.** `fluid_pipe`, `mechanical_pump`, `fluid_tank`, `hose_pulley`, `item_vault`, `chute`, воронки/туннели. Логистика Create 6: `packager`, `package_frogport`, `stock_ticker`, `stock_link`, `redstone_requester`, `factory_gauge`, `chain_conveyor`, предмет `package`.
4. **Контрапции и транспорт.** `mechanical_bearing`, `mechanical_piston`, `gantry_carriage`, `cart_assembler`, `rope_pulley`, `elevator_pulley`; поезда — `track`, `track_station`, `track_signal`, `controls`, `schedule`, `train_door`.
5. **Строительство.** `schematic_table` + `schematicannon` строят по чертежу (`empty_schematic`, `schematic`), `wand_of_symmetry`, `handheld_worldshaper`, `clipboard`.

### Предметы/блоки

| id (`create:<id>`) | Что делает |
|---|---|
| `wrench` | Гаечный ключ: поворот и разборка механизмов |
| `goggles` | Очки инженера: показ скорости и нагрузки |
| `super_glue` | Суперклей: соединяет блоки в контрапции |
| `extendo_grip` | Выдвижная рука (увеличенная дальность взаимодействия) |
| `copper_backtank`, `netherite_backtank` | Баллон со сжатым воздухом; с `copper_diving_helmet` + `copper_diving_boots` (и netherite-версиями) — снаряжение для подводных работ |
| `potato_cannon` | Картофельная пушка (боевой «снаряд» из еды) |
| `linked_controller` | Беспроводной пульт для блоков Create (`transmitter`, link-блоки) |
| `blaze_burner` | Горелка из ифрита: нагрев для миксера и котла |
| `sequenced assembly`: `incomplete_precision_mechanism`, `incomplete_track` | Промежуточные предметы многошаговых рецептов |
| `cardboard_*` | Картонная броня и меч, блоки картона (`cardboard_block`, `bound_cardboard_block`) |
| `copycat_base`, `copycat_panel`, `copycat_step`, `copycat_bars` | Блоки, копирующие вид другого блока (см. также [Copycats+](#copycats)) |
| `copper_*`, `*_casing`, `*_window`, `*_pillar`, `cut_*`, `layered_*` | Большой набор декоративных блоков: корпуса, окна, колонны, «обработанный» камень (асурин, кримзит, охрум, веридий и др.) |
| `display_board`, `display_link`, `nixie_tube` | Табло и трубки Никси, управляемые Display Link |

Руды Create: `create:zinc_ore` и `create:deepslate_zinc_ore` (цинк, `raw_zinc` → `zinc_ingot`); дробление даёт `crushed_raw_*` для всех металлов. Объединение дублей металлов — Almost Unified: приоритет `create` выше Mekanism ([compat.md](../compat.md), «Объединение дублей»).

### Данжи/структуры (/locate)

Своих структур у Create нет (в jar только тест-структуры). Генерация мира — руда `create:zinc_ore` (`count` 8 на чанк, высоты −63…70) и декоративные «полосатые» слои `create:striated_ores_overworld` / `create:striated_ores_nether` (слои асурина, кримзита, охрума, веридия и т. п. внутри камня).

### Чары

- `create:capacity` — Capacity: увеличивает ёмкость воздуха в баллоне (Backtank).
- `create:potato_recovery` — Potato Recovery: шанс повторно использовать снаряд картофельной пушки.

### Команды/гамерулы

- `/killtps start <tickTime>` и `/killtps stop` — искусственное замедление тика сервера для тестов (по сообщениям в lang).
- `/create overlay reset` — сбросить положение оверлеев Create на экране (клиент).
- В lang есть команда сбора отладочной информации в буфер обмена (`create.command.debuginfo`).

### Конфиги

Файлов конфигурации Create в `pack/config` нет — действуют значения по умолчанию. Из наших заметок: Flywheel 1.0.5, бэкенд `DEFAULT`, ошибок рендера не видно ([performance.md](../performance.md)); Create заменяет несовместимый оригинал Northstar портом Northstar Redux ([compat.md](../compat.md)). Чек-лист проверки — [client-checklist.md](../client-checklist.md).

## Create Crafts & Additions

<!-- auto:start card:createaddition -->
<a id="createaddition"></a>
- **Версия:** forge-1.20.1-1.3.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/createaddition)
- **Что это:** Create Crafts & Additions: мост между электричеством и кинетической энергией. _(источник: docs сборки)_
- **В наших docs:** [jei.md](../jei.md) (3), [compat.md](../compat.md) (1), [russification-audit.md](../russification-audit.md) (1)
<!-- auto:end card:createaddition -->

### Как играть

Аддон (MRH0) добавляет к Create **электричество**: вращение превращается в FE и обратно, провода соединяют установки на расстоянии.
1. `alternator` превращает вращение в энергию (FE), `electric_motor` — энергию во вращение; оба нужны для связи Create с любыми FE-совместимыми модами (Mekanism, AE2 и др.).
2. Провода: `connector` (малый) и `large_connector`; катушки `copper_spool` (низкий ток), `gold_spool` и `electrum_spool` (высокий ток). ПКМ катушкой по двум коннекторам создаёт линию; гаечным ключом режим коннектора переключается Push / Pull / None (по подсказкам в lang).
3. Накопление: `capacitor` (портативный), `accumulator` (помечен в lang как устаревший), `modular_accumulator`; `portable_energy_interface` заряжает контрапции.
4. Обработка: `rolling_mill` (прокатный стан: стержни, провода, листы), `diamond_grit_sandpaper`; металлы — `electrum_ingot`, `electrum_sheet`, стержни `copper_rod`, `iron_rod`, `gold_rod`, `brass_rod`, `bronze_rod`.
5. Топливо: `biomass`, `biomass_pellet`, `seed_oil`, `bioethanol`, `liquid_blaze_burner`.

### Предметы/блоки

| id (`createaddition:<id>`) | Что делает |
|---|---|
| `electric_motor`, `alternator` | Мотор (энергия → вращение) и генератор (вращение → энергия) |
| `connector`, `large_connector`, `small_light_connector` | Коннекторы проводов; «светящийся» вариант светит при подаче энергии |
| `redstone_relay` | Реле редстоуна для линий |
| `tesla_coil` | Катушка Теслы: бьёт по существам и накладывает эффект «Шок» |
| `digital_adapter` | Цифровой адаптер (интерфейс для компьютерных модов; в сборке не проверялся) |
| `barbed_wire` | Колючая проволока |
| `creative_energy` | Креативный генератор (бесконечный FE) |
| `electrum_amulet` | «Амулет из бледного золота» (удача, подсказка в lang) |
| `honey_cake`, `chocolate_cake`, `copper_goblet`, `gold_goblet`, `brass_figurine` | Декор и еда |

### Эффекты

- `createaddition:shocking` — «Shocked»: сильно снижает скорость передвижения (от катушки Теслы).

### Конфиги

Конфигурации аддона в `pack/config` нет — по умолчанию. JEI-категории аддона: `charging`, `liquid_burning`, `rolling` ([jei.md](../jei.md)). Приоритет Almost Unified: `createaddition` идёт после `create` и `mekanism` ([compat.md](../compat.md)).

## Create Deco

<!-- auto:start card:create-deco -->
<a id="create-deco"></a>
- **Версия:** 2.0.3-1.20.1-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/create-deco)
- **Что это:** Промышленный декор в стиле Create. _(источник: docs сборки)_
<!-- auto:end card:create-deco -->

### Что даёт игроку

Чисто декоративный аддон к Create (около 400 блоков): строительные блоки в стиле Create из латуни, меди, цинка, андезита, железа и «индустриального железа». В JEI рецепты на «чужом» плагине Create ([jei.md](../jei.md)).
- **Кирпичи** восьми оттенков (`blue`, `dean`, `dusk`, `pearl`, `red`, `scarlet`, `umber`, `verdant`) — варианты: потрескавшиеся (`cracked_*`), угловые (`corner_*`), длинные и короткие (`long_*`, `short_*`), плиты, ступени, стены.
- **Металл**: мостки `*_catwalk`, перила, ступени мостков, решётки `*_bars`, окна, двери и люки (в том числе `locked_*_door`), лампы `*_lamp`, листы металла `*_sheet_metal`, сетчатые ограждения `*_mesh_fence`, корпуса `*_hull`, опоры `*_support` / `*_support_wedge`, фасады `*_facade`.
- **Контейнеры и таблички**: цветные `*_shipping_container`, `*_placard`, декали (`decal_fire`, `decal_radioactive`, `decal_warning`, `decal_electrical` и т. д.).
- **Предметы**: монеты и стопки монет (`brass_coin`, `copper_coin`, `gold_coin`, `iron_coin`, `netherite_coin`, `zinc_coin`, `industrial_iron_coin` и `*_coinstack`), слитки, самородки и листы (`industrial_iron_ingot`, `netherite_sheet`, `andesite_sheet`).
Конфигов в `pack/config` нет.

## Create Stuff 'N Additions

<!-- auto:start card:create-stuff-additions -->
<a id="create-stuff-additions"></a>
- **Версия:** 2.1.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/create-stuff-additions)
- **Что это:** Дополнения к Create: новые механизмы и предметы. _(источник: docs сборки)_
<!-- auto:end card:create-stuff-additions -->

### Как играть

Create Stuff 'N Additions (`create_sa`, Furti_Two) — «снаряжение» на механике Create: реактивные ранцы, экзоскелеты, дроны и инструменты. Работает на топливе (уголь/дерево, лава) и воде; заряжается баками (`small_/medium_/large_fueling_tank` — топливо, `*_filling_tank` — вода): ПКМ по лаве/воде, предмет-гаджет кладут во вторую руку и «бросают» топливо.
- **Реактивные ранцы** `copper_jetpack_chestplate` (вода под давлением), `brass_jetpack_chestplate` (вода + топливо, пар), `netherite_jetpack_chestplate`: полёт, плавание и ускорение, режим зависания (hover), клавиша «Flying» (`key.create_sa.flying`). Высота полёта ограничена; ограничение снимает чара `create_sa:above_the_clouds`.
- **Экзоскелеты** `andesite_`, `copper_`, `brass_exoskeleton_chestplate`: эффекты Haste/Strength (у каждого свой «минус»: слабость, усталость) за расход топлива или воды.
- **Дрон**: `drone_controller` управляет латунным дроном (`brass_drone`), у дрона есть модули (`drill_module`, `fan_module`, `vault_module`, `magnet_module`); клавиши «Increase/Decrease Drone Reach».
- **Инструменты**: `portable_drill` (бур 3×3 с чарой `digging`), `block_picker` (магнитный перенос блоков; с чарой `gravity_gun` — и существ), `flamethrower` (огнемёт, плавит выброшенные рядом предметы), `grapplin_whisk` (качание на «паутине», урон с чарой `impact`), `blazing_*` (не ломаются в Нижнем мире, плавят добычу), `experience_*` (дают опыт с ударов), броня и инструменты из меди, латуни, цинка, слизи.

### Предметы/блоки

Ключевые id (`create_sa:<id>`): `portable_drill`, `block_picker`, `grapplin_whisk`, `flamethrower`, `copper_magnet`, `heat_engine`, `steam_engine`, `hydraulic_engine` (двигатели-компоненты рецептов), `vault_component`, `fan_component`, `drone_controller`, `slime_boots`, `slime_helmet` (цепляние за потолок и отскок), `*_propeler_*` (броня с пропеллерами).

### Чары

`create_sa:above_the_clouds` (ранцы без потолка высоты), `create_sa:impact` (импульс → урон), `create_sa:digging` (бур 3×3), `create_sa:gravity_gun`, `create_sa:chunk_eater`, `create_sa:hellfire`.

### Конфиги

Нет файла в `pack/config`. Мод помечен в наших заметках как «чужой плагин Create» ([jei.md](../jei.md)); клавиша полёта `key.create_sa.flying` использовалась как пример в проверке перепривязки клавиш ([compat.md](../compat.md), раздел про `mm_perf`).

## Create: Copycats+

<!-- auto:start card:copycats -->
<a id="copycats"></a>
- **Версия:** 3.0.10+mc.1.20.1-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/copycats)
- **Что это:** Блоки-«копикаты», принимающие облик других блоков, в одном моде. _(источник: docs сборки)_
<!-- auto:end card:copycats -->

### Что даёт игроку

Copycats+ (3.0.10) — блоки-«хамелеоны» для Create: любой полный блок вставляется в рамку и принимает его внешний вид. Расширяет `copycat_panel`/`copycat_step` из Create десятками форм (`id` — `copycats:<id>`):
- Формы: `copycat_slab`, `copycat_stairs`, `copycat_slope`, `copycat_vertical_slope`, `copycat_layer`, `copycat_half_layer`, `copycat_slice`, `copycat_byte`, `copycat_beam`, `copycat_board`, `copycat_corner_slice`, `copycat_fence`, `copycat_fence_gate`, `copycat_wall`, `copycat_ladder`, `copycat_catwalk`, `copycat_box`.
- Функциональные: двери и люки (`copycat_door`, `copycat_sliding_door`, `copycat_folding_door`, `copycat_trapdoor`), кнопки и нажимные плиты, валы и шестерни (`copycat_shaft`, `copycat_cogwheel`, `copycat_large_cogwheel`), трубы (`copycat_fluid_pipe`, `copycat_glass_fluid_pipe`), `copycat_ghost_block`.
- Родственные: `copycat_wing` в [Create: Propulsion](#create-propulsion) и `copycat_headstock` в [Create: Steam 'n' Rails](#create-steam-n-rails).
Конфигов нет.

## Create: Propulsion

<!-- auto:start card:create-propulsion -->
<a id="create-propulsion"></a>
- **Версия:** 0.4.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/create-propulsion)
- **Что это:** Красные магниты, оптические датчики, двигатели и др. для Create; работает с Valkyrien Skies 2. _(источник: docs сборки)_
- **В сборке:** Create × Valkyrien Skies (двигатели для кораблей)
- **В наших docs:** [compat.md](../compat.md) (26), [version-choice.md](../version-choice.md) (9), [performance.md](../performance.md) (7), [enchanting-table.md](../enchanting-table.md) (6), [russification.md](../russification.md) (6), [roadmap.md](../roadmap.md) (5)
<!-- auto:end card:create-propulsion -->

### Как играть

Create: Propulsion (0.4.1) добавляет к Create двигатели, крылья и датчики для **физических кораблей** [Valkyrien Skies](#valkyrien-skies): без VS многие блоки бесполезны.
1. Собрать конструкцию из блоков; **`physics_assembler`** (с `assembly_gauge` в слоте, при питании) превращает область в физическую сущность — корабль.
2. Тяга: `thruster` (на топливе; сила зависит от сигнала редстоуна и вида топлива), `ion_thruster` (на FE вместо топлива), `propeller` (≥ 2 лопастей `wooden_blade` / `copper_blade` / `andesite_blade`, тяга ∝ оборотам), `creative_thruster`.
3. Тепло и подъём: `solid_burner` / `liquid_burner` → нагрев, `stirling_engine` (тепло → вращение), `hot_air_burner`, `hot_air_pump`, `envelope` (цветные оболочки воздушного шара; в Ponder «Making a Hot Air Balloon»).
4. Управление: `redstone_helm` (штурвал), `redstone_transmission` (прямой/пошаговый режимы), `tilt_adapter`, `wing` / `tempered_wing` / `copycat_wing` (подъёмная сила от скорости), `reaction_wheel`.
5. Датчики: `optical_sensor`, `inline_optical_sensor` (+ линзы `optical_lens`, `fluid_lens`, `focus_lens`, `invisibility_lens`), `lodestone_tracker`, `redstone_magnet`.

### Предметы/блоки

Топливо и материалы: `turpentine_bucket`, `oxidizer_bucket`, `pine_resin`; музыкальный диск `cloudfarer_music_disc`. У блоков есть Ponder-анимации (тяга, мультиблок-двигатель и его эффективность, режимы трансмиссии).

### Конфиги

Нет файла в `pack/config`. В наших заметках: «Create × Valkyrien Skies (двигатели для кораблей)» ([compat.md](../compat.md)).

## Create: Steam 'n' Rails

<!-- auto:start card:create-steam-n-rails -->
<a id="create-steam-n-rails"></a>
- **Версия:** 1.7.3+forge-mc1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/create-steam-n-rails)
- **Что это:** Расширяет рельсы и паровую систему Create. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (26), [version-choice.md](../version-choice.md) (9), [performance.md](../performance.md) (7), [enchanting-table.md](../enchanting-table.md) (6), [russification.md](../russification.md) (6), [roadmap.md](../roadmap.md) (5)
<!-- auto:end card:create-steam-n-rails -->

### Как играть

Create: Steam 'n' Rails (`railways`, 1.7.3) — большое дополнение для поездов Create: паровозы, дизель, разные колёсные тележки и рельсы.
1. **Паровоз**: котёл из блоков Create + `smokestack_*` (дровяная, угольная, мазутная, длинная, обтекаемая и т. д.; варианты из латуни и меди) + `fuel_tank`.
2. **Тележки** (`*_bogey`): `singleaxle_bogey`, `doubleaxle_bogey`, `medium_bogey`, `narrow_scotch_bogey`, `narrow_double_scotch_bogey`, `mono_bogey` (монорельс); к ним — `headstock`, сцепки (`knuckle_coupler`, `split_knuckle_coupler`, `screwlink_coupler`, `link and pin`).
3. **Пути**: рельсы `track_*` по породам дерева и камня, у каждой есть узкий и широкий вариант; `semaphore` — семафорные сигналы; буферы `buffer`, `big_buffer`.
4. **Проводник**: сущность `railways:conductor` и `conductor_whistle` (свисток вызывает поезд к станции в обход расписания), `conductor_vent`.
5. **Краска**: ёмкости `*_paint_pitcher` и краски (`paint`), 30+ оттенков, команда подсчёта покрашенных блоков (`railways.commands.count_paint`).

### Предметы/блоки

Сущности: `railways:conductor` (Проводник), `railways:benchcart` (вагонетка с верстаком), `railways:jukeboxcart` (с проигрывателем). Блоки: `handcar`, `remote_lens`, `portable_fuel_interface`, листы `locometal` (корпус паровоза) и «hazard stripes» для декора. Фуражки проводников (`*_conductor_cap`) — цветные. Скрытое достижение `strange_tea` («That's not Earl Grey») — случайно выпить краску.

### Конфиги

Конфигурации в `pack/config` нет. Проверка рельсов — в [client-checklist.md](../client-checklist.md); на странице Create рельса из этого аддона названа примером для ручной проверки.

## Eureka! Ships! for Valkyrien Skies (Forge/Fabric)

<!-- auto:start card:eureka -->
<a id="eureka"></a>
- **Версия:** 1.20.1-forge-1.6.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/eureka)
- **Что это:** Корабли из обычных блоков с физикой (аддон Valkyrien Skies). _(источник: docs сборки)_
<!-- auto:end card:eureka -->

### Как играть

Eureka (Valkyrien Skies Eureka, `vs_eureka` 1.6.3; Tri0de, ewoudje и др.) — «жизнь на корабле» поверх [Valkyrien Skies](#valkyrien-skies): вы строите корабль из обычных блоков, ставите **штурвал** и «собираете» его. Это сам мод (jar), не путать с CurseForge-сборкой «Create: Eureka!» ([compat.md](../compat.md), п. 16).
1. Корпус из блоков плюс специальные блоки (по названиям; описаний в jar нет): **Floater** (`vs_eureka:floater`), **Balloon** (`balloon`, 16 цветов), **Ballast** (`ballast`), якорь **Anchor** (`anchor`), двигатель **Ship Engine** (`engine`).
2. **Штурвал** `*_ship_helm` (8 пород дерева: oak, spruce, birch, jungle, acacia, dark oak, crimson, warped): «Sneak to open the ship helm!» (присесть и ПКМ — меню). Кнопки меню: **Assemble** (собрать корабль), **Disassemble** (разобрать в блоки), **Align** (выровнять по сетке).
3. Круиз-контроль — в HUD есть сообщения «Cruise control enabled/disabled».
4. Максимальный размер корабля ограничен конфигом (сообщение «Ship is too big! Max size is %s blocks»); теги `vs_eureka:assemble_blacklist` не дают собирать запрещённые блоки.

Рецепты из jar (проверено по `data/vs_eureka/recipes`): штурвал — палки, заборы, золотой слиток и плита; якорь — поводки, железные слитки и железный блок; двигатель — камень, доменная печь, стеклянная панель и гладкий камень; `floater` — 4 нити вокруг доски; `balloon` — 4 кожи (есть варианты из шерсти, бумаги, мембраны: `balloon_leather`, `balloon_wool`, `balloon_paper`, `balloon_membrane`).

### Предметы/блоки

`vs_eureka:oak_ship_helm` … `warped_ship_helm`, `anchor`, `engine`, `ballast`, `floater`, `balloon` и 16 цветных `*_balloon`; креативная вкладка «VS Eureka». Массы блоков — `data/vs_eureka/vs_mass`.

### Конфиги

Файла в `pack/config` нет — настройки по умолчанию.

## Mekanism

<!-- auto:start card:mekanism -->
<a id="mekanism"></a>
- **Версия:** 10.4.16.80 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism)
- **Что это:** Высокотехнологичные машины, энергетика, гаджеты. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (13), [galacticraft.md](../galacticraft.md) (4), [jei.md](../jei.md) (3), [orespawn.md](../orespawn.md) (3), [roadmap.md](../roadmap.md) (3), [client-checklist.md](../client-checklist.md) (2)
<!-- auto:end card:mekanism -->

### Как играть

Mekanism 10.4.16 — «большая» техника: переработка руд в несколько ступеней, химия (газы, жидкости, слюри), энергия, хранилище, телепорты, скафандр и реакторы (через [Mekanism Generators](#mekanism-generators)). Внутриигровой справочник — достижения (ветка с корнем «Mekanism» — по ним удобно идти по шагам), JEI + [Just Enough Mekanism Multiblocks](../jei.md) показывают рецепты и схемы мультиблоков.
1. **Старт**: руды (`osmium`, `tin`, `lead`, `uranium`, `fluorite`, соль) → `enrichment_chamber`, `crusher`, `metallurgic_infuser` (железо + углерод → сталь, `ingot_steel`), `enriched_*`, `alloy_infused` → `alloy_reinforced` → `alloy_atomic`; «5-кратная» переработка руд — `purification_chamber`, `chemical_injection_chamber`, `chemical_dissolution_chamber`, `chemical_washer`, `chemical_crystallizer`. Машины улучшаются **апгрейдами** (`upgrade_speed`, `upgrade_energy`, …), а тиры фабрик ставят установщиками (достижение «installer»).
2. **Транспорт**: Universal Cable (энергия), Mechanical Pipe (жидкости), Pressurized Tube (химия), Logistical Transporter + `logistical_sorter` (предметы), Thermodynamic Conductor (тепло); у всех тиры basic/advanced/elite/ultimate. Настройка сторон — `configurator`, копирование настроек — `configuration_card`.
3. **Энергия**: Energy Cube (накопитель), генераторы — в аддоне [Mekanism Generators](#mekanism-generators).
4. **Хранилище**: Bin, `personal_chest`, `personal_barrel`, **QIO** (`qio_drive_array`, `qio_dashboard`, `qio_importer`, `qio_exporter`, `qio_redstone_adapter`; диски `qio_drive_base` → `qio_drive_hyper_dense` → `qio_drive_supermassive` → `qio_drive_time_dilating`) — сетевое хранилище без кабелей; сравнение с AE2 и Refined Storage — в карточках [AE2](#ae2) и [Refined Storage](#refined-storage).
5. **Снаряжение**: `jetpack`, `scuba_mask` + `scuba_tank`, `free_runners`, `hazmat_*` (защита от радиации), `atomic_disassembler`, `flamethrower`, `electric_bow`, `canteen`, и **MekaSuit** (`mekasuit_helmet/bodyarmor/pants/boots`) + `meka_tool`: модули ставятся в `modification_station`.

### Предметы/блоки

| id (`mekanism:<id>`) | Что делает |
|---|---|
| `digital_miner` | Цифровой шахтёр: автоматическая добыча по фильтрам в области |
| `teleporter`, `teleporter_frame`, `portable_teleporter` | Телепорт по частотам; портативный — предмет для личного перемещения |
| `quantum_entangloporter` | Мгновенный обмен предметами/жидкостями/энергией между точками по частоте |
| `robit` | Помощник-робот (зарядка на `chargepad`) |
| `laser`, `laser_amplifier`, `laser_tractor_beam` | Лазер (урон, добыча) и усилитель/притягивающий луч |
| `dimensional_stabilizer` + `upgrade_anchor` | Прогрузка чанков |
| `seismic_vibrator` + `seismic_reader` | Сейсморазведка: показывает, что под землёй |
| `nutritional_liquifier`, `canteen` | Пищевая паста и фляга |
| `network_reader`, `dosimeter`, `geiger_counter` | Диагностика сетей и радиации |
| `cardboard_box` | Перенос блоков вместе с содержимым |
| `sps_casing`, `sps_port`, `antiprotonic_nucleosynthesizer` | Поздняя игра: SPS (антиматерия) и нуклеосинтез |
| `radioactive_waste_barrel`, `pellet_plutonium`, `pellet_polonium`, `pellet_antimatter` | Хранение отходов и «пеллеты» |
| `hdpe_*`, `hdpe_elytra` | Пластик HDPE и усиленные надкрылья |

### Мобы

Сущности: `mekanism:robit` (Robit) и `mekanism:flame` (пламя огнемёта). Мобов в мире Mekanism не добавляет (у [Mekanism Additions](#mekanism-additions) есть «детёныши» мобов).

### События

Радиация: источники (`radioactive_waste_barrel`, уран и др.) создают излучение в мире и на игроке; урон — `mekanism:radiation` (смерть «killed by radiation poisoning»), лазеров — `mekanism:laser`. Защита — `hazmat_*`.

### Данжи/структуры (/locate)

Структур нет. Руды: `mekanism:ore_osmium_upper/middle/small`, `ore_tin_large/small`, `ore_lead_normal`, `ore_uranium_small/buried`, `ore_fluorite_normal/buried` и `mekanism:salt` (соляные залежи). На планетах Ad Astra те же руды даёт [Mekanism: Ad Astra Ores](space.md#mekanism-ad-astra-ores).

### Команды/гамерулы

Команда `/mek` (подкоманды по lang): `radiation add|get|reduce|clear|remove_all`, `build` (построить мультиблок), `chunk watch|unwatch|clear|flush`, `retrogen` (нужно включить в конфиге), `tp` / `tpop`, `debug`, `testrules` (включает keepInventory и выключает мобов/смену дня/погоду/mobGriefing — осторожно на живом сервере).

### Конфиги

Файлов конфигов Mekanism в `pack/config` нет — настройки по умолчанию. Дубли металлов объединяет Almost Unified: приоритет у `mekanism` выше `ad_astra`, а вся сталь выдаётся как `mekanism:ingot_steel`, свинец — Mekanism ([compat.md](../compat.md), «Объединение дублей»). Рецепты `mekanism:block_charcoal` против `quark:charcoal_block` разобраны в [compat.md](../compat.md); чек-лист — [client-checklist.md](../client-checklist.md).

## Mekanism Additions

<!-- auto:start card:mekanism-additions -->
<a id="mekanism-additions"></a>
- **Версия:** 10.4.16.80 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-additions)
- **Что это:** Модуль Mekanism с вещами, не вошедшими в другие модули. _(источник: docs сборки)_
<!-- auto:end card:mekanism-additions -->

### Что даёт игроку

Mekanism Additions 10.4.16 — «весёлый» аддон к Mekanism:
- **Пластик**: 16 цветов блоков `*_plastic` с вариантами — `*_reinforced_plastic`, `*_slick_plastic` (скользкий), `*_plastic_transparent`, `*_plastic_glow` (светится), `*_plastic_road`, плиты, ступени, заборы и калитки (`*_plastic_fence`, `*_plastic_fence_gate`), панели `*_glow_panel`.
- **Воздушные шарики** `*_balloon` (сущность `mekanismadditions:balloon`; достижения «Pop Pop», «Reach for the Skies»).
- **«Детёныши»**: `mekanismadditions:baby_creeper`, `baby_enderman`, `baby_skeleton`, `baby_stray`, `baby_wither_skeleton` (яйца `*_spawn_egg`); достижения «Not the Babies» и «Don't Try Taking Candy From Those Babies».
- `obsidian_tnt` (обсидиановая взрывчатка) и `walkie_talkie` (рация).
Конфигов в `pack/config` нет.

## Mekanism Covers

<!-- auto:start card:mekanism-covers -->
<a id="mekanism-covers"></a>
- **Версия:** 1.3-BETA+1.20 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-covers)
- **Что это:** Аддон Mekanism: простой способ закрывать облицовкой кабели и трубы. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:mekanism-covers -->

### Что даёт игроку

Mekanism Covers (1.3-BETA, автор D4rkness_King) добавляет «покрытия» (Cover) — предметы с видом другого блока:
- Крафт: стекло в центре, вокруг — по 4 осмиевых самородка (`forge:nuggets/osmium`) и 4 древесной пыли (`forge:dusts/wood`) → 4 `mekanismcovers:empty_cover`.
- Рецепт `crafting_special_cover` в JEI соединяет пустое покрытие с блоком — получается `mekanismcovers:cover` с видом этого блока. На какие именно блоки Mekanism покрытия накладываются, в jar не описано (по названию — маскировка).
- Плагин для Jade показывает компонент покрытия.
JEI-вид — «только special» ([jei.md](../jei.md)). Конфиг мода (класс `ModConfig`) в `pack/config` не лежит — настройки по умолчанию.

## Mekanism Curios

<!-- auto:start card:mekanism-curios -->
<a id="mekanism-curios"></a>
- **Версия:** 1.2.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-curios)
- **Что это:** Портативный QIO в слоте Curios. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:mekanism-curios -->

### Что даёт игроку

Mekanism Curios 1.2.1 (TonimatasDEV) добавляет в Curios API два слота и клавиши быстрого доступа:
- слот `qio` — для `mekanism:portable_qio_dashboard` (клавиша «Open Portable QIO»);
- слот `teleporter` — для `mekanism:portable_teleporter` (клавиши «Open Portable Teleporter» и «Quick-Teleport»; телепортируется на выбранную частоту, если телепорт найден, заряжен и привязан).
Вещи работают, не занимая руки. Конфигов нет.

## Mekanism Generators

<!-- auto:start card:mekanism-generators -->
<a id="mekanism-generators"></a>
- **Версия:** 10.4.16.80 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-generators)
- **Что это:** Модуль Mekanism: продвинутая генерация энергии. _(источник: docs сборки)_
<!-- auto:end card:mekanism-generators -->

### Как играть

Mekanism Generators 10.4.16 — генераторы и реакторы. Порядок (по достижениям): `heat_generator` («Your First Generator») → `solar_generator` / `advanced_solar_generator` → `wind_generator` → `gas_burning_generator` (топливо — этилен, «Burn the Gas»; также `bio_generator`, `bioethanol`) → **турбины** (`turbine_casing`, `turbine_rotor`, `turbine_valve`, `turbine_vent`, `rotational_complex`, `saturating_condenser`, `electromagnetic_coil`) → **деление** (`fission_reactor_casing`, `fission_fuel_assembly`, `control_rod_assembly`, `fission_reactor_port`, `fission_reactor_logic_adapter`) → **синтез** (`fusion_reactor_controller`, `fusion_reactor_frame`, `fusion_reactor_port`, `fusion_reactor_logic_adapter`, `laser_focus_matrix`, `reactor_glass`; топливо `fusion_fuel`, `hohlraum`, жидкости `deuterium`, `tritium`).
Мультиблоки удобно собирать по схемам Just Enough Mekanism Multiblocks ([jei.md](../jei.md)). Радиация и отходы — см. «События» у [Mekanism](#mekanism).

### Конфиги

Файла в `pack/config` нет — настройки по умолчанию. Large-версии генераторов (тепло, газ, ветер) добавляет [Mekanism: More Machine](#mekanismmore-machine).

## Mekanism Tools

<!-- auto:start card:mekanism-tools -->
<a id="mekanism-tools"></a>
- **Версия:** 10.4.16.80 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-tools)
- **Что это:** Модуль Mekanism: мощные инструменты и броня. _(источник: docs сборки)_
<!-- auto:end card:mekanism-tools -->

### Что даёт игроку

Mekanism Tools 10.4.16 — инструменты и броня из материалов Mekanism: `bronze_*`, `lapis_lazuli_*`, `osmium_*`, `refined_glowstone_*`, `refined_obsidian_*`, `steel_*` (кирка, топор, лопата, мотыга, меч, шлем, нагрудник, поножи, ботинки) и **щиты** (`*_shield`, 16 цветов). **Паксели** (`wood_/stone_/iron_/gold_/diamond_/netherite_/…_paxel`) — «три в одном»: кирка, топор и лопата. Достижения: «Multi-Tool», «Better Than Netherite» (броня из очищенного обсидиана), «Loved By Piglins» (броня из очищенного светящегося камня, пиглины ей «рады»). В JEI есть категории `banner_shield` и `paxel` ([jei.md](../jei.md)). Конфигов в `pack/config` нет.

## Mekanism:More Machine

<!-- auto:start card:mekanismmoremachine -->
<a id="mekanismmoremachine"></a>
- **Версия:** 1.20.1-1.2.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanismmoremachine)
- **Что это:** Дополнительные машины и фабрики для Mekanism. _(источник: docs сборки)_
- **В сборке:** Mekanism: больше машин и фабрик
- **В наших docs:** [compat.md](../compat.md) (16), [jei.md](../jei.md) (10), [galacticraft.md](../galacticraft.md) (4), [orespawn.md](../orespawn.md) (3), [roadmap.md](../roadmap.md) (3), [client-checklist.md](../client-checklist.md) (2)
<!-- auto:end card:mekanismmoremachine -->

### Как играть

Mekanism: More Machine (`mekmm`, 1.2.1, автор Lost Myself) — расширяет машины Mekanism и добавляет «фабрики» для тех машин, у которых их не было. Требует Mekanism 10.4.16.
- **Новые фабрики** (по 9 уровней: basic, advanced, elite, ultimate, **overclocked, quantum, dense, multiversal, creative**): окисление, растворение, промывка, кристаллизация, реакционная камера под давлением, центрифуга, ожижение (liquifying), извлечение пигмента (`*_<тип>_factory`).
- **Машины**: `recycler`, `planting_station`, `cnc_stamper`, `cnc_lathe`, `cnc_rolling_mill`, `replicator`, `fluid_replicator`, `chemical_replicator` (`uu_matter`), `ambient_gas_collector`, `wireless_charging_station`.
- **Large-машины**: `large_rotary_condensentrator`, `large_chemical_infuser`, `large_electrolytic_separator`, `large_solar_neutron_activator`, `large_antiprotonic_nucleosynthesizer`, `large_pigment_mixer`, `large_heat_generator`, `large_gas_burning_generator`, `large_wind_generator`.
- Предметы: `scrap`, `scrap_box`, `uu_matter`, `empty_crystal`, `advanced_electrolysis_core`.

### Наши правки и баги

- 4 модели покраски фабрик использовали загрузчик NeoForge (`neoforge:composite`) и показывались «потерянными»; исправлены копиями в `pack/kubejs/assets/mekmm/models/block/factory/painting/active/*.json` ([compat.md](../compat.md), «Находки клиентского лога»).
- 75 таблиц добычи на фабрики других аддонов Mekanism не загружаются (предметов нет) — некритично; в JEI рецепты Replicator не видны из-за NPE мода (`registerItemReplicator`) — [jei.md](../jei.md).

### Конфиги

Файла в `pack/config` нет — по умолчанию.

## Refined Storage

<!-- auto:start card:refined-storage -->
<a id="refined-storage"></a>
- **Версия:** 1.12.4 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/refined-storage)
- **Что это:** Сеть хранения: удобное решение проблемы захламлённых сундуков. _(источник: docs сборки)_
- **В сборке:** нужен More Backpack Upgrades (не объявлен на Modrinth)
- **В наших docs:** [compat.md](../compat.md) (4), [roadmap.md](../roadmap.md) (3), [client-checklist.md](../client-checklist.md) (1), [russification.md](../russification.md) (1)
<!-- auto:end card:refined-storage -->

### Как играть

Refined Storage 1.12.4 (Refined Mods) — более простая, «блочная» альтернатива AE2: сеть из кабеля, один контроллер и хранилище на дисках. В сборке он нужен ещё и мод **More Backpack Upgrades** (требует Refined Storage; добавлен с одобрения владельца — [compat.md](../compat.md)).
1. `controller` (питание сети), `cable`, затем `grid` (терминал) — `crafting_grid`, `fluid_grid`, `pattern_grid`.
2. Хранилище: `disk_drive` + диски `1k/4k/16k/64k_storage_disk` и жидкостные `64k…4096k_fluid_storage_disk` (части `*_storage_part` + `storage_housing`), блоки `*_storage_block`.
3. Автоматизация: `importer`, `exporter`, `constructor`, `destructor`, `interface`, `external_storage`, `detector`, `relay`, `disk_manipulator`, `security_manager` + `security_card`.
4. Автокрафт: `crafter` (+ `crafter_manager`), шаблоны `pattern`, `crafting_monitor`, улучшения `speed_upgrade`, `stack_upgrade`, `crafting_upgrade`, `range_upgrade`, `silk_touch_upgrade`, `fortune_*_upgrade`, `regulator_upgrade`.
5. Беспроводное: `wireless_transmitter` + `wireless_grid` / `wireless_fluid_grid` / `wireless_crafting_monitor`, `portable_grid` (клавиши «Open Wireless Grid», «Open Portable Grid»); связь сетей — `network_transmitter` / `network_receiver` + `network_card`.

### Предметы/блоки

| id (`refinedstorage:<id>`) | Что делает |
|---|---|
| `controller`, `creative_controller` | Контроллер сети (питание) |
| `grid`, `crafting_grid`, `pattern_grid`, `fluid_grid` | Терминалы доступа |
| `disk_drive`, `*_storage_disk` | Диски и накопители |
| `importer`, `exporter`, `constructor`, `destructor`, `interface`, `detector` | Автоматизация |
| `crafter`, `pattern`, `crafting_monitor` | Автокрафт |
| `wrench`, `network_card`, `security_card` | Инструменты и карты |
| `quartz_enriched_iron`, `basic/improved/advanced_processor`, `silicon` | Материалы |

### Команды/гамерулы

`/refinedstorage disk create <диск> <игрок>` — выдать диск; `/refinedstorage network list` (позиция, узлы, TPS-время тика, энергия, задачи автокрафта), `network get`, `network autocrafting get|cancel` (отмена задач).

### Конфиги

Файла в `pack/config` нет — по умолчанию. JEI: категории `cover_recipe`, `hollow_cover_recipe`, `upgrade_with_enchanted_book` ([jei.md](../jei.md)). Проверка — только пачка «сторон»; полный тест RS ещё не делался ([roadmap.md](../roadmap.md)).

## Valkyrien Sails

<!-- auto:start card:valkyrien-sails -->
<a id="valkyrien-sails"></a>
- **Версия:** 1.20.1-0.3.2-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/valkyrien-sails)
- **Что это:** Аддон Valkyrien Skies: механики парусного плавания. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:valkyrien-sails -->

### Как играть

Valkyrien Sails (`vs_sails`, 0.3.2; автор Quinton Center) — парусники для Valkyrien Skies: паруса, такелаж, якоря и ветер.
1. Штурвал: `helm_block` («Old Ship's Helm»), `redstone_helm_block`, колёса `*_helm_wheel` для всех пород дерева (oak…bamboo) и основания `*_helm`.
2. Движение: `sail_block` (16 цветов, `*_sail`), `magma_sail_block` (`*_magma_sail`), такелаж `rigging_block` / `magma_rigging_block`; ветер показывают `wind_flag` (флаги 16 цветов, есть «rainbow», «jolly roger» — JEI: `wind_flag_pattern`, `wind_flag_dye`).
3. Стоянка и остойчивость: `anchor_block`, `capstan_block` (кабестан), `rope_block` / `magma_rope_block`, `ballast_block` / `magic_ballast_block`, поплавки `buoy_block` (`*_buoy`, 16 цветов).
4. Предмет `dedication_bottle` — «бутылка посвящения»; звук `vs_sails:wind_ambience` («Wind blows»).

Масса блоков — `data/vs_sails/vs_mass`. Рецепты без отдельных категорий JEI ([jei.md](../jei.md)). Конфигов в `pack/config` нет; JEI-«пробел»: `wind_flag_pattern`, `wind_flag_dye`.

## Valkyrien Skies

<!-- auto:start card:valkyrien-skies -->
<a id="valkyrien-skies"></a>
- **Версия:** 1.20.1-forge-2.4.11 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/valkyrien-skies)
- **Что это:** Физика в Minecraft: корабли и целые постройки как движущиеся тела. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (30), [roadmap.md](../roadmap.md) (16), [performance.md](../performance.md) (15), [galacticraft.md](../galacticraft.md) (3), [hosting.md](../hosting.md) (2), [version-choice.md](../version-choice.md) (2)
<!-- auto:end card:valkyrien-skies -->

### Как играть

Valkyrien Skies 2.4.11 (Tri0de, Rubydesic; зависимость — Architectury) — **физика кораблей**: набор блоков превращается в «корабль» (отдельное тело на «верфи» мира), который двигается, плавает и летает, а игроки ходят по нему. Сама по себе VS даёт механику, а готовые «играбельные» корабли строят аддоны: [Eureka](#eureka-ships-for-valkyrien-skies-forgefabric) (штурвал, шарики, двигатель), [Valkyrien Sails](#valkyrien-sails) (паруса и такелаж), [Create: Propulsion](#create-propulsion) (двигатели и крылья), [VS Tournament Reforged](#valkyrien-skies-tournament-reforged) (оружие и боевые блоки).
1. **Создание корабля**: аддонные блоки (штурвал Eureka, `physics_assembler` Create: Propulsion) собирают корабль из соседних блоков; сама VS даёт отладочные предметы `valkyrienskies:ship_creator`, `ship_creator_smaller` (мини), `ship_assembler`, `area_assembler`.
2. **Масса и плавучесть**: у каждого блока есть масса (данные `vs_mass/…` — руды, растения, декор, функциональные блоки, блоки Create); подсказка предмета показывает «Mass». Правила поведения сущностей на корабле — `vs_entities` (вагонетки, рамки, картины, стойки для брони, сущности Create: контрапции, сиденья).
3. **Управление**: клавиши «Cruise» и «Descend» (категория «Driving»); в Create у деплоера есть режим «Ship Behaviour» (ставить на текущий корабль или в мир).
4. **Совместимость**: Superb Warfare сам трассирует снаряды по кораблям, а наш `mm-compat` чинит блок попадания (`scripts/tests/vshitcheck.js`); Canary × VS — отдельная миксин-проблема ([compat.md](../compat.md), п. 6). В сборке есть аддон Jade `jade-vs` ([jei.md](../jei.md)).

### Предметы/блоки

| id (`valkyrienskies:<id>`) | Что делает |
|---|---|
| `ship_creator`, `ship_creator_smaller` | Создание корабля из блока (креатив/отладка) |
| `ship_remover` | Удаление корабля |
| `ship_assembler`, `area_assembler` | Сборка корабля из выбранных блоков или области |
| `connection_checker` | Проверка связности блоков |
| `physics_entity_creator`, сущность `valkyrienskies:vs_physics_entity` | Физическая сущность (для отладки) |
| `test_chair`, `test_hinge`, `test_flap`, `test_wing`, `test_sphere`, `test_thruster`, `test_antigrav` | «Debug…»-блоки для проверки механики |

### События

Корабли — ресурсоёмкая механика: при фризе сервера виновником может быть очередь физики VS (в [compat.md](../compat.md) разобран один такой случай, spark «Timed out waiting for world statistics»). В конфиге VS есть параметр разделения кораблей (`enableWorldSplitting`).

### Команды/гамерулы

Команда `/vs` (подкоманды по lang): `delete`, `remass` (пересчитать массу), `shipify` (превратить блок в корабль), `set-static`, `teleport`, `get-ship`, `scale`, `backend`, `lod`, `air-values`, `get-gravity`, `set-splitting`, `dry` (убрать воду из корабля). Точный синтаксис — по автодополнению в игре; доступ задают настройки `…CommandPerms` в конфиге.

### Конфиги

Файла VS в `pack/config` нет — настройки по умолчанию; из наших заметок: `enableClientConnectivity` уже `false` ([performance.md](../performance.md)). Класс `VSGameConfig` содержит `allowMobSpawns`, `enableWorldSplitting`, `enablePocketBuoyancy`, `explosionBlastForce`, `defaultBlockMass` и др. Шейдеры и рендер Embeddium/Oculus не проверялись глазами ([performance.md](../performance.md)).

## Valkyrien Skies: Tournament Reforged

<!-- auto:start card:vs-tournament-reforged -->
<a id="vs-tournament-reforged"></a>
- **Версия:** 1.0.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/vs-tournament-reforged)
- **Что это:** Аддон Valkyrien Skies: механики и оружие для сражений кораблей. _(источник: docs сборки)_
- **В сборке:** порт VS Tournament под VS 2.4 (оригинал vs-tournament падает: VSConfigClass)
- **В наших docs:** [compat.md](../compat.md) (6), [performance.md](../performance.md) (6), [roadmap.md](../roadmap.md) (6), [galacticraft.md](../galacticraft.md) (1), [hosting.md](../hosting.md) (1), [jei.md](../jei.md) (1)
<!-- auto:end card:vs-tournament-reforged -->

### Как играть

VS Tournament Reforged (`vs_tournament` 1.0.3; alex-s168, Balatan, 411_Cat, blockninja124) — порт мода VS Tournament под VS 2.4 (оригинал падает с `NoClassDefFoundError VSConfigClass` — [compat.md](../compat.md)). Набор боевых и «турнирных» блоков для кораблей [Valkyrien Skies](#valkyrien-skies); работает вместе с Create (теги).
- **Двигатели**: `thruster`, `tiny_thruster`, `custom_thruster`, `six_way_thruster`, `spinner`, `custom_spinner`, `six_way_spinner`, `prop_big` / `prop_small` (пропеллеры помечены «Deprecated: use thrusters»), улучшение `upgrade_thruster`.
- **Подъём и вес**: `balloon`, `powered_balloon`, `floater`, `ballast`, `flap` («Tournament Flap»).
- **Сиденья и датчики**: `seat` (лёгкое), `obsidian_seat` (стойкое ко взрывам), `sensor` (датчик расстояния).
- **Оружие и взрывчатка**: `explosive_instant_small/medium/large`, `dampened_gunpowder`, `self_destruct_core` (при установке **без** сигнала редстоуна ломание уничтожает корабль; с сигналом — безопасное снятие), инструменты `pulse_gun`, `suction_gun`, `grab_gun`, `delete_wand` («Deleting Tool» удаляет корабль, чат «Deleted ship!»).
- `baishi_education` — предмет-обучение (Tutorial Item).

Параметры блоков настраиваются NBT («Configurable NBT parameters»). Конфиги в `pack/config` отсутствуют; в JEI мод использует чужой плагин (`create`) — [jei.md](../jei.md). Режим «Deleting Tool» и взрывчатка опасны на общем сервере с кораблями друзей.

<!-- cards:end -->
