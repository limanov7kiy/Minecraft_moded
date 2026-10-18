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

## Порт дополнений 1.12.2: Wizardry Golems и Necromancer's Delight (поток 12)
Решение владельца: портировать «потихоньку» (низкий приоритет, малыми порциями).

### Источники и авторство
| Дополнение | Автор | Исходники | Лицензия | Зависимости в 1.12.2 |
|---|---|---|---|---|
| Wizardry Golems 1.3.3 | WinDanesz (идеи — HatBot) | https://github.com/WinDanesz/WizardryGolems (ветка `1.12.2`, `c68f2d9`) | GPL-3.0 | EBWizardry, WizardryUtils, **Extra Golems** (skyjay1), Baubles |
| Wizardry — Necromancer's Delight | WinDanesz | https://github.com/WinDanesz/WizardryNecromancersDelight (`0e52462`) | GPL-3.0 | EBWizardry, WizardryUtils, **Fish's Undead Rising** (Fish, `mod_lavacow`) |
| Extra Golems 20.1.0.2 (1.20.1 Forge) | skyjay1 / MMD | https://github.com/MinecraftModDevelopmentMods/Extra-Golems (`dev-1.20.1`), CF 233300 файл 5206477 | zlib | — |
| Fish's Undead Rising | Fish | исходников нет; CF 321139, версии 1.12.2 (1.6.x) и **1.16.5 (2.4.1)**, под 1.20.1 нет | ARR; условия: «декомпилировать и менять можно приватно, не перевыкладывать» | — |

Оба дополнения — GPL-3.0: код и ассеты переносим свободно (сохраняем GPL для портов и шапку «© WinDanesz»).
FUR — ARR с явным запретом выкладывать изменённое; по решению владельца (CLAUDE.md «Назначение и лицензии») это
не причина отказываться, но это самый рискованный кусок — при претензии Fish убираем первым.

### Где делаем: отдельные моды
`mods-src/wizardry-golems` и `mods-src/necromancers-delight` (каркас — копия wizardry-restored: ModDevGradle legacyforge
+ Parchment, Redux 0.8.9 `compileOnly`). Почему не пакеты в wizardry-restored: у каждого своя жёсткая зависимость
(Extra Golems / порт FUR), которая не нужна Restored; их можно выключить/убрать (в т.ч. по лицензии) не трогая
Restored; namespace сохраняем оригинальный (`wizardrygolems`, `necromancersdelight`) — ассеты и lang ложатся как есть.

### Опись: Wizardry Golems
- **Заклинания (5):** Earth / Fire / Ice / Lightning Golemancy, Radiant (healing) Golemancy — все `minion`, advanced,
  стоимость 50, перезарядка 260, жизнь 600 т.; на мастер-палочке стихии — «старший» голем (Obsidian / Flame / Winter /
  Maelstrom/Spark / Gold).
- **Миньоны (31):** обычные голем-типы Extra Golems (clay, terracotta, concrete, obsidian, oak log, leaves, hay/straw,
  melon, sandstone, magma, nether brick, furnace, glowstone, mushroom, prismarine, tnt, bookshelf, gold, coal, bone,
  crafting, ice) + свои (thunderstone, lodestone, spark, maelstrom, flame, permafrost, packed ice, snow, winter;
  11 своих текстур). Кастующие (ISpellCaster): Flame, Winter, Spark, Lodestone, Maelstrom, Gold (лечит хозяина).
- **Артефакты (36):** кольца/амулеты/шармы/пояс/голова/плащ — меняют состав призыва (Ancient Emperor → терракотовая армия,
  Charcoal → 2 печных голема и т.п.), эффекты на смерти голема (землетрясение, молния, взрыв, снейры, ледяные сигилы),
  ауры около голема, «Ring of the Golemancer» (вечный голем).
- **Блоки (2):** Living Snow (+ TE, призывает снежных големов; генерация в мире), Permafrost (временный).
- **Предметы:** книга/свиток Golemancy, Bottle of Living Snow; книжные полки Redux (bookshelf items).
- **Лут:** инъекция `chests/dungeon_additions` в сундуки; лут снежного голема. **Звуки:** 2. **Lang:** en, **ru**, zh.
- **Конфиг:** места инъекции лута, измерения Living Snow.

