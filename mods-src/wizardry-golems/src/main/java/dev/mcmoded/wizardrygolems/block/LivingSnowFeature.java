package dev.mcmoded.wizardrygolems.block;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/// Генерация Living Snow (оригинал — `WorldGenLivingSnow`): в пятне 8×8 вокруг точки до 20 попыток заменить
/// верхний слой снега на «живой» (без хозяина). Где — задаёт biome modifier (снежные биомы).
public class LivingSnowFeature extends Feature<NoneFeatureConfiguration> {
    public LivingSnowFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        BlockPos origin = ctx.origin();
        var rnd = ctx.random();
        boolean placed = false;
        for (int i = 0; i < 20; i++) {
            int x = origin.getX() + rnd.nextInt(8) - rnd.nextInt(8), z = origin.getZ() + rnd.nextInt(8) - rnd.nextInt(8);
            BlockPos top = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z).below();
            BlockState state = level.getBlockState(top);
            if (!state.is(Blocks.SNOW)) continue;
            level.setBlock(top, GolemBlocks.LIVING_SNOW.get().defaultBlockState()
                    .setValue(SnowLayerBlock.LAYERS, state.getValue(SnowLayerBlock.LAYERS)), 2);
            placed = true;
        }
        return placed;
    }
}
