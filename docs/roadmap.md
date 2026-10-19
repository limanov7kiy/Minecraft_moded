# План работ и разделение по чатам

Состояние на 2026-10-12 (актуальная сводка — раздел «Координация», раунд 6). Связка **1.20.1 + Forge 47.4.26**, сборка — `pack/` (packwiz).
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
| Falling Tree 1.20.1-4.3.4: дерево падает целиком (FALL_BLOCK), присед = один блок, прочность «всё или ничего» (миксин в mm-compat), лимит 300 брёвен; тест на фейковом игроке | `docs/compat.md` «Falling Tree», `pack/config/fallingtree.json`, `scripts/tests/fallingtree.js` |
| Серверные тесты: гравитация, маршруты, ракеты, кислород, руды, заклинания | `scripts/tests/` |

## В работе
- Владелец проверяет установку по ссылке (Prism + packwiz-installer) и вход на сервер в офлайн-режиме; находки — в compat.md.
- Очередь (по решению владельца В5 «а» — после отзывов друзей): оригинальные ассеты Wizardry, Galacticraft этап 2.
- Идея владельца (низкий приоритет, после 17.10 и отзывов): многопоточность — исследовать практичные пути (async-моды
  под 1.20.1 Forge, параллельная генерация/загрузка чанков, JVM/GC: ZGC/Shenandoah), потокобезопасность наших модов
  (Valkyrien Skies, Create, Lycanites…), выгода и риски → отчёт в `docs/` и варианты владельцу. Модель: opus — архитектура
  и риск нестабильности (ванильный тик однопоточный, полная переделка ядра нереалистична).