### Опись: Necromancer's Delight
- **Заклинания (20):** bound_bone_sword, bound_frozen_dagger, bound_spectral_dagger, bound_vespa_shield (предметы FUR),
  holy_hand_grenade, ghost_bomb, wail_of_the_banshee (снаряды FUR), summon_vespa, conjure_banshee, conjure_mycosis,
  summon_salamander (мобы FUR), summon_mana_leech, summon_elemental_leech (свои миньоны на модели паразита FUR),
  stink_bomb, frost_bomb (свои снаряды), glowshroom (свой временный блок + блок FUR), corrode, trial_of_mushrooms
  (грибы FUR), carcinize (сундук → мимик FUR).
- **Артефакты (15):** Malice/Necromantic Ward, Amulet of Tenacity/Vigour, Bone Bag, Malevolent Ushabti (мумия FUR),
  Book of Witchcraft, Cursed Scarab, Crown of Pests, Ring of Hatred (Forsaken FUR), Frigidbone Ring, Fallen Legion's
  Ring (Unburied FUR), Ring of the Nameless, Thornbelt, Leechlink, Spell-Sapping Venom, Leech Crystal Amulet.
- **Эффекты (2):** Poisonous Spore, Locusts. **Сущности свои (5):** stink/frost bomb, mana/elemental leech, vespa minion.
- **Зависимость от FUR:** мобы Vespa, Banshee, Mycosis (ZombieMushroom), Salamander, Forsaken, Mummy, Unburied,
  Parasite, Bone Worm, Weta, Mimic; снаряды Holy Grenade, Ghost Bomb, Sonic Bomb, Cactus Thorn; предметы Frozen Dagger,
  Bone Sword, Spectral Dagger, Vespa Shield, Glowshroom/Cordyceps/Veil Shroom; эффекты FUR (Corroded и др.).
  **≈16 из 20 заклинаний и ≈5 артефактов без FUR не работают.**

### Что уже есть / маппинг API
| 1.12.2 | 1.20.1 / Redux 0.8.9 / EG 20.1 |
|---|---|
| `EntityXxxGolemMinion extends GolemBase implements ISummonedCreature` (31 класс) | один `GolemBase.create(level, "golems:<тип>")` + `MinionSpell`/`MinionData` Redux (как наш железный голем в wizardry-restored); свои голем-типы — JSON `data/wizardrygolems/golems/golem/*.json` + модели/текстуры; кастующие — свой `Behavior` (реестр `BEHAVIOR_SERIALIZER` EG) |
| WizardryUtils `SpellDynamicMinion`, `ItemArtefact`+Baubles | `MinionSpell` с подменой фабрики по артефактам/палочке; артефакты — `ArtifactItem`+эффект Redux (как 18 артефактов Restored 0.6.0), Curios вместо Baubles |
| `SpellDynamicConjuration` | `ConjureItemSpell` Redux (`ConjureData` на любой предмет) |
| `SpellProjectile`/`SpellThrowable` | `ProjectileSpell` Redux |
| `spells/*.json` 1.12 | `data/<ns>/spells/*.json` Redux (формат как в Restored) |
| loot inject по конфигу | Forge global loot modifier |
| WorldGen Living Snow | `placed_feature` + biome modifier (только снежные биомы) |

В Redux/Restored аналогов этих заклинаний нет (проверено по реестру 162 заклинаний).

### Этапы
1. **Golems-1:** каркас мода, зависимость Extra Golems 20.1.0.2 в `pack/` (`both`), 5 заклинаний на обычных типах EG,
   lang (en + оригинальный ru), castcheck-тест. ~1–2 порции.
2. **Golems-2:** 11 своих голем-типов (JSON + текстуры оригинала), кастующие голем-behavior'ы. ~2 порции.
3. **Golems-3:** 36 артефактов, Living Snow/Permafrost, лут, JEI (своих типов рецептов нет — не нужен), test-client. ~2–3 порции.
4. **ND-1:** то, что не требует FUR: stink/frost bomb, corrode (свой эффект), glowshroom (свой блок), mana/elemental
   leech (своя простая модель), эффекты Poisonous Spore / Locusts, артефакты без FUR. ~2 порции.
