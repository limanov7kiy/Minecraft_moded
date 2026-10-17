# Производительность (поток 8)

Жалоба владельца (Windows, Prism, одиночный мир): создание мира ≈2,5 мин, вход ещё ≈3 мин, в мире «очень сильно подвисает».
Железо владельца пока неизвестно (уточняется) — всё ниже сделано «для среднего ПК».

## Что нашли замером
Замер клиента (`scripts/test-client.sh`-стенд: контейнер 4 ядра, Xvfb + программный OpenGL llvmpipe — он сам отъедает 2 ядра, куча 6 ГБ,
свежий мир «New World», настройки сборки до и после). **Абсолютные цифры хуже, чем на обычном ПК с видеокартой; важны пропорции.**

| Этап | До (умолчания) | После | Причина / что сделано |
|---|---|---|---|
| Загрузка до главного меню | 273–301 с | 216–228 с | ModernFix `dynamic_resources` |
| «Initial datapack load» (экран создания мира) | 31 с | 23–35 с | — (шум замера) |
| Создание мира → «joined the game» | **263 с** (из них 167 с — «Preparing spawn area») | **75–88 с** | ModernFix `remove_spawn_chunks`; Distant Horizons не генерирует местность; дальность 10/6 |
| После входа: JEI «Starting JEI» (на потоке отрисовки, игра «висит») | **3,5 мин** | **1,6 мин** | см. ниже |
| — рецепты `jei:minecraft` (точило/наковальня) | 1,76 мин | 22 с | мод `mm-perf` |
| — «Building ingredient filter» (поиск по подсказкам) | 52–78 с | 1–2 с | JEI `tooltipSearchMode = DISABLED` |
| — плагин Iron's Spells (Arcane Anvil) | 22–38 с | 27–32 с | не тронуто (см. «Не сделано») |

