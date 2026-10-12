# План работ и разделение по чатам

Состояние на 2026-10-12 (актуальная сводка — раздел «Координация», раунд 4). Связка **1.20.1 + Forge 47.4.26**, сборка — `pack/` (packwiz).
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
- Владелец проверяет установку по ссылке (Prism + packwiz-installer) и вход на сервер в офлайн-режиме; находки — в compat.md.
- Очередь (по решению владельца В5 «а» — после отзывов друзей): оригинальные ассеты Wizardry, Galacticraft этап 2.
- Идея владельца (низкий приоритет, после 17.10 и отзывов): многопоточность — исследовать практичные пути (async-моды
  под 1.20.1 Forge, параллельная генерация/загрузка чанков, JVM/GC: ZGC/Shenandoah), потокобезопасность наших модов
  (Valkyrien Skies, Create, Lycanites…), выгода и риски → отчёт в `docs/` и варианты владельцу. Модель: opus — архитектура
  и риск нестабильности (ванильный тик однопоточный, полная переделка ядра нереалистична).
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
- Добавил мод с боссами — обнови `docs/bosses.md`/`docs/bosses.csv` в том же PR: прогон `scripts/tests/bossdump.js` на
  тестовом сервере + `scripts/boss_table.py --mods <папка jar> --dump <latest.log>`; описание нового босса — в `scripts/boss_info.py`.

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
4. **Офлайн-режим** (решение владельца «играем без лицензий») — сделано: серверный пакет с `online-mode=false`
   (`server.properties.default`, существующий не затирается), мод авторизации Sol's AuthLogin (side=server), доки
   `friends.md` / `hosting.md`. Проверен запуск сервера до `Done`; вход реальным клиентом не проверялся.
   **Сравнение хостингов сделано** (2026-10-11, `docs/hosting.md`: таблица, отсев, рекомендации — Millida 12–16 ГБ,
   запасной зарубежный VPS); по ответу владельца (игроки в РФ, оплата рублями) пересмотрено: основной Millida
   16 ГБ — 1290 ₽/мес, запасной российский VPS; зарубежные не оплатить. Вопросы поддержке — там же. Бой на хостинге ещё не проверен.

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
- [x] Mana and Artifice + Ars Magica: Legacy (решение владельца, ветка `claude/stream3-mana-artifice-ars-magica`): 6 модов добавлено,
  MNAWitchcraft отключён (старый M&A; позже возвращён портом, поток 4b, compat.md); сервер до `Done`, castcheck 69/69. Для Ars Magica дополнений нет. Подробности — compat.md.
  Потоку 4: проверить на клиенте клавиши/мана-HUD четырёх магических систем и дубли рецептов/предметов.
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
9. ~~**(из потока 5, БЛОКЕР клиента) Cataclysm Spellbooks 1.2.9 × Cataclysm 3.31**~~ — **сделано**: порт
   `patches/jar-ports/cataclysm-spellbooks` → `pack/mods/cataclysm_spellbooks-1.2.9-1.20.1-mmport.jar` (compat.md п. 14, К4),
   тест в мире `scripts/tests/sbcastcheck.js` 10/10, клиент со всей сборкой — `CLIENT OK`.
10. ~~(из потока 5) Lycanites refmap: поле `lightTexture`~~ — **сделано иначе**: refmap к `@Shadow`-полям не применяется,
    затемнение от страха перенесено в `mm-compat` 0.4.0 (`LycanitesFearLightMixin`, compat.md К6).
11. ~~(из потока 5) Origin Furs × AzureLib 3.1 (compat.md К3)~~ — **сделано** (поток 4b): Origin Furs и Arknights Furs
    возвращены; порт `patches/jar-ports/origin-furs` встраивает старый API AzureLib 2.0.39 в jar Origin Furs
    (`pack/mods/origin_visuals-1.0.5-mmport-client.jar`, только клиент), сервер `Done`, `CLIENT OK`.

