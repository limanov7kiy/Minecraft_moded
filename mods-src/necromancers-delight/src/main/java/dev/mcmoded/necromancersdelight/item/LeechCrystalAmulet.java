package dev.mcmoded.necromancersdelight.item;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.integration.curios.CuriosArtifactItem;
import com.binaris.wizardry.setup.registries.Elements;
import dev.mcmoded.necromancersdelight.entity.ElementalLeech;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

/// Leech Crystal Amulet (оригинал — `ItemLeechCrystalAmulet`, WinDanesz): в Arcane Workbench стихийный кристалл
/// (`magic_crystal_<стихия>`) привязывает к амулету стихию (NBT `element`), и Summon Elemental Leech у его владельца
/// всегда призывает пиявку этой стихии. Строка `desc2` подсказки показывает привязанную стихию.
public class LeechCrystalAmulet extends CuriosArtifactItem implements IWorkbenchItem {
    public LeechCrystalAmulet(Rarity rarity) {
        super(rarity, new IArtifactEffect() {});
    }

    public static Element getElement(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("element")) {
            String name = stack.getTag().getString("element");
            for (var e : ElementalLeech.ELEMENTS) if (e.get().getLocation().getPath().equals(name)) return e.get();
        }
        return Elements.MAGIC;
    }

    public static ItemStack setElement(ItemStack stack, Element element) {
        stack.getOrCreateTag().putString("element", element.getLocation().getPath());
        return stack;
    }

    /// Стихия надетого амулета или `null`.
    @Nullable
    public static Element equippedElement(Player player) {
        for (ItemStack s : ArtifactChannel.getEquippedArtifacts(player)) if (s.getItem() instanceof LeechCrystalAmulet) return getElement(s);
        return null;
    }

    /// Стихия кристалла Redux по id (`ebwizardry:magic_crystal_fire` → огонь), иначе `null`.
    @Nullable
    static Element crystalElement(ItemStack crystal) {
        var id = ForgeRegistries.ITEMS.getKey(crystal.getItem());
        if (id == null || !id.getNamespace().equals("ebwizardry") || !id.getPath().startsWith("magic_crystal_")) return null;
        String name = id.getPath().substring("magic_crystal_".length());
        for (var e : ElementalLeech.ELEMENTS) if (e.get().getLocation().getPath().equals(name)) return e.get();
        return null;
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        if (crystals.getItem().isEmpty()) return false;
        Element element = crystalElement(crystals.getItem());
        if (element == null || element == Elements.MAGIC || getElement(centre.getItem()) == element) return false;
        centre.set(setElement(centre.getItem().copy(), element));
        crystals.remove(1);
        return true;
    }

    @Override
    public boolean showTooltip(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.necromancersdelight.amulet_leech_crystal.desc2", getElement(stack).getDescriptionFormatted()));
    }
}
