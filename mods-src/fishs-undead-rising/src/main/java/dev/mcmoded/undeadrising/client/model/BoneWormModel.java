package dev.mcmoded.undeadrising.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.undeadrising.entity.BoneWormEntity;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/// Модель Osvermis: сегментированное тело с двумя черепами-головами и костяными отростками. Текстура 64×64.
/// Геометрия и анимация — как в оригинале. Оригинал: Fish's Undead Rising (Fish), `client/model/entity/BoneWormModel`.
public class BoneWormModel<T extends BoneWormEntity> extends FURBaseModel<T> implements HeadedModel {
    public final ModelPart Body_base;
    public final ModelPart Body_seg0;
    public final ModelPart Body_seg1;
    public final ModelPart Appendage_l0_seg0;
    public final ModelPart Body_seg2;
    public final ModelPart Appendage_l1_seg0;
    public final ModelPart Spine1;
    public final ModelPart Appendage_r1_seg0;
    public final ModelPart Appendage_r0_seg0;
    public final ModelPart Appendage_r0_seg1;
    public final ModelPart Appendage_l1_seg1;
    public final ModelPart Appendage_r1_seg1;
    public final ModelPart Head1;
    public final ModelPart Spine0;
    public final ModelPart Head0;
    public final ModelPart Head2;
    public final ModelPart Appendage_l0_seg1;
    

