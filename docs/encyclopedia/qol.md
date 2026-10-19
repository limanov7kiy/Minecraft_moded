# Удобство, хранилища и строительство

Рюкзаки, вейстоуны, гробницы, сундуки, WorldEdit, декор и мелкие улучшения.

<!-- auto:start toc -->
Модов в категории: **13**.

- [Amendments](#amendments)
- [FallingTree](#fallingtree)
- [GraveStone Mod](#gravestone-mod)
- [Gravestone x Curios API Compat](#gravestone-x-curios-api-compat)
- [Iron Chests](#iron-chests)
- [More Backpack Upgrades](#more-backpack-upgrades)
- [Quark](#quark)
- [Sol's AuthLogin](#sols-authlogin)
- [Sophisticated Backpacks](#sophisticated-backpacks)
- [Sophisticated Backpacks Create Integration](#sophisticated-backpacks-create-integration)
- [Supplementaries](#supplementaries)
- [Waystones](#waystones)
- [WorldEdit](#worldedit)

Вернуться: [оглавление справочника](INDEX.md).
<!-- auto:end toc -->

## Общее по категории

Хранилища, перемещение и сервис для друзей: **Sophisticated Backpacks** (рюкзаки с модулями) и **Iron Chests** (сундуки), **Waystones** (телепорты),
**Gravestone** (могила со всеми вещами), **Falling Tree** (дерево падает целиком), **Quark**, **Supplementaries** и **Amendments** (мелкие блоки и предметы) и
серверная авторизация **Sol's AuthLogin** (`/register`, `/login`). WorldEdit в сборке по умолчанию выключен. Практика игры — [friends.md](../friends.md),
рецепты — JEI ([jei.md](../jei.md)), хостинг и необязательные моды — [hosting.md](../hosting.md).

---

<!-- cards:start -->
## Amendments

<!-- auto:start card:amendments -->
<a id="amendments"></a>
- **Версия:** 1.20-2.2.6 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/amendments)
- **Что это:** Много изменений ванильных блоков (от автора Supplementaries). _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:amendments -->

### Что даёт игроку

Мелкие улучшения ванили и Supplementaries (Moonlight Lib):
- **Котёл**: жидкостный/красильный (`liquid_cauldron`, `dye_cauldron`), смешивание зелий («Mixed Potion»), бутылка красителя (`dye_bottle`, много цветов), заряд дракона (`dragon_charge`) для драконьего огня.
- Блоки: ковровые ступени/плиты (`carpet_stairs`, `carpet_slab`), `wall_lantern` (фонарь на стене), `hanging_flower_pot`, `tool_hook`, `skull_pile`, `double_cake`, `directional_cake`.

## FallingTree

<!-- auto:start card:fallingtree -->
<a id="fallingtree"></a>
- **Версия:** 1.20.1-4.3.4 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/fallingtree)
- **Что это:** Срубает дерево целиком, достаточно одного бревна. _(источник: docs сборки)_
- **В сборке:** both: 1.20.1-4.3.4; дерево падает целиком (FALL_BLOCK), присед = один блок; «всё или ничего» по прочности — миксин FallingTreeDamageMixin в mods-src/mm-compat; конфиг pack/config/fallingtree.json (docs/compat.md)
- **В наших docs:** [compat.md](../compat.md) (6), [roadmap.md](../roadmap.md) (4)
<!-- auto:end card:fallingtree -->

### Как играть

Срубленное дерево **падает целиком**: удар топором по бревну валит дерево. Присед — рубка по одному блоку (`sneakMode = SNEAK_DISABLE`).
- В сборке: режим `breakMode = FALL_BLOCK` (бревна падают блоками), `detectionMode = WHOLE_TREE`, `maxSize = 300` (дерево больше 300 блоков рубится по одному), `maxScanSize = 500`, прочность топора тратится по 1 за бревно (`tools.damageMultiplicand = 1.0`), `preserve = true` — топор не ломается на последнем бревне (наш миксин в `mm-compat` рубит столько, на сколько хватает прочности).
- Чары `fallingtree:chopper*` в сборке **не зарегистрированы** (`enchantment.registerEnchant = false`).

### Конфиги

`pack/config/fallingtree.json` (всё прочее — умолчания мода). Подробности и результаты теста — [compat.md](../compat.md), раздел «Falling Tree».

## GraveStone Mod

<!-- auto:start card:gravestone-mod -->
<a id="gravestone-mod"></a>
- **Версия:** forge-1.20.1-1.0.35 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/gravestone-mod)
- **Что это:** При смерти ставит могилу с вещами. _(источник: docs сборки)_
<!-- auto:end card:gravestone-mod -->

### Как играть

При смерти игрока на месте появляется **могила** (`gravestone:gravestone`) с его вещами; в моде есть сущность «призрак игрока» (`gravestone:player_ghost`). На могиле видно «Grave of …», дату смерти и число стаков.
- **Некролог** (`gravestone:obituary`) — книга со списком смертей: имя, координаты, измерение, время, содержимое, ID смерти (можно скопировать).
- Если могилу поставить нельзя, мод пишет «Grave could not be placed»; вещи можно вернуть командой `/restore`.
- Аксессуары Curios попадают в могилу через [Gravestone x Curios API Compat](#gravestone-x-curios-api-compat); для Blue Skies есть отдельная связка (миксин `SkiesPlayerMixin`, [compat.md](../compat.md)).

### Команды/гамерулы

`/restore` и `/restoreinventory` (оператор): вернуть вещи по ID смерти (`death_id`) — режим `replace` (заменить инвентарь тем, что было перед смертью) или `add` (добавить к текущему). ID виден в некрологе; точный синтаксис покажет автодополнение.

## Gravestone x Curios API Compat

<!-- auto:start card:gravestone-x-curios-api-compat -->
<a id="gravestone-x-curios-api-compat"></a>
- **Версия:** 4.0.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/gravestone-x-curios-api-compat)
- **Что это:** Позволяет сразу надевать вещи Curios из могилы в нужные слоты. _(источник: docs сборки)_
- **В сборке:** both (+ baguettelib both — его зависимость): аксессуары Curios попадают в гроб, в одиночке тоже
- **В наших docs:** [compat.md](../compat.md) (2)
<!-- auto:end card:gravestone-x-curios-api-compat -->

### Что даёт игроку

Кладёт предметы из слотов Curios (аксессуары, кольца и т. п.) в могилу Gravestone при смерти; без него в одиночной игре аксессуары терялись ([compat.md](../compat.md), таблица side=server и одиночка). Нужны Gravestone, Curios и BaguetteLib.

## Iron Chests

<!-- auto:start card:iron-chests -->
<a id="iron-chests"></a>
- **Версия:** 1.20.1-14.4.4 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/iron-chests)
- **Что это:** Сундуки из разных металлов с разной вместимостью. _(источник: docs сборки)_
- **В сборке:** Iron Chests 14.4.4 (alexbegt, GPL-3.0, both): сундуки медь/железо/серебро/золото/алмаз/кристалл/обсидиан + улучшения; ru_ru в jar полный
- **В наших docs:** [compat.md](../compat.md) (4), [roadmap.md](../roadmap.md) (2)
<!-- auto:end card:iron-chests -->

### Что даёт игроку

Сундуки с большей вместимостью: `copper_chest`, `iron_chest`, `gold_chest`, `diamond_chest`, `crystal_chest` (видно содержимое), `obsidian_chest` (взрывоустойчив), шуточный `dirt_chest` («DirtChest 9000!»); у каждого есть ловушечный вариант (`trapped_*`). Апгрейды (`wood_to_copper_chest_upgrade`, `copper_to_iron_chest_upgrade`, `iron_to_gold_chest_upgrade`, `gold_to_diamond_chest_upgrade`, `diamond_to_crystal_chest_upgrade`, `diamond_to_obsidian_chest_upgrade`, `wood_to_iron_chest_upgrade`) повышают сундук без потери содержимого.

## More Backpack Upgrades

<!-- auto:start card:more-backpack-upgrades -->
<a id="more-backpack-upgrades"></a>
- **Версия:** 1.0.2 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/more-backpack-upgrades)
- **Что это:** Аддон Sophisticated Backpacks: связь рюкзака с сетью Refined Storage. _(источник: docs сборки)_
- **В сборке:** требует Refined Storage (одобрено пользователем)
- **В наших docs:** [compat.md](../compat.md) (2), [roadmap.md](../roadmap.md) (2)
<!-- auto:end card:more-backpack-upgrades -->

### Что даёт игроку

Три улучшения для Sophisticated Backpacks под сеть Refined Storage: `mobackup:dimensional_magnet_upgrade` (магнит подтягивает предметы прямо в привязанную RS-сеть), `dimensional_pickup_upgrade` (подобранное сразу уходит в сеть), `dimensional_deposit_upgrade` (ПКМ по контейнеру с приседанием — сложить предметы в сеть или контейнер).

## Quark

<!-- auto:start card:quark -->
<a id="quark"></a>
- **Версия:** 1.20.1-4.0-462 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/quark)
- **Что это:** Сборник мелких улучшений ванили: блоки, механики, интерфейс. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:quark -->

### Как играть

Большой набор мелких улучшений «ванильного стиля» (модули по категориям: Automation, Building, Management, Tools, Tweaks, World, Mobs, Client, Oddities, Experimental). Конфиг Quark в сборке не меняли — действуют умолчания; отдельные модули выключаются в игровом меню конфигурации.
- **Блоки и стройматериалы** — сотни новых блоков (в локали 800+ записей): варианты камня и дерева, вертикальные плиты и т. п.
- **Хранение**: Рюкзак (`quark:backpack`), мешочек для семян (`seed_pouch`), Тотем хранения (`totem`, «Totem of Holding»).
- **Инструменты**: Лопатка-кельма (`trowel`), Пикаранг (`pickarang`), Огненный бумеранг (`flamerang`), Счёты (`abacus`).
- Подробности и список рецептов — через JEI ([jei.md](../jei.md)).

### Предметы/блоки

| id | Что делает |
|---|---|
| `quark:pickarang`, `flamerang` | Бумеранг-кирка (ломает блоки на лету) и огненная версия |
| `quark:trowel` | Кельма: ставит случайный блок из панели быстрого доступа |
| `quark:backpack`, `seed_pouch`, `totem` | Рюкзак, мешочек для семян, Тотем хранения (вещи при смерти) |
| `quark:bottled_cloud`, `dragon_scale`, `diamond_heart`, `ancient_tome` | Облако в бутылке, чешуя дракона, сердце-алмаз, древний том (чары) |
| `quark:glass_item_frame`, `glowing_glass_item_frame`, `dyed_item_frame` | Стеклянные рамки |
| `quark:slime_in_a_bucket`, `crab_bucket`, `crab_leg`, `cooked_crab_leg`, `crab_shell` | Слизень и краб в ведре, ножки краба |
| `quark:forgotten_hat`, `soul_bead`, `moss_paste`, `ravager_hide` | Шляпа Забытого, бусина души, мшистая паста, шкура разорителя |

### Мобы

Сущности Quark (`quark:` + id): `crab` (краб), `foxhound` (лис-гончая в Незере), `shiba` (сиба-ину), `stoneling` (каменный человечек), `toretoise` (Оретоис — «рудная черепаха»), `wraith` (призрак), `forgotten` (Забытый — скелет из данжей), `glass_frame`, `dyed_item_frame`, `totem`, `soul_bead`, `gravisand` (парящий гравийный песок), `pickarang`, лодки (`quark_boat`, `quark_chest_boat`).

### Эффекты

`quark:resilience` — «Стойкость».

### Данжи/структуры (/locate)

Шаблоны Большого данжа лежат в `data/quark/structures/big_dungeon`; биом `quark:glimmering_weald` («Мерцающая чаща»). Структуры Quark генерируются кодом мода, id для `/locate` в jar не заданы — не проверялось.

## Sol's AuthLogin

<!-- auto:start card:sols-authlogin -->
<a id="sols-authlogin"></a>
- **Версия:** 1.20.1-v1.3.1 · **Сторона:** server · **Источник:** [Modrinth](https://modrinth.com/mod/sols-authlogin)
- **Что это:** Серверная авторизация: регистрация и вход для серверов с доп. защитой аккаунтов. _(источник: docs сборки)_
- **В сборке:** server: пароль при входе (/register, /login) — офлайн-режим, защита ников; на клиенте не нужен и вреден (свои сетевые каналы и экран логина; в одиночке пароль не нужен) — остаётся server
- **В наших docs:** [compat.md](../compat.md) (3), [roadmap.md](../roadmap.md) (3), [hosting.md](../hosting.md) (2), [friends.md](../friends.md) (1)
<!-- auto:end card:sols-authlogin -->

### Как играть

Серверная авторизация паролем (только сервер; в клиентскую сборку не входит): пока игрок не вошёл, он заморожен. Нужна, потому что сервер в офлайн-режиме и ник можно подделать ([hosting.md](../hosting.md), «Подмена ника»).
1. Первый вход: `/register <пароль> <пароль ещё раз>`.
2. Дальше при каждом входе: `/login <пароль>`; сменить — `/change_password <старый> <новый> <новый>` (инструкция для друзей — [friends.md](../friends.md)).
3. После `/login` игрок возвращается на место, где зашёл (`teleportBackAfterLogin`); есть лимит попыток и временная блокировка.

### Команды/гамерулы

Игрок: `/register`, `/login`, `/change_password`. Админ: ветка `/auth_admin` (`forcelogin`, `forcelogout`, `info`, `list`, `reload`, `resetpassword`, `unregister`) — забытый пароль сбрасывает оператор.

### Конфиги

`pack/config/solsauthlogin-common.toml`: `loginTimeoutSeconds = 180`, `gracePeriodSeconds = 5`, `sessionCacheMinutes = 30`; `maxLoginAttempts = 5`, `lockoutTimeSeconds = 300`, `minPasswordLength = 6`/`maxPasswordLength = 32`, `kickOnLockout = true`, `preventMultipleLogins = true`, `requireClientMod = false`, `enableInSingleplayer = false`; телепорт к спауну до входа (`teleportToSpawn`, `useWorldSpawn`).

## Sophisticated Backpacks

<!-- auto:start card:sophisticated-backpacks -->
<a id="sophisticated-backpacks"></a>
- **Версия:** 1.20.1-3.26.9.2194 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/sophisticated-backpacks)
- **Что это:** Рюкзаки, которые можно ставить в мире, красить, расширять и улучшать множеством апгрейдов. _(источник: docs сборки)_
- **В наших docs:** [client-checklist.md](../client-checklist.md) (1), [compat.md](../compat.md) (1), [friends.md](../friends.md) (1)
<!-- auto:end card:sophisticated-backpacks -->

### Как играть

Рюкзаки с модулями. Клавиша **Open Backpack** назначается в «Управление» → Sophisticated Backpacks.
1. Рюкзаки по ступеням: `backpack` (кожаный/базовый), `copper_backpack`, `iron_backpack`, `gold_backpack`, `diamond_backpack`, `netherite_backpack` — больше слотов и модулей на каждой ступени; рюкзак можно поставить блоком.
2. Улучшения вставляются в слоты и включаются клавишами «Switch Upgrade in the Nth Slot On/Off» (1–5).
3. Из полезного: `pickup_upgrade`, `magnet_upgrade` (притягивает предметы), `feeding_upgrade` (сам кормит из рюкзака), `restock_upgrade`/`deposit_upgrade` (пополнить рюкзак из сундука / разложить в сундук приседая ПКМ — клавиша «Run Inventory Interaction Upgrades»), `compacting_upgrade`, `void_upgrade`, `crafting_upgrade`, `smelting_upgrade`, `jukebox_upgrade`, `stack_upgrade_tier_1…4` и `stack_upgrade_omega_tier` (умножают размер стаков), `tank_upgrade`, `battery_upgrade`, `pump_upgrade`, `xp_pump_upgrade`, `tool_swapper_upgrade`, `anvil_upgrade`, `smithing_upgrade`, `mob_catcher_upgrade`, `everlasting_upgrade`, `inception_upgrade` и др.
4. Связки: [Sophisticated Backpacks Create Integration](#sophisticated-backpacks-create-integration), [More Backpack Upgrades](#more-backpack-upgrades).

### Команды/гамерулы

Команда `/sbp` (оператор, по классам и `lang`): список рюкзаков (`list`), `give`, удаление «ничьих» рюкзаков, шаблоны (`template`) — выдать/удалить.

## Sophisticated Backpacks Create Integration

<!-- auto:start card:sophisticated-backpacks-create-integration -->
<a id="sophisticated-backpacks-create-integration"></a>
- **Версия:** 1.20.1-0.1.12.187 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/sophisticated-backpacks-create-integration)
- **Что это:** Рюкзаки Sophisticated на механизмах Create. _(источник: docs сборки)_
- **В наших docs:** [compat.md](../compat.md) (1)
<!-- auto:end card:sophisticated-backpacks-create-integration -->

### Что даёт игроку

Рюкзаки Sophisticated Backpacks работают на **механизмах Create** (на движущихся контрапциях, по описанию мода: «Full featured Sophisticated Backpacks on Create contraptions»). Своих предметов нет.

## Supplementaries

<!-- auto:start card:supplementaries -->
<a id="supplementaries"></a>
- **Версия:** 1.20-3.1.43-forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/supplementaries)
- **Что это:** Расширение ванили: банки, указатели, краны, флюгеры, пусковые пружины, подсвечники, кашпо, освещение, декор и автоматизация. _(источник: docs сборки)_
- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).
<!-- auto:end card:supplementaries -->

### Как играть

Сотни мелких предметов и блоков «вокруг дома и в путешествии» (авторы — mehvahdjukaar). Всё ищется в JEI ([jei.md](../jei.md)); в мире встречаются Указатели-«Way Sign» (структура `supplementaries:way_sign`) и красный торговец (`red_merchant`).
1. **Для дома**: фонари (`brass_lantern`, `crimson_lantern`), подсвечники (`candle_holder_*`, `candelabra`), `blackboard`, `bellows`, `cage`, `book_pile`, `hat_stand`, `sign_post`, `way_sign`, часы (`clock_block`), `cog_block`, `crank`.
2. **Инструменты**: `slingshot` (рогатка; чара `stasis` — без гравитации), `quiver` (колчан), `wrench` (гаечный ключ), `rope_arrow`, `bubble_blower`, `flute`, `lunch_basket`, `altimeter`, `speedometer`, `cartographers_quill`.
3. **Взрывчатка и огонь**: `bomb`, `bomb_blue`, `bomb_spiky`, `cannon` + `cannonball`, лодки с пушкой (`cannon_boat`), Lumisene (`lumisene`, `lumisene_bucket`) и чары `lumisene_fire_aspect`, `lumisene_flame`.
4. Прочее: `soap`, `key`, `candy`, `confetti_popper`, пластинка `heave_ho`.

### Эффекты

`supplementaries:overencumbered` (перегруз: нельзя бегать, дальше меньше прыжок и скорость), `supplementaries:flammable` (дольше горите и поджигаете землю при ходьбе).

### Чары

`supplementaries:stasis` (рогатка), `lumisene_fire_aspect`, `lumisene_flame`.

### Данжи/структуры (/locate)

`/locate structure supplementaries:way_sign`.

### Команды/гамерулы

`/supplementaries registry <реестр>` — служебный просмотр записей реестра (по `lang`); отдельная команда меток карты (`MapMarkerCommand`).

## Waystones

<!-- auto:start card:waystones -->
<a id="waystones"></a>
- **Версия:** 14.1.21+forge-1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/waystones)
- **Что это:** Телепорт между вейстоунами и магические свитки возврата. _(источник: docs сборки)_
<!-- auto:end card:waystones -->

### Как играть

Телепортация между «путевыми камнями».
1. Найдите **Waystone** (генерируется в мире и в деревнях; варианты под биом: `mossy_waystone`, `sandy_waystone`, `blackstone_waystone`, `deepslate_waystone`, `end_stone_waystone`) и коснитесь его — он **активируется** и попадает в ваш список.
2. Правый клик по активированному путевому камню — меню телепорта на любой из ваших открытых камней (стоимость — опыт: «Not enough experience! (N levels needed)»; есть перезарядка).
3. **Sharestone** (16 цветов): телепорт на любой другой Sharestone **того же цвета** (бесцветный — на любой бесцветный). **Portstone** — место, откуда можно телепортироваться, но нельзя прийти.
4. Свитки и камни: `warp_scroll` (свиток телепорта), `bound_scroll`/`warp_scroll_bound` (привязан к камню), `return_scroll`, `warp_stone`, `warp_dust`, `attuned_shard` и блок `warp_plate` (плита, телепортирует на подключённую плиту).
5. Наш мод «Xaero's Minimap & World Map — Waystones Compatibility» показывает камни на карте ([compat.md](../compat.md)).

### Команды/гамерулы

`/waystones` (оператор): список камней игрока (`list`), `activate`, `forget` (деактивировать у игрока), `cooldown reset` (сбросить перезарядки), `count`.

### Структуры

Путевые камни — части деревень (шаблоны `waystones:village/common/waystone`, `village/desert/waystone`) и отдельные фичи в мире (`worldgen/placed_feature/*waystone`); `/locate structure` для них не нужен — ищите по картам и деревням.

### Конфиги

Своего конфига в `pack/config` нет — действуют настройки мода по умолчанию (опыт, перезарядка, ограничения).

## WorldEdit

<!-- auto:start card:worldedit -->
<a id="worldedit"></a>
- **Версия:** 7.2.15 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/worldedit)
- **Что это:** Редактор карты прямо в игре: выделения, схематики, копирование, кисти, скрипты. _(источник: docs сборки)_
- **В сборке:** WorldEdit 7.2.15 (Forge 1.20.1): перенос построек схематиками, см. docs/friends.md
- **В наших docs:** [compat.md](../compat.md) (58), [friends.md](../friends.md) (24), [roadmap.md](../roadmap.md) (24), [hosting.md](../hosting.md) (10), [issue-history.md](../issue-history.md) (8), [orespawn.md](../orespawn.md) (4)
<!-- auto:end card:worldedit -->

### Как играть

Редактор мира для операторов (wand `//wand`, `//pos1`, `//set` и т. д.). **В сборке выключен по умолчанию**: лежит в `mods/optional/` (Forge вложенные папки не читает) — на старте он делает один тик дольше 60 с и держит около 2,7 ГБ кучи. Включить на время: перенести `worldedit-mod-7.2.15.jar` в `mods/`, перезапустить; сторож тиков `max-tick-time` по умолчанию выключен (`-1`) в серверном пакете. Подробно — [hosting.md](../hosting.md) («Необязательные моды», «Сторож тиков») и [friends.md](../friends.md) («Перенос построек»); тест — [compat.md](../compat.md).

<!-- cards:end -->
