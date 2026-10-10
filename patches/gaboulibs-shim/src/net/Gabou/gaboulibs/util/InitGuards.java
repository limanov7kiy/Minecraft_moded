package net.Gabou.gaboulibs.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Защита от повторной инициализации: true только при первом вызове с данным ключом. */
public final class InitGuards {
    private static final Set<String> STARTED = ConcurrentHashMap.newKeySet();

    private InitGuards() {
    }

    public static boolean tryStart(String key) {
        return STARTED.add(key);
    }
}
