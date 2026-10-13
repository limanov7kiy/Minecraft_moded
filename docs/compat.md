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
   - Силы `arks:*/thought` рас Arknights (атрибут `the_massestravels:thought_load_limit` из закрытого мода):
     `mm-compat` регистрирует пустой атрибут, только если The Masses Travels нет; сами силы заменены скрытой
     заглушкой (`pack/kubejs/data/arks/powers/*/thought.json`, `loading_priority` 10) — у рас не висит
     «Thought Load +7», который ни на что не влияет.
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
| Vibrative Voice | server | голос — вибрация для скалк-сенсоров/Хранителя |
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
- **Weather2**: оберег регистрируется как «Отражатель бурь» (`WeatherManagerServer.registerDeflector`, рефлексия, повтор раз в 5 с, `removeDeflector` при ломании); бури отводятся и **не рвут блоки**
  в радиусе, который задаёт сам Weather2 (**128** блоков, не наш радиус);
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
