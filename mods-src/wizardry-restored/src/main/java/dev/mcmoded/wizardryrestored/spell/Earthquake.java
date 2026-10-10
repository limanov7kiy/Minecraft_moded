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

/// Землетрясение: все наземные существа вокруг подбрасываются, получают урон и оглушение; земля «вздыбливается» частицами.
public class Earthquake extends Spell {
    private static final float DAMAGE = 6f;

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (!caster.onGround()) return false;
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        float radius = ctx.modifiers().get(SpellModifiers.BLAST, property(DefaultProperties.EFFECT_RADIUS));
        for (LivingEntity t : EntityUtil.getLivingWithinRadius(radius, caster.getX(), caster.getY(), caster.getZ(), server)) {
            if (t == caster || !t.onGround() || !AllyDesignation.isValidTarget(caster, t)) continue;
            t.hurt(MagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.FORCE), DAMAGE * ctx.modifiers().get(SpellModifiers.POTENCY));
            if (!dev.mcmoded.wizardryrestored.util.Artifacts.anchored(t)) t.setDeltaMovement(t.getDeltaMovement().add(0, 0.9, 0));
            t.hurtMarked = true;
            t.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
        }
        for (int i = 0; i < 120; i++) {
            double a = server.random.nextDouble() * Math.PI * 2, r = server.random.nextDouble() * radius;
            BlockPos ground = BlockPos.containing(caster.getX() + Math.cos(a) * r, caster.getY() - 1, caster.getZ() + Math.sin(a) * r);
            server.sendParticles(new net.minecraft.core.particles.BlockParticleOption(ParticleTypes.BLOCK, server.getBlockState(ground)),
                    ground.getX() + 0.5, ground.getY() + 1.1, ground.getZ() + 0.5, 4, 0.3, 0.1, 0.3, 0.3);
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.EARTH, SpellTypes.ATTACK, SpellAction.POINT_DOWN, 75, 25, 250)
                .add(DefaultProperties.EFFECT_RADIUS, 8)
                .build();
    }
}
