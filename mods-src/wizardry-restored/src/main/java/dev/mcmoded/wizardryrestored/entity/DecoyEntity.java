package dev.mcmoded.wizardryrestored.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/// Приманка: двойник заклинателя (его скин, броня и предметы в руках — копии без выпадения). Бродит рядом и
/// перетягивает на себя врагов, нацеленных на заклинателя; от любого удара или по истечении времени рассеивается.
public class DecoyEntity extends PathfinderMob {
    private static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(DecoyEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    /// Радиус, в котором враги заклинателя переключаются на приманку.
    public static final double LURE_RADIUS = 16;
    private int lifetime = 600;
    /// Мираж: двойник не бродит, а каждые 2,5 с перескакивает в случайную точку рядом с заклинателем и от ударов
    /// не рассеивается (удары проходят сквозь иллюзию).
    private boolean mirage;

    public DecoyEntity(EntityType<? extends DecoyEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20).add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    /// Копирует облик заклинателя и перетягивает на себя врагов вокруг.
    public void copyFrom(LivingEntity caster, int lifetime) {
        this.lifetime = lifetime;
        entityData.set(OWNER, Optional.of(caster.getUUID()));
        moveTo(caster.getX(), caster.getY(), caster.getZ(), caster.getYRot(), 0);
        setYHeadRot(caster.getYHeadRot());
        setCustomName(caster.getName());
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            setItemSlot(slot, caster.getItemBySlot(slot).copy());
            setDropChance(slot, 0);
        }
        setPersistenceRequired();
    }

    public void setMirage(boolean mirage) {
        this.mirage = mirage;
    }

    public boolean isMirage() {
        return mirage;
    }

    public void lureEnemies(LivingEntity caster) {
        List<Mob> mobs = level().getEntitiesOfClass(Mob.class, getBoundingBox().inflate(LURE_RADIUS), m -> m.getTarget() == caster);
        mobs.forEach(m -> m.setTarget(this));
    }

    public Optional<UUID> owner() {
        return entityData.get(OWNER);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER, Optional.empty());
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (tickCount > lifetime) vanish();
        else if (mirage && tickCount % 50 == 0) hop();
    }

    private void hop() {
        Player owner = owner().map(level()::getPlayerByUUID).orElse(null);
        if (owner == null) {
            vanish();
            return;
        }
        for (int i = 0; i < 8; i++) {
            double a = random.nextDouble() * Math.PI * 2, d = 2 + random.nextDouble() * 2;
            double x = owner.getX() + Math.cos(a) * d, z = owner.getZ() + Math.sin(a) * d;
            if (level().noCollision(this, getBoundingBox().move(x - getX(), owner.getY() - getY(), z - getZ()))) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.PORTAL, getX(), getY() + 1, getZ(), 10, 0.3, 0.6, 0.3, 0.1);
                teleportTo(x, owner.getY(), z);
                setYRot(owner.getYRot());
                return;
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide || isInvulnerableTo(source)) return false;
        if (mirage && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        vanish();
        return false;
    }

    private void vanish() {
        ((ServerLevel) level()).sendParticles(ParticleTypes.POOF, getX(), getY() + 1, getZ(), 20, 0.3, 0.6, 0.3, 0.02);
        discard();
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("lifetime", lifetime);
        tag.putBoolean("mirage", mirage);
        owner().ifPresent(u -> tag.putUUID("owner", u));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        lifetime = tag.getInt("lifetime");
        mirage = tag.getBoolean("mirage");
        if (tag.hasUUID("owner")) entityData.set(OWNER, Optional.of(tag.getUUID("owner")));
    }

    /// Пока приманка жива, враги, выбирающие целью заклинателя, переключаются на неё.
    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity target = event.getNewTarget();
        LivingEntity mob = event.getEntity();
        if (target == null || mob.level().isClientSide || mob instanceof DecoyEntity) return;
        UUID id = target.getUUID();
        mob.level().getEntitiesOfClass(DecoyEntity.class, mob.getBoundingBox().inflate(LURE_RADIUS),
                d -> d.isAlive() && d.owner().map(id::equals).orElse(false)).stream().findFirst().ifPresent(event::setNewTarget);
    }
}