- Бэклог (решение владельца 2026-10-19, не срочно, после энциклопедии и версии сборки): мод «чаровальный стол с выбором
  конкретных чар за большую стоимость» (кандидаты под 1.20.1 Forge: Enchanting Infuser, Easy Magic, Apotheosis-подобные).
  Исследовать совместимость (Redux/Wizardry, Iron's, Cataclysm, Almost Unified), сторону, баланс → вопрос владельцу В## с
  2–3 вариантами (стоимость: опыт/предметы/золото, доступные чары, предел уровней). Меняет баланс — ставим только после выбора.
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
- [x] Фриз сервера от Mana and Artifice (блокирующий `getChunk` в `onWorldTick`, тик до 45 мин) — mm-compat 0.5.2, миксин `MnaChunkTickMixin`, тест `mnachunk.js` (поток 24, compat.md).

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
- [x] Китайские литералы кода/NBT (Талон Боба и др.): миксин LiteralContents + словарь 953 строки в `ru-lang`, `docs/russification.md`, `docs/compat.md`.
- [ ] Прогнать `scripts/test-client.sh` и убедиться, что язык/паки подхватываются; строка в `docs/friends.md`.

### Поток 11 — Перевод книг (ветка `claude/translate-books`, от `claude/abandoned-cities`)
- [x] Кодекс **Mana and Artifice** (свой JSON `assets/mna/guide`, ≈ 246 тыс. знаков) + вычитка 660 названий MnA.
- [x] **Animus**, **AMI Wiki**, **Dimensional Dungeons** (lang лежал как `ru_RU.json` — не грузился), **Ars Magica Legacy**, аддоны Ars, bobbook, exguide; Ars Nouveau (+18 ключей).
- [x] Проверка всех jar на lang с неверным регистром/форматом (`ru_RU.json`) — только Dimensional Dungeons.
- [x] Superb Warfare — переводить нечего (3 имени техники).
- [ ] Приёмка в игре (кодекс MnA, Анимус, AMI Wiki, Dimensional Dungeons). Остальное — по отзывам.
- [x] **Caerula Arbor** `caerula_illustration` (429 китайских строк, ≈14 тыс. знаков) — переведена целиком (ветка `claude/translate-caerula`).
- PR в интегратор — после слияния #31 (пак внутри мода `ru-lang`).

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

## Координация (поток «Координатор (2)», session_01TzhEBzXMNvkdaYhGJBCkDA; до 2026-10-12 22:10 — session_015zoozt65pTMY4bR2XBFgb8)
Координатор раздаёт задачи потокам и выбирает модель; ревью и слияние — поток «Ревью и интеграция»;
вопросы владельцу — только через «Связь с владельцем (2)» (session_01Hncw6rYwbbT38frccVHWD8; прежняя session_01TAw9QPkPRVtJZY37wFhBNv архивирована).
Правило владельца: при большом контексте сеанс заменяется свежим с коротким handoff и ссылками на `docs/`.

**Политика автономности (постоянное решение владельца, 2026-10-19):** «там где есть риск пиши, там где возможно, чините сами,
мне лишь сообщайте о том, что было исправлено или изменено, я буду корректировать в случае чего».
- Сами, без вопроса: низкорисковое и обратимое — баг-фиксы, конфиги, патчи совместимости, тексты/доки, форма и Таблица отчётов,
  мелкие правки скриптов. Обычный процесс: тест-сервер → Ревью → слияние. Потом владельцу через Связь 1–3 строки «что исправлено/изменено»
  и как откатить (PR/коммит).
- Спрашивать (В## а/б/в с рекомендацией): потеря данных/миров; удаление или отключение модов/боссов/контента; заметный баланс/механики;
  безопасность, приватность, секреты; лицензии и публичность; большие загрузки (сотни МБ); действия на компьютере или в аккаунтах владельца;
  всё необратимое.
- Остаются в силе: боссов не удалять; личные данные — только в приватной части; лимиты (стоп на 95% недели); проверки Ревью не сокращаются.
- Друзьям «обновляйтесь» — пачкой, когда набирается заметное обновление.

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

Раунд 6 — 2026-10-12 вечер → 2026-10-13 ночь (владелец спит; решения — по принятым правилам):
| Задача | Поток (сеанс) | Модель — почему | Статус |
|---|---|---|---|
| Русификация: этап 2, мод `ru-lang` (русский пак всегда сверху), названия модов/Arknights английские; Lost Cities (В10 «а», `rarecities`) | Поток 6 (session_01QeeDTwLudyjkD12gbCa4Kp) | sonnet — строки, скрипты, конфиги | **влиты #27–#30, #31**; сеанс архивирован |
| Подсказки загрузки мира по-английски (Aether Redux/Deep Aether, 21 строка) → ru_ru пака `ru-lang`; см. `docs/russification.md` §8а | Поток 16 | sonnet — разбор jar, lang | PR в `claude/affectionate-davinci-4gddoh` |
| Длинные подписи клавиш уезжают за левый край на GUI x3+: префиксы зашиты в ключи Ars-модов; 100 строк сокращены в ru-lang (`scripts/lang_shorten_keys.py`), `docs/russification.md` §8б | Поток 17 | sonnet — разбор jar, lang | PR в `claude/affectionate-davinci-4gddoh` |
| Китайские строки в коде модов («Талон Боба»): миксин `LiteralContents` в `ru-lang` + словарь 953 литералов, jar не патчены | Поток 6 | sonnet — по готовому плану, миксин маленький | **влит #39** |
| Animus / «ключ разрешается в английский» | Поток 6 | — | закрыто без правок: из 72 тыс. ключей 742 английских — намеренные названия Arknights и имена собственные; Animus английский только при откате паков (см. Поток 13) |
| JEI: аддоны, плагин Imbuement Altar | Поток 7 | sonnet | **влиты** (раунд 5) |
| Производительность клиента | Поток 8 | sonnet | **влит #35**; дальше Поток 8 работал как «Ревью и интеграция (2)» |
| Боссы: таблица `docs/bosses.md`, Ice and Fire CE (#34), Bosses of Mass Destruction + Cerbon's API (В12 «а») | Поток 9 (session_01WKtpNF9pEz1ZLSL76u21Qw) | sonnet — моды по образцу + тест спавна | **влиты #32, #34, #36**; сеанс архивирован |
| Перевод книг: кодекс Mana and Artifice (+660 исправленных названий), Animus, AMI Wiki, Dimensional Dungeons, Ars и аддоны, bobbook | Поток 11 (session_01JahaRV9o4kKTT12dVPbUAf) | sonnet — перевод пачками + валидаторы | **влит #37**; Caerula Arbor (429 строк) — переведена (PR из claude/translate-caerula) |
| Порт Wizardry Golems 0.2.0 (Extra Golems) → Necromancer's Delight (В13 «полный перенос», включая мобов Fish's Undead Rising) | Поток 12 (session_01Bg28XprSLsi1MoAEr25m4F) | opus — порт кода 1.12.2, низкий приоритет | #38 готов (golemcheck 13/13, CLIENT OK), **отложен по лимиту**; ND-1 — WIP `claude/necromancers-delight`, точка остановки в `docs/wizardry-port.md` |
| Атлас текстур > 16384² (на слабых GPU слетали русские паки) | Поток 13 (session_01MpjMThXiMwvDnXrMqqdZXL) | opus — диагностика рендера | **влит #41** (~290 → ~125 Мпкс); сеанс архивирован |
| Битые модели: Mekanism More Machines (`neoforge:composite`) и кольцо пилона Mana and Artifice (нет `pylon_ring.obj`) | Поток 14 | sonnet — разбор jar, KubeJS-ассеты | PR в `claude/affectionate-davinci-4gddoh`; `CLIENT OK`, подробности — `docs/compat.md` |
| Ревью и слияние: #35, #31, #36, #39, #41, #37 | Ревью и интеграция (2) (session_01AtTBE2K9ovNYSKAU4EMyda) | sonnet — ревью по чек-листу | работает; при сильном росте контекста — замена свежим сеансом |

Порядок слияния PR, которые все трогают jar `ru-lang` и `index.toml`: следующий автор подтягивает интегратор и пересобирает
`scripts/build-ru-pack.sh` (бинарный jar — только пересборкой); повторный `test-client` не нужен, если влитое не пересекается с PR.
Ветка `claude/ru-force-packs` (принудительное включение MPLOC/RTF+) не слита: до #41 при переполнении атласа давала краш вместо отката;
после #41 — можно вернуться.

Лимиты: 2026-10-13 00:00 UTC `seven_day` 75% (порог владельца на эту неделю), расход ночью ~2,5%/ч → режим «2 рабочих потока»
(Ревью + Поток 13), Поток 12 остановлен с WIP, Поток 11 — после #37. Вопрос В14 владельцу: держать режим до сброса 17.10 06:00 UTC
или поднять порог.
Ждёт владельца (13.10): русские паки включены сразу; мобы Lycanites и драконы Ice and Fire без «missing»-текстур; книги
(MnA, Animus, AMI, Dimensional Dungeons, bobbook) открываются по-русски; спавн мобов после BoMD; ModernFix-текстуры; В2 Millida; В14.

Раунд 7 — 2026-10-13 (день; владелец на связи; лимит: «2 потока», стоп на 85% недели):
| Задача | Поток (сеанс) | Модель — почему | Статус |
|---|---|---|---|
| #38 Wizardry Golems 0.2.0 — довести и слить вместо остановленного Потока 12 | Ревью и интеграция (2) | sonnet — слияние + тесты по образцу | **влит**; Поток 12 архивирован (ND-1 — WIP `claude/necromancers-delight`) |
| Книга Caerula Arbor (429 строк) | Поток 11 | sonnet — перевод пачками | **влит #43**; сеанс архивирован |
| Битые модели: mekmm `neoforge:composite`, MnA `pylon_ring.obj` | Поток 14 (session_01GMirfF4NSv9aGz89BvsGpA) | sonnet — замена ассетов через KubeJS | **влит #44**; сеанс архивирован |
| Катаклизмы (В15): Weather2 + CoroUtil, Natural Disasters!, свой мод «Оберег» `mods-src/disaster-ward`; постройки ломаются (уточнение владельца) | Поток 15 (session_016FK9FaQRGFk4kHRNy6PKCJ) | sonnet — моды + маленький мод по образцу | **влит #45** |
| WorldEdit (В18) — перенос построек на сервер, только OP | Поток 15 | sonnet | **влит #46**; сеанс архивирован |

Решения владельца: В14 — 2 потока до сброса; В15 — катаклизмы (вариант 1 + на усмотрение 3/4, дубли убрать, оберег только из креатива, постройки ломаются);
В16 — OreSpawn: поставить ремейк Chaos Persists (1.20.1), сравнить с оригиналом 1.7.10 и доделать недостающее;
В17 — перенос построек: WorldEdit; В18 — «Да, добавляй».

Очередь после сброса лимита (17.10 06:00 UTC):
- OreSpawn: Chaos Persists в тестовую сборку → совместимость/дубли (Ice and Fire, Lycanites, Mowzie's) → список недостающего из 1.7.10 → выбор владельца → порт отдельным модом (opus).
  Этап 1 сделан (поток 23, ветка `claude/orespawn`): ремейк + AttributeFix в сборке, сравнение — `docs/orespawn.md` (из 1.7.10 не хватает только яйца Zombie Pigman); ждёт выбора владельца по списку в конце документа.
  Доводка (поток 25, ветка `claude/orespawn-tune`): спавн мобов в Overworld ×0,25 (KubeJS), руды в 4–6 раз меньше (`pack/config/chaospersists.cfg`), перевод +427 строк; Utopia и разрушения не трогали — `docs/orespawn.md`, раздел «Доводка».
- Necromancer's Delight ND-1: с WIP `claude/necromancers-delight` (точка остановки — `docs/wizardry-port.md`), свежий сеанс opus.
- Оберег: единый радиус для Weather2 (у него 128) и Natural Disasters! (64); бедствие, прилетевшее снаружи, не ломает блоки в зоне.
- Ветка `claude/ru-force-packs` (после #41 атлас помещается) — оценить.
- Extreme Natural Disasters (вулканы) / This Tsunami — только по запросу владельца.

Пауза 13–17.10 (неделя дошла до 96%: в основном тратили другие проекты аккаунта; стоп-порог владельца поднят до 95%):
| Задача | Поток (сеанс) | Статус |
|---|---|---|
| Экран клавиш при GUI x3: 100 подписей сокращены (`scripts/lang_shorten_keys.py`) | Поток 17 | коммит 1b07f3d ушёл прямо в интегратор без PR; Ревью проверил постфактум — оставлен; сеанс архивирован |
| Falling Tree (В20/В21) | Поток 18 (session_01JYt9eB41N7DcqRHeab2qLY) | PR #49, по статике чисто; тест на объединённой сборке не дошёл — сервер убивал WorldEdit (ниже) |
| Находка: WorldEdit 7.2.15 делает первый тик сервера >60 с → ServerHangWatchdog убивает сервер | Ревью | нужен `max-tick-time=-1` (тест-сервер, серверный zip, docs) |

Решения владельца: В19 — Lost Cities «Оставь как есть» (современный стиль, `rarecities`); В20/В21 — Falling Tree, Dynamic Trees не ставим:
присед = по блоку, прочность за каждое бревно, при нехватке прочности — один блок (миксин mm-compat), `maxSize` 300 ABORT, широкие стволы не трогаем;
В22 — фризы у игроков: скрипт сбора диагностики и анализ; В23 — Clanging Howl (мод на CurseForge, есть русский перевод на Modrinth) — **сделано, поток 20**;
В24 — паразиты: Fungal Infection: Spore (+ SRP System, если совместим), Оберег не пускает заразу и заражённых в зону;
В25 — разбор Embeddium/Oculus («Sodium на Forge») в рамках анализа фризов.

Раунд 8 — 2026-10-17 (сброс лимита; порядок владельца): скрипт диагностики (Поток 19, session_01VBNcQCP282sjURUfRbitCD) → max-tick-time и #49 (Ревью) →
анализ фризов + Embeddium/Oculus → Clanging Howl → паразиты + Оберег (единый радиус, бедствия снаружи, зараза) → OreSpawn, Necromancer's Delight.

Итоги раунда 8 (17.10; ночью пятичасовое окно дошло до 86% → пауза до сброса, затем «запускай процессы и сливай то, что готово»):
| Задача | Поток (сеанс) | Модель — почему | Статус |
|---|---|---|---|
| Скрипт сбора диагностики у игроков (`scripts/collect-diagnostics.ps1/.bat`, `docs/diagnostics.md`) | Поток 19 (session_01VBNcQCP282sjURUfRbitCD) | sonnet | **влит #51** |
| `max-tick-time=-1` (тест-сервер, серверный zip, docs) | Ревью | sonnet | **влит #52** |
| Falling Tree + миксин прочности mm-compat | Поток 18 (session_01JYt9eB41N7DcqRHeab2qLY) | opus — миксин | **влит #49** |
| Clanging Howl + перевод | Поток 20 | sonnet | **влит #53** |
| Перевод Caerula Arbor (2519 строк), опечатка «Ппластины» | Поток 21 | sonnet | **влит #54** |
| OreSpawn этап 1: Chaos Persists 0.9.1 + AttributeFix, `docs/orespawn.md` | Поток 23 | sonnet | **влит #55**; сеанс архивирован |
| Фризы у игроков: все дампы потоков — Mana and Artifice `onWorldTick` ждёт генерации чанка → `MnaChunkTickMixin` (mm-compat 0.5.2) | Поток 24 (session_01RJtt54NkGNcCBYBySSmGcG) | opus — разбор дампов + миксин | **влит #57** (a70d294; mnachunk.js maxTick 0,97 с) |
| Паразиты: Fungal Infection: Spore + SRP System, датапак на 682 вида мобов, Оберег 1.1.0 (радиус 96 для всех бедствий, зараза и заражённые не входят) | Поток 22 (session_014aSvECF1SDZZ69C9kbu4XW) | sonnet | **влит #56** (d319e23; sporeward.js 24/24); нагрузка `sporeload.js` и клиент не прогнаны |
| Iron Chests 14.4.4 (оригинал alexbegt, both); `docs/friends.md`: радиус Оберега 96, где взять блок, старые ресурспаки | Поток 26 (session_01VgSdALGeFhf4tEXBDbNjNY) | sonnet — механика | **влиты #58, #59** (3bfd34b) |
| Доводка OreSpawn: спавн в Overworld реже, руды меньше и мельче, перевод структур и настроек; Utopia и разрушения не трогаем | Поток 25 (session_013nBhme6Z4ja8tYJiNzhYwY) | sonnet — конфиг + перевод | **влит #61** (b51ee0f; руды −72…−85%, естественный спавн в Overworld ×0,25 через KubeJS, +427 строк ru); сеанс архивирован |
| Necromancer's Delight ND-1 с WIP | Поток 27 (session_01Q9GnopY8NLkbfXBhFoXQAp) | opus — порт кода 1.12.2 | **влит #62** (18f08d2; ndcheck 11/11, SERVER/CLIENT OK); ND-2 — ниже |
| Ars Nouveau «Incorrect key» — это создание конфигов при первом запуске (во втором 13 973 → 3); обзор Embeddium 0.3.31 / Oculus 1.8.0 (В25) | Поток 28 (session_015HbD2RG4mJzUSWPrjkkSTq) | sonnet — анализ логов, обзор | **влит #64** (c1779bc) |
| Прыжок «Охотников Бездны» ~12 → ~3,3 блока: ASM-патч Arknights_Weapon 1.5.6 (эффект «Глубинный охотник» 10 → 2, броня «Агор» 6/3 → 2); В29 — «Снежный» и бафы Caerula Arbor не трогаем | Поток 28 | sonnet — патч байткода | **влит #65** (a32fb9a; abyssaljump.js amp=2) |
| ND-2: 13 мобов Fish's Undead Rising | Поток 27 | opus | **влит #67** (e6bcda6; furcheck 13/13, ndcheck 11/11); сеанс архивирован |
| Первый тик ~130 с = WorldEdit (`serverStarted` → таблица состояний блоков); у клиента WorldEdit необязателен (по умолчанию выкл, в .mrpack client=unsupported), на сервере остаётся | Поток 28 | sonnet — jstack + packwiz option | **влит #66** (2b0195e; export-pack: WE client=unsupported в .mrpack, в server.zip есть; SERVER OK) |
| «Клиентские удобства» в mm-perf: В27 Режим слабого ПК + Вернуть, необязательный ресурспак низких текстур; В28 клавиши модов по умолчанию «Не назначено» (у играющих — только стоявшие на заводском), Controlling; В30 таблица памяти и «если виснет» в friends.md, DH по умолчанию полегче, подсказки о памяти | Поток 28 | sonnet — клиентский мод + docs | **влит #69** (eaaa64f; mm-perf 0.2.0-client, Controlling 12.0.2 + Searchables, DH радиус 48 LOW/LOW 1 поток, Potato Craft-8x (Saransh, CC-BY-4.0) выкл; selftest: клавиш модов назначено 0, игроком назначенные сохраняются); сеанс архивирован |
| ND-3: 15 заклинаний (16-е в оригинале не зарегистрировано) + 12 артефактов Necromancer's Delight; фикс «Грибного мертвеца» (слой грибов — missing model из-за ModernFix dynamic_resources) | Поток 29 (session_01785PcDqxv58AvXEsWqMKMc) | opus — порт кода 1.12.2 | **влит #71** (4aa630a; nd3check 55/55); глазами не проверено; сеанс архивирован |
| СРОЧНО: «мобы не спавнятся» (одиночка; Overworld/Aether/End/Nether/Twilight — по месту). Причина: Northstar Structures 1.0.0 `onEntityJoinLevel` удаляет любую сущность в «кратерах» Луны (круги r150–250 в ячейке 1024 + бассейн Эйткена r2400), считая их по зерну без проверки измерения → «мёртвые зоны» во всех измерениях. Cerbon's API/BoMD (владелец против удаления), ModernFix remove_spawn_chunks, Оберег, Spore — невиновны | Поток 30 (session_01HUeEvRBjKjz6n5mpujjYa3) | sonnet — бисекция слушателей EntityJoinLevelEvent | **влит #72** (6c18367; mm-compat 0.5.3 — обработчик только на Луне; овца и зомби: summon/яйцо, естественный спавн снаружи ок (600,600)); **владелец подтвердил в игре 17.10: «мобы спавнятся»**) |
| Скалк не реагирует на голос в одиночке: Vibrative Voice и другие геймплейные моды стояли `side=server` → в .mrpack client=unsupported → во встроенном сервере одиночки их нет. В `both`: Vibrative Voice, BaguetteLib, Gravestone x Curios Compat, Blue Skies Gravestones Compat, Twilight Cave Fix; Sol's AuthLogin, VC Admin, Broadcast — остаются server | Поток 31 (session_01PSHA2Kz5a1CoFn6omeF4Ro) | sonnet — стороны packwiz | **влит #74** (762685f; SERVER/CLIENT OK, плагин vibrativevoice в клиенте); «голос → сенсор» в одиночке руками не проверен — инструкция в compat.md; сеанс архивирован |
| Нагрузка Spore (`sporeload.js` + JFR → docs/performance.md): без заразы 0; 1 очаг ≈ +10–25 % тика, 3 очага ≈ +45–50 %, упор в лимит деспавна (~70); конфиг не менялся, рекомендация 40→25 / 20→12 в доках. Клиент: 254 моба spore/chaospersists/srpsystem в новом мире — ошибок моделей/текстур/lang нет; новый мир после #72 спавнит. Фон сервера: Torches Become Sunlight ~12 %, Ad Astra ~9 %, Chaos Persists ~7 % — в очередь (микрофризы) | Поток 30 (session_01HUeEvRBjKjz6n5mpujjYa3) | sonnet — нагрузка + клиент под Xvfb | **влит #76** (6472d2f; Ревью убрал случайные pip-колёса из корня; SERVER OK); звук/ИИ/реальная видеокарта не проверены; сеанс архивирован |
| Yes Steve Model: В32 «делаем», В33 — все модели Arknights/Endfield/PGR из Elaina69/Yes-Steve-Model-Repo (на сервер, YSM раздаёт сам) + своя Priestess (Arknights): модель, анимации, осколок Originium всегда рядом и принимает форму предмета в руке, светящиеся ромбы в глазах (при взаимодействии; при чате — KubeJS → `/ysm molang execute`, если другим не видно — mm-perf, В34 «проверим, при необходимости добавим») | Поток 32 (session_01ApyCbAZxm2K8ZEMVzN2quJ) | opus — Bedrock-модель + Molang | **влит #78** (a4186a8; 65 моделей Elaina69 через packwiz-метафайлы raw.githubusercontent, AK_Rosmontis_v0.9 не читается YSM 2.6.5 — исключена; Priestess: 13 форм осколка по тегам mm:ysm_form/*, ysmGlow-ромбы, KubeJS ysm_chat_glow.js; превью — свой рендер, клиент YSM в облаке не запускается; SERVER OK, fetch 65/65); в игре глазами не проверено, «видят ли другие вспышку при чате» — проверка владельцем вдвоём; сеанс оставлен до отзыва владельца |
| Priestess v2→v4 по отзывам владельца: лицо — глаза на высоте Irene, маленькие ромбы-зрачки (04_eyes), без рта/бровей/румянца; волосы выше и объёмнее; осколок Originium — чёрный кристалл с маленьким светящимся ядром и роем крошек, 13 форм; превью при игровом освещении (прошлый «студийный» рендер скрывал проблемы) | Поток 32 | opus | **влит #82** (8b01168; «Пока ок, добавляй в сборку»); сеанс открыт для правок |
| Нехватка памяти (OutOfMemory 8 ГБ при генерации структур, игрок с RTX 5060): Lost Cities BuildingInfo запоминал соседей → вся сетка городов вечно в куче; миксин LostCitiesNeighborMixin (работает на старых мирах) + фильтр огромных шаблонов Arknights (до 2 млн блоков) по коробке чанка; mm-compat 0.5.5. Маршрут Xmx 6G: было 6,1/6,1 ГБ и OOM за 14 мин → 3,54 ГБ за 15,5 мин | Поток 34 (session_01YYtBMEELFyPrUSnHXR66P8) | opus — jmap/heap dump + миксин | **влит #81** (b2a7bec; Ревью: генерация Lost Cities и шаблоны совпадают с 0.5.3 по хешам); сеанс архивирован |
| Микрофризы раз в ~25 с у игроков: клиентский spark — Origin Furs (наш порт) в ClientTick каждые 1000 вызовов перебирал все ресурсы (~4 с); OriginFursReloadCacheMixin, mm-compat 0.5.6. Серверный spark «Тазика»: тик 71 мс, 548 существ; Alex's Mobs 10,7 %, Arknights Weapon 9,8 %, Torches Become Sunlight 8,5 % | Поток 33 (session_01KSTMV78ZmXUMhcizJy4yMi) | sonnet — разбор spark protobuf | **влит #85** (b1885e7; CLIENT OK; строку применения в мире подтвердит игрок). #80 (Arknights/Torches, mm-compat 0.5.7, −8 мс тика) — В35 «б»: ждёт теста владельца после обновления |
| WorldEdit выключен по умолчанию и на сервере (В36 «б»; ~2,7 ГБ кучи): optional/default=false моды → mods/optional/ в server.zip и test-server; инструкция переноса постройки в hosting.md/friends.md | Поток 36 (session_0199vdh12KN4E7PEM8ZHCz96) | sonnet | **влит #83** (362ea32); сеанс архивирован |
| Вопросы игрока: урона от падения нет у рас Cautus/Feline/Aslan (особенность рас, владелец: «оставить как в моде»); Крейк нейтрален по задумке, Эпион/Балаянг агрессивны, «Пугало» в сборке нет; «Гриб-покров» — блок, модели целы. «Нет урона ни от чего» (прошло само): постоянного блокера нет (SRP DamageLimit, Contingency Contract, Sarkaz — исключены, hurtcheck.js 5→5), кандидаты — расы Griffin/Anasa, броня, долгие эффекты; что снять в момент — в compat.md | Поток 35 (session_0186tEUhP91kksjaHYsZB5F5) | sonnet | **влиты #84** (9831760), **#86** (974e9ff); сеанс архивирован |
| Решения владельца 18.10: В35 «б» (#80 после теста), В36 «б» (WorldEdit выключен и на сервере), В37 «в» (все модели Azur Lane из FREE-YSM-AUTHOR, с предупреждением о 18+), Оберег — рецепта нет, только креатив; защиту от Weather2 внутри зоны «оставить как есть»; Оберег доработать под «Ориджиниумную бурю» (мобы бури не появляются в зоне, но проходят; блоки/молнии бури в зоне не падают — решение Координатора), буря редкая, рост ориджиниума выкл | — | — | в работе: Поток 32 (ward-oristorm), Поток 37 (Azur Lane FREE-YSM-AUTHOR, session_01DkmZia3U8C8T5mkW4RrEjV); **влит #87** (765b8d9; 19 моделей Azur Lane из Elaina69, fetch 84/84, SERVER OK) |
| Решения владельца 18.10 (позже): В38 «в» — всё уникальное из второго сборника Azur Lane FREE-YSM-AUTHOR (~590 МБ, 136 файлов), отдельным пакетом в меню YSM, с замерами памяти/входа и предупреждением 18+ в friends.md; рост ориджиниума **включить**, медленно, не заходя в зону Оберега (отменяет «рост выкл» выше) | — | — | в работе: Поток 32 — PR #89 (буря + Оберег, дорабатывается под рост), Поток 37 (Azur Lane 2) |
| YSM: второй сборник Azur Lane из FREE-YSM-AUTHOR (В38 «в»): 124 модели / 501 МБ отдельным пакетом, 12 нечитаемых YSM 2.6.5 исключены (scripts/ysm-models-author-skip.txt), ссылки на коммит 17db8b6 + sha256, 18+ и «Тазик» в friends.md; куча сервера 3,23→3,45 ГБ; клиент не проверен (К7) | Поток 37 (session_01DkmZia3U8C8T5mkW4RrEjV) | sonnet | **влит #91** (32ba9bb; SERVER OK, 208/208 файлов); сеанс архивирован |
| Оберег 1.2.0 + «Ориджиниумная буря» + медленный рост ориджиниума: буря ~раз в 3 ч игры (бросок 5,5 %/10 мин в Overworld при игроке в выживании; oristormfrequency=100000, oristormsummon=200 — правила KubeJS один раз на мир); призывы бури в зоне (radius+edgeMargin) отменяются, пришедшие снаружи черви проходят; призывы в незагруженных чанках пропускаются (тик в бурю 300–900 мс → ~18–32 мс); alloworigrow=true, рост ×0,25, ядра ×0,1, в зоне (+2) рост/ядра/ломание отменены @Pseudo-миксинами; исправлен «буря не начинается после рестарта»; тест scripts/tests/oristormward.js | Поток 32 (session_01ApyCbAZxm2K8ZEMVzN2quJ) | opus | **влит #89** (107a268; SERVER OK, все OSW OK); сеанс архивирован |
| Решения и ответы владельцу 18.10: В39 «а» — рост ориджиниума вне Оберега как в моде (замедление по просьбе «потихоньку» остаётся, ограничений по типам блоков нет; рост заменяет любые блоки, защищает только Оберег); Aegir — защиты от падения в моде нет (arknights_races 1.5.3: только Каутус/Фелин/Аслан), версии: вода, раса «Абиссальный охотник», броня/эффект — ждём `/origin get` и эффекты в момент | — | — | отвечено через Связь |
| Проверка сборки и приём отчётов (В40 — владелец доверил выбор: «г»): публичный docs/whats-new-check.md (W1–W10, без ников), docs/report-form.md (одна Google Форма владельца + Таблица, R-###, статусы, лист «Сводка», роль приёмщика), scripts/report-sheet/Code.gs, маскировка ника/IP/USER/HOST в collect-diagnostics.ps1 (вкл. .log.gz и UTF-16 dxdiag — правки Ревью); не проверено на Windows PS 5.1 и в живой Таблице; владельцу: создать форму, запустить setup, раздать ссылку вне репо, выключить Issues | Поток 38 (session_01PzLZTgdQ31bKkyUYLtkEtT) | sonnet | **влит #94** (2a3617f); сеанс архивирован |
| Обратная связь по отчётам: В41 «б» — публичная docs/report-status.md (сводка, «открыто»/«решено», сортировка по R-номеру, группы по статусу/категории/моду; без имён — В42) + scripts/report-status.py (из CSV вне репо, только публичные колонки; блокирует ники/адреса/контакты всех авторов по словам и падежам, @, ссылки, HTML-сущности — дыры закрыл Ревью); форма собирает подтверждённый адрес (только в приватной Таблице), лист «По людям», «Кто что ждёт», категории, статус «отложено», ответы автору + 5 шаблонов; bootstrap() в Code.gs сам создаёт форму и листы (ручные шаги: «Подтверждённые» адреса, вопрос «Загрузка файла»); «Режим В» (раздел 9) — разрешён владельцем 18.10, исполняет Связь через Sheets-коннектор. В Google не запускалось — первый запуск у владельца | Поток 39 (session_014AC7Cx3Hf6xvQaKqb7ji4q) | sonnet | **влиты #96** (f75be80) и **#97** (b564d5b); сеанс архивирован |
| Фиксы Code.gs после первого запуска у владельца в настоящем Google (нашла Связь через Sheets API): лист ответов формы стал «Таблицей» → createFilter падал (#99: триггеры первыми, оформление в try/catch, «Начало» через setupThenStart_ — правка Ревью); подписи «Сводки» через setFormulas, русская локаль ломала формулы с запятыми, QUERY без данных (#100: setValues, setSpreadsheetLocale(en_US) первой строкой setup, IFERROR; Ревью — формат «@» в «По людям»); формулы в нике (#101: safeText_ — апостроф перед = + - @ \t \r). Первый bootstrap у владельца прошёл; режим В ведёт Связь | Поток 40 (session_018tx5QrXfDrq5q8bgJ91jn6) | sonnet | **влиты #99** (788c32a), **#100** (a86a7ec), **#101** (1f52ed1); сеанс архивирован |
| Code.gs: пересчёт формул после каждого отчёта (writeFormulas_ + flush), диапазон QUERY по последней колонке (вопрос «Файлы»); В43 «а» — авто-письмо автору «Отчёт R-### принят» (MailApp, только из onFormSubmit, один раз, фиксированный текст без ника, проверка адреса/квоты, выключатель SEND_CONFIRMATION; безопасно только при «Подтверждённых» адресах). Проверено вживую Связью: R-002, письмо пришло, «Сводка»/«По людям» обновились сами. Система отчётов работает; владельцу: выключить Issues, раздать ссылку на форму | Поток 40 (session_018tx5QrXfDrq5q8bgJ91jn6) | sonnet | **влит #103** (5315c27); сеанс архивирован |
| В44 «б» — авто-отправка «Ответа автору» без одобрения владельца (кроме «[ЧЕРНОВИК]», когда нужен ответ самого владельца): sendReplies_ раз в 15 мин, MailApp, только подтверждённый адрес, один раз (дата до отправки — Ревью), лимит 20/запуск, выключатель SEND_REPLIES (#106); защита текста ответа — ники/адреса/контакты всех авторов, IP, пути, @, URL кроме статуса/репо, HTML-сущности, разрезание переносом (#108, дыры закрыл Ревью). Рекомендации из ответов — целиком в «Пояснение» публичной docs/report-status.md (просьба владельца), генератор до 1500 знаков (#107; первая страница — R-003). Первый настоящий отчёт R-003 (зависание ПК на загрузке, устаревшая сборка, Xmx≈10 ГБ) разобран, ответ автору — через автоответ | Поток 42 (session_01Aaurt97zbsTpjueVnzHKf8) | sonnet | **влиты #106** (9f9a95b), **#107** (d45eeca), **#108** (f0b9867); сеанс архивирован |
| R-003 (зависание ПК на загрузке): mm-perf 0.2.1 — строка «[mm_perf] память» (Xmx/Xms/ОЗУ/свободно/ядра/GC, без путей) и WARN по-русски (Xmx > 70% ОЗУ или остаток < 4 ГБ) сразу из конструктора мода, до загрузки ресурсов; fml.toml versionCheck=false (HTML в логе); friends.md — Xmx ≤ ~60% ОЗУ, при 16 ГБ ≤ 8; test-client.sh — portablemc==4.4.1 (5.x без --work-dir) | Поток 41 (session_016tNRbCW4Gr4cQ5wizARqZS) | sonnet | **влит #110** (2021ee4; CLIENT OK); сеанс архивирован |
| README: раздел «Справочник файлов» — 109 файлов-инструкций по группам (игрокам, моды/совместимость, русификация, планы, скрипты, отчёты, свои моды/патчи), ссылка + что объясняется; ссылки проверены; правило в CLAUDE.md «новый файл-инструкция → строка в справочнике» (просьба владельца, политика автономности) | Поток 43 (session_016awtTyAgtsfbQ13xtpasWq) | sonnet | **влит #112** (2f92f94); сеанс архивирован |
| Публичная «История решений» docs/issue-history.md (просьба владельца, без личных данных): H-001…H-027 — прошлые проблемы из чата до формы (мобы/Northstar, OOM Lost Cities, микрофризы Origin Furs, скалк, WorldEdit, «нет урона», расы и падение, Priestess/YSM, Оберег/буря, R-003 и др.), причина, решение (PR), совет игроку, статус; ссылки с README и report-status. При слиянии #80 обновить H-017 | Поток 44 (session_01MEu4jvTktzeaBVH1axjJgf) | sonnet | **влит #114** (f6dcc41); сеанс архивирован |
| Пожелания игроков (просьба владельца 2026-10-19): категория «пожелание / идея» в форме (единые R-###), статусы идея → принята → в реализации → реализовано / нужно решение владельца / отклонено / отложено, отдельные счётчики в «Сводке», письмо для пожеланий, раздел «Пожелания» на report-status; граница в report-form.md §10 — без влияния на баланс делаем сами (косметика, YSM, удобство, русификация, доки, производительность), баланс/контент/моды/большие загрузки — только с решением владельца, спорное — тоже. Проверено вживую Связью 2026-10-19: тестовые R-004/R-005 — категория, статус «идея», письмо, счётчики «Сводки» работают; тесты помечены «отклонено/тест» | Поток 45 (session_01DASZpD9SXUjaKrEjpk5HqK) | sonnet | **влит #116** (67fd189); сеанс архивирован |
| Вопрос «Категория» в форме обязателен (решение владельца 2026-10-19 «Сделай обязательным»): `required: true` в FORM.FIELDS, повторный bootstrap ставит `setRequired(true)` у уже созданной формы; старые ответы без категории по-прежнему идут в «баги». Владельцу: заменить Code.gs и запустить bootstrap (ручной обход — тумблер «Обязательный»). Откат: revert #118 | Поток 46 (session_01BWpqdDiVj42KPBogXHEmr7) | sonnet | **влит #118** (beb2fbe); сеанс архивирован |
| Версия сборки видна игроку (вопрос владельца 2026-10-19: друзья не знают, что писать в «Версия сборки»): версия вида 2026.10.19-1 в pack.toml и файле в config, показ в главном меню/заголовке окна и в логе (mm-perf), правило поднятия версии в CLAUDE.md, подсказка в friends.md и форме. До обновления друзьям — версия mm-compat в Prism → «Изменить» → «Моды» | Поток 47 (session_01Q6hV3ampkn2qNCCBrix7j9) | sonnet | **влит #126** (dd7d846): mm-perf 0.3.0, `config/mm_pack_version.txt`, `scripts/check-pack-version.py`; CLIENT/SERVER OK, в логе «[mm_perf] версия сборки: 2026.10.19-1»; надпись в меню и заголовок окна глазами не проверены; сеанс архивирован |
| Эффект «Мысленная буря» (Arknights, вопрос владельца 2026-10-19): id, источник, действие, длительность, снятие, Оберег, команды → ответ владельцу + docs/encyclopedia/arknights/mind-storm.md | Поток 48 (session_01SqHz1AYnT82UxALF71fRMy) | opus — разбор jar | **влиты #122** (447beb5, статья) и **#124** (7266502, В45 «а» владельца: силы arks:{human,sarkaz,sarkaz_nephesh}/thought через pack/kubejs/data дают sarkazs_furnace_side_fables:thought_load_limit — предел 18 → 25 у Человека и 10 подрас Саркасов, 35 у Нефеш; racethought.js; откат — revert #124); сеанс архивирован |
| Энциклопедия сборки (просьба владельца 2026-10-19), этап 1: docs/encyclopedia/INDEX.md, страницы категорий, авто-карточки всех модов (scripts/encyclopedia/build.py, ручной текст между маркерами не затирается), ссылка из README. Этап 2 — подробно Arknights, свои моды, крупные (до 2 потоков); этап 3 — остальные | Поток 49 (session_01CTDomVofvHjo5eEFuEuG3U) | sonnet | этап 1 **влит #121** (e0331f8, 15 категорий, 286 карточек); этап 2б — Поток 51 (session_01Hb6Cbo62EDrBjByGUUovV1) **#123** (86af7f3: космос, Lost Cities, Spore/SRP/Chaos, Alex's Mobs, Cataclysm, Lycanites, Iron's, MnA, Redux); этап 2а — Поток 50 (session_01JwPutJUSCwXut2hZFL3yTM) **#125** (44f8728: Arknights, свои моды, own/disaster-ward.md). Этап 3 (остальные моды, перевод описаний) — в очереди. Сеансы 49–51 архивированы |
| Оберег против зараз (просьба владельца 2026-10-19): проверка на сервере в выживании — мицелий/блоки заражения Spore/SRP/Chaos (внутри, край +16, снаружи, уже лежащий), снятие зараз с игроков (таблица id: снимает/нет, в т.ч. орипатия oridisease), мобы-паразиты, ограничения; поломки — чинить, новые заразы в список — только В## владельцу | Поток 52 (session_01WNnoZBx7wojAJBaP9RaoFy) | sonnet | в работе |

Разбор логов трёх игроков (данные не в репо, копии удалены): у всех длинные фризы — MnA + генерация чанков (исправлено #57);
у одного окна съезжали из-за ресурспака Faithful 1.17.1 (не для 1.20.1); у одного OutOfMemory 13.10 при 8 ГБ (не повторялся).
Решения владельца: OreSpawn — «Utopia не мирная, пусть мобы смогут появляться как в оригинале»; в Overworld — «чтобы это происходило не часто и руды в малом кол-ве»;
Iron Chests — оригинал. Новая неделя с 17.10 06:00 UTC; режим прежний: 2 рабочих потока, стоп 95% недели (В23 — ждёт ответа владельца).
Пауза 13:05–16:10 (пятичасовое окно 82%; при 2 потоках + Ревью окно уходит за ~1,5 ч → держим 1 opus + 1 sonnet).
Решения владельца (17.10, вечер): В27 «а и б» (кнопка слабого ПК + ресурспак); В28 «выбери оптимальное между а и в» (Координатор: отвязать у играющих только заводские назначения, Controlling — да; б — все клавиши модов); В29 — прыжок только у «Охотников Бездны»; В30 — все три пункта о памяти. WorldEdit необязателен у клиента — решение Координатора (обратимо), владельцу сообщено.
Разбор: игрок с RTX 5060 — 10 из 16 ГБ отдано Java → ОЗУ ПК кончается на генерации чанков; рекомендация Xmx 8 / Xms 2 ГБ.
Очередь: при необходимости — нагрузка Spore (`sporeload.js`) и клиентский тест.

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

### Поток 13 — Атлас текстур на GPU с пределом 16384
- [x] Диагноз: «всеобщие» источники атласа блоков у Mana and Artifice (`entity`) и Blood Magic (`models`) тянут в атлас
  мобов Lycanites/Ice and Fire/Alex's/Cataclysm (293 Мпкс > 16384²). Фильтр `pack/kubejs/assets/minecraft/atlases/blocks.json`:
  ~125 Мпкс, атлас 16384×8192, `CLIENT OK` без отката паков (подробности — `docs/compat.md`).
- [ ] Владельцу/друзьям: проверить в игре мобов Lycanites и драконов Ice and Fire (нет ли «missing»-текстур).

### Поток 15 — Катаклизмы (ветка `claude/thread15-disasters`)
- [x] Выбор: **Weather2 + CoroUtil** (торнадо, бури, сирены; торнадо рвут постройки по списку блоков, сундуки не трогают) + **Natural Disasters!** (метеориты, цунами, пожары, землетрясения, кислотный дождь; торнадо и песчаная буря отключены как дубли) — решение владельца В15.
- [x] Свой мод `mods-src/disaster-ward`: «Оберег от катаклизмов» (только креатив, без рецепта и лута; зона 64 блока без бедствий обоих модов), редкий случайный спавн бедствий (30–80 мин на игрока), лимит одновременных бедствий. Extreme Natural Disasters не ставим (дубли, WIP) — `docs/compat.md`.
- [x] Тест `scripts/tests/disasterward.js` (все DWCHECK OK), замеры нагрузки `disasterload.js`, русификация, строка про бэкап в `docs/friends.md`.
- [ ] Владельцу: PR не слит; проверить в игре вид штормов (с шейдерами/Distant Horizons), нужны ли вулканы (Extreme Natural Disasters) и цунами-вёдра (This Tsunami); сервер с Natural Disasters! просадил TPS до ~5 при трёх бедствиях сразу над городом — лимит `maxConcurrent` (по умолчанию 1).
- [x] WorldEdit 7.2.15 (В18, ветка `claude/worldedit`): в сборке, сервер ОК, инструкция переноса построек в `docs/friends.md`.
