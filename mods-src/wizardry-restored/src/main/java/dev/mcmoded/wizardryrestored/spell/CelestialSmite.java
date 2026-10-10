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

/// Небесная кара: столп света с неба в точку взгляда; урон и поджог в радиусе, нежити — двойной урон.
public class CelestialSmite extends Spell {
    private static final SpellProperty<Integer> BURN = SpellProperty.intProperty("burn_duration", 10);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        HitResult hit = RayTracer.standardBlockRayTrace(ctx.world(), caster, range, false, true, false);
        if (hit.getType() == HitResult.Type.MISS) return false;
        if (ctx.world() instanceof ServerLevel server) {
            Vec3 p = hit.getLocation();
            float radius = ctx.modifiers().get(SpellModifiers.BLAST, property(DefaultProperties.EFFECT_RADIUS));
            for (LivingEntity t : EntityUtil.getLivingWithinRadius(radius, p.x, p.y, p.z, server)) {
                if (!AllyDesignation.isValidTarget(caster, t)) continue;
                float dmg = property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY);
                if (t.isInvertedHealAndHarm()) dmg *= 2;
                t.hurt(MagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.MAGIC), dmg);
                t.setSecondsOnFire(property(BURN));
            }
            for (int y = 0; y < 30; y++)
                server.sendParticles(ParticleTypes.END_ROD, p.x, p.y + y, p.z, 3, 0.2, 0.2, 0.2, 0);
            server.sendParticles(ParticleTypes.FLASH, p.x, p.y + 0.5, p.z, 1, 0, 0, 0, 0);
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.HEALING, SpellTypes.ATTACK, SpellAction.POINT, 100, 25, 200)
                .add(DefaultProperties.RANGE, 40f)
                .add(DefaultProperties.EFFECT_RADIUS, 3)
                .add(DefaultProperties.DAMAGE, 8f)
                .add(BURN)
                .build();
    }
}
