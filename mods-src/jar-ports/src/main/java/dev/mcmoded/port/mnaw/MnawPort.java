package dev.mcmoded.port.mnaw;

import com.mna.api.spells.base.ISpellDefinition;
import com.mna.items.sorcery.MagicStaff;
import com.mna.spells.crafting.SpellRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Порт M&A Witchcraft (nivthefox, GPLv3) под Mana and Artifice 3.1.11: то, что убрали из API M&A. */
public final class MnawPort {
    private MnawPort() {}

    /** Бывший ItemStaff.getSpell(stack, player): заклинание, записанное в посох. */
    public static ISpellDefinition getSpell(MagicStaff staff, ItemStack stack, Player player) {
        return SpellRecipe.fromNBT(staff.getSpellCompound(stack, player));
    }
}