5. **ND-2:** порт нужного подмножества FUR (11 мобов + 4 снаряда + 5 предметов + эффекты) с декомпиляции jar 1.16.5
   2.4.1 (API ближе к 1.20.1) — самый большой кусок (~4–6 порций: на каждого моба сущность, AI, модель, рендер,
   анимации, звуки, лут). Затем оставшиеся 16 заклинаний и 5 артефактов. Альтернатива — подставить похожих мобов,
   уже имеющихся в сборке (вопрос владельцу, см. отчёт).

### Решения и состояние (точка остановки потока 12)
- **Владелец (2026-10-12), ответ на В13: «полный перенос»** — портируем ≈11 нужных мобов Fish's Undead Rising сами и
  переносим Necromancer's Delight целиком. Порядок: Golems → ND без FUR → мобы FUR → остальное ND. Авторство Fish
  (FUR), WinDanesz (ND, Golems), Electroblob сохраняем в файлах и `docs/`. Риск лицензии FUR принят владельцем
  (по требованию правообладателя — убрать).
- **Сделано:** Golems-1 — мод `mods-src/wizardry-golems` 0.1.0 (5 заклинаний Golemancy, тип голема `thunderstone`,
  ассеты и en/ru/zh оригинала, `tools/convert_lang.py`), `pack/mods/wizardry-golems-0.1.0.jar` + Extra Golems 20.1.0.2 (`#jar`).
  ru_ru: `lang_validate` 0 ошибок.
