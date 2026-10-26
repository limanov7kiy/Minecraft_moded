# Оружие

Огнестрельное и тяжёлое оружие, пушки, патроны, зачарования для оружия.

<!-- auto:start toc -->
Модов в категории: **12**.

- [[TaCZ] ArsArms - Ars Nouveau & Timeless and Classics Zero](#tacz-arsarms---ars-nouveau--timeless-and-classics-zero)
- [[TACZ] Create: Precise Manufacturing](#tacz-create-precise-manufacturing)
- [[TaCZ] Curios For Ammo Box](#tacz-curios-for-ammo-box)
- [[TaCZ] IronsArms - Iron's Spells & Timeless and Classics Zero](#tacz-ironsarms---irons-spells--timeless-and-classics-zero)
- [[TACZ] LesRaisins Tactical Equipements](#tacz-lesraisins-tactical-equipements)
- [[TaCZ] Timeless and Classics Zero](#tacz-timeless-and-classics-zero)
- [[TACZ]Enchanted Arsenal](#taczenchanted-arsenal)
- [Create Big Cannons](#create-big-cannons)
- [Create Big Cannons: Advanced Technologies](#create-big-cannons-advanced-technologies)
- [Superb Warfare](#superb-warfare)
- [TaCZ addon](#tacz-addon)
- [TaCZ Tweaks](#tacz-tweaks)

Вернуться: [оглавление справочника](INDEX.md).
<!-- auto:end toc -->

## Общее по категории

_Обзор категории — вручную, следующие этапы._

---

<!-- cards:start -->
## [TaCZ] ArsArms - Ars Nouveau & Timeless and Classics Zero

<!-- auto:start card:arsarms -->
<a id="arsarms"></a>
- **Версия:** 3.0.4 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/arsarms)
- **Что это:** Запускает магию Ars Nouveau выстрелами из оружия TaCZ. _(источник: docs сборки)_
- **В сборке:** Ars Nouveau через оружие TACZ
- **В наших docs:** [compat.md](../compat.md) (1), [russification-audit.md](../russification-audit.md) (1)
<!-- auto:end card:arsarms -->

### Что даёт игроку

Магия Ars Nouveau из оружия TACZ: **заклинание, записанное на оружие**, срабатывает при выстреле (мод слушает событие выстрела TACZ и использует «реактивный» кастер Ars Nouveau). В подсказке оружия появляется блок «Ars Nouveau» с маной (NBT `arsarms:Mana`). Клавиша `key.ironsarms.irons_mode` («Toggle spell mode») переключает режим между ArsArms и [IronsArms](#ironsarms) — когда стоят оба аддона. Единственная настройка в конфиге — `castChance`: «управлять срабатыванием заклинания в зависимости от уровня зачарования Reactive».

Требования (mods.toml): TACZ ≥ 1.1.7-hotfix2, Ars Nouveau ≥ 4.12.7. Заметка в наших docs — [compat.md](../compat.md) (пачка 4); отдельных конфигов в `pack/config` нет.

## [TACZ] Create: Precise Manufacturing

<!-- auto:start card:precise-manufacturing -->
<a id="precise-manufacturing"></a>
- **Версия:** 0.4.4-cr6.0-SIMPLE-beta · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/precise-manufacturing)
- **Что это:** Аддон Create: автоматизируемые рецепты с предметами TaC Zero. _(источник: docs сборки)_
- **В сборке:** Create × TACZ
- **В наших docs:** [compat.md](../compat.md) (58), [dedup.md](../dedup.md) (29), [friends.md](../friends.md) (15), [roadmap.md](../roadmap.md) (9), [version-choice.md](../version-choice.md) (9), [enchanting-table.md](../enchanting-table.md) (8)
<!-- auto:end card:precise-manufacturing -->

### Как играть

Create-аддон для **автоматического производства патронов TACZ**: боеприпасы теперь собираются цепочкой Create (прессование, дробление, смешивание, секвенированная сборка, литьё в тазу), а не только в верстаке оружейника.
1. Сырьё: измельчённая медь/цинк/сера/базальт/кремень/известняк (дробилка/жернова Create), смеси «пороха» трёх размеров (малый/средний/длинный боеприпас) в миксере.
2. Литьё: **Литейный таз** (`prma:casting_basin`) + жидкие металлы (расплавленные медь, железо, алюминий, цинк, сплавы) и формы (`blank_cast`, `ingot_cast`, `blank_blueprint`).
3. Гильзы (малые/средние/длинные из латуни, меди, железа; отдельно гильза 40 мм и дробовая гильза), пули/головки (`*_head` для 9mm, 5.56x45, 7.62x39, 7.62x54, .308, .338, 50 BMG, .45 ACP, .357 Mag, .50 AE и др.) собираются конвейером Create; порох в патрон досыпается шагом «deploying».
4. Результат — готовые патроны TACZ (рецепты `sequenced_assembly` по калибрам из индекса TACZ).

### Предметы/блоки

Блок один — `prma:casting_basin`; остальное — предметы-заготовки: винты, пружины/катушки, прутки, дробовая гильза и подшипник, 40-мм гильза и снаряд, порошки (`sulfur_powder`, `flint_powder`, `basalt_powder`, `rock_powder`), вёдра расплавленных металлов. Вкладки творческого меню: Precise Manufacturing / Materials / Casts / Components / Blueprints.

Требования: Create ≥ 6.0.0, TACZ ≥ 1.1.4. В наших docs — [compat.md](../compat.md) (пачка 4).

## [TaCZ] Curios For Ammo Box

<!-- auto:start card:curios-for-ammo-box -->
<a id="curios-for-ammo-box"></a>
- **Версия:** 1.2.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/curios-for-ammo-box)
- **Что это:** Позволяет носить ящик патронов TaCZ в слотах Curios. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:curios-for-ammo-box -->

### Что даёт игроку

Добавляет Curios-слот `ammo_box` (4 ячейки, для сущности `player`): ящики для патронов TACZ (тег `curios:ammo_box` = `tacz:ammo_box`) можно носить в этом слоте, не занимая инвентарь; есть миксины для TACZ addon. Зависит от Curios API. Своих предметов нет, конфиг не нужен.

## [TaCZ] IronsArms - Iron's Spells & Timeless and Classics Zero

<!-- auto:start card:ironsarms -->
<a id="ironsarms"></a>
- **Версия:** 3.0.6 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ironsarms)
- **Что это:** Запускает магию Iron's Spells 'n Spellbooks выстрелами из оружия TaCZ. _(источник: docs сборки)_
- **В сборке:** Iron's Spells через оружие TACZ
- **В наших docs:** [compat.md](../compat.md) (2), [russification-audit.md](../russification-audit.md) (2)
<!-- auto:end card:ironsarms -->

### Что даёт игроку

Заклинания Iron's Spells 'n Spellbooks из оружия TACZ — зеркало [ArsArms](#arsarms): в подсказке оружия появляются строки «Spell», «Level», «Mana» (блок `//--- Iron's Spells 'n Spellbooks ---//`), режим включается клавишей «Toggle Irons Spellbook mode» (подсказка `IronsMode: ON/OFF`). В коде отдельно обрабатываются заклинания «Телепорт» и «Падающая звезда» (данные отложенного каста), мана хранится в NBT `ironsarms:Mana`, есть миксин меню Arcane Anvil. Файла настроек в `pack/config` нет. Заметка в [compat.md](../compat.md) (пачка 4).

## [TACZ] LesRaisins Tactical Equipements

<!-- auto:start card:lr-tactical -->
<a id="lr-tactical"></a>
- **Версия:** 0.4.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/lr-tactical)
- **Что это:** Аддон TaCZ: метательное и холодное оружие для оружейных паков. _(источник: docs сборки)_
- **В сборке:** ганпак для TACZ
<!-- auto:end card:lr-tactical -->

### Как играть

Ганпак для TACZ от LesRaisins: не огнестрел, а **расходники, гранаты и холодное оружие**. Все вещи используются через клавиши TACZ-стиля (обычная атака и «особая атака/действие»); у каждой в подсказке — урон, радиус, время действия (с Shift — подробности).
1. Крафт идёт в «Кузнечном столе (LRT)» (`lrtactical:smith_table`, блок-верстак, «Классический кузнечный стол»).
2. Аптечки лечат и снимают эффекты, гранаты бросаются с выдернутой чекой (можно «варить» — зажать), ближний бой даёт две атаки (левая/правая) с разным уроном и кулдауном.
3. Ограничения настраиваются: разрушают ли гранаты блоки, сила тряски экрана, расходуется ли прочность холодного оружия, число целей одного удара.

### Предметы/блоки

| Группа | Предметы (описание из lang) |
|---|---|
| Расходники | `blood_pack` (быстро восстанавливает здоровье и голод), `condensed_milk` (сытость), `ibuprofen` (снимает негативные эффекты), `ai2` (останавливает кровотечение), `amoxycillin`, `carfak` (боевая аптечка), `cms` (хирургический набор: снимает все негативные эффекты), `surv12`, `goldenstar` (бальзам), `vaseline` (даёт сопротивление) |
| Гранаты | `m67` (осколочная), `rgn` (наступательная), `smoke_grenade` (дымовая завеса), `flash_grenade` (ослепляет и оглушает), `molotov` (поджигает область), `c4` (липкая, дистанционный подрыв; помечена как WIP) |
| Холодное оружие | `dagger`, `baseball_bat`, `karambit` (быстрые серии ударов) |
| Прочее | `flash_shield`, блок `smith_table` |

### Эффекты

`lrtactical:blinded` (ослепление от светошумовой), `lrtactical:deafened` (оглушение, звон), `lrtactical:flammable` (горючесть).

### Чары

`lrtactical:backstab` («Удар в спину») — для холодного оружия мода.

### Конфиги

Настройки мода (экран «Effects/Grenades/Melee» в lang): чёрная вспышка вместо белой при светошумовой, разрушение блоков гранатами, множитель тряски экрана (0 — выключить), расход прочности холодного оружия, максимум целей за удар. Файла в `pack/config` нет — создаётся при запуске. Русификация — [russification.md](../russification.md).

## [TaCZ] Timeless and Classics Zero

<!-- auto:start card:timeless-and-classics-zero -->
<a id="timeless-and-classics-zero"></a>
- **Версия:** 1.1.8-hotfix2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/timeless-and-classics-zero)
- **Что это:** Современный FPS-геймплей в Minecraft: настраиваемое огнестрельное оружие, обвесы, анимации (переработка TaC). _(источник: docs сборки)_
- **В наших docs:** [enchanting-table.md](../enchanting-table.md) (5), [client-checklist.md](../client-checklist.md) (1), [compat.md](../compat.md) (1), [roadmap.md](../roadmap.md) (1), [russification.md](../russification.md) (1), [ysm-models.md](../ysm-models.md) (1)
<!-- auto:end card:timeless-and-classics-zero -->

### Как играть

TACZ (Timeless and Classics Zero) — основа всего огнестрельного оружия сборки: оружие, патроны и обвесы здесь не отдельные предметы на каждую модель, а **один предмет с NBT**, а содержимое берётся из «ганпака» (в jar лежит `tacz_default_gun`; наш ганпак LesRaisins Tactical — отдельная карточка ниже).
1. Сделайте **Верстак оружейника** (`tacz:gun_smith_table`: 3 любых бревна в ряд, железный блок и 4 слитка железа). Оружие, обвесы и патроны крафтятся в его меню (кнопка «Craft», фильтр «по предмету в руке»); состав берётся из рецептов ганпака — например AK-47: 38 слитков железа, 6 лазурита, 10 брёвен.
2. Патроны кладутся в **Ящик для патронов** (`tacz:ammo_box`, варианты iron/gold/diamond/creative) — ПКМ по патронам в инвентаре кладёт их в ящик, ПКМ по пустому слоту достаёт. Носить ящик в отдельном слоте позволяет [Curios For Ammo Box](#curios-for-ammo-box); патроны можно делать конвейером Create — [Precise Manufacturing](#precise-manufacturing).
3. Клавиши (категория «Timeless & Classics Guns: Zero»): стрельба, прицеливание, перезарядка, осмотр (`inspect`), выбор режима огня, **Refit** (обвесы), взаимодействие с оружием, ближний бой (`melee`), ползти (`crawl`), зум. Допустимые режимы огня: SEMI / BURST / AUTO.
4. Обвесы ставятся в меню Refit: прицелы, глушители/дульные тормоза, рукоятки, лазеры, приклады, расширенные магазины (три уровня). Подсказки с характеристиками (урон, RPM, дальность, отдача) — на самом оружии, зажав клавишу обвесов.
5. Мишень (`tacz:target`), каменная статуя (`tacz:statue`) и вагонетка-мишень (`tacz:target_minecart`) — для пристрелки; вагонетка пишет в чат нанесённый урон и дистанцию.

Что именно в сборке меняют аддоны: [TACZ Tweaks](#tacz-tweaks) (механика боя и настройки), [TaCZ addon](#tacz-addon) (обвесы и интерфейс верстака), [Enchanted Arsenal](#enchanted-arsenal) (чары), [ArsArms](#arsarms) и [IronsArms](#ironsarms) (магия из оружия).

### Предметы/блоки

| id | Что делает |
|---|---|
| `tacz:modern_kinetic_gun` | Само оружие; модель задаёт NBT `GunId` (например `tacz:ak47`) и режим огня `GunFireMode` |
| `tacz:ammo` | Патроны; тип в NBT `AmmoId` (`tacz:9mm`, `tacz:556x45`, `tacz:12g`, `tacz:rpg_rocket` и т. д.) |
| `tacz:attachment` | Обвес; NBT `AttachmentId` |
| `tacz:gun_smith_table` | Верстак оружейника (крафт оружия, обвесов и патронов) |
| `tacz:workbench_a` / `_b` / `_c` | Варианты верстака; рецепт `workbench_a` с `BlockId: tacz:ammo_workbench` даёт «патронный верстак» (2 медных слитка, 1 железный, ящик для патронов) |
| `tacz:ammo_box` | Ящик для патронов: iron / gold / diamond, креативные (`creative`, `all_type_creative` — бесконечные патроны) |
| `tacz:target`, `tacz:statue`, `tacz:target_minecart` | Мишень, статуя, вагонетка-мишень |

Содержимое ганпака по индексу в jar: **54 единицы оружия** (пистолеты 14, винтовки 17, дробовики 6, снайперские 6, SMG 5, пулемёты 4 — `m249`, `rpk`, `minigun`, `fn_evolys`, РПГ/гранатомёты 2 — `rpg7`, `m320`), **24 типа патронов** (`9mm`, `45acp`, `556x45`, `762x39`, `762x54`, `308`, `338`, `50bmg`, `12g`, `40mm`, `rpg_rocket` и др.) и **99 обвесов** (прицелы от красных точек до `scope_standard_8x`, глушители, тормоза, рукоятки, лазеры, приклады, расширенные магазины, патронные модификаторы `ammo_mod_fmj/he/hp/i/slug`). Рецепт пороха без селитры: кремень + 2 сахара + 3 древесных угля → 3 пороха.

### Эффекты

Эффектов у мода нет; урон от пуль — собственные типы урона `tacz:bullet` (сообщение «застрелен»), блоки `iron_bars`, заборы и листва пулями пробиваются (тег `tacz:bullet_ignore`).

### События

В сундуке бонусного сундука при создании мира (`minecraft:chests/spawn_bonus_chest`) лежит пистолет Taurus 943 (`tacz:taurus943`, SEMI) и 38–45 патронов `22wmr`.

### Команды/гамерулы

Корень `/tacz` (нужны права оператора), подкоманды из jar: `reload` (перечитать ганпаки), `overwrite true|false` (перезаписывать ли стандартный ганпак при запуске), `convert` (конвертация старых ганпаков), `config` (серверные проверки стрельбы), `debug`, `attachment_lock`, `hide_tooltip_part`. Подсказки по аргументам — Tab.

### Конфиги

В `pack/config` собственных настроек TACZ нет: файлы (`tacz-*.toml`, папка ганпаков) создаются при первом запуске. Что настраивается (из lang): дальность звука выстрела, взрывные патроны (ломают ли блоки, поджигают), прохождение пуль через блоки, бьётся ли стекло, множитель скорости пуль, расход патронов в творческом режиме. Наши заметки о TACZ — [compat.md](../compat.md), [jei.md](../jei.md).

## [TACZ]Enchanted Arsenal

<!-- auto:start card:enchanted-arsenal -->
<a id="enchanted-arsenal"></a>
- **Версия:** 1.0.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/enchanted-arsenal)
- **Что это:** Зачарования для оружия TaCZ. _(источник: docs сборки)_
- **В сборке:** зачарования для оружия TACZ
- **В наших docs:** [enchanting-table.md](../enchanting-table.md) (4), [compat.md](../compat.md) (1)
<!-- auto:end card:enchanted-arsenal -->

### Чары

Десять зачарований для оружия TACZ (теги предметов `enchantedarsenal:guns` = `tacz:modern_kinetic_gun`); id вида `enchanted_arsenal:<имя>`:

| id | Эффект (из lang) |
|---|---|
| `hollow_point` | шанс на лишние пол-сердца урона, уровень повышает шанс |
| `armour_piercing` | наносит урон броне цели, уровень задаёт величину |
| `enflamed` («Cindering Shells») | цель на короткое время поджигается |
| `poison_tipped` | цель на короткое время отравляется |
| `illuminating` | цель на короткое время светится |
| `shell_shock` | у цели на короткое время ухудшается зрение |
| `lightweight` | ускоряет игрока |
| `juggernaught` | игрок получает сопротивление, но замедляется |
| `curse_of_overpressuring` | проклятие: иногда при выстреле «перегазует» — на время слепит стрелка |
| `curse_of_overrecoiling` | проклятие: иногда при выстреле сбивает дыхание — замедляет стрелка |

Проклятия — очень редкие, `juggernaught` — редкое, остальные — обычные (rarity в классе). Наложение — стандартным столом зачарований/наковальней. Заметка в [compat.md](../compat.md) (пачка 4); настроек нет.

## Create Big Cannons

<!-- auto:start card:create-big-cannons -->
<a id="create-big-cannons"></a>
- **Версия:** 5.11.4 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/create-big-cannons)
- **Что это:** Постройка больших пушек на Create. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (17), [roadmap.md](../roadmap.md) (6), [dedup.md](../dedup.md) (3), [friends.md](../friends.md) (3), [ntm.md](../ntm.md) (3), [performance.md](../performance.md) (2)
<!-- auto:end card:create-big-cannons -->

### Как играть

Create-аддон: **артиллерия, построенная из блоков**. Орудие собирается из стволов, казёнников и миксинов-дополнений по принципу контрапции Create, заряжается пороховыми зарядами и снарядами и стреляет по сигналу или вручную.
1. Материал ствола: дерево (`log_cannon_end`), чугун, бронза, сталь, нетерит-сталь (`nethersteel`) — от слабых к мощным. Детали льются в **литейном тазу** (Basin Foundry) из расплавленного металла (`molten_cast_iron/bronze/steel/nethersteel`) по формам (`*_cast_mould`) в песке для литья (`casting_sand`); «наборные» стволы (`built_up_*`) собирает **Cannon Builder**, есть инструмент `cannon_welder`.
2. Сверление ствола — **Cannon Drill** (бур `cannon_drill_bit`), заряжание — **Cannon Loader** (штанга/червь/пробойник `ram_head`, `worm_head`).
3. Установка: **Cannon Mount** (Create-контрапция, управление сигналом редстоуна/Create) или **Cannon Carriage** (тележка с колёсами — стрельба вручную, прокрутка меняет скорострельность в RPM). Поворот по горизонтали — Yaw Controller.
4. Боеприпасы: `powder_charge`, `big_cartridge` (многоразовая гильза), снаряды — `solid_shot`, `ap_shot`, `ap_shell`, `he_shell`, `shrapnel_shell`, `smoke_shell`, `fluid_shell`, `bag_of_grapeshot`, `mortar_stone`, `drop_mortar_shell`; взрыватели — `impact_fuze`, `delayed_impact_fuze`, `inertia_fuze`, `delayed_inertia_fuze`, `timed_fuze`, `proximity_fuze`, `wired_fuze`.
5. Подсказки на блоках показывают «характеристики материала» ствола (максимальная длина, прочность казённика) — у казёнников есть предел прочности по заряду (данные `big_cannon_breech_strength`). Справка по блокам брони (`block_armor`) и прочности — инструмент `block_armor_inspection_tool`.

Дополнения: [Create Big Cannons: Advanced Technologies](#create-big-cannons-advanced-technologies) (дульные тормоза, нарезные стволы, спаренные/тяжёлые автопушки, ракетные направляющие). Порт CBC: Nuclear в сборке — свой ([compat.md](../compat.md), п. 14).

### Предметы/блоки

| id (`createbigcannons:`) | Что делает |
|---|---|
| `cannon_mount`, `fixed_cannon_mount`, `cannon_carriage`, `yaw_controller` | Установки орудий |
| `cannon_builder`, `cannon_drill`, `cannon_loader`, `cannon_welder` | Сборка, сверление и заряжание |
| `*_cannon_barrel/chamber/end`, `*_cannon_layer`, `*_sliding_breech`, `*_quickfiring_breech` | Элементы стволов (бронза, чугун, сталь, нетерит-сталь) |
| `autocannon_*`, `autocannon_ammo_container`, `machine_gun_round` | Автопушки и их боеприпасы |
| `powder_charge`, `big_cartridge`, `guncotton`, `nitropowder`, `congealed_nitro`, `packed_gunpowder` | Заряды и их сырьё |
| `casting_sand`, `*_cast_mould`, `basin_foundry_lid`, `molten_*` | Литьё |
| `gas_mask`, `cannon_crafting_wand`, `traffic_cone` | Служебные/декоративные |

### Мобы

Мобов нет. Сущности-снаряды (`createbigcannons:<id>`): `shot`, `ap_shot`, `ap_shell`, `he_shell`, `shrapnel_shell`, `smoke_shell`, `fluid_shell`, `bag_of_grapeshot`, `flak_burst`, `grapeshot_burst`, `machine_gun_bullet`, `gas_cloud`, `primed_propellant`, `cannon_carriage`, `pitch_contraption`.

### Данжи/структуры (/locate)

Своих структур нет.

### Конфиги

В `pack/config` нет (файл настроек создаётся при запуске). Наши правки: сталь объединяется в `mekanism:ingot_steel` ([compat.md](../compat.md), Almost Unified).

## Create Big Cannons: Advanced Technologies

<!-- auto:start card:create-big-cannons-advanced-technologies -->
<a id="create-big-cannons-advanced-technologies"></a>
- **Версия:** 0.1.4b-1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/create-big-cannons-advanced-technologies)
- **Что это:** Аддон Create Big Cannons: новые пушки и механики. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (4), [performance.md](../performance.md) (2), [dedup.md](../dedup.md) (1), [friends.md](../friends.md) (1), [ntm.md](../ntm.md) (1), [roadmap.md](../roadmap.md) (1)
<!-- auto:end card:create-big-cannons-advanced-technologies -->

### Что даёт игроку

Расширение CBC новыми деталями и боеприпасами (всё — в вкладке «CBC: Advanced Technologies»):
- **Дульные устройства для больших пушек:** дульные тормоза, газоотводы (`fume_extractor`), глушители (`silencer`), нарезные стволы (`rifled_barrel`) — из чугуна, бронзы, стали, нетерит-стали (+ наборные варианты); формы для литья — `*_cast_mould`.
- **Автопушки:** спаренные (`twin_autocannon_*`) и вертикально-спаренные, **тяжёлая автопушка** (`heavy_autocannon_*`, ящик `heavy_autocannon_ammo_box`, креативный вариант) и их боеприпасы: AP, APDS, APDSFS, HE, HE-I, HEF, HEAT, дымовой.
- **Заряженные патроны** для больших пушек: `*_cartridge` («Charged …») и безгильзовые `*_caseless` — AP/HE/осколочные/дымовые/кассетные (`cluster`).
- **Ракеты:** рельсы и казённики ракетных установок (`wrought_iron_medium_rocket_rail`), ракеты AP/HE/HEI/зенитные и средние (AP, HE, HEF, HEAT).

Требует Create Big Cannons ([карточка](#create-big-cannons)). Известные мелочи: 4 таблицы добычи на несуществующие блоки литейных форм (некритично) и 3 модели рекойл-пружины тяжёлой автопушки, которые ModernFix не смог запечь ([compat.md](../compat.md), [performance.md](../performance.md)). Конфиги в `pack/config` нет; русского перевода нет ([russification-audit.md](../russification-audit.md)).

## Superb Warfare

<!-- auto:start card:superb-warfare -->
<a id="superb-warfare"></a>
- **Версия:** 0.8.9.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/superb-warfare)
- **Что это:** Военный мод: оружие, тяжёлые пушки и мощная техника. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (20), [russification.md](../russification.md) (8), [roadmap.md](../roadmap.md) (6), [dedup.md](../dedup.md) (4), [friends.md](../friends.md) (2), [ntm.md](../ntm.md) (2)
<!-- auto:end card:superb-warfare -->

### Как играть

Большая военная сборка: **60+ видов оружия, боеприпасы, мины, техника и авиация, исследования и чертежи**. Внутриигровая справка — Patchouli-книга `superb_warfare_manual` («Superb Warfare Manual»); русификация — [russification.md](../russification.md).
1. **Оружие и боеприпасы.** Оружие крафтится/улучшается по рецептам (много `*_smithing` — улучшение кузнечным столом), патроны делятся на `handgun/rifle/sniper/shotgun/heavy ammo` с одноимёнными ящиками. Запас патронов хранится в игроке; `/sbw ammo` его читает и меняет.
2. **Перки и перековка.** Блок «Gun Reforge Table» (`superbwarfare:reforging_table`) и предмет `reforging`; перки связаны с чипами данных (Ammo / Damage / Functional Perk Data Chip). Чертежи (`<оружие>_blueprint`) лежат в сундуках структур и исследуются и копируются в «Blueprint Research Table» (рецепты `*_blueprint_copy_researching`).
3. **Техника.** Собирается в «Vehicle Assembling Table» (`vehicle_assembling_table`) из модулей и деталей (мотор, пропеллер, гусеница, колесо, модули вооружения), энергию дают «Charging Station» (`charging_station`) и `biogas_generator`; есть блок «Vehicle Deployer». Ключ — `vehicle_key`, ремонт — `repair_tool`, `vehicle_damage_analyzer`.
4. **Полигон.** `target_deployer`, `dps_generator_deployer` и миномёт (`mortar_deployer` + основание/ствол/сошки) — для проверки оружия.
5. С кораблями Valkyrien Skies мод взаимодействует сам (отдельный Superb VS не нужен) — см. [compat.md](../compat.md), п. 15.

### Предметы/блоки

| id (`superbwarfare:`) | Что делает |
|---|---|
| Огнестрел: `ak_47`, `ak_12`, `m_4`, `hk_416`, `qbz_95`, `qbz_191`, `mp_5`, `vector`, `sks`, `rpk`, `m_60`, `m_2_hb`, `minigun`, `devotion`, `mk_14`, `svd`, `k_98`, `mosin_nagant`, `m_98b`, `ntw_20`, `awm`, `sentinel`, `hunting_rifle`, `bocek`, `marlin`, `m_870`, `aa_12`, `homemade_shotgun`, `glock_17/18`, `m_1911`, `mp_443`, `taser` | Стрелковое оружие всех классов |
| Тяжёлое: `rpg`, `m_79`, `javelin`, `igla_9k38`, `ql_1031`, `trachelium`, `insidious`, `secondary_cataclysm`, `super_star_shooter` | Гранатомёты, ПТРК/ПЗРК и «особые» образцы |
| Ближний бой: `knife`, `crowbar`, `hammer` (+ золотой/стальной/алмазный/незеритовый/твердосплавный), `steel_pipe`, `electric_baton`, `t_baton`, `cemented_carbide_*` (меч, кирка, топор, лопата, мотыга) | Холодное оружие и твердосплавный инструмент |
| Взрывчатка и мины: `hand_grenade`, `rgo_grenade`, `m18_smoke_grenade`, `claymore_mine`, `c4_bomb`, `lunge_mine`, `tm_62`, `ptkm_1r`, `blu_43_mine`, `defuser`, `detonator` | Гранаты, мины, сапёрное |
| `ammo_box`, `creative_ammo_box`, `handgun_ammo`, `rifle_ammo`, `sniper_ammo`, `shotgun_ammo`, `heavy_ammo` | Боеприпасы стрелкового оружия |
| Снаряды и ракеты: `large_shell_he/ap/cm/gs/wp`, `medium_shell_*`, `small_shell_*`, `small_rocket`, `medium_rocket_*`, `*_aerial_bomb`, `*_anti_air_missile`, `*_anti_ground_missile` | Боеприпасы техники и артиллерии |
| Блоки: `sandbag`, `barbed_wire`, `dragon_teeth`, `jump_pad`, `container`, `small_container`, `lucky_container`, `fumo_25` (радар), `aircraft_catapult`, `catapult_controller`, `superb_item_interface` | Укрепления и инфраструктура |
| Руды и слитки: `silver_ore`, `galena_ore`, `scheelite_ore` (+ deepslate), блоки свинца, вольфрама, стали, серебра, твёрдого сплава; `tungsten_rod`, `steel_plate`, `copper_plate` | Материалы для оружия |
| `medical_kit`, `handsome_goggles`, `tactical_terminal`, `skin_spray`, `dog_tag`, `transcript`, `towline` | Прочее |

### Мобы

Это не мобы мира, но у мода много сущностей-техники (`superbwarfare:<id>`): бронетехника (`lav_150`, `lav_25`, `lav_ad`, `bmp_2`, `bradley`, `yx_100`, `t_90a`, `m_1a_2`, `ztz_99a`, `prism_tank`), вертолёты/самолёты (`ah_6`, `mi_28`, `a_10a`, `ju_87`, `ac_130h`, `kv_16`, `kirov`), артиллерия и ПВО (`mk_42`, `mle_1934`, `bl_132`, `plz_05`, `fh_77bw`, `hpj_11`, `type_63`, `tow`, `annihilator`), лодки и лёгкая техника (`speedboat`, `tiny_speedboat`, `truck`, `sodayo_pick_up`, `wheel_chair`, `drone`, `swarm_drone`), оборонительные башни (`laser_tower`, `waveforce_tower`). Из особых: `senpai` («Beast Senpai»), `steel_coil`, `happiest_ghast`, `air_sheep`. Яйца призыва: `senpai_spawn_egg`, `steel_coil_spawn_egg`.

### Эффекты

`superbwarfare:shock`, `burn`, `strike_protection`, `trauma`, `phosphorus_fire`.

### События

Чертежи и «древний ЦПУ» добавляются в добычу через глобальные модификаторы (`common_chest`, `rare_chest`, `epic_chest`, `ancient_cpu_chest`): например редкие чертежи — в сундуках древнего города, бастионов, зарытых сокровищ, пустынных пирамид, иглу и храмов джунглей; `ancient_cpu` — в древнем городе.

### Команды/гамерулы

Корень `/sbw`: `ammo` (получить/задать/добавить боезапас, лимит хранения), `config` (включить/выключить опции), `tdm` (режим «Death Match» для сущностей). В jar есть и команды `ride` (посадить сущность на место техники), `dismount`, `skin` (скин техники), `loiter` (барражирование самолёта: X/Y/Z/R) — по сообщениям lang; точный синтаксис подскажет Tab.

### Конфиги

В `pack/config` нет. Наши правки: генерация серебра и свинца (галенит) Superb Warfare в Верхнем мире отключена KubeJS-модификаторами биомов `pack/kubejs/data/superbwarfare/forge/biome_modifier/` (дубль Occultism/Mekanism, [compat.md](../compat.md)); вся сталь объединяется в `mekanism:ingot_steel` (Almost Unified). Блок попадания по кораблям VS исправлен в `mods-src/mm-compat`. Связка Superb VS отключена. Атлас: текстуры мода тяжёлые (23 Мпкс) — см. [compat.md](../compat.md) про предел атласа.

## TaCZ addon

<!-- auto:start card:taczaddon -->
<a id="taczaddon"></a>
- **Версия:** 1.1.8.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/taczaddon)
- **Что это:** Расширение TaCZ: удобное управление обвесами. _(источник: docs сборки)_
- **В наших docs:** [jei.md](../jei.md) (2), [compat.md](../compat.md) (1), [russification-audit.md](../russification-audit.md) (1), [russification.md](../russification.md) (1)
<!-- auto:end card:taczaddon -->

### Что даёт игроку

Расширение TACZ «об управлении обвесами» и верстаком:
- клавиша «Quickly Switch Gun» (быстрая смена оружия);
- в верстаке оружейника — страницы рецептов, фильтр по свойству («Prop Filter»), сообщения о крафте и причинах отказа (не хватает материалов и т. п.);
- подсказка ящика патронов: Shift+ПКМ — собрать все патроны;
- **кузнечные рецепты (smithing)**: шаблон — осколок эха, основа — оружие TACZ, добавка — раковина наутилуса (расширенные магазины и патронные модификации) или подзорная труба (прицелы): рецепт `gun_smithing` с перечнем обвесов (для подзорной трубы — прицелы `sight_*`/`scope_*`, для раковины — расширенные магазины и патронные модификаторы).

Конфиг мода в `pack/config` не лежит. В [jei.md](../jei.md) отмечено «проверить» (рецепты `gun_smithing`).

## TaCZ Tweaks

<!-- auto:start card:tacz-tweaks -->
<a id="tacz-tweaks"></a>
- **Версия:** 2.14.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/tacz-tweaks)
- **Что это:** Набор твиков и мелких функций для TaCZ. _(источник: docs сборки)_
- **В наших docs:** [enchanting-table.md](../enchanting-table.md) (1), [russification-audit.md](../russification-audit.md) (1)
<!-- auto:end card:tacz-tweaks -->

### Что даёт игроку

Сборник настроек боя TACZ (все — в конфиге мода, создаётся при запуске; в `pack/config` своего файла нет):
- **Свобода действий:** стрельба на бегу, бег при перезарядке, перезарядка во время стрельбы, смена режима огня при стрельбе, ручное досылание патрона (ЛКМ), выгрузка магазина (клавиша «Unload»), запрет стрельбы под водой.
- **Клавиши:** «Reduce Sensitivity», «Tilt Gun» (наклон оружия — настоящая механика, а не только анимация).
- **Реализм:** Projectile Protection снижает урон пуль; эндермены уворачиваются от пуль; улучшенный расчёт разброса (приседание + прицел складываются); запрет Refit в режиме приключения; бесконечные патроны не расходуются.
- **Модификаторы характеристик:** множители/добавки/Lua-функции для урона, урона по игрокам, множителя хедшота, бронебойности, скорости, гравитации, трения, времени прицеливания, разброса, RPM (максимум 1200) и отдачи.
- **Совместимость:** попадания по частям тела при FirstAid и Legendary Survival Overhaul, столкновения пуль и взрывы по кораблям Valkyrien Skies, Immersive Vehicles; ползание (`crawl`), подсказка RPS вместо RPM, подавление/замена звуков попаданий и маркеров.

Что в сборке — [compat.md](../compat.md).

<!-- cards:end -->
