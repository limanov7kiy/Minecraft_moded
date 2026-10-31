package dev.mcmoded.autoupdate;

import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

/**
 * Серверное автообновление сборки с GitHub (docs/hosting.md, «Автообновление сервера с git»).
 * На выделенном сервере: после {@link ServerStoppedEvent} (мир уже сохранён, игроки отключены) запускает
 * packwiz-installer-bootstrap в корне сервера — к следующему старту mods/, config/, kubejs/ свежие.
 * Если прошлое обновление оборвалось (хостинг убил процесс по таймауту), на старте повторяет его и
 * завершает сервер с кодом 0, чтобы не поднимать мир на неполном наборе модов. На клиенте ничего не делает.
 */
@Mod("mm_autoupdate")
public final class MmAutoupdate {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG = "[mm_autoupdate] ";
    private static boolean ran;

    public MmAutoupdate() {
        if (FMLEnvironment.dist != Dist.DEDICATED_SERVER) {
            LOGGER.info(TAG + "не выделенный сервер — мод бездействует / not a dedicated server, idle");
            return;
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AutoupdateConfig.SPEC, "mm-autoupdate.toml");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(MmAutoupdate::onServerSetup);
        MinecraftForge.EVENT_BUS.addListener(MmAutoupdate::onServerStopped);
    }

    private static final Updater.Log LOG = new Updater.Log() {
        @Override public void info(String s) { LOGGER.info(TAG + s); }
        @Override public void warn(String s) { LOGGER.warn(TAG + s); }
        @Override public void error(String s) { LOGGER.error(TAG + s); }
    };

    /** Ещё до мира и до регистрации shutdown-хука сервера: здесь можно безопасно выйти из процесса. */
    private static void onServerSetup(FMLDedicatedServerSetupEvent e) {
        Path root = FMLPaths.GAMEDIR.get();
        if (!AutoupdateConfig.ENABLED.get()) {
            LOGGER.info(TAG + "выключено в config/mm-autoupdate.toml (enabled = false) / disabled");
            return;
        }
        LOGGER.info(TAG + "обновление при остановке сервера включено: " + AutoupdateConfig.PACK_URL.get());
        if (!Updater.markerExists(root)) return;
        LOGGER.warn(TAG + "прошлое обновление не завершилось (есть " + Updater.MARKER + "): сервер мог быть остановлен хостингом "
            + "до конца обновления, mods/ может быть неполным / previous update did not finish");
        if (!AutoupdateConfig.RETRY_ON_START.get()) {
            LOGGER.warn(TAG + "retry_on_start = false — продолжаю запуск как есть; проверьте mods/ и " + Updater.STATUS);
            return;
        }
        Updater.Result r = Updater.run(root, AutoupdateConfig.settings(), LOG, "retry");
        if (r == Updater.Result.UPDATED) {
            ran = true;
            LOGGER.warn(TAG + "обновление доделано; сервер завершает работу, чтобы загрузить новые моды — ЗАПУСТИТЕ ЕГО СНОВА "
                + "/ update completed, exiting with code 0: START THE SERVER AGAIN");
            System.exit(0);
        } else {
            LOGGER.error(TAG + "повторить обновление не удалось (" + r + "); продолжаю запуск с тем, что есть в mods/ — "
                + "проверьте лог и " + Updater.STATUS + " / retry failed, starting anyway");
        }
    }

    private static void onServerStopped(ServerStoppedEvent e) {
        if (ran) return;
        ran = true;
        if (!AutoupdateConfig.ENABLED.get()) return;
        Updater.run(FMLPaths.GAMEDIR.get(), AutoupdateConfig.settings(), LOG, "stop");
    }
}
