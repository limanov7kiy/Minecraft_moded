package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/// Подсвечивает всех существ вокруг заклинателя — их видно сквозь стены.
/// Упрощение относительно оригинала: используется ванильное свечение (его видят все игроки поблизости).
public class SixthSense extends Spell {

    @Override
    public boolean cast(PlayerCastContext ctx) {
        float radius = ctx.modifiers().get(SpellModifiers.BLAST, property(DefaultProperties.EFFECT_RADIUS));
        int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
        Player caster = ctx.caster();
        if (!ctx.world().isClientSide()) {
            List<LivingEntity> found = EntityUtil.getLivingWithinRadius(radius, caster.getX(), caster.getY(), caster.getZ(), ctx.world());
            for (LivingEntity e : found) {
                if (e != caster) e.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration, 0, false, false));
            }
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellTypes.BUFF, SpellAction.POINT_UP, 20, 0, 100)
                .add(DefaultProperties.EFFECT_DURATION, 400)
                .add(DefaultProperties.EFFECT_RADIUS, 20)
                .build();
    }
}
