package dev.mcmoded.wizardryrestored.block;

import dev.mcmoded.wizardryrestored.WizardryRestored;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/// Блоки дополнения: камень перемещения (четыре камня вокруг точки — пункт назначения заклинания «Перемещение»).
public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, WizardryRestored.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WizardryRestored.MOD_ID);

    public static final RegistryObject<Block> TRANSPORTATION_STONE = BLOCKS.register("transportation_stone", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5f, 6f).requiresCorrectToolForDrops()
                    .sound(SoundType.STONE).lightLevel(s -> 4).noOcclusion()) {
        private static final VoxelShape SHAPE = Block.box(5, 0, 5, 11, 12, 11);

        @Override
        public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
            return SHAPE;
        }
    });
    public static final RegistryObject<Item> TRANSPORTATION_STONE_ITEM = ITEMS.register("transportation_stone",
            () -> new BlockItem(TRANSPORTATION_STONE.get(), new Item.Properties()));

    private ModBlocks() {
    }

    public static void onCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) event.accept(TRANSPORTATION_STONE_ITEM);
    }
}
