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
   - **Arknights_Weapon 1.5.6** (MCreator, AFL 3.0; задача из ревью PR #2): `RiseProcedure` на каждом
     `EntityJoinLevelEvent` ставила не-игрокам базу `max_health` = текущий максимум × 2; событие приходит и при загрузке
     чанка — здоровье росло вдвое при каждой подгрузке. Других вызовов у процедуры нет — отключена (`AkWeaponPort.noRise`).
     Проверено `scripts/tests/mobhealth_commands.txt` (моб в чанке x=2000, выгрузка и загрузка чанка): оригинал — зомби
     20→40→80, разбойник 24→48→96; порт — 20/24 и после перезагрузки.
   - **Apothic Attributes** (зависимость T.O) меняет формулы брони и защиты во всей игре (`config/attributeslib.cfg`,
     `combat_rules`): малая броня поглощает больше (зомби с бронёй 2 получает ×0.83 вместо ×0.98). Из-за этого в
     `castcheck.js` поправлен порог метки жертвы (сама метка работает: урон ×1.6). Вернуть ванильные формулы —
     решение пользователя (см. roadmap).
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
| Talking Heads, Let Them Talk, VoiceChatProfiles | client | анимация говорящего, приглушение музыки при речи, профили настроек |
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

## Открытые вопросы
- Серверный тест не проверяет клиент (рендер, шейдеры Oculus + Embeddium): нужен запуск клиента у игрока.

## Порт Galacticraft (если решим делать)
Galacticraft Legacy (1.12.2) → 1.20.1 — это фактически переписывание: между версиями сменились
система блоков (flattening), рендер, сеть, генерация мира и capabilities. Объём — десятки тысяч строк.
Лицензия не препятствует: сборка частная и некоммерческая (CLAUDE.md «Назначение и лицензии») —
код и ассеты Galacticraft Legacy можно переносить с указанием авторства, не публикуя результат.
