package dev.mcmoded.undeadrising.entity;

import dev.mcmoded.undeadrising.entity.projectile.AcidJetEntity;
import dev.mcmoded.undeadrising.entity.projectile.FlameJetEntity;
import dev.mcmoded.undeadrising.registry.FurSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/// Osvermis (костяной червь) — роет землю: на ходу закапывается (неуязвим, пока глубоко), вылезает, когда стоит,
/// и плюётся желудочным соком (яд + «Разложение» IV); после плевка в игрока на 5 секунд отступает. Вариант 1
/// (Долина песка душ) — огнестойкий, плюётся огнём. Пока не вылез полностью — не стреляет.
/// Оригинал: Fish's Undead Rising (Fish), `entities/BoneWormEntity`.
public class BoneWormEntity extends Monster implements RangedAttackMob {
    /// BoneWorm_Health / BoneWorm_Attack по умолчанию.
    public static final double HEALTH = 32.0, ATTACK = 6.0;
    private static final EntityDataAccessor<Boolean> UNDERGROUND = SynchedEntityData.defineId(BoneWormEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(BoneWormEntity.class, EntityDataSerializers.INT);

    /// Глубина «под землёй» (0 — снаружи, до 3.5 — закопан); считается на обеих сторонах по движению.
    public double LocationFix;
    public int[] attackTimer = {0, 0};
    public int[] diggingTimer = {0, 0};
    private RangedAttackGoal rangeAtk;
    private AvoidEntityGoal<Player> avoidPlayer;
    private int avoidCooldown;

    public BoneWormEntity(EntityType<? extends BoneWormEntity> type, Level level) {
        super(type, level);
        this.LocationFix = 0.0;
    }

    @Override
    protected void registerGoals() {
        this.rangeAtk = new RangedAttackGoal(this, 1.0, 40, 60, 12.0F);
        this.avoidPlayer = new AvoidEntityGoal<>(this, Player.class, 10.0F, 1.0, 1.2);
        this.goalSelector.addGoal(0, this.rangeAtk);
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.applyEntityAI();
    }

    protected void applyEntityAI() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(UNDERGROUND, false);
        this.entityData.define(SKIN_TYPE, 0);
    }

    private boolean isWalking() {
        return Math.abs(this.getX() - this.xOld) > 0.05 || Math.abs(this.getY() - this.yOld) > 0.05 || Math.abs(this.getZ() - this.zOld) > 0.05;
    }

    private boolean isDigging() {
        return this.diggingTimer[0] != 0 || this.diggingTimer[1] != 0;
    }

    private void digParticles(BlockState state, double ySpeed) {
        for (int i = 0; i < 4; i++) {
            float w = this.getBbWidth();
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                    this.getX() + this.random.nextFloat() * w * 2.0F - w,
                    this.getY() + this.random.nextFloat() * w * 2.0F - w,
                    this.getZ() + this.random.nextFloat() * w * 2.0F - w,
                    this.random.nextGaussian() * 0.02, this.random.nextGaussian() * ySpeed, this.random.nextGaussian() * 0.02);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        BlockState state = this.level().getBlockState(this.blockPosition().below());
        if (this.isWalking()) {
            if (state.isSolid() && this.level().isClientSide) this.digParticles(state, 0.02);
            if (this.tickCount % 10 == 0) this.playSound(SoundEvents.GRAVEL_STEP, 1.0F, 0.5F);
        }

        if (!this.level().isClientSide) {
            if (this.LocationFix > 0.0 && !this.isUnderGround() && !this.isDigging()) {
                this.clearFire();
                this.diggingTimer[0] = 30;
                this.setUnderGround(true);
                this.level().broadcastEntityEvent(this, (byte) 6);
                this.playSound(FurSounds.BONEWORM_BURROW.get(), 0.25F, 1.0F);
            } else if (this.LocationFix <= 1.5 && this.isUnderGround() && !this.isDigging()) {
                this.diggingTimer[1] = 20;
                this.setUnderGround(false);
                this.level().broadcastEntityEvent(this, (byte) 7);
                this.playSound(FurSounds.BONEWORM_BURROW.get(), 0.25F, 1.0F);
            }
        }

        if (this.isWalking() && state.isSolid()) {
            if (this.LocationFix <= 3.5) this.LocationFix += 0.125;
            if (this.level().isClientSide) this.digParticles(state, 0.1);
        } else if (this.LocationFix > 0.0 && state.isSolid()) {
            this.LocationFix -= 0.125;
            if (this.level().isClientSide) this.digParticles(state, 0.1);
        }

        if (!this.level().isClientSide) {
            if (this.avoidCooldown == 0) {
                this.goalSelector.addGoal(0, this.rangeAtk);
                this.goalSelector.removeGoal(this.avoidPlayer);
                this.avoidCooldown = -1;
            }
            if (this.avoidCooldown > 0) this.avoidCooldown--;
        }
    }

