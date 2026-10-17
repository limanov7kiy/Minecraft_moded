package dev.mcmoded.perf;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

/**
 * Подсказки о памяти (решение владельца В30): (а) в главном меню — если Xmx < 7 ГБ или > ~70 % физической ОЗУ;
 * (б) в игре — если занятость кучи ПОСЛЕ последней сборки мусора > 90 % около 30 с: «Память почти закончилась…» (не чаще раза в 10 минут).
 */
final class MemoryWatch {
    private static final double GIB = 1024.0 * 1024 * 1024;
    private static final long COOLDOWN_MS = 10 * 60_000L;
    private static boolean menuChecked;
    private static int highSeconds;
    private static long lastToast;

    private MemoryWatch() {
    }

    static void titleCheck(Minecraft mc) {
        if (menuChecked) return;
        menuChecked = true;
        double xmx = Runtime.getRuntime().maxMemory() / GIB;
        double phys = 0;
        try {
            phys = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize() / GIB;
        } catch (RuntimeException | LinkageError ignored) {
        }
        String msg = null;
        if (xmx < 6.9) {
            msg = Component.translatable("mm_perf.mem.low", String.format("%.1f", xmx)).getString();
        } else if (phys > 0 && xmx > phys * 0.70) {
            msg = Component.translatable("mm_perf.mem.high", String.format("%.1f", xmx), String.format("%.0f", phys)).getString();
        }
        MmPerf.LOGGER.info("[mm_perf] память: Xmx {} ГБ, ОЗУ {} ГБ", String.format("%.1f", xmx), String.format("%.0f", phys));
        if (msg != null) {
            SystemToast.addOrUpdate(mc.getToasts(), SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
                Component.translatable("mm_perf.mem.title"), Component.literal(msg));
        }
    }

    /** Раз в секунду, только в мире. */
    static void tickSecond(Minecraft mc) {
        if (mc.level == null) {
            highSeconds = 0;
            return;
        }
        long used = 0;
        for (MemoryPoolMXBean p : ManagementFactory.getMemoryPoolMXBeans()) {
            if (p.getType() != MemoryType.HEAP) continue;
            MemoryUsage u = p.getCollectionUsage();   // занято после последней сборки (null — пул без неё)
            if (u != null) used += u.getUsed();
        }
        long max = Runtime.getRuntime().maxMemory();
        highSeconds = (max > 0 && used > max * 0.90) ? highSeconds + 1 : 0;
        long now = System.currentTimeMillis();
        if (highSeconds >= 30 && now - lastToast > COOLDOWN_MS) {
            lastToast = now;
            highSeconds = 0;
            MmPerf.LOGGER.info("[mm_perf] куча после GC > 90 % ({} МБ из {})", used >> 20, max >> 20);
            SystemToast.addOrUpdate(mc.getToasts(), SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
                Component.translatable("mm_perf.mem.title"), Component.translatable("mm_perf.mem.full"));
        }
    }
}
