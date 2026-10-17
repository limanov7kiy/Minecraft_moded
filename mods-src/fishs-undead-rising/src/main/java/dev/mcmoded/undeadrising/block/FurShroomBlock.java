package dev.mcmoded.undeadrising.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.joml.Vector3f;

/// Грибы-растения (оригинал: Fish's Undead Rising (Fish), `block/FURShroomBlock`): два вида по `age` (выбирается
/// случайно при установке), растут на мицелии/подзоле или в темноте (< 13), светящийся — при любом свете и искрит.
/// Расползаются как обычные грибы. Огромный светящийся гриб (костная мука) не перенесён — костная мука не действует.
public class FurShroomBlock extends MushroomBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
    private static final DustParticleOptions SPARK = new DustParticleOptions(new Vector3f(0.0F, 0.98F, 0.93F), 1.0F);
    private final boolean glowing;

    public FurShroomBlock(Properties props, boolean glowing) {
        super(props, TreeFeatures.HUGE_BROWN_MUSHROOM);
        this.glowing = glowing;
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(AGE, ctx.getLevel().random.nextInt(2));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState soil = level.getBlockState(below);
        if (soil.is(BlockTags.MUSHROOM_GROW_BLOCK)) return true;
        return (glowing || level.getRawBrightness(pos, 0) < 13) && soil.canSustainPlant(level, below, Direction.UP, this);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean client) {
        return false;
    }

    @Override
    public boolean growMushroom(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        return false;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!glowing || random.nextInt(16) != 0) return;
        for (Direction d : Direction.values()) {
            BlockPos n = pos.relative(d);
            if (level.getBlockState(n).isSolidRender(level, n)) continue;
            Direction.Axis a = d.getAxis();
            double x = a == Direction.Axis.X ? 0.5 + 0.5625 * d.getStepX() : random.nextFloat();
            double y = a == Direction.Axis.Y ? 0.5 + 0.5625 * d.getStepY() : random.nextFloat();
            double z = a == Direction.Axis.Z ? 0.5 + 0.5625 * d.getStepZ() : random.nextFloat();
            level.addParticle(SPARK, pos.getX() + x, pos.getY() + y, pos.getZ() + z, 0, 0, 0);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}
