package dev.mcmoded.autoupdate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Обновление серверной сборки с GitHub силами packwiz-installer (docs/hosting.md, «Автообновление сервера с git»).
 * Чистая Java без классов Minecraft: тот же код зовут мод (при остановке сервера) и {@link Cli} (вручную).
 *
 * <p>Порядок: проверить ОС → при необходимости скачать packwiz-installer-bootstrap.jar и packwiz-installer.jar
 * (ссылки и sha256 зафиксированы в настройках) → поставить маркер «обновление идёт» → запустить
 * {@code java -jar packwiz-installer-bootstrap.jar --bootstrap-no-update -g -s server <pack.toml>} в корне сервера
 * → после успеха убрать из mods/ клиентские jar и перенести необязательные моды в mods/optional/ → снять маркер.
 *
 * <p>«Всё или ничего»: перед установщиком папки сборки (mods/, config/, kubejs/, defaultconfigs/) и манифест packwiz.json копируются
 * в {@link #BACKUP_DIR}; при ошибке или таймауте установщика копия возвращается целиком, маркер снимается, сервер стартует на прежней
 * версии, причина — в {@link #STATUS}. Маркер остаётся только если откат не удался (или копия выключена): тогда на следующем старте
 * мод по нему понимает, что mods/ может быть неполным, и повторяет обновление.
 */
public final class Updater {
    /**
     * Источник обновлений — тег {@code autoupdate-stable}, а не живая ветка сборки: тег двигает владелец после «да» на выкладку
     * (docs/hosting.md, «Как одобряем»); пока тег стоит, перезапуски сервера сборку не меняют.
     */
    public static final String DEFAULT_PACK_URL = "https://raw.githubusercontent.com/limanov7kiy/Minecraft_moded/refs/tags/autoupdate-stable/pack/pack.toml";
    /** Копия папок сборки перед обновлением; при ошибке установщика возвращается целиком. Хранится одна, последняя. */
    public static final String BACKUP_DIR = ".mm-autoupdate.backup";
    public static final List<String> DEFAULT_BACKUP_DIRS = List.of("mods", "config", "kubejs", "defaultconfigs");
    /** Манифест packwiz-installer: что он поставил и с какими хэшами; откатывается вместе с папками, иначе установщик «не увидит» откат. */
    static final String MANIFEST = "packwiz.json";
    private static final long BACKUP_SPARE_BYTES = 256L << 20;
    public static final String DEFAULT_BOOTSTRAP_JAR = "packwiz-installer-bootstrap.jar";
    public static final String DEFAULT_BOOTSTRAP_URL = "https://github.com/packwiz/packwiz-installer-bootstrap/releases/download/v0.0.3/packwiz-installer-bootstrap.jar";
    public static final String DEFAULT_BOOTSTRAP_SHA256 = "a8fbb24dc604278e97f4688e82d3d91a318b98efc08d5dbfcbcbcab6443d116c";
    public static final String DEFAULT_INSTALLER_JAR = "packwiz-installer.jar";
    public static final String DEFAULT_INSTALLER_URL = "https://github.com/packwiz/packwiz-installer/releases/download/v0.5.14/packwiz-installer.jar";
    public static final String DEFAULT_INSTALLER_SHA256 = "c9f646908d340d84773948a9a7d98bc1dae250d35e1016dc6e2b8459760b5598";
    public static final List<String> DEFAULT_JAVA_ARGS = List.of("-Xmx1G", "-Djava.awt.headless=true");
    public static final int DEFAULT_TIMEOUT_SECONDS = 600;
    /** Клиентские jar сборки (fetch-mods.py кладёт их только клиенту; packwiz-installer стороны простых файлов не знает). */
    public static final List<String> DEFAULT_REMOVE_AFTER = List.of("*-client.jar");
    /** Необязательные моды: packwiz-installer без GUI включает их все, а сервер держит WorldEdit в mods/optional/ (docs/hosting.md). */
    public static final List<String> DEFAULT_DISABLE_AFTER = List.of("worldedit-*.jar");

    /** Маркер в корне сервера: обновление начато и не завершено успешно. */
    public static final String MARKER = ".mm-autoupdate.updating";
    /** Итог последнего запуска (для хоста): ok/failed, время, версия сборки. */
    public static final String STATUS = "mm-autoupdate.status";
    static final String PACK_VERSION_FILE = "config/mm_pack_version.txt";
    private static final int KEEP_LINES = 25;

    public interface Log {
        void info(String s);
        void warn(String s);
        void error(String s);
    }

    public static final class Settings {
        public String packUrl = DEFAULT_PACK_URL;
        public String bootstrapJar = DEFAULT_BOOTSTRAP_JAR;
        public String bootstrapUrl = DEFAULT_BOOTSTRAP_URL;
        public String bootstrapSha256 = DEFAULT_BOOTSTRAP_SHA256;
        public String installerJar = DEFAULT_INSTALLER_JAR;
        public String installerUrl = DEFAULT_INSTALLER_URL;
        public String installerSha256 = DEFAULT_INSTALLER_SHA256;
        /** true — bootstrap сам спрашивает GitHub API о новой версии packwiz-installer (лимит 60 запросов/час на IP хостинга). */
        public boolean installerUpdate = false;
        /** Пусто — java текущего процесса. */
        public String javaPath = "";
        public List<String> javaArgs = new ArrayList<>(DEFAULT_JAVA_ARGS);
        public int timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;
        public boolean runOnWindows = false;
        public List<String> removeAfter = new ArrayList<>(DEFAULT_REMOVE_AFTER);
        public List<String> disableAfter = new ArrayList<>(DEFAULT_DISABLE_AFTER);
        /** «Всё или ничего»: перед установкой копировать backupDirs в {@link #BACKUP_DIR}, при ошибке вернуть копию целиком. */
        public boolean backup = true;
        public List<String> backupDirs = new ArrayList<>(DEFAULT_BACKUP_DIRS);
    }

    public enum Result { UPDATED, SKIPPED, FAILED }

    private Updater() {
    }

    public static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    }

    public static boolean markerExists(Path root) {
        return Files.exists(root.resolve(MARKER));
    }

    /** @param reason для лога и маркера: «stop» (остановка сервера), «retry» (повтор на старте), «cli». */
    public static Result run(Path root, Settings s, Log log, String reason) {
        root = root.toAbsolutePath().normalize();
        if (isWindows() && !s.runOnWindows) {
            log.warn("Windows не даёт заменять jar, которые держит запущенная Java: обновление пропущено. "
                + "Обновляйте сервер до запуска (start.bat запускает packwiz-installer сам) или поставьте run_on_windows = true "
                + "в config/mm-autoupdate.toml, если понимаете риск. / Windows keeps loaded jars locked: update skipped.");
            return Result.SKIPPED;
        }
        if (s.packUrl == null || s.packUrl.isBlank()) {
            log.error("pack_url пуст — обновлять нечего / pack_url is empty");
            return Result.FAILED;
        }
        Path bootstrap = root.resolve(s.bootstrapJar);
        Path installer = root.resolve(s.installerJar);
        try {
            ensureJar(bootstrap, s.bootstrapUrl, s.bootstrapSha256, log);
            ensureJar(installer, s.installerUrl, s.installerSha256, log);
        } catch (IOException | RuntimeException e) {
            log.error("не удалось получить packwiz-installer: " + e + " / failed to fetch packwiz-installer");
            writeStatus(root, "failed " + now() + " reason=" + reason + " no-installer", log);
            return Result.FAILED;
        }

        List<String> cmd = new ArrayList<>();
        cmd.add(javaExecutable(s));
        cmd.addAll(s.javaArgs);
        cmd.add("-jar");
        cmd.add(bootstrap.toString());
        cmd.add("--bootstrap-main-jar");
        cmd.add(installer.toString());
        if (!s.installerUpdate) cmd.add("--bootstrap-no-update");
        cmd.add("-g");
        cmd.add("-s");
        cmd.add("server");
        cmd.add(s.packUrl);
        log.info("обновление сборки (" + reason + "): " + String.join(" ", cmd));

        Path backupDir = root.resolve(BACKUP_DIR);
        if (s.backup) {
            try {
                makeBackup(root, backupDir, s.backupDirs, log);
            } catch (IOException | RuntimeException e) {
                log.error("копия сборки перед обновлением не сделана: " + e + " — обновление отменено, сервер остаётся на текущей версии "
                    + "/ backup failed, update cancelled");
                deleteTree(backupDir);
                writeStatus(root, "failed " + now() + " reason=" + reason + " backup-error", log);
                return Result.FAILED;
            }
        }

        try {
            Files.writeString(root.resolve(MARKER), reason + " " + now() + System.lineSeparator(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("не удалось создать маркер " + MARKER + ": " + e + " — обновление отменено / cannot write marker, update cancelled");
            return Result.FAILED;
        }

        Deque<String> tail = new ArrayDeque<>();
        int code;
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd).directory(root.toFile()).redirectErrorStream(true);
            Process p = pb.start();
            Thread pump = new Thread(() -> pump(p.getInputStream(), tail, log), "mm-autoupdate-log");
            pump.setDaemon(true);
            pump.start();
            if (!p.waitFor(Math.max(1, s.timeoutSeconds), TimeUnit.SECONDS)) {
                p.destroyForcibly();
                p.waitFor(15, TimeUnit.SECONDS);
                log.error("packwiz-installer не уложился в " + s.timeoutSeconds + " с и остановлен / timed out");
                return fail(root, s, backupDir, reason, "timeout", log);
            }
            pump.join(5000);
            code = p.exitValue();
        } catch (IOException e) {
            log.error("не удалось запустить packwiz-installer: " + e + " / cannot start packwiz-installer");
            return fail(root, s, backupDir, reason, "start-error", log);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("ожидание packwiz-installer прервано / interrupted");
            return fail(root, s, backupDir, reason, "interrupted", log);
        }
        if (code != 0) {
            log.error("packwiz-installer завершился с кодом " + code + "; последние строки / exit code " + code + ", last lines:");
            for (String l : tail) log.error("  " + l);
            return fail(root, s, backupDir, reason, "exit=" + code, log);
        }
        postProcess(root, s, log);
        pruneDuplicates(root, log);
        try {
            Files.deleteIfExists(root.resolve(MARKER));
        } catch (IOException e) {
            log.warn("не удалось снять маркер " + MARKER + ": " + e);
        }
        String ver = packVersion(root);
        writeStatus(root, "ok " + now() + " reason=" + reason + " version=" + ver, log);
        log.info("обновление завершено, версия сборки " + ver + " / update finished, pack version " + ver);
        return Result.UPDATED;
    }

    /**
     * Установщик не доработал: при включённой копии возвращаем папки сборки и манифест целиком («всё или ничего»), снимаем маркер —
     * сервер стартует на прежней версии, причина остаётся в {@link #STATUS}. Если копия выключена или откат не удался, маркер
     * остаётся: на следующем старте обновление повторится (retry_on_start), а mods/ до этого считается неполным.
     */
    private static Result fail(Path root, Settings s, Path backupDir, String reason, String why, Log log) {
        if (!s.backup) {
            log.error("mods/ может быть обновлён частично — обновление повторится при следующем запуске сервера (маркер " + MARKER + ")");
            writeStatus(root, "failed " + now() + " reason=" + reason + " " + why, log);
            return Result.FAILED;
        }
        try {
            rollback(root, backupDir, s.backupDirs, log);
        } catch (IOException | RuntimeException e) {
            log.error("откат из " + BACKUP_DIR + " не удался: " + e + " — mods/ может быть неполным, обновление повторится при следующем "
                + "запуске сервера (маркер " + MARKER + ") / rollback failed, will retry on next start");
            writeStatus(root, "failed " + now() + " reason=" + reason + " " + why + " rollback-error", log);
            return Result.FAILED;
        }
        try {
            Files.deleteIfExists(root.resolve(MARKER));
        } catch (IOException e) {
            log.warn("не удалось снять маркер " + MARKER + ": " + e);
        }
        log.error("сборка возвращена к версии до обновления (" + packVersion(root) + "); причина — выше и в " + STATUS
            + " / rolled back to the previous pack version");
        writeStatus(root, "failed " + now() + " reason=" + reason + " " + why + " rolled-back version=" + packVersion(root), log);
        return Result.FAILED;
    }

    /** Копия папок сборки и манифеста в backupDir (старая копия удаляется). Перед копированием — проверка места на диске. */
    static void makeBackup(Path root, Path backupDir, List<String> dirs, Log log) throws IOException {
        deleteTree(backupDir);
        long need = 0;
        List<Path> sources = new ArrayList<>();
        for (String d : dirs) {
            Path src = safeChild(root, d);
            if (src == null || !Files.isDirectory(src)) continue;
            sources.add(src);
            need += treeSize(src);
        }
        Path manifest = root.resolve(MANIFEST);
        if (Files.isRegularFile(manifest)) need += Files.size(manifest);
        long usable = Files.getFileStore(root).getUsableSpace();
        if (usable < need + BACKUP_SPARE_BYTES) {
            throw new IOException("мало места на диске для копии: нужно " + (need >> 20) + " МБ, свободно " + (usable >> 20)
                + " МБ / not enough disk space for backup");
        }
        Files.createDirectories(backupDir);
        for (Path src : sources) copyTree(src, backupDir.resolve(src.getFileName()));
        if (Files.isRegularFile(manifest)) Files.copy(manifest, backupDir.resolve(MANIFEST), StandardCopyOption.REPLACE_EXISTING);
        log.info("копия сборки перед обновлением: " + BACKUP_DIR + "/ (" + (need >> 20) + " МБ: "
            + String.join(", ", sources.stream().map(p -> p.getFileName().toString()).toList()) + ") / backup made");
    }

    /** Возвращает из backupDir всё, что там есть, поверх текущих папок (текущие удаляются целиком). */
    static void rollback(Path root, Path backupDir, List<String> dirs, Log log) throws IOException {
        if (!Files.isDirectory(backupDir)) throw new IOException("нет копии " + BACKUP_DIR);
        for (String d : dirs) {
            Path dst = safeChild(root, d);
            Path src = backupDir.resolve(d);
            if (dst == null || !Files.isDirectory(src)) continue;
            deleteTree(dst);
            moveTree(src, dst);
            log.info("откат: " + d + "/ восстановлен из копии / restored from backup");
        }
        Path manifest = backupDir.resolve(MANIFEST);
        if (Files.isRegularFile(manifest)) {
            Files.move(manifest, root.resolve(MANIFEST), StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.deleteIfExists(root.resolve(MANIFEST));   // до обновления манифеста не было — не должно быть и после отката
        }
        deleteTree(backupDir);
    }

    /** Имя папки первого уровня в корне сервера; пути с разделителями, «..» и пустые — не принимаются. */
    private static Path safeChild(Path root, String name) {
        if (name == null || name.isBlank() || name.contains("/") || name.contains("\\") || name.equals(".") || name.equals("..")) return null;
        return root.resolve(name.trim());
    }

    private static long treeSize(Path dir) throws IOException {
        long[] n = {0};
        Files.walkFileTree(dir, new SimpleFileVisitor<>() {
            @Override public FileVisitResult visitFile(Path f, BasicFileAttributes a) { n[0] += a.size(); return FileVisitResult.CONTINUE; }
        });
        return n[0];
    }

    static void copyTree(Path src, Path dst) throws IOException {
        Files.walkFileTree(src, new SimpleFileVisitor<>() {
            @Override public FileVisitResult preVisitDirectory(Path d, BasicFileAttributes a) throws IOException {
                Files.createDirectories(dst.resolve(src.relativize(d).toString()));
                return FileVisitResult.CONTINUE;
            }
            @Override public FileVisitResult visitFile(Path f, BasicFileAttributes a) throws IOException {
                Files.copy(f, dst.resolve(src.relativize(f).toString()), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /** Переименование (мгновенно на одном диске); если не вышло — копирование и удаление источника. */
    private static void moveTree(Path src, Path dst) throws IOException {
        try {
            Files.move(src, dst);
        } catch (IOException e) {
            copyTree(src, dst);
            deleteTree(src);
        }
    }

    static void deleteTree(Path dir) {
        if (!Files.exists(dir)) return;
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                @Override public FileVisitResult visitFile(Path f, BasicFileAttributes a) throws IOException {
                    Files.delete(f);
                    return FileVisitResult.CONTINUE;
                }
                @Override public FileVisitResult postVisitDirectory(Path d, IOException e) throws IOException {
                    if (e != null) throw e;
                    Files.delete(d);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("не удалено " + dir + ": " + e, e);
        }
    }

    private static void pump(InputStream in, Deque<String> tail, Log log) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                log.info("packwiz: " + line);
                String s = line.stripLeading();
                if (s.startsWith("at ") || s.startsWith("... ")) continue;   // стек — в полном логе выше, в хвосте только суть
                synchronized (tail) {
                    tail.addLast(line);
                    if (tail.size() > KEEP_LINES) tail.removeFirst();
                }
            }
        } catch (IOException e) {
            log.warn("вывод packwiz-installer оборван: " + e);
        }
    }

    /** Файлы из mods/ после обновления: клиентские jar удаляем, необязательные моды переносим в mods/optional/. */
    static void postProcess(Path root, Settings s, Log log) {
        Path mods = root.resolve("mods");
        if (!Files.isDirectory(mods)) return;
        List<PathMatcher> remove = matchers(mods, s.removeAfter);
        List<PathMatcher> disable = matchers(mods, s.disableAfter);
        if (remove.isEmpty() && disable.isEmpty()) return;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(mods)) {
            for (Path p : ds) {
                if (!Files.isRegularFile(p)) continue;
                Path name = p.getFileName();
                try {
                    if (remove.stream().anyMatch(m -> m.matches(name))) {
                        Files.delete(p);
                        log.info("удалён клиентский файл mods/" + name + " / removed client-only file");
                    } else if (disable.stream().anyMatch(m -> m.matches(name))) {
                        Path opt = mods.resolve("optional");
                        Files.createDirectories(opt);
                        Files.move(p, opt.resolve(name), StandardCopyOption.REPLACE_EXISTING);
                        log.info("необязательный мод перенесён в mods/optional/" + name + " / optional mod moved to mods/optional");
                    }
                } catch (IOException e) {
                    log.warn("mods/" + name + ": " + e);
                }
            }
        } catch (IOException e) {
            log.warn("не прочитан mods/: " + e);
        }
    }

    private static final Pattern MOD_ID = Pattern.compile("^\s*modId\s*=\s*\"([A-Za-z0-9_.-]+)\"", Pattern.MULTILINE);
    private static final Pattern MANIFEST_LOC = Pattern.compile("\"cachedLocation\"\s*:\s*\"([^\"]+)\"");

    /**
     * Оборванное обновление (сервер убит на середине) может оставить в mods/ старую и новую версию одного мода — Forge на дублях
     * не стартует. После успешного прогона: jar, чей modId совпадает с jar из манифеста packwiz.json, а сам он в манифесте не числится, удаляем.
     * Моды хоста вне сборки с уникальным modId не трогаем.
     */
    static void pruneDuplicates(Path root, Log log) {
        Path mods = root.resolve("mods");
        Path manifest = root.resolve("packwiz.json");
        if (!Files.isDirectory(mods) || !Files.isRegularFile(manifest)) return;
        Set<String> managed = new HashSet<>();
        try {
            Matcher m = MANIFEST_LOC.matcher(Files.readString(manifest, StandardCharsets.UTF_8));
            while (m.find()) managed.add(Path.of(m.group(1).replace("\\\\", "/").replace("\\", "/")).getFileName().toString());
        } catch (IOException | RuntimeException e) {
            log.warn("packwiz.json не прочитан, дубли модов не проверены: " + e);
            return;
        }
        Map<String, List<Path>> byId = new HashMap<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(mods, "*.jar")) {
            for (Path jar : ds) {
                if (!Files.isRegularFile(jar)) continue;
                for (String id : modIds(jar)) byId.computeIfAbsent(id, k -> new ArrayList<>()).add(jar);
            }
        } catch (IOException e) {
            log.warn("mods/ не прочитан, дубли модов не проверены: " + e);
            return;
        }
        for (Map.Entry<String, List<Path>> e : byId.entrySet()) {
            List<Path> jars = e.getValue();
            if (jars.size() < 2 || jars.stream().noneMatch(j -> managed.contains(j.getFileName().toString()))) continue;
            for (Path j : jars) {
                if (managed.contains(j.getFileName().toString())) continue;
                try {
                    Files.delete(j);
                    log.warn("удалён дубль мода " + e.getKey() + " вне сборки: mods/" + j.getFileName() + " / removed duplicate mod jar");
                } catch (IOException ex) {
                    log.warn("не удалён дубль mods/" + j.getFileName() + ": " + ex);
                }
            }
        }
    }

    static List<String> modIds(Path jar) {
        List<String> ids = new ArrayList<>();
        try (ZipFile z = new ZipFile(jar.toFile())) {
            ZipEntry e = z.getEntry("META-INF/mods.toml");
            if (e == null) return ids;
            String toml = new String(z.getInputStream(e).readAllBytes(), StandardCharsets.UTF_8);
            Matcher m = MOD_ID.matcher(toml);
            while (m.find()) ids.add(m.group(1));
        } catch (IOException | RuntimeException ignored) {
            // не zip или битый jar — не наше дело
        }
        return ids;
    }

    private static List<PathMatcher> matchers(Path dir, List<String> globs) {
        List<PathMatcher> out = new ArrayList<>();
        for (String g : globs) {
            if (g == null || g.isBlank() || g.contains("/") || g.contains("\\")) continue;   // только имена файлов в mods/
            out.add(dir.getFileSystem().getPathMatcher("glob:" + g.trim()));
        }
        return out;
    }

    static String javaExecutable(Settings s) {
        if (s.javaPath != null && !s.javaPath.isBlank()) return s.javaPath;
        return ProcessHandle.current().info().command()
            .orElseGet(() -> Path.of(System.getProperty("java.home"), "bin", isWindows() ? "java.exe" : "java").toString());
    }

    /** Есть файл — доверяем ему (хост мог положить свою версию); нет — качаем по ссылке и сверяем sha256. */
    static void ensureJar(Path jar, String url, String sha256, Log log) throws IOException {
        if (Files.isRegularFile(jar)) return;
        if (url == null || url.isBlank()) throw new IOException("нет " + jar.getFileName() + " и не задана ссылка для скачивания");
        log.info("скачиваю " + jar.getFileName() + " из " + url);
        byte[] data = download(url);
        if (sha256 != null && !sha256.isBlank()) {
            String got = sha256(data);
            if (!got.equalsIgnoreCase(sha256.trim())) {
                throw new IOException("sha256 " + jar.getFileName() + " не совпал: ожидали " + sha256 + ", получили " + got);
            }
        }
        Path tmp = jar.resolveSibling(jar.getFileName() + ".part");
        Files.write(tmp, data);
        Files.move(tmp, jar, StandardCopyOption.REPLACE_EXISTING);
        log.info(jar.getFileName() + ": " + data.length + " байт, sha256 совпал");
    }

    static byte[] download(String url) throws IOException {
        HttpURLConnection c = (HttpURLConnection) URI.create(url).toURL().openConnection();
        c.setInstanceFollowRedirects(true);
        c.setConnectTimeout(30_000);
        c.setReadTimeout(120_000);
        c.setRequestProperty("User-Agent", "Minecraft_moded mm-autoupdate");
        int status = c.getResponseCode();
        if (status / 100 != 2) throw new IOException("HTTP " + status + " для " + url);
        try (InputStream in = c.getInputStream()) {
            return in.readAllBytes();
        } finally {
            c.disconnect();
        }
    }

    static String sha256(byte[] data) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(data);
            StringBuilder sb = new StringBuilder(64);
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    static String packVersion(Path root) {
        try {
            Path p = root.resolve(PACK_VERSION_FILE);
            if (Files.isRegularFile(p) && Files.size(p) < 256) {
                String v = Files.readString(p, StandardCharsets.UTF_8).strip();
                if (v.matches("[0-9A-Za-z._+-]{1,32}")) return v;
            }
        } catch (IOException | RuntimeException ignored) {
            // версия — украшение статуса
        }
        return "unknown";
    }

    private static void writeStatus(Path root, String line, Log log) {
        try {
            Files.writeString(root.resolve(STATUS), line + System.lineSeparator(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("не записан " + STATUS + ": " + e);
        }
    }

    static String now() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
