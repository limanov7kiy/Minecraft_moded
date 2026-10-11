# Galacticraft Revival — откуда что взято

Неофициальный порт частей Galacticraft для частной сборки (CLAUDE.md → «Назначение и лицензии»). Не публикуется.
Лицензии оригиналов лежат в jar: `META-INF/GALACTICRAFT-LICENSE.txt` (GC5, MIT),
`META-INF/GALACTICRAFT-LEGACY-LICENSE.txt` (Legacy, MIT с 2022 г.), `META-INF/GALACTICRAFT-LEGACY-OLD-LICENSE.txt`
(исходная лицензия micdoodle8/radfast/fishtaco567).

Источники: Legacy — https://github.com/TeamGalacticraft/Galacticraft-Legacy (коммит `acec442`),
GC5 — https://github.com/TeamGalacticraft/Galacticraft (коммит `de07ca0`).

## Код
| Наш файл | Откуда |
|---|---|
| `world/dungeon/*` | GC5 `structure/dungeon/*` (Team Galacticraft) — порт на 1.20.1 Forge; сундук сокровищ в `RoomTreasure` дописан по Legacy `core/world/gen/dungeon/RoomTreasure` |
| `entity/AbstractBossEntity` | GC5 `content/entity/boss/AbstractBossEntity` + логика смерти (сундук, схема, ключ, опыт) из Legacy `core/entities/EntityBossBase` |
| `entity/SkeletonBoss` | GC5 `content/entity/boss/SkeletonBoss` + атрибуты, лут и ключ из Legacy `core/entities/EntitySkeletonBoss` |
| `block/BossSpawnerBlock`, `block/DungeonSpawnerBlockEntity` | GC5 `content/block/boss/BossSpawner`, `content/block/entity/DungeonSpawnerBlockEntity` |
| `block/TreasureChestBlock`, `block/TreasureChestBlockEntity` | Legacy `core/blocks/BlockTier1TreasureChest`, `core/tile/TileEntityTreasureChest` (упрощено: статичная модель) |
| `item/KeyItem` | Legacy `core/items/ItemKey` |
| `entity/evolved/*` | GC5 `content/entity/evolved/*` |
| `client/SkeletonBossModel`, `client/SkeletonBossRenderer` | GC5 `client/model/entity/EvolvedSkeletonBossModel`, `client/render/entity/EvolvedSkeletonBossRenderer` |
| `data/gcrevival/loot_tables/chests/dungeon_tier_1.json` | Legacy `loot_tables/dungeon_tier_1.json` (предметы GC заменены аналогами сборки) |

## Ассеты
| Наш файл | Откуда |
|---|---|
| `textures/block/moon_dungeon_brick.png` | GC5 |
| `textures/block/unlit_torch.png` | Legacy `galacticraftcore/textures/blocks/unlit_torch.png` |
| `textures/block/treasure_chest_t1*.png`, `models/block/treasure_chest_t1.json` | Legacy `galacticraftcore` (`model/treasure.png`, `blocks/treasure_chest.png`, `models/block/treasure_chest.json`) |
| `textures/item/key_t1.png`, `schematic_rocket_t2.png`, `schematic_buggy.png` | Legacy `galacticraftcore/textures/items/` |
| `textures/entity/skeleton_boss.png` | GC5 `textures/entity/skeletonboss.png` |
| `sounds/entity/skeleton_boss/*.ogg` | GC5 |
