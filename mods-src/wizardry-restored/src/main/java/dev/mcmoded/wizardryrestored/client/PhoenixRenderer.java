package dev.mcmoded.wizardryrestored.client;

import dev.mcmoded.wizardryrestored.WizardryRestored;
import dev.mcmoded.wizardryrestored.entity.PhoenixEntity;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

/// Феникс — модель ифрита со своей огненной текстурой (нарисована скриптом `tools/make_icons.py`), светится.
public class PhoenixRenderer extends MobRenderer<PhoenixEntity, BlazeModel<PhoenixEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WizardryRestored.MOD_ID, "textures/entity/phoenix.png");

    public PhoenixRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BlazeModel<>(ctx.bakeLayer(ModelLayers.BLAZE)), 0.5f);
    }

    @Override
    protected int getBlockLightLevel(PhoenixEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(PhoenixEntity entity) {
        return TEXTURE;
    }
}
