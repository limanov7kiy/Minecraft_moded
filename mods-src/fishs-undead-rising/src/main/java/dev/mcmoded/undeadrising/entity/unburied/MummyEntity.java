package dev.mcmoded.undeadrising.entity.unburied;

import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.registry.FurParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/// Мумия: обитает в пустынных храмах, окружена роем саранчи, удар разъедает (эффект Corroded).
/// Оригинал: Fish's Undead Rising (Fish), `entities/tameable/unburied/MummyEntity`.
public class MummyEntity extends UnburiedEntity {
    /// Mummy_Health / Mummy_Attack по умолчанию.
    public static final double HEALTH = 24.0, ATTACK = 4.0;
    private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(MummyEntity.class, EntityDataSerializers.INT);

    public MummyEntity(EntityType<? extends MummyEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKIN_TYPE, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new FleeSunGoal(this, 1.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.21F)
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK)
                .add(Attributes.ARMOR, 4.0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 2 == 0) {
            this.level().addParticle(FurParticles.LOCUST_SWARM.get(),
                    this.getX() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(),
                    this.getY() + (0.4F + this.random.nextFloat() * 0.6F) * this.getBbHeight(),
                    this.getZ() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(),
                    0.0, 0.0, 0.0);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!super.doHurtTarget(target)) return false;
        if (target instanceof LivingEntity living) {
            if (this.isTame()) {
                living.addEffect(this.corrosive > 0
                        ? new MobEffectInstance(FurEffects.CORRODED.get(), 80, Math.max(1, this.corrosive - 1))
                        : new MobEffectInstance(FurEffects.CORRODED.get(), 40, 1));
            } else {
                float localDifficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
                living.addEffect(new MobEffectInstance(FurEffects.CORRODED.get(), 40 * (int) localDifficulty, 1));
            }
        }
        return true;
    }

    @Override
    public int getSkin() {
        return this.entityData.get(SKIN_TYPE);
    }

    @Override
    public void setSkin(int skin) {
        this.entityData.set(SKIN_TYPE, skin);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getSkin());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSkin(tag.getInt("Variant"));
    }
}
