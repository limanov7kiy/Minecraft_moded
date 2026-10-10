package dev.mcmoded.wizardryrestored.entity;

import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import dev.mcmoded.wizardryrestored.WizardryRestored;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// Иссушающий тотем: раз в секунду иссушает врагов вокруг и вытягивает из них жизнь — половина отнятого
/// достаётся заклинателю, если он рядом.
public class WitheringTotemEntity extends AreaConstruct {
    public static final float DRAIN_FRACTION = 0.5f;

    public WitheringTotemEntity(EntityType<? extends WitheringTotemEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected Vec3 center() {
        return position().add(0, 1.3, 0);
    }

    @Override
    public void tick() {
        super.tick();
        var spell = WizardryRestored.WITHERING_TOTEM.get();
        double r = spell.property(DefaultProperties.EFFECT_RADIUS);
        Vec3 c = center();
        if (level().isClientSide) {
            if (random.nextInt(2) == 0)
                level().addParticle(ParticleTypes.SOUL, c.x + random.nextGaussian() * 0.2, c.y + 0.1, c.z + random.nextGaussian() * 0.2, 0, 0.02, 0);
            return;
        }
        if (tickCount % 20 != 0) return;
        ServerLevel server = (ServerLevel) level();

        float drained = 0;
        for (LivingEntity e : livingWithin(r)) {
            if (isFriend(e)) continue;
            float before = e.getHealth();
            e.addEffect(new MobEffectInstance(MobEffects.WITHER, spell.property(DefaultProperties.EFFECT_DURATION), 1));
            e.hurt(damage(EBDamageSources.WITHER), spell.property(DefaultProperties.DAMAGE) * damageMultiplier);
            drained += Math.max(0, before - e.getHealth());
            Vec3 from = e.position().add(0, e.getBbHeight() / 2, 0);
            for (int i = 0; i <= 6; i++) {
                Vec3 p = from.lerp(c, i / 6.0);
                server.sendParticles(ParticleTypes.SMOKE, p.x, p.y, p.z, 1, 0, 0, 0, 0);
            }
        }
        LivingEntity caster = getCaster();
        if (drained > 0 && caster != null && caster.distanceToSqr(this) <= 4 * r * r) caster.heal(drained * DRAIN_FRACTION);
    }
}
