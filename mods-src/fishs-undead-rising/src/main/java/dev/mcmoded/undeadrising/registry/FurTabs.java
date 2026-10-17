package dev.mcmoded.undeadrising.registry;

import dev.mcmoded.undeadrising.UndeadRising;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/// Творческая вкладка мода (оригинал: Fish's Undead Rising (Fish), `misc/FURItemGroup`, ключ `itemGroup.mod_lavacow_items`).
/// Содержимое — все предметы из {@link FurItems#ITEMS}, отдельно ничего добавлять не нужно.
public final class FurTabs {
    private FurTabs() {}

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, UndeadRising.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("mod_lavacow", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.mod_lavacow_items"))
            .icon(() -> new ItemStack(FurItems.ACIDICHEART.get()))
            .displayItems((params, output) -> FurItems.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
            .build());
}
