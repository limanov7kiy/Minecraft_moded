# Мир: выживание, погода и катастрофы

Температура и жажда, сезоны, штормы, торнадо, метеориты и обереги от них.

<!-- auto:start toc -->
Модов в категории: **9**.

- [Better Days](#better-days)
- [CoroUtil](#coroutil)
- [Legendary Survival Addon](#legendary-survival-addon)
- [Legendary Survival Overhaul](#legendary-survival-overhaul)
- [Natural Disasters!](#natural-disasters)
- [Serene Seasons](#serene-seasons)
- [Serene Seasons Plus](#serene-seasons-plus)
- [Weather Storms & Tornadoes](#weather-storms--tornadoes)
- [Оберег от катаклизмов (Disaster Ward)](#оберег-от-катаклизмов-disaster-ward)

Вернуться: [оглавление справочника](INDEX.md).
<!-- auto:end toc -->

## Общее по категории

Сезоны, погода, выживание и катаклизмы. **Serene Seasons** (+ Plus) задаёт сезоны, **Legendary Survival Overhaul** превращает их в температуру,
жажду и ранения, **Weather2** (+ CoroUtil) и **Natural Disasters!** добавляют бури, торнадо и бедствия, а наш мод **Оберег от катаклизмов**
([статья](own/disaster-ward.md)) создаёт защищённую зону и сам управляет частотой бедствий. Better Days меняет длину суток.
Тонкие места и замеры нагрузки — [compat.md](../compat.md), конфиги — `pack/config/Weather2/`, `sereneseasonsplus-common.toml`, `disaster_ward-common.toml`.

---

<!-- cards:start -->
## Better Days

<!-- auto:start card:betterdays -->
<a id="betterdays"></a>
- **Версия:** 1.20.1-3.3.4.5-FORGE · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/betterdays)
- **Что это:** Настройка длины суток и ускорение времени при сне. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
- **В наших docs:** [russification-audit.md](../russification-audit.md) (1)
<!-- auto:end card:betterdays -->

### Как играть

Настраивает длину дня и ночи и ускоряет время при сне. Мод стоит на обеих сторонах (на клиенте — индикатор сна). Общий конфиг мода в сборку не положен — действуют умолчания мода; в нём (по именам параметров в классе) есть `speedMethod`, `daySpeed`/`nightSpeed` (или `daySpeedMinutes`/`nightSpeedMinutes`), `dayStart`/`nightStart`, сглаживание `enableInterpolatedTime`, эффекты времени (`weatherEffect`, `randomTickEffect`, `potionEffect`, `hungerEffect`, `blockEntityEffect`) и сон (`enableSleepFeature`, `sleepSpeedMin`/`sleepSpeedMax`/`sleepSpeedAll`, `clearWeatherOnWake`).
- Длину суток в сборке **подхватывает Serene Seasons Plus**: см. карточку ниже, его секция `[dayNightCycle]` (`allowSeasonalControlOfBetterDaysTimeSpeed = true`).

### Конфиги

Нашего конфига нет (в `pack/config` файла `betterdays*` нет — умолчания). Связка со временем сезонов — [Serene Seasons Plus](#serene-seasons-plus).

## CoroUtil

<!-- auto:start card:coroutil -->
<a id="coroutil"></a>
- **Версия:** 1.20.1-1.3.7 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/coroutil)
- **Что это:** Общая библиотека модов Corosus. _(источник: docs сборки)_
- **В сборке:** библиотека Weather2
- **В наших docs:** [compat.md](../compat.md) (8), [roadmap.md](../roadmap.md) (4), [issue-history.md](../issue-history.md) (2)
<!-- auto:end card:coroutil -->

### Что даёт игроку

Библиотека Corosus: нужна Weather2 (штормы и торнадо, карточка ниже). Своих предметов нет; в игре даёт только команды-обёртки конфига (классы `CommandCoroConfig`, серверный и клиентский). Подробности по связке — [compat.md](../compat.md) (раздел «Катаклизмы: Weather2 + CoroUtil»).

## Legendary Survival Addon

<!-- auto:start card:legendary-survival-addon -->
<a id="legendary-survival-addon"></a>
- **Версия:** Forge-1.3.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/legendary-survival-addon)
- **Что это:** Новые способы охлаждения и нагрева с Create. _(источник: docs сборки)_
- **В сборке:** Create × Legendary Survival Overhaul
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:legendary-survival-addon -->

### Что даёт игроку

Мост **Create × Legendary Survival Overhaul**: блоки Create, которые греют и охлаждают игрока на большом расстоянии (`legendarysurvivaladdon:heater` «Нагреватель» — тепло в радиусе 30 блоков, `cooler` «Охладитель» — холод в радиусе 30, и `fan` «Охлаждающий вентилятор»). Требуют вращения (сообщения `insufficient_power`, `overheating`), есть клавиши «Включить/выключить охлаждение/нагрев». Креатив-вкладка «LSO Addon».

## Legendary Survival Overhaul

<!-- auto:start card:legendary-survival-overhaul -->
<a id="legendary-survival-overhaul"></a>
- **Версия:** 1.20.1-2.4.7 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/legendary-survival-overhaul)
- **Что это:** Температура, жажда, повреждения конечностей и переработка здоровья для выживания. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (4), [dedup.md](../dedup.md) (1), [performance.md](../performance.md) (1), [roadmap.md](../roadmap.md) (1), [version-choice.md](../version-choice.md) (1)
<!-- auto:end card:legendary-survival-overhaul -->

### Как играть

Модуль «реализма»: температура, жажда и ранения конечностей. Всё это видно в HUD.
1. **Температура.** Мир, биом, время суток, высота, огонь и погода греют или охлаждают вас; жара вызывает `heat_stroke` (тепловой удар) и `heat_thirst`, холод — `frostbite` (обморожение) и `cold_hunger`. Одежда: «Снежная» (`snow_*`) и «Пустынная» (`desert_*`) броня, покрытия (`heating_coat_1..3`, `cooling_coat_1..3`, `thermal_coat_1..3`) накладываются на броню за Швейным столом (`sewing_table`). Термометр (`thermometer`) показывает температуру.
2. **Жажда.** Пить воду из фляги (`canteen`, `large_canteen`) или соков (яблочный, морковный, арбузный и др.); сырую воду очищает `water_purifier`/чара «Чистота». Эффекты — `thirst`, `hydration_fill`.
3. **Ранения.** Урон ложится на части тела; лечат бинтами и аптечками (`bandage`, `plaster`, `medkit`, `tonic`, `healing_herbs`, `morphine`).
4. **Блоки**: Бойлер (`heater`) и Ледник (`cooler`) — локальный нагрев/охлаждение, `sun_fern_crop`/`ice_fern_crop` — тепло- и холодорастения, `water_plant_crop`. Сердца: `heart_container` (Кристальное сердце), `shield_container`.
5. **Сезоны.** Связка с Serene Seasons встроена (`[temperature.integration.serene-seasons]`) — сезон меняет температуру: летом жарче, зимой холоднее; проверено, значения — в [compat.md](../compat.md) (п. 11).

### Предметы/блоки

| id | Что делает |
|---|---|
| `thermometer`, `seasonal_calendar` | Термометр и сезонный календарь |
| `canteen`, `large_canteen`, `*_purified`, `purified_water_bottle`, `water_purifier` | Вода и очистка |
| `bandage`, `plaster`, `medkit`, `tonic`, `morphine`, `first_aid_supplies`, `healing_herbs` | Лечение ранений конечностей |
| `heart_container`, `heart_fragment`, `shield_container` | Дополнительные сердца и щит |
| `heat_resistance_ring`, `cold_resistance_ring`, `thermal_resistance_ring` | Кольца (слот Curios) |
| `snow_*`, `desert_*`, `cold_string`, `warm_string`, `heating_coat_*`, `cooling_coat_*`, `thermal_coat_*` | Утепление и охлаждение одежды |
| `apple_juice`, `carrot_juice`, `cactus_juice`, `melon_juice`… | Соки (напитки, дающие и воду, и эффекты) |
| `heater`, `cooler`, `sewing_table` | Блоки для температуры и шитья |

### Эффекты

Эффекты (id `legendarysurvivaloverhaul:…`): `heat_stroke`, `heat_thirst`, `frostbite`, `cold_hunger`, `thirst`, `hydration_fill`, `heat_resistance`, `cold_resistance`, `heat_immunity`, `cold_immunity`, `temperature_immunity`, `hot_food`, `cold_food`, `hot_drink`, `cold_drink`, `painkiller`, `painkiller_addiction`, `hard_falling`, `vulnerability`, `headache`, `recovery`. Есть зелья термо- и водных эффектов (в т. ч. взрывные и длинные).

### Чары

`legendarysurvivaloverhaul:refreshing` (больше воды и сытости от фляги), `purity` (автоочистка воды), `reservoir` (объём фляги).

### Команды/гамерулы

По именам в классах мода есть команды `temperature` (`get`/`set` — температура игрока), `bodydamage` (`get`/`set`/`heal`/`healAll` для частей тела), `healthoverhaul` (сердца, щит, «разбитые сердца»); точный синтаксис покажет автодополнение чата.

### Конфиги

Файла конфига LSO в `pack/config` нет — действуют умолчания мода; значения температур, жажды и ранений — датапак `data/legendarysurvivaloverhaul/…` (тут же большой набор готовых значений для чужих модов).

## Natural Disasters!

<!-- auto:start card:natural_disasters -->
<a id="natural_disasters"></a>
- **Версия:** 2.0.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/natural_disasters)
- **Что это:** 9 стихийных бедствий, которые могут возникать сами по себе. _(источник: docs сборки)_
- **В сборке:** Natural Disasters! 2.0.0 (MCreator, YouTuber): метеоры, цунами, пожары, кислотный дождь, воронки, землетрясения; торнадо и песчаная буря отключены (дубли Weather2) модом disaster-ward
- **В наших docs:** [compat.md](../compat.md) (34), [roadmap.md](../roadmap.md) (11), [friends.md](../friends.md) (2), [issue-history.md](../issue-history.md) (2), [performance.md](../performance.md) (1)
<!-- auto:end card:natural_disasters -->

### Как играть

Бедствия мода — **сущности** (MCreator, настроек нет): метеор, метеоритный дождь, цунами, лесной пожар, кислотный дождь, воронка (`sinkhole`), землетрясение. Вручную: клавиша «Открыть меню бедствий», яйца призыва, `/summon`; предмет `natural_disasters:disasterdetector` — детектор бедствий. У нас **торнадо и песчаная буря отключены** (дубли Weather2), а случайным спавном управляет наш мод [Оберег от катаклизмов](own/disaster-ward.md): бросок раз в 30–80 мин на игрока, не более одного бедствия на измерение (иначе ~5 TPS). Разрушение построек у этих бедствий встроенное и не отключается, содержимое контейнеров может пропасть. Подробности — [compat.md](../compat.md), «Катаклизмы».

### Мобы

Сущности-бедствия `natural_disasters:` `meteor`, `spawnmeteorshower`, `spawnmeteor`, `tsunami`, `wildfire`, `acidrain`, `sinkhole`, `earthquake`, `spawnaearthquake`, а также `tornado` и `sandstorm` (последние два у нас отключены).

### События

Случайный спавн бедствий — через `disaster-ward` (см. выше); их можно убрать из зоны блоком-оберегом.

### Данжи/структуры (/locate)

Структуры в мире: `natural_disasters:meteorite`, `natural_disasters:wreckedhouse`, `natural_disasters:sinkholestructure` (`/locate structure natural_disasters:meteorite`).

### Конфиги

Своего конфига нет. Отключённые сущности и лимит параллельных бедствий — в `pack/config/disaster_ward-common.toml` (`disabledEntities`, `maxConcurrent`).

## Serene Seasons

<!-- auto:start card:serene-seasons -->
<a id="serene-seasons"></a>
- **Версия:** 9.1.0.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/serene-seasons)
- **Что это:** Сезоны: смена цветов, температур и др. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (11), [performance.md](../performance.md) (5), [roadmap.md](../roadmap.md) (4), [client-checklist.md](../client-checklist.md) (1), [reset.md](../reset.md) (1), [version-choice.md](../version-choice.md) (1)
<!-- auto:end card:serene-seasons -->

### Как играть

Сезоны мира: весна, лето, осень, зима (каждый с ранним/средним/поздним подсезоном; в тропиках — «влажный/сухой» сезон). Цвет листвы и травы меняется, зимой выпадает снег и замерзает вода, **урожай зависит от сезона** (теги `sereneseasons:spring_crops`, `summer_crops`, `autumn_crops`, `winter_crops`, `year_round_crops`); теплица (`greenhouse_glass`) и бесплодные биомы (`infertile_biomes`) защищают от сезонных ограничений.
- **Календарь** (`sereneseasons:calendar`) показывает сезон и день; **Датчик сезона** (`season_sensor`) выдаёт сигнал редстоуна.
- Связка с температурой — через Legendary Survival Overhaul ([compat.md](../compat.md)).

### Предметы/блоки

`sereneseasons:calendar`, `sereneseasons:season_sensor` (блок).

### Команды/гамерулы

`/season get` — текущий сезон и день; `/season set <сезон>`; гамерул `doSeasonCycle` («Продвигать сезонный цикл»).

### Конфиги

Файлов `sereneseasons*.toml` в `pack/config` нет (кроме Plus, ниже). Списки биомов (`blacklisted_biomes`, `tropical_biomes`, `lesser_color_change_biomes`, `infertile_biomes`) — теги датапака `data/sereneseasons/tags/worldgen/biome`.

## Serene Seasons Plus

<!-- auto:start card:serene-seasons-plus -->
<a id="serene-seasons-plus"></a>
- **Версия:** Forge-1.20.1-5.1.4 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/serene-seasons-plus)
- **Что это:** Развитие Serene Seasons: скорость дня и ночи по подсезонам, лучшее накопление и таяние снега. _(источник: docs сборки)_
- **В сборке:** требует GabouLibs (удалена автором) — заменена нашей библиотекой pack/mods/gaboulibs-shim (patches/gaboulibs-shim)
- **В наших docs:** [compat.md](../compat.md) (7), [performance.md](../performance.md) (4), [roadmap.md](../roadmap.md) (3), [client-checklist.md](../client-checklist.md) (1)
<!-- auto:end card:serene-seasons-plus -->

### Как играть

Расширение сезонов (требует нашей замены библиотеки GabouLibs, см. [compat.md](../compat.md), п. 8): **накопление снега** по сезону (слои до 24, `maximumSnowLayersPerColumn`), сезонный рост травы и цветов, **длина дня и ночи по сезонам** (связано с Better Days). Температуру Plus не трогает — это делает связка LSO × Serene Seasons.

### Конфиги

`pack/config/sereneseasonsplus-common.toml` (в сборке включено накопление снега и сезонный рост травы; `changeDayNightLengthsWithSeasons = true`, `allowSeasonalControlOfBetterDaysTimeSpeed = true`, `fullDayNightCycleLengthInRealMinutes = 20.0`, `syncSeasonsToEasternCanadianCalendar = false`). Сброс конфига при обновлении — [compat.md](../compat.md), К5.

## Weather Storms & Tornadoes

<!-- auto:start card:weather-storms-tornadoes -->
<a id="weather-storms-tornadoes"></a>
- **Версия:** 1.20.1-2.8.3 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/weather-storms-tornadoes)
- **Что это:** Бури, торнадо, ветер и множество эффектов частиц. _(источник: docs сборки)_
- **В сборке:** Weather2 2.8.3 (Corosus/ProtoManly): штормы, торнадо, песчаные/снежные бури, сирены; профиль — pack/config (разрушение построек ВЫКЛ)
- **В наших docs:** [compat.md](../compat.md) (27), [roadmap.md](../roadmap.md) (5), [performance.md](../performance.md) (4), [issue-history.md](../issue-history.md) (2), [friends.md](../friends.md) (1), [reset.md](../reset.md) (1)
<!-- auto:end card:weather-storms-tornadoes -->

### Как играть

Weather2 (Corosus, с библиотекой CoroUtil) заменяет ванильную погоду на частицы: штормовые фронты, град, ветер, листопад, снежные и песчаные бури, **торнадо (F0–F5) и циклоны (C0–C5)**. Торнадо рвут постройки по списку блоков (деревянное, листва, стекло…) и подхватывают игроков и мобов; сундуки, печи и блоки земли/камня не трогают. Шансы F4/C4 = 10 %, F5/C5 = 5 %, молнии огонь не ставят. Зону без штормов создаёт [оберег](own/disaster-ward.md).

### Предметы/блоки

`weather2:tornado_sensor` (датчик торнадо), `tornado_siren` и `tornado_siren_manual` (сирены), `wind_vane` (флюгер), `anemometer`, `wind_turbine`, `weather_forecast`, `weather_machine`, `weather_deflector` («Отражатель бурь»), `sand_layer`; предметы `weather_item`, `pocket_sand`.

### События

Штормы, торнадо, град, песчаные и снежные бури — случайные события мода; частота — по умолчанию (шторм-«убийца» ~ раз в час на сервер).

### Команды/гамерулы

Команда `/weather2` (подкоманды в классе: `summon`, `kill_all_storms`, `wind_speed`, `wind_angle`, `storm_*` и др.; синтаксис — через автодополнение, нужен оператор).

### Конфиги

`pack/config/Weather2/Storm.toml`, `Tornado.toml`, `Misc.toml`: `Storm_Tornado_grabBlocks = true`, `Storm_Tornado_GrabCond_List = true` (разрушение по списку); отключить разрушения — `Storm_Tornado_grabBlocks = false`, только ветер и листва — `Aesthetic_Only_Mode = true` (`Misc.toml`). Подробности — [compat.md](../compat.md), «Катаклизмы: Weather2 + CoroUtil».

## Оберег от катаклизмов (Disaster Ward)

<!-- auto:start card:disaster-ward -->
<a id="disaster-ward"></a>
- **Версия:** 1.2.1 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/disaster-ward-1.2.1.jar`
- **Что это:** Блок-оберег (только креатив): в его зоне (один радиус) не бушуют катаклизмы Weather2 и Natural Disasters!, не распространяется заражение Fungal Infection: Spore и SRP System, пришедшие снаружи бедствия не ломают блоки внутри, ориджиниумная буря Bob's Originium Dream не призывает в зоне монстров и н… _(источник: mods.toml)_
- **В сборке:** наш мод (mods-src/disaster-ward): «Оберег от катаклизмов» (только креатив; зона без бедствий Weather2/Natural Disasters!) и редкий случайный спавн бедствий
- **В наших docs:** [compat.md](../compat.md) (3), [friends.md](../friends.md) (1), [issue-history.md](../issue-history.md) (1), [roadmap.md](../roadmap.md) (1)
<!-- auto:end card:disaster-ward -->

### Как играть

Блок `disaster_ward:ward` (только креатив: вкладка «Функциональные блоки»/«Операторские» или `/give @s disaster_ward:ward`) создаёт зону радиусом 96 блоков (по X/Z, на всю высоту), где не бушуют бури Weather2 и бедствия Natural Disasters!, не растёт ориджиниум и не расползаются паразиты. Мод же включает и дозирует **случайные бедствия** (раз в 30–80 мин на игрока, не более одного одновременно на измерение).
Полный разбор, числа и конфиг — в отдельной статье [Оберег от катаклизмов](own/disaster-ward.md); замеры нагрузки — [compat.md](../compat.md).

### Конфиги

`pack/config/disaster_ward-common.toml` (разделы `[ward]`, `[parasites]`, `[naturalDisasters]`, `[oriStorm]`, `[oriGrowth]`) — таблица параметров в [статье мода](own/disaster-ward.md#конфиги).

<!-- cards:end -->
