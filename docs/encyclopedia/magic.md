# Магия

Заклинания, ритуалы, мана, кровь и дополнения к магическим модам.

<!-- auto:start toc -->
Модов в категории: **36**.

- [Animus](#animus)
- [ArcaneAdditions](#arcaneadditions)
- [Ars Additions](#ars-additions)
- [Ars Artifice](#ars-artifice)
- [Ars Controle](#ars-controle)
- [Ars Creo](#ars-creo)
- [Ars Elemental](#ars-elemental)
- [Ars Instrumentum](#ars-instrumentum)
- [Ars Magica: Legacy](#ars-magica-legacy)
- [Ars Nouveau](#ars-nouveau)
- [Ars Ocultas](#ars-ocultas)
- [Ars Technica](#ars-technica)
- [Blood Magic](#blood-magic)
- [Botania](#botania)
- [Botanical Machinery](#botanical-machinery)
- [Botanical Machinery Extra](#botanical-machinery-extra)
- [Create: Occult Engineering](#create-occult-engineering)
- [Deeper and Darker: Spellbooks](#deeper-and-darker-spellbooks)
- [Dragon Magic And Relics](#dragon-magic-and-relics)
- [Electroblob's Wizardry Redux](#electroblobs-wizardry-redux)
- [Extra Golems](#extra-golems)
- [Extrabotany](#extrabotany)
- [Factions And Curiosities](#factions-and-curiosities)
- [GTBC's Geomancy Plus](#gtbcs-geomancy-plus)
- [Iron's Recipe Additions](#irons-recipe-additions)
- [Iron's Spells 'n Spellbooks](#irons-spells-n-spellbooks)
- [Mahou Tsukai](#mahou-tsukai)
- [Mana and Artifice](#mana-and-artifice)
- [Mechanical Botania](#mechanical-botania)
- [MNAOverpowered](#mnaoverpowered)
- [MythicBotany](#mythicbotany)
- [Occultism](#occultism)
- [Occultism Ritual Satchel](#occultism-ritual-satchel)
- [Spell Forge for Electroblob's Wizardry: Redux](#spell-forge-for-electroblobs-wizardry-redux)
- [Tome of Blood: Rebirth](#tome-of-blood-rebirth)
- [Too Many Glyphs](#too-many-glyphs)

Вернуться: [оглавление справочника](INDEX.md).
<!-- auto:end toc -->

## Общее по категории

_Обзор категории — вручную, следующие этапы._

---

<!-- cards:start -->
## Animus

<!-- auto:start card:animus -->
<a id="animus"></a>
- **Версия:** 1.20.1-3.0.35 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/animus)
- **Что это:** Добавка к Blood Magic: больше магии. _(источник: docs сборки)_
- **В сборке:** Blood Magic
- **В наших docs:** [russification.md](../russification.md) (9), [compat.md](../compat.md) (8), [roadmap.md](../roadmap.md) (7), [client-checklist.md](../client-checklist.md) (1), [jei.md](../jei.md) (1), [russification-audit.md](../russification-audit.md) (1)
<!-- auto:end card:animus -->

### Как играть

Версия 3.0.35: аддон к [Blood Magic](#blood-magic) (описание в мете: «new tools, mechanics, and automation to blood magic rituals and systems»). Требует Blood Magic.
- Добавляет ступень **Transcendent Blood Orb** (`blood_orb_transcendent`) сверху обычных шаров, новые **Сигилы** и **Реагенты** (`reagent*` — вставляются в сигилы/ритуалы).
- Оружие и снаряжение «воли»: копья (`spear_iron`, `spear_diamond`, `spear_bound`, `spear_sentient`), `sentient_bow`, `hellforged_bow`, `sentient_shield`, `runic_sentient_scythe`, `hand_of_death`.
- Инструменты: `sanguine_diviner` (расширенный ритуальный диагност), `ritual_designer`, `key_binding`.
- Растения и камни: Блод-вуд (`blood_wood`, `blood_sapling`, `blood_leaves`, `blood_core`), `willful_stone` во всех цветах, `crystallized_demon_will_block`, `diabolical_fungi`, `imperfect_ritual_stone`, `arcane_rune`, `rune_unleashed_nature`.
- Добавляет атрибуты (`animus:`): `unarmed_damage`, `bonus_sacrifice`, `bonus_self_sacrifice`, `bonus_demon_will`, `sigil_cost_reduction`.

### Предметы/блоки

| id (`animus:…`) | Что даёт |
|---|---|
| `blood_orb_transcendent` | Шар высшей ступени |
| `sigil_builder`, `sigil_chains`, `sigil_consumption`, `sigil_leach`, `sigil_storm`, `sigil_heavenly_wrath`, `sigil_remedium`, `sigil_reparare`, `sigil_transposition`, `sigil_free_soul`, `sigil_temporal_dominance`, `sigil_equivalency`, `sigil_boundless_nature`, `sigil_monk`, `sigil_crimson_will` | Новые Сигилы (названия — в ключах; описание — в игровых подсказках и в JEI) |
| `reagent*` | Реагенты для этих сигилов |
| `spear_*`, `sentient_bow`, `hellforged_bow`, `sentient_shield`, `hand_of_death` | Оружие |
| `blood_apple`, `fragment_healing`, `blood_infused_spellbook`, `mob_soul` | Расходники и книги |

### Мобы

Сущности — только снаряды: `animus:sentient_arrow`, `animus:hellforged_arrow`.

### Конфиги

В `pack/config` отдельных файлов нет. Мод русифицирован — [russification.md](../russification.md), в JEI есть свой плагин — [jei.md](../jei.md).

## ArcaneAdditions

<!-- auto:start card:arcaneadditions -->
<a id="arcaneadditions"></a>
- **Версия:** forge-1.10.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/arcaneadditions)
- **Что это:** Спутник Mana and Artifice: новые компоненты заклинаний, ритуалы и предметы; часть меняет баланс или даёт совместимость. _(источник: docs сборки)_
- **В сборке:** дополнение M&A: компоненты заклинаний, ритуалы
- **В наших docs:** [jei.md](../jei.md) (6), [compat.md](../compat.md) (2), [russification-audit.md](../russification-audit.md) (2), [russification.md](../russification.md) (2)
<!-- auto:end card:arcaneadditions -->

### Как играть

ArcaneAdditions 1.10.0 (`arcaneadditions`) — «a wealth of new magical mysteries» для [Mana and Artifice](#mana-and-artifice). Требует `mna`.
- **Компоненты заклинаний** (`arcaneadditions:components/*`): `astral_projection` (астральная проекция), `counterspell`, `enrage`, `ice_block`, `life_link`, `path`, `plow`, `polymorph` (требует заполненный Crystal Phylactery во второй руке), `strip`, `transfuse`, `tree_stride` (перемещение между сохранёнными точками по корневой сети).
- **Ритуалы:** «Ritual of the Little Friend» (`bind_familiar`, нужен заполненный кристалл-ловушка) и «Ritual of Inculcation» (`train_familiar`); достижение `summon_familiar`.
- Блок `scribes_bench` (Scribe's Bench), предмет `soulsearchers_lens`.
- Эффекты `arcaneadditions:` `anti_magic`, `astral_projection`, `enraged`, `ice_block` (+`ice_block-exhaustion` «Hypothermia»), `life_link`, `polymorph`. Сущность `ice_block`.

### Мобы

Своих мобов нет, кроме сущности-обёртки `arcaneadditions:ice_block`.

### Конфиги

В `pack/config` файлов нет; список рецептов — в JEI ([jei.md](../jei.md)).

## Ars Additions

<!-- auto:start card:ars-additions -->
<a id="ars-additions"></a>
- **Версия:** 1.6.7 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ars-additions)
- **Что это:** Аддон Ars Nouveau: мелкие правки и удобства. _(источник: docs сборки)_
- **В наших docs:** [russification.md](../russification.md) (3), [compat.md](../compat.md) (2)
<!-- auto:end card:ars-additions -->

### Как играть

Ars Additions 1.6.7 (`ars_additions`) — «small tweaks and quality of life additions» для Ars Nouveau.
- **Амулеты-обереги** (Charm): `undying_charm` (Second Wind), `fall_prevention_charm`, `fire_resistance_charm`, `night_vision_charm`, `dispel_protection_charm`, `ender_mask_charm`, `powdered_snow_walk_charm`, `sonic_boom_protection_charm`, `golden_charm`.
- Новые глифы: `Mark`, `Recall`, `Retaliate` (эффект `marked`).
- Свитки и кодексы: `exploration_warp_scroll`, `nexus_warp_scroll`, `stabilized_warp_index`, `codex_entry`, `lost_codex_entry`, `ancient_codex_entry` (учат глифам).
- Ритуалы: `ritual_chunk_loading` («Arcane Permanence»), `ritual_locate_structure` («Locate Structure»).
- Утилиты: `handy_haversack`, `advanced_dominion_wand`, `bound_wayfinder`, `ender_source_jar`, `enchanting_wixie_cauldron`; блоки-декор (`archwood_lantern`, `golden_chain`, треснувший `sourcestone`).

### Данжи/структуры (/locate)

`ars_additions:arcane_library`, `ars_additions:nexus_tower`, `ars_additions:ruined_portal`, `ars_additions:ruined_portal_large` (например, `/locate structure ars_additions:arcane_library`).

### Конфиги

В `pack/config` файлов нет. Шум о «Incorrect key» при первом запуске — [compat.md](../compat.md), раздел «Ars Nouveau и аддоны».

## Ars Artifice

<!-- auto:start card:ars-artifice -->
<a id="ars-artifice"></a>
- **Версия:** 2.0.4 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ars-artifice)
- **Что это:** Аддон Ars Nouveau: система применения заклинаний по событиям. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:ars-artifice -->

### Что даёт игроку

Ars Artifice 2.0.4: механика «Artificing» — **курио, автоматически кастующие заклинание при событиях** с игроком. Верстак `artificers_workbench`, `spell_gem_t1`/`t2`/`t3` («Spell Gem»), `spell_storing_amulet`, `spell_storing_belt`, `spell_storing_ring`. В JEI аддон без плагина, у него есть «пробел» (`no_damage_enchantment`) — [jei.md](../jei.md).

## Ars Controle

<!-- auto:start card:ars-controle -->
<a id="ars-controle"></a>
- **Версия:** 1.1.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ars-controle)
- **Что это:** Аддон Ars Nouveau: больше контроля над заклинаниями. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:ars-controle -->

### Что даёт игроку

Ars Controle 1.1.1: «increasing control» над заклинаниями — предмет `remote` («Remote») и блок `warping_spell_prism` («Warping Spell Prism»). Книга Patchouli аддона заглушена и выправлена нашим датапаком (`pack/kubejs/data/ars_controle/patchouli_books`) — [compat.md](../compat.md).

## Ars Creo

<!-- auto:start card:ars-creo -->
<a id="ars-creo"></a>
- **Версия:** 4.3.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ars-creo)
- **Что это:** Связка Ars Nouveau и Create. _(источник: docs сборки)_
- **В сборке:** Ars Nouveau × Create
- **В наших docs:** [compat.md](../compat.md) (1), [dedup.md](../dedup.md) (1), [russification.md](../russification.md) (1)
<!-- auto:end card:ars-creo -->

### Что даёт игроку

Ars Creo 4.3.0: совместимость Ars Nouveau и Create — блок `starbuncle_wheel` («Starbuncle Wheel», мост к Create). Конфигов в `pack/config` нет.

## Ars Elemental

<!-- auto:start card:ars_elemental -->
<a id="ars_elemental"></a>
- **Версия:** 1.20.1-0.6.8.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/ars_elemental-1.20.1-0.6.8.0.jar`
- **Что это:** Аддон Ars Nouveau о стихийных школах: фокусы, экипировка, новые глифы и скрытые комбинации. _(источник: docs сборки)_
- **В сборке:** 4 стихийные школы Ars Nouveau
- **В наших docs:** [compat.md](../compat.md) (4), [russification.md](../russification.md) (2)
<!-- auto:end card:ars_elemental -->

### Как играть

Ars Elemental 0.6.8.0 (модид `ars_elemental`) — аддон Ars Nouveau: четыре стихии (Огонь, Вода, Воздух, Земля) + «Некромантия» и «Манипуляция», 54 новых глифа и снаряжение.
- **Фокусы стихий** (`fire_focus`, `water_focus`, `air_focus`, `earth_focus` и `lesser_*`; `necrotic_focus`) усиливают заклинания своей стихии; **Браслеты** (`fire_bangle`, `water_bangle`, `air_bangle`, `earth_bangle`, `summon_bangle`, `anima_bangle`) и тома `*_caster_tome` — готовые заклинания.
- Глифы: `Spark` / `Discharge` (разряд), `Mana Bubble` (щит из маны), `Watery Grave`, `Conjure Terrain`, `Poison Spores`, `Phantom Grasp`, `Spike`, `Envenom`, `Homing Projectile`, `Arc Projectile` (+ «Propagate»), и семейство фильтров `Filter: Aquatic/Aerial/Fireproof/Flammable/Insect…`.
- **Башни** (`fire_turret`, `water_turret`, `air_turret`, `earth_turret`, `manipulation_turret`), **призмы** (`advanced_prism` + линзы `*_prism_lens`), **подъёмники** (`water_upstream`, `magma_upstream`, `air_upstream`), `spell_mirror`.
- Помощники: сирена (`siren_charm`, `siren_entity`), Flarecannon (`firenando_charm`), яйца и фамильяры.
- Жёлтое дерево: `yellow_archwood` («Flashing Archwood»).

### Предметы/блоки

| id (`ars_elemental:…`) | Что даёт |
|---|---|
| `spell_horn`, `fire_focus`/`water_focus`/`air_focus`/`earth_focus` | Рог зачарователя; фокусы стихий |
| `*_bangle`, `*_caster_tome`, `caster_bag`, `curio_bag` | Браслеты, тома, сумки заклинателя и безделушек |
| `thread_shock`, `thread_spore`, `thread_summon` | Нити улучшения брони |
| `everfull_urn`, `mermaid_rock` | Урна бесконечной воды; Святилище сирены |
| `ritual_*` | Ритуалы: `ritual_squirrels`, `ritual_tesla_coil`, `ritual_repulsion`, `ritual_attraction`, `ritual_detection` |
| `mark_of_mastery`, `anima_essence`, `debug` | Редкие предметы |

### Мобы

`siren_entity`, `firenando_entity` (Flarecannon), мятежные маги `fire_mage`, `water_mage`, `air_mage`, `earth_mage`, призываемые союзники (`summon_direwolf`, `summon_skelehorse`, `summon_camel`, `summon_dolphin`, `summon_strider`, `summon_vhex`, `summon_wskeleton`), `flashing_weald_walker`.

### Эффекты

`ars_elemental:` `enderference`, `watery_grave`, `mana_shield`, `static_charged`, `hellfire`, `life_link`, `enthralled`, `hymn_of_order`, `venom`.

### Данжи/структуры (/locate)

`ars_elemental:starbuncle_shrine` — `/locate structure ars_elemental:starbuncle_shrine`.

### Чары

`ars_elemental:mirror_shield` («Spell Reflection»), `ars_elemental:soulbound`.

### Конфиги

В `pack/config` файлов нет: аддон создаёт свои конфиги при первом запуске; шум «Incorrect key … was corrected» — [compat.md](../compat.md), раздел «Ars Nouveau и аддоны».

## Ars Instrumentum

<!-- auto:start card:ars_instrumentum -->
<a id="ars_instrumentum"></a>
- **Версия:** 1.20.1-4.1.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/ars_instrumentum-1.20.1-4.1.0.jar`
- **Что это:** Небольшой набор магических инструментов и утилит к Ars Nouveau. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (3), [russification.md](../russification.md) (3)
<!-- auto:end card:ars_instrumentum -->

### Что даёт игроку

Ars Instrumentum 4.1.0: набор утилит для Ars Nouveau — `arcane_applicator` (блок), `wizards_armarium`, `copy_paste_spell_scroll` (Scroll of Spelltransfer), `numeric_mana_charm` (Charm of Numeric Mana), `runic_storage_stone`, `scroll_of_save_starbuncle`, `fake_wilden_tribute` (Essence of Vanquished Foes). Конфигов в `pack/config` нет.

## Ars Magica: Legacy

<!-- auto:start card:ars-magica-legacy -->
<a id="ars-magica-legacy"></a>
- **Версия:** 1.20.1-1.5.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ars-magica-legacy)
- **Что это:** Наследие Ars Magica 2 от Mithion: создание и применение заклинаний из частей. _(источник: docs сборки)_
- **В сборке:** 1.20.1-1.5.0; зависимости GeckoLib, Patchouli уже в сборке; дополнений под 1.20.1 нет
- **В наших docs:** [compat.md](../compat.md) (10), [roadmap.md](../roadmap.md) (6), [bosses.md](../bosses.md) (4), [russification.md](../russification.md) (4), [client-checklist.md](../client-checklist.md) (2), [dedup.md](../dedup.md) (1)
<!-- auto:end card:ars-magica-legacy -->

### Как играть

Ars Magica: Legacy 1.5.0 (`arsmagicalegacy`) — порт классического Ars Magica 2: заклинания собираются в **Столе надписей** из частей (форма + компоненты + модификаторы), мана и «выгорание» (burnout) ограничивают каст. Справочник — книга Patchouli (`data/arsmagicalegacy/patchouli_books`).
- **Стартовые блоки:** `inscription_table` (Inscription Table, апгрейды `inscription_table_upgrade_tier_1..3`), `occulus` (интерфейс навыков, вкладки `occulus_tab`), `altar_core` (алтарь), `obelisk`, `celestial_prism`, `black_aurem` (данные `altar` и `obelisk_fuel`), `magic_wall`, `wizards_chalk`, руны (`blank_rune`, цветные `*_rune`, `rune_bag`) и `spell_rune`.
- **Материалы:** руды `chimerite`, `moonstone`, `sunstone`, `topaz`, `vinteum` (в том числе глубинные варианты), `arcane_ash`, `arcane_compound`, `purified_vinteum_dust`; Witchwood — местное дерево (`witchwood_*`).
- **Прогрессия:** очки навыков (`skill_point`), изучение навыков (`skill`), аффинности (`affinity`), опыт магии (`magic_xp`) — всё это настраивается командами ниже. Данные навыков и способностей — `data/arsmagicalegacy/arsmagicalegacy/skill` и `ability` (напр. `fire_resistance`, `frost_walker`, `night_vision`, `feather_falling`, `jump_boost`).
- **Заклинания:** 111 «частей» (`data/arsmagicalegacy/spell_parts`: `absorption`, `agility`, `aoe` и т. д.).
- Особенность сборки: единого «общего» мана-пула у модов нет, у Ars Magica своя мана; связка с Mana and Artifice — [compat.md](../compat.md), раздел «Mana and Artifice и Ars Magica: Legacy». Дополнений к Legacy под 1.20.1 в сборке нет.

### Предметы/блоки

| id (`arsmagicalegacy:…`) | Что делает |
|---|---|
| `spell_book`, `spell` | Книга заклинаний и готовое заклинание |
| `inscription_table` | Стол для сборки заклинаний |
| `occulus` | Блок/предмет доступа к дереву навыков |
| `crystal_wrench`, `magitech_goggles`, `rune_bag` | Инструменты и расходники |
| `mage_*`, `battlemage_*` | Броня мага и боевого мага (`*_helmet`/`chestplate`/`leggings`/`boots`) |
| `mana_cake`, `mana_martini` | Еда |
| `liquid_essence` | Жидкость-эссенция |
| `cerublossom`, `desert_nova`, `tarma_root`, `wakebloom`, `aum` | Растения |
| `gold_inlay`, `iron_inlay`, `redstone_inlay` | Инкрустации (ступени улучшения алтаря) |

### Мобы

Стражи стихий (яйца `*_guardian_spawn_egg`): `air_guardian`, `arcane_guardian`, `earth_guardian`, `ender_guardian`, `fire_guardian`, `ice_guardian`, `life_guardian`, `lightning_guardian`, `nature_guardian`, `water_guardian`; `dryad`, `mage`, `mana_creeper`. Прочие сущности — снаряды и зоны заклинаний (`blizzard`, `fire_rain`, `whirlwind`, `wave`, `wall`, `zone`, `mana_vortex`, `winters_grasp`, `nature_scythe`, `falling_star`).

### Эффекты

`arsmagicalegacy:` `agility`, `astral_distortion`, `burnout_reduction`, `clarity`, `entangle`, `flight`, `frost`, `fury`, `gravity_well`, `illumination`, `instant_mana`, `magic_shield`, `mana_boost`, `mana_regen`, `reflect`, `scramble_synapses`, `shield`, `shrink`, `silence`, `swift_swim`, `temporal_anchor`, `true_sight`, `watery_grave`. Атрибуты: `max_mana`, `mana_regen`, `max_burnout`, `burnout_regen`, `magic_vision`.

### Команды/гамерулы

Команды в `…arsmagicalegacy.server.commands`: `skill` (list / learn / forget / known …), `skill_point` (выдать/снять очки), `affinity` (unlock / reset …), `magic_xp` (очки или уровни). Нужны права оператора.

### Конфиги

В `pack/config` файлов нет.

## Ars Nouveau

<!-- auto:start card:ars-nouveau -->
<a id="ars-nouveau"></a>
- **Версия:** 4.12.7 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ars-nouveau)
- **Что это:** Создание заклинаний из глифов и магическая автоматизация. _(источник: docs сборки)_
- **В наших docs:** [russification.md](../russification.md) (18), [compat.md](../compat.md) (8), [roadmap.md](../roadmap.md) (8), [enchanting-table.md](../enchanting-table.md) (6), [russification-glossary.md](../russification-glossary.md) (4), [bosses.md](../bosses.md) (2)
<!-- auto:end card:ars-nouveau -->

### Как играть

Мод (автор Bailey Holl, модид `ars_nouveau`, версия 4.12.7) строит магию на **Источнике (Source)** и конструкторе заклинаний из **глифов**. Книга-гид в игре — Worn Notebook (`worn_notebook`, «Потёртый блокнот»).
- **Книга заклинаний** (`novice_spell_book` → `apprentice_spell_book` → `archmage_spell_book`) открывается клавишей «Open Book»; в ней заклинание собирается как цепочка: форма (проецирует: себя, прикосновение, снаряд…), эффекты и усилители (Amplify, AOE, Extend Time и т. д.). В jar 164 глифа с описаниями. Быстрый каст по слотам — клавиши `Quick Cast Slot 01…10`, переключение слотов — `Next/Previous Slot`.
- **Изучение глифов** — на **Столе глифов** (`scribes_table`) и через **Стол изменений** (`alteration_table`); глифы добываются в основном крафтом из **Исходных самоцветов** (`source_gem`) и сущностей/материалов по ходу прогрессии.
- **Источник** хранится в **Кувшинах** (`source_jar`), течёт по **Реле** (`relay`, `relay_splitter`, `relay_collector`, `relay_deposit`, `relay_warp`) и вырабатывается **Источниками-связями** (`agronomic_sourcelink`, `alchemical_sourcelink`, `mycelial_sourcelink` и др.) и ритуалами; питает **Зачаровывающий аппарат** (`enchanting_apparatus`) на **Арканных пьедесталах** (`arcane_pedestal`) — там делается снаряжение и усилители.
- **Фамильяры и спутники:** Старбанкл, Виксен-кузнец (Wixie), Вирлиспрайт, Дрюгми, Книжный змей, Аметистовый голем — приручаются/создаются через амулеты-обереги (`*_charm`) и настраиваются **Жезлом Доминиона** (`dominion_wand`).
- **Ритуалы** (`ritual_brazier` + `ritual_*`): проводятся на Ритуальной жаровне, список — см. ниже.
- Как работает связка Ars Nouveau с аддонами сборки: Ars Elemental, Ars Technica, Ars Instrumentum, Ars Creo, Ars Controle, Ars Additions, Ars Artifice, Ars Ocultas, Too Many Glyphs — карточки на этой странице. Шум «Incorrect key … was corrected» при первом запуске — [compat.md](../compat.md), раздел «Ars Nouveau и аддоны».

### Предметы/блоки

| id | Что делает |
|---|---|
| `novice_spell_book`, `apprentice_spell_book`, `archmage_spell_book` | Книги заклинаний трёх ступеней (больше слотов и глифов). `caster_tome` — одноразовый свиток-заклинание |
| `wand`, `dominion_wand` | Жезл зачарователя (кастует вписанное заклинание) и Жезл Доминиона (связывает фамильяров и механизмы, режим «строгий») |
| `source_jar`, `relay*`, `*_sourcelink` | Хранение, передача и выработка Источника |
| `enchanting_apparatus`, `arcane_pedestal`, `arcane_core`, `arcane_platform` | Алтарь усиления предметов |
| `scribes_table`, `alteration_table`, `imbuement_chamber` | Изучение глифов, нити улучшений брони (`thread_*`), наполнение эссенциями |
| `ritual_brazier`, `brazier_relay`, `runic_chalk` | Ритуалы (см. ниже) |
| `basic_spell_turret`, `rotating_spell_turret`, `rune` | Башенки и руны, автоматически кастующие заклинание |
| `scryers_oculus`, `scryers_crystal`, `scryer_scroll`, `repository`, `item_detector` | Логистика предметов и дальнее наблюдение |
| `potion_jar`, `potion_melder`, `potion_diffuser`, `potion_flask`, `*_flask_cannon` | Зелья: хранение, смешивание, распыление, метание |
| `mob_jar` | Контейнер для мобов (`Containment Jar`) |
| `warp_scroll`, `stable_warp_scroll`, `mimic_scroll`, `allow_scroll`, `deny_scroll` | Свитки телепортации и фильтров |
| Броня магов | `arcanist_*`/`battlemage_*`/`sorcerer_*` + нити `thread_*` (Spell Power, Magic Capacity, Warding, Undying, Life Drain…) |
| Растения | `magebloom`, `sourceberry_bush`, `bombegranate_pod`, `frostaya_pod`, `bastion_pod`, `mendosteen_pod`, `lava_lily`; блоки архивуда (`archwood_*`) и всех видов камня источника (`sourcestone*`) |
| Музыка | `music_disc_aria_biblio`, `music_disc_firel_the_wild_hunt`, `music_disc_thistle_the_sound_of_glass` |

**Ритуалы** (`ars_nouveau:ritual_*`): `animal_summon`, `awakening`, `binding`, `burrowing`, `challenge`, `cloudshaping`, `conjure_island_desert`, `conjure_island_plains`, `containment`, `disintegration`, `fertility`, `flight`, `flowering`, `forestation`, `gravity`, `harvest`, `moonfall`, `overgrowth`, `restoration`, `sanctuary`, `scrying`, `sunrise`, `warping`, `wilden_summon`.

### Мобы

| Моб (`ars_nouveau:…`) | Что |
|---|---|
| `starbuncle`, `whirlisprig`, `wixie`, `drygmy`, `bookwyrm`, `amethyst_golem` | Дружелюбные существа-помощники (переносят предметы, собирают ресурсы, крафтят зелья, чинят и т. п.) |
| `wilden_hunter`, `wilden_stalker`, `wilden_guardian` | Дикари — стража лагерей |
| `wilden_boss` | Дикий химера-босс (Wilden Chimera, 1000 HP) — [bosses.md](../bosses.md) |
| `summon_wolf`, `summon_horse`, `summon_skeleton`, `ally_vex`, `animated_block`, `animated_head` | Призванные заклинаниями союзники |
| `*_weald_walker` (blazing, cascading, flourishing, vexing) | Деревья-стражи биома «лес архивуда» |

Предмет `wilden_tribute` и ритуал `wilden_summon` связаны с призывом Дикарей. Дропы: `wilden_horn`, `wilden_spike`, `wilden_wing` — материалы для верхних глифов/усилителей.

### Эффекты

Эффекты в игре (`ars_nouveau:<id>`): `blasting`, `bounce`, `familiar_sickness`, `flight`, `freezing`, `glide`, `gravity`, `hex`, `immolate`, `magic_find`, `mana_regen`, `recovery`, `scrying`, `shield`, `shielding`, `shocked`, `snared`, `spell_damage`, `summoning_sickness`.

### Данжи/структуры (/locate)

Лагеря Дикарей: `ars_nouveau:guardian_wilden_den` (самый частый, вес 25), `ars_nouveau:hunter_wilden_den`, `ars_nouveau:stalker_wilden_den` (вес 1) — `/locate structure ars_nouveau:guardian_wilden_den`. Расстояние между лагерями 36 чанков с минимумом 10; рядом с деревнями не появляются (зона исключения 10 чанков). Биом: `ars_nouveau:archwood_forest` (`/locate biome ars_nouveau:archwood_forest`).

### Чары

`ars_nouveau:mana_boost`, `ars_nouveau:mana_regen`, `ars_nouveau:reactive`.

### Команды/гамерулы

`/ars-tome` (выдать том заклинаний), `/ars-reset`, `/ars-data` (выгрузка данных), `/ars-pathing`; регистрируются в `com.hollingsworth.arsnouveau.common.command`, нужны права оператора.

### Конфиги

В `pack/config` отдельных файлов нет: Ars Nouveau сам создаёт `config/ars_nouveau/` (по файлу на глиф и ритуал) при первом запуске — см. [compat.md](../compat.md), раздел «Ars Nouveau и аддоны». Книга Patchouli аддона Ars Controle заглушена нашим датапаком (`pack/kubejs/data/ars_controle/patchouli_books/…`), см. [compat.md](../compat.md).

## Ars Ocultas

<!-- auto:start card:ars-ocultas -->
<a id="ars-ocultas"></a>
- **Версия:** 1.20.1-1.2.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ars-ocultas)
- **Что это:** Интеграция Ars Nouveau и Occultism. _(источник: docs сборки)_
- **В сборке:** Ars Nouveau × Occultism
- **В наших docs:** [russification.md](../russification.md) (1)
<!-- auto:end card:ars-ocultas -->

### Что даёт игроку

Ars Ocultas 1.2.2 («Ars Nouveau corrupted by Occultism») — рецепты и записи в Worn Notebook для связки Ars Nouveau и Occultism:
- **Контейнер для духов:** духи Occultism работают внутри Containment Jar (`mob_jar`), не «распадаются» и подходят для автоматизации; Фолиот-уборщик в банке сам подбирает предметы и кладёт в соседний инвентарь.
- **Трансмутация:** серебро ↔ золото (Imbuement-рецепты `imbuement_silver_transmute`, `imbuement_gold_transmute`).
- Альтернативный рецепт `spirit_attuned_gem` (когда мало алмазов).

Конфигов в `pack/config` нет. Совпадения по JEI — [jei.md](../jei.md).

## Ars Technica

<!-- auto:start card:ars_technica -->
<a id="ars_technica"></a>
- **Версия:** 1.20.1-1.4.2 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/ars_technica-1.20.1-1.4.2.jar`
- **Что это:** Связка Ars Nouveau и Create. _(источник: docs сборки)_
- **В сборке:** Ars Nouveau × Create
- **В наших docs:** [compat.md](../compat.md) (4), [russification.md](../russification.md) (1)
<!-- auto:end card:ars_technica -->

### Как играть

Ars Technica 1.4.2 (`ars_technica`): связка Ars Nouveau и Create — заклинания превращаются в «станки».
- Глифы-станки (20): `Fuse` (слияние), `Press`, `Polish`, `Carve`, `Pack`, `Insert`, `Whirl` (центрифуга; с водой/огнём/Smelt/Hex — мытьё/копчение/плавка/«призрачная» обработка), `Obliterate` (молот; с Sensitive дробит предметы), `Telefeast`, `Superheat` (если включён в конфиге).
- Предметы: `technomancer_*` (шляпа, мантия, штаны, ботинки), `runic_spanner`, `spy_monocle`, `transmutation_focus`, `giant_experience_gem`, `gargantuan_experience_gem`, `calibrated_precision_mechanism`, нить `thread_pressure`.
- Блоки: `source_engine` (Source Motor — преобразует Источник во вращение) и `precise_relay`.
- В JEI у аддона нет своего плагина для брони-апгрейдов (`armor_upgrade` — «чужой тип без плагина»), см. [jei.md](../jei.md).

### Конфиги

В `pack/config` файлов нет. Нужны Create и `create_enchantment_industry` (в [jei.md](../jei.md) отмечено как отсутствующая зависимость).

## Blood Magic

<!-- auto:start card:blood-magic -->
<a id="blood-magic"></a>
- **Версия:** 3.3.8-50 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/blood-magic)
- **Что это:** Магия крови: алтари, ритуалы, сети души ради мощи. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (6), [russification.md](../russification.md) (4), [roadmap.md](../roadmap.md) (3), [russification-glossary.md](../russification-glossary.md) (2), [client-checklist.md](../client-checklist.md) (1), [dedup.md](../dedup.md) (1)
<!-- auto:end card:blood-magic -->

### Как играть

Версия в сборке — 3.3.8-50 (модид `bloodmagic`, WayofTime). Вся магия строится на **Сущностной сети (Soul Network)**: **Эссенция жизни (LP)** принадлежит игроку, к которому привязан предмет.
- **Старт.** Ранний мини-путь: `sacrificialdagger` (Жертвенный нож) → **Кровавый алтарь** (`altar`, наполняется ножом/блоком) → Слабый кровавый шар (`weakbloodorb`) → Сигилы (`airsigil`, `watersigil`, `lavasigil`, `voidsigil`, `divinationsigil` и т. д.). Шары выше уровнем: `apprenticebloodorb`, `magicianbloodorb`, `masterbloodorb`, `archmagebloodorb` — повышают ёмкость сети и открывают рецепты.
- **Руны алтаря** (`altarcapacityrune`, `accelerationrune`, `chargingrune`, `dislocationrune`, …) увеличивают ёмкость и скорость алтаря; есть усиленные варианты (`*2`).
- **Ритуалы**: `masterritualstone` + камни стихий (`airritualstone`…) на подложке из рун; по числу — 41 ритуал в lang (`ritual.bloodmagic.*`, см. ниже). Проводится Диагностом ритуалов (`ritualdiviner`) и настраивается `ritualtinkerer`.
- **Живая броня** (`livinghelmet`, `livingplate`, `livingleggings`, `livingboots`): растёт от действий игрока, улучшения (`living_armor/*.json` — 22 шт., например `arrow_protect`, `critical_strike`, `digging`, `elytra`, `jump`, `speed`, `repair`) ставятся томами (`upgradetome`) и Эволюционным ритуалом.
- **Воля демонов (Demon Will)**: убийства мобов оружием/душами — собирается в кристаллы (`defaultcrystal`, `corrosivecrystal`, `destructivecrystal`), включает «чувствующее» снаряжение (`soulsword`, `soulaxe`, `soulpickaxe`, `soulshovel`, `soulscythe`), **Тартарические самоцветы** (`soulgem*`) и Крепость демонов (`demoncrucible`, `demoncrystallizer`, `demonpylon`).
- **Алхимия**: `alchemytable`, `alchemicalreactionchamber`, `arcaneashes`, катализаторы (`*catalyst`) — для переработки руды (гравий/песок/фрагменты) и колб (`alchemy_flask*`).
- Дополнение к моду — [Animus](#animus); текст мода в сборке русифицирован — [russification.md](../russification.md).

### Предметы/блоки

| id | Что делает |
|---|---|
| `sacrificialdagger`, `daggerofsacrifice` | Нож для получения LP с игрока; `daggerofsacrifice` — Кинжал жертвы (вариант для жертвы) |
| `altar` | Кровавый алтарь: заполняет шары, делает предметы |
| `weakbloodorb` … `archmagebloodorb` | Кровавые шары (ёмкость сети и ступени) |
| `airsigil`, `watersigil`, `lavasigil`, `voidsigil`, `growthsigil`, `icesigil`, `miningsigil`, `bloodlightsigil`, `sigilofholding`, `sigilofmagnetism`, `sigilofsuppression`, `telepositionsigil`, `seersigil`, `divinationsigil` | Сигилы — переключаемые предметы, тратящие LP |
| `livinghelmet`, `livingplate`, `livingleggings`, `livingboots`, `upgradetome` | Живая броня и том улучшения |
| `soulsword`, `soulaxe`, `soulpickaxe`, `soulshovel`, `soulscythe`, `soulsnare`, `soulgem*` | Снаряжение «воли демонов», ловушка душ, Тартарические самоцветы |
| `alchemytable`, `alchemicalreactionchamber` | Алхимия |
| `demoncrucible`, `demoncrystallizer`, `demonpylon`, `dungeon_brick*`, `demonslate`, `etherealslate`, … | Крепость демонов и плиты-чертежи |
| `bleedingedge`, `experiencebook` | Hellforged Record, Tome of Peritia |

Помощники — `bow_power_anointment*`, `bow_velocity_anointment*`, `fortune_anointment*` и пр. (**помазания**, ложатся на оружие/инструмент; `data/bloodmagic/anointment`: `bow_power`, `bow_velocity`, `hidden_knowledge`, `holy_water`, `melee_damage`, `quick_draw`, `repairing`).

**Ритуалы** (`ritual.bloodmagic.<id>`): `altarBuilderRitual`, `animalGrowthRitual`, `armourEvolveRitual`, `cobblestoneRitual`, `condorRitual`, `containmentRitual`, `craftingRitual`, `crushingRitual`, `crystalHarvestRitual`, `crystalSplitRitual`, `downgradeRitual`, `ellipseRitual`, `expulsionRitual`, `featheredKnifeRitual`, `fellingRitual`, `forsakenSoulRitual`, `fullStomachRitual`, `geode`, `greenGroveRitual`, `groundingRitual`, `harvestRitual`, `interdictionRitual`, `jumpRitual`, `lavaRitual`, `magneticRitual`, `meteorRitual`, `placerRitual`, `portalRitual`, `pumpRitual`, `regenerationRitual`, `simpleDungeonRitual`, `speedRitual`, `sphereRitual`, `standardDungeonRitual`, `suppressionRitual`, `upgradeRemoveRitual`, `waterRitual`, `wellOfSufferingRitual`, `yawningVoidRitual`, `zephyrRitual` (+ тестовый `testRitual`).

### Мобы

Своих мобов в jar нет (в lang нет `entity.bloodmagic.*`): есть снаряды `EntitySoulSnare` и `EntityShapedCharge` (заряды `aug_shaped_charge`, `deforester_charge`).

### Эффекты

`bloodmagic:` `bounce`, `flight`, `gravity`, `grounded`, `hard_cloak`, `heavy_heart`, `obsidian_cloak`, `passivity`, `soft_fall`, `spectral_sight`, `suspended`.

### Данжи/структуры (/locate)

Генерируемых в мире структур нет (`worldgen/structure` пуст). Есть измерение-подземелье `bloodmagic:dungeon`, связанное с ритуалами `portalRitual`, `simpleDungeonRitual`, `standardDungeonRitual`; его биом и настройки шума — `none`.

### Команды/гамерулы

`/bloodmagic …` (классы `CommandBloodMagic`: подкоманды привязки `bind` и управления Сущностной сетью `network`).

### Конфиги

В `pack/config` файлов Blood Magic нет (используются значения по умолчанию).

## Botania

<!-- auto:start card:botania -->
<a id="botania"></a>
- **Версия:** 1.20.1-456-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/botania)
- **Что это:** Природная магия и техника: цветы-генераторы, мана, пестик и т. д. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (4), [dedup.md](../dedup.md) (4), [russification.md](../russification.md) (4), [bosses.md](../bosses.md) (2), [roadmap.md](../roadmap.md) (2), [russification-glossary.md](../russification-glossary.md) (2)
<!-- auto:end card:botania -->

### Как играть

Botania 1.20.1 (сборка 456, модид `botania`, автор Vazkii; поддержка — Tristaccount/Artemis). Магия растений: энергия **маны** добывается **генерирующими цветами**, по воздуху передаётся **излучателями маны**, копится в **пулах** и тратится на крафт и снаряжение. Справочник в игре — книга **Lexicon Botania** (Patchouli: `data/botania/patchouli_books`); в сборке есть русская локаль.
- **Старт.** Мистические цветы (`mystical_*`, генерируются патчами в мире: `black_mystical_flower_patch` … 16 цветов, плюс `mystical_mushrooms`) → лепестки → **Аптекарь** (`apothecary_*` — 11 биомных видов; лепестки + семена → цветы) → первый генерирующий цветок.
- **Генерация маны:** `endoflame` (горение), `hydroangeas`, `thermalily` (лава), `rosa_arcana` (опыт), `munchdew`, `entropinnyum` (взрывы TNT), `kekimurus` (торт), `gourmaryllis` (еда), `narslimmus`, `spectrolus`, `dandelifeon`, `rafflowsia`, `shulk_me_not`. Функциональные: `pure_daisy` (превращает блоки в живой камень/древесину), `orechid`/`orechid_ignem` (руда из камня), `tigerseye`, `jaded_amaranthus`, `bellethorn`, `dreadthorn`, `heisei_dream`, `fallen_kanade`, `exoflame`, `agricarnation`, `hopperhock`. У каждого есть «летающий» вариант (`floating_*`) и горшочный.
- **Передача и хранение:** `mana_spreader` (+ `redstone_`, `elven_`, `gaia_spreader`), `mana_pool` (+ `creative_pool`, `diluted_pool`, `fabulous_pool`), `mana_pylon` / `natura_pylon` / `gaia_pylon`, `mana_distributor`, `mana_void`, `mana_detector`, линзы `lens_*` (27 вариантов: скорость, мощность, отскок, огонь, поршень, магнит, свет, телепорт и др.), `spark` (искра-разветвитель).
- **Крафт на мане:** `runic_altar` (руны `rune_*`), `terra_plate` (Терра-сталь), `enchanter` (Алтарь зачарования), `brewery` (зелья), `alfheim_portal` (портал в Альфхейм; в jar — структуры-шаблоны `alfheim_portal`, `enchanter`, `gaia_ritual`, `gog_island`), `alchemy_catalyst`/`conjuration_catalyst`.
- **Снаряжение по ступеням:** манасталь → элементиум → терра-сталь (`terrasteel_*`, `terra_sword`, `terra_pick`, `terraform_rod`) → предметы Гайи; кольца (`mana_ring`, `aura_ring`, `magnet_ring`, `reach_ring`, `mining_ring`, `water_ring`, `pixie_ring`), подвески (`ice_pendant`, `lava_pendant`, `super_lava_pendant`).
- Boss-вызов: ритуал «Страж Гайи» по структуре `gaia_ritual` (Гайя-пилоны + Гайя-голова).
- Аддоны этой связки: [MythicBotany](#mythicbotany), [Extrabotany](#extrabotany), [Botanical Machinery](#botanical-machinery), [Botanical Machinery Extra](#botanical-machinery-extra), [Mechanical Botania](#mechanical-botania). Сравнение версии и доступности — [version-choice.md](../version-choice.md).

### Предметы/блоки

| id | Что делает |
|---|---|
| `twig_wand`, `dreamwood_wand` | Жезл Флоры: привязка передатчиков, настройка, взаимодействие с цветами |
| `mana_tablet`, `mana_ring`, `aura_ring` | Хранение и подзарядка маны в предметах |
| `livingrock`, `livingwood`, `dreamwood`, `shimmer*` | Строительные блоки и их версии (камень/дерево Botania) |
| `corporea_index`, `corporea_funnel`, `corporea_crystal_cube`, `corporea_spark` | Корпореа — сеть хранения и запроса предметов |
| `red_string_container`, `red_string_dispenser`, `red_string_fertilizer`, `red_string_comparator`, `red_string_relay`, `red_string_interceptor` | Красная нить: управление контейнерами и их автоматизацией |
| `horn_grass`, `horn_leaves`, `horn_snow`, `drum_wild`, `drum_gathering`, `drum_canopy` | Рога и барабаны: добыча растительности |
| `bifrost`, `bifrost_perm`, `prism`, `light_relay`, `light_launcher` | Радужные мосты и световые реле |

### Мобы

Мобов в основном нет: босс **Страж Гайи** (`botania:doppleganger`, 320 HP) вызывается ритуалом — [bosses.md](../bosses.md), и **Розовый Иссушитель** (`botania:pink_wither`, 300 HP) — [bosses.md](../bosses.md). Вспомогательные сущности: `pixie` (пикси), `spark`, `corporea_spark`, `mana_burst`, `pool_minecart`, `magic_missile`, `falling_star`, `thorn_chakram`, `babylon_weapon` (Treasury Weapon).

### Эффекты

`botania:` `soul_cross`, `feather_feet`, `emptiness`, `bloodthirst`, `allure`, `clear` (Absolution).

### Данжи/структуры (/locate)

Отдельных генерируемых структур у самого Botania нет: мир получает только цветы (`mystical_flowers`, `mystical_mushrooms`). Площадки игрок строит сам по шаблонам из jar (`data/botania/structures`: `alfheim_portal`, `enchanter`, `gaia_ritual`, `gog_island`). Цветок Loonium настраивается файлами `data/botania/loonium_config` (`default`, `ocean_ruins`, `village`).

### Чары

Своих чар в Botania нет.

### Команды/гамерулы

Особых команд в данных jar не найдено.

### Конфиги

В `pack/config` файлов Botania нет (значения по умолчанию).

## Botanical Machinery

<!-- auto:start card:botanical-machinery -->
<a id="botanical-machinery"></a>
- **Версия:** 1.20.1-3.0.10 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/botanical-machinery)
- **Что это:** Аддон Botania: автоматизация. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (3), [roadmap.md](../roadmap.md) (2)
<!-- auto:end card:botanical-machinery -->

### Что даёт игроку

Botanical Machinery 3.0.10 (`botanicalmachinery`): «машинные» версии алтарей Botania, работающие на энергии/мане и автоматизируемые: `mechanical_mana_infuser`, `mechanical_brewery`, `mechanical_apothecary`, `mechanical_daisy`, `mechanical_mana_pool`, `mechanical_runic_altar`, `industrial_agglomeration_factory`, `alfheim_market`, `mana_battery` (+ `mana_battery_creative`), материал `mana_emerald`. Рецепты — в JEI ([jei.md](../jei.md)).

### Конфиги

В `pack/config` файлов нет.

## Botanical Machinery Extra

<!-- auto:start card:botanical-extra-machinery -->
<a id="botanical-extra-machinery"></a>
- **Версия:** 1.20.1-1.1.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/botanical-extra-machinery)
- **Что это:** Дополнение к Botanical Machinery. _(источник: docs сборки)_
- **В сборке:** тянет Applied Energistics 2 (одобрено пользователем)
- **В наших docs:** [compat.md](../compat.md) (2), [roadmap.md](../roadmap.md) (2)
<!-- auto:end card:botanical-extra-machinery -->

### Что даёт игроку

Botanical Extra Machinery 1.1.2 (`botanicalextramachinery`) — расширение Botanical Machinery с четырьмя ступенями машин: `base_*`, `upgraded_*`, `advanced_*`, `ultimate_*` — для `mana_pool`, `runic_altar`, `daisy`, `apothecary`, `industrial_agglomeration_factory`, `alfheim_market`, `orechid`; отдельно `greenhouse`, `jaded_amaranthus`.
- Улучшения (`upgrade_*`: хранение маны/энергии, слоты, множители цветов 4×…64×, теплица, генерация маны) и катализаторы (`catalyst_*`: бесконечные вода, камень, дерево, семена, мана, живой камень; `catalyst_speed`).
- Материалы: искры (`*_spark`), драконьи камни и слитки малахита/шафрана/тени/багрянца/кристалла.
- Рецепты — в JEI ([jei.md](../jei.md)). В `pack/config` файлов нет.

## Create: Occult Engineering

<!-- auto:start card:occult-engineering -->
<a id="occult-engineering"></a>
- **Версия:** 1.20.1-0.11.4 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/occult-engineering)
- **Что это:** Связка Create и Occultism. _(источник: docs сборки)_
- **В сборке:** Occultism × Create
- **В наших docs:** [compat.md](../compat.md) (58), [dedup.md](../dedup.md) (29), [friends.md](../friends.md) (15), [version-choice.md](../version-choice.md) (9), [enchanting-table.md](../enchanting-table.md) (8), [performance.md](../performance.md) (7)
<!-- auto:end card:occult-engineering -->

### Как играть

Create: Occult Engineering 0.11.4 (`occultengineering`): связка Occultism и техники.
- Новые **мелки** (`chalk_brass`, `chalk_copper`, `chalk_zinc`) и блоки пентаклей (`brass_chalk`, `copper_chalk`, `zinc_chalk`), `pentacle_altar`, `empty_pentacle_schematic`/`pentacle_schematic`.
- Ритуалы крафта: `ritual_dummy_craft_mechanical_chamber`, `ritual_dummy_craft_otherworld_detector`, `ritual_dummy_craft_phlogiport`.
- Машины: `mechanical_chamber`, `mechanical_pulverizer`, `phlogiport`, `otherworld_detector`, `fan_enspirit_catalyst`; материалы `phlogiston`, `silver_phlogistate`, `sterling_silver_ingot`, `spirit_solution`.
- Моб: `occultengineering:puca` (Púca; книги `book_of_binding_puca`, яйцо `puca_spawn_egg`), блок `pucalith`.
- Справочник: `encyclopedia_of_souls`; `combined_goggles` («Otherworldly Engineer's Goggles»).

### Конфиги

В `pack/config` файлов нет. JEI-плагин (`occultengineering`) есть — [jei.md](../jei.md); проверка запуска — [compat.md](../compat.md).

## Deeper and Darker: Spellbooks

<!-- auto:start card:deeper-and-darker-spellbooks -->
<a id="deeper-and-darker-spellbooks"></a>
- **Версия:** 1.3.1-1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/deeper-and-darker-spellbooks)
- **Что это:** Связка Iron's Spells 'n Spellbooks и Deeper and Darker. _(источник: docs сборки)_
- **В сборке:** Iron's Spells × Deeper and Darker
- **В наших docs:** [compat.md](../compat.md) (7), [enchanting-table.md](../enchanting-table.md) (2), [bosses.md](../bosses.md) (1), [roadmap.md](../roadmap.md) (1), [version-choice.md](../version-choice.md) (1)
<!-- auto:end card:deeper-and-darker-spellbooks -->

### Что даёт игроку

Deeper and Darker: Spellbooks 1.3.1 (`darkermagic`) — мост между Deeper and Darker и [Iron's Spells 'n Spellbooks](#irons-spells-n-spellbooks) (автор отмечает «very early development»):
- Броня `warden_mage_*` (Warden Battlemage), посох `whisper_staff` («The Whispers»), `warden_stick`, книга `volume_of_the_deep`.
- Призываемые сущности заклинаний: `summoned_warden`, `summoned_shattered`, `summoned_sculk_centipede`, `summoned_sculk_snapper`.
Дополнительные связки с Cataclysm и Deeper and Darker (`deeper_and_darker_to_cataclysm`) лежат отдельными jar в `pack/mods/`. Конфигов в `pack/config` нет.

## Dragon Magic And Relics

<!-- auto:start card:dragon-magic-and-relics -->
<a id="dragon-magic-and-relics"></a>
- **Версия:** 3.2.14 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/dragon-magic-and-relics)
- **Что это:** Расширение Mana and Artifice: драконья магия и реликвии. _(источник: docs сборки)_
- **В сборке:** дополнение M&A
- **В наших docs:** [enchanting-table.md](../enchanting-table.md) (2), [compat.md](../compat.md) (1)
<!-- auto:end card:dragon-magic-and-relics -->

### Как играть

Dragon Magic And Relics 3.2.14 (`dmnr`) — расширение [Mana and Artifice](#mana-and-artifice) («expand your magic to compete with gods»): реликвии и амулеты для Curios, три «ранга» магической брони и улучшения-печати. Зависит от `mna`, `magichem`, `curios`, `caelus`, `geckolib`.
- **Броня драконьих магов:** `wild_`, `infernal_`, `abyssal_`, `arch_dragon_mage_*` (шлем/нагрудник/штаны/ботинки).
- **Кольца и амулеты:** `ring_of_power`, `ring_of_ruling`, `ring_of_spell_storing`, `angel_ring` (полёт; `fallen_angel_ring` — туманная форма), `devil_ring` (огненные заклинания превращаются в «адский огонь»), `amulet_of_dragon_power`, `amulet_of_hellfire`, `faction_amulet` (предотвращает набеги фракций M&A, пока не нанесёшь удар первым), `regeneration_amulet`, `curse_protection_amulet`, `collectors_amulet`, `hydra_crown`, `bracelet_of_friendship` (без дружественного огня).
- **Пояса:** `glass_cannon_belt` (весь урон ×2 туда и обратно), `sturdy_belt` (×0,5), `belt_of_life*`.
- **Печати улучшения** `upgrade_seal_*` (около 30 печатей: полёт, скорость, туманная форма, мана, здоровье, огнестойкость, водное дыхание и др.) и `modifier_book` — настраивают предметы.
- Другое: `rift_emitter` (блок), `dragon_core`, `weather_fairy_staff`, `the_clickers_cookie`, `key_of_homestead`.

### Эффекты

`dmnr:` `ultimate_armor`, `hellfire`, `fly_disabled` (Disabled DM&R Fly), `peace`, `broken_peace`, `spell_storing_cooldown`, `elytra` (Angel Fly).

### Конфиги

В `pack/config` файлов нет.

## Electroblob's Wizardry Redux

<!-- auto:start card:electroblobs-wizardry-redux -->
<a id="electroblobs-wizardry-redux"></a>
- **Версия:** 0.8.9-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/electroblobs-wizardry-redux)
- **Что это:** Возрождение классического Electroblob's Wizardry для 1.20.1: семь стихий и четыре ступени заклинаний, посохи, кристаллы, броня и артефакты, Таинственный верстак и Алтарь наполнения, башни магов, святилища и обелиски; в сборке дополнен модом Wizardry Restored. _(источник: docs сборки)_
- **В наших docs:** [wizardry-port.md](../wizardry-port.md) (73), [compat.md](../compat.md) (32), [roadmap.md](../roadmap.md) (10), [jei.md](../jei.md) (7), [client-checklist.md](../client-checklist.md) (6), [russification.md](../russification.md) (5)
<!-- auto:end card:electroblobs-wizardry-redux -->

Автор порта Redux — Binaris (лицензия MIT/GPL-3.0, см. [wizardry-port.md](../wizardry-port.md)); оригинал 1.12.2 — Electroblob. Модид `ebwizardry`, сторона — both. Мана у мода своя (хранится в посохах и магической броне).

**Связь с нашей сборкой.** В Redux 0.8.9 нет примерно 61 заклинания оригинала. Их (и оживлённые артефакты) дописывает наш мод-дополнение `wizardry_restored` (`mods-src/wizardry-restored`, jar в `pack/mods/`): регистрирует заклинания в реестре Redux под своим namespace, зависит от Redux, переживает его обновления. Состояние — 0.6.0, в реестре 185 заклинаний, `scripts/tests/castcheck.js` 69/69. Полная опись, таблицы «что и как сделано» и список отключённых на сервере «тестовых» заклинаний — [wizardry-port.md](../wizardry-port.md). Рядом: Spell Forge for Wizardry Redux (CurseForge, +11 заклинаний), порты `wizardry-golems`, `necromancers-delight` ([compat.md](../compat.md)).

### Как играть

- **Стихии** (`element`): Огонь, Лёд, Молния, Некромантия, Земля, Колдовство (sorcery), Исцеление + «Без стихии» (`magic`). Каждой соответствует кристалл (`magic_crystal_fire` и т. д.), посох, броня и руна.
- **Ступени заклинаний** (`tier`): Новичок → Ученик → Продвинутый → Мастер. Посох повышается Тайным фолиантом (`arcane_tome_*`) после накопления прогресса (прогресс идёт от применения посоха).
- **Откуда заклинания.** Книги заклинаний (`spell_book`) и свитки (`scroll`) находятся в сундуках структур, у магов и в торговле; чтобы узнать, что в книге, нужно её «открыть» (достижение `discover_spell`) или применить **Свиток идентификации** (`identification_scroll`). Заклинание вписывается в посох на **Таинственном верстаке** (`arcane_workbench`); посох хранит до N заклинаний (панель быстрого доступа). Улучшения посоха — `condenser_upgrade`, `cooldown_upgrade`, `duration_upgrade`, `storage_upgrade`, `range_upgrade`, `attunement_upgrade`, `siphon_upgrade`, `melee_upgrade`, `blast_upgrade`.
- **Мана.** Хранится в посохе и броне мага; пополняется кристаллами (`magic_crystal`, осколки), склянками маны (`mana_flask_small|medium|large`), кольцом конденсации (`ring_condensing`) и эффектом `font_of_mana`. Ограничение ресурса — атрибуты `cast.cost`, `cast.chargeup`, `cast.cooldown`, `cast.potency`, `cast.range`, `cast.duration`, `cast.blast`, `cast.progression`.
- **Алтарь наполнения** (`imbuement_altar`): наполняет предметы магией (120 рецептов; в JEI плагина нет — [jei.md](../jei.md); для Redux наш `mm-compat` даёт JEI-категорию). Книжные шкафы и аналои (`*_bookshelf`, `*_lectern`) — декор мага.
- **Броня:** боевой маг (`battlemage_*`), мудрец (`sage_*`), колдун (`warlock_*`) — каждый комплект по стихиям (`battlemage_helmet_fire`, `sage_robe_ice` и т. д.), плюс призрачная (`spectral_*`); бонусы — за комплект стихии; **великолепная нить** (`resplendent_thread`) улучшает обычную магическую броню в броню мудреца.
- **Артефакты** (кольца, амулеты, обереги в Curios): например `charm_haggler` (всегда новая сделка), `charm_experience_tome` (+50% прогресса посоха), `charm_flight`, `charm_storm`, `charm_black_hole`. Часть была «неработающей» — оживлена нашим Restored ([wizardry-port.md](../wizardry-port.md), «Этап 4»).

**Заклинания — ключевые (id `ebwizardry:…`; около 120 в jar + дополнения):**

| Ступень | Стихия | Заклинания |
|---|---|---|
| Новичок | — | `magic_missile` магическая ракета (базовое) |
| Новичок | Огонь | `ignite` поджечь; `smoke_bomb` |
| Новичок | Лёд | `freeze` заморозить |
| Новичок | Молния | `arc` электродуга; `thunderbolt` |
| Новичок | Земля | `dart` дротик; `leap` прыжок |
| Новичок | Исцеление | `heal` (+2 сердца); `ward` (щит от магии 30 с); `evade` |
| Новичок | Колдовство / Некромантия | `telekinesis`; `summon_zombie` (30 с) |
| Ученик | Огонь | `firebolt`, `fireball`, `firebomb`, `flame_ray`, `fire_sigil`, `pocket_furnace` |
| Ученик | Лёд | `iceball`, `frost_ray`, `ice_shard`, `ice_charge`, `frost_sigil`, `summon_snow_golem` |
| Ученик | Молния | `lightning_arrow`, `spark_bomb`, `homing_spark`, `charge` (рывок с уроном), `lightning_sigil` |
| Ученик | Земля | `whirlwind`, `poison`, `poison_bomb`, `growth_aura`, `dark_vision`, `bubble` |
| Ученик | Исцеление | `heal_ally`, `cure_effects`, `replenish_hunger`, `invigorating_presence` |
| Ученик | Некромантия | `life_drain`, `summon_skeleton`, `reversal`, `enrage` |
| Ученик | Колдовство | `conjure_sword`/`conjure_pickaxe`/`conjure_bow`, `force_arrow`, `agility`, `pocket_workbench` |
| Продвинутый | Огонь | `ring_of_fire`, `flaming_weapon`, `flaming_axe`, `summon_blaze`, `detonate`, `combustion_rune`, `fire_skin`, `fire_resistance` |
| Продвинутый | Лёд | `ice_lance`, `ice_spikes`, `blizzard`, `frost_step`, `permafrost`, `frost_axe` |
| Продвинутый | Молния | `static_aura`, `summon_lightning_wraith`, `invoke_weather` |
| Продвинутый | Земля | `tornado`, `spider_swarm`, `slime`, `cobwebs`, `fangs`, `water_breathing` |
| Продвинутый | Исцеление | `greater_heal`, `group_heal` (+3 сердца союзникам), `healing_aura`, `greater_ward`, `oakflesh`, `remove_curse`, `ray_of_purification`, `arcane_jammer` |
| Продвинутый | Некромантия | `wither`, `decay`, `wither_skull`, `darkness_orb`, `summon_wither_skeleton`, `banish`, `entrapment`, `curse_of_undeath`, `curse_of_soulbinding`, `dragon_fireball` |
| Продвинутый | Колдовство | `phase_step` (через стены), `containment` (удержание 20 с), `arcane_lock` (замок на контейнер), `force_orb`, `levitation`, `invisibility`, `vanishing_box` |
| Мастер | Огонь | `meteor`, `firestorm`, `fire_breath`, `flamecatcher` |
| Мастер | Лёд | `ice_age`, `hailstorm` |
| Мастер | Земля | `flight`, `boulder`, `forests_curse` |
| Мастер | Исцеление | `font_of_mana`, `font_of_vitality` |
| Мастер | Некромантия | `zombie_apocalypse`, `summon_skeleton_legion`, `plague_of_darkness`, `curse_of_enfeeblement` |
| Мастер | Колдовство | `arrow_rain`, `vex_swarm`, `speed_time` (ускорение времени) |

Из Restored (мастера/продвинутые): `lightning_bolt`, `chain_lightning`, `thunderstorm`, `lightning_hammer`, `earthquake`, `black_hole`, `slow_time`, `resurrection`, `possession`, `summon_phoenix`, `blink`, `decoy`, `mirage`, `petrify`, `transportation` и др. — см. [wizardry-port.md](../wizardry-port.md).

### Предметы/блоки

| Предмет/блок | Назначение |
|---|---|
| `wizard_handbook` Пособие мага | гайд-книга |
| `arcane_workbench` Таинственный верстак | вписывание заклинаний, улучшения, броня |
| `imbuement_altar` Алтарь наполнения | наполнение предметов магией (120 рецептов) |
| `wand_novice_<стихия>` … `wand_master_<стихия>` | посохи по ступеням: «Посох тлеющих углей» (новичок, огонь), «Посох ученика-пироманта», …, «Посох мастера-пироманта» |
| `magic_crystal*`, `crystal_ore`, `deepslate_crystal_ore`, `crystal_flower`, `crystal_block_*` | кристаллы (мана, крафт), руда и цветок встречаются в мире |
| `*_runestone`, `*_runestone_pedestal`, `receptacle` | рунические камни и пьедесталы (святилища/обелиски) |
| `spectral_dust*` | призрачная пыль стихий (крафт брони) |
| `mana_flask_*`, `arcane_tome_*`, `identification_scroll`, `resplendent_thread` | расходники |
| `gilded_*_wood`, `*_bookshelf`, `*_lectern` | декор мага (шкафы и аналои) |
| `astral_diamond`, `ruined_spell_book` | материалы редких улучшений |

### Мобы

| Моб (`ebwizardry:…`) | Что |
|---|---|
| `wizard` Маг | дружелюбный маг (торгует заклинаниями/свитками; жители башен, храмов) |
| `evil_wizard` Злой маг | враждебный, спавн биом-модификатором (вес 8, по одному) в биомах из тега `spawns_evil_wizard`; достижение `defeat_evil_wizard` |
| `remnant` Остаток | охраняет обелиски, роняет призрачную пыль (`spectral_dust`; достижение `defeat_remnant`) |
| `ice_giant`, `ice_wraith`, `lightning_wraith`, `shadow_wraith`, `storm_elemental`, `magic_slime`, `zombie_spawner` | существа заклинаний-призывов и враги структур |
| `boulder`, `meteor`, `tornado`, `blizzard`, `hailstorm`, `heal_aura`, `ring_of_fire`, `fire_sigil`/`frost_sigil`/`lightning_sigil`, `ice_barrier` | сущности-эффекты заклинаний |

### Эффекты

| Эффект (`ebwizardry:…`) | Что делает |
|---|---|
| `frost` | обморожение (замедление, лёд) |
| `fireskin` | огненная кожа |
| `ward` | щит от магического урона |
| `static_aura` | статическая аура |
| `containment` | не позволяет цели уйти далеко |
| `paralysis` | паралич |
| `arcane_jammer` | глушитель магии |
| `curse_of_enfeeblement`, `curse_of_soulbinding`, `curse_of_undeath` | проклятия (некромантия) |
| `decay` | разложение |
| `oakflesh` | дубовая плоть (броня) |
| `font_of_mana` | источник маны |
| `frost_step` | ледяной шаг |

Наши эффекты в Restored (например `mark_sacrifice`, «Метка жертвы») — [wizardry-port.md](../wizardry-port.md).

### События

Достижения-вехи: `apprentice`, `advanced`, `master`, `arcane_initiate`, `battlemage`, `sage`, `warlock`, `wizard_tower`, `visit_shrine`, `find_library_ruins`, `defeat_evil_wizard`, `defeat_remnant`, `buy_master_spell`, `restore_imbuement_altar`, `restore_ruined_book`. Спавн злых магов (`evil_wizard`) описан выше; специальных мировых событий нет.

### Данжи/структуры (/locate)

`/locate structure ebwizardry:<id>`. Места — `structure_set` (spacing/separation в чанках).

| Структура | Что | Spacing / sep. |
|---|---|---|
| `wizard_tower` | башня мага (джунгли, лес, тайга, пустыня, равнины, снежные равнины, саванна) | 42 / 8 |
| `library_ruins` | руины библиотеки (то же + болота/мангровые) | 42 / 8 |
| `fire_shrine`, `ice_shrine`, `lightning_shrine`, `necromancy_shrine`, `earth_shrine`, `sorcery_shrine`, `healing_shrine` | святилища стихий (набор из 7, выбор случайный) | 38 / 8 |
| `fire_obelisk`, `ice_obelisk`, `lightning_obelisk`, `necromancy_obelisk`, `earth_obelisk`, `sorcery_obelisk`, `healing_obelisk` | обелиски стихий | 38 / 8 |

Святилища регенерируются (опция `shrine_regeneration_enabled`); шаблоны — `structures/*.nbt` (`shrine_0..7`, `obelisk_0..3`, `library_ruins_0..3`, `underground_library_ruins_0..3`, `wizard_tower_0..3`).

### Чары

Мод использует «наполнение» (imbuement) вместо ванильных чар; временные чары от наших заклинаний `imbue_weapon` (Острота II / Сила II на 45 с) идут через механизм `ImbuementEnchantData` ([wizardry-port.md](../wizardry-port.md)).

### Команды/гамерулы

- `/cast <заклинание>` (ядро Redux: команда `cast`; сообщения «Заклинание … успешно произнесено»);
- `/discover <игрок> <заклинание|all>` и `/undiscover` — открыть/закрыть заклинания (аргумент `all` — все);
- `/ally add|remove <кого> <чьим союзником>` — союзники по заклинаниям (союзников не затрагивают AoE);
- `magic_attribute` — выдать модификатор атрибута `cast.*` сущности (add / multiply_base / multiply_total);
- отладочные (выдача посохов, свитков, книг, брони, магов) включаются `enable_debug_commands` в общем конфиге.
Гамерулов нет; разрушение блоков заклинаниями зависит от ванильного `mobGriefing` и опции `player_block_damage`.
Тесты — `castcheck.js` (Wizardry Restored): кастует все заклинания фейковым игроком.

### Конфиги

Три файла: сервер (`EBServerConfig`), общий (`EBCommonConfig`: `enable_debug_commands`), клиент. Серверные опции: `mana_per_crystal`, `mana_per_shard`, `grand_crystal_mana`, `forfeit_chance`, `potency_increase_per_tier`, `range/duration/cooldown…_per_level`, `max_progression_reduction`, `upgrade_stack_limit`, `passive_mobs_are_allies`, `block_players_allies_damage`, `player_block_damage`, `replace_vanilla_fall_damage`, `shrine_regeneration_enabled`, `single_use_spell_books`, `inject_mob_drops`/`loot_injection_to_mobs` (добавление свитков в добычу мобов), `melt_items_blacklist`.
Включённость каждого заклинания и параметры (урон, затраты, перезарядка, доступность в книгах/посохах/торговле/сундуках/диспенсерах) — в данных `data/ebwizardry/spells/*.json`; переопределяются датапаком/KubeJS.

Наши правки:
- `mods-src/wizardry-restored` — недостающие заклинания и артефакты ([wizardry-port.md](../wizardry-port.md)).
- `mods-src/mm-compat` — JEI-категория Алтаря наполнения (`ImbuementAltarCategory`, [jei.md](../jei.md)).
- Русификация — [russification.md](../russification.md) (перевод мода в `ebwizardry` уже есть в jar).

## Extra Golems

<!-- auto:start card:extragolems -->
<a id="extragolems"></a>
- **Версия:** 20.1.0.2 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/extragolems-20.1.0.2.jar`
- **Что это:** Десятки новых големов с уникальными текстурами и способностями. _(источник: docs сборки)_
- **В сборке:** Extra Golems 20.1.0.2 (skyjay1, zlib, both): база для Wizardry Golems; CF без стороннего скачивания
- **В наших docs:** [wizardry-port.md](../wizardry-port.md) (6), [compat.md](../compat.md) (4), [roadmap.md](../roadmap.md) (1)
<!-- auto:end card:extragolems -->

### Как играть

Extra Golems 20.1.0.2 (модид `golems`, автор skyjay1, лицензия zlib): голем строится из блоков по образцу железного — **199 типов** (`data/golems/golems/golem/*.json`) под любые блоки: камни, брёвна, шерсть, стекло, бетон, терракота, руды, редстоун-лампа, TNT, мёд, слизь, золото, алмаз, незерит, обсидиан и др.
- **Как построить** (текст «Extra Golems Guide», `guide_book`): поставить 2 блока друг на друга, ещё 2 по бокам верхнего, и **Голова голема** (`golem_head`) на средний. Для крафта головы и других предметов используется `golem_spell` (Golem Spell, по тексту гида применяется на блоке или как ингредиент). Книга-гид — `guide_book`.
- Параметры у типа свои (пример `gold`: 85 HP, урон 11.5, броня 2.5; Бедрок-голем — `invulnerable`, 999 HP, не строится руками, только `spawn_bedrock_golem`).
- Поведение задают «мозги» (`data/golems/golems/behavior_list`: `empty`, `froglight`, `leaves`, `melon` и др.); Jade показывает особенности (`golem.description.*`: топливо, группа, здоровье, атака, отбрасывание).
- Предметы динамики: `light_provider` (Dynamic Golem Light), `power_provider` (Dynamic Golem Power Source).
- Связка с Electroblob's Wizardry: наш порт Wizardry Golems (`mods-src/wizardry-golems`, тест `scripts/tests/golemcheck.js`) — заклинания Golemancy призывают голем-миньонов Extra Golems; подробности — [compat.md](../compat.md) (раздел «Wizardry Golems») и [wizardry-port.md](../wizardry-port.md).

### Предметы/блоки

| id (`golems:…`) | Что делает |
|---|---|
| `golem_spell` | Ингредиент/заклинание для крафта головы |
| `golem_head` | Оживляет конструкцию из блоков |
| `guide_book` | «Extra Golems Guide» |
| `spawn_bedrock_golem` | Яйцо Бедрок-голема (повторное использование на нём убирает) |
| `light_provider`, `power_provider` | Динамические блоки света и питания |

### Мобы

`golems:golem` (один тип сущности, форма и параметры — по данным; 226 названий в локали, например «Librarian Golem» из книжных полок). Джейд показывает атаку, здоровье и особенности голема.

### Конфиги

В `pack/config` нет файла: при первом запуске `golems-common.toml` дописывается мододом (безвредные WARN) — [compat.md](../compat.md). CurseForge запрещает стороннее скачивание: jar лежит в `pack/mods/`.

## Extrabotany

<!-- auto:start card:extrabotany -->
<a id="extrabotany"></a>
- **Версия:** 1.20.1-1.9.2-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/extrabotany)
- **Что это:** Дополнение к Botania. _(источник: docs сборки)_
- **В наших docs:** [bosses.md](../bosses.md) (12), [russification.md](../russification.md) (4), [client-checklist.md](../client-checklist.md) (2), [compat.md](../compat.md) (2), [jei.md](../jei.md) (2), [russification-audit.md](../russification-audit.md) (2)
<!-- auto:end card:extrabotany -->

### Как играть

ExtraBotany 1.9.2 (модид `extrabotany`) — большое расширение Botania: новые металлы, оружие, броня, цветы и боссы. Требует Botania.
- **Металлы:** `aerialite_ingot`, `orichalcos_ingot`, `photonium_ingot`, `shadowium_ingot` (+ блоки/самородки) — для оружия и молотов (`*_hammer`: `manasteel` … `rhein_hammer`).
- **Оружие и артефакты:** `excalibur`, `failnaught`, `the_chaos`, `the_origin`, `the_end`, `the_universe`, `zadkiel`, `camera` (Shameimaru's Camera), `feather_of_jingwei`, `magic_finger`, `walking_cane`, `core_of_the_void`, `void_archives`, `pandoras_box`.
- **Боссы:** **Страж Гайи (старый вариант)** (`extrabotany:gaia_legacy`, 320 HP) и **Страж Гайи III** (`extrabotany:gaia_3rd`, 600 HP, награда `music_disc_gaia3`, мешки `eins_`…`vier_reward_bag`, `nine_and_three_quarters_reward_bag`) — [bosses.md](../bosses.md).
- **Броня и наряды:** `starry_idol_*`, `pleiades_combat_maid_*`, `shadow_warrior_*`, `goblin_slayer_*`; щиты `manasteel_shield`, `elementium_shield`, `terrasteel_shield`, `achilles_shield`.
- **Новые цветы:** `trade_orchid`, `woodienia`, `reikarlily`, `bellflower`, `annoyingflower`, `stonesia`, `edelweiss`, `resoncund`, `sunshine_lily`, `moonlight_lily`, `serenitian`, `twinstar`, `omniviolet`, `tinkle`, `blood_enchantress`, `mirrowtunia`, `necrofleur`, `manalink`.
- **Кольца и линзы:** `frost_ring`, `dispersive_ring`, `curse_ring`, `spider_ring`, `parkour_ring`, `jackie_chan_ring`, `mana_ring_master`; `lens_push`, `lens_smelt`, `lens_mana`, `lens_potion`, `lens_trace`.

### Мобы

`extrabotany:gaia_legacy`, `extrabotany:gaia_3rd`; снаряды и ловушки: `skull_missile`, `skull_landmine_red|blue|green`, `magic_landmine` (Gaia Trap), `aura_fire`, `holy_water_grenade`.

### Эффекты

`extrabotany:` `immobilize`, `link`, `heal_reverse`, `discount`, `warm`, `thirror`.

### Конфиги

В `pack/config` файлов нет. Событийная обёртка для мода упомянута в [compat.md](../compat.md) (ExtraBotany + Event Wrapper).

## Factions And Curiosities

<!-- auto:start card:factions-and-curiosities -->
<a id="factions-and-curiosities"></a>
- **Версия:** 1.3.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/factions-and-curiosities)
- **Что это:** Аддон Mana and Artifice: новые фракции. _(источник: docs сборки)_
- **В сборке:** дополнение M&A: новые фракции
- **В наших docs:** [compat.md](../compat.md) (2), [enchanting-table.md](../enchanting-table.md) (2)
<!-- auto:end card:factions-and-curiosities -->

### Как играть

Factions And Curiosities 1.3.1 (`fnc`): дополнение к [Mana and Artifice](#mana-and-artifice) и [Dragon Magic And Relics](#dragon-magic-and-relics) («additional factions»): «дикая магия», смайты паладина и кольца-модификаторы. Зависит от `mna`, `dmnr`, `curios`, `geckolib`.
- **Броня фракций:** `divine_*` (божественная), `mischief_*` (шутовская, а также `mischief_dragon_mage_*`).
- **Кольца и пояса:** `ring_of_wild_luck`, `smiting_ring`, `ring_of_the_four_leaf_clover`, `frenzy_ring` (+greater), `heavy_weapon_ring` (+greater), `second_protection_ring`, `second_attack_ring`, `second_drop_ring`, `aura_fraud_ring`, `time_belt`.
- **Игральные кости:** `dice_six` (D6), `dice_twenty` (D20) и `deck_of_many` («Deck of Many»).
- **Оружие и инструменты:** `brimstone_sword`, `blessed_bow`, `wild_pickaxe`; браслеты `black_cat_bracelet`, `blood_lust_bracelet`.
- Отладочные шары (`debug_orb_smite`, `debug_orb_spell_adjustment`, `debug_orb_wild_magic`) — для тестов.

### Эффекты

`fnc:` `wild_magic` / `good_wild_magic` / `bad_wild_magic`, `wild_magic_cooldown`, `paladin_smite` и «смайты» (`booming_smite`, `burning_smite`, `crippling_smite`, `dispelling_smite`, `hexing_smite`, `silencing_smite`), `empowered`, `maximized`, `minimized`, `random_spell_adjustment`, `random_ore_miner`, `living_bomb`, `frenzy`, `counter_attack`, `hex`, `explosion_resistance`.

### Чары

`fnc:holy_smite`.

### Конфиги

В `pack/config` файлов нет.

## GTBC's Geomancy Plus

<!-- auto:start card:gtbcs_geomancy_plus -->
<a id="gtbcs_geomancy_plus"></a>
- **Версия:** 2.0.0-1.20.1 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/gtbcs_geomancy_plus-2.0.0-1.20.1.jar`
- **Что это:** Аддон Iron's Spells: заклинания по мотивам Mowzie's Mobs и оригинальная магия земли. _(источник: docs сборки)_
- **В сборке:** Iron's Spells × Mowzie's
- **В наших docs:** [compat.md](../compat.md) (2)
<!-- auto:end card:gtbcs_geomancy_plus -->

### Как играть

GTBC's Geomancy Plus 2.0.0 (`gtbcs_geomancy_plus`) — аддон Iron's Spells 'n Spellbooks («spells inspired by Mowzie's Mobs»): школа **геомантии**. Требует `irons_spellbooks` и библиотеку `gtbcs_spell_lib` (карточка в разделе библиотек).
- **Заклинания** (`gtbcs_geomancy_plus:…`): `chunker` (катящийся блок земли), `eroding_boulder` (валун, эффект `erode`), `fissure`, `seismic_surf` (летящий валун, на котором можно ехать), `tremor_spike`, `tremor_step`, `geo_conductor`, `pillar_of_the_resounding_earth`, `dripstone_bolt`, `petrivise`, `weeping_wall`, `weeping_cluster`, `crystalline_conduit`; `solar_beam` и `solar_storm` выдаются за победу над Umvuthi, `solar_chorus` — с Umvuthana Raptor (по описаниям в локали).
- **Предметы:** `geo_rune`, `geo_upgrade_orb`, `geo_echo`, `codex_of_shattered_horizons`, `the_stonecaller`, `ring_of_the_resounding_earth`, `bracelet_of_inversion`, `measure_of_stone`, `bluff_instruction_manual`.
- Атрибуты: `geo_spell_power`, `geo_magic_resist`; эффекты `solar_storm`, `tremor_step_effect`, `erode`, `aegis`, `geo_spell_power`.

### Мобы

`gtbcs_geomancy_plus:geomancer` (яйцо `geomancer_spawn_egg`), `geo_grandmaster` (яйцо `geo_grandmaster_spawn_egg`); существа заклинаний: `summoned_umvuthana_raptor`, `summoned_umvuthana_crane`, `resonator`, `chunker`, `seismic_ride` и колонны (`resonate_pillar`, `petrivise_pillar`, `obsidian_wall_pillar`).

### Данжи/структуры (/locate)

`gtbcs_geomancy_plus:geomancer_tower` (шаг 40, мин. 25 чанков): `/locate structure gtbcs_geomancy_plus:geomancer_tower`.

### Конфиги

В `pack/config` файлов нет.

## Iron's Recipe Additions

<!-- auto:start card:irons_recipe_additions -->
<a id="irons_recipe_additions"></a>
- **Версия:** 1.0.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/irons_recipe_additions-2.7-forge-1.20.1.jar`
- **Что это:** Добавляет множество рецептов к Iron's Spells 'n Spellbooks. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (2)
<!-- auto:end card:irons_recipe_additions -->

### Что даёт игроку

Iron's Recipe Additions 2.7: добавляет рецепты к [Iron's Spells 'n Spellbooks](#irons-spells-n-spellbooks) (12 файлов `data/irons_recipe_additions/recipes`): крафт и переплавка расходников — `arcane_essence`, `cinder_essence` (+ плавка), `common_ink`, `blaze_instruction_manual`, `rotten_spellbook`, `ruined_book`, `rune_duplication`, `scroll_forge`, `frozen_bone_smelting`. Предметов, блоков и настроек у мода нет; в JEI рецепты видны через плагин Iron's Spells — [jei.md](../jei.md).

## Iron's Spells 'n Spellbooks

<!-- auto:start card:irons-spells-n-spellbooks -->
<a id="irons-spells-n-spellbooks"></a>
- **Версия:** 1.20.1-3.16.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/irons-spells-n-spellbooks)
- **Что это:** Магия в духе классических RPG: заклинания хранятся в свитках и книгах, колдуются за ману в школах (огонь, лёд, молния, свет, эндер, кровь, призыв, природа, потусторонье); добавляет посохи, броню магов, станции начертания и улучшения, магические структуры с боссами (Мёртвый король, Эхо Тироса). _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (18), [russification.md](../russification.md) (9), [performance.md](../performance.md) (8), [roadmap.md](../roadmap.md) (8), [ysm-models.md](../ysm-models.md) (6), [version-choice.md](../version-choice.md) (3)
<!-- auto:end card:irons-spells-n-spellbooks -->

Автор — Iron431 (лицензия All Rights Reserved; в сборке — по решению пользователя, см. [CLAUDE.md](../../CLAUDE.md)). Модид `irons_spellbooks`, зависимости: `irons_lib`, `playeranimator`, GeckoLib, Curios ([compat.md](../compat.md), п. 1). Сторона — both.

### Как играть

**Идея.** Заклинания хранятся в **свитках** и **книгах заклинаний**; колдуются расходом **маны** с перезарядкой. Книга надевается в слот Curios `spellbook` и даёт ячейки; заклинание выбирается колесом/панелью и применяется клавишей («Применить выбранное заклинание», по умолчанию настраивается в «Управление → Iron's Spells»). Есть клавиши быстрых слотов 1–15 и «колесо заклинаний». Посохи и некоторые мечи колдуют правым кликом.

**Школы магии** (у каждой есть «фокус» — предмет для крафта свитков, руна, сфера улучшения и атрибуты силы/сопротивления):

| Школа (`id`) | Фокус | Суть |
|---|---|---|
| Огонь `fire` | Огненный стержень (blaze rod) | урон и урон со временем |
| Лёд `ice` | `frozen_bone` | урон и контроль (заморозка) |
| Молния `lightning` | `lightning_bottle` | концентрированный урон, скорость |
| Свет `holy` | `divine_pearl` | лечение, поддержка, баффы |
| Эндер `ender` | жемчуг Эндера | перемещение, утилиты, чёрные дыры |
| Кровь `blood` | `blood_vial` | урон с лечением заклинателя, некромантия |
| Призыв `evocation` | изумруд | призыв, щиты, разное |
| Природа `nature` | ядовитый картофель | дебаффы, яд, земля |
| Потусторонье `eldritch` | осколок эха (`echo_shard`) | нельзя изучить сразу: нужен `eldritch_manuscript`, собранный из найденных обрывков |

Дополнительные школы добавляет [Cataclysm: Spellbooks](own.md#cataclysm_spellbooks) (`cataclysm_spellbooks:abyssal`, `technomancy`).

**Прогрессия (по путеводителю мода):**
1. *Начало.* Свитки из сундуков магических структур и с магов; книга заклинаний (`copper_spell_book`, `iron_spell_book`) + `inscription_table` — вписываем свиток в книгу (обратно свиток снимается ценой стирания). Из `arcane_essence` и кожаной брони — броня странствующего мага (+макс. мана).
2. *Середина (уровень Незера).* `scroll_forge`: чернила + бумага + фокус школы → свиток (редкость чернил определяет уровень, фокус — школу). Средние книги: `gold_spell_book` (3 золота + 4 `arcane_essence` + 2 `hogskin`), `diamond_spell_book`. Броня школ из `magic_cloth` + руны школы; руны класс-мобов выпадают с них.
3. *Поздняя игра.* `arcane_anvil` — улучшение свитков (чернила следующей редкости), брони и книг сферами улучшения (`*_upgrade_orb`, сфера школы = базовая сфера + 8 рун), вписывание заклинаний в оружие («высечка»), снятие улучшений `shriving_stone`. Лучшие книги: `netherite_spell_book` (нужен `ruined_book` из Древних городов), `dragonskin_spell_book` (шкура дракона Края). Мифрил (`mithril_ore`) — глубже Y=−38, без контакта с воздухом, киркой из незерита.

**Чернила** (`common_ink` … `legendary_ink`) определяют максимальную редкость/уровень свитка; хорошие — в магических сундуках и с магов. **Алхимический котёл** (`alchemist_cauldron`) перерабатывает ненужные свитки и варит эликсиры/чернила (13 рецептов `brew_*`).

**Мана.** Атрибуты: `max_mana`, `mana_regen`, `spell_power` (общая и по школе), `cooldown_reduction`, `cast_time_reduction`, `spell_resist`, `summon_damage`, сопротивление по школам. Источники: броня магов, кольца (`mana_ring`), сферы, эффект «Прилив маны». Мана у каждого мода магии своя — общей нет ([compat.md](../compat.md), поток 3).

**Заклинания — обзор по школам** (id в `код`; всего ~115, в списке самые полезные; полный список — `/cast`/JEI/подсказки в игре):

| Школа | Ключевые заклинания |
|---|---|
| Огонь | `fireball` огненный шар (взрыв); `firebolt` снаряд; `fire_breath` конус пламени; `wall_of_fire` стена огня; `blaze_storm` шквал огненных шаров; `burning_dash` рывок; `magma_bomb`; `scorch` зона ожога; `raise_hell` (`Адский удар`) ударная волна от оружия; `flaming_barrage` |
| Лёд | `icicle` пробивающая сосулька; `cone_of_cold` морозное дыхание; `ray_of_frost` луч; `ice_block` глыба сверху; `frost_step` телепорт + ледяная тень; `ice_tomb` защитный гроб; `summon_polar_bear`; `blizzard`; `frostwave` |
| Молния | `lightning_bolt` удар с небес; `lightning_lance` копьё; `electrocute` конус тока; `chain_lightning` цепная; `thunderstorm` гроза вокруг (молния раз в 2 с); `ascension` взлёт; `charge` бонус скорости/урона; `ball_lightning`; `volt_strike`; `thunder_step` |
| Свет | `heal` / `greater_heal` лечение; `healing_circle` круг лечения; `cloud_of_regeneration`; `blessing_of_life`; `fortify` поглощение союзникам; `haste` ускорение; `cleanse` снятие эффектов; `wisp`; `sunbeam`; `divine_smite` удар по области; `guiding_bolt`; `angel_wing` призрачные элитры |
| Эндер | `magic_missile` базовый снаряд; `teleport`; `counterspell` прерывание чужого заклинания; `portal` пара порталов; `recall` на спавн; `starfall` дождь комет; `black_hole`; `summon_ender_chest`; `evasion` уклонение; `echoing_strikes`; `gravity_fissure`; `shadow_slash`; `arcane_shackle`; `summon_swords` |
| Кровь | `blood_slash` клинок (лечит 15% нанесённого); `raise_dead` нежить-миньоны; `blood_step` телепорт + невидимость; `devour` укус; `heartstop` неуязвимость с возвратом 50% урона; `sacrifice` взрыв своего призыва; `ray_of_siphoning` высасывающий луч; `wither_skull`; `blood_needles`; `acupuncture` |
| Призыв | `summon_vex`; `summon_horse` призрачная лошадь; `shield` магический щит; `slow`; `invisibility`; `fang_strike`/`fang_ward` клыки эвокера; `spectral_hammer` (добыча камня); `throw`; `scapegoat` козёл-приманка; `arrow_volley`; `lob_creeper`/`chain_creeper`/`creeper_revenge`; `wololo`; `firecracker` |
| Природа | `oakskin` дубовая кожа; `root` корни-путы; `earthquake`; `touch_dig` магическая добыча (учитывает предмет в руке); `gluttony` еда → мана; `stomp`; `spider_aspect`; `blight`; `acid_orb`; `poison_breath`/`poison_arrow`/`poison_splash`; `firefly_swarm` |
| Потусторонье | `eldritch_blast` лучи; `sculk_tentacles` / `void_tentacles` щупальца; `pocket_dimension` личный карман; `sonic_boom`; `telekinesis`; `planar_sight` вид сквозь стены; `abyssal_shroud` |

Расширение по теме Wizardry — отдельный мод [Electroblob's Wizardry Redux](#electroblobs-wizardry-redux) (у него своя мана).

### Предметы/блоки

**Станции**

| Блок | Для чего |
|---|---|
| `inscription_table` (Начертательный стол) | вписать свиток в книгу; рецепт: книга + 3 деревянные плиты + 2 забора |
| `scroll_forge` (Кузница свитков) | чернила + бумага + фокус → свиток; 3 полированных глубинных сланца + 4 плачущего обсидиана |
| `arcane_anvil` (Волшебная наковальня) | апгрейд свитков/брони/книг, высечка заклинаний в оружие; 3 аметистовых блока + алмаз + наковальня + 2 полированных сланца |
| `alchemist_cauldron` | переработка свитков, эликсиры, чернила |
| `pedestal`, `brazier`, `portal_frame`, `wisewood_bookshelf` | декор/механика (рамки портала связываются заклинанием `portal`) |
| `mithril_ore`, `deepslate_mithril_ore` | руда мифрила → `raw_mithril`, `mithril_ingot` |

**Книги заклинаний (по силе):** `copper_spell_book`, `wimpy_spell_book` (находка), `iron_spell_book`, `gold_spell_book`, `diamond_spell_book`, `netherite_spell_book`, `dragonskin_spell_book`; уникальные с зафиксированными заклинаниями: `evoker_spell_book` (с эвокеров/архизаклинателя), `necronomicon_spell_book` и `blood_staff` (с Мёртвого короля), `rotten_spell_book`, `blaze_spell_book` (с всполохов), `villager_spell_book`, `druidic_spell_book`, `ice_spell_book`, `cursed_doll_spell_book`.

**Посохи (колдуют по ПКМ):** `graybeard_staff`, `artificer_cane`, `ice_staff` (`frosted_helve` + `permafrost_shard`), `lightning_rod`, `blood_staff`, `pyrium_staff` (из `pyrium_ingot`, незерит), `staff_of_the_nines`.

**Броня:** странствующего мага (`wandering_magician_*`), школьная (`pyromancer_*`, `cryomancer_*`, `electromancer_*`, `archevoker_*`, `cultist_*`, `priest_*`, `shadowwalker_*`, `plagued_*`, `pumpkin_*`, `netherite_mage_*`), `wizard_*`; делается из `magic_cloth` + руны.

**Украшения (Curios):** `mana_ring`, `cooldown_ring`, `cast_time_ring`, `affinity_ring` (по школе), `fireward_ring`/`frostward_ring`/`poisonward_ring`, `invisibility_ring`, `conjurers_talisman`, `concentration_amulet`, `teleportation_amulet`. Обереги: `enchanted_ward_amulet` защищает от магического поля Затерянной цитадели.

**Навигация:** `wayward_compass` (4 `arcane_essence` + 4 железа + фонарь душ) ведёт к склепу-катакомбам; `antiquated_compass`; карты `citadel_map`, `ancient_battleground_map`, `evoker_fort_battle_plans` и др. (`furled_map*`).

**Боссы-награды:** `divine_soulshard`, `decrepit_scythe`, `betrayer_signet`, `cinder_essence`, `decrepit_key` (открывает хранилища цитадели), `dead_king_phylactery` (воскресить Мёртвого короля), `hellrazor`, `legionnaire_flamberge`, `magehunter`, `spellbreaker`.

**Еда/зелья:** `casters_tea`, `oakskin_elixir`, `invisibility_elixir`, `evasion_elixir` и «великие» версии, `greater_healing_potion`, `fire_ale`, `netherward_tincture`. **Данные:** музыкальные диски `dead_king_lullaby`, `flame_still_burns`, `whispers_of_ice`.

### Мобы

Именные маги спавнятся в своих структурах и дают руны школы и чернила:

| Моб (`irons_spellbooks:…`) | Где |
|---|---|
| `pyromancer` Пиромант | башня пироманта |
| `cryomancer` Криомант | горная башня (`mountain_tower`) |
| `archevoker` Архизаклинателя + эвокеры | крепость эвокеров (`evoker_fort`) |
| `necromancer` Некромант | свободно в Верхнем мире (биом-модификатор `forge:add_spawns`, вес 18) |
| `priest` Жрец, `apothecarist` Фармацевт | домики жреца в деревнях и отдельно |
| `cultist` Культист | в структурах |
| `citadel_keeper` Древний рыцарь | Незер: Затерянная цитадель / Лагерь легиона; «просыпается» при разрушении груд доспехов (`armor_pile`), роняет `cinder_essence` |
| `ice_spider` Ледяной паук | логово ледяных пауков, патрули в заснеженных биомах |
| `dead_king` Мёртвый король | **босс**, катакомбы (`catacombs`) |
| `fire_boss` Эхо Тироса, Первый Пламеносец | **босс**; есть блок `tyros_statue`, но структуру, где он появляется, в данных мода однозначно найти не удалось |

Боссы подробнее — [bosses.md](../bosses.md), раздел `irons_spellbooks`: Тирос — 1000 HP, урон 10, броня 15; Мёртвый король — 500 HP, урон 10, броня 15. Дропы Короля (`loot_tables/entities/dead_king`): 28–45 `arcane_essence`, свитки, руны, чернила, с шансом 50% — `blood_staff` или `necronomicon_spell_book`. Оживить Короля — `dead_king_phylactery` на его душе. Боссы имеют «зловещий» (ominous) вариант и доп. ХП/урон из конфига (`tyros_*`, `dead_king_*`).

Призываемые: `summoned_vex`, `summoned_skeleton`, `summoned_zombie`, `summoned_polar_bear`, `spectral_steed`, `frozen_humanoid`.

### Эффекты

| Эффект (`irons_spellbooks:…`) | Что делает |
|---|---|
| `instant_mana` | восстанавливает 25 маны + 5% максимума за уровень |
| `vigor` | +1 сердце к максимуму за уровень |
| `oakskin` | −15% входящего урона (+5% за уровень) |
| `fortify` | сердца поглощения |
| `evasion` | телепорт от атаки, урон не получаете |
| `abyssal_shroud` | уклонение от любой атаки на время |
| `heartstop` | неуязвимость, затем возвращает 50% накопленного урона |
| `true_invisibility` | невидимость с бронёй и предметами |
| `ascension` | невесомость; `antigravity` — тоже |
| `angel_wings` | призрачные элитры |
| `hastened` / `slowed` | ускорение / замедление (движение, атака, копание, мана, чтение заклинаний) |
| `charged` | +10% урона, +5% силы заклинаний, +20% скорости за уровень |
| `rend` | −5% брони за уровень; `blight` — −5% урона, −10% входящего лечения |
| `chilled`, `frostbite` | замедление/заморозка; убитое замороженное существо становится ледяной тенью |
| `immolate` | при 3 стаках взрыв и поджог соседей |
| `spider_aspect` | +5% урона по отравленным за уровень |
| `guided` | притягивает ближайшие снаряды |
| `planar_sight` | видеть существ сквозь блоки |
| `gluttony` | еда превращается в ману |
| `echoing_strikes` | немагические удары оставляют эхо-взрыв |
| `thunderstorm` | молния по врагам каждые 2 с |
| `enchanted_ward` | разрушает тело в поле Затерянной цитадели (защита — `enchanted_ward_amulet`) |
| `fall_damage_immunity`, `airborne` | без урона падения / уязвимость к кинетическому урону |

Таймеры призванных (`vex_timer`, `raise_dead_timer`, `polar_bear_timer`, `summon_horse_timer`) показывают время жизни фамильяров.

### Данжи/структуры (/locate)

Все — `/locate structure irons_spellbooks:<id>`. Расстояние/разброс — `structure_set` (в чанках).

| Структура | Где и что | Spacing / sep. |
|---|---|---|
| `catacombs` | катакомбы Мёртвого короля; подземелье, биомы холмов/тайги/джунглей/леса/саванны/пустыни/равнин/болота/пещер | 72 / 48 |
| `pyromancer_tower` | башня пироманта с подвалом; холмы, лес, равнины, болота | 85 / 50 |
| `mountain_tower` | башня криоманта; горы, снежные равнины/тайга, роща | 45 / 33 |
| `evoker_fort` | крепость эвокеров/Архизаклинателя; холмы, лес, пустыня, равнины, саванна | 85 / 50 |
| `mangrove_hut` | хижина алхимика в болотах | 25 / 18 |
| `ice_spider_den` | логово ледяных пауков, заснеженные биомы | 85 / 50 |
| `impaled_icebreaker` | ледокол во льдах; замёрзший океан, ледяные шипы, Terralith ледники | 75 / 50 |
| `citadel` | Затерянная цитадель (Незер, биомы бастиона; поле `enchanted_ward`) | 100 / 80 |
| `ancient_battleground` | Лагерь легиона / поле древней битвы, Незер (бастион) | 35 / 12 |

Дополнительно: `priest_house` в деревнях, интеграции деревень (`structures/villages`). Достижения-вехи: `enter_catacombs`, `make_wayward_compass`. Лут катакомб и цитадели на 1.20.1 не грузился (формат 1.21) — исправлено нашим патчем, см. «Конфиги».

### Команды/гамерулы

Команды (нужны права оператора; названия литералов — из байткода 3.16.3):
- `/cast <кто> <заклинание> <уровень>` — произнести заклинание;
- `/mana get|set|add <игроки> [число]` — мана;
- `/createScroll <заклинание> <уровень>`, `/createSpellBook <слоты> [randomize]`, `/createImbuedSword <предмет> <заклинание> <уровень>` — выдать предметы;
- `/learnSpell` с подкомандами `learn <заклинание>`, `learn_all`, `forget_all` — изученные заклинания (нужно для эльдричских);
- `/clearCooldowns <игроки|all>`, `/clearRecasts`, `/clearSpellSelection`;
- `/ironsSpellbooks` — служебное: `summons … setOwner`, `upgrade`, `camera_shake`, `config`, `inscription_table`.
Гамерулов мод не добавляет (`SPELL_GREIFING` — опция конфига, см. ниже).

### Конфиги

Файл — `serverconfig/irons_spellbooks-server.toml` (создаётся в каждом мире) и `config/irons_spellbooks-client.toml`. Среди опций: `spellGriefing` (разрушение блоков заклинаниями), `manaRegenMultiplier`, `maxUpgrades`, `scrollMerging`, `scrollRecycleChance`, `swordsConsumeMana`, `iceSpiderPatrols`, `priestHouseWeight`, `portalFrameRestrictBreaking`, лимиты HP/урона боссов `tyros*`/`deadKing*`, чёрные/белые списки апгрейдов и высечки, а также настройки каждого заклинания (`enabled`, `maxLevel`, `cooldownInSeconds`, школа, мин. редкость).

Наши правки:
- `patches/irons_loot_1_20_1.py` → `pack/kubejs/data/irons_spellbooks/loot_tables/chests/{catacombs/crypt_loot,citadel/citadel_tomes}.json` — таблицы в формате 1.21 переписаны под 1.20.1 ([compat.md](../compat.md), п. 7); после обновления мода запустить скрипт заново.
- Порты зависящих от Iron's модов (`alshanex_familiars-…-mmport`, T.O Magic/`traveloptics-…-mmport`, `cataclysm_spellbooks-…-mmport`) и переходники `mods-src/jar-ports` (`SimpleAttributeCurio`, `KeyState`, `OwnerGetter`, удалённые из 3.16).
- Русификация: [russian-moded](../../pack/resourcepacks-src/russian-moded/assets/irons_spellbooks/lang/ru_ru.json); гайд-книга `iss_guide_book` без русского перевода ([russification-audit.md](../russification-audit.md)).

## Mahou Tsukai

<!-- auto:start card:mahou-tsukai -->
<a id="mahou-tsukai"></a>
- **Версия:** 1.20.1-v1.34.74 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mahou-tsukai)
- **Что это:** Магия со спецэффектами и уникальными заклинаниями. _(источник: docs сборки)_
- **В наших docs:** [roadmap.md](../roadmap.md) (3), [compat.md](../compat.md) (2), [orespawn.md](../orespawn.md) (1), [russification.md](../russification.md) (1)
<!-- auto:end card:mahou-tsukai -->

### Как играть

Mahou Tsukai (`mahoutsukai`, v1.34.74; вдохновлён Fate/stay night) — магия «Мастера и слуги» с кругами (магуйн), ментальной силой и древними реликвиями. Справочник — `guidebook` («Mahou Tsukai Knowledge Compendium»). Зависит от мода `jousting`.
- **Свитки и магические круги.** Каждое заклинание-«свиток» (`scroll_*`: `ascension`, `black_flame`, `borrowed_authority`, `boundary_*`, `butterfly_effect`, `cup_of_heaven`, `damage_exchange`, `familiars_garden`, `fay_sight`, `geas`, `insight`, `mystic_eyes`, `reality_marble`, `rho_aias`, `treasury_projection`, `weapon_shooter` и др.) соответствует блоку `mahoujin_*` (61 блок-круг; материалы — `mortar_and_pestle`, порошки `powdered_*`).
- **Реликвии/легендарное оружие:** `caliburn`, `clarent`, `rhongomyniad`, `rule_breaker`, `kodoku`, `morgan`, `replica`, `mystic_code`, `mystic_staff`.
- **Кодекс:** `guidebook`.
- Эффекты (31) — Eyes (Binding, Black Flame, Fay Sight, Insight, Reversion), `borrowed_authority`, `fear`, `rage`, `foresight`, `misfortune`, `probability_alter`, `wall_stick` и др.

### Предметы/блоки

| id (`mahoutsukai:…`) | Что делает |
|---|---|
| `guidebook` | Справочник |
| `scroll_*` (≈45 штук) | Свитки заклинаний |
| `mahoujin_*`, `mahoujin` | Магические круги |
| `mortar_and_pestle`, `powdered_*`, `attuned_diamond`, `attuned_emerald`, `attuner` | Материалы и «настройка» |
| `caliburn`, `clarent`, `rhongomyniad`, `rule_breaker`, `kodoku`, `morgan` | Оружие-реликвии |
| `mystic_code`, `mystic_staff`, `staff_emrys`, `treasury_projection_gauntlet` | Магические коды/посохи |
| `black_fire`, `fog_projector`, `invisible_barrier_block`, `mana_circuit` | Блоки заклинаний |
| `faecake`, `fae_essence` | Материалы фей |

### Мобы

`mahoutsukai:butterfly`, `fae`, `familiar_entity`, `kodoku_entity`.

### Чары

`mahoutsukai:projector` («Mahoujin Projector»).

### Команды/гамерулы

`/mahousummon` (призыв реликвий: в классе есть параметры `lance`, `morganball`, `rhongomyniad`, `treasury`, `weapon`) и `/maxmahou` (установить максимум маны игроку). Нужны права оператора.

### Конфиги

В `pack/config` файлов нет.

## Mana and Artifice

<!-- auto:start card:mana-and-artifice -->
<a id="mana-and-artifice"></a>
- **Версия:** 3.1.11 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mana-and-artifice)
- **Что это:** Магия плетения маны от Mithion: заклинания и ритуалы создаются из частей и рисунков-узоров, прогресс ведёт Оккулус и Кодекс мага; игрок выбирает фракцию (Совет, демоны, феи, нежить) и может вызвать боссов вроде Одина или Повелителя демонов. _(источник: docs сборки)_
- **В сборке:** 3.1.11 Forge 1.20.1; зависимости GeckoLib, Curios уже в сборке
- **В наших docs:** [compat.md](../compat.md) (32), [roadmap.md](../roadmap.md) (20), [bosses.md](../bosses.md) (12), [enchanting-table.md](../enchanting-table.md) (8), [russification.md](../russification.md) (6), [issue-history.md](../issue-history.md) (4)
<!-- auto:end card:mana-and-artifice -->

Автор — Mithion (лицензия All Rights Reserved), модид `mna`; требует Forge ≥ 47.1.64 и Curios, в сборке — вместе с GeckoLib. Сторона — both (нужен и клиенту, и серверу). Дополнения в сборке: ArcaneAdditions, Factions And Curiosities, MNAOverpowered, MNAWitchcraft (порт), Dragon Magic And Relics ([compat.md](../compat.md), раздел «Mana and Artifice и Ars Magica: Legacy»).

### Как играть

Мод об **Элдрине** (тайной энергии) и **плетении маны** (manaweaving): заклинания создаются из частей и «ткутся» рисованием узоров, а не берутся из книги. Путь ведёт через **Кодекс мага** (`guide_book`, встроенная энциклопедия мода с поиском) и **Оккулус** (`occulus`) — блок, показывающий, что нужно сделать для следующего уровня прогресса. Достижения идут по ступеням `tier_0` … `tier_4` (и далее), отдельные ветки — боссы и фракции.

**Основы**
- **Мана** зависит от магического уровня и растёт с ним; полностью восстанавливается за 120 с по умолчанию (настраивается), медленнее при неполной сытости и под эффектом щита. Показана слева сверху вместе с опытом магии. Быстрее — Мана-чай (`mana_coffee`, регенерация) и зелья маны (5 уровней: Minor … Superior, по 20% максимума).
- **Первые шаги (tier 0–1):** добыть `vinteum_ore` → `vinteum_ingot`/пыль; поставить **Рунную кузню** (`runeforge`), **Алтарь плетения маны** (`manaweaving_altar`), **Стол надписей** (`inscription_table`); взять «Мешочек практикующего» (`practitioners_pouch`); сделать жезл плетения (`improvized_manaweaver_wand` → `manaweaver_wand` винтеумовый → химеритовый).
- **Заговоры (cantrips):** рисуются 2+ узорами жезлом, например `ignite`, `gust`, `transmute` (камень → арканный камень, медь → ртуть, гравий → кремень), `dispel`, `drought`, `halp` (сигнальная вспышка), `grimoire`, три «заклинания произвольной формы». Новые открываются с каждым tier.
- **Заклинания** собираются на Столе надписей из частей: форма (`shapes/*`: луч, волна, стена, конус, цепь, рунa, эманация, …), компоненты (`components/*`: урон огнём/молнией, телекинез, взрыв, щит маны, симулякр, приманка …) и модификаторы. Части изучаются/осваиваются («rote» → «mastery») с практикой.
- **Ритуалы:** рисуются **Волшебным мелом** (`wizard_chalk`), запускаются **Очищенной пылью Винтеума** (`purified_vinteum_dust`) в центре, затем выкладываются реагенты и (иногда) рисуются узоры плетения. Примеры из Кодекса: Ритуал Локации (зачаровать компас на структуру/биом), Странствующего мага (торговля, карты, паттерны), Железного колокола (обмен с фракциями), Призыва/Врат, Арканы, Изменения заклинания, и ритуалы-стихии (Ancient Stone — глубоко, Untamed Wind — высоко, Deep Ocean — в океане, Searing Inferno — в Незере, Endless Void — в темноте, Aurora — ночью, Eventide — днём).
- **Мотыльки/«моты»** (motes), мини-башни **конструкций** (`animated_construct`, Станция делегирования), **Источники силы** (`wellspring`: arcane/earth/ender/fire/water/wind) и **Пилоны** Элдрина — поздние механики.

**Фракции** (выбор необратим; враждебны остальным, свои мобы защищают игрока; используя чужие реликвии, провоцируете рейды сильных ассасинов):

| Фракция | Ритуал вступления | Особенность |
|---|---|---|
| Совет волшебников (Council) | Ритуал Совета (`ancient_council`) | ресурс — мана; воины Council: Spellbreaker, Witch Hunter, Living Ward |
| Демоны Бездны (Demons of the Nether) | Burning Hells (`searing_inferno`), подношение — Mote of Fire | ресурс **бримстоун**; больше урона/радиуса, −25% перезарядки; тратит здоровье при нехватке |
| Дворы фей (Fey Courts) | Faerie Courts, подношение — Mote of Air | ресурсы **летний огонь / зимний лёд**; пикси, смертные шапки |
| Нежить (Undead) | Cold Dark (`cold_dark`) | ресурс **души**: сами не восстанавливаются, нужно убивать; горят на солнце, дышат под водой, спят в гробах (+25% душ) — самый трудный путь |

После выбора — Ритуал Sanctum Invocation: блок «Loci of Power» и мультиблок Круга силы (баффы союзникам фракции).

### Предметы/блоки

| Предмет/блок | Назначение |
|---|---|
| `guide_book` (Кодекс мага), `occulus` (Оккулус) | энциклопедия и дерево прогресса |
| `vinteum_ore`, `vinteum_ingot`, `vinteum_dust`, `purified_vinteum_dust` | базовый материал и порошок для ритуалов |
| `chimerite_gem` | самоцвет из `*_chimerite`-руд (изумруд/алмаз/уголь; видимость руды зависит от магического уровня 5/10/20) |
| `runeforge`, `manaweaving_altar`, `inscription_table` | основные станции (жар рунной кузни превращает кости в `bone_ash`) |
| `manaweaver_wand` (винтеумовый), `wand_chimerite`, `staff_chimerite`, `wand_prismarinecrystal`, `improvized_manaweaver_wand` | жезлы/посохи плетения и фокусы |
| `spell_book`, `bangle` (браслеты), `pilgrim_staff`, `thaumaturgic_compass` | заклинания, фокусы, поиск |
| `practitioners_pouch` + «патчи» (Void, Depth, Speed, Glyph, Mote, Rift, Weave, Conveyance, Collection) | карманное хранилище-мешок с улучшениями |
| `wizard_chalk`, `purified_vinteum_dust`, `bell_of_bidding`, `player_charm`, `world_charm` | ритуалы (призыв, обмен, вызов игрока порталом) |
| Цветы: `aum` (леса/равнины), `cerublossom` (леса, равнины, снег), `tarma_root` (болота), `desert_nova` (пустыня), `wakebloom` (вода) | реагенты; красители |
| `mana_coffee`, `mana_coffee_beans` | чай/кофе маны |
| Реликвии фракций: `hellfire_staff`, `grimoire_council`, `allfather_axe`, `bound_axe`, `faction_horn_council` | босс-лут и награды фракций |

Вспомогательные блоки: арканный камень/песчаник (`arcane_stone`, `arcane_sandstone` — результат заговора transmute), `manaweave_cache` (тайники плетения, часть даёт мощный бафф), `mana_crystal`.

### Мобы

Обычные (все — фракционные, нейтральны к безфракционным игрокам): Council — `spell_breaker` (щит, отражает заклинания), `witch_hunter` (арбалет, зелья), `living_ward` (малый летающий конструкт); Fey — `mushroom_soldier` (Смертельная шапка, яд), `pixie` (элементальные болты, 4 стихии); Demons — `demon_imp`. Прочие существа мода (фракцию по данным не определил): `skeleton_assassin`, `hulking_zombie`, `lantern_wraith`, `barkling`, `chatterer`, `wandering_wizard`, `broker`, `arcane_eye`, `insect_swarm`. Спавн — биом-модификатор `mob_spawns_overworld` и структуры фракций.

**Боссы** (характеристики — [bosses.md](../bosses.md), раздел `mna`; зона применения структур ниже):

| Босс | HP | Как вызвать / найти |
|---|---|---|
| `council_warden` Страж Совета | 850 | Ритуал Совета в Большом зале Совета (`council_island`) — «Вызвать на дуэль» старейшин, откликается Warden |
| `wither_lich` Лич-иссушитель | 850 | Ритуал Cold Dark в Большом зале Собора (`cathedral`) |
| `demon_lord` Повелитель демонов | 375 | Ритуал Burning Hells (Mote of Fire) или в Арене демонов (`demon_arena`, Незер) |
| `faerie_queen` Королева фей (лето/зима) | 675 | Ритуал Faerie Courts (Mote of Air) или Великое Древо фей (`fey_tree`, лес) — дуэль с обеими |
| `odin` Один (Всеотец) | 850 | Ритуал «Зов Одина» под открытым небом **во время грозы**; награда — топор Всеотца |
| `pumpkin_king` Король тыкв | 300 | способ призыва не выяснен |
| `tfpd` (Pig Dragon, пасхалка) | — | подсказка в игре о «свинье-драконе»; способ не выяснен |

Достижения боссов: `mna:boss/defeat_council_warden`, `defeat_demon_lord`, `defeat_fey_queens`, `defeat_odin`, `defeat_pig_dragon`, `defeat_pumpkin_king`, `defeat_wither_lich`.

### Эффекты

| Эффект (`mna:…`) | Что делает |
|---|---|
| `mana_regen`, `mana_boost`, `instant_mana` | регенерация / увеличение максимума / мгновенное восстановление маны |
| `mana_shield` | щит маны (замедляет регенерацию) |
| `mana_burn`, `mana_stunt` | сжигание / задержка маны |
| `silence` | нельзя колдовать |
| `levitation`, `icarian_flight`, `eldrin_flight`, `soar` | левитация / полёт |
| `water_walking`, `spider_climbing`, `mist_form`, `greater_invisibility`, `telekinesis` | движение и скрытность |
| `flame_cloak`, `aura_of_frost`, `wind_wall`, `briarthorn_barrier`, `burning_rage` | защитные/боевые ауры |
| `fossilize`, `entangle`, `gradity_well`, `watery_grave`, `asphyxiate`, `snowblind`, `chrono_anchor` | контроль цели |
| `living_bomb`, `lacerate`, `life_tap`, `sunder`, `frailty`, `disjunction`, `soul_vulnerability` | урон и дебаффы |
| `bind_wounds` | лечение стоя на месте (снимается движением/уроном) |
| `eldrin_sight`, `wellspring_sight`, `mind_vision`, `divination` | разведка |
| `mind_control`, `possession`, `confusion` | контроль разума |
| `cold_dark` (Объятия смерти), `circle_of_power` (Союзное святилище) | события ритуалов фракций |
| `enlarged`/`reduced`, `amplify_magic`/`dampen_magic`, `repair`, `earthquake` | прочее |

### События

- Фракционные **рейды**: если игрок использует вещи чужих фракций, его начинают атаковать сильные ассасины (параметры `RaidChanceBase`, `RaidChanceTier`, `ClassicRaids` в конфиге; `/mna raid`).
- Ритуал фей требует «решающих» цветов (летних или зимних) — одновременно нельзя (иначе ритуал не удаётся).
- Подсказки (`helptip.mna.*`): визуализация мультиблоков, закрепление рецепта на HUD, Shift+клик по модификаторам (шаг ×5).

### Данжи/структуры (/locate)

`/locate structure mna:<путь>`; места — `structure_set`: разбросанные (`scatter`) — 20/15, фракционные цитадели — 25/20, тайники плетения — 25/20, боссы — 150/50 (чанки).

| Структура | Биомы | Что |
|---|---|---|
| `mna:scatter/ritual_circle_forest` / `_desert` / `_tundra` | лес-зелёные / сухие / тайга | ритуальные круги |
| `mna:scatter/lonely_tower` | грибные поля | одинокая башня |
| `mna:scatter/winter_tower` | тайга | зимняя башня |
| `mna:scatter/potion_hut` | зелёные биомы | хижина зельевара |
| `mna:scatter/wistful_runesmith` | зелёные биомы | кузнец рун |
| `mna:scatter/research_outpost_swamp` | болота | исследовательский форпост |
| `mna:scatter/respawn_altar` | Незер | алтарь возрождения |
| `mna:caches/smallarcane`, `largearcane` (+ `_broken`) | множество, в т.ч. Эндер | тайники плетения (Manaweave caches) |
| `mna:faction/council_library` | горы | библиотека Совета |
| `mna:faction/demon_fortress` | Незер | крепость демонов |
| `mna:faction/fairy_glade` | леса | поляна фей |
| `mna:faction/undead_crypt` | под землёй в большинстве биомов | склеп нежити |
| `mna:faction/boss/council_island` | равнины, лес, горы, тайга, саванна, пустыня и др. | остров Совета (Большой зал) |
| `mna:faction/boss/cathedral` | леса, холмы, джунгли, тайга, саванна, равнины, болота | собор (босс — лич) |
| `mna:faction/boss/fey_tree` | леса | Великое Древо фей |
| `mna:faction/boss/demon_arena` | Незер: долина душ, базальтовые дельты, пустоши | арена демонов |

В Ритуале Локации игрок зачаровывает компас на структуру/биом (паттерны покупаются у Странствующего мага).

### Чары

Чары мода (`mna:…`): ауры на предметах — `aura-healing` (регенерация), `aura-running`, `aura-jumping`, `aura-deeps` (глубины), `aura-mining`, `aura-repair`, `aura-pilgrim`; оружие/броня — `soulbound` (не теряется), `fireproof`, `gilded`, `xp_to_souls`, `bouncing`, `mana-repair` (чинит предмет маной), `beheading`, `cloudstep`, `transitorystep`, `returning` (телекинетическое возвращение), `bludgeoning`; модификаторы заклинаний фокусов — `duration_modifier`, `range_modifier`, `speed_modifier`. Накладываются ритуально («runic enchant», tier 3+).

### Команды/гамерулы

Корень `/mna` (права оператора). Подкоманды из байткода: прогресс (`tier`, `level`, `complete`, `reset_codex`, `fullReset`), `faction <игрок> <фракция>`, `affinity add|remove|set|reset <игрок> <родство>`, `raid` (шанс 100% — сразу рейд), `wellspring` (создать/удалить источник, `wellspringpower set|add|remove`), `rote` и `mastery` (освоить части заклинаний), `structurediff` (отладка). Гамерулов нет.

### Конфиги

Конфиги Forge мода (общий, клиентский, заклинаний); среди опций: `ManaTicksForRegen` (120 с), `ClassicRaids`, `RaidChanceBase/Tier`, `FactionMobKillIre`, `SoulsForFactionMobKill`, `AverageManaweaveCost` и др.

Наши правки:
- **Фриз сервера** — `mods-src/mm-compat`, миксин `MnaChunkTickMixin` (`mm-compat` 0.5.2): `CommonEventHandler.onWorldTick` обходил чанки 11×11 вокруг игрока через `Level.getChunk`, который на сервере синхронно ждёт генерацию недогруженных чанков; миксин пропускает чанки, которые ещё не FULL. Тест `scripts/tests/mnachunk.js`. Подробно — [compat.md](../compat.md), «Фриз сервера от Mana and Artifice»; [issue-history.md](../issue-history.md) (H-003).
- **Модель пилона** — `pack/kubejs/assets/mna/models/block/obj/pylon/pylon_ring.obj` (копия `pylon_ring_old.obj`): в jar ошибка ссылки ([compat.md](../compat.md)).
- **Атлас блоков** — `pack/kubejs/assets/minecraft/atlases/blocks.json` вырезает `entity/**` других модов, которые `mna` затягивает в атлас (иначе переполнение текстур, [compat.md](../compat.md)).
- Порт MNAWitchcraft под M&A 3.1 — `patches/jar-ports/mnawitchcraft`; русификация Кодекса — `scripts/books/mna_guide.py` ([russification.md](../russification.md)); проверка клиента — [client-checklist.md](../client-checklist.md).

## Mechanical Botania

<!-- auto:start card:mechanicalbotania -->
<a id="mechanicalbotania"></a>
- **Версия:** 1.0.2 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/mechanicalbotania-1.0.2.jar`
- **Что это:** Превращает ману Botania в обороты Create. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (3), [dedup.md](../dedup.md) (1)
<!-- auto:end card:mechanicalbotania -->

### Что даёт игроку

Create: Mechanical Botania (`mechanicalbotania` 1.0.2) связывает Botania и Create: переводит ману в вращение (RPM). Блоки: `mana_motor` (мотор на мане), `spinerette` (+ `spinerette_floating`). В `pack/config` настроек нет; остаточные 2–3 строки в логе про `rpmvalues` — [compat.md](../compat.md).

## MNAOverpowered

<!-- auto:start card:mnaoverpowered -->
<a id="mnaoverpowered"></a>
- **Версия:** 1.20.1-0.11.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mnaoverpowered)
- **Что это:** Аддон Mana and Artifice: мелкие твики для облегчения игры. _(источник: docs сборки)_
- **В сборке:** дополнение M&A: QoL-твики
- **В наших docs:** [jei.md](../jei.md) (4), [compat.md](../compat.md) (2), [russification-audit.md](../russification-audit.md) (2)
<!-- auto:end card:mnaoverpowered -->

### Что даёт игроку

MNAOverpowered 0.11.1: «tweaks about easy gaming experiences» для [Mana and Artifice](#mana-and-artifice). Содержимое (`data/mnaoverpowered`):
- **Ритуалы:** `create_wellspring` («Ritual of Creating Wellspring»), `destroy_wellspring`, `magichem/forget_relic` («Ritual of Relic Reset»).
- **Конструкт:** задача `batch_provide_materia` — массовая подача материи в алхимические устройства по меткам из Book of Marks.
- Рецепт `materia_bottling`; в локали есть подсказка, что вместо бутылки подойдёт Glass Bottle; запрос в духе `/locate` для источников силы (`commands.locate.wellspring.info`).
Конфигов в `pack/config` нет.

## MythicBotany

<!-- auto:start card:mythicbotany -->
<a id="mythicbotany"></a>
- **Версия:** 1.20.1-4.0.4 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mythicbotany)
- **Что это:** Дополнение Botania: новые цветы, предметы и механики. _(источник: docs сборки)_
- **В сборке:** Botania: руны, альфхейм
- **В наших docs:** [jei.md](../jei.md) (4), [enchanting-table.md](../enchanting-table.md) (2), [russification-audit.md](../russification-audit.md) (2)
<!-- auto:end card:mythicbotany -->

### Как играть

MythicBotany 4.0.4 (расширение Botania): после терра-ступени открывает **Альфхейм**, руны девяти миров и **Альфсталь**. Требует Botania.
- Измерение `mythicbotany:alfheim` (биомы `alfheim_plains`, `alfheim_hills`, `alfheim_lakes`, `dreamwood_forest`, `golden_fields`), обратный портал — `return_portal`.
- **Mana-Infuser** (`mana_infuser`) — основной крафтовый блок; для рун миров: `asgard_rune`, `vanaheim_rune`, `alfheim_rune`, `midgard_rune`, `joetunheim_rune`, `muspelheim_rune`, `niflheim_rune`, `nidavellir_rune`, `helheim_rune`; ставятся в `rune_holder`/`central_rune_holder`.
- **Альфсталь** (`alfsteel_ingot`, `alfsteel_sword` Alf Blade, `alfsteel_pick`, `alfsteel_axe`, броня `alfsteel_*`, `alfsteel_pylon`). Кольца: `mana_ring_greatest`, `aura_ring_greatest`, `fire_ring` (Muspelheim), `ice_ring` (Niflheim), `andwari_ring` / `cursed_andwari_ring`.
- Артефакты: `mjoellnir` (молот Мьёльнир; чары `hammer_mobility`), `fimbultyr_tablet`, `kvasir_blood`, `kvasir_mead`, `gjallar_horn_*`, `dream_cherry`.
- Новые цветы: `exoblaze`, `wither_aconite`, `aquapanthus`, `hellebore`, `raindeletia`, `feysythia`, `petrunia`.

### Предметы/блоки

| id (`mythicbotany:…`) | Что даёт |
|---|---|
| `mana_infuser`, `mana_collector` | Алтарь-крафт на мане, сборщик |
| `alfsteel_ingot` и снаряжение, `alfsteel_block`, `alfsteel_pylon` | Верхняя ступень снаряжения |
| `*_rune`, `rune_holder`, `central_rune_holder` | Руны миров |
| `elementium_ore`, `dragonstone_ore`, `gold_ore`, `raw_elementium` | Руды Альфхейма |
| `yggdrasil_branch`, `dreamwood_leaves` | Ветвь Иггдрасиля, листва дримвуда |

### Мобы

`mythicbotany:alf_pixie` (Alfheim Pixie, яйцо `alf_pixie_spawn_egg`), снаряд `mythicbotany:mjoellnir`.

### Данжи/структуры (/locate)

`mythicbotany:elven_house` (шаг 24 / минимум 6 чанков) и `mythicbotany:andwari_cave` (шаг 28 / 8, под землёй, высота -6): `/locate structure mythicbotany:elven_house`, `/locate structure mythicbotany:andwari_cave`.

### Чары

`mythicbotany:hammer_mobility` — «Hammer Mobility» (скорость атаки молота).

### Конфиги

В `pack/config` файлов нет.

## Occultism

<!-- auto:start card:occultism -->
<a id="occultism"></a>
- **Версия:** 1.20.1-1.158.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/occultism)
- **Что это:** Ритуальная магия с призываемыми духами: автоматизация, хранение предметов, демоны, добывающие в пустоте, инструменты и украшения. _(источник: docs сборки)_
- **В наших docs:** [jei.md](../jei.md) (10), [russification.md](../russification.md) (8), [compat.md](../compat.md) (4), [roadmap.md](../roadmap.md) (4), [russification-audit.md](../russification-audit.md) (4), [client-checklist.md](../client-checklist.md) (2)
<!-- auto:end card:occultism -->

### Как играть

Occultism 1.158.0 (модид `occultism`, klikli-dev): магия вызова духов. Гид — Modonomicon-книга (`data/occultism/modonomicon`; в игре — «Dictionary of Spirits»).
- **Старт.** Жертвенные чаши (`sacrificial_bowl`, `golden_sacrificial_bowl`) ставятся в пентакле из **мела** (`chalk_white`, `chalk_gold`, `chalk_purple`, `chalk_red` + кисть `brush`); пентакль можно заранее показать призраком через Dictionary of Spirits. Рецепты ритуалов — `data/occultism/recipes/ritual` (67 файлов); подсказки «Invalid pentacle/ritual» подскажут, чего не хватает.
- **Духи.** Книги связывания (`book_of_binding_*`) нужны, чтобы вызвать духов: Фолиот (`foliot`), Джинн (`djinni`), Африт (`afrit`), Марид (`marid`). Книги призыва (`book_of_calling_*`) задают работу: уборщик, дровосек, перевозчик предметов, оператор машин (`book_of_calling_foliot_*`, `book_of_calling_djinni_manage_machine`).
- **Руды и материалы:** серебро (`silver_ore`, `silver_ore_deepslate`, `silver_ingot`), иезний (`iesnium_ore`, `iesnium_ingot`), **иной камень** (`otherstone`), иномировое дерево (`otherworld_log`, `otherworld_sapling`); подземная роща `occultism:grove_underground` генерируется в мире.
- **Шахтёры-духи** (`miner_*`: `miner_foliot_unspecialized`, `miner_djinni_ores`, `miner_afrit_deeps`, `miner_marid_master`) и «Dimensional Storage» (`storage_controller`, `storage_stabilizer_tier1..4`, `stable_wormhole`, `storage_remote`) — главные автоматизации.
- **Фамильяры**: ритуалы `familiar_*` создают питомцев (`bat_familiar`, `beaver_familiar`, `dragon_familiar`, `fairy_familiar`, `mummy_familiar`, …; бонусы дают эффекты `bat_lifesteal`, `beaver_harvest`, `dragon_greed`, `mummy_dodge`, `double_jump`, `step_height`). Кольцо `familiar_ring` носит одного питомца.
- Дополнения: [Occultism Ritual Satchel](#occultism-ritual-satchel), [Create: Occult Engineering](#create-occult-engineering), [Ars Ocultas](#ars-ocultas).

### Предметы/блоки

| id (`occultism:…`) | Что делает |
|---|---|
| `sacrificial_bowl`, `golden_sacrificial_bowl`, `chalk_glyph_*`, `chalk_*`, `brush` | Пентакль и жертвенники |
| `book_of_binding_*`, `book_of_calling_*`, `soul_gem`, `soul_shard` | Связывание духов и работа |
| `divination_rod`, `otherworld_goggles`, `infused_lenses` | Поиск и зрение «третьего глаза» |
| `satchel` | Ритуальная сумка (расширена аддоном [Occultism Ritual Satchel](#occultism-ritual-satchel)) |
| `storage_controller`, `storage_stabilizer_tier1..4`, `stable_wormhole`, `storage_remote` | Хранилище «иного измерения» |
| `dimensional_mineshaft`, `miner_*` | Автоматическая добыча духами |
| `spirit_campfire`, `spirit_lantern`, `spirit_torch`, `spirit_fire` | Духовные источники света/огня |
| `datura` | Растение для рецептов |
| `butcher_knife`, `taboo_book` | Утилитарные/ритуальные предметы |

### Мобы

`occultism:foliot`, `djinni`, `afrit` (+ `afrit_wild`), `marid` — духи; около 20 фамильяров (`*_familiar`, включая `cthulhu_familiar`, `shub_niggurath_familiar`, `chimera_familiar`); `demonic_husband`, `demonic_wife`; одержимые версии ванильных мобов (`possessed_enderman`, `possessed_ghast`, `possessed_hoglin`, `possessed_phantom`, `possessed_shulker`, `possessed_skeleton`, `possessed_warden` и др. — цели ритуалов `possess_*`); `wild_hunt_skeleton`, `wild_hunt_wither_skeleton` (Дикая охота); `otherworld_bird` (Drikwing).

### Эффекты

`occultism:` `bat_lifesteal`, `beaver_harvest`, `double_jump`, `dragon_greed`, `mummy_dodge`, `step_height`, `third_eye`.

### Конфиги

В `pack/config` файлов Occultism нет. JEI — свой плагин ([jei.md](../jei.md)).

## Occultism Ritual Satchel

<!-- auto:start card:occultism-ritual-satchel -->
<a id="occultism-ritual-satchel"></a>
- **Версия:** 1.0.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/occultism-ritual-satchel)
- **Что это:** Перенос Ritual Satchel из Occultism 1.21.1 в 1.20.1. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1), [jei.md](../jei.md) (1), [russification-audit.md](../russification-audit.md) (1)
<!-- auto:end card:occultism-ritual-satchel -->

### Что даёт игроку

Occultism Ritual Satchel 1.0.2 (`occultism_rs`): сумка, которая **строит пентакль за игрока** по призрачному предпросмотру из Dictionary of Spirits (нужно навести сумку на блок-превью).
- `ritual_satchel_t1` (Apprentice) — внутри связан Фолиот, достраивает пентакль шаг за шагом.
- `ritual_satchel_t2` (Artisanal) — внутри Африт, строит пентакль целиком за раз; апгрейд t1 → t2 ритуалом сохраняет содержимое.
- Сами сумки делаются ритуалами (`ritual_dummy.craft_ritual_satchel_t1`, `…_t2`, `…_upgrade_ritual_satchel`).

Настроек в `pack/config` нет.

## Spell Forge for Electroblob's Wizardry: Redux

<!-- auto:start card:spellforge -->
<a id="spellforge"></a>
- **Версия:** 1.1.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/spellforge-1.1.0.jar`
- **Что это:** Кузница заклинаний: позволяет крафтить все заклинания Electroblob's Wizardry Redux (по образцу Iron's Spells). _(источник: docs сборки)_
- **В наших docs:** [wizardry-port.md](../wizardry-port.md) (1)
<!-- auto:end card:spellforge -->

### Как играть

Spell Forge 1.1.0 (`spellforge`, автор ZettaSword; CurseForge, в сборке лежит jar в `pack/mods/`) — дополнение к [Electroblob's Wizardry Redux](#electroblobs-wizardry-redux): «Spell Forge, позволяющий скрафтить любое заклинание» (по образцу кузницы Iron's Spells).
1. Блок `spell_forge` (Spell Forge) — GUI с полями «Tome of Arcana», «Spectral Dust», «Result» и кнопкой «Forge».
2. Положить внутрь **призрачную пыль** нужной стихии и **Тайный том** (Tome of Arcana) нужной ступени — результат: заклинание-книга этой стихии и ступени.
3. Тома — `novice_tome`, а также рецепты `apprentice_tome`, `advanced_tome`, `master_tome` (`data/spellforge/recipes`); есть рецепты призрачной пыли по стихиям (`spectral_dust_*`) и `magic_crystal_grand`.
Описание заклинаний, которые даёт мод (+11 к реестру Redux), — [wizardry-port.md](../wizardry-port.md). В JEI рецепты — [jei.md](../jei.md).

### Предметы/блоки

| id (`spellforge:…`) | Что делает |
|---|---|
| `spell_forge` | Кузница заклинаний |
| `novice_tome` («Tome of Arcana») | Том для кузницы (по ступеням — отдельные рецепты) |

### Конфиги

В `pack/config` файлов нет.

## Tome of Blood: Rebirth

<!-- auto:start card:tome-of-blood-rebirth -->
<a id="tome-of-blood-rebirth"></a>
- **Версия:** 1.20.1-0.4.5 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/tome-of-blood-rebirth)
- **Что это:** Интеграция Ars Nouveau и Blood Magic; духовный преемник Tome of Blood. _(источник: docs сборки)_
- **В сборке:** Ars Nouveau × Blood Magic
- **В наших docs:** [russification.md](../russification.md) (2)
<!-- auto:end card:tome-of-blood-rebirth -->

### Что даёт игроку

Tome of Blood: Rebirth 0.4.5 (`tomeofblood`) — «Ars Nouveau Integration for Blood Magic»: связывает [Blood Magic](#blood-magic) и [Ars Nouveau](#ars-nouveau). Требует Patchouli, Curios, Ars Nouveau и Blood Magic.
- Книги заклинаний, питающиеся кровью: `novice_tome_of_blood`, `apprentice_tome_of_blood`, `archmage_tome_of_blood`.
- Броня «Еретика»: `living_mage_hood`, `living_mage_robes`, `living_mage_leggings`, `living_mage_boots`.
- `mint_tea`; эффекты `tomeofblood:serene`, `tomeofblood:vulnerable`.
Конфигов в `pack/config` нет.

## Too Many Glyphs

<!-- auto:start card:toomanyglyphs -->
<a id="toomanyglyphs"></a>
- **Версия:** 1.20.1-2.3.2.12345 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/toomanyglyphs-1.20.1-2.3.2.12345.jar`
- **Что это:** Набор дополнений: более гибкие, умные (а иногда просто сильные) заклинания Ars Nouveau. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (2), [russification.md](../russification.md) (1)
<!-- auto:end card:toomanyglyphs -->

### Что даёт игроку

Too Many Glyphs 2.3.2: «more flexible, more intelligent, or just plain overpowered» глифы для Ars Nouveau (17 глифов):
- Фильтры: `Filter: Block`, `Entity`, `Living`, `Not Player`, `Not Monster`, `Player`, `Monster`, `Item`, `Animal`, `Baby`, `Mature`.
- Эффекты и формы: `Lay on Hands`, `Reverse Direction`, `Chaining`, `Ray`.
- Усилители `Amplify II`, `Amplify III`.

Рецепты — `data/toomanyglyphs/recipes`; своя страница в Worn Notebook. Конфигов в `pack/config` нет.

<!-- cards:end -->
