# Совместимость: журнал

Связка: **Minecraft 1.20.1 + Forge 47.4.26** (Java 17 или 21).
Проверка — `scripts/test-server.sh <папка>`: настоящий запуск выделенного сервера со всеми серверными модами.

## Решения по запрошенным модам

| Мод | Состояние на 1.20.1 Forge | Что сделали |
|---|---|---|
| Electroblob's Wizardry | Оригинал есть только для 1.12.2. На 1.20.1 есть **Electroblob's Wizardry Redux** — пересоздание с нуля (GPL-3.0), статус alpha, заклинания ещё добавляются | Взяли Redux |
| Lycanites Mobs | Официальная версия для 1.20.1 вышла 2026-08-01 (LGPL-3.0) | Взяли официальную |
| Galacticraft | Galacticraft Legacy — только 1.12.2. Galacticraft 5 — пре-альфа, только Fabric 1.21.1, без релиза. Официального порта на 1.20.1 Forge нет | Пока в сборке Ad Astra + Northstar Redux + Creating Space (ракеты, кислород, планеты). Порт Galacticraft — отдельный большой проект, см. ниже |
| Create: Northstar | Оригинал заброшен в 2024 г., собран под Create 0.5.1 и **роняет сервер** с Create 6 (`NoClassDefFoundError: MovementBehaviour`) | Заменили на **Northstar Redux** (MIT, поддерживается) + его дополнения |

## Найдено тестовыми запусками

1. Iron's Spells требует `playeranimator` и `irons_lib` (>=1.20.1-2, <1.20.1-3), Origins (apoli) — `caelus`,
   Originium Arts — `ldlib`. На Modrinth эти зависимости не объявлены — добавлены вручную.
2. Create: Northstar (оригинал) несовместим с Create 6 — заменён (см. выше).
3. Origin Furs (unofficial port) на выделенном сервере падает: в общей инициализации обращается к
   клиентскому классу `GeoObjectRenderer` из AzureLib. Это чисто визуальный мод (шерсть/уши origins),
   поэтому `origin-furs-unofficial-forge-port` и `arknights-furs` вручную помечены
   `side = "client"` (AzureLib остаётся на обеих сторонах — он нужен Cataclysm Spellbooks на сервере). **После `packwiz update` проверить, что пометка не слетела.**
   **Поток 5:** оба мода **убраны** — Origin Furs собран под AzureLib 2.x, а в сборке AzureLib 3.1 (см. «Проверка клиента», п. К3).
   **Поток 4b:** возвращены — Origin Furs портом `pack/mods/origin_visuals-1.0.5-mmport-client.jar` (К3); суффикс
   `-client.jar` — `fetch-mods.py` не кладёт такой jar на сервер (замена пометки `side = "client"`).
4. Lycanites Mobs 0.1.0-alpha: в jar нет `lycanitesmobs.refmap.json`, хотя конфиг миксинов на него ссылается —
   миксины к ванильным классам (`StructureAccessor`, `ReloadCommandMixin`, клиентский `LightTextureMixin`) в
   настоящей игре не применялись (`/reload` не перечитывал данные Lycanites, на клиенте ломался эффект освещения).
   **Патч:** `patches/lycanites-refmap` — маленькая библиотека (GAMELIBRARY) с этим refmap (имена → SRG из
   маппингов 1.20.1), сам мод (120 МБ) не трогаем. Проверено: предупреждение `Mixin apply failed` исчезло,
   миксины применяются. Когда автор выпустит исправленный jar — убрать `pack/mods/lycanites-refmap-fix-*.jar`.
5. Create: Northstar - Structures 1.0.0 (MIT) — совместимой версии под Forge 1.20.1 нет (на 2026-10-10 последний
   Forge-релиз 1.0.0). Аддон переопределяет 7 лунных биомов Northstar копиями под старый Northstar:
   фичи `northstar:moon_<камень>_blob` в Redux 0.6 переименованы в `moon_blob_<камень>` — мир не создавался
   (`Unbound values in registry ... placed_feature`). **Патч:** `patches/northstar_structures_fix.py <папка с jar>`
   собирает биомы заново (основа — биом Redux: спавны, эффекты; фичи — аддона, имена переведены; добавлены
   фичи Redux, которых у аддона нет, кроме старых `moon_crater*`) и разрешает кратеры на Луне Ad Astra
   (`ad_astra:lunar_wastelands`, дно/вал — песок и камень Луны Ad Astra) — туда летают ракеты сборки.
   → `pack/kubejs/data/northstar/worldgen/biome`, `pack/kubejs/data/northstar_structures`. Мод возвращён.
   Проверено (`scripts/tests/northstar_structures_commands.txt`): мир создаётся, `locate structure` находит
   малый и старый большой кратер на `ad_astra:moon` и кратер на `northstar:moon`, ошибок генерации нет.
   После обновления аддона или Northstar перезапустить скрипт (или удалить патч, если автор исправил).
6. Canary × Valkyrien Skies: миксин Canary `ai.poi.PoiManagerMixin` перезаписывает
   `PoiManager.getInRange`, после чего миксин VS `feature.poi.MixinPOIManager` не может встроиться —
   сервер падает при создании мира. Фикс: `pack/config/canary.properties` → `mixin.ai.poi=false`
   (отключена одна оптимизация, остальные работают).
7. Iron's Spells 1.20.1-3.16.3 поставляет таблицы добычи `chests/catacombs/crypt_loot` и
   `chests/citadel/citadel_tomes` в формате 1.21 (`"value"` вместо `"name"`, функция
   `set_written_book_pages`) — на 1.20.1 они не загружаются, сундуки пустые. **Патч:**
   `patches/irons_loot_1_20_1.py` переписывает их в формат 1.20.1 → `pack/kubejs/data/...`
   (KubeJS перекрывает данные модов). Проверено командой `/loot spawn`: книги и добыча выпадают.
   После обновления Iron's Spells перезапустить скрипт или удалить патч, если автор исправил.
8. Serene Seasons Plus требует библиотеку GabouLibs, которую автор удалил отовсюду (ни Modrinth, ни
   CurseForge). **Патч:** своя замена `patches/gaboulibs-shim` — 7 классов с тем же API (снежные бури,
   защита от двойной инициализации, совместимость с Project Atmosphere), восстановлены по декомпиляции
   SS+. Собирается `patches/gaboulibs-shim/build.sh <тестовый сервер>` → `pack/mods/gaboulibs-shim-1.0.0.jar`
   (FMLModType: GAMELIBRARY). Включено накопление снега (`pack/config/sereneseasonsplus-common.toml`).
   Проверено: зима + дождь → буря записана в `ssp_snow_history.dat`.
   Замечание: первая смена сезона командой `/season set` после запуска сервера не шлёт событие
   (особенность Serene Seasons) — при естественной смене сезонов не мешает.
9. Шум в логе сервера — разобран (поток 4), проверено `scripts/tests/noisecheck.js`:
   - **Теги со ссылками на отсутствующие предметы выбрасывались целиком** (это не шум, а потеря данных):
     `farmersdelight:snacks/sweets`, `diet:sugars` (мороженое и торты Twilight Delight, которые мод не
     регистрирует), `aether_redux:swet_jelly` (Aether Genesis), `forge:nuggets/reinforced_copper`,
     `aether_villages:collections/ancient_aether_biomes`. `pack/kubejs/server_scripts/missing_mod_refs.js`
     убирает только недостающие ссылки; список генерирует `scripts/missing_tag_refs.py <latest.log>` —
     после обновления модов прогнать сервер и запустить его снова. Теперь в тегах 20/18/25/3 предмета.
   - Рецепты для отсутствующих модов (результат пустой): `ancient_aether:ancient_rune`,
     `aether_redux:genesis_dark_gummy_swet`, три шаблона `aether_treasure_reforging`, плюс опечатка Cataclysm
     `azure_seastone_brick_stair_from_stonecutting` (дубль правильного `..._stairs_...`) — отключены
     условием `forge:false` в `pack/kubejs/data/*/recipes`.
   - `cataclysm_spellbooks:extended_(death_)laser_beam has no attributes` (368 строк): мод регистрирует лучи
     с категорией `MONSTER` — они ещё и занимали лимит враждебных мобов. `mods-src/mm-compat` 0.2.0 ставит
     им `MISC` → 0 строк.
   - Силы `arks:*/thought` рас Arknights (в оригинале — атрибут `the_massestravels:thought_load_limit` из закрытого мода
     The Masses Travels; его преемник — Sarkaz's Furnace Side Fables, пакет `com.zhixu.themassestravels`):
     переопределены в `pack/kubejs/data/arks/powers/{human,sarkaz,sarkaz_nephesh}/thought.json` (`loading_priority` 10) —
     дают `sarkazs_furnace_side_fables:thought_load_limit` (+7 / +7 / +17), то есть поднимают предел «нагрузки мысли»
     Sarkaz (`ThoughtLoadService.getFinalLoadLimit` = 18 + атрибут + …; см. `docs/encyclopedia/arknights/mind-storm.md`).
     Модификатор ставит и снимает Origins вместе с силой, двойного учёта нет. Пустой атрибут `the_massestravels:*` из
     `mm-compat` остаётся (безвредно, на него больше ничто не ссылается). Поток 48, 2026-10-19, `scripts/tests/racethought.js`:
     без расы предел 18; `arks:human` 25; `arks:sarkaz_lich` (сила `arks:sarkaz/thought`) 25; `arks:sarkaz_nephesh` 35;
     после смены расы на `origins:empty` — 18; `arks:feline` (без силы) 18. Ошибок загрузки сил в логе нет.
   - Остаётся (безвредно): KubeJS не разбирает `deeperdarker:resonarium_*_smithing` (шаблон намеренно пустой)
     и `northstar:electrolysis/*` — рецепты загружает ванилла, они работают; попытки модов загрузить
     клиентские классы на сервере (`RuntimeDistCleaner`); внутренний миксин Valkyrien Skies
     `MixinVibrationSystemTicker`; Lycanites пишет «getEntityType() registry lookup failed» для 9 существ
     (alpha-версия мода).
10. После падения загрузки сторонние потоки держат JVM живой, сервер «висит» без вывода.
   `test-server.sh` теперь это распознаёт.
11. Legendary Survival Overhaul × Serene Seasons (+ Serene Seasons Plus): связка встроена в сам LSO
   (`[temperature.integration.serene-seasons]`, включена по умолчанию), патч не нужен. Проверено
   `scripts/tests/seasontemp.js` + `season_commands.txt`: у точки появления (тёмный лес, полдень) вклад
   сезона в температуру мира — середина лета +8, осени +1.5, зимы −9.5, весны −1.5 (итог 22 / 15.5 / 4.5 / 12.5).
   Serene Seasons Plus температуру не трогает (только снег/бури и длина дня).
12. `.gitignore` с правилом `world*/` скрывал все папки `worldgen/`: размещения руд из `patches/ore_crossgen.py`
   (`pack/kubejs/data/mm_space/worldgen`) не попали в git, и на чистой копии мир не создавался
   (`Unbound values ... mm_space:*`). Правило сделано корневым (`/world*/`), данные добавлены.
13. **Битый мод-файл выключает все моды.** Zombie Have Ears 1.1.2 (дополнение к голосовому чату) объявляет
   зависимость без поля `mandatory` (формат NeoForge) — Forge 47 пишет «not a valid mod file», «Mods loaded
   with 1 warnings» и запускает сервер **без единого мода** (а `Done` в логе есть). Исправлено перепаковкой (п. 14);
   `test-server.sh` теперь считает предупреждения загрузки модов провалом.

14. **Порты чужих jar под наши версии зависимостей** (поток 4, по задачам потока 3; правка ARR-модов разрешена —
   см. CLAUDE.md, «Назначение и лицензии»; авторство указано в `source.txt` и переходниках).
   Инструменты: `scripts/linkcheck/linkcheck.sh <jar> <тестовый сервер>` — без запуска показывает, какие классы,
   методы и поля других модов jar использует, а их нет (в т.ч. в лямбдах и унаследованных методах; классы
   клиента и методы, которые Forge добавляет в классы Minecraft, на сервере не проверить). `scripts/jarport/JarPort.java`
   — переписывает ссылки в байткоде по правилам (`class`, `field`, `factory`, `call`/`scall`, `bridge`, `ownerinit`)
   и добавляет классы-переходники из `mods-src/jar-ports`. Сборка порта: `patches/jar-ports/build.sh <мод> <сервер>`
   → `pack/mods/*-mmport.jar` (оригинал качается по ссылке с проверкой хеша).
   - **CBC: Nuclear 2.1.1** (MIT): `CBCBuilderTransformers.safeNbt()` в CBC 5.11 есть, а его Forge-реализацию удалили —
     возвращён тег `create:safe_nbt` (как в CBC 1.20); `explosivePower()` разделён на урон блокам/существам —
     размер ядерного взрыва считается от урона блокам.
   - **Alshanex's Familiars 1.20.1_v1.1.2** (ARR, под Iron's Spells ~3.4): переходники `SimpleAttributeCurio`,
     `KeyState`, `OwnerGetter` (теперь `Supplier<Entity>` с мостом), владелец призыва `getSummoner()` — мост к
     `Entity`, таймеры призыва убраны, `SyncedSpellData` с `HolderLookup`, Oakskin — прежняя формула. Рецепт котла
     алхимика (Iron's 3.16 варит только жидкости) заменён крафтом: 2 зелья яда + клык паука → флакон яда.
   - **T.O Magic 'n Extras 6.3.0** (ARR, под Cataclysm 3.16): частицы `*Data` → record'ы `Options` (у `CircleLightning`
     размер 0.1 как у Cataclysm, у `Ring` цвет 0..1 → 0..255), `Boltstrike_Entity` → `Bolt_strike_Entity`,
     `Dungeon_Eye.DungeonEyeItem` → `items.DungeonEyeItem`, лазер — направление `Vec3`, ракета Харбингера — тип +
     владелец, `CMConfig.HarbingerHealingMultiplier` → `Harbinger.LifeSteal`, поджог лазерами — как было (true),
     частица `SANDSTORM` (в 3.31 не регистрируется) → `minecraft:white_ash`. Требует Apothic Attributes + Placebo
     (добавлены). Три тега T.O ссылаются на убранных существ Cataclysm — чистятся `missing_mod_refs.js`.
   - **Zombie Have Ears 1.1.2** (поток 2, ARR): `mods.toml` в формате NeoForge → перепакован `patches/zombie-have-ears/build.sh`
     (код не тронут, добавлен `pack.mcmeta`) → `pack/mods/zombiehaveears-1.1.2-forgefix.jar` (см. п. 13).
     Проверено вместе с Simple Voice Chat 2.6.24: плагин регистрируется, моды грузятся.
   Проверено (`scripts/tests/ports_commands.txt` + `portcheck.js`): сервер стартует, «разъярённый» Dead King и
   питомцы Familiars (друид, некромант, бард) появляются и живут, ядерный снаряд ставится, предметы и рецепт-замена
   на месте, ни одного NoSuchMethod/NoClassDef/AbstractMethod в логе. Клиентскую часть (частицы, клавиша KeyState,
   рендер) — проверить в потоке 5.
   - **Cataclysm: Spellbooks 1.2.9** (Ace The Eldritch King & StarOcean, MIT; под Cataclysm 3.1x; задача 9 из потока 5,
     К4): частицы `RingData` → `RingParticleOptions` (цвет 0..1 → 0..255), `LightTrail`/`Storm`/`TrackLightning`
     `OrbData` → `Options/*` (те же конструкторы), `Ancient_Remnant_Rework_Renderer` → `Ancient_Remnant_Renderer`
     (слой и модель те же), ракета Иссушителя — направление `Vec3`, `causeLaserDamage(Entity, Entity)`,
     `setSleep` Древнего Остатка (в 3.31 его нет: Остаток «спит», пока на нём нет ожерелья) → фантому из Thoth's
     Witness ставится ожерелье. `azurelib` теперь отдельной строкой в modlist (раньше подтягивался packwiz'ом).
     linkcheck чистый, кроме `Ancient_Remnant_Rework_Model.renderToBuffer` — унаследован от `HierarchicalModel`
     (клиентского jar на сервере нет). Проверено `scripts/tests/sbcastcheck.js` (10/10): Gravitational Pull,
     Thoth's Witness, Aerial Assault, Parting Shot, Cryopiercer, Scorched Earth, Laserbolt кастуются фейковым игроком
     без исключений, залп Aerial Assault создаёт `cataclysm:wither_missile`, фантом Остатка призывается и не спит,
     эффекты Cursed Frenzy и King's Wrath тикают; клиент со всей сборкой (вместе с магией из PR #15, 235 модов на
     сервере) — до главного меню. Вход клиента на тестовый сервер с портом **не проверен**: сервер (6 ГБ) и клиент
     (5 ГБ) вместе выбили лимит памяти контейнера — OOM убил обоих, swap от этого не спасает.
   - **Arknights_Weapon 1.5.6** (MCreator, AFL 3.0; задача из ревью PR #2): `RiseProcedure` на каждом
     `EntityJoinLevelEvent` ставила не-игрокам базу `max_health` = текущий максимум × 2; событие приходит и при загрузке
     чанка — здоровье росло вдвое при каждой подгрузке. Других вызовов у процедуры нет — отключена (`AkWeaponPort.noRise`).
     Проверено `scripts/tests/mobhealth_commands.txt` (моб в чанке x=2000, выгрузка и загрузка чанка): оригинал — зомби
     20→40→80, разбойник 24→48→96; порт — 20/24 и после перезагрузки.
   - **Apothic Attributes** (зависимость T.O) меняет формулы брони и защиты во всей игре (`config/attributeslib.cfg`,
     `combat_rules`): малая броня поглощает больше (зомби с бронёй 2 получает ×0.83 вместо ×0.98). Из-за этого в
     `castcheck.js` поправлен порог метки жертвы (сама метка работает: урон ×1.6). **Решено (2026-10-11):**
     смешанный вариант в `pack/config/attributeslib.cfg` — броня по Apothic (`a/(a+броня)`, броня выше 20 не упирается
     в потолок 80%), защита-зачарование по ванилле (`1 - min(0.04·очки, 0.8)` вместо `0.025`/`0.85`). Расчёт урона
     (ваниль / Apothic / смешанный): босс 30 по незериту + Защита IV — 4.75 / 7.71 / 4.63; босс 50 — 10.8 / 16.7 / 10.0;
     моб 10 по алмазу + Защита IV — 1.08 / 2.00 / 1.20; зомби 3 по коже — 2.34 / 1.76 / 1.76. С Apothic по умолчанию
     конец игры почти вдвое тяжелее, чем рассчитывали авторы боссов (Защита IV ×4 срезала бы 40% вместо 64%).
     Проверено `scripts/tests/armor_commands.txt`: зомби в алмазе с Защитой IV после удара 10 — 18.875 HP (как в расчёте).
15. **Superb Warfare × Valkyrien Skies.** Superb VS не нужен: Superb Warfare 0.8.9+ сам трассирует снаряды по
   кораблям VS (`compat/valkyrienskies`), и с VS 2.4.11 это работает. Но в попадании по кораблю он ставит не блок
   корабля (тот живёт на «верфи» VS), а точку вылета — снаряд останавливается правильно, а эффекты по блоку
   (стекло, урон блоку, частицы) уходят в воздух. `mods-src/mm-compat` 0.3.0 (миксин `SbwShipHitMixin`) подставляет
   блок корабля из ванильной трассировки VS. Проверено `scripts/tests/vshitcheck.js`: корабль 3×3 из железа на
   высоте 200, луч через него — попадание в `iron_block` на верфи VS.
16. **Create: Eureka!** (CurseForge) — не датапак, а сборка из 134 модов (Forge 47.2.0, 2024) с одними конфигами;
   переносить нечего. Список модов — материал для потока 3.

### Дополнения, волна 2 (поток 3)
Пачка 1 — измерения: Ancient Aether, Aether's Delight, Protect Your Moa, Treasure Reforging, Enhanced Extinguishing,
Aether Emissivity (клиент), Umbral Skies (TF × Aether), TF Dungeons & Villages, TF Lost Blocks, TF Bosses Resurrection,
Twilight Cave Fix (сервер), Create Blue Skies Compat, Blue Skies Tweaks, Farmer's Cutting: Blue Skies,
Blue Skies Gravestones Compat (сервер), Undergarden Delight, Deeper and Darker: Spellbooks, Deeper and Darker → Cataclysm,
Dungeons and Villages: Deeper and Darker, Create: Alex's Caves Compat. Сервер — `Done`.
- CBC: Nuclear 2.1.1 (и все прежние версии) — `NoSuchMethodError CBCBuilderTransformersImpl.safeNbt()` с
  Create Big Cannons 5.11.4, мод не грузится. Отключён (задача в потоке 4).
- Create: Alex's Caves Compat: часть рецептов и тегов для Create: New Age (его нет в сборке) не загружается — некритично.
- При добавлении с CurseForge packwiz перезаписывает уже стоящие зависимости (Aether, Structure Gel) CF-метаданными —
  такие изменения откатывать (`git checkout pack/mods/<мод>.pw.toml`).
- Almost Unified: новые моды не дают предметов в объединяемые теги; изменений не нужно.

Пачка 2 — мобы и боссы: Lycanite Delight, Lycanites Kin, Mowzie's Cataclysm, GTBC's Geomancy Plus (Iron's Spells ×
Mowzie's, + GTBC's SpellLib), Alex's Delight, Alex's Mobs Interaction, Capsid Fix, Cinematic Cataclysm (+ FDLib),
Loot Integrations: Cataclysm (+ Loot Integrations). Сервер — `Done`.
- Loot Integrations требует **Cupboard** (`>=1.20.1-1.5`, только CurseForge), не объявленную в зависимостях — добавлена.
  На Modrinth slug `cupboard` занят другим модом (Aesthetic Storage) — брать только `cf cupboard`.
- Integrated Mowzie's Mobs не добавлен: обязательные зависимости Quark, Supplementaries, Amendments.
- В логе только `gtbcs_geomancy_plus:chests/test` (тестовая таблица добычи автора) — некритично. Almost Unified — без изменений.

Пачка 3 — магия: Ars Elemental, Too Many Glyphs, Ars Technica (Ars × Create), Ars Instrumentum, Ars Controle,
Ars Artifice, Iron's Recipe Additions, Botanical Machinery, ExtraBotany (+ Event Wrapper), Create: Mechanical Botania,
Create: Occult Engineering, Occultism Ritual Satchel, Animus (Blood Magic). Сервер — `Done`.
- T.O Magic 'n Extras 6.3.0 — `NoClassDefFoundError: cataclysm/items/Dungeon_Eye/DungeonEyeItem` (класс убран в
  Cataclysm 3.31). Отключён; откатывать Cataclysm нельзя — на неё завязаны другие связки.
- Alshanex's Familiars 1.1.2 — `NoClassDefFoundError: ironsspellbooks/item/curios/SimpleAttributeCurio` (старый API
  Iron's Spells). Отключён.
- Botanical Machinery Extra не добавлен: обязательная зависимость Applied Energistics 2.
- В логе: книга Patchouli `ars_controle` не загружается (у Ars Nouveau 4.12 своя документация) и тег
  `hexcasting:dusts/amethyst` от Ars Technica (Hexcasting нет) — некритично.
- Almost Unified: Occult Engineering даёт `forge:dusts/zinc` и `forge:dusts/brass` — единственный поставщик, объединять
  не с чем. Изменений не нужно.

Пачка 4 — оружие, VS, Arknights: TaCZ addon, Enchanted Arsenal, Create: Precise Manufacturing, ArsArms, IronsArms,
Curios For Ammo Box, CBC: Advanced Technologies, Valkyrien Sails, VS Tournament Reforged, Gathering Torches Become Sunlight,
Arknights Weapon, Sarkaz's Furnace Side Fables. Сервер — `Done`.
- Arknights Weapon в оригинале удваивал здоровье всех мобов при каждой загрузке чанка — в сборке вместо него порт
  потока 4 без `RiseProcedure` (п. 14); CBC: Nuclear, T.O Magic 'n Extras, Alshanex's Familiars — тоже порты (п. 14),
  Superb VS не нужен (п. 15).
- Superb VS 1.0.0-beta (связка Superb Warfare × VS) собрана под VS 2.3: с VS 2.4.11 падает загрузка Superb Warfare
  (`MixinApplyError superbvs.mixins.json:ProjectileEntityMixin`, нет `ShipWorldCore`). Отключена, задача — поток 4.
- VS Tournament 1.1.0-beta (2024) — `NoClassDefFoundError VSConfigClass` с VS 2.4. Заменён на **VS Tournament Reforged**.
- SBW Vintage Vehicle Pack 0.2.1 требует ровно `superbwarfare 0.8.9` (у нас 0.8.9.2) и `snassets_particles`. Отключён.
- Create: Eureka! — датапак, не мод; отложен (задача — поток 4).
- CBC: Advanced Technologies: 4 таблицы добычи на несуществующие блоки литейных форм — некритично.
- Almost Unified: новые моды не дают предметов в объединяемые теги.

Пачка 5 — техника и удобство: Mekanism: More Machine, Mekanism Covers, Mekanism Curios, Just Enough Mekanism
Multiblocks (клиент), Sophisticated Backpacks Create Integration, Gravestone x Curios API Compat (+ BaguetteLib; сервер),
Xaero's Minimap & World Map — Waystones Compatibility, Jade Addons, Legendary Survival Addon (Create × LSO). Сервер — `Done`.
- More Backpack Upgrades 1.0.2 требует Refined Storage (`Mod mobackup requires refinedstorage`) — не добавлен.
- Mekanism: More Machine: 75 таблиц добычи на фабрики, которые регистрируются только с другими аддонами Mekanism
  (предметов `mekmm:*_factory` нет) — некритично.
- Almost Unified: новые моды не дают предметов в объединяемые теги.

Пачка 6 — моды с крупными зависимостями (одобрено пользователем): Integrated Mowzie's Mobs (+ Quark, Supplementaries,
Amendments, Integrated API, Integrated Patches), Botanical Machinery Extra (+ Applied Energistics 2),
More Backpack Upgrades (+ Refined Storage). Сервер — `Done`, новый мир создаётся, ошибок от новых модов в логе нет.
- Не объявлены как зависимости на Modrinth (найдены запуском), добавлены вручную: **Refined Storage** (More Backpack
  Upgrades), **GuideME** `[20.1.7, 20.2)` (AE2), **Moonlight Lib** (Supplementaries, Amendments), **Zeta** `[1.0-28, 2.0)` (Quark).
