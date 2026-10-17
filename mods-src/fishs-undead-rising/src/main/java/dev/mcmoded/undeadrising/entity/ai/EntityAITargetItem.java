package dev.mcmoded.undeadrising.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

/// Идти к ближайшему лежащему предмету, который моб хочет подобрать (сидящий — не идёт).
/// Оригинал: Fish's Undead Rising (Fish), `entities/ai/EntityAITargetItem`.
public class EntityAITargetItem extends TargetGoal {
    private final int targetChance;
    private ItemEntity targetEntity;

    public EntityAITargetItem(PathfinderMob creature, boolean checkSight) {
        super(creature, checkSight, false);
        this.targetChance = 10;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Mob mob = this.mob;
        boolean sitting = mob instanceof TamableAnimal t && t.isInSittingPose();
        boolean riding = mob.isPassenger() || mob.isVehicle() && mob.getControllingPassenger() != null;
        if (riding && sitting) return false;
        if (!mob.getMainHandItem().isEmpty() && this.targetChance > 0 && mob.getRandom().nextInt(this.targetChance) != 0) return false;
        List<ItemEntity> list = mob.level().getEntitiesOfClass(ItemEntity.class, this.getTargetableArea(this.getFollowDistance()),
                item -> !item.getItem().isEmpty() && mob.wantsToPickUp(item.getItem()));
        if (list.isEmpty()) return false;
        list.sort(Comparator.comparingDouble(mob::distanceToSqr));
        this.targetEntity = list.get(0);
        return true;
    }

    protected AABB getTargetableArea(double distance) {
        return this.mob.getBoundingBox().inflate(distance, 4.0, distance);
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
        super.start();
    }

    @Override
    public void tick() {
        if (this.targetEntity != null && this.targetEntity.isAlive() && this.mob.getMainHandItem().isEmpty()) {
            this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
        } else {
            this.stop();
            this.mob.getNavigation().stop();
        }
        if (this.targetEntity != null && this.mob.hasLineOfSight(this.targetEntity) && this.mob.onGround()) {
            this.mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.0);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetEntity != null && this.targetEntity.isAlive() && !this.mob.getNavigation().isDone();
    }
}
