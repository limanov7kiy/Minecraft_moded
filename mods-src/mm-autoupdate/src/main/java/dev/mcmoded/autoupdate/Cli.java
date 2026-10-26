package dev.mcmoded.autoupdate;

import java.nio.file.Path;

/**
 * Ручной запуск того же обновления без Minecraft (проверка хостом или тест):
 * {@code java -cp mods/mm-autoupdate-<версия>.jar dev.mcmoded.autoupdate.Cli [--root <корень сервера>] [--timeout <с>]
 * [--windows] [--no-post] [<ссылка на pack.toml>]}. Код выхода 0 — обновлено, 2 — пропущено, 1 — ошибка.
 * Конфиг мода (config/mm-autoupdate.toml) здесь не читается: ссылка и параметры — из аргументов или значения по умолчанию.
 */
public final class Cli {
    private Cli() {
    }

    public static void main(String[] args) {
        Updater.Settings s = new Updater.Settings();
        Path root = Path.of(".");
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--root" -> root = Path.of(args[++i]);
                case "--timeout" -> s.timeoutSeconds = Integer.parseInt(args[++i]);
                case "--windows" -> s.runOnWindows = true;
                case "--no-post" -> { s.removeAfter.clear(); s.disableAfter.clear(); }
                case "--java" -> s.javaPath = args[++i];
                default -> {
                    if (args[i].startsWith("-")) {
                        System.err.println("неизвестный аргумент: " + args[i]);
                        System.exit(1);
                    }
                    s.packUrl = args[i];
                }
            }
        }
        Updater.Log log = new Updater.Log() {
            @Override public void info(String m) { System.out.println("[mm_autoupdate] " + m); }
            @Override public void warn(String m) { System.out.println("[mm_autoupdate] WARN " + m); }
            @Override public void error(String m) { System.out.println("[mm_autoupdate] ERROR " + m); }
        };
        Updater.Result r = Updater.run(root, s, log, "cli");
        System.exit(r == Updater.Result.UPDATED ? 0 : r == Updater.Result.SKIPPED ? 2 : 1);
    }
}
