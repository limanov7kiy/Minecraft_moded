# OreSpawn: Chaos Persists против оригинала 1.7.10

Решение владельца В16 (раунд 8, поток 23): «брать самую проработанную версию, скорее всего 1.7.10», «посмотрим на этот перенос, чего не хватает, сравним и доделаем».
Этап 1 — поставить ремейк, проверить совместимость и сравнить с оригиналом. **Сам порт недостающего не начат**: владелец выбирает по списку в конце.

## Что поставлено

**Chaos Persists 1.20.1-0.9.1.** Автор — Astryxion, CurseForge project 1460238, файл 9130290, вышел 11.10.2026. Лицензия All Rights Reserved. На странице мода написано, что его можно включать в сборки без разрешения автора.
- Jar лежит в `pack/mods/chaospersists-1.20.1-0.9.1.jar`, в `modlist.txt` — строка `#jar`, как у остальных модов с CurseForge.
- Сторона **both**.
- Обязательных зависимостей нет. Связки, которые мод объявляет необязательными и которые есть у нас: `cataclysm`, `iceandfire`, `torchesbecomesunlight`, `jade`, `jei`.
- Исходники ремейка: github.com/astryxion/Chaos-Persists, задачи — github.com/astryxion23/Chaos-Persists/issues.

**AttributeFix 21.0.5** (Darkhax, LGPL-2.1, Modrinth, both). Без него здоровье боссов ремейка обрезается до 1024: ваниль ограничивает атрибут `generic.max_health` значением 1024. Собственный обход ремейка в нашей сборке падает — в логе `ChaosPersists: failed to raise generic.maxHealth cap; boss HP may stay capped at 1024`. Из-за этого The King (7000), The Queen (6000), Mobzilla (4000) и Kraken (1000 — у него запас есть) получились бы заметно слабее.

**Откуда сведения об оригинале:**
- декомпилированный OreSpawn 1.7.10.20.3 (github.com/mglucas0123/OreSpawn-1.7.10): `OreSpawnMain.java` — названия, `GenericDungeon.java` и `OreSpawnWorld.java` — структуры, `ChunkProvider*` — измерения;
- зеркало orespawn.com (github.com/ShrekLeaker/orespawn.com): описания, HP, где спавнится;
- вики orespawn.fandom.com закрыта Cloudflare (402/403), её не читали.

**Откуда сведения о ремейке:** классы, ассеты, `lang/en_us.json`, датапак jar (измерения, биомы, структуры, biome_modifier), конфиг `config/chaospersists.cfg`, описание на CurseForge и открытые задачи на GitHub.

## Главный вывод

**Chaos Persists — не «по мотивам», а почти построчный перенос кода OreSpawn 1.7.10 на 1.20.1.**
- В jar сохранились классы оригинала под теми же именами: `Godzilla`, `PitchBlack`, `GiantRobot`, `GenericDungeon` с теми же методами `makeEnormousCastleQ`, `makerobotower` и т. д.
- Есть слой-переходник `legacy/forge/...` (GameRegistry, EntityRegistry) для старого API.
- Числа в конфиге совпадают с оригиналом («Restore OreSpawn defaults»).

**Сверка по названиям** (все 678 строк `en_US` из `OreSpawnMain.java` против `lang/en_us.json` ремейка): **совпало 676**. Не хватает только яйца-руды и клетки **Zombie Pigman** — в 1.16 этого моба заменил Zombified Piglin.

**Структуры:** все 54 генератора `GenericDungeon` оригинала есть в ремейке, включая роботов Village Dimension (башня, алтарь, железная дорога, сборочная линия, сокровищница) и Queen's Challenge Dungeon (`makeEnormousCastleQ`).

**Измерения:** все 6, входы те же (муравьи и бабочка).
- Danger/Islands устроено так же, как в оригинале: `ChunkProviderOreSpawn4` — бедрок + 6 слоёв земли + трава, а парящие острова — это сущности `Island` / `IslandToo`.
- Chaos — острова над пустотой.

