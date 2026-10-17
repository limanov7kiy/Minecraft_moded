package dev.mcmoded.undeadrising.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

/// Следование за владельцем для летунов: летит на 2 блока выше владельца, телепортируется к нему издалека
/// (дистанция задаётся). Оригинал: Fish's Undead Rising (Fish), `entities/ai/FlyerFollowOwnerGoal`.
public class FlyerFollowOwnerGoal extends Goal {
    private final TamableAnimal tamable;
    private LivingEntity owner;
    private final LevelReader level;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;
    private final boolean canFly;
    private final double teleportDistance;

    public FlyerFollowOwnerGoal(TamableAnimal tamable, double speed, float startDistance, float stopDistance, boolean canFly, double teleportDistance) {
        this.tamable = tamable;
        this.level = tamable.level();
        this.speedModifier = speed;
        this.navigation = tamable.getNavigation();
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.canFly = canFly;
        this.teleportDistance = teleportDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(tamable.getNavigation() instanceof GroundPathNavigation) && !(tamable.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = this.tamable.getOwner();
        if (owner == null || owner.isSpectator() || this.tamable.isOrderedToSit()) return false;
        if (this.tamable.distanceToSqr(owner) < this.startDistance * this.startDistance) return false;
        this.owner = owner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone() || this.tamable.isOrderedToSit()) return false;
        return this.tamable.distanceToSqr(this.owner) > this.stopDistance * this.stopDistance;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        this.tamable.getLookControl().setLookAt(this.owner, 10.0F, this.tamable.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                if (this.tamable.distanceToSqr(this.owner) >= this.teleportDistance * this.teleportDistance) {
                    this.teleportToOwner();
                } else {
                    BlockPos pos = this.owner.blockPosition();
                    this.navigation.moveTo(pos.getX(), pos.above(2).getY(), pos.getZ(), this.speedModifier);
                }
            }
        }
    }

    private void teleportToOwner() {
        BlockPos pos = this.owner.blockPosition();
        for (int i = 0; i < 10; i++) {
            int dx = this.randomIntInclusive(-3, 3);
            int dy = this.randomIntInclusive(-1, 1);
            int dz = this.randomIntInclusive(-3, 3);
            if (this.maybeTeleportTo(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz)) return;
        }
    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        if (Math.abs(x - this.owner.getX()) < 2.0 && Math.abs(z - this.owner.getZ()) < 2.0) return false;
        if (!this.canTeleportTo(new BlockPos(x, y, z))) return false;
        this.tamable.moveTo(x + 0.5, y + 4.0, z + 0.5, this.tamable.getYRot(), this.tamable.getXRot());
        this.navigation.stop();
        return true;
    }

    private boolean canTeleportTo(BlockPos pos) {
        BlockPathTypes type = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());
        if (type != BlockPathTypes.WALKABLE) return false;
        BlockState below = this.level.getBlockState(pos.below());
        if (!this.canFly && below.getBlock() instanceof LeavesBlock) return false;
        BlockPos offset = pos.subtract(this.tamable.blockPosition());
        return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(offset));
    }

    private int randomIntInclusive(int min, int max) {
        return this.tamable.getRandom().nextInt(max - min + 1) + min;
    }
}
