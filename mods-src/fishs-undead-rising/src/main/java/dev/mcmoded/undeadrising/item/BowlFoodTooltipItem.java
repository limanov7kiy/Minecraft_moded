package dev.mcmoded.undeadrising.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/// Блюдо в миске с подсказкой: после еды остаётся миска (кормление прирученных мобов тоже возвращает миску —
/// см. `FURTameableEntity#mobInteract`). Оригинал: Fish's Undead Rising (Fish), `item/NetherStewItem`.
public class BowlFoodTooltipItem extends TooltipItem {
    public BowlFoodTooltipItem(Properties props) {
        super(props);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack rest = super.finishUsingItem(stack, level, entity);
        if (entity instanceof Player player && player.getAbilities().instabuild) return rest;
        if (rest.isEmpty()) return new ItemStack(Items.BOWL);
        if (entity instanceof Player player && !player.getInventory().add(new ItemStack(Items.BOWL))) player.drop(new ItemStack(Items.BOWL), false);
        return rest;
    }
}
