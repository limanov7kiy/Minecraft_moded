package dev.mcmoded.undeadrising.entity;

import dev.mcmoded.undeadrising.UndeadRising;
import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurItems;
import dev.mcmoded.undeadrising.registry.FurSounds;
import dev.mcmoded.undeadrising.registry.FurTags;
import dev.mcmoded.undeadrising.util.FurSpawnUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/// Паразит — личинка, ползающая по стенам и потолку. Цепляется на игроков и зомби (заражение паразитами), живёт
/// {@link #LIFESPAN} секунд и погибает; паразит джунглей (вариант 2) вместо этого с шансом {@link #EVOLVE_RATE}%
/// (прирученный — всегда) плетёт кокон веспы, если рядом есть игрок. Вылезает из умерших заражённых существ и
/// (пустынный) из разбитого песка в пустыне. Варианты: 0 — обычный, 1 — пустынный, 2 — джунглей (ядовитый),
/// 3 — Beelzebub (куколка Beelzebub в порт не входит). Крадучись пустой рукой можно подобрать (сырой паразит).
/// Оригинал: Fish's Undead Rising (Fish), `entities/ParasiteEntity`.
public class ParasiteEntity extends Spider {
    /// Parasite_Health / Parasite_Attack / Parasite_Lifespan (с) / pEvolveRate_Vespa (%) по умолчанию.
    public static final double HEALTH = 6.0, ATTACK = 1.0;
    public static final int LIFESPAN = 16, EVOLVE_RATE = 20;

