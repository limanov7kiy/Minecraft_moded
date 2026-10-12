package dev.mcmoded.undeadrising;

import dev.mcmoded.undeadrising.effect.FurEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/// Частичный порт Fish's Undead Rising (Fish; ARR, порт по решению владельца сборки — см. NOTICE.md)
/// под Necromancer's Delight. Namespace оригинала `mod_lavacow`.
@Mod(UndeadRising.MOD_ID)
public class UndeadRising {
    public static final String MOD_ID = "mod_lavacow";

    public UndeadRising() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        FurEffects.EFFECTS.register(modBus);
        MinecraftForge.EVENT_BUS.register(FurEffects.class);
    }
}
