package dev.mcmoded.perf;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

/**
 * Клиентские удобства сборки (docs/performance.md, docs/friends.md): Режим слабого ПК (кнопка в настройках видео + клавиша),
 * клавиши модов по умолчанию «Не назначено», подсказки о нехватке памяти. Миксины JEI — в mm_perf.mixins.json.
 */
@Mod("mm_perf")
public final class MmPerf {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final String KEY_NAME = "key.mm_perf.lowspec";
    private static KeyMapping lowSpecKey;
    private static boolean keysDone;
    private static int ticks;

    public MmPerf() {
        if (FMLEnvironment.dist != Dist.CLIENT) return;
        MemoryWatch.earlyCheck();   // до загрузки ресурсов: если ПК зависнет, диагностика уже в логе
        FMLJavaModLoadingContext.get().getModEventBus().addListener(MmPerf::registerKeys);
        MinecraftForge.EVENT_BUS.addListener(MmPerf::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(MmPerf::onScreenInit);
    }

    private static void registerKeys(RegisterKeyMappingsEvent e) {
        lowSpecKey = new KeyMapping(KEY_NAME, InputConstants.UNKNOWN.getValue(), "key.categories.mm_perf");
        e.register(lowSpecKey);
    }

    private static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (!keysDone && mc.options != null && mc.getOverlay() == null) {   // настройки загружены, игра дошла до меню
            keysDone = true;
            String assign = System.getProperty("mm_perf.selftest.assign");   // тест: «игрок сам назначил» клавишу до миграции
            if (assign != null) {
                for (KeyMapping m : mc.options.keyMappings) {
                    if (m.getName().equals(assign)) m.setKey(InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_F9));
                }
                KeyMapping.resetMapping();
            }
            try {
                KeyDefaults.apply(mc, KEY_NAME);
            } catch (RuntimeException | LinkageError ex) {
                LOGGER.warn("[mm_perf] клавиши модов не обработаны: {}", ex.toString());
            }
            if (System.getProperty("mm_perf.selftest") != null) SelfTest.run(mc);
        }
        if (lowSpecKey != null) {
            while (lowSpecKey.consumeClick()) LowSpecMode.toggle();
        }
        if (++ticks % 20 == 0) MemoryWatch.tickSecond(mc);
    }

    private static void onScreenInit(ScreenEvent.Init.Post e) {
        Screen s = e.getScreen();
        if (s instanceof TitleScreen) {
            MemoryWatch.titleCheck(Minecraft.getInstance());
            return;
        }
        String cn = s.getClass().getName();
        // Настройки видео: ваниль или экран Embeddium (Sodium) / Oculus; кнопка внизу слева, чтобы не мешать «Готово» справа
        boolean video = s instanceof VideoSettingsScreen || cn.contains("SodiumOptionsGUI") || cn.contains("EmbeddiumOptionsGUI");
        if (!video && !(s instanceof OptionsScreen)) return;
        Component label = Component.translatable(LowSpecMode.isActive() ? "mm_perf.lowspec.button.off" : "mm_perf.lowspec.button.on");
        Button b = Button.builder(label, btn -> {
            LowSpecMode.toggle();
            btn.setMessage(Component.translatable(LowSpecMode.isActive() ? "mm_perf.lowspec.button.off" : "mm_perf.lowspec.button.on"));
        }).bounds(6, s.height - 26, 150, 20).build();
        e.addListener(b);
    }
}
