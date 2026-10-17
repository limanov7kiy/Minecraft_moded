package dev.mcmoded.undeadrising.entity;

import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.entity.ai.EntityAIDestroyCrops;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurItems;
import dev.mcmoded.undeadrising.registry.FurSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
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
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

/// Вета-исполин — гигантский новозеландский кузнечик саванн. Дикая объедает посевы и кусты; приручается чумной
/// свининой (1 к 3) или «Зелёным беконом с яйцами» (сразу), прирученная бродит и собирает спелый урожай с пересадкой.
/// Не ранится о кактусы и ягодные кусты. Больная вета (5% при рождении или после больного хлеба) — нежить: удар
/// накладывает «Загрязнение», с пшеницы роняет больную пшеницу, приручить её нельзя.
/// Оригинал: Fish's Undead Rising (Fish), `entities/tameable/WetaEntity`.
public class WetaEntity extends FURTameableEntity implements IAggressive {
    private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(WetaEntity.class, EntityDataSerializers.INT);

    /// Weta_Health, Weta_Attack по умолчанию.
    public static final double HEALTH = 12.0, ATTACK = 1.0;

    private int attackTimer = 0;
    private EntityAIDestroyCrops destroyCrops;

    public WetaEntity(EntityType<? extends WetaEntity> type, Level level) {
        super(type, level);
        this.xpReward = 1;
    }

    @Override
    protected void registerGoals() {
        this.destroyCrops = new EntityAIDestroyCrops(this, 1.1, this.isTame());
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, Ingredient.of(FurItems.PLAGUED_PORKCHOP.get()), false));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, Ingredient.of(FurItems.GREEN_BACON_AND_EGGS.get()), false));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, this.destroyCrops);
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKIN_TYPE, this.random.nextFloat() < 0.05F ? 2 : 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK);
    }

    @Override
    public void aiStep() {
        if (this.attackTimer > 0) this.attackTimer--;
        super.aiStep();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.CACTUS) || source.is(DamageTypes.SWEET_BERRY_BUSH)) return false;
        return super.hurt(source, amount);
    }

    @Override
    protected float getJumpPower() {
        return 1.5F * super.getJumpPower();
    }

    @Override
    public void doSitCommand(@Nullable Player player) {
        this.goalSelector.removeGoal(this.destroyCrops);
        super.doSitCommand(player);
    }

    @Override
    public void doWanderCommand(@Nullable Player player) {
        this.goalSelector.removeGoal(this.destroyCrops);
        this.destroyCrops = new EntityAIDestroyCrops(this, 1.1, this.isTame());
        this.goalSelector.addGoal(5, this.destroyCrops);
        super.doWanderCommand(player);
    }

    /// При приручении: вета игрока, которая не бродит, урожай не трогает.
    @Override
    protected void reassessTameGoals() {
        super.reassessTameGoals();
        if (this.isTame() && !this.isWandering() && this.destroyCrops != null && this.getOwner() instanceof Player) {
            this.goalSelector.removeGoal(this.destroyCrops);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(FurItems.DISEASED_BREAD.get()) && this.getSkin() == 0) {
            if (!player.getAbilities().instabuild) stack.shrink(1);
            this.setSkin(2);
            this.playSound(SoundEvents.AMBIENT_CAVE.value(), 1.0F, 1.0F);
            for (int i = 0; i < 16; i++) {
                this.level().addParticle(ParticleTypes.ENTITY_EFFECT,
                        this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth(),
                        this.getY() + this.random.nextFloat() * this.getBbHeight(),
                        this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth(),
                        this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02);
            }
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!super.doHurtTarget(target)) return false;
        this.attackTimer = 5;
        this.level().broadcastEntityEvent(this, (byte) 4);
        if (target instanceof LivingEntity living && this.getSkin() == 2) {
            living.addEffect(new MobEffectInstance(FurEffects.SOILED.get(), 160, 1));
        }
        return true;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(FurItems.PLAGUED_PORKCHOP.get()) || stack.is(FurItems.GREEN_BACON_AND_EGGS.get());
    }

    @Override
    protected boolean canTameCondition() {
        return !this.isTame() && !this.isBaby() && this.getSkin() != 2;
    }

    @Override
    protected int tameRate(ItemStack stack) {
        if (stack.is(FurItems.PLAGUED_PORKCHOP.get())) return 3;
        return stack.is(FurItems.GREEN_BACON_AND_EGGS.get()) ? 1 : super.tameRate(stack);
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
    public int getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    public void setAttackTimer(int ticks) {
        this.attackTimer = ticks;
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
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attackTimer = 5;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 0.1F;
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
    protected SoundEvent getAmbientSound() {
        return FurSounds.WETA_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return FurSounds.WETA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return FurSounds.WETA_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    @Nullable
    @Override
    public WetaEntity getBreedOffspring(ServerLevel level, AgeableMob other) {
        WetaEntity child = FurEntities.WETA.get().create(level);
        if (child == null) return null;
        UUID owner = this.getOwnerUUID();
        if (owner != null) {
            child.setOwnerUUID(owner);
            child.setTame(true);
            child.setHealth(this.getMaxHealth());
        }
        return child;
    }

    @Override
    public MobType getMobType() {
        return this.getSkin() == 2 ? MobType.UNDEAD : MobType.ARTHROPOD;
    }
}
