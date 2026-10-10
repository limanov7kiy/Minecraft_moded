# Electroblob's Wizardry: перенос недостающего

В сборке — **Electroblob's Wizardry Redux 0.8.9** (пересоздание мода под 1.20.1, alpha, активно развивается).
Сравнение с оригиналом 1.12.2 (Electroblob77/Wizardry, `assets/ebwizardry/spells/*.json` — 189 заклинаний)
и исходниками Redux (Binaris00/ElectroblobsWizardryRedux, коммит 27156de от 2026-10-07).

## Лицензии — как переносим
- **Оригинал**: собственная лицензия Electroblob. Форки и изменения разрешены **только на GitHub**;
  распространять код/ассеты где-либо ещё — только с письменного разрешения автора. Коммерция запрещена.
- **Redux**: GPL-3.0 (на Modrinth; в gradle.properties указан MIT).
- Поэтому недостающее делаем **«чистой комнатой»**: новый код в стиле Redux, поведение — по описаниям
  заклинаний; числовые параметры (стоимость, перезарядка, урон) — игровые характеристики. Код и текстуры
  оригинала не копируем. Иконки и звуки — свои (или ссылки на звуки, уже присутствующие у игрока).
- Вариант на будущее: попросить у Electroblob разрешение — тогда можно переносить и ассеты.

## Архитектура
Отдельный мод-дополнение **`wizardry_restored`** (`mods-src/wizardry-restored`), зависит от Redux.
Регистрирует заклинания в реестре Redux `ebwizardry:spell` под своим namespace. Плюсы:
переживает обновления Redux; когда Redux сам добавит заклинание — наше удаляем.
Кандидаты на отправку в Redux pull request'ами (GPL), чтобы не расходиться с апстримом.

## Недостающие заклинания (61)
Переименованы, но есть: `conjure_armour`→`conjure_armor`, `darkvision`→`dark_vision`, `fireskin`→`fire_skin`.

| Уровень | Стихия | Заклинания |
|---|---|---|
| novice | earth | mine, snare |
| novice | ice | snowball |
| novice | necromancy | mind_trick |
| novice | sorcery | light, muffle |
| apprentice | earth | grapple, sixth_sense, summon_spirit_wolf |
| apprentice | healing | empowering_presence, mark_sacrifice, shield |
| apprentice | ice | frost_barrier, ice_statue |
| apprentice | lightning | lightning_ray |
| apprentice | necromancy | intimidate, metamorphosis |
| apprentice | sorcery | blink, clairvoyance, divination, imbue_weapon |
| advanced | earth | glide, summon_spirit_horse |
| advanced | fire | disintegration, greater_fireball |
| advanced | healing | forcefield, ironflesh, radiant_totem, transience |
| advanced | ice | freezing_weapon, ice_shroud, summon_ice_wraith |
| advanced | lightning | chain_lightning, lightning_bolt, lightning_disc, paralysis, stormcloud |
| advanced | necromancy | mind_control, shadow_ward, withering_totem |
| advanced | sorcery | decoy, mirage, petrify, transportation |
| master | earth | earthquake, forest_of_thorns |
| master | fire | summon_phoenix |
| master | healing | celestial_smite, diamondflesh, resurrection |
| master | ice | wall_of_frost |
| master | lightning | lightning_hammer, lightning_web, summon_storm_elemental, thunderstorm |
| master | necromancy | possession, summon_shadow_wraith |
| master | sorcery | black_hole, shockwave, slow_time, summon_iron_golem |

(В исходнике Redux стоят комментарии-заглушки `// decoy`, `// lightning web`, `// summon storm elemental`,
`// earthquake` — автор планирует их сам; при выходе — убираем свои.)

## Отключены на выделенном сервере
Redux помечает «тестовые» заклинания `SENSIBLE` и удаляет их на выделенном сервере, т.е. в нашей сетевой
игре их нет: **lightning_pulse, summon_ice_giant, conjure_armor, silverfish_swarm** (+ служебные
ojospocos, block_surprise, player_hand, wizard_hand). Вернуть — после проверки, что они не ломаются.

