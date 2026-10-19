package dev.mcmoded.perf;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
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
    /** Порог красного предупреждения: доля ОЗУ ПК (docs/friends.md: «Xmx не больше ~60 % ОЗУ»). Для проверки занижается: -Dmm_perf.mem.threshold=<проценты>. */
    private static double threshold = 0.60;
    private static boolean overShare;   // Xmx больше порога — рисуем красную строку, шлём чат при входе в мир
    private static double shareXmx;
    private static double sharePhys;
    private static int shareRec;
    private static boolean chatSent;

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
        boolean forced = false;   // тест: свойство порога отключает защиту «штатной конфигурации»
        try {
            String t = System.getProperty("mm_perf.mem.threshold");
            if (t != null) forced = true;
            if (t != null) threshold = Math.max(1, Math.min(100, Double.parseDouble(t))) / 100.0;
        } catch (RuntimeException ignored) {
        }
        int rec = phys > 0 ? recommended(phys) : 0;
        // штатная конфигурация по таблице (напр. 6 ГБ на ПК с 8 ГБ) — не повод ругаться, даже если она выше 60 %
        if (phys > 0 && xmx > phys * threshold && (forced || xmx > rec + 0.5)) {
            overShare = true;
            shareXmx = xmx;
            sharePhys = phys;
            shareRec = rec;
            MmPerf.LOGGER.warn("[mm_perf] ВНИМАНИЕ: Xmx {} ГБ — это {} % физической ОЗУ ({} ГБ), больше порога {} %: возможны фризы из-за свопа. Рекомендуем не больше {} ГБ (docs/friends.md, «Память»).",
                String.format("%.1f", xmx), Math.round(xmx / phys * 100), String.format("%.1f", phys), Math.round(threshold * 100), shareRec);
        }
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

    /** Рекомендуемый Xmx, ГБ — таблица «Память» в friends.md (≥ 28 ГБ → 10, ≥ 15 → 8, иначе 6: ниже 6 сборка не тянет). */
    static int recommended(double phys) {
        return phys >= 28 ? 10 : phys >= 15 ? 8 : 6;
    }

    /** Красная строка для главного меню; null — предупреждать не о чем (или ОЗУ ПК неизвестна). */
    static Component menuWarning() {
        if (!overShare) return null;
        return Component.translatable("mm_perf.mem.share", String.format("%.1f", shareXmx), String.format("%.0f", sharePhys),
            Math.round(threshold * 100), shareRec);
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
        if (overShare && !chatSent && mc.player != null) {   // первое сообщение после входа в мир, только клиенту
            chatSent = true;
            mc.player.sendSystemMessage(Component.translatable("mm_perf.mem.share.chat", String.format("%.1f", shareXmx),
                String.format("%.0f", sharePhys), Math.round(threshold * 100), shareRec).withStyle(ChatFormatting.RED));
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
