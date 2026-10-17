package dev.mcmoded.perf;

import com.google.gson.JsonObject;
import java.lang.reflect.Method;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

/**
 * «Режим слабого ПК»: одним нажатием ставит самые экономные настройки графики, «Вернуть как было» возвращает прежние
 * (они лежат в {@code config/mm-perf-lowspec.json}, пока режим включён). Сторонние моды (Embeddium, Oculus,
 * Distant Horizons) трогаются через отражение и только если есть: нет мода или API изменился — шаг пропускается.
 */
public final class LowSpecMode {
    private static final String FILE = "lowspec";

    private LowSpecMode() {
    }

    public static boolean isActive() {
        return PerfJson.read(FILE).has("saved");
    }

    public static void toggle() {
        Minecraft mc = Minecraft.getInstance();
        boolean on = !isActive();
        if (on) {
            apply(mc);
        } else {
            restore(mc);
        }
        SystemToast.add(mc.getToasts(), SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
            Component.translatable(on ? "mm_perf.lowspec.on.title" : "mm_perf.lowspec.off.title"),
            Component.translatable(on ? "mm_perf.lowspec.on.text" : "mm_perf.lowspec.off.text"));
    }

    // ---- включить ----

    private static void apply(Minecraft mc) {
        Options o = mc.options;
        JsonObject saved = new JsonObject();
        saved.addProperty("renderDistance", o.renderDistance().get());
        saved.addProperty("simulationDistance", o.simulationDistance().get());
        saved.addProperty("graphics", o.graphicsMode().get().name());
        saved.addProperty("clouds", o.cloudStatus().get().name());
        saved.addProperty("particles", o.particles().get().name());
        saved.addProperty("ao", o.ambientOcclusion().get());
        saved.addProperty("shadows", o.entityShadows().get());
        saved.addProperty("biomeBlend", o.biomeBlendRadius().get());
        saved.addProperty("mipmaps", o.mipmapLevels().get());
        saved.addProperty("fps", o.framerateLimit().get());
        saved.addProperty("vsync", o.enableVsync().get());
        JsonObject emb = Embeddium.snapshot();
        if (emb != null) saved.add("embeddium", emb);
        Boolean shaders = Oculus.shadersEnabled();
        if (shaders != null) saved.addProperty("shaders", shaders);
        Boolean dh = DistantHorizons.rendering();
        if (dh != null) saved.addProperty("dh", dh);
        JsonObject root = new JsonObject();
        root.add("saved", saved);
        PerfJson.write(FILE, root);   // сначала сохраняем прежнее, потом меняем

        o.renderDistance().set(5);
        o.simulationDistance().set(5);
        o.graphicsMode().set(GraphicsStatus.FAST);
        o.cloudStatus().set(CloudStatus.OFF);
        o.particles().set(ParticleStatus.MINIMAL);
        o.ambientOcclusion().set(false);
        o.entityShadows().set(false);
        o.biomeBlendRadius().set(0);
        o.mipmapLevels().set(0);
        o.framerateLimit().set(60);
        o.enableVsync().set(false);
        Embeddium.economy();
        Oculus.setShaders(false);
        DistantHorizons.setRendering(false);
        o.save();
    }

    // ---- вернуть ----

    private static void restore(Minecraft mc) {
        JsonObject s = PerfJson.read(FILE).getAsJsonObject("saved");
        if (s != null) {
            Options o = mc.options;
            if (s.has("renderDistance")) o.renderDistance().set(s.get("renderDistance").getAsInt());
            if (s.has("simulationDistance")) o.simulationDistance().set(s.get("simulationDistance").getAsInt());
            if (s.has("graphics")) o.graphicsMode().set(GraphicsStatus.valueOf(s.get("graphics").getAsString()));
            if (s.has("clouds")) o.cloudStatus().set(CloudStatus.valueOf(s.get("clouds").getAsString()));
            if (s.has("particles")) o.particles().set(ParticleStatus.valueOf(s.get("particles").getAsString()));
            if (s.has("ao")) o.ambientOcclusion().set(s.get("ao").getAsBoolean());
            if (s.has("shadows")) o.entityShadows().set(s.get("shadows").getAsBoolean());
            if (s.has("biomeBlend")) o.biomeBlendRadius().set(s.get("biomeBlend").getAsInt());
            if (s.has("mipmaps")) o.mipmapLevels().set(s.get("mipmaps").getAsInt());
            if (s.has("fps")) o.framerateLimit().set(s.get("fps").getAsInt());
            if (s.has("vsync")) o.enableVsync().set(s.get("vsync").getAsBoolean());
            if (s.has("embeddium")) Embeddium.restore(s.getAsJsonObject("embeddium"));
            if (s.has("shaders")) Oculus.setShaders(s.get("shaders").getAsBoolean());
            if (s.has("dh")) DistantHorizons.setRendering(s.get("dh").getAsBoolean());
            o.save();
        }
        PerfJson.delete(FILE);
    }

