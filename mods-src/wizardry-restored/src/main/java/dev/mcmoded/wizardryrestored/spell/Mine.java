package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;

/// Ломает блок, на который смотрит заклинатель. Сила (potency) позволяет ломать более твёрдые блоки.
/// Уважает защиту территорий: перед разрушением шлёт обычное событие ломания блока игроком.
public class Mine extends Spell {
    /// Твёрдость, которую ломает заклинание без усилений (камень — 1.5, руда — 3, обсидиан — 50).
    private static final float BASE_HARDNESS = 3.0f;

    @Override
    public boolean cast(PlayerCastContext ctx) {
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        HitResult hit = RayTracer.standardBlockRayTrace(ctx.world(), ctx.caster(), range, false, true, false);
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) return false;
        BlockPos pos = blockHit.getBlockPos();
        BlockState state = ctx.world().getBlockState(pos);
        float hardness = state.getDestroySpeed(ctx.world(), pos);
        float limit = BASE_HARDNESS * ctx.modifiers().get(SpellModifiers.POTENCY);
        if (hardness < 0 || hardness > limit || state.isAir()) return false;

        if (!ctx.world().isClientSide) {
            if (MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(ctx.world(), pos, state, ctx.caster()))) return false;
            // Лунный камень: блок всегда выпадает сам (как с шёлковым касанием)
            if (dev.mcmoded.wizardryrestored.util.Artifacts.has(ctx.caster(), com.binaris.wizardry.setup.registries.EBItems.CHARM_SILK_TOUCH) && state.getBlock().asItem() != net.minecraft.world.item.Items.AIR) {
                ctx.world().destroyBlock(pos, false, ctx.caster());
                net.minecraft.world.level.block.Block.popResource(ctx.world(), pos, new net.minecraft.world.item.ItemStack(state.getBlock()));
            } else ctx.world().destroyBlock(pos, true, ctx.caster());
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.EARTH, SpellTypes.UTILITY, SpellAction.POINT, 5, 0, 5)
                .add(DefaultProperties.RANGE, 8f)
                .build();
    }
}
