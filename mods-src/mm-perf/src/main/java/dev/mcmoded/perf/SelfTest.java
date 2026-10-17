package dev.mcmoded.perf;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

/** Проверка в запущенной игре ({@code -Dmm_perf.selftest=true}, scripts/test-client.sh): строки {@code MMPERF-SELFTEST} в latest.log. */
final class SelfTest {
    private SelfTest() {
    }

    static void run(Minecraft mc) {
        Options o = mc.options;
        int vanilla = 0, vanillaBound = 0, modded = 0, moddedBound = 0, moddedDefaultBound = 0;
        for (KeyMapping m : o.keyMappings) {
            if (KeyDefaults.isVanilla(m)) {
                vanilla++;
                if (!m.isUnbound()) vanillaBound++;
            } else {
                modded++;
                if (!m.isUnbound()) {
                    moddedBound++;
                    MmPerf.LOGGER.info("MMPERF-SELFTEST bound-mod-key {} = {} (default {})", m.getName(), m.saveString(), m.getDefaultKey().getName());
                }
                if (!m.getDefaultKey().equals(com.mojang.blaze3d.platform.InputConstants.UNKNOWN)) moddedDefaultBound++;
            }
        }
        MmPerf.LOGGER.info("MMPERF-SELFTEST keys: vanilla {} (назначено {}), mods {} (назначено {}, заводское не пустое {}); key.forward={}",
            vanilla, vanillaBound, modded, moddedBound, moddedDefaultBound, o.keyUp.saveString());
        String before = snapshot(o);
        boolean wasOn = LowSpecMode.isActive();
        LowSpecMode.toggle();
        String on = snapshot(o);
        boolean active = LowSpecMode.isActive();
        LowSpecMode.toggle();
        String after = snapshot(o);
        MmPerf.LOGGER.info("MMPERF-SELFTEST lowspec before[{}] on[{}] (active={}) after[{}] restored={} wasOn={}", before, on, active, after, before.equals(after), wasOn);
    }

    private static String snapshot(Options o) {
        return "rd=" + o.renderDistance().get() + " sim=" + o.simulationDistance().get() + " gfx=" + o.graphicsMode().get()
            + " clouds=" + o.cloudStatus().get() + " part=" + o.particles().get() + " ao=" + o.ambientOcclusion().get()
            + " fps=" + o.framerateLimit().get() + " vsync=" + o.enableVsync().get();
    }
}
