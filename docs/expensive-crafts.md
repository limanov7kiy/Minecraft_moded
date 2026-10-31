# «Невероятно дорогие многоступенчатые крафты» и призыв сложного босса: исследование (без установки)

Поток 70, 2026-10-26. Желание владельца: невероятно дорогие многоступенчатые крафты — скорее оружие/броня, возможно призыв
невероятно сложного босса. **Ничего не установлено и не изменено в `pack/`** — решение за владельцем (вопрос в конце).

Как смотрели: jar сборки только на чтение (Cataclysm 3.31, Iron's 3.16.3, Mekanism 10.4.16, ExtraBotany 1.9.2, Ice and Fire 2.1.13,
Occultism, M&A 3.1.11, T.O Magic, Chaos Persists), здоровье боссов — из `docs/bosses.md`, кандидаты — Modrinth API и CurseForge.
Что не проверено по файлам, помечено «по памяти» или «не проверено».

## Коротко
- Самые длинные цепочки в сборке — **ExtraBotany** (Rhein Hammer, 4 млн маны) и **Mekanism** (модули MekaSuit на антиматерии).
  Остальное боевое снаряжение (Cataclysm, Iron's, Ice and Fire, BoMD) — «убил босса → 1–2 крафта».
- **Нет ни одного предмета, требующего трофеев сразу с нескольких модов**, и **нет предмета, вызывающего усиленного босса**.
- Рекомендация — **своё**: цепочки на KubeJS через уже стоящие станки (новых модов 0, дублей 0) + «Печать Возвышения»
  с усиленным боссом. Готовый «супер-босс» такого масштаба — только Cracker's Wither Storm, и он рискованный.

## 1. Что уже есть в сборке

| Мод | Самое дорогое снаряжение | Босс и призыв | Длина цепочки |
|---|---|---|---|
| L_Ender's Cataclysm 3.31 | Void Forge = Infernal Forge (Незеритовое чудовище) + Void Core (Страж Края) в `cataclysm:weapon_fusion`; Gauntlet of Bulwark; броня Ignitium/Cursium поверх незерита | 8 боссов, 333–600 HP: арена + «глаз», алтари огня/пустоты | **короткая** (1–2 босса → 1–2 крафта; проверено в jar) |
| Mekanism (+Generators, Additions, More Machines) | MekaSuit: полоний, ultimate-схемы, индукция; модуль гравитации — 3 гранулы антиматерии | боссов нет | **длинная**: обогащение → реактор деления → полоний → SPS → схемы |
| Botania + Mythic Botany | Terrasteel → Страж Гайи → Gaia Spirit → броня/меч Террастали | Гайя 320 HP — ритуал (маяк, пилоны) | средняя (4–5 этапов, Альфхейм) |
| ExtraBotany 1.9.2 | **Rhein Hammer**: Terra Plate на 4 000 000 маны из 10 предметов (8 молотов, Das Rheingold, The Universe = Chaos + Origin + End, 1 млн маны); Orichalcos | Гайя III 600 HP — ритуал | **самая длинная в сборке** (проверено в jar) |
| Blood Magic (+Animus, Tome of Blood) | Архимагическая сфера (алтарь 5 ур.), Sentient-оружие, Living Armour | — | средняя/длинная |
| Occultism | духи Foliot → Djinni → Afrit → Marid | ритуалы `occultism:summon`; **поле `entity_nbt`** — вызов любой сущности с заданным NBT | средняя |
| Iron's Spells (+T.O Magic, Cataclysm Spellbooks) | Netherite Mage, Legendary Ink; T.O Magic — оружие level_one → three ковкой из материалов Cataclysm/Alex's Caves/Iron's (75 рецептов) | Эхо Тироса 1000 HP, Король мертвецов 500; T.O: Enraged Dead King, Nightwarden 600 | Iron's короткая, **T.O средняя через 3 мода** |
| Ars Nouveau (+аддоны) | броня архимага | Wilden Chimera 1000 HP (ритуал, по памяти) | средняя |
| Ice and Fire CE 2.1.13 | драконья сталь (кровь дракона + Dragonforge, по памяти) | драконы 500 HP, гидра, циклоп, горгона | средняя |
| Bosses of Mass Destruction | добыча с боссов | Лич, Обсидилит, Void Blossom, Gauntlet 250–350 HP — алтари на аренах | короткая |
| Mowzie's | маски Умвуту, Sol Visage, Wrought Axe | Frostmaw, Umvuthi, Wroughtnaut — по структурам | короткая |
| Twilight Forest | Fiery, Knightmetal, Phantom, Arctic | Нага → Лич → Гидра → Ур-Гаст → Снежная королева (замки прогрессии) | длинная **по прогрессии**, не по крафту |
| Chaos Persists 0.9.1 | Ultimate, Queen Scale, Mobzilla Scale, Royal Guardian, Big Bertha | **King 7000 HP, Queen 6000, Mobzilla 4000** (AttributeFix); яйца из частей-руд, алтари | боссы самые «толстые», крафт короткий |
| Alex's Caves | Ortholance, Raygun (по памяти) | Tremorzilla 500, Luxtructosaurus 600, Hullbreaker — Abyssal Altar | средняя |
| Aether и аддоны | Valkyrie, Phoenix, Gravitite (по памяти) | 4 босса 400–500 HP, ключи подземелий | средняя |
| Mana and Artifice 3.1.11 | уровни, фракции | Allfather (`odins_call`), Council Warden, Wither Lich, Queen of the Fey, Demon Lord 375–850 HP — ритуалы | средняя/длинная |
| Ars Magica Legacy | сродства | 10 стражей 80–500 HP (призыв не проверен) | средняя |
| Lycanites | — | Амальгалич, Асмодей, Раховарт по 4000 HP (алтари, по памяти) | — |
| Arknights-моды | — | Qunyou Wanted Isharmla 625 554 HP, Amiya и Kuilong 1000 (не проверено) | — |

## 2. Кандидаты-моды под 1.20.1 Forge

| Мод | Версия 1.20.1 Forge | Что даёт | Зависимости / размер / лицензия | Баланс, дубли |
|---|---|---|---|---|
| [Draconic Evolution](https://modrinth.com/mod/draconic-evolution) | 3.1.2.621 (2025-07) | Draconium → Wyvern → Draconic → **Chaotic** (Fusion Crafting, осколки с Chaos Guardian) | CodeChicken Lib, Brandon's Core (нет); 5,9 МБ; «Don't Be a Jerk» | **дубль** MekaSuit (силовая броня) и индукционной матрицы; энергия RF — ждёт В63. Не советую |
| [Re:Avaritia](https://modrinth.com/mod/re-avaritia) | 1.20.1-1.4.2 (2026-09) | верстак 9×9, нейтроний, сингулярности, **Infinity-броня и меч** | нет; 3,7 МБ; MIT | «режим бога»; только с переделкой рецептов под трофеи; спорит с MekaSuit. Альтернатива — [AvaritiaNeo](https://modrinth.com/mod/avaritianeo) (ставить одну) |
| [Extended Crafting](https://modrinth.com/mod/extended-crafting) | 6.0.10 | столы 3×3…9×9, Combination Crafting; своего снаряжения нет | Cucumber; 0,7 МБ; MIT | не обязателен — есть Mechanical Crafter Create |
| [Cracker's Wither Storm](https://modrinth.com/mod/crackers-wither-storm-mod) | 4.2.1 (**2024-08**) | растущий Шторм-иссушитель: засасывает блоки, охотится; вызов — командный блок (по описанию) | нет; **47 МБ**; ARR | **самый сложный готовый босс**, но разрушение мира и нагрузка; DH, VS, Lost Cities, Disaster Ward не проверены — только после стенда |
| [Bosses'Rise](https://modrinth.com/mod/bossesrise) | 2.1.2 (2026-05) | 5 боссов в духе Souls с фазами и аренами, снаряжение | GeckoLib (есть); 24 МБ; ARR | новые боссы (не дубль); темы «ледяной дракон»/«кракен» пересекаются с Ice and Fire/Cataclysm |
| [Legendary Monsters](https://modrinth.com/mod/legendary-monsters) | 2.2.4 (2026-10) | боссы ранней/средней/поздней игры | нет; 45 МБ; ARR | популярный; есть отдельный fix-мод |
| [Dark Doppelganger](https://modrinth.com/mod/dark-doppelganger) | 9.9.0 (2026-09) | **призываемый** многофазный босс-копия игрока (аддон Iron's) | Iron's (есть); 4,5 МБ; ARR | совместимость с Iron's 3.16 не сверялась |
| [Goety](https://modrinth.com/mod/goety) | 2.5.59.2 | некромантия, ритуалы, боссы | Curios, Patchouli; **93 МБ**; MIT | ещё одно «ядро магии» — пересекается с Occultism/Blood Magic/Iron's |
| [Enigmatic Legacy](https://modrinth.com/mod/enigmatic-legacy) | 2.30.1 (**2024-08**) | реликвии, Seven Curses | Curios, Patchouli, Caelus; 7,3 МБ | старый, возможны пересечения Curios-реликвий |
| [Born in Chaos](https://modrinth.com/mod/borninchaos), [The Graveyard](https://modrinth.com/mod/the-graveyard-forge), [Aquamirae](https://modrinth.com/mod/aquamirae), [Mutant Monsters](https://modrinth.com/mod/mutant-monsters) | да | мобы/боссы среднего уровня | разные | не эндгейм |
| [Apotheosis](https://modrinth.com/mod/apotheosis) | 7.4.8 | аффиксы, самоцветы, элиты | все зависимости есть | **конфликт** со столом чар ESO (`docs/enchanting-table.md`) |
| [Tinkers' Construct](https://modrinth.com/mod/tinkers-construct), [Mythic Upgrades](https://modrinth.com/mod/mythic-upgrades), [Simply Swords](https://modrinth.com/mod/simply-swords), [Forbidden & Arcanus](https://modrinth.com/mod/forbidden-arcanus) | да | оружие/улучшения | — | дубли оружия и ковки Cataclysm, или старые |
| [Celestisynth](https://modrinth.com/mod/celestisynth) | 1.3.4 | 7 клинков | — | **проект отменён** автором |
| [Eternal Starlight](https://modrinth.com/mod/eternal-starlight) | **нет** (Fabric/NeoForge 1.21) | — | — | не подходит |

KubeJS-аддоны: [KubeJS Create](https://modrinth.com/mod/kubejs-create) под 1.20.1 есть (не обязателен). **KubeJS Mekanism под 1.20.1 нет**
(только 1.19.2 и 1.21.1), аддонов для Botania/Blood Magic нет — всё делается через `event.custom({...JSON})` в KubeJS для любого типа рецепта.

## 3. Вариант «сделать своё» (KubeJS + имеющиеся станки)

Что нужно технически:
- **Новые предметы** — `StartupEvents.registry('item', ...)` в `pack/kubejs/startup_scripts` (сейчас таких нет — будет первый случай);
  меч/броня — `.type('sword')` и свои уровни; текстуры и `ru_ru` — `pack/kubejs/assets/mm/`.
- **Способности** — серверные события KubeJS (`EntityEvents.hurt`, `ItemEvents.rightClicked`) или Java в `mods-src/mm-compat` для сложной логики.
- **Этапы-рецепты** через `event.custom` под типы, которые уже есть: `create:sequenced_assembly`, `create:mechanical_crafting`,
  `create:mixing` (перегрев); `mekanism:nucleosynthesizing` (антиматерия), `mekanism:reaction`; `botania:runic_altar`, `botania:terra_plate`;
  `bloodmagic:altar`, `bloodmagic:soulforge`; `ars_nouveau:enchanting_apparatus`; `cataclysm:weapon_fusion`; `occultism:ritual`.
- **Усиленный босс**: (1) ритуал Occultism `occultism:summon` с `entity_to_summon` и `entity_nbt` (`Attributes` — max_health, armor,
  attack_damage; `Health`, `CustomName`, `PersistenceRequired`, `Tags:["mm_ascended"]`) или (2) KubeJS: правый клик предметом →
  `level.createEntity(...)` с теми же NBT. Здоровье > 1024 работает благодаря AttributeFix. Награда — `EntityEvents.death` по тегу.
  **Проверить на стенде**, не перезаписывают ли Cataclysm/Chaos Persists атрибуты при появлении.
- **Баланс**: броня считается по формуле Apothic (`docs/bosses.md`) — поднимать множителем здоровье и урон, броню осторожно.
- PR такого рода меняет `pack/` → поднимает версию сборки; прогоны `test-server.sh` и `test-client.sh`; дубли — по `dedup_list.js`.

### Пример 1. Оружие «Клинок Последнего Рассвета» (`mm:dawnbreaker`): 6 этапов, 6 модов
1. **Сплав** — Create, смешивание с перегревом: 4 Ignitium + 4 Cursium (Cataclysm) + по 2 драконьей стали огня/льда/молнии
   (Ice and Fire) + Mobzilla Scale (Chaos Persists).
2. **Стабилизация** — Mekanism, антипротонный синтезатор: сплав + 500 мБ антиматерии (нужны SPS и реактор деления).
3. **Одухотворение** — Botania, рунный алтарь, 1 млн маны: + 4 Gaia Spirit, руны Гордыни и Гнева, Hero Medal (ExtraBotany).
4. **Кровь** — Blood Magic, алтарь 5-го уровня, 150 000 LP.
5. **Сборка клинка** — Create, последовательная сборка (5 циклов) на основе `cataclysm:the_incinerator`: Void Core, пресс,
   ледяная драконья сталь, заливка.
6. **Финал** — Terra Plate на 4 млн маны (как Rhein Hammer): клинок + ядро + трофей King (Chaos Persists) + 4 Fiery Ingot (Twilight)
   + трофей Alex's Caves.

«Цена» (прикидка, не замер): ~8 разных боссов, сотни гранул полония, ~5 млн маны, 150 тыс. LP, промышленная база Create —
десятки часов на группу.

### Пример 2. Предмет призыва «Печать Возвышения» (`mm:ascension_sigil`) → «Возвышенное чудовище»
**Сделано (поток 80, сборка 2026.10.31-11)** — описание для игроков: [encyclopedia/own/ascension-sigil.md](encyclopedia/own/ascension-sigil.md),
проверка — [compat.md](compat.md), «Печать Возвышения». Отличия от наброска ниже: на этапе 1 — заготовка «Спящая печать» (`mm:ascension_sigil_blank`);
в ритуале печать — активирующий предмет, в чашах 2 незеритовых слитка + Рог чудовища + слиток игнития; Hero Medal — ExtraBotany; сердце пока ни во что не крафтится.
1. **Заготовка** — Cataclysm `weapon_fusion`: Monstrous Eye + Void Core.
2. **Зарядка** — Ars Nouveau, Enchanting Apparatus: + 8 «эссенций» (Essence of the Storm, Lacrima, Abyssal Egg, Gaia Spirit,
   демонический кристалл Blood Magic, Hero Medal, `alexscaves:pure_darkness`, звезда Незера).
3. **Ритуал** Occultism (пентаграмма Marid): `entity_to_summon: "cataclysm:netherite_monstrosity"`, здоровье 6000, урон ×2,
   своё имя, тег `mm_ascended`.
4. **Награда** — уникальное `mm:ascended_heart`, ингредиент финала примера 1 (петля «босс → оружие»).

Вариант серьёзнее — тем же ритуалом The King на 20 000 HP (проверить ломание блоков и нагрузку, `docs/orespawn.md`).

Плюсы: новых модов 0, дублей 0, нужны все роли (технарь, маг, боец). Минусы: текстуры (fal или рисовать), баланс-тест на стенде,
уникальные способности — только кодом.

## 4. Рекомендация
1. **Своё** (раздел 3): пилот — «Печать Возвышения» + усиленный босс + трофей; затем 1–2 предмета-вершины (меч и/или броня).
2. **По желанию — один готовый мод с боссами**: Bosses'Rise (красивые многофазные бои без разрушений) или Cracker's Wither Storm
   (именно «невероятно сложный» босс-событие; только после прогона на стенде).

Не советую: Draconic Evolution (дубль MekaSuit и энергии, В63 открыт), Tinkers' Construct, Apotheosis (конфликт с ESO),
Celestisynth (отменён), Eternal Starlight (нет 1.20.1 Forge); Avaritia — только если нужна именно «бесконечность», с переделкой рецептов.

## 5. Вопрос владельцу (черновик для координатора)

**В## Дорогое снаряжение и супер-босс — как делаем?** Сейчас самые длинные цепочки — молот Рейна ExtraBotany (4 млн маны)
и модули костюма Mekanism на антиматерии; остальное оружие боссов — 1–2 крафта. Предмета, которому нужны трофеи сразу с нескольких
модов, и предмета призыва усиленного босса нет.
- **а) Своё, без новых модов** *(рекомендую)*: 1–2 легендарных предмета (меч и/или броня), которые собираются по очереди на
  Create → Mekanism (антиматерия) → Botania → Blood Magic из трофеев боссов Cataclysm, Ice and Fire, Chaos Persists, Twilight Forest.
  Плюс «Печать Возвышения»: ритуалом вызывает босса в разы сильнее обычного (например, Незеритовое чудовище на 6000 HP), с него —
  сердце для финального крафта. Дублей нет. Сначала пилот — печать и босс.
- **б) Готовый мод «бесконечности»** (Re:Avaritia): верстак 9×9, броня и меч Infinity; рецепты переделаем под трофеи наших боссов.
  Броня спорит с MekaSuit — решить, оставлять ли обе.
- **в) Готовый мод с супер-боссом**: Wither Storm (огромный растущий иссушитель, съедает мир; сначала тест нагрузки и разрушений)
  или Bosses'Rise (5 боссов в стиле Dark Souls, без разрушений).

Можно «а» + один из «в». И уточнить: меч, броню, предмет призыва — или всё сразу?
