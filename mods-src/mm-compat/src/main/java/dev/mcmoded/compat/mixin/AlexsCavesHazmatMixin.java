package dev.mcmoded.compat.mixin;

import dev.mcmoded.compat.RadiationSuitShielding;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Один защитный костюм (В70 п.1, поток 66): костюм Create Nuclear защищает и от облучения и кислоты Alex's Caves
 * (Alex's Caves — AlexModGuy). Мод считает надетые части своего Hazmat по точным id предметов в
 * {@code HazmatArmorItem.getWornAmount}; прибавляем части костюма Create Nuclear в тех слотах, где нет части Hazmat AC.
 * Hazmat Alex's Caves в сборке без крафта (KubeJS dedup_list.js), уже существующий продолжает работать.
 */
@Pseudo
@Mixin(targets = "com.github.alexmodguy.alexscaves.server.item.HazmatArmorItem", remap = false)
public abstract class AlexsCavesHazmatMixin {
    private static final EquipmentSlot[] MM_COMPAT$SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    @Inject(method = "getWornAmount", at = @At("RETURN"), cancellable = true, remap = false)
    private static void mm_compat$countCreateNuclearSuit(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        int extra = 0;
        for (EquipmentSlot slot : MM_COMPAT$SLOTS) {
            var item = entity.getItemBySlot(slot).getItem();
            if (RadiationSuitShielding.isSuit(item)) {
                extra++;
            }
        }
        if (extra > 0) {
            cir.setReturnValue(Math.min(4, cir.getReturnValue() + extra));
        }
    }
}
