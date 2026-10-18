package dev.mcmoded.compat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Фон сервера (поток 33): MegaStructureManager.tick, пока идёт хоть одна задача, КАЖДЫЙ тик вызывает syncState() — заново собирает NBT
 * всех задач и карты якорей (≈40 % времени MegaStructureEventHandle.onServerTick в профиле JFR, заодно много мусора для GC).
 * Состояние нужно только для сохранения мира, поэтому пишем его не чаще раза в секунду; отложенный вызов дописывается в начале следующего
 * тика, как только секунда прошла, и при остановке сервера. Потеря при аварийном вылете — не больше секунды прогресса постройки.
 */
@Pseudo
@Mixin(targets = "com.freefish.torchesbecomesunlight.server.gameframework.mega.MegaStructureManager", remap = false)
public abstract class TorchesMegaStructureSyncMixin {
    @Unique private static long mm_compat$last;
    @Unique private static boolean mm_compat$pending;

    @Shadow(remap = false) private void syncState() { throw new AssertionError(); }

    @Inject(method = "syncState", at = @At("HEAD"), cancellable = true, remap = false)
    private void mm_compat$throttle(CallbackInfo ci) {
        long now = System.nanoTime();
        if (now - mm_compat$last < 1_000_000_000L && mm_compat$last != 0) {
            mm_compat$pending = true;
            ci.cancel();
        } else {
            mm_compat$last = now;
            mm_compat$pending = false;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void mm_compat$flush(CallbackInfo ci) {
        if (mm_compat$pending && System.nanoTime() - mm_compat$last >= 1_000_000_000L) syncState();
    }

    @Inject(method = "onServerStopped", at = @At("HEAD"), remap = false)
    private void mm_compat$flushOnStop(CallbackInfo ci) {
        mm_compat$last = 0;
        if (mm_compat$pending) syncState();
        mm_compat$last = 0;
        mm_compat$pending = false;
    }
}
