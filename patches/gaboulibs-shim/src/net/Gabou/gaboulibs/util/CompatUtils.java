package net.Gabou.gaboulibs.util;

import net.minecraftforge.fml.loading.FMLLoader;

/** Serene Seasons Plus и Project Atmosphere умеют одно и то же; решаем, кто из них «хозяин» общих функций. */
public final class CompatUtils {
    public enum PaSspHost { SERENE_SEASONS_PLUS, PROJECT_ATMOSPHERE, NONE }

    private CompatUtils() {
    }

    private static boolean loaded(String modId) {
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }

    public static boolean isSereneSeasonsPlusLoaded() {
        return loaded("sereneseasonsplus");
    }

    public static boolean isProjectAtmosphereLoaded() {
        return loaded("projectatmosphere");
    }

    public static PaSspHost getPaSspHost() {
        if (isSereneSeasonsPlusLoaded()) return PaSspHost.SERENE_SEASONS_PLUS;
        if (isProjectAtmosphereLoaded()) return PaSspHost.PROJECT_ATMOSPHERE;
        return PaSspHost.NONE;
    }

    public static boolean shouldThisModHostPaSspFeature(String modId) {
        PaSspHost host = getPaSspHost();
        return host == PaSspHost.SERENE_SEASONS_PLUS && "sereneseasonsplus".equals(modId)
            || host == PaSspHost.PROJECT_ATMOSPHERE && "projectatmosphere".equals(modId);
    }
}
