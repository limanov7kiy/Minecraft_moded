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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Магический свет: невидимый источник света (ванильный блок света) на 30 секунд перед заклинателем.
public class Light extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        boolean forever = dev.mcmoded.wizardryrestored.util.Artifacts.has(ctx.caster(), com.binaris.wizardry.setup.registries.EBItems.CHARM_LIGHT);
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        HitResult hit = RayTracer.standardBlockRayTrace(server, ctx.caster(), range, false, true, false);
        BlockPos pos = hit instanceof BlockHitResult b && hit.getType() == HitResult.Type.BLOCK
                ? b.getBlockPos().relative(b.getDirection())
                : BlockPos.containing(ctx.caster().getEyePosition(1).add(ctx.caster().getLookAngle().scale(range)));
        int duration = (int) (property(DefaultProperties.DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
        // Вечный фонарь Элевинии: свет постоянный; Shift — погасить свой свет вокруг точки взгляда
        if (forever && ctx.caster().isShiftKeyDown()) {
            int removed = 0;
            for (BlockPos q : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2)))
                if (server.getBlockState(q).is(Blocks.LIGHT)) {
                    server.removeBlock(q, false);
                    removed++;
                }
            return removed > 0;
        }
        if (forever) {
            BlockState here = server.getBlockState(pos);
            if (!here.isAir() && !here.canBeReplaced() || !here.getFluidState().isEmpty()) return false;
            server.setBlockAndUpdate(pos, Blocks.LIGHT.defaultBlockState());
        } else if (!TempBlocks.place(server, pos, Blocks.LIGHT.defaultBlockState(), duration)) return false;
        server.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15, 0.2, 0.2, 0.2, 0.02);
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.SORCERY, SpellTypes.UTILITY, SpellAction.POINT, 5, 0, 15)
                .add(DefaultProperties.RANGE, 4f)
                .add(DefaultProperties.DURATION, 600)
                .build();
    }
}
