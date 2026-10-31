package dev.mcmoded.autoupdate;

import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

/**
 * Серверное автообновление сборки с GitHub (docs/hosting.md, «Автообновление сервера с git»). Два режима (mode в конфиге):
 * <ul>
 * <li>{@code on_start} (по умолчанию с 0.3.0): на {@link FMLDedicatedServerSetupEvent}, до загрузки мира, сверяет pack.toml по pack_url
 * с поставленным (packwiz.json); есть новая версия — packwiz-installer-bootstrap в корне сервера и выход с кодом 0 (панель хостинга
 * запускает сервер снова уже с новыми jar), ошибка — полный откат и запуск на прежней версии, нет изменений — обычный запуск;</li>
 * <li>{@code on_stop}: после {@link ServerStoppedEvent} (мир уже сохранён, игроки отключены) — к следующему старту сборка свежая.</li>
 * </ul>
 * С 0.4.0 можно задать окно update_window (в 0.4.0 по умолчанию ночь, с 0.5.0 по умолчанию пусто — без ограничения); вне окна — обычный запуск.
 * Повтор оборванного обновления (маркер) окна не ждёт: mods/ может быть неполным.
 * С 0.5.0 окна по умолчанию нет (обновление на каждом старте) и есть самоперезапуск пустого сервера (idle_restart): при 0 игроков
 * и новой сборке (в idle_restart_window, если задано) — обычная остановка с кодом 0, обновляет следующий старт
 * (расписание панели Millida спящий сервер не будит).
 * В обоих режимах: если прошлое обновление оборвалось (маркер), на старте повторяет его и завершает сервер с кодом 0, чтобы не
 * поднимать мир на неполном наборе модов. На клиенте ничего не делает.
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
        MinecraftForge.EVENT_BUS.addListener(MmAutoupdate::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(MmAutoupdate::onServerStopping);
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
        boolean onStart = onStartMode();
        LOGGER.info(TAG + (onStart ? "обновление на старте сервера (mode = on_start): " : "обновление при остановке сервера (mode = on_stop): ")
            + AutoupdateConfig.PACK_URL.get() + ", окно " + (AutoupdateConfig.UPDATE_WINDOW.get().isBlank() ? "— без ограничения"
            : AutoupdateConfig.UPDATE_WINDOW.get() + " " + AutoupdateConfig.UPDATE_WINDOW_TZ.get()));
        if (Updater.markerExists(root)) {
            retryInterrupted(root);
            return;
        }
        if (!onStart) return;
        Updater.StartResult r = Updater.onStart(root, AutoupdateConfig.settings(), LOG, AutoupdateConfig.START_COOLDOWN_MINUTES.get());
        if (r == Updater.StartResult.UPDATED) {
            ran = true;
            LOGGER.warn(TAG + "сборка обновлена до " + Updater.packVersion(root) + "; сервер завершает работу с кодом 0, чтобы загрузить "
                + "новые моды — панель хостинга запустит его снова (если нет — ЗАПУСТИТЕ СЕРВЕР ВРУЧНУЮ) "
                + "/ pack updated, exiting with code 0 so the panel restarts the server on the new mods");
            System.exit(0);
        }
        if (r == Updater.StartResult.FAILED) {
            LOGGER.error(TAG + "обновление на старте не удалось — сервер запускается на прежней версии " + Updater.packVersion(root)
                + ", причина — выше и в " + Updater.STATUS + " / update failed, starting the previous version");
        }
    }

    static boolean onStartMode() {
        return !AutoupdateConfig.MODE_ON_STOP.equals(AutoupdateConfig.MODE.get());
    }

    private static void retryInterrupted(Path root) {
        LOGGER.warn(TAG + "прошлое обновление не завершилось (есть " + Updater.MARKER + "): сервер мог быть остановлен хостингом "
            + "до конца обновления, mods/ может быть неполным / previous update did not finish");
        if (!AutoupdateConfig.RETRY_ON_START.get()) {
            LOGGER.warn(TAG + "retry_on_start = false — продолжаю запуск как есть; проверьте mods/ и " + Updater.STATUS);
            return;
        }
        Updater.Result r = Updater.run(root, AutoupdateConfig.settings(), LOG, "retry");
        if (r == Updater.Result.UPDATED) {
            ran = true;
            LOGGER.warn(TAG + "обновление доделано; сервер завершает работу с кодом 0, чтобы загрузить новые моды — панель хостинга "
                + "запустит его снова (если нет — ЗАПУСТИТЕ СЕРВЕР ВРУЧНУЮ) / update completed, exiting with code 0: START THE SERVER AGAIN");
            System.exit(0);
        } else {
            LOGGER.error(TAG + "повторить обновление не удалось (" + r + "); продолжаю запуск с тем, что есть в mods/ — "
                + "проверьте лог и " + Updater.STATUS + " / retry failed, starting anyway");
        }
    }

    private static ScheduledExecutorService idleTimer;
    private static volatile boolean idleStopping;

    /** Самоперезапуск пустого сервера: таймер в своём потоке (сеть не на главном), остановка — на главном потоке сервера. */
    private static void onServerStarted(ServerStartedEvent e) {
        if (!AutoupdateConfig.ENABLED.get() || !AutoupdateConfig.IDLE_RESTART.get()) return;
        Updater.Settings s = AutoupdateConfig.settings();
        if ("bad-window".equals(Updater.idleWindowBlock(s, ZonedDateTime.now()))) {
            LOGGER.error(TAG + "idle_restart_window = \"" + s.idleRestartWindow + "\" не разобрано (нужно «ЧЧ:ММ-ЧЧ:ММ» или пусто) — самоперезапуска не будет"
                + " / bad idle_restart_window, idle restart off");
            return;
        }
        int minutes = AutoupdateConfig.IDLE_CHECK_MINUTES.get();
        MinecraftServer server = e.getServer();
        LOGGER.info(TAG + "самоперезапуск пустого сервера: раз в " + minutes + " мин, при 0 игроков и новой сборке, "
            + (s.idleRestartWindow == null || s.idleRestartWindow.isBlank() ? "в любое время" : "только в окне " + s.idleRestartWindow + " " + s.updateWindowTz)
            + " (idle_restart = false — выключить) / idle restart check every " + minutes + " min");
        idleTimer = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "mm_autoupdate idle check");
            t.setDaemon(true);
            return t;
        });
        idleTimer.scheduleWithFixedDelay(() -> idleTick(server), minutes, minutes, TimeUnit.MINUTES);
    }

    private static void idleTick(MinecraftServer server) {
        try {
            if (idleStopping || !server.isRunning() || server.getPlayerCount() > 0) return;
            Path root = FMLPaths.GAMEDIR.get();
            Updater.Settings s = AutoupdateConfig.settings();
            Updater.IdleDecision d = Updater.idleCheck(root, s, ZonedDateTime.now());
            switch (d.result) {
                case OUTSIDE_WINDOW -> { return; }
                case GUARD -> LOGGER.info(TAG + "самоперезапуск: " + d.why);
                case CHECK_FAILED -> LOGGER.warn(TAG + "самоперезапуск: проверка pack.toml не удалась (" + d.why + ") — сервер не трогаю / check failed, not restarting");
                case UP_TO_DATE -> LOGGER.info(TAG + "самоперезапуск: сервер пуст, сборка актуальна (" + Updater.packVersion(root) + ", проверка "
                    + d.check.millis + " мс) — не останавливаю / up to date");
                case SAME_VERSION -> LOGGER.warn(TAG + "самоперезапуск: " + d.why);
                case RESTART -> server.execute(() -> idleStop(server, root, d.check));
            }
        } catch (RuntimeException ex) {
            LOGGER.error(TAG + "самоперезапуск: ошибка проверки — сервер не трогаю / idle check error", ex);
        }
    }

    /** На главном потоке: между проверкой и остановкой никто не войдёт; игрок успел войти — отмена. */
    private static void idleStop(MinecraftServer server, Path root, Updater.Check c) {
        if (idleStopping || !server.isRunning()) return;
        int online = server.getPlayerCount();
        if (online > 0) {
            LOGGER.info(TAG + "самоперезапуск отменён: пока проверял обновление, вошёл игрок (" + online + ") / player joined, cancelled");
            return;
        }
        if (Updater.idleWindowBlock(AutoupdateConfig.settings(), ZonedDateTime.now()) != null) return;
        idleStopping = true;
        LOGGER.warn(TAG + "самоперезапуск: сервер пуст, вышла новая сборка " + Updater.packVersion(root) + " → " + c.remoteVersion
            + " — сохраняю мир и останавливаю сервер (код выхода 0); панель хостинга запустит его снова, старт поставит обновление "
            + "/ idle restart: empty server, new pack, saving and stopping with exit code 0");
        Updater.markIdleRestart(root, c, LOG);
        server.saveEverything(false, false, true);
        server.halt(false);
    }

    private static void onServerStopping(ServerStoppingEvent e) {
        idleStopping = true;
        if (idleTimer != null) idleTimer.shutdownNow();
    }

    private static void onServerStopped(ServerStoppedEvent e) {
        if (ran) return;
        ran = true;
        if (!AutoupdateConfig.ENABLED.get() || onStartMode()) return;
        Updater.Settings s = AutoupdateConfig.settings();
        if (Updater.blockedByWindow(FMLPaths.GAMEDIR.get(), s, LOG, "stop")) return;
        Updater.run(FMLPaths.GAMEDIR.get(), s, LOG, "stop");
    }
}
