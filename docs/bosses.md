# Таблица боссов сборки

Сгенерировано `scripts/boss_table.py` (источники — в его шапке); руками правятся только записи в `scripts/boss_info.py`. **Добавил мод с боссами — перезапусти скрипт и закоммить `docs/bosses.md` и `docs/bosses.csv` в том же PR** (правило в `docs/roadmap.md`).

Числа (здоровье / урон / броня) — атрибуты существа в запущенной сборке, после конфигов и патчей (`scripts/tests/bossdump.js`). «≈/не извлечено» — число не удалось получить. Урон — базовый атрибут атаки; у многих боссов основной урон идёт заклинаниями и особыми атаками, их множители в атрибут не входят. Размер: огромный — хитбокс от 6 блоков в высоту или 4 в ширину; большой — от 3 / 2.

Учёт баланса (`docs/compat.md`): броня считается по Apothic (`a/(a+броня)`), защита-зачарование по ваниле — поздняя игра примерно вдвое тяжелее, чем рассчитывали авторы боссов; Arknights Weapon в оригинале удваивал здоровье мобов при загрузке чанка — в сборке порт без этого.

Всего в таблице: **216**; без записи в `boss_info.py` (нужно описать): **144**.

## aether

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Королева валькирий** / Valkyrie Queen | Эфир, Серебряное подземелье | 500 | 13.5 | 0 | обычный (1.95×0.8) | — | — | aether:silver_dungeon_key, minecraft:golden_sword (из лута) | — | босс |  | нет |
| **Дух Солнца** / Sun Spirit | Эфир, Золотое подземелье | 500 | ≈/не извлечено | 0 | большой (3.4×2.5) | огненные и ледяные атаки; ночь/день | — | aether:gold_dungeon_key, aether:sun_altar (из лута) | — | босс |  | нет |
| **Слайдер** / Slider | Эфир, Бронзовое подземелье | 400 | ≈/не извлечено | 0 | большой (2.0×2.0) | — | — | aether:bronze_dungeon_key, aether:carved_stone (из лута) | — | босс |  | нет |

## alexscaves

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Люкструктозавр** / Luxtructosaurus | Alex’s Caves | 600 | 12 | 20 | огромный (8.5×6.0) | — | — | — | — | босс |  | нет |
| **Тремороцилла** / Tremorzilla | Alex’s Caves (яйцо Tremorzilla) | 500 | 30 | 10 | огромный (11.0×4.5) | — | — | — | — | сильный моб | Не босс по боссбару — огромный моб. | нет |
| **Атлатитан** / Atlatitan | Alex’s Caves | 400 | 8 | 0 | огромный (8.0×5.0) | — | — | — | — | сильный моб |  | нет |
| **Хуллбрейкер** / Hullbreaker | Abyssal Chasm (Alex’s Caves) | 400 | 16 | 0 | огромный (4.5×4.65) | — | Abyssal Altar | — | — | сильный моб |  | нет |

## alexsmobs

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Червь Пустоты** / Void Worm | Alex’s Mobs, призыв предметом (Void Worm Eye) | 160 | 5 | 4 | большой (3.0×3.4) | — | — | alexsmobs:void_worm_eye, alexsmobs:void_worm_mandible (из лута) | — | босс |  | нет |

## ancient_aether

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Мутировавший эхор** / Mutated Aechor Plant | Ancient Aether | 500 | ≈/не извлечено | 0 | большой (2.0×3.0) | — | — | ancient_aether:laboratory_dungeon_key (из лута) | — | босс |  | нет |

## ars_nouveau

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Дикий химера-босс** / Wilden Chimera | Ars Nouveau, лагеря Дикарей | 1000 | 8 | 6 | большой (2.25×2.5) | — | — | — | — | босс |  | нет |

## arsmagicalegacy

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Страж Края** / Ender Guardian | Ars Magica: Legacy | 500 | 2 | 20 | обычный (2.25×1.0) | — | — | arsmagicalegacy:affinity_essence (из лута) | — | босс |  | нет |
| **Страж природы** / Nature Guardian | Ars Magica: Legacy | 500 | 2 | 20 | большой (4.25×1.25) | — | — | arsmagicalegacy:affinity_essence (из лута) | — | босс |  | нет |

## blue_skies

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Арахнарх** / Arachnarch | Blue Skies | 500 | 14 | 6 | большой (1.7×3.0) | — | — | — | — | босс |  | нет |
| **Звёздный крушитель** / Starlit Crusher | Blue Skies | 500 | 10 | 0 | большой (3.2×1.6) | — | — | — | — | босс |  | нет |
| **Алхимик** / Alchemist | Blue Skies | 500 | 2 | 0 | обычный (2.0×0.6) | — | — | — | — | босс |  | нет |
| **Призыватель** / Summoner | Blue Skies | 350 | 2 | 0 | обычный (2.0×0.6) | — | — | — | — | босс |  | нет |

## bosses_of_mass_destruction

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Цветок пустоты** / Void Blossom | Пещеры Overworld (рядом с лилиями пустоты) | 350 | 12 | 4 | огромный (10.0×8.0) | закапывание, шипы, споры, лепестковые клинки | блок «Призыватель цветка пустоты» в пещере | bosses_of_mass_destruction:void_thorn, bosses_of_mass_destruction:crystal_fruit (из лута) | — | босс | Хитбокс 10×8 блоков. | нет |
| **Ночной лич** / Night Lich | Башня лича (структура lich_tower) — Overworld | 300 | 9 | 0 | большой (3.0×1.8) | кометы, ракеты с замедлением, призыв мертвецов; вечная ночь на время боя | подняться в башню и активировать алтарь на вершине | опыт; звезда души (если перед боем убито 50 нежитей) | щит, лук, быстрое лечение | босс | Параметр eternalNighttime=true: ночь на время боя. | нет |
| **Обсидилит** / Obsidilith | Арена Обсидилита (obsidilith_arena) — Край | 300 | 16 | 14 | большой (4.4×2.0) | руны, вспышки, наковальни, взрывы | обсидиановый алтарь на арене | столб при смерти (spawnPillarOnDeath) | — | босс |  | нет |
| **Перчатка незера** / Nether Gauntlet | Арена Перчатки (gauntlet_arena) — Незер | 250 | 16 | 8 | огромный (4.0×5.0) | удары, лазеры, вращающийся удар | арена из древнего резного чернита; подойти к ней | древние обломки при смерти (spawnAncientDebrisOnDeath) | — | босс |  | нет |

