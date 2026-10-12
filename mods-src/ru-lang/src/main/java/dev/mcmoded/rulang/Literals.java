package dev.mcmoded.rulang;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * Словарь «китайская строка → русская» для литералов Component.literal в модах (тексты, зашитые в код/NBT, а не в lang).
 * Данные — assets/ru_lang/literals_ru.json (scripts/cjk_literals_harvest.py → перевод → scripts/literals_build.py).
 * Подстановку делает миксин на LiteralContents (клиент); строки без иероглифов не трогаются.
 */
public final class Literals {
    private static volatile Map<String, String> map;

    private Literals() {}

    private static Map<String, String> map() {
        Map<String, String> m = map;
        if (m == null) {
            synchronized (Literals.class) {
                if ((m = map) == null) {
                    try (Reader r = new InputStreamReader(Literals.class.getResourceAsStream("/assets/ru_lang/literals_ru.json"), StandardCharsets.UTF_8)) {
                        m = new Gson().fromJson(r, new TypeToken<Map<String, String>>() {}.getType());
                    } catch (Exception e) {
                        RuLang.LOGGER.error("ru_lang: не удалось прочитать literals_ru.json", e);
                        m = Collections.emptyMap();
                    }
                    map = m;
                }
            }
        }
        return m;
    }

    private static boolean hasCjk(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= 0x3040 && c <= 0x30ff) || (c >= 0x3400 && c <= 0x4dbf) || (c >= 0x4e00 && c <= 0x9fff) || (c >= 0xac00 && c <= 0xd7af)) return true;
        }
        return false;
    }

    /** Русский перевод литерала, либо сама строка, если перевода нет / иероглифов нет. */
    public static String translate(String s) {
        if (s == null || s.isEmpty() || !hasCjk(s)) return s;
        String r = map().get(s);
        return r != null ? r : s;
    }

    public static int size() { return map().size(); }
}
