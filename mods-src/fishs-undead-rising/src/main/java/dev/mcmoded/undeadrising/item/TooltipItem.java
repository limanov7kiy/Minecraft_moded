package dev.mcmoded.undeadrising.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/// Предмет с одной строкой подсказки `tooltip.mod_lavacow.<id>` (в оригинале — `FURItem` с Tooltip = 2).
/// Оригинал: Fish's Undead Rising (Fish), `item/FURItem`.
public class TooltipItem extends Item {
    public TooltipItem(Item.Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip." + this.getDescriptionId().substring("item.".length())));
    }
}
