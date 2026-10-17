package dev.mcmoded.undeadrising.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.undeadrising.entity.MimicEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/// Модель мимикраба: сундук на крабьих ногах с клешнями; в засаде ноги и клешни прячутся и он неотличим от
/// сундука. Текстура 64×64; сам сундук дорисовывает слой `LayerMimicChest` текстурой ванильного сундука.
/// Оригинал: Fish's Undead Rising (Fish), `client/model/entity/MimicModel`.
public class MimicModel<T extends MimicEntity> extends FURBaseModel<T> {
    public final ModelPart Chest_Base;
    public final ModelPart Eye_r;
    public final ModelPart Leg1_r_Seg0;
    public final ModelPart Leg1_r_Seg1;
    public final ModelPart Leg0_l_Seg0;
    public final ModelPart Leg0_l_Seg1;
    public final ModelPart Leg0_l_Seg2;
    public final ModelPart Leg1_l_Seg0;
    public final ModelPart Leg1_l_Seg1;
    public final ModelPart Pincer_l_Seg0;
    public final ModelPart Pincer_l_Seg1;
    public final ModelPart Pincer_l_Seg3;
    public final ModelPart Leg1_l_Seg2;
    public final ModelPart Pincer_r_Seg0;
    public final ModelPart Pincer_r_Seg1;
    public final ModelPart Chest_top;
    public final ModelPart Chest_lock;
    public final ModelPart Leg0_r_Seg0;
    public final ModelPart Leg0_r_Seg1;
    public final ModelPart Leg0_r_Seg2;
    public final ModelPart Pincer_l_Seg2;
    public final ModelPart Pincer_r_Seg3;
    public final ModelPart Eye_l;
    public final ModelPart Leg1_r_Seg2;
    public final ModelPart Pincer_r_Seg2;
    public final ModelPart Chest_Base_Outer;
    public final ModelPart Chest_top_outer;
    public final ModelPart Chest_lock_outer;
    

