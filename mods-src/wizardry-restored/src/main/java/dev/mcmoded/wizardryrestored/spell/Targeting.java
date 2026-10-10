package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.util.RayTracer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Общие помощники наведения для заклинаний дополнения.
final class Targeting {
    private Targeting() {
    }

    /// Живое существо, на которое смотрит заклинатель (в пределах range), или null.
    static LivingEntity lookTarget(LivingEntity caster, double range) {
        Vec3 eye = caster.getEyePosition(1);
        Vec3 end = eye.add(caster.getLookAngle().scale(range));
        HitResult hit = RayTracer.rayTrace(caster.level(), caster, eye, end, 0.3f, false, LivingEntity.class,
                RayTracer.ignoreEntityFilter(caster));
        return hit instanceof EntityHitResult e && e.getEntity() instanceof LivingEntity l ? l : null;
    }
}
