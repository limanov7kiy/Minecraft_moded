package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/// Разряд молнии, парализующий цель (эффект паралича Redux). На игроков действует вдвое короче.
public class Paralysis extends RaySpell {
    public Paralysis() {
        this.particleSpacing(0.4);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult hit, Vec3 origin) {
        if (!(hit.getEntity() instanceof LivingEntity target)) return false;
        if (ctx.caster() != null && !AllyDesignation.isValidTarget(ctx.caster(), target)) return false;
        if (ctx.world().isClientSide) return true;
        float damage = ctx.modifiers().get(SpellModifiers.POTENCY, property(DefaultProperties.DAMAGE));
        target.hurt(ctx.caster() != null ? MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.SHOCK)
                : target.damageSources().magic(), damage);
        int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
        if (target instanceof Player) duration /= 2;
        target.addEffect(new MobEffectInstance(EBMobEffects.PARALYSIS.get(), duration, 0));
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult hit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ctx.world().addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, vx, vy, vz);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.LIGHTNING, SpellTypes.ALTERATION, SpellAction.POINT, 20, 10, 60)
                .add(DefaultProperties.RANGE, 10f)
                .add(DefaultProperties.DAMAGE, 4f)
                .add(DefaultProperties.EFFECT_DURATION, 100)
                .build();
    }
}
