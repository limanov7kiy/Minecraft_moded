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

/// Сгусток пламени: урон и поджог; убитая цель рассыпается горящими углями, поджигающими всё вокруг.
public class Disintegration extends com.binaris.wizardry.content.spell.abstr.RaySpell {
    private static final SpellProperty<Integer> BURN = SpellProperty.intProperty("burn_duration", 10);

    public Disintegration() {
        this.particleSpacing(0.3);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, net.minecraft.world.phys.EntityHitResult hit, Vec3 origin) {
        if (!(hit.getEntity() instanceof LivingEntity target)) return false;
        if (ctx.caster() != null && !AllyDesignation.isValidTarget(ctx.caster(), target)) return false;
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        float damage = ctx.modifiers().get(SpellModifiers.POTENCY, property(DefaultProperties.DAMAGE));
        target.setSecondsOnFire(property(BURN));
        target.hurt(ctx.caster() != null ? MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.FIRE)
                : target.damageSources().onFire(), damage);
        if (!target.isAlive()) {
            server.sendParticles(ParticleTypes.FLAME, target.getX(), target.getY() + 0.5, target.getZ(), 60, 0.5, 0.6, 0.5, 0.08);
            server.sendParticles(ParticleTypes.LAVA, target.getX(), target.getY() + 0.5, target.getZ(), 12, 0.4, 0.4, 0.4, 0);
            for (LivingEntity e : EntityUtil.getLivingWithinRadius(2.5, target.getX(), target.getY(), target.getZ(), server))
                if (e != ctx.caster() && AllyDesignation.isValidTarget(ctx.caster(), e)) e.setSecondsOnFire(property(BURN));
        }
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
        ctx.world().addParticle(ParticleTypes.FLAME, x, y, z, vx, vy, vz);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.FIRE, SpellTypes.ATTACK, SpellAction.POINT, 35, 0, 40)
                .add(DefaultProperties.RANGE, 10f)
                .add(DefaultProperties.DAMAGE, 8f)
                .add(BURN)
                .build();
    }
}
