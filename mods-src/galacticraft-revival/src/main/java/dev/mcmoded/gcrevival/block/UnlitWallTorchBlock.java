/*
 * Unlit torch of Galacticraft dungeons (Legacy BlockUnlitTorch / GC5 GCBlocks.UNLIT_WALL_TORCH): a wall torch without
 * flame particles or light. Texture from Galacticraft Legacy (micdoodle8, radfast, Team Galacticraft), see CREDITS.md.
 */
package dev.mcmoded.gcrevival.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

public class UnlitWallTorchBlock extends WallTorchBlock {
    public UnlitWallTorchBlock(Properties properties) {
        super(properties, ParticleTypes.SMOKE);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    }
}
