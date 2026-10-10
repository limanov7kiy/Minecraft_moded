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

/// Замедление времени: все враги вокруг почти замирают, их снаряды теряют скорость.
public class SlowTime extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        float radius = ctx.modifiers().get(SpellModifiers.BLAST, property(DefaultProperties.EFFECT_RADIUS));
        int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
        for (LivingEntity t : EntityUtil.getLivingWithinRadius(radius, caster.getX(), caster.getY(), caster.getZ(), server)) {
            if (t == caster || !AllyDesignation.isValidTarget(caster, t)) continue;
            t.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 4));
            t.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, 3));
            t.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, 1));
        }
        server.getEntitiesOfClass(net.minecraft.world.entity.projectile.Projectile.class, caster.getBoundingBox().inflate(radius))
                .forEach(pr -> { if (pr.getOwner() != caster) { pr.setDeltaMovement(pr.getDeltaMovement().scale(0.1)); pr.hurtMarked = true; } });
        server.sendParticles(ParticleTypes.REVERSE_PORTAL, caster.getX(), caster.getY() + 1, caster.getZ(), 120, radius / 2, 1, radius / 2, 0.02);
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.SORCERY, SpellTypes.ALTERATION, SpellAction.POINT_UP, 100, 15, 200)
                .add(DefaultProperties.EFFECT_RADIUS, 8)
                .add(DefaultProperties.EFFECT_DURATION, 300)
                .build();
    }
}
