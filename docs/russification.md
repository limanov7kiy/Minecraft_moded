# Русификация сборки (поток 6) — этап 1: аудит, готовые русификаторы, план

Дата: 2026-10-12. Связка 1.20.1 + Forge 47.4.26. Перевод вручную **не начат** — ждёт решения владельца (вопросы — в конце).

Инструменты (в репозитории):
- `scripts/lang_audit.py <папка jar> [--md out.md] [--json out.json]` — `ru_ru` против `en_us` по каждому jar (включая jar в jar),
  Patchouli-книги, Modonomicon, Ponder, квесты внутри jar.
- `scripts/lang_packs_cover.py <папка jar> <pack.zip>...` — сколько недостающих строк закрывает каждый русификатор.
- `scripts/font_audit.py <папка jar>` — кастомные шрифты и кириллица.
- `scripts/lang_validate.py lang <ru_ru.json|папка пака> <папка jar>` — валидатор перевода строк (JSON, ключи есть в `en_us`, плейсхолдеры
  `%s/%1$s/{0}/$(..)` по составу, §-коды, пустые и «нерусские» значения); `lang_validate.py book <en_us книги> <ru_ru книги>` —
  те же файлы, теги `$(..)` и служебные поля Patchouli совпадают. Код возврата 1 при ошибках. Проверен на `wizardry-restored` (0 ошибок)
  и на паке RTF+ (находит реальные расхождения версий).
- Полная таблица по модам — `docs/russification-audit.md` (генерируется скриптами).
  Набор для аудита: `python3 scripts/fetch-mods.py client <папка вне репо>` (242 jar = клиент + наши jar из `pack/mods/`).

## 1. Итог аудита (цифры)

| | |
|---|---:|
| jar проверено | 242 |
| из них с `en_us` (есть что переводить) | 190 |
| из них вообще без `ru_ru` | 91 |
| английских ключей всего | 72 053 |
| не переведено (нет в `ru_ru` или `ru == en`) | **25 678** (покрытие 64,4 %) |
| **не переведено после включённых русификаторов** | **12 011** (покрытие ≈ 83 %) в 158 пространствах имён |
| jar без lang-файлов (библиотеки и т.п.) | 51 |

Мод с основным китайским языком не встретился: у Arknights-модов, Caerula Arbor, Animus и др. есть `en_us` (+ `zh_cn`),
а `ru_ru` нет — переводить с английского. Единственный jar без `en_us` — `dimdungeons` (там только `ru_ru`).
Мы не нашли квестов (FTB Quests/Heracles) ни в jar, ни в `pack/`; KubeJS-скрипты `pack/kubejs` не содержат
отображаемых текстов; наши моды (`wizardry-restored`, `galacticraft-revival`) уже переведены полностью.
Кастомные шрифты — раздел 5.

Книги и гайды (Patchouli `assets/*/patchouli_books/<книга>/<язык>`):

| Книга (мод) | файлов | знаков en | в jar ru | закрыто паками |
|---|---:|---:|---:|---|
| Blood Magic `guide` | 237 | 402 796 | нет | да (RTF+ и MPLOC) |
| Botania `lexicon` | 265 | 186 718 | нет | да (RTF+) |
| Caerula Arbor | 48 | 72 290 | нет | да |
| Extra Botany `lexicon` | 67 | 45 631 | нет | да |
| Iron's Spellbooks `iss_guide_book` | 10 | 18 933 | нет | да (MPLOC) |
| Mythic Botany, Twilight Delight, Botanical (Extra) Machinery | 51 | ≈ 38 000 | нет | да |
| Ad Astra `astrodux` | 35 | 33 739 | 25 файлов | частично |
| **Animus `codex_animus`** | 96 | 132 105 | нет | **нет** |
| **Ars Nouveau `worn_notebook`** | 281 | 131 347 | нет | **нет** |
| **Ars Magica Legacy `arcane_compendium`** | 174 | 85 683 | нет | **нет** |
| Ars Elemental, Ars Additions, Tome of Blood, Ars Instrumentum, Too Many Glyphs, Ars Technica/Delight/Ocultas/Creo/Artifice | 142 | ≈ 74 000 | нет | **нет** |
| Alex's Mobs Interaction `amiwiki` | 51 | 25 295 | нет | **нет** |
| Superb Warfare `manual` | 31 | 14 733 | нет | **нет** |
| Extra Botany `exguide`, Bob's Originium `bobbook` | 12 | 12 596 | нет | **нет** |

