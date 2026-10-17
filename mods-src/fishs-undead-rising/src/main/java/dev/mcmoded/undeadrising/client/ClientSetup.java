package dev.mcmoded.undeadrising.client;

import dev.mcmoded.undeadrising.UndeadRising;
import dev.mcmoded.undeadrising.client.layer.LayerMycosis;
import dev.mcmoded.undeadrising.client.model.UnburiedModel;
import dev.mcmoded.undeadrising.client.particle.LocustSwarmParticle;
import dev.mcmoded.undeadrising.client.renderer.DeathCoilRenderer;
import dev.mcmoded.undeadrising.client.renderer.ForsakenRenderer;
import dev.mcmoded.undeadrising.client.renderer.MycosisRenderer;
import dev.mcmoded.undeadrising.client.renderer.UnburiedRenderer;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurParticles;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/// Клиентская регистрация (оригинал: Fish's Undead Rising (Fish), `ClientProxy`): слои моделей, рендеры, частицы,
/// дополнительные модели блоков для слоёв. Новый моб: `ModelLayerLocation` + `createBodyLayer` в {@link #onLayers},
/// рендер в {@link #onRenderers}.
@Mod.EventBusSubscriber(modid = UndeadRising.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    public static final ModelLayerLocation UNBURIED_LAYER = layer("unburied");
    public static final ModelLayerLocation FORSAKEN_LAYER = layer("forsaken");

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(UndeadRising.MOD_ID, name), "main");
    }

    @SubscribeEvent
    public static void onLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(UNBURIED_LAYER, UnburiedModel::createBodyLayer);
        event.registerLayerDefinition(FORSAKEN_LAYER, SkeletonModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(FurEntities.UNBURIED.get(), ctx -> new UnburiedRenderer<>(ctx, UnburiedRenderer.UNBURIED, UnburiedRenderer.UNBURIED_EYES));
        event.registerEntityRenderer(FurEntities.MUMMY.get(), ctx -> new UnburiedRenderer<>(ctx, UnburiedRenderer.MUMMY, null));
        event.registerEntityRenderer(FurEntities.FRIGID.get(), ctx -> new UnburiedRenderer<>(ctx, UnburiedRenderer.FRIGID, UnburiedRenderer.FRIGID_EYES));
        event.registerEntityRenderer(FurEntities.MYCOSIS.get(), MycosisRenderer::new);
        event.registerEntityRenderer(FurEntities.FORSAKEN.get(), ForsakenRenderer::new);
        event.registerEntityRenderer(FurEntities.DEATHCOIL.get(), DeathCoilRenderer::new);
    }

    @SubscribeEvent
    public static void onParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FurParticles.LOCUST_SWARM.get(), LocustSwarmParticle.Provider::new);
        event.registerSpriteSet(FurParticles.WITHER_FLAME.get(), FlameParticle.Provider::new);
    }

    /// Модели блоков-грибов для {@link LayerMycosis} (сами блоки не зарегистрированы).
    @SubscribeEvent
    public static void onRegisterModels(ModelEvent.RegisterAdditional event) {
        event.register(LayerMycosis.GLOWSHROOM_MODEL);
        event.register(LayerMycosis.CORDY_SHROOM_MODEL);
    }
}
