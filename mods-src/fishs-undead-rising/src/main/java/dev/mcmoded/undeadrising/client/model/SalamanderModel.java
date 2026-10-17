package dev.mcmoded.undeadrising.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.undeadrising.entity.SalamanderEntity;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/// Модель взрослой саламандры (пушка на спине, седло) — текстура 128×64; личинка-нимфа рисуется вложенной
/// {@link SalamanderNymphModel} (64×32), как в оригинале.
/// Оригинал: Fish's Undead Rising (Fish), `client/model/entity/SalamanderModel`.
public class SalamanderModel<T extends SalamanderEntity> extends FURBaseModel<T> implements HeadedModel {
    public final ModelPart Body;
    public final ModelPart Body3;
    public final ModelPart Tail1;
    public final ModelPart Tail2;
    public final ModelPart CannonBase;
    public final ModelPart Cannon1;
    public final ModelPart Cannon2;
    public final ModelPart Cannon3;
    public final ModelPart LeftLeg;
    public final ModelPart LeftLeg2;
    public final ModelPart Head;
    public final ModelPart Jaw_lower;
    public final ModelPart Tooth_r;
    public final ModelPart Saddle_base;
    public final ModelPart RightArm;
    public final ModelPart Tooth_l;
    public final ModelPart LeftArm;
    public final ModelPart RightLeg;
    public final ModelPart Body2;
    public final ModelPart Jaw_upper;
    public final ModelPart Tail3;
    public final ModelPart RightArm2;
    public final ModelPart Tooth_lower;
    public final ModelPart RightLeg2;
    public final ModelPart Tooth_upper;
    public final ModelPart LeftArm2;
    
    private final SalamanderNymphModel<SalamanderEntity> ChildModel;
    public boolean nymph = true;

