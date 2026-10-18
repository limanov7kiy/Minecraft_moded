package dev.mcmoded.undeadrising.client.layer;

import dev.mcmoded.undeadrising.block.FurShroomBlock;
import dev.mcmoded.undeadrising.registry.FurBlocks;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.mcmoded.undeadrising.client.model.UnburiedModel;
import dev.mcmoded.undeadrising.entity.unburied.MycosisEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

/// Грибы, проросшие из головы и спины Mycosis: светящийся гриб (вариант 1) или кордицепс (вариант 0).
/// Как в оригинале, рисуются блоки грибов порта (`age=1`: модели `block/glowshroom_m`, `block/cordy_shroom_s`).
/// До 0.3.0 блоков не было и модели брались как «дополнительные» (`getModel(ResourceLocation)`): с ModernFix
/// `dynamic_resources` такая модель приходила как missing model — фиолетово-чёрные кубы над мобом.
/// Оригинал: Fish's Undead Rising (Fish), `client/layer/LayerMycosis`.
public class LayerMycosis<T extends MycosisEntity> extends RenderLayer<T, UnburiedModel<T>> {

    public LayerMycosis(RenderLayerParent<T, UnburiedModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isBaby() || entity.isInvisible()) return;
        Minecraft mc = Minecraft.getInstance();
        BlockState model = (entity.getSkin() == 1 ? FurBlocks.GLOWSHROOM : FurBlocks.CORDY_SHROOM).get().defaultBlockState().setValue(FurShroomBlock.AGE, 1);
        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        UnburiedModel<T> m = this.getParentModel();

        pose.pushPose();
        m.Body_base.translateAndRotate(pose);
        m.Body_waist.translateAndRotate(pose);
        m.Body_chest.translateAndRotate(pose);
        m.Neck0.translateAndRotate(pose);
        m.Neck1.translateAndRotate(pose);
        m.Head.translateAndRotate(pose);
        pose.translate(0.0, -0.675F, -0.25);
        pose.mulPose(Axis.YP.rotationDegrees(-78.0F));
        pose.scale(-1.0F, -1.0F, 1.0F);
        pose.translate(-0.5, -0.5, -0.5);
        renderShroom(mc, pose, buffers, model, light, overlay);
        pose.popPose();

        pose.pushPose();
        m.Body_base.translateAndRotate(pose);
        m.Body_waist.translateAndRotate(pose);
        m.Body_chest.translateAndRotate(pose);
        pose.translate(0.0, -0.1F, 0.65);
        pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
        pose.scale(-1.0F, -1.0F, 1.0F);
        pose.translate(-0.5, -0.5, -0.5);
        renderShroom(mc, pose, buffers, model, light, overlay);
        pose.popPose();
    }

    private static void renderShroom(Minecraft mc, PoseStack pose, MultiBufferSource buffers, BlockState state, int light, int overlay) {
        mc.getBlockRenderer().renderSingleBlock(state, pose, buffers, light, overlay, net.minecraftforge.client.model.data.ModelData.EMPTY, null);
    }
}
