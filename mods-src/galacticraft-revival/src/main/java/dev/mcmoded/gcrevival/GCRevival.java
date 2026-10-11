package dev.mcmoded.gcrevival;

import com.mojang.logging.LogUtils;
import dev.mcmoded.gcrevival.entity.SkeletonBoss;
import dev.mcmoded.gcrevival.entity.evolved.EvolvedCreeper;
import dev.mcmoded.gcrevival.entity.evolved.EvolvedSkeleton;
import dev.mcmoded.gcrevival.entity.evolved.EvolvedSpider;
import dev.mcmoded.gcrevival.entity.evolved.EvolvedZombie;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Galacticraft Revival — приключенческая часть Galacticraft поверх Ad Astra.
 * Порт из Galacticraft Legacy (micdoodle8, radfast, Team Galacticraft) и Galacticraft 5 (Team Galacticraft),
 * см. CREDITS.md. Своих планет нет: данжи встают в измерения Ad Astra.
 */
@Mod(GCRevival.MOD_ID)
public class GCRevival {
    public static final String MOD_ID = "gcrevival";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GCRevival() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.register(bus);
        bus.addListener(GCRevival::attributes);
        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(ModRegistry.SKELETON_BOSS.get(), SkeletonBoss.createAttributes().build());
        event.put(ModRegistry.EVOLVED_ZOMBIE.get(), EvolvedZombie.createAttributes().build());
        event.put(ModRegistry.EVOLVED_SKELETON.get(), EvolvedSkeleton.createAttributes().build());
        event.put(ModRegistry.EVOLVED_SPIDER.get(), EvolvedSpider.createAttributes().build());
        event.put(ModRegistry.EVOLVED_CREEPER.get(), EvolvedCreeper.createAttributes().build());
    }
}
