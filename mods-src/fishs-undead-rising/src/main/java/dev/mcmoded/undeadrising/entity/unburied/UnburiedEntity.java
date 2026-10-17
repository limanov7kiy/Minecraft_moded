package dev.mcmoded.undeadrising.entity.unburied;

import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.entity.FURTameableEntity;
import dev.mcmoded.undeadrising.entity.IAggressive;
import dev.mcmoded.undeadrising.registry.FurSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/// Непогребённый — зомби FUR, база семейства (Mummy, Mycosis, Frigid). Сам по себе в мире не появляется: его
/// поднимают надгробия, Гробовщик и оружие призыва (в оригинале; здесь — пока только яйцом). Призванный несёт уровни
/// чар оружия призыва (NBT `sharpness`, `fire_aspect`…, `LifeTicks`), их эффекты — в {@link #doHurtTarget} и
/// `event/FurEntityEvents` (доп. урон, вампиризм владельцу). Горит на солнце (шлем спасает, изнашиваясь).
/// Оригинал: Fish's Undead Rising (Fish), `entities/tameable/unburied/UnburiedEntity`.
public class UnburiedEntity extends FURTameableEntity implements IAggressive {
    /// Значения по умолчанию конфига оригинала (FURConfig): Unburied_Health / Unburied_Attack.
    public static final double HEALTH = 20.0, ATTACK = 3.0;

    private int attackTimer;
    protected int spellTicks;
    private int limitedLifeTicks = -1;
    private int fireAspect;
    private int sharpness;
    private int knockback;
    protected int baneOfArthropods;
    protected int smite;
    private int lifesteal;
    private int poisonous;
    protected int corrosive;
    private int unbreaking;
    private boolean isSmoking;

    public UnburiedEntity(EntityType<? extends UnburiedEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.5, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.5, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.applyEntityAI();
    }

