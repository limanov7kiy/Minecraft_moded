package dev.mcmoded.necromancersdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/// Временный светящийся гриб (оригинал — `BlockTemporaryGlowShroom`; модель и текстура — гриб Fish's Undead Rising).
/// Свет 15, без коллизии и добычи; исчезает по запланированному тику (время жизни задаёт заклинание).
public class TempGlowshroom extends Block {
    private static final VoxelShape SHAPE = Block.box(4.8, 0, 4.8, 11.2, 6.4, 11.2);

    public TempGlowshroom(Properties props) {
        super(props);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.removeBlock(pos, false);
    }
}
