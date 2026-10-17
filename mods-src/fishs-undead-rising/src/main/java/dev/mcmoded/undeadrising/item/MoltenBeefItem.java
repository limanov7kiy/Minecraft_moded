package dev.mcmoded.undeadrising.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;

/// Раскалённое мясо (добыча саламандры): поджигает как огниво, но тратится целиком; топливо на 16 переплавок.
/// Оригинал: Fish's Undead Rising (Fish), `item/MoltenBeefItem`.
public class MoltenBeefItem extends FlintAndSteelItem {
    public MoltenBeefItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        InteractionResult result = super.useOn(ctx);
        if (result != InteractionResult.FAIL && result != InteractionResult.PASS && ctx.getPlayer() != null
                && !ctx.getPlayer().getAbilities().instabuild) {
            ctx.getItemInHand().shrink(1);
        }
        return result;
    }

    @Override
    public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return 3200;
    }
}