Не закрытые паками книги — **≈ 478 000 знаков** (≈ 130 тыс. токенов английского текста). Modonomicon (Occultism 170 файлов,
Occult Engineering 23): тексты лежат в lang-ключах, учтены в таблице строк. Ponder-сцены (Create, Northstar, Steam Rails…) —
тоже в lang-ключах. Оговорка: пак «закрывает» книгу по имени папки; версия книги в паке может отличаться от версии мода
(тогда часть страниц останется английской) — проверять глазами при приёмке.

Распределение остатка 12 011 строк (после паков): предметы/блоки/сущности/эффекты ≈ 5 470; GUI/сообщения/подсказки ≈ 2 800;
достижения и прочее ≈ 3 740.

## 2. Готовые русификаторы под 1.20.1 (Modrinth)

Проверено скриптом на реальных jar сборки (закрыто = ключи, которых нет в родном `ru_ru` мода и которые в паке отличаются от en).
Кроме 5 добавленных, прогнаны ещё 8 — они не дали пользы (таблица ниже).

| Пак | Лицензия | Обновлён | pack_format | Закрывает | Решение |
|---|---|---|---:|---:|---|
| **MPLOC mods** (`mploc_mods`) | CC-BY-SA-4.0 | 2026-10-05 | 88 (supported 4–88, включает 15) | 7 464 ключа / 67 ns | **добавлен**, нижний слой; перевод ИИ, высокое покрытие аддонов (Aether, Blood Magic, Animus, MNA…) |
| **RTF Extension** (`rtf-extension`) | AGPL-3.0 | 2026-08-01 | 15 | 8 109 / 83 ns | **добавлен**, верхний слой (переводчики-люди, книги Goety, Tinkers, Oritech, JDT, Productive Bees…) |
| RTF (`rtf`) | MIT | 2024-02 | 15 | 310 / 15 | **добавлен**: Ad Astra, Twilight Delight, Undergarden |
| Russian Delight | MIT | 2026-05 | 15 | 332 / 4 | **добавлен**: Aether's Delight, Ars Delight, Lender's Delight, Alex's Delight |
| JustSomeTranslations | MIT | 2026-09 | 34 | 323 / 2 | **добавлен**: Xaero's World Map (+Sodium) |
| Rus For Mods (`mods-ru`) | MIT | 2024-12 | 15 | 145 | не нужен, всё покрыто выше |
| Russian Translation Pack | свои права | 2026-06 | 34 | 243 | отказ: Create Big Cannons (уже есть в RTF+) |
| RTC, Take me Translate, Русский перевод, ERP, Unusual mods, Better Russian | разные | — | — | 0–113 | не нужны / ничего нового |

Вместе добавленные паки закрывают **не менее 13 600 строк** (с учётом пересечений: 25 678 → 12 011).
Файлы не лежат в репозитории — packwiz качает их с Modrinth (`pack/resourcepacks/*.pw.toml`, `side = client`),
в `pack/modlist.txt` — раздел «Русификация» (источник `rp`, обрабатывается `scripts/add-mods.sh`).
CurseForge-«RUS For Mods» отдельно не искали: Modrinth-аналог (`mods-ru`) показал, что он ничего не добавляет.
Модов-русификаторов (не ресурспаков) под 1.20.1 в сборку не требуется.

Условия лицензий (сборка некоммерческая): CC-BY-SA и AGPL требуют указания авторов — указываем здесь и в
`pack/modlist.txt`: **MPLOC** (CC-BY-SA-4.0), **RTF/RTF+ — авторы проектов `rtf` и `rtf-extension` на Modrinth** (MIT / AGPL-3.0),
**Russian Delight**, **JustSomeTranslations** (MIT). Сами zip мы не раздаём (ссылки на Modrinth), форков нет.

