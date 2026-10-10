package dev.mcmoded.wizardryrestored.entity;

import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import dev.mcmoded.wizardryrestored.WizardryRestored;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

/// Сияющий тотем: раз в секунду лечит заклинателя и союзников рядом, дважды в секунду бьёт лучом света
/// ближайшего врага (нежити — вдвое сильнее).
public class RadiantTotemEntity extends AreaConstruct {
    public RadiantTotemEntity(EntityType<? extends RadiantTotemEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected Vec3 center() {
        return position().add(0, 1.3, 0);
    }

    @Override
    public void tick() {
        super.tick();
        var spell = WizardryRestored.RADIANT_TOTEM.get();
        double r = spell.property(DefaultProperties.EFFECT_RADIUS);
        Vec3 c = center();
        if (level().isClientSide) {
            if (random.nextInt(3) == 0)
                level().addParticle(ParticleTypes.END_ROD, c.x + random.nextGaussian() * 0.2, c.y + 0.2, c.z + random.nextGaussian() * 0.2, 0, 0.03, 0);
            return;
        }
        ServerLevel server = (ServerLevel) level();

        if (tickCount % 20 == 0) {
            for (LivingEntity e : livingWithin(r)) {
                if (!isFriend(e) || e.getHealth() >= e.getMaxHealth()) continue;
                e.heal(spell.property(DefaultProperties.HEALTH) * damageMultiplier);
                server.sendParticles(ParticleTypes.HAPPY_VILLAGER, e.getX(), e.getY() + e.getBbHeight() * 0.7, e.getZ(), 4, 0.3, 0.3, 0.3, 0);
            }
        }

        if (tickCount % 10 == 5) {
            livingWithin(r).stream().filter(e -> !isFriend(e) && hasLineOfSight(e))
                    .min(Comparator.comparingDouble(e -> e.distanceToSqr(c))).ifPresent(target -> {
                        float dmg = spell.property(DefaultProperties.DAMAGE) * damageMultiplier;
                        if (target.getMobType() == MobType.UNDEAD) dmg *= 2;
                        target.hurt(damage(EBDamageSources.RADIANT), dmg);
                        Vec3 to = target.position().add(0, target.getBbHeight() / 2, 0);
                        for (int i = 0; i <= 8; i++) {
                            Vec3 p = c.lerp(to, i / 8.0);
                            server.sendParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 1, 0, 0, 0, 0);
                        }
                        server.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, getSoundSource(), 0.8f, 1.5f);
                    });
        }
    }

    private boolean hasLineOfSight(LivingEntity e) {
        return level().clip(new net.minecraft.world.level.ClipContext(center(), e.getEyePosition(),
                net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, this)).getType()
                == net.minecraft.world.phys.HitResult.Type.MISS;
    }
}
