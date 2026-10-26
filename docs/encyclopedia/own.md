# Свои моды и патчи

Моды и порты, собранные в этом репозитории (mods-src/, patches/).

<!-- auto:start toc -->
Модов в категории: **20**.

- [Alshanex Familiars Mod](#alshanex-familiars-mod)
- [Cataclysm: Spellbooks](#cataclysm-spellbooks)
- [CBC: Nukes](#cbc-nukes)
- [Enchanting System Overhaul](#enchanting-system-overhaul)
- [Fish's Undead Rising (1.20.1 port)](#fishs-undead-rising-1201-port)
- [gaboulibs-shim](#gaboulibs-shim)
- [Galacticraft Revival (private port)](#galacticraft-revival-private-port)
- [lycanites-refmap-fix](#lycanites-refmap-fix)
- [M&A Witchcraft](#ma-witchcraft)
- [Minecraft_moded Compat](#minecraft_moded-compat)
- [Minecraft_moded Perf](#minecraft_moded-perf)
- [Origin Furs Forge](#origin-furs-forge)
- [Russian Lang (Minecraft_moded)](#russian-lang-minecraft_moded)
- [T.O Magic 'n Extras](#to-magic-n-extras)
- [Voice Chat Interaction (Forge port)](#voice-chat-interaction-forge-port)
- [Wizardry - Necromancer's Delight (1.20.1 port)](#wizardry---necromancers-delight-1201-port)
- [Wizardry Golems (1.20.1 port)](#wizardry-golems-1201-port)
- [Wizardry Restored](#wizardry-restored)
- [Zombie Have Ears](#zombie-have-ears)
- [方舟武器](#方舟武器)

**См. также:** [Оберег от катаклизмов](own/disaster-ward.md) — подробный разбор нашего мода disaster-ward

Вернуться: [оглавление справочника](INDEX.md).
<!-- auto:end toc -->

## Общее по категории

Здесь — всё, что собрано или починено в этом репозитории. Три группы:

1. **Наши моды** (`mods-src/`, Gradle: ModDevGradle legacyforge + Parchment): [Оберег от катаклизмов](own/disaster-ward.md) (`disaster-ward`), Wizardry Restored, Wizardry Golems, Necromancer's Delight, Fish's Undead Rising (порт), Galacticraft Revival, `mm-compat`, `mm-perf`, `ru-lang`, порт аддонов голосового чата.
2. **Порты чужих jar** (`patches/jar-ports/`, правила для `JarPort` + переходники `mods-src/jar-ports`): Alshanex Familiars, Cataclysm: Spellbooks, CBC: Nukes, M&A Witchcraft, T.O Magic 'n Extras, Arknights Weapon, Origin Furs Forge. Моды собраны под другую версию зависимости; мы переписываем вызовы в байткоде и кладём `*-mmport.jar` в `pack/mods/`.
3. **Патчи** (`patches/`): `gaboulibs-shim`, `lycanites-refmap`, `zombie-have-ears`, `abyssal-jump`, а также скрипты правки данных `space_unify.py`, `ore_crossgen.py`, `northstar_structures_fix.py`, `irons_loot_1_20_1.py`.

Авторство и лицензии: сборка некоммерческая, репозиторий публичный (см. [CLAUDE.md](../../CLAUDE.md), «Назначение и лицензии»). В изменённых файлах и в `docs/` указан автор оригинала; если правообладатель попросит — мод убираем (особенно Fish's Undead Rising, у которого лицензия ARR).
**Убрать наш мод из сборки:** удалить jar из `pack/mods/`, строку `#jar` в `pack/modlist.txt`, выполнить `packwiz refresh` в `pack/`; описание сборки — CLAUDE.md, «Сборка (packwiz)».

**Патчи без своей карточки**
| Патч | Что делает |
|---|---|
| `patches/abyssal-jump` (ASM) | усиление Прыгучести у «Охотников Бездны» (Arknights Weapon) 10 → 2: прыжок ≈12 → ≈3,3 блока. Описание — [compat.md](../compat.md) («Охотники Бездны») |
| `patches/space_unify.py`, `patches/ore_crossgen.py` | единая Солнечная система из Ad Astra + Northstar + Creating Space; руды — [compat.md](../compat.md) («Одна Солнечная система») |
| `patches/northstar_structures_fix.py` | биомы Northstar Structures под Northstar Redux 0.6 и кратеры только на Луне |
| `patches/irons_loot_1_20_1.py` | две таблицы лута Iron's Spells (крипта катакомб, цитадель) поставляются в формате 1.21 и на 1.20.1 не грузятся — скрипт переписывает их в `pack/kubejs/data` |
| `scripts/missing_tag_refs.py` | убирает из тегов ссылки на предметы отсутствующих модов → `pack/kubejs/server_scripts/missing_mod_refs.js` |

---

<!-- cards:start -->
## Alshanex Familiars Mod

<!-- auto:start card:alshanex_familiars -->
<a id="alshanex_familiars"></a>
- **Версия:** 1.20.1_v1.1.2_HotFix · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/alshanex_familiars-1.20.1_v1.1.2-mmport.jar`
- **Что это:** Волшебные питомцы-фамильяры для игроков с Iron's Spells 'n Spellbooks; порт под версию сборки. _(источник: docs сборки)_
<!-- auto:end card:alshanex_familiars -->

### Как играть
Мод Alshanex: **фамильяры** — волшебные питомцы для игроков с Iron's Spells 'n Spellbooks (1.20.1_v1.1.2). Порт `*-mmport.jar` под версию Iron's 3.16 в сборке (`patches/jar-ports/familiars`): правки `KeyState`, `OwnerGetter` (→ `Supplier<Entity>`), `getSummoner()`, `SyncedSpellData` с `HolderLookup`; рецепт котла алхимика заменён крафтом «2 зелья яда + клык паука → флакон яда» (котёл Iron's 3.16 варит только жидкости).

### Предметы/блоки
40 предметов (яйца, предметы призыва, спец-предметы), 1 блок.

### Мобы
34 сущности — питомцы (друид, некромант, бард и др.) и миньоны.

### Эффекты
2 эффекта, 2 атрибута, своя школа магии, 18 заклинаний.

### Команды/гамерулы
2 клавиши (команды питомцам), своих команд нет.

## Cataclysm: Spellbooks

<!-- auto:start card:cataclysm_spellbooks -->
<a id="cataclysm_spellbooks"></a>
- **Версия:** 1.2.9-1.20.1-mmport · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/cataclysm_spellbooks-1.2.9-1.20.1-mmport.jar`
- **Что это:** Мод-связка Cataclysm и Iron's Spells: заклинания, книги, посохи, оружие и броня по мотивам боссов Cataclysm, две новые школы магии (Бездна и Техномантия); в сборке — наш порт под Cataclysm 3.31. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (35), [bosses.md](../bosses.md) (32), [roadmap.md](../roadmap.md) (13), [enchanting-table.md](../enchanting-table.md) (4), [orespawn.md](../orespawn.md) (4), [jei.md](../jei.md) (3)
<!-- auto:end card:cataclysm_spellbooks -->

Автор — Ace The Eldritch King и StarOcean (MIT). В сборке — наш порт `cataclysm_spellbooks-1.2.9-1.20.1-mmport.jar` (мод собран под другую версию Cataclysm; подробности порта — [compat.md](../compat.md), п. К4 и «Cataclysm: Spellbooks 1.2.9»). Требует [Iron's Spells 'n Spellbooks](magic.md#irons-spells-n-spellbooks), L_Ender's Cataclysm, AzureLib.

### Как играть
Мод Ace The Eldritch King и StarOcean (MIT): совместимость **L_Ender's Cataclysm × Iron's Spells 'n Spellbooks** — броня, книги заклинаний и уникальные заклинания по мотивам боссов Cataclysm. Порт `*-mmport.jar` под Cataclysm 3.3x (частицы → `Options`, ракета Иссушителя, ожерелье для фантома Остатка).

Дополнение к Iron's Spells: заклинания, книги, оружие и броня по мотивам боссов Cataclysm. Добавляет две **школы магии** — Бездна (`cataclysm_spellbooks:abyssal`, руна `abyssal_rune`, фокус `abyssal_focus`) и Техномантия (`technomancy`) — со своими атрибутами силы и сопротивления. Заклинания части боссовых наборов нужно **разблокировать**: специальный предмет в инвентаре открывает все заклинания набора (таблица ниже).

| Предмет | Эффект | Откуда |
|---|---|---|
| `burning_manuscript` (из `burning_knowledge_fragment`) | открывает заклинания Игниса | фрагменты с Игниса (80%) |
| `frozen_tablet` (из `frozen_knowledge_fragment`) | открывает заклинания Маледиктуса | фрагменты с Маледиктуса (80%) |
| `strange_disc` | открывает заклинания Вестника (Harbinger) | Вестник (вместе с `mechanical_scrap` 95%, `mechanical_weapon_parts`, `burst_sheath`) |
| `desert_spell_book` (Пустынный том) | уникальная книга | Древний остаток, 50% |
| `abyss_spell_book` Фолиант Р'льеха | уникальная книга | рецепт `cataclysm:amethyst_bless` из `irons_spellbooks:ruined_book` |
| `leviathans_blessing` | иммунитет к бездонным эффектам | Левиафан, 30% |
| `fake_wudjets_staff` | посох | Уаджет, 50% |

### Предметы/блоки

- Книги: `abyss_spell_book`, `desert_spell_book`, `ignis_spell_book`, `codex_of_malice_spell_book`.
- Посохи: `bloom_stone_staff`, `coral_staff`, `void_staff`, `fake_wudjets_staff`; оружие: `spirit_sunderer`, `the_nightstalker`, `the_berserker`, `the_combuster`, `monstrous_flamberge`, перчатки `gauntlet_of_power`/`gauntlet_of_gattling`, `engineers_power_glove`, `soul_brazier`.
- Броня: Игнис (`ignis_*`, есть версия с элитрами), Бездонный чернокнижник (`abyssal_warlock_*`), Курсиум (`cursium_mage_*`), Фараон (`pharaoh_*`), Аметистовая (`bloom_stone_*`), Инженер (`engineer_*`), Экселсиус (`excelsius_*`, режимы Speed/Power/Resist), `monstrous_wizard_hat`; апгрейды `excel_upgrade_mana|resistence|cooldown`, сферы `abyssal_upgrade_orb`, `technomancy_upgrade_orb`.
- Крафт: 35 рецептов + 28 кузнечных (Smithing) для улучшений оружия/брони.

### Мобы

Своих мобов-врагов нет: добавляются **призываемые** существа (`summoned_ignited_revenant`, `summoned_ignited_berserker`, `summoned_koboldiator`, `summoned_koboleton`, `summoned_draugur`/`_royal_`/`_elite_`, `summoned_aptrgangr`, `summoned_amethyst_crab`, `summoned_coral_golem`, `summoned_coralssus`, `summoned_watcher`, `summoned_prowler`, `surveillance_drone`) и босс-призрак `cataclysm_spellbooks:phantom_ancient_remnant` (заклинание `thoths_witness`; 450 HP, [bosses.md](../bosses.md)). Лучи-сущности (`extended_laser_beam`) — служебные.

### Эффекты

`abyssal_predator_effect` (скорость, плавание и урон в воде/под дождём), `incapacitated_effect` (нельзя двигать головой, медленно), `wrathful_effect` (урон растёт с ударами), `shutdown_effect` (нельзя атаковать и использовать предметы, получает урон), `rewired_effect`, `hemophilia`, `sniper` (Глаз снайпера), `*_overcharged_effect` (заряженная мана, сопротивление, перезарядка), `hardware_update`/`software_update`, `intrusion_defense_system_software`, таймеры призыва (`abyssal_gnawer_timer`, `crab_timer`).

### Данжи/структуры (/locate)

Своих структур нет; дропы встроены в таблицы боссов Cataclysm через глобальные модификаторы добычи (`cataclysm_spellbooks:add_to_table`), ID структур Cataclysm — в [bosses.md](../bosses.md).

### Команды/гамерулы

Своих нет. Заклинания — через `/cast`, `/createScroll`, `/learnSpell` из [Iron's Spells](magic.md#irons-spells-n-spellbooks); id вида `cataclysm_spellbooks:void_beam`, `cataclysm_spellbooks:incineration`, `cataclysm_spellbooks:sandstorm`, `cataclysm_spellbooks:atomic_laser`, `cataclysm_spellbooks:comet_shower` и т. д. (около 65 заклинаний: Бездна — `void_beam`, `abyssal_blast`, `dimensional_rift`; Игнис — `incineration`, `bone_storm`, `hellish_blade`; Пустыня — `sandstorm`, `pharaohs_wrath`, `thoths_witness`; Техномантия — `laserbolt`, `emp_blast`, `ddos`, `missile_launch`).

- Тест Потока 50: 136 заклинаний (в т. ч. Thoth's Witness, Aerial Assault, Parting Shot, Cryopiercer, Scorched Earth, Laserbolt, Gravitational Pull); тест `scripts/tests/sbcastcheck.js` 10/10. См. [bosses.md](../bosses.md), [compat.md](../compat.md).

### Конфиги

Наши правки в `mods-src/mm-compat` (0.2.0): лучи `cataclysm_spellbooks:extended_laser_beam` и `extended_death_laser_beam` переведены из категории `MONSTER` в `MISC` (убирает 368 строк «has no attributes» в логе и занятие лимита враждебных мобов; `EntityCategoryFix`). Остальное — порт `-mmport` (классы частиц, ракеты, рендер Древнего остатка). Проверка — `scripts/tests/sbcastcheck.js` (10/10).

## CBC: Nukes

<!-- auto:start card:cbc_nukes -->
<a id="cbc_nukes"></a>
- **Версия:** 2.1.1 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/cbc_nukes-2.1.1-mmport.jar`
- **Что это:** Ядерные боеприпасы для Create Big Cannons; порт под CBC 5.11. _(источник: docs сборки)_
<!-- auto:end card:cbc_nukes -->

### Как играть
Мод SnackPirate (MIT) «CBC: Nuclear» (2.1.1): ядерные боеприпасы для **Create Big Cannons**. Порт `*-mmport.jar` под CBC 5.11.4 (`patches/jar-ports/cbc-nuclear`): свой `safeNbt()`, размер ядерного взрыва считается от силы по блокам.

### Предметы/блоки
4 блока (ядерные снаряды/боеприпасы), 1 сущность (снаряд).

### Конфиги
Нет. Проверка — `portcheck.js`: ядерный снаряд ставится ([compat.md](../compat.md), п. 14).

## Fish's Undead Rising (1.20.1 port)

<!-- auto:start card:fishs-undead-rising-port -->
<a id="fishs-undead-rising-port"></a>
- **Версия:** 0.3.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/fishs-undead-rising-port-0.3.0.jar`
- **Что это:** Частичный порт Fish's Undead Rising: мобы, предметы и эффекты, нужные Necromancer's Delight. _(источник: docs сборки)_
- **В сборке:** свой мод (both): pack/mods/fishs-undead-rising-port-*.jar из mods-src/fishs-undead-rising — частичный порт Fish's Undead Rising (Fish, ARR; по решению владельца, убрать по требованию автора) под ND
- **В наших docs:** [compat.md](../compat.md) (3), [wizardry-port.md](../wizardry-port.md) (3), [roadmap.md](../roadmap.md) (2)
<!-- auto:end card:fishs-undead-rising-port -->

### Как играть
Частичный порт **Fish's Undead Rising** (автор Fish, ARR; namespace `mod_lavacow`) — только то, что нужно Necromancer's Delight. **По требованию автора будет удалён**: см. `mods-src/fishs-undead-rising/NOTICE.md`.
Мобы появляются в естественной среде: Mycosis — влажные и речные биомы, Frigid — холодные, Forsaken — песчаные, Mummy — пустынные храмы; Unburied сам не появляется (только призыв заклинанием).

### Предметы/блоки
55 предметов: добыча (hyphae, poisonspore, cordy_shroom, glowshroom, cursed_fabric, ushabti, acidicheart, shattered_ice, frozenthigh, hatred_shard, forsaken_staff), яйца призыва; 6 блоков.

### Мобы
21 сущность: Unburied, Mummy, Mycosis, Frigid, Forsaken, Salamander (нимфа → 3 стадии, приручение, седло), Emperor Weta, Mimicrab (засада сундуком) и др. + снаряды Death Coil, War Small Fireball.

### Эффекты
Soiled, Corroded, Infested, Fragile («Смертное касание»), Immolation (зелья не перенесены).

### События
Нет.

### Команды/гамерулы
В lang мода есть 3 строки команд; подробности — в NOTICE и [wizardry-port.md](../wizardry-port.md).

### Конфиги
Значения появления по умолчанию как в 1.16.5 (вес/группа в `mod_lavacow-common`); в `pack/config` правок нет.

## gaboulibs-shim

<!-- auto:start card:gaboulibs-shim -->
<a id="gaboulibs-shim"></a>
- **Версия:** 1.0.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/gaboulibs-shim-1.0.0.jar`
- **Что это:** Замена удалённой автором библиотеки GabouLibs для Serene Seasons Plus (patches/gaboulibs-shim). _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (3)
<!-- auto:end card:gaboulibs-shim -->

### Как играть
Патч-заглушка (`patches/gaboulibs-shim`, jar `gaboulibs-shim-1.0.0.jar`): заменяет удалённую автором библиотеку **GabouLibs**, которая нужна Serene Seasons Plus. Игроку ничего делать не надо. Сторона — both.

### Конфиги
Нет. Сборка — `patches/gaboulibs-shim/build.sh`. См. [compat.md](../compat.md) (Serene Seasons Plus).

## Galacticraft Revival (private port)

<!-- auto:start card:galacticraft-revival -->
<a id="galacticraft-revival"></a>
- **Версия:** 0.1.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/galacticraft-revival-0.1.0.jar`
- **Что это:** Перенос геймплея Galacticraft на Ad Astra: лунный данж с боссом, ключи и схемы. _(источник: docs сборки)_
- **В сборке:** свой мод (both): pack/mods/galacticraft-revival-*.jar из mods-src/galacticraft-revival — порт GC: лунный данж с боссом, ключи, схемы (docs/galacticraft.md)
- **В наших docs:** [bosses.md](../bosses.md) (2), [compat.md](../compat.md) (2), [galacticraft.md](../galacticraft.md) (2), [client-checklist.md](../client-checklist.md) (1), [roadmap.md](../roadmap.md) (1)
<!-- auto:end card:galacticraft-revival -->

### Как играть
Наш мод **Galacticraft Revival 0.1.0** — перенос геймплея Galacticraft (Team Galacticraft, micdoodle8, radfast, fishtaco567) на Ad Astra (только он нужен как зависимость). Сейчас готов этап 1: **лунный данж с боссом** в `ad_astra:moon`. Дальше (этап 2) — схемы как прогрессия и Марс. План — [galacticraft.md](../galacticraft.md).
1. Долететь до Луны на ракете Northstar/Creating Space/Ad Astra (одна Солнечная система — [compat.md](../compat.md)).
2. Найти лунный данж, победить босса и открыть сундук ключом.

### Предметы/блоки
Предметы: «Ключ данжа 1 ур.», «Схема: ракета 2 ур.» (открывает детали ракет дальше Луны), «Схема: лунный багги». Блоки: «Кирпич лунного данжа», «Незажжённый факел», «Спавнер босса», «Сундук с сокровищами 1 ур.» (нужен ключ).

### Мобы
5 сущностей: **Evolved Skeleton Boss** и «эволюционировавшие» зомби, скелет, паук, крипер. Мобы данжа внесены в теги кислорода/температуры Ad Astra и Northstar. Если выйти из комнаты босса в бою, босс пропадает; вернитесь, чтобы он появился снова.

### Данжи/структуры (/locate)
Лунный данж — в измерении `ad_astra:moon`. Обычный `/locate` в сборке асинхронный (Async Locator): ответ приходит позже, результат команды всегда 0.

### Команды/гамерулы
Своих нет. Тест: `scripts/tests/gcdungeon.js` (12/12).

### Конфиги
Нет. Авторство ассетов — `mods-src/galacticraft-revival/CREDITS.md`, лицензии GC (MIT и старая) — внутри jar (`META-INF/GALACTICRAFT-*.txt`).

## lycanites-refmap-fix

<!-- auto:start card:lycanites-refmap-fix -->
<a id="lycanites-refmap-fix"></a>
- **Версия:** 1.0.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/lycanites-refmap-fix-1.0.0.jar`
- **Что это:** Недостающий refmap миксинов Lycanites Mobs — без пересборки 120-МБ jar (patches/lycanites-refmap). _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:lycanites-refmap-fix -->

### Как играть
Патч (`patches/lycanites-refmap`, jar `lycanites-refmap-fix-1.0.0.jar`, ~900 байт): добавляет недостающий **refmap миксинов Lycanites Mobs**, чтобы не пересобирать 120-МБ jar. Без него миксины Lycanites (в частности клиентские) не применялись. Игроку ничего делать не надо.

### Конфиги
Нет. Сборка — `patches/lycanites-refmap/build.sh`. Родственное исправление — `LycanitesFearLightMixin` в [mm-compat](#mm-compat).

## M&A Witchcraft

<!-- auto:start card:mnaw -->
<a id="mnaw"></a>
- **Версия:** 0.2.1-alpha · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/mnaw-1.20.1-forge-0.2.1-alpha-mmport.jar`
- **Что это:** Аддон к Mana and Artifice: ведьмовство, куклы и ритуалы симпатической магии; порт под M&A 3.1.11. _(источник: docs сборки)_
- **В наших docs:** [enchanting-table.md](../enchanting-table.md) (1)
<!-- auto:end card:mnaw -->

### Как играть
Аддон **M&A Witchcraft** (nivthefox, GPLv3, 0.2.1-alpha) к Mana and Artifice: ведьмовство — куклы, ритуалы симпатической магии. Порт `*-mmport.jar` под M&A 3.1.11 (классы `ItemPlayerCharm` → `PlayerCharm`, `ItemStaff` → `MagicStaff`).

### Предметы/блоки
13 предметов: **Кукла** и **Связанная кукла** (привязка к мобу/игроку), Окровавленная игла, Амулет из кровавого камня (против симпатии), Амулет и мешочек зелий (иммунитет к зельям), Глаз ведьмы, Трансмутированный серебряный самородок.

### Эффекты
`mnaw:broken_sympathy` («Разорванная симпатия»).

### Чары
`dedication` («Преданность»), `potion_immune` («Иммунитет к зельям») и ещё одно из 3 чар мода.

### Конфиги
Нет. 11 ритуалов (`rituals.*`) подгружаются Mana and Artifice. Фриз M&A у края чанков — `MnaChunkTickMixin` ([mm-compat](#mm-compat)).

## Minecraft_moded Compat

<!-- auto:start card:mm-compat -->
<a id="mm-compat"></a>
- **Версия:** 0.5.12 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/mm-compat-0.5.12.jar`
- **Что это:** Кодовые связки и исправления сборки: кислород скафандров, баги чужих модов, утечки памяти, JEI-категории. _(источник: docs сборки)_
<!-- auto:end card:mm-compat -->

### Как играть
Мод без предметов и блоков (`mm-compat` 0.5.13, `mods-src/mm-compat`): кодовые связки и исправления сборки. Включён у всех, игроку ничего делать не нужно.
1. Скафандры **Northstar** и **Creating Space** работают в мирах **Ad Astra**: пока на существе полный скафандр и в баллоне есть воздух, Ad Astra считает, что кислород есть (`SuitOxygen`, 1 единица в секунду). Пустой баллон — обычное удушье.
2. Прочие исправления — в списке ниже.

### Эффекты
Не добавляет.

### События
Исправления (миксины и обработчики; все описаны в [compat.md](../compat.md)):
| Что | Проблема → решение |
|---|---|
| `EntityCategoryFix` | лучи Cataclysm: Spellbooks с категорией `MONSTER` писали 368 строк «has no attributes» и занимали лимит мобов → категория `MISC` |
| `MissingAttributes` | атрибут `the_massestravels:thought_load_limit` (силы рас Arknights) из отсутствующего мода: регистрируется пустая заглушка, только если оригинального мода нет. Бонус рас к нагрузке мыслей идёт не через неё, а через переопределение сил в `pack/kubejs/data/arks/powers` — см. [Sarkaz's Furnace](arknights.md#sarkazs-furnace-side-fables) |
| `jei/ImbuementAltarCategory` | категория JEI для алтаря наложения (Redux) |
| `SbwShipHitMixin` | Superb Warfare: попадание по кораблям Valkyrien Skies ставило блок «в воздух» → берём блок корабля |
| `MnaChunkTickMixin` | Mana and Artifice: блокирующий `getChunk` в `onWorldTick` вешал сервер на минуты у края загруженной области |
| `LycanitesFearLightMixin` | затемнение от страха Lycanites: их миксин в собранной игре не применялся (SRG-имена) |
| `LostCitiesNeighborMixin` | утечка кучи The Lost Cities (`BuildingInfo` держал соседей) → OOM при полёте |
| `StructureTemplatePlaceMixin` | огромные шаблоны структур (1–2 млн блоков у bobsoriginiumdream, caerula_arbor, Aether Villages) копировались целиком в каждый чанк → фильтр по коробке чанка |
| `NorthstarCraterJoinMixin` | Northstar Structures удаляло любых существ в «кратерах» Обычного мира → обработчик только на Луне |
| `FallingTreeDamageMixin` | Falling Tree: если топору не хватает прочности на всё дерево — ломается один блок, как в ванили |
| `DhChunkyBindMixin` | Distant Horizons 3.3.3 подписывался на Chunky на первом загруженном чанке, пока Chunky ещё не запущен → «Chunky is not loaded», падение сервера при старте; ждём запуска Chunky |
| `OriginFursReloadCacheMixin` | клиент: раз в ~25 с перечислялись все ресурс-паки (фриз 4 с) → кэш до следующей перезагрузки ресурсов |
| `SspSnowChunkMixin`, `FallingTreeLeafChunkMixin`, `Weather2StormBiomeMixin` | Serene Seasons Plus (снег/таяние), Falling Tree (опадание листвы у соседей) и Weather2 (биом новой тучи) синхронно грузили ещё не готовые чанки на главном потоке → тик до 54 с при входе/телепорте в новое место; теперь такие чанки пропускаются (поток 67) |

### Команды/гамерулы
Нет.

### Конфиги
Нет собственных; зависимости — Forge ≥ 47 и Ad Astra ≥ 1.15. Сборка: `cd mods-src/mm-compat && ./gradlew build` → `pack/mods/mm-compat-*.jar`.

## Minecraft_moded Perf

<!-- auto:start card:mm-perf -->
<a id="mm-perf"></a>
- **Версия:** 0.3.2 · **Сторона:** client · **Источник:** jar в репозитории: `pack/mods/mm-perf-0.3.2-client.jar`
- **Что это:** Клиентские удобства: режим слабого ПК, отвязка клавиш модов, предупреждения о памяти, ускорение JEI. _(источник: docs сборки)_
<!-- auto:end card:mm-perf -->

### Как играть
Только клиент (`mm-perf` 0.3.2, `pack/mods/mm-perf-*-client.jar`; на сервер не ставится).
1. **Режим слабого ПК:** кнопка «Режим слабого ПК» / «Вернуть как было» внизу слева на экранах настроек видео (ваниль и Embeddium) и в «Настройки», либо клавиша `key.mm_perf.lowspec` («Minecraft_moded: производительность», не назначена). Ставит дальность 5, симуляцию 5, графику «Быстро», облака выкл., частицы «минимум», мягкий свет и тени сущностей выкл., 60 FPS, VSync выкл.; Embeddium, Oculus (шейдеры) и Distant Horizons (рендер) выключаются, если они есть. Прежние значения хранятся в `config/mm-perf-lowspec.json` до «Вернуть».
2. **Клавиши модов «Не назначено».** При первом запуске каждая клавиша не ванильного мода, стоявшая на заводском значении, отвязывается (флаг `config/mm-perf-keys.json`). Назначьте нужные в «Управление» — там есть поиск по названию (Controlling).
3. **Память.** В главном меню — красная строка, если Xmx > 60 % ОЗУ ПК (с рекомендацией ≤ Z ГБ; тот же текст — один раз в чат при входе в мир и WARN в лог; порог для проверки: `-Dmm_perf.mem.threshold=<%>`), и тост, если выделено < ~7 ГБ или > 70 % ОЗУ; в мире — если > 90 % кучи занято после сборки мусора около 30 с. В лог при старте пишется `[mm_perf] память: Xmx …, ОЗУ ПК …`; предупреждение, если Xmx > 70 % ОЗУ или свободно меньше 4 ГБ.
4. Миксины `JeiAnvilRecipeMaker`/`JeiGrindstoneRecipeMaker` убирают из JEI строки «снять чары на точиле» и «предмет + зачарованная книга» на наковальне: при каждом входе в мир JEI прогонял настоящие меню для каждого предмета и чар (~1,8 мин из ~3,5 мин запуска JEI); рецепты ремонта остаются.
5. **Экран «Сохранение мира»** (0.3.2, R-012): подпись «не закрывайте игру» и счётчик секунд — при выходе из одиночного мира после полёта/портала игра ждёт догенерации местности (до минуты); в лог `[mm_perf] мир сохранён за N с / world saved in N s` (дольше 10 с — WARN с подсказкой). Строки mm-perf в логе продублированы латиницей.

### Команды/гамерулы
Нет. Самотест: `-Dmm_perf.selftest=true` (`SelfTest`), `-Dmm_perf.selftest.quit=<с>` — через N с в одиночном мире «Сохранить и выйти» (`SaveWatch`).

### Конфиги
`config/mm-perf-lowspec.json` (прежние настройки), `config/mm-perf-keys.json` (флаг первого отвязывания). Рядом в сборке: `pack/config/DistantHorizons.toml` (радиус 48, LOW/LOW, 1 поток), `pack/config/fml.toml` (`versionCheck=false`), ресурспак Potato Craft 8× в `pack/resourcepacks/` (выключен по умолчанию), Controlling + Searchables. Подробности — [compat.md](../compat.md) («Клиентские удобства», «R-003»); если зависает загрузка — `docs/friends.md` (Xmx ≤ ~60 % ОЗУ).

## Origin Furs Forge

<!-- auto:start card:origin_visuals -->
<a id="origin_visuals"></a>
- **Версия:** 1.0.5 · **Сторона:** client · **Источник:** jar в репозитории: `pack/mods/origin_visuals-1.0.5-mmport-client.jar`
- **Что это:** Клиентский порт Origin Furs: модели игрока для рас Origins (со встроенной копией AzureLib 2). _(источник: docs сборки)_
- **В сборке:** свой порт (client): pack/mods/origin_visuals-1.0.5-mmport-client.jar — Origin Furs (зависимость Arknights Furs) под
<!-- auto:end card:origin_visuals -->

### Как играть
Клиентский порт **Origin Furs** (pandaismyname1, MIT; оригинал для Fabric; `origin_visuals-1.0.5-mmport-client.jar`): модели игрока для рас Origins (в паре с *Arknights origin furs*). Порт встраивает копию AzureLib 2.0.39 (MIT, AzureDoom) в свой пакет, потому что в сборке AzureLib 3.1 без старого API. Фриз клиента (перечисление ресурс-паков раз в ~25 с) снимает `OriginFursReloadCacheMixin` в [mm-compat](#mm-compat).

### Конфиги
Нет. Сторона — client.

## Russian Lang (Minecraft_moded)

<!-- auto:start card:ru-lang -->
<a id="ru-lang"></a>
- **Версия:** 1.0.0 · **Сторона:** client · **Источник:** jar в репозитории: `pack/mods/ru-lang-1.0.0-client.jar`
- **Что это:** Клиентский мод: всегда включает русский ресурспак сборки и переводит захардкоженные строки модов. _(источник: docs сборки)_
- **В сборке:** наш клиентский мод (mods-src/ru-lang): всегда включённый верхний ресурспак russian-moded; пересобирать scripts/build-ru-pack.sh после правок переводов
<!-- auto:end card:ru-lang -->

### Как играть
Клиентский мод (`ru-lang` 1.0.0, `mods-src/ru-lang`): подключает русский ресурспак сборки (`russian-moded`: недостающие переводы, названия модов остаются английскими) как всегда включённый и самый верхний — не зависит от `options.txt` игрока. Также переводит «литералы» (строки, захардкоженные в коде модов) по таблице `assets/ru_lang/literals_ru.json` (миксин `LiteralContentsMixin`). Включайте язык игры «Русский».
Источник переводов — `pack/resourcepacks-src/russian-moded/assets/<мод>/lang/ru_ru.json`, пакуется скриптом `scripts/build-ru-pack.sh`. Проверка — `scripts/lang_validate.py`; аудит и глоссарий — [russification.md](../russification.md), [russification-glossary.md](../russification-glossary.md).

### Конфиги
Нет. Сторона — client.

## T.O Magic 'n Extras

<!-- auto:start card:traveloptics -->
<a id="traveloptics"></a>
- **Версия:** 6.3.0-1.20.1 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/traveloptics-6.3.0-1.20.1-mmport.jar`
- **Что это:** Большое дополнение к Cataclysm и Iron's Spells: оружие, броня, боссы и 176 заклинаний; порт под версии сборки. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (7), [roadmap.md](../roadmap.md) (4), [enchanting-table.md](../enchanting-table.md) (2)
<!-- auto:end card:traveloptics -->

### Как играть
**T.O Magic 'n Extras** (GameTechBC, ARR; 6.3.0): большое дополнение к Cataclysm × Iron's Spells — оружие, броня, боссы и 176 заклинаний. Порт `*-mmport.jar` под Cataclysm 3.31 и Iron's 3.16.3 (частицы → `Options`, `Boltstrike_Entity` → `Bolt_strike_Entity`, ракета Харбингера и др.). Требует **Apothic Attributes** и **Placebo** (в сборке).

### Предметы/блоки
591 предмет (оружие, броня, ключи боссов).

### Мобы
79 сущностей (боссы с боссбарами и обычные), 41 строка `boss_screen`.

### Эффекты
123 эффекта (в основном боевых).

### Команды/гамерулы
Три тега мода ссылаются на убранных существ Cataclysm — чистятся `missing_mod_refs.js`. Формулы брони Apothic (смешанный вариант) — в `pack/config/attributeslib.cfg` ([compat.md](../compat.md), п. 14).

## Voice Chat Interaction (Forge port)

<!-- auto:start card:voicechat-addons-forge -->
<a id="voicechat-addons-forge"></a>
- **Версия:** 1.20.1-1.0.6-forge-1.0.1 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/voicechat-addons-forge-1.0.1.jar`
- **Что это:** Forge-порт трёх аддонов Simple Voice Chat от Max Henkel: AudioPlayer, Enhanced Groups, Voice Chat Interaction. _(источник: docs сборки)_
- **В сборке:** mods-src/voicechat-addons → pack/mods/voicechat-addons-forge-*.jar (both)
- **В наших docs:** [compat.md](../compat.md) (5), [roadmap.md](../roadmap.md) (3)
<!-- auto:end card:voicechat-addons-forge -->

### Как играть
Порт на Forge трёх аддонов **Max Henkel** (автор Simple Voice Chat; в оригинале только Fabric) в одном jar: **Voice Chat Interaction** (`vcinteraction`), **AudioPlayer** (`audioplayer`), **Enhanced Groups** (`enhancedgroups`). Нужны Simple Voice Chat и Admiral.
- **AudioPlayer.** Оператор загружает свой звук и записывает его на пластинку, рог козла, нотный блок, голову игрока или инструмент: `/audioplayer upload web` (ссылка для загрузки через браузер, порт 8080 при `run_web_server=true`), `/audioplayer upload serverfile [имя]` (файл из папки сервера, mp3/wav), `upload url <ссылка>`, `upload filebin`; затем с предметом в руке `/audioplayer apply [файл] [радиус] [название]`, `/audioplayer apply musicdisc|goathorn …`, `/audioplayer clear`, `/audioplayer volume`, `/audioplayer play|stop`.
- **Enhanced Groups.** Постоянные группы, автовход и «мгновенные» группы по расстоянию: `/persistentgroup add|remove|list`, `/instantgroup [радиус]`, `/autojoingroup set|remove`, `/autojoingroup global …`, `/forcejoingroup <игрок>`.
- **Voice Chat Interaction** (голос будит скалк-сенсоры) в сборке **выключен** (`pack/config/vcinteraction/vcinteraction.properties`: `enabled=false`), потому что те же вибрации даёт Vibrative Voice.

### Эффекты
Нет. Событие игры `vcinteraction:voice` (частота 7) зарегистрировано, но при `enabled=false` микрофон не слушается.

### События
При входе игроку показывается сводка групп (`group_summary`).

### Команды/гамерулы
Права — по уровням оригинала (Fabric Permissions API на Forge нет): у `enhancedgroups` по умолчанию `instantgroup` и `autojoingroup` — всем, `persistentgroup`, `autojoingroup global` и `forcejoingroup` — только операторам (настраивается в конфиге `*_permission_level`).

### Конфиги
`pack/config/vcinteraction/vcinteraction.properties` (мод выключен); конфиги AudioPlayer и Enhanced Groups создаются в `config/` при первом запуске. Сервер голоса — `pack/config/voicechat/voicechat-server.properties` (UDP 24454). Подробности порта, изменённых мест и авторства — `mods-src/voicechat-addons/README.md`; прогон — `scripts/tests/voicecheck.js` и [compat.md](../compat.md) («Голосовой чат»).

## Wizardry - Necromancer's Delight (1.20.1 port)

<!-- auto:start card:necromancers-delight -->
<a id="necromancers-delight"></a>
- **Версия:** 0.2.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/necromancers-delight-0.2.0.jar`
- **Что это:** Порт Wizardry — Necromancer's Delight (WinDanesz): 15 заклинаний некромантии и 12 артефактов. _(источник: docs сборки)_
- **В сборке:** свой мод (both): pack/mods/necromancers-delight-*.jar из mods-src/necromancers-delight — порт Necromancer's Delight 1.12.2 (WinDanesz, GPL-3.0), docs/wizardry-port.md
- **В наших docs:** [wizardry-port.md](../wizardry-port.md) (28), [roadmap.md](../roadmap.md) (21), [compat.md](../compat.md) (15), [client-checklist.md](../client-checklist.md) (4), [jei.md](../jei.md) (4), [enchanting-table.md](../enchanting-table.md) (3)
<!-- auto:end card:necromancers-delight -->

### Как играть
Порт Wizardry — Necromancer's Delight (WinDanesz, GPL-3.0). Для работы использует мобов и предметы **Fish's Undead Rising** (наш порт, см. ниже).
Дополнение к Redux (0.2.0): **15 заклинаний** и **12 артефактов** (ND-3 закрыт). Заклинания: `bound_bone_sword`, `bound_frozen_dagger`, `bound_spectral_dagger`, `bound_vespa_shield` (призванное оружие), `holy_hand_grenade`, `ghost_bomb`, `wail_of_the_banshee` (бомбы), `summon_vespa`, `conjure_banshee`, `conjure_mycosis`, `summon_salamander` (призывы), `summon_mana_leech`, `summon_elemental_leech` (пиявки), `trial_of_mushrooms`, `carcinize` (луч по сундуку: ломает его и даёт ручного мимикраба). Рецептов оружия и бомб нет — предметы берутся из заклинаний и креатива.

### Предметы/блоки
37 предметов; артефакты (Curios): Leechlink, Spell-Sapping Venom, Fallen Legion's Ring, Ring of the Nameless, Frigidbone Ring, Ring of Hatred, Bone Bag, Malevolent Ushabti, Book of Witchcraft, Thornbelt, Crown of Pests, Leech Crystal Amulet; блок — временный светящийся гриб.

### Мобы
Свои сущности: бомбы (`stink_bomb`, `frost_bomb`), миньоны-пиявки (`mana_leech`, `elemental_leech`), `vespa_minion`. Остальные мобы призыва — из порта Fish's Undead Rising.

### Эффекты
`poisonous_spore`, `locusts` (оба свои); эффекты Fish (Corroded и др.) — из `mod_lavacow`.

### Команды/гамерулы
Своих нет. Состояние порта и список ещё не перенесённых заклинаний — [wizardry-port.md](../wizardry-port.md) (разделы «ND-3»).

## Wizardry Golems (1.20.1 port)

<!-- auto:start card:wizardry-golems -->
<a id="wizardry-golems"></a>
- **Версия:** 0.2.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/wizardry-golems-0.2.0.jar`
- **Что это:** Порт Wizardry Golems (WinDanesz): заклинания Golemancy, 31 тип голема, 36 артефактов и Living Snow. _(источник: docs сборки)_
- **В сборке:** свой мод (both): pack/mods/wizardry-golems-*.jar из mods-src/wizardry-golems — порт Wizardry Golems 1.12.2 (WinDanesz, GPL-3.0), docs/wizardry-port.md
- **В наших docs:** [wizardry-port.md](../wizardry-port.md) (5), [roadmap.md](../roadmap.md) (3), [compat.md](../compat.md) (2), [enchanting-table.md](../enchanting-table.md) (1)
<!-- auto:end card:wizardry-golems -->

### Как играть
Порт Wizardry Golems 1.3.3 (WinDanesz, GPL-3.0) на Redux. Нужны Electroblob's Wizardry Redux 0.8.9 и **Extra Golems 20.1.0.2** (`#jar` в modlist).
1. Заклинания **Golemancy** пяти стихий (Earth, Fire, Ice, Lightning, Radiant): продвинутый уровень, мана 50, перезарядка 260, голем живёт 600 тиков. На мастер-палочке стихии призывается «старший» голем (Obsidian, Flame, Winter, Maelstrom/Spark, Gold).
2. Артефакты (кольца, амулеты, пояса, плащи) меняют состав призыва: «Древний император» → терракотовая армия, «Уголь» → 2 печных голема и т. д.
3. **Living Snow** генерируется в снежных биомах, призывает снежных големов.

### Предметы/блоки
74 предмета (артефакты, бутылка Living Snow), блоки `living_snow` и `permafrost` (временный). Книги/свитки Golemancy не перенесены — заклинания идут в обычные книги Redux.

### Мобы
31 тип миньона-голема: обычные Extra Golems + 11 своих (thunderstone, lodestone, spark, maelstrom, flame, permafrost, packed ice, snow, winter…). Кастующие: Flame, Winter, Spark, Lodestone, Maelstrom, Gold (лечит хозяина).

### События
Эффекты при смерти голема (землетрясение, молния, взрыв, силки, ледяные сигилы), ауры около големов.

### Данжи/структуры (/locate)
Нет структур; Living Snow — `placed_feature` в снежных биомах.

### Конфиги
Конфиг: места инъекции лута в сундуки подземелий и измерения Living Snow. Тест: `scripts/tests/golemcheck.js` (13/13). Подробности — [wizardry-port.md](../wizardry-port.md) и [compat.md](../compat.md).

## Wizardry Restored

<!-- auto:start card:wizardry-restored -->
<a id="wizardry-restored"></a>
- **Версия:** 0.6.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/wizardry-restored-0.6.0.jar`
- **Что это:** Дополнение к Electroblob's Wizardry Redux: 61 недостающее заклинание оригинала 1.12.2, сущности, статуи и 18 артефактов. _(источник: docs сборки)_
- **В наших docs:** [roadmap.md](../roadmap.md) (3), [client-checklist.md](../client-checklist.md) (2), [compat.md](../compat.md) (2), [jei.md](../jei.md) (1), [russification.md](../russification.md) (1), [wizardry-port.md](../wizardry-port.md) (1)
<!-- auto:end card:wizardry-restored -->

### Как играть
Дополнение к **Electroblob's Wizardry Redux 0.8.9**: возвращает 61 заклинание оригинала 1.12.2, которых нет в Redux, плюс 4 «тестовых» заклинания Redux (`lightning_pulse`, `summon_ice_giant`, `conjure_armor`, `silverfish_swarm`) и 18 артефактов.
1. Заклинания добавлены в реестр Redux (`ebwizardry:spell`) под namespace `wizardry_restored` и используются как остальные заклинания Redux: книги и свитки, палочки по уровню и стихии (Novice → Apprentice → Advanced → Master).
2. Артефакты (кольца, амулеты, обереги) надеваются в слоты Curios.
3. Для «камней перемещения» (заклинание `transportation`): 4 каменных кирпича + магический кристалл → 4 камня; ставятся кругом в 2 блоках от центра по сторонам света, Shift в центре — запомнить место, обычный каст — перенестись (в том числе в другое измерение).

Полный список, статусы и упрощения — [wizardry-port.md](../wizardry-port.md).

### Предметы/блоки
- Блок `wizardry_restored:transportation_stone` («Камень перемещения»).
- Артефакты (Curios): `ring_stormcloud`, `ring_hammer`, `ring_interdiction`, `ring_defender`, `ring_mind_control`, `ring_disintegration`, `charm_light`, `charm_silk_touch`, `charm_stop_time`, `charm_transportation`, `charm_abseiling`, `charm_black_hole`, `amulet_auto_shield`, `amulet_resurrection`, `amulet_anchoring`, `amulet_fire_cloaking`, `amulet_frost_warding`, `amulet_banishing`. Не оживлены: `charm_sixth_sense`, `charm_hunger_casting`, `ring_soulbinding` (последний есть в Redux).

### Мобы
Миньоны и конструкции (сущности): `phoenix` (Феникс), `radiant_totem`, `withering_totem`, `forcefield`, `black_hole`, `stormcloud`, `decoy` (Приманка), `snare` (Силок), `thorns` (Шипы), `lightning_disc`, `lightning_hammer`; плюс призывы Redux (духовные волк/конь, ледяной/теневой призрак, штормовой элементаль, железный голем) через его `MinionSpell`.

### Эффекты
`wizardry_restored:mark_sacrifice` («Метка жертвы»): следующая магическая атака владельца +60 % за уровень, метка снимается. Остальные эффекты заклинаний — из Redux (обморожение, паралич, «Защита»…).

### События
Нет. Временные блоки (лёд, свет) убираются по таймеру и при остановке сервера (`util/TempBlocks`); длительные состояния (заслон, покров, контроль, конь) сохраняются в NBT существа и переживают перезапуск.

### Данжи/структуры (/locate)
Своих структур нет.

### Команды/гамерулы
Своих команд нет. Заклинания проверяются тестом `scripts/tests/castcheck.js` (69/69 заклинаний и артефактов кастуются фейковым игроком).
**Отключены на выделенном сервере** (метка `SENSIBLE` у Redux): `lightning_pulse`, `summon_ice_giant`, `conjure_armor`, `silverfish_swarm` — у нас оставлены классы под своим именем и они работают; упрощённо: sixth_sense светит всем рядом, possession/resurrection без подмены камеры/экрана смерти.

### Конфиги
Конфиг-файла нет. Сборка: `cd mods-src/wizardry-restored && ./gradlew build`, jar без `-reobf` → `pack/mods/`. Иконки — `tools/make_icons.py`. Перевод — `ru_ru.json` в jar (155 строк, из них 142 — заклинания).

## Zombie Have Ears

<!-- auto:start card:zombiehaveears -->
<a id="zombiehaveears"></a>
- **Версия:** 1.1.2 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/zombiehaveears-1.1.2-forgefix.jar`
- **Что это:** Зомби слышат голос игроков в Simple Voice Chat; наша пересборка с исправленным mods.toml для Forge 47. _(источник: docs сборки)_
- **В сборке:** patches/zombie-have-ears/build.sh → pack/mods/zombiehaveears-1.1.2-forgefix.jar
- **В наших docs:** [compat.md](../compat.md) (3), [roadmap.md](../roadmap.md) (2)
<!-- auto:end card:zombiehaveears -->

### Как играть
Мод **Zombie Have Ears** (SKALKMine, ARR): зомби слышат, как игрок говорит в Simple Voice Chat, и идут на звук; чем громче голос, тем дальше слышно, шёпот безопаснее. В оригинале 1.1.2 `mods.toml` записан в формате NeoForge (`type = "required"`, без `mandatory`), и Forge 47 считал файл битым и запускал сервер без модов. Мы перепаковали jar с исправленным `mods.toml` (код не тронут) скриптом `patches/zombie-have-ears/build.sh` → `zombiehaveears-1.1.2-forgefix.jar`.

### Конфиги
Нет. Проверено вместе с Simple Voice Chat 2.6.24: плагин регистрируется ([compat.md](../compat.md)).

## 方舟武器

<!-- auto:start card:arknights_weapon -->
<a id="arknights_weapon"></a>
- **Версия:** 1.5.6 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/arknights_weapon-1.5.6-mmport.jar`
- **Что это:** Оружие Arknights (порт оригинала с CurseForge): убран RiseProcedure, удваивавший здоровье мобов при каждой загрузке чанка. _(источник: docs сборки)_
<!-- auto:end card:arknights_weapon -->

### Как играть
Мод «Arknights_Weapon» (MCreator, AFL 3.0, 1.5.6): оружие и броня по Arknights, в т. ч. гигантский меч Скади, арбалет Анжелы, аптечка Люмен, якорь Ульбиана, смычок и виолончель, набор «Охотник из бездны» (образец «Агор») и «Снежный» (Snow Suit). Наш порт `*-mmport.jar`: **отключён `RiseProcedure`**, который в оригинале удваивал `max_health` всех мобов при каждой загрузке чанка. Поверх порта наложен ASM-патч `patches/abyssal-jump`.

### Предметы/блоки
Оружие: Гигантский меч Скади/«рассекающий волны», арбалет «стреляющий чернилами», Мельница Лаурентины, Тонкий меч и стандартное огнестрельное оружие Трибунала, Копьё Гледии, Якорь Ульбиана, «Печь, сжигающая зло»; снаряжение: «Агор» (4 предмета), Snow Suit (4), Игла генетической модификации глубинных охотников, Аптечки; блок/руда «Горная снежная руда».

### Мобы
`arknights_weapon:snow_ghost` («Горный снежный призрак») — моб с топором.

### Эффекты
`abyssal_hunters` («Глубинный охотник»), `apoptotic_injury`, `burning_injury`, `circular_saw_2` («Тяжесть выживания»), `freeze`, `snow` и «нормальные состояния» апоптоза/горения. **Патч:** Прыгучесть от этих эффектов и брони «Агор» снижена с усиления 10/6/3 до 2 («Прыгучесть III», ≈3,3 блока вместо ≈12).

### Чары
`apoptoic_damage` (апоптоз), `fire_damage_1` (горение), `urbians_wardrobe` («Гардероб Ульбиана»), `wet_sword_bag`.

### Конфиги
Нет. Проверка: `scripts/tests/mobhealth_commands.txt` (здоровье мобов не растёт после перезагрузки чанка), `scripts/tests/abyssaljump.js`. Подробности — [compat.md](../compat.md).

## Enchanting System Overhaul

<!-- auto:start card:eso-forge -->
<a id="eso-forge"></a>
- **Версия:** 1.2.2 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/eso-forge-1.20.1-1.2.2-mmport.jar`
- **Что это:** Enchanting System Overhaul: стол зачарования без рандома — чары сначала изучают из найденных книг, потом накладывают за материалы; порт под моды брони сборки. _(источник: docs сборки)_
- **В наших docs:** [enchanting-table.md](../enchanting-table.md) (3), [compat.md](../compat.md) (1), [friends.md](../friends.md) (1), [roadmap.md](../roadmap.md) (1)
<!-- auto:end card:eso-forge -->

### Как играть

Ванильный стол зачарования открывает меню ESO: никаких случайных чар и опыта. Чару сначала **изучают** — правый клик
зачарованной книгой в руке (книга рассыпается, знание остаётся у персонажа), потом накладывают на предмет за
материалы. Подробно для игроков — [friends.md](../friends.md) «Стол зачарования», для ведущего и таблица источников
книг — [enchanting-table.md](../enchanting-table.md).

### Чары

Своих чар нет. Книги для модовых чар, у которых их не было (Alex's Caves, Mana and Artifice, Spore, «Аннигиляция»
Sarkaz's Fables), добавлены в лут сундуков: `pack/kubejs/server_scripts/eso_enchant_books.js`.

### Команды/гамерулы

`/eso learn <игрок> <чара|all>`, `/eso forget <игрок> <чара|all>`, `/eso learn-leveled <игрок> <чара> add|set|sub <уровень>`,
`/eso get-enchantment-id-in-hand` / `get-item-id-in-hand` / `get-mat-id-in-hand` (оператор).

### Конфиги

`config/eso/config.json` (изучение включено, уровни открываются сразу, лимит чар 4), `config/eso/default-recipe.json`
(цена чар без своего рецепта ≈ ×2,5), `config/eso/material-overrides/` (лимиты по материалам, генерируется модом).
Порт: `patches/jar-ports/eso` — `getName()` модовой брони с `@OnlyIn(CLIENT)` ронял сервер (поток 57).

<!-- cards:end -->
