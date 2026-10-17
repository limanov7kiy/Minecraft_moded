package dev.mcmoded.undeadrising.entity;

import dev.mcmoded.undeadrising.entity.ai.EntityFishAIAttackRange;
import dev.mcmoded.undeadrising.entity.projectile.WarSmallFireballEntity;
import dev.mcmoded.undeadrising.item.SalamanderBucketItem;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurItems;
import dev.mcmoded.undeadrising.registry.FurSounds;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

/// Саламандра — огненная ящерица Незера. Рождается нимфой (стадия 0): труслива, бегает от игроков, плюётся одиночными
/// огненными шарами; только нимфу можно приручить (рог беса — 1 к 3, Kung Pao Chicken — сразу) и посадить в ведро
/// лавой. Растёт вместе с возрастом (−24000…0): стадии 1–2 — крупнее и сильнее, взрослая (3) — пушка на спине, очередь
/// из 8 шаров, седло (ездовая владельца; «особая способность» — веер из 8 огненных шаров, перезарядка 4 с), всадник
/// получает огнестойкость. Сидящая прирученная раздувает соседнюю печь (горит дольше). 64 пыли ифрита возвращают
/// обычный окрас, 64 эктоплазмы — окрас душ (так рождаются в Долине песка душ).
/// Оригинал: Fish's Undead Rising (Fish), `entities/tameable/SalamanderEntity`.
public class SalamanderEntity extends FURTameableEntity implements IAggressive, Saddleable {
    private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(SalamanderEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TIMER = SynchedEntityData.defineId(SalamanderEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GROWING_STAGE = SynchedEntityData.defineId(SalamanderEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Byte> DATA_FLAGS = SynchedEntityData.defineId(SalamanderEntity.class, EntityDataSerializers.BYTE);

    /// Значения конфига оригинала по умолчанию: Salamander_Health, Salamander_Attack, Salamander_Defender.
    public static final double HEALTH = 60.0, ATTACK = 4.0;
    public static final boolean DEFENDER = false;

    private EntityFishAIAttackRange<WarSmallFireballEntity> rangeAtk;
    @Nullable private AvoidEntityGoal<Player> avoidEntity;
    /// Перезарядка залпа всадника (тики); тикает и на клиенте, и на сервере.
    public int barrageCD;
    @Nullable public BlockPos savedFurnacePos = null;

    public SalamanderEntity(EntityType<? extends SalamanderEntity> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.xpReward = 20;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKIN_TYPE, 0);
        this.entityData.define(ATTACK_TIMER, 0);
        this.entityData.define(GROWING_STAGE, -1);
        this.entityData.define(DATA_FLAGS, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.rangeAtk = this.isNymph() ? nymphAttack() : adultAttack();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, this.rangeAtk);
        this.goalSelector.addGoal(5, new LookAtFurnaceGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        if (DEFENDER) {
            this.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal(this));
            this.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal(this));
        }
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, Player.class, false,
                p -> !p.isPassenger() || !(p.getVehicle() instanceof SalamanderEntity)));
    }

    private EntityFishAIAttackRange<WarSmallFireballEntity> adultAttack() {
        return new EntityFishAIAttackRange<>(this, FurEntities.WARSMALLFIREBALL.get(), 8, 5, 2.5, 1.0, 2.5);
    }

    private EntityFishAIAttackRange<WarSmallFireballEntity> nymphAttack() {
        return new EntityFishAIAttackRange<>(this, FurEntities.WARSMALLFIREBALL.get(), 1, 5, 1.0, 0.1, 1.0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected Goal followGoal() {
        return new FollowOwnerGoal(this, 1.5, 6.0F, 2.0F, false);
    }

    /// Светится сама (в оригинале `getBrightness` = 1).
    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 4;
    }

    protected ItemStack getFishBucket() {
        ItemStack stack = new ItemStack(FurItems.SALAMANDER_BUCKET.get());
        CompoundTag tag = new CompoundTag();
        this.addAdditionalSaveData(tag);
        stack.getOrCreateTag().put(SalamanderBucketItem.DATA, tag);
        if (this.hasCustomName()) stack.setHoverName(this.getCustomName());
        return stack;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean food = this.isFood(stack);
        if (this.isOwnedBy(player) && stack.is(Items.BLAZE_POWDER) && stack.getCount() >= 64 && this.isAlive() && this.getSkin() == 1) {
            if (!player.getAbilities().instabuild) stack.shrink(64);
            this.setSkin(0);
            this.transformEffects();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (this.isTame() && stack.is(FurItems.ECTOPLASM.get()) && stack.getCount() >= 64 && this.isAlive() && this.getSkin() == 0) {
            if (!player.getAbilities().instabuild) stack.shrink(64);
            this.setSkin(1);
            this.transformEffects();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (this.isTame() && this.isNymph() && stack.is(Items.LAVA_BUCKET) && this.isAlive()) {
            this.playSound(SoundEvents.BUCKET_FILL_FISH, 1.0F, 1.0F);
            stack.shrink(1);
            ItemStack bucket = this.getFishBucket();
            if (!this.level().isClientSide) CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, bucket);
            if (stack.isEmpty()) {
                player.setItemInHand(hand, bucket);
            } else if (!player.getInventory().add(bucket)) {
                player.drop(bucket, false);
            }
            this.discard();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            if (!food && this.isOwnedBy(player) && this.isSaddled() && !this.isVehicle()) {
                if (stack.is(Items.SHEARS)) {
                    this.setSaddled(false);
                    this.spawnAtLocation(Items.SADDLE, 1);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
                if (!player.isSecondaryUseActive() && !player.isPassenger()) {
                    if (!this.level().isClientSide) player.startRiding(this);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
            }
            return super.mobInteract(player, hand);
        }
    }

    private void transformEffects() {
        // Звук и частицы превращения — как в оригинале (AMBIENT_CAVE + частицы эффекта).
        this.playSound(SoundEvents.AMBIENT_CAVE.value(), 1.0F, 1.0F);
        for (int i = 0; i < 16; i++) {
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT,
                    this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth(),
                    this.getY() + this.random.nextFloat() * this.getBbHeight(),
                    this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth(),
                    this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02);
        }
    }

    @Override
    protected boolean canTameCondition() {
        return this.isBaby() && super.canTameCondition();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(FurItems.IMP_HORN.get()) || stack.is(FurItems.KUNG_PAO_CHICKEN.get());
    }

    @Override
    protected int tameRate(ItemStack stack) {
        if (stack.is(FurItems.IMP_HORN.get())) return 3;
        return stack.is(FurItems.KUNG_PAO_CHICKEN.get()) ? 1 : super.tameRate(stack);
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight() * 1.3;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (passenger instanceof Mob mob) {
            this.yBodyRot = mob.yBodyRot;
            this.setYRot(passenger.getYRot());
        }
    }

    /// Управляет только владелец-игрок.
    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player player && this.isTame() && player.getUUID().equals(this.getOwnerUUID())) return player;
        }
        return null;
    }

    public boolean isRidingPlayer(Player player) {
        return this.getControllingPassenger() instanceof Player rider && rider.getUUID().equals(player.getUUID());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getAttackTimer() > 0) this.setAttackTimer(this.getAttackTimer() - 1);
        if (this.barrageCD > 0) this.barrageCD--;
        if (this.level().isClientSide) return;

        if (this.isTame() && this.random.nextInt(this.isInLava() ? 45 : 900) == 0 && this.deathTime == 0) this.heal(1.0F);

        LivingEntity rider = this.getControllingPassenger();
        if (this.isVehicle() && rider != null && this.tickCount % 40 == 0 && !rider.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            rider.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0));
        }

        int age = this.getAge();
        int stage = age < -16000 ? 0 : age < -8000 ? 1 : age < 0 ? 2 : 3;
        if (this.getGrowingStage() != stage) this.setGrowingStage(stage);

        if (this.savedFurnacePos != null && (this.blockPosition().distSqr(this.savedFurnacePos) > this.searchRange() * this.searchRange()
                || !(this.level().getBlockState(this.savedFurnacePos).getBlock() instanceof AbstractFurnaceBlock) || !this.isInSittingPose())) {
            this.savedFurnacePos = null;
            this.setBoostingFurnace(false);
        }

        if (this.tickCount % 80 == 0 && this.isAlive() && this.isTame() && this.isInSittingPose()) {
            if (this.savedFurnacePos == null) {
                search:
                for (int i = this.searchRange(); i > -this.searchRange(); i--) {
                    for (int j = this.searchRange(); j > -this.searchRange(); j--) {
                        for (int k = this.searchRange(); k > -this.searchRange(); k--) {
                            BlockPos pos = this.blockPosition().offset(i, j, k);
                            if (this.level().getBlockState(pos).getBlock() instanceof AbstractFurnaceBlock) {
                                this.savedFurnacePos = pos;
                                break search;
                            }
                        }
                    }
                }
            }
            if (this.savedFurnacePos != null) {
                BlockState state = this.level().getBlockState(this.savedFurnacePos);
                this.setBoostingFurnace(true);
                if (this.level().getBlockEntity(this.savedFurnacePos) instanceof AbstractFurnaceBlockEntity furnace && !furnace.getItem(0).isEmpty()) {
                    CompoundTag tag = furnace.saveWithoutMetadata();
                    if (tag.contains("BurnTime") && tag.getShort("BurnTime") <= 100) {
                        tag.putShort("BurnTime", (short) 200);
                        furnace.load(tag);
                        furnace.setChanged();
                        if (state.hasProperty(BlockStateProperties.LIT)) {
                            this.level().setBlock(this.savedFurnacePos, state.setValue(BlockStateProperties.LIT, true), 3);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> dev.mcmoded.undeadrising.client.MountControls.tickSalamander(this));
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        if (distance > 1.0F) this.playSound(SoundEvents.HORSE_LAND, 0.4F, 1.0F);
        int i = this.calculateFallDamage(distance, multiplier);
        if (i <= 0) return false;
        this.hurt(source, i);
        if (this.isVehicle()) {
            for (Entity entity : this.getIndirectPassengers()) entity.hurt(source, i);
        }
        this.playBlockFallSound();
        return true;
    }

    @Override
    protected int calculateFallDamage(float distance, float multiplier) {
        return Mth.ceil((distance * 0.5F - 3.0F) * multiplier);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (GROWING_STAGE.equals(key)) this.refreshDimensions();
        super.onSyncedDataUpdated(key);
    }

    public int getGrowingStage() {
        return this.entityData.get(GROWING_STAGE);
    }

    /// Стадия роста: здоровье, скорость, сила, опыт и стрельба (нимфа — одиночные выстрелы и бегство от игроков).
    public void setGrowingStage(int stage) {
        this.entityData.set(GROWING_STAGE, stage);
        this.refreshDimensions();
        switch (stage) {
            case 0 -> {
                this.xpReward = 5;
                if (this.avoidEntity == null) {
                    this.avoidEntity = new AvoidEntityGoal<>(this, Player.class, 4.0F, 0.8, 1.6);
                    if (!this.isTame()) this.goalSelector.addGoal(3, this.avoidEntity);
                }
                this.swapAttack(this.nymphAttack());
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH * 0.25);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.28);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK * 0.5);
                if (this.getHealth() > this.getMaxHealth()) this.setHealth(this.getMaxHealth());
                if (this.isSaddled()) {
                    this.setSaddled(false);
                    this.spawnAtLocation(Items.SADDLE, 1);
                }
            }
            case 1 -> {
                this.xpReward = 10;
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH * 0.4);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK * 0.65);
                this.heal(this.getHealth() * 0.6F);
            }
            case 2 -> {
                this.xpReward = 15;
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH * 0.6);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK * 0.75);
                this.heal(this.getHealth() * 0.5F);
            }
            default -> {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH);
                this.heal(this.getHealth() * 2.0F / 3.0F);
                this.xpReward = 20;
                if (this.avoidEntity != null) this.goalSelector.removeGoal(this.avoidEntity);
                this.swapAttack(this.adultAttack());
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.23);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK);
            }
        }
    }

    private void swapAttack(EntityFishAIAttackRange<WarSmallFireballEntity> goal) {
        if (this.rangeAtk != null) this.goalSelector.removeGoal(this.rangeAtk);
        this.rangeAtk = goal;
        this.goalSelector.addGoal(4, this.rangeAtk);
    }

    public boolean isNymph() {
        return this.getGrowingStage() == 0;
    }

    @Override
    public void setTame(boolean tamed) {
        if (tamed && this.avoidEntity != null) this.goalSelector.removeGoal(this.avoidEntity);
        super.setTame(tamed);
    }

    @Nullable
    @Override
    public SalamanderEntity getBreedOffspring(ServerLevel level, AgeableMob other) {
        SalamanderEntity child = FurEntities.SALAMANDER.get().create(level);
        if (child == null) return null;
        UUID owner = this.getOwnerUUID();
        if (owner != null) {
            child.setOwnerUUID(owner);
            child.setTame(true);
        }
        child.setHealth(child.getMaxHealth());
        child.setSkin(this.random.nextBoolean() || !(other instanceof SalamanderEntity s) ? this.getSkin() : s.getSkin());
        return child;
    }

    @Override
    public boolean isSaddled() {
        return (this.entityData.get(DATA_FLAGS) & 1) != 0;
    }

    public void setSaddled(boolean saddled) {
        byte b = this.entityData.get(DATA_FLAGS);
        this.entityData.set(DATA_FLAGS, (byte) (saddled ? b | 1 : b & ~1));
    }

    public boolean isBoostingFurnace() {
        return (this.entityData.get(DATA_FLAGS) & 2) != 0;
    }

    public void setBoostingFurnace(boolean boosting) {
        byte b = this.entityData.get(DATA_FLAGS);
        this.entityData.set(DATA_FLAGS, (byte) (boosting ? b | 2 : b & ~2));
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (!this.isAlive()) return;
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player controller && this.isSaddled()) {
            this.setYRot(controller.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(controller.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
            this.setMaxUpStep(1.0F);
            float strafe = controller.xxa * 0.5F;
            float forward = controller.zza;
            if (this.isControlledByLocalInstance()) {
                if (this.isInLava()) {
                    Vec3 m = this.getDeltaMovement();
                    this.setDeltaMovement(m.x * 1.5, m.y + 0.02F, m.z * 1.5);
                }
                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(strafe, travelVector.y, forward));
            } else {
                this.setDeltaMovement(Vec3.ZERO);
            }
            this.calculateEntityAnimation(false);
        } else {
            super.travel(travelVector);
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
        float childChance = switch (level.getDifficulty()) {
            case PEACEFUL -> 0.85F;
            case EASY -> 0.8F;
            case NORMAL -> 0.5F;
            case HARD -> 0.15F;
        };
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data, tag);
        this.setBaby(level.getRandom().nextFloat() <= childChance);
        return result;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return this.isBaby() ? 0.2F : 0.8F;
    }

    @Override
    public int getAttackTimer() {
        return this.entityData.get(ATTACK_TIMER);
    }

    @Override
    public void setAttackTimer(int ticks) {
        this.entityData.set(ATTACK_TIMER, ticks);
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
    protected SoundEvent getAmbientSound() {
        return FurSounds.SALAMANDER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return FurSounds.SALAMANDER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return FurSounds.SALAMANDER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby() && this.isTame();
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) this.spawnAtLocation(Items.SADDLE);
    }

    @Override
    public void equipSaddle(@Nullable SoundSource source) {
        this.setSaddled(true);
        if (source != null) this.level().playSound(null, this, SoundEvents.HORSE_SADDLE, source, 0.5F, 1.0F);
    }

    /// Молодь роняет `salamanderlesser`.
    @Override
    protected ResourceLocation getDefaultLootTable() {
        return this.isBaby() ? new ResourceLocation("mod_lavacow", "entities/salamanderlesser") : super.getDefaultLootTable();
    }

    @Override
    protected boolean shouldDropLoot() {
        return true;
    }

    @Override
    public boolean shouldDropExperience() {
        return true;
    }

    /// Размер хитбокса по стадии (0,3 / 0,5 / 0,75 / 1).
    @Override
    public float getScale() {
        return switch (this.getGrowingStage()) {
            case 0 -> 0.3F;
            case 1 -> 0.5F;
            case 2 -> 0.75F;
            default -> 1.0F;
        };
    }

    private int searchRange() {
        return 2 + this.getGrowingStage();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 5) {
            this.setAttackTimer(80);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSaddled(tag.getBoolean("Saddled"));
        this.setGrowingStage(tag.getInt("GrowingStage"));
        this.setSkin(tag.getInt("Variant"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Saddled", this.isSaddled());
        tag.putInt("GrowingStage", this.getGrowingStage());
        tag.putInt("Variant", this.getSkin());
    }

    /// Сидящая прирученная саламандра смотрит на печь и дышит на неё огнём.
    public static class LookAtFurnaceGoal extends Goal {
        private final SalamanderEntity mob;

        public LookAtFurnaceGoal(SalamanderEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.mob.isAlive() && this.mob.isTame() && this.mob.isInSittingPose() && this.mob.savedFurnacePos != null && !this.mob.isAggressive();
        }

        @Override
        public void tick() {
            BlockPos furnace = this.mob.savedFurnacePos;
            if (furnace == null) return;
            this.mob.getLookControl().setLookAt(furnace.getX() + 0.5, furnace.getY(), furnace.getZ() + 0.5);
            if (this.mob.level() instanceof ServerLevel level && this.mob.tickCount % 20 == 0) {
                BlockPos at = this.mob.blockPosition();
                Vec3 v = new Vec3(this.mob.getLookControl().getWantedX() - at.getX(), this.mob.getLookControl().getWantedY() - at.getY(),
                        this.mob.getLookControl().getWantedZ() - at.getZ()).normalize();
                double k = this.mob.getGrowingStage() + 1.0;
                level.sendParticles(this.mob.getSkin() == 0 ? ParticleTypes.FLAME : ParticleTypes.SOUL_FIRE_FLAME,
                        at.getX() + 0.5 + v.x * k * 0.5, at.getY() + this.mob.getBbHeight() * 0.2F, at.getZ() + 0.5 + v.z * k * 0.5,
                        15, 0.2 + v.x * k * 0.05, 0.2, 0.2 + v.z * k * 0.05, 0.01);
            }
        }
    }
}
