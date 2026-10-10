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
На Modrinth дополнений для Redux пока нет. С CurseForge в сборке **Spell Forge for Electroblob's Wizardry:
Redux 1.1.0** (`cf spell-forge-for-electroblobs-wizardry-redux`, сторона both): сервер стартует, в реестре
162 заклинания (было 151), castcheck 42/42. Wizardry Golems и Wizardry — Necromancer's Delight выпущены
только для 1.12.2, на 1.20.1 их нет. Аддоны оригинала 1.12.2 (Ancient Spellcraft и др.) на 1.20.1 тоже не существуют.

## Сделано (Wizardry Restored 0.5.0 — все 61 + 4 «тестовых» Redux)
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

### Этап 3 (0.5.0): новые сущности
Конструкции — наследники `MagicConstructEntity` Redux (заклинатель, время жизни, множитель силы хранятся там),
общая основа `entity/AreaConstruct`; заклинания-конструкции — на `ConstructSpell`/`ConstructRangedSpell` Redux
(их могут кастовать и существа/раздатчики). Модели упрощённые: столбики ванильных блоков (`client/BlockStackRenderer`),
двойник — модель игрока со скином заклинателя (`client/DecoyRenderer`), феникс — модель ифрита со своей
текстурой (рисует `tools/make_icons.py`), поле/туча/диск — только частицы. Звуки — готовые события Redux
`ebwizardry:spell.*` (в Redux уже есть звуки почти всех заклинаний оригинала).

| Заклинание | Уровень, стихия | Как сделано |
|---|---|---|
| forcefield | advanced, исцеление | сфера R=3 на 30 с: чужие снаряды внутри рассыпаются, враги выталкиваются |
| radiant_totem | advanced, исцеление | тотем 30 с: раз в секунду лечит своих (R=5), дважды в секунду бьёт лучом ближайшего врага в прямой видимости (нежить ×2) |
| withering_totem | advanced, некромантия | тотем 30 с: раз в секунду иссушение + урон врагам вокруг, половина отнятого — заклинателю |
| black_hole | master, колдовство | висит 20 с, затягивает врагов, предметы, опыт и снаряды (R=6), у центра — урон; блоки не вырывает |
| stormcloud | advanced, молния | туча плывёт над головами по взгляду 15 с, раз в 0,5 с бьёт током врагов под собой (без настоящих молний) |
| decoy | advanced, колдовство | двойник (скин, броня, предметы — копии без выпадения) 30 с, враги переключаются на него; заклинатель невидим 5 с |
| mirage | advanced, колдовство | тот же двойник-иллюзия скачет вокруг заклинателя 20 с, удары проходят сквозь; заклинатель невидим |
| summon_ice_wraith / summon_shadow_wraith / summon_storm_elemental | advanced/master | призыв сущностей Redux (они есть в 0.8.9, а заклинаний нет), `MinionSpell` |
| summon_phoenix | master, огонь | своя сущность-феникс (поведение ифрита), один раз возрождается из пепла, 45 с |
| shield | apprentice, исцеление | непрерывное: снаряды и магия спереди отменяются (`LivingAttackEvent`), Сопротивление I |
| lightning_web | master, молния | непрерывное: каждые 0,5 с дуга в цель → до 3 соседей → по 2 от каждого |
| imbue_weapon | apprentice, колдовство | меч — Острота II, лук — Сила II на 45 с через механизм временных чар Redux (`ImbuementEnchantData`) |
| mark_sacrifice | apprentice, исцеление | свой эффект «Метка жертвы»: следующая магическая атака +60% за уровень, метка снимается |
| snare | novice, земля | ловушка на земле (5 мин): первый враг — урон + замедление VI на 5 с |
| forest_of_thorns | master, земля | два кольца колючих кустов на 20 с: урон и замедление прижавшимся врагам |
| lightning_disc | advanced, молния | диск летит по взгляду, доворачивает к врагам, прорезает каждого один раз, гаснет о стену |
| lightning_hammer | master, молния | молот падает с неба в точку взгляда: разряд + отбрасывание (R=5, видимая молния без пожара), потом 30 с бьёт током ближайшего |
| clairvoyance | apprentice, колдовство | Shift — метка блока; обычное — путь (штатный поиск пути существ, до 64 бл.) искрами 90 с, видно только заклинателю |
| divination | apprentice, колдовство | руды (`forge:ores`) в кубе R=16: указывает искрами и подсказкой на ближайшую или (шанс растёт с силой) самую ценную |
| resurrection | master, исцеление | игрок, лежащий мёртвым (не нажал «Возродиться») в 32 бл., встаёт рядом с заклинателем с половиной здоровья (штатное возрождение сервера; вещи остаются на месте смерти). На клиенте может остаться экран смерти — кнопка «Возродиться» его просто закроет |
| possession | master, некромантия | вселение в существо (не босс, ≤60 HP) на 30 с: заклинатель невидим, тело без разума и неуязвимо повторяет его движения (держится чуть позади, чтобы не мешать прицелу); монстры не трогают «своего». Без подмены камеры/способностей. Shift — выйти |

Временные блоки (лёд, свет) убираются по таймеру и при остановке сервера (`util/TempBlocks`); длительные
состояния (заслон, покров, контроль, конь) хранятся в Forge-NBT существа и переживают перезапуск (`util/SpellStates`).

