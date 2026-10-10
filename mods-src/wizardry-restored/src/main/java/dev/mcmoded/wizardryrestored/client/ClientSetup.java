package dev.mcmoded.wizardryrestored.client;

import dev.mcmoded.wizardryrestored.WizardryRestored;
import dev.mcmoded.wizardryrestored.entity.ModEntities;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/// Рендер сущностей — только на клиенте. Силовое поле и туча видны по частицам, тотемы и чёрная дыра —
/// модели из ванильных блоков, приманка — модель игрока.
@Mod.EventBusSubscriber(modid = WizardryRestored.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FORCEFIELD.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.STORMCLOUD.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.RADIANT_TOTEM.get(), ctx -> new BlockStackRenderer<>(ctx, 0.5f, 0,
                Blocks.QUARTZ_PILLAR.defaultBlockState(), Blocks.CHISELED_QUARTZ_BLOCK.defaultBlockState(), Blocks.GLOWSTONE.defaultBlockState()));
        event.registerEntityRenderer(ModEntities.WITHERING_TOTEM.get(), ctx -> new BlockStackRenderer<>(ctx, 0.5f, 0,
                Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), Blocks.CHISELED_POLISHED_BLACKSTONE.defaultBlockState(), Blocks.CRYING_OBSIDIAN.defaultBlockState()));
        event.registerEntityRenderer(ModEntities.BLACK_HOLE.get(), ctx -> new BlockStackRenderer<>(ctx, 0.7f, 0.15f, Blocks.BLACK_CONCRETE.defaultBlockState()));
        event.registerEntityRenderer(ModEntities.DECOY.get(), DecoyRenderer::new);
        event.registerEntityRenderer(ModEntities.PHOENIX.get(), PhoenixRenderer::new);
        event.registerEntityRenderer(ModEntities.LIGHTNING_DISC.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.SNARE.get(), ctx -> new BlockStackRenderer<>(ctx, 0.8f, 0, false, Blocks.SPRUCE_PRESSURE_PLATE.defaultBlockState()));
        event.registerEntityRenderer(ModEntities.THORNS.get(), ctx -> new BlockStackRenderer<>(ctx, 1f, 0, false,
                Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(net.minecraft.world.level.block.SweetBerryBushBlock.AGE, 1)));
        event.registerEntityRenderer(ModEntities.LIGHTNING_HAMMER.get(), ctx -> new BlockStackRenderer<>(ctx, 0.8f, 0, false,
                Blocks.IRON_BLOCK.defaultBlockState(), Blocks.LIGHTNING_ROD.defaultBlockState()));
    }
}