Замечания по качеству:
- MPLOC — машинный перевод «на современных ИИ», в нём реклама (3 картинки `boostylogo.png` и т.п. в `textures/item`) —
  вреда нет; и MPLOC, и RTF+ содержат `assets/minecraft/lang/ru_ru.json` (правка ванильных строк) — при несогласии с терминологией
  убрать эту запись, порядок слоёв задаёт `options.txt`.
- Порядок слоёв (чем правее — тем приоритетнее): `just-some-translations`, `russian-delight`, `rtf`, `mploc_mods`, `rtf-extension`.
  Родной `ru_ru` мода (в jar) всегда ниже любого ресурспака — пак перекрывает его; для модов с хорошим родным переводом
  (Create, Ars Nouveau…) это может ухудшить термины. Если у кого-то это заметно — решить выборочно через `RP`-исключения
  (см. «Вопросы»).

## 3. Язык по умолчанию и включение паков

`pack/options.txt` (в индексе packwiz с `preserve = true` — packwiz-installer не затирает настройки игрока при обновлении):

```
lang:ru_ru
resourcePacks:["vanilla","mod_resources","file/just-some-translations.zip","file/russian-delight-1.0.zip","file/rtf-2.8.4.zip","file/mploc-mods-v43.zip","file/rtf-extension-2.7.0.zip"]
```

Особенности:
- Файл неполный (только две строки): Minecraft добавит остальные настройки сам. `mod_resources` оставлен — без него Forge
  не подключит ресурсы модов.
- Имена `file/...` совпадают с `filename` в `pack/resourcepacks/*.pw.toml` (переименованы из «§eRTF+ …» в безопасные).
  При обновлении версии пака имя в `options.txt` обновлять вручную.
- Для packwiz-installer: `options.txt` скопируется только если его ещё нет (`preserve`). Для `.mrpack` (Prism/Modrinth App) он
  идёт в `overrides/` и применяется при **создании** экземпляра; при обновлении лаунчер может его перезаписать — тогда игрок
  просто включает паки в «Ресурспаки». Инструкцию для друзей дописать в `docs/friends.md` — в этом этапе не трогаем (общий файл,
  меняется вместе с экспортом; предложение: одна строка «Настройки → Язык → Русский; ресурспаки должны быть включены»).
- Если у друга `options.txt` уже есть (старый экземпляр), язык и паки включаются вручную: Настройки → Язык, Ресурспаки.
- Сервер ресурспаки не использует; `fetch-mods.py` их не качает (берёт только `pack/mods`), так что серверный набор не менялся.

## 4. План перевода остатка (12 011 строк + книги)

Приоритет 1 — **предметы/блоки/сущности/GUI** (≈ 8 300 строк), приоритет 2 — достижения и прочее (≈ 3 700), приоритет 3 — книги
(≈ 478 тыс. знаков). Строки короткие (в среднем ≈ 25 знаков), поэтому пачка ≈ 250–300 строк ≈ 3–4 тыс. токенов вывода.

| Пачка | Содержимое | Строк | Пачек |
|---|---|---:|---:|
| A. Arknights-семейство | arks, arknights_endfield/skills/furniture/weapon/races, contigency_contract, fnc, ognmarts, prma | ≈ 1 700 | 7 |
| B. Магия | arsmagicalegacy, mna (Mana & Artifice), ebwizardry (Redux), occultism, extrabotany, cataclysm, arcaneadditions, ars_elemental, lycanites*, blue_skies, lost_aether_content | ≈ 2 800 | 11 |
| C. Техника/космос/оружие | mekmm, creatingspace, northstar, northstar_structures, ad_astra, vs_sails, superbwarfare, lrtactical, dmnr, jei_mekanism_multiblocks | ≈ 1 400 | 6 |
| D. Прочие моды | mahoutsukai (1 038 «совпадающих с en»), bobsoriginiumdream, sarkazs_furnace_side_fables, torchesbecomesunlight, caerula_arbor, tflostblocks, jei, jeed, distanthorizons, xaerominimap, alexsmobsinteraction | ≈ 4 100 | 16 |
| E. Хвост | ≈ 120 модов по 1–50 строк | ≈ 2 000 | 8 |
| F. Книги | Animus, Ars Nouveau (Worn Notebook), Ars Magica Legacy, Ars-аддоны, AMI wiki, Superb Warfare manual | ≈ 478 тыс. знаков | ≈ 40 (по 12 тыс. знаков) |

