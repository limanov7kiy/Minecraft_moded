package dev.mcmoded.disasterward;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Публичные проверки зоны (их же вызывают тесты scripts/tests/disasterward.js и sporeward.js). */
public final class WardApi {
    private WardApi() {}

    /** Сколько раз оберег развернул бурю Weather2 (для теста). */
    public static long stormsSteered;

    /** Лежит ли точка в зоне какого-либо оберега (по X/Z, на всю высоту), с запасом {@code margin}. */
    public static boolean isProtected(ServerLevel level, double x, double z, int margin) {
        return WardIndex.inZone(WardIndex.get(level), x, z, margin);
    }

    public static boolean isProtected(ServerLevel level, double x, double z) {
        return isProtected(level, x, z, 0);
    }

    /** Сколько изменений блоков в зоне отклонено с запуска сервера. */
    public static long blocksDenied() { return WardGuard.denied; }

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

    /**
     * Убрать оберег из «Отражателей бурь» Weather2. До потока 22 оберег регистрировался отражателем, но радиус отражателя в Weather2 зашит (128),
     * а теперь бури разворачиваем сами по единому радиусу оберега — старые записи из сохранения Weather2 надо снять.
     */
    static void weatherUnregister(Level level, BlockPos pos) {
        try {
            Object mgr = weatherManager(level);
            if (mgr != null) mgr.getClass().getMethod("removeDeflector", BlockPos.class).invoke(mgr, pos);
        } catch (ClassNotFoundException e) {
            // Weather2 не установлен
        } catch (ReflectiveOperationException | LinkageError e) {
            WardMod.LOGGER.warn("Weather2: не удалось снять отражатель: {}", e.toString());
        }
    }

    private static boolean w2Broken;
    private static Class<?> stormClass;
    private static Field stageField, posGroundField, formingField;
    private static Method aimAway, stormList;

    /** Развернуть бури Weather2, чей центр подошёл к оберегу ближе radius + edgeMargin (бури — не сущности, поэтому вручную). */
    static void steerStorms(ServerLevel level, BlockPos[] wards) {
        if (w2Broken || wards.length == 0) return;
        try {
            if (stormClass == null) {
                stormClass = Class.forName("weather2.weathersystem.storm.StormObject");
                Class<?> wo = Class.forName("weather2.weathersystem.storm.WeatherObject");
                stageField = stormClass.getField("levelCurIntensityStage");
                formingField = stormClass.getField("STATE_FORMING");
                posGroundField = wo.getField("posGround");
                aimAway = stormClass.getMethod("aimAwayFromCoords", Vec3.class);
                stormList = Class.forName("weather2.weathersystem.WeatherManager").getMethod("getStormObjects");
            }
            Object mgr = weatherManager(level);
            if (mgr == null) return;
            int forming = formingField.getInt(null);
            for (Object so : (List<?>) stormList.invoke(mgr)) {
                if (!stormClass.isInstance(so) || stageField.getInt(so) < forming) continue;
                Vec3 g = (Vec3) posGroundField.get(so);
                BlockPos w = WardIndex.owner(wards, g.x, g.z, WardConfig.EDGE_MARGIN.get());
                if (w == null) continue;
                aimAway.invoke(so, new Vec3(w.getX() + 0.5, w.getY(), w.getZ() + 0.5));
                stormsSteered++;
            }
        } catch (ClassNotFoundException e) {
            w2Broken = true;   // Weather2 не установлен
        } catch (ReflectiveOperationException | LinkageError | ClassCastException e) {
            w2Broken = true;
            WardMod.LOGGER.warn("Weather2: не удалось развернуть бурю, разворот отключён до перезапуска: {}", e.toString());
        }
    }
}
