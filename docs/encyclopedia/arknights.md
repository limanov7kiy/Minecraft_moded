# Arknights

Расы, умения, мебель, Источник, Эндфилд и всё, что связано с миром Arknights.

<!-- auto:start toc -->
Модов в категории: **15**.

- [Arknights origin furs](#arknights-origin-furs)
- [Arknights origins originium arts](#arknights-origins-originium-arts)
- [Arknights origins races](#arknights-origins-races)
- [Arknights skills](#arknights-skills)
- [ArknightsFurniture](#arknightsfurniture)
- [BOB's Originium dream](#bobs-originium-dream)
- [Caerula Arbor](#caerula-arbor)
- [Contigency Contract Lite](#contigency-contract-lite)
- [Endfield Industry](#endfield-industry)
- [Gathering Torches Become Sunlight](#gathering-torches-become-sunlight)
- [OriginiumArts](#originiumarts)
- [Origins (Forge)](#origins-forge)
- [Pufferfish's Skills](#pufferfishs-skills)
- [Pufferfish's Skills x Origins](#pufferfishs-skills-x-origins)
- [Sarkaz's Furnace Side Fables](#sarkazs-furnace-side-fables)

**См. также:** [Мысленная буря](arknights/mind-storm.md) — отдельная статья (ведёт Поток 48)

Вернуться: [оглавление справочника](INDEX.md).
<!-- auto:end toc -->

## Общее по категории

Страница собирает всё, что связано с миром Arknights (Terra): расы и профессии поверх мода Origins, Источник (Originium), несколько больших
сюжетных модов с боссами и структурами, мебель и «заводской» мод Эндфилд. Все моды — Forge 1.20.1, сторона `both` (кроме `arknights-furs` —
это только клиентские модели).

**С чего начать.** При первом входе в мир Origins открывает экран выбора: **раса** (слой `arks`), затем **класс/подкласс** (Arknights Skills)
и **Искусство Ориджиниума** (Arknights Origins Originium Arts). Просмотр своей расы — клавиша «View Origin» (Origins), способности — клавиши
«Active Power (Primary/Secondary)». Сменить выбор: предмет «Сфера происхождения» (`origins:orb_of_origin`) или оператор командой
`/origin set <игрок> <слой> <происхождение>`.

> **Клавиши модов в сборке «не назначены».** `mm-perf` (см. [Свои моды](own.md)) при первом запуске клиента отвязывает все клавиши модов,
> стоявшие на заводском значении. Назначьте в «Настройки → Управление» (там есть поиск по названию): способности Origins, дерево навыков
> («Open Skill Tree»), рюкзак мыслей Sarkaz's Furnace и меню напарников Gathering Torches.

**Как моды связаны между собой**
- Расы (`arks:*`) читают Origins; профессии и искусства — отдельные слои тех же Origins; Puffish Skills ×Origins дают дерево навыков под каждую профессию.
- *Sarkaz's Furnace Side Fables* ведёт «нагрузку мыслей»; бонус рас Human/Sarkaz/Nephesh «Thought Load +N» увеличивает её предел (подробности у карточки мода и в [mind-storm.md](arknights/mind-storm.md)).
- *Bob's Originium Dream* — буря и рост ориджиниума; их укрощает наш [Оберег](own/disaster-ward.md) (`disaster-ward`).
- *Caerula Arbor*, *Bob's Originium Dream*, *Sarkaz's Furnace*, *Gathering Torches* — источники боссов; статы — в [bosses.md](../bosses.md).
- Русский перевод всех этих модов (включая книги Patchouli Каэрулы и Боба) — ресурспак сборки, см. [russification.md](../russification.md).

**Статья по механике:** [Мысленная буря](arknights/mind-storm.md) (эффект `sarkazs_furnace_side_fables:thought_load`) — отдельный материал.

---

<!-- cards:start -->
## Arknights origin furs

<!-- auto:start card:arknights-furs -->
<a id="arknights-furs"></a>
- **Версия:** 1.0.2-1.20.1 · **Сторона:** client · **Источник:** [Modrinth](https://modrinth.com/mod/arknights-furs)
- **Что это:** Клиентский мод: подменяет модель игрока в зависимости от расы Arknights (уши, хвосты, крылья). _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:arknights-furs -->

### Как играть
Клиентский мод (west wind): подменяет модель игрока в зависимости от расы Arknights (уши, хвост, крылья и т. д.). На сервер не ставится. Нужен Origins и расы `arks`. В сборке к нему добавлен наш порт «Origin Furs Forge» (`origin_visuals`, [Свои моды](own.md)) — он отвечает за рендер.

### Конфиги
Своих правок нет.

## Arknights origins originium arts

<!-- auto:start card:arknights-origins-originium-arts -->
<a id="arknights-origins-originium-arts"></a>
- **Версия:** 1.0.2-1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/arknights-origins-originium-arts)
- **Что это:** Слой Origins «Искусства Ориджиниума»: девять школ на выбор (огонь, лёд, молния, природа, свет, пространство, призыв, кровь, новичок). _(источник: docs сборки)_
<!-- auto:end card:arknights-origins-originium-arts -->

### Как играть
Слой «Искусства Ориджиниума» Origins — 9 «искусств» на выбор: Blaze (огонь), Frost (лёд), Lightning (молния), Nature (природа), Holy (свет), Ender (пространство), Evocation (призыв), Crimson (кровь) и Novice (нет искусств, но крепкое тело). Описание в игре подсказывает, где «учиться» (Казимеж, Урсус, Саргон…). Сами заклинания даёт мод *OriginiumArts* (`ognmarts`) и профильные силы.

### Команды/гамерулы
`/origin set <игрок> arkarts:<id> …`. Своих команд нет.

## Arknights origins races

<!-- auto:start card:arknights-races -->
<a id="arknights-races"></a>
- **Версия:** 1.5.3-1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/arknights-races)
- **Что это:** Около 50 рас и подрас Terra для Origins со своими бонусами и навыками (Сарказ, Феллайн, Урсус, Эгир и др.). _(источник: docs сборки)_
<!-- auto:end card:arknights-races -->

### Как играть
Мод west wind для Origins (Forge 1.20.1): **около 50 рас и подрас** слоя «Раса» (`arks:*`). Выбор — на экране Origins при первом входе или `/origin set <игрок> origins:origin arks:<раса>`. Каждая раса даёт пассивные бонусы/штрафы и 0–2 активных навыка (клавиши «Active Power»), которые в сборке нужно назначить вручную.

### Предметы/блоки
Своих предметов нет (способности завязаны на предметы ванили: красная пыль для «магической атаки» Caprinae, булыжник для Gargoyle и т. п.).

### Мобы
Мобов нет. Расы «Сарказ» делают нейтральных мобов агрессивными к игроку и запрещают торговать с жителями без «Героя деревни».

### Эффекты
Отдельных эффектов нет; поверх них — силы Origins (`arks:<раса>/<сила>`: `status`, `modify`, `skill`…).

### События
Нет. Часть расовых бонусов зависит от биома (Archosauria усиливается в джунглях).

### Команды/гамерулы
Команды Origins: `/origin set|get|has|gui|random`, слой — `origins:origin`. Своих команд нет.

### Конфиги
Расы по группам (ключевые силы):
- **Лёгкие/универсальные:** Cautus, Feline (лазание по блокам, без урона от падения, −10 % здоровья, +10 % скорости), Lupo, Perro («нюх»), Vulpo, Zalak, Forte (регенерация по навыку), Kuranta (спринт), Liberi (полёт), Vouivre, Draco, Aslan, Pegasus, Hippogryph (полёт), Griffin.
- **Водные:** Aegir (верстак, ходьба по воде и дыхание в воде), Seaborn (рой: эволюция/размножение), Seaborn Endspeaker, Seahunter («Охотники Бездны»; см. патч прыжка в [compat.md](../compat.md)), Anura (нейротоксин).
- **Сильные/воины:** Ursus, Oni, Lung, Kylin (молнии), Durin (копание), Petram (кузнечное), Savra, Sankta (стрелковое оружие), Archosauria, Caprinae (магия: −40 % магического урона).
- **Особые:** Human («Pre-human»: −60 % нервного урона, +50 % урона по ориджиниуму, защита от Орипатии), Elf, Phidia, Anasa, Anaty, Kukulkan, Manticore, Nightzmora, Elafia.
- **Сарказы:** базовый Sarkaz (-20 % здоровья, враждебность, голод ×2) и 12 подрас: Banshee (проклятие), Cyclops (пророчество), Diablo (огонь), Djall (отталкивание), Gargoyle (камень), Goliath (сила), Lich (хранилище), Nachzehrer (грязь), Nephesh (вселение, полёт), Vampire (шаг крови), Wendigo (ритуал).
Расовый бонус «Thought Load +N» (Human и подрасы сарказов +7, Nephesh +17) увеличивает предел нагрузки мыслей Sarkaz's Furnace — см. [Sarkaz's Furnace Side Fables](#sarkazs-furnace-side-fables) и [mind-storm.md](arknights/mind-storm.md). Перевод — `pack/resourcepacks-src/russian-moded/assets/arks`.

## Arknights skills

<!-- auto:start card:arknights-skills -->
<a id="arknights-skills"></a>
- **Версия:** 1.1.1-1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/arknights-skills)
- **Что это:** Восемь классов и 25 подклассов Arknights для Origins; к каждому классу — дерево навыков Pufferfish's Skills. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:arknights-skills -->

### Как играть
Мод west wind: **8 классов** и **25 подклассов** по Arknights, каждый — «происхождение» Origins в слое «класс» / «подкласс».
- Классы: Vanguard (Charger, Pioneer, Tactician), Guard (Crusher, Dreadnought, Fighter, Lord, Musha), Defender (Arts Protector, Fortress, Protector, Sentinel Protector), Sniper (Artilleryman, Besieger, Deadeye, Marksman), Caster (Core, Primal), Medic, Supporter (Bard, Decel Binder), Specialist (Dollkeeper, Executor, Hookmaster, Push Stroker).
- Каждый подкласс даёт пассивку/навык (например, Musha — не регенерирует естественно, урон растёт с потерей здоровья; Besieger — дальний урон выше вблизи; Fortress/Artilleryman — отложенный физический взрыв по цели).
- Вместе с *Pufferfish's Skills × Origins* для классов есть деревья навыков (45 навыков: +10 % урона по воздуху, пробивание брони, скорость атаки…), клавиша «Open Skill Tree».

### Предметы/блоки
33 предмета-иконки профессий и подклассов (для интерфейса выбора).

### Команды/гамерулы
Как у Origins (`/origin set <игрок> <слой> arkskills:<класс>` / `arkbranches:<подкласс>`) и `/puffish_skills …`.

### Конфиги
Своих правок нет; перевод — `pack/resourcepacks-src/russian-moded/assets/arknights_skills` (имена подклассов оставлены латиницей, описания русские).

## ArknightsFurniture

<!-- auto:start card:arknightsfurniture -->
<a id="arknightsfurniture"></a>
- **Версия:** 1.20.1-1.0.4-Forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/arknightsfurniture)
- **Что это:** Строительный набор: мебель, обои, полы и логотипы фракций Arknights (113 блоков). _(источник: docs сборки)_
<!-- auto:end card:arknightsfurniture -->

### Как играть
Мод Beishan_Besson и RJ (1.0.4): строительный набор «мебель и флаги фракций».
1. Мебель — 113 блоков в 8 вкладках креатива: «Логотипы», «Материалы», «Склад», чёрно-белая/оранжевая мебель, «Колумбийское кафе», «Био-документалка», «Каюта № 12», лаунж «Лунмэнь»; детали — `furn_parts`, картон `cardboard`.
2. Логотипы фракций (Rhodes Island, Babel, Kazimierz, Siracusa, Lungmen, Penguin Logistics и ещё ~40) — настенные блоки и декор.

### Предметы/блоки
Воздушный матрас, большие стеллажи, шкафчики и тумбочки (контейнеры на 3–9 предметов), столы и стулья, диваны, кровати, ТВ, потолочные светильники, обои и полы, горшечные растения, кофейные зёрна, логотипы фракций и др.

### Конфиги
Своих правок нет.

## BOB's Originium dream

<!-- auto:start card:bobs-originium-dream -->
<a id="bobs-originium-dream"></a>
- **Версия:** 1.1.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/bobs-originium-dream)
- **Что это:** Мод Боба: ориджиниумные черви, бури и рост ориджиниума, биомы и структуры пустоши, десятки боссов, внутреннее измерение. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (5), [russification.md](../russification.md) (2), [friends.md](../friends.md) (1), [performance.md](../performance.md) (1), [version-choice.md](../version-choice.md) (1)
<!-- auto:end card:bobs-originium-dream -->

### Как играть
MCreator-мод goods (в jar названия на китайском; мы перевели, включая книгу `bobbook`). Источник (Originium) и Орипатия — «рудная болезнь».
1. Сначала найдите **Ориджиниумную пустошь** (биом `oridesert`, рядом с пустыней — достижение «Добро пожаловать в пустоши»); добывайте/получайте **осколки ориджиниума**.
2. Победите **ориджиниумного червя** — его можно приручить корм-ом; из червей и жителей через **шприц** (достижение «Сделайте шприц!») получают жидкости; еда из червей («Очень вкусная еда», «Ух ты, еда из Терры!», пиво «Супер~большой~кубок!»).
3. Охота за боссами: Мефисто, Милан, Помпеи, Блуждающий домик, Заблудший колосс, Плод чревоугодия и др.; термоплавкий топор («Большой Чёрный»), «Неистовство» (серп).
4. Лаборатория в холодном биоме даёт ключ в **внутреннюю вселенную** (`bobsoriginiumdream:oriuniverse`, достижение «Вселенная поёт мне!»).
5. **Орипатия:** включена гейм-рулом `oriillness`; защита — чары «Ориджиниумная защита» и атрибуты защиты.
6. Ориджиниумную бурю и рост в сборке настроили, чтобы они были редкими и обходили зоны Оберега — см. ниже и [compat.md](../compat.md) («Ориджиниумная буря Bob's Originium Dream + оберег 1.2.0»).

### Предметы/блоки
~525 предметов и ~77 блоков: шприц, ориджиниумные инструменты (топор «Большой Чёрный»), серп «Неистовство», «Монета Боба», еда из червей, призыватель/сердце/камень «Заблудшего колосса», блоки роста (`oriblockb`, ядро `oriblock_core`, блок бури `oristormblock`), патчули-книга Боба.

### Мобы
118 сущностей: ориджиниумные черви (обычные, «Альфа», горячие, ледяные, взрывные…), крабы и клешни (`stonetones`, `crab`, `rockdog`), заражённые существа (`oripignormal`, `oribear`, `orienderman`, `originiutant`, `oripillar`), и боссы **Мефисто (поёт)**, **Милан**, **Помпеи**, **Блуждающий домик** (`roamhouse`), **Заблудший колосс** (`losewaycolossus`), **Плод чревоугодия**, **Пёстрый жук** (`shinybug`), **Остатки мира** (`worldremains`, две фазы), **Суть эволюции** (3 формы), **Колосс Боба**. HP до 1000, статы — [bosses.md](../bosses.md).

### Эффекты
| id (`bobsoriginiumdream:`) | Что делает |
|---|---|
| `oristormeffect` | «Ориджиниумная буря» |
| `oriboost` | «Всплеск Ориджиниума» |
| `oristop` | «Запрет ориджиниума» |
| `orielectricity` | «Эндер-заряд» |
| `distortion` | «Искажение наростов» |
| `bleed` | кровотечение |
| `afraid` | паника |
| `clawarmor` | «Защита твёрдого панциря» |
| `losewayskill` | «Режим уборки» (Заблудший колосс) |
| `rigidharpoon`, `roll`, `rolling`, `rushrush`, `furioso`, `furiosoready`, `ea_ttime`, `mudsolid`, `lifeordie`, `waittoboom` | эффекты оружия и боссов |

### События
**Ориджиниумная буря:** игровое правило `oristorm`; в моде запускается счётчиком опыта (`oristormfrequency`). В сборке счётчик поднят до 100000, а запуск — случайный бросок раз в 10 мин с шансом 5,5 % (в среднем раз в ~3 часа игры + 10 минут предупреждения) из мода-оберега. Буря длится 3000 тиков (при `oristormsummon=200`), призывает червей, огненные шары, молнии и падающие блоки, шипы бури 50×53×57. Подробности и тесты — [compat.md](../compat.md).
**Рост ориджиниума** (`alloworigrow`): блоки медленно расползаются (в сборке — шаг раз в ~8 мин вместо 2), ядра ломаются алмазной киркой; в зоне Оберега не растёт.

### Данжи/структуры (/locate)
`/locate structure bobsoriginiumdream:<id>`:
`bobbar` («Бар Боба»), `bo_bfarm` («Ферма Боба»), `gluttonywaterway`, `meltarena`, `losewaychurch`, `kingnest`, `orilabortory_1`, `orilaboratorykey` (лаборатория с ключом), `orilaboratorymud`, `oristele`, `orispike`/`orispikebig` (шипы бури). Особо тяжёлые шаблоны (`orilabortory_key` ~2,1 млн блоков, `kingtemple`) мы ограничиваем — [compat.md](../compat.md).
Биомы: `oridesert` (Ориджиниумная пустошь), `oriforest` («Лес погони за огнём»), `oriplain`. Измерение: `bobsoriginiumdream:oriuniverse`.

### Чары
`oridamageattack` — «Сила ориджиниума» (доп. ориджиниумный урон = атака ×0,5 × уровень), `oriprotect` — «Ориджиниумная защита» (меньше орипатии), `attributeliberate` — «Освобождение атрибута».

### Команды/гамерулы
Команды без проверки прав (доступны **любому игроку**, мод это не ограничивает): `/origrowopen`, `/origrowclose` (рост ориджиниума), `/oribreakopen`, `/oribreakoff` (разрушение блоков ориджиниумными монстрами), `/redstoneactionbegin`, `/redstoneactionclose` (операция «Красный камень» — враги сильнее). Они просто переключают гейм-рулы; если нужен контроль, лучше менять гейм-рулы самому оператору.
Игровые правила: `oristorm` (буря), `oristormfrequency` (чем выше, тем реже; по умолчанию 200), `oristormsummon` (сила бури, чем больше — тем слабее; ≥500 выключает), `oriwait` (ожидание перед бурей), `alloworigrow`, `oribreak`, `oriillness`, `orisummonlimit` («Протокол Ориджиниума» — снимает лимиты спавна и урона боссов), `redstoneaction`, `gluttonypower`.

### Конфиги
Один раз на мир KubeJS (`pack/kubejs/server_scripts/oristorm_rules.js`) ставит: `oristorm=true`, `oristormfrequency=100000`, `oristormsummon=200`, `alloworigrow=true`, `oribreak=false`; потом админ может менять правила как хочет. Запуск и рост регулирует `config/disaster_ward-common.toml`, разделы `[oriStorm]` и `[oriGrowth]` — см. [Оберег](own/disaster-ward.md).

## Caerula Arbor

<!-- auto:start card:caerula-arbor -->
<a id="caerula-arbor"></a>
- **Версия:** 0.12.7.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/caerula-arbor)
- **Что это:** Порождения моря и расползающаяся метка пучины: сложная кампания с рассудком, эволюцией, 46 структурами и около 15 боссами. _(источник: docs сборки)_
- **В наших docs:** [russification.md](../russification.md) (9), [roadmap.md](../roadmap.md) (6), [compat.md](../compat.md) (4), [performance.md](../performance.md) (3), [enchanting-table.md](../enchanting-table.md) (1), [version-choice.md](../version-choice.md) (1)
<!-- auto:end card:caerula-arbor -->

### Как играть
Мод про «Порождений моря» (Seaborn) и «Метку пучины» — расползающуюся по суше ткань моря; сложный (на предупреждающих экранах мода: «высокая сложность, не для обычного выживания»).
1. Сделайте **глубокосиний регистратор** (`caerula_recorder`): ему нужна кутиновая стеклянная панель из глубоководного кутина (падает с карманных морских криперов, лежит в структурах). Регистратор (и блок «Терминал») показывает Огонь, Рассудок, Целевые жизни, Отторжение, коллекцию реликвий, эволюцию. Книга мода (Patchouli) переведена целиком.
2. Следите за четырьмя «шкалами»: **Целевые жизни и щит** (гасят смертельный урон, расходуют Огонь), **Огонь** 0…100 (при низком растут шансы Отторжения и «Зова Многих»), **Рассудок** (при 0 — большой урон и головокружение 10 с; «нервный урон» от мобов и метки пучины), **Реликвии** (постоянные и одноразовые, из структур и боссов).
3. **Метка пучины** распространяется и разъедает природные блоки; копать мотыгой, прямая добыча бьёт по рассудку. Остановить — строить и копать барьеры.
4. **Океанизация игрока:** мутагенная клетка (три стадии) → все порождения нейтральны, иммунитет к метке, подводное дыхание, доступна «эволюция игрока». Если умереть от урона самой клетки, прогресс сбрасывается в 0.
5. **Эволюция** порождений идёт четырьмя стратегиями (миграция, выживание, размножение, рост) и открывает «безмолвную» и «сублимацию»; ход виден на станции наблюдения за приливом. Хотите притормозить — гейм-рулы ниже.
6. Прокачка снаряжения: хитин → композитный хитин → сплав преисподней бездны (после «Иссохшего духа безмолвного моря»). Порядок записей книги = рекомендуемый порядок боссов.

### Предметы/блоки
~1270 предметов и ~200 блоков. Главные: глубокосиний регистратор, **Геном охотника** (находится в «Слиянии водоворотов», очищается центрифугой в `aegir_lab`), «Хитиновая окарина» (зажать ПКМ — будит предводителя в структуре при табличке шифров водоворота), **Рука клеймения**/«Золотой потир»/«Контракт выжившего» (реликвии), хитиновое оружие и броня (трезубец, лук из флоэмы), мутагенные клетки и капсулы, блоки «Нижнего моря» (`trail_*`: доски, брёвна, кирпич, дверь), еда (лапша, консервы, моти).

### Мобы
179 сущностей: «океанизированные» версии ванильных мобов (житель, разбойник, пиглин, хоглин, эндермен, паук, свинья…), эволюционные порождения (одно-/многоклеточные дрейфующие, ловцы), производные метки и боссы. Боссы (порядок по книге): **Береговой нюхач-археолог**, **Скалковый наблюдатель**, **Иссохший дух безмолвного моря** (океанизированный визер), **Тиран Огненной долины**, **Прокладчик путей**, **Мартус**, **Квинтус**, **Эндерина «Пролог»**, **Иллюзионист заколдованного берега**, **Приливный епископ**, **Убийца Вещаний (Endspeaker)**, **Изумик, Источник жизни**, **«Иллюзия паранойи» (Хайморе)**, **Ишармла**. Статы — [bosses.md](../bosses.md).

### Эффекты
93 эффекта; главные (id `caerula_arbor:`):
| id | Что делает |
|---|---|
| `essence_resistance` | «Сопротивление сути»: защита от нервного урона (даёт чара «Защита от опасностей») |
| `sanity_heal`, `instant_sanity` | восстановление рассудка |
| `sanity_immue`, `add_sanity_resistance` | защита рассудка |
| `haemophilia`, `hemostatic` | кровотечение после урона / остановка кровотечения |
| `frozen`, `trapped`, `mute` | заморозка, ловушка, «немота» (отключает способности) |
| `immortal`, `fake_death`, `invulnerable` | бессмертие, «мнимая смерть», неуязвимость (боссы и реликвии) |
| `infested` | заражение |
| `isharmla_curse`, `izumik_shock`, `izumik_learn` | эффекты боссов |
| `kings_breath`, `kings_boost`, `echo_fight`, `survivors_guide` | эффекты реликвий и оружия |

### События
**Зов Многих** — ночное событие для игрока с низким Огнём: действует 2 игровых дня, смотрится в регистраторе. **Отторжение** — постоянные недуги (расстройство внимания, нарушение кроветворения, искажение плоти, нейродегенерация), лечатся лекарствами. Рейды «Прилив мести» (`raid_sign`) по волнам, «Бродячий торговец» (торговцы в структурах), возникновение боссов от знаков: «Последний рыцарь» у моря, Мартус, Хайморе, Тирстер.

### Данжи/структуры (/locate)
46 структур, `/locate structure caerula_arbor:<id>` (ru-названия — в нашем переводе). Примеры по среде:
- **суша:** `church` (Церковь Лосен, равнины), `lighthouse`, `coral_crown`, `sea_nest` (у моря), `watchtower`, `lamp` (леса), `haunted_house` (тёмный лес, болото), `aircraft` (пляж, пустыня), `oddfactory` (бесплодные земли), `chest_museum` (джунгли), торговцы `trader_oak|sand|cave|sky|tnt|end`;
- **«Нижнее море»** (биом `caerula_arbor:branded_land`): `brand_portal`, `brand_palace`, `iberia_eye`, `flourish`, `air_base`;
- **море:** `sink_field`, `sink_garden`, `sink_hall`, `sink_remains`, `anchor_ruin`, `tide_station`, `deep_reef`, `submarine` («Слияние водоворотов»), `aegir_lab`, `drilling_platform`, `bishop_cave`, `isharmlacemetry`, `slider_statu`, `golden_age`;
- **подземье/особые:** `abyssal_lab` (заснеженная лаборатория), `containment_cave`, `crawler_lab_room`, `izumik_island` («Вихрь сублимации», арена Изумика), `rhodes_site`, `inquisition_outpost`, `believer_home`, `safe_house`, `branded_town`.
Много структур огромные (2 млн блоков у `iberia_eye`/`abyssal_lab`) — их шаблоны мы режем в `mm-compat`, см. [compat.md](../compat.md) (OOM при полёте).

### Чары
| id (`caerula_arbor:`) | Действие |
|---|---|
| `magic_tolerance` | сопротивление магии |
| `hazard_protection` | периодически даёт «Сопротивление сути» |
| `sanity_defend` / `sanity_injury_curse` | меньше урона рассудку / проклятие урона рассудку |
| `sanity_reaper` | атака наносит урон рассудку |
| `rejection_curse` | выше шанс Отторжения |
| `flexibility` | шанс промаха |
| `mute_attack` | шанс отключить способность моба |
| `nethersea_walker` | быстрее по клейму, меньше урона |
| `oceanospr_killer` | доп. урон порождениям моря |
| `synesthesia` | усиливает доп. атаку хитинового трезубца |
| `reflection` | урон и скорость стрел из лука из флоэмы |
| `metabolism` | лук из флоэмы без стрел |

### Команды/гамерулы
Оператор (уровень 2): `/caerula_arbor:sanity check|set|hurt|heal <цель> [n]`; `/caerula_arbor:evolution common|special …` (подъём/блок стратегий grow/breed/migration/subsisting и особых silence/sublimation); `/caerula_arbor:endspeaker inquiry|bestow|revoke|can_summon`; `/caerula_arbor:player_data <игрок> life_point|max_life_point|shield|lights|oceanization|rejection|collectibles|evolution|call_of_we_many …`.
Игровые правила (по умолчанию): `targetLifeFunction` true (целевые жизни), `naturalEvolution` true, `cloneNumberLimit` 24 (предел размножения), `spreadRate` 100 (скорость метки, %), `aggresiveMode` false, `defensiveMode` false, `oceanizationMode` true (заражать других мобов), `externalOceanization` true (мобы без океан-версии → первобытное порождение), `surgingWaves` 0 (сложность), `seabornSpawnRate` 40 (макс. 100), `naturalGainCallOfWeMany` true.

### Конфиги
Своих правок в `pack/config` нет. Книга Patchouli: `pack/kubejs/data/caerula_arbor/patchouli_books/caerula_illustration` (оригинал) + перевод `pack/resourcepacks-src/russian-moded/assets/caerula_arbor` (2791 строка). В Almost Unified изумруд Caerula Arbor не объединяется (ошибочные теги) — [compat.md](../compat.md), раздел «Объединение дублей». Источники Прыгучести (клеймо, кофе — `NetherseaBuffFunc`) описаны в [compat.md](../compat.md). Если мир тяжёл для группы — можно снизить `spreadRate` и `seabornSpawnRate` или выключить `naturalEvolution`.

## Contigency Contract Lite

<!-- auto:start card:contigency-contract-lite -->
<a id="contigency-contract-lite"></a>
- **Версия:** 1.3.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/contigency-contract-lite)
- **Что это:** Добровольные «контракты»: выбираете усложнения мобов и игрока, за прохождение получаете медали. _(источник: docs сборки)_
<!-- auto:end card:contigency-contract-lite -->

### Как играть
Мод Apocalypse (MCreator, Contingency Contract Lite 1.3.2): «контракты» — добровольные усложнения мира в духе режима Contingency Contract.
1. Нажмите клавишу «Open Tag Selector» (в сборке не назначена; есть ещё «Open Tag Helper») или используйте блок «Стол следа катастроф» (`catastrophy_trace_table`).
2. В «селекторе тегов» выберите «метки» (Tags): Zombie/Creeper/Illager/Piglin/Blaze/Ghast/Wither Skeleton/Guardian/Warden/Slime/Spider/Enderman +HP/атака/эффекты, «Игрок: макс. здоровье 15/10/…», «Ускоренный голод», «Задыхаетесь, если едите слишком быстро» и т. д. Выбор подтверждается один раз; после подтверждения в выживании менять нельзя до конца игры.
3. Сложность считается очками; выше 1120 — «Пик контракта». «Крисис-монеты» и **медали** (S, SS, SSS, Fullmark, Hero…) — награда; есть пластинки-«Record».
4. Усиленная сложность видна в HUD «Сложность:» (клавиша «Close Difficulty Showcase»).

### Предметы/блоки
Медали, «Монета кризиса», осколок записи, пластинки (Lingangu, Hit Ash), кукла Мику, голова кролика (достижение «Rabbit Beheaded»).

### Эффекты
`ban_gui`, `lack_of_nutrition` (вы едите одно и то же), `armor_less`, `blaze_tag`, `bonus_sign`, `suffocated` («съели слишком много»), `armor_add`, `anti_hunger`, `health_lessener`, `our_peak` («Наша вершина»: атака +330 %, здоровье до 1000 %), `fullfilled`.

### Данжи/структуры
Нет.

### Команды/гамерулы
Своих нет. Усиление мобов выполняет `ZbHbFuncProcedure` на `EntityJoinLevelEvent` (только усиливает) — см. [compat.md](../compat.md).

### Конфиги
Своих правок нет. Клавиши (`open_selector`, `open_helper`, `close_difficulty`) — в «Управлении».

## Endfield Industry

<!-- auto:start card:endfield-industry -->
<a id="endfield-industry"></a>
- **Версия:** 1.0.3-1.20.1-Forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/endfield-industry)
- **Что это:** Фабрика по мотивам Arknights: Endfield: электробуры на жилах, ленты, опоры, цепочки машин и хранилище. _(источник: docs сборки)_
- **В наших docs:** [version-choice.md](../version-choice.md) (1)
<!-- auto:end card:endfield-industry -->

### Как играть
Мод Beishan_Besson «Arknights: Endfield Industry» (1.0.3) — фабрика по мотивам игры Endfield; есть достижения (дерево с корнем `root`).
1. Найдите **жилы руд** на поверхности (структуры `*_ore`, 11 видов: уголь, медь, железо, золото, алмаз, изумруд, лазурит, редстоун, аметист, **ориджиниум**, **ферриум**; достижение «Найдите все руды»; `/locate structure arknights_endfield:<руда>_ore`).
2. Поставьте **электрическую буровую** (Mk I/II) на жилу, электрические опоры (`electric_pylon`) для сети, ленту, мосты, сплиттеры и конвейерные «сходилки».
3. Цепочки машин: дробилка/мельница, плавильня, формовка, посадка и сбор семян, упаковка, сборка («grinding / moulding / shredding / refining / planting / seed picking / packaging / gearing / fitting / filling unit»).
4. Хранилище «депо» (loader/unloader/bus section) и «якорь протокола» (`protocol_anchor_core_port`).

### Предметы/блоки
231 предмет и блок: 10 типов минеральных жил (`*_mineral_vein_block`), руды (`ferrium`, `cuprium`, `originium`, `amethyst`), башни (`beam_tower`, `grenade_tower`), контейнеры, трубы и насосы (`fluid_pipe`, `fluid_pump`, `fluid_tank`).

### Команды/гамерулы
`/storage deposit|withdraw <предмет> <кол-во>` — положить в хранилище «протокола»/достать из него; `/storage setcap <число>` — лимит хранилища (оператор).

### Конфиги
Своих правок нет. JEI-плагин есть (`arknights_endfield`, см. [jei.md](../jei.md)).

## Gathering Torches Become Sunlight

<!-- auto:start card:torchesbecomesunlight -->
<a id="torchesbecomesunlight"></a>
- **Версия:** 1.20.1-0.4.2 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/torchesbecomesunlight-0.5.0-hotfix2.jar`
- **Что это:** Сюжетное приключение с диалогами и напарниками: Урсус, Родос Айленд, Святой город, мега-структуры и боссы. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (3)
<!-- auto:end card:torchesbecomesunlight -->

### Как играть
Мод free fish (сюжетное приключение с диалогами, напарниками и кат-сценами; ~78 МБ, нужен Curios). Корневое достижение — «Факелы становятся солнцем».
1. Три сюжетные линии в дереве достижений: **«Снежные партизаны»** (главная: деревня Урсуса → «Кризис» → «Сбор налогов» → вступление в партизаны → финалы «Принцесса снега» / «Патриот»), **«Родос»** (кабинет Родос Айленда → временный контракт → «Хватка фантома» → доверие) и **«Святой город»** (Время и Свет → паломничество и испытание → молитва Санкты).
2. В диалогах: «Выбрать реплику» (колесо мыши) и «Пропустить» (удержание клавиши) — клавиши мода в категории «Факелы становятся солнцем»; по умолчанию в сборке не назначены.
3. **Напарники** (достижения «Компаньон»: Фростнова, Патриот, Розмонтис, Клинок императора, Ган-рыцарь): меню команд — клавиша «Открыть меню команд напарника».
4. Кулинария: доска для резки, печь, котёл (достижения «Начать готовить», «Терранская кухня!») — еда мода (борщ, пирог, бургер из бурденбиста и др.).
5. Пустынная ветка: отель Саргона → через игровые сутки пробуждается «Надгробие» → победа даёт «Возвращение в пустошь».

### Предметы/блоки
«Время» и «Свет» (ключевые предметы линии Святого города), «Тяжёлое роторное ружьё паломника», «Огонь и сталь», «Требование от насилия», «Осколочная граната», мачете инспектора, ржавая алебарда, полные военные щиты Урсуса, «Глаз демона», обледенелый талисман, «Благословение Сами», «Шлем из костяных рогов». Блоки: набор из «Святозвучной ивы» (брёвна, доски, двери, листва…), бетонные плиты/ступени/вертикальные плиты, тяжёлый железный сундук, лифт с контроллером, звёздные врата (контроллер/рама), «Биг-Бен», доски кулинарии. Служебные: контроллер сцены, слот роли, мега-пустота.

### Мобы
Живые NPC и боссы: **Патриот**, **Фростнова**, **Розмонтис** (на тактическом снаряжении «Вектор»), **Булдроккас'ти, Святой ган-рыцарь**, **«Клинок императора», Преследователь**, **Ред**, **«Надгробие»**, **Кресон Бесконечного резонанса** (в разработке), **Следопыт-прорыватель линии**, Военный фантом, Тактический защитник, Йети-ледокол, Павший снежный жрец, Сломленный чемпион; жители Урсуса, инспектор Урсуса, оператор резерва Родос; Бурденбист, Манглер, Осквернённая туша, дроны и турели. Статы — [bosses.md](../bosses.md).

### Эффекты
| id (`torchesbecomesunlight:`) | Что делает |
|---|---|
| `armor_piercing` | «Бронебойность»: броня носителя −50 % |
| `song_of_guerrilla` | «Песнь партизана»: урон +20 %, броня +20 % |
| `full_of_energy` | «Полон энергии»: снимает заморозку (ёмкость `FrozenCapability`) |
| `collapsal` | «Коллапсал»: служебная метка, своих атрибутов нет |

### События
Кат-сцены и сюжетные бои управляются «контроллерами сцен»; после поражения напарника/босса действует перезарядка появления (в конфиге: Розмонтис и Патриот/Фростнова 600 тиков, оператор резерва и охранники 12000). Если бой уже идёт, повторный вызов отклоняется сообщением «…всё ещё в бою».

### Данжи/структуры (/locate)
`/locate structure torchesbecomesunlight:<id>`:
| id | Биомы | Шаг |
|---|---|---|
| `ursus_village` | снежная равнина | spread 80/34 |
| `sankta_statue` | равнины, подсолнечные равнины | 180/100 |
| `sargon_hotel` | пустыня | 48/36 |
| `stargate` | биом `endless_snowfield` | фиксированно: чанк (1053, 862), около X 16848, Z 13792 |
Мега-структуры (большие постройки из `.mega`): `sankta_holy_city`, `plain_rodes_office`, `snow_rodes_office`, `snow_guerrilla_camp`; по коду мода есть команда `locate megastructure <id>` (оператор). Биомы мода: плоские равнины, «Равнина Святого Звука» и около 30 регионов Terra (Урсус, Колумбия, Сами…), а также измерение `terra_dimension`.

### Команды/гамерулы
Оператор (уровень 2), корень `/torchesbecomesunlight`: `partner <цель> <тип>`, `remove_partner <цель>`, `storyLine <да/нет>`, `checkfoodattribute`, `open_configmanager`. Отдельные корни: `/quest list|show|accept|claim|sync|reset <квест>`, `/scene types|select|init|activate|deactivate|start|end|list`, `/megastructure locate|queue|start|end|save|speed|files|status|cancel`, `/largestructure import|generate|pause|resume|cancel|list|structures|speed`, `/maphint timed|distance|look|remove|clear`, `/entity_sdata listStoryLine|setStoryPoint`, `/crazelyseon_fight init|remove|init_structure`. Игровых правил нет.

### Конфиги
`config/torchesbecomesunlight-common.toml` (секция `server`) создаётся при первом запуске; в сборке не менялся. Основное: `spawnDemon` (демоны, по умолчанию выкл.), блок `megaStructure` (генерировать ли каждую мега-структуру, свой биом и плоский рельеф вокруг, ограничение спавна мобов рядом), перезарядки появления NPC. Подписи опций в игре перепутаны автором (у всех — «Whether to generate demons»). Нагрузка: мега-структуры и их NBT большие — см. [performance.md](../performance.md).

## OriginiumArts

<!-- auto:start card:originiumarts -->
<a id="originiumarts"></a>
- **Версия:** 0.1.6-beta · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/originiumarts)
- **Что это:** Технологическая система Искусств Ориджиниума: руда и кристаллы, компилятор ядер, терминал травления и модули. _(источник: docs сборки)_
<!-- auto:end card:originiumarts -->

### Как играть
Мод azane, seymour (бета 0.1.6): **Искусства Ориджиниума** как технологическая прокачка.
1. Добывайте **инертную руду ориджиниума** (`iognm`), осколки (`ognm_shard`) и кристаллы; из эмбрионов кристаллов (малый/средний/большой, `aognm_*`) получают ядра.
2. Создайте **компилятор ядер (OCC)** и **терминал протокола травления (EPT)** — ими «выжигают» чипы (`ogna_chip_base`) с модулями искусств; **энергоузел** (`energy_eh`) и простые энергоблоки (`eu_simple`) питают их.
3. В игре 13 атрибутов мода и ~100 строк описаний модулей (`ogna.*`) и 2 клавиши (в сборке не назначены).

### Предметы/блоки
OCC, EPT, энергоузел, ориджиниумная руда/кристаллы, ориджиниум-железо (`oriron`), куб ориджирока.

### Конфиги
Своих правок нет. Зависимости (в `modlist.txt` добавлены вручную): LDLib. JEI-категории рецептов — свои (3 `jei.*`).

## Origins (Forge)

<!-- auto:start card:origins-forge -->
<a id="origins-forge"></a>
- **Версия:** 1.20.1-1.10.0.9 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/origins-forge)
- **Что это:** Основа для рас: происхождения, слои выбора и силы (Forge-порт Origins). _(источник: docs сборки)_
- **В наших docs:** [vampirism.md](../vampirism.md) (14), [compat.md](../compat.md) (13), [roadmap.md](../roadmap.md) (3), [performance.md](../performance.md) (2), [reset.md](../reset.md) (2), [dedup.md](../dedup.md) (1)
<!-- auto:end card:origins-forge -->

### Как играть
Основной мод-платформа «Origins» (Forge-порт 1.10.0.9): происхождения, слои выбора и силы (Apoli/Calio).
1. При первом входе откроется экран слоёв: Раса (`arks`), Класс и Подкласс (Arknights Skills), Искусство Ориджиниума.
2. «View Origin» — свой выбор; «Active Power (Primary/Secondary)» — способности (клавиши в сборке не назначены).
3. «Сфера происхождения» (`origins:orb_of_origin`) позволяет выбрать заново.

### Предметы/блоки
`origins:orb_of_origin` (выбор нового происхождения слоя).

### Команды/гамерулы
`/origin set <игроки> <слой> <происхождение>`, `/origin get|has|gui|random`, `/origin gui` — оператор. Слои: `origins:origin` (раса/происхождение), `arks:*` (расы), `arkskills:*`, `arkarts:*`.

### Конфиги
Своих правок нет. Если слои не открываются на сервере — проверьте, что у клиента та же версия Origins (экран «версия сервера отличается»).

## Pufferfish's Skills

<!-- auto:start card:skills -->
<a id="skills"></a>
- **Версия:** 0.19.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/skills)
- **Что это:** Деревья навыков Pufferfish: категории, очки, опыт и команды; сами деревья приносят другие моды. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:skills -->

### Как играть
Библиотека и мод Pufferfish: **деревья навыков** (0.19.2). Сами деревья задаются датапаками — в сборке их приносят *Arknights Skills* (по дереву на каждый класс, 45 навыков вроде «+10 % урона по воздуху», «пробивание брони», «скорость атаки») и аналогичные моды.
1. Откройте дерево клавишей «Open Skill Tree» (в сборке по умолчанию не назначена — «Настройки → Управление»).
2. Очки навыков идут за опыт/уровни категории; потратить их можно на узлы, соседние с уже открытыми. Стоимость, текущий уровень и прогресс видны во всплывающей подсказке.

### Команды/гамерулы
Оператор, корень `/puffish_skills`: `open`, `category open|lock|unlock|erase`, `experience add|set|get`, `level add|set|get`, `points add|set|get`, `skills unlock|lock|reset`.

### Конфиги
Своих правок нет. Библиотека `puffish_attributes` — отдельная; перевод — `pack/resourcepacks-src/russian-moded/assets/puffish_skills`.

## Pufferfish's Skills x Origins

<!-- auto:start card:skills-origins -->
<a id="skills-origins"></a>
- **Версия:** 0.2.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/skills-origins)
- **Что это:** Совместимость Pufferfish's Skills и Origins: сила «разблокировать категорию» и награда «выдать силу Origins». _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:skills-origins -->

### Как играть
Мост между Pufferfish's Skills и Origins (0.2.1) даёт две вещи: **силу Origins «разблокировать категорию»** (`UnlockCategoryPower`) и **награду навыка «выдать силу Origins»** (`PowerReward`). В сборке Arknights Skills создаёт 8 категорий деревьев (по классу, `unlocked_by_default = false`), и дерево открывается через силу выбранного класса. Отдельных действий от игрока не требует; управление деревом — как в [Pufferfish's Skills](#pufferfishs-skills).

### Конфиги
Своих правок нет.

## Sarkaz's Furnace Side Fables

<!-- auto:start card:sarkazs_furnace_side_fables -->
<a id="sarkazs_furnace_side_fables"></a>
- **Версия:** 1.0.0 · **Сторона:** both · **Источник:** jar в репозитории: `pack/mods/sarkazs_furnace_side_fables-0.2.3-forge-1.20.1.jar`
- **Что это:** Сюжет «печи» сарказов: мысли и их нагрузка, эпохи, Амия и измерение «Вечный покой», оружие и броня «Финал» и «Учжоу». _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (5), [friends.md](../friends.md) (1)
<!-- auto:end card:sarkazs_furnace_side_fables -->

### Как играть
Мод Zhi_Xu (Forge 1.20.1, нужны GeckoLib и Curios). Сюжет — «печь» сарказов: мысли, эпохи, Амия и «Вечный покой».
1. С первого входа в мир даётся достижение «Первая печь» (`furnace_beginning`) — это корень дерева достижений мода.
2. Скрафтите **Кодекс духа души** (`whimsical_chronicle`): книга + любое бревно + кожа + нить. Он показывает нагрузку мыслей, шкалы апоптоза/горения/коррозии и список коллекционных предметов; в нём же включаются/скрываются HUD и частицы.
3. **Мысли** (`thought_*`, ~50 штук: «Спасение», «Отчаяние», «Прекращение», «Замысел» и др.) хранятся не в инвентаре, а в **рюкзаке мыслей** — открывается клавишей «Открыть рюкзак мыслей» (по умолчанию в сборке не назначена). Там их можно раскладывать, делить и использовать; в обычный инвентарь они не кладутся. Падают мысли с мобов (шанс растёт от чар «Расхититель мыслей»).
4. Скрафтите **Материю духа душ** (из блока духа душ) и **Очищение истины** (`refine_truth`: души-песок ×3, кафедра, материя духа ×3). Присядьте и ПКМ материей/блоком, чтобы зарядить, затем «очищайте» пары мыслей: две «Замысла» не тратят уровней опыта, дают сферу опыта; «Последнее желание» + «Последнее желание» всегда даёт коллекционный предмет; «Вдохновение» + «Вдохновение» — щит; смешанные пары — шанс 1/10…1/2 на коллекционный предмет, иначе щит.
5. **Ковка** (`forging`) в основной руке + «Спасение», «Отчаяние» или «Прекращение» в левой даёт концовку и **Бесконечный странный ключ** (`endless_odd_key`). Зажмите ключ, чтобы войти в измерение «Вечный покой» (достижение «Конец -»); там вызывается босс **Амия, Финал пепла** (достижения «…Уже здесь», «Безупречный день»). Полный набор «Финал» (`finale_*`) даёт «Финал возрождённый».
6. Инструменты и броня «Духа душ», «Учжоу» и «Финал» делаются верстаком и кузнечным столом (рецепты `soul_spirit_*`, `wuzhou_*`, `finale_*`, шаблоны улучшения); есть интеграция с Tetra (улучшения «Непринятие финала» и «Импровизированная самозащита»).

**Бонус рас к пределу нагрузки.** Предел считается кодом мода: `18 + 3 × уровень чар «Высвобождение мыслей» (на броне) + значение атрибута sarkazs_furnace_side_fables:thought_load_limit` (броня «Финал», «Учжоу» и «Связанный теневой дух» добавляют к нему модификаторы). Силы рас `arks:{human,sarkaz,sarkaz_nephesh}/thought` изначально были заданы на чужой атрибут `the_massestravels:thought_load_limit` (закрытый мод The Masses Travels, в сборке его нет), поэтому бонус не работал. Решение владельца (В45 «а»): сборка переопределяет эти силы в `pack/kubejs/data/arks/powers` так, чтобы они давали `sarkazs_furnace_side_fables:thought_load_limit`. Итог по пределу: без расы и у прочих рас — 18; Человек и 10 подрас сарказов — 25 (+7); Нефеш — 35 (+17); базовая `arks:sarkaz` без подрасы — 18. Механика и последствия переполнения — [mind-storm.md](arknights/mind-storm.md); подробности реализации — в PR Потока 48 (#124).

### Предметы/блоки
- **Кодекс духа души**, **Очищение истины** (блок и предмет), **Бесконечный странный ключ**, **Ковка**, **Потеря и приобретение** (зарядка с коллекционным предметом в левой руке заменяет его случайным), **Мешок замысла** (3 мысли «замысла»), **Клятва Бабеля** (макс. здоровье/атака/броня +10 %, стойкость +2), **Особый ингибитор** (+2 макс. здоровья).
- Артефакты (Curios, слоты «Жажда бытия», «Продолжение», кодекс): **Продолжение истории** (Ночное зрение I, Сила II, Спешка II, Регенерация III, обновляется каждые 15 с), **Ореол** (враги-сарказ −30 % скорости), **Карта сокровищ** (броня +8 %), **Алтарь из булыжника** (с шансом 3 % за убийство +5 % к атаке и броне, до 10 стаков), «Реальность/Скелет/Тело финала» (враги получают −20 % урона, цена — +20 % входящего или замедление II).
- Оружие и броня: **Прекращение**/**Суйе** (лук; чары «Аннигиляция» — без расхода стрел)/«Пусковая установка предков» («Взрыв раннего утра»; урон союзникам — гейм-рул ниже), **Инсяо** (двойной магический удар; навык «Абсолютная тень» — 10 магических ударов по 220 %, последний — истинный), «Зрение милосердия» (лечение и перенаправление избытка союзникам), меч «Учжоу» (доп. апоптоз), набор «Связанный теневой дух».
- Блоки: Блок духа душ, «Кирпич грёз», «Небесный блок Латерано», «Всё или ничего».

### Мобы
- Естественно появляются в Обычном мире: **Сарказ-мечник** (`sarkaz_greatswordsman`), **Сарказ-арбалетчик** (`sarkaz_crossbowman`) — по 1 штуке (вес 1) во всех биомах Overworld.
- Боссы и особые: **Амия, Финал пепла** (`amiya`), **Куйлун, Воплощение махасаттвы** (`kuilong`), **Тошэн Ляньцзо** (`tuosheng_lianzuo`, есть «особый»), **Возвышение Сакоты**, «Остаточный образ Доминуса», **Сфокусированный остаток души**, «Изгнанный чёрный гроб», «Маяк минувших дней», «Последняя крупица творчества». Статы — [bosses.md](../bosses.md).
- Теги мобов (`world_difficulty_*`, `sarkaz`, `soul_spirits`, `narrators`, `dark_bosses`) задают, кого затрагивают «эпохи» и «Встреча с душами».

### Эффекты
| id (`sarkazs_furnace_side_fables:`) | Что делает |
|---|---|
| `thought_load` | «Мысленная буря»: нагрузка мыслей достигла предела (по умолчанию 18). Разбор — [mind-storm.md](arknights/mind-storm.md) |
| `thought_load_blocked` | «Мысли заблокированы»: нагрузка ≥ 1,5 × предел; скорость передвижения −100 % (ступор) |
| `dreaming` | «Сновидение»: скорость −50 %, дальность рук и атаки −1,5 блока |
| `empathy_sorrow` | «Сопереживание-скорбь»: скорость −100 % |
| `apoptosis_burst` / `_immunity` / `_exhaustion`; `combustion_*`; `corrosion_*` | Три «тёмных» урона (апоптоз, горение, коррозия): накопление → вспышка → иммунитет/истощение. Защита — чары и атрибуты `*_resistance` |
| `cinder_finale`, `finale_chapter`, `finale_critical` | Состояния боя с Амией |
| `yingxiao_absolute` | «Абсолютная тень» меча Инсяо |
| `mercy_vision`, `prayer_invitation`, `inheritance`, `the_return`, `glorious_kazimierz`, `tipsy`, `scream_cherry_power`, `molten_bread_armor` | Эффекты предметов/еды и артефактов |

### События
Эпохи (команда `era`, гейм-рул `EraEnabled`): случайно накладываются на игрока, чем выше нагрузка мыслей, тем чаще (шанс при проверке 15 → 25 → 35 %). У эпохи три стадии — «Формирование», «Расширение», «Пик». Эпохи: Бедствий, Короля демонов, Страданий, Финансовая, Чудес, Теснота, Философии (в ней нельзя расшифровывать), Процветания, Парадокс, Все эпохи.
«Встреча с душами» (`world_difficulty`, 0…18) — мировой уровень сложности для сарказов.

### Данжи/структуры (/locate)
Все — Overworld, `/locate structure sarkazs_furnace_side_fables:<id>`:
| id | Где | Шаг/интервал |
|---|---|---|
| `place_in_memory` | поверхность: равнины, луга, леса, берёзовые леса | spread 96/64 |
| `small_furnace_station` | поверхность, Overworld и Нижний мир, кроме океанов и рек | 128/96 |
| `all_or_nothing` | под землёй, Overworld без океанов/рек (сундуки `all_or_noth_*`) | 128/96 |
| `buried_warehouse` | под землёй, глубина −48…−20 (сундуки обычный/особый) | 128/96 |
Измерение — `sarkazs_furnace_side_fables:endless_rest` («Вечный покой», плоский мир; нет кроватей, жидкости не держатся). Попасть: Бесконечный странный ключ или `/execute in sarkazs_furnace_side_fables:endless_rest run tp @s 0 100 0`.

### Чары
| id | Действие |
|---|---|
| `apoptosis`, `combustion`, `corrosion` | атака наносит доп. урон апоптозом/горением/коррозией от силы атаки |
| `apoptosis_protection`, `combustion_protection`, `corrosion_protection`, `magic_protection` | сопротивление соответствующему урону |
| `true_reduction` | снижает истинный урон |
| `armor_breaker`, `magic_penetration` | игнорируют часть брони / магической стойкости цели |
| `spell_tempering` | доп. магический урон |
| `thought_plunderer` | выше шанс выпадения мыслей |
| `thought_release` | +3 к пределу нагрузки мыслей за уровень (на броне) |
| `annihilation` | лук Суйе не тратит стрелы |

### Команды/гамерулы
Права — оператор (уровень 2). Корень `/sarkazs_furnace_side_fables`:
- `era enter <игроки> <эпоха> [стадия]` / `era enter <игроки> all <стадия> <длительность>|infinite` / `era clear <игроки>` / `era query <игрок>`
- `world_difficulty get|set <0..18>|add <n>|remove <n>`
- `ride_silent <всадник> <транспорт>`, `refresh yingxiao|mercy_vision <игроки>`, `beacon_of_yesteryear <цели>`, `move creativity`
Игровые правила: `EraEnabled` (включить эпохи, по умолчанию true), `LianKillCreative` (урон Лиан в творческом режиме, true), `AncestorLauncherFriendlyFire` (пусковая установка предков бьёт союзников, false).

### Конфиги
Своих файлов в `pack/config` нет — настройки по умолчанию. Из наших правок: оверрайд сил рас `thought.json` (см. «Бонус рас к пределу нагрузки») и `pack/kubejs/server_scripts/missing_mod_refs.js` (убирает из тегов предметы отсутствующих модов). Перевод — `pack/resourcepacks-src/russian-moded/assets/sarkazs_furnace_side_fables`.

<!-- cards:end -->