Итого ≈ 48 пачек для строк + ≈ 40 для книг. Исполнитель — дешёвая модель (Haiku/Sonnet), на выходе — `ru_ru.json`
только с недостающими ключами в ресурспак `pack/resourcepacks-src/russian-moded` (свой пак «сверху» всех остальных),
проверка скриптом: те же плейсхолдеры (`%s`, `%1$s`, `{0}`, `§x`), JSON валиден, нет ключей, которых нет в `en_us`.
Для книг — копии Patchouli-JSON в `assets/<ns>/patchouli_books/<книга>/ru_ru/...` с сохранением структуры и ссылок
(`$(item)`, `$(l:...)`, `$(br)` не трогать), проверка — сравнение набора тегов `$(...)`.

### Глоссарий (единые термины для всех пачек)

Общее: item — предмет, block — блок, mob — моб, spawner — спавнер, dimension — измерение, loot — добыча, tier — уровень,
cooldown — перезарядка, durability — прочность, hunger — голод.
Магия: mana — мана (не «магическая энергия»), spell — заклинание, spellbook — книга заклинаний, scroll — свиток, wand — жезл,
staff — посох, rune — руна, glyph — глиф, ritual — ритуал, familiar — фамильяр, ward — оберег, altar — алтарь, tome — фолиант,
source (Ars Nouveau) — Источник, will (Blood Magic) — воля, essence — эссенция, aura — аура, sigil — сигил, curse — проклятие,
school — школа (магии), summon — призыв, bound — связанный, arcane — тайная магия/чародейский (по контексту), Eldritch — потусторонний,
Fire/Ice/Lightning/Nature/Ender/Holy/Blood Spells — огонь/лёд/молния/природа/эндер/свет/кровь.
Техника: machine — механизм, generator — генератор, conveyor — конвейер, gear — шестерня, cogwheel — зубчатое колесо, shaft — вал,
stress (Create) — нагрузка, rotation speed — скорость вращения, ponder — осмысление (Create), blueprint — чертёж, wrench — ключ,
energy/FE — энергия (FE), tank — бак, pipe — труба, cable — кабель, upgrade — улучшение, circuit — плата/схема.
Космос: oxygen — кислород, space suit — скафандр, rocket — ракета, launch pad — стартовая площадка, planet — планета,
orbit — орбита, gravity — гравитация, fuel — топливо, tier N rocket — ракета N уровня, station — станция, lander — посадочный модуль.
Оружие: ammo — боеприпасы, magazine — магазин, round — патрон, attachment — модификация/обвес, scope — прицел, recoil — отдача,
cannon — пушка, shell — снаряд, barrel — ствол, firing rate — скорострельность.
Имена собственные (Create, Botania, Mekanism и т.п.), названия модов, `Iron's Spells`, `Ars Nouveau` — не переводить.
Arknights: оставлять транслит-имена операторов, Originium — Оригиниум, Operator — оператор, Infected — заражённый, Oripathy — орипатия.

## 5. Шрифты

`scripts/font_audit.py` нашёл всего 5 кастомных шрифтов в 242 jar:

| Мод | Шрифт | Что с кириллицей |
|---|---|---|
| Aether `aether:logomarks` | bitmap | не используется для текста, только логотип |
| Caerula Arbor `caerula_arbor:seabornish` | bitmap+reference | декоративный «язык» для книги — кириллица не нужна; **проверить глазами**, что обычный текст книги не отображается им |
| ldlib `ldlib:jetbrains_mono_bold` | ttf | JetBrains Mono содержит кириллицу |
| Modonomicon `modonomicon:default`, Supplementaries `supplementaries:antiquable` | reference на ваниль | ок |

Подозрительных шрифтов нет; визуально проверить: книга Caerula Arbor, меню Modonomicon (Occultism), Jade/JEI (отдельных шрифтов нет).
Чат Simple Voice Chat и Xaero используют ванильный шрифт.

## 6. Что сделано в этом запуске / не сделано

