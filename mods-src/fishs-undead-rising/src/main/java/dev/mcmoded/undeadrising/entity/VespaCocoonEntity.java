package dev.mcmoded.undeadrising.entity;

import dev.mcmoded.undeadrising.entity.flying.VespaEntity;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurSounds;
import dev.mcmoded.undeadrising.util.FurSpawnUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/// Кокон веспы: неподвижен, через 8 секунд ({@code lifespan}) из него вылупляется веспа (у прирученного кокона —
/// прирученная, с тем же именем). Вариант 1 (кокон Enigmoth) и куколка Beelzebub в порт не входят.
/// Оригинал: Fish's Undead Rising (Fish), `entities/VespaCocoonEntity`.
public class VespaCocoonEntity extends FURTameableEntity {
    private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(VespaCocoonEntity.class, EntityDataSerializers.INT);
    private int lifespan = 160;

    public VespaCocoonEntity(EntityType<? extends VespaCocoonEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKIN_TYPE, 0);
    }

    @Override
    protected boolean isCommandable() {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.tickCount >= this.lifespan) {
            this.playSound(SoundEvents.SLIME_SQUISH, 1.0F, 1.0F);
            if (this.level() instanceof ServerLevel server && this.getType() == FurEntities.VESPACOCOON.get() && this.getSkin() == 0) {
                VespaEntity adult = FurSpawnUtil.trySpawnEntity(FurEntities.VESPA.get(), server, this.blockPosition());
                if (adult != null && this.isTame() && this.getOwner() instanceof Player owner) {
                    adult.tame(owner);
                    adult.setCustomName(this.getCustomName());
                }
            }
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
    }

    @Override
    public int getSkin() {
        return this.entityData.get(SKIN_TYPE);
    }

    @Override
    public void setSkin(int skin) {
        this.entityData.set(SKIN_TYPE, skin);
    }

    /// Не задыхается в блоках и под водой.
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.DROWN)) return false;
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("lifespan", this.lifespan);
        tag.putInt("Variant", this.getSkin());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("lifespan")) this.lifespan = tag.getInt("lifespan"); // в оригинале без проверки: /summon с NBT давал 0 и кокон вылуплялся сразу
        this.setSkin(tag.getInt("Variant"));
    }

    @Override
    protected boolean isImmobile() {
        return true;
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 0.8F;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WOOL_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return FurSounds.PARASITE_DEATH.get();
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }
}
