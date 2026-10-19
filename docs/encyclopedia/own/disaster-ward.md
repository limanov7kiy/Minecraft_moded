# Оберег от катаклизмов (`disaster-ward`)

Наш мод (`mods-src/disaster-ward`, версия 1.2.0, лицензия MIT, сторона `both`). Один блок — **«Оберег от катаклизмов»** (`disaster_ward:ward`), который
создаёт зону, где не бушуют бури, не случаются бедствия, не растёт ориджиниум и не распространяется заражение паразитами.
Карточка мода в [«Мир: выживание, погода и катастрофы»](../world.md) ведётся отдельно; здесь — подробный разбор. Подробные замеры и тесты — [compat.md](../../compat.md)
(разделы «Катаклизмы: Weather2 + CoroUtil, Natural Disasters!…», «Паразиты…», «Ориджиниумная буря Bob's Originium Dream + оберег 1.2.0»).

Вернуться: [Свои моды и патчи](../own.md) · [оглавление справочника](../INDEX.md).

## Как играть
1. Блок есть **только в креативе** (вкладка «Функциональные блоки»/«Операторские») или `/give @s disaster_ward:ward`. Рецепта и лута нет; при ломании ничего не выпадает. Ставится и снимается кем угодно; взрывоустойчив, светит на 7.
2. Зона — **квадрат по X/Z на всю высоту** вокруг блока, радиус `ward.radius` = **96** блоков (в конфиге сборки; допустимо 8…512), плюс запас у края `edgeMargin` = 16.
3. Внутри зоны (и в полосе запаса):
   - бури Weather2 (стадия «формируется» и выше) разворачиваются, когда их центр подходит ближе `radius + edgeMargin`;
   - бедствия Natural Disasters! не появляются, а зашедшие — убираются; уже сделанное (вода цунами, лава) остаётся;
   - бедствия и паразиты **снаружи не меняют блоки внутри** (`protectBlocks`: торнадо, метеоры, землетрясение, цунами, пожар, заражение Spore); взрывы из «контекста бедствия» не разрушают блоки зоны;
   - паразиты (Fungal Infection: Spore, SRP System) не появляются, в полосе запаса их отталкивает наружу, внутри — убираются; эффекты заражения не накладываются и снимаются (`cureInZone`);
   - буря Bob's Originium Dream: её призывы (черви, огненные шары, молнии, падающие блоки) в зоне отменяются, шип бури не ставит блоки; ориджиниум не растёт.
4. Позиции хранятся в данных мира (`data/disaster_ward.dat`) и переживают перезапуск. Ограничения: вода цунами и огонь пожара, поставленные снаружи, могут зайти по ванильным правилам; песок песчаной бури Weather2 и падающие блоки землетрясения, уже упавшие в зону, не защищены.

## Предметы/блоки
`disaster_ward:ward` — «Оберег от катаклизмов». Подсказка: «Только креатив. В радиусе N блоков не бушуют бури и бедствия, не распространяется заражение паразитами…».

## События
Мод сам управляет случайными бедствиями Natural Disasters! (в самом моде бросок раз в ~5 секунд на игрока — непригодно): при первом запуске мира включает галку «случайные бедствия» и делает паузу между бросками **36000–96000 тиков (30–80 мин) на игрока**; торнадо и песчаная буря (дубли Weather2) отключены везде. Одновременно идёт не больше одного бедствия на измерение (`maxConcurrent = 1`; замер: три сразу над городом — ~200 мс/тик, ~5 TPS).
Ориджиниумную бурю Bob's Originium Dream мод запускает случайно: раз в 10 минут игрового времени в Overworld бросок с шансом 5,5 % (в среднем раз в ~3 часа + 10 минут предупреждения); свой счётчик опыта в Bob's поднят до 100000. Подробности и числа — [bobs-originium-dream](../arknights.md#bobs-originium-dream) и [compat.md](../../compat.md).

## Команды/гамерулы
Своих команд нет. Полезно: `/give @s disaster_ward:ward`; `/gamerule oristorm false` — выключить бури Bob's; `/gamerule alloworigrow false` — запретить рост ориджиниума. Теги датапака: `disaster_ward:infection_immune` (40 видов + `forge:bosses` — не заражаются нигде), `spore_to_licker` (животные → `spore:licker`), `spore_to_claw` (монстры → `spore:claw`).

## Конфиги
`pack/config/disaster_ward-common.toml` — всё с комментариями на русском:
| Раздел | Что настраивается |
|---|---|
| `[ward]` | `radius` 96; `edgeMargin` 16; `protectBlocks` true; `steerStorms` true; `protectedNamespaces = ["natural_disasters"]`; `disabledEntities` (торнадо и песчаная буря ND); `maxConcurrent` 1; `rootDisasters` (какие существа считаются самим бедствием) |
| `[parasites]` | `namespaces = ["spore"]`; `infectionEffects` (`spore:mycelium_ef`, `srpsystem:infection`); `cureInZone`; `pushParasites`; `immuneBosses` (боссы и существа из тега не получают заражение) |
| `[naturalDisasters]` | `enabled`; `minTicks` 36000; `maxTicks` 96000 |
| `[oriStorm]` | `protect`; `stormEntities`; `stormBlocks`; `roll` true; `rollTicks` 12000; `rollChance` 0.055; `dimensions = ["minecraft:overworld"]`; `fixStuckStart` (снять «зависший» запуск бури после выключения сервера); `skipUnloaded` (пропускать призывы в незагруженных чанках — иначе сервер тормозит до 2 с на тик) |
| `[oriGrowth]` | `protect`; `growChance` 0.25 (шаг роста раз в ~8 мин вместо 2); `coreChance` 0.1 (ядро раз в ~11 мин вместо 68 с) |
Игровые правила Bob's, которые один раз на мир выставляет KubeJS (`pack/kubejs/server_scripts/oristorm_rules.js`): `oristorm=true`, `oristormfrequency=100000`, `oristormsummon=200`, `alloworigrow=true`, `oribreak=false`. Параметры Weather2 — `pack/config/Weather2/Tornado.toml`, `Storm.toml`, `Misc.toml`; заражение Spore — `pack/config/sporeconfig.toml`; SRP System — `pack/config/srpsystem-common.toml`.

Интеграция идёт без зависимостей: Spore, SRP System и Weather2 узнаются по пространству имён и id эффектов (Weather2 — рефлексией), без них оберег работает и не падает. Тесты: `scripts/tests/disasterward.js`, `sporeward.js`, `oristormward.js`.

**Откат:** убрать jar `pack/mods/disaster-ward-*.jar` и конфиг — все ограничения исчезнут; блоки оберега в мире пропадут как неизвестные.