**«Вход ещё 3 минуты» у владельца — это запуск JEI**: при каждом входе в мир JEI на главном потоке строит рецепты. Для каждого предмета с чарами
он прогоняет настоящие меню точила и наковальни, а каждая копия предмета в нашей сборке (Iron's Spells, TravelOptics, AzureLib) запускает
тяжёлые хуки. Каждый новый JEI-плагин от дополнений (поток 7) прибавляет к этому времени — следите за строками
`Registering recipes: <плагин> took` в `latest.log`.

## Что добавлено / настроено
- **Моды** (`pack/modlist.txt`, раздел «Оптимизация»): Let Me Despawn (`lmd`, сервер) + библиотека Almanac, FastFurnace и FastWorkbench (сервер).
  Уже стояли: ModernFix, FerriteCore, Canary, Saturn, Embeddium, Oculus, Entity Culling, ImmediatelyFast, Clumps, AI Improvements, Async Locator,
  Smooth Boot, MemoryLeakFix, Distant Horizons, spark.
- **`mods-src/mm-perf`** (клиентский jar `pack/mods/mm-perf-0.1.0-client.jar`, на сервер не ставится): миксины в JEI — не строить строки
  «снять чары на точиле» и «предмет + зачарованная книга» на наковальне (ремонт остаётся). Сборка: `cd mods-src/mm-perf && ./gradlew build`.
  Если JEI обновится и имена методов изменятся, миксины просто не применятся (`require = 0`) — игра не упадёт, вернётся старая медленная загрузка.
- **`pack/config/modernfix-mixins.properties`**: `remove_spawn_chunks`, `dynamic_resources` (+ как включить авто-профиль входа spark — в `friends.md`).
  Оговорка `remove_spawn_chunks`: механизмы у точки спавна не работают, пока рядом никого нет (общий конфиг — касается и сервера).
  `dynamic_resources`: запуск клиента до меню и мира проверен, в логе ошибок моделей нет; **визуально** (отсутствующие модели, «фиолетово-чёрные» блоки,
  Create/Valkyrien Skies/Wizardry) в контейнере не проверялось — на глаз проверит владелец; отключить: удалить строку.
- **`pack/config/DistantHorizons.toml`**: радиус LOD 256 → 64; SSAO и сглаживание выкл.; генерация далёкой местности самим DH выключена
  (`generatorPlan = DISABLED`, `chunkGeneratorMode = PRE_EXISTING_ONLY`: у DH по умолчанию `FEATURES` — «может ломаться с модами генерации мира»,
  в логе он ругался на неизвестный генератор AE2 и запускал очереди в 9 измерениях); потоки 2, загрузка потока 50 %; автообновление DH выкл.
  (версия DH должна совпадать у сервера и клиентов). LOD строятся из чанков, которые вы реально загружали. Вернуть дальние виды «из воздуха»:
  `generatorPlan = "SURFACE_ONLY"`.
- **`pack/config/jei/client/default/jei-client.ini`**: поиск по подсказкам выкл. (поиск по названию, `@мод`, `#тег` работают).
- **`pack/config/sound_physics_remastered/soundphysics.properties`**: 16 лучей, 2 отражения, радиус обработки 128.
- **`pack/options.txt`** (пресет графики, применяется только к новым установкам — файл `preserve`): дальность 10, симуляция 6, частицы «уменьшенные»,
  облака «быстрые», сглаженное освещение «минимум», сглаживание биомов 1, тени сущностей выкл., дальность сущностей 75 %, mipmap 2.
  **Как применить у владельца**, у которого `options.txt` уже есть: либо удалить `options.txt` в папке экземпляра перед обновлением, либо вручную
  в игре поставить те же значения (*Настройки → Параметры видео*), либо дописать строки из `pack/options.txt` в его файл (при закрытой игре).
- **Память/JVM** — только инструкцией (`docs/friends.md`, «Если тормозит»): формат `.mrpack`/packwiz не передаёт `Xmx` и аргументы JVM, это настройки
  лаунчера. Хост может выдать экземпляр Prism, экспортированный из своего (там уже будут `Xmx` и флаги).
- **Скрипт**: `scripts/test-client.sh` — `CLIENT_GAME_ARGS`, `CLIENT_JVM_EXTRA`.

## Аудит других оптимизаторов (1.20.1 Forge)
По данным Modrinth (версии/описания), в сборке не запускалось, кроме отмеченного «добавлены».
| Мод | Решение |
|---|---|
| Entity Culling, ImmediatelyFast, Clumps, AI Improvements, Async Locator, Saturn, Smooth Boot, MemoryLeakFix | уже стоят |
| Let Me Despawn, FastFurnace, FastWorkbench | **добавлены** (серверные, безопасные) |
| Radium | не нужен: Canary уже порт Lithium (два Lithium-порта конфликтуют) |
| Rubidium Extra (Embeddium Extra) | в отчёт: ставится на Rubidium, поверх Embeddium/Oculus — риск; даёт только расширенные настройки видео |
| Async Particles (`asyncparticles`) | в отчёт: клиент, асинхронная обработка частиц; модовые частицы (Lycanites, Ars, Create) могут давать мерцание/вылеты — требует проверки глазами |
| BadOptimizations | в отчёт: мелкие миксины, известны конфликты с другими миксин-модами (Valkyrien Skies, Embeddium) |
| FastSuite | в отчёт: пересекается с Clumps (предметы-сущности) |
| Starlight/ScalableLux, Krypton | нет версий под 1.20.1 Forge (Starlight — Fabric/1.19; Krypton — Fabric); Connectivity (чинит «таймаут при входе» с большим числом модов) на Modrinth для Forge 1.20.1 не нашёлся — искать на CurseForge |
| More Culling, Noisium, Dynamic FPS | More Culling — нет версии Forge 1.20.1; Noisium — серверный (оптимизация генерации шума), в отчёт; Dynamic FPS — снижает FPS в свёрнутом окне, безопасен, по желанию |

## Не сделано / нужно решение владельца
1. **JEI всё ещё ~1,6 мин на вход в мир** (в контейнере): остаток — плагин Iron's Spells (Arcane Anvil, ~30 с), остальные плагины, голая нагрузка
   JEI. Варианты: (а) миксин `mm-perf` на плагин Iron's Spells (потеряем рецепты Arcane Anvil в JEI); (б) заменить JEI на EMI (под 1.20.1 Forge есть,
   но это смена мода и всех JEI-дополнений — решение потока 7/владельца); (в) оставить.
