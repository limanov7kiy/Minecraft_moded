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

/// Ледяная статуя: цель вмерзает в лёд на 20 секунд (паралич + обморожение Redux, ледяная оболочка вокруг).
public class IceStatue extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        LivingEntity target = Targeting.lookTarget(ctx.caster(), range);
        if (target == null || !AllyDesignation.isValidTarget(ctx.caster(), target)) return false;
        if (ctx.world() instanceof ServerLevel server) {
            int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            target.addEffect(new MobEffectInstance(com.binaris.wizardry.setup.registries.EBMobEffects.PARALYSIS.get(), duration, 0));
            target.addEffect(new MobEffectInstance(com.binaris.wizardry.setup.registries.EBMobEffects.FROST.get(), duration, 2));
            target.setDeltaMovement(Vec3.ZERO);
            BlockPos base = target.blockPosition();
            int h = (int) Math.ceil(target.getBbHeight());
            for (int dy = 0; dy <= h; dy++)
                for (Direction dir : Direction.Plane.HORIZONTAL)
                    TempBlocks.place(server, base.above(dy).relative(dir), Blocks.ICE.defaultBlockState(), duration);
            TempBlocks.place(server, base.above(h + 1), Blocks.ICE.defaultBlockState(), duration);
            server.sendParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getY() + 1, target.getZ(), 40, 0.4, 0.6, 0.4, 0.02);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.ICE, SpellTypes.ATTACK, SpellAction.POINT, 15, 0, 40)
                .add(DefaultProperties.RANGE, 10f)
                .add(DefaultProperties.EFFECT_DURATION, 400)
                .build();
    }
}
