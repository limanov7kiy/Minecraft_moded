package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// Пока кнопка зажата в воздухе — плавное планирование вперёд вместо падения.
public class Glide extends Spell {
    private static final SpellProperty<Float> FALL_SPEED = SpellProperty.floatProperty("fall_speed", 0.1f);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player p = ctx.caster();
        if (p.onGround() || p.isInWater()) return false;
        Vec3 v = p.getDeltaMovement();
        Vec3 look = p.getLookAngle();
        double speed = property(DefaultProperties.SPEED);
        double acc = property(DefaultProperties.ACCELERATION);
        double vx = v.x + (look.x * speed - v.x) * acc;
        double vz = v.z + (look.z * speed - v.z) * acc;
        double vy = Math.max(v.y, -property(FALL_SPEED));
        p.setDeltaMovement(vx, vy, vz);
        p.fallDistance = 0;
        if (ctx.world().isClientSide && ctx.world().random.nextInt(3) == 0) {
            ctx.world().addParticle(ParticleTypes.CLOUD, p.getX(), p.getY(), p.getZ(), 0, -0.05, 0);
        }
        return true;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    // Непрерывное заклинание без звукового цикла
    @Override
    protected void playSound(Level world, LivingEntity entity, int castTicks, int duration) {
    }

    @Override
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.EARTH, SpellTypes.UTILITY, SpellAction.POINT_UP, 5, 0, 0)
                .add(DefaultProperties.SPEED, 0.4f)
                .add(DefaultProperties.ACCELERATION, 0.1f)
                .add(FALL_SPEED)
                .build();
    }
}
