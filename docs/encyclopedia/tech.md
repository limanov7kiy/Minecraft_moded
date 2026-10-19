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
- **В наших docs:** [compat.md](../compat.md) (26), [version-choice.md](../version-choice.md) (9), [performance.md](../performance.md) (7), [russification.md](../russification.md) (6), [roadmap.md](../roadmap.md) (5), [russification-glossary.md](../russification-glossary.md) (4)
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
- **В наших docs:** [compat.md](../compat.md) (26), [version-choice.md](../version-choice.md) (9), [performance.md](../performance.md) (7), [russification.md](../russification.md) (6), [roadmap.md](../roadmap.md) (5), [russification-glossary.md](../russification-glossary.md) (4)
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

## Mekanism

<!-- auto:start card:mekanism -->
<a id="mekanism"></a>
- **Версия:** 10.4.16.80 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism)
- **Что это:** Высокотехнологичные машины, энергетика, гаджеты. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (13), [galacticraft.md](../galacticraft.md) (4), [jei.md](../jei.md) (3), [orespawn.md](../orespawn.md) (3), [roadmap.md](../roadmap.md) (3), [client-checklist.md](../client-checklist.md) (2)
<!-- auto:end card:mekanism -->

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

## Mekanism Additions

<!-- auto:start card:mekanism-additions -->
<a id="mekanism-additions"></a>
- **Версия:** 10.4.16.80 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-additions)
- **Что это:** Модуль Mekanism с вещами, не вошедшими в другие модули. _(источник: docs сборки)_
<!-- auto:end card:mekanism-additions -->

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

## Mekanism Covers

<!-- auto:start card:mekanism-covers -->
<a id="mekanism-covers"></a>
- **Версия:** 1.3-BETA+1.20 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-covers)
- **Что это:** Аддон Mekanism: простой способ закрывать облицовкой кабели и трубы. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:mekanism-covers -->

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

## Mekanism Curios

<!-- auto:start card:mekanism-curios -->
<a id="mekanism-curios"></a>
- **Версия:** 1.2.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-curios)
- **Что это:** Портативный QIO в слоте Curios. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:mekanism-curios -->

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

## Mekanism Generators

<!-- auto:start card:mekanism-generators -->
<a id="mekanism-generators"></a>
- **Версия:** 10.4.16.80 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-generators)
- **Что это:** Модуль Mekanism: продвинутая генерация энергии. _(источник: docs сборки)_
<!-- auto:end card:mekanism-generators -->

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

## Mekanism Tools

<!-- auto:start card:mekanism-tools -->
<a id="mekanism-tools"></a>
- **Версия:** 10.4.16.80 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanism-tools)
- **Что это:** Модуль Mekanism: мощные инструменты и броня. _(источник: docs сборки)_
<!-- auto:end card:mekanism-tools -->

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

## Mekanism:More Machine

<!-- auto:start card:mekanismmoremachine -->
<a id="mekanismmoremachine"></a>
- **Версия:** 1.20.1-1.2.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mekanismmoremachine)
- **Что это:** Дополнительные машины и фабрики для Mekanism. _(источник: docs сборки)_
- **В сборке:** Mekanism: больше машин и фабрик
- **В наших docs:** [compat.md](../compat.md) (16), [jei.md](../jei.md) (10), [galacticraft.md](../galacticraft.md) (4), [orespawn.md](../orespawn.md) (3), [roadmap.md](../roadmap.md) (3), [client-checklist.md](../client-checklist.md) (2)
<!-- auto:end card:mekanismmoremachine -->

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

## Refined Storage

<!-- auto:start card:refined-storage -->
<a id="refined-storage"></a>
- **Версия:** 1.12.4 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/refined-storage)
- **Что это:** Сеть хранения: удобное решение проблемы захламлённых сундуков. _(источник: docs сборки)_
- **В сборке:** нужен More Backpack Upgrades (не объявлен на Modrinth)
- **В наших docs:** [compat.md](../compat.md) (4), [roadmap.md](../roadmap.md) (3), [client-checklist.md](../client-checklist.md) (1), [russification.md](../russification.md) (1)
<!-- auto:end card:refined-storage -->

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

## Valkyrien Sails

<!-- auto:start card:valkyrien-sails -->
<a id="valkyrien-sails"></a>
- **Версия:** 1.20.1-0.3.2-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/valkyrien-sails)
- **Что это:** Аддон Valkyrien Skies: механики парусного плавания. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:valkyrien-sails -->

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

## Valkyrien Skies

<!-- auto:start card:valkyrien-skies -->
<a id="valkyrien-skies"></a>
- **Версия:** 1.20.1-forge-2.4.11 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/valkyrien-skies)
- **Что это:** Физика в Minecraft: корабли и целые постройки как движущиеся тела. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (30), [roadmap.md](../roadmap.md) (16), [performance.md](../performance.md) (15), [galacticraft.md](../galacticraft.md) (3), [hosting.md](../hosting.md) (2), [version-choice.md](../version-choice.md) (2)
<!-- auto:end card:valkyrien-skies -->

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

## Valkyrien Skies: Tournament Reforged

<!-- auto:start card:vs-tournament-reforged -->
<a id="vs-tournament-reforged"></a>
- **Версия:** 1.0.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/vs-tournament-reforged)
- **Что это:** Аддон Valkyrien Skies: механики и оружие для сражений кораблей. _(источник: docs сборки)_
- **В сборке:** порт VS Tournament под VS 2.4 (оригинал vs-tournament падает: VSConfigClass)
- **В наших docs:** [compat.md](../compat.md) (6), [performance.md](../performance.md) (6), [roadmap.md](../roadmap.md) (6), [galacticraft.md](../galacticraft.md) (1), [hosting.md](../hosting.md) (1), [jei.md](../jei.md) (1)
<!-- auto:end card:vs-tournament-reforged -->

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