2. **Распределение ОЗУ**: рекомендация 8 ГБ на ПК с 16 ГБ (см. `friends.md`). Если у владельца 8 ГБ — сборка будет тяжёлой при любых настройках: тогда отключить Distant Horizons и шейдеры, дальность 6–8.
3. **Железо**: ориентир для комфорта — 6+ ядер (тик встроенного сервера и отрисовка конкурируют), 16 ГБ ОЗУ, дискретная видеокарта уровня GTX 1660 / RX 580 и лучше,
   SSD. На 4 ядрах без видеокарты (наш стенд) мир создаётся и ведёт себя, но с «Can't keep up» при первой подгрузке.
4. **Lycanites / Valkyrien Skies / Create**: настройки по умолчанию оставлены (у Lycanites `typeSpawnLimit` 64 на вид; у VS `enableClientConnectivity` уже `false`).
   Если после правок остаются лаги в самой игре — `/spark profiler` (см. `friends.md`) и пришлите ссылку: тогда тюнить по факту, а не вслепую.
5. Сервер: в `export-pack.sh` флаги JVM сервера — `-Xms4G -Xmx10G` без GC-флагов; можно добавить набор Aikar (как в `friends.md`) — по желанию хоста.

## Embeddium и Oculus: «Sodium на Forge» (поток 28, 2026-10-17, решение владельца В25)
**Что стоит.** `Embeddium 0.3.31+mc1.20.1` (порт Sodium 0.5.x на Forge от embeddedt) и `Oculus 1.8.0` (порт Iris на Forge). Оба — **последние версии под 1.20.1 Forge**
(Modrinth: Embeddium 0.3.31 от 2024-08-31, Oculus 1.8.0 от 2024-12-24; новее нет, обновлять нечего). Оба `client`, на сервер не ставятся; Oculus зависит от Embeddium.

**Почему не настоящий Sodium.** Sodium и Iris — моды для Fabric/NeoForge; на Forge 1.20.1 (где живёт почти вся сборка) их нет. Embeddium — форк Sodium,
переписанный под Forge и расширенный API для модов; Oculus — то же для Iris (поэтому «шейдеры» тут через Oculus, багрепорты официальному Iris слать нельзя).
Скорость рендера земли та же, что у Sodium 0.5 (чанки в видеопамяти, отсечение невидимого); отличие — версии отстают от свежего Sodium и поддерживаются одним человеком.

**Что проверено** (`scripts/test-client.sh`, `CLIENT OK`; программный OpenGL, поэтому FPS/картинку не оцениваем):
- Загружаются без ошибок, `Embeddium: 335 options available`, ImmediatelyFast 1.5.5 «Found Iris/Oculus 1.8.0. Enabling compatibility» (версия 1.2.7 здесь закрывала игру — см. compat.md К1, не откатывать).
- Distant Horizons 3.3.3: «Partial Oculus support enabled… use Iris instead if possible», «DH Iris events bound», рендер «bound to OpenGL». «Partial» — норма для Oculus: ограничения DH с шейдерами
  (LOD под шейдером могут быть без части эффектов). DH предупреждает, что Alex's Caves «может требовать правок конфига» для дальнего рендера — к Embeddium не относится.
- Create 6.0.8 + Flywheel 1.0.5: бэкенд `DEFAULT`, `lightSmoothness SMOOTH`; ошибок рендера не видно. Valkyrien Skies 2.4.11 и Oculus вешают миксины на классы Embeddium
  (`SodiumWorldRenderer`, `SodiumBufferBuilder`…) — это ожидаемо для связки (VS рассчитан на Sodium-подобные рендеры), но отдельно не проверялось. **Не проверено глазами**: Create-механизмы и корабли VS с включённым шейдером,
  небо планет Ad Astra, Aether — нужна видеокарта (`docs/client-checklist.md`, раздел 3).
