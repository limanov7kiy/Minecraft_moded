package dev.mcmoded.compat;

import mekanism.api.radiation.capability.IRadiationShielding;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.item.gear.ItemHazmatSuitArmor;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// Один защитный костюм на сборку (решение владельца В54, поток 66): антирадиационный костюм Create Nuclear
/// защищает и от радиации Mekanism, а Hazmat Mekanism убран из крафта (KubeJS `dedup.js`, список — `startup_scripts/dedup_list.js`).
///
/// От своей радиации костюм Create Nuclear защищает сам (атрибут `createnuclear:irradiated_resistance`).
/// Mekanism же считает защиту по capability `IRadiationShielding` на броне — вешаем её на все части костюма
/// Create Nuclear с теми же долями, что у Hazmat: шлем 0,25, нагрудник 0,4, поножи 0,2,
/// ботинки 0,15 — полный костюм даёт 100 %. Класс костюма проверяем по имени: Create Nuclear при компиляции не нужен.
public final class RadiationSuitShielding {
    private static final ResourceLocation KEY = new ResourceLocation(MmCompat.MOD_ID, "cn_suit_radiation_shielding");
    private static final String CN_SUIT = "net.nuclearteam.createnuclear.content.equipment.armor.AntiRadiationArmorItem";
    private static final Map<Class<?>, Boolean> IS_SUIT = new ConcurrentHashMap<>();

    private RadiationSuitShielding() {
    }

    static void register() {
        if (ModList.get().isLoaded("mekanism") && ModList.get().isLoaded("createnuclear")) {
            MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, RadiationSuitShielding::onAttach);
        }
    }

    private static void onAttach(AttachCapabilitiesEvent<ItemStack> event) {
        if (!(event.getObject().getItem() instanceof ArmorItem armor) || !isSuit(armor)) {
            return;
        }
        double shielding = ItemHazmatSuitArmor.getShieldingByArmor(armor.getType());
        LazyOptional<IRadiationShielding> handler = LazyOptional.of(() -> () -> shielding);
        event.addCapability(KEY, new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return Capabilities.RADIATION_SHIELDING.orEmpty(cap, handler);
            }
        });
    }

    private static boolean isSuit(Item item) {
        return IS_SUIT.computeIfAbsent(item.getClass(), c -> {
            for (Class<?> k = c; k != null; k = k.getSuperclass()) {
                if (k.getName().equals(CN_SUIT)) {
                    return true;
                }
            }
            return false;
        });
    }
}
