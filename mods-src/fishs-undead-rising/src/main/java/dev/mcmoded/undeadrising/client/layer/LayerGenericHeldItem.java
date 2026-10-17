package dev.mcmoded.undeadrising.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/// Предметы в руках для нестандартных моделей: смещение и масштаб задаются под модель.
/// Оригинал: Fish's Undead Rising (Fish), `client/layer/LayerGenericHeldItem`.
public class LayerGenericHeldItem<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M> {
    private final ItemInHandRenderer itemRenderer;
    private final float translateX, translateY, translateZ, scale;

    public LayerGenericHeldItem(RenderLayerParent<T, M> parent, ItemInHandRenderer itemRenderer, float x, float y, float z, float scale) {
        super(parent);
        this.itemRenderer = itemRenderer;
        this.translateX = x;
        this.translateY = y;
        this.translateZ = z;
        this.scale = scale;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean rightMain = entity.getMainArm() == HumanoidArm.RIGHT;
        ItemStack left = rightMain ? entity.getOffhandItem() : entity.getMainHandItem();
        ItemStack right = rightMain ? entity.getMainHandItem() : entity.getOffhandItem();
        if (left.isEmpty() && right.isEmpty()) return;
        pose.pushPose();
        if (this.getParentModel().young) {
            pose.translate(0.0, 0.75, 0.0);
            pose.scale(0.5F, 0.5F, 0.5F);
        }
        this.renderArmWithItem(entity, right, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, pose, buffers, light);
        this.renderArmWithItem(entity, left, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, pose, buffers, light);
        pose.popPose();
    }

    private void renderArmWithItem(LivingEntity entity, ItemStack stack, ItemDisplayContext ctx, HumanoidArm arm, PoseStack pose,
                                   MultiBufferSource buffers, int light) {
        if (stack.isEmpty()) return;
        pose.pushPose();
        this.getParentModel().translateToHand(arm, pose);
        pose.scale(this.scale, this.scale, this.scale);
        pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
        pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        boolean leftHand = arm == HumanoidArm.LEFT;
        pose.translate(leftHand ? -this.translateX : this.translateX, this.translateY, this.translateZ);
        this.itemRenderer.renderItem(entity, stack, ctx, leftHand, pose, buffers, light);
        pose.popPose();
    }
}
