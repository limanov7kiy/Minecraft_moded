package dev.mcmoded.undeadrising.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/// Таран: моб разгоняется к цели, которая не выше него, и сбивает её с ног. У Forsaken со щитом.
/// Оригинал: Fish's Undead Rising (Fish), `entities/ai/EntityChargeAttackGoal`.
public class EntityChargeAttackGoal extends Goal {
    protected final Mob charger;
    protected int attackStep;
    private int attackTime;

    public EntityChargeAttackGoal(Mob mob) {
        this.charger = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.charger.getTarget();
        return target != null && target.isAlive() && target.getY() <= this.charger.getY() && this.charger.distanceToSqr(target) > 6.0;
    }

    @Override
    public void start() {
        this.attackStep = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.attackTime--;
        LivingEntity target = this.charger.getTarget();
        if (target != null) {
            double dist = this.charger.distanceTo(target);
            if (this.charger.getBoundingBox().intersects(target.getBoundingBox())) {
                if (this.attackTime <= 0) {
                    this.attackTime = 30;
                    this.charger.doHurtTarget(target);
                    target.knockback(1.0F, Mth.sin(this.charger.getYRot() * Mth.DEG_TO_RAD), -Mth.cos(this.charger.getYRot() * Mth.DEG_TO_RAD));
                }
            } else if (dist < this.getFollowDistance() * this.getFollowDistance()) {
                double v = 4.0;
                double dx = v * (target.getX() - this.charger.getX()) / dist;
                double dy = v * (target.getY() - this.charger.getY()) / dist;
                double dz = v * (target.getZ() - this.charger.getZ()) / dist;
                if (this.attackTime <= 0) {
                    this.attackStep++;
                    if (this.attackStep > 20) {
                        this.charger.getMoveControl().setWantedPosition(this.charger.getX() + dx, this.charger.getY() + dy, this.charger.getZ() + dz, 2.0);
                    }
                    // В оригинале ветка «attackStep > 100 → передышка 60 тиков» недостижима (стоит после > 20) — сохранено как есть.
                }
                this.charger.getLookControl().setLookAt(target, 100.0F, 100.0F);
            } else {
                this.charger.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
            }
        }
    }

    private double getFollowDistance() {
        AttributeInstance attr = this.charger.getAttribute(Attributes.FOLLOW_RANGE);
        return attr == null ? 16.0 : attr.getValue();
    }

    public boolean isCharging() {
        return this.attackStep > 20;
    }
}
