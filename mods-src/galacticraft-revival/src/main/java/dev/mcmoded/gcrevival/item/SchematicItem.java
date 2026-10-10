/*
 * Schematics of Galacticraft (Legacy ItemSchematic / ItemSchematicTier2, micdoodle8, radfast, Team Galacticraft).
 * See CREDITS.md. In this pack a schematic is a progression item: recipes of the next tier require it
 * (and give it back), see docs/galacticraft.md.
 */
package dev.mcmoded.gcrevival.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.List;

public class SchematicItem extends Item {
    public SchematicItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }
}
