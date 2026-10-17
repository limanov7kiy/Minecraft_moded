package dev.mcmoded.undeadrising.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mcmoded.undeadrising.client.layer.LayerGenericGlowing;
import dev.mcmoded.undeadrising.client.layer.LayerMycosis;
import dev.mcmoded.undeadrising.client.model.UnburiedModel;
import dev.mcmoded.undeadrising.entity.unburied.MycosisEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/// Рендер Mycosis: два варианта текстуры, грибы на голове и спине ({@link LayerMycosis}), светящиеся глаза только у
/// светящегося варианта.
/// Оригинал: Fish's Undead Rising (Fish), `client/renderer/entity/MycosisRenderer`.
public class MycosisRenderer extends UnburiedRenderer<MycosisEntity> {
    private static final ResourceLocation TEXTURES_EYE = tex("unburied3_eyes");
    private static final ResourceLocation[] TEXTURES = {tex("unburied2"), tex("unburied3")};

    public MycosisRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, TEXTURES[0], null);
    }

    @Override
    protected void addCommonLayers(EntityRendererProvider.Context ctx) {
        this.addLayer(new LayerMycosis<>(this));
        this.addLayer(new EyeLayer(this));
        super.addCommonLayers(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(MycosisEntity entity) {
        return TEXTURES[Math.floorMod(entity.getSkin(), TEXTURES.length)];
    }

    static class EyeLayer extends RenderLayer<MycosisEntity, UnburiedModel<MycosisEntity>> {
        EyeLayer(RenderLayerParent<MycosisEntity, UnburiedModel<MycosisEntity>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack pose, MultiBufferSource buffers, int light, MycosisEntity entity, float limbSwing,
                           float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity.getSkin() != 0 && LayerGenericGlowing.eyesVisible(entity)) {
                this.getParentModel().renderToBuffer(pose, buffers.getBuffer(RenderType.eyes(TEXTURES_EYE)), light,
                        OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
