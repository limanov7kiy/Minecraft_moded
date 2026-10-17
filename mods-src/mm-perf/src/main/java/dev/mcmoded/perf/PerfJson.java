package dev.mcmoded.perf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraftforge.fml.loading.FMLPaths;

/** Маленькие JSON-файлы {@code config/mm-perf-*.json}: флаг миграции клавиш и сохранённые настройки до Режима слабого ПК. */
final class PerfJson {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private PerfJson() {
    }

    static Path path(String name) {
        return FMLPaths.CONFIGDIR.get().resolve("mm-perf-" + name + ".json");
    }

    static JsonObject read(String name) {
        try {
            Path p = path(name);
            if (Files.exists(p)) {
                return JsonParser.parseString(Files.readString(p, StandardCharsets.UTF_8)).getAsJsonObject();
            }
        } catch (IOException | RuntimeException e) {
            MmPerf.LOGGER.warn("[mm_perf] не прочитан {}: {}", name, e.toString());
        }
        return new JsonObject();
    }

    static void write(String name, JsonObject o) {
        try {
            Files.writeString(path(name), GSON.toJson(o), StandardCharsets.UTF_8);
        } catch (IOException e) {
            MmPerf.LOGGER.warn("[mm_perf] не записан {}: {}", name, e.toString());
        }
    }

    static void delete(String name) {
        try {
            Files.deleteIfExists(path(name));
        } catch (IOException ignored) {
        }
    }
}
