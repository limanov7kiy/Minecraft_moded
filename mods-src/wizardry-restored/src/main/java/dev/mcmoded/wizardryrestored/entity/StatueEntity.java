package dev.mcmoded.wizardryrestored.entity;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

/// Статуя: существо, превращённое в камень (окаменение) или вмороженное в лёд (ледяная статуя). Само существо
/// убрано из мира, его NBT хранится в статуе; по истечении времени оно возвращается на место. Статую можно разбить:
/// урон по ней копится, и когда превысит здоровье существа — оно погибает (с обычной добычей).
public class StatueEntity extends Entity {
    private static final EntityDataAccessor<CompoundTag> CREATURE = SynchedEntityData.defineId(StatueEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Boolean> ICE = SynchedEntityData.defineId(StatueEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(StatueEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(StatueEntity.class, EntityDataSerializers.FLOAT);
    private int lifetime = 900;
    private float damage;
    /// Модель существа для рендера (только на клиенте, создаётся лениво из NBT).
    @Nullable
    public Entity clientModel;

    public StatueEntity(EntityType<? extends StatueEntity> type, Level level) {
        super(type, level);
    }

    /// Превращает существо в статую. Возвращает статую или null, если существо нельзя убрать из мира.
    @Nullable
    public static StatueEntity petrify(Mob mob, boolean ice, int lifetime) {
        if (!(mob.level() instanceof ServerLevel server)) return null;
        CompoundTag tag = new CompoundTag();
        mob.stopRiding();
        mob.ejectPassengers();
        if (!mob.save(tag)) return null;
        StatueEntity statue = new StatueEntity(ModEntities.STATUE.get(), server);
        statue.entityData.set(CREATURE, tag);
        statue.entityData.set(ICE, ice);
        statue.entityData.set(WIDTH, mob.getBbWidth());
        statue.entityData.set(HEIGHT, mob.getBbHeight());
        statue.lifetime = lifetime;
        statue.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), 0);
        statue.refreshDimensions();
        mob.discard();
        server.addFreshEntity(statue);
        return statue;
    }

    public CompoundTag creature() {
        return entityData.get(CREATURE);
    }

    public boolean isIce() {
        return entityData.get(ICE);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(CREATURE, new CompoundTag());
        entityData.define(ICE, false);
        entityData.define(WIDTH, 0.6f);
        entityData.define(HEIGHT, 1.8f);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (WIDTH.equals(key) || HEIGHT.equals(key)) refreshDimensions();
        if (CREATURE.equals(key)) clientModel = null;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(entityData.get(WIDTH), entityData.get(HEIGHT));
    }

    @Override
    public void tick() {
        super.tick();
        if (!onGround()) {
            setDeltaMovement(getDeltaMovement().add(0, -0.08, 0).scale(0.98));
            move(net.minecraft.world.entity.MoverType.SELF, getDeltaMovement());
        }
        if (!level().isClientSide && tickCount > lifetime) release(null);
    }

    /// Один тик с заданным счётчиком — для серверных тестов.
    public void tickAt(int tick) {
        tickCount = tick;
        tick();
    }

    /// Вернуть существо в мир; если задан source — оно тут же гибнет от разбитой статуи.
    private void release(@Nullable DamageSource source) {
        ServerLevel server = (ServerLevel) level();
        Entity e = EntityType.loadEntityRecursive(creature().copy(), server, x -> {
            x.moveTo(getX(), getY(), getZ(), getYRot(), x.getXRot());
            return x;
        });
        BlockState shard = isIce() ? Blocks.ICE.defaultBlockState() : Blocks.STONE.defaultBlockState();
        server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, shard), getX(), getY() + getBbHeight() / 2, getZ(),
                40, getBbWidth() / 2, getBbHeight() / 3, getBbWidth() / 2, 0.1);
        playSound(isIce() ? SoundEvents.GLASS_BREAK : SoundEvents.STONE_BREAK, 1, 1);
        discard();
        if (e == null) return;
        server.addFreshEntityWithPassengers(e);
        if (e instanceof LivingEntity living) {
            if (source != null) living.hurt(source, Float.MAX_VALUE);
            else if (isIce()) living.addEffect(new MobEffectInstance(com.binaris.wizardry.setup.registries.EBMobEffects.FROST.get(), 100, 1));
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide || isInvulnerableTo(source) || isRemoved()) return false;
        damage += amount;
        float health = creature().contains("Health") ? creature().getFloat("Health") : 10;
        if (damage >= health) release(source);
        else playSound(isIce() ? SoundEvents.GLASS_HIT : SoundEvents.STONE_HIT, 1, 1);
        return true;
    }

    @Override
    public boolean isPickable() {
        return !isRemoved();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(CREATURE, tag.getCompound("creature"));
        entityData.set(ICE, tag.getBoolean("ice"));
        entityData.set(WIDTH, tag.getFloat("width"));
        entityData.set(HEIGHT, tag.getFloat("height"));
        lifetime = tag.getInt("lifetime");
        damage = tag.getFloat("damage");
        tickCount = tag.getInt("age");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("creature", creature());
        tag.putBoolean("ice", isIce());
        tag.putFloat("width", entityData.get(WIDTH));
        tag.putFloat("height", entityData.get(HEIGHT));
        tag.putInt("lifetime", lifetime);
        tag.putFloat("damage", damage);
        tag.putInt("age", tickCount);
    }
}
