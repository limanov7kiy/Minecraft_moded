package dev.mcmoded.undeadrising.entity.projectile;

import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

/// «Кольцо смерти» — летящий череп из посоха Forsaken: урон, Иссушение II и Смертное касание III. Живёт 30 тиков.
/// Оригинал: Fish's Undead Rising (Fish), `entities/projectiles/DeathCoilEntity`.
public class DeathCoilEntity extends AbstractHurtingProjectile {
    private float damage = 4.0F;
    protected int knockbackStrength;

    public DeathCoilEntity(EntityType<? extends DeathCoilEntity> type, Level level) {
        super(type, level);
    }

    public DeathCoilEntity(Level level, LivingEntity shooter, double ax, double ay, double az) {
        super(FurEntities.DEATHCOIL.get(), shooter, ax, ay, az, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= 30) this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity shooter = this.getOwner();
        Entity target = result.getEntity();
        if (!this.level().isClientSide && shooter instanceof LivingEntity livingShooter && target instanceof LivingEntity living && target != shooter) {
            if (!(shooter instanceof Player)) this.setDamage((float) livingShooter.getAttributeValue(Attributes.ATTACK_DAMAGE));
            if (target.hurt(this.damageSources().mobProjectile(this, livingShooter), this.getDamage())) {
                float localDifficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 200 * (int) localDifficulty, 1));
                living.addEffect(new MobEffectInstance(FurEffects.FRAGILE.get(), 200 * (int) localDifficulty, 2));
                if (this.isOnFire()) target.setSecondsOnFire(5);
                if (this.knockbackStrength > 0) {
                    Vec3 push = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(this.knockbackStrength * 0.6);
                    if (push.lengthSqr() > 0.0) target.push(push.x, 0.1, push.z);
                }
                this.doEnchantDamageEffects(livingShooter, target);
            }
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) this.discard();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setKnockbackStrength(int strength) {
        this.knockbackStrength = strength;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return this.isOnFire() ? ParticleTypes.FLAME : FurParticles.WITHER_FLAME.get();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
