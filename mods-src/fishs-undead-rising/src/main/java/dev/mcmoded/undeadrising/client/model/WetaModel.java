package dev.mcmoded.undeadrising.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.undeadrising.entity.WetaEntity;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/// Модель веты (шестиногий кузнечик-переросток с жвалами и усами), текстура 64×32.
/// Оригинал: Fish's Undead Rising (Fish), `client/model/entity/WetaModel`.
public class WetaModel<T extends WetaEntity> extends FURBaseModel<T> implements HeadedModel {
    public final ModelPart Body_base;
    public final ModelPart Leg2_Seg0_r;
    public final ModelPart Leg2_Seg1_r;
    public final ModelPart Leg0_Seg0_r;
    public final ModelPart Head;
    public final ModelPart Antenna_Seg0_r;
    public final ModelPart Antenna_Seg1_r;
    public final ModelPart Abdomen;
    public final ModelPart Stinger;
    public final ModelPart Leg2_Seg0_l;
    public final ModelPart Leg2_Seg1_l;
    public final ModelPart Antenna_Seg0_l;
    public final ModelPart Antenna_Seg1_l;
    public final ModelPart Leg0_Seg0_l;
    public final ModelPart Leg0_Seg1_l;
    public final ModelPart Leg1_Seg0_r;
    public final ModelPart Jaw_l;
    public final ModelPart Leg0_Seg1_r;
    public final ModelPart Leg1_Seg0_l;
    public final ModelPart Jaw_r;
    public final ModelPart Leg1_Seg1_r;
    public final ModelPart Leg2_Seg2_l;
    public final ModelPart Leg2_Seg2_r;
    public final ModelPart Leg1_Seg1_l;
    