    @Override
    public void tick() {
        for (int i = 0; i < 2; i++) {
            if (this.attackTimer[i] > 0) this.attackTimer[i]--;
            if (this.diggingTimer[i] > 0) this.diggingTimer[i]--;
        }

        LivingEntity target = this.getTarget();
        if (target != null && this.getSensing().hasLineOfSight(target) && this.getAttackTimer(0) == 7 && this.deathTime <= 0 && this.LocationFix == 0.0
                && !this.level().isClientSide) {
            this.spit(target);
        }

        // SunScreen_Mode = false: горит на солнце.
        if (this.isSunBurnTick()) this.setSecondsOnFire(8);
        if (this.isWalking()) this.clearFire();
        super.tick();
    }

    public double getLocationFix() {
        return this.LocationFix;
    }

    /// Глубоко закопанный неуязвим.
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return this.LocationFix <= 3.0 && super.hurt(source, amount);
    }

    private void spit(LivingEntity target) {
        ThrowableItemProjectile jet;
        SoundEvent sound;
        if (this.getSkin() == 1) {
            jet = new FlameJetEntity(this, this.level());
            sound = SoundEvents.BLAZE_SHOOT;
        } else {
            jet = new AcidJetEntity(this, this.level());
            sound = FurSounds.BONEWORM_ATTACK.get();
        }
        double ty = target.getY() + target.getEyeHeight() - 1.1F;
        double dx = target.getX() - this.getX();
        double dy = ty - jet.getY();
        double dz = target.getZ() - this.getZ();
        float f = Mth.sqrt((float) (dx * dx + dz * dz)) * 0.2F;
        jet.shoot(dx, dy + f, dz, 1.6F, 1.0F);
        this.playSound(sound, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(jet);
        if (target instanceof Player) this.setRunning(100);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        this.attackTimer[0] = 15;
        this.level().broadcastEntityEvent(this, (byte) 4);
    }

    /// Отступить от игрока на {@code cooldown} тиков (без стрельбы).
    public void setRunning(int cooldown) {
        this.avoidPlayer = new AvoidEntityGoal<>(this, Player.class, 10.0F, 1.0, 1.2);
        this.goalSelector.removeGoal(this.rangeAtk);
        this.goalSelector.addGoal(1, this.avoidPlayer);
        this.avoidCooldown = cooldown;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean flag = super.doHurtTarget(target);
        if (flag) {
            this.attackTimer[1] = 16;
            this.level().broadcastEntityEvent(this, (byte) 5);
        }
        return flag;
    }

    /// Заряженный крипер выбивает череп скелета.
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (source.getEntity() instanceof Creeper creeper && creeper.canDropMobsSkull()) {
            creeper.increaseDroppedSkulls();
            this.spawnAtLocation(Items.SKELETON_SKULL);
        }
    }

    public int getSkin() {
        return this.entityData.get(SKIN_TYPE);
    }

    public void setSkin(int skin) {
        this.entityData.set(SKIN_TYPE, skin);
    }

    public int getAttackTimer(int i) {
        return this.attackTimer[i];
    }

    public boolean isUnderGround() {
        return this.entityData.get(UNDERGROUND);
    }

    public void setUnderGround(boolean underground) {
        this.entityData.set(UNDERGROUND, underground);
    }

    @Override
    public boolean fireImmune() {
        return this.getSkin() == 1 || super.fireImmune();
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case 4 -> this.attackTimer[0] = 15;
            case 5 -> this.attackTimer[1] = 16;
            case 6 -> this.diggingTimer[0] = 30;
            case 7 -> this.diggingTimer[1] = 20;
            default -> super.handleEntityEvent(id);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK);
        this.setHealth(this.getMaxHealth());
        if (level.getBiome(this.blockPosition()).is(Biomes.SOUL_SAND_VALLEY)) this.setSkin(1);
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return size.height * 0.8F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return FurSounds.BONEWORM_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return FurSounds.BONEWORM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return FurSounds.BONEWORM_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
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
}
