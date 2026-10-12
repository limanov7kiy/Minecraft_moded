package dev.mcmoded.necromancersdelight.spell;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.necromancersdelight.NecromancersDelight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/// Glowshroom (оригинал — WinDanesz): луч ставит временный светящийся гриб на 30 с (`ITEM_LIFETIME` 600 т.);
/// с Shift по уже стоящему — убирает его.
public class Glowshroom extends RaySpell {
    public Glowshroom() {
        this.ignoreLivingEntities(true);
        this.particleSpacing(0.5);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult hit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult hit, Vec3 origin) {
        var level = ctx.world();
        var block = NecromancersDelight.TEMP_GLOWSHROOM.get();
        if (ctx.caster() != null && ctx.caster().isShiftKeyDown() && level.getBlockState(hit.getBlockPos()).is(block)) {
            if (!level.isClientSide) level.removeBlock(hit.getBlockPos(), false);
            return true;
        }
        BlockPos pos = hit.getBlockPos().relative(hit.getDirection());
        if (!level.getBlockState(pos).canBeReplaced() || !level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), net.minecraft.core.Direction.UP)) return false;
        if (!level.isClientSide) {
            level.setBlock(pos, block.defaultBlockState(), 3);
            level.scheduleTick(pos, block, (int) (property(DefaultProperties.ITEM_LIFETIME) * ctx.modifiers().get(SpellModifiers.DURATION)));
        }
        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ctx.world().addParticle(ParticleTypes.GLOW, x, y, z, vx, vy, vz);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.EARTH, SpellTypes.UTILITY, SpellAction.POINT, 10, 0, 40)
                .add(DefaultProperties.RANGE, 8f)
                .add(DefaultProperties.ITEM_LIFETIME, 600)
                .build();
    }
}
