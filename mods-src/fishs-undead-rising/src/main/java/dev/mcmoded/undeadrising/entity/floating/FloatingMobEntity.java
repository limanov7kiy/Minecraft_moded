package dev.mcmoded.undeadrising.entity.floating;

import dev.mcmoded.undeadrising.entity.IAggressive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

/// Парящий призрак: летает сквозь блоки, пока агрессивен, горит на солнце и прячется от него, сторонится света
/// (блочный свет > 11). Рывок к цели — {@link AIChargeAttack}, каст заклинания — {@link #spellTicks}.
/// Оригинал: Fish's Undead Rising (Fish), `entities/floating/FloatingMobEntity`.
public class FloatingMobEntity extends Monster implements IAggressive {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(FloatingMobEntity.class, EntityDataSerializers.BYTE);
    private int attackTimer = 0;
    protected int spellTicks;

    public FloatingMobEntity(EntityType<? extends FloatingMobEntity> type, Level level) {
        super(type, level);
        this.moveControl = new AIMoveControl(this);
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        super.move(type, movement);
        this.checkInsideBlocks();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AICastingSpell());
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        // SunScreen_Mode = false (по умолчанию): прячутся от солнца.
        this.goalSelector.addGoal(5, new FleeSunGoal(this, 1.0));
        this.goalSelector.addGoal(7, this.wanderGoal());
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.applyEntityAI();
    }

    protected void applyEntityAI() {
    }

    protected Goal wanderGoal() {
        return new AIMoveRandom();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    /// Частицы-шлейф (null — без шлейфа).
    @Nullable
    protected ParticleOptions particleType() {
        return null;
    }

    private boolean getFloaterFlag(int mask) {
        return (this.entityData.get(DATA_FLAGS_ID) & mask) != 0;
    }

    private void setFloaterFlag(int mask, boolean on) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        i = on ? i | mask : i & ~mask;
        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 0xFF));
    }

    public boolean isSpellcasting() {
        return this.getFloaterFlag(2);
    }

    public void setIsCasting(boolean on) {
        this.setFloaterFlag(2, on);
    }

    public boolean isCharging() {
        return this.getFloaterFlag(1);
    }

    public void setIsCharging(boolean on) {
        this.setFloaterFlag(1, on);
    }

    public int getSpellTicks() {
        return this.spellTicks;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        ParticleOptions particle = this.particleType();
        if (this.tickCount % 2 == 0 && particle != null) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(particle, this.getRandomX(1.0), this.getRandomY() + this.getBbHeight() * 0.75, this.getRandomZ(1.0), d0, d1, d2);
        }
    }

    @Override
    public void tick() {
        if (this.spellTicks > 0) this.spellTicks--;
        if (this.attackTimer > 0) this.attackTimer--;
        if (this.isSunBurnTick()) this.setSecondsOnFire(8);
        // Агрессивный призрак проходит сквозь стены.
        if (this.isAggressive()) this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
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
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBrightness(LightLayer.BLOCK, pos) > 11 ? -1.0F : super.getWalkTargetValue(pos, level);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 10) {
            this.spellTicks = 30;
        } else if (id == 4) {
            this.attackTimer = 20;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return size.height * 0.8F;
    }

    /// Пока идёт заклинание — стоит и смотрит на цель.
    public class AICastingSpell extends Goal {
        public AICastingSpell() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return FloatingMobEntity.this.getSpellTicks() > 0;
        }

        @Override
        public void start() {
            super.start();
            FloatingMobEntity.this.setIsCasting(true);
            FloatingMobEntity.this.navigation.stop();
        }

        @Override
        public void stop() {
            super.stop();
            FloatingMobEntity.this.setIsCasting(false);
        }

        @Override
        public void tick() {
            LivingEntity target = FloatingMobEntity.this.getTarget();
            if (target != null) {
                FloatingMobEntity.this.getLookControl().setLookAt(target, FloatingMobEntity.this.getMaxHeadYRot(), FloatingMobEntity.this.getMaxHeadXRot());
            }
        }
    }

    /// Рывок к цели (как у вексов).
    class AIChargeAttack extends Goal {
        AIChargeAttack() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            FloatingMobEntity self = FloatingMobEntity.this;
            return self.getTarget() != null && !self.getMoveControl().hasWanted() && self.getRandom().nextInt(7) == 0
                    && self.distanceToSqr(self.getTarget()) > 4.0;
        }

        @Override
        public boolean canContinueToUse() {
            FloatingMobEntity self = FloatingMobEntity.this;
            return self.getMoveControl().hasWanted() && self.isCharging() && self.getTarget() != null && self.getTarget().isAlive();
        }

        @Override
        public void start() {
            Vec3 eye = FloatingMobEntity.this.getTarget().getEyePosition(1.0F);
            FloatingMobEntity.this.moveControl.setWantedPosition(eye.x, eye.y, eye.z, 1.2);
            FloatingMobEntity.this.setIsCharging(true);
        }

        @Override
        public void stop() {
            FloatingMobEntity.this.setIsCharging(false);
        }

        @Override
        public void tick() {
            FloatingMobEntity self = FloatingMobEntity.this;
            LivingEntity target = self.getTarget();
            if (target == null) return;
            if (self.getBoundingBox().intersects(target.getBoundingBox())) {
                self.doHurtTarget(target);
                self.setIsCharging(false);
            } else if (self.distanceToSqr(target) < 9.0) {
                Vec3 eye = target.getEyePosition(1.0F);
                self.moveControl.setWantedPosition(eye.x, eye.y, eye.z, 1.0);
                if (self.attackTimer == 0) {
                    self.attackTimer = 20;
                    self.level().broadcastEntityEvent(self, (byte) 4);
                }
            }
        }
    }

    /// Плавное парение к точке; без цели — смотрит по ходу движения, с целью — на неё.
    class AIMoveControl extends MoveControl {
        AIMoveControl(FloatingMobEntity mob) {
            super(mob);
        }

        @Override
        public void tick() {
            if (this.operation != MoveControl.Operation.MOVE_TO) return;
            FloatingMobEntity self = FloatingMobEntity.this;
            Vec3 vec = new Vec3(this.wantedX - self.getX(), this.wantedY - self.getY(), this.wantedZ - self.getZ());
            double d0 = vec.length();
            if (d0 < self.getBoundingBox().getSize()) {
                this.operation = MoveControl.Operation.WAIT;
                self.setDeltaMovement(self.getDeltaMovement().scale(0.5));
            } else {
                self.setDeltaMovement(self.getDeltaMovement().add(vec.scale(this.speedModifier * 0.05 / d0)));
                if (self.getTarget() == null) {
                    Vec3 dm = self.getDeltaMovement();
                    self.setYRot(-((float) Mth.atan2(dm.x, dm.z)) * (180.0F / (float) Math.PI));
                } else {
                    double dx = self.getTarget().getX() - self.getX();
                    double dz = self.getTarget().getZ() - self.getZ();
                    self.setYRot(-((float) Mth.atan2(dx, dz)) * (180.0F / (float) Math.PI));
                }
                self.yBodyRot = self.getYRot();
            }
        }
    }

    /// Медленное блуждание по случайным точкам.
    class AIMoveRandom extends Goal {
        AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !FloatingMobEntity.this.getMoveControl().hasWanted() && FloatingMobEntity.this.getRandom().nextInt(7) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            FloatingMobEntity self = FloatingMobEntity.this;
            for (int i = 0; i < 3; i++) {
                Vec3 vec = this.findPos();
                if (vec != null) {
                    self.moveControl.setWantedPosition(vec.x + 0.5, vec.y + 0.5, vec.z + 0.5, 0.25);
                    if (self.getTarget() == null) self.getLookControl().setLookAt(vec.x + 0.5, vec.y + 0.5, vec.z + 0.5, 180.0F, 20.0F);
                    break;
                }
            }
        }

        @Nullable
        private Vec3 findPos() {
            FloatingMobEntity self = FloatingMobEntity.this;
            Vec3 view = self.getViewVector(0.0F);
            Vec3 pos = HoverRandomPos.getPos(self, 8, 7, view.x, view.z, (float) (Math.PI / 2), 2, 1);
            return pos != null ? pos : AirAndWaterRandomPos.getPos(self, 8, 4, -2, view.x, view.z, (float) (Math.PI / 2));
        }
    }
}