## Предметы
Почти все предметы оригинала есть под новыми именами (`wand_novice_fire` вместо `novice_fire_wand`,
`magic_crystal_*`, `gilded_*_wood` и т.д.). Отсутствуют только предметы, привязанные к недостающим
заклинаниям: кольцо `ring_spirit_animal`, `transportation_stone`, `petrified_stone`, `ice_statue`,
`magic_light`, `thorns`, `snare`, `vanishing_cobweb`, `spectral_block`, а также праздничные
(`festive_*`).

## Дополнения
На Modrinth дополнений для Redux пока нет; на CurseForge есть Spell Forge for Electroblob's Wizardry:
Redux, Wizardry Golems, Wizardry — Necromancer's Delight (проверить совместимость с 0.8.9).
Аддоны оригинала 1.12.2 (Ancient Spellcraft и др.) на 1.20.1 не существуют.

## Сделано (Wizardry Restored 0.4.0 — 38 из 61 + 4 «тестовых» Redux)
| Заклинание | Уровень, стихия | Как сделано |
|---|---|---|
| lightning_bolt | advanced, молния | ванильная молния в точку взгляда (нужно открытое небо) |
| sixth_sense | apprentice, земля | свечение существ вокруг (упрощение: видно всем рядом) |
| shockwave | master, колдовство | урон + отбрасывание вокруг заклинателя |
| snowball | novice, лёд | ванильный снежок |
| mine | novice, земля | ломает блок по взгляду; сила → твёрже блоки; уважает защиту территорий |
| mind_trick | novice, некромантия | цель сбрасывает агрессию, тошнота + слабость |
| blink | apprentice, колдовство | короткий телепорт с поиском безопасного места |
| intimidate | apprentice, некромантия | существа бросают цель и убегают, слабость |
| glide | advanced, земля | непрерывное: планирование вместо падения |
| ironflesh / diamondflesh | advanced/master, исцеление | бафф Сопротивление I/III + замедление (механика BuffSpell Redux) |
| greater_fireball | advanced, огонь | большой огненный шар (разрушение — по mobGriefing) |
| chain_lightning | advanced, молния | цель + до 5 соседей + до 2 от каждого, урон 10/8/6 |
| paralysis | advanced, молния | луч (RaySpell Redux) + эффект паралича Redux; на игроков вдвое короче |
| summon_iron_golem | master, колдовство | призыв голема на 30 с (MinionSpell Redux) |
| lightning_ray | apprentice, молния | непрерывный луч, урон раз в 0,5 с |
| disintegration | advanced, огонь | луч: урон + поджог; убитая цель рассыпается углями, поджигая соседей |
| transience | advanced, исцеление | невидимость + Сопротивление V (неуязвимость) + слабость и усталость (почти не вредит) |
| mind_control | advanced, некромантия | моб (не босс, ≤60 HP) 30 с воюет за заклинателя и не трогает его союзников |
| metamorphosis | apprentice, некромантия | превращение: корова↔грибная, зомби↔кадавр, скелет↔зимогор, паук↔пещерный, слизень↔магмовый, житель↔ведьма, свинья↔хоглин, лошади |
| frost_barrier | apprentice, лёд | стена из 15 блоков плотного льда на 20 с (только в воздух, потом исчезает) |
| wall_of_frost | master, лёд | непрерывное: наращивает лёд по взгляду, тает через 30 с |
| thunderstorm | master, молния | 10 молний по врагам вокруг + разряды на соседей |
| earthquake | master, земля | наземные существа вокруг подброшены, ранены, замедлены |
| celestial_smite | master, исцеление | столп света: урон и поджог в радиусе, нежити двойной урон |
| slow_time | master, колдовство | враги почти замирают (замедление V, усталость, слабость), их снаряды тормозят |
| light | novice, колдовство | ванильный блок света на 30 с |
| shadow_ward | advanced, некромантия | непрерывное: половина входящего урона возвращается нападающему |
| ice_shroud | advanced, лёд | 30 с: напавшие получают обморожение Redux |
| summon_spirit_wolf | apprentice, земля | волк-миньон (механика Redux), пока не убьют |
| summon_spirit_horse | advanced, земля | осёдланный быстрый конь, исчезает через 30 с без всадника, без дропа |
| freezing_weapon | advanced, лёд | 45 с: удары в ближнем бою накладывают обморожение Redux (вместо «первого оружия на хотбаре») |
| empowering_presence | apprentice, исцеление | заклинатель и союзники: +15% к атрибуту Redux `cast.potency` на 45 с |
| muffle | novice, колдовство | 30 с: мобы дальше 6 блоков не берут заклинателя в цель |
| grapple | apprentice, земля | мгновенный рывок: существо — к заклинателю, или заклинатель — к блоку (без сущности-лозы) |
| transportation | advanced, колдовство | Shift — запомнить точку, обычное — вернуться (то же измерение); круг камней перемещения — этап 4 |
| ice_statue | apprentice, лёд | паралич + обморожение Redux + ледяная оболочка (временные блоки) на 20 с |
| petrify | advanced, колдовство | паралич + Сопротивление IV + замедление на 45 с, не на боссов (без каменной статуи) |
| lightning_pulse, summon_ice_giant, conjure_armor, silverfish_swarm | — | классы Redux под нашим именем без метки SENSIBLE — работают на выделенном сервере (Redux свои удаляет) |

