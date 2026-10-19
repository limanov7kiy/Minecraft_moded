# Свои моды и патчи

Моды и порты, собранные в этом репозитории (mods-src/, patches/).

<!-- auto:start toc -->
Модов в категории: **19**.

- [Alshanex Familiars Mod](#alshanex-familiars-mod)
- [Cataclysm: Spellbooks](#cataclysm-spellbooks)
- [CBC: Nukes](#cbc-nukes)
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

Вернуться: [оглавление справочника](INDEX.md).
<!-- auto:end toc -->

## Общее по категории

_Обзор категории — вручную, следующие этапы._

---

<!-- cards:start -->
## Alshanex Familiars Mod

<!-- auto:start card:alshanex_familiars -->
<a id="alshanex_familiars"></a>
- **Версия:** 1.20.1_v1.1.2_HotFix · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/alshanex_familiars-1.20.1_v1.1.2-mmport.jar`
- **Что это:** Mod that adds a bunch of magical pets to aid you in your journey. _(источник: mods.toml)_
<!-- auto:end card:alshanex_familiars -->

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

## Cataclysm: Spellbooks

<!-- auto:start card:cataclysm_spellbooks -->
<a id="cataclysm_spellbooks"></a>
- **Версия:** 1.2.9-1.20.1-mmport · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/cataclysm_spellbooks-1.2.9-1.20.1-mmport.jar`
- **Что это:** Мод-связка Cataclysm и Iron's Spells: заклинания, книги, посохи, оружие и броня по мотивам боссов Cataclysm, две новые школы магии (Бездна и Техномантия); в сборке — наш порт под Cataclysm 3.31. _(источник: docs сборки)_
- **В наших docs:** [bosses.md](../bosses.md) (32), [compat.md](../compat.md) (32), [roadmap.md](../roadmap.md) (12), [orespawn.md](../orespawn.md) (4), [jei.md](../jei.md) (3), [client-checklist.md](../client-checklist.md) (2)
<!-- auto:end card:cataclysm_spellbooks -->

Автор — Ace The Eldritch King и StarOcean (MIT). В сборке — наш порт `cataclysm_spellbooks-1.2.9-1.20.1-mmport.jar` (мод собран под другую версию Cataclysm; подробности порта — [compat.md](../compat.md), п. К4 и «Cataclysm: Spellbooks 1.2.9»). Требует [Iron's Spells 'n Spellbooks](magic.md#irons-spells-n-spellbooks), L_Ender's Cataclysm, AzureLib.

### Как играть

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

### Конфиги

Наши правки в `mods-src/mm-compat` (0.2.0): лучи `cataclysm_spellbooks:extended_laser_beam` и `extended_death_laser_beam` переведены из категории `MONSTER` в `MISC` (убирает 368 строк «has no attributes» в логе и занятие лимита враждебных мобов; `EntityCategoryFix`). Остальное — порт `-mmport` (классы частиц, ракеты, рендер Древнего остатка). Проверка — `scripts/tests/sbcastcheck.js` (10/10).

## CBC: Nukes

<!-- auto:start card:cbc_nukes -->
<a id="cbc_nukes"></a>
- **Версия:** 2.1.1 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/cbc_nukes-2.1.1-mmport.jar`
- **Что это:** Nukes innit? _(источник: mods.toml)_
<!-- auto:end card:cbc_nukes -->

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

## Fish's Undead Rising (1.20.1 port)

<!-- auto:start card:fishs-undead-rising-port -->
<a id="fishs-undead-rising-port"></a>
- **Версия:** 0.3.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/fishs-undead-rising-port-0.3.0.jar`
- **Что это:** Порт части Fish's Undead Rising (Fish, CurseForge 321139; 1.12.2/1.16.5, на 1.20.1 нет) — то, что нужно дополнению Necromancer's Delight. Ассеты и механики — © Fish. Некоммерческий порт; по требованию автора будет убран. _(источник: mods.toml)_
- **В сборке:** свой мод (both): pack/mods/fishs-undead-rising-port-*.jar из mods-src/fishs-undead-rising — частичный порт Fish's Undead Rising (Fish, ARR; по решению владельца, убрать по требованию автора) под ND
- **В наших docs:** [wizardry-port.md](../wizardry-port.md) (3), [compat.md](../compat.md) (2), [roadmap.md](../roadmap.md) (2)
<!-- auto:end card:fishs-undead-rising-port -->

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

## gaboulibs-shim

<!-- auto:start card:gaboulibs-shim -->
<a id="gaboulibs-shim"></a>
- **Версия:** 1.0.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/gaboulibs-shim-1.0.0.jar`
- **Что это:** Замена удалённой автором библиотеки GabouLibs для Serene Seasons Plus (patches/gaboulibs-shim). _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (3)
<!-- auto:end card:gaboulibs-shim -->

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

## Galacticraft Revival (private port)

<!-- auto:start card:galacticraft-revival -->
<a id="galacticraft-revival"></a>
- **Версия:** 0.1.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/galacticraft-revival-0.1.0.jar`
- **Что это:** Приключенческая часть Galacticraft поверх Ad Astra: данжи с боссами, ключи, сундуки сокровищ и схемы. Планеты, кислород и гравитация — Ad Astra / Northstar. _(источник: mods.toml)_
- **В сборке:** свой мод (both): pack/mods/galacticraft-revival-*.jar из mods-src/galacticraft-revival — порт GC: лунный данж с боссом, ключи, схемы (docs/galacticraft.md)
- **В наших docs:** [bosses.md](../bosses.md) (2), [compat.md](../compat.md) (2), [galacticraft.md](../galacticraft.md) (2), [client-checklist.md](../client-checklist.md) (1), [roadmap.md](../roadmap.md) (1)
<!-- auto:end card:galacticraft-revival -->

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

## lycanites-refmap-fix

<!-- auto:start card:lycanites-refmap-fix -->
<a id="lycanites-refmap-fix"></a>
- **Версия:** 1.0.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/lycanites-refmap-fix-1.0.0.jar`
- **Что это:** Недостающий refmap миксинов Lycanites Mobs — без пересборки 120-МБ jar (patches/lycanites-refmap). _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:lycanites-refmap-fix -->

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

## M&A Witchcraft

<!-- auto:start card:mnaw -->
<a id="mnaw"></a>
- **Версия:** 0.2.1-alpha · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/mnaw-1.20.1-forge-0.2.1-alpha-mmport.jar`
- **Что это:** Avoid the faction wars as a member of the Coven of Apostates. _(источник: mods.toml)_
<!-- auto:end card:mnaw -->

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

## Minecraft_moded Compat

<!-- auto:start card:mm-compat -->
<a id="mm-compat"></a>
- **Версия:** 0.5.6 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/mm-compat-0.5.6.jar`
- **Что это:** Связки и исправления модов сборки Minecraft_moded, которые нельзя сделать данными: кислород скафандров Northstar и Creating Space в мирах Ad Astra, категория лучей Cataclysm Spellbooks, недостающий атрибут рас Arknights, фриз сервера от Mana and Artifice (блокирующая загрузка чанков), утечка кучи T… _(источник: mods.toml)_
<!-- auto:end card:mm-compat -->

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

## Minecraft_moded Perf

<!-- auto:start card:mm-perf -->
<a id="mm-perf"></a>
- **Версия:** 0.3.0 · **Сторона:** client · **Источник:** jar в репозитории: `pack/mods/mm-perf-0.3.0-client.jar`
- **Что это:** Клиентские удобства сборки Minecraft_moded (docs/performance.md): Режим слабого ПК, клавиши модов «Не назначено», подсказки о памяти, ускорение JEI при входе в мир, версия сборки в главном меню и заголовке окна. _(источник: mods.toml)_
<!-- auto:end card:mm-perf -->

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

## Origin Furs Forge

<!-- auto:start card:origin_visuals -->
<a id="origin_visuals"></a>
- **Версия:** 1.0.5 · **Сторона:** client · **Источник:** jar в репозитории: `pack/mods/origin_visuals-1.0.5-mmport-client.jar`
- **Что это:** свой порт (client): pack/mods/origin_visuals-1.0.5-mmport-client.jar — Origin Furs (зависимость Arknights Furs) под
<!-- auto:end card:origin_visuals -->

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

## Russian Lang (Minecraft_moded)

<!-- auto:start card:ru-lang -->
<a id="ru-lang"></a>
- **Версия:** 1.0.0 · **Сторона:** client · **Источник:** jar в репозитории: `pack/mods/ru-lang-1.0.0-client.jar`
- **Что это:** Подключает русский ресурспак сборки (недостающие переводы, названия модов остаются английскими) как всегда включённый и самый верхний — не зависит от options.txt игрока. Только клиент. _(источник: mods.toml)_
- **В сборке:** наш клиентский мод (mods-src/ru-lang): всегда включённый верхний ресурспак russian-moded; пересобирать scripts/build-ru-pack.sh после правок переводов
<!-- auto:end card:ru-lang -->

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

## T.O Magic 'n Extras

<!-- auto:start card:traveloptics -->
<a id="traveloptics"></a>
- **Версия:** 6.3.0-1.20.1 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/traveloptics-6.3.0-1.20.1-mmport.jar`
- **Что это:** T.O Magic 'n Extras contains custom & immersive never before seen spells, weapons, armors, unique curios, spellbooks, staves, skilltree gems... You name it, you have it!! _(источник: mods.toml)_
- **В наших docs:** [compat.md](../compat.md) (7), [roadmap.md](../roadmap.md) (4)
<!-- auto:end card:traveloptics -->

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

## Voice Chat Interaction (Forge port)

<!-- auto:start card:voicechat-addons-forge -->
<a id="voicechat-addons-forge"></a>
- **Версия:** 1.20.1-1.0.6-forge-1.0.1 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/voicechat-addons-forge-1.0.1.jar`
- **Что это:** Голос из Simple Voice Chat — вибрация: скалк-сенсоры и Хранитель его слышат. Оригинал: https://github.com/henkelmax/voicechat-interaction (Fabric). _(источник: mods.toml)_
- **В сборке:** mods-src/voicechat-addons → pack/mods/voicechat-addons-forge-*.jar (both)
- **В наших docs:** [compat.md](../compat.md) (5), [roadmap.md](../roadmap.md) (3)
<!-- auto:end card:voicechat-addons-forge -->

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

## Wizardry - Necromancer's Delight (1.20.1 port)

<!-- auto:start card:necromancers-delight -->
<a id="necromancers-delight"></a>
- **Версия:** 0.2.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/necromancers-delight-0.2.0.jar`
- **Что это:** Порт Wizardry - Necromancer's Delight 1.12.2 (https://github.com/WinDanesz/WizardryNecromancersDelight, GPL-3.0) на 1.20.1 Forge поверх Electroblob's Wizardry Redux и нашего порта Fish's Undead Rising (mod_lavacow). Код и ассеты оригинала — © WinDanesz, GPL-3.0; мобы и их ассеты — © Fish. _(источник: mods.toml)_
- **В сборке:** свой мод (both): pack/mods/necromancers-delight-*.jar из mods-src/necromancers-delight — порт Necromancer's Delight 1.12.2 (WinDanesz, GPL-3.0), docs/wizardry-port.md
- **В наших docs:** [wizardry-port.md](../wizardry-port.md) (28), [roadmap.md](../roadmap.md) (21), [compat.md](../compat.md) (14), [client-checklist.md](../client-checklist.md) (4), [jei.md](../jei.md) (4), [russification.md](../russification.md) (3)
<!-- auto:end card:necromancers-delight -->

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

## Wizardry Golems (1.20.1 port)

<!-- auto:start card:wizardry-golems -->
<a id="wizardry-golems"></a>
- **Версия:** 0.2.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/wizardry-golems-0.2.0.jar`
- **Что это:** Порт Wizardry Golems 1.12.2 (https://github.com/WinDanesz/WizardryGolems, GPL-3.0) на 1.20.1 Forge поверх Electroblob's Wizardry Redux и Extra Golems 20.1. Код и ассеты оригинала — © WinDanesz, GPL-3.0. _(источник: mods.toml)_
- **В сборке:** свой мод (both): pack/mods/wizardry-golems-*.jar из mods-src/wizardry-golems — порт Wizardry Golems 1.12.2 (WinDanesz, GPL-3.0), docs/wizardry-port.md
- **В наших docs:** [wizardry-port.md](../wizardry-port.md) (5), [roadmap.md](../roadmap.md) (3), [compat.md](../compat.md) (2)
<!-- auto:end card:wizardry-golems -->

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

## Wizardry Restored

<!-- auto:start card:wizardry-restored -->
<a id="wizardry-restored"></a>
- **Версия:** 0.6.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/wizardry-restored-0.6.0.jar`
- **Что это:** Заклинания Electroblob's Wizardry, которых пока нет в Electroblob's Wizardry Redux. Новый код (не копия оригинала). _(источник: mods.toml)_
- **В наших docs:** [roadmap.md](../roadmap.md) (3), [client-checklist.md](../client-checklist.md) (2), [compat.md](../compat.md) (2), [jei.md](../jei.md) (1), [russification.md](../russification.md) (1), [wizardry-port.md](../wizardry-port.md) (1)
<!-- auto:end card:wizardry-restored -->

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

## Zombie Have Ears

<!-- auto:start card:zombiehaveears -->
<a id="zombiehaveears"></a>
- **Версия:** 1.1.2 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/zombiehaveears-1.1.2-forgefix.jar`
- **Что это:** [Пересобрано для Minecraft_moded: mods.toml в формате Forge 47, код без изменений.] Zombies hear players talking in Simple Voice Chat and come to investigate the sound. Louder voices are heard from farther away; whispering is much safer. Server-side only: clients only need Simple Voice Chat itself. _(источник: mods.toml)_
- **В сборке:** patches/zombie-have-ears/build.sh → pack/mods/zombiehaveears-1.1.2-forgefix.jar
- **В наших docs:** [compat.md](../compat.md) (3), [roadmap.md](../roadmap.md) (2)
<!-- auto:end card:zombiehaveears -->

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

## 方舟武器

<!-- auto:start card:arknights_weapon -->
<a id="arknights_weapon"></a>
- **Версия:** 1.5.6 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/arknights_weapon-1.5.6-mmport.jar`
- **Что это:** Оружие Arknights (порт оригинала с CurseForge): убран RiseProcedure, удваивавший здоровье мобов при каждой загрузке чанка. _(источник: docs сборки)_
<!-- auto:end card:arknights_weapon -->

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
