package dev.mcmoded.compat.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Фон сервера (поток 33): Torches Become Sunlight строит «мегаструктуры» (Санкта, офис Родса и др.) в тике сервера по бюджету на тик.
 * Режим normal — 10240 блоков, 24 света и 96 подгрузок чанков за ТИК; на слабом железе это тики по 100+ мс (микрофризы).
 * Пока среднее время тика сервера выше 35 мс, бюджеты урезаются вчетверо (минимум 1024 / 4 / 8), как только тики снова быстрые — полные.
 * Геймплей: мегаструктуры в новых местах дорисовываются медленнее только когда сервер и так перегружен.
 */
@Pseudo
@Mixin(targets = "com.freefish.torchesbecomesunlight.server.gameframework.mega.MegaStructureGenerationSpeed", remap = false)
public abstract class TorchesMegaStructureLoadMixin {
    private static boolean mm_compat$busy() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server != null && server.getAverageTickTime() > 35.0F;
    }

    @Inject(method = "getManualBlockBudget", at = @At("RETURN"), cancellable = true, remap = false)
    private void mm_compat$blocks(CallbackInfoReturnable<Integer> cir) {
        if (mm_compat$busy()) cir.setReturnValue(Math.max(1024, cir.getReturnValue() / 4));
    }

    @Inject(method = "getManualLightBudget", at = @At("RETURN"), cancellable = true, remap = false)
    private void mm_compat$light(CallbackInfoReturnable<Integer> cir) {
        if (mm_compat$busy()) cir.setReturnValue(Math.max(4, cir.getReturnValue() / 4));
    }

    @Inject(method = "getManualChunkPreloads", at = @At("RETURN"), cancellable = true, remap = false)
    private void mm_compat$preloads(CallbackInfoReturnable<Integer> cir) {
        if (mm_compat$busy()) cir.setReturnValue(Math.max(8, cir.getReturnValue() / 4));
    }
}
