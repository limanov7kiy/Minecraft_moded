package dev.mcmoded.compat.mixin;

import com.mojang.logging.LogUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Падение сервера с Chunky (popcraft, GPL-3.0) и Distant Horizons 3.3.3 (James Seibel, LGPL-3.0), поток 64:
 * DH на первой загрузке чанка (ServerApi.serverChunkLoadEvent → AbstractChunkyAccessor.tryRunFirstTimeSetup) один раз
 * подписывается на события Chunky через ChunkyProvider.get(). На Forge Chunky создаётся позже — при запуске сервера,
 * а первые чанки грузятся раньше; ChunkyProvider.get() бросает IllegalStateException «Chunky is not loaded» прямо
 * в ChunkEvent.Load, и сервер падает («Exception during promotion of chunk to FULL status»).
 * Пока Chunky не готов, пропускаем настройку, не ставя флаг listenerBound: DH попробует снова на следующем чанке.
 * Подписка DH нужна только чтобы приостановить собственную генерацию DH на время работы Chunky (у нас она и так выключена).
 */
@Pseudo
@Mixin(targets = "com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.AbstractChunkyAccessor", remap = false)
public abstract class DhChunkyBindMixin {
    @Shadow(remap = false) private boolean listenerBound;
    @Unique private static boolean mm_compat$logged;

    @Inject(method = "tryRunFirstTimeSetup", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void mm_compat$waitForChunky(CallbackInfo ci) {
        if (listenerBound || mm_compat$chunkyReady()) return;
        if (!mm_compat$logged) {
            mm_compat$logged = true;
            LogUtils.getLogger().info("mm_compat: Distant Horizons ждёт запуска Chunky перед подпиской на его события / DH waits for Chunky to load");
        }
        ci.cancel();
    }

    @Unique
    private static boolean mm_compat$chunkyReady() {
        try {
            return Class.forName("org.popcraft.chunky.ChunkyProvider").getMethod("get").invoke(null) != null;
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            return false;
        }
    }
}
