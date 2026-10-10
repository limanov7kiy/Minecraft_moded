package dev.mcmoded.wizardryrestored.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;

/// Феникс: огненная летучая тварь (поведение ифрита — парит и мечет огненные шары). Один раз за жизнь
/// возрождается из пепла с полным здоровьем, вместо того чтобы погибнуть.
public class PhoenixEntity extends Blaze {
    private boolean reborn;

    public PhoenixEntity(EntityType<? extends Blaze> type, Level level) {
        super(type, level);
        this.xpReward = 15;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Blaze.createAttributes().add(Attributes.MAX_HEALTH, 40).add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    public boolean isReborn() {
        return reborn;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && random.nextInt(2) == 0)
            level().addParticle(ParticleTypes.FLAME, getRandomX(0.6), getRandomY(), getRandomZ(0.6), 0, 0.02, 0);
    }

    @Override
    public void die(DamageSource source) {
        if (!reborn && !level().isClientSide && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            reborn = true;
            setHealth(getMaxHealth());
            ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME, getX(), getY() + 1, getZ(), 60, 0.5, 0.8, 0.5, 0.15);
            playSound(SoundEvents.BLAZE_SHOOT, 1.5f, 0.6f);
            return;
        }
        super.die(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("reborn", reborn);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        reborn = tag.getBoolean("reborn");
    }
}