- Almost Unified: Quark кладёт `quark:charcoal_block` в `forge:storage_blocks/charcoal` вместе с `mekanism:block_charcoal` —
  по приоритету остаётся блок Mekanism. `hardrefs.py`: по имени на блок Quark ссылается только его собственный рецепт
  распаковки (у Mekanism есть свой) — конфиг менять не нужно. AE2 (истинный кварц, флюикс) и Supplementaries (пепел)
  дают только новые материалы.

## Голосовой чат (Simple Voice Chat 2.6.24)
Состав (раздел «Удобство и мультиплеер» в `pack/modlist.txt`):
| Мод | Сторона | Что даёт |
|---|---|---|
| Simple Voice Chat | both | голос по близости, группы; сервер слушает **UDP 24454** |
| Simple Radio (+ Lexiconfig) | both | рации, радио, микрофоны/динамики; связки с Create, Valkyrien Skies, Curios, Vibrative Voice включились сами |
| Sound Physics Remastered | client | реверберация и заглушение звуков и голосов стенами |
| Talking Heads, Let Them Talk | client | анимация говорящего, приглушение музыки при речи (VoiceChatProfiles убран — К2 «Проверки клиента») |
| Vibrative Voice | both | голос — вибрация для скалк-сенсоров/Хранителя (в одиночке — через встроенный сервер, см. «Моды side=server и одиночная игра») |
| VC Admin, SimpleVoiceChat Broadcast | server | модерация групп и вещание на всех для OP |

| Voice Chat Interaction, AudioPlayer, Enhanced Groups (+ Admiral) | both | **свой порт на Forge** (`mods-src/voicechat-addons`): голос будит скалк, свои звуки на пластинках/рогах/нотных блоках/головах, постоянные и авто-группы |

Voice Chat Interaction, AudioPlayer и Enhanced Groups Max Henkel выпускает только под Fabric — перенесены на
Forge в одном jar `pack/mods/voicechat-addons-forge-*.jar` (подробности и авторство — `mods-src/voicechat-addons/README.md`).
Проверено на сервере: SVC регистрирует плагины `vcinteraction`, `audioplayer`, `enhancedgroups`; все 6 миксинов
AudioPlayer применились; `/audioplayer serverfile` принимает mp3 и wav, `/audioplayer play` проигрывает через SVC
(Opus/LAME инициализированы); веб-загрузка (`run_web_server=true`) отдаёт страницу на :8080; `/persistentgroup add`
создаёт группу; `scripts/tests/voicecheck.js` — событие `vcinteraction:voice` есть, частота 7, в тегах
`vibrations` и `warden_can_listen`. Simple Radio сам включил связку с AudioPlayer.
Голос как вибрацию умеют два мода — Voice Chat Interaction и Vibrative Voice. Оставлен **Vibrative Voice**: родная
Forge-сборка от автора (порт VCI в игре с голосом не проверен), связка с рациями Simple Radio, пороги слабой/сильной
вибрации, учёт шёпота и приседания. Voice Chat Interaction выключен опцией порта `enabled=false`
(`pack/config/vcinteraction/vcinteraction.properties`; мод загружен, событие зарегистрировано, микрофон не слушает). Не проверено без клиента: настоящий голос → сенсор, загрузка через браузер (нужен токен игрока).
Ошибка «Failed to load persistent groups» при первом запуске — так у оригинала, файла ещё нет.
Не взяты: связки с модами, которых нет в сборке (CPM, Figura, Plasmo Voice).
Серверные моды проверены на отсутствие своих сетевых каналов — клиенту без них подключаться можно.
Sound Physics на Modrinth помечен `client_only_server_optional` — packwiz ставит `both`, исправлено на `client`.
Тест: SVC загрузил 3 плагина (vibrativevoice, simplevoicechat_broadcast, simpleradio), `Voice chat server
started at port 24454`. Конфиг сервера — `pack/config/voicechat/voicechat-server.properties` (по умолчанию;
`voice_host` — для туннеля playit.gg, см. `docs/hosting.md`).

## Экспорт сборки
`scripts/export-pack.sh` → `dist/`: `.mrpack` (packwiz modrinth export; моды `side=server` в индексе с
`env.client=unsupported`, Twilight Forest с CurseForge и свои jar — в `overrides/`) и `*-server.zip`
(моды both+server, config, kubejs, установщик Forge, `start.sh`/`start.bat`). Серверный пакет проверен:
распаковка → `start.sh` ставит Forge, просит EULA → `Done (114 s)`, голосовой сервер поднят.
Клиентский `.mrpack` в лаунчере пока не проверен (поток 5). `pack/modlist.txt` исключён из раздачи
(`pack/.packwizignore`).

## Объединение дублей (Almost Unified)
Один ресурс — один предмет: `pack/config/almostunified/unify.json`. Приоритет модов:
minecraft → kubejs → create → mekanism → createaddition → ad_astra → northstar. Например, вся сталь
(Ad Astra, Create Big Cannons, Mekanism, Superb Warfare) выдаётся как `mekanism:ingot_steel`, свинец —
Mekanism, листы — Create. Не объединяются (`ignoredItems`): уран Alex's Caves (своя механика радиации),
изумруд Caerula Arbor и никелевая руда Creating Space (у них ошибочные теги).
Генерация серебра и свинца Superb Warfare в Верхнем мире отключена (дубль Occultism/Mekanism):
`pack/kubejs/data/superbwarfare/forge/biome_modifier/`. Проверка рецептов, завязанных на конкретный
предмет: `scripts/hardrefs.py`.

## Одна Солнечная система (Ad Astra + Northstar + Creating Space)
Планеты и орбиты — **только Ad Astra**; ракеты — **только Northstar и Creating Space** (строятся из блоков,
требуют расчёта тяги/топлива/Δv, планеты Northstar открываются телескопом). Простые крафтовые ракеты Ad Astra
убраны: `pack/kubejs/server_scripts/space_rockets.js` (рецепты ракет T1–T4, верстак NASA и его автоматическая
версия из Giselle Addon, конус, стабилизаторы, стартовая площадка) и `client_scripts/space_rockets.js` (скрыты в
JEI). Двигатели и баки Ad Astra оставлены — из них делаются реактивный костюм и ровер.

Генераторы данных (берут исходные файлы из jar):
- `patches/space_unify.py` → маршруты: `planet_dimension` Northstar и `rocket_accessible_dimension`
  Creating Space ведут в `ad_astra:*`; старые узлы Creating Space пустые. Добавлены: орбита Луны для Northstar;
  Меркурий и Глацио (межзвёздный перелёт, самый дорогой по Δv) для Creating Space; исправлена гравитация Венеры
  у Creating Space (было 1.6 → 8.87).
  **Гравитация** — реальная, от Northstar (по ней он считает тягу ракет); у Ad Astra на тех же планетах и
  орбитах — нейтральная, иначе миксины двух модов перемножили бы её. Глацио — гравитация Ad Astra.
  **Кислород** — система Ad Astra. Скафандры Northstar и Creating Space в ней работают через мод
  `mods-src/mm-compat`: Ad Astra считает, что существо дышит, пока в баллоне скафандра есть воздух, и воздух
  тратится 1 ед./с (как в самих модах). Пустой баллон — удушье.
  **Температура** — Northstar сохраняет свою (на ней работают его машины); скафандры связаны тегами.
- `patches/ore_crossgen.py` → ресурсы Northstar и Creating Space на планетах Ad Astra (см. ниже в журнале).

Проверено на сервере (`scripts/tests/`): `gravitycheck.js` (Луна 1.6, Марс 3.71, Венера 8.87, Меркурий 3.7,
орбиты 0 — Northstar; у Ad Astra множитель 1; Глацио 0.38 — Ad Astra), `routecheck.js`, `rocketcheck.js`
(рецептов ракет Ad Astra нет, детали ракет Northstar/CS есть), `oxygen_commands.txt` (моб в полном скафандре
Northstar/CS выживает на Луне, воздух тратится ~1/с; при 5 ед. воздуха — через 5 с удушье и гибель),
`spacecheck.js` (руды на планетах).

## Galacticraft Revival (свой мод, порт GC)
`pack/mods/galacticraft-revival-0.1.0.jar` из `mods-src/galacticraft-revival`, сторона both, зависит только от Ad Astra.
Лунный данж GC с боссом в `ad_astra:moon`; мобы данжа в тегах кислорода/температуры Ad Astra и Northstar.
Проверено на полной сборке в новом мире (2026-10-10): `Done`, `scripts/tests/gcdungeon.js` 12/12, castcheck 69/69.
Ванильный `/locate` в сборке асинхронный (Async Locator) — результат команды всегда 0, ответ приходит позже.
План и ход работ — `docs/galacticraft.md`.

## Falling Tree (поток 18)
Falling Tree 1.20.1-4.3.4 (RakSrinaNa, LGPL-3.0; Modrinth `fallingtree`, сторона `both`): срубленное дерево падает целиком.
- Конфиг `pack/config/fallingtree.json` (файл мода; в нём всё, что не перечислено ниже, — значения по умолчанию):
  `trees.breakMode = FALL_BLOCK`, `sneakMode = SNEAK_DISABLE` (присед = рубить по одному блоку),
  `tools.damageMultiplicand = 1` (1 прочности за бревно), `tools.preserve = true`, `tools.forceToolUsage = true`,
  `trees.maxSize = 300`, `trees.maxSizeAction = ABORT` (дерево больше 300 брёвен — рубится один блок).
- Миксин `FallingTreeDamageMixin` (`mods-src/mm-compat`, необязательный, `@Pseudo`): при `preserve = true` Falling Tree сам рубит
  столько брёвен, на сколько хватает прочности. Миксин в конце конструктора `ToolDamageHandler` бросает то же
  `BreakTreeTooBigException`, что мод бросает при превышении maxSize, если прочности не хватает на все брёвна дерева
  (с `preserve` — так, чтобы топор не сломался на последнем бревне). `TreeHandler` ловит его и отвечает `TREE_TOO_BIG_BREAK`
  (`cancel = false`) — событие не отменяется, игра ломает один блок. Исключение летит одинаково из всех трёх обработчиков
  (Instantaneous, FallingAnimation, ShiftDown): все создают `ToolDamageHandler` и пропускают исключение наверх.
  Параметры конструктора (javap): `int` №1 — число брёвен дерева (`Tree.getBreakableCount`), `int` №2 — `maxSize`.
- Широкие стволы 2×2 не трогали (решение владельца).
- Тест `scripts/tests/fallingtree.js` (фейковый игрок Forge, `ServerPlayerGameMode.destroyBlock`, железный топор 250 прочности;
  запускать в чистом мире — брёвна прошлых прогонов остаются в мире и попадают в дерево). Результат на чистом мире, `Done`:
  полная прочность (6 брёвен) — дерево целиком, −6 прочности; осталось 3 прочности — один блок (−1: 247 → 248);
  присед — один блок; куб 384 бревна — один блок; осталось 7 на 6 брёвен — целиком, топор остаётся с 1; осталось 6 на 6 — один блок.
  Не проверено: игра живым игроком и реальные деревья (листва `persistent=false` проверена справочно — тоже −6),
  мод Falling Tree без миксина-цели (запуск без мода), клиент.
## Проверка клиента (поток 5, 2026-10-11)
Клиент запускался в облаке: `scripts/test-client.sh` — Forge через portablemc, моды из `.mrpack` так же, как
ставит Prism (только `env.client != unsupported` + `overrides/`), Xvfb + Mesa llvmpipe (OpenGL 4.5), без звука.
Тестовый сервер — `scripts/test-server.sh`, затем вход с клиента. Скриншоты — `docs/client-check/`.
Стороны: в `.mrpack` 7 серверных модов помечены `env.client = unsupported`, 11 клиентских — `env.server =
unsupported`; в серверном zip клиентских нет. Совпадает с `side` в `pack/mods/*.pw.toml`.

Найдено (до исправлений клиент **не доходил до меню** ни разу):
- К1. **ImmediatelyFast 1.2.7+1.20.2 × Oculus 1.8.0** — Oculus 1.8 переименовал пакеты Iris
  (`net.coderbot.iris` → `net.irisshaders.iris`), IF не находит `ImmediateState` и вызывает `System.exit(-1)`:
  игра закрывается сразу после создания окна **без краш-репорта** (найдено агентом `scripts/clienttest/ExitTrace.java`).
  **Исправлено:** IF обновлён до 1.5.5+1.20.4-forge (знает оба пакета; mods.toml: minecraft [1.20,1.20.4], forge [46,)).
  `packwiz update` сам его не обновляет («inconsistent between latest version number and newest release date») —
  при обновлениях не откатывать на 1.2.7.
- К2. **VoiceChatProfiles 1.0.1 (1.20.1 Forge)** — диапазон `voicechat [2.6.0,)` против версии
  `1.20.1-2.6.24` (по Maven 1 < 2: «Missing mandatory dependencies»), а после правки диапазона — вылет на первом
  тике меню: `NoSuchMethodError KeyMapping.consumeClick()` — jar не перемаплен в SRG (имена Mojang).
  Исправленной версии нет. **Убран** из сборки (клиентская мелочь, в `modlist.txt` закомментирован).
- К3. **Origin Furs (`origin_visuals` 1.0.5) × AzureLib 3.1** — миксины Origin Furs используют старый API
  (`mod.azure.azurelib.model.GeoModel`, `cache.object.GeoBone`, `core.animation.*`, `renderer.GeoObjectRenderer`),
  которого нет в AzureLib 3.1.x; а Cataclysm Spellbooks 1.2.9 требует новый API (`AzAnimator`, `AzArmorRenderer`…),
  которого нет в 3.0.27. Ни одна версия AzureLib (проверены 3.0.27, 3.1.0/3/4/6/10/20) не подходит обоим.
  Миксин падает → «broken mod state» → вылет. **Убраны** Origin Furs и Arknights Furs (обе только клиентские,
  на сервер и реестры не влияют). Вернуть можно портом (задача потоку 4): переходник старого API AzureLib
  внутри jar Origin Furs (`scripts/jarport`) или собрать Origin Furs под AzureLib 3.
  **Решено портом (поток 4b):** готовой версии под AzureLib 3.x нет (Modrinth: Origin Furs 1.0.5 от 2024-10,
  Arknights Furs 1.0.2). В AzureLib 3.1 старого API нет целиком (GeoModel/GeoBone/GeoObjectRenderer/анимации заменены
  Az*-рендером), поэтому не переходники, а **встроенная копия**: 195 классов AzureLib 2.0.39 (MIT, AzureDoom; транзитивное
  замыкание от ссылок мода) переносятся в jar Origin Furs в пакет `dev/mcmoded/port/originfurs/azl` — новое правило
  `prefix` в `scripts/jarport/JarPort.java` и строка `embed` в `patches/jar-ports/build.sh` (`patches/jar-ports/origin-furs`).
  Поведение — как у AzureLib 2, с AzureLib 3.1 копия не пересекается. Платформенные сервисы AzureLib 2 (ServiceLoader →
  NeoForge-классы; их сетевой канал `azurelib:main` занят AzureLib 3.1) заменены прокси `OriginFursPort`
  (`mods-src/jar-ports`): события рендера не шлются, `AzureLib.initialize()` → только загрузчик `geo/`/`animations/`
  встроенного кэша (на клиенте). Arknights Furs кода под AzureLib не содержит (только ресурсы `origin_visuals`) —
  возвращён как есть. Проверено: linkcheck — 31 «несвязанная» ссылка, все на клиентские классы Minecraft (на сервере
  их нет, мод клиентский), ссылок на `mod/azure` не осталось; сервер до `Done`; `CLIENT OK`, в логе ни ошибок
  встроенной AzureLib, ни провалов миксинов Origin Furs. Предупреждение Forge `Unable to find registry with key
  origin_visuals:furs` (ObjectHolder, не фатально) — особенность самого мода. Рендер шерсти в облаке не проверен:
  клиент (без YSM) зашёл на сервер, раса Vulpo выдана `/origin set`, но мир не догрузился — JEI на старте строит
  поисковый индекс по подсказкам всех предметов (больше часа на 4 CPU; с `tooltipSearchMode = DISABLED` — 3 мин), а
  сервер рядом с клиентом упал по ServerHangWatchdog (тик 60 с, нехватка CPU, не порт). Рендер проверяет игрок —
  пункт в `docs/client-checklist.md`.
- К4. ~~**Cataclysm Spellbooks 1.2.9 × L_Ender's Cataclysm 3.31 — БЛОКЕР**~~ — **решено портом** (поток 4, п. 14):
  вылет клиента при регистрации рендереров (`NoClassDefFoundError .../Ancient_Remnant_Rework_Renderer`) и 9 несвязанных
  ссылок (частицы `*Data`, ракета Иссушителя, урон лазером, рендер и сон Древнего Остатка). В сборке
  `cataclysm_spellbooks-1.2.9-1.20.1-mmport.jar`, клиент со всей сборкой доходит до главного меню.
- К5. **Serene Seasons Plus сбрасывал конфиг сборки.** Новая версия при первом запуске переносит
  `sereneseasonsplus-common.toml` в `.bak` и пишет умолчания, если в `config/` нет маркера
  `.sereneseasonsplus-descriptive-config-reset.done` — на сервере и на клиенте выключалось накопление снега
  (`enableSeasonalSnowAccumulation`, п. 8), игроку показывалось окно «config was reset». **Исправлено:** маркер
  лежит в `pack/config/` (попадает и в `.mrpack`, и в серверный zip).
- К6. ~~**Lycanites `LightTextureMixin` на клиенте не применяется**~~ — **решено** (поток 4): «@Shadow field lightTexture
  was not located… Using refmap lycanitesmobs.refmap.json». Запись в refmap не помогает: Mixin ищет `@Shadow`-поля
  только по имени в классе миксина и через переименователи окружения (в собранной игре их нет), refmap к ним не
  применяется — поле в jar должно уже называться `f_109870_`. Чтобы не перепаковывать 120-МБ jar, логика затемнения
  перенесена в `mods-src/mm-compat` (`LycanitesFearLightMixin`, SRG-имена заданы явно, вызывает их
  `FearVisualHandler`; `require = 1` — неудача была бы видна в логе: на клиенте применяется без ошибок); миксин
  Lycanites по-прежнему пишет это предупреждение — безвредно. Если Lycanites
  исправят у себя — убрать наш миксин, иначе свет затемнится дважды.
- К7. **Yes Steve Model под ВМ:** «YSM client cannot run under a virtual machine» (экран предупреждений Forge),
  затем выгружает свою `libysm-core.so`, и её поток роняет JVM нативным SIGSEGV (`__call_tls_dtors`) при входе
  в мир. Особенность облака — на настоящем ПК так быть не должно; для теста YSM убирали с обеих сторон.
  Игрокам — пункт в `docs/client-checklist.md`.
- К8. Остальное при входе в мир (не вылеты): Distant Horizons пишет «Unable to get a complete buffer for a
  received payload» (LOD с сервера при сильной задержке тиков); Arknights Weapon — «Missing textures in model
  arknights_weapon:<оружие>#inventory» (11 шт., в `models/item/*.json` текстура без пространства имён →
  `minecraft:item/…`; предметы рисует GeckoLib, в руке/инвентаре проверить глазами); 3 пластинки — «Palette mask …
  amendments:block/music_discs» (Amendments не нашёл маску под чужие пластинки, только вид на проигрывателе).
- К9. **Нагрузка:** с 5 ГБ кучи клиент в мире занят на 94 % (F3) — игрокам ставить 6–8 ГБ (`docs/friends.md`).
  Вход идёт долго: рецепты и синхронизация данных на медленном ПК занимают десятки секунд, а сервер ждёт ответ
  на keep-alive не дольше 15 с — на облачном CPU первый вход кончился «Timed out», второй прошёл.
  Если у игрока со слабым ПК повторяется — сообщить (можно поставить мод, увеличивающий таймаут).

Проверено в мире (llvmpipe, 2 FPS): вход без «mismatched mod channel» и рассинхрона реестров, выбор Origin и
Originium Arts, HUD (LSO, миникарта Xaero, Jade), рендер Evolved Skeleton Boss (Galacticraft Revival) с полосой
здоровья босса, феникс Wizardry Restored. Миксины портов `*-mmport.jar` (T.O Magic, Familiars, CBC: Nuclear,
Arknights Weapon) применяются без ошибок. Микрофон, звук, шейдеры на GPU, детали рендера — `docs/client-checklist.md`.

## Mana and Artifice и Ars Magica: Legacy (поток 3, 2026-10-11)
Решение владельца: добавить оба мода и дополнения. Оба есть под 1.20.1 Forge, side=both (обязательны и на клиенте, и на сервере).

| Мод | Версия | Источник | Зависимости | Итог |
|---|---|---|---|---|
| Mana and Artifice (Mithion/Rhodes) | 3.1.11 | Modrinth `mana-and-artifice` | GeckoLib, Curios — уже в сборке; Lootr, Lucent — необязательные, не ставили | добавлен |
| Ars Magica: Legacy | 1.20.1-1.5.0 | Modrinth `ars-magica-legacy` | GeckoLib, Patchouli — в сборке; JEI, Curios, Jade — необязательные, есть | добавлен |
| ArcaneAdditions | forge-1.10.0 | Modrinth | M&A, GeckoLib, Curios | добавлен |
| Factions And Curiosities | 1.3.1 | Modrinth | M&A, Curios | добавлен |
| MNAOverpowered | 1.20.1-0.11.1 | Modrinth | M&A | добавлен (QoL-твики) |
| Dragon Magic And Relics | 3.2.14 | Modrinth (All Rights Reserved, см. CLAUDE.md) | M&A | добавлен |
| MNAWitchcraft | 0.2.1-alpha | Modrinth | M&A | **порт** (В4, поток 4b): собран под M&A 3.0.x (`NoClassDefFoundError com.mna.items.ritual.ItemPlayerCharm`). `patches/jar-ports/mnawitchcraft` → `pack/mods/mnaw-…-mmport.jar`: в M&A 3.1.11 классы `ItemPlayerCharm`/`ItemPractitionersPatch`/`ItemStaff` → `PlayerCharm`/`PractitionersPatch`/`MagicStaff`, `ItemStaff.getSpell` → `SpellRecipe.fromNBT(getSpellCompound)` (`MnawPort`). linkcheck: к M&A всё связывается; остались 12 ссылок на Mystical Alchemy — необязательная зависимость (под 1.20.1 Forge её нет), код сработает только с ней. Сервер до `Done`; в игре не проверялся |

- Дополнений к Ars Magica: Legacy под 1.20.1 не найдено (Modrinth, поиск по CurseForge) — у мода только API для аддонов.
- Не взяты: `ctov-mana-and-artifice-compatibility` (в сборке нет CTOV), `mana-and-artifice-villages` (только 1.18.2).
- Сервер: новый мир, `Done (90.7s)`, без «Mods loaded with N warnings», castcheck 69/69. Из новых модов в логе только
  `TOPCompat` AML (нет The One Probe — необязателен) и «M&A -> Failed to query auras from the server» (при старте, не мешает).
- Almost Unified не менялся: у Ars Magica свои камни (chimerite, moonstone, sunstone, topaz, vinteum), металлов нет, у M&A тегов `forge:ores/*` нет.
  Возможный пересечение `forge:gems/topaz` с другими модами — не проверялось.
- **Не проверено (задача потоку 4):** конфликты клавиш и интерфейса маны на клиенте (у M&A, Ars Magica, Iron's Spells,
  Ars Nouveau, Wizardry — у каждого своя система маны; общей нет), рецепты и дубли предметов, генерация мира/структур M&A рядом с остальными.

### Фриз сервера от Mana and Artifice — блокирующая загрузка чанков (mm-compat 0.5.2, поток 24, 2026-10-17)
- **Находка (логи игрока, Координатор):** ModernFix IntegratedWatchdog — «A single server tick has taken 2269630 ms», сервер
  висел ~45 мин; во всех дампах Server thread стоит в `ServerChunkCache.getChunkBlocking` ← `Level.getChunk(int,int)` ←
  `com.mna.events.CommonEventHandler.onWorldTick` (строка 1256). В других сессиях — частые «Can't keep up» по 2–9 с с тем же стеком.
- **Причина (байткод M&A 3.1.11, строки 1241–1259):** на каждый `LevelTickEvent` (END) для каждого игрока мира — цикл по чанкам
  в радиусе 5 (11×11 = 121 чанк), затухание остаточной магии чанка (`ChunkMagicProvider.MAGIC` → `removeResidualMagic(0.01)`).
  Отбор — `Level.hasChunk(x,z)`: на сервере это «у чанка есть тикет уровня FULL», а не «чанк готов». У края загруженной
  области (вход, телепорт, полёт, быстрый бег) чанки с тикетом ещё генерируются, и `Level.getChunk(x,z)` синхронно ждёт
  их генерацию на серверном потоке — тик длится секунды, при тяжёлой генерации (много модов мира) — минуты.
- **Исправление:** миксин `MnaChunkTickMixin` (`mods-src/mm-compat`, необязательный: `@Pseudo`, без M&A не применяется,
  `require = 1` — неудача видна в логе) — `@Redirect` вызова `hasChunk` (SRG `m_7232_`) в `onWorldTick`: на `ServerLevel`
  чанк берётся, только если он уже FULL (`ServerChunkCache.getChunkNow != null`), иначе пропускается в этом тике;
  следующий `getChunk` вызывается только для готовых чанков и не блокирует. Клиент (`ClientLevel`) — без изменений.
  Поведение M&A то же: магия в недогруженном чанке затухает со следующих тиков, когда он догрузится.
  Маркер в логе: `[com.mna.events.CommonEventHandler/]: mm_compat: MnA onWorldTick — только готовые чанки…` (один раз, при первом вызове).
- **Проверка** (`scripts/tests/mnachunk.js`, свежий мир): через 90 с после старта фейковый игрок Forge входит в мир
  (`addNewPlayer`) у чанка 20000,20000, плюс region-тикет радиуса 6 — чанки с тикетом, но не сгенерированные; меряется
  длительность 100 следующих тиков. С исправлением (2 прогона): `MNACHUNK OK maxTick=182 фон=885` и `maxTick=1056 фон=1203`
  (тик не дольше фоновых всплесков первых минут; `full=1` — за 5 с готов 1 чанк
  из 121 — сервер не ждёт генерацию). Без исправления (mm-compat 0.5.1): после входа ни одного тика за 21 с до остановки —
  spark «Timed out waiting for world statistics», переполнена очередь физики Valkyrien Skies; фриз воспроизведён.
  Сервер с исправлением прожил 5,7 мин, «Mixin apply failed» нет, `Mixing MnaChunkTickMixin … into com.mna.events.CommonEventHandler` в debug.log.
  `forceload add` для такого теста не годится: сама команда ждёт загрузку чанков (~30 с на 121 чанк).
