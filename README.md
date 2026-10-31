# Minecraft_moded

Модовая сборка Minecraft для игры с друзьями по сети: **Minecraft 1.20.1 + Forge 47.4.26**.
Магия, ракеты и космос, оружие, мобы, Arknights, измерения, умеренный реализм, оптимизация.

- 📖 **[Справочник-энциклопедия сборки](docs/encyclopedia/INDEX.md)** — карточка каждого мода по категориям
- Список модов с пояснениями: [pack/modlist.txt](pack/modlist.txt)
- Журнал совместимости: [docs/compat.md](docs/compat.md)
- Почему 1.20.1: [docs/version-choice.md](docs/version-choice.md)
- Где поднимать сервер: [docs/hosting.md](docs/hosting.md)

## Установка для игроков
Основной способ — файл `.mrpack` (импорт в Prism Launcher / Modrinth App), пошагово — [docs/friends.md](docs/friends.md).
Хост собирает его `scripts/export-pack.sh` → `dist/*.mrpack` и передаёт друзьям.

Альтернатива с автообновлением при каждом запуске — packwiz-installer (работает, пока репозиторий публичный):
1. Prism: создать экземпляр Minecraft 1.20.1 + Forge 47.4.26, памяти 8 ГБ.
2. Скачать [packwiz-installer-bootstrap.jar](https://github.com/packwiz/packwiz-installer-bootstrap/releases)
   в папку `.minecraft` экземпляра.
3. Настройки экземпляра → «Пользовательские команды» → «Команда перед запуском»:
   ```
   "$INST_JAVA" -jar packwiz-installer-bootstrap.jar https://raw.githubusercontent.com/limanov7kiy/Minecraft_moded/refs/heads/claude/affectionate-davinci-4gddoh/pack/pack.toml
   ```
   Ветки `main` нет — сборка живёт в `claude/affectionate-davinci-4gddoh`, отсюда `refs/heads/...` в ссылке
   (со ссылкой на `main` будет 404 «Failed to download pack.toml»).
   Моды CurseForge с запретом стороннего скачивания лежат в репозитории готовыми jar (`pack/mods/*.jar`) и
   качаются установщиком по той же ссылке — вручную ничего скачивать не нужно.

## Сервер
`dist/*-server.zip` из того же `scripts/export-pack.sh` (моды both+server, конфиги, установщик Forge,
скрипты запуска), либо `scripts/fetch-mods.py server <папка>/mods`. Хостинг — [docs/hosting.md](docs/hosting.md).

## Claude Code
Проект подключает плагин `universal-modder` через `.claude/settings.json`. Вручную:
```
/plugin marketplace add rehan-remade/universal-modder
/plugin install universal-modder@universal-modder
```

## Справочник файлов
Где что лежит и что в каком файле объясняется. Пути — от корня репозитория. Новый файл с инструкцией или подсказками добавляется сюда строкой (правило — в [CLAUDE.md](CLAUDE.md)).

### Установка и игрокам
| Файл | Что здесь объясняется |
|---|---|
| [docs/friends.md](docs/friends.md) | Как зайти на сервер: установка сборки (`.mrpack`, Prism), подключение, голосовой чат, память, слабый ПК, обновление, клавиши модов, как отправить отчёт; для хоста — как собрать пакеты |
| [docs/client-checklist.md](docs/client-checklist.md) | Чек-лист игрока на настоящем ПК (~30 минут): запуск, вход, графика, модели, магия, Galacticraft, голос, JEI; что прислать при проблеме |
| [docs/whats-new-check.md](docs/whats-new-check.md) | Что недавно вошло в сборку и что нужно проверить глазами; пополняется после каждого слияния |
| [docs/diagnostics.md](docs/diagnostics.md) | Как собрать и прислать диагностику при фризах: запуск `collect-diagnostics` под Windows, что попадает в zip |
| [docs/enchanting-table.md](docs/enchanting-table.md) | Чаровальный стол ESO (В47 «в»): порт, настройка «средний» режим, команды `/eso`, таблица «чара → где взять книгу», откат; исследование кандидатов (Enchanting Infuser, Modest Magic, Apotheosis) |
| [docs/report-form.md](docs/report-form.md) | Как устроен приём отчётов игроков: форма, таблица, номера и статусы, процесс приёмщика, правила приватности для публичного репозитория; пожелания (раздел 10): статусы, граница «влияет на баланс», шаги обновления |
| [docs/report-status.md](docs/report-status.md) | Открытая страница статусов отчётов: номер, суть, категория, мод, статус, PR; без ников и текстов игроков; отдельный раздел «Пожелания» |
| [docs/issue-history.md](docs/issue-history.md) | История решений: прошлые проблемы и вопросы игроков (H-###) — причина, PR или версия мода, что делать игроку, статус; без ников |
| [docs/hosting.md](docs/hosting.md) | Где поднимать сервер: замер ресурсов сборки, сервер на ПК друга или платный хостинг; автообновление сервера с git (mm-autoupdate) |

### Моды и совместимость
| Файл | Что здесь объясняется |
|---|---|
| [pack/modlist.txt](pack/modlist.txt) | Источник истины по модам: источник (Modrinth/CurseForge), slug и заметка, зачем мод в сборке |
| [docs/compat.md](docs/compat.md) | Журнал совместимости: решения по запрошенным модам, находки тестовых запусков, Almost Unified, единая Солнечная система, катаклизмы, паразиты, голосовой чат и др. |
| [docs/version-choice.md](docs/version-choice.md) | Сравнение 1.20.1 Forge и 1.21.1 NeoForge: какие моды где доступны (данные Modrinth) |
| [docs/performance.md](docs/performance.md) | Производительность: замеры клиента и сервера, оптимизаторы, Embeddium/Oculus, клиентские удобства `mm-perf`, разбор spark-отчётов |
| [docs/jei.md](docs/jei.md) | JEI: горячие клавиши, какие аддоны стоят, как проверено, что рецепты видны для всех модов |
| [docs/galacticraft.md](docs/galacticraft.md) | Galacticraft в сборке: цель, этапы работ, как он вписывается в единую Солнечную систему |
| [docs/lost-cities.md](docs/lost-cities.md) | Заброшенные города (The Lost Cities): выбор мода, настройка генерации в обычном мире |
| [docs/orespawn.md](docs/orespawn.md) | OreSpawn: ремейк Chaos Persists против оригинала 1.7.10, чего не хватает для порта |
| [docs/wizardry-port.md](docs/wizardry-port.md) | Electroblob's Wizardry: опись недостающих заклинаний Redux против оригинала 1.12.2 и план переноса |
| [docs/ntm.md](docs/ntm.md) | HBM's Nuclear Tech Mod (NTM): решение владельца не добавлять alpha-порты, сверка функций NTM с тем, что уже есть в сборке, список портов на будущее |
| [docs/scp.md](docs/scp.md) | SCP-моды под 1.20.1 Forge: кандидаты (структуры в мире, сущности, предметы), совместимость, нагрузка, дубли, рекомендация и вопрос владельцу |
| [docs/expensive-crafts.md](docs/expensive-crafts.md) | «Невероятно дорогие многоступенчатые крафты»: что уже есть (эндгейм, боссы), моды-кандидаты, вариант своих KubeJS-цепочек, вопрос владельцу |
| [docs/dedup.md](docs/dedup.md) | Логические дубли между модами: метод, очевидные и спорные группы, раздел «Энергия» (виды энергии и варианты), откат |
| [docs/ysm-models.md](docs/ysm-models.md) | Модели игроков Yes Steve Model: где лежат, как перезагрузить, какие подключены, ограничения |
| [docs/encyclopedia/INDEX.md](docs/encyclopedia/INDEX.md) | Справочник-энциклопедия: оглавление по категориям и алфавитный указатель всех модов (генерируется) |
| [docs/encyclopedia/README-authoring.md](docs/encyclopedia/README-authoring.md) | Как писать в справочник: что генерируется (авто-блоки), что вручную, как добавить или перенести мод |
| [docs/encyclopedia/](docs/encyclopedia/) | Страницы категорий справочника с карточками модов (`<категория>.md`) и статьи (`arknights/`) |
| [scripts/encyclopedia/](scripts/encyclopedia/) | Генератор справочника: `build.py` (карточки), `fetch_meta.py` (описания Modrinth), `categories.toml` (категории), `meta-cache.json` |
| [docs/bosses.md](docs/bosses.md) | Таблица боссов сборки: где встретить, как призвать, здоровье/урон/броня, награда (генерируется) |
| [docs/bosses.csv](docs/bosses.csv) | Та же таблица боссов в CSV для сортировки и фильтров |
| [docs/client-check/](docs/client-check/) | Скриншоты клиента из проверок (главное меню, мир, F3, мобы) |
| [docs/img/](docs/img/) | Картинки к документам, в т.ч. превью моделей Priestess и Vivi |

### Русификация
| Файл | Что здесь объясняется |
|---|---|
| [docs/russification.md](docs/russification.md) | План русификации: аудит, готовые русификаторы, инструменты, решения по названиям модов и терминам |
| [docs/russification-audit.md](docs/russification-audit.md) | Таблица по каждому моду: покрытие родным ru и после ресурспаков, книги (генерируется `scripts/lang_audit.py`) |
| [docs/russification-glossary.md](docs/russification-glossary.md) | Глоссарий: единые русские термины для переводов |
| [pack/resourcepacks-src/russian-moded/](pack/resourcepacks-src/russian-moded/) | Исходники нашего русского ресурспака (переводы, книги); собирается `scripts/build-ru-pack.sh` |
| [pack/options.txt](pack/options.txt) | Настройки клиента по умолчанию: русский язык, порядок ресурспаков, дальность прорисовки |

### Планы и решения
| Файл | Что здесь объясняется |
|---|---|
| [CLAUDE.md](CLAUDE.md) | Правила проекта: версии и стороны модов, лицензии и публичность репозитория, команды сборки и тестов |
| [docs/roadmap.md](docs/roadmap.md) | Что сделано, что в работе, разделение задач по потокам, правила интеграции PR |

### Скрипты: сборка, раздача, тесты
| Файл | Что здесь объясняется |
|---|---|
| [scripts/add-mods.sh](scripts/add-mods.sh) | Добавляет в packwiz все моды из `pack/modlist.txt` |
| [scripts/fetch-mods.py](scripts/fetch-mods.py) | Скачивает jar модов из `pack/mods/*.pw.toml` для клиента или сервера (optional — в отдельную папку) |
| [scripts/fetch-config-files.py](scripts/fetch-config-files.py) | Разворачивает метафайлы конфигов (модели YSM) для сервера |
| [scripts/export-pack.sh](scripts/export-pack.sh) | Раздача: `dist/*.mrpack` для клиента и `dist/*-server.zip` для сервера |
| [scripts/check-pack-version.py](scripts/check-pack-version.py) | Проверка версии сборки: `pack.toml` и `config/mm_pack_version.txt` совпадают и вида `ГГГГ.ММ.ДД-N` |
| [scripts/mrpack-install.py](scripts/mrpack-install.py) | Ставит `.mrpack` в папку как лаунчер — для проверки без лаунчера |
| [scripts/mrpack-optional.py](scripts/mrpack-optional.py) | Помечает необязательные моды в `.mrpack`, чтобы клиент их не получал |
| [scripts/pack-index-refresh.py](scripts/pack-index-refresh.py) | Пересчёт хэшей `pack/index.toml` и `pack/pack.toml` после правки метафайлов моделей YSM |
| [scripts/patch-rp-format.py](scripts/patch-rp-format.py) | Копия чужого ресурспака с `pack_format` 15, чтобы 1.20.1 его принимал |
| [scripts/build-ru-pack.sh](scripts/build-ru-pack.sh) | Сборка русского ресурспака в мод `ru-lang` |
| [scripts/test-server.sh](scripts/test-server.sh) | Тестовый запуск сервера со сборкой до `Done`; лог в `<папка>/logs/latest.log` |
| [scripts/test-client.sh](scripts/test-client.sh) | Тестовый запуск клиента из `.mrpack` без лаунчера (Xvfb + llvmpipe) до главного меню |
| [scripts/tests/](scripts/tests/) | Проверочные скрипты и списки команд для тестового сервера (заклинания, ракеты, спавн, города, боссы, отчёты) |
| [scripts/tests/savecheck.js](scripts/tests/savecheck.js) | Замер сохранения мира по измерениям: автосейв, сохранение с flush (как выход из мира), во время генерации — см. `docs/performance.md` «Долгое сохранение» |
| [scripts/tests/cn_nogrief.js](scripts/tests/cn_nogrief.js) | Проверка, что взрыв реактора Create Nuclear не ломает блоки (`createnuclear_no_grief.js`) — см. `docs/compat.md` «Create Nuclear» |
| [scripts/tests/radsuit.js](scripts/tests/radsuit.js) | Проверка единого защитного костюма: костюм Create Nuclear защищает от радиации Mekanism и Create Nuclear, Hazmat не крафтится — см. `docs/compat.md` «Один защитный костюм» |
| [scripts/tests/dedup_dump.js](scripts/tests/dedup_dump.js) | Выгрузка всех предметов и рецептов со стенда в JSON для аудита дублей — см. `docs/dedup.md` |
| [scripts/tests/dedupcheck.js](scripts/tests/dedupcheck.js) | Проверка убранных логических дублей: нет рецептов, нет жёстких ссылок в чужих рецептах, оставшийся предмет есть — см. `docs/compat.md` «пачка dedup-1» |
| [scripts/tests/hazardgear.js](scripts/tests/hazardgear.js) | Проверка защитного снаряжения после объединения дублей: костюм Create Nuclear в Alex's Caves, противогаз Spore против газа CBC — см. `docs/compat.md` «пачка dedup-2» |
| [scripts/dedup-audit/](scripts/dedup-audit/) | Аудит дублей: теги из jar, сложность крафта по графу рецептов, группы кандидатов, виды энергии, перенос тегов (tagcopy.py), переопределения лута и достижений (overrides.py) — см. `docs/dedup.md` «Как считали» |
| [scripts/clienttest/clientmobs.js](scripts/clienttest/clientmobs.js) | Клиентская проверка рендера мобов: по одному мобу каждого вида в ряд |
| [scripts/clienttest/ExitTrace.java](scripts/clienttest/ExitTrace.java) | Java-агент для `test-client.sh`: печатает стек каждого `System.exit` |
| [scripts/collect-diagnostics.bat](scripts/collect-diagnostics.bat) | Запуск двойным щелчком (Windows): вызывает `collect-diagnostics.ps1` |
| [scripts/collect-diagnostics.ps1](scripts/collect-diagnostics.ps1) | Собирает логи и данные о ПК в один zip на рабочем столе — см. `docs/diagnostics.md` |
| [scripts/memwatch.sh](scripts/memwatch.sh) | Слежение за кучей работающего сервера (поиск утечек): old gen и гистограммы объектов |
| [scripts/jfr/JfrTop.java](scripts/jfr/JfrTop.java) | Разбор JFR-профиля серверного потока: топ методов и доля по модам |
| [scripts/jfr/JfrMods.java](scripts/jfr/JfrMods.java) | JFR: «чьё время» в потоке — первый кадр мода от вершины стека, методы модов, события Forge по типу — см. `docs/performance.md` «Поток 67» |
| [scripts/jfr/JfrMore.java](scripts/jfr/JfrMore.java) | JFR: CPU по потокам (сервер, генерация, Distant Horizons…), паузы GC, аллокации по модам и классам |
| [scripts/jfr/JfrGrep.java](scripts/jfr/JfrGrep.java) | JFR: доля сэмплов с заданным методом в стеке и кто его вызывает (например `getChunkBlocking`) |
| [scripts/tests/perfload.js](scripts/tests/perfload.js) | Нагрузочный замер сервера: покой → фейковые игроки → полёт с генерацией → save-all / flush; MSPT по фазам — `docs/performance.md` «Поток 67» |
| [scripts/tests/hungercheck.js](scripts/tests/hungercheck.js) | Замер расхода голода (усталости) за минуту: стоя / ходьба / бег / прыжки, без расы и с расами Arknights — см. `docs/compat.md`, «Быстрый голод» |
| [scripts/spore_infection_map.py](scripts/spore_infection_map.py) | Карта заражения мобов для Spore и SRP System по выгрузке реестра |

### Скрипты: проверка модов и совместимость
| Файл | Что здесь объясняется |
|---|---|
| [scripts/hardrefs.py](scripts/hardrefs.py) | Рецепты, требующие предмет по id, а не по тегу — проверка перед объединением дублей |
| [scripts/missing_tag_refs.py](scripts/missing_tag_refs.py) | Убирает из тегов ссылки на отсутствующие предметы → `missing_mod_refs.js` |
| [scripts/linkcheck/linkcheck.sh](scripts/linkcheck/linkcheck.sh) | Что мод использует из других модов, но не находит в сборке (собран под другую версию) |
| [scripts/linkcheck/LinkCheck.java](scripts/linkcheck/LinkCheck.java) | Сама проверка связывания классов, методов и полей для `linkcheck.sh` |
| [scripts/jarport/JarPort.java](scripts/jarport/JarPort.java) | Порт jar под новые версии зависимостей: переписывает ссылки в байткоде по правилам |
| [scripts/check_mod_models.py](scripts/check_mod_models.py) | Статическая проверка моделей мода: blockstates → models → textures |
| [scripts/jei_audit.py](scripts/jei_audit.py) | Аудит JEI: типы рецептов модов и наличие JEI-плагина |
| [scripts/boss_table.py](scripts/boss_table.py) | Генератор `docs/bosses.md` и `docs/bosses.csv` из jar сборки |
| [scripts/boss_info.py](scripts/boss_info.py) | Ручные описания боссов (названия, где найти, фазы), которые код не отдаёт |
| [scripts/ysm-models.py](scripts/ysm-models.py) | Подключает готовые модели YSM из открытого репозитория как метафайлы packwiz |
| [scripts/ysm-models-author.py](scripts/ysm-models-author.py) | Подключает модели Azur Lane для YSM из сборника по ссылкам с закреплённым коммитом |
| [scripts/ysm-models-author-skip.txt](scripts/ysm-models-author-skip.txt) | Модели, которые YSM 2.6.5 не читает, — пропускаются при подключении |

### Скрипты: русификация и книги
| Файл | Что здесь объясняется |
|---|---|
| [scripts/lang_audit.py](scripts/lang_audit.py) | Аудит `ru_ru` против `en_us` по каждому jar и книгам |
| [scripts/lang_packs_cover.py](scripts/lang_packs_cover.py) | Сколько строк закрывает каждый ресурспак-русификатор |
| [scripts/lang_residual.py](scripts/lang_residual.py) | Недостающие строки после родных ru и паков → пачки для перевода |
| [scripts/lang_merge.py](scripts/lang_merge.py) | Сборка переведённых пачек в ресурспак с проверкой ключей и плейсхолдеров |
| [scripts/lang_validate.py](scripts/lang_validate.py) | Проверка перевода: ключи, плейсхолдеры, §-коды, книги |
| [scripts/lang_modnames.py](scripts/lang_modnames.py) | Общие правила: какие ключи — названия модов (они остаются английскими) |
| [scripts/lang_keep_en_modnames.py](scripts/lang_keep_en_modnames.py) | Возвращает английские названия модов в нашем паке |
| [scripts/lang_keep_en_names.py](scripts/lang_keep_en_names.py) | Оставляет английскими названия рас/классов Arknights и деревьев навыков |
| [scripts/lang_term_fix.py](scripts/lang_term_fix.py) | Единая терминология в паке (например, «ориджиниум») |
| [scripts/lang_shorten_keys.py](scripts/lang_shorten_keys.py) | Сокращает подписи клавиш, чтобы они не уезжали за экран на крупном интерфейсе |
| [scripts/font_audit.py](scripts/font_audit.py) | Есть ли кириллица в кастомных шрифтах модов и паков |
| [scripts/cjk_nonlang_audit.py](scripts/cjk_nonlang_audit.py) | Иероглифы вне lang-файлов: data, assets, NBT, константы в классах |
| [scripts/cjk_nonlang_audit_lib.py](scripts/cjk_nonlang_audit_lib.py) | Общие функции для двух CJK-скриптов: строки из `.class` и NBT |
| [scripts/cjk_literals_harvest.py](scripts/cjk_literals_harvest.py) | Собирает китайские/японские строки-литералы в пачки для перевода |
| [scripts/literals_build.py](scripts/literals_build.py) | Собирает словарь литералов `ru-lang` из переведённых пачек |
| [scripts/books/patchouli_units.py](scripts/books/patchouli_units.py) | Перевод Patchouli-книг с «сырым» текстом: разложить на единицы и применить |
| [scripts/books/mna_guide.py](scripts/books/mna_guide.py) | То же для Кодекса Mana and Artifice (собственный формат мода) |
| [scripts/books/](scripts/books/) | Данные переводов внутриигровых книг: `*_data/units_en.json` и `units_ru.json` по книгам (аддоны Ars Nouveau, Alex's Mobs Interaction, Ars Magica Legacy, Animus, Bob's Originium Dream, Caerula Arbor, Dimensional Dungeons, Кодекс Mana and Artifice) |

### Таблица и форма отчётов
| Файл | Что здесь объясняется |
|---|---|
| [scripts/report-sheet/Code.gs](scripts/report-sheet/Code.gs) | Apps Script: создаёт форму, связывает с таблицей, нумерует отчёты и подсвечивает статусы; без личных данных |
| [scripts/report-status.py](scripts/report-status.py) | Генератор публичной `docs/report-status.md` из выгрузки таблицы (выгрузка — вне репозитория) |

### Внутриигровые подсказки и настройки сборки (pack/)
| Файл | Что здесь объясняется |
|---|---|
| [pack/kubejs/server_scripts/](pack/kubejs/server_scripts/) | KubeJS-правки: убранные ссылки в тегах, спавн OreSpawn, правила бури, ракеты, свечение и формы модели Priestess, выключенный бонусный лут мобов Contingency Contract |
| [pack/kubejs/startup_scripts/dedup_list.js](pack/kubejs/startup_scripts/dedup_list.js) | Список логических дублей между модами (`global.mmDedup`: убранный предмет → оставшийся): рецепты, JEI, креатив — см. `docs/compat.md` «Один защитный костюм» |
| [pack/kubejs/data/](pack/kubejs/data/) | Данные KubeJS: рецепты, лут, теги, патчи Patchouli-книг модов |
| [pack/kubejs/assets/](pack/kubejs/assets/) | Модели и атласы текстур, поправленные KubeJS |
| [pack/config/almostunified/](pack/config/almostunified/) | Объединение дублей предметов между модами (Almost Unified) |
| [pack/config/Weather2/Misc.toml](pack/config/Weather2/Misc.toml) | Настройки Weather2 (торнадо, бури); в шапке — зачем изменено |
| [pack/config/jei/client/default/jei-client.ini](pack/config/jei/client/default/jei-client.ini) | Настройки клиента JEI по умолчанию |
| [pack/config/voicechat/voicechat-server.properties](pack/config/voicechat/voicechat-server.properties) | Настройки сервера голосового чата |
| [pack/config/yes_steve_model/custom/](pack/config/yes_steve_model/custom/) | Метафайлы моделей игроков YSM (скачиваются при установке) |

### Свои моды и патчи
| Файл | Что здесь объясняется |
|---|---|
| [mods-src/wizardry-restored/](mods-src/wizardry-restored/) | Недостающие заклинания Electroblob's Wizardry поверх Redux (план — `docs/wizardry-port.md`) |
| [mods-src/wizardry-golems/NOTICE.md](mods-src/wizardry-golems/NOTICE.md) | Порт Wizardry Golems на 1.20.1: оригинал, авторство, лицензия GPL-3.0 |
| [mods-src/necromancers-delight/NOTICE.md](mods-src/necromancers-delight/NOTICE.md) | Порт Necromancer's Delight на 1.20.1: оригинал, авторство, лицензия GPL-3.0 |
| [mods-src/fishs-undead-rising/NOTICE.md](mods-src/fishs-undead-rising/NOTICE.md) | Частичный порт Fish's Undead Rising: оригинал, авторство, условия |
| [mods-src/galacticraft-revival/CREDITS.md](mods-src/galacticraft-revival/CREDITS.md) | Galacticraft Revival: откуда что взято, лицензии оригиналов |
| [mods-src/voicechat-addons/README.md](mods-src/voicechat-addons/README.md) | Порт на Forge аддонов Simple Voice Chat (Voice Chat Interaction, AudioPlayer, Enhanced Groups) |
| [mods-src/mm-compat/](mods-src/mm-compat/) | Кодовые связки и исправления сборки (кислород скафандров, фризы, утечки памяти и др.) |
| [mods-src/mm-perf/](mods-src/mm-perf/) | Клиентские удобства: режим слабого ПК, клавиши «Не назначено», подсказки о памяти |
| [mods-src/mm-autoupdate/](mods-src/mm-autoupdate/) | Серверный мод: при остановке сервера обновляет сборку с GitHub через packwiz-installer (хостинг без команды перед стартом; `docs/hosting.md`) |
| [mods-src/disaster-ward/](mods-src/disaster-ward/) | Блок-оберег от катаклизмов, заражения и ориджиниумной бури; редкий спавн бедствий |
| [mods-src/ru-lang/](mods-src/ru-lang/) | Клиентский мод, всегда включающий русский ресурспак сборки |
| [mods-src/jar-ports/](mods-src/jar-ports/) | Классы-переходники для портов чужих jar |
| [mods-src/ysm-priestess/](mods-src/ysm-priestess/) | Генератор и офлайн-превью модели игрока Priestess для YSM |
| [mods-src/ysm-vivi/](mods-src/ysm-vivi/) | Генератор модели игрока Vivi для YSM (девушка-волчица в бордовом пальто): подбор углов пальто и хвоста по позам (`tune.py`), проверка пересечений (`vivicheck.py`), превью |
| [patches/jar-ports/build.sh](patches/jar-ports/build.sh) | Порт чужого jar под версии зависимостей сборки: скачивание, правила, переходники |
| [patches/jar-ports/origin-furs/embed.txt](patches/jar-ports/origin-furs/embed.txt) | Пример встраивания старой библиотеки внутрь порта |
| [patches/abyssal-jump/build.sh](patches/abyssal-jump/build.sh) | Ослабляет «Прыгучесть» у «Охотников Бездны» в jar arknights_weapon |
| [patches/gaboulibs-shim/build.sh](patches/gaboulibs-shim/build.sh) | Замена удалённой библиотеки для Serene Seasons Plus |
| [patches/lycanites-refmap/build.sh](patches/lycanites-refmap/build.sh) | Подкладывает недостающий refmap миксинов Lycanites Mobs |
| [patches/zombie-have-ears/build.sh](patches/zombie-have-ears/build.sh) | Zombie Have Ears с `mods.toml` в формате Forge |
| [patches/irons_loot_1_20_1.py](patches/irons_loot_1_20_1.py) | Переписывает таблицы добычи Iron's Spells из формата 1.21 в 1.20.1 |
| [patches/northstar_structures_fix.py](patches/northstar_structures_fix.py) | Биомы Northstar Structures под Northstar Redux 0.6 и кратеры на Луне |
| [patches/ore_crossgen.py](patches/ore_crossgen.py) | Ресурсы Northstar и Creating Space на планетах Ad Astra |
| [patches/space_unify.py](patches/space_unify.py) | Одна Солнечная система: пункты назначения ракет перенаправлены на миры Ad Astra |
