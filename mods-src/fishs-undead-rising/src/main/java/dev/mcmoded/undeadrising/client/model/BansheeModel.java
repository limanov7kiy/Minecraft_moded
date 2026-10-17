package dev.mcmoded.undeadrising.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.undeadrising.entity.floating.BansheeEntity;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/// Модель банши: парящий торс с лохмотьями вместо ног и развевающимися волосами; полупрозрачная (альфа ≤ 0.85).
/// Текстура 64×64. Геометрия и анимация — как в оригинале.
/// Оригинал: Fish's Undead Rising (Fish), `client/model/entity/BansheeModel`.
public class BansheeModel<T extends BansheeEntity> extends FURBaseModel<T> implements HeadedModel {
    public final ModelPart Body_base;
    public final ModelPart Body_waist;
    public final ModelPart Body_chest;
    public final ModelPart Neck0;
    public final ModelPart Neck1;
    public final ModelPart Head;
    public final ModelPart Head_teeth;
    public final ModelPart Jaw0;
    public final ModelPart Body_lower_l;
    public final ModelPart Body_lower_r;
    public final ModelPart Hair_back;
    public final ModelPart Jaw1;
    public final ModelPart Hair_mid;
    public final ModelPart Body_lower_front;
    public final ModelPart Hair_l;
    public final ModelPart Hair_front;
    public final ModelPart Arm_r_Seg0;
    public final ModelPart Arm_r_Seg2;
    public final ModelPart Hair_r;
    public final ModelPart Body_lower_back;
    public final ModelPart Arm_l_Seg0;
    public final ModelPart Arm_l_Seg2;
    public final ModelPart Jaw1_teeth;
    

