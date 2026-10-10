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
| Wizardry Restored 0.2.0: 15 из 61 недостающего заклинания | `mods-src/wizardry-restored`, `docs/wizardry-port.md` |
| Серверные тесты: гравитация, маршруты, ракеты, кислород, руды, заклинания | `scripts/tests/` |

## В работе (основной чат «Сборка для сетевой игры Minecraft», ещё не запушено)
- Wizardry Restored **0.3.0**: ещё 16 заклинаний (переводы, звуки, иконки), серверный тест применения
  `castcheck.js` (чинится вызов `moveTo`/`setYRot` у FakePlayer).
- Последняя просьба пользователя там — **мод голосового чата**. Переносим в поток 2.

Основному чату: доделать 0.3.0, прогнать тест, обновить `docs/wizardry-port.md`,
закоммитить и **запушить**, после чего он закрывается. Новые чаты начинают с этого коммита.

## Потоки (по чату на каждый)

Правила для всех потоков:
- Ветка от свежей `claude/affectionate-davinci-4gddoh`, свои файлы — свои; общие файлы
  (`pack/modlist.txt`, `pack/index.toml`, `docs/compat.md`) правим точечно. Конфликт в `index.toml`
  не правим руками — пересоздаём `packwiz refresh`.
- После изменения набора модов — `scripts/test-server.sh`, находки — в `docs/compat.md`.
- В конце — отметка о сделанном в этом файле (раздел потока).

### Поток 1 — Wizardry Restored (заклинания)
Файлы: `mods-src/wizardry-restored/`, `docs/wizardry-port.md`, `scripts/tests/spellcheck.js`, `castcheck.js`.
1. Оставшиеся заклинания (≈30): этап 2 — на механиках Redux; этап 3 — новые сущности и блоки
   (phoenix, storm elemental, black hole, ice statue, ice wraith, forest of thorns…).
2. Этап 4: предметы-артефакты (`ring_spirit_animal`, `transportation_stone`, `snare`, `thorns`…).
3. Вернуть на выделенный сервер `SENSIBLE`-заклинания Redux (lightning_pulse, summon_ice_giant,
   conjure_armor, silverfish_swarm), если они не ломаются.
4. Проверить дополнения Redux с CurseForge: Spell Forge, Wizardry Golems, Necromancer's Delight.

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
- [ ] Пункт 3 (хостинг с пользователем, боевая проверка) — открыт.

### Поток 3 — Дополнения, волна 2 (связки для каждого мода)
Файлы: `pack/modlist.txt`, `pack/mods/*.pw.toml`, `pack/config/almostunified/`, `docs/compat.md`.
1. Пройти каждый мод из `modlist.txt` и найти недостающие дополнения и связки (особенно: Lycanites,
   Mowzie's, Alex's Mobs, Blue Skies, Undergarden, Deeper Darker, Bumblezone, Twilight Forest, Arknights,
   Superb Warfare/TACZ, Valkyrien Skies, Mekanism, Botania, Occultism, Blood Magic, Mahou Tsukai).
2. Для каждой новой пачки: тест сервера, Almost Unified (`scripts/hardrefs.py` перед изменением),
   запись в `docs/compat.md`.
3. Где совместимость плохая — не чинить самому, а записать задачу в поток 4.

### Поток 4 — Совместимость и патчи
Файлы: `patches/`, `mods-src/mm-compat/`, `pack/kubejs/`, `docs/compat.md`.
1. Lycanites Mobs: собрать недостающий `lycanitesmobs.refmap.json` (миксины `/reload` и освещение).
2. Legendary Survival Overhaul × Serene Seasons: проверить связку температуры с сезонами, при надобности патч.
3. Create: Northstar — Structures: следить за совместимой версией или сделать свой патч на `moon_*_blob`.
4. Шум в логах (рецепты для отсутствующих модов, `laser_beam has no attributes`, атрибут `thought_load_limit`).
5. Задачи совместимости, пришедшие из потока 3.
6. (из потока 2) По желанию: Zombie Have Ears 1.1.2 (зомби слышат голосовой чат) — в `mods.toml` нет поля
   `mandatory`, Forge 47 его не грузит. Можно перепаковать jar с исправленным `mods.toml` в `patches/`.

### Поток 5 — Проверка клиента
Серверный тест клиент не покрывает. Запуск клиента (Xvfb в облаке или у игрока): Oculus + Embeddium,
Distant Horizons, Yes Steve Model, AzureLib-рендер, клиентские моды Arknights furs. Найденное — в
`docs/compat.md`; правки модов — через поток 3/4.
Из потока 2: импорт `dist/*.mrpack` в Prism Launcher и Modrinth App (серверные моды не должны ставиться),
голосовой чат клиент↔сервер (микрофон, группы, рации Simple Radio), Sound Physics вместе с Oculus. Начинать после потоков 2–3, когда набор модов устоится.

### Отложено (решение за пользователем)
- Порт Galacticraft Legacy на 1.20.1 — фактически переписывание (см. `docs/compat.md`). Нужен отдельный
  чат и проверка лицензии; пока космос закрыт связкой Ad Astra + Northstar + Creating Space.