- **Радиус цикла (не меняли):** 121 чанк на игрока за тик в каждом мире, где есть игроки, с `HashSet<Long>` против повторов;
  на готовый чанк — поиск в кэше чанков + `getCapability` (LazyOptional) + чтение float. Это ~120 дешёвых обращений на игрока
  за тик (у 5 игроков ≈ 600) — заметно в профиле, но не фриз. Тот же цикл идёт и на клиенте (`LevelTickEvent` для `ClientLevel`),
  там `getChunk` не блокирует. Если профиль spark покажет `onWorldTick` в верхних строках — можно прореживать (раз в N тиков).

## Офлайн-режим и авторизация (поток 2, 2026-10-12)
- `online-mode=false`: тест-сервер (`test-server.sh`, новый мир) — `Done`, предупреждение «OFFLINE/INSECURE MODE» ожидаемо.
  Simple Voice Chat стартует, пишет «Running in offline mode — encryption is not secure» (шифрование голоса слабее, настройки не нужны).
- Мод авторизации: **Sol's AuthLogin 1.20.1-1.3.1** (Modrinth `sols-authlogin`, side=server; лицензия ARR — см. «Назначение и лицензии»).
  Отвергнуты: `auth` (это датапак с «кик крашем клиента»), `nedologin`/`authlogic` (нужны и на клиенте), `offlineauth` (требует TrueUUID).
  Загрузился без ошибок, команды зарегистрированы. **Подвох**: по умолчанию `requireClientMod = true` — игроков без клиентского мода
  кикает; в `pack/config/solsauthlogin-common.toml` выставлено `false` (мод только на сервере; без клиентской части не работает лишь токен-автовход).
  Вход реальным клиентом (`/register`, `/login`, заморозка до входа) не проверялся.

## Заброшенные города — The Lost Cities (поток 6, 2026-10-12)
- Мод `the-lost-cities` 1.20-7.5.7 (both), профиль в `pack/defaultconfigs/lostcities-server.toml` → `rarecities` (≈ 6 % городских чанков вместо 37 % у `default`) — **частота выбрана владельцем (В10, вариант «а»), действует для нового мира**.
  Ключи конфига — в таблице `[profiles]`, иначе Forge «исправляет» их на пустые и города не включаются. `defaultconfigs/` теперь копируют
  `test-server.sh` и `export-pack.sh`. Тест `scripts/tests/lostcities.js` (`LCCHECK OK`), подробности и выбор — `docs/lost-cities.md`.
- Не связано с мод-конфликтами: в логе `NoClassDefFoundError mcjty/theoneprobe` — от Ars Magica Legacy (необязательная связка с TOP), не от Lost Cities.

## Находки клиентского лога при проверке книг (поток 11, 2026-10-12)

Клиент (`test-client.sh`, Forge 47.4.26) доходит до главного меню; в логе модельные ошибки, **не наши и не чинились**:
- `Model loader 'neoforge:composite' not found` — 4 модели **Mekanism More Machines** (`mekmm-1.20.1-1.2.1`): `mekmm:models/block/factory/painting/active/{basic,advanced,elite,ultimate}.json`
  (активное состояние фабрик покраски). Мод собран под NeoForge-загрузчик моделей (`neoforge:composite`), у Forge он называется `forge:composite`. Ожидаемо: пока фабрика
  покраски работает (активное состояние), блок рисуется как «потерянная» модель — **фиолетово-чёрная текстура**; в простое — нормально.
  **Исправлено (поток 14):** в jar это единственные 4 модели с `neoforge:composite`; их копии с `"loader": "forge:composite"` (части те же) лежат в
  `pack/kubejs/assets/mekmm/models/block/factory/painting/active/*.json` и перекрывают модели мода. Автор мода — Mekanism More Machines (jar не менялся).
- `Failed to load model mna:models/block/eldrin/pylon_ring.json` (Mana and Artifice 3.1.11) — `pylon_ring.json` ссылается на `mna:models/block/obj/pylon/pylon_ring.obj`, а в jar лежит только
  `pylon_ring_old.obj` (ошибка автора мода). Деталь «кольцо» пилона Элдрина не рисуется / рисуется как отсутствующая модель (фиолетово-чёрная). Обход — копия `pylon_ring_old.obj` под именем
  `pylon_ring.obj` в нашем паке.
  **Исправлено (поток 14):** `pack/kubejs/assets/mna/models/block/obj/pylon/pylon_ring.obj` — копия `pylon_ring_old.obj` (его `mtllib pylon_ring.mtl` в jar есть;
  иных «ring»-моделей нет). Автор — Mana and Artifice (авторство модели сохранено, файл не менялся).
  Проверка: `test-client.sh` — `CLIENT OK`, в `logs/latest.log` нет `neoforge:composite`, `pylon_ring` и «Failed to load model».
- Patchouli-книга `ars_controle`: «use_resource_pack set to false» — у мода в jar только заглушка `book.json` (`extend: ars_nouveau:worn_notebook`) без содержимого; исправлено
  kubejs-override `pack/kubejs/data/ars_controle/patchouli_books/ars_controle/book.json` (`use_resource_pack: true`, описание по-русски). Записей в книге у мода нет, переводить нечего.

## Китайские литералы мод→Component.literal (поток 6, 2026-10-12)
- Баг владельца «Талон Боба»: подсказки BOB's Originium Dream — китайские литералы в коде. Причина — не lang (аудит `cjk_nonlang_audit.py`: ≈ 880 строк в 13 jar).
  Решение: миксин `LiteralContentsMixin` + словарь 953 строки в `ru-lang` (jar модов не патчим). Проверено: `test-client.sh` — `CLIENT OK`, в логе самопроверка `[OK]`;
  миксин применился (ошибок `Mixin apply failed` от ru_lang нет; единственная — известная Lycanites К6).

## Открытые вопросы
- Клиент в облаке проверяется `scripts/test-client.sh` (без GPU и звука); шейдеры, микрофон и macOS — только у игрока (`docs/client-checklist.md`).

## Порт Galacticraft (если решим делать)
Galacticraft Legacy (1.12.2) → 1.20.1 — это фактически переписывание: между версиями сменились
система блоков (flattening), рендер, сеть, генерация мира и capabilities. Объём — десятки тысяч строк.
Лицензия не препятствует: сборка частная и некоммерческая (CLAUDE.md «Назначение и лицензии») —
код и ассеты Galacticraft Legacy можно переносить с указанием авторства, не публикуя результат.

## Моды CurseForge с запретом сторонней загрузки → jar в репозитории (2026-10-12)
packwiz-installer не может скачать мод без `url` в `.pw.toml` (CurseForge запрещает стороннее скачивание), поэтому
у друзей установка по ссылке падала. Jar этих модов теперь лежат в `pack/mods/` и раздаются по raw-ссылке GitHub;
`.pw.toml` удалены, строки в `modlist.txt` помечены `#jar`. Если правообладатель попросит — jar убираем.
- На Modrinth переведён только **GTBC's SpellLib/API** (GameTechBC).
- Остальные 23 мода на Modrinth под 1.20.1 Forge не найдены. Авторы по `mods.toml`: Alex's Delight (NCP Bails),
  Capsid Fix (k1sak1), Ars Elemental (Alexthw), Ars Instrumentum, Ars Technica и Iron's Recipe Additions (MCreator),
  Blue Skies Tweaks (Leclowndu93150), Create Blue Skies Compat (celsiusqc), Cupboard и Loot Integrations
  (+ Cataclysm) (Someaddon), Deeper and Darker to Cataclysm (armya), Gathering Torches Become Sunlight (free fish и др.),
  GTBC's Geomancy Plus (GameTechBC), Lycanite Delight (MythicModder), Lycanites Kin, Create: Mechanical Botania
  (Micaxs), Sarkaz's Furnace Side Fables (Zhi_Xu), Spell Forge for Wizardry Redux (ZettaSword), The Twilight Forest
  (Benimatic, Tamaized и др.), Too Many Glyphs (DerringersMods), Twilight Forest Bosses Resurrection (DimasStudio),
  Umbral Skies (Drullkus).
- Размеры: все меньше 100 МБ; самые большие — Gathering Torches Become Sunlight (76 МБ) и The Twilight Forest (23 МБ).

### JEI: Imbuement Altar Redux (mm-compat 0.5.0, поток 7)
Клиентский `@JeiPlugin` в mm-compat добавляет категорию Imbuement Altar (центр + 4 приёмника → результат, 120 рецептов) и каталист-алтарь.
JEI и Redux — только `modCompileOnly`; сервер без JEI загружается (проверено `Done`). Клиент с входом на сервер: категория есть, ошибок нет.
Первая сборка падала на рецепте `fix_ruined_spell_book` (`getReceptacleIngredients()` = null) — исправлено.

## JEI: аддоны и проверка клиента в мире (поток 7, 2026-10-12)
Подробности и таблица покрытия — `docs/jei.md`, скрипт — `scripts/jei_audit.py`.
- Добавлены (серверный тест `Done` за 101 с; клиент `CLIENT OK`, вход на сервер): Just Enough Breeding 3.1.2 (client),
  Just Enough Professions 3.0.1 (client; Modrinth даёт `client_only_server_optional`, packwiz ставил both — поправлено),
  Just Enough Beacons 1.2.0 (client; файл «Forge-1.20.4», но на 1.20.1 регистрируется и работает) + Cerbon's API 1.1.0 (client,
  зависимость), Just Enough Advancements 5.0.1 (both). Все четыре зарегистрировали JEI-плагины без ошибок.
- **Убраны Just Enough Beacons и Cerbon's API** (поток 10, 2026-10-12): JEBeacons для 1.20.1 есть только в сборке «1.20.4»
  и требует Cerbon's API; у того общие (не клиентские) миксины коллизий — AABB.intersects, Shapes.create, VoxelShape.collide,
  Entity.getBoundingBox/setPosRaw. Сторона client в сборке не спасает: в одиночном мире они работают на встроенном сервере.
  Причастность к жалобе «перестали спавниться мобы» не доказана (у владельца починилось само), но риск > пользы.
  Сервер после удаления: `Done` за 93 с.
- `test-client.sh` без входа в мир плагины JEI не проверяет (они стартуют после синхронизации рецептов). Для проверки
  клиент подключали к тестовому серверу: вместе нужно ~10 ГБ — в облаке пришлось включить своп 8 ГБ, сервер `-Xmx4G`,
  клиент `-Xmx5G` (с 5+6 ГБ без свопа клиент убивал OOM, код 137); YSM убрать на обеих сторонах.
- Ошибки JEI в логе клиента (все — собственные баги модов, сборку не ломают):
  - **Blue Skies**: `SkiesJEIPlugin.registerRecipes` → «There is no recipe category registered for blue_skies:horizonite_forge_fuel»;
    дальше по методу рецепты плагина не регистрируются.
  - **mekmm**: `MoreMachineJEI` → NPE `TileEntityReplicator.customRecipeMap` = null (рецепты Replicator не в JEI).
  - **JER**: «Exception caught when registering villager» (`turnToQuill`, trader = null) — безвредно.
- `Unknown recipe category: …` (≈94 строк) — ванильная книга рецептов, к JEI не относится.
- Пробел покрытия: Imbuement Altar Electroblob's Wizardry Redux (120 рецептов) — плагина нет; план — `docs/jei.md`.

