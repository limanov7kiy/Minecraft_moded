package dev.mcmoded.undeadrising.entity.ai;

import dev.mcmoded.undeadrising.entity.WetaEntity;
import dev.mcmoded.undeadrising.registry.FurItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;

/// Объедание посевов (при разрешённом mobGriefing): дикий моб идёт к ближайшему растению на грядке или кусту в
/// радиусе 16 и через 1,5 с ломает его; прирученный (`harvest`) — только спелые посевы и пересаживает их. Больная
/// вета с шансом 15% роняет с пшеницы больную пшеницу.
/// Оригинал: Fish's Undead Rising (Fish), `entities/ai/EntityAIDestroyCrops`.
public class EntityAIDestroyCrops extends Goal {
    /// Weta_Harvest_Diseased_Wheat по умолчанию.
    public static final double DISEASED_WHEAT_CHANCE = 0.15;

    protected final PathfinderMob entity;
    public int destroyTicks;
    private final boolean isHarvest;
    private final double movementSpeed;
    protected int runDelay;
    private int timeoutCounter;
    private int maxStayTicks;
    protected BlockPos destinationBlock = BlockPos.ZERO;
    private boolean isAboveDestination;
    private final int searchLength = 16;

    public EntityAIDestroyCrops(PathfinderMob creature, double speed, boolean harvest) {
        this.entity = creature;
        this.isHarvest = harvest;
        this.movementSpeed = speed;
    }

    @Override
    public boolean canUse() {
        if (!ForgeEventFactory.getMobGriefingEvent(this.entity.level(), this.entity)) return false;
        if (this.runDelay > 0) {
            this.runDelay--;
            return false;
        }
        this.runDelay = 40 + this.entity.getRandom().nextInt(40);
        return this.searchForDestination();
    }

    @Override
    public boolean canContinueToUse() {
        return this.timeoutCounter >= -this.maxStayTicks && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.entity.level(), this.destinationBlock);
    }

    @Override
    public void start() {
        this.entity.getNavigation().moveTo(this.destinationBlock.getX() + 0.5, this.destinationBlock.getY() + 1, this.destinationBlock.getZ() + 0.5, this.movementSpeed);
        this.timeoutCounter = 0;
        this.maxStayTicks = this.entity.getRandom().nextInt(this.entity.getRandom().nextInt(1200) + 1200) + 1200;
    }

    @Override
    public void tick() {
        BlockPos above = this.destinationBlock.above();
        if (this.entity.distanceToSqr(above.getX(), above.getY(), above.getZ()) > 1.0) {
            this.isAboveDestination = false;
            this.timeoutCounter++;
            if (this.timeoutCounter % 40 == 0) {
                this.entity.getNavigation().moveTo(this.destinationBlock.getX() + 0.5, this.destinationBlock.getY() + 1, this.destinationBlock.getZ() + 0.5, this.movementSpeed);
            }
        } else {
            this.isAboveDestination = true;
            this.timeoutCounter--;
        }

        this.entity.getLookControl().setLookAt(this.destinationBlock.getX() + 0.5, this.destinationBlock.getY() + 1, this.destinationBlock.getZ() + 0.5,
                10.0F, this.entity.getMaxHeadXRot());
        if (this.isAboveDestination) {
            Level level = this.entity.level();
            this.destroyTicks++;
            if (this.destroyTicks > 30) {
                Block block = level.getBlockState(above).getBlock();
                if (!level.isClientSide && this.entity instanceof WetaEntity weta && weta.getSkin() == 2 && block == Blocks.WHEAT
                        && this.entity.getRandom().nextDouble() < DISEASED_WHEAT_CHANCE) {
                    this.entity.spawnAtLocation(new ItemStack(FurItems.DISEASED_WHEAT.get()), 0.0F);
                }
                level.destroyBlock(above, true);
                if (this.isHarvest) level.setBlock(above, block.defaultBlockState(), 3);
                level.destroyBlockProgress(this.entity.getId(), above, -1);
                // В оригинале счётчик не сбрасывался (следующие растения ломались мгновенно) — здесь каждое по 1,5 с.
                this.destroyTicks = 0;
            } else if (this.destroyTicks % 10 == 0) {
                level.destroyBlockProgress(this.entity.getId(), above, this.destroyTicks / 10);
            }
        }
    }

    private boolean searchForDestination() {
        BlockPos origin = this.entity.blockPosition();
        for (int k = 0; k <= 1; k = k > 0 ? -k : 1 - k) {
            for (int l = 0; l < this.searchLength; l++) {
                for (int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    for (int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                        BlockPos pos = origin.offset(i1, k - 1, j1);
                        if (this.entity.isWithinRestriction(pos) && this.shouldMoveTo(this.entity.level(), pos)) {
                            this.destinationBlock = pos;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected boolean shouldMoveTo(Level level, BlockPos pos) {
        BlockPos up = pos.above();
        BlockState above = level.getBlockState(up);
        if (level.getBlockState(pos).is(Blocks.FARMLAND) && above.getBlock() instanceof CropBlock crop && (!this.isHarvest || crop.isMaxAge(above))) {
            return true;
        }
        return !this.isHarvest && above.getBlock() instanceof BushBlock;
    }
}
