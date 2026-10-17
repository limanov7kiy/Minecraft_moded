package dev.mcmoded.undeadrising;

import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.event.FurEntityEvents;
import dev.mcmoded.undeadrising.network.FurNetwork;
import dev.mcmoded.undeadrising.registry.FurBannerPatterns;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurItems;
import dev.mcmoded.undeadrising.registry.FurParticles;
import dev.mcmoded.undeadrising.registry.FurSounds;
import dev.mcmoded.undeadrising.registry.FurTabs;
import dev.mcmoded.undeadrising.world.AddStructureSpawnsModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/// Частичный порт Fish's Undead Rising (Fish; ARR, порт по решению владельца сборки — см. NOTICE.md)
/// под Necromancer's Delight. Namespace оригинала `mod_lavacow`.
///
/// Устройство: регистры — пакет `registry` (сущности, предметы, звуки, частицы, вкладка, узоры флагов), мобы —
/// `entity` (базовый приручаемый класс `FURTameableEntity`, семейство `entity/unburied`), клиент — `client/ClientSetup`
/// (рендеры, слои моделей, частицы), появление в мире — biome/structure modifiers в `data/mod_lavacow/forge/`.
@Mod(UndeadRising.MOD_ID)
public class UndeadRising {
    public static final String MOD_ID = "mod_lavacow";

    public UndeadRising() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        FurEffects.EFFECTS.register(modBus);
        FurSounds.SOUNDS.register(modBus);
        FurParticles.PARTICLES.register(modBus);
        FurEntities.ENTITIES.register(modBus);
        FurItems.ITEMS.register(modBus);
        FurTabs.TABS.register(modBus);
        FurBannerPatterns.PATTERNS.register(modBus);
        AddStructureSpawnsModifier.SERIALIZERS.register(modBus);
        modBus.register(FurEntities.class);
        modBus.addListener((FMLCommonSetupEvent e) -> e.enqueueWork(FurNetwork::register));

        MinecraftForge.EVENT_BUS.register(FurEffects.class);
        MinecraftForge.EVENT_BUS.register(FurEntityEvents.class);
    }
}