## Ice and Fire CE — огромные драконы (поток 9, 2026-10-12)
Мод с огромными сильными существами: `ice-and-fire-dragons` 2.1.13-1.20.1-beta-5 (Modrinth, both, LGPL-3.0, зависимость
Citadel 2.6.3 — уже была от Alex's Mobs). Выбран по умолчанию (рекомендация владельцу через «Связь»); альтернатива — Bosses of
Mass Destruction Forge 1.1.2. Cataclysm/Mowzie's/Lycanites не дублируются.
- **Сервер**: `test-server.sh` — `SERVER OK` (Done за ~29 с на прогретом мире), ошибок Ice and Fire/Citadel в логе нет;
  «Object did not get ID» — только в первом мире после добавления мода (ID блоков сдвинулись), дальше нет.
- **Клиент**: `test-client.sh` — `CLIENT OK` (451 с на облачном CPU). В логе клиента один пункт мода:
  `IllegalStateException: Duplicate registration for type iceandfire:hippogryph` в `IafEntityRegistry.commonSetup`
  (`SpawnPlacements.register`; на сервере его нет). Метод только регистрирует правила естественного спавна 5 существ — это
  серверная логика, на выделенном сервере всё работает; затрагивает лишь одиночный мир на клиенте (правила спавна
  не регистрируются — мобы IaF могут спавниться без проверок места). Для сервера друзей не критично.
- **Цифры** (`scripts/tests/iafcheck.js`): взрослый (стадия 5, `AgeTicks: 3000000`) огненный/ледяной/молниевый дракон —
  500 HP, урон 18, броня 21; только что созданный — 20 HP (стадия 1). Гидра 250 HP, циклоп 150, королева мирмексов 120.
  Дыхание: огонь 2.0 / лёд 2.5 / молния 3.5 за тик ×стадия (конфиг мода).
- **Баланс**: дракон 500 HP с бронёй 21 по формуле Apothic (`a/(a+броня)`) срезает меньше урона, чем в оригинале. Конец игры
  и без того тяжелее расчётного (см. выше «Apothic Attributes»); дракон — вровень с Cataclysm-боссами поздней игры (400–600 HP).
  Конфиг `config/iceandfire-common.toml`: **«Dragon Griefing» = 1** (по умолчанию 0 — драконы ломают любые блоки; 1 — только слабые)
  — чтобы сервер друзей не лишился баз; вернуть 0, если нужно «как в оригинале».
- **Русификация**: у мода уже есть `ru_ru` (98.6 % ключей) и русский бестиарий; недостающие 6 строк (серебряная руда в
  глубинном сланце, необработанное серебро, заряд драконьей молнии, стрела амфитеры, «Бестиарий») — в нашем паке
  `pack/resourcepacks-src/russian-moded/assets/iceandfire/lang/ru_ru.json`, `lang_validate.py lang` — 0 ошибок.
- **JEI**: в jar есть собственный плагин (`IceAndFireJEIPlugin`: кузницы драконов); регистрация плагина происходит после входа
  в мир и в `test-client.sh` не проверяется (как у остальных модов, см. выше) — проверить глазами по `docs/client-checklist.md`.
- **Нагрузка**: драконы — самые тяжёлые мобы по ИИ/полёту; в логе сервера до ~100 тиков нагрузки нет, но замеров с живым
  драконом на ноутбуке (i5-9/RTX 2060) пока нет — проверить в игре. «Dragons Despawn» оставлен `true` (иначе «SERIOUS lag»
  по описанию конфига).
- **Боссы**: таблица обновлена (`docs/bosses.md`/`csv`).
- **Атлас текстур**: переполнение атласа блоков на видеокартах с пределом 16384 исправлено (поток 13) — см. раздел
  «Атлас блоков не помещался в 16384×16384» ниже.

## Wizardry Golems — порт 1.12.2 (поток 12, 2026-10-12)
Мод `mods-src/wizardry-golems` 0.1.0 (оригинал WinDanesz, GPL-3.0) + Extra Golems 20.1.0.2 (skyjay1, zlib; CF без
стороннего скачивания → jar в `pack/mods/`). Заклинания Golemancy призывают `golems:golem` нужного типа и делают его
миньоном Redux (`MinionData`: владелец, время жизни, следование) — своих классов-сущностей нет.
- Сервер: стартует (Done 104 с), `scripts/tests/golemcheck.js` — 6/6 OK: earth 3× clay, fire 2× nether_brick, ice 2× ice,
  lightning 5× маленьких `wizardrygolems:thunderstone`, healing 1× gold; через 200 тиков миньоны живы.
- Маленький голем Extra Golems получает половину здоровья типа (thunderstone: 25 → ~10; в оригинале у «ребёнка» 12).
- При первом запуске Extra Golems дописывает недостающие ключи `golems-common.toml` (WARN, безвредно).
- Клиент: CLIENT OK (меню за 450 с), ошибок по `wizardrygolems` в логе нет.
- Свои голем-типы — `data/wizardrygolems/golems/golem/*.json` с `"hidden": true` и без `blocks`: руками не строятся.

### Wizardry Golems 0.2.0 — этапы 2–3 (поток 12, 2026-10-12)
- 10 своих голем-типов оригинала (flame, winter, spark, lodestone, maelstrom, packed_ice, permafrost, snow, concrete,
  strawthorns) + thunderstone; особые способности — по типу голема (`golem/SpecialGolems`): каст заклинаний Redux/Restored
  своей целью `GolemSpellGoal` (у `GolemBase` нет `ISpellCaster`, `AttackSpellGoal` Redux не подходит), аура тока,
  магический удар Spark, лечение Gold (в оригинале условие события было перевёрнуто и лечение не срабатывало), след Permafrost.
- 36 артефактов — `ArtifactChannel.createArtifact` Redux (Curios: ring/necklace/charm/head/body/belt; слоты head/body/belt
  включаются нашим `data/curios/entities`). Состав призыва и время жизни по артефактам, эффекты на смерти/ударе/аурах
  (`golem/GolemArtifacts`). Нет в Redux → аналоги: Empowerment → атрибут `CAST_POTENCY` (+15%), Ice Shroud → состояние
  Wizardry Restored, снейры/землетрясение — сущность и заклинание Restored (без Restored эти два эффекта молчат).
- Добыча: пул в `ebwizardry:subsets/{uncommon,rare,epic}_artifacts` (таблицу Redux не заменяем; шанс n/(N+n)).
- Living Snow: блок + BlockEntity, бутылка (без маны оригинала — только перезарядка 30 с), генерация `#forge:is_snowy`.
- Исправлено относительно 0.1.0: «Големантия грозы» без артефактов — 2 обычных Thunderstone (как в оригинале), 5 малых — с
  Electric Scatter; аура Thunderstone не бьёт других големов Extra Golems.
- Сервер: `golemcheck.js` 13/13 OK (5 заклинаний, время жизни, аура, каст огня, след, 11 типов, Living Snow, добыча
  163/463 из эпической выборки, кольцо в слоте Curios фейкового игрока → 2 бетонных голема).
- Клиент: CLIENT OK (418 с), ошибок по `wizardrygolems` нет.
- KubeJS/Rhino: `const` внутри блока `if`/`try` обработчика тика даёт «redeclaration» — в тестах только `let`.

### Necromancer's Delight — ND-1 (потоки 12 и 27, 2026-10-17)
Моды `mods-src/necromancers-delight` 0.1.0 (оригинал WinDanesz, аддон Electroblob's Wizardry 1.12.2) и
`mods-src/fishs-undead-rising` (`mod_lavacow` 0.1.0, порт нужных частей Fish's Undead Rising, автор Fish, ARR — NOTICE.md),
решение владельца В13 «полный перенос». В ND-1 — то, что не требует мобов FUR: Stink/Frost Bomb, Corrode, Glowshroom,
эффекты Soiled/Corroded/Infested/Poisonous Spore/Locusts, 17 артефактов (действуют 5), добыча.
- Сервер: `scripts/tests/ndcheck.js` 11/11 OK, SERVER OK. Клиент: CLIENT OK (858 с), ошибок по `necromancersdelight`/`mod_lavacow` нет.
- Снаряды Redux (`MagicProjectileEntity.aim`) вылетают из позиции стрелка прошлого тика (`xo/yo/zo`): в тестах фейкового
  игрока ставить `setPos` + `setOldPosAndRot()`, иначе бомба появляется там, где он стоял раньше.
- Тестовый сервер: «Done» в логе выводится за ~3 мин до первого тика (ModernFix догружает мир после строки) — паузу
  в `TEST_COMMANDS` для тиковых тестов брать от 420 с; мир между прогонами сохраняется — арены чистить за 40 тиков до каста.
- Рецепты бомб требуют предметов FUR (`foul_bristle`, `shattered_ice`) — появятся с мобами в ND-2.

### Fish's Undead Rising — мобы для Necromancer's Delight, ND-2 (поток 27, 2026-10-17)
`mods-src/fishs-undead-rising` 0.2.0 (`mod_lavacow`, порт по декомпиляции FUR 1.16.5 2.4.1, автор Fish, ARR — NOTICE.md):
13 сущностей — Unburied, Mummy, Mycosis, Frigid, Forsaken, Vespa (+ кокон), Banshee, Parasite, Bone Worm, Salamander, Weta,
Mimic; снаряды Death Coil, Acid/Flame Jet, War Small Fireball; эффекты Fear, Fragile, Immolation.
- Сервер: `scripts/tests/furcheck.js` 13/13 OK (призыв, жив через 100 тиков, яйцо, таблица добычи), ndcheck не сломан; SERVER OK.
  Клиент: CLIENT OK (568 с), ошибок по `mod_lavacow` нет (модели и рендеры пекутся при загрузке ресурсов).
- Biome modifier `forge:add_spawns`: несколько тегов в `biomes` — только через `{"type": "forge:or", "values": [...]}`;
  JSON-массив тегов не парсится, и мир не загружается вовсе («Failed to parse … biome_modifier», сервер висит до таймаута).
- Спавн мобов в строениях — свой structure modifier `mod_lavacow:add_spawns` (в Forge 1.20.1 своего типа нет).
- test-client: если Xvfb прошлого запуска умер, а `/tmp/.X11-unix/X99` остался — «glfwInit failed»; удалить
  `/tmp/.X99-lock` и `/tmp/.X11-unix/X99`.
- Предметы приручения Salamander/Weta/Mimic падают с мобов FUR, которых нет в порте, — пока только в творческом разделе.

### Necromancer's Delight — ND-3 (поток 29, 2026-10-17)
`mods-src/necromancers-delight` 0.2.0 (15 заклинаний и 12 артефактов на мобах/предметах FUR, пиявки-миньоны) и
`mods-src/fishs-undead-rising` 0.3.0 (оружие, бомбы, кактусовый шип, грибы-блоки с генерацией). Подробности — docs/wizardry-port.md «ND-3».
- Сервер: `scripts/tests/nd3check.js` 55/55 OK (касты 15 заклинаний и эффекты в мире: призванное оружие, морозный кинжал,
  три бомбы FUR, 6 миньонов с хозяином, грибы, мимикраб из сундука, подмены зомби/скелетов, Bone Bag, грибной талисман,
  мана-пиявка ворует ману и делится с хозяином, цена ×2 и Venom ×0,6, амулет кристалла, корона, пояс шипов, генерация гриба)
  + ndcheck 11/11 + furcheck 13/13, SERVER OK. Прогон: `TEST_COMMANDS=<«#sleep 480»> EXTRA_SERVER_SCRIPTS=<копии трёх скриптов>`.
- Один раз JVM тестового сервера не завершилась после «All dimensions are saved» (висела 10+ мин) — результаты в логе уже есть, убит вручную.
- Миньоны Redux (`MinionData`) телепортируются к хозяину; не приручённые мобы FUR (`removeWhenFarAway`) **деспавнились**, когда хозяин
  уходил дальше 128 блоков (оригинальный `ISummonedCreature` не исчезал) — ND помечает призванных мобов FUR и пиявок
  `setPersistenceRequired()`, убирает их время жизни. Пиявкам следование выключено (как в оригинале), иначе телепорт к хозяину
  снимает их с носителя.
- Тесты с фейковым игроком: хозяин миньонов ищется по UUID в мире (`ServerLevel.getEntity`) — фейкового игрока надо добавить
  в мир (`level.addFreshEntity(fake)`), в конце убрать (`remove(CHANGED_DIMENSION)`). Он не тикает (нет `PlayerTickEvent`) и не
  выбирается командой `/damage`; прямой `spell.cast(...)` не шлёт `SpellCastEvent` Redux — для проверки слать вручную.
- KubeJS/Rhino: у `ItemStack` не видны `getTag()`/`hasTag()` — свойство `stack.nbt`; у сущностей не виден `getUUID()`;
  `new SimpleContainer(2)` Rhino вызывает как varargs `ItemStack...` (контейнер на 1 ячейку).
- «Грибной мертвец» (Mycosis, ND-2) с фиолетово-чёрными кубами над головой и на спине (жалоба владельца): слой `LayerMycosis`
  брал модели грибов как «дополнительные» (`ModelEvent.RegisterAdditional` + `getModel(ResourceLocation)`) — с ModernFix
  `mixin.perf.dynamic_resources=true` (наш конфиг) такая модель приходила missing model. В 0.3.0 грибы — настоящие блоки,
  слой рисует блок-состояние `age=1` (`renderSingleBlock`), как оригинал. В test-client.sh добавлен вывод
  «Unable to load model/Missing textures» по своим модам-портам (в логе этой ошибки не было — missing model молчит).
- Клиент: CLIENT OK (481 с), ошибок по `mod_lavacow`/`necromancersdelight` в логе нет.
  Глазами исправление Mycosis не проверено: в одиночном мире облачного клиента (YSM убран, мир создан кнопками) вход
  обрывается «Сервер отправил неверный пакет» сразу после входа; клиентская причина в latest.log не пишется — осмотр
  Mycosis (оба варианта) остаётся владельцу/следующему потоку.

### Bosses of Mass Destruction + Cerbon's API (поток 9, 2026-10-12, решение владельца В12 «а»)
Владелец ответил «оба», затем «а»: вернуть Cerbon's API и добавить BoMD, с регрессионной проверкой спавна; если у владельца
спавн мобов снова пропадёт — **сразу убрать оба мода** (`mr cerbons-api`, `mr bosses-of-mass-destruction-forge` и Cloth Config,
если его больше никто не использует).
- BoMD Forge 1.1.2 жёстко требует **Cerbon's API ≥ 1.1.0** (`ModLoadingException: requires cerbons_api 1.1.0 or above`) и Cloth Config 11.x.
  Cerbon's API поток 10 убирал из-за общих (не клиентских) миксинов коллизий (`AABB.intersects`, `Shapes.create`, `VoxelShape.collide`,
  `Entity.getBoundingBox/setPosRaw`); теперь возвращён со стороной **both** (на сервере он обязателен для BoMD).
- **Регрессия спавна** (`scripts/tests/spawncheck.js`, свежий мир, сид 12345): с Cerbon's API + BoMD + Cloth Config числа в пределах шума относительно базы —
  summon cow/zombie/iceandfire:troll/alexsmobs:crow OK, яйцо овцы OK; места естественного спавна: зомби 62/200 (база 50), тролль 54/200 (база 47),
  корова 200/200 (база 200); ошибок 0. Просадки нет. Тест проверяет место для моба (`checkSpawnRules` + `noCollision`), а не сам цикл
  `NaturalSpawner` (на сервере без игроков он не работает) — жалобу «не спавнятся мобы» в игре это не исключает, поэтому условие выше остаётся.
- `test-server.sh` — `SERVER OK` (Done за 91 с на свежем мире с генерацией). Параметры боссов в конфиге `bosses_of_mass_destruction.json5`
  (замеры `bossdump.js`): Ночной лич 300 HP / ракета 9 / броня 0; Обсидилит 300 / 16 / 14 (4.4×2); Перчатка незера 250 / 16 / 8 (4×5);
  Цветок пустоты 350 / 12 / 4 (10×8). В пределах Cataclysm-боссов (400–600 HP), отдельный баланс не нужен; конфиг оставлен по умолчанию.
- Арены: `lich_tower` (Overworld), `gauntlet_arena` (Незер), `obsidilith_arena` (Край), Цветок пустоты — в пещере (блок-призыватель).
  Лич включает «вечную ночь» на время боя (`eternalNighttime`), кометы и ракеты с замедлением рвут блоки (`comet.explosionStrength` 4).
- Русификация: у мода `ru_ru` 98.7 %, недостающие 2 строки экрана конфига — в `pack/resourcepacks-src/russian-moded/assets/bosses_of_mass_destruction/`.
- **Регрессионный тест спавна** `scripts/tests/spawncheck.js` (`EXTRA_SERVER_SCRIPTS`, поток 9): `/summon` ванильных и модовых мобов, яйцо
  призыва фейковым игроком Forge, доля допустимых мест для естественного спавна (`SpawnPlacements.checkSpawnRules` +
  `level.noCollision` — то, что ломают миксины коллизий) на тёмной и освещённой площадке. Настоящий `NaturalSpawner` на сервере без
  игроков не работает (нужен ближайший игрок), поэтому проверяется место, а не сам цикл спавна.
  **База (интегратор без Cerbon's API, 2026-10-12):** summon cow/zombie/iceandfire:troll/alexsmobs:crow — OK; яйцо овцы — OK (`CONSUME`);
  места: зомби ночью на тёмной площадке 50/200, тролль 47/200 (остальное отсекает свет), корова на освещённой 200/200, ошибок 0.
  После возврата Cerbon's API прогнать снова: заметное падение долей или ошибки — повод не возвращать.
- **Клиент с BoMD + Cerbon's API**: `test-client.sh` — `CLIENT OK` (442 с). Ошибок BoMD и Cloth Config в логе клиента нет. В логе есть
  `NetworkDirection channel cerbons_api:packets already registered` в `FMLCommonSetupEvent` — но то же самое (повторная регистрация
  в `FMLCommonSetupEvent`) на этом тестовом клиенте происходит у 44 модов сразу (Forge, Aether, Ad Astra, Alex's Mobs, Mowzie's, Ice and Fire…),
  то есть это особенность стенда (событие, похоже, отправляется дважды), а не конкретного мода; на сервере таких ошибок нет. Выше замечание про
  `Duplicate registration … iceandfire:hippogryph` — из той же серии.

## Атлас блоков не помещался в 16384×16384 (поток 13, 2026-10-13)
**Симптом** (облачный `test-client.sh`, Mesa llvmpipe, `GL_MAX_TEXTURE_SIZE` 16384 — как у встроенной графики Intel/AMD APU):
первая загрузка ресурсов падает `Requested atlas size 32768x16384 exceeds maximum of 16384x16384` →
`StitcherException: Unable to fit: superbwarfare:item/lod/bocek`; Minecraft откатывается и грузит ресурсы **без пользовательских
паков** (два «Reloading ResourceManager», в `options.txt` остаётся `resourcePacks:[]`) — слетают MPLOC/RTF+/RTF Extension.
С принудительными паками (ветка `claude/ru-force-packs`, не слита) вместо отката — краш «Stitching». На RTX 2060 (предел 32768) помещалось.

**Причина** — не моды с огромными текстурами сами по себе, а два чужих `assets/minecraft/atlases/blocks.json`, источники которых
не ограничены своим namespace (тип `directory` сканирует папку во **всех** модах):
- Mana and Artifice (`mna`): `{"type":"directory","source":"entity"}` — в атлас блоков попадают **все** `textures/entity/**`
  всех модов: Lycanites Mobs 85 Мпкс (728 спрайтов, почти все 1024²), Alex's Caves 11,5, Cataclysm 8,9, Alex's Mobs 7,2 …;
- Blood Magic: `{"type":"directory","source":"models"}` — все `textures/models/**`: Ice and Fire 48,8 Мпкс (631 спрайт), Blood Magic 10 …

Собственные текстуры предметов/блоков (Superb Warfare 23 Мпкс, Arknights Endfield 19, Arknights Furniture 18) переполнения не дают:
блоки+предметы всей сборки ≈ 97 Мпкс. Свой jar у Lycanites/Ice and Fire атласов не объявляет.

**Исправление** — `pack/kubejs/assets/minecraft/atlases/blocks.json` (ресурс KubeJS; его пак стоит выше `mod_resources`, источники
атласа складываются по всем пакам снизу вверх, поэтому наш `minecraft:filter` срабатывает после `mna` и Blood Magic):
- убрать `entity/**` у `lycanitesmobs`, `alexscaves`, `alexsmobs`, `cataclysm`;
- убрать `models/**` у `iceandfire`, кроме `models/ghost/ghost_chest` (частица блока «Призрачный сундук»).
Эти моды рисуют мобов своими `RenderType` по прямому пути к png, а не из атласа: проверено, что ни одна модель предмета/блока во
всех jar не ссылается на их `entity/`/`models/` (кроме сундука), и что их код не берёт такие спрайты из атласа блоков.
Свои `entity/`-текстуры у `mna` и `models/` у Blood Magic не тронуты (им они нужны). Jar не правились, качество текстур не менялось.

**Замеры** (сумма площадей спрайтов по списку из лога + итоговый размер):
| | спрайтов | площадь | от 16384² | атлас |
|---|---|---|---|---|
| до (без пользовательских паков, после отката) | 35 715 | 285,7 Мпкс | 106 % | не помещается (32768×16384) |
| до (с паками, данные потока 6) | 35 721 | 293,5 Мпкс | 109 % | не помещается |
| после | ~33 440 | ~125 Мпкс | ~47 % | `Created: 16384x8192x2 minecraft:textures/atlas/blocks.png-atlas` |

**Проверка**: `test-client.sh` — `CLIENT OK`, в логе 0 строк `Unable to fit`/`StitcherException`/`exceeds maximum`, одна загрузка
ресурсов со всеми паками (`ru_lang`, just-some-translations, russian-delight, RTF, MPLOC, RTF Extension), `options.txt` сохраняет их
список, меню по-русски. Сервер не менялся (атласы — только клиент).
Если в игре у мобов этих модов, у предметов Ice and Fire или в эффектах Alex's/Cataclysm появится фиолетово-чёрная «missing»-текстура —
это первое подозрение: вернуть нужный путь, сузив регулярное выражение в фильтре.
Запас: новые моды с большими текстурами в `block/`/`item/` или новые «всеобщие» `directory`-источники снова увеличат атлас — после
добавления модов смотреть в логе клиента строку `Created: …blocks.png-atlas` (должно быть ≤ 16384×16384).

## Катаклизмы: Weather2 + CoroUtil, Natural Disasters!, «Оберег от катаклизмов» (поток 15, 2026-10-13)

Идея владельца: «мод на катаклизмы — торнадо, цунами». Решение владельца (В15): первый вариант ставим, третий и четвёртый на усмотрение, нужны разные эффекты,
дубли убрать, постройки **должны ломаться**, нужен блок защиты (только креатив), создающий зону без катаклизмов.
Выбор из 4 вариантов — таблица в PR/докладе. Все моды — Modrinth (`url` в `.pw.toml`, сторона `both`), чужие jar в репозиторий не кладём.

### Weather2 2.8.3 + CoroUtil 1.3.7 (Corosus / ProtoManly, All Rights Reserved)
Штормовые фронты по температурам биомов, торнадо и циклоны F0–F5/C0–C5, град, песчаные и снежные бури, ветер, листопад, сирены, датчик торнадо,
флюгер, анемометр, «Отражатель бурь». Заменяет ванильные облака, дождь и снег частицами (`lockServerWeatherMode = 0`, Serene Seasons работает по температуре биомов).
**Настройки сборки** (`pack/config/Weather2/Tornado.toml`, `Storm.toml`; формат TOML, мод создаёт остальные файлы сам):
- `Storm_Tornado_grabBlocks = true`, `GrabCond_List = true`: торнадо **рвут постройки** по списку (деревянное: заборы, двери, ступени, доски, шерсть, кровати, знаки,
  листва + брёвна, стекло, кнопки, посевы, ковры), но **не** сундуки/бочки/печи/блоки земли и камня (содержимое не пропадает);
- подхват игроков, мобов и жителей включён, урон от падения выключен; лимиты: 200 летящих блоков, 5 блоков за тик;
- шансы F4/C4 = 10 %, F5/C5 = 5 % (в оригинале 20/10); частота — по умолчанию (шторм-«убийца» на сервер раз в час, без перепада климата — раз в ~3,3 часа), молнии огонь не ставят.
Отключить разрушения: `Storm_Tornado_grabBlocks = false`; только ветер и листва: `Aesthetic_Only_Mode = true` (`Misc.toml`).

### Natural Disasters! 2.0.0 (MCreator, YouTuber, All Rights Reserved)
Бедствия — сущности `natural_disasters:*`: метеор и метеоритный дождь, цунами, лесной пожар, кислотный дождь, воронка, землетрясение (+ торнадо и песчаная буря).
Настроек у мода нет (MCreator). Вручную: меню по клавише (**бинд «Открыть меню бедствий»**), яйца призыва, `/summon`.
**Найдено**: «случайный спавн» (галка в меню, в мире по умолчанию выключена) бросает кубик **каждые ~100 тиков (5 с) на игрока** и спавнит бедствие в ±200 блоков — непригодно.
Наш мод `disaster-ward` включает галку один раз при первом запуске мира (`WardData.naturalInit`) и управляет паузами: бросок раз в **36 000–96 000 тиков (30–80 мин) на игрока**
(`naturalDisasters.minTicks/maxTicks`; механика: таймер `NaturalSpawningNaturalDisastersTimer` в данных игрока не даёт дойти до нуля, пока не прошла наша пауза; на отдельный бросок
приходится одно из 10 бедствий, торнадо и песчаная буря отсеиваются). Выключить: `naturalDisasters.enabled = false`.
**Дубли убраны**: `disabledEntities = [natural_disasters:tornado, natural_disasters:sandstorm]` — события `EntityJoinLevelEvent` отменяются везде (в меню мода кнопки «Торнадо»/«Песчаная буря» ничего не делают).
Разрушение построек у его бедствий — встроенное (метеоры, цунами, пожар, землетрясение), отключить нельзя; содержимое контейнеров он может разрушить вместе с блоком.

### Оберег от катаклизмов — `mods-src/disaster-ward` (наш мод, MIT)
Блок `disaster_ward:ward`: **нет рецепта и лута** (`noLootTable`, при ломании ничего не падает), взрывоустойчив, свет 7; берётся только из творческой вкладки («Функциональные блоки»/«Операторские»)
или `/give @s disaster_ward:ward`. Ставится и снимается кем угодно; позиции — `SavedData` мира (`data/disaster_ward.dat`, переживают рестарт).
Зона — **квадрат по X/Z на всю высоту**, радиус `ward.radius` (по умолчанию 64, в `config/disaster_ward-common.toml`):
- **Weather2**: ~~оберег регистрируется как «Отражатель бурь» (радиус Weather2 — 128)~~ — с потока 22 бури разворачивает сам оберег по единому радиусу, см. «Паразиты»;
- **Natural Disasters!**: сущности пространства имён `natural_disasters` не появляются в радиусе `radius + edgeMargin` (16) и убираются, если зашли (проверка раз в 10 тиков);
  уже сделанное (вода цунами, воронки, лава) остаётся;
- Ограничение: «апокалипсисы»/вулканы Extreme Natural Disasters не ставим, поэтому ими не управляем.

**Extreme Natural Disasters 1.5 НЕ ставим**: землетрясения, провалы и метеориты дублируют Natural Disasters!, остальное — вулканы и WIP-«апокалипсисы» (потоп, солнечный, снежный, помечены автором как лагучие);
его бедствия — не сущности, защиту пришлось бы делать миксинами в чужой код. Если владелец захочет вулканы — отдельный шаг.
Метеориты/цунами других авторов (Skyfall, This Tsunami) тоже не добавлены: Natural Disasters! уже даёт метеоры и цунами.

**Нагрузка** (`disasterload.js`, тестовый сервер, свежий мир seed 12345, застройка The Lost Cities; машина медленная — базовый такт ~50–85 мс при догенерации, поэтому важна разница):
| что идёт 600 тиков (30 с) | мс/тик среднее | p95 | вывод |
|---|---|---|---|
| торнадо Weather2 стадии F2 над городом (без Natural Disasters!) | 49,7 – 49,9 (до: 50,1 – 50,6) | 68 – 69 | **нагрузки нет**, сервер держит 20 TPS; торнадо за 30 с унёс 376 из 1352 досок 13×13×8 «домика» под собой |
| метеоритный дождь + землетрясение + лесной пожар Natural Disasters! вместе | 205 (до: 67) | 450 | **~5 TPS**, из 128×128 выборки пропало ~13 % непустых блоков |
| то же, но лимит `maxConcurrent = 2` (третье бедствие отклонено) | 163 (до: 87) | 383 | ~6 TPS |
Вывод: Natural Disasters! тяжёлый — по умолчанию `maxConcurrent = 1` (одновременно идёт одно бедствие на измерение; для одного бедствия замер не делался), случайный бросок — раз в 30–80 мин на игрока.
Админ, которому нужно больше, ставит `maxConcurrent = 0` (без лимита). Лимит действует и на ручной призыв. Торнадо Weather2 с этим лимитом не связан.
Оба замера и схема прогона — в `scripts/tests/disasterload.js` (в «режимах» `weather2` / `nd` / `both`, константа `DL_MODE`).

**Русификация**: `weather2`, `coroutil`, `natural_disasters` — `pack/resourcepacks-src/russian-moded/assets/…` (`lang_validate` 0 ошибок); оберег — собственный `ru_ru.json` в его jar.

**Тест** `scripts/tests/disasterward.js` (+ `disasterload.js`; запуск: `EXTRA_SERVER_SCRIPTS=<папка только с этими двумя файлами> TEST_COMMANDS=<«#sleep 260»+list> scripts/test-server.sh <папка>`): оберег ставится; зона внутри/снаружи;
бедствие у зоны не появляется; вне зоны появляется; `tornado`/`sandstorm` Natural Disasters! отключены; бедствие, телепортированное внутрь, исчезает; Weather2 видит отражатель (радиус 128) и перестаёт видеть после ломания;
галка случайных бедствий включена; оберег ничего не роняет; после ломания зона пропадает. Все проверки `DWCHECK … OK`.

**Проверка клиента** (поток 15, `test-client.sh`, сборка с Weather2, CoroUtil, Natural Disasters!, disaster-ward): `CLIENT OK` (492 с). В логе клиента нашлась единственная
претензия к нашим модам — `Skipped language file: disaster_ward:lang/ru_ru.json` (лишняя запятая в JSON); исправлено после прогона (правка только файла перевода в jar оберега, повторный прогон клиента не делался).
Предыдущий `Mixin apply failed lycanitesmobs.mixins.json:LightTextureMixin` — прежний (см. запись про Lycanites выше). Что смотреть глазами: `docs/client-checklist.md` + вид штормов Weather2 (облака-частицы, дождь) с шейдерами и Distant Horizons,
блок «Оберег от катаклизмов» в творческой вкладке «Функциональные блоки» (текстура, русское название, подсказка с радиусом).

## Паразиты: Fungal Infection: Spore, SRP System; Оберег против заражения (поток 22, 2026-10-17, решение владельца В24)

### Fungal Infection: Spore 2.2.0j (Harbinger, All Rights Reserved) — `both`, Modrinth
Заражение мицелием, заражённые люди/жители/разбойники, эволюция в «эволюционировавших» и «гипер», курганы (Mound), Гастгебер, Протомозг-улей,
бедствия-«калимити». Русский перевод — в самом jar (914 из 917 строк), недостающие 3 — `pack/resourcepacks-src/russian-moded/assets/spore`.
**Ограниченное распространение** — `pack/config/sporeconfig.toml` (заголовок файла перечисляет правки):
| что | было | стало | зачем |
|---|---|---|---|
| курган: кулдаун заражения блоков / взросление | 30 с / 900 с | 120 с / 2400 с | в 4 раза реже заражает, взрослеет ~40 мин |
| радиус кургана по возрастам | 5/10/15/20 | 4/6/8/10 | пятно заражения вдвое меньше |
| Гастгебер: радиус / период | 25 / 30 с | 10 / 120 с | — |
| эволюция: таймер / убийств | 300 с, 1 | 600 с, 3 | эволюция требует, чтобы заражённый реально охотился |
| гипер-эволюция | 600 с, 7 | 1200 с, 10 | — |
| «мир меняется» (Proto World Modifier) | 3 Протомозга | 6 | всемирное усиление — только при запущенной вспышке |
| Протомозг и калимити грузят чанки | да | **нет** | заражение не живёт в пустых чанках, нет фоновой нагрузки |
| шанс рейда Протомозга | 3 % | 1,5 % | — |
| лимиты числа: обычные/эволюц./гипер/органоиды | 60/35/20/40 | 40/20/10/20 | потолок нагрузки |
| мобкап спавна заражённых | 40 | 25 | — |
| «выключить ванильную музыку», музыка в меню | да, да | нет, нет | у нас своя музыка/моды звука |

**Заражение мобов наших модов**: Spore превращает умершего с мицелием только по своему списку (ваниль, гвардейцы, рекруты). Добавлено datapack-ом
`pack/kubejs/data/spore/spore_mob_conversion/mm_moded.json` по тегам `disaster_ward:spore_to_licker` (животные → `spore:licker`, 249 видов)
и `disaster_ward:spore_to_claw` (монстры → `spore:claw`, 433 вида): Ice and Fire, Lycanites, Cataclysm, Mowzie's, Aether (+Redux/Deep/Ancient/Lost),
Alex's Mobs/Caves, Twilight Forest, Undergarden, Blue Skies, Quark, Bumblezone, Deeper and Darker, Arknights-моды, ванильные животные.
Генератор — `scripts/spore_infection_map.py <выгрузка EDUMP>` (KubeJS `ServerEvents.loaded` → `ForgeRegistries.ENTITY_TYPES`, строка `EDUMP <id> <категория> boss=<…>`);
он же пишет `infection_mappings` в конфиг SRP System. Записи тегов `required: false` — пропавший мод не ломает тег.
Не заражаются нигде (тег `disaster_ward:infection_immune`, 40 видов + `forge:bosses`): драконы Ice and Fire, морской змей, гидра, циклоп, горгона, Лич,
Феррус/Фростмо/Умвути/Нага (Mowzie's), боссы Lycanites, Watcher/Prowler/Ignited Revenant/Coralssus/Ender Golem (Cataclysm), Warden, Elder Guardian и т. п.
**Fungal Infection Spore: MINI additions НЕ ставим**: это datapack-мод с 16 tick-функциями (`@e`-селекторы каждый тик) и «ордалиями»-рейдами Протомозга —
против «ограниченного распространения» и лишняя нагрузка. Его идея (животные → licker) перенесена в наш datapack (Minigato668, MIT). Другие аддоны
(Spore:SRP!, Unhinged, Cataclysm-аддон) — не ставили, это усиление, а не связки.
Дубли: у Spore нет `forge:`-тегов металлов/пыли — Almost Unified объединять нечего, `hardrefs.py` не нужен.

### SRP System 0.0.1 («One-Click Parasite Transformation», MIT) — `both`, Modrinth
«Вселение в любых» из Scape and Run: Parasites: существо с мицелием Spore получает эффект «Заражение паразитом», при ≤ 30 % здоровья превращается
(по `infection_mappings`, иначе в `spore:inf_human`); перерождение и «колония» Протомозга. На Modrinth помечен «только сервер», но **регистрирует эффекты
и звуки** — без мода на клиенте реестры не совпадут, поэтому `both` (клиенту он ничего не добавляет, кроме названий эффектов).
**Найдено в коде и выключено** (`pack/config/srpsystem-common.toml`):
- «минимальный урон» ставил здоровье цели напрямую (`setHealth`) мимо брони — убитый так игрок умирал **без события смерти** (вещи не выпадали
  и пропадали при возрождении, могилы не срабатывали) → `target_mod_ids = []` (заодно выключены «Вздутие»-взрывы, они на том же списке);
- «колония ослабляет лечение»: на **каждое** лечение любого существа искал колонию в радиусе 100 → `enable_colony_heal_reduction = false`, радиус колонии 32;
- превращение боссов/драконов при 30 % здоровья — защищает disaster-ward (`immuneBosses`, тег выше; проверено на визере).
Перевод эффектов — `pack/resourcepacks-src/russian-moded/assets/srpsystem` (в jar нет ни одного lang-файла). Звук «колонии» в jar назван
`node.ogg`, а `sounds.json` ссылается на `nude` — звука не будет (ошибка мода, на игру не влияет).
**Решение: ставим** — совместим после этих настроек, нагрузка — см. замер ниже.

### Оберег (disaster-ward 1.1.0) — доработка
- **Единый радиус** `ward.radius` = **96** (было 64 у Natural Disasters! и зашитые 128 у Weather2). Оберег больше не регистрируется «Отражателем бурь»
  Weather2 (там радиус зашит в коде), старые записи из сохранения снимаются при запуске. Бури Weather2 (стадия «формируется» и выше) разворачивает сам оберег,
  когда центр бури ближе `radius + edgeMargin` (раз в секунду, `aimAwayFromCoords`). Отражатели Weather2, которые крафтят игроки, работают как раньше.
- **Бедствие снаружи не ломает блоки внутри** (`protectBlocks`):
  - Natural Disasters! и паразиты Spore — «контекст бедствия»: миксин на `ServerLevel.tickNonPassenger/tickPassenger` отмечает, что тикает существо
    из `protectedNamespaces`/`parasites.namespaces`, миксин на `Level.setBlock`/`destroyBlock` отклоняет изменения в зоне (в том числе `/setblock`/`/fill`,
    которые MCreator-бедствия запускают от своего имени); взрывы (`ExplosionEvent.Detonate`) в этом контексте или от такого существа не рушат блоки зоны
    (иначе выпал бы лут без разрушения). Без оберега в измерении — одна проверка поля.
  - Weather2 — торнадо рвёт блоки через отменяемое `BreakEvent` от `weather2FakePlayer` (включено `blockBreakingInvokesCancellableEvent = true`
    в `pack/config/Weather2/Misc.toml`), в зоне событие отменяется.
  - **Не защищено**: вода цунами и огонь пожара, поставленные снаружи, дальше растекаются/распространяются по ванильным правилам и могут зайти в зону;
    песок от песчаной бури Weather2; падающие блоки землетрясения, упавшие в зону.
- **Заражение Spore в зоне**: блоки не заражаются (тот же контекст — курган/Гастгебер/Протомозг снаружи не трогают блоки внутри), существа Spore
  (заражённые, курганы, ульи, споры-«запахи», снаряды) не появляются ближе `radius + edgeMargin` (16), эффекты заражения (мицелий, SRP) в зоне не накладываются
  и снимаются с вошедших (`cureInZone`, включая игроков).
- **Заражённые не входят в зону** — выбран вариант «отталкивание в полосе + убрать внутри»: в полосе `edgeMargin` у края паразит раз в 5 тиков получает толчок
  наружу, сбрасывает путь и цель внутри зоны; если всё же оказался внутри (телепорт, падение, призыв в обход) — убирается без лута. Почему так: чистое
  отталкивание не держит телепортирующихся/летающих калимити, а деспавн на границе позволил бы «стирать» боссов, подманивая их к краю; полоса снаружи
  отталкивает, удаление — только внутри. Ограничение: стрелки и плевки заражённых из полосы могут долететь в зону (снаряд убирается проверкой раз в 5 тиков).
- Интеграция без зависимостей: Spore/SRP узнаются по пространству имён и id эффектов, Weather2 — рефлексией; без этих модов оберег работает и не падает.
- Конфиг `pack/config/disaster_ward-common.toml` (новые: `protectBlocks`, `steerStorms`, раздел `[parasites]`).

**Тест** `scripts/tests/sporeward.js` (запуск: `EXTRA_SERVER_SCRIPTS=<папка только с ним> TEST_COMMANDS=<файл «#sleep 420»> scripts/test-server.sh <папка>`,
свежий мир). Все проверки `SWCHECK … OK`: радиус 96 и граница; паразит в центре и курган у края не появляются, снаружи появляется; корова в зоне не получает
мицелий, снаружи получает мицелий и SRP-заражение, после входа в зону эффекты сняты; визер (босс) не заражается; заражение блоков Spore (`SpreadInfection`
кургана в его контексте) — внутри 0 блоков, снаружи 131; взрыв из контекста бедствия на краю — внутри 0 разрушено, снаружи разрушено;
телепортированный внутрь паразит убран; паразит в полосе вытолкнут наружу (x 104 → 115); `BreakEvent` торнадо Weather2 в зоне отменён, снаружи нет;
буря Weather2 у края развёрнута; оберег не в списке отражателей Weather2; **землетрясение Natural Disasters! у угла зоны** за ~15 с разрушило 651 блок
диагонали снаружи и 0 внутри (отклонено 449 изменений). `disasterward.js` обновлён (проверка отражателя Weather2 → «не отражатель»).
**Не проверено скриптом**: настоящий торнадо Weather2, рвущий постройку на краю зоны (проверено только событие разрыва и разворот бури); метеоры/цунами/пожар
целиком (механизм тот же, что у землетрясения); естественное распространение кургана во времени (проверен вызов его метода заражения в контексте тика);
отталкивание калимити и летающих; вход клиента на сервер с SRP System (клиентский прогон не делался).

### Оберег и заражение: фактическая проверка, оберег 1.2.1 (поток 52, 2026-10-19, просьба владельца)
Вопрос владельца: «блокирует ли наш Оберег распространение мицелия и лечит ли заражённых игроков». Проверено на тестовом сервере (1.20.1, Forge 47.4.26, сборка целиком,
свежий мир): `scripts/tests/wardinfect.js` (запуск и конфиг для «настоящего кургана» — в шапке файла), игрок — Forge `FakePlayer` в режиме **выживания**, добавленный в мир
(`level.players` его видит), мобы — настоящие. 32 проверки `WICHECK … OK`, 0 `FAIL` (до исправления 1.2.1 две проверки про выгруженный чанк оберега были `FAIL`). Оберег (0,150,0), радиус 96: внутри x,z ∈ [−96; 96], запас `edgeMargin` 16 (до 112).

**Что есть в сборке** (по jar): у Spore нет заражения биомов и нет «заражённых» биомов — только блоки (`spore:infested_*`, `biomass_block`, мембраны, фолиаж; тег `spore:fungal_blocks`), биом при заражении не меняется
(`dark_forest → dark_forest` после 2000+ заражённых блоков). Распространяют блоки существа `spore:*` (по коду jar: Курган, Гастгебер, Протомозг, Опухоль улья, Щупальце; метод `SpreadInfection`
в тике существа), а также игрок предметом **грибная костная мука**. «Scape and Run Parasites» (SRP) как мод в сборке **нет** — стоит только SRP System (эффекты и превращение); у Chaos Persists
механики заражения мицелием нет (по составу jar: случайные тики — у урожаев, «кротовьей земли», кристальных муравьёв и т. п.; биомы — статичные измерения мода).

**1. Мицелий и блоки заражения**
| что | результат |
|---|---|
| заражение внутри зоны, включая последний столбец x = 96 | **0 блоков**: и ручной вызов `SpreadInfection` в контексте кургана (центры x = 100, 97, 96, 60), и **настоящий курган** снаружи (x = 113, кулдаун 1 с, радиус 30 из тестового конфига, 17 с: снаружи 2038 блоков, внутри 0 — контрольный курган вне зоны заразил 645 блоков на тех же x ≤ 96) |
| первый столбец снаружи x = 97 и полоса запаса 97–112 | **заражается** (запас у края защищает от *появления* паразитов и отталкивает их, но блоки в полосе не защищены: защита блоков — ровно по радиусу) |
| заражение снаружи заходит внутрь | **нет**: курган в 113 с радиусом 30 дотягивается до x = 83 — заражение обрывается на x = 97 |
| уже лежавшее внутри заражение | **остаётся** (оберег его не убирает и не лечит; блок, поставленный командой, цел и через 17 с) |
| биомы | не меняются (ни Spore, ни оберег биомы не трогают) |
| **не блокируется** | действие игрока: грибная костная мука в руке — в зоне заразила 134 блока, снаружи 297 (это не тик паразита; сознательно не закрываем — игрок сам решает) |

**2. Заражённые игроки (выживание)**
- Снаружи на игрока ложатся все 14 эффектов Spore и SRP System; при **входе в зону** снимаются **`spore:mycelium_ef`** и **`srpsystem:infection`** — через **≤ 5 тиков (0,25 с)** (проверка раз в 5 тиков; в тесте ровно 5). Повторно в зоне эти два эффекта не накладываются (событие `MobEffectEvent.Applicable` → отказ).
- Край: x = 96,4 — вылечен, x = 97,4 — не вылечен (граница лечения = граница зоны, без запаса +16).
- **Остаются** (это не «заражение», а боевые эффекты существ Spore/SRP; список — `infectionEffects` в конфиге оберега): `spore:biled`, `spore:corrosion`, `spore:frostbite`, `spore:ignitable`, `spore:madness`, `spore:marker`,
  `spore:starvation`, `spore:symbiosis`, `spore:uneasy`, `srpsystem:boom`, `srpsystem:nude`, `srpsystem:reborn`. Они сами заканчиваются по времени; новые наложить в зоне можно (паразиты в зону не заходят, поэтому на практике их источника нет).

**3. Мобы**: заражённые снаружи житель и зомби (мицелий + SRP) после входа в зону лечатся (за те же ≤ 5 тиков); паразиты `spore:*` (человек, житель, рыцарь, прыгун, курган, хаск), телепортированные внутрь, **убираются** (6 из 6);
в полосе запаса паразита выталкивает (`sporeward.js`). Боссы не заражаются нигде (`immuneBosses`, визер).

**4. Ограничения**
- **Измерения**: оберег действует **только в том измерении, где стоит** (таблица оберегов — по измерениям). Оберег в Верхнем мире Нижний мир и Энд не закрывает: там курган на тех же координатах появился и заразил 208 блоков.
  Поставленный в Нижнем мире — работает так же (0 блоков, курган убран); в Энде — ставится и действует. Нужен свой оберег в каждом измерении, где нужна защита.
- **Выгруженные чанки**: защита блоков (`setBlock` в контексте паразита) от загруженности чанка оберега не зависит. **Найдено и исправлено в 1.2.1**: проверка раз в 5 тиков (лечение, удаление и выталкивание паразитов)
  пропускала оберег, чей чанк выгружен (`level.isLoaded`), — при обереге в 96 блоках от принудительно загруженного угла зоны житель с мицелием **не лечился**, а `spore:inf_human` **не убирался** (на тесте FAIL до исправления);
  теперь проверка идёт всегда (в выгруженных чанках сущностей нет — стоит копейки).
- В незагруженных чанках зоны ничего не тикает: ни курган, ни мобы — заражаться нечему.
- Заражение блоков остаётся возможным для действий вне «контекста паразита»: костная мука игрока, команды `/setblock`/`/fill` (так и задумано).

**Таблица для владельца: что Оберег снимает с игрока** (`scripts/tests/wardeffects.js`: эффект наложен снаружи зоны на игрока в выживании, игрок перенесён в зону, через 25 тиков проверка; затем попытка наложить в зоне). «Источник» — по названию/описанию в jar.
| id | мод | что делает | снимает? (тест) | в зоне накладывается? | предложение |
|---|---|---|---|---|---|
| `spore:mycelium_ef` («Мицелий») | Spore | заражение мицелием: голод, затем урон `mycelium_overtake`, смерть → заражённый | **да** (≤ 5 тиков) | нет | уже снимается |
| `srpsystem:infection` («Заражение паразитом») | SRP System | при ≤ 30 % здоровья превращает в заражённого | **да** | нет | уже снимается |
| `spore:madness`, `spore:uneasy` | Spore | безумие/тревога: галлюцинации, помехи; наносят заражённые и калимити | нет | да | **нет**: боевые дебаффы существ, в зоне источника нет; проходят сами |
| `spore:marker`, `spore:starvation`, `spore:biled`, `spore:corrosion`, `spore:ignitable` | Spore | метка для охотников, голод, жёлчь, коррозия, воспламеняемость | нет | да | **нет**, то же |
| `spore:frostbite`, `spore:symbiosis` | Spore | «полезные» (BENEFICIAL) эффекты для заражённых/симбионтов | нет | да | **нет**: не вред |
| `spore:stunt` | Spore | есть в lang, **в реестре нет** | — | — | — |
| `srpsystem:reborn`, `srpsystem:nude`, `srpsystem:boom` | SRP System | возрождение заражённым, «обнажение», вздутие-взрыв (выключено `target_mod_ids = []`) | нет | да | **нет** |
| `lycanitesmobs:plague` («Чума») | Lycanites | яд и **заражает ближних существ** чумой на уровень ниже (единственный по описанию «заразный» эффект вне Spore) | нет | да | **на решение владельца**. За «добавить»: настоящая зараза. Против: в зоне Lycanites-мобы разрешены, игрок станет неуязвим к их чуме (баланс боёв). Рекомендация: **нет** (оставить как боевой яд) |
| `lycanitesmobs:decay`, `ebwizardry:decay` | Lycanites, Wizardry Redux | «гниение»: меньше исцеления / урон со временем | нет | да | **нет** |
| `cataclysm:ghost_sickness` («Призрачная болезнь») | Cataclysm | дебафф от призрачных мобов | нет | да | **нет** (боевой дебафф босса) |
| `irons_spellbooks:blight` («Порча») | Iron's Spells | −5 % урона и −10 % лечения за уровень | нет | да | **нет** |
| `clanginghowl:neurotoxin`, `ars_elemental:venom`, `necromancersdelight:poisonous_spore` | Clanging Howl, Ars Elemental, Necromancer's Delight | яды/нейротоксин/ядовитые споры (от оружия, заклинаний, еды) | нет | да | **нет** |
| `bobsoriginiumdream:distortion`, `oristop`, `oristormeffect`, `afraid`, `bleed` | Bob's Originium Dream | дебаффы ориджиниума и бури (искажение, запрет, буря, страх, кровотечение) | нет | да | **нет**: бури и рост ориджиниума Оберег и так не пускает в зону |
| `minecraft:poison`, `wither`, `hunger`, `nausea`, `weakness` | ваниль | обычные яды (контроль) | нет | да | нет |
| **атрибут `bobsoriginiumdream:orillness`** («Орипатия», индекс рудной болезни) | Bob's Originium Dream | накапливается от урона `oridamage` (кристаллы, мобы, буря), гейт — игровое правило `oriillness`; лечится лекарствами мода | **нет** (30 → 30 в зоне) | значение не блокируется | **на решение владельца**. Это прогрессирующая болезнь с лекарствами, а не эффект; бесплатный сброс в зоне обесценит медицину Bob's. Рекомендация: **нет**; источники (рост ориджиниума, ядра, буря) в зоне и так закрыты |
| атрибут `bobsoriginiumdream:oridisease` (тип болезни) | то же | тип рудной болезни, ставится при заражении | нет (2 → 2) | — | как орипатия |
| атрибуты `orillnessprotect`, `oricompletelyprotect` | то же | защита от орипатии (броня «oriprotect», чары) | не затрагиваются | — | — |
Список снимаемых эффектов — `parasites.infectionEffects` в `pack/config/disaster_ward-common.toml`; добавление id туда — одна строка (применяется и к «не накладывается в зоне», и к «снимается»). Контроль настоящих SRP (Viral, Primitive и т. п.): мода Scape and Run Parasites в сборке нет, этих эффектов нет.
Alex's Mobs, Mowzie's, Cataclysm (кроме призрачной болезни), Iron's (кроме порчи): заразных эффектов/болезней по названиям и описаниям эффектов в jar не найдено (просмотрено 999 эффектов сборки по ключевым словам infect/disease/plague/virus/parasite/fung/spore/mycel/sick/pathogen/toxin/blight/decay/corrupt/fever).

**Исправление в оберег 1.2.1**: `WardEvents.onLevelTick` — убрана проверка `level.isLoaded(ward)` (см. выше). Версия мода 1.2.0 → 1.2.1, jar `pack/mods/disaster-ward-1.2.1.jar`. Откат — revert PR (вернётся 1.2.0 с дырой для выгруженного чанка оберега).
Версию сборки не поднимали (правило потока 47 ещё не влито).
**Варианты, которые НЕ делали** (меняют поведение): (а) очищать уже лежащее заражение в зоне (периодический обход блоков `spore:infested_*` → исходные; нагрузка и «возврат» блоков — решение владельца);
(б) защищать полосу запаса 97–112 от заражения блоков; (в) запретить костную муку в зоне; (г) добавить в `infectionEffects` боевые эффекты (`spore:madness`, `spore:marker`, …); (д) если появится настоящий SRP (`srparasites`) — добавить его в `parasites.namespaces`.

## Ориджиниумная буря Bob's Originium Dream + оберег 1.2.0 (поток 32, 2026-10-18, решение владельца)
Владелец: «пусть буря имеет небольшой шанс произойти, однако монстры не могут появляться на территории оберега, но проходить могут сквозь неё»;
постройки в зоне от падающих блоков и молний бури защитить, ночь от бури не трогать. Позже (по #89): «Правила на рост ореджиниума включи, пусть растёт
потихоньку, медленно не заходя на зону оберега».

**Как устроена буря** (код 1.1.1, MCreator; переменные `bobsoriginiumdream_worldvars` — отдельно на каждое измерение):
- Запуск — `OristormpredictProcedure` на `PlayerXpEvent.XpChange`: каждое получение опыта (каждая сфера) +1 к счётчику `ori_storm`; при
  `ori_storm ≥ oristormfrequency` (по умолчанию 200) центр бури — ±100 блоков от игрока, сообщение «源石风暴将在附近发生», через `oriwait`
  (12000 тиков = 10 мин, ожидание `queueServerWork` — в памяти) ставится ночь (`setDayTime(13000)`) и `oristorm = true`. Шанса нет — это счётчик,
  общий для всех игроков измерения; ферма опыта вызывала бы бурю постоянно.
- Тик бури — `OristormbeginProcedure` (`LevelTickEvent`, END), призывы на высоте 200 в квадрате **±300** от центра, `S = oristormsummon`:
  червь `evildustoriworm` (иммунен к падению) и большой огненный шар вниз — раз в S/4 тиков; ядро ориджиниума (падающий блок) — раз в S/2;
  молния — раз в S; **блок бури** (падающий) — раз в 2·S, через 100 тиков после приземления ставит шип `oristorm_spike` **50×53×57**
  (взрыв силы 4 без разрушения + структура поверх рельефа). Длительность **5000 − 10·S** тиков. Чем больше S, тем реже призывы и короче буря:
  | S | длительность | черви и шары | шипы |
  |---|---|---|---|
  | 10 | 4900 тиков (4 мин) | ~2450 + ~2450 | ~245 |
  | 40 (по умолч.) | 4600 (3,8 мин) | ~460 + ~460 | ~57 |
  | **200 (сборка)** | **3000 (2,5 мин)** | **~60 + ~60** | **~7** |
  | ≥500 | 0 — буря сразу кончается | — | — |
- Ошибка мода: если сервер выключили во время ожидания `oriwait`, флаг `oristorm_begin` остаётся в сохранении, и буря в этом измерении больше не начинается.
- Прочие правила: `alloworigrow` — рост ориджиниума (ядра разрастаются блоками, урон 8 рядом, из ориджиниума появляются мобы); `oribreak` —
  снаряды сарказа-заклинателя (`orimagic`) ломают блок, куда попали (к буре не относится).

**Настройки сборки** (`pack/kubejs/server_scripts/oristorm_rules.js`, один раз на мир, флаг `mm_oristorm_rules` в persistentData сервера —
потом правила можно менять вручную): `oristorm=true`, `oristormfrequency=100000` (опытом бурю практически не вызвать), `oristormsummon=200`,
`alloworigrow=true` (рост — «потихоньку», см. ниже), `oribreak=false`. Поручение предлагало `oristormsummon≈10` — по коду это в ~40 раз больше призывов (таблица выше), поэтому 200.

**Оберег 1.2.0** (`mods-src/disaster-ward`, `OriStorm.java`, раздел `[oriStorm]` в `disaster_ward-common.toml`):
- **Шанс вместо счётчика** (`roll`): раз в `rollTicks` (12000 = 10 мин) в Overworld, если там есть игрок в выживании, бросок с шансом
  `rollChance` (0,055) → запуск бури у случайного такого игрока тем же кодом мода (`OristormpredictProcedure.execute`). Среднее время до бури
  12000 / 0,055 ≈ 218 000 тиков **≈ 3 часа игры** (+10 мин предупреждения). Выключить бури: `/gamerule oristorm false`.
- **Зона** (`protect`): наши обработчики `LevelTickEvent` END с приоритетами HIGHEST/LOWEST окружают тик бури; призывы из `stormEntities`
  (червь, огненный шар, молния, падающий блок) ближе `radius + edgeMargin` отменяются (`EntityJoinLevelEvent`, HIGHEST), изменения блоков в зоне
  в это время отклоняются (контекст `WardGuard`). Тик блока бури (`ServerLevel.tickBlock`, миксин) — тоже контекст: шип не ставит блоки в зоне,
  сам блок бури в зоне убирается (иначе взрывался бы раз в 5 с). Червь, зашедший снаружи, и черви из яиц/команд/обычного спавна не трогаются.
- **Незагруженные чанки** (`skipUnloaded`): призывы бури в незагруженных чанках пропускаются. Причина — замер: мод при падающем блоке сначала
  ставит воздух в точке появления (`FallingBlockEntity.fall` → `setBlock`), а Animus на появлении молнии читает блок под ней — оба **синхронно
  грузят/генерируют чанк** в тике сервера (jstack: `ServerChunkCache.getChunkBlocking` из `OristormbeginProcedure` и `AnimusEventHandler.onLightningStrike`).
- **Зависший запуск** (`fixStuckStart`): при старте сервера снимается `oristorm_begin` без идущей бури.
- Ночь во всём измерении от бури не трогаем.

**Рост ориджиниума** (`alloworigrow=true`; оберег 1.2.0, `OriGrowth.java`, раздел `[oriGrowth]`; @Pseudo-миксины на процедуры Bob's) — факты из кода 1.1.1:
- **Шаг роста** (`oriblockb`, `OrigrowProcedure`): раз в 2400 тиков (2 мин), только если в 50 блоках есть игрок (или правило `redstoneaction`).
  Блок становится `oldoriblock`, на **случайную соседнюю клетку** (x±1, z±1, тот же y) ставит новый `oriblockb` — **заменяет любой блок**, в том числе
  постройку игрока (проверка «твёрдый сосед» делается по другим случайным клеткам; у сундуков и т. п. содержимое выпадает по ванильным правилам),
  и с шансом 30 % ставит **ядро на клетку над собой**, тоже заменяя что там стоит. Каждый шаг даёт не больше одного нового фронта; в воздухе рост затухает.
- **Откуда растущие блоки:** шипы бури (по 37 `oriblockb` в каждом), жидкость пылевого червя при уроне (`oriblockb` под ней). Рудные жилы `oriblockb`
  в биоме `oridesert` (добавлен модом в Overworld) от генерации не растут — им не назначается тик.
- **Ядро** (`oriblock_core`, `Origrow2Procedure`) на каждом случайном тике (~раз в 68 с, только у игрока): урон 8 всем мобам и игрокам в выживании
  в радиусе 4 и призыв монстров — originiutant (40 %, +40 % translationbase) или oripuppt (60 %, +40 % каменный паук-мутант). Originiutant при
  `mobGriefing` **ломает 8 блоков вокруг себя без дропа**. Ядро ломается алмазной киркой.
- **Жидкость ориджиниума** (`oriliquid`, ставит пылевой червь у себя под ногами при уроне) при любом обновлении соседа **ставит воздух на 1–3 блока над собой**.
- Ещё мобы Bob's взрываются (`MOB`-взрывы — подчиняются `mobGriefing`, как криперы) — к росту не относится, не трогали.

**«Потихоньку»** — у мода нет настроек скорости, поэтому шанс в обереге: шаг роста выполняется с шансом `growChance` = 0,25 (пропущенный шаг не теряется —
блок сам переназначает себе тик через 2 мин): в среднем шаг раз в 8 мин вместо 2. Случайный тик ядра — с шансом `coreChance` = 0,1 (~раз в 11 мин вместо 68 с).
**Зона:** в квадрате `radius + edgeMargin + 2` (соседняя клетка) отменяются шаг роста, тик ядра, жидкость червя, её «воздух над собой», ядро на месте
заражённого (`Ori_infect*`, `Infectoriman`) и ломание блоков originiutant'ом; шип бури в полосе `edgeMargin` тоже не ставит блоки.

**Тест** `scripts/tests/oristormward.js` (`EXTRA_SERVER_SCRIPTS=<папка только с ним> TEST_COMMANDS=<файл «#sleep 1080»> scripts/test-server.sh <папка>`):
ward в (0,200,0), фейковый игрок в центре зоны, `OriStorm.start`, `oriwait 100`. Все проверки `OSW … OK`: правила мира выставлены скриптом;
блок бури в зоне не ставит шип (0 блоков Bob's), снаружи — ставит (2802 блока); буря идёт 3000 тиков, призывов 172: **26 отменено в зоне**,
112 пропущено в незагруженных чанках (в тесте нет игроков — загружены только чанки `forceload`); после бури в зоне 0 червей/падающих блоков бури;
червь, заведённый снаружи (`tp`), и червь из команды в зоне остаются. **Тик сервера**: до бури 22,0 мс; во время бури **без** пропуска
незагруженных чанков — 309–405 мс в среднем, пики 924–2133 мс; **с пропуском — 18,4 мс, максимум 22,9 мс**. `SERVER OK`.
**Рост** (тот же тест, `redstoneaction=true` вместо игрока рядом, 6,2 мин = 3 тика роста; два прогона): ряд из 39 семян у края снаружи (x = 115,
зона+полоса — 112): в зоне **0** блоков ориджиниума, ближайший к зоне — x = 114; поле из 100 семян в камне: **62–67 шагов роста** (≈ 6–6,5 шага на семя
в час; у мода без оберега было бы ~30), 79–84 активных `oriblockb`, 14–15 новых ядер; пропущено броском 70–74 % попыток при шансе 0,25.
**Ядра** (случайные тики без живого игрока не идут — тест вызывает `randomTick` сам, 20 ядер снаружи и 10 в зоне по 10 раз): в зоне все 100 тиков отменены,
монстров ориджиниума в зоне 0; снаружи из 200 тиков сработали 20 (10 %), призванные монстры падают с висящих в воздухе тестовых ядер (выжил 1).
Тик: до бури 21–22 мс, во время бури 15,6–17,6 мс (макс 23,5), после бури с ростом 22,3–26,8 мс (единичные пики 228–311 мс). `SERVER OK`.
Не проверено: бросок по времени в долгой игре (механизм запуска тот же `OriStorm.start`), снятие зависшего запуска после перезапуска,
вид бури у игроков (клиент — `docs/client-checklist.md`).

## WorldEdit 7.2.15 (2026-10-13, решение владельца В18)
Modrinth `1u6JkXh5`, версия `Wrlqaul6`, Forge 1.20.1, сторона `both`, в `pack/modlist.txt`. `test-server.sh`: `SERVER OK` (Done 32 с), в логе «WorldEdit for Forge (version 7.2.15) is loaded», команды зарегистрированы, ошибок нет.
Команды `//…` — по правам OP (разрешений-мода в сборке нет, действует уровень оператора). Клиентский прогон не делался (мод серверный по сути, интерфейса нет). Инструкция переноса построек — `docs/friends.md` («Администратору»).

## Clanging Howl 1.1.0 (поток 20, 2026-10-17, решение владельца В23)
Автор — Mongoose_artist (владелец), Polarice3; CurseForge project 1367022, ModJam 2025, лицензия All Rights Reserved, **стороннее скачивание
запрещено** (в API нет `downloadUrl`) → jar `pack/mods/clanging-howl-1.20.1-1.1.0.jar` лежит в репозитории (правило CLAUDE.md), строка `#jar` в
`modlist.txt`. Единственный мод с таким названием; Forge 1.20.1, сторона **both**. Зависимости: GeckoLib (≥84) и Curios (≥5.3.4) — уже в сборке;
JEI необязательна. Добавляет: метеоритные дожди, внеземные объекты/гнёзда/исследовательские станции, мобов-техноплоть (гематома, охотник, туша,
кровораспространитель…), энергогаджеты (curios-слоты heart/legs/feet), броню-инструменты из внеземной стали.
Конфликты: теги `forge:ingots`/`ores` содержат только свои предметы (Almost Unified не затрагивается — `steel` в `unify.json` это `forge:ingots/steel`);
слоты Curios `replace:false` — с остальными модами складываются. Русификация — см. docs/russification.md.

**Прогон `test-server.sh` (max-tick-time=-1): `Done (48.6s)`**, 152 ERROR в логе — фоновые, как у остальных модов; строк с `clanginghowl` уровня ERROR/exception нет.
Единственное — WARN «config/clanginghowl.toml исправлен до значений по умолчанию» (первый запуск: метеоритный дождь включён, раз в 15 дней).
Не проверено: клиент (рендер GeckoLib, перевод в игре), реальный метеоритный дождь и спавн мобов/структур в мире, поведение рядом с Lycanites/Cataclysm.

## Chaos Persists 0.9.1 — ремейк OreSpawn (поток 23, 2026-10-17, решение владельца В16)
CurseForge 1460238, файл 9130290, Astryxion, All Rights Reserved. Jar `pack/mods/chaospersists-1.20.1-0.9.1.jar`, сторона both, обязательных зависимостей нет.
Полное сравнение с OreSpawn 1.7.10 и находки — **`docs/orespawn.md`**. Коротко:
- потолок здоровья 1024 обрезал бы King, Queen, Mobzilla → добавлен **AttributeFix** (mr `attributefix`, both);
- рецепты ремейка создаются в коде и не видны KubeJS и Almost Unified;
- металлы (уран, титан, соль, рубин, аметист) не стоят в тегах `forge:` и **намеренно не объединяются**;
- `chaospersists:utopia` добавлен в `#minecraft:is_overworld` — туда попадут мобы и руды других модов.

**Прогоны `test-server.sh`:**
- `Done (81.8s)` — первый запуск, 153 ERROR, от ремейка один (потолок здоровья);
- с AttributeFix — `Done (35.3s)`, 152 ERROR.

Первый тик после Done ~136 с (WorldEdit + 6 новых измерений; `max-tick-time=-1` обязателен).
Не проверено: клиент, измерения и бои в игре, HP боссов в бою.

**Доводка (поток 25, 2026-10-17, ветка `claude/orespawn-tune`)** — подробности и числа в `docs/orespawn.md`, раздел «Доводка»:
- у мода **нет в конфиге весов естественного спавна** (они зашиты в `EntityRegistry.addSpawn`), `LessLag` режет только декор (бабочки, комары, кукуруза, муравьи), а не спавн;
  поэтому спавн мобов в Overworld прореживает наш KubeJS-скрипт `orespawn_spawn_thin.js` (оставляет 25% попыток);
- `LessOre=1` делит число попыток генерации каждой руды на 3 (`ChunkOreGenerator`); размер жилы устроен нелинейно (эллипсоид, радиус ∝ clumpsize):
  clumpsize 4→3 даёт ещё −90% блоков, clumpsize 2 — **ноль** блоков; поэтому жилы мельчим осторожно;
- сообщений «конфиг прочитан» мод в лог не пишет; применение подтверждено подсчётом руды на новых чанках (`scripts/tests/orecount.js`);
- русский перевод ремейка дописан: 427 строк (`pack/resourcepacks-src/russian-moded/assets/chaospersists/lang/ru_ru.json`), `lang_validate` — 0 ошибок;
- `test-server.sh` с итоговым конфигом и скриптом — `SERVER OK: Done (21.3s)` (мир уже создан), 153 ERROR = прежний фон 152 + известная строка ремейка про потолок здоровья; ошибок KubeJS нет.


## Iron Chests 14.4.4 (поток 26, 2026-10-17, решение владельца)
Оригинал alexbegt (Modrinth `iron-chests`, GPL-3.0, both, `ironchest-1.20.1-14.4.4.jar`): сундуки медь/железо/серебро/золото/алмаз/кристалл/обсидиан и улучшения.
- Зависимости только Forge ≥47 и Minecraft 1.20.1; ошибок и предупреждений от `ironchest` в логе нет.
- Дублей нет: Iron Chests: Restocked, Upgraded Iron Chests, Metal Barrels, Sophisticated Storage в сборке не стоят; Quark добавляет только сундуки по породам дерева. Аддонов-связок под 1.20.1 Forge не найдено — не добавляли.
- Русский перевод: `ru_ru.json` внутри jar покрывает все 36 ключей en_us — наш пак не менялся.
- `test-server.sh`: `SERVER OK: Done (50.9s)`, 153 ERROR в логе — как до мода (ни одной от ironchest).
- Не проверено: крафт улучшений сундуков и рецепты с серебром (зависят от тегов `forge:ingots/silver`, Almost Unified) — в игре.

## «Охотники Бездны»: прыжок 10 блоков → 3 (поток 28, 2026-10-17, просьба владельца)
Жалоба: у «Охотников Бездны» прыжок ≈10 блоков, неудобно. Разбор:
- Раса `arks:seahunter` (Arknights Races 1.5.3) **прыжка не даёт** (силы `seahunter/modify|status|waterful` — здоровье, урон, плавание, регенерация, вода; в `data/arks/` нет ни `jump`, ни атрибута прыжка).
- Источник — мод **Arknights_Weapon 1.5.6** (MCreator, всё зашито в байткоде): эффект `arknights_weapon:abyssal_hunters` («Глубинный охотник», игла генной модификации) через 6 с после наложения выдаёт на 1800 тиков
  Прыгучесть с усилением **10** (+уровень эффекта; союзникам рядом то же) = «Прыгучесть XI», ≈12 блоков. Сверх того броня образца «Агор» (набор «Охотника из бездны»): **ботинки** — Прыгучесть усиление 6 (VII, ≈7 блоков),
  **поножи** — 3 (IV, ≈4 блока). Датапаком/конфигом это не меняется (значения в коде), поэтому — патч байткода.
- **Сделано:** `patches/abyssal-jump/` (ASM, `build.sh <тестовый сервер>`): усиление Прыгучести в этих трёх классах → **2** («Прыгучесть III», ≈3,3 блока: ваниль 1,25 → I 1,8 → II 2,5 → III 3,3). Эффект: `10 + уровень` → `2 + уровень`.
  Остальные эффекты (скорость, сила, сопротивление, регенерация, ночное зрение…) не тронуты. Применено к `pack/mods/arknights_weapon-1.5.6-mmport.jar` (порт для Rise-процедуры сохранён; патч поверх), `packwiz refresh`.
- Другие источники Прыгучести в сборке (не менялись, не «Охотники Бездны»): `SnowSuitLeggingsProcedure` (Arknights_Weapon, поножи набора «Снежный», усиление 7), Caerula Arbor — `NetherseaBuffFunc`, `EatArmaticoffee`
  (кофе), `NetherseaWalkerExtraFunc`, `DamagedByNethseabrand`. Ванильные Jump Boost зелья/маяк — как прежде.
- Проверка: `scripts/tests/abyssaljump.js` (фейковый игрок; запуск `TEST_COMMANDS` с `#sleep 220` — первый тик после Done ≈2 мин). До патча: `effect amp=10`, `boots amp=6`, `leggings amp=3`; после: `OK amp=2` во всех трёх;
  `SERVER OK: Done (32.3s)`. Не проверено: реальный прыжок игрока на высоту (ожидание по формуле ванильной физики — 3,3 блока), клиентский показ уровня эффекта.
- Побочное: при проверке на «чистой» папке один раз `ConcurrentModificationException` в Aether при common_setup (гонка загрузчика, ModernFix `MappedRegistry`); повторный запуск прошёл — к патчу не относится.

## Ars Nouveau и аддоны: «Incorrect key … was corrected» (поток 28, 2026-10-17)
Жалоба: в логах игроков сотни WARN `ForgeConfigSpec: Incorrect key general.cost was corrected from null to its default`. Разбор
(Ars Nouveau 4.12.7, Ars Elemental 0.6.8, Ars Additions 1.6.7, Ars Technica, Ars Creo, Ars Instrumentum и др.):
- **В репозитории нет ни одного конфига Ars или его аддона** (`pack/config/`, `pack/defaultconfigs/`) — стартовых значений мы не задавали,
  поэтому ключей, не совпадающих со схемой, у нас нет, и править нечего (значения не менялись). Смысл настроек не тронут.
- Источник шума — **создание файлов при первом запуске**: Ars пишет по файлу на каждый глиф/форму/эффект (`config/ars_nouveau/glyph_*.toml`,
  `ars_elemental/`, `tomeofblood/`…), Forge создаёт файл и для каждого ключа пишет WARN «corrected from null». Все 13 973 строки первого прогона
  сервера (и 7 454 на клиенте) — именно «from null»; ни одной «устаревший/лишний/не того типа ключ». Это не вина Ars: так ведёт себя любой
  мод на `ForgeConfigSpec` при первом запуске (в списке и Bumblezone, Zombie Have Ears, Alex's Caves, Clanging Howl…).
- Проверка (сервер, папка вне репо, `scripts/test-server.sh` дважды подряд на одной папке): запуск 1 — 13 973 WARN «Incorrect key» (все `from null`),
  `SERVER OK: Done (58.7s)`; **запуск 2 — 3 WARN, ни одного от Ars**, `SERVER OK: Done (29.9s)`.
  Клиент (`scripts/test-client.sh`, `.mrpack`, дважды): запуск 1 — 7 454 WARN (все `from null`), запуск 2 — 2 WARN, `CLIENT OK` оба раза
  (в первом `Mixin apply failed … lycanitesmobs LightTextureMixin` — прежняя известная строка, см. «затемнение Lycanites»).
- Остаток (2–3 строки при каждом запуске, не Ars): Mechanical Botania `rpmvalues` и Legendary Survival Overhaul `temperature.wetness.Wetness Immunity
  Mounts` — Forge считает списки невалидными и «исправляет» их на то же значение (`from [8,16,32,64,128] to its default [8,16,32,64,128]`);
  `soulsearchers_lens.creatureModifiers` (`[minecraft:villager,1.5]` → то же). Безвредны; поменять значения тут нечем — формат списка задан модом.
- Если у игрока WARN повторяются **при каждом запуске** — значит, лаунчер каждый раз начинает с пустой папки `config/` (новый экземпляр/«чистая»
  переустановка/удаление `config`), а не конфиг сборки неверен. Проверка: открыть `config/ars_nouveau/` после первого запуска — файлы должны остаться.
  Класть в `pack/config/` сгенерированные по умолчанию файлы Ars (≈240 штук) **не стали**: они заморозят значения по умолчанию и при обновлении Ars
  дадут настоящие «Incorrect key» (устаревшие ключи); к тому же `packwiz` перезаписывал бы у игроков их правки.
- Если понадобится менять значения Ars (стоимость глифов и т. п.) — править через KubeJS/датапак либо класть в `pack/config/` только нужные файлы целиком (Forge допишет недостающие ключи с одним WARN «from null»).

## Клиентские удобства: mm-perf 0.2.0, Controlling, Potato Craft 8x (поток 28, решения владельца В27/В28/В30)
Всё в клиентском моде `mods-src/mm-perf` (`pack/mods/mm-perf-0.3.1-client.jar`, на сервер не попадает: серверный zip и `test-server` его не содержат).
- **Режим слабого ПК** (`LowSpecMode`): кнопка «Режим слабого ПК» / «Вернуть как было» внизу слева на экранах настроек видео (ваниль и Embeddium) и «Настройки»; клавиша `key.mm_perf.lowspec`
  (не назначена). Ставит: дальность 5, симуляция 5, графика «Быстро», облака выкл., частицы «минимум», мягкий свет выкл., тени сущностей выкл., сглаживание биомов 0, mipmap 0, лимит 60 FPS, VSync выкл.;
  Embeddium — погода/листва «Быстро», виньетка выкл. (через `SodiumClientMod.options().quality`, `writeChanges`), Oculus — `Iris.toggleShaders(false)`, Distant Horizons — `DhApi.Delayed.configs.graphics().renderingEnabled(false)`
  (все три — отражением, нет мода или API — шаг пропускается). Прежние значения — `config/mm-perf-lowspec.json` до «Вернуть». Анимации текстур: у ванили нет настройки, у Embeddium `animateOnlyVisibleTextures` уже включено.
- **Клавиши модов «Не назначено»** (`KeyDefaults`): после загрузки настроек у каждой не ванильной `KeyMapping` (ванильные — по списку имён 1.20.1 + `key.hotbar.*`) заводское значение (`defaultKey`) = не назначено, поэтому «Сбросить» тоже даёт «Не назначено».
  Один раз при первом запуске (флаг `config/mm-perf-keys.json`) клавиши модов, стоявшие на заводском значении, отвязываются; назначенные игроком — остаются.
- **Память** (`MemoryWatch`): тост в главном меню, если Xmx < ~7 ГБ или > 70 % физической ОЗУ (`com.sun.management.OperatingSystemMXBean`); в мире — если занято > 90 % кучи **после** последней сборки мусора около 30 с,
  тост «Память почти закончилась — включите Режим слабого ПК» (не чаще раза в 10 минут).
- **Controlling 12.0.2 + Searchables 1.0.3** (MIT, client): поиск по названию в экране «Управление» и кнопка «только конфликты». Поиск по моду: у 372 из 384 клавиш модов своя категория (группируются по моду),
  в ванильных общих категориях только 12 (`key.dragon_*`, `natural_disasters`, `contigency_contract`, `special_ability`, `create_sa`) — 3 %, подпись мода не добавляли.
- **Ресурспак низких текстур**: Potato Craft-8x v1.4 (автор — Saransh, https://modrinth.com/resourcepack/potato-craft-8x; CC-BY-4.0, изменений нет; ванильные 8×8, текстуры модов не меняет — выигрыш небольшой), лежит в `pack/resourcepacks/`, в `options.txt` **не включён**.
  Других подходящих по лицензии с покрытием модов не нашлось (Potato Graphics, 1x1 Potato — All Rights Reserved).
- **Distant Horizons по умолчанию полегче**: радиус 48, качество LOW/LOW, потоков 1 (`pack/config/DistantHorizons.toml`).
Проверка: сборка `./gradlew build` без ошибок; `test-client.sh` из нового `.mrpack` с `-Dmm_perf.selftest=true` — `CLIENT OK` (565 с), строки `MMPERF-SELFTEST`: ванильных клавиш 34 (назначено 32, `key.forward=W`),
клавиш модов 385, назначено 0, заводское не пустое 0; отвязано заводских 240; Режим: `rd 10→5, sim 6→5, graphics fancy→fast, clouds FAST→OFF, particles→MINIMAL, ao→false, fps 120→60, vsync→false`, после «Вернуть» всё как было (`restored=true`).
Второй прогон (`-Dmm_perf.selftest.assign=key.create_sa.flying` — клавиша, «назначенная игроком» F9): остаётся F9, отвязано 239 остальных. `test-server.sh`: `SERVER OK: Done (29.0s)`.
**Не проверено:** нажатие кнопки мышью и горячей клавиши (нет `xdotool`; логика та же `LowSpecMode.toggle()`, вызывается из самотеста); тосты на экране; применение Embeddium/Oculus/DH в реальном рендере (без GPU) —
ошибок отражения в логе нет, но DH к меню ещё не запущен (шаг пропускается, как задумано); вид Controlling-экрана.

## Первый тик после Done ≈ 2 минуты — виновник WorldEdit (поток 28, 2026-10-17)
Метод: `scripts`-стенд без репозитория — сервер `run.sh` на готовом мире, сразу после `Done` `jstack` потока `Server thread` каждые 5 с (40 дампов, 3,5 мин).
- `Done (29.9s)` в 18:18:37; поток сервера занят до ≈18:20:50 (**≈130 с**), затем первые нормальные тики (Northstar `PlanetTracker.updateOrbits` — доли секунды, остальное ожидание).
- **Все 25 дампов с 18:18:38 по 18:20:46 — один и тот же стек:** `ForgeWorldEdit.serverStarted` (ServerStartedEvent) → `PlatformManager.handleNewPlatformReady` → `Capability$4.ready` (WORLD_EDITING)
  → для каждого `BlockType` из реестра `getAllStates()` → `BlockType.getBlockStatesMap` → `BlockState.generateStateMap/populate/withValue` (Guava `ImmutableMap.build/equals`, `HashMap$TreeNode.find` — коллизии хэшей карт состояний) →
  `BlockStateIdAccess.register(state, id)` для каждого состояния. Это построение таблицы состояний блоков **всех модов** (десятки тысяч блоков × состояния), целиком на главном потоке, до обработки тиков.
  Мы уже встречали то же как «WorldEdit делает первый тик >60 с, сторож убивает сервер» (`max-tick-time=-1`, PR #52) — причина та же.
- Других заметных потребителей в первом тике нет: OreSpawn/DH/Spore/KubeJS в дампах не встречаются.
- Версия WorldEdit 7.2.15 — последняя для 1.20.1 Forge на Modrinth; обновлять нечем.
Варианты (не применены, нужен выбор):
1. **Оставить** (сервер всё равно запускается; игроки ждут «Done» и первые 2 мин сервер не отвечает на тики — вход возможен только после; на сильном железе время меньше).
2. **Убрать WorldEdit** с сервера/из сборки (схематики нужны администратору — `docs/friends.md`; можно держать как отдельный серверный мод, включаемый вручную) — выигрыш 130 с на каждый запуск сервера и встроенного сервера клиента.
3. **Миксин в `mm-compat`** на `Capability$4.ready`: выполнить обход в фоновом потоке (после Done сервер сразу тикает). Риск: `BlockStateIdAccess.TO_STATE` (`Int2ObjectOpenHashMap`) и `LazyReference` не потокобезопасны; команда WorldEdit в первые 2 минуты (или WE-код, читающий таблицу) может увидеть недостроенную таблицу.
   Защита: подставить блокировку/ожидание завершения в `//`-команды (`ForgeWorldEdit` `PlatformManager.getPlatformManager().getPlatformCommandManager()`), либо `synchronized` вокруг `register`. Средний объём, нужен тест `//set` под нагрузкой.

**Решение (Координатор, 2026-10-17): вариант 2.** WorldEdit — необязательный мод, по умолчанию выключен у клиента (в одиночной игре встроенный сервер тратил бы те же ≈130 с на каждый вход в мир):
`pack/mods/worldedit.pw.toml` — `[option] optional = true, default = false` (packwiz-installer спрашивает пользователя); формат `.mrpack` не знает «выключено по умолчанию», а Prism/Modrinth App ставят и optional-моды,
поэтому `scripts/mrpack-optional.py` (вызывается из `export-pack.sh`) ставит таким модам `env.client = unsupported` в `modrinth.index.json`. Сервер (`fetch-mods.py`, серверный zip) WorldEdit получает как раньше.
Включение у администратора — `docs/friends.md`, «Администратору: перенос построек».
Проверка: `export-pack.sh` — в `.mrpack` WorldEdit с `env.client = unsupported`, в серверном zip он есть; `test-server.sh` — `SERVER OK: Done (28.6s)`, WorldEdit загружен;
`test-client.sh` из нового `.mrpack` — `CLIENT OK` (582 с), `worldedit` в `game/mods` нет. **Не измерено:** вход в одиночный мир без 130 с — в стенде нет `xdotool`, а `--quickPlaySingleplayer` мир не создал
(клиент остался в меню); ожидание следует из стека (всё время первого тика — WorldEdit).

## Northstar Structures удаляло всех существ в «кратерах» Обычного мира (поток 30, 2026-10-17, баг владельца «мобы не спавнятся»)
**Симптом** (одиночная игра, новый обычный мир; суперплоский и Сумеречный лес работали): `/summon` без ответа, яйцо — только звук, естественного спавна нет.
**Причина** — `com.northstar_structures.NorthstarStructures.onEntityJoinLevel` (Create: Northstar — Structures 1.0.0): для каждого не-игрового живого существа
вызывает `ProtectedCraterAreas.isInsideProtectedCrater(seed, chunkPos)` и при попадании делает `discard()` + `setCanceled(true)`. «Защищённые кратеры» считаются
только по зерну и координатам чанка (круги радиусом 150–250 блоков в каждой ячейке 64×64 чанка + бассейн Эйткена радиусом 2400) — **измерение не проверяется**,
поэтому на Земле такие круги стали «мёртвыми зонами»: ни одно существо там не появляется и не живёт. Найдено на dedicated: корова, добавленная `addFreshEntity`,
возвращала `false` и была `DISCARDED` в (600,600) snowy_taiga и (-600,-600) jungle, но не в (0,30), (100,100), (300,300), (1000,0), (0,1000); вызов слушателей
`EntityJoinLevelEvent` по одному назвал виновника. Не виноваты: Cerbon's API/BoMD, ModernFix `remove_spawn_chunks`, Оберег, Spore/SRP, `orespawn_spawn_thin.js`.
**Исправление** — `mods-src/mm-compat` 0.5.3: миксин `NorthstarCraterJoinMixin` пропускает обработчик во всех измерениях, кроме Луны (путь измерения `moon`: `northstar:moon`, `ad_astra:moon`).
Кратеры и бассейн (структуры, фичи) — только на Луне, их это не затрагивает. После исправления корова добавляется во всех семи точках, в логе: «mm_compat: Northstar Structures onEntityJoinLevel — только на Луне».
Тесты: `scripts/tests/spawnnat.js` (диагностика + яйца/summon/естественный спавн, два фейковых игрока; `culprit` перебирает слушателей), `scripts/tests/explorecheck.js` (тики при исследовании нового мира).
Побочно: `scripts/test-server.sh` — переменные `EXTRA_CONFIG`, `SERVER_JVM_EXTRA`, `SERVER_XMX`.

## Модели Yes Steve Model: 65 готовых + Priestess, + 19 Azur Lane (поток 32, 2026-10-18, решение владельца В33)
Подробно — `docs/ysm-models.md`. Находки:
- YSM 2.6.5 не пишет в лог, какие модели загрузил и почему отверг. Счёт — по числу записей в серверном кэше
  `config/yes_steve_model/cache/server` (встроенные + свои; базу мерить прогоном с пустым `custom/` — 2026-10-18 было 26);
  неподходящая модель тихо пропадает.
- `AK_Rosmontis_v0.9.ysm` (Elaina69, формат 26) не читается вовсе — не раздаём; остальные 65 из `_Arknights`,
  `_ArknightsEndfield`, `_PunishingGrayRaven` грузятся. Файлы качаются по ссылкам из packwiz-метафайлов в `config/`
  (`scripts/fetch-config-files.py` для сервера; `test-server.sh` и `export-pack.sh` его вызывают).
- Azur Lane (`_AzurLane`, 19 файлов, 2026-10-18): прогон только с ними — кэш 45 = 26 + 19, читаются все; полный `test-server.sh` — `fetch-config-files: 84 файлов`, кэш 111 (было 92), `SERVER OK`.
- Модели-папки (как Priestess) в этот кэш не попадают — их загрузку проверять командой `/ysm export <id>` из консоли (файл появляется в `config/yes_steve_model/export/` только у загруженной модели; зашифрованные `.ysm` не экспортируются). Priestess: `[YSM] Saved as 'export/priestess.ysm'`. Тест `scripts/tests/ysmcheck.js`: теги форм собираются (посохи Iron's/Cataclysm Spellbooks, палочки Wizardry, жезлы Botania → staff; книги Ars → tome; еда → food), команда `ysm molang execute` сервером принимается.
- Клиент YSM в облаке не запускается (К7) — внешний вид и molang-логика Priestess проверены своим рендером
  (`mods-src/ysm-priestess/ysmrender.py`), в игре — по чек-листу в `docs/ysm-models.md`.

**ModernFix не виноват** (проверено): со старым mm-compat 0.5.2 и `remove_spawn_chunks=false` корова так же удаляется в (600,600), (-600,-600), в Энде (-400,400) и в Нижнем мире (-600,-600); с mm-compat 0.5.3 и `remove_spawn_chunks=true` (как в паке) во всех точках Земли, Энда и Нижнего мира сущность добавляется. «Помогло» у владельца — совпадение: «мёртвые зоны» считаются по зерну, новый мир даёт другие. `modernfix-mixins.properties` не менялся.

## Модели Yes Steve Model: +124 Azur Lane из FREE-YSM-AUTHOR (поток 37, 2026-10-18, решение владельца В38 «всё уникальное»)
Подробно и список — `docs/ysm-models.md`. Находки и замеры (облако, 4 ядра, `-Xmx8G`, `scripts/test-server.sh`):
- Сборник организован по авторам (8242 файла), модели в ветке `main`, не в ветке по умолчанию. 66 AL-папок, 148 файлов, 617 МБ → без дублей (Elaina69, внутренние) 136 файлов / 590 МБ → **YSM 2.6.5 не читает 12** (проверено по одному файлу в минимальном сервере только с YSM) → раздаётся **124 файла, 501 МБ**.
- Скачивание: ссылки raw.githubusercontent.com на коммит `17db8b6b` + sha256; `fetch-config-files: 220 файлов` (84 + 136), все прошли проверку хэша целиком, самый большой файл 14 МБ (лимит GitHub raw 100 МБ не затронут). Сорванных загрузок моделей не было (повторные попытки были только у больших jar модов). На клиенте packwiz-installer качает те же ссылки; в облаке клиент с YSM не запускается (К7), поэтому синхронизацию модели клиенту и время входа здесь измерить нельзя.
- Сервер, `Done`: **62,4 с без новых моделей → 62,2 с с ними** (разницы нет). Кэш YSM `config/yes_steve_model/cache/server`: 111 → **233** записи (52 МБ → 471 МБ на диске).
- Куча сервера после `GC.run` на `Done`: **3,23 ГБ → 3,45 ГБ** (+0,22 ГБ) при максимуме 8 ГБ; Metaspace без изменений (585 МБ). OOM и вылетов нет.
- Диск: +501 МБ файлов моделей + ~420 МБ серверного кэша; у игрока в `config/yes_steve_model/custom/` ещё 501 МБ при установке.
- Имена файлов приведены к ASCII (`<автор>_<латиница>`), иначе у трёх авторов совпали бы имена `AL_柴郡_Cheshire.ysm`.

### Нагрузка Spore + SRP System на сервере (поток 30, 2026-10-17)
Числа и вывод — `docs/performance.md`, «Нагрузка от Spore + SRP System». Коротко: без заразы нагрузки нет; один очаг (~50 существ) ≈ +10–25 % тика (11 % потока сервера),
три очага (~70 существ — работает лимит деспавна) ≈ +45–50 % (20 %). Конфиг не меняли. Тест `scripts/tests/sporeload.js`.

### Клиентская проверка Spore, SRP System, Chaos Persists (OreSpawn) (поток 30, 2026-10-17)
Стенд `scripts/test-client.sh` (Xvfb + llvmpipe, без звука; YSM убран, как всегда) из свежего `.mrpack` (интегратор 4aaf3ad + mm-compat 0.5.3): **`CLIENT OK`, 785 с**.
**В мир зашли** — GUI через `xdotool` (в стенд поставлен: `apt-get install xdotool`): Одиночная игра → «Создать новый мир» (тип по умолчанию, нормальная сложность) → окна Origin/Искусства/Класс → мир
на встроенном сервере. Скрипт `scripts/clienttest/clientmobs.js` (кладётся в `<папка клиента>/game/kubejs/server_scripts/` перед запуском, в сборку не входит) призвал по одному мобу **каждого вида**
из пространств имён `spore`, `chaospersists`, `srpsystem` — **254 вида, 0 ошибок призыва** — рядами на ровной площадке, игрок телепортировался вдоль рядов; скриншоты —
`docs/client-check/spore-orespawn-chaos-row.jpg` (мобы Chaos Persists и Spore в ряду, Jade) и `docs/client-check/spore-verfaldrache.jpg` (босс Verfalldrache и заражённые, русские названия и подсказка Jade).
Это же подтверждает фикс Northstar Structures: в новом обычном мире мобы появляются.
**В логе клиента по Spore/SRP/Chaos Persists — без ошибок моделей, текстур, рендереров и lang.** Остались только безвредные предупреждения самих модов:
- `srpsystem:sounds/nude.ogg` и `spore:sounds/bairn_ambient1.ogg` — файла звука нет (ошибка модов);
- анимированные спрайты предметов Spore (`scythe`, `mace`, `great_sword`, `halberd`, `rapier`, `saber`, `armads`) — «Invalid frame index / Unused frames» (в `.mcmeta` лишние кадры);
- `defineId called for …` (Spore: Spitter/Howler/Busser/Vigil/Hinderburg) — предупреждение о порядке регистрации данных сущностей, на работу не влияет;
- `ChaosPersists: failed to raise generic.maxHealth cap` — известное, потолок здоровья снимает AttributeFix;
- Spore/SRP/Chaos пересоздали свои конфиги при первом запуске (`Incorrect key … corrected from null`) — нормально для нового клиента.
Не проверено: звук (в стенде без звука), поведение мобов (призывались без ИИ), анимации ходьбы/атаки, шейдеры, настоящая видеокарта (llvmpipe). В логе клиента есть и чужие «Missing textures»
(Create Big Cannons Additional Types, Refined Storage, Wizardry, Ars Additions, Deep Aether — к Spore/OreSpawn не относятся, не разбирались).


## Моды side=server и одиночная игра (поток 31, 2026-10-17, баг владельца «скалк не реагирует на голос»)
Причина: одиночная игра — это встроенный сервер клиента, а моды `side = "server"` в `.mrpack` получают
`env.client=unsupported` и в клиентский экземпляр не ставятся. Значит Vibrative Voice в одиночке просто не было.
Разбор по jar (мод, нужен ли в одиночке, безопасен ли на клиенте):
| Мод | Решение | Обоснование |
|---|---|---|
| Vibrative Voice 1.2.0 | **both** | плагин SVC + миксины Хранителя; клиентских классов и своих сетевых каналов нет. Зависимости (SVC, Lexiconfig) уже both |
| BaguetteLib 2.0.7 | **both** | библиотека событий (смерть, инвентарь, подбор); все миксины в общей секции, нужна Gravestone x Curios (mandatory-зависимость); `displayTest=IGNORE_ALL_VERSION` |
| Gravestone x Curios API Compat 4.0.2 | **both** | аксессуары Curios попадают в гроб; в одиночке без него гроб теряет их. Зависимости (Gravestone, Curios, BaguetteLib) — both |
| Blue Skies Gravestones Compat 0.1 | **both** | миксин смерти игрока (`SkiesPlayerMixin`); единственный клиентский класс — пустой `onClientSetup` под `Dist.CLIENT`, безопасен |
| Twilight Cave Fix 1.0.0 | **both** | миксин генерации пещер Twilight Forest (`TFCavesCarverMixin`); мир одиночки генерирует встроенный сервер — без мода там нет фикса |
| Sol's AuthLogin | **server** (оставлен) | есть клиентский код (`com.forgeauth.client`) и сетевой канал `AuthNetworkHandler`: на клиенте — экран логина и токены; в одиночке пароль не нужен, а канал мог бы ломать вход. Мод про офлайн-ники на публичном сервере |
| VC Admin, SimpleVoiceChat Broadcast | **server** (оставлены) | чисто административные команды OP для групп/вещания; в одиночке бессмысленны |
Тесты: `scripts/test-server.sh` — `SERVER OK` (Done за 48 с), SVC загрузил плагин `vibrativevoice`; клиент из нового `.mrpack` дошёл до `CLIENT OK`; в его логе Vibrative Voice загружен, SVC зарегистрировал события плагина `vibrativevoice` («Vibrative Voice is present!»), `config/vibrativevoice.toml` создан. Одиночный мир автотестом не открывался (`--quickPlaySingleplayer` в облаке мир не запустил), поэтому «голос → сенсор в одиночке» глазами не проверен — это ручная проверка по инструкции ниже.
Проверка: `packwiz refresh`, `scripts/export-pack.sh` → в `modrinth.index.json` у пяти модов `env.client=required`,
у AuthLogin/VC Admin/Broadcast — `unsupported`; серверный zip содержит все 8 (`fetch-mods.py` берёт `server`+`both`).
`mrpack-optional.py` не затронут (трогает только `option.optional`).

### Vibrative Voice: пороги и как проверить
Конфиг `config/vibrativevoice.toml` создаётся Lexiconfig при первом запуске (в репозитории его нет, действуют значения по умолчанию):
- `weakVibrationThreshold = 400` — громкость пика, с которой идёт слабая вибрация (будит скалк-сенсор и Хранителя), `strongVibrationThreshold = 1500` — сильная (скалк-крикун без сенсора), `cooldownDuration = 20` тиков между сигналами, `peakDuration = 20`, `frequency = 7`;
- `restrictions.whispering = true`, `whisperFactor = 0.8` — шёпот снижает громкость до 80 %;
- `restrictions.sneaking = true`, `sneakFactor = 0.7` — приседание снижает до 70 %;
- `warden.loudnessFactor = 200`, `loudnessRemembrance = 300` тиков — Хранитель переключается на более громкого.
Как проверить игроку (одиночная игра или сервер): подключить голосовой чат (в одиночке SVC работает через
встроенный сервер, порт 24454 локально), поставить рядом скалк-сенсор (калиброванный или обычный) и **громко**
сказать в микрофон стоя рядом, не приседая и не в режиме шёпота (оба режима глушат голос); сенсор должен
сработать (сигнал редстоуна / звук). Если не реагирует — проверить, что микрофон не выключен (M) и порог
активации голосом не слишком высок.
Voice Chat Interaction (наш порт) остаётся `enabled=false` в `pack/config/vcinteraction/`, чтобы не было двойных вибраций.

## WorldEdit выключен по умолчанию и на сервере (поток 36, 2026-10-18, решение владельца В36)
- `scripts/fetch-mods.py`: моды с `[option] optional=true, default=false` кладутся в `mods/optional/` (Forge вложенные папки не читает). Это попадает и в `server.zip` (`mods/optional/worldedit-mod-7.2.15.jar`), и в `test-server.sh`. Включить = перенести jar в `mods/`.
- `test-server.sh` без WorldEdit: **SERVER OK** (Done 58,9 с на холодном старте; в логе WorldEdit не упоминается).
- `EXTRA_MODS=<папка с worldedit-mod-7.2.15.jar> test-server.sh` (WorldEdit включён): **SERVER OK** (Done 25,7 с, кэши уже тёплые; в логе WorldEdit есть). Тяжёлый тик WorldEdit (~2 мин, +2,7 ГБ) идёт после `Done`, поэтому время `Done` его не показывает.
- Инструкция «включить на время» — `docs/hosting.md` («Необязательные моды») и `docs/friends.md` («Перенос построек»).

## OOM «Java heap space» при полёте над новыми чанками (поток 34, 2026-10-18, краш у игрока)
Жалоба: одиночная игра, новый мир, Xmx 8 ГБ (ноутбук, 16 ГБ ОЗУ), около (-700, 96, 300) — `Exception in server tick loop` →
`OutOfMemoryError: Java heap space` в `ChunkGenerator.createStructures → JigsawStructure → SinglePoolElement.getTemplate →
ModernFix CachingStructureManager.readStructure → StructureTemplate.load`. Место падения — не виновник: куча была уже забита.
Логи игрока (`Logs.rar`) разобрать не удалось (распаковка RAR в облаке заблокирована) — выводы по воспроизведению.

**Воспроизведение.** `scripts/tests/memroute.js`: область forceload 9×9 чанков летит от (0,0) к (-700,300) и дальше по квадрату
со стороной 3000 (шаг 64 блока, следующий — когда центр предыдущей области сгенерирован), куча — `scripts/memwatch.sh`
(jstat каждые 30 с, `GC.class_histogram` каждые 3 мин). Сервер Xmx 6G (быстрее упирается), новый мир, без WorldEdit (`EXCLUDE_MODS=worldedit`, как у клиента).
DH на выделенном сервере сам ничего не генерирует; в сборке у клиента `generatorPlan = "DISABLED"`, так что DH в одиночке не нагружает генерацию, если игрок не включал её сам.

**Виновники (по убыванию):**
1. **The Lost Cities 1.20-7.5.7 — утечка `BuildingInfo`.** `getXmin/getXmax/getZmin/getZmax` запоминают соседа в полях; все
   BuildingInfo сгенерированных чанков сцепляются в одну сетку, и любой живой держит её целиком — TTL-кэш `ChunkPlanner`
   (`cacheCleanupSeconds`) ничего не освобождает. Без исправления: BuildingInfo 47 тыс. (3 мин) → 306 тыс. (6 мин) → 550 тыс. → **705 тыс.** (12,5 мин),
   `HashMap$Node` 0,23 → 1,6 ГБ, old gen **6,1 / 6,1 ГБ, 52 полные сборки** к 14-й минуте (тики по несколько секунд, до OOM — минуты).
   `cacheCleanupSeconds = 60` не помогает (замерено: рост тот же). **Исправление — `LostCitiesNeighborMixin` (mm-compat 0.5.5)**: сосед не запоминается,
   каждый раз берётся из кэша Lost Cities. Работает и на уже созданных мирах (конфиг мира не нужен).
2. **WorldEdit 7.2.15 — ~2,7 ГБ постоянно** (живая куча после старта 5,4 ГБ против 2,67 ГБ без него): таблицы Guava по каждому из
   **841 тыс. состояний блоков** сборки (Quark 145 тыс., VS Sails 65 тыс., bobsoriginiumdream 59 тыс. — один `plantbox` 57 тыс., Ad Astra 59 тыс.).
   С ним при Xmx 6G OOM тем же стеком, что у игрока, — через 3 минуты маршрута. У клиентов WorldEdit по умолчанию выключен
   (решение от 2026-10-17); на выделенном сервере он есть — рекомендация ниже, решение за Координатором/владельцем.
3. **Гигантские шаблоны структур** (`bobsoriginiumdream`: `orilabortory_key` 2,1 млн блоков, `kingtemple` 1,5 млн, `ori_stele`;
   `caerula_arbor`: `iberiaeye_looted_1` 2,0 млн, `abyssal_lab_2`, `izumik_island`, `drilling_platform`, `cloister`; Aether Villages `olympic_citadel_main_0` 1,4 млн) —
   кубы до 128³, на 86–98 % из воздуха. Загруженный шаблон ~100 МБ (держится в мягком кэше ModernFix `dynamic_structure_manager` — при нехватке
   памяти выгружается), но `StructureTemplate.placeInWorld` для **каждого** чанка структуры копирует все блоки шаблона в `processBlockInfos`
   (BlockPos + StructureBlockInfo + списки — 100+ МБ мусора на чанк на поток генерации). **`StructureTemplatePlaceMixin`** (mm-compat 0.5.5):
   шаблонам от 65 536 блоков в чанк передаются только блоки его коробки по X/Z (то, что placeInWorld всё равно поставил бы; Y не трогаем).
   Отдельно этот миксин не замерялся (выигрыш — пиковый мусор и CPU, не удерживаемая куча).
4. Невиновны по замерам: кэш шаблонов ModernFix (мягкие ссылки), Valkyrien Skies/Bumblezone/Alex's Caves/Lycanites (их классы не растут), Spore.

**Замер «после»** (тот же маршрут, Xmx 6G, без WorldEdit, `cacheCleanupSeconds` по умолчанию 300):
| Время маршрута | без исправлений | mm-compat 0.5.5 |
|---|---|---|
| ~3 мин | old gen растёт, BuildingInfo 47 тыс. | живая куча 2,58 ГБ |
| ~6 мин | BuildingInfo 306 тыс. | 2,76 ГБ |
| ~12,5 мин | BuildingInfo 705 тыс., old gen 6,1/6,1 ГБ, 35 полных сборок | 3,48 ГБ, BuildingInfo нет в топ-45 |
| ~15 мин | 52 полные сборки, сервер почти не тикает | 3,54 ГБ, old gen 3,6/3,8 ГБ, **7** полных сборок, OOM нет |
Прогон с TTL 60 до 19-й минуты: живая куча 3,5–4,6 ГБ, плато (остаток — данные чанков и шаблоны в мягком кэше).

**Что делать игроку:** обновить сборку (mm-compat 0.5.5) — мир пересоздавать не нужно. Xmx 8 ГБ при 16 ГБ ОЗУ остаётся верным.
**Рекомендация по WorldEdit на сервере:** сделать его необязательным и на сервере (класть в `mods/` только на время переноса построек) —
это −2,7 ГБ кучи и −2 мин первого тика; либо оставить и давать серверу Xmx ≥ 10 ГБ (как сейчас в `docs/friends.md`).
Инструменты: `scripts/tests/memroute.js`, `scripts/memwatch.sh`, `scripts/tests/templload.js` (сколько кучи держит шаблон), `test-server.sh` `EXCLUDE_MODS=`.

### mm-compat 0.5.6 — Origin Furs: кэш списка ресурсов (поток 33, 2026-10-18)
`OriginFursReloadCacheMixin` (клиент, `require` по умолчанию 0): `OriginalFurClient.reload` каждые ≈25 с вызывал `ResourceManager.listResources("furs", …)` по всем пакам и zip-модам (≈4 с на ПК игрока, spark клиента);
результат кэшируется до перезагрузки ресурсов. Origin Furs — pandaismyname1 (MIT), здесь только перехват вызова. `CLIENT OK`, миксин применился. Подробности — `docs/performance.md`, «Разбор двух spark-отчётов игрока».

## Вопросы игрока: агрессия мобов, урон от падения, «квадратные» грибы (поток 35, 2026-10-18)
Тесты — `scripts/tests/mobaggro.js`, `falldamage.js` (через `EXTRA_SERVER_SCRIPTS`, `TEST_COMMANDS` с `#sleep`), `scripts/check_mod_models.py`.

**1. «Крейки, Пугало, Большая Летучая Мышь не бьют в выживании».**
- **Крейк** (`lycanitesmobs:krake`, Lycanites Mobs): в `config/lycanitesmobs/creatures/krake.json` `"peaceful": true`, в описании мода
  «Крейки нейтральны и атакуют только в возбуждённом состоянии» (если бить или бегать рядом). **По задумке не нападает**, баг не найден.
- **Большая летучая мышь** — по описанию это Lycanites **Эпион** (`epion`, «гигантские теневые кровавые летучие мыши», плюются
  пиявками, выходят в полнолуние, на солнце падают и взрываются) или **Балаянг** (`balayang`, «циклопические летучие мыши с клыками»).
  Оба **агрессивны** и на тесте выбирают игрока целью (survival, normal, ночь, 20 с). Если днём «не бьют» — это ИИ Эпиона/ночной спавн.
- **Пугало**: сущности с таким названием **нет** ни в одном ru/en-lang серверных и клиентских jar сборки и ни в `pack/resourcepacks-src`
  (есть лишь предметы «Шляпа/Накидка/Штаны/Ботинки пугала» Iron's Spells). Нужно точное название (англ. из Jade/подсказки) или мод.
- Общий прогон (469 видов мобов Lycanites, FUR, Alex's Mobs/Caves, Spore, Chaos Persists; фейковый игрок survival, normal, ночь, 20 с,
  моб в 6 блоках): выбрали игрока целью 242 (Lycanites 90 из 122, Spore 59/81, Chaos Persists 51/116, Alex's Caves 15/44, FUR 8/13,
  Alex's Mobs 19/91). Ожидаемо не нападают: 14 Lycanites с `peaceful: true` (крейк, ика, висп, аризавр, аспид…) и мирные/водные/дневные виды.
  Не разбирали: Lycanites с `peaceful: false`, но IDLE (barghest, warg, kobold, vespid, wildkin…) — вероятно, условия суши/дня/стаи;
  FUR `mimic`/`mummy`/`weta`. Урон не измеряется (**FakePlayer Forge неуязвим**), признак — `getTarget()`.
  Контроль: ваниль (husk, skeleton, spider), mycosis, unburied, ghoul, wraith — TARGET.

**2. «Урона от падения нет».** Не баг сборки. Расы Arknights Races **Cautus, Feline (`arks:feline`) и Aslan** имеют силу
  `origins:modify_falling` с `take_fall_damage: false` (`data/arks/powers/{cautus,feline,aslan}/modify.json`): `LivingFallEvent` получает
  множитель 0, `causeFallDamage` возвращает false. Без расы / human / `arks:seahunter` / `arks:agir` / `origins:feline` множитель 1 и урон
  проходит (событие `dist=20 mult=1`). Класс **«Авангард»** (Arknights Skills: Charger/Pioneer/Tactician) падение не меняет — только
  `puffish_attributes:jump` (высота прыжка), скорость, добыча. Ни KubeJS, ни наш ASM-патч прыжка (#65), ни конфиги падение не отключают.
  Проверка — `falldamage.js` (ServerPlayer без соединения; замер здоровья у него шумит, надёжны `event mult` и `causeFallDamage`).
  Правок нет: «особенность» трёх рас; чтобы включить падение им, надо править `data/arks/powers/*` датапаком (по решению владельца).

**3. «Грибы иногда превращаются в квадрат».** «Гриб-покров» (`mod_lavacow:veil_shroom`) — **блок-растение**, не моб (как `cordy_shroom`,
  `glowshroom`, `bloodtooth_shroom`; у Mycosis на голове/спине — кордицепс или светящийся гриб). Фикс #71 (слой Mycosis рисует
  блок-состояния через `renderSingleBlock`, а не «дополнительные» модели) относится к мобу; сами блоки-грибы всегда шли обычными
  blockstate-моделями, которым ModernFix `dynamic_resources` не мешает. `check_mod_models.py` по `fishs-undead-rising-port-0.3.0.jar`
  и `necromancers-delight-0.2.0.jar`: blockstate → модель → текстура — 0 проблем (все `age=0/1` у четырёх грибов, предметные модели).
  Воспроизвести фиолетово-чёрный куб **не удалось и в клиенте не проверялось** (облачный клиент входит в мир только с долгой пересборкой
  JEI-индекса, см. «Проверка клиента»). Если у игрока повторится — нужны скриншот, F3 (что за блок/моб) и строки `Unable to load model`
  / `Missing textures` из `logs/latest.log` клиента.

### «Игрок не получает урон вообще» (поток 35, 2026-10-18, жалоба владельца: сложность «Сложная», урон потом «появился» без правки файлов) — **причина не найдена**
- **SRP DamageLimitHandler — не виноват**: слушает `LivingHurtEvent` и режет урон только если *получатель* урона из `[damage_limit].entities` (у нас `spore:proto`) до `maxHealth / min_kill_count`; на игрока не действует.
- HUD «Сложность:» — Contingency Contract (его `ZbHbFuncProcedure` на `EntityJoinLevelEvent` лишь усиливает мобов: зомби/утопленники получают эффекты/атрибуты), «Мысль / Спокойно» — Sarkaz's Furnace Side Fables (HUD «нагрузки мысли», хроника); обнуления входящего урона у них не найдено (Sarkaz `CombatEventHandler`: срезы ×0.88/0.8/0.65 за открытые «концовки», отмена урона между «нарраторами» и ударами Амии).
- `scripts/tests/hurtcheck.js`: события Attack/Hurt/Damage (generic, fall, удар зомби, magic, огонь) через `EVENT_BUS` для свежего `ServerPlayer` на hard — **ни один слушатель не отменяет и не режет** (amount 5 → 5). Постоянного глобального блокера нет; исключения от Mahou Tsukai/Caerula Arbor в тесте — только из-за отсутствия сетевого соединения у тестового игрока.
- Значит, это **временное/условное состояние игрока**. Кандидаты (не подтверждены): расовые силы Arknights Races — Griffin (`arks:griffin`: обнуляет один удар раз в 10 с, ресурс `arks:griffin/time`), Anasa (переключаемый навык, множитель −1), Cautus/Feline/Aslan (только падение); броня Finale/Wuzhou (Sarkaz); эффекты длительностью в десятки минут (Contingency Contract, Mahou Tsukai); ваниль: 60 тиков неуязвимости после входа.
- Что снять у владельца в момент «нет урона» (F3 не нужен): `/effect give` не трогать; `/data get entity @s ActiveEffects`, `/data get entity @s Attributes`, `/data get entity @s abilities`, `/data get entity @s Invulnerable`, `/data get entity @s ForgeCaps` (раса/ресурсы Origins в `ForgeCaps`), `/origin get @s origins:origin`; какая надета броня; вернулась ли вода/Эндер и т. п.

## R-003: ПК завис при загрузке ресурсов, mm-perf 0.2.1 (поток 41, 2026-10-18)
Отчёт игрока (ник не приводим): компьютер завис при загрузке ресурсов, до главного меню; лог оборвался без ошибки, в нём не было ни Xmx, ни объёма ОЗУ.
Вероятная причина — Xmx слишком близко к физической ОЗУ (своп), но по логу это не проверить. Что сделано:
- `MemoryWatch.earlyCheck()` вызывается из конструктора мода (до загрузки ресурсов) и пишет `[mm_perf] память: Xmx …, Xms …, ОЗУ ПК …, свободно ОЗУ …, ядер …, GC …`
  (из аргументов JVM берутся только `-Xmx/-Xms/-XX:+Use*GC`); при Xmx > 70 % ОЗУ или ОЗУ − Xmx < 4 ГБ — WARN по-русски с указанием, где уменьшить (Prism → Настройки → Java → Память).
  Тост в главном меню остался, условие то же.
- `pack/config/fml.toml`: `versionCheck = false` (Forge не ходит за обновлениями модов). Источник HTML-страницы GitHub в логе по jar не установлен; если после этого она останется — искать в конфиге Saturn/другого мода.
- Рекомендация в `docs/friends.md`: Xmx не больше ~60 % ОЗУ, при 16 ГБ — не больше 8 ГБ.

## Версия сборки для игрока: mm-perf 0.3.0, `config/mm_pack_version.txt` (поток 47, 2026-10-19)
Зачем: в форме отчётов есть поле «Версия сборки», а игрок не знал, где её взять (`pack.toml` версию не менял, Prism пакет не показывает).
- **Схема** `ГГГГ.ММ.ДД-N` (дата + номер за день), сейчас `2026.10.19-1`. Задаётся в `pack/pack.toml` (`version`) и в `pack/config/mm_pack_version.txt` — две одинаковые строки;
  правило поднятия и проверка `scripts/check-pack-version.py` — CLAUDE.md, «Сборка (packwiz)». Имя `.mrpack`/`-server.zip` теперь тоже содержит версию.
- **Почему файл в `config/`, а не `pack.toml`** (проверено на собранном `.mrpack` и серверном zip, `scripts/export-pack.sh`): в `.mrpack` `pack.toml` нет (только `modrinth.index.json`,
  где `versionId` = версия пакета, но до игры она не доходит), packwiz-installer кладёт в экземпляр только файлы из `index.toml`, а не сам `pack.toml`/`index.toml`.
  `config/mm_pack_version.txt` лежит в `overrides/config/` `.mrpack`, доставляется packwiz-installer'ом и попадает в `config/` серверного zip.
- **Мод**: `PackVersion` (mm-perf 0.3.0-client) читает файл в конструкторе (до загрузки ресурсов) → строка `[mm_perf] версия сборки: …` в логе; «Minecraft_moded <версия>» — в левом верхнем углу
  главного меню (`ScreenEvent.Render.Post` на `TitleScreen`) и в заголовке окна («Minecraft 1.20.1 | Minecraft_moded …», ставится раз в секунду: ваниль перезаписывает заголовок при смене экрана/мира,
  `Minecraft.createTitle()` приватный — название мира/сервера в заголовке теряется). Нет файла, больше 255 байт или не `[0-9A-Za-z._+-]{1,32}` — «Minecraft_moded: версия неизвестна» + WARN, без падений.
- **Проверка**: `test-client.sh` на собранном `.mrpack` — `CLIENT OK` (586 с), в логе `[mm_perf] версия сборки: 2026.10.19-1`, на скриншоте главного меню в левом верхнем углу «Minecraft_moded 2026.10.19-1»
  (после нажатия «Перейти в главное меню» на экране предупреждений YSM — облачная особенность). Заголовок окна под Xvfb без оконного менеджера не проверялся (код: `Window.setTitle`).
  `test-server.sh` — `SERVER OK: Done (86.5 с)`; файл версии в `config/` сервера есть, mm-perf на сервере нет (клиентский jar).
- Форма отчётов: подсказка к «Версии сборки» теперь про угол меню; `bootstrap()` обновляет её у уже созданной формы (`applyVersionHelp_`, идемпотентно; тест `scripts/tests/report-sheet-test.js`).

## Красное предупреждение «Xmx > 60 % ОЗУ»: mm-perf 0.3.1 (поток 55, 2026-10-19)
Зачем: в R-003 и R-006 игроки ставили Xmx 10 ГБ на ПК с 16 ГБ → своп и фризы; мелкий всплывающий тост (порог 70 %) не замечали.
- **Условие**: `Runtime.maxMemory()` > 60 % `OperatingSystemMXBean.getTotalMemorySize()` (в `earlyCheck`, из конструктора мода). Не удалось узнать ОЗУ (исключение/0) — ничего не показываем и не падаем.
- **Что видит игрок**: красная (0xFF5555) строка с переносом под версией сборки в главном меню (`PackVersion.drawOnTitle`); одно красное сообщение в чат после первого входа в мир
  (`MemoryWatch.tickSecond`, `sendSystemMessage` — только клиенту); WARN в логе `[mm_perf] ВНИМАНИЕ: Xmx … ГБ — это N % физической ОЗУ …, больше порога 60 %…`.
- **Z (рекомендация)**: строго по таблице friends.md (≥28 ГБ → 10, ≥15 → 8, иначе 6; без поправки на «половину ОЗУ» — ниже 6 ГБ сборка уходит в OOM). Предупреждение — только если Xmx > 60 % ОЗУ **и** Xmx > Z + 0,5 ГБ (штатные 6 ГБ на 8 ГБ ОЗУ не ругаем) (правка по ревью #143).
- **Проверка порога**: `-Dmm_perf.mem.threshold=<проценты>` занижает порог (для test-client.sh: `CLIENT_JVM_EXTRA="-Dmm_perf.mem.threshold=1"`).
- Старый тост (Xmx < 7 ГБ, > 70 % или остаётся < 4 ГБ) оставлен как был.
- **Проверка** (сборка `2026.10.19-3`): `test-client.sh` с `CLIENT_JVM_EXTRA="-Dmm_perf.mem.threshold=1"` — `CLIENT OK` (533 с); в логе `[mm_perf] ВНИМАНИЕ: Xmx 5.0 ГБ — это 37 % физической ОЗУ (13.4 ГБ), больше порога 1 %… Рекомендуем не больше 6 ГБ`
  (облачный ПК 13 ГБ → Z = 6). Красная строка в меню и сообщение в чат глазами **не проверены**: скриншот под Xvfb снят на экране предупреждений YSM (облачная особенность), в мир тест не входил.
  `test-server.sh` — `SERVER OK: Done (63 с)`; mm-perf в серверном zip нет (0 совпадений), в логе сервера строк `[mm_perf]` нет.
- **Повторная проверка после правок ревью** (jar пересобран, сборка `2026.10.19-3`): `CLIENT OK` (535 с), WARN в логе есть (порог `-Dmm_perf.mem.threshold=1` отключает защиту «штатного Xmx»); `SERVER OK: Done (66 с)`.

## R-007: чёрно-розовые заглушки — «Заражение паразитом» и «Гриб-покров» (поток 56, 2026-10-19, сборка 2026.10.19-4)
Отчёт игрока (ник не приводим): вместо текстур чёрно-розовые заглушки в двух местах, «каждый раз».
- **Иконка эффекта «Заражение паразитом»** (`srpsystem:infection`, «Вредный», −20 % к скорости, цвет #8b0000) — это эффект мода **SRP System**
  («One-Click Parasite Transformation» 1.0.0, Modrinth), не FUR. **Причина найдена:** в jar вообще нет `textures/mob_effect/` (только звуки и `pack.png`),
  а эффект рисуется спрайтом `srpsystem:textures/mob_effect/infection.png`, которого нет → заглушка в HUD и в инвентаре.
  (Заодно: `srpsystem:sounds/nude.ogg` в `sounds.json` нет — WARN при загрузке звуков, на внешний вид не влияет.)
  **Правка:** иконка 18×18 `pack/resourcepacks-src/russian-moded/assets/srpsystem/textures/mob_effect/infection.png` — нарисована нами (тёмно-красное
  кольцо #8b0000 с зелёной спорой), чужих ассетов нет; в сборку идёт тем же путём, что русский пак (клиентский мод `ru-lang`, пак всегда включён и сверху;
  `scripts/build-ru-pack.sh`). Сервера это не касается.
- **«Гриб-покров»** (`mod_lavacow:veil_shroom`, наш порт Fish's Undead Rising 0.3.0): blockstate (`age=0/1`) → `block/veil_shroom`, `block/veil_shroom_s` →
  `textures/block/veil_shroom(_s).png`, предметная модель `item/veil_shroom` → `block/veil_shroom` — все файлы на месте, текстуры 16×16 RGBA с нормальным
  рисунком (шляпка и сетчатый «покров»), как у `cordy_shroom`. В логе клиента сборки 2026.10.19-3 (CLIENT OK, 595 с) нет `Unable to load model`
  / `Missing textures` ни по `mod_lavacow`, ни по `srpsystem`. Ресурспаки сборки (`just-some-translations`, `mploc-mods-v43`, `russian-moded`) файлов `mod_lavacow`/`srpsystem` (кроме `lang` и новой иконки) не содержат,
  так что подменить модель нечему. **Воспроизвести не удалось, кода-причины нет → правок по блоку не делали.** Поток 35 («модели целы») подтверждён заново.
  Вероятно, игрок видел заглушку именно у иконки эффекта (она рисуется рядом с подсказкой эффектов). Если «Гриб-покров» останется фиолетово-чёрным и
  после 2026.10.19-4 — нужны скриншот, F3 (что за блок/предмет, состояние `age`) и строки `Unable to load model` / `Missing textures` из `logs/latest.log`.
- **Проверка:** сборка 2026.10.19-4 из `export-pack.sh` → `test-client.sh`: **CLIENT OK** (главное меню 582 с, в углу «Minecraft_moded 2026.10.19-4»);
  в логе нет `Unable to load model` / `Missing textures` по `mod_lavacow` и `srpsystem`. Вход в мир в облаке не удался (клиент + интегрированный сервер
  на 15 ГБ ОЗУ: OOM при 5 ГБ кучи, затем cgroup-OOM при 11 ГБ; `xdotool` в облаке не стоит — `apt-get install xdotool`), поэтому саму иконку
  в HUD и блок в мире глазами не видели: иконка проверена по наличию файла в jar `ru-lang` (`resourcepacks/russian-moded/assets/srpsystem/textures/mob_effect/infection.png`)
  и по разбору движка (спрайт эффекта берётся по `<namespace>:textures/mob_effect/<id>.png`). SERVER OK — см. ниже.
- Прочее по `ru-lang`: чтобы поправить иконку позже — править файл в `pack/resourcepacks-src/`, затем `scripts/build-ru-pack.sh`, поднять версию сборки.

## Enchanting System Overhaul: AbstractMethodError на модовой броне (поток 57, 2026-10-19, сборка 2026.10.19-5)
- **Симптом (до порта):** сервер с ESO 1.2.2 не стартует — `Failed to load datapacks`, причина
  `AbstractMethodError: Receiver class de.joh.dmnr.common.item.material.ArmorMaterials does not define or inherit an implementation
  of ... m_6082_()` в `aiefu.eso.data.materialoverrides.MaterialOverrides.<init>`. По очереди так же падали Factions and Curiosities,
  Deep Aether, Ancient Aether (поток 54).
- **Причина:** у этих материалов брони `getName()` помечен `@OnlyIn(Dist.CLIENT)` (проверено `javap -v` у DMnR); на выделенном
  сервере RuntimeDistCleaner вырезает метод, а ESO обходит все предметы реестра и зовёт его у каждой брони.
- **Решение:** порт `patches/jar-ports/eso` (`scall` на `dev.mcmoded.port.eso.EsoPort.materialName`: `getName()`, при вырезанном
  методе — имя enum/класса). Переходник вызывает `getName()` через ссылку на метод: JarPort переписывает и классы-переходники, и прямой
  вызов превратился бы в рекурсию (первая сборка порта так и упала `StackOverflowError`).
- **Проверка:** `SERVER OK` (Done 22–27 с) со всей сборкой; `scripts/tests/esobooks.js` — книги модовых чар выпадают во всех
  20 изменённых таблицах, фейковый игрок изучает чару правым кликом, `/eso` зарегистрирована (`ESOBOOKS_DONE bad=0`);
  `scripts/tests/enchdump.js` — 227 чар. **Проверка клиента:** `CLIENT OK` (535 с) из `.mrpack` 2026.10.19-5 с первой сборкой
  порта (в финальном jar добавлен только `ru_ru.json`); в логе клиента по ESO только безвредное `mixins.eso.json does not specify
  "minVersion"`. Меню стола, оплату и наковальню глазами не смотрели (нужен живой клиент на сервере).
- **Подвох для лута:** Alex's Caves миксином в `EnchantRandomlyFunction` подменяет свои чары на случайные чужие, пока в его конфиге
  `enchantments_in_loot = false` — поэтому книги AC в `eso_enchant_books.js` делаются через `set_enchantments`, не `enchant_randomly`.
  Подробно и таблица «чара → где взять книгу» — `docs/enchanting-table.md`.

## R-009 / R-010: раса Origins, сила `arks:human/ori`, достижения Animus (поток 58, 2026-10-24, сборка 2026.10.24-2)
Отчёты игроков на 2026.10.19-5: «пропал класс и бафы класса» (один раз) и «раса не работает» (каждый раз, без деталей).
В логе у всех: `Power "arks:human/ori" will only be partially loaded: … Unknown registry key: origins:type`.

- **`arks:human/ori`** — сила расы «Человек» из jar Arknights Races 1.5.3 (не наш оверрайд; #124 трогал только
  `arks:*/thought`). Три подсилы `invulnerability` проверяли урон условием `origins:type` + `damage_type` — в Apoli 2.9
  (Origins-Forge 1.10.0.9) такого условия урона нет (типы урона с 1.19.4 — через теги: `origins:in_tag`). Apoli отбрасывал
  эти три подсилы, остальное грузилось: Человек терял **иммунитет к урону ориджиниумом** (`bobsoriginiumdream:oridamage`,
  `origintantdamage`, `oribleed`). Бонус нагрузки мыслей (#124, отдельная сила `thought`) и «Орипатия» (атрибуты
  `orillnessprotect` +20 / `oricompletelyprotect` −1 и сброс `oridisease` раз в 5 с) не задеты.
  Починка: `pack/kubejs/data/arks/powers/human/ori.json` (`loading_priority` 10, копия оригинала) — три подсилы заменены
  одной `immune` с `origins:in_tag` → тег `pack/kubejs/data/arks/tags/damage_type/human_ori_immune.json` (те же три типа урона).
  Предупреждение в логе **остаётся** — Apoli всё равно читает копию из jar, но в реестр попадает наша (`ori_immune` есть, `ori_immune1` нет).
- Другие «partially loaded» на сервере (все силы Origins-паков, кроме этих, читаются чисто):
  `arks:seaborn_endspeaker/modify1` — нет предмета `caerula_arbor:whole_collector_cooked` (в Caerula Arbor 0.12.7.2, видимо,
  `cooked_collector`): у «Глашатая» нет одного бонуса еды (регенерация 60 с), остальное работает; `arks:sankta/status` —
  атрибут `gunsmithlib:reload_speed` (мода нет в сборке): у Санкты нет только ускорения перезарядки. Не чинили — мелочь.
  В слоях Origins есть ссылки на несуществующие происхождения (`arkarts:eldritch`, `arkbranches:defender_guardian`,
  `defender_juggermaut`) — их просто нет в выборе; `defender_fortress` и `defender_ars_protector` есть в jar, но в слой не внесены.
- **Сброс расы** (`scripts/tests/origincheck.js`, фейковый игрок: Человек + Guard + Guard Lord + Fire): после выбора, смерти
  и штатного `PlayerList.respawn`, `/tp` в Незер и обратно, выхода и **перезапуска сервера** (второй запуск того же мира,
  `PlayerList.load`) — все 4 слоя, 13 сил, иммунитет `111` и предел мыслей 25 на месте. В `pack/kubejs`, конфигах и своих модах
  ничего не меняет и не сбрасывает происхождение. Origins-Forge сам стирает происхождение только если его ID или ID слоя
  пропали из реестра (`OriginContainer.deserializeNBT` «Missing origin/layer») — версии модов рас с 18.10 не менялись.
  Причина R-010 не найдена; нужен вывод `/origin get @s origins:arkclass_layers` в момент пропажи и что было до этого.
  У фейкового игрока `PlayerList.respawn` падает NPE в Xaero's lib (нет `ServerPlayerData`) — тест заводит её вручную.
- **Animus** (аддон Blood Magic): `animus:root` — родитель `bloodmagic:recipes/misc/sacrificial_dagger`, а Quark
  «Automatic Recipe Unlock» удаляет все рецептные достижения (13 885) → вся ветка Animus (9 достижений) не грузилась.
  На игру не влияло (только вкладка достижений). Починка: `pack/kubejs/data/animus/advancements/root.json` без родителя —
  своя вкладка с фоном алтаря. Ошибок `Couldn't load advancement animus:*` больше нет.

## R-008: Priestess — ноги «как две палочки»; вылет меню YSM (поток 59, 2026-10-24, сборка 2026.10.24-1)
- **Причина «палочек»:** наша собственная модель (папка, `mods-src/ysm-priestess/build.py`, не `.ysm`): в v2 ноги сделаны 3,1×3,2 (бедро) и 2,9×2,9 (голень) при высоте 10 px — прямые столбики. UV, привязка анимаций и версия YSM ни при чём (кости ног — от скелета YSM default).
- **Исправление (v5):** бедро 4,0×3,8, голень 3,5×3,4, остальное без изменений. Проверено своим рендером (`previews.py`), в игре глазами — нет. Авторство — `docs/ysm-models.md`.
- **Проверка:** `SERVER OK` (Done 60 с, `SERVER_XMX=5G`; с 8G контейнер убил java по `oom-kill` на ~6 ГБ — это лимит памяти облака, не сборка), `CLIENT OK` (514 с) из `.mrpack` 2026.10.24-1.
- **Вылет меню YSM (отчёт автора):** на текущей сборке воспроизвести нельзя: клиент YSM в облаке не запускается (`YSM client cannot run under a virtual machine`, `[YSM] Unsatisfied runtime environment`), меню моделей под Xvfb не открывается. Других ошибок YSM в логе клиента нет. Ждём лог вылета от автора.

## «Из любого моба падают железные/алмазные блоки, звёзды Незера» — бонусные мобы Contingency Contract (поток 60, 2026-10-24, сборка 2026.10.24-3)
- Жалоба владельца: самые поздние ресурсы выпадают почти из любого моба, даже обычного — ломает прогрессию.
- Воспроизведено тестом `scripts/tests/mobdrops.js` (по 100 зомби/скелетов/пауков/кур/коров, смерть от фейкового игрока и `kill()`;
  плюс 1000 бросков таблиц `entities/*` с GLM Forge). Таблицы лута и глобальные модификаторы **чистые** (LOOTROLL без ценностей);
  лишнее падает **только при убийстве игроком**: на 500 мобов — 45 алмазов, 11 незеритового лома, 5 железных и 4 алмазных блока,
  звезда Незера, 4 зачарованных золотых яблока, сердце моря, трезубец, яйца нюхача, осколки эха, око Эндера, пузырьки опыта.
- Причина — штатная механика **Contingency Contract Lite** (Arknights, в сборке с PR #94): `ZbHbFuncProcedure` на `EntityJoinLevelEvent`
  с шансом 10 % помечает каждого моба, кроме игрока, атрибутом `contigency_contract:bonusable = 1` (рядом с ним частицы `bonus_singal`),
  а `DeadFuncProcedure` при убийстве игроком бросает для такого моба таблицу `contigency_contract:gameplay/chronicle_bonus`
  (алмазы 3–7, алмазный блок, незерит, железо 8–16, 1–2 железных блока, звезда Незера, яблоки…; 1 бросок + бонусный).
  Включено по умолчанию (`MapVariables.enableBonus = true`, хранится в мире). Не ESO (#148) и не Loot Integrations (те — только сундуки).
- Исправление: `pack/kubejs/server_scripts/cc_no_mob_bonus.js` — на старте сервера штатная команда мода `/enableBonus false`,
  плюс снятие метки `bonusable` с мобов при входе в мир (уже помеченные животные из старых чанков не деспавнятся). Остальные
  механики мода (усиление мобов, контракты, хроники) не тронуты. Вернуть: удалить файл и `/enableBonus true`. **Заменено потоком 61 (ниже): бонус редкий, 0,1 %.**
- После: `MOBDROPS_DONE bad=0`, `ccbonus=0` у всех 1000 мобов, корова из «старого чанка» (summon с `bonusable = 1`) → `bonusable=0`; дроп — ваниль + задуманные добавки модов (книги заклинаний EBWizardry
  ~1–2 %, «мысли» и `soul_spirit_matter` Sarkaz's Furnace Side Fables при убийстве игроком, филе Arknights Endfield с коров,
  `aggagrit*` Arknights Endfield со скелетов, лук/морковь у зомби). SERVER OK, CLIENT OK (меню за 576 с, llvmpipe).

## Бонусные мобы Contingency Contract — редкие, 0,1 % (поток 61, 2026-10-24, сборка 2026.10.24-4; решение владельца В49 «б»)
- Владелец: «шанс выпадения около 0.1%, оставь шанс на железо, золото, алмазы, но не блоки и очень низкий шанс на звезду незера».
  Заменяет полное выключение из PR #154 (раздел выше).
- Штатно настроить шанс нельзя: конфига у CC Lite нет, шанс пометки зашит в `ZbHbFuncProcedure` (`Math.random() < 0.1`,
  один раз на моба — флаг-атрибут `contigency_contract:tagged`). Штатно настраивается только награда — лут-таблица
  `contigency_contract:gameplay/chronicle_bonus` (её же мод бросает при копании в хронике 99, `DigFuncProcedure`, 4 %).
- Награда — перекрытие датапаком `pack/kubejs/data/contigency_contract/loot_tables/gameplay/chronicle_bonus.json`:
  1-й пул (1 бросок, без bonus_rolls): железо 3–8 (34 %), золото 3–8 (26 %), алмазы 1–3 (13 %), лазурит, уголь, золотая морковь, золотое яблоко (2,5 %);
  2-й пул (0–1 бросок): аметист, пузырьки опыта, раковины наутилуса, `crisis_coin` мода, **звезда Незера — вес 2 из 412 (~0,25 % наград)**.
  Убраны: блоки железа/алмазов, незерит (лом, меч, шаблон), зачарованные яблоки, тотем, трезубец, сердце моря, яйца нюхача, осколки эха, око Эндера.
- Шанс пометки — `pack/kubejs/server_scripts/cc_rare_mob_bonus.js` (вместо `cc_no_mob_bonus.js`): на старте `/enableBonus true`;
  при появлении моба метку мода оставляем с шансом 1 % (снимаем атрибут `bonusable` и эффект-частицы `bonus_sign`) → 10 % × 1 % = 0,1 %.
  Решение одно на моба (флаг `mm_cc_bonus_roll` в persistentData), перезагрузка чанка шанс не повышает. Порядок нашего обработчика
  и обработчика мода не задан, поэтому если метки ещё нет — проверка через тик и при смерти (`EntityEvents.death`).
  Итог на убийство: звезда Незера ≈ 0,1 % × 0,25 % ≈ 1 на 400 000 мобов; алмазы ≈ 1 на 7 700 мобов.
- Тест `scripts/tests/ccbonus.js` (`EXTRA_SERVER_SCRIPTS=<папка только с ним> TEST_COMMANDS=<«#sleep 900»>`): `CCBONUS_DONE bad=0`.
  - ROLL ×100 000 прямых бросков таблицы: блоков/незерита нет; звезда Незера — 257 (2,57 на 1000), алмазы — в 13 %, золотое яблоко — 2,5 %.
  - MARK 20 000 появившихся мобов (зомби/скелет/паук/крипер/корова/курица): помечено 14 (0,070 %; ожидание 20 ± 4,5), флаг броска у всех 20 000,
    частиц без метки — 0.
  - KILL 2000 мобов убиты фейковым игроком через 2 тика после появления: помечен 1, из него алмазы 3; блоков нет.
  - SAME 1000 мобов убиты в тик появления: наград 0 (без проверки при смерти было бы ~100).
  - FORCE 300 помеченных мобов убиты игроком (настоящий путь `DeadFuncProcedure` с новой таблицей): железо 536, золото 447, алмазы 83, аметист,
    `crisis_coin`… — блоков, незерита и пр. нет.
- Откат: полностью выключить — вернуть `cc_no_mob_bonus.js` из PR #154 (`git show 1188f03:pack/kubejs/server_scripts/cc_no_mob_bonus.js`) вместо
  `cc_rare_mob_bonus.js` и `/enableBonus false`; вернуть исходную награду мода — удалить перекрытие таблицы; другой шанс — `CC_KEEP` в скрипте.

## Орипатия Bob's Originium Dream на всех расах, кроме лорно иммунных (поток 62, 2026-10-24, сборка 2026.10.24-5, В50 «б»)
Просьба владельца: «чтобы орипатия работала на все расы», но «не должна действовать на тех, у кого по лору есть к ней иммунитет
(раса Предтеч, Эбисалы… из этой серии)».

**Как устроено (код Bob's 1.1.1):** индекс `bobsoriginiumdream:orillness` +1 за каждый урон `bobsoriginiumdream:oridamage` (кристаллы,
ядра, буря, мобы) любому существу (`OrichangeProcedure`, кроме полного комплекта брони oriprotect, до 150). Тип болезни
`oridisease` 1–3 (бафф + дебафф при уроне, см. `Oripathy1Procedure`/`OrillinessbenefitProcedure`) ставится игроку, **только если
`oricompletelyprotect` ≠ 0** (по умолчанию 1) и правило `oriillness` включено. От расы Bob's сам ничего не зависит.

**Было (тест `scripts/tests/oripathy.js`, 61 раса слоя `origins:origin` + без расы, 40 событий урона `oridamage`):** заражаются все,
кроме `arks:human` («Pre-human» = Предтеча, в моде — Доктор): сила `arks:human/ori` (наш оверрайд #152) — неуязвимость к урону
ориджиниумом, `oricompletelyprotect` −1, сброс `oridisease`. Seaborn/Endspeaker только −10 индекса раз в 5 мин, Abyssal Hunter — обычный.
Ванильные Origins (human, avian, arachnid, blazeborn, elytrian, enderian, feline, merling, phantom, shulk) и игрок без расы — заражаются.

**Стало:** новая сила `pack/kubejs/data/arks/powers/mm_ori_immune.json` («Иммунитет к орипатии»: `oricompletelyprotect` −1,
`orillnessprotect` +20, сброс `orillness` и `oridisease` в 0 раз в 100 тиков; урон ориджиниумом проходит — неуязвимость к самому урону
только у Предтечи, как задумано модом) добавлена расам переопределением `pack/kubejs/data/arks/origins/{seahunter,seaborn,seaborn_endspeaker}.json`
(копии из jar Arknights Races 1.5.3 + `arks:mm_ori_immune`, `loading_priority` 10).

| Раса | Орипатия | Почему |
|---|---|---|
| `arks:human` (Pre-human, Предтеча) | **нет** | лор и задумка мода, сила `arks:human/ori` (без изменений) |
| `arks:seahunter` (Абиссальные охотники) | **нет** | лор: гены Seaborn, `arks:mm_ori_immune` |
| `arks:seaborn`, `arks:seaborn_endspeaker` | **нет** | лор: Seaborn «из той же серии», `arks:mm_ori_immune` (В50 владельцу — подтверждение) |
| `arks:agir` (обычные Эгир) | да | по лору не иммунны, в Эгире просто мало ориджиниума (В50 владельцу — подтверждение) |
| `arks:sankta`, `arks:elf` и остальные 43 расы Arknights | да | по лору заражаются (Эльфы, по описанию мода, особенно уязвимы) |
| 10 рас ванильных Origins, без расы | да | не из Arknights — по решению владельца орипатия на них действует |

Тест «после» (тот же `oripathy.js`, урон → 120 тиков игрока): у трёх иммунных `orillness` 40 → 0, `oridisease` → 0, `complete=0`
`protect=20`; у Предтечи урон не проходит (0/40); у остальных 57 — `orillness` 40, `oridisease` 1–3. Ошибок загрузки новой силы и
рас в логе нет (прежнее `arks:seaborn_endspeaker/modify1` «partially loaded» — см. R-009/R-010).
Тонкость: тест шлёт `LivingHurtEvent` напрямую (фейковый игрок вне мира для ванильного `hurt()` неуязвим), неуязвимость Origins проверяет
`InvulnerablePower.isInvulnerableTo`. Между ударом и сбросом (≤ 5 с) индекс у иммунных может ненадолго подняться — эффектов это не даёт.
- Откат: удалить `pack/kubejs/data/arks/powers/mm_ori_immune.json` и папку `pack/kubejs/data/arks/origins/` (расы вернутся к jar).
  Сделать иммунной/неиммунной ещё одну расу: скопировать её `data/arks/origins/<раса>.json` из jar, добавить/убрать `arks:mm_ori_immune`.

## R-011: Priestess — юбка сквозь плащ, разрыв волос, щель в коленях сидя (поток 63, 2026-10-25, сборка 2026.10.25-3)
- **Причины (все — наша модель, `mods-src/ysm-priestess/build.py`):** юбка висит на ногах (`Skirt` ← `DownBody`), плащ — на корпусе,
  клёш юбки шире вертикального плаща; длинные пряди (`LongHair`) начинались на уровне шеи, а у затылка/боков «шапки» были синие
  кончики — полоса-разрыв; голень заканчивалась на оси колена — при сгибе V-вырез.
- **Исправление (v6):** плащ — А-силуэт (постоянные углы в `parallel4`), клёш юбки умереннее; полы отводятся вперёд при приседе
  (`q.is_sneaking`), беге, sit, езде, в лодке; пряди от макушки поверх шапки; продолжение голени выше оси колена. Подробно и
  картинки «было/стало» — `docs/ysm-models.md` (v6), `docs/img/ysm-priestess/r011_*.png`.
- **Проверка:** свой рендер — точек юбки снаружи плаща: стоя/меню/ходьба/бег/прыжок/полёт/лодка 0 (было 1 800–4 400),
  присед/езда/sit ≤ 18 (было 1 500–4 500). `SERVER OK` (Done 77 с, `SERVER_XMX=5G`); `/ysm export priestess` → `Saved as
  'export/priestess.ysm'` (и старая v5 рядом — тоже). Заметка: на первом запуске свежего сервера команда через ~60 с после Done
  ответила `Failed to export model : priestess`, на повторном запуске того же сервера — успешно для обеих версий; это тайминг
  загрузки моделей YSM, не модель. `CLIENT OK` (510 с) из `.mrpack` 2026.10.25-1; ошибок по Priestess в логе нет, клиент YSM
  в облаке по-прежнему отказывается работать (`YSM client cannot run under a virtual machine`, К7). В игре глазами (клиент YSM в облаке не запускается, К7) — по чек-листу в `docs/ysm-models.md`.
- **Откат:** `git revert` коммита PR (модель — генерируемые файлы в `pack/config/yes_steve_model/custom/priestess/`).

## R-012: долгое «Сохранение мира» — замер и mm-perf 0.3.2 (поток 64, 2026-10-25, сборка 2026.10.25-3)
Отчёт: одиночная игра, 4 ядра / 16 ГБ / Xmx 8 ГБ, «завис мир и игра при сохранении», 52 «Saving chunks», последний «Can't keep up» 26 с на автосейве/выходе.
Разбор и таблица — `docs/performance.md`, «Долгое сохранение мира». Коротко: 52 измерения стоят 0,3–0,5 с на сохранение, автосейв ≤ 2 с; долгая пауза —
сохранение с flush (выход из мира) ждёт догенерацию начатых чанков: 47 с во время генерации ~320 чанков против 0,9 с, когда генерация закончилась.
- **Тест** `scripts/tests/savecheck.js` (`EXTRA_SERVER_SCRIPTS`): фазы A (пусто), B/B2 (forceload во всех 52 измерениях, сразу и через 90 с), C (после снятия),
  G (сохранение через 5 с после новых тикетов в несгенерированном месте). KubeJS: `dimension` у уровня — свойство, `java.lang.System` закрыт фильтром классов (время — `Date.now()`).
  Ни одно измерение не держит чанки без игроков/forceload (C и D: 0 чанков во всех 52).
- **mm-perf 0.3.2** (`SaveWatch`, клиент): на экране «Сохранение мира» (`GenericDirtMessageScreen` с `menu.savingLevel`) — «Мир сохраняется — не закрывайте игру»,
  подсказка про полёт/портал (с переносом по ширине окна) и «Идёт N с» с 3-й секунды; в лог `[mm_perf] сохранение мира... / saving world...` и
  `[mm_perf] мир сохранён за N с / world saved in N s` (≥ 10 с — WARN с советом). Само сохранение не меняется. Заодно строки `[mm_perf]` о версии и памяти (INFO/WARN Xmx)
  продублированы латиницей (у части игроков кириллица в `latest.log` приходит битой). Самотест: `-Dmm_perf.selftest.quit=<с>` — «Сохранить и выйти» через N с в одиночном мире.
- **Проверка** (`test-client.sh`, `.mrpack` без jar YSM — облачная особенность, Xvfb + llvmpipe): в логе `[mm_perf] версия сборки: 2026.10.25-1 / pack version: 2026.10.25-1` (проверялось под номером -1; перед PR номер сменён на -3 — -1 и -2 заняли другие потоки),
  `[mm_perf] память: … / memory: Xmx 5G (5.0 GB) …`. Вход в мир: Xmx 5G — `OutOfMemoryError` при создании мира, Xmx 8G — процесс убит OOM-killer контейнера (≈14 ГБ RSS);
  это ограничения стенда, не изменения. С Xmx 6G: `MMPERF-SELFTEST save-and-quit` → экран «Сохранение мира» с подписью и счётчиком (скриншот глазами: строки на месте),
  `[mm_perf] мир сохранён за 5.7 с / world saved in 5.7 s`, 52 «Saving chunks», `All dimensions are saved`. Между нажатием и появлением экрана ещё ~6 с отключения клиента
  (ваниль, экран рисуется после `level.disconnect()`).
- Повтор после переноса строк (jar пересобран): подсказка в две строки по ширине окна (скриншот), `мир сохранён за 4.7 с / world saved in 4.7 s`.
- Тестовый сервер (`test-server.sh`) — `SERVER OK: Done (65 с)`; в серверных модах mm-perf нет, строк `[mm_perf]` в логе сервера нет.
- **Откат**: вернуть `pack/mods/mm-perf-0.3.1-client.jar` из истории git вместо `mm-perf-0.3.2-client.jar`, `packwiz refresh`.