### Поток 7 — JEI (просмотр крафтов и использования)
Файлы: `scripts/jei_audit.py`, `docs/jei.md`, `docs/client-checklist.md` (раздел 8), `pack/modlist.txt` (раздел «JEI»).
- [x] Аудит: 242 jar — 65 модов со своим плагином + 30 аддонов, покрытых плагином основного мода; пробелы — в `docs/jei.md`.
- [x] Добавлены Just Enough Breeding, Professions, Beacons (+ Cerbon's API; позже убраны потоком 10 — см. compat.md), Advancements; сервер `Done`, клиент `CLIENT OK` и вход в мир.
- [x] KubeJS: `missing_mod_refs.js` правит только теги, `space_rockets.js` убирает рецепты ракет Ad Astra и прячет их из JEI — нужные рецепты не скрыты.
- [x] Ошибки JEI в логе клиента (Blue Skies, mekmm, JER) — собственные баги модов, `docs/compat.md`.
- [x] JEI-категория Imbuement Altar для Redux — mm-compat 0.5.0 (отдельный PR).

### Поток 5 — Проверка клиента
Клиент в облаке: `scripts/test-client.sh <папка>` (Forge через portablemc, моды из `.mrpack` как у Prism,
Xvfb + Mesa llvmpipe, агент `scripts/clienttest/ExitTrace.java` ловит «тихие» `System.exit`), установщик
`scripts/mrpack-install.py`. Чек-лист для игрока — `docs/client-checklist.md`, скриншоты — `docs/client-check/`,
находки — compat.md «Проверка клиента» (К1–К9). Сделано 2026-10-11:
- **Было: клиент не запускался вообще** — 4 независимых вылета до главного меню.
- Исправлено в сборке: ImmediatelyFast 1.2.7 → 1.5.5 (с Oculus 1.8 закрывал игру без краш-репорта, К1);
  убраны VoiceChatProfiles (битый jar, К2), Origin Furs и Arknights Furs (AzureLib 2.x против 3.1, К3);
  маркер Serene Seasons Plus в `pack/config/` (мод сбрасывал конфиг сборки на сервере и клиенте, К5).
  Стороны в `.mrpack`/серверном zip совпадают с `side` — править не пришлось.
- ~~**Осталось (блокер):** Cataclysm Spellbooks 1.2.9 роняет клиент (К4)~~ — порт сделан потоком 4 (задача 9).
- Проверено с убранным локально Spellbooks (и YSM — он не работает под ВМ, К7): главное меню (старт 272 с на
  llvmpipe), вход на тестовый сервер без рассинхрона реестров и «mismatched mod channel», выбор Origin, HUD,
  рендер скелета-босса Galacticraft с полосой здоровья, феникс Wizardry; миксины портов `*-mmport.jar` применяются;
  Lycanites `LightTextureMixin` — нет (К6, задача 10 потока 4).
- Не проверено в облаке и почему: шейдеры Oculus, Distant Horizons, YSM (нет GPU, 2 FPS, ВМ), детали рендера
  сущностей Wizardry/GC, микрофон, рации, Sound Physics, `/audioplayer` (нет звука), импорт в Prism/Modrinth App
  (нет GUI-лаунчера; установщик повторяет его правила), macOS → `docs/client-checklist.md`.
- Следующий шаг: после порта Spellbooks — `scripts/test-client.sh` до `CLIENT OK`, затем раздать чек-лист игрокам.

### Поток 6 — Русификация
Ветка `claude/russification`; документ — `docs/russification.md`, таблица по модам — `docs/russification-audit.md`.
Этап 1 (2026-10-12) сделан: аудит (`scripts/lang_audit.py`, `lang_packs_cover.py`, `font_audit.py`) — 72 053 ключа,
без паков не переведено 25 678 (64 % покрытие); добавлены 5 ресурспаков-русификаторов (MPLOC, RTF+, RTF, Russian Delight,
JustSomeTranslations; `pack/resourcepacks/`, источник `rp` в `modlist.txt`/`add-mods.sh`), остаток **12 011 строк** +
≈ 478 тыс. знаков книг; `pack/options.txt` — `lang:ru_ru` и порядок паков (`preserve`). `test-client.sh` не прогонялся.
- [x] Этап 2 (строки): 11 485 строк переведены (`pack/resourcepacks-src/russian-moded` → `pack/resourcepacks/russian-moded.zip`, последним в `options.txt`), ветка `claude/russification-2`. Книги — по отзывам друзей (Ars Nouveau и Animus первыми).
- [x] Заброшенные города (по просьбе владельца, 2026-10-12): The Lost Cities, профиль `rarecities`, `docs/lost-cities.md`.
- [ ] Прогнать `scripts/test-client.sh` и убедиться, что язык/паки подхватываются; строка в `docs/friends.md`.

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

## Координация (поток «Координатор», session_015zoozt65pTMY4bR2XBFgb8)
Координатор раздаёт задачи потокам и выбирает модель; ревью и слияние — поток «Ревью и интеграция»;
вопросы владельцу — только через «Связь с владельцем» (session_01TAw9QPkPRVtJZY37wFhBNv).

Раунд 1 — 2026-10-11:
| Задача | Поток (сеанс) | Модель — почему | Статус |
|---|---|---|---|
| Поток 4, задачи 9–10: порт Cataclysm Spellbooks 1.2.9 × Cataclysm 3.31 (К4, **блокер клиента**) + refmap Lycanites `lightTexture` (К6), до `CLIENT OK` | Поток 4 · Spellbooks (session_019V8WPE7gTHCrhB3kUX3W4M) | opus — порт jar с переходниками и отладка вылета клиента, риск сломать сборку | в работе |
| Поток 4, задача 11: Origin Furs / Arknights Furs × AzureLib 3.1 (К3) — оценка, порт или вопрос владельцу | Поток 4b · Origin Furs (session_01S19ePL8u9HoLhUJ1ck9Jb8) | opus — переходник чужого API рендера, многошаговый порт | сделано (порт `patches/jar-ports/origin-furs`), PR |
| PR #11 (поток 5) → затем PR задач 9–10 → PR задачи 11 | Ревью и интеграция (session_01VfvQ6JaX74GK5NGjWZnWoi) | opus — уже ведёт интеграцию, знает тесты | передано |
| После порта: раздать `docs/client-checklist.md` игрокам (шейдеры, DH, YSM, звук, Prism/Modrinth App) | владелец | — | ждёт `CLIENT OK` |
| Поток 2, п. 3: исследование хостингов (цены под наши требования, свои jar/SFTP, Forge 47.4.26, UDP 24454 для голосового чата, оплата) → таблица и рекомендация в `docs/hosting.md`, затем вопрос владельцу (В1) | Поток 2 · Хостинг (session_011uQdyPF3w7opk8J86p7SvH) | sonnet — веб-исследование и документация, без риска для сборки | PR #13 (игроки в РФ, оплата в ₽; рекомендация — Millida, запасной — российский VPS) → ревью; вопрос В1 у владельца |
| Поток 1, п. 5 (оригинальные ассеты Wizardry), Galacticraft этап 2 | — | sonnet / opus соответственно, когда запустим | в очереди: после `CLIENT OK`, чтобы держать ≤ 3–4 рабочих потоков |

Раунд 2 — 2026-10-11 (вечер):
| Задача | Поток (сеанс) | Модель — почему | Статус |
|---|---|---|---|
| Mana and Artifice + Ars Magica: Legacy и их дополнения (решение владельца, «важно»); если нет под 1.20.1 Forge — вопрос владельцу о порте | Поток 3 · Магия (session_01NDk4w99jqw1tVgRhPtVTAA) | sonnet — типовое добавление модов + тест сервера; порт, если понадобится, — отдельной задачей opus | влит PR #15 (3964be1); MNAWitchcraft не вошёл — порт по решению владельца В4 |
| PR #11, #12 | Ревью и интеграция | — | влиты (24e3b9b, b508c04) |
| PR #13 (хостинги, РФ/₽) | Ревью и интеграция | — | влит (37dcd27); вопрос В1 у владельца |

Лимиты Claude (указание владельца — следить и приостанавливать заранее). Координатор на каждой проверке смотрит
`rate_limit_event` потоков (окна `five_hour` и `seven_day`):
- `five_hour` ≥ 75% — новые потоки не запускаются, второстепенные не будятся;
- `five_hour` ≥ 90% — всем потокам «WIP-коммит и стоп», будить после `resetsAt`;
- `seven_day` ≥ 80% — сообщить владельцу.
Порядок остановки: исследования и документация → второстепенные порты/моды → блокер клиента и ревью — последними.
Потоки пушат WIP после каждого заметного шага. 2026-10-11 19:13 UTC окно `five_hour` упёрлось в 100% (оплата сверх лимита
отключена), оба порта встали посреди шага; после сброса в 21:00 работа продолжена.
Правило лимитов утверждено владельцем («B3 да», 2026-10-11).

Раунд 3 — 2026-10-12:
| Задача | Поток (сеанс) | Модель — почему | Статус |
|---|---|---|---|
| Задачи 9–10 потока 4: порт Cataclysm Spellbooks, затемнение Lycanites в `mm-compat` 0.4.0 | Поток 4 · Spellbooks (session_019V8WPE7gTHCrhB3kUX3W4M) | opus | **влит PR #16 (a1e11e8): К4 снят, CLIENT OK со всей сборкой (202 с)** |
| Задача 11: Origin Furs / Arknights Furs × AzureLib 3.1 | Поток 4b (session_01S19ePL8u9HoLhUJ1ck9Jb8) | opus — переходник API рендера | в работе |
| MNAWitchcraft × Mana and Artifice 3.1.11 (В4: «если не слишком тяжело — портировать, иначе пропускаем») | Поток 4b, вторым шагом | opus — порт jar; тем же сеансом, чтобы не плодить контексты | в очереди |
| Раздать `docs/client-checklist.md` друзьям; выбор хостинга (В1) | владелец | — | ждёт владельца |

Лимиты: 2026-10-11 22:14 UTC окно `five_hour` дошло до 96% (все остановлены заранее), 2026-10-12 03:10 — 80%+. Недельное окно
растёт ~10%/окно (37% на 2026-10-12 03:00, сброс 17.10) — держим один рабочий opus-поток за раз.

Раунд 4 — 2026-10-12:
| Задача | Поток (сеанс) | Модель — почему | Статус |
|---|---|---|---|
| Задача 11 (Origin Furs / Arknights Furs × AzureLib 3.1) + порт MNAWitchcraft (В4) | Поток 4b (session_01S19ePL8u9HoLhUJ1ck9Jb8) | opus — порт jar | **влит PR #18**: встроенный AzureLib 2.0.39 (клиентский `*-mmport-client.jar`), MNAWitchcraft портирован; сеанс архивирован |
| Репозиторий публичный, установка по ссылке (raw `refs/heads/claude/affectionate-davinci-4gddoh/pack/pack.toml`; ветки `main` нет) | Координатор | — (только документация) | **влит PR #20**: README, `docs/friends.md`, условия в CLAUDE.md |
| Игра без лицензий (В7): `online-mode=false` + защита ника паролем (Sol's AuthLogin, `/register`, `/login`) | Поток «Офлайн-сервер» (session_01EDF9Qw9dbkJPF2zXsCo8ME) | sonnet — конфиги и серверный мод по образцу | **влит PR #19** |
| 23 мода CurseForge без `url` (запрет стороннего скачивания) — jar в `pack/mods/`, 1 переведён на Modrinth; test-server `Done` | Ревью и интеграция (2) (session_01AtTBE2K9ovNYSKAU4EMyda) | sonnet — механическая замена + тест по образцу | **влит PR #22** (правило — в CLAUDE.md) |
| Ревью и слияние | Ревью и интеграция (2), вместо старого сеанса session_01VfvQ6JaX74GK5NGjWZnWoi (контекст ~720k съедал лимит, архивирован) | sonnet — ревью по чек-листу, экономия лимита | работает |
| Проверка установки по ссылке и входа на сервер | владелец | — | в работе |
| Ассеты Wizardry, Galacticraft этап 2 | — | sonnet / opus | в очереди (В5 «а»: ждём отзывов друзей) |

Решения владельца (через «Связь»):
- В1б хостинг: до 700–800 ₽ в месяц на игрока; рекомендован Millida 16 ГБ (1290 ₽), российский VPS 3000–4750 ₽ —
  при 6+ игроках; В2: бесплатный тариф Millida владелец проверит позже.
- В4: MNAWitchcraft портировать, если не слишком тяжело (сделано). В5 «а»: доделать текущее и ждать отзывов друзей.
- В7: играем без лицензий — `online-mode=false` с защитой от подмены ника.
- Репозиторий публичный (2026-10-12); при претензии правообладателя — снова приватный, раздача только `.mrpack`.

Лимиты: пороги прежние (`five_hour` 75% — без новых потоков, 90% — всем стоп; `seven_day` 80% — сообщить владельцу).
2026-10-12 16:38 UTC: `five_hour` 43%, `seven_day` 59% (сброс 17.10 06:00 UTC).

Раунд 5 — 2026-10-12 (вечер, новые требования владельца после первого теста):
| Задача | Поток (сеанс) | Модель — почему | Статус |
|---|---|---|---|
| Русификация («очень важно»): аудит ru_ru, готовые русификаторы, русский по умолчанию; этап 2 — свой пак строк (В8 «как рекомендуют»: строки сейчас, книги по отзывам) | Поток 6 (session_01QeeDTwLudyjkD12gbCa4Kp) | sonnet — аудит/поиск; перевод строк — субагенты haiku + валидатор | этап 1 влит PR #26; этап 2 — PR #27 (11 485 строк), ждёт CLIENT OK |
| JEI: рецепты и использование для всех модов, недостающие аддоны, KubeJS не прячет рецепты, пункт в client-checklist | Поток 7 (session_015Rs6ouZTeLQJqbVN5ca2yz) | sonnet — аудит + аддоны по образцу | в работе |
| Производительность клиента: тест владельца — загрузка 2,5+3 мин, подвисания (ноутбук 16 ГБ, RTX 2060, i5-9) → оптимизации, дефолты тяжёлых модов, пресет графики, «Если тормозит» в friends.md, замеры | Поток 8 (session_01Ae6LbVeTMH97URzNTPeK6E) | sonnet — конфиги/моды по образцу; opus — если понадобится отладка | в работе; у владельца после Xmx 14→8 ГБ, дискретной видеокарты, без шейдеров/DH — «не подвисает сильно» |

Тест владельца (Prism, офлайн-аккаунт, ссылка packwiz): установка 387/387, «Missing libraries» решено онлайн-запуском (friends.md, PR #24),
главное меню без ошибок, одиночный мир создан, Origins и русский интерфейс работают.

### Поток 8 — Производительность
Файлы: `docs/performance.md`, `mods-src/mm-perf/`, `pack/config/{modernfix-mixins.properties,DistantHorizons.toml,jei/,sound_physics_remastered/}`, `pack/options.txt` (только строки графики).
Сделано (ветка `claude/perf-stream8`, подробности и замеры — `docs/performance.md`):
- [x] Причины: «вход ~3 мин» — запуск JEI на потоке отрисовки при каждом входе в мир (3,5 мин в стенде); создание мира — спавн-чанки (167 из 263 с) и генерация Distant Horizons.
- [x] ModernFix `remove_spawn_chunks` + `dynamic_resources`, DH (радиус 64, без генерации), JEI без поиска по подсказкам, мод `mm-perf` (JEI без рецептов чар точила/наковальни),
  Sound Physics, пресет графики `options.txt`, Let Me Despawn / FastFurnace / FastWorkbench; сервер до `Done` (58 с). Результат: создание мира 263 → 75–88 с, JEI 3,5 → 1,6 мин, меню 273–301 → 216–228 с.
- [x] `friends.md` — «Если тормозит» (Xmx, JVM-флаги, видеокарта, spark); Xmx/JVM через packwiz/.mrpack задать нельзя — только инструкцией.
- [ ] Решения владельца: JEI остаток (плагин Iron's Spells ~30 с; EMI вместо JEI?), железо владельца, `dynamic_resources` — визуальная проверка моделей; список «в отчёт» (Async Particles, BadOptimizations…) — в `docs/performance.md`.

### Поток 9 — Боссы (таблица и огромные боссы)
Файлы: `scripts/boss_table.py`, `scripts/boss_info.py`, `scripts/tests/bossdump.js`, `iafcheck.js`, `docs/bosses.md`/`csv`.
1. ~~Таблица боссов сборки~~ — **сделано** (PR #32): 212 записей, числа из живой сборки; правило ведения — в «Правилах для всех потоков».
2. Новый мод с огромными боссами — **Ice and Fire CE 2.1.13** (по умолчанию; вопрос владельцу ушёл через «Связь», альтернатива —
   Bosses of Mass Destruction): в сборке, сервер/клиент ОК, русификация, `docs/compat.md` («Ice and Fire CE»).
3. **Bosses of Mass Destruction 1.1.2 + Cerbon's API** (решение владельца В12 «а», PR ветки `claude/boss-bmd`): в сборке, spawncheck без просадки,
   SERVER/CLIENT OK; условие — если у владельца спавн мобов снова пропадёт, убрать оба мода (см. compat.md).
4. Дальше: описать остальные боссы в `boss_info.py` (Arknights-моды), проверка драконов в игре (нагрузка на ноутбуке), JEI-плагин IaF.
