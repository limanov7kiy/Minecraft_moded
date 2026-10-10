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

/// Усиливающее присутствие: заклинатель и союзники рядом на 45 секунд получают +15% к силе заклинаний.
public class EmpoweringPresence extends Spell {
    private static final SpellProperty<Float> POTENCY = SpellProperty.floatProperty("potency_per_level", 0.15f);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (ctx.world() instanceof ServerLevel server) {
            float radius = ctx.modifiers().get(SpellModifiers.BLAST, property(DefaultProperties.EFFECT_RADIUS));
            int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            java.util.Set<LivingEntity> targets = new java.util.LinkedHashSet<>();
            targets.add(caster);   // заклинатель — всегда, даже если поиск по миру его не вернёт
            for (LivingEntity e : EntityUtil.getLivingWithinRadius(radius, caster.getX(), caster.getY(), caster.getZ(), server))
                if (AllyDesignation.isAllied(caster, e)) targets.add(e);
            for (LivingEntity e : targets) {
                var attr = e.getAttribute(com.binaris.wizardry.setup.registries.EBAttributes.CAST_POTENCY.get());
                if (attr == null) continue;
                attr.removeModifier(SpellStates.EMPOWER_MODIFIER);
                attr.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(SpellStates.EMPOWER_MODIFIER,
                        "Empowering presence", property(POTENCY), net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_TOTAL));
                SpellStates.data(e).putLong(SpellStates.EMPOWER_UNTIL, server.getGameTime() + duration);
                server.sendParticles(ParticleTypes.ENCHANT, e.getX(), e.getY() + 1, e.getZ(), 30, 0.4, 0.6, 0.4, 0.5);
            }
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.HEALING, SpellTypes.BUFF, SpellAction.POINT_UP, 30, 20, 60)
                .add(DefaultProperties.EFFECT_RADIUS, 5)
                .add(DefaultProperties.EFFECT_DURATION, 900)
                .add(POTENCY)
                .build();
    }
}
