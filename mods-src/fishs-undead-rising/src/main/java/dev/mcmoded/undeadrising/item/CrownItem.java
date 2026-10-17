package dev.mcmoded.undeadrising.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/// Корона короля. Тип 0 — запятнанная (добыча Osvermis), только подсказка. Тип 1 (проклятая корона, призыв
/// Короля-скелета) в порт не входит. Оригинал: Fish's Undead Rising (Fish), `item/CrownItem`.
public class CrownItem extends Item {
    private final int type;

    public CrownItem(Item.Properties props, int type) {
        super(props);
        this.type = type;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String key = "tooltip.mod_lavacow.kings_crown." + this.type;
        tooltip.add(Component.translatable(key));
        tooltip.add(Component.empty());
        for (int i = 0; i < 3; i++) tooltip.add(Component.translatable(key + ".desc" + i).withStyle(ChatFormatting.ITALIC));
    }
}
