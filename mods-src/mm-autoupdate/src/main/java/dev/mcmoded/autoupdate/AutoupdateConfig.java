package dev.mcmoded.autoupdate;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

/** config/mm-autoupdate.toml — создаётся Forge при первом запуске; значения по умолчанию берутся из {@link Updater}. */
final class AutoupdateConfig {
    static final ForgeConfigSpec SPEC;
    static final ForgeConfigSpec.BooleanValue ENABLED;
    static final ForgeConfigSpec.ConfigValue<String> PACK_URL;
    static final ForgeConfigSpec.ConfigValue<String> BOOTSTRAP_JAR;
    static final ForgeConfigSpec.ConfigValue<String> BOOTSTRAP_URL;
    static final ForgeConfigSpec.ConfigValue<String> BOOTSTRAP_SHA256;
    static final ForgeConfigSpec.ConfigValue<String> INSTALLER_JAR;
    static final ForgeConfigSpec.ConfigValue<String> INSTALLER_URL;
    static final ForgeConfigSpec.ConfigValue<String> INSTALLER_SHA256;
    static final ForgeConfigSpec.BooleanValue INSTALLER_UPDATE;
    static final ForgeConfigSpec.ConfigValue<String> JAVA_PATH;
    static final ForgeConfigSpec.ConfigValue<List<? extends String>> JAVA_ARGS;
    static final ForgeConfigSpec.IntValue TIMEOUT_SECONDS;
    static final ForgeConfigSpec.BooleanValue RUN_ON_WINDOWS;
    static final ForgeConfigSpec.BooleanValue RETRY_ON_START;
    static final ForgeConfigSpec.ConfigValue<List<? extends String>> REMOVE_AFTER;
    static final ForgeConfigSpec.ConfigValue<List<? extends String>> DISABLE_AFTER;
    static final ForgeConfigSpec.BooleanValue BACKUP;
    static final ForgeConfigSpec.ConfigValue<List<? extends String>> BACKUP_DIRS;
    static final ForgeConfigSpec.BooleanValue KEEP_BACKUP;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.comment("Автообновление серверной сборки с GitHub при остановке сервера (docs/hosting.md, «Автообновление сервера с git»).",
            "Server pack auto-update from GitHub on server stop. Only runs on a dedicated server.");
        ENABLED = b.comment("Включить обновление при остановке сервера (по умолчанию выключено: включает хост осознанно, см. docs/hosting.md)",
                "Run packwiz-installer on server stop (off by default)")
            .define("enabled", false);
        PACK_URL = b.comment("Ссылка на pack.toml сборки: по умолчанию тег autoupdate-stable (его двигает владелец после «да» на выкладку),",
                "не живая ветка — перезапуск сервера без нового тега сборку не меняет / pack.toml URL (tag autoupdate-stable)")
            .define("pack_url", Updater.DEFAULT_PACK_URL);
        BACKUP = b.comment("Перед обновлением копировать папки сборки (backup_dirs) и packwiz.json в " + Updater.BACKUP_DIR + "/;",
                "при ошибке установщика вернуть копию целиком — сервер стартует на прежней версии. Нужно место на диске (~размер mods/ и config/)",
                "Back up pack folders before updating and restore them completely if the installer fails")
            .define("backup", true);
        BACKUP_DIRS = b.comment("Папки в корне сервера, которые копируются и откатываются / folders to back up and roll back")
            .defineListAllowEmpty(List.of("backup_dirs"), () -> new ArrayList<>(Updater.DEFAULT_BACKUP_DIRS), o -> o instanceof String);
        KEEP_BACKUP = b.comment("Оставлять копию после успешного обновления (до следующего); по умолчанию удаляется — место на диске хостинга",
                "Keep the backup after a successful update (default: delete it)")
            .define("keep_backup", false);
        TIMEOUT_SECONDS = b.comment("Сколько секунд ждать packwiz-installer; дольше — процесс останавливается, обновление повторится на следующем старте",
                "Seconds to wait for packwiz-installer before killing it")
            .defineInRange("timeout_seconds", Updater.DEFAULT_TIMEOUT_SECONDS, 10, 86400);
        RETRY_ON_START = b.comment("Если прошлое обновление не завершилось (маркер " + Updater.MARKER + "), повторить его на старте сервера",
                "и остановить сервер с кодом 0, чтобы хостинг запустил его заново уже с обновлёнными модами",
                "Retry an interrupted update on server start, then exit(0) so the panel restarts the server")
            .define("retry_on_start", true);
        RUN_ON_WINDOWS = b.comment("На Windows запущенная Java держит jar — заменить их нельзя; по умолчанию обновление пропускается с предупреждением",
                "On Windows loaded jars are locked; set true to try anyway")
            .define("run_on_windows", false);
        REMOVE_AFTER = b.comment("Имена файлов в mods/, которые удалить после обновления (клиентские jar сборки: packwiz-installer стороны простых файлов не знает)",
                "File-name globs in mods/ to delete after the update (client-only jars)")
            .defineListAllowEmpty(List.of("remove_after"), () -> new ArrayList<>(Updater.DEFAULT_REMOVE_AFTER), o -> o instanceof String);
        DISABLE_AFTER = b.comment("Имена файлов в mods/, которые перенести в mods/optional/ (необязательные моды; без GUI packwiz-installer включает их все)",
                "File-name globs in mods/ to move into mods/optional/ after the update (optional mods, e.g. WorldEdit)")
            .defineListAllowEmpty(List.of("disable_after"), () -> new ArrayList<>(Updater.DEFAULT_DISABLE_AFTER), o -> o instanceof String);
        b.push("installer");
        BOOTSTRAP_JAR = b.comment("Путь к packwiz-installer-bootstrap.jar от корня сервера; нет файла — скачивается по bootstrap_url")
            .define("bootstrap_jar", Updater.DEFAULT_BOOTSTRAP_JAR);
        BOOTSTRAP_URL = b.define("bootstrap_url", Updater.DEFAULT_BOOTSTRAP_URL);
        BOOTSTRAP_SHA256 = b.comment("sha256 скачанного файла; пусто — не проверять").define("bootstrap_sha256", Updater.DEFAULT_BOOTSTRAP_SHA256);
        INSTALLER_JAR = b.comment("Путь к packwiz-installer.jar; нет файла — скачивается по installer_url").define("installer_jar", Updater.DEFAULT_INSTALLER_JAR);
        INSTALLER_URL = b.define("installer_url", Updater.DEFAULT_INSTALLER_URL);
        INSTALLER_SHA256 = b.define("installer_sha256", Updater.DEFAULT_INSTALLER_SHA256);
        INSTALLER_UPDATE = b.comment("true — bootstrap сам спрашивает GitHub API о новой версии packwiz-installer (лимит 60 запросов в час на IP)",
                "Let the bootstrap check GitHub for a newer packwiz-installer")
            .define("installer_update", false);
        JAVA_PATH = b.comment("Java для packwiz-installer; пусто — та же, что у сервера / java executable, empty = current JVM")
            .define("java_path", "");
        JAVA_ARGS = b.comment("Аргументы JVM для packwiz-installer (он держит каждый скачиваемый файл в памяти целиком)",
                "JVM args for packwiz-installer")
            .defineListAllowEmpty(List.of("java_args"), () -> new ArrayList<>(Updater.DEFAULT_JAVA_ARGS), o -> o instanceof String);
        b.pop();
        SPEC = b.build();
    }

    private AutoupdateConfig() {
    }

    static Updater.Settings settings() {
        Updater.Settings s = new Updater.Settings();
        s.packUrl = PACK_URL.get();
        s.bootstrapJar = BOOTSTRAP_JAR.get();
        s.bootstrapUrl = BOOTSTRAP_URL.get();
        s.bootstrapSha256 = BOOTSTRAP_SHA256.get();
        s.installerJar = INSTALLER_JAR.get();
        s.installerUrl = INSTALLER_URL.get();
        s.installerSha256 = INSTALLER_SHA256.get();
        s.installerUpdate = INSTALLER_UPDATE.get();
        s.javaPath = JAVA_PATH.get();
        s.javaArgs = new ArrayList<>(JAVA_ARGS.get());
        s.timeoutSeconds = TIMEOUT_SECONDS.get();
        s.runOnWindows = RUN_ON_WINDOWS.get();
        s.removeAfter = new ArrayList<>(REMOVE_AFTER.get());
        s.disableAfter = new ArrayList<>(DISABLE_AFTER.get());
        s.backup = BACKUP.get();
        s.backupDirs = new ArrayList<>(BACKUP_DIRS.get());
        s.keepBackup = KEEP_BACKUP.get();
        return s;
    }
}
