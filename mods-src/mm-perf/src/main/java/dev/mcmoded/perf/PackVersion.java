package dev.mcmoded.perf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * Версия сборки для игрока (docs/friends.md, форма отчётов): читает {@code config/mm_pack_version.txt}
 * (его ведёт pack/, формат {@code ГГГГ.ММ.ДД-N}), показывает в углу главного меню и в заголовке окна, пишет в лог.
 * Нет файла или в нём мусор — «версия неизвестна», игра не падает.
 */
final class PackVersion {
    static final String FILE = "mm_pack_version.txt";
    private static final Pattern VALID = Pattern.compile("[0-9A-Za-z._+-]{1,32}");
    private static String version;   // null — неизвестна
    private static int lastTitleTick = -1;

    private PackVersion() {
    }

    static void load() {
        version = null;
        try {
            Path p = FMLPaths.CONFIGDIR.get().resolve(FILE);
            if (Files.isRegularFile(p) && Files.size(p) < 256) {
                String s = Files.readString(p, StandardCharsets.UTF_8).strip();
                if (VALID.matcher(s).matches()) version = s;
            }
        } catch (IOException | RuntimeException e) {
            MmPerf.LOGGER.warn("[mm_perf] не прочитан {}: {}", FILE, e.toString());
        }
        if (version != null) MmPerf.LOGGER.info("[mm_perf] версия сборки: {} / pack version: {}", version, version);
        else MmPerf.LOGGER.warn("[mm_perf] версия сборки неизвестна (нет или повреждён config/{}) / pack version unknown (config/{} missing or broken)", FILE, FILE);
    }

    static String label() {
        return version != null ? Component.translatable("mm_perf.version.known", version).getString()
            : Component.translatable("mm_perf.version.unknown").getString();
    }

    /** Ваниль переписывает заголовок окна при смене экрана/мира (название мира в нём мы теряем); раз в секунду ставим свой (дёшево: один вызов GLFW). */
    static void tickTitle(Minecraft mc, int ticks) {
        if (ticks == lastTitleTick || ticks % 20 != 0 || mc.getWindow() == null) return;
        lastTitleTick = ticks;
        try {
            mc.getWindow().setTitle("Minecraft " + SharedConstants.getCurrentVersion().getName() + " | " + label());
        } catch (RuntimeException | LinkageError e) {
            // заголовок — украшение, не мешаем игре
        }
    }

    static void drawOnTitle(ScreenEvent.Render.Post e) {
        if (!(e.getScreen() instanceof TitleScreen)) return;
        GuiGraphics g = e.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();
        g.drawString(mc.font, label(), 4, 4, 0xFFFFFF, true);
        try {
            Component warn = MemoryWatch.menuWarning();
            if (warn != null) g.drawWordWrap(mc.font, warn, 4, 16, Math.max(120, e.getScreen().width - 8), 0xFF5555);
        } catch (RuntimeException ex) {
            // украшение, не мешаем игре
        }
    }
}
