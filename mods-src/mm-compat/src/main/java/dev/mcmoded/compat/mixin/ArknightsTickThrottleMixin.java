package dev.mcmoded.compat.mixin;

import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Фон сервера (поток 33): Arknights Weapon (MCreator) вешает на LivingTickEvent процедуры Fire2Procedure и Apoptoic3Procedure,
 * которые каждый тик для КАЖДОГО живого существа берут из реестра эффект и читают счёт scoreboard (профиль JFR: ≈4,6 % + 1,3 % тика сервера).
 * Они лишь поддерживают статус-эффекты длительностью 20 тиков (Fire2: 20 и 300, Apoptoic3: 20 и 100) — пропуск 3 тиков из 4 их не ломает:
 * эффект переприменяется раньше, чем истечёт, а снятие эффекта запаздывает не более чем на 3 тика (0,15 с).
 */
@Pseudo
@Mixin(targets = {"net.mcreator.arknightsweaopn.procedures.Fire2Procedure",
                  "net.mcreator.arknightsweaopn.procedures.Apoptoic3Procedure"}, remap = false)
public abstract class ArknightsTickThrottleMixin {
    @Inject(method = "onEntityTick", at = @At("HEAD"), cancellable = true, remap = false)
    private static void mm_compat$every4th(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if ((event.getEntity().tickCount & 3) != 0) ci.cancel();
    }
}
