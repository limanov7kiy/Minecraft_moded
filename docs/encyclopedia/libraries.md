# Библиотеки и инструменты

Модули, нужные другим модам, а также KubeJS и Almost Unified.

<!-- auto:start toc -->
Модов в категории: **52**.

- [AeroBlender](#aeroblender)
- [Almanac](#almanac)
- [Almost Unified](#almost-unified)
- [Apothic Attributes](#apothic-attributes)
- [Architectury API](#architectury-api)
- [AttributeFix](#attributefix)
- [AzureLib](#azurelib)
- [BaguetteLib](#baguettelib)
- [Balm](#balm)
- [Botarium](#botarium)
- [Caelus API](#caelus-api)
- [CERBON's API](#cerbons-api)
- [Citadel](#citadel)
- [Cloth Config API](#cloth-config-api)
- [Cupboard utilities](#cupboard-utilities)
- [Curios API](#curios-api)
- [Event Wrapper](#event-wrapper)
- [FDLib](#fdlib)
- [Geckolib](#geckolib)
- [GlitchCore](#glitchcore)
- [GTBC's SpellLib/API](#gtbcs-spelllibapi)
- [GuideME](#guideme)
- [Integrated API](#integrated-api)
- [Integrated Patches](#integrated-patches)
- [Iron's Lib](#irons-lib)
- [Kotlin for Forge](#kotlin-for-forge)
- [KubeJS](#kubejs)
- [LDLib](#ldlib)
- [Lexiconfig](#lexiconfig)
- [LibX](#libx)
- [Lionfish-API](#lionfish-api)
- [Lootintegrations mod](#lootintegrations-mod)
- [lootintegrations_cataclysm mod](#lootintegrations_cataclysm-mod)
- [MezzConfig](#mezzconfig)
- [Modonomicon](#modonomicon)
- [Moonlight Lib](#moonlight-lib)
- [Patchouli](#patchouli)
- [Photon Editor](#photon-editor)
- [Placebo](#placebo)
- [playerAnimator](#playeranimator)
- [Pufferfish's Attributes](#pufferfishs-attributes)
- [Resourceful Config](#resourceful-config)
- [Resourceful Lib](#resourceful-lib)
- [Rhino](#rhino)
- [Ritchie's Projectile Library](#ritchies-projectile-library)
- [Searchables](#searchables)
- [SmartBrainLib](#smartbrainlib)
- [Sophisticated Core](#sophisticated-core)
- [Structure Gel API](#structure-gel-api)
- [TerraBlender](#terrablender)
- [YetAnotherConfigLib (YACL)](#yetanotherconfiglib-yacl)
- [Zeta](#zeta)

Вернуться: [оглавление справочника](INDEX.md).
<!-- auto:end toc -->

## Общее по категории

_Обзор категории — вручную, следующие этапы._

---

<!-- cards:start -->
## AeroBlender

<!-- auto:start card:aeroblender -->
<a id="aeroblender"></a>
- **Версия:** 1.20.1-1.0.1-neoforge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/aeroblender)
- **Что это:** Библиотека на основе TerraBlender: добавляет биомы в совместимости с системой рельефа и биомов Aether. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:aeroblender -->

### Что даёт игроку

Совместимость добавления биомов с рельефом Эфира (Aether); игроку ничего напрямую.

## Almanac

<!-- auto:start card:almanac -->
<a id="almanac"></a>
- **Версия:** 1.0.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/almanac)
- **Что это:** Общая библиотека кода для модов того же автора. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:almanac -->

### Что даёт игроку

Ничего напрямую — общий код модов одного автора.

## Almost Unified

<!-- auto:start card:almostunified -->
<a id="almostunified"></a>
- **Версия:** 1.20.1-0.11.0+forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/almostunified)
- **Что это:** Объединяет дубли ресурсов разных модов в один (руды, слитки и т. д.). _(источник: docs сборки)_
- **В сборке:** объединение дублей: один предмет на ресурс (config/almostunified)
- **В наших docs:** [compat.md](../compat.md) (13), [roadmap.md](../roadmap.md) (5), [orespawn.md](../orespawn.md) (2), [enchanting-table.md](../enchanting-table.md) (1), [jei.md](../jei.md) (1)
<!-- auto:end card:almostunified -->

### Что даёт игроку

Игроку видно только результат: одинаковые руды, слитки и пыль разных модов сводятся к одному предмету. Настройка — `pack/config/almostunified/`; перед правками смотрите `scripts/hardrefs.py` и [compat.md](../compat.md).

## Apothic Attributes

<!-- auto:start card:apothic-attributes -->
<a id="apothic-attributes"></a>
- **Версия:** 1.3.7 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/apothic-attributes)
- **Что это:** Библиотека: окно атрибутов и набор новых атрибутов. _(источник: docs сборки)_
- **В сборке:** обязательная зависимость T.O Magic 'n Extras (+ Placebo)
- **В наших docs:** [compat.md](../compat.md) (3), [roadmap.md](../roadmap.md) (3), [enchanting-table.md](../enchanting-table.md) (2)
<!-- auto:end card:apothic-attributes -->

### Что даёт игроку

Добавляет окно атрибутов в инвентарь (характеристики персонажа) и новые атрибуты, которыми пользуются другие моды.

## Architectury API

<!-- auto:start card:architectury-api -->
<a id="architectury-api"></a>
- **Версия:** 9.2.14+forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/architectury-api)
- **Что это:** Промежуточный API для облегчения разработки модов под несколько загрузчиков. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:architectury-api -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## AttributeFix

<!-- auto:start card:attributefix -->
<a id="attributefix"></a>
- **Версия:** 21.0.5 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/attributefix)
- **Что это:** Снимает произвольные ограничения на атрибуты Minecraft; чинит многие моды. _(источник: docs сборки)_
- **В сборке:** both: AttributeFix (Darkhax, LGPL-2.1) — снимает потолок здоровья 1024: без него King (7000), Queen (6000), Mobzilla (4000) у Chaos Persists урезаны до 1024 (его собственный обход падает: «failed to raise generic.maxHealth cap»)
- **В наших docs:** [orespawn.md](../orespawn.md) (16), [compat.md](../compat.md) (8), [roadmap.md](../roadmap.md) (4)
<!-- auto:end card:attributefix -->

### Что даёт игроку

Снимает потолок значений атрибутов (здоровье, броня и др.), чтобы работали моды с сильными бонусами. Настройка — в `pack/config/`.

## AzureLib

<!-- auto:start card:azurelib -->
<a id="azurelib"></a>
- **Версия:** 3.1.20 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/azurelib)
- **Что это:** Библиотека: модели и анимации Bedrock для предметов, блоков и существ. _(источник: docs сборки)_
- **В сборке:** обязательная зависимость Cataclysm: Spellbooks (раньше подтягивалась packwiz'ом)
- **В наших docs:** [compat.md](../compat.md) (44), [roadmap.md](../roadmap.md) (14), [client-checklist.md](../client-checklist.md) (6), [enchanting-table.md](../enchanting-table.md) (2), [performance.md](../performance.md) (2)
<!-- auto:end card:azurelib -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## BaguetteLib

<!-- auto:start card:baguettelib -->
<a id="baguettelib"></a>
- **Версия:** 2.0.7 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/baguettelib)
- **Что это:** Библиотека: корректная обработка смерти и учёта инвентаря для модов. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
- **В наших docs:** [compat.md](../compat.md) (6), [issue-history.md](../issue-history.md) (2), [roadmap.md](../roadmap.md) (2)
<!-- auto:end card:baguettelib -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Balm

<!-- auto:start card:balm -->
<a id="balm"></a>
- **Версия:** 7.3.44+forge-1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/balm)
- **Что это:** Слой абстракции для модов под несколько загрузчиков. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:balm -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Botarium

<!-- auto:start card:botarium -->
<a id="botarium"></a>
- **Версия:** 2.3.4 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/botarium)
- **Что это:** Кроссплатформенный API: передача и хранение предметов, жидкостей и энергии. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:botarium -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Caelus API

<!-- auto:start card:caelus -->
<a id="caelus"></a>
- **Версия:** 3.2.0+1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/caelus)
- **Что это:** Коремод и API: полёт на элитрах через атрибут существа. _(источник: docs сборки)_
- **В сборке:** нужна Origins (apoli)
<!-- auto:end card:caelus -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## CERBON's API

<!-- auto:start card:cerbons-api -->
<a id="cerbons-api"></a>
- **Версия:** 1.1.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/cerbons-api)
- **Что это:** Библиотека для модов CERBON. _(источник: docs сборки)_
- **В сборке:** both (решение владельца В12 «а»: вернуть ради BoMD; поток 10 убирал из-за миксинов коллизий — регрессионный тест spawncheck.js)
- **В наших docs:** [compat.md](../compat.md) (13), [roadmap.md](../roadmap.md) (4), [jei.md](../jei.md) (2)
<!-- auto:end card:cerbons-api -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Citadel

<!-- auto:start card:citadel -->
<a id="citadel"></a>
- **Версия:** 2.6.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/citadel)
- **Что это:** Лёгкая библиотека (нужна Alex's Mobs, Ice and Fire и др.). _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:citadel -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Cloth Config API

<!-- auto:start card:cloth-config -->
<a id="cloth-config"></a>
- **Версия:** 11.1.136+forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/cloth-config)
- **Что это:** Библиотека экранов настройки модов. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:cloth-config -->

### Что даёт игроку

Экраны настройки модов в игре. Ничего другого игроку не даёт.

## Cupboard utilities

<!-- auto:start card:cupboard -->
<a id="cupboard"></a>
- **Версия:** 1.20.1-4.2 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/cupboard-1.20.1-4.2.jar`
- **Что это:** Библиотека утилит для других модов. _(источник: docs сборки)_
- **В сборке:** нужна Loot Integrations (>=1.20.1-1.5)
<!-- auto:end card:cupboard -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Curios API

<!-- auto:start card:curios -->
<a id="curios"></a>
- **Версия:** 5.14.1+1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/curios)
- **Что это:** Универсальный API слотов аксессуаров и экипировки. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:curios -->

### Что даёт игроку

Даёт слоты аксессуаров (кольца, амулеты, пояса и т. п.) в окне инвентаря; сами предметы приносят другие моды.

## Event Wrapper

<!-- auto:start card:event-wrapper -->
<a id="event-wrapper"></a>
- **Версия:** 1.20.1-1.1.4-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/event-wrapper)
- **Что это:** Обёртка для шины событий под Forge и Fabric. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:event-wrapper -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## FDLib

<!-- auto:start card:fdlib -->
<a id="fdlib"></a>
- **Версия:** 1.0.9 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/fdlib)
- **Что это:** Набор утилит для модов одного автора. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
- **В наших docs:** [compat.md](../compat.md) (1), [russification-audit.md](../russification-audit.md) (1), [russification.md](../russification.md) (1)
<!-- auto:end card:fdlib -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Geckolib

<!-- auto:start card:geckolib -->
<a id="geckolib"></a>
- **Версия:** 4.8.4 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/geckolib)
- **Что это:** Библиотека 3D-анимаций для существ, блоков, предметов, брони. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
- **В наших docs:** [compat.md](../compat.md) (12), [client-checklist.md](../client-checklist.md) (2), [russification-audit.md](../russification-audit.md) (2)
<!-- auto:end card:geckolib -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## GlitchCore

<!-- auto:start card:glitchcore -->
<a id="glitchcore"></a>
- **Версия:** 0.0.1.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/glitchcore)
- **Что это:** Библиотека: абстракция загрузчиков и общие утилиты модов-авторов Serene Seasons. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:glitchcore -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## GTBC's SpellLib/API

<!-- auto:start card:gtbcs-spelllib -->
<a id="gtbcs-spelllib"></a>
- **Версия:** 2.2.0-1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/gtbcs-spelllib)
- **Что это:** Библиотека и общий код для аддонов Iron's Spells 'n Spellbooks одного автора. _(источник: docs сборки)_
- **В сборке:** библиотека для GTBC Geomancy Plus (было CurseForge, теперь Modrinth)
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:gtbcs-spelllib -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## GuideME

<!-- auto:start card:guideme -->
<a id="guideme"></a>
- **Версия:** 20.1.15 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/guideme)
- **Что это:** Набор для внутриигровых справочников: разметка markdown и живые 3D-сцены. _(источник: docs сборки)_
- **В сборке:** нужна Applied Energistics 2 (>=20.1.7, <20.2)
- **В наших docs:** [compat.md](../compat.md) (1), [russification-audit.md](../russification-audit.md) (1)
<!-- auto:end card:guideme -->

### Что даёт игроку

Движок внутриигровых справочников с 3D-сценами (нужен AE2 и др.); сам ничего не добавляет.

## Iron's Lib

<!-- auto:start card:irons-lib -->
<a id="irons-lib"></a>
- **Версия:** 1.20.1-2.2.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/irons-lib)
- **Что это:** Общая функциональность и контент для модов Iron's. _(источник: docs сборки)_
- **В сборке:** Iron's Spells: >=1.20.1-2, <1.20.1-3
<!-- auto:end card:irons-lib -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Kotlin for Forge

<!-- auto:start card:kotlin-for-forge -->
<a id="kotlin-for-forge"></a>
- **Версия:** 4.12.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/kotlin-for-forge)
- **Что это:** Загрузчик языка Kotlin и вспомогательные утилиты. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:kotlin-for-forge -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## KubeJS

<!-- auto:start card:kubejs -->
<a id="kubejs"></a>
- **Версия:** 2001.6.5-build.26+forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/kubejs)
- **Что это:** Скрипты на JavaScript: правка рецептов, новые предметы, события мира. _(источник: docs сборки)_
- **В сборке:** патчи данных/рецептов/добычи: pack/kubejs/
- **В наших docs:** [compat.md](../compat.md) (44), [roadmap.md](../roadmap.md) (16), [russification.md](../russification.md) (8), [orespawn.md](../orespawn.md) (6), [wizardry-port.md](../wizardry-port.md) (5), [enchanting-table.md](../enchanting-table.md) (4)
<!-- auto:end card:kubejs -->

### Что даёт игроку

Скриптовый движок сборки: наши правки рецептов, тегов и событий лежат в `pack/kubejs/`. Игрок видит только результат (изменённые рецепты, фиксы тегов).

## LDLib

<!-- auto:start card:ldlib -->
<a id="ldlib"></a>
- **Версия:** mc1.20.1-1.0.52.a-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/ldlib)
- **Что это:** Библиотека: свой рендер и модульный GUI. _(источник: docs сборки)_
- **В сборке:** нужна Originium Arts
- **В наших docs:** [russification.md](../russification.md) (2), [compat.md](../compat.md) (1), [jei.md](../jei.md) (1), [russification-audit.md](../russification-audit.md) (1)
<!-- auto:end card:ldlib -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Lexiconfig

<!-- auto:start card:lexiconfig -->
<a id="lexiconfig"></a>
- **Версия:** 1.4.21-epic · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/lexiconfig)
- **Что это:** API конфигов с интеграциями с другими библиотеками. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
- **В наших docs:** [compat.md](../compat.md) (6)
<!-- auto:end card:lexiconfig -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## LibX

<!-- auto:start card:libx -->
<a id="libx"></a>
- **Версия:** 1.20.1-5.0.14 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/libx)
- **Что это:** Библиотека с общим кодом для модов. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
- **В наших docs:** [russification-audit.md](../russification-audit.md) (1)
<!-- auto:end card:libx -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Lionfish-API

<!-- auto:start card:lionfish-api -->
<a id="lionfish-api"></a>
- **Версия:** 3.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/lionfish-api)
- **Что это:** Очень лёгкий API анимаций. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:lionfish-api -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## MezzConfig

<!-- auto:start card:mezzconfig -->
<a id="mezzconfig"></a>
- **Версия:** 0.6.8 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/mezzconfig)
- **Что это:** Простая библиотека конфигов для модов. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:mezzconfig -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Modonomicon

<!-- auto:start card:modonomicon -->
<a id="modonomicon"></a>
- **Версия:** 1.20.1-1.79.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/modonomicon)
- **Что это:** Внутриигровая документация в духе Thaumonomicon и Patchouli. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
- **В наших docs:** [russification.md](../russification.md) (10), [russification-audit.md](../russification-audit.md) (6), [jei.md](../jei.md) (2)
<!-- auto:end card:modonomicon -->

### Что даёт игроку

Движок внутриигровых книг (используется Mana and Artifice, Occultism и др.): книга открывается как предмет, сам мод ничего не добавляет.

## Moonlight Lib

<!-- auto:start card:moonlight -->
<a id="moonlight"></a>
- **Версия:** 1.20-2.16.36-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/moonlight)
- **Что это:** Библиотека: динамические датапаки и регистрация, занятия жителей, свои маркеры на карте и др. _(источник: docs сборки)_
- **В сборке:** нужна Supplementaries и Amendments
<!-- auto:end card:moonlight -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Patchouli

<!-- auto:start card:patchouli -->
<a id="patchouli"></a>
- **Версия:** 1.20.1-85-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/patchouli)
- **Что это:** Библиотека внутриигровых книг-справочников на данных (JSON), без зависимостей. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:patchouli -->

### Что даёт игроку

Книги-справочники других модов (например, гайды Botania, Ars Nouveau, Alex's Mobs Interaction) открываются как предметы; сам по себе ничего не добавляет.

## Photon Editor

<!-- auto:start card:photon-editor -->
<a id="photon-editor"></a>
- **Версия:** mc1.20.1-1.1.17-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/photon-editor)
- **Что это:** Библиотека и редактор эффектов (частиц) в духе Unity. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:photon-editor -->

### Что даёт игроку

Нужна для визуальных эффектов (частицы) некоторых модов; игроку ничего напрямую.

## Placebo

<!-- auto:start card:placebo -->
<a id="placebo"></a>
- **Версия:** 8.6.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/placebo)
- **Что это:** Библиотека модов Shadows; сама почти ничего не даёт, максимум пара отладочных команд. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:placebo -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## playerAnimator

<!-- auto:start card:playeranimator -->
<a id="playeranimator"></a>
- **Версия:** 1.0.2-rc1+1.20-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/playeranimator)
- **Что это:** Библиотека анимации игрока. _(источник: docs сборки)_
- **В сборке:** нужна Iron's Spells
- **В наших docs:** [compat.md](../compat.md) (2)
<!-- auto:end card:playeranimator -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Pufferfish's Attributes

<!-- auto:start card:attributes -->
<a id="attributes"></a>
- **Версия:** 0.8.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/attributes)
- **Что это:** Добавляет в игру дополнительные атрибуты. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:attributes -->

### Что даёт игроку

Библиотека атрибутов Pufferfish (используется Pufferfish's Skills); игроку ничего напрямую.

## Resourceful Config

<!-- auto:start card:resourceful-config -->
<a id="resourceful-config"></a>
- **Версия:** 2.1.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/resourceful-config)
- **Что это:** Кроссплатформенная библиотека конфигов. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:resourceful-config -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Resourceful Lib

<!-- auto:start card:resourceful-lib -->
<a id="resourceful-lib"></a>
- **Версия:** 2.1.29 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/resourceful-lib)
- **Что это:** Библиотека Resourceful. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:resourceful-lib -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Rhino

<!-- auto:start card:rhino -->
<a id="rhino"></a>
- **Версия:** 2001.2.3-build.10+forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/rhino)
- **Что это:** Форк Mozilla Rhino (JavaScript) для модов; нужен KubeJS. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:rhino -->

### Что даёт игроку

Движок JavaScript для KubeJS. Игроку ничего напрямую.

## Ritchie's Projectile Library

<!-- auto:start card:rpl -->
<a id="rpl"></a>
- **Версия:** 2.1.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/rpl)
- **Что это:** Библиотека улучшенных снарядов. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:rpl -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Searchables

<!-- auto:start card:searchables -->
<a id="searchables"></a>
- **Версия:** 1.0.3 · **Сторона:** client · **Источник:** [Modrinth](https://modrinth.com/mod/searchables)
- **Что это:** Библиотека поиска и фильтрации элементов с автодополнением. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
- **В наших docs:** [compat.md](../compat.md) (2), [roadmap.md](../roadmap.md) (2)
<!-- auto:end card:searchables -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## SmartBrainLib

<!-- auto:start card:smartbrainlib -->
<a id="smartbrainlib"></a>
- **Версия:** 1.15 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/smartbrainlib)
- **Что это:** Библиотека, упрощающая систему «мозгов» существ. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:smartbrainlib -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Sophisticated Core

<!-- auto:start card:sophisticated-core -->
<a id="sophisticated-core"></a>
- **Версия:** 1.20.1-1.5.6.2383 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/sophisticated-core)
- **Что это:** Общая библиотека модов Sophisticated. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:sophisticated-core -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Structure Gel API

<!-- auto:start card:structure-gel-api -->
<a id="structure-gel-api"></a>
- **Версия:** 2.16.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/structure-gel-api)
- **Что это:** API для упрощённого создания генерируемых структур. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:structure-gel-api -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## TerraBlender

<!-- auto:start card:terrablender -->
<a id="terrablender"></a>
- **Версия:** 3.0.1.11 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/terrablender)
- **Что это:** Библиотека добавления биомов с поддержкой совместимости. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:terrablender -->

### Что даёт игроку

Общая система биомов Верхнего мира: нужна, чтобы биомы разных модов вместе уживались в одном мире.

## YetAnotherConfigLib (YACL)

<!-- auto:start card:yacl -->
<a id="yacl"></a>
- **Версия:** 3.6.6+1.20.1-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/yacl)
- **Что это:** Библиотека конфигов с построителем интерфейса. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:yacl -->

### Что даёт игроку

Экраны настройки модов в игре (кнопка «Настройки» у мода в списке). Ничего другого игроку не даёт.

## Zeta

<!-- auto:start card:zeta -->
<a id="zeta"></a>
- **Версия:** 1.20.1-1.0-31 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/zeta)
- **Что это:** Общая несущая библиотека модульных модов (Quark и др.). _(источник: docs сборки)_
- **В сборке:** нужна Quark (>=1.0-28, <2.0)
<!-- auto:end card:zeta -->

### Что даёт игроку

Ничего напрямую — библиотека, которая нужна другим модам сборки.

## Integrated API

<!-- auto:start card:integrated-api -->
<a id="integrated-api"></a>
- **Версия:** 1.8.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/integrated-api)
- **Что это:** Библиотека серии Integrated Structures (для генерации структур). _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
- **В наших docs:** [compat.md](../compat.md) (1), [roadmap.md](../roadmap.md) (1)
<!-- auto:end card:integrated-api -->

### Что даёт игроку

Библиотека серии Integrated Structures: общий код (события торговцев, совместимость с Async Locator, обновление NBT структур) для дата-паков «Integrated …» — в сборке они переделывают структуры других модов. Сама по себе игроку ничего не добавляет.

## Integrated Patches

<!-- auto:start card:integrated-patches -->
<a id="integrated-patches"></a>
- **Версия:** 1.2.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/integrated-patches)
- **Что это:** Лёгкий фикс проблем генерации мира в Integrated API. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:integrated-patches -->

### Что даёт игроку

Лёгкое исправление генерации мира для Integrated API (Integrated Patches): миксины `FixedJigsawManager`, `PieceLimitedJigsawManager`, `StructureStart`, `ChunkGenerator`, `EnhancedTerrainAdaptation` и очистка висящих лоз (`ProtectedVines`, `UnsupportedVineCleaner`) — лечат ошибки генерации интегрированных структур. Игровых предметов нет.

## Lootintegrations mod

<!-- auto:start card:lootintegrations -->
<a id="lootintegrations"></a>
- **Версия:** 1.20.1-4.7 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/lootintegrations-1.20.1-4.7.jar`
- **Что это:** Подмешивает предметы одних таблиц добычи в другие — совместимость лута разных модов. _(источник: docs сборки)_
<!-- auto:end card:lootintegrations -->

### Что даёт игроку

Библиотека-связка лута (мод `lootintegrations`): через миксины и глобальные лут-модификаторы подмешивает предметы из таблиц лута других модов в ванильные сундуки (`minecraft:chests/ancient_city`, пирамиды, крепости, шахты, лесные особняки, города Края и др.) по уровням сложности `easy`/`hard` (`lootintegrations:chests/hard`, лимит `max_result_itemcount`). Игроку даёт больше разнообразного лута в ванильных структурах; команд нет.

## lootintegrations_cataclysm mod

<!-- auto:start card:lootintegrations_cataclysm -->
<a id="lootintegrations_cataclysm"></a>
- **Версия:** 1 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/lootintegrations_cataclysm-1.2.jar`
- **Что это:** Часть Loot Integrations: лут Cataclysm. _(источник: docs сборки)_
<!-- auto:end card:lootintegrations_cataclysm -->

### Что даёт игроку

Расширение `lootintegrations` для Cataclysm: таблицы `loot_cataclysm_easy`, `loot_cataclysm_medium`, `loot_cataclysm_hard`, `loot_cataclysm_nether`, `loot_cataclysm_water` — добавляют вещи Cataclysm в сундуки ванильных структур по уровням сложности. Требует `lootintegrations`; своих предметов нет.

<!-- cards:end -->
