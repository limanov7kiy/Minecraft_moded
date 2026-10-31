package dev.mcmoded.autoupdate;

import java.nio.file.Path;

/**
 * Ручной запуск того же обновления без Minecraft (проверка хостом или тест):
 * {@code java -cp mods/mm-autoupdate-<версия>.jar dev.mcmoded.autoupdate.Cli [--root <корень сервера>] [--timeout <с>]
 * [--windows] [--no-post] [--no-backup] [--on-start [--cooldown <мин>]] [<ссылка на pack.toml>]}. Код выхода 0 — обновлено, 2 — пропущено,
 * 1 — ошибка. {@code --on-start} — как режим on_start мода: сначала проверить pack.toml; 0 — обновлено, 3 — изменений нет,
 * 4 — защита от цикла, 1 — ошибка проверки или установки (откат), 2 — пропущено (Windows). {@code --check} — только проверить:
 * 0 — есть обновление, 3 — изменений нет, 1 — ошибка.
 * Конфиг мода (config/mm-autoupdate.toml) здесь не читается: ссылка и параметры — из аргументов или значения по умолчанию.
 */
public final class Cli {
    private Cli() {
    }

    public static void main(String[] args) {
        Updater.Settings s = new Updater.Settings();
        Path root = Path.of(".");
        boolean onStart = false;
        boolean checkOnly = false;
        int cooldown = Updater.DEFAULT_START_COOLDOWN_MINUTES;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--root" -> root = Path.of(args[++i]);
                case "--timeout" -> s.timeoutSeconds = Integer.parseInt(args[++i]);
                case "--windows" -> s.runOnWindows = true;
                case "--no-post" -> { s.removeAfter.clear(); s.disableAfter.clear(); }
                case "--no-backup" -> s.backup = false;
                case "--keep-backup" -> s.keepBackup = true;
                case "--java" -> s.javaPath = args[++i];
                case "--on-start" -> onStart = true;
                case "--check" -> checkOnly = true;
                case "--cooldown" -> cooldown = Integer.parseInt(args[++i]);
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
        if (checkOnly) {
            try {
                Updater.Check c = Updater.check(root.toAbsolutePath().normalize(), s);
                log.info("по ссылке " + c.remoteVersion + " (" + Updater.shortHash(c.remoteHash) + "), поставлено "
                    + Updater.packVersion(root) + " (" + Updater.shortHash(c.installedHash) + "), " + c.millis + " мс: "
                    + (c.updateAvailable() ? "есть обновление" : "изменений нет"));
                System.exit(c.updateAvailable() ? 0 : 3);
            } catch (Exception e) {
                log.error("проверка не удалась: " + e);
                System.exit(1);
            }
        }
        if (onStart) {
            Updater.StartResult sr = Updater.onStart(root, s, log, cooldown);
            System.exit(switch (sr) {
                case UPDATED -> 0;
                case UP_TO_DATE -> 3;
                case LOOP_GUARD -> 4;
                case SKIPPED -> 2;
                case CHECK_FAILED, FAILED -> 1;
            });
        }
        Updater.Result r = Updater.run(root, s, log, "cli");
        System.exit(r == Updater.Result.UPDATED ? 0 : r == Updater.Result.SKIPPED ? 2 : 1);
    }
}