    public MimicModel(ModelPart root) {
        this.Chest_Base = root.getChild("Chest_Base");
        this.Eye_r = this.Chest_Base.getChild("Eye_r");
        this.Leg1_r_Seg0 = this.Chest_Base.getChild("Leg1_r_Seg0");
        this.Leg1_r_Seg1 = this.Leg1_r_Seg0.getChild("Leg1_r_Seg1");
        this.Leg0_l_Seg0 = this.Chest_Base.getChild("Leg0_l_Seg0");
        this.Leg0_l_Seg1 = this.Leg0_l_Seg0.getChild("Leg0_l_Seg1");
        this.Leg0_l_Seg2 = this.Leg0_l_Seg1.getChild("Leg0_l_Seg2");
        this.Leg1_l_Seg0 = this.Chest_Base.getChild("Leg1_l_Seg0");
        this.Leg1_l_Seg1 = this.Leg1_l_Seg0.getChild("Leg1_l_Seg1");
        this.Pincer_l_Seg0 = this.Chest_Base.getChild("Pincer_l_Seg0");
        this.Pincer_l_Seg1 = this.Pincer_l_Seg0.getChild("Pincer_l_Seg1");
        this.Pincer_l_Seg3 = this.Pincer_l_Seg1.getChild("Pincer_l_Seg3");
        this.Leg1_l_Seg2 = this.Leg1_l_Seg1.getChild("Leg1_l_Seg2");
        this.Pincer_r_Seg0 = this.Chest_Base.getChild("Pincer_r_Seg0");
        this.Pincer_r_Seg1 = this.Pincer_r_Seg0.getChild("Pincer_r_Seg1");
        this.Chest_top = this.Chest_Base.getChild("Chest_top");
        this.Chest_lock = this.Chest_top.getChild("Chest_lock");
        this.Leg0_r_Seg0 = this.Chest_Base.getChild("Leg0_r_Seg0");
        this.Leg0_r_Seg1 = this.Leg0_r_Seg0.getChild("Leg0_r_Seg1");
        this.Leg0_r_Seg2 = this.Leg0_r_Seg1.getChild("Leg0_r_Seg2");
        this.Pincer_l_Seg2 = this.Pincer_l_Seg1.getChild("Pincer_l_Seg2");
        this.Pincer_r_Seg3 = this.Pincer_r_Seg1.getChild("Pincer_r_Seg3");
        this.Eye_l = this.Chest_Base.getChild("Eye_l");
        this.Leg1_r_Seg2 = this.Leg1_r_Seg1.getChild("Leg1_r_Seg2");
        this.Pincer_r_Seg2 = this.Pincer_r_Seg1.getChild("Pincer_r_Seg2");
        this.Chest_Base_Outer = this.Chest_Base.getChild("Chest_Base_Outer");
        this.Chest_top_outer = this.Chest_top.getChild("Chest_top_outer");
        this.Chest_lock_outer = this.Chest_top_outer.getChild("Chest_lock_outer");
    
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition Chest_Base = root.addOrReplaceChild("Chest_Base", CubeListBuilder.create().texOffs(0, 19).addBox(-7.0F, -5.0F, -7.0F, 0.0F, 0.0F, 0.0F), PartPose.offset(0.0F, 18.0F, 1.0F));
        Chest_Base.addOrReplaceChild("Eye_r", CubeListBuilder.create().texOffs(44, 0).addBox(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F), PartPose.offsetAndRotation(-2.0F, -4.0F, -5.0F, 0.81559235F, 0.3642502F, 0.0F));
        PartDefinition Leg1_r_Seg0 = Chest_Base.addOrReplaceChild("Leg1_r_Seg0", CubeListBuilder.create().texOffs(1, 44).addBox(-0.5F, -8.0F, -0.5F, 1.0F, 8.0F, 1.0F), PartPose.offsetAndRotation(-6.0F, 4.0F, 5.0F, 0.0F, 0.5462881F, -0.5009095F));
        PartDefinition Leg1_r_Seg1 = Leg1_r_Seg0.addOrReplaceChild("Leg1_r_Seg1", CubeListBuilder.create().texOffs(6, 44).addBox(-1.0F, -2.0F, -1.5F, 2.0F, 10.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.8651597F));
        PartDefinition Leg0_l_Seg0 = Chest_Base.addOrReplaceChild("Leg0_l_Seg0", CubeListBuilder.create().texOffs(1, 44).mirror(true).addBox(-0.5F, -8.0F, -0.5F, 1.0F, 8.0F, 1.0F), PartPose.offsetAndRotation(6.0F, 4.0F, 0.0F, 0.0F, 0.22759093F, 0.5009095F));
        PartDefinition Leg0_l_Seg1 = Leg0_l_Seg0.addOrReplaceChild("Leg0_l_Seg1", CubeListBuilder.create().texOffs(6, 44).mirror(true).addBox(-1.0F, -2.0F, -1.5F, 2.0F, 10.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0F, 0.0F, -0.8651597F));
        Leg0_l_Seg1.addOrReplaceChild("Leg0_l_Seg2", CubeListBuilder.create().texOffs(1, 56).mirror(true).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.59184116F));
        PartDefinition Leg1_l_Seg0 = Chest_Base.addOrReplaceChild("Leg1_l_Seg0", CubeListBuilder.create().texOffs(1, 44).mirror(true).addBox(-0.5F, -8.0F, -0.5F, 1.0F, 8.0F, 1.0F), PartPose.offsetAndRotation(6.0F, 4.0F, 5.0F, 0.0F, -0.5462881F, 0.5009095F));
        PartDefinition Leg1_l_Seg1 = Leg1_l_Seg0.addOrReplaceChild("Leg1_l_Seg1", CubeListBuilder.create().texOffs(6, 44).mirror(true).addBox(-1.0F, -2.0F, -1.5F, 2.0F, 10.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0F, 0.0F, -0.8651597F));
        PartDefinition Pincer_l_Seg0 = Chest_Base.addOrReplaceChild("Pincer_l_Seg0", CubeListBuilder.create().texOffs(17, 45).mirror(true).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(6.0F, 2.0F, -4.0F, 0.59184116F, 1.0016445F, 1.3658947F));
        PartDefinition Pincer_l_Seg1 = Pincer_l_Seg0.addOrReplaceChild("Pincer_l_Seg1", CubeListBuilder.create().texOffs(26, 45).mirror(true).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 10.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, -0.3642502F, 0.0F, -1.2292354F));
        Pincer_l_Seg1.addOrReplaceChild("Pincer_l_Seg3", CubeListBuilder.create().texOffs(17, 56).mirror(true).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        Leg1_l_Seg1.addOrReplaceChild("Leg1_l_Seg2", CubeListBuilder.create().texOffs(1, 56).mirror(true).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.59184116F));
        PartDefinition Pincer_r_Seg0 = Chest_Base.addOrReplaceChild("Pincer_r_Seg0", CubeListBuilder.create().texOffs(17, 45).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(-6.0F, 2.0F, -4.0F, 0.59184116F, -1.0016445F, -1.3658947F));
        PartDefinition Pincer_r_Seg1 = Pincer_r_Seg0.addOrReplaceChild("Pincer_r_Seg1", CubeListBuilder.create().texOffs(26, 45).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 10.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, -0.3642502F, 0.0F, 1.2292354F));
        PartDefinition Chest_top = Chest_Base.addOrReplaceChild("Chest_top", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -5.0F, -14.0F, 0.0F, 0.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -4.0F, 7.0F, -0.13665928F, 0.0F, 0.0F));
        Chest_top.addOrReplaceChild("Chest_lock", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 0.0F, 0.0F, 0.0F), PartPose.offset(0.0F, 0.0F, -14.0F));
        PartDefinition Leg0_r_Seg0 = Chest_Base.addOrReplaceChild("Leg0_r_Seg0", CubeListBuilder.create().texOffs(1, 44).addBox(-0.5F, -8.0F, -0.5F, 1.0F, 8.0F, 1.0F), PartPose.offsetAndRotation(-6.0F, 4.0F, 0.0F, 0.0F, -0.22759093F, -0.5009095F));
        PartDefinition Leg0_r_Seg1 = Leg0_r_Seg0.addOrReplaceChild("Leg0_r_Seg1", CubeListBuilder.create().texOffs(6, 44).addBox(-1.0F, -2.0F, -1.5F, 2.0F, 10.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.8651597F));
        Leg0_r_Seg1.addOrReplaceChild("Leg0_r_Seg2", CubeListBuilder.create().texOffs(1, 56).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, -0.59184116F));
        Pincer_l_Seg1.addOrReplaceChild("Pincer_l_Seg2", CubeListBuilder.create().texOffs(17, 56).mirror(true).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 8.0F, -1.0F, -0.37542033F, 0.0F, 0.0F));
        Pincer_r_Seg1.addOrReplaceChild("Pincer_r_Seg3", CubeListBuilder.create().texOffs(17, 56).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        Chest_Base.addOrReplaceChild("Eye_l", CubeListBuilder.create().texOffs(44, 0).mirror(true).addBox(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F), PartPose.offsetAndRotation(2.0F, -4.0F, -5.0F, 0.81559235F, -0.3642502F, 0.0F));
        Leg1_r_Seg1.addOrReplaceChild("Leg1_r_Seg2", CubeListBuilder.create().texOffs(1, 56).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, -0.59184116F));
        Pincer_r_Seg1.addOrReplaceChild("Pincer_r_Seg2", CubeListBuilder.create().texOffs(17, 56).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 8.0F, -1.0F, -0.37542033F, 0.0F, 0.0F));
        Chest_Base.addOrReplaceChild("Chest_Base_Outer", CubeListBuilder.create().texOffs(0, 19).addBox(-7.0F, -5.0F, -7.0F, 14.0F, 10.0F, 14.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (float) Math.PI, (float) Math.PI));
        PartDefinition Chest_top_outer = Chest_top.addOrReplaceChild("Chest_top_outer", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (float) Math.PI, (float) Math.PI));
        Chest_top_outer.addOrReplaceChild("Chest_lock_outer", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 14.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.Chest_Base.render(pose, buffer, light, overlay, red, green, blue, alpha);
    }
    private void showAllBodyPart(boolean showIn) {
        this.Leg0_r_Seg0.visible = showIn;
        this.Leg0_l_Seg0.visible = showIn;
        this.Leg1_r_Seg0.visible = showIn;
        this.Leg1_l_Seg0.visible = showIn;
        this.Pincer_r_Seg0.visible = showIn;
        this.Pincer_l_Seg0.visible = showIn;
    }

    private void toCamouflagePose(float TickIn) {
        this.Leg0_r_Seg0.zRot = this.GradientAnimation(-0.5009095F, 0.59184116F, TickIn);
        this.Leg0_r_Seg0.x = this.GradientAnimation(-6.0F, -5.0F, TickIn);
        this.Leg0_r_Seg0.y = this.GradientAnimation(4.0F, 3.5F, TickIn);
        this.Leg0_l_Seg0.zRot = this.GradientAnimation(0.5009095F, -0.59184116F, TickIn);
        this.Leg0_l_Seg0.x = this.GradientAnimation(6.0F, 5.0F, TickIn);
        this.Leg0_l_Seg0.y = this.GradientAnimation(4.0F, 3.5F, TickIn);
        this.Leg0_r_Seg1.zRot = this.GradientAnimation(0.8651597F, 0.13665928F, TickIn);
        this.Leg0_l_Seg1.zRot = this.GradientAnimation(-0.8651597F, -0.13665928F, TickIn);
        this.Leg1_r_Seg0.xRot = this.GradientAnimation(0.0F, -0.045553092F, TickIn);
        this.Leg1_r_Seg0.yRot = this.GradientAnimation(0.5462881F, 0.0F, TickIn);
        this.Leg1_r_Seg0.zRot = this.GradientAnimation(-0.5009095F, 0.31869712F, TickIn);
        this.Leg1_r_Seg0.x = this.GradientAnimation(-6.0F, -5.0F, TickIn);
        this.Leg1_r_Seg0.y = this.GradientAnimation(4.0F, 3.0F, TickIn);
        this.Leg1_l_Seg0.xRot = this.GradientAnimation(0.0F, -0.045553092F, TickIn);
        this.Leg1_l_Seg0.yRot = this.GradientAnimation(-0.5462881F, 0.0F, TickIn);
        this.Leg1_l_Seg0.zRot = this.GradientAnimation(0.5009095F, -0.31869712F, TickIn);
        this.Leg1_l_Seg0.x = this.GradientAnimation(6.0F, 5.0F, TickIn);
        this.Leg1_l_Seg0.y = this.GradientAnimation(4.0F, 3.0F, TickIn);
        this.Leg1_r_Seg1.zRot = this.GradientAnimation(0.8651597F, 0.13665928F, TickIn);
        this.Leg1_l_Seg1.zRot = this.GradientAnimation(-0.8651597F, -0.13665928F, TickIn);
        this.Pincer_r_Seg0.x = this.GradientAnimation(-6.0F, 0.6F, TickIn);
        this.Pincer_r_Seg0.z = this.GradientAnimation(-4.0F, 3.6F, TickIn);
        this.Pincer_l_Seg0.x = this.GradientAnimation(6.0F, -0.6F, TickIn);
        this.Pincer_l_Seg0.z = this.GradientAnimation(-4.0F, 3.6F, TickIn);
        this.Pincer_r_Seg1.xRot = this.GradientAnimation(-0.3642502F, -0.59184116F, TickIn);
        this.Pincer_r_Seg1.yRot = this.GradientAnimation(0.0F, -0.63739425F, TickIn);
        this.Pincer_l_Seg1.xRot = this.GradientAnimation(-0.3642502F, -0.59184116F, TickIn);
        this.Pincer_l_Seg1.yRot = this.GradientAnimation(0.0F, 0.63739425F, TickIn);
        this.Pincer_r_Seg3.xRot = this.GradientAnimation(0.0F, -0.4553564F, TickIn);
        this.Pincer_l_Seg3.xRot = this.GradientAnimation(0.0F, -0.4553564F, TickIn);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float i = (float)entityIn.getSitTimer() / 20.0F;
        float j = (float)entityIn.getAttackTimer() / 5.0F;
        float f = 0.5F;
        if ((entityIn.isAggressive() || entityIn.isTame()) && !entityIn.isInSittingPose()) {
            this.showAllBodyPart(true);
            if (i <= 1.0F) {
                this.toCamouflagePose(i);
            }

            if (i == 1.0F) {
                this.Chest_top.xRot = -0.2F
                    + -0.02F * Mth.sin(0.12F * ageInTicks + 0.1F)
                    + 0.06F * Mth.cos(limbSwing * f) * limbSwingAmount;
                this.Chest_Base.zRot = 0.08F * Mth.cos(limbSwing * f) * limbSwingAmount;
                this.Chest_Base.y = 18.0F + Mth.cos(limbSwing * f);
                this.Chest_Base.setPos(0.0F, 18.0F, 1.0F);
                this.Eye_r.setPos(-2.0F, -4.0F, -5.0F);
                this.Eye_l.setPos(2.0F, -4.0F, -5.0F);
                this.Leg0_r_Seg0.zRot = -0.5F + Mth.cos(limbSwing * f) * 0.7F * limbSwingAmount;
                this.Leg0_l_Seg0.zRot = 0.5F + Mth.cos(limbSwing * f + (float) (Math.PI / 2)) * 0.7F * limbSwingAmount;
                this.Leg1_r_Seg0.zRot = -0.5F + Mth.cos(limbSwing * f + (float) Math.PI) * 0.7F * limbSwingAmount;
                this.Leg1_l_Seg0.zRot = 0.5F + Mth.cos(limbSwing * f + (float) (Math.PI * 3.0 / 2.0)) * 0.7F * limbSwingAmount;
            }

            if (j > 0.0F) {
                this.Pincer_l_Seg0.y = this.GradientAnimation(-4.0F, 1.0F, j);
                this.Pincer_r_Seg0.y = this.GradientAnimation(-4.0F, 1.0F, j);
            } else {
                this.Pincer_l_Seg0.y = 1.0F + -0.55F * Mth.sin(0.12F * ageInTicks);
                this.Pincer_r_Seg0.y = 1.0F + -0.55F * Mth.sin(0.12F * ageInTicks);
            }
        } else if (entityIn.isInSittingPose()) {
            if (i >= 0.0F) {
                this.toCamouflagePose(i);
                this.showAllBodyPart(true);
            }

            if (i == 0.0F) {
                this.showAllBodyPart(false);
                if (!entityIn.isTame() && entityIn.idleTimer <= 0) {
                    this.Chest_top.xRot = 0.0F;
                    this.Chest_Base.zRot = 0.0F;
                    this.Chest_Base.y = 18.0F;
                    this.Chest_Base.setPos(0.0F, 19.0F, 1.0F);
                    this.Eye_r.setPos(0.0F, 0.0F, 0.0F);
                    this.Eye_l.setPos(0.0F, 0.0F, 0.0F);
                } else {
                    this.Chest_top.xRot = -0.2F + -0.02F * Mth.sin(0.12F * ageInTicks + 0.1F);
                    this.Chest_Base.zRot = 0.0F;
                    this.Chest_Base.y = 18.0F;
                    this.Chest_Base.setPos(0.0F, 18.0F, 1.0F);
                    this.Eye_r.setPos(-2.0F, -4.0F, -5.0F);
                    this.Eye_l.setPos(2.0F, -4.0F, -5.0F);
                }
            }
        }
    }
}
