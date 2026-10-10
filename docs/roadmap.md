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
1. ~~Lycanites Mobs: недостающий `lycanitesmobs.refmap.json`~~ — **сделано** (`patches/lycanites-refmap`,
   коммит `b8f38e7`); осталось проверить клиентский `LightTextureMixin` в потоке 5.
2. Legendary Survival Overhaul × Serene Seasons: проверить связку температуры с сезонами, при надобности патч.
3. Create: Northstar — Structures: следить за совместимой версией или сделать свой патч на `moon_*_blob`.
4. Шум в логах (рецепты для отсутствующих модов, `laser_beam has no attributes`, атрибут `thought_load_limit`).
5. Задачи совместимости, пришедшие из потока 3.
6. (из потока 2) По желанию: Zombie Have Ears 1.1.2 (зомби слышат голосовой чат) — в `mods.toml` нет поля
   `mandatory`, Forge 47 его не грузит. Можно перепаковать jar с исправленным `mods.toml` в `patches/`.
7. (из ревью PR #2) **Arknights Weapon 1.5.6 удваивает здоровье всех мобов.** `net.mcreator.arknightsweaopn.procedures.RiseProcedure`
   на каждом `EntityJoinLevelEvent` ставит любому не-игроку `max_health` базой = текущий максимум × 2. Событие
   приходит и при загрузке чанка, поэтому сохранённые мобы удваиваются при каждой подгрузке. Проверено бисекцией
   пачки 4 потока 3 (без мода — зомби 20/разбойник 24, с ним — 40/48). Патч: перепаковать jar без подписки
   этого обработчика (или миксин, отменяющий `execute`) в `patches/`, проверить тестом: summon zombie → `max_health` 20,
   после `/forceload` выгрузки-загрузки чанка — всё ещё 20. До патча мод в сборку не берём.

### Поток 5 — Проверка клиента
Серверный тест клиент не покрывает. Запуск клиента (Xvfb в облаке или у игрока): Oculus + Embeddium,
Distant Horizons, Yes Steve Model, AzureLib-рендер, клиентские моды Arknights furs. Найденное — в
`docs/compat.md`; правки модов — через поток 3/4. Начинать после потоков 2–3, когда набор модов устоится.
- (из потока 1) Wizardry Restored 0.6.0: сервер проверен, клиент — нет. Посмотреть рендер новых сущностей
  (тотемы, чёрная дыра, силок, шипы, молот — модели из ванильных блоков; двойник/мираж — скин владельца; феникс —
  своя текстура; статуи — модель существа текстурой камня/льда; лоза — линия до крюка), частицы силового поля/тучи/диска, иконки заклинаний и эффекта «Метка жертвы», звуки Redux.
- (из потока 2) Импорт `dist/*.mrpack` в Prism Launcher и Modrinth App (серверные моды не должны ставиться),
  голосовой чат клиент↔сервер (микрофон, группы, рации Simple Radio), Sound Physics вместе с Oculus.

## Интеграция (ревью PR, поток «Ревью и интеграция», ветка `claude/confident-hypatia-2zqr1t`)
Цикл 1 — 2026-10-10:
- **PR #1, поток 2** (голосовой чат, экспорт, UDP): проверен, **готов к слиянию**. `add-mods.sh` пусто,
  `packwiz refresh` без изменений, стороны совпадают с Modrinth. Тест в новом мире вместе с Wizardry 0.6.0:
  `Done`, без «Mods loaded with N warnings», голосовой сервер на 24454, castcheck 69/69, гравитация/маршруты/
  руды/кислород как раньше. Поправка интегратора: `export-pack.sh` подкладывает packwiz-у jar CurseForge с
  запретом стороннего скачивания (иначе после PR #2 `.mrpack` не собирается) и падает, если `.mrpack` не создан.
- **PR #2, поток 3** (дополнения, волна 2; черновик): **заблокирован** — Arknights Weapon удваивает здоровье
  мобов (задача 7 потока 4), отсюда 5 провалов castcheck (урон есть, но порог «< 20 HP»). Тесты сервера — по
  пачкам 1–4; пачки 5–6 (Quark, Supplementaries, AE2, Refined Storage…) — только стороны, полный тест — когда
  выйдет из черновика. При слиянии с PR #1 в `docs/compat.md` убрать дубль пункта про `.gitignore`.
- Заметка для тестов: тестовый мир хранит состояние модов между запусками — для `castcheck.js` и проверок
  здоровья мобов удалять `<папка>/world` перед запуском.

### Отложено (решение за пользователем)
- Порт Galacticraft Legacy на 1.20.1 — фактически переписывание (см. `docs/compat.md`). Нужен отдельный
  чат (лицензия не препятствует — CLAUDE.md «Назначение и лицензии»); пока космос закрыт связкой Ad Astra + Northstar + Creating Space.
