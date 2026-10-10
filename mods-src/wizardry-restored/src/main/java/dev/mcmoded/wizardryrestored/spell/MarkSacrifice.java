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
import dev.mcmoded.wizardryrestored.WizardryRestored;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

/// Метка жертвы: цель помечена, и следующая магическая атака по ней наносит на 60% больше урона за каждый
/// уровень метки (эффект `wizardry_restored:mark_sacrifice`, обработка — `SpellStates`).
public class MarkSacrifice extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        LivingEntity target = Targeting.lookTarget(ctx.caster(), range);
        if (target == null || !AllyDesignation.isValidTarget(ctx.caster(), target)) return false;
        if (ctx.world() instanceof ServerLevel server) {
            int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            int amp = Math.max(0, Math.round((ctx.modifiers().get(SpellModifiers.POTENCY) - 1) / 0.15f) / 2);
            target.addEffect(new MobEffectInstance(WizardryRestored.MARK_SACRIFICE_EFFECT.get(), duration, amp));
            server.sendParticles(ParticleTypes.WAX_ON, target.getX(), target.getY() + target.getBbHeight() + 0.3, target.getZ(), 12, 0.3, 0.2, 0.3, 0);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.HEALING, SpellTypes.ALTERATION, SpellAction.POINT, 20, 0, 50)
                .add(DefaultProperties.RANGE, 12f)
                .add(DefaultProperties.EFFECT_DURATION, 600)
                .build();
    }
}