    private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(ParasiteEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Direction> ATTACHED_BLK = SynchedEntityData.defineId(ParasiteEntity.class, EntityDataSerializers.DIRECTION);
    protected static final EntityDataAccessor<Byte> PARASITE_FLAGS = SynchedEntityData.defineId(ParasiteEntity.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(ParasiteEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final Direction[] DIRECTIONS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public int lifespawn = LIFESPAN * 20;

    public ParasiteEntity(EntityType<? extends ParasiteEntity> type, Level level) {
        super(type, level);
        this.xpReward = 1;
    }

    /// Что даёт подобранный паразит (по варианту).
    private static Item drop(int skin) {
        return switch (skin) {
            case 1 -> FurItems.PARASITE_DESERT.get();
            case 2 -> FurItems.PARASITE_JUNGLE.get();
            case 3 -> FurItems.PARASITE_COOKED.get();
            default -> FurItems.PARASITE_COMMON.get();
        };
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new AttackGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.applyEntityAI();
    }

    protected void applyEntityAI() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, true, p -> !p.isPassenger() && !this.isTame()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, true, true,
                e -> e.attackable() && e.getType().is(FurTags.PARASITE_TARGETS) && !this.isTame()));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 8.0)
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKIN_TYPE, this.random.nextInt(4));
        this.entityData.define(ATTACHED_BLK, Direction.DOWN);
        this.entityData.define(PARASITE_FLAGS, (byte) 0);
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return !this.isTame();
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return !this.isTame() || !(this.getOwner() instanceof Player);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return this.isTame() && this.getOwner() instanceof Player || super.requiresCustomPersistence();
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return !this.isLeashed() && this.isTame();
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == FurEffects.INFESTED.get()) {
            MobEffectEvent.Applicable event = new MobEffectEvent.Applicable(this, effect);
            MinecraftForge.EVENT_BUS.post(event);
            return event.getResult() == Event.Result.ALLOW;
        }
        return super.canBeAffected(effect);
    }

    /// Без ванильных «паучьих» бонусов (наездник-скелет, эффекты сложности) — как в оригинале.
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK);
        this.setHealth(this.getMaxHealth());
        return data;
    }

    private void dieOfAge() {
        this.hurt(this.damageSources().mobAttack(this), this.getMaxHealth());
    }

    @Override
    public void tick() {
        if (this.lifespawn > 0) {
            if (this.getVehicle() == null) this.lifespawn--;
        } else if (!this.isSummoned() && this.getSkin() == 2 && (this.getRandom().nextInt(100) < EVOLVE_RATE || this.isTame())) {
            List<Player> players = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(this.getAttributeValue(Attributes.FOLLOW_RANGE)));
            if (players.isEmpty() && !this.isTame()) {
                this.dieOfAge();
            } else {
                this.lifespawn = 100;
                if (this.level() instanceof ServerLevel server) {
                    this.playSound(FurSounds.PARASITE_WEAVE.get(), 1.0F, 1.0F);
                    VespaCocoonEntity pupa = FurSpawnUtil.trySpawnEntity(FurEntities.VESPACOCOON.get(), server, this.blockPosition());
                    if (pupa != null) {
                        pupa.setSkin(0);
                        if (this.isTame() && this.getOwner() instanceof Player owner) {
                            pupa.tame(owner);
                            pupa.setCustomName(this.getCustomName());
                        }
                    }
                }
                this.discard();
            }
        } else {
            // Вариант 3 у прирученного в оригинале плёл куколку Beelzebub — его в порте нет, поэтому просто погибает.
            this.dieOfAge();
        }

        if (this.getVehicle() instanceof LivingEntity mount && mount.isAlive() && !this.level().isClientSide) {
            if (!mount.hasEffect(FurEffects.INFESTED.get()) && !this.isSummoned()) {
                this.stopRiding();
                this.dieOfAge();
            } else if (mount.isOnFire()) {
                this.setRemainingFireTicks(20);
                this.stopRiding();
            } else if (this.tickCount % 20 == 0) {
                this.doHurtTarget(mount);
            }
        }

        if (!this.level().isClientSide) {
            if (this.onGround() || this.isInWaterOrBubble() || this.isInLava()) {
                this.entityData.set(ATTACHED_BLK, Direction.DOWN);
            } else if (this.verticalCollision) {
                this.entityData.set(ATTACHED_BLK, Direction.UP);
            } else {
                Direction closest = Direction.DOWN;
                double closestDistance = 100.0;
                BlockPos here = BlockPos.containing(this.getX(), this.getY(), this.getZ());
                for (Direction dir : DIRECTIONS) {
                    BlockPos offsetPos = here.relative(dir);
                    Vec3 offset = Vec3.atCenterOf(offsetPos);
                    if (closestDistance > this.position().distanceTo(offset)
                            && this.level().loadedAndEntityCanStandOnFace(offsetPos, this, dir.getOpposite())) {
                        closestDistance = this.position().distanceTo(offset);
                        closest = dir;
                    }
                }
                this.entityData.set(ATTACHED_BLK, closest);
            }
        }

        super.tick();
    }

    /// Крадучись пустой рукой — подобрать (Parasite_Pickup = true).
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() && !this.isTame() && player.isSecondaryUseActive()) {
            player.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
            ItemStack drop = new ItemStack(drop(this.getSkin()));
            if (!player.getInventory().add(drop)) player.drop(drop, false);
            this.discard();
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public double getMyRidingOffset() {
        Entity vehicle = this.getVehicle();
        if (vehicle == null) return super.getMyRidingOffset();
        boolean humanoid = vehicle instanceof Player || vehicle instanceof Zombie || vehicle instanceof AbstractVillager
                || vehicle instanceof Raider || vehicle instanceof AbstractSkeleton;
        if (humanoid && !((LivingEntity) vehicle).isBaby()) return vehicle.getBbHeight() / 2.0F - 0.85F;
        return vehicle.getBbHeight() * 0.65 - 1.0;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!super.doHurtTarget(target)) return false;
        if (target instanceof LivingEntity living) {
            if (!this.isSummoned()) living.addEffect(new MobEffectInstance(FurEffects.INFESTED.get(), 160, 0));
            if (this.getSkin() == 2) living.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
        }
        return true;
    }

    /// Цепляется к зомби (Parasite_Attach = true).
    @Override
    public void push(Entity entity) {
        super.push(entity);
        if (entity instanceof LivingEntity living && !(entity instanceof Player) && entity.getType().is(FurTags.PARASITE_TARGETS) && !this.isPassenger()) {
            if (!this.isSummoned()) living.addEffect(new MobEffectInstance(FurEffects.INFESTED.get(), 160, 0));
            this.startRiding(entity);
        }
    }

    /// Цепляется к игроку.
    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);
        if (!player.isCreative() && !player.isSpectator() && !this.isPassenger()) {
            if (!this.isSummoned()) player.addEffect(new MobEffectInstance(FurEffects.INFESTED.get(), 160, 0));
            this.startRiding(player);
        }
    }

    public Direction getAttachedBlock() {
        return this.entityData.get(ATTACHED_BLK);
    }

    public int getSkin() {
        return this.entityData.get(SKIN_TYPE);
    }

    public void setSkin(int skin) {
        this.entityData.set(SKIN_TYPE, skin);
    }

    private boolean getFlag(int mask) {
        return (this.entityData.get(PARASITE_FLAGS) & mask) != 0;
    }

    private void setFlag(int mask, boolean on) {
        byte b = this.entityData.get(PARASITE_FLAGS);
        this.entityData.set(PARASITE_FLAGS, (byte) (on ? b | mask : b & ~mask));
    }

    /// Призванный (хозяином-веспой или Beelzebub): не заражает, ничего не роняет.
    public boolean isSummoned() {
        return this.getFlag(1);
    }

    public void setSummoned(boolean on) {
        this.setFlag(1, on);
    }

    public boolean isTame() {
        return this.getFlag(4);
    }

    public void setTame(boolean on) {
        this.setFlag(4, on);
    }

    public void tame(Player player) {
        this.setTame(true);
        this.setOwnerUUID(player.getUUID());
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }

    @Nullable
    public LivingEntity getOwner() {
        UUID uuid = this.getOwnerUUID();
        return uuid == null ? null : this.level().getPlayerByUUID(uuid);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSkin(tag.getInt("Variant"));
        if (tag.hasUUID("Owner")) {
            this.setOwnerUUID(tag.getUUID("Owner"));
            this.setTame(true);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getSkin());
        if (this.getOwnerUUID() != null) tag.putUUID("Owner", this.getOwnerUUID());
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 0.1F;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return FurSounds.PARASITE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return FurSounds.PARASITE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return FurSounds.PARASITE_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SILVERFISH_STEP, 0.15F, 1.0F);
    }

    /// Добыча по варианту: `entities/parasite` (0, 3), `parasite1` (пустынный), `parasite2` (джунглей).
    @Override
    protected ResourceLocation getDefaultLootTable() {
        return switch (this.getSkin()) {
            case 1 -> new ResourceLocation(UndeadRising.MOD_ID, "entities/parasite1");
            case 2 -> new ResourceLocation(UndeadRising.MOD_ID, "entities/parasite2");
            default -> super.getDefaultLootTable();
        };
    }

    @Override
    public boolean shouldDropExperience() {
        return !this.isSummoned();
    }

    /// Умерший от старости или призванный — без добычи.
    @Override
    protected boolean shouldDropLoot() {
        return !this.isSummoned() && this.lifespawn > 0;
    }

    static class AttackGoal extends MeleeAttackGoal {
        AttackGoal(PathfinderMob mob) {
            super(mob, 1.0, true);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.mob.isPassenger();
        }

        @Override
        public boolean canContinueToUse() {
            float brightness = this.mob.getLightLevelDependentMagicValue();
            if (brightness >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
                this.mob.setTarget(null);
                return false;
            }
            return super.canContinueToUse();
        }

        @Override
        protected double getAttackReachSqr(LivingEntity target) {
            return 0.1F + target.getBbWidth();
        }
    }
}
