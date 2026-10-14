package dev.mcmoded.disasterward;

import java.lang.reflect.Method;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/** Публичные проверки зоны (их же вызывает тест scripts/tests/disasterward.js). */
public final class WardApi {
    private WardApi() {}

    /** Лежит ли точка в зоне какого-либо оберега (по X/Z, на всю высоту), с запасом {@code margin}. */
    public static boolean isProtected(ServerLevel level, double x, double z, int margin) {
        WardData data = WardData.get(level);
        if (data.isEmpty()) return false;
        double r = WardConfig.RADIUS.get() + margin;
        for (BlockPos p : data.positions()) {
            if (Math.abs(x - (p.getX() + 0.5)) <= r && Math.abs(z - (p.getZ() + 0.5)) <= r) return true;
        }
        return false;
    }

    public static boolean isProtected(ServerLevel level, double x, double z) {
        return isProtected(level, x, z, 0);
    }

    /** Знает ли Weather2 об отражателе рядом с точкой (мягкая связь, рефлексия). */
    public static boolean weatherDeflected(ServerLevel level, BlockPos pos, int radius) {
        try {
            Object mgr = weatherManager(level);
            if (mgr == null) return false;
            Method find = mgr.getClass().getMethod("findWeatherDeflector", BlockPos.class, int.class);
            return ((Optional<?>) find.invoke(mgr, pos, radius)).isPresent();
        } catch (ReflectiveOperationException | LinkageError e) {
            return false;
        }
    }

    static Object weatherManager(Level level) throws ReflectiveOperationException {
        Class<?> h = Class.forName("weather2.ServerTickHandler");
        return h.getMethod("getWeatherManagerFor", Level.class).invoke(null, level);
    }

    static void weatherRegister(Level level, BlockPos pos, boolean add) {
        try {
            Object mgr = weatherManager(level);
            if (mgr == null) return;
            mgr.getClass().getMethod(add ? "registerDeflector" : "removeDeflector", BlockPos.class).invoke(mgr, pos);
        } catch (ClassNotFoundException e) {
            // Weather2 не установлен — нечего защищать
        } catch (ReflectiveOperationException | LinkageError e) {
            WardMod.LOGGER.warn("Weather2: не удалось (раз)регистрировать отражатель: {}", e.toString());
        }
    }
}
