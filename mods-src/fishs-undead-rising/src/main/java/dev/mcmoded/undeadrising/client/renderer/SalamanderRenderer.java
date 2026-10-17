package dev.mcmoded.undeadrising.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.undeadrising.client.ClientSetup;
import dev.mcmoded.undeadrising.client.layer.LayerGenericGlowing;
import dev.mcmoded.undeadrising.client.model.SalamanderModel;
import dev.mcmoded.undeadrising.entity.SalamanderEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

/// Рендер саламандры: два окраса (огонь / души) у взрослой и нимфы, седло, светящиеся пятна в темноте; размер по
/// стадии роста (нимфа 1, затем 0,8 / 1,25 / 1,5), сама светится (уровень света блока 5).
/// Оригинал: Fish's Undead Rising (Fish), `client/renderer/entity/SalamanderRenderer` + `client/layer/LayerSalamander`.
public class SalamanderRenderer extends MobRenderer<SalamanderEntity, SalamanderModel<SalamanderEntity>> {
    private static final ResourceLocation[] TEXTURES = {tex("salamander"), tex("salamander1")};
    private static final ResourceLocation[] TEXTURES_CHILD = {tex("salamanderlesser"), tex("salamanderlesser1")};
    private static final ResourceLocation[] TEXTURES_GLOW = {tex("salamander_glow"), tex("salamander_glow1"), tex("salamanderlesser_glow"), tex("salamanderlesser_glow1")};
    private static final ResourceLocation SADDLE = tex("salamander_saddle");

    private final SalamanderModel<SalamanderEntity> saddleModel;

    public SalamanderRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SalamanderModel<>(ctx.bakeLayer(ClientSetup.SALAMANDER_LAYER), ctx.bakeLayer(ClientSetup.SALAMANDER_NYMPH_LAYER)), 1.0F);
        this.saddleModel = new SalamanderModel<>(ctx.bakeLayer(ClientSetup.SALAMANDER_LAYER), ctx.bakeLayer(ClientSetup.SALAMANDER_NYMPH_LAYER));
        this.addLayer(new SaddleLayer<>(this, this.saddleModel, SADDLE));
        this.addLayer(new GlowLayer(this));
    }

    private static ResourceLocation tex(String name) {
        return new ResourceLocation("mod_lavacow", "textures/mobs/salamander/" + name + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(SalamanderEntity entity) {
        int skin = Math.floorMod(entity.getSkin(), 2);
        return entity.isNymph() ? TEXTURES_CHILD[skin] : TEXTURES[skin];
    }

    @Override
    protected void scale(SalamanderEntity entity, PoseStack pose, float partialTick) {
        float s = switch (entity.getGrowingStage()) {
            case 0 -> 1.0F;
            case 1 -> 0.8F;
            case 2 -> 1.25F;
            default -> 1.5F;
        };
        pose.scale(s, s, s);
    }

    @Override
    public void render(SalamanderEntity entity, float yaw, float partialTicks, PoseStack pose, MultiBufferSource buffers, int light) {
        this.model.nymph = entity.isNymph();
        this.saddleModel.nymph = entity.isNymph();
        super.render(entity, yaw, partialTicks, pose, buffers, light);
    }

    @Override
    protected int getBlockLightLevel(SalamanderEntity entity, BlockPos pos) {
        return 5;
    }

    /// Светящиеся пятна (ночью, в Незере, без неба), приглушённые вдвое.
    static class GlowLayer extends RenderLayer<SalamanderEntity, SalamanderModel<SalamanderEntity>> {
        GlowLayer(RenderLayerParent<SalamanderEntity, SalamanderModel<SalamanderEntity>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack pose, MultiBufferSource buffers, int light, SalamanderEntity entity, float limbSwing, float limbSwingAmount,
                           float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!LayerGenericGlowing.eyesVisible(entity)) return;
            int skin = Math.floorMod(entity.getSkin(), 2);
            ResourceLocation texture = TEXTURES_GLOW[entity.isNymph() ? skin + 2 : skin];
            VertexConsumer vc = buffers.getBuffer(RenderType.eyes(texture));
            this.getParentModel().renderToBuffer(pose, vc, 15728640, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
        }
    }
}
