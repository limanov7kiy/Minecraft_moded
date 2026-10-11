/*
 * Ported from Galacticraft Legacy (micdoodle8, radfast, Team Galacticraft):
 * micdoodle8/mods/galacticraft/core/items/ItemKey.java. See CREDITS.md.
 */
package dev.mcmoded.gcrevival.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.List;

/** Ключ от сундука сокровищ своего тира; выпадает из босса данжа. */
public class KeyItem extends Item {
    private final int tier;

    public KeyItem(int tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.gcrevival.key.tooltip", this.tier));
    }
}