    // ---- Embeddium (порт Sodium): отражение по SodiumClientMod.options() ----

    static final class Embeddium {
        private static Object options() throws ReflectiveOperationException {
            return Class.forName("me.jellysquid.mods.sodium.client.SodiumClientMod").getMethod("options").invoke(null);
        }

        static JsonObject snapshot() {
            try {
                Object q = options().getClass().getField("quality").get(options());
                JsonObject j = new JsonObject();
                j.addProperty("weather", String.valueOf(q.getClass().getField("weatherQuality").get(q)));
                j.addProperty("leaves", String.valueOf(q.getClass().getField("leavesQuality").get(q)));
                j.addProperty("vignette", (Boolean) q.getClass().getField("enableVignette").get(q));
                return j;
            } catch (ReflectiveOperationException | LinkageError e) {
                return null;
            }
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static void set(JsonObject j, boolean economy) {
            try {
                Object opts = options();
                Object q = opts.getClass().getField("quality").get(opts);
                Class<?> qc = q.getClass();
                Class<? extends Enum> en = (Class<? extends Enum>) qc.getField("weatherQuality").getType();
                qc.getField("weatherQuality").set(q, Enum.valueOf(en, economy ? "FAST" : j.get("weather").getAsString()));
                qc.getField("leavesQuality").set(q, Enum.valueOf(en, economy ? "FAST" : j.get("leaves").getAsString()));
                qc.getField("enableVignette").set(q, economy ? Boolean.FALSE : j.get("vignette").getAsBoolean());
                Method w = opts.getClass().getMethod("writeChanges");
                w.invoke(opts);
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
                MmPerf.LOGGER.info("[mm_perf] Embeddium: настройки не изменены ({})", e.toString());
            }
        }

        static void economy() {
            set(null, true);
        }

        static void restore(JsonObject j) {
            set(j, false);
        }
    }

    // ---- Oculus (порт Iris): шейдеры ----

    static final class Oculus {
        static Boolean shadersEnabled() {
            try {
                Object cfg = Class.forName("net.irisshaders.iris.Iris").getMethod("getIrisConfig").invoke(null);
                return (Boolean) cfg.getClass().getMethod("areShadersEnabled").invoke(cfg);
            } catch (ReflectiveOperationException | LinkageError e) {
                return null;
            }
        }

        static void setShaders(boolean on) {
            try {
                Boolean now = shadersEnabled();
                if (now == null || now == on) return;
                Class.forName("net.irisshaders.iris.Iris").getMethod("toggleShaders", Minecraft.class, boolean.class)
                    .invoke(null, Minecraft.getInstance(), on);
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
                MmPerf.LOGGER.info("[mm_perf] Oculus: шейдеры не переключены ({})", e.toString());
            }
        }
    }

    // ---- Distant Horizons: DhApi.Delayed.configs.graphics().renderingEnabled() ----

    static final class DistantHorizons {
        private static Object value() throws ReflectiveOperationException {
            Object configs = Class.forName("com.seibel.distanthorizons.api.DhApi$Delayed").getField("configs").get(null);
            if (configs == null) return null;   // DH ещё не запущен
            Object graphics = Class.forName("com.seibel.distanthorizons.api.interfaces.config.IDhApiConfig").getMethod("graphics").invoke(configs);
            return Class.forName("com.seibel.distanthorizons.api.interfaces.config.client.IDhApiGraphicsConfig")
                .getMethod("renderingEnabled").invoke(graphics);
        }

        private static Class<?> cfgValue() throws ClassNotFoundException {
            return Class.forName("com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue");
        }

        static Boolean rendering() {
            try {
                Object v = value();
                return v == null ? null : (Boolean) cfgValue().getMethod("getValue").invoke(v);
            } catch (ReflectiveOperationException | LinkageError e) {
                return null;
            }
        }

        static void setRendering(boolean on) {
            try {
                Object v = value();
                if (v != null) cfgValue().getMethod("setValue", Object.class).invoke(v, on);
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
                MmPerf.LOGGER.info("[mm_perf] Distant Horizons: рендер не переключён ({})", e.toString());
            }
        }
    }
}
