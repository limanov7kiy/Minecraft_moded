/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/content/entity/boss/SkeletonBoss.java; attributes, rider offset, guaranteed loot and key
 * from Galacticraft Legacy (micdoodle8, radfast, Team Galacticraft) core/entities/EntitySkeletonBoss.java.
 * See CREDITS.md. Adapted to Minecraft 1.20.1 Forge.
 */
package dev.mcmoded.gcrevival.entity;

import dev.mcmoded.gcrevival.GCRevival;
import dev.mcmoded.gcrevival.ModRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

/** Лунный босс GC: огромный скелет-лучник, хватает игрока и швыряет его. */
public class SkeletonBoss extends AbstractBossEntity implements RangedAttackMob {
    /** Множитель здоровья боссов (в GC — dungeonBossHealthMod). */
    public static final float BASE_HEALTH = 150.0F;

    public int throwTimer;
    public int postThrowDelay = 20;
    public Entity thrownEntity;
    public Entity targetEntity;
    /** Разрешает спешивание игрока: только когда босс сам его бросает. */
    public boolean allowDismount;

    public SkeletonBoss(EntityType<? extends SkeletonBoss> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 25, 10.0F));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data, tag);
        // Legacy applyEntityAttributes: скорость растёт со сложностью
        double bonus = switch (level.getDifficulty()) {
            case HARD -> 2.0D;
            case NORMAL -> 1.0D;
            default -> 0.0D;
        };
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D + 0.075D * bonus);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setHealth(this.getMaxHealth());
        return result;
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (this.deathTime == 100 && !this.level().isClientSide) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModRegistry.SKELETON_BOSS_DEATH.get(), SoundSource.HOSTILE, 10.0F, 1.5F);
        }
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction move) {
        if (!this.hasPassenger(passenger)) return;
        double offsetX = Math.sin(-this.getYHeadRot() * Mth.DEG_TO_RAD);
        double offsetZ = Math.cos(this.getYHeadRot() * Mth.DEG_TO_RAD);
        double offsetY = 2 * Math.cos((this.throwTimer + this.postThrowDelay) * 0.05F);
        move.accept(passenger, this.getX() + offsetX, this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset() + offsetY, this.getZ() + offsetZ);
    }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.isNoAi() && this.getPassengers().isEmpty() && this.postThrowDelay == 0 && this.throwTimer == 0
                && player.equals(this.targetEntity) && this.deathTime == 0 && !player.isSpectator()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModRegistry.SKELETON_BOSS_LAUGH.get(), SoundSource.HOSTILE, 10.0F, 0.2F);
            if (!this.level().isClientSide) {
                player.startRiding(this, true);
            }
            this.throwTimer = 40;
        }
        super.playerTouch(player);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        this.playSound(ModRegistry.SKELETON_BOSS_OOH.get(), this.getSoundVolume(), this.getVoicePitch() + 1.0F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public void aiStep() {
        Player player = this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 20.0, false);
        if (player != null && !player.equals(this.targetEntity)) {
            if (this.distanceToSqr(player) < 400.0D) {
                this.getNavigation().createPath(player, 0);
                this.targetEntity = player;
            }
        } else if (player == null) {
            this.targetEntity = null;
        }

        if (this.throwTimer > 0) {
            this.throwTimer--;
        }
        if (this.postThrowDelay > 0) {
            this.postThrowDelay--;
        }

        if (!this.getPassengers().isEmpty() && this.throwTimer == 0) {
            this.postThrowDelay = 20;
            this.thrownEntity = this.getPassengers().get(0);
            if (!this.level().isClientSide) {
                this.allowDismount = true;
                this.ejectPassengers();
                this.allowDismount = false;
            }
        }

        if (this.thrownEntity != null && this.postThrowDelay == 18 && !this.level().isClientSide) {
            double x = this.getX() - this.thrownEntity.getX();
            double z;
            for (z = this.getZ() - this.thrownEntity.getZ(); x * x + z * z < 1.0E-4D; z = (Math.random() - Math.random()) * 0.01D) {
                x = (Math.random() - Math.random()) * 0.01D;
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.HOSTILE, 10.0F, 0.2F);
            if (this.thrownEntity instanceof LivingEntity living) {
                living.knockback(2.4F, x, z);
                living.hurtMarked = true;
            }
            this.thrownEntity = null;
        }

        super.aiStep();
    }

    @Override
    public ItemEntity spawnAtLocation(ItemStack stack, float yOffset) {
        ItemEntity item = new ItemEntity(this.level(), this.getX(), this.getY() + yOffset, this.getZ(), stack);
        item.setDeltaMovement(new Vec3(0, -2.0D, 0));
        item.setDefaultPickUpDelay();
        this.level().addFreshEntity(item);
        return item;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (!this.getPassengers().isEmpty()) {
            return;
        }
        ItemStack weapon = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
        if (weapon.isEmpty()) {
            weapon = new ItemStack(Items.BOW);
        }
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, this.getProjectile(weapon), pullProgress);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - arrow.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        arrow.shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, (float) (14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(arrow);
    }

    @Override
    public ItemStack getGuaranteedLoot(RandomSource random) {
        // Legacy GalacticraftRegistry.getDungeonLoot(1): схема ракеты T2 или лунохода
        return random.nextInt(3) == 0 ? new ItemStack(ModRegistry.SCHEMATIC_BUGGY.get()) : new ItemStack(ModRegistry.SCHEMATIC_ROCKET_T2.get());
    }

    @Override
    public ResourceLocation getTreasureLootTable() {
        return GCRevival.id("chests/dungeon_tier_1");
    }

    @Override
    public int getChestTier() {
        return 1;
    }

    @Override
    public void dropKey() {
        this.spawnAtLocation(new ItemStack(ModRegistry.KEY_T1.get()), 0.5F);
    }

    @Override
    public BossEvent.BossBarColor getHealthBarColor() {
        return BossEvent.BossBarColor.GREEN;
    }
}