- Две строки в логе, которые выглядят страшно, но безвредны: `ERROR Mod mixin into Embeddium internals detected. This instance is now tainted` и серия `WARN Mod(s) [oculus] are modifying Embeddium class…` —
  Oculus по определению правит классы Embeddium (его меню шейдеров), Embeddium так предупреждает о «неподдерживаемой» сборке. `disable_incompatible_mod_warnings` в
  `config/embeddium-options.json` убирает окно предупреждений в игре, но не строки лога. Строка `Force-disabling mixin … features.render.* (added by mods [oculus])` — Oculus сам отключает конфликтующие
  оптимизации Embeddium (небо, шрифт, тени сущностей), чтобы шейдеры работали; это тоже штатно (часть выигрыша FPS с шейдерами недоступна).
- `OpenGL debug message … GLSL 4.60 is not supported` — особенность программного Mesa стенда, на реальных видеокартах не встречается.
- Встретилось попутно (не Embeddium): ModernFix `dynamic_resources` — `Exception baking cbc_at:block/heavy_autocannon/*_recoil_spring` (3 модели Create Big Cannons: Advanced Technologies не найдены, пушка без пружины; в сборку не лезет, проверить глазами).

**Известные ограничения этой пары** (по Modrinth/описаниям, на нашей сборке без GPU не воспроизводились):
- Совместимость с шейдерами — «best effort»: часть модовых блоков/существ с кастомным рендером (Create, Valkyrien Skies, Lycanites, Wizardry, Ars) может выглядеть без теней/подсветки или мерцать. Лечение — другой шейдер или выключить шейдеры; в логе ищите `Oculus`/`shader`.
- Моды, вешающие миксины на рендер мира (`Rubidium Extra`, `BadOptimizations`, `More Culling`), с Embeddium/Oculus конфликтуют — их не ставим (таблица выше). Любой новый «оптимизатор» рендера — только после теста клиента.
- Шейдеры тяжёлые: даже на хорошей видеокарте с DH FPS падает вдвое; на слабой — шейдеры выключить (*Настройки → Видео → Пакеты шейдеров → Выкл.*), Embeddium остаётся полезным и без них.

**Рекомендации игрокам.**
1. Драйвер видеокарты свежий (NVIDIA/AMD с сайта производителя; Intel — Intel Arc Control / Windows Update). Ноутбуки с двумя видеокартами: Java (`javaw.exe` Prism) прописать на дискретную в *Параметры Windows → Дисплей → Графика*; в F3 должна быть ваша карта, а не Intel UHD/`llvmpipe`.
2. *Настройки → Видео (Embeddium)*: дальность 8–10 (не выше, дальше рисует Distant Horizons), облака «быстрые», частицы «уменьшенные», «Вертикальная синхронизация» — по вкусу; `chunk_builder_threads=0` (автоматически) оставить; включена «Сортировка прозрачных граней» (делает воду/стекло правильными).
3. Шейдеры: сначала без них; потом лёгкий (Complementary Reimagined/BSL «Low»), *Распределение теней* ≤ 32 (в `oculus.properties` уже `maxShadowRenderDistance=32`). На ПК без дискретной карты шейдеры не включать.
4. Distant Horizons: в сборке радиус 64, генерация самим DH выключена. Предупреждение «G1 Garbage collector detected» можно игнорировать: наш набор — G1 с флагами Aikar (`friends.md`), ZGC/Shenandoah на Java 17 для клиента **не советуем**
   (ZGC без поколений на 17 хуже G1 и не проверялся на сборке). Если рывки именно при подгрузке LOD: уменьшить в настройках DH «Потоки» и «Радиус LOD», дать клиенту 8 ГБ, держать мир на SSD.
5. Если видите вылет/фиолетовые блоки с шейдером — сначала выключить шейдер, затем (для проверки) вынуть `oculus-*.jar` из `mods/`; Embeddium без Oculus работает и даёт FPS.
6. Для диагностики: `scripts/collect-diagnostics.*` (`docs/diagnostics.md`) уже собирает `embeddium-options.json` и `oculus.properties`; присылать вместе с `latest.log`.

**Вывод.** Явной поломки не найдено: версии свежие и совместимые между собой, `CLIENT OK`, код и моды не менялись. Реальные FPS и шейдеры проверит только игра на видеокарте (проверочный список — `client-checklist.md`, раздел 3).
