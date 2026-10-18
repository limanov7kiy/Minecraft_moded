package dev.mcmoded.perf;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
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

    /** Раньше всего (конструктор мода, до загрузки ресурсов): диагностика памяти в лог и WARN при опасных настройках. */
    static void earlyCheck() {
        double xmx = Runtime.getRuntime().maxMemory() / GIB;
        double phys = 0;
        double free = 0;
        try {
            OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            phys = os.getTotalMemorySize() / GIB;
            free = os.getFreeMemorySize() / GIB;
        } catch (RuntimeException | LinkageError ignored) {
        }
        // из аргументов JVM — только размеры и тип GC (без путей, имён пользователя и прочего)
        List<String> flags = new ArrayList<>();
        try {
            for (String a : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                if (a.matches("-Xm[sx]\\d+[kKmMgG]?") || a.matches("-XX:\\+Use\\w+GC")) flags.add(a);
            }
        } catch (RuntimeException ignored) {
        }
        String xmxArg = "?";
        String xmsArg = "?";
        StringBuilder gc = new StringBuilder();
        for (String a : flags) {
            if (a.startsWith("-Xmx")) xmxArg = a.substring(4);
            else if (a.startsWith("-Xms")) xmsArg = a.substring(4);
            else gc.append(gc.length() > 0 ? " " : "").append(a);
        }
        if (gc.length() == 0) gc.append("по умолчанию");
        MmPerf.LOGGER.info("[mm_perf] память: Xmx {} ({} ГБ), Xms {}, ОЗУ ПК {} ГБ, свободно ОЗУ {} ГБ, ядер {}, GC {}",
            xmxArg, String.format("%.1f", xmx), xmsArg, String.format("%.0f", phys), String.format("%.1f", free),
            Runtime.getRuntime().availableProcessors(), gc);
        if (tooMuch(xmx, phys)) {
            MmPerf.LOGGER.warn("[mm_perf] ВНИМАНИЕ: игре выделено {} ГБ из {} ГБ ОЗУ ПК — для системы остаётся меньше 30 % (или меньше 4 ГБ). "
                + "Компьютер может зависнуть при загрузке. Уменьшите память: Prism → экземпляр → Изменить → Настройки → Java → Память "
                + "(Xmx не больше ~60 % ОЗУ; при 16 ГБ — не больше 8 ГБ).", String.format("%.1f", xmx), String.format("%.0f", phys));
        }
        if (phys > 0 && phys < 7) {
            MmPerf.LOGGER.warn("[mm_perf] ВНИМАНИЕ: на ПК всего {} ГБ ОЗУ — для этой сборки нужно не меньше 8 ГБ. Включите Режим слабого ПК и закройте лишние программы.",
                String.format("%.1f", phys));
        }
    }

    private static boolean tooMuch(double xmx, double phys) {
        return phys > 0 && (xmx > phys * 0.70 || phys - xmx < 4);
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
        } else if (tooMuch(xmx, phys)) {
            msg = Component.translatable("mm_perf.mem.high", String.format("%.1f", xmx), String.format("%.0f", phys)).getString();
        }
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