    public SalamanderModel(ModelPart root, ModelPart nymphRoot) {
        this.Body = root.getChild("Body");
        this.Body3 = this.Body.getChild("Body3");
        this.Tail1 = this.Body3.getChild("Tail1");
        this.Tail2 = this.Tail1.getChild("Tail2");
        this.CannonBase = this.Body.getChild("CannonBase");
        this.Cannon1 = this.CannonBase.getChild("Cannon1");
        this.Cannon2 = this.Cannon1.getChild("Cannon2");
        this.Cannon3 = this.Cannon2.getChild("Cannon3");
        this.LeftLeg = this.Body.getChild("LeftLeg");
        this.LeftLeg2 = this.LeftLeg.getChild("LeftLeg2");
        this.Head = this.Body.getChild("Head");
        this.Jaw_lower = this.Head.getChild("Jaw_lower");
        this.Tooth_r = this.Jaw_lower.getChild("Tooth_r");
        this.Saddle_base = this.Cannon1.getChild("Saddle_base");
        this.RightArm = this.Body.getChild("RightArm");
        this.Tooth_l = this.Jaw_lower.getChild("Tooth_l");
        this.LeftArm = this.Body.getChild("LeftArm");
        this.RightLeg = this.Body.getChild("RightLeg");
        this.Body2 = this.Body.getChild("Body2");
        this.Jaw_upper = this.Head.getChild("Jaw_upper");
        this.Tail3 = this.Tail2.getChild("Tail3");
        this.RightArm2 = this.RightArm.getChild("RightArm2");
        this.Tooth_lower = this.Jaw_lower.getChild("Tooth_lower");
        this.RightLeg2 = this.RightLeg.getChild("RightLeg2");
        this.Tooth_upper = this.Jaw_upper.getChild("Tooth_upper");
        this.LeftArm2 = this.LeftArm.getChild("LeftArm2");
    
        this.ChildModel = new SalamanderNymphModel<>(nymphRoot);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition Body = root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -4.0F, -7.0F, 8.0F, 8.0F, 22.0F), PartPose.offset(0.0F, 17.5F, -8.2F));
        PartDefinition Body3 = Body.addOrReplaceChild("Body3", CubeListBuilder.create().texOffs(41, 0).addBox(-4.5F, -4.0F, -6.0F, 9.0F, 8.0F, 12.0F), PartPose.offsetAndRotation(0.0F, -4.0F, 10.0F, -0.18203785F, 0.0F, 0.0F));
        PartDefinition Tail1 = Body3.addOrReplaceChild("Tail1", CubeListBuilder.create().texOffs(92, 20).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 10.0F), PartPose.offset(0.0F, 2.0F, 6.0F));
        PartDefinition Tail2 = Tail1.addOrReplaceChild("Tail2", CubeListBuilder.create().texOffs(64, 20).addBox(-3.0F, -2.0F, 0.0F, 6.0F, 6.0F, 8.0F), PartPose.offsetAndRotation(0.0F, -1.0F, 8.0F, 0.091106184F, 0.0F, 0.0F));
        PartDefinition CannonBase = Body.addOrReplaceChild("CannonBase", CubeListBuilder.create().texOffs(76, 46).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -8.0F, 7.0F, -0.4098033F, 0.0F, 0.0F));
        PartDefinition Cannon1 = CannonBase.addOrReplaceChild("Cannon1", CubeListBuilder.create().texOffs(92, 38).addBox(-4.0F, -5.0F, -6.0F, 8.0F, 8.0F, 10.0F), PartPose.offsetAndRotation(0.0F, -6.5F, 0.0F, 0.4098033F, 0.0F, 0.0F));
        PartDefinition Cannon2 = Cannon1.addOrReplaceChild("Cannon2", CubeListBuilder.create().texOffs(68, 34).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 6.0F), PartPose.offset(0.0F, 0.0F, -6.0F));
        Cannon2.addOrReplaceChild("Cannon3", CubeListBuilder.create().texOffs(58, 50).addBox(-2.0F, -2.0F, -10.0F, 4.0F, 4.0F, 10.0F), PartPose.offset(0.0F, -1.0F, -6.0F));
        PartDefinition LeftLeg = Body.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(0, 18).mirror(true).addBox(0.0F, -2.0F, -2.0F, 4.0F, 8.0F, 6.0F), PartPose.offsetAndRotation(4.0F, -2.0F, 12.0F, -0.22759093F, 0.0F, -0.4098033F));
        LeftLeg.addOrReplaceChild("LeftLeg2", CubeListBuilder.create().texOffs(78, 0).mirror(true).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F), PartPose.offsetAndRotation(2.0F, 3.5F, 1.0F, 0.22759093F, 0.0F, 0.5009095F));
        PartDefinition Head = Body.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -3.0F, -8.0F, 8.0F, 4.0F, 8.0F), PartPose.offset(0.0F, 0.0F, -7.0F));
        PartDefinition Jaw_lower = Head.addOrReplaceChild("Jaw_lower", CubeListBuilder.create().texOffs(0, 48).addBox(-5.0F, 1.0F, -10.0F, 10.0F, 4.0F, 12.0F), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.19547687F, 0.0F, 0.0F));
        Jaw_lower.addOrReplaceChild("Tooth_r", CubeListBuilder.create().texOffs(32, 46).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F), PartPose.offsetAndRotation(-4.0F, 2.0F, -9.0F, 0.5604252F, 0.91053826F, 0.0F));
        Cannon1.addOrReplaceChild("Saddle_base", CubeListBuilder.create().texOffs(88, 60).addBox(0.0F, 0.0F, 0.0F, 8.0F, 2.0F, 2.0F), PartPose.offset(-4.0F, -7.0F, 1.0F));
        PartDefinition RightArm = Body.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 18).addBox(-4.0F, -2.0F, -4.0F, 4.0F, 8.0F, 6.0F), PartPose.offsetAndRotation(-4.0F, 0.0F, 2.6F, 0.091106184F, 0.0F, 0.95609134F));
        Jaw_lower.addOrReplaceChild("Tooth_l", CubeListBuilder.create().texOffs(32, 46).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F), PartPose.offsetAndRotation(4.0F, 2.0F, -9.0F, 0.5604252F, -0.90931654F, 0.0F));
        PartDefinition LeftArm = Body.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 18).mirror(true).addBox(0.0F, -2.0F, -4.0F, 4.0F, 8.0F, 6.0F), PartPose.offsetAndRotation(4.0F, 0.0F, 2.6F, 0.091106184F, 0.0F, -0.95609134F));
        PartDefinition RightLeg = Body.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 18).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 6.0F), PartPose.offsetAndRotation(-4.0F, -2.0F, 12.0F, -0.22759093F, 0.0F, 0.4098033F));
        Body.addOrReplaceChild("Body2", CubeListBuilder.create().texOffs(84, 0).addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F), PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.59184116F, 0.0F, 0.0F));
        PartDefinition Jaw_upper = Head.addOrReplaceChild("Jaw_upper", CubeListBuilder.create().texOffs(30, 0).addBox(-4.0F, -1.0F, -2.0F, 8.0F, 2.0F, 2.0F), PartPose.offset(0.0F, 0.0F, -8.0F));
        Tail2.addOrReplaceChild("Tail3", CubeListBuilder.create().texOffs(40, 20).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 4.0F, 8.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, 0.091106184F, 0.0F, 0.0F));
        RightArm.addOrReplaceChild("RightArm2", CubeListBuilder.create().texOffs(78, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F), PartPose.offsetAndRotation(-2.0F, 4.0F, -1.0F, -0.091106184F, 0.0F, -0.9536479F));
        Jaw_lower.addOrReplaceChild("Tooth_lower", CubeListBuilder.create().texOffs(34, 53).addBox(-4.0F, -1.0F, -2.5F, 8.0F, 1.0F, 6.0F), PartPose.offset(0.0F, 1.0F, -7.0F));
        RightLeg.addOrReplaceChild("RightLeg2", CubeListBuilder.create().texOffs(78, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F), PartPose.offsetAndRotation(-2.0F, 3.5F, 1.0F, 0.22759093F, 0.0F, -0.5009095F));
        Jaw_upper.addOrReplaceChild("Tooth_upper", CubeListBuilder.create().texOffs(34, 46).addBox(-3.5F, -0.5F, -2.0F, 7.0F, 1.0F, 6.0F), PartPose.offset(0.0F, 1.5F, 0.0F));
        LeftArm.addOrReplaceChild("LeftArm2", CubeListBuilder.create().texOffs(78, 0).mirror(true).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F), PartPose.offsetAndRotation(2.0F, 4.0F, -1.0F, -0.091106184F, 0.0F, 0.9536479F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!this.nymph) {
            this.Body.render(pose, buffer, light, overlay, red, green, blue, alpha);
        } else if (this.young) {
            this.ChildModel.renderToBuffer(pose, buffer, light, overlay, red, green, blue, alpha);
        }
    }
    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float i = (float)entityIn.getAttackTimer() / 80.0F;
        float[] Anime_threshold = new float[]{0.85F, 0.67F, 0.12F};
        float j = 1.0F / (Anime_threshold[0] - Anime_threshold[1]);
        float k = 1.0F / (Anime_threshold[1] - Anime_threshold[2]);
        if (!entityIn.isNymph()) {
            if (entityIn.isBaby()) {
                if (entityIn.getGrowingStage() == 1) {
                    this.Tooth_l.visible = false;
                    this.Tooth_r.visible = false;
                } else {
                    this.Tooth_l.visible = true;
                    this.Tooth_r.visible = true;
                }

                this.CannonBase.visible = false;
                this.Cannon1.visible = false;
                this.Cannon2.visible = false;
                this.Cannon3.visible = false;
            } else {
                this.Tooth_l.visible = true;
                this.Tooth_r.visible = true;
                this.CannonBase.visible = true;
                this.Cannon1.visible = true;
                this.Cannon2.visible = true;
                this.Cannon3.visible = true;
            }

            if (entityIn.isInSittingPose() && !entityIn.isVehicle()) {
                if (entityIn.isBoostingFurnace()) {
                    this.Head.xRot = -0.4691445F + 0.08F * Mth.sin(0.3F * ageInTicks);
                    this.Head.yRot = 0.0F;
                    this.Head.zRot = 0.0F;
                    if (this.Jaw_lower.xRot < 0.66F) {
                        this.Jaw_lower.xRot += 0.01F;
                    } else {
                        this.Jaw_lower.xRot = 0.7F + -0.04F * Mth.sin(0.3F * ageInTicks);
                    }
                } else {
                    this.Head_Looking(this.Head, -0.35185838F, 0.0F, netHeadYaw, headPitch);
                    this.Head.zRot = 0.0F;
                    this.Jaw_lower.xRot = 0.19547687F + -0.08F * Mth.sin(0.03F * ageInTicks);
                }

                this.RightArm.xRot = -0.76899207F;
                this.LeftArm.xRot = -0.76899207F;
                this.RightLeg.xRot = 0.63268185F;
                this.LeftLeg.xRot = 0.63268185F;
                this.RightArm.zRot = 0.95609134F;
                this.LeftArm.zRot = -0.95609134F;
                this.RightLeg.zRot = 0.4098033F;
                this.LeftLeg.zRot = -0.4098033F;
                this.Body.xRot = 0.0F;
                this.Body.y = 20.0F;
                this.Tail1.yRot = 0.15F * Mth.sin(0.03F * ageInTicks);
                this.Tail2.yRot = 0.15F * Mth.sin(0.03F * ageInTicks + 0.02F);
                this.Tail3.yRot = 0.15F * Mth.sin(0.03F * ageInTicks + 0.04F);
                this.Cannon2.z = -3.0F;
                this.Cannon3.z = -3.0F;
            } else if (i <= Anime_threshold[0] && i > Anime_threshold[1]) {
                this.Head_Looking(this.Head, 0.0F, 0.0F, netHeadYaw, headPitch);
                this.Head.zRot = this.GradientAnimation(0.0F, -0.3642502F, j * (i - Anime_threshold[1]));
                this.Jaw_lower.xRot = this.GradientAnimation(0.0F, 0.68294734F, j * (i - Anime_threshold[1]));
                this.RightArm.xRot = this.GradientAnimation(0.091106184F, -0.5009095F, j * (i - Anime_threshold[1]));
                this.LeftArm.xRot = this.GradientAnimation(0.091106184F, -0.5009095F, j * (i - Anime_threshold[1]));
                this.RightLeg.xRot = -0.22759093F;
                this.LeftLeg.xRot = -0.22759093F;
                this.RightArm.zRot = 0.95609134F;
                this.LeftArm.zRot = -0.95609134F;
                this.RightLeg.zRot = 0.4098033F;
                this.LeftLeg.zRot = -0.4098033F;
                this.Body.xRot = this.GradientAnimation(0.0F, -0.18203785F, j * (i - Anime_threshold[1]));
                this.Body.y = this.GradientAnimation(17.5F, 16.2F, j * (i - Anime_threshold[1]));
                this.Tail1.yRot = this.GradientAnimation(0.0F, -0.13665928F, j * (i - Anime_threshold[1]));
                this.Tail2.yRot = this.GradientAnimation(0.0F, -0.4098033F, j * (i - Anime_threshold[1]));
                this.Tail3.yRot = this.GradientAnimation(0.0F, -0.4098033F, j * (i - Anime_threshold[1]));
                this.Cannon2.z = -3.0F;
                this.Cannon3.z = -3.0F;
            } else if (i <= Anime_threshold[1] && i > Anime_threshold[2]) {
                this.Head_Looking(this.Head, 0.0F, 0.0F, netHeadYaw, headPitch);
                this.Head.zRot = this.GradientAnimation(-0.3642502F, 0.0F, k * (i - Anime_threshold[2]));
                this.Jaw_lower.xRot = this.GradientAnimation(0.68294734F, 0.0F, k * (i - Anime_threshold[2]));
                this.RightArm.xRot = this.GradientAnimation(-0.5009095F, 0.091106184F, k * (i - Anime_threshold[2]));
                this.LeftArm.xRot = this.GradientAnimation(-0.5009095F, 0.091106184F, k * (i - Anime_threshold[2]));
                this.RightLeg.xRot = -0.22759093F;
                this.LeftLeg.xRot = -0.22759093F;
                this.RightArm.zRot = 0.95609134F;
                this.LeftArm.zRot = -0.95609134F;
                this.RightLeg.zRot = 0.4098033F;
                this.LeftLeg.zRot = -0.4098033F;
                this.Body.xRot = this.GradientAnimation(-0.18203785F, 0.0F, k * (i - Anime_threshold[2]));
                this.Body.y = this.GradientAnimation(16.2F, 17.5F, k * (i - Anime_threshold[2]));
                this.Tail1.yRot = this.GradientAnimation(-0.13665928F, 0.0F, k * (i - Anime_threshold[2]));
                this.Tail2.yRot = this.GradientAnimation(-0.4098033F, 0.0F, k * (i - Anime_threshold[2]));
                this.Tail3.yRot = this.GradientAnimation(-0.4098033F, 0.0F, k * (i - Anime_threshold[2]));
                this.Cannon2.z = -1.8F + 1.2F * Mth.sin(1.0F * ageInTicks);
                this.Cannon3.z = -1.3F + 1.7F * Mth.sin(1.0F * ageInTicks);
            } else {
                float walking_f = 0.44F;
                float walking_a = 0.4F;
                this.Head_Looking(this.Head, 0.0F, 0.0F, netHeadYaw, headPitch);
                this.Head.zRot = 0.0F;
                this.Jaw_lower.xRot = 0.19547687F + -0.08F * Mth.sin(0.03F * ageInTicks);
                this.RightArm.xRot = 0.091106184F + Mth.cos(limbSwing * 0.44F) * 0.4F * limbSwingAmount;
                this.LeftArm.xRot = 0.091106184F + Mth.cos(limbSwing * 0.44F + (float) Math.PI) * 0.4F * limbSwingAmount;
                this.RightLeg.xRot = -0.22759093F + Mth.cos(limbSwing * 0.44F + (float) Math.PI) * 0.4F * limbSwingAmount;
                this.LeftLeg.xRot = -0.22759093F + Mth.cos(limbSwing * 0.44F) * 0.4F * limbSwingAmount;
                this.RightArm.zRot = 0.95609134F + Mth.cos(limbSwing * 0.44F) * 0.4F * limbSwingAmount;
                this.LeftArm.zRot = -0.95609134F + Mth.cos(limbSwing * 0.44F + (float) Math.PI) * 0.4F * limbSwingAmount;
                this.RightLeg.zRot = 0.4098033F + Mth.cos(limbSwing * 0.44F + (float) Math.PI) * 0.4F * limbSwingAmount;
                this.LeftLeg.zRot = -0.4098033F + Mth.cos(limbSwing * 0.44F) * 0.4F * limbSwingAmount;
                this.RightArm.y = 0.0F + Mth.cos(limbSwing * 0.44F) * 1.0F * limbSwingAmount;
                this.LeftArm.y = 0.0F + Mth.cos(limbSwing * 0.44F + (float) Math.PI) * 1.0F * limbSwingAmount;
                this.RightLeg.y = -0.2F + Mth.cos(limbSwing * 0.44F + (float) Math.PI) * 1.0F * limbSwingAmount;
                this.LeftLeg.y = -0.2F + Mth.cos(limbSwing * 0.44F) * 1.0F * limbSwingAmount;
                this.Body.xRot = 0.0F;
                this.Body.y = 17.5F;
                this.Tail1.yRot = 0.15F * Mth.sin(0.03F * ageInTicks);
                this.Tail2.yRot = 0.15F * Mth.sin(0.03F * ageInTicks + 0.02F);
                this.Tail3.yRot = 0.15F * Mth.sin(0.03F * ageInTicks + 0.04F);
                this.Cannon2.z = -3.0F;
                this.Cannon3.z = -3.0F;
            }
        } else {
            this.ChildModel.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @Override
    public ModelPart getHead() {
        return this.Head;
    }
}