    protected void applyEntityAI() {
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new CopyOwnerTargetGoal(this));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, e -> !(this.getOwner() instanceof Player)));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, 10, true, false, e -> !(this.getOwner() instanceof Player)));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, IronGolem.class, 10, true, false, e -> !(this.getOwner() instanceof Player)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK)
                .add(Attributes.ARMOR, 2.0);
    }

    @Override
    public void setLimitedLife(int ticks) {
        if (ticks != 0) this.limitedLifeTicks = ticks;
    }

    /// Доп. урон от чар призыва (применяется в `FurEntityEvents#onHurt`).
    public float getBonusDamage(LivingEntity target) {
        return 0.5F * this.sharpness + 0.5F
                + (target.getMobType() == MobType.ARTHROPOD ? this.baneOfArthropods * 2.5F : 0.0F)
                + (target.getMobType() == MobType.UNDEAD ? this.smite * 2.5F : 0.0F);
    }

    public int getLifestealLevel() {
        return this.lifesteal;
    }

    public boolean isSpellcasting() {
        return this.spellTicks > 0;
    }

    public int getSpellTicks() {
        return this.spellTicks;
    }

    @Override
    protected boolean isCommandable() {
        return false;
    }

    @Override
    public double getMyRidingOffset() {
        return this.isBaby() ? 0.0 : -0.25;
    }

    @Override
    public void aiStep() {
        BlockPos below = this.blockPosition().below();
        BlockState state = this.level().getBlockState(below);
        if (this.isSpellcasting()) {
            // Вылезает из-под земли (spellTicks выставляет событие 32): крошки блока под ногами.
            if (state.isSolidRender(this.level(), below) && this.level().isClientSide) {
                for (int i = 0; i < 4; i++) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(below),
                            this.getX() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(),
                            this.getY() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(),
                            this.getZ() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(),
                            this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02);
                }
            }
            if (this.tickCount % 10 == 0) this.playSound(SoundEvents.GRAVEL_BREAK, 1.0F, 0.5F);
            this.setDeltaMovement(Vec3.ZERO);
        }

        if (this.isSmoking) {
            for (int j = 0; j < 8; j++) {
                float a = this.random.nextFloat() * (float) (Math.PI * 2);
                float r = this.getBbHeight() * 0.4F + this.random.nextFloat() * 0.5F;
                this.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX() + Mth.sin(a) * r,
                        this.getBoundingBox().minY + r, this.getZ() + Mth.cos(a) * r, 0.0, 0.05, 0.0);
            }
        }
        super.aiStep();
    }

    @Override
    public void tick() {
        if (this.attackTimer > 0) this.attackTimer--;
        if (this.spellTicks > 0) this.spellTicks--;

        if (this.limitedLifeTicks >= 0 && this.tickCount >= this.limitedLifeTicks) {
            // Время призыва вышло: «%s обращается в прах» (Show_Expire_Death_Messege = true в оригинале).
            if (!this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)
                    && this.getOwner() instanceof Player owner) {
                owner.sendSystemMessage(Component.translatable("death.mod_lavacow.timeup", this.getDisplayName()));
            }
            this.level().broadcastEntityEvent(this, (byte) 11);
            this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getVoicePitch());
            this.discard();
        }

        if (this.isAlive()) {
            // SunScreen_Mode = false (оригинал): дикие горят на солнце, шлем защищает и изнашивается.
            boolean burn = !(this.getOwner() instanceof Player) && this.isSunBurnTick();
            if (burn) {
                ItemStack helmet = this.getItemBySlot(EquipmentSlot.HEAD);
                if (!helmet.isEmpty()) {
                    if (helmet.isDamageableItem()) {
                        helmet.setDamageValue(helmet.getDamageValue() + this.random.nextInt(2));
                        if (helmet.getDamageValue() >= helmet.getMaxDamage()) {
                            this.broadcastBreakEvent(EquipmentSlot.HEAD);
                            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                    burn = false;
                }
                if (burn) this.setSecondsOnFire(8);
            }
        }
        super.tick();
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        if (random.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(random.nextInt(3) == 0 ? Items.IRON_SWORD : Items.IRON_SHOVEL));
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!super.doHurtTarget(target)) return false;
        this.attackTimer = 5;
        this.level().broadcastEntityEvent(this, (byte) 4);
        if (target instanceof LivingEntity living) {
            if (this.fireAspect > 0) target.setSecondsOnFire(this.fireAspect * 4 - 1);
            if (this.knockback > 0) {
                float dist = this.distanceTo(target);
                living.knockback(this.knockback * 0.5F, (this.getX() - target.getX()) / dist, (this.getZ() - target.getZ()) / dist);
            }
            if (this.baneOfArthropods > 0 && living.getMobType() == MobType.ARTHROPOD) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 + this.random.nextInt(10 * this.baneOfArthropods), 3));
            }
            if (this.poisonous > 0) living.addEffect(new MobEffectInstance(MobEffects.POISON, 160, this.poisonous - 1));
            if (this.corrosive > 0) living.addEffect(new MobEffectInstance(FurEffects.CORRODED.get(), 80, this.corrosive - 1));
        }
        return true;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        data = super.finalizeSpawn(level, difficulty, reason, data, tag);
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return data;
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
    public void handleEntityEvent(byte id) {
        if (id == 32) this.spellTicks = 20;
        else if (id == 4) this.attackTimer = 5;
        else if (id == 11) this.isSmoking = true;
        else super.handleEntityEvent(id);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return FurSounds.UNBURIED_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return FurSounds.UNBURIED_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return FurSounds.UNBURIED_DEATH.get();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setLimitedLife(tag.getInt("LifeTicks"));
        this.fireAspect = tag.getInt("fire_aspect");
        this.sharpness = tag.getInt("sharpness");
        this.knockback = tag.getInt("knockback");
        this.baneOfArthropods = tag.getInt("bane_of_arthropods");
        this.smite = tag.getInt("smite"); // в оригинале по ошибке читалось "fire_aspect"
        this.lifesteal = tag.getInt("lifesteal");
        this.poisonous = tag.getInt("poisonous");
        this.corrosive = tag.getInt("corrosive");
        this.unbreaking = tag.getInt("unbreaking");
        // Оригинал всегда ставил здоровье 20 + 2·unbreaking — сбрасывая 30 у Frigid и 24 у Mummy при загрузке мира;
        // здесь только для призванных с «Прочностью».
        if (this.unbreaking > 0) this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0 + this.unbreaking * 2.0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("LifeTicks", this.limitedLifeTicks - this.tickCount);
        tag.putInt("fire_aspect", this.fireAspect);
        tag.putInt("sharpness", this.sharpness);
        tag.putInt("knockback", this.knockback);
        tag.putInt("bane_of_arthropods", this.baneOfArthropods);
        tag.putInt("smite", this.smite);
        tag.putInt("lifesteal", this.lifesteal);
        tag.putInt("poisonous", this.poisonous);
        tag.putInt("corrosive", this.corrosive);
        tag.putInt("unbreaking", this.unbreaking);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean shouldDropExperience() {
        return !this.isTame() || !(this.getOwner() instanceof Player);
    }

    /// Атакует цель хозяина-моба (Гробовщик и т. п.). В оригинале хозяин запоминался при создании цели (всегда null);
    /// здесь берётся при проверке.
    class CopyOwnerTargetGoal extends TargetGoal {
        private final TargetingConditions copyOwnerTargeting = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

        CopyOwnerTargetGoal(PathfinderMob mob) {
            super(mob, false);
        }

        @Override
        public boolean canUse() {
            return UnburiedEntity.this.getOwner() instanceof Mob owner && owner.isAggressive()
                    && this.canAttack(owner.getTarget(), this.copyOwnerTargeting);
        }

        @Override
        public void start() {
            if (UnburiedEntity.this.getOwner() instanceof Mob owner) UnburiedEntity.this.setTarget(owner.getTarget());
            super.start();
        }
    }
}