## botania

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Страж Гайи (Gaia Guardian)** / Guardian of Gaia | Любой мир, на площадке «Gaia Guardian» | 320 | ≈/не извлечено | 0 | обычный (1.8×0.6) | — | ритуал Botania (Gaia Guardian) | — | — | босс |  | нет |
| **Розовый Иссушитель** / Pink Wither | Botania (Pink Wither) | 300 | 2 | 4 | большой (3.5×0.9) | — | — | — | — | босс |  | нет |

## cataclysm

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Незеритовое чудовище** / Netherite Monstrosity | Незер, структура soul_black_smith | 600 | 25 | 12 | большой (5.75×3.0) | — | арена + Eye of Monstrous | cataclysm:infernal_forge, cataclysm:monstrous_horn, cataclysm:lava_power_cell, cataclysm:music_disc_netherite_monstrosity (из лута) | — | босс |  | нет |
| **Древний реликт** / Ancient Remnant | Пустыня, структура cursed_pyramid | 450 | 25 | 12 | огромный (5.0×4.35) | — | Eye of Desert | cataclysm:sandstorm_in_a_bottle, cataclysm:remnant_skull, cataclysm:ancient_metal_block, cataclysm:music_disc_ancient_remnant (из лута) | — | босс |  | нет |
| **Игнис** / Ignis | Незер, структура burning_arena | 450 | 14 | 10 | большой (3.5×2.25) | две фазы | арена с Altar of Fire + Eye | cataclysm:ignitium_ingot, cataclysm:music_disc_ignis (из лута) | огнестойкость, незерит+ | босс |  | нет |
| **Малединктус** / Maledictus | Мир, структура frosted_prison | 420 | 13 | 10 | большой (3.0×1.5) | — | Eye of Curse | cataclysm:music_disc_maledictus, cataclysm:cursium_ingot (из лута) | — | босс |  | нет |
| **Левиафан** / The Leviathan | Океан, структура sunken_city | 400 | 15 | 10 | огромный (3.0×4.5) | — | Altar of Abyss + Eye of Abyss | cataclysm:tidal_claws, cataclysm:abyssal_egg, cataclysm:music_disc_the_leviathan (из лута) | — | босс |  | нет |
| **Предвестник** / The Harbinger | Мир, структура ancient_factory | 390 | 9 | 12 | большой (3.75×1.6) | — | Eye of Mech | cataclysm:witherite_block, cataclysm:music_disc_the_harbinger (из лута) | — | босс |  | нет |
| **Сцилла** / Scylla | Океан, структура acropolis | 390 | 18 | 12 | большой (3.0×1.4) | — | Eye of Storm | cataclysm:lacrima, cataclysm:music_disc_scylla, cataclysm:essence_of_the_storm (из лута) | — | босс |  | нет |
| **Страж Края** / Ender Guardian | Край, структура ruined_citadel | 333 | 16 | 20 | большой (3.8×2.5) | — | Altar of Void + предмет Eye of … | cataclysm:gauntlet_of_guard, cataclysm:music_disc_ender_guardian (из лута) | — | босс |  | нет |

## deep_aether

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Глаз бури (Eye of the Storm)** / Eye of the Storm | Deep Aether | 220.1 | ≈/не извлечено | 0 | большой (3.0×3.0) | — | — | deep_aether:brass_dungeon_key, aether:cold_aercloud (из лута) | — | босс | Состоит из контроллера и сегментов (eots_segment). | нет |

## deeperdarker

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Сталкер** / Stalker | Deeper and Darker (Бездна) | 200 | 22 | 4 | большой (4.4×1.0) | — | — | deeperdarker:soul_crystal (из лута) | — | босс |  | нет |

## extrabotany

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Гайя 3-я** / Guardian of Gaia III | ExtraBotany | 600 | 2 | 0 | обычный (1.8×0.6) | — | ритуал ExtraBotany | extrabotany:pandoras_box, extrabotany:music_disc_gaia3 (из лута) | — | босс |  | нет |

## gcrevival

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Скелет-босс (Galacticraft Revival)** / Evolved Skeleton Boss | Лунный данж, Луна (Ad Astra) | 150 | 2 | 0 | большой (4.0×1.5) | — | — | — | — | босс | Из нашего порта Galacticraft Revival. | нет |

