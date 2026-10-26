package dev.mcmoded.perf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.event.ScreenEvent;

/**
 * Экран «Сохранение мира» (R-012, поток 64, docs/performance.md «Долгое сохранение»): при выходе из одиночного мира ванильный сервер
 * ждёт, пока догенерируются все начатые чанки (после полёта/портала — до минуты), и игрок принимает это за зависание.
 * Пишем подпись «не закрывайте игру» и сколько секунд идёт сохранение; в лог — сколько ждали (по-русски и латиницей:
 * у части игроков кириллица в latest.log приходит битой). Само сохранение не трогаем.
 */
final class SaveWatch {
    private static final int SLOW_MS = 10_000;
    private static long start;
    private static int worldSeconds;

    private SaveWatch() {
    }

    private static boolean isSaving(Screen s) {
        return s instanceof GenericDirtMessageScreen && s.getTitle().getContents() instanceof TranslatableContents t
            && "menu.savingLevel".equals(t.getKey());
    }

    static void onOpening(ScreenEvent.Opening e) {
        if (isSaving(e.getNewScreen())) {
            if (start == 0) {
                start = System.currentTimeMillis();
                MmPerf.LOGGER.info("[mm_perf] сохранение мира... / saving world...");
            }
            return;
        }
        if (start != 0) finish();
    }

    private static void finish() {
        long ms = System.currentTimeMillis() - start;
        start = 0;
        String sec = String.format("%.1f", ms / 1000.0);
        if (ms >= SLOW_MS) {
            MmPerf.LOGGER.warn("[mm_perf] мир сохранялся {} с — ждали догенерацию чанков (после полёта/портала постойте 10–30 с перед выходом, docs/friends.md) / "
                + "world save took {} s - waiting for chunk generation to finish (wait 10-30 s after flying/portals before quitting)", sec, sec);
        } else {
            MmPerf.LOGGER.info("[mm_perf] мир сохранён за {} с / world saved in {} s", sec, sec);
        }
    }

    static void onRender(ScreenEvent.Render.Post e) {
        if (start == 0 || !isSaving(e.getScreen())) return;
        Screen s = e.getScreen();
        GuiGraphics g = e.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();
        long sec = (System.currentTimeMillis() - start) / 1000;
        // ванильная надпись «Сохранение мира» у GenericDirtMessageScreen 1.20.1 — на y = 70; наши строки ниже
        int y = 90;
        int w = Math.max(100, s.width - 40);   // на узком окне подсказка переносится, а не уходит за край
        for (FormattedCharSequence line : mc.font.split(Component.translatable("mm_perf.save.wait"), w)) {
            g.drawCenteredString(mc.font, line, s.width / 2, y, 0xFFFFFF);
            y += 10;
        }
        y += 2;
        for (FormattedCharSequence line : mc.font.split(Component.translatable("mm_perf.save.hint"), w)) {
            g.drawCenteredString(mc.font, line, s.width / 2, y, 0xA0A0A0);
            y += 10;
        }
        if (sec >= 3) g.drawCenteredString(mc.font, Component.translatable("mm_perf.save.elapsed", sec), s.width / 2, y + 8, 0xFFFF55);
    }

    /** Самотест для test-client.sh: -Dmm_perf.selftest.quit=<с> — через столько секунд в одиночном мире «Сохранить и выйти» (как кнопка меню паузы). */
    static void selfTestSecond(Minecraft mc) {
        String q = System.getProperty("mm_perf.selftest.quit");
        if (q == null || mc.level == null || mc.player == null || !mc.isLocalServer()) return;
        if (++worldSeconds < Integer.parseInt(q)) return;
        worldSeconds = Integer.MIN_VALUE;   // один раз
        MmPerf.LOGGER.info("MMPERF-SELFTEST save-and-quit");
        mc.level.disconnect();
        mc.clearLevel(new GenericDirtMessageScreen(Component.translatable("menu.savingLevel")));
        mc.setScreen(new TitleScreen());
    }
}
