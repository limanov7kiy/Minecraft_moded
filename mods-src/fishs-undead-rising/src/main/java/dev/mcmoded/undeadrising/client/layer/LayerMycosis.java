package dev.mcmoded.undeadrising.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.mcmoded.undeadrising.UndeadRising;
import dev.mcmoded.undeadrising.client.model.UnburiedModel;
import dev.mcmoded.undeadrising.entity.unburied.MycosisEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

/// Грибы, проросшие из головы и спины Mycosis: светящийся гриб (вариант 1) или кордицепс (вариант 0).
/// В оригинале рисовались блоки грибов (возраст 1); блоков в порте нет — рисуются те же модели блоков
/// (`block/glowshroom_m`, `block/cordy_shroom_s`), подгруженные отдельно (`ClientSetup#onRegisterModels`).
/// Оригинал: Fish's Undead Rising (Fish), `client/layer/LayerMycosis`.
public class LayerMycosis<T extends MycosisEntity> extends RenderLayer<T, UnburiedModel<T>> {
    public static final ResourceLocation GLOWSHROOM_MODEL = new ResourceLocation(UndeadRising.MOD_ID, "block/glowshroom_m");
    public static final ResourceLocation CORDY_SHROOM_MODEL = new ResourceLocation(UndeadRising.MOD_ID, "block/cordy_shroom_s");

    public LayerMycosis(RenderLayerParent<T, UnburiedModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isBaby() || entity.isInvisible()) return;
        Minecraft mc = Minecraft.getInstance();
        BakedModel model = mc.getModelManager().getModel(entity.getSkin() == 1 ? GLOWSHROOM_MODEL : CORDY_SHROOM_MODEL);
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

    private static void renderShroom(Minecraft mc, PoseStack pose, MultiBufferSource buffers, BakedModel model, int light, int overlay) {
        mc.getBlockRenderer().getModelRenderer().renderModel(pose.last(), buffers.getBuffer(Sheets.cutoutBlockSheet()), null, model,
                1.0F, 1.0F, 1.0F, light, overlay);
    }
}