    public BoneWormModel(ModelPart root) {
        this.Body_base = root.getChild("Body_base");
        this.Body_seg0 = this.Body_base.getChild("Body_seg0");
        this.Body_seg1 = this.Body_seg0.getChild("Body_seg1");
        this.Appendage_l0_seg0 = this.Body_seg1.getChild("Appendage_l0_seg0");
        this.Body_seg2 = this.Body_seg1.getChild("Body_seg2");
        this.Appendage_l1_seg0 = this.Body_seg1.getChild("Appendage_l1_seg0");
        this.Spine1 = this.Body_seg1.getChild("Spine1");
        this.Appendage_r1_seg0 = this.Body_seg1.getChild("Appendage_r1_seg0");
        this.Appendage_r0_seg0 = this.Body_seg1.getChild("Appendage_r0_seg0");
        this.Appendage_r0_seg1 = this.Appendage_r0_seg0.getChild("Appendage_r0_seg1");
        this.Appendage_l1_seg1 = this.Appendage_l1_seg0.getChild("Appendage_l1_seg1");
        this.Appendage_r1_seg1 = this.Appendage_r1_seg0.getChild("Appendage_r1_seg1");
        this.Head1 = this.Body_seg2.getChild("Head1");
        this.Spine0 = this.Body_seg2.getChild("Spine0");
        this.Head0 = this.Body_seg2.getChild("Head0");
        this.Head2 = this.Body_seg2.getChild("Head2");
        this.Appendage_l0_seg1 = this.Appendage_l0_seg0.getChild("Appendage_l0_seg1");
    
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition Body_base = root.addOrReplaceChild("Body_base", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -10.0F, -3.0F, 8.0F, 10.0F, 6.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition Body_seg0 = Body_base.addOrReplaceChild("Body_seg0", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -10.0F, -6.0F, 8.0F, 10.0F, 6.0F), PartPose.offsetAndRotation(0.0F, -10.0F, 3.0F, 0.27314404F, 0.0F, 0.0F));
        PartDefinition Body_seg1 = Body_seg0.addOrReplaceChild("Body_seg1", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -10.0F, -6.0F, 8.0F, 10.0F, 6.0F), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.27314404F, 0.0F, 0.0F));
        PartDefinition Appendage_l0_seg0 = Body_seg1.addOrReplaceChild("Appendage_l0_seg0", CubeListBuilder.create().texOffs(56, 12).mirror(true).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(5.0F, -7.0F, -3.0F, -1.2747885F, -0.68294734F, 0.0F));
        PartDefinition Body_seg2 = Body_seg1.addOrReplaceChild("Body_seg2", CubeListBuilder.create().texOffs(28, 16).addBox(-4.0F, -6.0F, -6.0F, 8.0F, 6.0F, 6.0F), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.68294734F, 0.0F, 0.0F));
        PartDefinition Appendage_l1_seg0 = Body_seg1.addOrReplaceChild("Appendage_l1_seg0", CubeListBuilder.create().texOffs(56, 12).mirror(true).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(5.0F, -4.0F, -3.0F, -0.95609134F, -0.68294734F, 0.0F));
        Body_seg1.addOrReplaceChild("Spine1", CubeListBuilder.create().texOffs(56, 12).mirror(true).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 2.0F), PartPose.offsetAndRotation(0.0F, -7.9F, 0.0F, 1.4570009F, 0.0F, 0.0F));
        PartDefinition Appendage_r1_seg0 = Body_seg1.addOrReplaceChild("Appendage_r1_seg0", CubeListBuilder.create().texOffs(56, 12).mirror(true).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(-5.0F, -4.0F, -3.0F, -0.95609134F, 0.68294734F, 0.0F));
        PartDefinition Appendage_r0_seg0 = Body_seg1.addOrReplaceChild("Appendage_r0_seg0", CubeListBuilder.create().texOffs(56, 12).mirror(true).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(-5.0F, -7.0F, -3.0F, -1.2747885F, 0.68294734F, 0.0F));
        Appendage_r0_seg0.addOrReplaceChild("Appendage_r0_seg1", CubeListBuilder.create().texOffs(56, 22).mirror(true).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(-1.0F, 6.0F, 0.0F, 0.0F, 0.0F, -1.0927507F));
        Appendage_l1_seg0.addOrReplaceChild("Appendage_l1_seg1", CubeListBuilder.create().texOffs(56, 22).mirror(true).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(1.0F, 6.0F, 0.0F, 0.0F, 0.0F, 1.0927507F));
        Appendage_r1_seg0.addOrReplaceChild("Appendage_r1_seg1", CubeListBuilder.create().texOffs(56, 22).mirror(true).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(-1.0F, 6.0F, 0.0F, 0.0F, 0.0F, -1.0927507F));
        Body_seg2.addOrReplaceChild("Head1", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F), PartPose.offsetAndRotation(-3.0F, -5.0F, -4.0F, -1.3658947F, -0.3642502F, -0.7285004F));
        Body_seg2.addOrReplaceChild("Spine0", CubeListBuilder.create().texOffs(56, 12).mirror(true).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(0.0F, -2.2F, 0.0F, 1.2292354F, 0.0F, 0.0F));
        Body_seg2.addOrReplaceChild("Head0", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), PartPose.offsetAndRotation(3.0F, -5.0F, -3.0F, -0.8651597F, 0.0F, 0.27314404F));
        Body_seg2.addOrReplaceChild("Head2", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F), PartPose.offsetAndRotation(-1.0F, -5.0F, 0.0F, -2.276433F, -0.22759093F, 0.31869712F));
        Appendage_l0_seg0.addOrReplaceChild("Appendage_l0_seg1", CubeListBuilder.create().texOffs(56, 22).mirror(true).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(1.0F, 6.0F, 0.0F, 0.0F, 0.0F, 1.0927507F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.Body_base.render(pose, buffer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.Head_Looking(this.Head0, -0.8651597F, 0.0F, netHeadYaw, headPitch);
        this.Head_Looking(this.Head1, -1.3658947F, -0.3642502F, netHeadYaw, headPitch);
        float f = limbSwingAmount < 0.02F ? 1.0F : 1.5F;
        float g = 0.1F;
        int i = entityIn.getAttackTimer(0);
        int j = entityIn.getAttackTimer(1);
        int k = entityIn.diggingTimer[0];
        int l = entityIn.diggingTimer[1];
        if (j > 0) {
            this.setRotateAngle(this.Appendage_r0_seg0, -1.2747885F, -0.18203785F, 0.0F);
            this.setRotateAngle(this.Appendage_l1_seg0, -1.3203416F, 0.5009095F, 0.0F);
            this.setRotateAngle(this.Appendage_r0_seg1, 0.0F, 0.0F, -1.0927507F);
            this.setRotateAngle(this.Appendage_l0_seg0, -1.6390387F, 0.31869712F, 0.0F);
            if (j > 8) {
                this.Body_seg0.xRot = this.GradientAnimation(0.27314404F, 0.8196066F, ((float)j - 8.0F) / 8.0F);
                this.Body_seg1.xRot = this.GradientAnimation(0.27314404F, 0.95609134F, ((float)j - 8.0F) / 8.0F);
                this.Body_seg2.xRot = this.GradientAnimation(0.68294734F, (float) (Math.PI / 3), ((float)j - 8.0F) / 8.0F);
            }
        } else if (k > 0) {
            f = 3.5F;
            g = 0.5F;
            this.Appendage_r0_seg0.yRot = 0.68294734F + f * -0.16F * Mth.sin(f * g * ageInTicks);
            this.Appendage_r0_seg1.yRot = f * -0.25F * Mth.sin(g * ageInTicks);
            this.Appendage_r1_seg0.yRot = 0.68294734F + f * -0.16F * Mth.sin(f * g * ageInTicks + (float) (Math.PI / 4));
            this.Appendage_r1_seg1.yRot = f * -0.25F * Mth.sin(g * ageInTicks + (float) (Math.PI / 4));
            this.Appendage_l0_seg0.yRot = -0.68294734F + f * -0.16F * Mth.sin(f * g * ageInTicks + (float) (Math.PI / 2));
            this.Appendage_l0_seg1.yRot = f * -0.25F * Mth.sin(g * ageInTicks + (float) (Math.PI / 2));
            this.Appendage_l1_seg0.yRot = -0.68294734F + f * -0.16F * Mth.sin(f * g * ageInTicks + (float) (Math.PI * 3.0 / 4.0));
            this.Appendage_l1_seg1.yRot = f * -0.25F * Mth.sin(g * ageInTicks + (float) (Math.PI * 3.0 / 4.0));
            this.Body_seg0.xRot = this.GradientAnimation(0.27314404F, 0.0F, (float)k / 30.0F);
            this.Body_seg1.xRot = this.GradientAnimation(0.27314404F, 0.0F, (float)k / 30.0F);
            this.Body_seg2.xRot = this.GradientAnimation(0.68294734F, 0.0F, (float)k / 30.0F);
        } else if (l > 0) {
            f = 3.5F;
            g = 0.5F;
            this.Appendage_r0_seg0.yRot = 0.68294734F + f * -0.16F * Mth.sin(f * g * ageInTicks);
            this.Appendage_r0_seg1.yRot = f * -0.25F * Mth.sin(g * ageInTicks);
            this.Appendage_r1_seg0.yRot = 0.68294734F + f * -0.16F * Mth.sin(f * g * ageInTicks + (float) (Math.PI / 4));
            this.Appendage_r1_seg1.yRot = f * -0.25F * Mth.sin(g * ageInTicks + (float) (Math.PI / 4));
            this.Appendage_l0_seg0.yRot = -0.68294734F + f * -0.16F * Mth.sin(f * g * ageInTicks + (float) (Math.PI / 2));
            this.Appendage_l0_seg1.yRot = f * -0.25F * Mth.sin(g * ageInTicks + (float) (Math.PI / 2));
            this.Appendage_l1_seg0.yRot = -0.68294734F + f * -0.16F * Mth.sin(f * g * ageInTicks + (float) (Math.PI * 3.0 / 4.0));
            this.Appendage_l1_seg1.yRot = f * -0.25F * Mth.sin(g * ageInTicks + (float) (Math.PI * 3.0 / 4.0));
            this.Body_seg0.xRot = this.GradientAnimation(0.0F, 0.27314404F, (float)l / 20.0F);
            this.Body_seg1.xRot = this.GradientAnimation(0.0F, 0.27314404F, (float)l / 20.0F);
            this.Body_seg2.xRot = this.GradientAnimation(0.0F, 0.68294734F, (float)l / 20.0F);
        } else if (i > 0) {
            this.Appendage_l0_seg0.xRot = this.GradientAnimation(-1.6845918F, -1.6390387F, (float)i / 15.0F);
            this.Appendage_l0_seg0.yRot = this.GradientAnimation((float) (-Math.PI * 2.0 / 3.0), 0.31869712F, (float)i / 15.0F);
            this.Appendage_l1_seg0.yRot = this.GradientAnimation(-1.9577358F, 0.5009095F, (float)i / 15.0F);
            this.Appendage_r0_seg0.xRot = this.GradientAnimation(-1.6845918F, -1.6390387F, (float)i / 15.0F);
            this.Appendage_r0_seg0.yRot = this.GradientAnimation((float) (Math.PI * 2.0 / 3.0), -0.31869712F, (float)i / 15.0F);
            this.Appendage_r1_seg0.yRot = this.GradientAnimation(1.9577358F, -0.5009095F, (float)i / 15.0F);
            this.Body_seg0.xRot = (float)i * -0.8F / 15.0F + 0.27314404F + f * -0.07F * Mth.sin(0.03F * ageInTicks);
            this.Body_seg1.xRot = (float)i * -0.8F / 15.0F + 0.27314404F + f * -0.07F * Mth.sin(0.03F * ageInTicks);
            this.Body_seg2.xRot = (float)i * -1.8F / 15.0F + 0.68294734F + f * -0.07F * Mth.sin(0.03F * ageInTicks);
        } else {
            g = 0.1F;
            this.Appendage_r0_seg0.yRot = 0.68294734F + f * -0.16F * Mth.sin(f * g * ageInTicks);
            this.Appendage_r0_seg1.yRot = f * -0.25F * Mth.sin(g * ageInTicks);
            this.Appendage_r1_seg0.yRot = 0.68294734F + f * -0.16F * Mth.sin(f * g * ageInTicks + (float) (Math.PI / 4));
            this.Appendage_r1_seg1.yRot = f * -0.25F * Mth.sin(g * ageInTicks + (float) (Math.PI / 4));
            this.Appendage_l0_seg0.yRot = -0.68294734F + f * -0.16F * Mth.sin(f * g * ageInTicks + (float) (Math.PI / 2));
            this.Appendage_l0_seg1.yRot = f * -0.25F * Mth.sin(g * ageInTicks + (float) (Math.PI / 2));
            this.Appendage_l1_seg0.yRot = -0.68294734F + f * -0.16F * Mth.sin(f * g * ageInTicks + (float) (Math.PI * 3.0 / 4.0));
            this.Appendage_l1_seg1.yRot = f * -0.25F * Mth.sin(g * ageInTicks + (float) (Math.PI * 3.0 / 4.0));
            this.Body_seg0.xRot = 0.27314404F + f * -0.07F * Mth.sin(0.03F * ageInTicks);
            this.Body_seg1.xRot = 0.27314404F + f * -0.07F * Mth.sin(0.03F * ageInTicks);
            this.Body_seg2.xRot = 0.68294734F + f * -0.07F * Mth.sin(0.03F * ageInTicks);
        }
    }

    @Override
    public ModelPart getHead() {
        return this.Head0;
    }
}
