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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

/// Приманка: на месте заклинателя остаётся его двойник, враги переключаются на него, а сам заклинатель
/// ненадолго становится невидимым, чтобы уйти.
public class Decoy extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (ctx.world() instanceof ServerLevel server) {
            DecoyEntity decoy = new DecoyEntity(ModEntities.DECOY.get(), server);
            decoy.copyFrom(caster, (int) (property(DefaultProperties.DURATION) * ctx.modifiers().get(SpellModifiers.DURATION)));
            server.addFreshEntity(decoy);
            decoy.lureEnemies(caster);
            caster.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, property(DefaultProperties.EFFECT_DURATION), 0, false, false, true));
            server.sendParticles(ParticleTypes.POOF, caster.getX(), caster.getY() + 1, caster.getZ(), 20, 0.3, 0.6, 0.3, 0.02);
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellTypes.DEFENCE, SpellAction.POINT_DOWN, 40, 0, 200)
                .add(DefaultProperties.DURATION, 600)
                .add(DefaultProperties.EFFECT_DURATION, 100)
                .build();
    }
}
