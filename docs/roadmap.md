# План работ и разделение по чатам

Состояние на 2026-10-10. Связка **1.20.1 + Forge 47.4.26**, сборка — `pack/` (packwiz).
Ветка-интегратор — `claude/affectionate-davinci-4gddoh` (на GitHub она стоит веткой по умолчанию).
Каждый чат-поток работает в **своей** ветке от неё и вливается обратно через PR.

## Сделано (в истории git)
| Что | Где |
|---|---|
| Выбор версии и загрузчика, сравнение модов | `docs/version-choice.md` |
| Варианты хостинга | `docs/hosting.md` |
| Сборка packwiz, ~120 модов, тест выделенного сервера до `Done` | `pack/`, `scripts/test-server.sh` |
| Связки, волна 1 (магия, техника, оружие, измерения, Farmer's Delight) | `pack/modlist.txt` |
| Патчи: таблицы добычи Iron's Spells (KubeJS), замена GabouLibs для Serene Seasons Plus, Canary × Valkyrien Skies | `patches/`, `pack/config/`, `docs/compat.md` |
| Объединение дублей (Almost Unified), проверка жёстких ссылок | `pack/config/almostunified/`, `scripts/hardrefs.py` |
| Одна Солнечная система: планеты Ad Astra, ракеты Northstar/Creating Space, реальная гравитация, руды на всех планетах | `patches/space_unify.py`, `patches/ore_crossgen.py` |
| Кислород скафандров Northstar/CS в системе Ad Astra | `mods-src/mm-compat` |
| Wizardry Restored 0.4.0: 38 из 61 недостающего заклинания + 4 SENSIBLE-заклинания Redux, тест в мире `castcheck.js` (42/42) | `mods-src/wizardry-restored`, `docs/wizardry-port.md` |
| Lycanites Mobs: недостающий refmap миксинов (сделано в основном чате до разделения) | `patches/lycanites-refmap`, `docs/compat.md` |
| Серверные тесты: гравитация, маршруты, ракеты, кислород, руды, заклинания | `scripts/tests/` |

## В работе
- Поток 1 (Wizardry Restored): **0.6.0** — все 61 недостающее заклинание + 4 «тестовых» Redux; этап 3
  (сущности) и этап 4 (статуи petrify/ice_statue, лоза grapple, камни перемещения, 18 артефактов Redux)
  сделаны, `castcheck.js` 69/69. Дальше — оригинальные ассеты и сверка параметров с оригиналом (лицензия
  больше не мешает); план — «Точка остановки» в `docs/wizardry-port.md`.

## Потоки (по чату на каждый)

Правила для всех потоков:
- Ветка от свежей `claude/affectionate-davinci-4gddoh`, свои файлы — свои; общие файлы
  (`pack/modlist.txt`, `pack/index.toml`, `docs/compat.md`) правим точечно. Конфликт в `index.toml`
  не правим руками — пересоздаём `packwiz refresh`.
- После изменения набора модов — `scripts/test-server.sh`, находки — в `docs/compat.md`.
- В конце — отметка о сделанном в этом файле (раздел потока).

### Поток 1 — Wizardry Restored (заклинания)
Файлы: `mods-src/wizardry-restored/`, `docs/wizardry-port.md`, `scripts/tests/spellcheck.js`, `castcheck.js`.
1. ~~Оставшиеся 23 заклинания — этап 3~~ — **сделано** в 0.5.0 (все 61 есть, `castcheck.js` 65/65).
2. ~~Этап 4: артефакты, камни перемещения, статуи, лоза~~ — **сделано** в 0.6.0.
3. ~~Вернуть на выделенный сервер `SENSIBLE`-заклинания Redux~~ — **сделано** в 0.4.0 (копии под
   `wizardry_restored:*`, проверены `castcheck.js`).
4. ~~Проверить дополнения Redux с CurseForge~~ — **сделано**: Spell Forge 1.1.0 в сборке (сервер ОК,
   162 заклинания); Wizardry Golems и Necromancer's Delight — только 1.12.2.
5. Оригинальные ассеты Electroblob вместо самодельных (с указанием авторства), сверка параметров заклинаний
   с оригиналом, доработка упрощённых possession/resurrection/decoy — см. `docs/wizardry-port.md`.

### Поток 2 — Мультиплеер: голосовой чат, раздача сборки, хостинг
Файлы: `pack/modlist.txt` (раздел «Удобство и мультиплеер»), `docs/hosting.md`, новые `scripts/export-*`.
1. **Голосовой чат**: Simple Voice Chat (сторона `both`) + дополнения/связки к нему (проверить на
   1.20.1 Forge: Sound Physics Remastered — клиент, Voice Chat Interaction, группы/радио и т.п.).
   Конфиг сервера `pack/config/voicechat/`. Ему нужен **UDP-порт 24454**: дописать в `docs/hosting.md`
   (проброс порта, UDP-туннель в playit.gg, Tailscale работает без настроек).
2. Клиентский пакет `.mrpack` (Prism Launcher / Modrinth App) и серверный пакет (только `both`+`server`)
   — скрипт экспорта и короткая инструкция для друзей.
3. Выбор хостинга с пользователем и проверка сервера в боевых условиях (RAM, флаги JVM, `spark`).

Сделано (ветка `claude/lucid-babbage-8qj4no`):
- [x] Голосовой чат: Simple Voice Chat + Simple Radio, Sound Physics Remastered, Talking Heads, Let Them Talk,
  VoiceChatProfiles, Vibrative Voice, VC Admin, Broadcast; конфиг `pack/config/voicechat/`. Voice Chat
  Interaction, Audio Player, Enhanced Groups — только Fabric. Подробности — `docs/compat.md`.
- [x] UDP 24454 в `docs/hosting.md`: проброс, UDP-туннель playit.gg + `voice_host`, Tailscale, платный хостинг.
- [x] `scripts/export-pack.sh` → `.mrpack` и `*-server.zip` (проверен запуском); инструкция — `docs/friends.md`.
- [x] Попутно: `test-server.sh` ловит «Mods loaded with N warnings» (сервер без модов); `.gitignore` больше не
  прячет `kubejs/data/*/worldgen/` — закоммичены размещения `mm_space` из `patches/ore_crossgen.py`.
- [x] Порт на Forge Voice Chat Interaction, AudioPlayer, Enhanced Groups (Max Henkel, только Fabric) —
  `mods-src/voicechat-addons`, тест `scripts/tests/voicecheck.js`. Voice Chat Interaction выключен в пользу
  Vibrative Voice (родная Forge-сборка, связка с рациями), вернуть — `enabled=true` в его конфиге.
- [~] Пункт 3: замер ресурсов (`spark`: 3 ГБ кучи без игроков, TPS 20) и сравнение вариантов — `docs/hosting.md`;
  серверный пакет теперь `-Xmx10G` (5–8 игроков). Ждём решения пользователя, где хостить, и первую сессию с
  игроками — тогда `spark profiler` под нагрузкой.

### Поток 3 — Дополнения, волна 2 (связки для каждого мода)
Файлы: `pack/modlist.txt`, `pack/mods/*.pw.toml`, `pack/config/almostunified/`, `docs/compat.md`.
1. Пройти каждый мод из `modlist.txt` и найти недостающие дополнения и связки (особенно: Lycanites,
   Mowzie's, Alex's Mobs, Blue Skies, Undergarden, Deeper Darker, Bumblezone, Twilight Forest, Arknights,
   Superb Warfare/TACZ, Valkyrien Skies, Mekanism, Botania, Occultism, Blood Magic, Mahou Tsukai).
2. Для каждой новой пачки: тест сервера, Almost Unified (`scripts/hardrefs.py` перед изменением),
   запись в `docs/compat.md`.
3. Где совместимость плохая — не чинить самому, а записать задачу в поток 4.
4. (из потока Galacticraft) Проверить `beyond-oxygen` (GPL, 1.20.1 Forge) — герметизация отсеков кораблей
   Valkyrien Skies; нужна ли связка с кислородом Ad Astra.

Ход работы (ветка `claude/determined-goodall-fs240t`, список согласован с пользователем; моды — `pack/modlist.txt`,
раздел «волна 2»):
- [x] Пачка 1 — измерения (Aether, Twilight Forest, Blue Skies, Undergarden, Deeper and Darker, Alex's Caves): 20 модов,
  сервер до `Done`. CBC: Nuclear отключён (несовместим с Create Big Cannons 5.11.4).
- [x] Пачка 2 — мобы и боссы (Lycanites, Mowzie's, Alex's Mobs, Cataclysm): 9 модов + библиотеки, сервер до `Done`.
  Integrated Mowzie's Mobs отложен: обязательно тянет Quark + Supplementaries + Amendments — решает пользователь.
- [x] Пачка 3 — магия (Ars Nouveau, Iron's Spells, Botania, Occultism, Blood Magic): 13 модов, сервер до `Done`.
  Отключены T.O Magic 'n Extras и Alshanex's Familiars (собраны под старые Cataclysm / Iron's Spells);
  Botanical Machinery Extra отложен (обязательно тянет Applied Energistics 2).
- [x] Пачка 4 — оружие, Valkyrien Skies, Arknights: 12 модов, сервер до `Done`. Отключены Superb VS и
  SBW Vintage Vehicle Pack (несовместимые версии), VS Tournament заменён портом Tournament Reforged;
  Create: Eureka! отложен (это датапак).
- [x] Пачка 5 — техника и удобство (Mekanism, рюкзаки, Waystones × Xaero, Jade, LSO): 9 модов, сервер до `Done`.
  More Backpack Upgrades отложен (обязательно требует Refined Storage).
- [x] Пачка 6 — моды с крупными зависимостями (одобрено пользователем): Integrated Mowzie's Mobs (+ Quark, Supplementaries,
  Amendments, Integrated API/Patches), Botanical Machinery Extra (+ Applied Energistics 2), More Backpack Upgrades
  (+ Refined Storage). Сервер до `Done`, новый мир создаётся.
- Для Bumblezone и Mahou Tsukai подходящих дополнений под 1.20.1 Forge нет (Mahou Tsukai Combat требует Better Combat).

### Поток 4 — Совместимость и патчи
Файлы: `patches/`, `mods-src/mm-compat/`, `pack/kubejs/`, `docs/compat.md`.
1. ~~Lycanites Mobs: недостающий `lycanitesmobs.refmap.json`~~ — **сделано** (`patches/lycanites-refmap`,
   коммит `b8f38e7`); осталось проверить клиентский `LightTextureMixin` в потоке 5.
2. ~~Legendary Survival Overhaul × Serene Seasons~~ — **проверено**: связка встроена в LSO и работает
   (лето +8, зима −9.5 к температуре мира), патч не нужен (`scripts/tests/seasontemp.js`, compat.md п. 11).
3. ~~Create: Northstar — Structures~~ — **сделано**: совместимой версии нет, патч
   `patches/northstar_structures_fix.py` (биомы под Redux 0.6, кратеры и на Луне Ad Astra), мод возвращён.
4. ~~Шум в логах~~ — **сделано** (compat.md п. 9): теги с отсутствующими ссылками больше не теряются,
   рецепты для отсутствующих модов отключены, лучи Cataclysm Spellbooks и `thought_load_limit` — в `mm-compat` 0.2.0.
   Попутно: `.gitignore` прятал `pack/kubejs/data/*/worldgen` — без них чистая копия не создавала мир (п. 12).
5. ~~Задачи совместимости из потока 3~~ — **сделано** (compat.md п. 14–16):
   - ~~Superb Warfare × Valkyrien Skies~~ — Superb VS не нужен (SBW 0.8.9+ умеет сам); исправлен блок попадания
     по кораблю (`mm-compat` 0.3.0), тест `vshitcheck.js`.
   - ~~CBC: Nuclear~~, ~~Alshanex's Familiars~~, ~~T.O Magic 'n Extras~~ — порты `patches/jar-ports` в `pack/mods/*-mmport.jar`
     (+ Apothic Attributes и Placebo для T.O). Потоку 3: строки `# mr cbc-nuclear`, `# mr alshanexs-familiars`,
     `# cf to-tweaks-irons-spells` оставить отключёнными — в сборке порты.
   - ~~Create: Eureka!~~ — это сборка (134 мода, только конфиги), не датапак: переносить нечего.
   Инструменты для следующих случаев: `scripts/linkcheck` (что не связывается), `scripts/jarport` + `mods-src/jar-ports`.
6. ~~(из потока 2) Zombie Have Ears 1.1.2~~ — **сделано**: `patches/zombie-have-ears/build.sh` (исправленный `mods.toml`,
   авторство SKALKMine указано) → `pack/mods/zombiehaveears-1.1.2-forgefix.jar`, проверен с Simple Voice Chat.
7. ~~(из ревью PR #2) Arknights Weapon 1.5.6 удваивает здоровье мобов~~ — **сделано**: порт без `RiseProcedure`
   (`patches/jar-ports/arknights-weapon` → `pack/mods/arknights_weapon-1.5.6-mmport.jar`), тест
   `mobhealth_commands.txt`: оригинал 20→40→80, порт 20→20. Оригинал из PR #2 убран при слиянии, в сборке порт.
8. ~~Apothic Attributes и формулы брони~~ — **решено**: броня по Apothic, защита-зачарование по ванилле
   (`pack/config/attributeslib.cfg`, расчёт — compat.md п. 14, тест `scripts/tests/armor_commands.txt`).

### Поток 5 — Проверка клиента
Серверный тест клиент не покрывает. Запуск клиента (Xvfb в облаке или у игрока): Oculus + Embeddium,
Distant Horizons, Yes Steve Model, AzureLib-рендер, клиентские моды Arknights furs. Найденное — в
`docs/compat.md`; правки модов — через поток 3/4. Начинать после потоков 2–3, когда набор модов устоится.
- (из потока 1) Wizardry Restored 0.6.0: сервер проверен, клиент — нет. Посмотреть рендер новых сущностей
  (тотемы, чёрная дыра, силок, шипы, молот — модели из ванильных блоков; двойник/мираж — скин владельца; феникс —
  своя текстура; статуи — модель существа текстурой камня/льда; лоза — линия до крюка), частицы силового поля/тучи/диска, иконки заклинаний и эффекта «Метка жертвы», звуки Redux.
- (из потока 2) Импорт `dist/*.mrpack` в Prism Launcher и Modrinth App (серверные моды не должны ставиться),
  голосовой чат клиент↔сервер (микрофон, группы, рации Simple Radio), Sound Physics вместе с Oculus;
  порт аддонов: своя пластинка/козий рог (`/audioplayer`), загрузка через браузер.

- (из потока Galacticraft) Galacticraft Revival 0.1.0 (в сборке): рендер скелета-босса (модель GC5),
  модель сундука сокровищ (Legacy, UV под 1.12 — может быть перевёрнута), потухший факел, полоса здоровья босса,
  захват и бросок игрока боссом, звуки босса.
## Интеграция (ревью PR, поток «Ревью и интеграция», ветка `claude/confident-hypatia-2zqr1t`)
Цикл 1 — 2026-10-10:
- **PR #1, поток 2** (голосовой чат, экспорт, UDP): проверен и **влит** (`013bf69`). `add-mods.sh` пусто,
  `packwiz refresh` без изменений, стороны совпадают с Modrinth. Тест в новом мире вместе с Wizardry 0.6.0:
  `Done`, без «Mods loaded with N warnings», голосовой сервер на 24454, castcheck 69/69, гравитация/маршруты/
  руды/кислород как раньше. Поправка интегратора: `export-pack.sh` подкладывает packwiz-у jar CurseForge с
  запретом стороннего скачивания (иначе после PR #2 `.mrpack` не собирается) и падает, если `.mrpack` не создан.
- **PR #2, поток 3** (дополнения, волна 2; черновик): **заблокирован** — Arknights Weapon удваивает здоровье
  мобов (задача 7 потока 4), отсюда 5 провалов castcheck (урон есть, но порог «< 20 HP»). Тесты сервера — по
  пачкам 1–4; пачки 5–6 (Quark, Supplementaries, AE2, Refined Storage…) — только стороны, полный тест — когда
  выйдет из черновика. При слиянии с PR #1 в `docs/compat.md` убрать дубль пункта про `.gitignore`.
Цикл 2 — 2026-10-10 (пользователь разрешил сливать готовые PR без подтверждения):
- **PR #5** (интеграция, цикл 1) — влит.
- **PR #3, поток 4** (Northstar Structures, сезоны × LSO, шум в логе) — проверен и **влит**. Конфликт с интегратором
  разобран merge'ем в ветке PR; `northstar_structures_fix.py` воспроизводит данные байт в байт; тест в новом мире:
  castcheck 69/69, NOISECHECK/SEASONTEMP/кратеры как в отчёте потока, 0 `Couldn't load tag`.
- **PR #4, Galacticraft** — в работе (этап 1, срез «данж Луны»), не сливается; `pack/` не трогает, конфликтов нет.
- **PR #2, поток 3** — по-прежнему заблокирован Arknights Weapon (задача 7 потока 4).
Цикл 3 — 2026-10-11 (все открытые PR влиты, открытых нет):
- **PR #4, Galacticraft Revival 0.1.0**, **PR #7, поток 2** (порт голосовых аддонов), **PR #8, поток 4** (порты jar,
  SBW × VS, Zombie Have Ears, Arknights Weapon) — проверены вместе (merge в ветках PR fast-forward), тест в новом
  мире: castcheck 69/69, GCTEST 12/12, PORTCHECK 10/10, VOICECHECK, VSHIT, здоровье мобов 20/24 после перезагрузки чанка.
- **PR #2, поток 3** (волна 2, пачки 1–6) — влит: оригинал Arknights Weapon заменён портом потока 4, теги с
  отсутствующими ссылками волны 2 добавлены в `missing_mod_refs.js` (0 `Couldn't load tag`), полный тест — как выше.
- Экспорт полной сборки: `.mrpack` 165 МБ, серверный zip 1,5 ГБ (229 модов).
- Apothic Attributes — решено: защита по ванилле, броня по Apothic (п. 8 потока 4). Для хостинга
  (поток 2): с волной 2 в тесте задержки тиков до 50 с при генерации далёких чанков.
- Заметка для тестов: тестовый мир хранит состояние модов между запусками — для `castcheck.js` и проверок
  здоровья мобов удалять `<папка>/world` перед запуском.

### Отложено (решение за пользователем)
- Galacticraft — **в работе отдельным потоком** (0.1.0 «лунный данж с боссом» в сборке) (ветка `claude/determined-sagan-51u10p`): свой мод поверх Ad Astra
  с портом недостающего из GC Legacy/GC5 (данжи, боссы, схемы, астероиды). План и точка остановки — `docs/galacticraft.md`.
