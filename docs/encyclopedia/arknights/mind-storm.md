# «Мысленная буря» (Thought Turbulence) — Sarkaz's Furnace Side Fables

| | |
|---|---|
| Мод | **Sarkaz's Furnace Side Fables** (`sarkazs_furnace_side_fables`), автор **Zhi_Xu**, лицензия All Rights Reserved |
| Файл в сборке | `pack/mods/sarkazs_furnace_side_fables-0.2.3-forge-1.20.1.jar` (CurseForge, без стороннего скачивания) |
| Эффект | `sarkazs_furnace_side_fables:thought_load` — en «Thought Turbulence», zh «思绪混乱», ru (наш перевод) «Мысленная буря» |
| Связанный эффект | `sarkazs_furnace_side_fables:thought_load_blocked` — en «Thought Blocked», ru «Мысли заблокированы» |
| Механика | «Нагрузка мысли» (Thought Load) — сумма нагрузок мыслей в рюкзаке мыслей |
| Сторона | `both` (логика на сервере, HUD и рюкзак — клиент) |

Разбор — по декомпиляции jar (CFR 0.152) и проверке на тестовом сервере (поток 48, 2026-10-19). Названия классов ниже —
из пакета `com.zhixu.themassestravels` внутри jar. Код мода не копировался в репозиторий, описано только поведение.

Не путать с **«Ориджиниумной бурей»** (`bobsoriginiumdream:oristormeffect`, мод Bob's Originium Dream) — это погодное бедствие,
от него защищает наш Оберег. Других эффектов со словом «буря»/storm у Arknights-модов сборки нет.

## Коротко
«Мысленная буря» — не самостоятельный дебаф, а **индикатор перегрузки**: сервер ставит его игроку каждый тик, пока нагрузка
мыслей ≥ предела (по умолчанию 18). Сам эффект ничего не делает. Вред наносит проверка нагрузки:
**каждые 12 блоков, пройденных по горизонтали, игрок теряет 1 «целевую жизнь»** (очко жизни Caerula Arbor), но не ниже 1.
Здоровье (сердца) не отнимается, убить буря не может. При нагрузке ≥ 1,5 × предела (27) вместо бури — «Мысли заблокированы»:
игрок полностью обездвижен.

## Откуда берётся нагрузка
- **Мысли** (`sarkazs_furnace_side_fables:thought_*`, 44 предмета) — выпадают при убийстве игроком:
  обычные монстры, морские мобы Caerula Arbor и существа Bob's Originium Dream — 5 %; элита — 7,99 %; саркасы (тег
  `sarkazs_furnace_side_fables:sarkaz`) — 15 %; боссы (`forge:bosses`) — 1 гарантированно + 50 % вторая. Чары «Расхититель мыслей»
  +1 % за уровень (`ThoughtDropService`). Ещё мысли даёт «Очищение истины» (Refine Truth).
- Подобранная мысль **сама уходит в рюкзак мыслей** (45 слотов, хранится в NBT игрока `TheMassesTravelsThoughtBackpack`), в обычный
  инвентарь её не положить (`ThoughtInventoryEventHandler`). Рюкзак сохраняется после смерти.
- У каждой мысли своя нагрузка 1–5 (видно в подсказке «Нагрузка: N»), считается **за каждый предмет в стаке**.
  Например, «Отчаяние» (`thought_despair`) — 5. Мысли-«замыслы» (Conception) получают ещё надбавку от «эпохи» (`EraService`).
- Итог: `нагрузка = Σ(нагрузка мысли × количество)` (`ThoughtLoadService.getCurrentLoad`). Четыре мысли по 5 уже дают бурю.

