package dev.mcmoded.necromancersdelight.client;

import dev.mcmoded.necromancersdelight.NecromancersDelight;
import dev.mcmoded.necromancersdelight.entity.NdBombs;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/// Бомбы рисуются своим предметом, как гранаты Redux.
@Mod.EventBusSubscriber(modid = NecromancersDelight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NdBombs.STINK_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(NdBombs.FROST_BOMB.get(), ThrownItemRenderer::new);
    }
}
