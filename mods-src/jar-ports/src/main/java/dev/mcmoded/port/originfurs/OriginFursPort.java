package dev.mcmoded.port.originfurs;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Порт Origin Furs (pandaismyname1, MIT) на AzureLib 3.1: старый API AzureLib 2.0.39 (AzureDoom, MIT) встроен
 * в jar мода в пакет dev.mcmoded.port.originfurs.azl (patches/jar-ports/origin-furs). Здесь — то, что у AzureLib 2
 * делала платформа (ServiceLoader → NeoForge*-классы, не встроены: их сетевой канал azurelib:main занят AzureLib 3.1).
 */
public final class OriginFursPort {
    private static final String AZL = "dev.mcmoded.port.originfurs.azl.";
    private static boolean initialized;

    private OriginFursPort() {}

    /** AzureLib.initialize(): на клиенте — загрузчик geo/ и animations/ в кэш встроенной копии. */
    public static void initialize() {
        if (initialized || !FMLEnvironment.dist.isClient()) return;
        initialized = true;
        try {
            Class.forName(AZL + "cache.AzureLibCache").getMethod("registerReloadListener").invoke(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Origin Furs port: AzureLibCache", e);
        }
    }

    /** Services.load(интерфейс) — заглушка: события рендера «не отменены», сеть и платформенные мелочи — минимум. */
    public static Object service(Class<?> itf) {
        return Proxy.newProxyInstance(itf.getClassLoader(), new Class<?>[]{itf}, (proxy, m, args) -> answer(itf, proxy, m, args));
    }

    private static Object answer(Class<?> itf, Object proxy, Method m, Object[] args) {
        switch (m.getName()) {
            case "toString": return "OriginFursPort$" + itf.getSimpleName();
            case "hashCode": return System.identityHashCode(proxy);
            case "equals": return proxy == args[0];
            case "isModLoaded": return ModList.get() != null && ModList.get().isLoaded((String) args[0]);
            case "isServerEnvironment": return FMLEnvironment.dist.isDedicatedServer();
            case "isDevelopmentEnvironment": return !FMLLoader.isProduction();
            case "getPlatformName": return "Forge";
            case "getEnvironmentName": return FMLLoader.isProduction() ? "production" : "development";
            case "getGameDir": return FMLPaths.GAMEDIR.get();
            case "modsDir": return FMLPaths.MODSDIR.get();
            case "initialize": initialize(); return null;
            default: break;
        }
        Class<?> r = m.getReturnType();
        if (r == boolean.class) return true;   // fire*PreRender: true — рендер не отменён
        if (!r.isPrimitive()) return null;
        if (r == void.class) return null;
        if (r == char.class) return '\0';
        if (r == long.class) return 0L;
        if (r == float.class) return 0f;
        if (r == double.class) return 0d;
        if (r == byte.class) return (byte) 0;
        if (r == short.class) return (short) 0;
        return 0;
    }
}
