package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.wizardryrestored.util.SpellStates;
import dev.mcmoded.wizardryrestored.util.TempBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Ледяной покров на 30 секунд: всё, что бьёт заклинателя, покрывается инеем (эффект обморожения Redux).
public class IceShroud extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.world() instanceof ServerLevel server) {
            int duration = (int) (property(DefaultProperties.DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            var d = SpellStates.data(ctx.caster());
            d.putLong(SpellStates.ICE_SHROUD_UNTIL, server.getGameTime() + duration);
            d.putInt(SpellStates.ICE_SHROUD_FROST, property(DefaultProperties.EFFECT_DURATION));
            server.sendParticles(ParticleTypes.SNOWFLAKE, ctx.caster().getX(), ctx.caster().getY() + 1, ctx.caster().getZ(), 60, 0.5, 0.8, 0.5, 0.02);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.ICE, SpellTypes.DEFENCE, SpellAction.POINT_UP, 40, 0, 250)
                .add(DefaultProperties.DURATION, 600)
                .add(DefaultProperties.EFFECT_DURATION, 100)
                .build();
    }
}
