package dev.mcmoded.gcrevival.client;

import dev.mcmoded.gcrevival.GCRevival;
import dev.mcmoded.gcrevival.ModRegistry;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GCRevival.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SkeletonBossRenderer.LAYER, SkeletonBossModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModRegistry.SKELETON_BOSS.get(), SkeletonBossRenderer::new);
        event.registerEntityRenderer(ModRegistry.EVOLVED_ZOMBIE.get(), ZombieRenderer::new);
        event.registerEntityRenderer(ModRegistry.EVOLVED_SKELETON.get(), SkeletonRenderer::new);
        event.registerEntityRenderer(ModRegistry.EVOLVED_SPIDER.get(), SpiderRenderer::new);
        event.registerEntityRenderer(ModRegistry.EVOLVED_CREEPER.get(), CreeperRenderer::new);
    }

    private ClientSetup() {
    }
}
