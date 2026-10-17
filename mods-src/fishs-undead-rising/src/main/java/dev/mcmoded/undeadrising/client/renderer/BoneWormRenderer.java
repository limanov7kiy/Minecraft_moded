package dev.mcmoded.undeadrising.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.mcmoded.undeadrising.client.ClientSetup;
import dev.mcmoded.undeadrising.client.model.BoneWormModel;
import dev.mcmoded.undeadrising.entity.BoneWormEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/// Рендер Osvermis: закапываясь, уходит вниз со скручиванием и исчезает; у огненного (вариант 1) светятся глаза.
/// Оригинал: Fish's Undead Rising (Fish), `client/renderer/entity/BoneWormRenderer`.
public class BoneWormRenderer extends MobRenderer<BoneWormEntity, BoneWormModel<BoneWormEntity>> {
    private static final ResourceLocation TEXTURES_EYE = new ResourceLocation("mod_lavacow", "textures/mobs/boneworm/boneworm_glow.png");
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation("mod_lavacow", "textures/mobs/boneworm/boneworm.png"),
            new ResourceLocation("mod_lavacow", "textures/mobs/boneworm/boneworm1.png")};

    public BoneWormRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BoneWormModel<>(ctx.bakeLayer(ClientSetup.BONEWORM_LAYER)), 0.5F);
        this.addLayer(new EyeLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(BoneWormEntity entity) {
        return TEXTURES[Math.floorMod(entity.getSkin(), TEXTURES.length)];
    }

    @Override
    public void render(BoneWormEntity entity, float yaw, float partialTicks, PoseStack pose, MultiBufferSource buffers, int light) {
        this.shadowRadius = entity.getLocationFix() > 3.0 ? 0.0F : 0.5F;
        if (entity.getLocationFix() < 1.5) super.render(entity, yaw, partialTicks, pose, buffers, light);
    }

    @Override
    protected void setupRotations(BoneWormEntity entity, PoseStack pose, float ageInTicks, float rotationYaw, float partialTicks) {
        pose.translate(0.0, -entity.getLocationFix(), 0.0);
        pose.mulPose(Axis.YP.rotationDegrees(90.0F * (float) entity.getLocationFix()));
        super.setupRotations(entity, pose, ageInTicks, rotationYaw, partialTicks);
    }

    static class EyeLayer extends RenderLayer<BoneWormEntity, BoneWormModel<BoneWormEntity>> {
        EyeLayer(RenderLayerParent<BoneWormEntity, BoneWormModel<BoneWormEntity>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack pose, MultiBufferSource buffers, int light, BoneWormEntity entity, float limbSwing, float limbSwingAmount,
                           float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity.getSkin() != 0) {
                VertexConsumer vc = buffers.getBuffer(RenderType.eyes(TEXTURES_EYE));
                this.getParentModel().renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
            }
        }
    }
}