    public WetaModel(ModelPart root) {
        this.Body_base = root.getChild("Body_base");
        this.Leg2_Seg0_r = this.Body_base.getChild("Leg2_Seg0_r");
        this.Leg2_Seg1_r = this.Leg2_Seg0_r.getChild("Leg2_Seg1_r");
        this.Leg0_Seg0_r = this.Body_base.getChild("Leg0_Seg0_r");
        this.Head = this.Body_base.getChild("Head");
        this.Antenna_Seg0_r = this.Head.getChild("Antenna_Seg0_r");
        this.Antenna_Seg1_r = this.Antenna_Seg0_r.getChild("Antenna_Seg1_r");
        this.Abdomen = this.Body_base.getChild("Abdomen");
        this.Stinger = this.Abdomen.getChild("Stinger");
        this.Leg2_Seg0_l = this.Body_base.getChild("Leg2_Seg0_l");
        this.Leg2_Seg1_l = this.Leg2_Seg0_l.getChild("Leg2_Seg1_l");
        this.Antenna_Seg0_l = this.Head.getChild("Antenna_Seg0_l");
        this.Antenna_Seg1_l = this.Antenna_Seg0_l.getChild("Antenna_Seg1_l");
        this.Leg0_Seg0_l = this.Body_base.getChild("Leg0_Seg0_l");
        this.Leg0_Seg1_l = this.Leg0_Seg0_l.getChild("Leg0_Seg1_l");
        this.Leg1_Seg0_r = this.Body_base.getChild("Leg1_Seg0_r");
        this.Jaw_l = this.Head.getChild("Jaw_l");
        this.Leg0_Seg1_r = this.Leg0_Seg0_r.getChild("Leg0_Seg1_r");
        this.Leg1_Seg0_l = this.Body_base.getChild("Leg1_Seg0_l");
        this.Jaw_r = this.Head.getChild("Jaw_r");
        this.Leg1_Seg1_r = this.Leg1_Seg0_r.getChild("Leg1_Seg1_r");
        this.Leg2_Seg2_l = this.Leg2_Seg1_l.getChild("Leg2_Seg2_l");
        this.Leg2_Seg2_r = this.Leg2_Seg1_r.getChild("Leg2_Seg2_r");
        this.Leg1_Seg1_l = this.Leg1_Seg0_l.getChild("Leg1_Seg1_l");
    
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition Body_base = root.addOrReplaceChild("Body_base", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, -2.5F, -2.0F, 4.0F, 5.0F, 4.0F), PartPose.offset(0.0F, 20.0F, -2.5F));
        PartDefinition Leg2_Seg0_r = Body_base.addOrReplaceChild("Leg2_Seg0_r", CubeListBuilder.create().texOffs(36, 0).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 2.0F, 6.0F), PartPose.offsetAndRotation(-2.0F, 2.0F, 1.1F, 0.59184116F, -0.4098033F, 0.0F));
        PartDefinition Leg2_Seg1_r = Leg2_Seg0_r.addOrReplaceChild("Leg2_Seg1_r", CubeListBuilder.create().texOffs(41, 9).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 8.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 6.0F));
        PartDefinition Leg0_Seg0_r = Body_base.addOrReplaceChild("Leg0_Seg0_r", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 3.0F), PartPose.offsetAndRotation(-2.0F, 2.0F, -1.0F, 0.5009095F, -2.048842F, 0.0F));
        PartDefinition Head = Body_base.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F), PartPose.offset(0.0F, 0.5F, -2.0F));
        PartDefinition Antenna_Seg0_r = Head.addOrReplaceChild("Antenna_Seg0_r", CubeListBuilder.create().texOffs(17, 19).addBox(-0.5F, 0.0F, -4.0F, 1.0F, 0.0F, 4.0F), PartPose.offsetAndRotation(-1.0F, -2.0F, -4.0F, -1.2292354F, 0.0F, 0.0F));
        Antenna_Seg0_r.addOrReplaceChild("Antenna_Seg1_r", CubeListBuilder.create().texOffs(15, 13).addBox(-0.5F, 0.0F, -6.0F, 1.0F, 0.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.5462881F, 0.0F, 0.0F));
        PartDefinition Abdomen = Body_base.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(0, 18).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 8.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 1.5F, -0.18203785F, 0.0F, 0.0F));
        Abdomen.addOrReplaceChild("Stinger", CubeListBuilder.create().texOffs(9, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 0.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 2.0F, 8.0F, 0.31869712F, 0.0F, 0.0F));
        PartDefinition Leg2_Seg0_l = Body_base.addOrReplaceChild("Leg2_Seg0_l", CubeListBuilder.create().texOffs(36, 0).mirror(true).addBox(0.0F, -1.0F, 0.0F, 1.0F, 2.0F, 6.0F), PartPose.offsetAndRotation(2.0F, 2.0F, 1.1F, 0.59184116F, 0.4098033F, 0.0F));
        PartDefinition Leg2_Seg1_l = Leg2_Seg0_l.addOrReplaceChild("Leg2_Seg1_l", CubeListBuilder.create().texOffs(41, 9).mirror(true).addBox(0.0F, -1.0F, 0.0F, 1.0F, 8.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 6.0F));
        PartDefinition Antenna_Seg0_l = Head.addOrReplaceChild("Antenna_Seg0_l", CubeListBuilder.create().texOffs(17, 19).mirror(true).addBox(-0.5F, 0.0F, -4.0F, 1.0F, 0.0F, 4.0F), PartPose.offsetAndRotation(1.0F, -2.0F, -4.0F, -1.2292354F, 0.0F, 0.0F));
        Antenna_Seg0_l.addOrReplaceChild("Antenna_Seg1_l", CubeListBuilder.create().texOffs(15, 13).mirror(true).addBox(-0.5F, 0.0F, -6.0F, 1.0F, 0.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.5462881F, 0.0F, 0.0F));
        PartDefinition Leg0_Seg0_l = Body_base.addOrReplaceChild("Leg0_Seg0_l", CubeListBuilder.create().texOffs(18, 0).mirror(true).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 3.0F), PartPose.offsetAndRotation(2.0F, 2.0F, -1.0F, 0.5009095F, 2.048842F, 0.0F));
        Leg0_Seg0_l.addOrReplaceChild("Leg0_Seg1_l", CubeListBuilder.create().texOffs(20, 5).mirror(true).addBox(0.0F, -1.0F, 0.0F, 1.0F, 6.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 3.0F));
        PartDefinition Leg1_Seg0_r = Body_base.addOrReplaceChild("Leg1_Seg0_r", CubeListBuilder.create().texOffs(27, 0).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 3.0F), PartPose.offsetAndRotation(-2.0F, 2.0F, 0.0F, 0.18203785F, -1.2292354F, 0.0F));
        Head.addOrReplaceChild("Jaw_l", CubeListBuilder.create().texOffs(13, 9).mirror(true).addBox(-2.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(1.9F, 1.2F, -4.0F, 0.0F, -0.7740535F, -0.5462881F));
        Leg0_Seg0_r.addOrReplaceChild("Leg0_Seg1_r", CubeListBuilder.create().texOffs(20, 5).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 6.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 3.0F));
        PartDefinition Leg1_Seg0_l = Body_base.addOrReplaceChild("Leg1_Seg0_l", CubeListBuilder.create().texOffs(27, 0).mirror(true).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 3.0F), PartPose.offsetAndRotation(2.0F, 2.0F, 0.0F, 0.18203785F, 1.2292354F, 0.0F));
        Head.addOrReplaceChild("Jaw_r", CubeListBuilder.create().texOffs(13, 9).addBox(0.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(-1.9F, 1.2F, -4.0F, 0.0F, 0.7740535F, 0.5462881F));
        Leg1_Seg0_r.addOrReplaceChild("Leg1_Seg1_r", CubeListBuilder.create().texOffs(29, 5).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 3.0F));
        Leg2_Seg1_l.addOrReplaceChild("Leg2_Seg2_l", CubeListBuilder.create().texOffs(41, 19).mirror(true).addBox(0.0F, -1.0F, 0.0F, 1.0F, 8.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 1.0F));
        Leg2_Seg1_r.addOrReplaceChild("Leg2_Seg2_r", CubeListBuilder.create().texOffs(41, 19).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 8.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 1.0F));
        Leg1_Seg0_l.addOrReplaceChild("Leg1_Seg1_l", CubeListBuilder.create().texOffs(29, 5).mirror(true).addBox(0.0F, -1.0F, 0.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 3.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.Body_base.render(pose, buffer, light, overlay, red, green, blue, alpha);
    }
    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = entityIn.getSkin() == 2 ? 0.5F : 1.0F;
        this.Head_Looking(this.Head, 0.0F, 0.0F, netHeadYaw, headPitch);
        this.Body_base.y = 20.0F - 0.5F * Mth.sin(limbSwing * 1.3F);
        this.SwingY_Sin(this.Jaw_l, -0.7740535F, ageInTicks, 0.125F, 0.4F, true, 0.0F);
        this.SwingY_Sin(this.Jaw_r, 0.7740535F, ageInTicks, 0.125F, 0.4F, false, 0.0F);
        this.SwingX_Sin(this.Antenna_Seg0_l, -1.2292354F, ageInTicks, 0.4F, 0.41F * f, false, 0.0F);
        this.SwingX_Sin(this.Antenna_Seg0_r, -1.2292354F, ageInTicks, 0.4F, 0.32F * f, false, (float) (Math.PI / 4));
        this.SwingX_Sin(this.Antenna_Seg1_l, 0.5462881F, ageInTicks, 0.4F, 0.41F * f, false, 0.0F);
        this.SwingX_Sin(this.Antenna_Seg1_r, 0.5462881F, ageInTicks, 0.4F, 0.32F * f, false, (float) (Math.PI / 4));
        this.SwingX_Sin(this.Leg0_Seg0_r, 0.5009095F, limbSwing, 0.5F * limbSwingAmount, 1.3F, false, 0.0F);
        this.SwingX_Sin(this.Leg0_Seg0_l, 0.5009095F, limbSwing, 0.5F * limbSwingAmount, 1.3F, false, (float) (Math.PI / 2));
        this.SwingX_Sin(this.Leg1_Seg0_r, 0.18203785F, limbSwing, 0.5F * limbSwingAmount, 1.3F, false, 1.0367256F);
        this.SwingX_Sin(this.Leg1_Seg0_l, 0.18203785F, limbSwing, 0.5F * limbSwingAmount, 1.3F, false, 2.607522F);
        this.SwingX_Sin(this.Leg2_Seg0_r, 0.59184116F, limbSwing, 0.5F * limbSwingAmount, 1.3F, false, 2.0734513F);
        this.SwingX_Sin(this.Leg2_Seg0_l, 0.59184116F, limbSwing, 0.5F * limbSwingAmount, 1.3F, false, 3.6442475F);
        this.SwingY_Sin(this.Leg0_Seg0_r, -2.048842F, limbSwing, 0.3F * limbSwingAmount, 1.3F, true, 0.0F);
        this.SwingY_Sin(this.Leg0_Seg0_l, 2.048842F, limbSwing, 0.3F * limbSwingAmount, 1.3F, false, (float) (Math.PI / 2));
        this.SwingY_Sin(this.Leg1_Seg0_r, -1.2292354F, limbSwing, 0.3F * limbSwingAmount, 1.3F, true, 1.0367256F);
        this.SwingY_Sin(this.Leg1_Seg0_l, 1.2292354F, limbSwing, 0.3F * limbSwingAmount, 1.3F, false, 2.607522F);
        this.SwingX_Sin(this.Leg2_Seg1_r, 0.0F, limbSwing, 0.8F * limbSwingAmount, 1.3F, false, 2.0734513F);
        this.SwingX_Sin(this.Leg2_Seg1_l, 0.0F, limbSwing, 0.8F * limbSwingAmount, 1.3F, false, 3.6442475F);
    }

    @Override
    public ModelPart getHead() {
        return this.Head;
    }
}
