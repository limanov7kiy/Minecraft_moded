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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/// Грозовая туча: плывёт над головами в направлении взгляда заклинателя и бьёт разрядами врагов под собой
/// (урон электричеством, без настоящих молний — не поджигает постройки).
public class StormcloudEntity extends AreaConstruct {
    /// Высота столба под тучей, в котором ищутся цели.
    public static final double REACH_DOWN = 8;
    private Vec3 drift = Vec3.ZERO;

    public StormcloudEntity(EntityType<? extends StormcloudEntity> type, Level level) {
        super(type, level);
    }

    public void setDrift(Vec3 drift) {
        this.drift = drift;
    }

    @Override
    public void tick() {
        super.tick();
        double r = getBbWidth() / 2 + 0.5;
        if (level().isClientSide) {
            for (int i = 0; i < 3; i++)
                level().addParticle(ParticleTypes.CLOUD, getX() + (random.nextDouble() - 0.5) * getBbWidth(), getY() + random.nextDouble() * 0.8,
                        getZ() + (random.nextDouble() - 0.5) * getBbWidth(), 0, 0, 0);
            level().addParticle(ParticleTypes.FALLING_WATER, getX() + (random.nextDouble() - 0.5) * getBbWidth(), getY() - 0.1,
                    getZ() + (random.nextDouble() - 0.5) * getBbWidth(), 0, 0, 0);
            return;
        }
        setPos(position().add(drift));

        if (tickCount % 10 != 0) return;
        var spell = WizardryRestored.STORMCLOUD.get();
        // Кольцо Локензена: туча идёт к ближайшему врагу и живёт дольше с каждым ударом
        boolean ring = dev.mcmoded.wizardryrestored.util.Artifacts.has(getCaster(), com.binaris.wizardry.setup.registries.EBItems.RING_STORMCLOUD);
        if (ring) level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(10, REACH_DOWN, 10), e -> e.isAlive() && !isFriend(e))
                .stream().min(java.util.Comparator.comparingDouble(e -> e.position().subtract(position()).horizontalDistanceSqr()))
                .ifPresent(e -> {
                    Vec3 to = e.position().subtract(position()).multiply(1, 0, 1);
                    if (to.lengthSqr() > 0.25) drift = to.normalize().scale(Math.max(0.08, drift.length()));
                });
        ServerLevel server = (ServerLevel) level();
        AABB column = new AABB(getX() - r, getY() - REACH_DOWN, getZ() - r, getX() + r, getY() + 1, getZ() + r);
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, column, LivingEntity::isAlive)) {
            if (isFriend(e) || e.position().subtract(position()).horizontalDistanceSqr() > r * r) continue;
            e.hurt(damage(EBDamageSources.SHOCK), spell.property(DefaultProperties.DAMAGE) * damageMultiplier);
            Vec3 from = position(), to = e.position().add(0, e.getBbHeight(), 0);
            for (int i = 0; i <= 10; i++) {
                Vec3 p = from.lerp(to, i / 10.0).add(random.nextGaussian() * 0.1, 0, random.nextGaussian() * 0.1);
                server.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 2, 0.05, 0.05, 0.05, 0);
            }
            server.playSound(null, e.getX(), e.getY(), e.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, getSoundSource(), 0.5f, 1.6f);
            if (ring) setLifetime(getLifetime() + 20);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        drift = new Vec3(tag.getDouble("driftX"), 0, tag.getDouble("driftZ"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("driftX", drift.x);
        tag.putDouble("driftZ", drift.z);
    }
}