То есть «чего не хватает» — это почти не контент, а **качество переноса** (баги, баланс) и **стыковка с нашей сборкой**.

## Совместимость с нашей сборкой

| Что | Что нашли | Что делать |
|---|---|---|
| **Здоровье боссов** | Обход ремейка падает, и HP боссов было бы урезано до 1024 | Поставлен AttributeFix: потолок 1 000 000 (см. «Прогоны»); HP в бою проверить |
| **Рецепты** | В jar **нет** `data/chaospersists/recipes`: все рецепты (Ultimate, Big Bertha, броня и т. д.) создаются в коде и вставляются в `RecipeManager` после каждой перезагрузки датапаков (`LegacyRecipeInjector`) | **KubeJS и Almost Unified их не видят и поменять не могут.** Если править рецепт — только своим модом (событие после инъекции) |
| **Металлы-дубли:** Uranium, Titanium, Salt, Ruby, Amethyst | У ремейка свои `chaospersists:ingoturanium`, `ingottitanium`, `salt`, `ruby`, `amethyst`; они **не стоят в тегах** `forge:ingots/*`, `forge:dusts/salt` и т. п. У нас уже есть уран (Mekanism, Alex's Caves), титан (Northstar), соль (Mekanism, Northstar) | **Не объединять.** Если повесить теги, Almost Unified (в `unify.json` есть `uranium`, `ruby`) спрячет предметы ремейка, а рецепты из кода (Ultimate-набор) требуют именно их — крафт сломается. Сейчас это просто отдельные руды в Overworld. Проверка `scripts/hardrefs.py` не нужна: рецептов-JSON у мода нет |
| **Utopia в `#minecraft:is_overworld`** | Ремейк добавляет `chaospersists:utopia` в теги `is_overworld` и `is_plains`. Всё, что другие моды спавнят и генерируют «во всём Overworld» (Lycanites, Alex's Mobs, руды, деревни по тегу `is_plains`), попадёт и в Utopia. В оригинале Utopia мирная: враждебные мобы только под землёй | Убрать биом из тегов скриптом KubeJS (`ServerEvents.tags('worldgen/biome', e => e.remove('minecraft:is_overworld', 'chaospersists:utopia'))`) — сначала посмотреть в игре |
| **Одинаковые имена мобов** (это разные существа) | Butterfly (Mahou Tsukai), Crab (Quark), Firefly (Blue Skies), Gazelle (Alex's Mobs), Ghost (Ice and Fire), **Triffid (Lycanites)** | Ничего не ломается. В JEI и Jade видно, чей моб |
| **Похожие по смыслу с Ice and Fire, Lycanites, Mowzie's, Cataclysm, Alex's Mobs, BoMD** | Драконы (Dragon, Baby Dragon, Water Dragon) рядом с драконами Ice and Fire; Sea Monster / Sea Viper — с морским змеем Ice and Fire; Triffid — с одноимённым мобом Lycanites. Боссы — свои, совпадений нет | Оставить как есть: у OreSpawn свой стиль (огромные боссы, юмор). Дубли по функции есть, но все мобы разные |
| **Мобы из модов-связок** | Объявлены связки с `eeeabsmobs`, `legendary_monsters`, `mutantmonsters`, `guardvillagers` и т. п. — этих модов у нас нет. Есть `iceandfire`, `cataclysm` | Ничего не нужно. В конфиге включено `IgnorePercentHealthDamage=1`: удары «процентом от HP» (боссы Cataclysm) считаются так, будто у моба 100 HP |
| **Разрушения** | Mobzilla, King, Queen, роботы и «зверьки» (бобры, термиты) ломают блоки по игровому правилу `mobGriefing` (по умолчанию «следовать правилу» = да). Оберег (`disaster-ward`) их не останавливает | Решить владельцу: оставить, как в оригинале, или поставить `BossesGriefing=2` / `RobotsGriefing=2` в `config/chaospersists.cfg` (2 = никогда) |
| **Питомцы** | Задача апстрима #306: питомцы **других модов** в режиме «гулять» переносятся вместе с игроком между измерениями | Знать. Можно сажать питомцев |
| **Русский перевод** | В jar есть `ru_ru.json`: 1634 из 2062 строк. Не переведены названия структур (54, видны в `/locate`), горшки с цветами (8), экран настроек (356) | Дописать в `russian-moded` (поток русификации) — около 1 ч |

### Нагрузка
- Jar весит 19 МБ: 4905 файлов, ~130 сущностей, 310 звуков.
- Ремейк по умолчанию, как и оригинал, добавляет в **Overworld**:
  - естественный спавн большинства мобов (общий лимит мобов — значит, других мобов станет меньше);
  - 5 новых руд;
  - ~120 «высушенных яиц» в руде;
  - муравейники;
  - ~25 структур на поверхности и в океане, 4 — в Энде.
- Всё это выключается в `chaospersistsoverworld` (`DisableOverworldNaturalSpawning`, `DisableOverworldDriedEggs` и т. д.). Есть опции `LessLag`, `LessOre`, `PlayNicely`.
- 6 новых измерений; Distant Horizons заводит под каждое свою базу при первом запуске.
- Тестовый сервер:
  - `Done (81.8s)` — прошлые замеры: 48,6 с у Clanging Howl, 32 с у WorldEdit; но это первый запуск в новой папке, с созданием мира;
  - первый тик после Done — ~136 с. Он и раньше был >60 с из-за WorldEdit (поэтому `max-tick-time=-1`), вклад ремейка отдельно не измерялся.
- **Реальную нагрузку при игре (мобы, спавн, тики) на сервере не мерили.** Это стоит сделать на живом сервере через `spark`.

### Прогоны
1. **`test-server.sh`, сборка + Chaos Persists — `SERVER OK: Done (81.762s)`.**
   - В логе 153 ERROR; прежний фон — 152 у Clanging Howl.
   - Единственный ERROR от ремейка — про потолок здоровья (см. выше).
   - Сообщения `failed to re-inject legacy recipes` нет.
   - DH завёл базы для `chaospersists:utopia` и `chaospersists:mining`, то есть измерения загружаются.
2. **`test-server.sh` + AttributeFix — `SERVER OK: Done (35.321s)`** (мир уже создан), 152 ERROR.
   - AttributeFix загрузился и поднял максимум `minecraft:generic.max_health` до 1 000 000 (`config/attributefix.json`, 198 атрибутов).
   - Строка ремейка `failed to raise generic.maxHealth cap` остаётся: его собственный обход по-прежнему падает, но потолок уже снят AttributeFix.
   - Проверка через консоль: `summon chaospersists:the_king` / `chaospersists:mobzilla` ответили «Summoned new The King / Mobzilla», а следующие запросы `attribute … get` и `data get … Health` — «No entity was found».
     Команды выполнились в один тик вместе с `forceload` (сервер был занят первым тиком), причину не разбирали.
     **Фактическое HP боссов в игре не подтверждено** — это первый пункт проверки ниже.

Не проверено:
- клиент (модели, текстуры, рендер ~130 мобов, экран настроек);
- вход в измерения и их генерация на деле;
- бои с боссами;
- русский текст в игре.

## Сравнение с OreSpawn 1.7.10

Статус: **есть** / **частично** / **нет**. «Есть» означает: предмет, моб или структура зарегистрированы, у них есть ассеты и код, перенесённый с оригинала. В игре поведение каждого не проверялось.

### Боссы и крупные мобы
| В оригинале (HP) | Chaos Persists | Замечания | Что делать | Трудоёмкость |
|---|---|---|---|---|
| The King (7000), Utopia, King Tree / King Altar | есть | `the_king`, структуры `king_tree`, `king_altar`; в конфиге `FullPowerKingEnable=0` (как в оригинале) | HP — нужен AttributeFix (поставлен) | — |
| The Queen (6000), Queen Altar / Queen Tree | есть | `the_queen`, `queen_altar`, `queen_tree` | так же | — |
| Mobzilla (4000), Village Dimension | есть | `mobzilla` + `mobzilla_head` | так же | — |
| The Kraken (1000), появляется после убийства Attack Squid | есть | `the_kraken`, Kraken Repellent | — | — |
| Nightmare / Pitch Black (125–1000), Danger | есть | `nightmare`; апстрим: не исчезает на «мирной» сложности и если появился из спавнера (#311, #312) | убрать на «мирной» — KubeJS (событие тика) или свой мод | 1 ч |
| Robot Spider (1500), Jeffery (550) | есть | `robot_spider`, `jeffery`; приручение гаечным ключом | — | — |
| CaterKiller, Emperor Scorpion, Hammerhead, Hercules Beetle, MOTHRA, Vortex, Large Worm | есть | все с трофеями для Big Bertha | — | — |
| The Prince → Young Prince → Young Adult Prince, The Princess | есть | рост как в оригинале + за алмазный блок (`PrinceAutoGrow`), подсказка в Jade | — | — |

### Остальные мобы
| Группа (оригинал) | Chaos Persists | Замечания |
|---|---|---|
| Питомцы: Girlfriend (41 скин), Boyfriend, Frog → принц/принцесса, Baby Dragon → Dragon, Camarasaurus, Velocity Raptor, Hydrolisc, Ostrich, Gazelle, Chipmunk, Lizard, Stinky, Cephadrome, Leonopteryx, Robot Red Ant, Robot Spider, WTF?, Rat, Fairy, Hoverboard | есть | Boyfriend выключен, как в оригинале (`BoyfriendBroMode`) |
| Мирные: 4 коровы (Apple, Golden, Enchanted, Crystal), Butterfly, Moth, Firefly, Cricket, муравьи (Ant, Rainbow, Unstable, Red), Termite, Bird, Ruby Bird, Cassowary, Beaver, Peacock, Whale, Flounder, Gold Fish, Cliff Racer, Rubber Ducky, Easter Bunny, Baryonyx, Stink Bug, Small/Medium Worm, Ghost, Ghost Pumpkin Skelly, Crab | есть | — |
| Враждебные: Mosquito, Bee, Dragonfly, Brutalfly, Scorpion, Mantis, Jumpy Bug, Spit Bug, Cave Fisher, Alien, Attack Squid, Water Dragon, Sea Monster, Sea Viper, Kyuubi, Leaf Monster, Molenoid, Ender Knight, Ender Reaper, Criminal, динозавры (Alosaurus, Cryolophosaurus, T. Rex, Nastysaurus, Pointysaurus, Basilisk), роботы (Bomb-Omb, Robo-Pounder, -Gunner, -Warrior, -Sniper, Spider Driver), Danger (Creeping Horror, Terrible Terror, Lurking Terror, Triffid, Cloud Shark), Crystal (Rotator, Vortex, Crystal Urchin, Dungeon Beast, Skate, Irukandji) | есть | — |
| Прочие сущности: T-Shirt, Coin, Shoes, камни, Island / IslandToo | есть | — |
| Ancient Dried **Zombie Pigman** Spawn Egg + Caged ZombiePigman | **нет** | В 1.20 моба нет. Можно сделать руду-яйцо и клетку для Zombified Piglin своим модом (`mods-src/orespawn-restored`) — около 2 ч, ценность низкая |

### Измерения
| Оригинал (вход) | Chaos Persists | Замечания | Что делать |
|---|---|---|---|
| Utopia (Ant) | есть | `chaospersists:utopia`, свой генератор равнин; Wind/Sky/Apple-деревья, King Tree | убрать из `#is_overworld` (см. совместимость) |
| Mining (Red Ant) | есть | горы, биом `windswept_hills` (в оригинале `extremeHills`) | — |
| Village (Rainbow Ant) | есть | деревни везде + роботы, Mobzilla | — |
| Danger / Islands (Unstable Ant) | есть | плоский мир (как `ChunkProviderOreSpawn4`) + движущиеся острова-сущности (`IslandSizeFactor` / `IslandSpeedFactor`) | — |
| Crystal (Termite, вход только с пустым инвентарём) | есть | свой генератор и декоратор, мобы Crystal | — |
| Chaos (Butterfly) | есть | острова над пустотой, 57 видов мобов в биоме | — |

### Руды и материалы
| Оригинал | Chaos Persists | Замечания |
|---|---|---|
| Uranium, Titanium, Ruby (под лавой), Amethyst, Salt | есть + глубинносланцевые варианты | Отдельно от Mekanism / Northstar / Alex's Caves (см. совместимость). Настраиваются в `chaospersistsores` |
| ~120 Ancient Dried Spawn Egg ores; части яиц King, Queen, Mobzilla | есть | кроме Zombie Pigman |
| Crystal: Kyanite, Pink Tourmaline, Tiger's Eye, Crystal Energy, Crystalized Rats / Fairies, Crystal Grass | есть | — |
| Troll-блоки, Lava Foam, Molenoid Dirt, Random Teleport Block, блоки из Ender Pearl / Eye of Ender / Mobzilla Scale, блоки хранения | есть | Ruby, Amethyst, Uranium, Titanium — ещё и материалы отделки брони (trim) |

### Броня
| Оригинал | Chaos Persists |
|---|---|
| Ultimate, Lava Eel, Moth Scale, Emerald, Experience, Ruby, Amethyst, Pink Tourmaline, Tiger's Eye, Mobzilla Scale, Royal Guardian, Lapis, Queen Scale, Peacock Feather (+ обувь подружек) | **есть все 14 наборов**; характеристики оригинала настраиваются в `chaospersistsarmor`. Royal Guardian — планирование (`RoyalGlideEnable`) |

### Оружие и инструменты
| Оригинал | Chaos Persists | Замечания |
|---|---|---|
| Наборы инструментов: Ultimate, Emerald, Ruby, Amethyst, Pink Tourmaline, Tiger's Eye, Kyanite, Crystal Wood | есть | — |
| Ultimate Bow, Ultimate Fishing Rod, Big Bertha (+3 части), Slice, Royal Guardian Sword, Queen Scale Battle Axe, Nightmare Sword, Attitude Adjuster, Battle Axe, Big Hammer, Chainsaw, Experience / Poison / Rat / Fairy / Rose Sword, Mantis Claw, Skate Bow + Irukandji Arrow, Ray Gun, SquidZooka, Thunder Staff, Creeper Launcher, Wrench | есть | Апстрим: Ultimate Bow не лечит питомцев (#303); урон 1 / «физический урон» (#315, #323) — проверить в игре |
| Метательное: 8 камней, 4 кристалла, Acid, Dead Irukandji, Laser Charge, WaterDragon Charge, Ice Ball, Sunspot Urchin | есть | работают и из раздатчика |

### Еда и растения
| Оригинал | Chaos Persists |
|---|---|
| Corn, Tomato, Lettuce, Radish, Strawberry, Quinoa, Rice; Cherries, Peach, Apple Tree Seed, Crystal Apple; Butter, Cheese, Butter Candy, Salt, 4 вида попкорна; Bacon, Corn Dog, Crab Meat, Crabby Patty, Peacock; Garden Salad, BLT, Pizza; 10 видов рыбы для Ultimate-удочки | **есть все.** Овощи и зерно — в тегах `forge:crops/*`, `forge:seeds/*`, `forge:vegetables/*`, то есть работают с Farmer's Delight и другими модами по тегам |

### Структуры
| Где (оригинал) | Chaos Persists |
|---|---|
| Overworld: Igloo, Haunted House, Pumpkin, Leaf Monster Dungeon, Mantis Hive, Frog Pond, Rubber Ducky Pond, Damsel in Distress, Bouncy Castle, Spit Bug Lair, Stinky House, White House, Inca Pyramid, Greenhouse, Robot Lab, Rainbow, Bee Hive, Red Ant / Spider Hangout, Dungeon | есть |
| Океан: Play Pool, Water Dragon Lair, Goldfish Bowl, Girlfriend Island, Monster Island | есть |
| End: Ender Castle, Ender Dragon Hospital, Ender Knight Outpost, Ender Reaper Graveyard | есть (апстрим #314 просит выключатель) |
| Utopia: King Altar / Tree, Queen Altar / Tree, Ruby Bird Dungeon; Mining: Alien/WTF?, Basilisk Maze, Shadow Dungeon, Ender Knight, Bee Hive, Kyuubi, Leon Nest; Village: деревни + роботы; Danger: Mini / Challenge Dungeons 1–6, Queen's Challenge, Cloud Shark, Nightmare Rookery, Cephadrome Altar; Crystal: Rotator Station, Round Rotator, Urchin Spawner, Crystal Haunted House, Crystal Battle Tower | есть; каждую можно выключить в `chaospersistsstructures`, а `/locate` подсказывает, в каком измерении искать |

### Механики и прочие предметы
| Оригинал | Chaos Persists |
|---|---|
| Муравьи-телепорты + «телепад» из гнёзд и соли; Creeper / Kraken Repellent; клетки для существ и зоопарки, ZooKeeper Shard; Experience Orb Catcher и Experience Tree; Miner's Dream, Stairs Up/Down, Insta-Bridge, Instant Shelter / Garden, Duct Tape, Sifter, Nether Tracker, Extreme / Crystal Torch, Crystal Workbench / Furnace, «OMG! No! Don't do it!!!», Duplicator Tree, Sky Tree, Island Block, блоки призыва King, Queen и подземелий; уловки Girlfriend (сидеть, без звука, переодевание); боевые команды (Battle Mobs) | **есть.** Новое по сравнению с оригиналом: экран настроек, Jade, JEI, отделка брони, глубинносланцевые руды, опции против гриферства и лагов |

## Что предлагаю сделать в первую очередь (для владельца)

Портировать почти нечего: в ремейке есть всё из 1.7.10, кроме одного яйца. Поэтому предлагаю не «порт», а **доводку** — по убыванию пользы:

1. **Проверить в игре** (клиент + сервер, 1–2 ч):
   - вход во все 6 измерений;
   - бой с King, Queen, Mobzilla (HP 7000 / 6000 / 4000 с AttributeFix);
   - рецепты Ultimate и Big Bertha в JEI;
   - Girlfriend.
   Найденные баги ремейка записать в compat.md.
2. **Utopia — снова мирная** (KubeJS, 15 мин): убрать `chaospersists:utopia` из `#minecraft:is_overworld` / `#is_plains`, чтобы туда не лезли мобы и руды других модов.
3. **Решение по разрушениям** (конфиг, 5 мин): оставить, как в оригинале, или `BossesGriefing=2` / `RobotsGriefing=2`. И нужен ли ремейку Overworld-спавн при нашей сотне модов с мобами (`DisableOverworldNaturalSpawning`).
4. **Русификация остатка** (поток русификации, ~1 ч): названия структур, горшки, экран настроек.
5. **Исправления апстрим-багов своим модом** `mods-src/orespawn-restored` (миксины, по образцу wizardry-restored), только по тем, что подтвердятся в игре: Nightmare не исчезает (#311, #312), Ultimate Bow не лечит питомцев (#303), невидимые мобы со свечением (#304), питомцы других модов переносятся между измерениями (#306). 1–3 ч на каждый.
6. *(низкая ценность)* Яйцо-руда и клетка Zombified Piglin вместо Zombie Pigman — свой мод, ~2 ч.

## Доводка (поток 25, 2026-10-17)

Решения владельца: «На твое усмотрение, Utopia не мирная, пусть мобы смогут появляться как в оригинале»; про Overworld: «Просто чтобы это происходило не часто и руды в малом кол-ве».

### Что НЕ менялось
- Utopia остаётся в `#minecraft:is_overworld` / `#is_plains` — теги не трогали, мобы Utopia как в оригинале.
- Разрушения боссами и роботами: `BossesGriefing=0`, `RobotsGriefing=0` (следовать правилу `mobGriefing`), как в оригинале.

### Спавн мобов в Overworld — реже, не выключен
В конфиге нет ни множителей, ни весов спавна: вес каждого моба зашит в код (`EntityRegistry.addSpawn`, потом `LegacyOverworldSpawnPopulation` подмешивает их в ванильные биомы).
В конфиге есть только «выключить всё» (`DisableOverworldNaturalSpawning`, `AllMobsDisable`) и «выключить конкретного моба» (`<Моб>Enable`). `LessLag` на спавн не влияет: он режет только расстановку декора при генерации чанка (бабочки/мотыльки, комары, кукуруза/помидоры, муравьи). `PlayNicely` делает мобов менее агрессивными, а не более редкими.

Поэтому: **`pack/kubejs/server_scripts/orespawn_spawn_thin.js`** — на событии `EntityEvents.checkSpawn` отсекает 75% попыток *естественного* спавна (`NATURAL`, `CHUNK_GENERATION`) мобов `chaospersists:*` в `minecraft:overworld`.
| Что | Было | Стало |
|---|---|---|
| Естественный спавн Chaos Persists в Overworld | 100% попыток | **25%** (в 4 раза реже) |
| Измерения Chaos (Utopia, Mining, Village, Danger, Crystal, Chaos), Незер, Энд | как в оригинале | без изменений |
| Яйца, спавнеры, `/summon`, структуры | — | без изменений |

Почему 25%: у OreSpawn ~130 мобов в списках спавна биомов Overworld на сотню модов с собственными мобами; общий лимит мобов один, и OreSpawn вытеснял бы остальных. С 25% его мобы всё равно встречаются, но не доминируют. Отсечённая попытка не занимает лимит мобов, остальной спавн идёт как раньше. Значение — константа `ORESPAWN_KEEP` в начале скрипта.
Раз в 10 минут скрипт пишет в `logs/kubejs/server.log` строку «попыток спавна … отсечено …» — по ней на живом сервере видно, что фильтр работает.
**Не проверено на деле:** сам фильтр на настоящем естественном спавне. На сервере без игрока цикл спавна не запускается, имитировать его (`NaturalSpawner.spawnForChunk`) не получилось; проверены только загрузка скрипта без ошибок и формат полей события (`type` = `NATURAL`, `level.dimension` = `minecraft:overworld`, `entity.type` = id сущности). Первый пункт для проверки на живой игре.

### Руды в Overworld — меньше и мельче
Генерация идёт кодом (`ChunkOreGenerator`), числа берутся из `config/chaospersists.cfg` (раздел `chaospersistsores`), помещён в `pack/config/chaospersists.cfg`.
- `LessOre=0 → 1`: число попыток генерации каждой руды делится на 3 (в том числе для добавок к ванильным рудам).
- Число попыток на чанк = `rate` + случайная добавка (яйца: `rate + 0..29`, уран/титан/соль: `+0..8`, аметист: `+0..11`), поэтому `rate` ниже определённого порога почти ничего не меняет.
- Размер жилы — эллипсоид, радиус растёт с `clumpsize`; уменьшение 4→3 убирает ~90% блоков, `clumpsize=2` не даёт ни одного блока (проверено). Поэтому мельчим только крупные жилы.

| Параметр (`chaospersistsores`) | Было | Стало |
|---|---|---|
| `SpawnOres_rate` / `_clumpsize` (высушенные яйца) | 28 / 4 | **12** / 4 |
| `Uranium_rate` / `_clumpsize` | 3 / 4 | 3 / 4 (только `LessOre`) |
| `Titanium_rate` / `_clumpsize` | 3 / 4 | 3 / 4 (только `LessOre`) |
| `Amethyst_rate` / `_clumpsize` | 2 / 6 | 2 / **5** |
| `Salt_rate` / `_clumpsize` | 5 / 12 | **4 / 10** |
| `Ruby_rate` / `_clumpsize` | 10 / 1 | **5** / 1 |
| `Diamond`, `Emerald` rate / clump (добавка к ванильным) | 4 / 6 | **2 / 4** |
| `Gold` rate / clump | 4 / 8 | **2 / 6** |
| `BlockDiamond`, `BlockEmerald`, `BlockGold` rate / clump | 2 / 4 | **1 / 2** |
| `LessOre` | 0 | **1** |

**Измерение** (`scripts/tests/orecount.js`: квадрат 7×7 = 49 новых чанков Overworld, тот же сид, те же координаты до и после — регион удалялся и генерировался заново; один прогон на точку, разброс по биомам заметный):
| Блоков на 49 чанков | Точка 1 было → стало | Точка 2 было → стало |
|---|---|---|
| Высушенные яйца | 574 → 106 (−82%) | 14 → 5 (океан, мало и до того) |
| Уран | 156 → 23 (−85%) | 222 → 62 (−72%) |
| Титан | 149 → 35 (−77%) | 208 → 52 (−75%) |
| Аметист | 233 → 66 (−72%) | 399 → 67 (−83%) |
| Соль | 1426 → 306 (−79%) | 1095 → 202 (−82%) |
| Рубин | не встретился | 31 → 15 (−52%) |
| Алмаз/изумруд/золото (руды, включая ванильные и другие моды) | 1604 → 1437 | 2073 → 1926 |
В последней строке сидит весь ванильный и чужой генератор, поэтому падение небольшое — добавка OreSpawn там лишь часть.
Итог: Chaos-руды в Overworld остаются, но их примерно в 4–6 раз меньше (≈0,5 блока урана и титана на чанк вместо ≈3; яиц ≈2 на чанк вместо ≈12).

### Конфиг применён
- Файл `pack/config/chaospersists.cfg` (раздача — packwiz, `test-server.sh` копирует `pack/config` поверх сгенерированного). Мод в лог о чтении конфига не пишет; применение подтверждено **подсчётом руды** (таблица выше) — с ним числа упали, без него (прогон до помещения файла) были базовыми.
- Мод оставляет файл как есть (после запуска сервера совпадает с нашим).
- Остальные значения конфига — по умолчанию мода (`IgnorePercentHealthDamage=1`, `PlayNicely=0`, `LessLag=0`, griefing 0/0/0/0).

### Перевод
Добавлен `pack/resourcepacks-src/russian-moded/assets/chaospersists/lang/ru_ru.json`: **427 строк**, которых не было в `ru_ru.json` самого мода — 54 названия структур (видны в `/locate`), 8 горшков, экран настроек (в т. ч. подсказки и названия мобов/наборов), сообщения питомцев, Jade. Имена мобов согласованы с уже переведёнными в jar; название мода «Chaos Persists» оставлено английским (решение владельца 2026-10-12). Собран в мод `ru-lang` (`scripts/build-ru-pack.sh`), `scripts/lang_validate.py lang` — 0 ошибок, 0 предупреждений. Как выглядит в игре — не проверено (нет клиента).