## iceandfire

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Огненный дракон** / Fire Dragon | Логова драконов в мире (горы/пустоши) | 500 | 18 | 21 | огромный (1.2×0.78) | дыхание огнём; пикирование; приручаемый — летающий скакун | яйцо дракона из логова; взрослые встречаются в небе у логова | — | алмаз/незерит, дальний бой, защита от огня | босс | Здоровье/урон/броня — взрослый (стадия 5, AgeTicks 3 000 000), у стадии 1 — 20 HP. «Dragon Health»/«Dragon Attack Damage» в config/iceandfire-common.toml. | нет |
| **Ледяной дракон** / Ice Dragon | Логова драконов в заснеженных районах | 500 | 18 | 21 | огромный (1.2×0.78) | ледяное дыхание, замораживание | яйцо из логова | — | огнестойкость не нужна; тёплая броня, огонь по дракону | босс | Замер как у огненного: стадия 5. | нет |
| **Молниевый дракон** / Lightning Dragon | Логова драконов (Lightning) | 500 | 18 | 21 | огромный (1.2×0.78) | дыхание молнией, цепной урон | яйцо из логова | — | громоотвод/диэлектрическая броня | босс | Замер как у огненного: стадия 5. | нет |
| **Гидра (Ice and Fire)** / Hydra | Болота, логово гидры | 250 | 3 | 1 | большой (1.39×2.8) | много голов, отравляющее дыхание | — | — | — | босс |  | нет |
| **Циклоп** / Cyclops | Пещеры циклопа / бродит по миру | 150 | 15 | 20 | огромный (7.4×1.95) | — | — | — | — | босс |  | нет |
| **Королева мирмексов** / Myrmex Queen | Улей мирмексов (джунгли/пустыня) | 120 | 10.5 | 15 | большой (1.86×2.9) | — | — | — | — | босс |  | нет |
| **Горгона** / Gorgon | Храм горгоны | 100 | 3 | 1 | обычный (1.99×0.8) | взгляд превращает в камень — нужно зеркало/слепота | — | — | — | босс |  | нет |
| **Тролль** / Troll | горы, лес | 50 | 10 | 9 | большой (3.5×1.2) | — | — | — | — | сильный моб |  | нет |
| **Морской змей** / Sea Serpent | Океан | ≈/не извлечено | ≈/не извлечено | ≈/не извлечено | огромный (0.5×0.5) | — | — | — | — | босс | Растёт со стадиями; созданный с нуля даёт лишь 20 HP — взрослый не замерен. | нет |

## irons_spellbooks

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Огненный босс** / Echo of Tyros, First Flamebearer | структура в мире Iron’s Spellbooks | 1000 | 10 | 15 | большой (3.67×1.49) | — | — | irons_spellbooks:divine_soulshard, irons_spellbooks:decrepit_scythe, irons_spellbooks:betrayer_signet, irons_spellbooks:arcane_essence, irons_spellbooks:cinder_essence (из лута) | — | босс |  | нет |
| **Король мертвецов** / The Dead King | структура Iron’s Spellbooks (Archevoker/Dead King Dungeon) | 500 | 10 | 15 | большой (3.5×0.9) | — | найти склеп-арену | irons_spellbooks:arcane_essence, irons_spellbooks:scroll, irons_spellbooks:blank_rune, irons_spellbooks:blood_staff, irons_spellbooks:necronomicon_spell_book (из лута) | — | босс |  | нет |

## lost_aether_content

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Король эрвалей** / Aerwhale King | Эфир (Lost Aether Content) | 500 | 15 | 0 | огромный (4.0×4.0) | — | — | aether:blue_aercloud, lost_aether_content:platinum_key (из лута) | — | босс |  | нет |

