package net.Gabou.gaboulibs.util;

import net.minecraft.server.level.ServerLevel;

/** Ссылка на Верхний мир запущенного сервера. */
public final class WorldContext {
    private static volatile ServerLevel overworld;

    private WorldContext() {
    }

    public static ServerLevel getOverworld() {
        return overworld;
    }

    public static void setOverworld(ServerLevel level) {
        overworld = level;
    }

    public static void clear() {
        overworld = null;
    }
}
