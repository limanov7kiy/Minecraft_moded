package dev.mcmoded.undeadrising.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/// Светящиеся глаза: видны ночью, в измерениях с потолком (Незер) и там, где не видно неба.
/// Оригинал: Fish's Undead Rising (Fish), `client/layer/LayerGenericGlowing`.
public class LayerGenericGlowing<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final RenderType renderType;

    public LayerGenericGlowing(RenderLayerParent<T, M> parent, ResourceLocation texture) {
        super(parent);
        this.renderType = RenderType.eyes(texture);
    }

    /// Условие оригинала (`SpawnUtil.isDay`: время дня ≤ 12000).
    public static boolean eyesVisible(Entity entity) {
        Level level = entity.level();
        return level.getDayTime() % 24000L > 12000L || level.dimensionType().hasCeiling() || !level.canSeeSky(entity.blockPosition());
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (eyesVisible(entity)) {
            VertexConsumer vc = buffers.getBuffer(this.renderType);
            this.getParentModel().renderToBuffer(pose, vc, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