**Проверено в мире** (`scripts/tests/castcheck.js`, 0.5.0 — **65/65 OK**, в реестре 185 заклинаний): все заклинания
кастуются фейковым игроком Forge; для конструкций тест сам вызывает их тик (`AreaConstruct.tickAt`) и проверяет
действие (выталкивание, урон, затягивание, перехват цели двойником, снятие метки, путь ясновидения и т.д.).
Ранее (0.4.0): все 31 заклинание кастуются фейковым игроком Forge на
сервере, результат проверяется (поставлен лёд/свет, призваны существа, урон и эффекты на зомби, превращение
коровы, телепорт и т.д.) — 42/42 OK (0.4.0). Звук и графику на клиенте — проверить игроку.

## Точка остановки (для нового чата потока 1)
- Состояние: **0.5.0** — все 61 заклинание оригинала + 4 «тестовых» Redux; jar `pack/mods/wizardry-restored-0.5.0.jar`;
  тесты `spellcheck.js` (65 в реестре, всего 185) и `castcheck.js` (65/65) — запуск: `TEST_COMMANDS=<файл с «#sleep 40»>
  EXTRA_SERVER_SCRIPTS=<папка с копиями scripts/tests/spellcheck.js и castcheck.js> scripts/test-server.sh <папка вне репо>`.
- Сборка: `cd mods-src/wizardry-restored && ./gradlew build` (JDK 17 качает foojay; Maven Central иногда отвечает 429 —
  просто повторить), иконки/текстуры — `python3 tools/make_icons.py`, версия — `gradle.properties`; старый jar в
  `pack/mods/` удалить, новый положить, `packwiz refresh` в `pack/` (packwiz: `go install github.com/packwiz/packwiz@latest`).
- Особенности теста: фейковый игрок не добавлен в мир (поиск сущностей вокруг его не находит — конструкции считают
  его «чужим»); телепорт ему — `setPos` + `teleportTo`; состояния мода — в Forge-NBT (`String(e.nbt)`); углы —
  `fake.yRot = …`. Rhino не видит поля/методы под SRG-именами в наших классах (`tickCount`, `hurt`, `getUUID`) —
  для тестов есть `AreaConstruct.tickAt(t)`, урон — командой `/damage`.
- Тест сервера в ветке без коммита `2a6d6a4` (данные `pack/kubejs/data/mm_space/worldgen/` скрыты правилом `world*/`
  в `.gitignore`) падает на загрузке реестров — исправлено в ветке потока 3, до слияния подкладывать эти файлы в
  папку тестового сервера (`git archive origin/claude/determined-goodall-fs240t pack/kubejs/data/mm_space/worldgen`).
- Найдено в Redux 0.8.9: `MinionSpell.flying(true)` ставит миньонов на абсолютную высоту y=2 (баг `atY(2)`) — не
  использовать; кандидат на исправление pull request'ом в Redux.
- **Следующий шаг — этап 4** (артефакты) и замена упрощений:
  1. Упрощения 0.4.0: **petrify** и **ice_statue** → общая сущность-статуя (хранит NBT существа, рендер модели существа
     с текстурой камня/льда, по таймеру — существо обратно, разбить статую — убить); **grapple** → снаряд-лоза
     с линией (как леска), подтягивание пока удерживается; **transportation** → блок «камень перемещения»
     (круг из 4 камней вокруг точки — пункт назначения, рецепт из ванильных материалов + магический кристалл Redux).
  2. Артефакты: предметы уже есть в Redux, но без эффекта (`NotImplementedItems`); проверка ношения —
     `ArtifactChannel.isEquipped(player, EBItems.X.get())`. Связанные с нашими заклинаниями: `ring_stormcloud`
     (туча идёт к целям, дольше живёт за каждый удар), `charm_black_hole` (дыра ломает блоки — с учётом
     mobGriefing), `ring_hammer` (молот можно подобрать и метнуть — предмет `EBItems.LIGHTNING_HAMMER`),
     `ring_interdiction` (поле ранит касающихся), `ring_defender` (свои снаряды проходят сквозь поле — тогда без
     кольца поле должно задерживать все снаряды), `charm_light` (свет навсегда), `charm_silk_touch` (mine роняет
     сам блок), `charm_stop_time` (slow_time останавливает полностью), `charm_transportation` (до 4 кругов),
     `amulet_auto_shield` (25% — щит при уроне), `amulet_resurrection` (воскрешение себя), `ring_mind_control`,
     `ring_disintegration`, `amulet_anchoring` (наши толчки его уважают), `amulet_fire_cloaking`,
     `amulet_frost_warding`, `amulet_banishing`, `ring_soulbinding`, `charm_abseiling` (после лозы grapple).
     Подсказку Redux «не реализовано» на этих предметах снять клиентским `ItemTooltipEvent`.
  3. Клиент (поток 5): проверить модели/частицы новых сущностей, скин двойника, текстуру феникса, звуки.

## План
1. Каркас дополнения + первые заклинания без новых сущностей/блоков (вертикальный срез, проверка на сервере).
2. Заклинания на существующих механиках Redux (лучи, снаряды, призывы, баффы).
3. ~~Заклинания, требующие новых сущностей/блоков~~ — **сделано** в 0.5.0.
4. Предметы-артефакты, связанные с ними.
