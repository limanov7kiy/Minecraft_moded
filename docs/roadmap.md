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
- Поток 1 (Wizardry Restored): **0.4.0** — 38 из 61 заклинания + 4 «тестовых» заклинания Redux возвращены
  на выделенный сервер; `castcheck.js` 42/42. Этап 2 (механики Redux/ванилла) исчерпан; дальше — этап 3
  (новые сущности и блоки), точка остановки и список — в `docs/wizardry-port.md`.

## Потоки (по чату на каждый)

Правила для всех потоков:
- Ветка от свежей `claude/affectionate-davinci-4gddoh`, свои файлы — свои; общие файлы
  (`pack/modlist.txt`, `pack/index.toml`, `docs/compat.md`) правим точечно. Конфликт в `index.toml`
  не правим руками — пересоздаём `packwiz refresh`.
- После изменения набора модов — `scripts/test-server.sh`, находки — в `docs/compat.md`.
- В конце — отметка о сделанном в этом файле (раздел потока).

### Поток 1 — Wizardry Restored (заклинания)
Файлы: `mods-src/wizardry-restored/`, `docs/wizardry-port.md`, `scripts/tests/spellcheck.js`, `castcheck.js`.
1. Оставшиеся 23 заклинания — этап 3, новые сущности и блоки (phoenix, storm elemental, black hole,
   ice wraith, forest of thorns…); список и точка остановки — `docs/wizardry-port.md`. Этап 2 сделан (0.4.0).
2. Этап 4: предметы-артефакты (`ring_spirit_animal`, `transportation_stone`, `snare`, `thorns`…).
3. ~~Вернуть на выделенный сервер `SENSIBLE`-заклинания Redux~~ — **сделано** в 0.4.0 (копии под
   `wizardry_restored:*`, проверены `castcheck.js`).
4. ~~Проверить дополнения Redux с CurseForge~~ — **сделано**: Spell Forge 1.1.0 в сборке (сервер ОК,
   162 заклинания); Wizardry Golems и Necromancer's Delight — только 1.12.2.

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
2. Legendary Survival Overhaul × Serene Seasons: проверить связку температуры с сезонами, при надобности патч.
3. Create: Northstar — Structures: следить за совместимой версией или сделать свой патч на `moon_*_blob`.
4. Шум в логах (рецепты для отсутствующих модов, `laser_beam has no attributes`, атрибут `thought_load_limit`).
5. Задачи совместимости, пришедшие из потока 3.
   - (поток 3) CBC: Nuclear (Create Big Cannons × Alex's Caves) собран под старую CBC: `NoSuchMethodError
     CBCBuilderTransformersImpl.safeNbt()` — отключён. Вернуть, если выйдет совместимая версия.
   - (поток 3) T.O Magic 'n Extras 6.3.0 (последняя) обращается к `cataclysm.items.Dungeon_Eye.DungeonEyeItem`, которого
     нет в Cataclysm 3.31 — мод не грузится, отключён. Следить за обновлением.
   - (поток 3) Alshanex's Familiars 1.1.2 (единственная версия под 1.20.1) обращается к
     `ironsspellbooks.item.curios.SimpleAttributeCurio`, которого нет в Iron's Spells 3.16 — отключён.
   - (поток 3) **Superb Warfare × Valkyrien Skies: попадания по кораблям.** Связка Superb VS 1.0.0-beta собрана под VS 2.3;
     с VS 2.4.11 её миксин `ProjectileEntityMixin` не применяется (`ClassMetadataNotFoundException
     org.valkyrienskies.core.apigame.world.ShipWorldCore`) и роняет загрузку Superb Warfare. Отключена. Без неё пули
     Superb Warfare не попадают по блокам кораблей VS. Нужен свой патч (миксин на трассировку снаряда с учётом кораблей
     через API VS 2.4) или обновление Superb VS.
   - (поток 3) Create: Eureka! (CurseForge) — датапак `.zip`, а не мод. В сборке нет загрузки глобальных датапаков
     (`fetch-mods.py` берёт только `mods/`). Решить: подключить загрузчик датапаков или развернуть его в `pack/kubejs/data/`.

### Поток 5 — Проверка клиента
Серверный тест клиент не покрывает. Запуск клиента (Xvfb в облаке или у игрока): Oculus + Embeddium,
Distant Horizons, Yes Steve Model, AzureLib-рендер, клиентские моды Arknights furs. Найденное — в
`docs/compat.md`; правки модов — через поток 3/4. Начинать после потоков 2–3, когда набор модов устоится.

### Отложено (решение за пользователем)
- Порт Galacticraft Legacy на 1.20.1 — фактически переписывание (см. `docs/compat.md`). Нужен отдельный
  чат и проверка лицензии; пока космос закрыт связкой Ad Astra + Northstar + Creating Space.
