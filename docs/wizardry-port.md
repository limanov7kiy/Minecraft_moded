# Electroblob's Wizardry: перенос недостающего

В сборке — **Electroblob's Wizardry Redux 0.8.9** (пересоздание мода под 1.20.1, alpha, активно развивается).
Сравнение с оригиналом 1.12.2 (Electroblob77/Wizardry, `assets/ebwizardry/spells/*.json` — 189 заклинаний)
и исходниками Redux (Binaris00/ElectroblobsWizardryRedux, коммит 27156de от 2026-10-07).

## Лицензии — как переносим
- **Оригинал**: собственная лицензия Electroblob. Форки и изменения разрешены **только на GitHub**;
  распространять код/ассеты где-либо ещё — только с письменного разрешения автора. Коммерция запрещена.
- **Redux**: GPL-3.0 (на Modrinth; в gradle.properties указан MIT).
- Сделанное до 0.5.0 написано «чистой комнатой» (свой код в стиле Redux, свои иконки, ванильные звуки).
- **С 2026-10-10 (решение пользователя, см. CLAUDE.md «Назначение и лицензии»):** сборка частная и
  некоммерческая, поэтому код, текстуры и звуки оригинала 1.12.2 переносить можно — с указанием авторства
  Electroblob. Ограничение: не публиковать это вне приватного репозитория и круга друзей
  (в том числе не отправлять перенесённое в Redux pull request'ами).

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

## Сделано (Wizardry Restored 0.6.0 — все 61 + 4 «тестовых» Redux + артефакты)
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
| grapple | apprentice, земля | непрерывное (0.6.0): лоза-сущность с линией летит по взгляду; зацепилась за блок — тянет заклинателя, за существо — тянет его (тяжёлое — наоборот) |
| transportation | advanced, колдовство | (0.6.0) круг из 4 камней перемещения (блок `transportation_stone`, рецепт: 4 каменных кирпича + магический кристалл → 4) в 2 бл. по сторонам света; Shift в центре — запомнить, обычное — перенестись (и в другое измерение); разрушенный круг забывается |
| ice_statue | apprentice, лёд | (0.6.0) существо на 20 с становится ледяной статуей (`StatueEntity`), потом — обморожение; игрок — паралич + обморожение + ледяная оболочка |
| petrify | advanced, колдовство | (0.6.0) существо на 45 с — каменная статуя (NBT существа внутри, рендер его модели текстурой камня; разбить — убить); игрок — паралич + Сопротивление IV, не на боссов |
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

### Этап 4 (0.6.0): артефакты
Предметы есть в Redux 0.8.9, но без действия (Redux пишет на них «This item is not working…» — для оживлённых
эту строку убирает `client/ArtifactTooltips`). Ношение — `ArtifactChannel.isEquipped` (в сборке Curios).

| Артефакт | Действие |
|---|---|
| ring_stormcloud | туча идёт к ближайшему врагу, +1 с жизни за каждый удар |
| charm_black_hole | чёрная дыра поглощает блоки (твёрдость < 50, без блок-сущностей; mobGriefing и `BlockEvent.BreakEvent` от заклинателя — защита территорий работает) |
| ring_hammer | упавший молот подбирается (предмет Redux `lightning_hammer`, живёт сколько оставалось молоту) и мечется правым кликом |
| ring_interdiction / ring_defender | поле ранит касающихся врагов / пропускает свои и союзные снаряды (без кольца поле задерживает все) |
| charm_light | свет постоянный; Shift — погасить свет вокруг точки взгляда |
| charm_silk_touch | mine роняет сам блок |
| charm_stop_time | slow_time ещё и парализует, снаряды замирают |
| charm_transportation | до 4 кругов; Shift вне круга — переключить пункт |
| charm_abseiling | Shift при зацепленной лозе — стравливать её (спуск) |
| amulet_auto_shield | 25% при уроне — щит на 3 с |
| amulet_resurrection | смерть отменяется (½ здоровья), если на палочке в инвентаре есть «Воскрешение»; раз в 20 мин |
| ring_mind_control | подконтрольное существо с шансом 20% подчиняет того, кого бьёт |
| ring_disintegration | жертвы огненных заклинаний рассыпаются углями, поджигая соседей |
| amulet_anchoring | толчки наших заклинаний (поле, дыра, лоза, ударная волна, землетрясение) носителя не двигают |
| amulet_fire_cloaking | урон ×0,25 внутри своего/союзного огненного кольца Redux |
| amulet_frost_warding | рядом со льдом (стены мороза) — эффект «Защита» Redux |
| amulet_banishing | 15% — напавший вблизи переносится в случайное место рядом |

Не сделаны: `charm_sixth_sense` (клиентская подсветка), `charm_hunger_casting` (внутренности маны Redux),
`ring_soulbinding` (в Redux уже есть `SoulBindingRingEffect`).

Временные блоки (лёд, свет) убираются по таймеру и при остановке сервера (`util/TempBlocks`); длительные
состояния (заслон, покров, контроль, конь) хранятся в Forge-NBT существа и переживают перезапуск (`util/SpellStates`).

**Проверено в мире** (`scripts/tests/castcheck.js`, 0.6.0 — **69/69 OK**, в реестре 185 заклинаний; артефакты
надеваются фейковому игроку в слот Curios): все заклинания
кастуются фейковым игроком Forge; для конструкций тест сам вызывает их тик (`AreaConstruct.tickAt`) и проверяет
действие (выталкивание, урон, затягивание, перехват цели двойником, снятие метки, путь ясновидения и т.д.).
Ранее (0.4.0): все 31 заклинание кастуются фейковым игроком Forge на
сервере, результат проверяется (поставлен лёд/свет, призваны существа, урон и эффекты на зомби, превращение
коровы, телепорт и т.д.) — 42/42 OK (0.4.0). Звук и графику на клиенте — проверить игроку.

## Точка остановки (для нового чата потока 1)
- Состояние: **0.6.0** — все 61 заклинание + 4 «тестовых» Redux, этап 3 (сущности) и этап 4 (статуи, лоза,
  камни перемещения, 18 артефактов) сделаны; jar `pack/mods/wizardry-restored-0.6.0.jar`; `castcheck.js` 69/69.
  Запуск теста: `TEST_COMMANDS=<файл с «#sleep 40»> EXTRA_SERVER_SCRIPTS=<папка с копиями scripts/tests/spellcheck.js
  и castcheck.js> scripts/test-server.sh <папка вне репо>`.
- Сборка: `cd mods-src/wizardry-restored && ./gradlew build` (Maven Central иногда отвечает 429 — повторить),
  иконки/текстуры — `python3 tools/make_icons.py`, версия — `gradle.properties`; старый jar из `pack/mods/` удалить,
  новый положить, `packwiz refresh` в `pack/` (packwiz: `go install github.com/packwiz/packwiz@latest`).
- **Осторожно с `packwiz refresh`:** правило `world*/` в `.gitignore` на ветке до коммита `2a6d6a4` прячет
  `pack/kubejs/data/mm_space/worldgen/`, и refresh без этих файлов выкидывает их из `index.toml`. Пока исправление
  потока 3 не влито — перед refresh подложить их (`git archive origin/claude/determined-goodall-fs240t
  pack/kubejs/data/mm_space/worldgen | tar -x`), после — удалить; тестовый сервер без них не стартует.
- Особенности теста: фейковый игрок не в мире — `getCaster()` конструкций его не находит (эффекты колец на
  конструкциях тестом не проверить); телепорт — `setPos` + `teleportTo`; состояния — в Forge-NBT (`String(e.nbt)`);
  Rhino не видит SRG-имена в наших классах (`tickCount`, `hurt`, `getUUID`) — есть `tickAt(t)`, урон — `/damage`.
- Redux 0.8.9: `MinionSpell.flying(true)` ставит миньонов на абсолютную высоту y=2 — не использовать.
- **Лицензия больше не ограничивает** (CLAUDE.md «Назначение и лицензии»): код/ассеты оригинала Electroblob
  можно переносить с указанием авторства, только в приватном репо. Всё до 0.6.0 — свой код и свои упрощённые
  ассеты. **Следующие шаги потока 1:**
  1. Заменить упрощённые ассеты оригинальными (с пометкой «© Electroblob, Electroblob's Wizardry» в `docs/` и
     рядом с файлами): иконки заклинаний, модели/текстуры тотемов, феникса, чёрной дыры, силового поля, молота,
     камня перемещения; звуки, которых нет в Redux.
  2. Сверить параметры (стоимость, перезарядка, урон, длительности) с `spells/*.json` оригинала и поправить.
  3. Доделать по оригиналу упрощённое: possession (подмена камеры/способностей), resurrection своей рукой из
     экрана смерти, decoy/mirage, charm_sixth_sense, charm_hunger_casting.
  4. Клиент (поток 5): рендер новых сущностей, статуй (подмена текстуры), лозы, подсказки артефактов, звуки.

## План
1. Каркас дополнения + первые заклинания без новых сущностей/блоков (вертикальный срез, проверка на сервере).
2. Заклинания на существующих механиках Redux (лучи, снаряды, призывы, баффы).
3. ~~Заклинания, требующие новых сущностей/блоков~~ — **сделано** в 0.5.0.
4. ~~Предметы-артефакты, связанные с ними~~ — **сделано** в 0.6.0 (кроме трёх, см. выше).
