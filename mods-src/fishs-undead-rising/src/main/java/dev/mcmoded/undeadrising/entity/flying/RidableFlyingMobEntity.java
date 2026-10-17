package dev.mcmoded.undeadrising.entity.flying;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.EnumSet;

/// Ездовой летун: прирученного можно оседлать (седло), владелец управляет полётом; прыжок — вверх, клавиша
/// «вниз» мода — вниз, клавиша «особая способность» — умение ездового (пакет {@code MountSpecialPacket}).
/// Оригинал: Fish's Undead Rising (Fish), `entities/flying/RidableFlyingMobEntity`.
public class RidableFlyingMobEntity extends FlyingMobEntity implements Saddleable {
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(RidableFlyingMobEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> CONTROL_STATE = SynchedEntityData.defineId(RidableFlyingMobEntity.class, EntityDataSerializers.BYTE);
    public int abilityCooldown = 0;
    protected int spellTicks;

    public RidableFlyingMobEntity(EntityType<? extends FlyingMobEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SADDLED, false);
        this.entityData.define(CONTROL_STATE, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AICastingSpell());
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    public boolean isSpellcasting() {
        return this.spellTicks > 0;
    }

    public int getSpellTicks() {
        return this.spellTicks;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isFood(stack) && this.isOwnedBy(player) && this.isSaddled() && !this.isVehicle()) {
            if (stack.is(Items.SHEARS)) {
                this.setSaddled(false);
                this.spawnAtLocation(Items.SADDLE, 1);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (!player.isSecondaryUseActive() && !player.isPassenger()) {
                player.startRiding(this);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight() * 0.9;
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

    /// Перезарядка особой способности, тиков.
    public int abilityCooldown() {
        return 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.abilityCooldown > 0) this.abilityCooldown--;
        if (this.spellTicks > 0) {
            this.spellTicks--;
            this.setDeltaMovement(Vec3.ZERO);
        }
        if (this.isUp() && !this.isDown()) this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.05F, 0.0));
        if (!this.isUp() && this.isDown()) this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.05F, 0.0));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> dev.mcmoded.undeadrising.client.MountControls.tick(this));
        }
        if (!this.level().isClientSide && this.isAlive() && this.random.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0F);
        }
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, saddled);
    }

    @Override
    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    private boolean isUp() {
        return (this.entityData.get(CONTROL_STATE) & 1) == 1;
    }

    private boolean isDown() {
        return (this.entityData.get(CONTROL_STATE) >> 1 & 1) == 1;
    }

    /// Бит 0 — вверх, бит 1 — вниз. Ставится на клиенте всадника (движением ездового управляет его клиент).
    public void setControlState(int bit, boolean on) {
        byte prev = this.entityData.get(CONTROL_STATE);
        this.entityData.set(CONTROL_STATE, (byte) (on ? prev | 1 << bit : prev & ~(1 << bit)));
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
                super.travel(new Vec3(strafe, travelVector.y, forward));
            } else {
                this.setDeltaMovement(Vec3.ZERO);
            }
            this.calculateEntityAnimation(false);
        } else {
            super.travel(travelVector);
        }
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
        if (source != null) this.level().playSound(null, this, SoundEvents.PIG_SADDLE, source, 0.5F, 1.0F);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSaddled(tag.getBoolean("Saddled"));
        this.spellTicks = tag.getInt("SpellTicks");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Saddled", this.isSaddled());
        tag.putInt("SpellTicks", this.spellTicks);
    }

    /// Пока идёт заклинание — стоит и смотрит на цель.
    public class AICastingSpell extends Goal {
        public AICastingSpell() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return RidableFlyingMobEntity.this.getSpellTicks() > 0;
        }

        @Override
        public void start() {
            super.start();
            RidableFlyingMobEntity.this.getNavigation().stop();
        }

        @Override
        public void tick() {
            LivingEntity target = RidableFlyingMobEntity.this.getTarget();
            if (target != null) {
                RidableFlyingMobEntity.this.getLookControl().setLookAt(target, RidableFlyingMobEntity.this.getMaxHeadYRot(),
                        RidableFlyingMobEntity.this.getMaxHeadXRot());
            }
        }
    }
}
