package dev.mcmoded.undeadrising.entity.flying;

import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.entity.ParasiteEntity;
import dev.mcmoded.undeadrising.entity.ai.FlyerFollowOwnerGoal;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurSounds;
import dev.mcmoded.undeadrising.registry.FurTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/// Веспа — гигантская оса джунглей. Жалит ядом (и с шансом 1/5 — заражением паразитами), охотится на игроков и
/// зомби (по зомби бьёт вдвое сильнее, от них получает вдвое меньше). Вылупляется из кокона, который плетёт личинка
/// (паразит джунглей); прирученную (из личинки хозяина) можно оседлать: особая способность — удар жалом вперёд.
/// Вариант 1 — из пчелы, в которую ударила молния. Детёныш превращается обратно в личинку.
/// Оригинал: Fish's Undead Rising (Fish), `entities/flying/VespaEntity`.
public class VespaEntity extends RidableFlyingMobEntity {
    /// Vespa_Health / Vespa_Attack по умолчанию.
    public static final double HEALTH = 20.0, ATTACK = 5.0;
    private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(VespaEntity.class, EntityDataSerializers.INT);

    public VespaEntity(EntityType<? extends VespaEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, Player.class, false,
                p -> !p.isPassenger() || !(p.getVehicle() instanceof VespaEntity)).setUnseenMemoryTicks(160));
        this.targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false,
                e -> e.attackable() && e.getType().is(FurTags.VESPA_TARGETS)).setUnseenMemoryTicks(160));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.1)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK)
                .add(Attributes.FLYING_SPEED, 0.1);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKIN_TYPE, 0);
    }

    /// Прирученная — вдвое больше здоровья. В оригинале здоровье пересчитывалось при каждом вызове (в т. ч. при
    /// загрузке мира); здесь — только при смене состояния.
    @Override
    public void setTame(boolean tamed) {
        boolean was = this.isTame();
        super.setTame(tamed);
        if (was == tamed) return;
        if (tamed) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH * 2.0);
            this.setHealth(this.getHealth() * 2.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH);
            this.setHealth(this.getHealth() * 0.5F);
        }
    }

    @Override
    protected Goal wanderGoal() {
        return new FlyingMobEntity.AIRandomFly(this);
    }

    @Override
    protected Goal followGoal() {
        return new FlyerFollowOwnerGoal(this, 1.0, 10.0F, 4.0F, false, 24.0);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    /// Невосприимчива к яду и заражению (если другой мод не разрешит событием).
    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() != FurEffects.INFESTED.get() && effect.getEffect() != MobEffects.POISON) return super.canBeAffected(effect);
        MobEffectEvent.Applicable event = new MobEffectEvent.Applicable(this, effect);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() == Event.Result.ALLOW;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return size.height * 0.45F;
    }

    @Override
    public int abilityCooldown() {
        return 30;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.onGround() && this.tickCount % 20 == 0) {
            this.playSound(this.getFlyingSound(), 1.0F, 1.0F);
        }

        // Особая способность ездовой веспы: удар жалом по всем перед собой.
        if (!this.level().isClientSide && this.getAttackTimer() == 6 && this.abilityCooldown > 0 && this.deathTime <= 0) {
            Vec3 look = this.getLookAngle().normalize();
            double x = this.getX() + 1.75 * look.x;
            double y = this.getY();
            double z = this.getZ() + 1.75 * look.z;
            for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, new AABB(x, y, z, x, y, z).inflate(1.0))) {
                if (!this.equals(e) && !this.isAlliedTo(e)) this.doHurtTarget(e);
            }
            this.playSound(SoundEvents.PUFFER_FISH_STING, 0.6F, 2.0F);
        }
    }

    /// Детёныш веспы превращается в личинку (паразит джунглей); седло падает.
    @Override
    protected void ageBoundaryReached() {
        if (!this.isBaby()) return;
        if (this.isSaddled()) {
            this.setSaddled(false);
            this.spawnAtLocation(Items.SADDLE, 1);
        }
        if (!this.level().isClientSide) {
            ParasiteEntity larva = FurEntities.PARASITE.get().create(this.level());
            if (larva != null) {
                larva.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                larva.setSkin(2);
                if (this.isTame() && this.getOwner() instanceof Player owner) {
                    larva.tame(owner);
                    larva.setCustomName(this.getCustomName());
                }
                this.level().addFreshEntity(larva);
            }
        }
        this.discard();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean prey = target instanceof LivingEntity && target.getType().is(FurTags.VESPA_TARGETS);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(prey ? ATTACK * 2.0 : ATTACK);
        if (!super.doHurtTarget(target)) return false;
        if (target instanceof LivingEntity living) {
            float localDifficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 120 * (int) localDifficulty, 0));
            if (this.getRandom().nextInt(5) == 0) {
                living.addEffect(new MobEffectInstance(FurEffects.INFESTED.get(), 120 * (int) localDifficulty, 0));
            }
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof LivingEntity attacker && attacker.getType().is(FurTags.VESPA_TARGETS)) {
            return super.hurt(source, amount * 0.5F);
        }
        return super.hurt(source, amount);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK);
        this.setHealth(this.getMaxHealth());
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
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
    protected double VehicleSpeedMod() {
        return !this.isInLava() && !this.isInWater() ? 2.0 : 0.2;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSkin(tag.getInt("Variant"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getSkin());
    }

    @Override
    public int getAmbientSoundInterval() {
        return 1000;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return FurSounds.VESPA_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return FurSounds.VESPA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return FurSounds.VESPA_DEATH.get();
    }

    protected SoundEvent getFlyingSound() {
        return FurSounds.VESPA_FLYING.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (this.getLandTimer() > 10) this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    protected float getSoundVolume() {
        return 0.7F;
    }
}
