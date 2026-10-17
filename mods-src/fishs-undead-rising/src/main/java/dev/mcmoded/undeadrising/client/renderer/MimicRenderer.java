package dev.mcmoded.undeadrising.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.undeadrising.client.ClientSetup;
import dev.mcmoded.undeadrising.client.model.MimicModel;
import dev.mcmoded.undeadrising.entity.MimicEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

/// Рендер мимикраба: тело краба (8 окрасов: обычные, пустотный, адский) + сам сундук поверх — ванильной текстурой
/// сундука (у пустотного — эндер-сундука), так что в засаде он выглядит как настоящий сундук. У адского — светящиеся
/// глаза. Оригинал: Fish's Undead Rising (Fish), `client/renderer/entity/MimicRenderer` + `client/layer/LayerMimicChest`.
public class MimicRenderer extends MobRenderer<MimicEntity, MimicModel<MimicEntity>> {
    private static final ResourceLocation[] TEXTURES = {tex("mimic"), tex("mimic2"), tex("mimic3"), tex("mimicvoid"), tex("mimic4"), tex("mimic5"),
            tex("mimicnether"), tex("mimic6")};
    private static final ResourceLocation TEXTURE_ENDER = new ResourceLocation("textures/entity/chest/ender.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");

    public MimicRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MimicModel<>(ctx.bakeLayer(ClientSetup.MIMIC_LAYER)), 0.4F);
        this.addLayer(new EyeLayer(this));
        this.addLayer(new ChestLayer(this));
    }

    private static ResourceLocation tex(String name) {
        return new ResourceLocation("mod_lavacow", "textures/mobs/mimic/" + name + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(MimicEntity entity) {
        return TEXTURES[Math.floorMod(entity.getSkin(), TEXTURES.length)];
    }

    @Override
    protected void scale(MimicEntity entity, PoseStack pose, float partialTick) {
        if (entity.isBaby()) pose.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    protected int getBlockLightLevel(MimicEntity entity, BlockPos pos) {
        return entity.getSkin() == MimicEntity.VOID_SKIN ? 7 : super.getBlockLightLevel(entity, pos);
    }

    static class EyeLayer extends RenderLayer<MimicEntity, MimicModel<MimicEntity>> {
        EyeLayer(RenderLayerParent<MimicEntity, MimicModel<MimicEntity>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack pose, MultiBufferSource buffers, int light, MimicEntity entity, float limbSwing, float limbSwingAmount,
                           float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity.getSkin() == MimicEntity.NETHER_SKIN) {
                VertexConsumer vc = buffers.getBuffer(RenderType.eyes(TEXTURES[MimicEntity.NETHER_SKIN]));
                this.getParentModel().renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    /// Сундук поверх краба (оригинал: `LayerMimicChest`).
    static class ChestLayer extends RenderLayer<MimicEntity, MimicModel<MimicEntity>> {
        ChestLayer(RenderLayerParent<MimicEntity, MimicModel<MimicEntity>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack pose, MultiBufferSource buffers, int light, MimicEntity entity, float limbSwing, float limbSwingAmount,
                           float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            ResourceLocation texture;
            if (entity.getSkin() == MimicEntity.VOID_SKIN) {
                texture = TEXTURE_ENDER;
            } else {
                String chest = entity.getChestTexture();
                texture = MimicEntity.TEXTURE_POOL.contains(chest) ? new ResourceLocation(chest) : TEXTURE_NORMAL;
            }
            VertexConsumer vc = buffers.getBuffer(RenderType.entityCutout(texture));
            this.getParentModel().renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
