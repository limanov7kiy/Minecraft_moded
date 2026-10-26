package dev.mcmoded.compat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MmCompat.MOD_ID)
public class MmCompat {
    public static final String MOD_ID = "mm_compat";

    public MmCompat(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        SuitOxygen.register();
        MinecraftForge.EVENT_BUS.register(SuitOxygen.class);
        modBus.addListener(EntityCategoryFix::onSetup);
        MissingAttributes.register(modBus);
        RadiationSuitShielding.register();
    }
}
