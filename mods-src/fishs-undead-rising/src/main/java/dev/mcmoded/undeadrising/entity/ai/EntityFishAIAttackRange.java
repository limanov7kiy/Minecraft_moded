package dev.mcmoded.undeadrising.entity.ai;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;

import java.util.EnumSet;

/// Стрельба очередью снарядов-«фаерболов» по цели: замах (30 тиков, событие 5 — анимация), затем `times` выстрелов
/// с интервалом 6 тиков и перезарядка `attackCD` секунд. Вплотную к цели цель не работает (дерётся ближний бой/ничего).
/// Оригинал: Fish's Undead Rising (Fish), `entities/ai/EntityFishAIAttackRange`.
public class EntityFishAIAttackRange<T extends AbstractHurtingProjectile> extends Goal {
    private final PathfinderMob shooter;
    private final EntityType<T> shot;
    private final SoundEvent sound;
    private final int shotTimes;
    private final int attackCD;
    private final double curve;
    private final double range;
    private final double xOffset, yOffset, zOffset;
    private int attackStep;
    private int attackTime;

    public EntityFishAIAttackRange(PathfinderMob shooter, EntityType<T> shot, int times, int attackCD, double x, double y, double z) {
        this(shooter, shot, SoundEvents.BLAZE_SHOOT, times, attackCD, 0.0, shooter.getAttributeValue(Attributes.FOLLOW_RANGE), x, y, z);
    }

    public EntityFishAIAttackRange(PathfinderMob shooter, EntityType<T> shot, SoundEvent sound, int times, int attackCD, double curve, double range,
                                   double x, double y, double z) {
        this.shooter = shooter;
        this.shot = shot;
        this.sound = sound;
        this.shotTimes = times;
        this.attackCD = attackCD;
        this.curve = curve;
        this.range = range;
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.shooter.getTarget();
        if (target == null || !target.isAlive()) return false;
        float w = this.shooter.getBbWidth() + target.getBbWidth();
        return this.shooter.distanceToSqr(target) >= w * w;
    }

    @Override
    public void start() {
        this.attackStep = 0;
        this.shooter.setAggressive(true);
    }

    @Override
    public void stop() {
        this.shooter.setAggressive(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.attackTime--;
        LivingEntity target = this.shooter.getTarget();
        if (target == null) return;
        double d0 = this.shooter.distanceToSqr(target);
        if (d0 < this.range * this.range) {
            double d1 = target.getX() - this.shooter.getX();
            double d2 = target.getBoundingBox().minY + target.getBbHeight() / 2.0F - (this.shooter.getY() + this.shooter.getBbHeight() / 2.0F);
            double d3 = target.getZ() - this.shooter.getZ();
            if (this.attackTime <= 0) {
                this.attackStep++;
                if (this.attackStep == 1) {
                    this.attackTime = 30;
                    if (!this.shooter.isBaby()) this.shooter.level().broadcastEntityEvent(this.shooter, (byte) 5);
                } else if (this.attackStep <= this.shotTimes + 1) {
                    this.attackTime = 6;
                } else {
                    this.attackTime = this.attackCD * 20;
                    this.attackStep = 0;
                }

                if (this.attackStep > 1) {
                    float f = Mth.sqrt(Mth.sqrt((float) d0)) * 0.1F;
                    this.shooter.playSound(this.sound, 1.0F, 1.0F);
                    double t1 = d1 + this.shooter.getRandom().nextGaussian() * f;
                    double t2 = d2 + this.curve * d0 / 64.0;
                    double t3 = d3 + this.shooter.getRandom().nextGaussian() * f;
                    double t4 = Math.sqrt(t1 * t1 + t2 * t2 + t3 * t3);
                    T projectile = this.shot.create(this.shooter.level());
                    if (projectile != null) {
                        projectile.setOwner(this.shooter);
                        projectile.moveTo(this.shooter.getX() + d1 / t4 * this.xOffset, this.shooter.getY() + this.shooter.getBbHeight() / 2.0F + this.yOffset,
                                this.shooter.getZ() + d3 / t4 * this.zOffset, this.shooter.getYRot(), this.shooter.getXRot());
                        projectile.xPower = t1 / t4 * 0.75;
                        projectile.yPower = t2 / t4 * 0.75;
                        projectile.zPower = t3 / t4 * 0.75;
                        this.shooter.level().addFreshEntity(projectile);
                    }
                }
            }
            this.shooter.getLookControl().setLookAt(target, 30.0F, 30.0F);
        } else {
            this.shooter.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
        }
    }
}