HUD «Мысль N» / «Нагрузка мысли N/M» — эта шкала. Настройка HUD — клавиша **Z**, рюкзак мыслей — клавиша **L**
(по умолчанию совпадает с ванильными «Достижениями» — переназначьте в «Управление → Sarkaz's Furnace Side Fables»).

## Пороги (`ThoughtLoadService.resolveState`)
| Состояние | Условие | Что происходит |
|---|---|---|
| Спокойно (`CLEAR`) | нагрузка < предела | ничего |
| **Мысленная буря** (`TURBULENT`) | предел ≤ нагрузка < ⌊1,5 × предела⌋ | эффект `thought_load` (40 тиков, обновляется каждый тик, без частиц); потеря целевых жизней при ходьбе |
| **Мысли заблокированы** (`BLOCKED`) | нагрузка ≥ ⌊1,5 × предела⌋ | эффект `thought_load_blocked`: скорость ×0 (модификатор `MULTIPLY_TOTAL −1`), каждый тик скорость обнуляется; достижение `story/thought_blocked` |

Предел (`getFinalLoadLimit`) = **18** + бонус «воображаемых сущностей» + 3 за каждый уровень чар «Высвобождение мыслей» на броне
+ атрибут `sarkazs_furnace_side_fables:thought_load_limit`. При пределе 18 буря начинается с 18, блокировка — с 27.
Уровней у эффекта нет: мод всегда ставит уровень 0 (I).

## Что делает буря (`EffectEventHandler.applyTurbulentMovementLifeDrain`)
- Считается только горизонтальное смещение (X/Z) за тик; шаги больше 3 блоков за тик (телепорт) не считаются. Езда верхом/в лодке
  тоже двигает игрока — тоже считается.
- Накопилось 12 блоков → `VitalsService.tryGiveLife(player, −1, "thought_movement")`. В нашей сборке есть Caerula Arbor, поэтому
  списываются **целевые жизни Caerula Arbor** (`CaerulaUtil.PlayerData.giveLifePoint`; «Целевые жизни» в левом нижнем углу).
  При активном эхо Caerula Arbor «Эхо: Веретено» (Echo Spindling) потеря ×2, на 3-й стадии стратегии роста ×3.
- Ниже **1** целевая жизнь не опускается (`ChangePlayerLPOrShieldProcedure`: `max(после, 1)`), щит не тратится.
- Последствие: целевые жизни спасают от смерти (правило `targetLifeFunction`), поэтому после бури следующая смерть может стать
  настоящей. Сама буря урона не наносит, галлюцинаций, тошноты и прочих эффектов нет.
- На мобов не действует (только игроки). «Эмпатия» мода эффект бури и блокировки другим существам не передаёт
  (`EmpathyEffectShareRules.isThoughtLoadStatus`).

## Длительность
Пока нагрузка ≥ предела — бесконечно: эффект на 2 с, но перевешивается каждый тик. Как только нагрузка ниже предела, мод
перестаёт его ставить и он спадает за ≤ 2 с (явно мод снимает его только при полном комплекте «Финала»).

## Как снять и защититься
- **Выбросить лишние мысли**: рюкзак мыслей (L) → взять мысль → кнопка «Выбросить» (ЛКМ — весь стак, ПКМ — одну).
  Мысли «Вдохновения» можно «Использовать» — они расходуются.
- **Поднять предел**: чары «Высвобождение мыслей» на броне (+3 за уровень); «Проклятая история битв» (+2, растёт от убийств);
  броня «Связанного теневого духа»; бонусы «воображаемых сущностей».
- **Полный комплект «Финал»** (Смысл продолжения / Растерянные мысли / Эхо отчаяния / Отголоски судного дня) — штрафы мыслей
  подавлены полностью (`FinaleArmorService.suppressesThoughtPenalty`), плюс нагрузка «Отчаяния», «Спасения», «Прекращения» = 0.
- **Полный комплект «Учжоу»** — нагрузка каждой мысли −1 (не ниже 1), замыслов −2.
- **Молоко и `/effect clear`** не помогают: эффект вернётся на следующем тике, а потеря жизней от эффекта не зависит.
- **Наш Оберег (`disaster_ward`) не защищает**: он снимает только эффекты из `infectionEffects` (Spore, SRP), а буря — состояние
  нагрузки, а не внешнее заражение. Добавлять её в список бессмысленно по той же причине, что и молоко.
- **Расы Arknights Races не помогают**: их силы «Thought Load +N» дают атрибут старого мода `the_massestravels:thought_load_limit`,
  в сборке это пустой атрибут из `mm-compat`, а силы заменены заглушкой (см. `docs/compat.md`). Предел Sarkaz они не меняют.
- Правило `/gamerule targetLifeFunction false` отключает целевые жизни Caerula Arbor целиком — тогда и терять нечего,
  но пропадает и защита от смерти (решение владельца сервера, не рекомендуется ради одной бури).
- Конфига, отключающего бурю, у мода нет (`ModCommonConfig` настраивает только «Эмпатию» и прочее).

## Команды (OP)
```
/effect give @s sarkazs_furnace_side_fables:thought_load 30 0          # значок «Мысленная буря» на 30 с — только иконка, без последствий
/effect give @s sarkazs_furnace_side_fables:thought_load_blocked 10 0  # 10 с полной неподвижности (скорость ×0)
/effect clear @s sarkazs_furnace_side_fables:thought_load
/effect clear @s sarkazs_furnace_side_fables:thought_load_blocked

# настоящая буря — набрать нагрузку мыслями (4 × «Отчаяние» = 20 ≥ 18):
/give @s sarkazs_furnace_side_fables:thought_despair 4
# поднять предел на время теста (обычный ванильный атрибут):
/attribute @s sarkazs_furnace_side_fables:thought_load_limit base set 20
/attribute @s sarkazs_furnace_side_fables:thought_load_limit base set 0   # вернуть
```
Через `/effect give` вызывается **только иконка** бури: потеря жизней завязана на нагрузку, а не на эффект. «Мысли заблокированы»
через `/effect give` действительно обездвиживают (модификатор скорости висит на самом эффекте), но без обнуления импульса.

## Безопасно ли тестировать
- Убить не может, урона нет. Мир и сохранение не ломает: всё хранится в NBT игрока (`TheMassesTravelsThoughtBackpack`,
  `TheMassesTravelsThoughtTurbulence*`) и в его возможностях Caerula Arbor.
- Цена теста настоящей бурей — потерянные целевые жизни (до 1). Вернуть: еда/реликвии Caerula Arbor или OP-командой Caerula Arbor.
- Блокировку снимайте выбрасыванием мыслей — в ней нельзя сдвинуться, но рюкзак (L) открывается.

## Проверка на тестовом сервере
Одноразовый KubeJS-скрипт (в репозиторий не добавлялся): `ServerPlayer` с фейковым соединением, мысли кладутся
через `ThoughtBackpackService.insertThought`, тики — `LivingTickEvent` через `EVENT_BUS`, ходьба — `setPos` шагами по 1 блоку;
целевые жизни Caerula Arbor выставлены 6/6. Сервер сборки до `Done`, 2026-10-19:

| Случай | Результат |
|---|---|
| Регистрация, разбор команд | `thought_load`, `thought_load_blocked` есть; `/effect give`, `/effect clear` и `/attribute … thought_load_limit` разбираются без ошибок |
| 4 × «Отчаяние» | нагрузка 20/18, `TURBULENT`, эффект бури есть, скорость 0,1 (без изменений), HP 20 |
| 36 блоков пешком | целевые жизни 6 → **3** (по 1 за 12 блоков), HP 20 |
| ещё 120 блоков | целевые жизни **1** (ниже не опускаются), HP 20 |
| молоко (`removeAllEffects`) | эффект снят → **на следующем тике снова есть** |
| 6 × «Отчаяние» (30) | `BLOCKED` (порог 27): эффект блокировки, скорость **0**, импульс (0, 0, 0) |
| `/effect give thought_load` при пустом рюкзаке, 40 блоков | значок есть, целевые жизни 6/6 — **без последствий** |
| `/effect give thought_load_blocked` | скорость 0 |
| атрибут `thought_load_limit` = 10 при нагрузке 20 | предел 28, блокировка 42, `CLEAR` |

## Источник
- `ModMobEffects` — регистрация `thought_load` (класс `ThoughtTurbulentMobEffect`, вредный, цвет `#D4B64D`) и
  `thought_load_blocked` (`ThoughtBlockedMobEffect`, модификатор скорости `3b1bf8e6-…`).
- `common/event/EffectEventHandler` — `applyThoughtLoadState`, `applyTurbulentMovementLifeDrain` (12 блоков, шаг ≤ 3).
- `content/empathy/common/thought/ThoughtLoadService` — предел 18, `+3` за «Высвобождение мыслей», блокировка ×1,5.
- `content/thought/common/service/ThoughtBackpackService`, `ThoughtDropService`, `common/event/ThoughtInventoryEventHandler`.
- `content/vitals/common/VitalsService`, `compat/caerulaarbor/CaerulaArborLifeCompat`;
  Caerula Arbor 0.12.7.2: `CaerulaUtil$PlayerData`, `procedures/ChangePlayerLPOrShieldProcedure`, `LifePointFuncProcedure`.
- Строки: `assets/sarkazs_furnace_side_fables/lang/{en_us,zh_cn}.json`, наш перевод —
  `pack/resourcepacks-src/russian-moded/assets/sarkazs_furnace_side_fables/lang/ru_ru.json`.
