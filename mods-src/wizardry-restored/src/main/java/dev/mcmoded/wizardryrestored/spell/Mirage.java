package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.wizardryrestored.entity.DecoyEntity;
import dev.mcmoded.wizardryrestored.entity.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

/// Мираж: заклинатель становится невидимым, а в нескольких блоках от него появляется его иллюзия, которая
/// перескакивает с места на место и притягивает внимание врагов. Длится 20 секунд.
public class Mirage extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (ctx.world() instanceof ServerLevel server) {
            int duration = (int) (property(DefaultProperties.DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            DecoyEntity image = new DecoyEntity(ModEntities.DECOY.get(), server);
            image.copyFrom(caster, duration);
            image.setMirage(true);
            image.setNoAi(true);
            image.moveTo(caster.getX() + caster.getLookAngle().x * 3, caster.getY(), caster.getZ() + caster.getLookAngle().z * 3, caster.getYRot(), 0);
            server.addFreshEntity(image);
            image.lureEnemies(caster);
            caster.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false, true));
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellTypes.DEFENCE, SpellAction.POINT_UP, 35, 0, 300)
                .add(DefaultProperties.DURATION, 400)
                .build();
    }
}