    public BansheeModel(ModelPart root) {
        this.Body_base = root.getChild("Body_base");
        this.Body_waist = this.Body_base.getChild("Body_waist");
        this.Body_chest = this.Body_waist.getChild("Body_chest");
        this.Neck0 = this.Body_chest.getChild("Neck0");
        this.Neck1 = this.Neck0.getChild("Neck1");
        this.Head = this.Neck1.getChild("Head");
        this.Head_teeth = this.Head.getChild("Head_teeth");
        this.Jaw0 = this.Head.getChild("Jaw0");
        this.Body_lower_l = this.Body_base.getChild("Body_lower_l");
        this.Body_lower_r = this.Body_base.getChild("Body_lower_r");
        this.Hair_back = this.Head.getChild("Hair_back");
        this.Jaw1 = this.Jaw0.getChild("Jaw1");
        this.Hair_mid = this.Head.getChild("Hair_mid");
        this.Body_lower_front = this.Body_base.getChild("Body_lower_front");
        this.Hair_l = this.Head.getChild("Hair_l");
        this.Hair_front = this.Head.getChild("Hair_front");
        this.Arm_r_Seg0 = this.Body_chest.getChild("Arm_r_Seg0");
        this.Arm_r_Seg2 = this.Arm_r_Seg0.getChild("Arm_r_Seg2");
        this.Hair_r = this.Head.getChild("Hair_r");
        this.Body_lower_back = this.Body_base.getChild("Body_lower_back");
        this.Arm_l_Seg0 = this.Body_chest.getChild("Arm_l_Seg0");
        this.Arm_l_Seg2 = this.Arm_l_Seg0.getChild("Arm_l_Seg2");
        this.Jaw1_teeth = this.Jaw1.getChild("Jaw1_teeth");
    
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition Body_base = root.addOrReplaceChild("Body_base", CubeListBuilder.create().texOffs(19, 15).addBox(-4.0F, -4.0F, -3.0F, 8.0F, 14.0F, 6.0F), PartPose.offset(0.0F, 10.0F, 1.0F));
        PartDefinition Body_waist = Body_base.addOrReplaceChild("Body_waist", CubeListBuilder.create().texOffs(0, 45).addBox(-3.5F, -5.0F, -1.5F, 7.0F, 5.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -3.2F, 0.0F, 0.091106184F, 0.0F, -0.045553092F));
        PartDefinition Body_chest = Body_waist.addOrReplaceChild("Body_chest", CubeListBuilder.create().texOffs(0, 35).addBox(-5.0F, -4.0F, -2.5F, 10.0F, 5.0F, 5.0F), PartPose.offsetAndRotation(0.0F, -5.0F, -0.5F, 0.5009095F, 0.0F, 0.0F));
        PartDefinition Neck0 = Body_chest.addOrReplaceChild("Neck0", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 5.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.3642502F, 0.0F, 0.0F));
        PartDefinition Neck1 = Neck0.addOrReplaceChild("Neck1", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -4.0F, 0.3F, (float) (-Math.PI / 3), 0.045553092F, -0.18203785F));
        PartDefinition Head = Neck1.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -3.0F, -8.0F, 8.0F, 6.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        Head.addOrReplaceChild("Head_teeth", CubeListBuilder.create().texOffs(0, 15).addBox(-3.5F, 3.0F, -7.5F, 7.0F, 1.0F, 4.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition Jaw0 = Head.addOrReplaceChild("Jaw0", CubeListBuilder.create().texOffs(24, 0).addBox(-3.5F, -3.5F, -3.0F, 7.0F, 5.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.59184116F, 0.0F, 0.0F));
        Body_base.addOrReplaceChild("Body_lower_l", CubeListBuilder.create().texOffs(34, 48).mirror(true).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 16.0F, 0.0F), PartPose.offsetAndRotation(4.0F, -2.0F, 0.0F, 0.0F, (float) (-Math.PI / 2), 0.0F));
        Body_base.addOrReplaceChild("Body_lower_r", CubeListBuilder.create().texOffs(34, 48).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 16.0F, 0.0F), PartPose.offsetAndRotation(-4.0F, -2.0F, 0.0F, 0.0F, (float) (-Math.PI / 2), 0.0F));
        Head.addOrReplaceChild("Hair_back", CubeListBuilder.create().texOffs(31, 37).mirror(true).addBox(-4.0F, -10.0F, 0.0F, 8.0F, 10.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -1.775698F, 0.0F, 0.0F));
        PartDefinition Jaw1 = Jaw0.addOrReplaceChild("Jaw1", CubeListBuilder.create().texOffs(40, 2).addBox(-3.0F, 0.0F, -7.8F, 6.0F, 2.0F, 6.0F), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.091106184F, 0.0F, -0.045553092F));
        Head.addOrReplaceChild("Hair_mid", CubeListBuilder.create().texOffs(31, 37).mirror(true).addBox(-4.0F, -10.0F, 0.0F, 8.0F, 10.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -3.0F, -2.5F, -1.3658947F, 0.0F, 0.0F));
        Body_base.addOrReplaceChild("Body_lower_front", CubeListBuilder.create().texOffs(48, 48).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 16.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -2.0F, -3.0F, -0.31869712F, 0.0F, 0.0F));
        Head.addOrReplaceChild("Hair_l", CubeListBuilder.create().texOffs(21, 48).mirror(true).addBox(-3.0F, -10.0F, 0.0F, 6.0F, 10.0F, 0.0F), PartPose.offsetAndRotation(4.0F, 0.1F, -4.0F, -1.1838568F, 0.0F, (float) (Math.PI / 2)));
        Head.addOrReplaceChild("Hair_front", CubeListBuilder.create().texOffs(31, 37).addBox(-4.0F, -10.0F, 0.0F, 8.0F, 10.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -3.0F, -5.0F, -0.91053826F, 0.0F, 0.0F));
        PartDefinition Arm_r_Seg0 = Body_chest.addOrReplaceChild("Arm_r_Seg0", CubeListBuilder.create().texOffs(52, 34).mirror(true).addBox(-2.0F, -1.0F, -2.0F, 2.0F, 12.0F, 2.0F), PartPose.offsetAndRotation(-5.0F, -2.0F, 0.0F, -0.7740535F, 0.0F, 0.5462881F));
        Arm_r_Seg0.addOrReplaceChild("Arm_r_Seg2", CubeListBuilder.create().texOffs(0, 50).addBox(0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 6.0F), PartPose.offset(-2.0F, 1.0F, 0.0F));
        Head.addOrReplaceChild("Hair_r", CubeListBuilder.create().texOffs(21, 48).addBox(-3.0F, -10.0F, 0.0F, 6.0F, 10.0F, 0.0F), PartPose.offsetAndRotation(-4.0F, 0.1F, -4.0F, -1.1838568F, 0.0F, (float) (-Math.PI / 2)));
        Body_base.addOrReplaceChild("Body_lower_back", CubeListBuilder.create().texOffs(48, 48).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 16.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -2.0F, 3.0F, 0.27314404F, 0.0F, 0.0F));
        PartDefinition Arm_l_Seg0 = Body_chest.addOrReplaceChild("Arm_l_Seg0", CubeListBuilder.create().texOffs(52, 18).addBox(0.0F, -1.0F, -2.0F, 2.0F, 12.0F, 2.0F), PartPose.offsetAndRotation(5.0F, -2.0F, 0.0F, -0.7740535F, 0.0F, -0.5462881F));
        Arm_l_Seg0.addOrReplaceChild("Arm_l_Seg2", CubeListBuilder.create().texOffs(0, 50).mirror(true).addBox(0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 6.0F), PartPose.offset(2.0F, 1.0F, 0.0F));
        Jaw1.addOrReplaceChild("Jaw1_teeth", CubeListBuilder.create().texOffs(0, 21).addBox(-2.5F, 0.0F, -7.5F, 5.0F, 1.0F, 4.0F), PartPose.offset(0.0F, -1.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.Body_base.render(pose, buffer, light, overlay, red, green, blue, Math.min(alpha, 0.85F));
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.Head_Looking(this.Head, 0.0F, 0.0F, netHeadYaw, headPitch);
        this.SwingX_Sin(this.Hair_front, -0.91053826F, ageInTicks, 0.21F, 0.06F, false, 0.0F);
        this.SwingX_Sin(this.Hair_mid, -1.3658947F, ageInTicks, 0.23F, 0.08F, false, (float) (Math.PI / 4));
        this.SwingX_Sin(this.Hair_back, -1.775698F, ageInTicks, 0.2F, 0.06F, false, 0.0F);
        this.SwingX_Sin(this.Hair_r, -1.1838568F, ageInTicks, 0.17F, 0.09F, false, (float) (Math.PI / 4));
        this.SwingX_Sin(this.Hair_l, -1.1838568F, ageInTicks, 0.19F, 0.07F, true, 0.0F);
        this.SwingX_Sin(this.Jaw1, 0.091106184F, ageInTicks, -0.03F, 0.06F, false, 0.0F);
        if (limbSwingAmount < 0.08F && this.Body_base.xRot > 0.0F) {
            this.Body_base.xRot -= 0.005F;
            this.Head.xRot += 0.005F;
        } else if (limbSwingAmount >= 0.08F && this.Body_base.xRot < 0.35F) {
            this.Body_base.xRot += 0.005F;
            this.Head.xRot -= 0.005F;
        }

        this.SwingX_Sin(this.Body_lower_front, -0.5369F, ageInTicks, 0.3F, 0.08F, true, 0.0F);
        this.SwingZ_Sin(this.Body_lower_r, 0.4894F, ageInTicks, 0.24F, 0.07F, false, (float) (Math.PI / 4));
        this.SwingZ_Sin(this.Body_lower_l, -0.4894F, ageInTicks, 0.27F, 0.09F, true, 1.4137167F);
        this.SwingX_Sin(this.Body_lower_back, 0.5786F, ageInTicks, 0.38F, 0.06F, false, (float) (Math.PI * 7.0 / 10.0));
        this.SwingY_Sin(this.Arm_r_Seg2, 0.0F, ageInTicks, 0.22F, 0.08F, false, 0.0F);
        this.SwingY_Sin(this.Arm_l_Seg2, 0.0F, ageInTicks, 0.18F, 0.06F, true, 0.0F);
        boolean aggressive = entityIn.isAggressive();
        float i = (float)entityIn.getAttackTimer() / 20.0F;
        float j = (float)entityIn.getSpellTicks() / 30.0F;
        if (entityIn.isSpellcasting()) {
            this.Arm_r_Seg0.xRot = this.GradientAnimation(1.1046189F, -0.61557764F, j);
            this.Arm_l_Seg0.xRot = this.GradientAnimation(1.1046189F, -0.61557764F, j);
            this.Arm_r_Seg0.yRot = 2.1116738F;
            this.Arm_l_Seg0.yRot = -2.1116738F;
            this.Arm_r_Seg0.zRot = 1.5627679F;
            this.Arm_l_Seg0.zRot = -1.5627679F;
            if (this.Body_base.y > 5.0F) {
                this.Body_base.y -= 0.17F;
            }
        } else if (i > 0.0F) {
            this.Jaw0.xRot = this.GradientAnimation((float)Math.toRadians(57.39), (float)Math.toRadians(18.26), i);
            this.Body_chest.xRot = this.GradientAnimation((float)Math.toRadians(-2.61), (float)Math.toRadians(44.35), i);
            this.Arm_r_Seg0.xRot = this.GradientAnimation((float)Math.toRadians(67.83), (float)Math.toRadians(-86.09), i);
            this.Arm_r_Seg0.yRot = this.GradientAnimation((float)Math.toRadians(101.74), (float)Math.toRadians(-13.04), i);
            this.Arm_r_Seg0.zRot = this.GradientAnimation((float)Math.toRadians(106.96), (float)Math.toRadians(65.22), i);
            this.Arm_l_Seg0.xRot = this.GradientAnimation((float)Math.toRadians(67.83), (float)Math.toRadians(-86.09), i);
            this.Arm_l_Seg0.yRot = this.GradientAnimation((float)Math.toRadians(-101.74), (float)Math.toRadians(13.04), i);
            this.Arm_l_Seg0.zRot = this.GradientAnimation((float)Math.toRadians(-106.96), (float)Math.toRadians(-65.22), i);
        } else if (aggressive) {
            this.setRotateAngle(this.Arm_r_Seg0, -1.9577358F, 0.0F, 0.091106184F);
            this.setRotateAngle(this.Arm_l_Seg0, -1.821251F, 0.0F, 0.045553092F);
            if (this.Body_base.y < 10.0F) {
                this.Body_base.y += 0.17F;
            } else {
                this.Body_base.y = 11.0F + Mth.sin(0.08F * ageInTicks);
            }
        } else {
            this.SwingX_Sin(this.Jaw0, 0.59184116F, ageInTicks, -0.04F, 0.06F, false, 0.0F);
            this.setRotateAngle(this.Arm_r_Seg0, -0.7740535F, 0.0F, 0.5462881F);
            this.setRotateAngle(this.Arm_l_Seg0, -0.7740535F, 0.0F, -0.5462881F);
            if (this.Body_base.y < 10.0F) {
                this.Body_base.y += 0.17F;
            } else {
                this.Body_base.y = 11.0F + Mth.sin(0.08F * ageInTicks);
            }
        }
    }

    @Override
    public ModelPart getHead() {
        return this.Head;
    }
}
