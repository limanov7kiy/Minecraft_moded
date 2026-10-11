package dev.mcmoded.gcrevival;

import dev.mcmoded.gcrevival.block.BossSpawnerBlock;
import dev.mcmoded.gcrevival.block.DungeonSpawnerBlockEntity;
import dev.mcmoded.gcrevival.block.TreasureChestBlock;
import dev.mcmoded.gcrevival.block.TreasureChestBlockEntity;
import dev.mcmoded.gcrevival.block.UnlitWallTorchBlock;
import dev.mcmoded.gcrevival.entity.SkeletonBoss;
import dev.mcmoded.gcrevival.entity.evolved.EvolvedCreeper;
import dev.mcmoded.gcrevival.entity.evolved.EvolvedSkeleton;
import dev.mcmoded.gcrevival.entity.evolved.EvolvedSpider;
import dev.mcmoded.gcrevival.entity.evolved.EvolvedZombie;
import dev.mcmoded.gcrevival.item.KeyItem;
import dev.mcmoded.gcrevival.item.SchematicItem;
import dev.mcmoded.gcrevival.world.dungeon.Corridor;
import dev.mcmoded.gcrevival.world.dungeon.DungeonStart;
import dev.mcmoded.gcrevival.world.dungeon.DungeonStructure;
import dev.mcmoded.gcrevival.world.dungeon.RoomBoss;
import dev.mcmoded.gcrevival.world.dungeon.RoomChest;
import dev.mcmoded.gcrevival.world.dungeon.RoomEmpty;
import dev.mcmoded.gcrevival.world.dungeon.RoomEntrance;
import dev.mcmoded.gcrevival.world.dungeon.RoomPieceType;
import dev.mcmoded.gcrevival.world.dungeon.RoomSpawner;
import dev.mcmoded.gcrevival.world.dungeon.RoomTreasure;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GCRevival.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GCRevival.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GCRevival.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GCRevival.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GCRevival.MOD_ID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, GCRevival.MOD_ID);
    public static final DeferredRegister<StructurePieceType> PIECES = DeferredRegister.create(Registries.STRUCTURE_PIECE, GCRevival.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GCRevival.MOD_ID);

    // Блоки
    public static final RegistryObject<Block> MOON_DUNGEON_BRICK = BLOCKS.register("moon_dungeon_brick", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(4.0F, 40.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final RegistryObject<Block> UNLIT_WALL_TORCH = BLOCKS.register("unlit_wall_torch", () -> new UnlitWallTorchBlock(
            BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BOSS_SPAWNER = BLOCKS.register("boss_spawner", () -> new BossSpawnerBlock(
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noCollission().noLootTable().noOcclusion()));
    public static final RegistryObject<Block> TREASURE_CHEST_T1 = BLOCKS.register("treasure_chest_t1", () -> new TreasureChestBlock(1,
            BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.5F, 3600000.0F).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<BlockEntityType<DungeonSpawnerBlockEntity>> DUNGEON_SPAWNER_BE = BLOCK_ENTITIES.register("boss_spawner",
            () -> BlockEntityType.Builder.of(DungeonSpawnerBlockEntity::new, BOSS_SPAWNER.get()).build(null));
    public static final RegistryObject<BlockEntityType<TreasureChestBlockEntity>> TREASURE_CHEST_BE = BLOCK_ENTITIES.register("treasure_chest",
            () -> BlockEntityType.Builder.of(TreasureChestBlockEntity::new, TREASURE_CHEST_T1.get()).build(null));

    // Предметы
    public static final RegistryObject<Item> MOON_DUNGEON_BRICK_ITEM = ITEMS.register("moon_dungeon_brick", () -> new BlockItem(MOON_DUNGEON_BRICK.get(), new Item.Properties()));
    public static final RegistryObject<Item> TREASURE_CHEST_T1_ITEM = ITEMS.register("treasure_chest_t1", () -> new BlockItem(TREASURE_CHEST_T1.get(), new Item.Properties()));
    public static final RegistryObject<Item> KEY_T1 = ITEMS.register("key_t1", () -> new KeyItem(1, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SCHEMATIC_ROCKET_T2 = ITEMS.register("schematic_rocket_t2", () -> new SchematicItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SCHEMATIC_BUGGY = ITEMS.register("schematic_buggy", () -> new SchematicItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    // Сущности
    public static final RegistryObject<EntityType<SkeletonBoss>> SKELETON_BOSS = ENTITIES.register("skeleton_boss",
            () -> EntityType.Builder.of(SkeletonBoss::new, MobCategory.MONSTER).sized(1.5F, 4.0F).fireImmune().clientTrackingRange(10).build("skeleton_boss"));
    public static final RegistryObject<EntityType<EvolvedZombie>> EVOLVED_ZOMBIE = ENTITIES.register("evolved_zombie",
            () -> EntityType.Builder.of(EvolvedZombie::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("evolved_zombie"));
    public static final RegistryObject<EntityType<EvolvedSkeleton>> EVOLVED_SKELETON = ENTITIES.register("evolved_skeleton",
            () -> EntityType.Builder.of(EvolvedSkeleton::new, MobCategory.MONSTER).sized(0.6F, 1.99F).clientTrackingRange(8).build("evolved_skeleton"));
    public static final RegistryObject<EntityType<EvolvedSpider>> EVOLVED_SPIDER = ENTITIES.register("evolved_spider",
            () -> EntityType.Builder.of(EvolvedSpider::new, MobCategory.MONSTER).sized(1.4F, 0.9F).clientTrackingRange(8).build("evolved_spider"));
    public static final RegistryObject<EntityType<EvolvedCreeper>> EVOLVED_CREEPER = ENTITIES.register("evolved_creeper",
            () -> EntityType.Builder.of(EvolvedCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build("evolved_creeper"));

    // Звуки (GC5)
    public static final RegistryObject<SoundEvent> SKELETON_BOSS_LAUGH = sound("entity.skeleton_boss.laugh");
    public static final RegistryObject<SoundEvent> SKELETON_BOSS_DEATH = sound("entity.skeleton_boss.death");
    public static final RegistryObject<SoundEvent> SKELETON_BOSS_OOH = sound("entity.skeleton_boss.ooh");
    public static final RegistryObject<SoundEvent> SKELETON_BOSS_OUCH = sound("entity.skeleton_boss.ouch");

    // Данж
    public static final RegistryObject<StructureType<DungeonStructure>> MOON_DUNGEON = STRUCTURE_TYPES.register("moon_dungeon", () -> () -> DungeonStructure.CODEC);
    public static final RegistryObject<StructurePieceType> DUNGEON_START = PIECES.register("moon_dungeon_start", () -> (StructurePieceType.ContextlessType) DungeonStart::new);
    public static final RegistryObject<StructurePieceType> ROOM_ENTRANCE = PIECES.register("moon_dungeon_entrance_room", () -> (StructurePieceType.ContextlessType) RoomEntrance::new);
    public static final RegistryObject<StructurePieceType> CORRIDOR = PIECES.register("moon_dungeon_corridor", () -> (StructurePieceType.ContextlessType) Corridor::new);
    public static final RegistryObject<StructurePieceType> ROOM_EMPTY = PIECES.register("moon_dungeon_empty_room", () -> (StructurePieceType.ContextlessType) RoomEmpty::new);
    public static final RegistryObject<StructurePieceType> ROOM_SPAWNER = PIECES.register("moon_dungeon_spawner_room", () -> (StructurePieceType.ContextlessType) RoomSpawner::new);
    public static final RegistryObject<StructurePieceType> ROOM_CHEST = PIECES.register("moon_dungeon_chest_room", () -> (StructurePieceType.ContextlessType) RoomChest::new);
    public static final RegistryObject<StructurePieceType> ROOM_BOSS = PIECES.register("moon_dungeon_boss_room",
            () -> new RoomPieceType(RoomBoss::new, RoomBoss::new, RoomBoss::new));
    public static final RegistryObject<StructurePieceType> ROOM_TREASURE = PIECES.register("moon_dungeon_treasure_room",
            () -> new RoomPieceType(RoomTreasure::new, RoomTreasure::new, RoomTreasure::new));

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.gcrevival"))
            .icon(() -> KEY_T1.get().getDefaultInstance())
            .displayItems((params, out) -> ITEMS.getEntries().forEach(i -> out.accept(i.get())))
            .build());

    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(GCRevival.id(name)));
    }

    static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ENTITIES.register(bus);
        SOUNDS.register(bus);
        STRUCTURE_TYPES.register(bus);
        PIECES.register(bus);
        TABS.register(bus);
    }

    private ModRegistry() {
    }
}