Сделано: аудит и скрипты; 5 русификаторов в `pack/resourcepacks/` + `pack/modlist.txt` + `scripts/add-mods.sh` (источник `rp`);
`pack/options.txt` (ru_ru + порядок паков, `preserve`); `packwiz refresh`; отметка в `docs/roadmap.md`.

Не сделано / ограничения: серверный набор не менялся (паки `client`), поэтому `scripts/test-server.sh` не гонял;
`scripts/test-client.sh` до `CLIENT OK` в этом запуске **не прогонялся** (нужен полный экспорт `.mrpack`, экономия лимита) —
первое, что стоит проверить при приёмке: что клиент стартует с `options.txt`, паки подхватились и в главном меню русский язык.
Версии MPLOC (pack_format 88) на 1.20.1 «несовместимость» не выдают благодаря `supported_formats`, но это не проверено в запущенном клиенте.

## 7. Этап 2 — перевод строк (2026-10-12)

Переведено **11 485 строк** (+382 имён собственных оставлены как есть) в 156 пространствах имён: 54 пачки, перевод — Haiku-агентами
по `scripts/lang_residual.py` (пачки по ≤ 250 ключей) → `scripts/lang_merge.py` (проверка набора ключей и плейсхолдеров) →
`pack/resourcepacks-src/russian-moded/assets/<ns>/lang/ru_ru.json` → `scripts/build-ru-pack.sh` → `pack/resourcepacks/russian-moded.zip`
(в индексе packwiz; исходники исключены `.packwizignore`). Пак стоит **последним** в `options.txt` (наивысший приоритет).
`lang_validate.py lang` по всему паку: 0 ошибок (302 предупреждения — «лишние» ключи из `.lang`-файлов, безвредны).
После пересчёта остаётся ~377 строк — это названия/имена, совпадающие с английским. Книги и гайды не трогались (по решению владельца).
Замечания: у мода `bobsoriginiumdream` (и ряда Arknights-модов) в `en_us.json` лежит китайский текст — переведён с китайского;
качество Haiku выборочно проверено на `arsmagicalegacy` (литературно, без потери плейсхолдеров), полная вычитка глазами — на приёмке клиента.
Повторить пересчёт: `python3 scripts/lang_residual.py <jar> <out> --packs <5 zip> --skip-done pack/resourcepacks-src/russian-moded/assets`.

### Находка проверки клиента (`test-client.sh`)
Первый прогон дал `CLIENT OK` (371 с, главное меню по-русски), но в логе: *«Removed resource pack file/mploc-mods-v43.zip from options because it is no longer
compatible»* — 1.20.1 принимает только `pack_format` 15, а у MPLOC он 88 (`supported_formats` в 1.20.1 не работает), у JustSomeTranslations — 34.
Оба пака отброшены, держались только RTF/RTF+/Russian Delight/наш пак. Исправлено: `scripts/patch-rp-format.py` делает копию с `pack_format: 15`
(содержимое без изменений; MIT и CC-BY-SA это допускают с сохранением авторства: **MPLOC**, **JustSomeTranslations**) → `pack/resourcepacks/mploc-mods-v43.zip`
и `just-some-translations.zip` (обычные файлы в индексе packwiz, исходные `.pw.toml` — в `pack/resourcepacks-src/upstream/`, в `modlist.txt` помечены `#patched`).
При обновлении версии MPLOC — перегнать скрипт и поправить имя в `options.txt`.

## Вопросы владельцу

1. **Не блокирует.** Принять MPLOC (ИИ-перевод) и RTF+ базой, а остальное доводить своим паком сверху? Рекомендация: да.
2. **Блокирует этап 2.** Переводить ли книги (≈ 478 тыс. знаков, ≈ 40 пачек), или только строки (≈ 48 пачек)? Рекомендация:
   строки приоритета 1 сейчас, книги — по отзывам друзей (Ars Nouveau и Animus первыми).
3. **Не блокирует.** Модель для пачек (Haiku против Sonnet): Haiku дешевле, Sonnet заметно лучше сохраняет теги и плейсхолдеры.
   Рекомендация: Sonnet для книг, Haiku со скриптовой проверкой для строк.
