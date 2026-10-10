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

### Поток 3 — Дополнения, волна 2 (связки для каждого мода)
Файлы: `pack/modlist.txt`, `pack/mods/*.pw.toml`, `pack/config/almostunified/`, `docs/compat.md`.
1. Пройти каждый мод из `modlist.txt` и найти недостающие дополнения и связки (особенно: Lycanites,
   Mowzie's, Alex's Mobs, Blue Skies, Undergarden, Deeper Darker, Bumblezone, Twilight Forest, Arknights,
   Superb Warfare/TACZ, Valkyrien Skies, Mekanism, Botania, Occultism, Blood Magic, Mahou Tsukai).
2. Для каждой новой пачки: тест сервера, Almost Unified (`scripts/hardrefs.py` перед изменением),
   запись в `docs/compat.md`.
3. Где совместимость плохая — не чинить самому, а записать задачу в поток 4.
4. **(из потока 5, клиент не запускается)** Версии клиентских модов, подробности — `docs/compat.md`,
   «Проверка клиента»:
   - ImmediatelyFast 1.2.7+1.20.2 → **1.5.5+1.20.4-forge** (старая закрывает игру при Oculus 1.8.0;
     новая проверена). Поправить `pack/mods/immediatelyfast.pw.toml` (`packwiz update immediatelyfast`).
   - Cataclysm Spellbooks 1.2.9 не работает на клиенте с L_Ender's Cataclysm 3.31 (нет
     `Ancient_Remnant_Rework_Renderer`, убран в 3.24). Поискать новую версию Spellbooks (CurseForge/GitHub
     автора); если нет — решить вместе с потоком 4 (патч или откат Cataclysm 3.16 + LionfishAPI под неё +
     проверка Cataclysm Delight и других дополнений).
   - После правок — прогнать `scripts/test-client.sh` (или отдать потоку 5).

### Поток 4 — Совместимость и патчи
Файлы: `patches/`, `mods-src/mm-compat/`, `pack/kubejs/`, `docs/compat.md`.
1. ~~Lycanites Mobs: недостающий `lycanitesmobs.refmap.json`~~ — **сделано** (`patches/lycanites-refmap`,
   коммит `b8f38e7`); осталось проверить клиентский `LightTextureMixin` в потоке 5.
2. Legendary Survival Overhaul × Serene Seasons: проверить связку температуры с сезонами, при надобности патч.
3. Create: Northstar — Structures: следить за совместимой версией или сделать свой патч на `moon_*_blob`.
4. Шум в логах (рецепты для отсутствующих модов, `laser_beam has no attributes`, атрибут `thought_load_limit`).
5. Задачи совместимости, пришедшие из потока 3.
6. **(из потока 5, срочно)** `.gitignore`: правило `world*/` отрезает `pack/kubejs/data/mm_space/worldgen/` —
   размещения от `patches/ore_crossgen.py` не в git, сервер из чистой ветки не стартует (`Unbound values ...
   placed_feature`). Заменить на `/world*/` (только корень) или `world/`, перегенерировать и закоммитить.
7. **(из потока 5)** Origin Furs (`origin_visuals` 1.0.5, клиент) собран под старый API AzureLib (`Geo*`, есть до
   3.0.27), а Cataclysm Spellbooks — под новый (`Az*`, 3.1+); общей версии AzureLib нет, клиент падает. Варианты
   (лицензия не помеха — CLAUDE.md «Назначение и лицензии», авторство указать): а) пересобрать Origin Furs со
   встроенной копией AzureLib 3.0.27 в своём пакете (jar-in-jar с переименованием пакетов), б) портировать
   рендер Origin Furs на API AzureLib 3.1, в) убрать Origin Furs + Arknights furs. Без этого Arknights furs не работает.
8. **(из потока 5)** `patches/lycanites-refmap`: клиентский `LightTextureMixin` всё ещё не применяется —
   в refmap нет `@Shadow`-поля `lightTexture` (`LightTexture`, SRG `f_109870_` — сверить по маппингам 1.20.1).