- **Golems-1 проверен** (PR #38). **Golems-2/3 — 0.2.0** (ветка `claude/wizardry-golems-2`): свои големы, касты, 36
  артефактов, лут, Living Snow; сервер 13/13 (docs/compat.md). Не перенесено: книга/свиток Golemancy (заклинания идут в
  обычные книги Redux), мана бутылки Living Snow, интеграция Electric Scatter с Ancient Spellcraft (его нет в сборке).
  Следующий этап — ND-1 (Necromancer's Delight без мобов FUR).
- Прогон теста:
  `TEST_COMMANDS=<файл с «#sleep 45»> EXTRA_SERVER_SCRIPTS=<папка с копией scripts/tests/golemcheck.js> scripts/test-server.sh <папка вне репо>`,
  ждать строк `GOLEMCHECK ... OK` (5 заклинаний + lifetime); затем test-client, docs/compat.md.

### Точка остановки потока 12 (2026-10-13, стоп по лимиту — неделя 75%)
- **Wizardry Golems 0.2.0** — PR #38 (ветка `claude/wizardry-addons-port`): этапы 1–3 готовы, golemcheck 13/13, CLIENT OK;
  интегратор подтянут 2026-10-12 (`62ae839`). Дальше — по сигналу координатора подтянуть интегратор, refresh, тесты, «Ревью».
- **ND-1** — ветка `claude/necromancers-delight` (от `claude/wizardry-addons-port` до слияния интегратора — перед PR влить его):
  - мод-порт FUR `mods-src/fishs-undead-rising` (`mod_lavacow` 0.1.0): эффекты Soiled, Corroded, Infested (паразиты — ND-2),
    модель гриба; ARR, авторство Fish — NOTICE.md;
  - `mods-src/necromancers-delight` 0.1.0: Stink/Frost Bomb (заклинания + гранаты), Corrode, Glowshroom (временный гриб),
    эффекты Poisonous Spore/Locusts, 17 артефактов (работают 5 без FUR: Necromantic/Malice Ward, иммунитеты, Cursed Scarab),
    добыча, ru 86 ключей (lang_validate 0 ошибок); оба jar в `pack/mods/`, refresh сделан.
  - **ND-1 доведён потоком 27 (2026-10-17):** `scripts/tests/ndcheck.js` — 11/11 OK (касты Stink/Frost Bomb, Corrode,
    Glowshroom; бомбы накладывают Soiled/Frost и наносят урон; Corroded на хаске и урон ×1.1; временный гриб — 1 блок;
    Poisonous Spore отравляет свинью; амулет иммунитета к замедлению в слоте Curios). SERVER OK, CLIENT OK.
    Причины прежних провалов теста (сам мод исправлять не пришлось):
    `MagicProjectileEntity.aim` Redux ставит снаряд в `xo/yo/zo` стрелка — фейкового игрока двигать `setPos` + `setOldPosAndRot()`;
    хаски стояли в 0.7 блока от линии луча (`${x}.5` при отрицательном x); `server.persistentData` KubeJS — не CompoundTag
    (фазы — переменной скрипта); строка «Done» в логе — за ~3 мин до первого тика (ModernFix), поэтому `TEST_COMMANDS` с `#sleep 420`.
    Перевод: ru_ru обоих модов — ещё и в ru-пакете (`pack/resourcepacks-src/russian-moded/assets/{necromancersdelight,mod_lavacow}`),
    `lang_validate` 0 ошибок.
  - Прогон: `TEST_COMMANDS=<файл «#sleep 420»> EXTRA_SERVER_SCRIPTS=<папка с копией ndcheck.js> scripts/test-server.sh <папка вне репо>`,
    ждать 11 строк `NDCHECK ... OK`.
  - **Точка остановки потока 27 (2026-10-17, стоп по лимиту координатора):** ND-1 готов полностью — ndcheck 11/11,
    SERVER OK, CLIENT OK (858 с), docs обновлены, ветка запушена; осталось только открыть PR в `claude/affectionate-davinci-4gddoh`
    (если ещё не открыт). Следующий этап — ND-2 (мобы FUR).
  - Не перенесено в ND-1 (уходит в ND-2, нужны мобы/предметы FUR): Mana/Elemental Leech (пиявки FUR), 12 артефактов с
    эффектами через мобов FUR (лежат как предметы, без эффекта), остальные 16 заклинаний.
  - Рецепты бомб требуют предметов FUR (`foul_bristle`, `shattered_ice`) — вместе с мобами в ND-2.
- **ND-2 — мобы FUR (поток 27, 2026-10-17, ветка `claude/necromancers-delight-nd2`):** порт `mods-src/fishs-undead-rising`
  0.2.0 по декомпиляции jar 1.16.5 2.4.1 (Vineflower; модели ModelRenderer → LayerDefinition 1.20.1, анимации построчно).
  - Мобы (13 сущностей): Unburied, Mummy, Mycosis, Frigid, Forsaken (+ снаряд Death Coil, посох), Vespa (+ кокон, езда,
    жало по G), Banshee (крик → эффект Fear), Parasite (4 варианта, связь с Infested), Bone Worm/Osvermis (снаряды acid/flame jet),
    Salamander (+ нимфа, езда, залп по G, эффект Immolation), Weta, Mimic (мимикраб). Модели, текстуры, звуки, добыча,
    яйца призыва, свой творческий раздел, ru/en.
  - Спавн — значения конфига 1.16.5 по умолчанию, через biome modifiers (`data/mod_lavacow/forge/biome_modifier/`) и свой
    structure modifier `mod_lavacow:add_spawns` (мумии — пустынный храм; мимикраб — тег `mod_lavacow:mimic_habitat`
    у сундуков, адская крепость, аванпост; в оригинале спавн-лист строения вытеснял всех прочих монстров — здесь нет).
    Unburied сам не появляется (как в оригинале).
  - Исправлены баги оригинала: кокон веспы при `/summon` с NBT вылуплялся сразу; здоровье Frigid/Mummy/веспы при загрузке;
    smite читался из `fire_aspect`; вета после первого растения ломала остальные мгновенно.
  - Не перенесено: мобы, не нужные ND (Beelzebub, Enigmoth, Ptera, Imp…), поэтому предметы приручения Salamander/Weta/Mimic
    (`imp_horn`, `ptera_wing`, `plagued_porkchop`…) есть только в творческом разделе; блоки glowshroom/cordy_shroom FUR
    (Mycosis рисует их модели как слой); weta_hoe, гробница в пустыне, зелья FUR, конфиг FUR; `foul_bristle` (крафт из
    `foul_hide` моба вне списка) — рецепт вонючей бомбы ND пока без него не собрать.
  - Тест `scripts/tests/furcheck.js`: призыв каждого моба, жив через 100 тиков, яйцо и таблица добычи.
  - **Дальше — ND-3**, см. раздел «Передача ND-3» ниже.
  jar FUR 1.12.2 (8253053) и 1.16.5 (7327600) — с CurseForge (`mediafilez.forgecdn.net/files/7327/600/...`).

### Передача ND-3 (поток 27 → свежий сеанс, 2026-10-17)
Цель: оставшиеся заклинания и артефакты Necromancer's Delight поверх мобов порта FUR (ND-2, PR #67). Ветка — от
`claude/necromancers-delight-nd2` (или от интегратора после слияния #67), PR в `claude/affectionate-davinci-4gddoh`.

**Исходники оригинала:**
- ND 1.12.2 — https://github.com/WinDanesz/WizardryNecromancersDelight, коммит `0e52462` (GPL-3.0). Регистрация
  заклинаний — `registry/NDSpells.java` (строки 80–117), артефактов — `registry/NDItems.java` (66–89), эффекты артефактов —
  `handler/NDEventHandler.java` (372 строки), свои классы — `spell/`, `item/`, `entity/`; ассеты и
  `assets/necromancersdelight/spells/*.json` (параметры заклинаний).
- FUR 1.16.5 2.4.1 — jar с CurseForge, файл 7327600 (`mediafilez.forgecdn.net/files/7327/600/Fish's%20Undead%20Rising-2.4.1.jar`),
  декомпиляция Vineflower 1.10.1 (`org/vineflower/vineflower/1.10.1` на Maven Central): `java -jar vineflower.jar -dgs=1 <jar> <out>`.
  FUR 1.12.2 1.6.2 — файл 8253053. Классы FUR, нужные ND-3, — в декомпиляции 1.16.5: предметы `item/` (FrozenDagger,
  BoneSword, SpectralDagger, VespaShield), снаряды `entities/projectiles/` (HolyGrenade, GhostBomb, SonicBomb),
  блоки грибов `block/` (Glowshroom, Cordyceps, VeilShroom).

**Опись: заклинания (16 из 20; 4 сделаны в ND-1 — stink_bomb, frost_bomb, corrode, glowshroom):**
| id | оригинал (NDSpells) | что нужно |
|---|---|---|
| bound_frozen_dagger, bound_bone_sword, bound_spectral_dagger, bound_vespa_shield | `SpellDynamicConjuration` + `FishItems.*` | перенести 4 предмета FUR; Redux `ConjureItemSpell` |
| holy_hand_grenade, ghost_bomb, wail_of_the_banshee | `SpellThrowable` + EntityHolyGrenade/GhostBomb/SonicBomb (FUR) | перенести 3 снаряда FUR; Redux `ProjectileSpell` (как бомбы ND-1, `NdBomb`) |
| summon_vespa | `SpellMinion` + своя `EntityVespaMinion` | миньон на `VespaEntity` порта; Redux `MinionSpell` |
| conjure_banshee, conjure_mycosis, summon_salamander | `SpellDynamicMinion` + мобы FUR | мобы есть в порте (`FurEntities`); Redux `MinionSpell` + `MinionData` (как в wizardry-golems) |
| summon_mana_leech, summon_elemental_leech | `SummonManaLeech`/`SummonElementalLeech` + `EntityLeechMinionBase` | миньоны на модели `ParasiteModel` порта |
| trial_of_mushrooms | `TrialOfMushrooms` (IMBUE) | грибы FUR (блоки — не перенесены в ND-2) |
| carcinize | `Carcinize` (сундук → мимик) | `MimicEntity` порта, перенос содержимого сундука |

**Опись: артефакты** (из 15 + Leech Crystal работают 5 — Necromantic/Malice Ward, иммунитеты к замедлению/слабости,
Cursed Scarab; остальные зарегистрированы в `NdArtifacts`/`NdItems` без эффекта): `amulet_leechlink`, `ring_legion`
(Unburied), `ring_nameless`, `ring_frozen_dagger`, `ring_forsaken` (Forsaken), `charm_bone_bag`, `charm_mummy_minion`
(Mummy), `charm_mushroom_minion` (Mycosis), `belt_thorns`, `head_pest_crown` (`ItemPestCrown`), `amulet_leech_crystal`
(`ItemLeechCrystalAmulet`). Логика — `NDEventHandler`.

**Что уже есть для переиспользования:**
- `mods-src/necromancers-delight` 0.1.0: регистрация заклинаний (`NecromancersDelight` — `SPELLS` registry Redux
  `ebwizardry:spells`), `NdSpells` (свойства через `SpellProperties.builder()`), `NdBomb`/`NdBombs` (снаряд + граната),
  `NdArtifacts` (Curios, проверки слота), `NdEffects`, `NdLoot`, lang en/ru/zh (ru — ещё в ru-пакете).
- `mods-src/fishs-undead-rising` 0.2.0: `registry/FurEntities` (в javadoc — порядок добавления моба), `FurItems`
  (вкладка, `egg(...)`), `FurSounds`, `entity/FURTameableEntity` (владелец/приручение — удобно для миньонов),
  `client/model/FURBaseModel` (хелперы анимаций), `client/ClientSetup`, `world/AddStructureSpawnsModifier`.
- Образец миньонов Redux — `mods-src/wizardry-golems` (`MinionData`: владелец, время жизни, следование).
- Тесты: `scripts/tests/ndcheck.js` (касты фейковым игроком, арены, фазы), `scripts/tests/furcheck.js` (призыв мобов).

**Ловушки (найдено в ND-1/ND-2):**
- Снаряды Redux (`MagicProjectileEntity.aim`) вылетают из `xo/yo/zo` стрелка — в тестах фейкового игрока ставить
  `setPos` + `setOldPosAndRot()` (5-аргументный `moveTo` Rhino не находит).
- Тестовый сервер: «Done» в логе за ~3 мин до первого тика — `TEST_COMMANDS` с `#sleep 420`; мир между прогонами
  сохраняется — арены чистить `kill` за 40 тиков до каста; `server.persistentData` KubeJS — не CompoundTag (фазы — в
  переменной скрипта); `const` внутри блока в обработчике тика — «redeclaration», только `let`.
- Перед проверкой урона сбрасывать `invulnerableTime` цели (чужой удар за ≤10 тиков съедает часть урона).
- Biome/structure modifiers: несколько тегов — только `{"type": "forge:or", "values": [...]}`, массив не парсится и
  мир не грузится. NBT-поля сущностей читать с `tag.contains(...)` (иначе `/summon` с NBT обнуляет их).
- test-client: остатки мёртвого Xvfb (`/tmp/.X99-lock`, `/tmp/.X11-unix/X99`) дают «glfwInit failed» — удалить.
- Модели ND-2 переводились автоматическим конвертером — при работе с миньонами на их моделях глянуть позы в мире.


### ND-3 — сделано (поток 29, 2026-10-17, ветка `claude/necromancers-delight-nd3`)
`mods-src/necromancers-delight` 0.2.0 + `mods-src/fishs-undead-rising` 0.3.0. Сборка ND теперь компилируется против jar
порта FUR (`flatDir` на `../fishs-undead-rising/build/libs`, `modCompileOnly "local:fishs-undead-rising-port:<версия>"`) и
Curios (`maven.modrinth:curios`) — сначала собрать FUR, потом ND.

**Заклинания (15 зарегистрированных в оригинале; `forsaken_shield` — только json, в `NDSpells` его нет, не переносили):**
| id | как сделано |
|---|---|
| bound_frozen_dagger / bound_bone_sword / bound_spectral_dagger / bound_vespa_shield | Redux `ConjureItemSpell` с предметами порта FUR (`frozen_dagger`, `bonesword`, `spectral_dagger`, `vespa_shield`); время жизни — `item_lifetime` из json |
| holy_hand_grenade / ghost_bomb / wail_of_the_banshee | свой `ThrowBombSpell` бросает `FurBombEntity` порта FUR (holy_grenade / ghostbomb / sonicbomb); скорость — из `range` |
| summon_vespa / conjure_banshee / conjure_mycosis / summon_salamander | Redux `MinionSpell` с мобами порта FUR (`MinionData`); саламандра ещё и приручена; укус призванной веспы снимает Infested (как `EntityVespaMinion`) |
| summon_mana_leech / summon_elemental_leech | свои `ManaLeech`/`ElementalLeech` (`entity/LeechMinion` — паук, модель паразита FUR, текстуры оригинала): впиваются в не-союзника и становятся бессрочными; мана-пиявка тянет 5% маны палочки носителя в секунду, стихийная кусает уроном и эффектом стихии |
| trial_of_mushrooms | гриб FUR из второй руки → эффекты (параметры из json) |
| carcinize | луч по сундуку: тратит кристалл Земли, ломает сундук, прирученный мимикраб |

**Артефакты (12):** Leechlink (половина украденной маны — в палочку хозяина), Spell-Sapping Venom (пиявки этого хозяина
ослабляют заклинания носителя ×0,6), Fallen Legion's Ring / Ring of the Nameless (зомби Redux → Unburied; оба — + предмет
снаряжения, с `charm_undead_helmets` — ещё шлем), Frigidbone Ring (ледяной кинжал морозит цель: Frost 10 с), Ring of Hatred
(скелет → Forsaken: щит с гербом Короля-скелета / меч / лук / всадник на костяной лошади), Bone Bag (скелеты наполняют мешок
по 25%, полный — следующий призыв даёт Osvermis), Malevolent Ushabti (зомби → мумия), Book of Witchcraft (гриб из второй руки
усиливает призванного), Thornbelt (16 кактусовых шипов при уроне, откат 15 с), Crown of Pests (вечный вета-спутник, после
гибели — новый через 40 с), Leech Crystal Amulet (стихия привязывается кристаллом в Arcane Workbench).

**Отличия от оригинала (и почему):**
- Мана-пиявка на кастующем: в оригинале условие `caster.getRidingEntity() instanceof EntityManaLeechMinion` (кастующий *верхом
  на* пиявке) не срабатывало никогда; сделано по описанию — пиявка *на* кастующем: цена ×2, с Venom ещё ×0,6 силы/взрыва/
  дальности/длительности. `setWeakensSpells` в оригинале никто не вызывал — Venom ставит его при призыве.
- Frigidbone Ring: зачарования `freezing_weapon` в Redux 0.8.9 нет — метка `necromancersdelight_freezing` на кинжале и
  Frost 10 с при ударе (как уровень I «Freezing Weapon» 1.12.2).
- Стихийная пиявка: усиление от силы заклинания — общее у Redux `MinionSpell` (в оригинале своя формула с ошибкой
  приоритета `potency - 1 * 0.7`); стихия палочки — по `IElementValue`, а не по подстроке id.
- Святая граната: в 1.16.5 FUR проверка не срабатывала (имя `holygrenade` ≠ id `holy_grenade`) — сделано как в 1.12.2.
- Предмет-лук у мана-пиявки (оригинал кладёт в руку `Items.BOW`, моделью не рисуется) не выдаём.
- Рецептов bonesword / spectral_dagger / бомб нет (нужны sharptooth, ectoplasm_ingot, basic_bomb FUR — не перенесены);
  предметы — из заклинаний и творческой вкладки. Огромный светящийся гриб FUR (костная мука) не перенесён.

**Тест:** `scripts/tests/nd3check.js` 55/55 OK (+ ndcheck 11/11, furcheck 13/13), SERVER OK — фейковый игрок добавляется в мир (иначе `MinionData.getOwner()` не находит хозяина по
UUID), касты/артефакты проверяются в мире. Crown of Pests и Thornbelt вызываются напрямую (`NdArtifacts.pestCrown`/
`thornBurst`: фейковый игрок не тикает и не ранится командой), Bone Bag и цена у пиявки — через `SpellCastEvent` Redux (прямой
каст событий не шлёт). KubeJS: у `ItemStack` нет `getTag()`/`hasTag()` — только свойство `stack.nbt`.

**Состояние на конец потока 29 (2026-10-17):** ND-3 готов: 15 заклинаний и 12 артефактов, nd3check 55/55, ndcheck 11/11, furcheck 13/13,
SERVER OK, CLIENT OK (481 с). Из сообщения владельца исправлен слой грибов Mycosis (фиолетово-чёрные кубы, docs/compat.md),
но глазами он не проверен. Тест одиночного мира в облачном клиенте обрывается при входе — это отдельная задача.
