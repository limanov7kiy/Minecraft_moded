package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.wizardryrestored.util.SpellStates;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// Щит (непрерывное): пока заклинание удерживается, перед заклинателем висит барьер силы — снаряды и магия
/// спереди не проходят (`SpellStates`), плюс слабое Сопротивление.
public class Shield extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        LivingEntity caster = ctx.caster();
        if (ctx.world() instanceof ServerLevel server) {
            SpellStates.data(caster).putLong(SpellStates.SHIELD_UNTIL, server.getGameTime() + 5);
            caster.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 0, false, false, false));
            if (ctx.castingTicks() % 3 == 0) {
                Vec3 look = caster.getLookAngle();
                Vec3 f = caster.getEyePosition(1).add(look.scale(1.0));
                Vec3 side = look.cross(new Vec3(0, 1, 0)).normalize();
                for (int i = -2; i <= 2; i++)
                    for (int j = -2; j <= 2; j++) {
                        Vec3 p = f.add(side.scale(i * 0.25)).add(0, j * 0.25, 0);
                        server.sendParticles(ParticleTypes.ENCHANT, p.x, p.y, p.z, 1, 0, 0, 0, 0);
                    }
            }
        }
        return true;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected void playSound(Level world, LivingEntity entity, int castTicks, int duration) {
        if (castTicks % 30 == 0) super.playSound(world, entity, castTicks, duration);
    }

    @Override
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.HEALING, SpellTypes.DEFENCE, SpellAction.POINT, 5, 0, 0)
                .build();
    }
}