9. **(из потока 5)** `pack/config/sereneseasonsplus-common.toml` в старом формате: SS+ переименовал ключи,
   откладывает файл в `.bak` и создаёт новый — накопление снега из сборки не действует. Переписать конфиг
   в новом формате (взять сгенерированный модом и перенести настройки).

### Поток 5 — Проверка клиента
Серверный тест клиент не покрывает. Запуск клиента (Xvfb в облаке или у игрока): Oculus + Embeddium,
Distant Horizons, Yes Steve Model, AzureLib-рендер, клиентские моды Arknights furs. Найденное — в
`docs/compat.md`; правки модов — через поток 3/4. Начинать после потоков 2–3, когда набор модов устоится.
- (из потока 1) Wizardry Restored 0.6.0: сервер проверен, клиент — нет. Посмотреть рендер новых сущностей
  (тотемы, чёрная дыра, силок, шипы, молот — модели из ванильных блоков; двойник/мираж — скин владельца; феникс —
  своя текстура; статуи — модель существа текстурой камня/льда; лоза — линия до крюка), частицы силового поля/тучи/диска, иконки заклинаний и эффекта «Метка жертвы», звуки Redux.

**Сделано 2026-10-10** (подробно — `docs/compat.md`, «Проверка клиента»): клиент запущен в облаке под Xvfb
(`scripts/test-client.sh`), зашёл на тестовый сервер. С текущей сборкой клиент **не запускается** — блокеры
переданы: ImmediatelyFast и Cataclysm Spellbooks × Cataclysm — поток 3 (п. 4); `.gitignore`/worldgen,
Origin Furs × AzureLib, refmap Lycanites, конфиг SS+ — поток 4 (пп. 6–9). С локальными обходами проверены:
Oculus + Embeddium + шейдеры, вход и выбор Origin, Xaero (миникарта и карта мира), рендер AzureLib,
загрузка Distant Horizons. YSM в облаке не работает (запрет на ВМ).

Дальше: после исправлений потоков 3–4 — повторить `scripts/test-client.sh` (Arknights furs, Wizardry 0.6.0,
Simple Voice Chat после потока 2). Остальное — только на настоящем ПК, **чек-лист для игрока**:
1. Запуск без крашей, вход на сервер; в `logs/latest.log` нет `Mixin apply failed` и `Game crashed`.
2. Шейдеры: Options → Video → Shader Packs, Complementary/BSL; FPS ≥ 40, нет чёрного неба/мерцания воды.
3. Distant Horizons: в настройках DH дальность LOD 128–256 чанков; дальний ландшафт виден, нет «дыр» на
   стыке с обычными чанками, с шейдерами LOD не мерцают. В пещерах Alex's Caves — нет ли артефактов.
4. Yes Steve Model: открыть меню YSM (клавиша — в Управлении, раздел YSM), сменить модель — видна себе (F5) и другу.
5. AzureLib: броня и посохи Cataclysm Spellbooks на игроке (F5), заклинания-лучи; Origin Furs + Arknights furs —
   шерсть/уши у выбранной расы Arknights (после исправления п. 7 потока 4).
6. Simple Voice Chat (после потока 2): клавиша `V`, микрофон слышен другу, работает через хостинг (UDP 24454).
7. Xaero: миникарта, карта мира `M`, метки игроков друг друга.
8. Lycanites: эффект страха затемняет экран (это `LightTextureMixin`).
9. Память: выставить в лаунчере `-Xmx6G`; следить за фризами (DH советует ZGC/Shenandoah вместо G1).

### Отложено (решение за пользователем)
- Порт Galacticraft Legacy на 1.20.1 — фактически переписывание (см. `docs/compat.md`). Нужен отдельный
  чат (лицензия не препятствует — CLAUDE.md «Назначение и лицензии»); пока космос закрыт связкой Ad Astra + Northstar + Creating Space.
