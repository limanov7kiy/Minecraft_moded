# Galacticraft в сборке (поток «Galacticraft»)

Цель: вернуть геймплей Galacticraft на **1.20.1 + Forge 47.4.26** так, чтобы ощущалось как GC и вписывалось в
единую Солнечную систему (`docs/compat.md` → «Одна Солнечная система»): планеты и кислород — Ad Astra,
ракеты — Northstar / Creating Space, гравитация — Northstar.

## Точка остановки (для следующего чата)
- **Этап 0 — сделан** (этот документ).
- **Этап 1 — в работе:** вертикальный срез «данж Луны с боссом» в `mods-src/galacticraft-revival`
  (см. «План этапа 1»). Следующий шаг — пункт, не отмеченный ✅ в плане.
- Исходники для переноса держать **вне репозитория** (в scratchpad):
  `git clone --depth 1 https://github.com/TeamGalacticraft/Galacticraft-Legacy` и
  `git clone --depth 1 https://github.com/TeamGalacticraft/Galacticraft` (GC5).
  Коммиты, на которых проводилась разведка: Legacy `acec442` (2024-04-09), GC5 `de07ca0` (2026-10-06).

## 1. Лицензия и авторство
Сборка частная и некоммерческая (CLAUDE.md → «Назначение и лицензии»): переносим и правим код и ассеты
напрямую, авторство сохраняем, ничего не публикуем. Ниже — что формально написано у оригиналов, для учёта.

