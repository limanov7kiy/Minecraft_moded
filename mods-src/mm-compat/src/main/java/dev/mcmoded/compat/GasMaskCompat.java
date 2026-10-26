package dev.mcmoded.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Predicate;

/// Один противогаз (В70 п.5, поток 66): противогаз Spore защищает и от газовых снарядов Create Big Cannons.
/// У CBC есть открытый список проверок «надета рабочая маска» — GasMaskItem.addIsWearingPredicate; добавляем туда
/// маску Spore на голове. Противогаз CBC в сборке без крафта (KubeJS dedup_list.js). CBC при компиляции не нужен — рефлексия.
public final class GasMaskCompat {
    private static final String CBC_MASK = "rbasamoyai.createbigcannons.equipment.gas_mask.GasMaskItem";

    private GasMaskCompat() {
    }

    static void onSetup(FMLCommonSetupEvent event) {
        if (!ModList.get().isLoaded("createbigcannons") || !ModList.get().isLoaded("spore")) {
            return;
        }
        event.enqueueWork(() -> {
            Predicate<LivingEntity> sporeMask = e -> "spore:gas_mask".equals(
                    String.valueOf(BuiltInRegistries.ITEM.getKey(e.getItemBySlot(EquipmentSlot.HEAD).getItem())));
            try {
                Class.forName(CBC_MASK).getMethod("addIsWearingPredicate", Predicate.class).invoke(null, sporeMask);
            } catch (ReflectiveOperationException ex) {
                org.slf4j.LoggerFactory.getLogger(MmCompat.MOD_ID).warn("CBC: не удалось зарегистрировать противогаз Spore", ex);
            }
        });
    }
}
