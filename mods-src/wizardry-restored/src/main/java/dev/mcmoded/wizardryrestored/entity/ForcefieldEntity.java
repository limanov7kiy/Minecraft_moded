package dev.mcmoded.wizardryrestored.entity;

import com.binaris.wizardry.content.spell.DefaultProperties;
import dev.mcmoded.wizardryrestored.WizardryRestored;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// Силовое поле: сфера вокруг места сотворения. Чужие снаряды внутри рассыпаются, враги выталкиваются наружу;
/// заклинатель и союзники ходят и стреляют сквозь неё свободно.
public class ForcefieldEntity extends AreaConstruct {
    public ForcefieldEntity(EntityType<? extends ForcefieldEntity> type, Level level) {
        super(type, level);
    }

    public double radius() {
        return WizardryRestored.FORCEFIELD.get().property(DefaultProperties.EFFECT_RADIUS);
    }

    @Override
    protected Vec3 center() {
        return position().add(0, 1, 0);
    }

    @Override
    public void tick() {
        super.tick();
        double r = radius();
        Vec3 c = center();

        if (level().isClientSide) {
            // Оболочка сферы из искр
            for (int i = 0; i < 6; i++) {
                Vec3 d = new Vec3(random.nextGaussian(), random.nextGaussian(), random.nextGaussian()).normalize().scale(r);
                if (c.y + d.y < getY() - 0.2) continue;
                level().addParticle(ParticleTypes.END_ROD, c.x + d.x, c.y + d.y, c.z + d.z, 0, 0, 0);
            }
            return;
        }

        for (Projectile p : level().getEntitiesOfClass(Projectile.class, getBoundingBox().inflate(r))) {
            Entity owner = p.getOwner();
            if (p.position().distanceToSqr(c) > r * r || (owner != null && isFriend(owner))) continue;
            ((ServerLevel) level()).sendParticles(ParticleTypes.ENCHANTED_HIT, p.getX(), p.getY(), p.getZ(), 8, 0.2, 0.2, 0.2, 0.1);
            level().playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.SHIELD_BLOCK, getSoundSource(), 0.6f, 1.4f);
            p.discard();
        }

        for (LivingEntity e : livingWithin(r)) {
            if (isFriend(e)) continue;
            Vec3 away = e.position().subtract(c).multiply(1, 0, 1);
            if (away.lengthSqr() < 1.0e-4) away = new Vec3(1, 0, 0);
            push(e, away.normalize().scale(0.6).add(0, 0.2, 0));
        }
    }
}
