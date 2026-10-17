package dev.mcmoded.undeadrising.entity.flying;

import dev.mcmoded.undeadrising.entity.FURTameableEntity;
import dev.mcmoded.undeadrising.entity.IAggressive;
import dev.mcmoded.undeadrising.util.FurSpawnUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

/// Летающий приручаемый моб: садится на землю отдохнуть ({@link #getLandTimer()} — для анимации «на земле»), летает
/// по случайным точкам не выше {@link #FLYING_HEIGHT_LIMIT} блоков над землёй, бьёт в ближнем бою.
/// Оригинал: Fish's Undead Rising (Fish), `entities/flying/FlyingMobEntity`.
public class FlyingMobEntity extends FURTameableEntity implements IAggressive {
    /// FlyingHeight_limit по умолчанию (0 — без ограничения).
    public static final int FLYING_HEIGHT_LIMIT = 16;

    private int attackTimer;
    private int hoverTimer;
    private int landTimer;

    public FlyingMobEntity(EntityType<? extends FlyingMobEntity> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveHelper(this);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.setNoGravity(true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AIFlyingAttackMelee(this, 1.0, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    /// Летуны появляются только под открытым небом (кроме Незера/Энда).
    public static boolean checkFlyerSpawnRules(EntityType<? extends FlyingMobEntity> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos,
                                               net.minecraft.util.RandomSource random) {
        return FURTameableEntity.checkMonsterSpawnRules(type, level, reason, pos, random)
                && (level.canSeeSky(pos) || level.getLevel().dimension() != Level.OVERWORLD);
    }

    @Override
    public void doFollowCommand(@Nullable Player player) {
        if (!this.isBaby()) this.setNoGravity(true);
        super.doFollowCommand(player);
    }

    @Override
    public void doWanderCommand(@Nullable Player player) {
        if (!this.isBaby()) this.setNoGravity(true);
        super.doWanderCommand(player);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackTimer > 0) this.attackTimer--;

        if (!this.level().isClientSide && !this.isBaby()) {
            if (this.onGround()) {
                if (this.getLandTimer() < 20) {
                    this.setLandTimer(this.getLandTimer() + 1);
                    this.level().broadcastEntityEvent(this, (byte) 40);
                }
                if (this.isNoGravity()) this.setNoGravity(this.getTarget() != null);
                if (!this.isNoGravity() && !this.isInSittingPose() && this.getRandom().nextFloat() < 0.15F) {
                    this.setNoGravity(true);
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.25, 0.0));
                }
            } else {
                if (this.getLandTimer() > 0) {
                    this.setLandTimer(this.getLandTimer() - 1);
                    this.level().broadcastEntityEvent(this, (byte) 41);
                }
                if (!this.isNoGravity()) this.setNoGravity(true);
            }

            // Сидящий по приказу летун опускается на землю.
            if (this.isInSittingPose() && this.getTarget() == null && !this.onGround()
                    && FurSpawnUtil.hasGround(this.level(), FurSpawnUtil.getHeight(this))) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.025F, 0.0));
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.attackTimer == 0) {
            this.attackTimer = 20;
            this.level().broadcastEntityEvent(this, (byte) 4);
        }
        return super.doHurtTarget(target);
    }

    @Override
    public int getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    public void setAttackTimer(int ticks) {
        this.attackTimer = ticks;
    }

    public int getHoverTimer() {
        return this.hoverTimer;
    }

    public void setHoverTimer(int ticks) {
        this.hoverTimer = ticks;
    }

    public int getLandTimer() {
        return this.landTimer;
    }

    public void setLandTimer(int ticks) {
        this.landTimer = ticks;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attackTimer = 20;
        } else if (id == 40) {
            if (this.getLandTimer() < 20) this.setLandTimer(this.getLandTimer() + 1);
        } else if (id == 41) {
            if (this.getLandTimer() > 0) this.setLandTimer(this.getLandTimer() - 1);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    @Nullable
    public Entity getLowestPassenger() {
        Entity lowest = null;
        for (Entity passenger : this.getPassengers()) {
            if (lowest == null || passenger.getBoundingBox().minY < lowest.getBoundingBox().minY) lowest = passenger;
        }
        return lowest;
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 motionMultiplier) {
        if (state.is(Blocks.COBWEB)) {
            this.getMoveControl().setWantedPosition(this.getX(), this.getY() - 1.0, this.getZ(), 1.0);
        }
        super.makeStuckInBlock(state, motionMultiplier);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    /// Множитель скорости в полёте (у ездовых — свой).
    protected double VehicleSpeedMod() {
        return 1.0;
    }

    @Override
    public void travel(Vec3 travelVector) {
        Entity lowest = this.getLowestPassenger();
        if (lowest != null && !(lowest instanceof Player)) {
            AABB bounds = lowest.getBoundingBox();
            if (!lowest.level().noCollision(lowest, bounds)) {
                this.getMoveControl().setWantedPosition(this.getX(), this.getY() + lowest.getBbHeight(), this.getZ(), 1.0);
            }
        }

        if (!this.isNoGravity() && !this.isVehicle() && !(this.getControllingPassenger() instanceof Player)) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().x, -0.15, this.getDeltaMovement().z);
        }

        if (this.getTarget() != null) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(1.05));
        }

        if (this.isInWater()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
        } else if (this.isInLava()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
        } else {
            BlockPos ground = BlockPos.containing(this.getX(), this.getY() - 1.0, this.getZ());
            float f = 0.91F;
            if (this.onGround()) f = this.level().getBlockState(ground).getFriction(this.level(), ground, this) * 0.91F;
            float f1 = 0.16277137F / (f * f * f);
            this.moveRelative(this.onGround() ? 0.1F * f1 : 0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement().scale(this.VehicleSpeedMod()));
            this.setDeltaMovement(this.getDeltaMovement().scale(f));
        }

        this.calculateEntityAnimation(false);
    }

    @Override
    public boolean onClimbable() {
        return this.isBaby() && super.onClimbable();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        if (!this.isBaby()) this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.5, 0.0));
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    /// Ближний бой летуна: дальность — сумма квадратов ширин.
    static class AIFlyingAttackMelee extends MeleeAttackGoal {
        AIFlyingAttackMelee(PathfinderMob mob, double speed, boolean followUnseen) {
            super(mob, speed, followUnseen);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity target) {
            return this.mob.getBbWidth() * this.mob.getBbWidth() + target.getBbWidth() * target.getBbWidth();
        }
    }

    /// Блуждание в воздухе по случайным точкам (как у пчелы), с ограничением высоты над землёй.
    static class AIRandomFly extends Goal {
        private final FlyingMobEntity parent;

        AIRandomFly(FlyingMobEntity parent) {
            this.parent = parent;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            MoveControl move = this.parent.getMoveControl();
            if (this.parent.getNavigation() instanceof GroundPathNavigation) return false;
            if (!move.hasWanted()) return true;
            if (this.parent.getTarget() != null) return false;
            double dx = move.getWantedX() - this.parent.getX();
            double dy = move.getWantedY() - this.parent.getY();
            double dz = move.getWantedZ() - this.parent.getZ();
            double d = dx * dx + dy * dy + dz * dz;
            return d < 1.0 || d > 3600.0;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            Level level = this.parent.level();
            for (int i = 0; i < 3; i++) {
                Vec3 vec = this.findPos();
                if (vec == null) continue;
                BlockPos pos = BlockPos.containing(vec);
                int groundHeight;
                if (level.dimension() != Level.END) {
                    groundHeight = FurSpawnUtil.getHeight(level, pos).getY();
                } else {
                    groundHeight = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos).getY();
                    if (groundHeight <= level.getMinBuildHeight()) {
                        groundHeight = Math.min((this.parent.blockPosition().getY() / 16 + 1) * 8, 60);
                    }
                }

                if (this.parent.isInWater()) {
                    vec = new Vec3(vec.x, Math.min(vec.y, groundHeight + 3.0), vec.z);
                } else if (FLYING_HEIGHT_LIMIT != 0
                        && (vec.y > groundHeight + FLYING_HEIGHT_LIMIT + 4.0 || vec.y < groundHeight + FLYING_HEIGHT_LIMIT - 8.0)) {
                    vec = new Vec3(vec.x, groundHeight + FLYING_HEIGHT_LIMIT, vec.z);
                }

                this.parent.getMoveControl().setWantedPosition(vec.x + 0.5, vec.y + 0.5, vec.z + 0.5, 1.0);
                if (this.parent.getTarget() == null) {
                    this.parent.getLookControl().setLookAt(vec.x + 0.5, vec.y + 0.5, vec.z + 0.5, 180.0F, 20.0F);
                }
                break;
            }
        }

        @Nullable
        private Vec3 findPos() {
            Vec3 view = this.parent.getViewVector(0.0F);
            Vec3 pos = HoverRandomPos.getPos(this.parent, 8, 7, view.x, view.z, (float) (Math.PI / 2), 2, 1);
            return pos != null ? pos : AirAndWaterRandomPos.getPos(this.parent, 8, 4, -2, view.x, view.z, (float) (Math.PI / 2));
        }
    }

    /// Прямолинейный полёт к цели (как у гаста), с проверкой столкновений своего хитбокса и нижнего седока.
    static class FlyingMoveHelper extends MoveControl {
        private final FlyingMobEntity parent;
        private int courseChangeCooldown;
        private final double entityMoveSpeed;

        FlyingMoveHelper(FlyingMobEntity flyer) {
            super(flyer);
            this.parent = flyer;
            this.entityMoveSpeed = flyer.getAttributeValue(Attributes.FLYING_SPEED);
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                if (this.courseChangeCooldown-- <= 0) {
                    this.courseChangeCooldown += this.parent.getRandom().nextInt(5) + 2;
                    Vec3 vec = new Vec3(this.wantedX - this.parent.getX(), this.wantedY - this.parent.getY(), this.wantedZ - this.parent.getZ());
                    double dist = vec.length();
                    vec = vec.normalize();
                    if (this.isNotColliding(dist) && this.isNotPassengerColliding(dist)) {
                        this.parent.setDeltaMovement(this.parent.getDeltaMovement().add(vec.scale(this.entityMoveSpeed)));
                        float yaw = (float) (Mth.atan2(this.wantedZ - this.parent.getZ(), this.wantedX - this.parent.getX()) * (180.0 / Math.PI)) - 90.0F;
                        this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yaw, this.movement2RotationAngle(this.entityMoveSpeed)));
                    } else {
                        this.operation = MoveControl.Operation.WAIT;
                    }
                }
                if (this.parent.isInSittingPose() && this.parent.getTarget() == null) {
                    this.operation = MoveControl.Operation.WAIT;
                }
            }
        }

        private float movement2RotationAngle(double movement) {
            return (float) (movement * 1214.2856F - 31.428572F);
        }

        private boolean isNotPassengerColliding(double distance) {
            Entity lowest = this.parent.getLowestPassenger();
            if (lowest == null) return true;
            double dx = (this.wantedX - this.parent.getX()) / distance;
            double dy = (this.wantedY - this.parent.getY()) / distance;
            double dz = (this.wantedZ - this.parent.getZ()) / distance;
            AABB box = lowest.getBoundingBox();
            for (int i = 1; i < distance; i++) {
                box = box.move(dx, dy, dz);
                if (!lowest.level().noCollision(lowest, box)) return false;
            }
            return true;
        }

        private boolean isNotColliding(double distance) {
            double dx = (this.wantedX - this.parent.getX()) / distance;
            double dy = (this.wantedY - this.parent.getY()) / distance;
            double dz = (this.wantedZ - this.parent.getZ()) / distance;
            AABB box = this.parent.getBoundingBox();
            for (int i = 1; i < distance; i++) {
                box = box.move(dx, dy, dz);
                if (!this.parent.level().noCollision(this.parent, box)) return false;
            }
            return true;
        }
    }
}
