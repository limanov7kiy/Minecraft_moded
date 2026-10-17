package dev.mcmoded.undeadrising.entity.projectile;

import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurItems;
import dev.mcmoded.undeadrising.registry.FurParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/// Желудочный сок — плевок Osvermis: урон силой стрелка, яд и «Разложение» IV (время — от сложности).
/// Оригинал: Fish's Undead Rising (Fish), `entities/projectiles/AcidJetEntity`.
public class AcidJetEntity extends ThrowableItemProjectile {
    private float damage = 6.0F;

    public AcidJetEntity(EntityType<? extends AcidJetEntity> type, Level level) {
        super(type, level);
    }

    public AcidJetEntity(LivingEntity shooter, Level level) {
        super(FurEntities.ACIDJET.get(), shooter, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            for (int i = 0; i < 8 + this.random.nextInt(8); i++) {
                this.level().addParticle(this.particleType(), this.getX() + this.random.nextDouble() * 0.5,
                        this.getY() + 0.5 + this.random.nextDouble() * 0.5, this.getZ() + this.random.nextDouble() * 0.5, 0.0, 0.0, 0.0);
            }
        }
    }

    protected ParticleOptions particleType() {
        return FurParticles.GASTRO_ACID.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        if (!this.level().isClientSide && this.getOwner() instanceof LivingEntity owner && target instanceof LivingEntity living && target != owner) {
            this.damage = (float) owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if (target.hurt(this.damageSources().thrown(this, owner), this.damage)) {
                this.onDamaged(living);
                this.doEnchantDamageEffects(owner, target);
            }
            this.discard();
        }
    }

    protected void onDamaged(LivingEntity target) {
        float localDifficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 40 * (int) localDifficulty, 0));
        target.addEffect(new MobEffectInstance(FurEffects.CORRODED.get(), 40 * (int) localDifficulty, 3));
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) this.discard();
    }

    @Override
    protected Item getDefaultItem() {
        return FurItems.POISONSPORE.get();
    }
}
