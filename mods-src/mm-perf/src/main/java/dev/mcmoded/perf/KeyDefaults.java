package dev.mcmoded.perf;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Set;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

/**
 * Клавиши модов по умолчанию «Не назначено» (решение владельца В28): у каждой не ванильной клавиши заводское значение
 * и значение «Сброса» = не назначено. Ванильные не трогаются. Один раз при первом запуске этой версии (флаг в
 * {@code config/mm-perf-keys.json}) у уже играющих отвязываются те клавиши модов, что стоят на заводском значении;
 * назначенные игроком вручную остаются.
 */
final class KeyDefaults {
    /** Имена ванильных {@code KeyMapping} 1.20.1 (net.minecraft.client.Options). */
    private static final Set<String> VANILLA = Set.of(
        "key.attack", "key.use", "key.forward", "key.left", "key.back", "key.right", "key.jump", "key.sneak", "key.sprint",
        "key.drop", "key.inventory", "key.chat", "key.playerlist", "key.pickItem", "key.command", "key.socialInteractions",
        "key.screenshot", "key.togglePerspective", "key.smoothCamera", "key.fullscreen", "key.spectatorOutlines",
        "key.swapOffhand", "key.saveToolbarActivator", "key.loadToolbarActivator", "key.advancements");

    private static final Set<String> VANILLA_CATEGORIES = Set.of("key.categories.movement", "key.categories.gameplay",
        "key.categories.inventory", "key.categories.multiplayer", "key.categories.misc", "key.categories.ui", "key.categories.creative");

    private KeyDefaults() {
    }

    static boolean isVanilla(KeyMapping m) {
        String n = m.getName();
        return VANILLA.contains(n) || n.startsWith("key.hotbar.");
    }

    /** @return сколько клавиш отвязано при миграции (0, если миграция уже была). */
    static int apply(Minecraft mc, String ownKeyName) {
        JsonObject flag = PerfJson.read("keys");
        boolean migrate = !flag.has("migrated");
        int unbound = 0, modded = 0, shared = 0;
        StringBuilder sharedNames = new StringBuilder();
        for (KeyMapping m : mc.options.keyMappings) {
            if (isVanilla(m) || m.getName().equals(ownKeyName)) continue;
            modded++;
            if (VANILLA_CATEGORIES.contains(m.getCategory())) {   // клавиша мода в общей категории — в списке не видно, чей это мод
                shared++;
                if (sharedNames.length() < 1500) sharedNames.append(m.getName()).append(' ');
            }
            boolean atFactory = m.getKey().equals(m.getDefaultKey()) && m.getKeyModifier() == m.getDefaultKeyModifier();
            ObfuscationReflectionHelper.setPrivateValue(KeyMapping.class, m, InputConstants.UNKNOWN, "f_90814_");   // KeyMapping.defaultKey
            if (migrate && atFactory && m.getKey() != InputConstants.UNKNOWN) {
                m.setKeyModifierAndCode(KeyModifier.NONE, InputConstants.UNKNOWN);
                unbound++;
            }
        }
        MmPerf.LOGGER.info("[mm_perf] клавиши модов: всего {}, в общих (ванильных) категориях {}: {}", modded, shared, sharedNames.toString().trim());
        if (migrate) {
            KeyMapping.resetMapping();
            mc.options.save();
            JsonObject j = new JsonObject();
            j.addProperty("migrated", true);
            j.addProperty("unbound", unbound);
            PerfJson.write("keys", j);
            MmPerf.LOGGER.info("[mm_perf] клавиши модов: всего {}, отвязано заводских {}", modded, unbound);
        }
        return unbound;
    }
}