| Проект | Лицензия сейчас | Авторы / правообладатели |
|---|---|---|
| **Galacticraft Legacy** (1.12.2), [TeamGalacticraft/Galacticraft-Legacy](https://github.com/TeamGalacticraft/Galacticraft-Legacy) | Файл `LICENSE` — **MIT**, «Copyright (c) 2022 Team Galacticraft»; в заголовках java — «Copyright (c) 2023 Team Galacticraft, Licensed under the MIT license». На [Modrinth](https://modrinth.com/mod/galacticraft-legacy) тоже MIT | Исходный код — micdoodle8 (Mitchell Petrie), radfast, fishtaco567 и участники, продолжение — Team Galacticraft (ROMVoid95, SteveKunG и др.) |
| Старая лицензия того же кода, [micdoodle8/Galacticraft `LICENSE.txt`](https://github.com/micdoodle8/Galacticraft/blob/master/LICENSE.txt) (в Legacy лежит как `etc/OLD_LICENSE.txt`) | Своя, некоммерческая. П. 28(e): «Copies or ports to other versions of Minecraft or Forge ... are PROHIBITED without the written permission of the Galacticraft developers»; «distribution of out-dated, altered, modified, converted or partial copies ... is prohibited» | «the persons known by the Github usernames micdoodle8, radfast and fishtaco567» |
| **Galacticraft 5** (1.21.1 Fabric, пре-альфа), [TeamGalacticraft/Galacticraft](https://github.com/TeamGalacticraft/Galacticraft) | **MIT**, «Copyright (c) 2019-2026 Team Galacticraft» (и в каждом java-файле) | Team Galacticraft; micdoodle8 указан как developer в `capes_roles.json` |

Замечания:
- MIT в Legacy появился коммитом `9ed93d8` (2022-08-18, «build: rework publishing in GitHub Actions», автор rom /
  ROMVoid95); старый текст переименован в `OLD_LICENSE.txt`. Отдельного заявления micdoodle8/radfast о смене
  лицензии не нашёл; репозиторий micdoodle8 сам отсылает к Legacy («This project is continued with updates @
  TeamGalacticraft/Galacticraft-Legacy»). Для нашего частного круга это не важно (решение пользователя), но при
  любой мысли о публикации — пересмотреть.
- 82 PNG в GC5 побайтно совпадают с Legacy — GC5 сам переиспользует старые ассеты.
- Как сохраняем авторство: в каждом перенесённом java-файле — шапка «Ported from Galacticraft Legacy
  (micdoodle8, radfast, Team Galacticraft) / Galacticraft 5 (Team Galacticraft), file: <путь>»; в jar —
  `META-INF/GALACTICRAFT-LICENSE.txt` (MIT) и `META-INF/GALACTICRAFT-OLD-LICENSE.txt`; в `mods.toml` — authors и
  credits; ассеты перечислены в `mods-src/galacticraft-revival/CREDITS.md`.

## 2. Что уже есть на 1.18–1.20
- **Официального GC для 1.20.1 нет.** Legacy — только 1.12.2. GC5 — пре-альфа, только Fabric 1.21.1, сборки лишь
  в GitHub Actions; Forge — «после играбельной альфы для Fabric», сроков нет.
- Неофициальные порты/форки: на Forge 1.18–1.20 не найдено. Есть форк GC5 «Boobcat Edition» — тоже 1.21.1 Fabric.
  Запуск GC5 через Sinytra Connector невозможен (Connector не поддерживает 1.21.1 → 1.20.1, это разные версии игры).
- Моды «в духе GC» под 1.20.1 Forge, которых нет в сборке (Modrinth):
  | Мод | Что даёт | Оценка |
  |---|---|---|
  | `ad-astra-more-structures` 1.0.2 (ARR, 2 класса + датапак) | по данжу с «боссом» на Луне, Марсе, Венере, Меркурии, Глацио | Боссы — ванильные мобы с NBT, лут — стаки ванильных ресурсов; на GC не похоже. Можно как дополнительный контент, но не замена |
  | `ad-astra-rocketed` (ARR) | ракеты T5–T7 Ad Astra | Не нужен: ракеты Ad Astra у нас убраны |
  | `planetsplus` (MIT) | новые планеты/системы Ad Astra | Вне зоны GC; добавлять планеты — решение отдельное |
  | `beyond-oxygen` (GPL) | герметизация отсеков кораблей Valkyrien Skies | Полезная связка для VS, но это не GC; передать в поток 3 |
  | `ad-astra-canned-food` | консервы в духе GC | Мелочь, можно сделать сами |
  | `gcyr` | космос для GregTech | Не подходит (GregTech нет) |

## 3. Что из GC покрывает сборка
Сборка: Ad Astra 1.15.21, Northstar Redux, Creating Space, Mekanism (+ Giselle Addon, Create).

| Возможность GC | Чем закрыто в сборке | Чего не хватает |
|---|---|---|
| Ракеты T1–T3 (+T4 в аддонах), верстак NASA | Ракеты строятся из блоков (Northstar, Creating Space), ограничены тягой/Δv; простые ракеты Ad Astra убраны | **Прогрессия по схемам**: в GC следующий тир открывает схема из сундука босса |
| Кислород: сборщик (из листвы), компрессор/декомпрессор баллонов, маска, снаряжение | Ad Astra: загрузчик кислорода, скафандры; Northstar: электролиз, сепаратор; Mekanism; баллоны Northstar/CS через `mm-compat` | Сборщик кислорода от растений, баллоны GC (лёгкий/средний/тяжёлый) — по вкусу |
| Герметизатор, распределитель-«пузырь», датчик, шлюз | Ad Astra: Oxygen Distributor (заполняет замкнутое помещение), Oxygen Sensor, Airlock; CS: Oxygen Sealer | Сферический «пузырь» без стен (GC Bubble Distributor) — нет |
| Луноход (Moon Buggy), посадочный модуль, парашют, парасундук | Ad Astra: Tier 1 Rover, Lander | Парашют/парасундук; схема лунохода |
| Космическая станция | Орбиты Ad Astra — общие измерения, можно строить | Личная станция «по кнопке» из материалов (в Ad Astra она создаётся из экрана ракеты Ad Astra, а их мы убрали) |
| **Данжи с боссами, ключи, сундуки со схемами** | Ad Astra `moon_dungeon` — только спаунеры (Corrupted Lunarian, Star Crawler) и обычные сундуки; Northstar — лунная/марсианская базы без боссов | **Нет совсем**: Лунный босс-скелет, Марсианский крипер-босс, Венерианская паучиха, ключи T1–T3, сундуки сокровищ со схемами |
| Марс / Астероиды / Венера | Марс, Венера, Меркурий, Глацио, Луна, орбиты — Ad Astra (+ рельеф/мобы Northstar) | **Астероиды** (пояс + астро-майнер), мобы GC (слаймлинг, стражи/«эволюционировавшие» мобы в масках) |
| Метеориты | Ad Astra: статичные структуры `meteor1..6` | Падающие метеориты на Луне, метеоритное железо |
| Солнечные панели, генераторы, провода | Ad Astra Solar Panel, Coal Generator; Mekanism | Ничего существенного |
| Транспортёр / телепад (Asteroids), грузовые ракеты, пусковая площадка с автозапуском | Mekanism Teleporter; Northstar Auto-Landing Station | Грузовые ракеты GC, телепад — низкий приоритет |
| Консервы, криокамера, флаги | Ad Astra флаги | Консервирование, криокамера (сон в космосе) |

Итог: техника и окружение в целом есть; **не хватает «приключенческой» половины GC** — данжи с боссами,
ключи и схемы как прогрессия, астероиды, падающие метеориты, мобы GC.

## 4. Варианты
| Вариант | Суть | Объём | Качество |
|---|---|---|---|
| A. Полный порт Legacy | 1.12.2 → 1.20.1: ~213 тыс. строк java; меняются блоки (flattening), рендер, сеть, генерация, capabilities. Свои планеты/кислород/ракеты GC пришлось бы вырезать (Ad Astra — единственный источник планет) | Огромный (месяцы) | Половина кода выбрасывается как дубль Ad Astra |
| B. Порт GC5 | 1.21.1 Fabric → 1.20.1 Forge, ~104 тыс. строк, Mojang-маппинги (близко к нам) | Большой | Пре-альфа: босс без лута/ключа, Марса/Венеры-данжей нет; свои планеты опять дублируют Ad Astra |
| C. Связка готовых модов | `ad-astra-more-structures` и т.п. | Минимальный | На GC не похоже, прогрессии нет |
| **D. Свой мод поверх Ad Astra, порт по частям (рекомендация)** | Берём из GC ровно недостающее (таблица выше). Каркас и модели — из **GC5** (код уже на современном API, Mojang-имена, переносится почти построчно); поведение и недостающее — из **Legacy** (там всё доделано: лут босса, ключи, сундук сокровищ, схемы, марсианский/венерианский данжи, слаймлинг, астероиды). Ассеты — из обоих. Планеты, кислород, гравитация — Ad Astra/Northstar | Средний, по срезам | Ближе всего к GC и без дублей |

**Рекомендация — D.** Мод `mods-src/galacticraft-revival` (mod id `gcrevival`), без собственных планет.

Порядок срезов:
1. **Луна:** процедурный данж GC (коридоры, комнаты со спаунерами, комната босса, комната сокровищ) в
   `ad_astra:moon`, Skeleton Boss, спаунер босса, ключ T1, сундук сокровищ, схема T2. ← вертикальный срез.
2. **Схемы как прогрессия:** схема T2 открывает компоненты ракет для Марса (Northstar/CS — рецепты из данных
   мода), T3 — для Венеры/Меркурия. Точный выбор деталей — после среза, с учётом Δv.
3. **Марс:** данж GC + Creeper Boss, ключ T2, схема T3; слаймлинг.
4. **Венера:** данж + Spider Queen, ключ T3.
5. **Падающие метеориты** на Луне, метеоритное железо; «эволюционировавшие» мобы в масках на планетах.
6. **Астероиды:** новая «планета» в данных Ad Astra (не своё измерение-дубль) + генерация астероидов GC;
   астро-майнер — по желанию.
7. Мелочи: парашют/парасундук, пузырь-распределитель, консервы, криокамера.

## 5. План этапа 1 (срез «Данж Луны с боссом»)
- [ ] Каркас `mods-src/galacticraft-revival` (как `wizardry-restored`: ModDevGradle legacyforge + Parchment),
      `CREDITS.md`, лицензии в `META-INF`.
- [ ] Блоки: кирпич лунного данжа (+ступени/плиты — по Legacy/GC5), спаунер босса (BossSpawner), сундук сокровищ T1.
- [ ] Предметы: ключ T1, схема T2 (пока без рецептов), лут-таблицы сокровищ по Legacy `dungeon_tier_1.json`.
- [ ] Сущность Skeleton Boss (GC5 + доделки из Legacy: здоровье 150×множитель, ключ при смерти, полоса босса,
      бросок игрока), модель и текстура, звуки.
- [ ] Генерация: структура данжа GC5 (`structure/dungeon/*`) в биомах Луны Ad Astra.
- [ ] Серверный тест `scripts/tests/gcdungeon.js`: найти/поставить данж командой, призвать босса, убить, проверить
      ключ → сундук → схема. Прогон на копии сервера (`scripts/test-server.sh` + jar вручную), без правки `pack/`.
- [ ] Итог среза — вопрос пользователю о включении в сборку.

## Журнал
- 2026-10-10 — этап 0: лицензии, обзор, сравнение, рекомендация D.
