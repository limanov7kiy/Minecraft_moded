package dev.mcmoded.wizardrygolems.block;

import dev.mcmoded.wizardrygolems.WizardryGolems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/// Блоки Wizardry Golems. Permafrost оригинала не переносится — используется блок Permafrost из Redux.
public final class GolemBlocks {
    private GolemBlocks() {}

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, WizardryGolems.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, WizardryGolems.MOD_ID);

    public static final RegistryObject<Block> LIVING_SNOW = BLOCKS.register("living_snow",
            () -> new LivingSnow(BlockBehaviour.Properties.copy(Blocks.SNOW)));
    public static final DeferredRegister<net.minecraft.world.level.levelgen.feature.Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, WizardryGolems.MOD_ID);
    public static final RegistryObject<LivingSnowFeature> LIVING_SNOW_FEATURE = FEATURES.register("living_snow",
            () -> new LivingSnowFeature(net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration.CODEC));
    @SuppressWarnings("DataFlowIssue")
    public static final RegistryObject<BlockEntityType<LivingSnow.Tile>> LIVING_SNOW_TILE = TILES.register("living_snow",
            () -> BlockEntityType.Builder.of(LivingSnow.Tile::new, LIVING_SNOW.get()).build(null));
}
