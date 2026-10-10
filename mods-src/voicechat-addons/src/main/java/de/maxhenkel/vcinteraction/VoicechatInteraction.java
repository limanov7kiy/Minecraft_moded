package de.maxhenkel.vcinteraction;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.vcinteraction.config.ServerConfig;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Порт на Forge (Minecraft_moded): вместо ModInitializer — @Mod, событие регистрируется DeferredRegister'ом,
// частота вибрации пишется прямо в VibrationSystem (на Fabric это делал SculkSensorFrequencyRegistry).
@Mod(VoicechatInteraction.MODID)
public class VoicechatInteraction {

    public static final String MODID = "vcinteraction";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static ServerConfig SERVER_CONFIG;

    private static final DeferredRegister<GameEvent> GAME_EVENTS = DeferredRegister.create(Registries.GAME_EVENT, MODID);
    private static final RegistryObject<GameEvent> VOICE = GAME_EVENTS.register("voice", () -> new GameEvent("voice", 16));

    public static GameEvent VOICE_GAME_EVENT;

    public VoicechatInteraction() {
        SERVER_CONFIG = ConfigBuilder
                .builder(ServerConfig::new)
                .path(FMLPaths.CONFIGDIR.get().resolve(MODID).resolve("%s.properties".formatted(MODID)))
                .build();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        GAME_EVENTS.register(modBus);
        modBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(() -> {
            VOICE_GAME_EVENT = VOICE.get();
            // в 1.20.1 поле объявлено как ToIntFunction, но внутри — изменяемая Object2IntOpenHashMap
            if (VibrationSystem.VIBRATION_FREQUENCY_FOR_EVENT instanceof Object2IntMap<GameEvent> map) {
                map.put(VOICE_GAME_EVENT, SERVER_CONFIG.voiceSculkFrequency.get().intValue());
            } else {
                LOGGER.error("Unable to register vibration frequency for {}", VOICE_GAME_EVENT);
            }
        }));
    }
}