Временные блоки (лёд, свет) убираются по таймеру и при остановке сервера (`util/TempBlocks`); длительные
состояния (заслон, покров, контроль, конь) хранятся в Forge-NBT существа и переживают перезапуск (`util/SpellStates`).

**Проверено в мире** (`scripts/tests/castcheck.js`): все 31 заклинание кастуются фейковым игроком Forge на
сервере, результат проверяется (поставлен лёд/свет, призваны существа, урон и эффекты на зомби, превращение
коровы, телепорт и т.д.) — 42/42 OK (0.4.0). Звук и графику на клиенте — проверить игроку.

## Точка остановки (для нового чата потока 1)
- Состояние: **0.4.0**, jar `pack/mods/wizardry-restored-0.4.0.jar`; тесты `spellcheck.js` (42 в реестре) и
  `castcheck.js` (42/42) — запуск: `TEST_COMMANDS=<файл с «#sleep 40»> EXTRA_SERVER_SCRIPTS=<папка с копиями
  scripts/tests/spellcheck.js и castcheck.js> scripts/test-server.sh <папка вне репо>`.
- Сборка: `cd mods-src/wizardry-restored && ./gradlew build` (JDK 17 качает foojay), иконки —
  `python3 tools/make_icons.py`, версия — `gradle.properties`; старый jar в `pack/mods/` удалить, новый
  положить, `packwiz refresh` в `pack/`.
- Особенности теста: фейковый игрок не добавлен в мир (поиск сущностей вокруг его не находит — заклинателя
  обрабатывать отдельно); телепорт ему делать через `setPos` + `teleportTo`; состояния мода — в Forge-NBT
  (`String(e.nbt)`), а не `persistentData` KubeJS; углы — `fake.yRot = …`.
- **Следующий шаг — этап 3** (новые сущности/блоки), осталось 23 заклинания:
  snare, shield, forcefield, radiant_totem, withering_totem, decoy, mirage, black_hole, stormcloud,
  lightning_disc, lightning_web, lightning_hammer, forest_of_thorns, summon_ice_wraith, summon_shadow_wraith,
  summon_storm_elemental, summon_phoenix, resurrection, possession, clairvoyance, divination, imbue_weapon,
  mark_sacrifice. Начать с простых сущностей-конструкций (forcefield, decoy, totems, black_hole, stormcloud):
  регистрировать `EntityType` в `WizardryRestored` (DeferredRegister ENTITY_TYPES), рендер — клиентский класс
  (`Dist.CLIENT`), модели упрощённые (fal недоступен — ассеты рисовать скриптами или из ванильных частей).
  Заменить упрощения 0.4.0 (grapple без лозы, petrify без статуи, transportation без камней) — этап 3/4.

## План
1. Каркас дополнения + первые заклинания без новых сущностей/блоков (вертикальный срез, проверка на сервере).
2. Заклинания на существующих механиках Redux (лучи, снаряды, призывы, баффы).
3. Заклинания, требующие новых сущностей/блоков (phoenix, storm elemental, black hole, ice statue…).
4. Предметы-артефакты, связанные с ними.
