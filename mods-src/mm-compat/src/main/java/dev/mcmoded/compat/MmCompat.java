package dev.mcmoded.compat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(MmCompat.MOD_ID)
public class MmCompat {
    public static final String MOD_ID = "mm_compat";

    public MmCompat() {
        SuitOxygen.register();
        MinecraftForge.EVENT_BUS.register(SuitOxygen.class);
    }
}
