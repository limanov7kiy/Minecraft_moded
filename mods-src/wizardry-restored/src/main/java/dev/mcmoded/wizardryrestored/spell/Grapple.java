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

/// Захват: лоза притягивает существо к заклинателю или подтягивает заклинателя к блоку.
public class Grapple extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        LivingEntity target = Targeting.lookTarget(caster, range);
        Vec3 from = caster.getEyePosition(1);
        if (target != null) {
            Vec3 pull = from.subtract(target.position()).normalize().scale(Math.min(2.0, 0.25 * target.distanceTo(caster)));
            target.setDeltaMovement(pull.add(0, 0.3, 0));
            target.hurtMarked = true;
            line(ctx.world(), from, target.getEyePosition(1));
        } else {
            HitResult hit = RayTracer.standardBlockRayTrace(ctx.world(), caster, range, false, true, false);
            if (hit.getType() != HitResult.Type.BLOCK) return false;
            Vec3 pull = hit.getLocation().subtract(caster.position()).normalize().scale(Math.min(2.2, 0.22 * hit.getLocation().distanceTo(caster.position())));
            caster.setDeltaMovement(pull.add(0, 0.25, 0));
            caster.hurtMarked = true;
            caster.fallDistance = 0;
            line(ctx.world(), from, hit.getLocation());
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    private static void line(Level world, Vec3 a, Vec3 b) {
        if (!(world instanceof ServerLevel server)) return;
        Vec3 step = b.subtract(a);
        int n = (int) Math.max(4, step.length() * 2);
        for (int i = 0; i <= n; i++) {
            Vec3 p = a.add(step.scale(i / (double) n));
            server.sendParticles(ParticleTypes.COMPOSTER, p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellTypes.UTILITY, SpellAction.GRAPPLE, 5, 0, 10)
                .add(DefaultProperties.RANGE, 20f)
                .build();
    }
}
