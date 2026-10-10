package dev.mcmoded.wizardryrestored.entity;

import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import dev.mcmoded.wizardryrestored.WizardryRestored;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/// Молниевый диск: летит по прямой, слегка доворачивая к ближайшему врагу впереди, прорезает всех на пути
/// (каждого — один раз) и рассыпается искрами о стену.
public class LightningDiscEntity extends AreaConstruct {
    public static final double SEEK_RANGE = 6;
    private Vec3 velocity = Vec3.ZERO;
    private final Set<UUID> hit = new HashSet<>();

    public LightningDiscEntity(EntityType<? extends LightningDiscEntity> type, Level level) {
        super(type, level);
    }

    public void setVelocity(Vec3 velocity) {
        this.velocity = velocity;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            for (int i = 0; i < 4; i++) {
                double a = random.nextDouble() * Math.PI * 2;
                level().addParticle(ParticleTypes.ELECTRIC_SPARK, getX() + Math.cos(a) * 0.6, getY() + 0.1, getZ() + Math.sin(a) * 0.6, 0, 0, 0);
            }
            return;
        }
        double speed = velocity.length();
        if (speed < 1.0e-4) {
            discard();
            return;
        }
        // Самонаведение: доворот к ближайшему врагу в конусе впереди
        Vec3 dir = velocity.normalize();
        level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(SEEK_RANGE), e -> e.isAlive() && !isFriend(e) && !hit.contains(e.getUUID()))
                .stream().filter(e -> e.position().add(0, e.getBbHeight() / 2, 0).subtract(position()).normalize().dot(dir) > 0.7)
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                .ifPresent(e -> velocity = dir.scale(0.8).add(e.position().add(0, e.getBbHeight() / 2, 0).subtract(position()).normalize().scale(0.2)).normalize().scale(speed));

        Vec3 next = position().add(velocity);
        if (level().clip(new ClipContext(position(), next, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY(), getZ(), 20, 0.3, 0.3, 0.3, 0.2);
            discard();
            return;
        }
        setPos(next);
        float damage = WizardryRestored.LIGHTNING_DISC.get().property(DefaultProperties.DAMAGE) * damageMultiplier;
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.3), LivingEntity::isAlive)) {
            if (isFriend(e) || !hit.add(e.getUUID())) continue;
            e.hurt(damage(EBDamageSources.SHOCK), damage);
            level().playSound(null, e.getX(), e.getY(), e.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, getSoundSource(), 0.4f, 1.8f);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        velocity = new Vec3(tag.getDouble("vx"), tag.getDouble("vy"), tag.getDouble("vz"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("vx", velocity.x);
        tag.putDouble("vy", velocity.y);
        tag.putDouble("vz", velocity.z);
    }
}
