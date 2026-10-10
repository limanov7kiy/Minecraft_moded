package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/// Сбивает цель с толку: существо забывает, кого атаковало, и слабеет; игрока — шатает.
public class MindTrick extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        LivingEntity target = Targeting.lookTarget(ctx.caster(), range);
        if (target == null || !AllyDesignation.isValidTarget(ctx.caster(), target)) return false;
        int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));

        if (ctx.world() instanceof ServerLevel server) {
            if (target instanceof Mob mob) mob.setTarget(null);
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 0));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, 1));
            server.sendParticles(ParticleTypes.WITCH, target.getX(), target.getEyeY(), target.getZ(), 20, 0.3, 0.3, 0.3, 0.05);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.NECROMANCY, SpellTypes.ATTACK, SpellAction.POINT, 10, 0, 40)
                .add(DefaultProperties.RANGE, 8f)
                .add(DefaultProperties.EFFECT_DURATION, 300)
                .build();
    }
}
