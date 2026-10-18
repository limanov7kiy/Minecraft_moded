package dev.mcmoded.necromancersdelight.entity;

import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.core.platform.Services;
import dev.mcmoded.undeadrising.entity.ParasiteEntity;
import dev.mcmoded.undeadrising.registry.FurSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/// Пиявка-миньон (оригинал — `EntityLeechMinionBase`, WinDanesz): маленький «паук» на модели паразита Fish's
/// Undead Rising. Коснувшись не-союзника, впивается в него (садится верхом), становится бессрочной и атакует носителя;
/// если носитель горит — горит и пиявка. Владелец, время жизни и цели — `MinionData` Redux (как у призыва зомби).
public abstract class LeechMinion extends Spider {
    protected LeechMinion(EntityType<? extends LeechMinion> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    /// Без паука-наездника и случайных эффектов ванильного паука.
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty, net.minecraft.world.entity.MobSpawnType reason,
            net.minecraft.world.entity.SpawnGroupData data, net.minecraft.nbt.CompoundTag tag) {
        return data;
    }

    /// Атрибуты оригинала (паук 1.12.2): 16 ОЗ, урон 2, скорость 0,3; у стихийной — 8 ОЗ.
    public static AttributeSupplier.Builder createAttributes() {
        return Spider.createAttributes().add(Attributes.MAX_HEALTH, 16.0).add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, true) {
            @Override
            protected double getAttackReachSqr(LivingEntity target) {
                return target.getBbWidth() + 0.1F;
            }
        });
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public MinionData minionData() {
        return Services.OBJECT_DATA.getMinionData(this);
    }

    public LivingEntity getCaster() {
        MinionData data = minionData();
        return data == null ? null : data.getOwner();
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getVehicle() instanceof LivingEntity host && host.isAlive()) {
            if (host.isOnFire()) setSecondsOnFire(20);
            attachedLogic(host);
        }
    }

    /// Что делает пиявка, сидя на носителе (раз в тик, на сервере).
    protected void attachedLogic(LivingEntity host) {}

    /// Впивается в коснувшегося не-союзника (оригинал — `collideWithEntity`).
    @Override
    protected void doPush(Entity other) {
        super.doPush(other);
        if (level().isClientSide || isPassenger() || !(other instanceof LivingEntity living) || other instanceof ParasiteEntity
                || other instanceof LeechMinion) return;
        LivingEntity caster = getCaster();
        if (caster == null || living == caster || AllyDesignation.isAllied(caster, living)) return;
        if (living instanceof Player p && (p.isCreative() || p.isSpectator())) return;
        if (startRiding(living, true)) {
            if (living instanceof ServerPlayer sp) sp.connection.send(new ClientboundSetPassengersPacket(living));
            getNavigation().stop();
            MinionData data = minionData();
            if (data != null) data.setLifetime(-1);
            setTarget(living);
        }
    }

    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);
        doPush(player);
    }

    @Override
    public double getMyRidingOffset() {
        Entity vehicle = getVehicle();
        if (vehicle == null) return super.getMyRidingOffset();
        return vehicle.getBbHeight() / 2.0F - 0.85F;
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(net.minecraft.world.entity.Pose pose, net.minecraft.world.entity.EntityDimensions size) {
        return 0.1F;
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return getCaster() == null && (minionData() == null || !minionData().isSummoned());
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
        playSound(SoundEvents.SILVERFISH_STEP, 0.15F, 1.0F);
    }
}