## lycanitesmobs

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Амальгалич** / Amalgalich | — | 4000 | 2 | 0 | огромный (24.0×18.0) | — | — | minecraft:bone, minecraft:ender_pearl, minecraft:gold_ingot, minecraft:diamond, minecraft:netherite_scrap, minecraft:nether_star … (из лута) | — | босс | Числа из creatures/*.json. | нет |
| **Асмодей** / Asmodeus | по JSON creature — см. колонку | 4000 | 2 | 0 | огромный (21.0×20.0) | — | — | minecraft:redstone, minecraft:iron_ingot, minecraft:gold_ingot, minecraft:diamond, minecraft:netherite_scrap, minecraft:nether_star … (из лута) | — | босс | Числа из creatures/*.json. | нет |
| **Раховарт** / Rahovart | Незер | 4000 | 2 | 0 | огромный (25.0×7.0) | много призывов и нескольких видов атак | — | minecraft:blaze_powder, minecraft:blaze_rod, minecraft:gold_ingot, minecraft:diamond, minecraft:netherite_scrap, minecraft:nether_star … (из лута) | — | босс | Числа из creatures/*.json. | нет |

## minecraft

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Хранитель** / Warden | Древний город (Тёмные глубины Overworld) | 500 | 30 | 0 | обычный (2.9×0.9) | звуковая волна сквозь стены | не призывается; пробуждается шумом/сенсорами | — | незерит, обувь с Тихой поступью (Swift Sneak) | сильный моб | Нет боссбара, но по силе — босс. | нет |
| **Иссушитель** / Wither | любое измерение (бой лучше в пещере/Незере) | 300 | 2 | 4 | большой (3.5×0.9) | после 50% здоровья — броня и щит от стрел | 4 блока песка душ/почвы душ «Т» + 3 черепа скелета-иссушителя | звезда Нижнего мира | незерит, зачарованный меч, молоко/золотые яблоки | босс |  | нет |
| **Дракон Края** / Ender Dragon | Край (The End) | 200 | ≈/не извлечено | 0 | огромный (8.0×16.0) | кристаллы лечат; пикирование, дыхание; после смерти — яйцо | заходит в Край; повторно — 4 кристалла Края вокруг портала | яйцо дракона, опыт | алмаз+/незерит, лук, зелья падения | босс | У Sarkaz's Furnace и Bumblezone есть теги, где он числится «тёмным боссом». | нет |

## mna

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Страж Совета** / Council Warden | Mana and Artifice | 850 | 20 | 20 | обычный (2.0×1.0) | — | — | mna:spell_part_thesis (из лута) | — | босс |  | нет |
| **Один** / The Allfather | Mana and Artifice | 850 | 15 | 20 | обычный (2.0×1.0) | — | — | — | — | босс |  | нет |
| **Лич-иссушитель** / Wither Lich | Mana and Artifice | 850 | 20 | 14 | большой (3.0×2.0) | — | — | mna:spell_part_thesis (из лута) | — | босс |  | нет |
| **Королева фей** / Queen of the Fey | Mana and Artifice | 675 | 5 | 14 | обычный (2.0×1.0) | — | — | mna:spell_part_thesis (из лута) | — | босс |  | нет |
| **Повелитель демонов** / Demon Lord | Mana and Artifice | 375 | 20 | 14 | большой (5.0×3.0) | — | — | mna:spell_part_thesis (из лута) | — | босс |  | нет |
| **Король тыкв** / Pumpkin King | Mana and Artifice | 300 | 2 | 4 | огромный (6.0×1.0) | — | — | — | — | босс |  | нет |

## mowziesmobs

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Фростмо** / Frostmaw | снежные биомы (Frostmaw) | 250 | 10 | 0 | огромный (4.0×4.0) | — | — | — | — | босс |  | нет |
| **Умвути, Птица солнца** / Umvuthi, the Sunbird | Деревня племени Умвутана | 150 | 2 | 0 | большой (3.2×1.5) | — | поселение племени (бой стартует у трона) | — | — | босс |  | нет |
| **Тонби, Скульптор** / Tongbi, the Sculptor | структура-испытание Скульптора | 140 | 10 | 0 | обычный (2.3×1.0) | — | по найденной структуре | — | — | босс |  | нет |

## the_bumblezone

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Королева пчёл** / Bee Queen | Bumblezone (Королевский сот) | 150 | 10 | 0 | большой (2.9×2.9) | — | — | the_bumblezone:bee_stinger (из лута) | — | сильный моб | Мирный NPC с большим запасом здоровья. | нет |
| **Космический кристалл** / Cosmic Crystal Entity | Bumblezone (Космическая арена) | 60 | 1 | 0 | обычный (2.0×1.0) | — | — | — | — | босс |  | нет |

## twilightforest

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Гидра** / Hydra | Twilight Forest, Логово гидры (Огненное болото) | 360 | ≈/не извлечено | 0 | огромный (12.0×16.0) | три головы отращиваются заново | — | twilightforest:hydra_chop, twilightforest:fiery_blood, twilightforest:hydra_trophy (из лута) | — | босс |  | нет |
| **Ур-Гаст** / Ur-Ghast | Twilight Forest, Тёмная башня (Тёмный лес) | 250 | ≈/не извлечено | 0 | огромный (18.0×14.0) | — | — | twilightforest:carminite, twilightforest:fiery_tears, twilightforest:ur_ghast_trophy (из лута) | — | босс |  | нет |
| **Снежная королева** / Snow Queen | Twilight Forest, Аврора-дворец (Ледник) | 200 | 7 | 0 | обычный (2.2×0.7) | ледяные кристаллы | — | twilightforest:triple_bow, twilightforest:seeker_bow, minecraft:packed_ice, minecraft:snowball, twilightforest:snow_queen_trophy (из лута) | — | босс |  | нет |
| **Альфа-йети** / Alpha Yeti | Twilight Forest, Логово йети (Снежный лес) | 200 | 1 | 0 | большой (5.0×3.8) | — | — | twilightforest:alpha_yeti_fur, twilightforest:ice_bomb, twilightforest:alpha_yeti_trophy (из лута) | — | босс |  | нет |
| **Миношрум** / Minoshroom | Twilight Forest, Лабиринт | 120 | 2 | 0 | обычный (2.5×1.49) | — | дойти до арены лабиринта | twilightforest:meef_stroganoff, twilightforest:minoshroom_trophy, twilightforest:diamond_minotaur_axe (из лута) | — | босс |  | нет |
| **Нага** / Naga | Измерение Twilight Forest, Двор Наги | 120 | 5 | 0 | большой (3.0×2.0) | — | найти двор в Лесу сумерек | twilightforest:naga_scale, twilightforest:naga_trophy (из лута) | железо/алмаз | босс | Первый босс линейки TF. | нет |
| **Лич** / Lich | Twilight Forest, Башня Лича | 100 | 3 | 0 | обычный (2.1×1.1) | клоны, телепорт, щит | подняться в башню | twilightforest:twilight_scepter, twilightforest:lifedrain_scepter, twilightforest:zombie_scepter, twilightforest:fortification_scepter, minecraft:golden_sword, minecraft:golden_helmet … (из лута) | — | босс |  | нет |
| **Призрачный рыцарь** / Knight Phantom | Twilight Forest, Крепость рыцарей (Тёмный лес) | 35 | 1 | 0 | обычный (2.5×1.25) | группа из нескольких рыцарей | — | — | — | босс |  | нет |

## undergarden

| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | Награда | Экипировка | Тип | Заметки | Проверен в игре |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Забытый страж** / Forgotten Guardian | Андергарден (структура Forgotten Vault) | 80 | 10 | 10 | большой (3.8×1.0) | — | — | undergarden:forgotten_nugget (из лута) | — | босс |  | нет |

## Найдены автоматически, ещё не описаны

Только числа из сборки и сигнал, по которому босс найден; описание (где встретить, как призвать) дописывается в `scripts/boss_info.py` по мере прохождения. Полные колонки — в `bosses.csv`.

| ID | Тип | HP | Урон | Броня | Размер | Сигнал |
|---|---|---|---|---|---|---|
| `alexscaves:forsaken` (Forsaken) | сильный моб | 250 | 10 | 0 | большой (3.5×3.0) | здоровье ≥ 150 |
| `alexscaves:gum_worm` (Gum Worm) | сильный моб | 150 | 9 | 10 | большой (2.7×3.5) | здоровье ≥ 150 |
| `alexscaves:tremorsaurus` (Tremorsaurus) | сильный моб | 150 | 14 | 8 | большой (3.85×2.5) | здоровье ≥ 150 |
| `alexsmobs:sea_bear` (Sea Bear) | сильный моб | 200 | 8 | 0 | большой (1.99×2.4) | здоровье ≥ 150 |
| `alexsmobs:cachalot_whale` (Cachalot Whale) | сильный моб | 160 | 30 | 0 | огромный (4.0×9.0) | здоровье ≥ 150 |
| `arsmagicalegacy:life_guardian` (Life Guardian) | босс | 400 | 2 | 10 | обычный (1.25×1.0) | боссбар в классе; здоровье ≥ 150 |
| `arsmagicalegacy:ice_guardian` (Ice Guardian) | босс | 300 | 2 | 20 | большой (3.0×1.5) | боссбар в классе; здоровье ≥ 150 |
| `arsmagicalegacy:fire_guardian` (Fire Guardian) | босс | 250 | 2 | 20 | большой (3.0×1.0) | боссбар в классе; здоровье ≥ 150 |
| `arsmagicalegacy:lightning_guardian` (Lightning Guardian) | босс | 250 | 2 | 20 | обычный (1.25×0.5) | боссбар в классе; здоровье ≥ 150 |
| `arsmagicalegacy:air_guardian` (Air Guardian) | босс | 200 | 2 | 10 | обычный (1.75×0.6) | боссбар в классе; здоровье ≥ 150 |
| `arsmagicalegacy:arcane_guardian` (Arcane Guardian) | босс | 120 | 2 | 10 | обычный (2.25×0.9) | боссбар в классе |
| `arsmagicalegacy:earth_guardian` (Earth Guardian) | босс | 120 | 2 | 10 | обычный (2.5×1.5) | боссбар в классе |
| `arsmagicalegacy:water_guardian` (Water Guardian) | босс | 80 | 2 | 10 | обычный (1.5×1.0) | боссбар в классе |
| `bobsoriginiumdream:evolutionnaturenewborn` (进化的本质_初生) | босс | 1000 | 0 | 5 | огромный (5.0×5.0) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:evolutionnatureevolution` (进化的本质_进化形态) | босс | 1000 | 0 | 10 | огромный (5.0×5.0) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:evolutionnatureperfect` (完美_进化的本质_完美) | босс | 1000 | 0 | 30 | огромный (5.0×5.0) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:bobcolossus` (鲍勃巨像) | сильный моб | 1000 | 36 | 8 | большой (4.6×3.0) | здоровье ≥ 150 |
| `bobsoriginiumdream:milan` (米兰) | босс | 600 | 15 | 10 | обычный (2.2×1.2) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:roamhouse` (漫游小屋) | босс | 600 | 18 | 10 | большой (2.5×2.0) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:worldremains` (尘世遗骸) | босс | 600 | 25 | 30 | огромный (6.0×5.0) | боссбар в классе; здоровье ≥ 150 |
| `bobsoriginiumdream:losewaycolossus` (迷路的巨像) | босс | 550 | 20 | 18 | огромный (3.4×4.0) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:gluttonyfruit` (暴食之果-钳兽壳粉) | босс | 550 | 10 | 10.5 | огромный (4.4×4.4) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:shinybugangry` (斑斓虫) | босс | 500 | 20 | 0 | обычный (1.8×0.6) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:shinybug` (斑斓虫) | босс | 500 | 10 | 0 | обычный (1.8×1.1) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:pompeii` (庞贝) | босс | 350 | 3 | 5.5 | огромный (5.0×4.0) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:stonetones` (石之钳兽) | сильный моб | 300 | 40 | 20 | обычный (1.3×1.3) | здоровье ≥ 150 |
| `bobsoriginiumdream:mephisto` (梅菲斯特歌者) | босс | 200 | 6 | 5 | большой (2.0×3.0) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:mephistoend` (梅菲斯特歌者) | босс | 200 | 8 | 4.5 | большой (2.0×3.0) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:mephistosinger` (梅菲斯特歌者) | босс | 200 | 6 | 4.5 | большой (2.0×3.0) | боссбар в классе; здоровье ≥ 150; тег bobsoriginiumdream:boss; тег forge:bosses; тег the_bumblezone:bosses |
| `bobsoriginiumdream:oribear` (狂躁异质北极熊) | сильный моб | 200 | 20 | 3 | обычный (2.0×1.0) | здоровье ≥ 150 |
| `bobsoriginiumdream:orienderman` (假想敌:充能) | сильный моб | 200 | 15 | 3 | большой (3.3×0.9) | здоровье ≥ 150 |
| `bobsoriginiumdream:sly` (狡兽) | сильный моб | 200 | 10 | 12 | обычный (2.0×1.2) | здоровье ≥ 150 |
| `bobsoriginiumdream:fiercepig` (凶豕兽) | сильный моб | 170 | 10 | 5 | обычный (1.2×1.6) | здоровье ≥ 150 |
| `bobsoriginiumdream:ori_1750` (狂暴宿主组长) | сильный моб | 150 | 14 | 3 | обычный (1.8×0.6) | здоровье ≥ 150 |
| `bobsoriginiumdream:oristoneman` (固化源石巨像) | сильный моб | 150 | 15 | 25 | большой (2.2×2.2) | здоровье ≥ 150 |
| `bobsoriginiumdream:orivilliager` (源石化绿袍尊者_哈亚) | сильный моб | 150 | 20 | 7 | обычный (1.8×0.6) | здоровье ≥ 150 |
| `bobsoriginiumdream:rushoriworm` (冲锋源石虫) | сильный моб | 150 | 10 | 10 | обычный (0.6×0.6) | здоровье ≥ 150 |
| `bobsoriginiumdream:worldremains_2` (尘世遗骸_2阶段) | босс | 100 | 3 | 0 | огромный (7.0×5.0) | боссбар в классе |
| `caerula_arbor:damage_tester` (Damage Tester) | сильный моб | 1000000 | 3 | 0 | обычный (1.8×0.6) | здоровье ≥ 150 |
| `caerula_arbor:qunyou_wanted_isharmla` (Qunyou Wanted Isharmla) | босс | 625554 | 6593 | 24 | огромный (20.0×20.0) | боссбар в классе; здоровье ≥ 150 |
| `caerula_arbor:mega_stonecutter` (Mega Oean Stonecutter) | босс | 1200 | 9 | 30 | большой (2.0×2.0) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:oceanized_warden` (Sculk Scope Watcher) | босс | 825 | 50 | 10 | большой (3.1×1.2) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:oceanized_wardenis` (Wardenis the Seaborn) | босс | 825 | 50 | 10 | обычный (1.85×0.6) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:al_1_s_helper` (Little Helper the Al-1s) | сильный моб | 799 | 256 | 5 | обычный (0.2×0.88) | здоровье ≥ 150 |
| `caerula_arbor:little_helper` (Little Helper) | сильный моб | 799 | 256 | 5 | обычный (0.2×0.88) | здоровье ≥ 150 |
| `caerula_arbor:izumik` (Izumik) | босс | 600 | 22 | 18 | огромный (9.0×3.3) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:bishop_fish` (Quintus) | босс | 560 | 7 | 8 | обычный (2.2×1.4) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:oceanized_wither` (Tranquil Sea Ethereality) | босс | 550 | 12 | 8 | большой (3.5×1.1) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:martus` (Martus) | босс | 500 | 2 | 30 | обычный (2.8×0.7) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:super_big_cat` (Super Big Cat) | босс | 500 | 16 | 10 | большой (4.0×3.5) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:aegir_mortar` (Aegir Mortar) | сильный моб | 500 | 15 | 20 | большой (4.0×3.0) | здоровье ≥ 150 |
| `caerula_arbor:iberia_monstrosity` (Iberia Monstrosity) | сильный моб | 500 | 19 | 22 | большой (5.0×3.0) | здоровье ≥ 150 |
| `caerula_arbor:complex_chitin_golem` (Complex Chitin Golem) | босс | 475 | 24 | 18 | большой (4.0×1.75) | здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:runvolution` (Runvolution the Imaginary Enemy) | босс | 450 | 7 | 5 | большой (5.25×2.0) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:ulpians` (Ulpians) | сильный моб | 430 | 55 | 0 | обычный (1.8×0.6) | здоровье ≥ 150 |
| `caerula_arbor:skadi_corrupted` (Skadi Corrupted) | босс | 405 | 15 | 9 | обычный (1.8×0.6) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:last_knight_and_horse` (The Last Knight) | босс | 400 | 20 | 24 | большой (4.0×1.2) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:oceanized_witheria` (Witheria the Tranquility) | босс | 400 | 24 | 15 | обычный (2.5×0.7) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:the_last_knight` (The Tide-hunt Knight) | босс | 400 | 20 | 24 | большой (3.6×1.0) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:oceanized_enderina` (Enderina the Peramble) | босс | 385 | 14 | 15 | обычный (1.95×0.75) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:thirster` (Thirster) | босс | 340 | 5 | 15 | обычный (1.75×1.5) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:isharmla` (Isharmla) | босс | 320 | 16 | 12 | обычный (1.8×0.6) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:tide_chimera` (Underdawn Chimera) | босс | 300 | 19 | 8 | большой (4.65×1.8) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:saint_carmen` (Saint Carmen) | сильный моб | 280 | 22 | 8 | обычный (1.8×0.6) | здоровье ≥ 150 |
| `caerula_arbor:lingering_pathshaper` (Path Shaper) | босс | 270 | 10 | 9 | большой (4.0×1.8) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:flamarine_golem` (Flamarine Golem) | босс | 270 | 19 | 23 | обычный (2.75×1.0) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:skadi` (Skadi) | сильный моб | 270 | 38 | 5 | обычный (1.8×0.6) | здоровье ≥ 150 |
| `caerula_arbor:super_slider` (Slider Fish) | босс | 250 | 24 | 20 | обычный (0.8×0.3) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:mega_chest` (Mega Chest) | босс | 240 | 23 | 17 | обычный (0.9×0.8) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:endspeaker_3` (Endspeaker, Will of We Many) | босс | 224 | 11 | 8 | большой (3.38×1.0) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:specter` (Specter) | сильный моб | 218 | 34 | 4 | обычный (1.8×0.6) | здоровье ≥ 150 |
| `caerula_arbor:specter_doll` (Specter Doll) | сильный моб | 218 | 34 | 7 | обычный (1.8×0.6) | здоровье ≥ 150 |
| `caerula_arbor:tiderage_direswine` (Tiderage Direswine) | сильный моб | 218 | 16 | 0 | обычный (1.4×1.0) | здоровье ≥ 150 |
| `caerula_arbor:gladiia` (Gladiia) | сильный моб | 216 | 27 | 9 | обычный (1.8×0.6) | здоровье ≥ 150 |
| `caerula_arbor:oceanized_ravager` (Ravage Shore Podcracker) | сильный моб | 215 | 17 | 6 | обычный (2.5×1.9) | здоровье ≥ 150 |
| `caerula_arbor:highmore` ("Paranoia Illusion") | босс | 210 | 6 | 0 | обычный (1.6×1.4) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:rocinante` (Rocinante) | сильный моб | 200 | 3 | 8 | большой (3.0×1.2) | здоровье ≥ 150 |
| `caerula_arbor:slider_ball_huge` (Slider Ball Huge) | сильный моб | 199 | 1 | 0 | большой (2.0×2.0) | здоровье ≥ 150 |
| `caerula_arbor:chitin_golem` (Chitin Golem) | босс | 185 | 15 | 16 | большой (4.0×1.75) | здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:promoted_elder_guardian` (Allotropic Elder Guardian) | босс | 165 | 14 | 11 | большой (2.0×2.0) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:tide_bishop` (Tidelinked Bishop) | босс | 160 | 9 | 6 | обычный (2.2×1.1) | боссбар в классе; здоровье ≥ 150; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:route_shaper` (Path Shaper) | босс | 140 | 9 | 8 | большой (4.0×1.8) | боссбар в классе; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:endspeaker_2` (Endspeaker, Bloodlogged Nest) | босс | 140 | 9 | 6 | обычный (2.8×1.2) | боссбар в классе; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:compassion_prayer` (Compassion Prayer) | босс | 136 | 7 | 2 | обычный (1.8×0.6) | тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:endspeaker_1` (Endspeaker, Static Spawn) | босс | 120 | 7 | 5 | обычный (1.5×0.75) | боссбар в классе; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:oceanized_brute` (Flame Valley Despot) | босс | 115 | 16 | 5 | обычный (2.0×0.6) | боссбар в классе; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:gunmu` (Mother of Electric Baton) | босс | 114 | 750 | 30 | обычный (1.8×0.6) | боссбар в классе |
| `caerula_arbor:first_to_talk` (The First to Talk) | босс | 112 | 7 | 10 | обычный (2.7×0.9) | тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:oceanized_illusioner` (Bewitched Shore Illusioner) | босс | 108 | 9 | 6 | обычный (1.9×0.6) | боссбар в классе; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:tide_deathrepeller` (Tidelinked Immortal) | босс | 75 | 8 | 10 | обычный (2.0×1.5) | боссбар в классе; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:oceanized_sniffer` (Sniff Shore Archeologist) | босс | 72 | 7 | 0 | большой (2.0×2.0) | боссбар в классе; тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:oceanized_wandering_trader` (Managing Shore Waterside Trader) | босс | 63 | 6 | 6 | обычный (1.8×0.6) | тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `caerula_arbor:endspeaker_0` (Endspeaker, Hateful One) | босс | 16 | 1 | 0 | обычный (0.7×0.65) | тег caerula_arbor:bossoffspring; тег forge:bosses; тег the_bumblezone:bosses |
| `cataclysm:clawdian` (Clawdian) | сильный моб | 225 | 16 | 12 | большой (4.5×2.5) | здоровье ≥ 150 |
| `cataclysm:amethyst_crab` (Amethyst Crab) | сильный моб | 200 | 13 | 10 | большой (2.6×2.5) | здоровье ≥ 150 |
| `cataclysm:kobolediator` (Kobolediator) | сильный моб | 180 | 14 | 10 | большой (4.4×2.4) | здоровье ≥ 150 |
| `cataclysm:aptrgangr` (Aptrgangr) | сильный моб | 160 | 18 | 10 | большой (4.0×2.4) | здоровье ≥ 150 |
| `cataclysm:coralssus` (Coralssus) | сильный моб | 160 | 10 | 5 | большой (2.85×2.75) | здоровье ≥ 150 |
| `cataclysm:the_prowler` (The Prowler) | сильный моб | 160 | 14 | 10 | большой (2.75×2.5) | здоровье ≥ 150 |
| `cataclysm:ender_golem` (Ender Golem) | сильный моб | 150 | 10 | 12 | большой (3.5×2.5) | здоровье ≥ 150 |
| `cataclysm:modern_remnant` (Modern Remnant) | сильный моб | 150 | 6 | 5 | обычный (0.42×0.75) | здоровье ≥ 150 |
| `cataclysm:wadjet` (Wadjet) | сильный моб | 150 | 11 | 5 | большой (3.4×0.85) | здоровье ≥ 150 |
| `cataclysm_spellbooks:phantom_ancient_remnant` (Phantom Remnant) | босс | 450 | 25 | 12 | огромный (5.0×5.0) | боссбар в классе; здоровье ≥ 150 |
| `extrabotany:gaia_legacy` (Guardian of Gaia) | босс | 320 | 2 | 0 | обычный (1.8×0.6) | боссбар в классе; здоровье ≥ 150; тег c:bosses; тег forge:bosses; тег the_bumblezone:bosses |
| `gtbcs_geomancy_plus:geo_grandmaster` (Geo Grandmaster) | сильный моб | 180 | 3 | 15 | обычный (2.0×0.6) | здоровье ≥ 150 |
| `minecraft:elder_guardian` (Elder Guardian) | босс | 80 | 8 | 0 | большой (2.0×2.0) | тег the_bumblezone:essence/calming_arena/boss_enemy |
| `mowziesmobs:bluff` (Bluff) | босс | 45 | 18 | 0 | обычный (1.8×0.6) | боссбар в классе |
| `mowziesmobs:ferrous_wroughtnaut` (Ferrous Wroughtnaut) | босс | 40 | 30 | 0 | большой (3.5×2.5) | боссбар в классе |
| `sarkazs_furnace_side_fables:amiya` (Amiya, Cinder Finale) | босс | 1000 | 20 | 30 | обычный (1.99×0.6) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег sarkazs_furnace_side_fables:dark_bosses; тег the_bumblezone:bosses |
| `sarkazs_furnace_side_fables:kuilong` (Kuilong, Mahasattva Manifestation) | босс | 1000 | 70 | 30 | обычный (1.99×0.6) | здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `sarkazs_furnace_side_fables:tuosheng_lianzuo_special` (Special Tuosheng Lianzuo) | сильный моб | 512 | 0 | 5 | большой (1.8×2.7) | здоровье ≥ 150 |
| `sarkazs_furnace_side_fables:last_bit_of_creativity` (Last Bit of Creativity) | сильный моб | 325 | 200 | 5 | обычный (0.8×0.8) | здоровье ≥ 150 |
| `sarkazs_furnace_side_fables:banished_black_coffin` (Banished Black Coffin) | сильный моб | 150 | ≈/не извлечено | 10 | обычный (2.0×1.0) | здоровье ≥ 150 |
| `torchesbecomesunlight:pursuer` ("Emperor's Blade", Pursuer) | босс | 400 | 16 | 10 | большой (3.5×1.0) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `torchesbecomesunlight:gravestone` ("Gravestone") | босс | 380 | 8 | 10 | обычный (1.8×0.75) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `torchesbecomesunlight:rosmontis` (Rosmontis) | босс | 300 | 12 | 2 | обычный (1.6×0.45) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `torchesbecomesunlight:gun_knight_patriot` (Buldrokkas'tee, Holy Gun-Knight) | босс | 300 | 15 | 20 | большой (4.0×1.8) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `torchesbecomesunlight:frost_nova` (FrostNova) | босс | 300 | 12 | 2 | обычный (2.0×0.5) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `torchesbecomesunlight:patriot` (Patriot) | босс | 300 | 20 | 20 | большой (4.0×1.8) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `torchesbecomesunlight:crazelyseon_proxy` (Endless Resonance Kreson) | сильный моб | 300 | 15 | 20 | огромный (52.0×52.0) | здоровье ≥ 150 |
| `torchesbecomesunlight:demons_blade` ("Demons Blade") | сильный моб | 300 | 18 | 16 | обычный (2.8×0.9) | здоровье ≥ 150 |
| `torchesbecomesunlight:red` (Red) | босс | 280 | 12 | 16 | обычный (2.4×0.8) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `torchesbecomesunlight:shattered_champion` (Shattered Champion) | сильный моб | 240 | 18 | 12 | обычный (2.1×0.8) | здоровье ≥ 150 |
| `torchesbecomesunlight:fallen_snowpriest` (Fallen Snowpriest) | сильный моб | 220 | 14 | 8 | обычный (1.8×0.6) | здоровье ≥ 150 |
| `torchesbecomesunlight:pathfinder_ballistarius` (Pathfinder Line Breaker) | сильный моб | 200 | 16 | 12 | большой (3.0×1.2) | здоровье ≥ 150 |
| `torchesbecomesunlight:war_phantom` (War Phantom) | сильный моб | 180 | 12 | 8 | обычный (2.6×1.0) | здоровье ≥ 150 |
| `torchesbecomesunlight:burdenbeast` (Burdenbeast) | сильный моб | 160 | 20 | 8 | большой (2.8×2.8) | здоровье ≥ 150 |
| `traveloptics:enraged_dead_king` (§c§lEnraged Dead King) | босс | 600 | 14 | 20 | большой (3.5×0.9) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `traveloptics:the_nightwarden` (The Nightwarden) | босс | 600 | 18 | 10 | большой (3.5×1.6) | боссбар в классе; здоровье ≥ 150; тег forge:bosses; тег the_bumblezone:bosses |
| `traveloptics:fading_mage` (Fading Mage) | сильный моб | 600 | 18 | 10 | обычный (2.4×0.8) | здоровье ≥ 150 |
| `traveloptics:the_nightwarden_defeated` (The Nightwarden) | сильный моб | 600 | 18 | 10 | обычный (2.1×1.7) | здоровье ≥ 150 |
| `traveloptics:summoned_atlatitan` (Summoned Atlatitan) | сильный моб | 400 | 8 | 0 | огромный (8.0×5.0) | здоровье ≥ 150 |
| `traveloptics:summoned_hullbreaker` (Summoned Hullbreaker) | сильный моб | 400 | 16 | 0 | огромный (4.5×4.65) | здоровье ≥ 150 |
| `traveloptics:test_wizard` (Test Wizard) | сильный моб | 300 | 3 | 0 | обычный (1.8×0.6) | здоровье ≥ 150 |
| `traveloptics:summoned_forsaken` (Summoned Forsaken) | сильный моб | 250 | 10 | 0 | большой (4.0×3.0) | здоровье ≥ 150 |
| `traveloptics:summoned_clawdian` (Summoned Clawdian) | сильный моб | 225 | 16 | 12 | большой (4.5×2.5) | здоровье ≥ 150 |
| `traveloptics:summoned_kobolediator` (Summoned Kobolediator) | сильный моб | 180 | 14 | 10 | большой (4.4×2.4) | здоровье ≥ 150 |
| `traveloptics:summoned_aptrgangr` (Summoned Aptrgangr) | сильный моб | 160 | 18 | 10 | большой (4.0×2.4) | здоровье ≥ 150 |
| `traveloptics:summoned_the_prowler` (Summoned The Prowler) | сильный моб | 160 | 14 | 10 | большой (2.75×2.5) | здоровье ≥ 150 |
| `traveloptics:summoned_ender_golem` (Summoned Ender Golem) | сильный моб | 150 | 10 | 12 | большой (3.5×2.5) | здоровье ≥ 150 |
| `traveloptics:summoned_gum_worm` (Summoned Gum Worm) | сильный моб | 150 | 9 | 10 | большой (2.7×3.5) | здоровье ≥ 150 |
| `traveloptics:summoned_tremorsaurus` (Summoned Tremorsaurus) | сильный моб | 150 | 14 | 8 | большой (3.85×2.5) | здоровье ≥ 150 |
| `traveloptics:summoned_wadjet` (Summoned Wadjet) | сильный моб | 150 | 11 | 5 | большой (3.4×0.85) | здоровье ≥ 150 |
| `twilightforest:plateau_boss` (Plateau Boss) | босс | 20 | 2 | 0 | обычный (1.0×1.0) | тег forge:bosses; тег the_bumblezone:bosses; тег twilightforest:bosses |
