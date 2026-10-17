package dev.mcmoded.necromancersdelight.spell;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.undeadrising.entity.MimicEntity;
import dev.mcmoded.undeadrising.registry.FurEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/// Carcinize (оригинал — `Carcinize`, WinDanesz): луч по сундуку тратит кристалл стихии Земли из инвентаря, ломает
/// сундук (содержимое выпадает) и на его месте появляется прирученный игроком мимикраб Fish's Undead Rising.
/// Только для игроков.
public class Carcinize extends RaySpell {
    public Carcinize() {
        this.ignoreLivingEntities(true);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult hit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult hit, Vec3 origin) {
        var level = ctx.world();
        BlockPos pos = hit.getBlockPos();
        if (!(level.getBlockState(pos).getBlock() instanceof ChestBlock)) {
            if (ctx.caster() instanceof Player p && !level.isClientSide) p.sendSystemMessage(Component.translatable("spell.necromancersdelight.carcinize.not_chest"));
            return false;
        }
        if (!(ctx.caster() instanceof Player player)) return false;
        ItemStack crystal = ItemStack.EMPTY;
        for (ItemStack s : player.getInventory().items) {
            if (s.is(EBItems.MAGIC_CRYSTAL_EARTH.get())) {
                crystal = s;
                break;
            }
        }
        if (crystal.isEmpty()) {
            if (!level.isClientSide) player.sendSystemMessage(Component.translatable("spell.necromancersdelight.carcinize.no_crystal"));
            return false;
        }
        if (!level.isClientSide) {
            if (!player.getAbilities().instabuild) crystal.shrink(1);
            level.destroyBlock(pos, true);
            MimicEntity mimic = FurEntities.MIMIC.get().create(level);
            if (mimic != null) {
                mimic.moveTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, player.getYRot() + 180, 0);
                mimic.tame(player);
                level.addFreshEntity(mimic);
            }
        }
        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.EARTH, SpellTypes.UTILITY, SpellAction.POINT, 150, 40, 200)
                .add(DefaultProperties.RANGE, 10f)
                .build();
    }
}
