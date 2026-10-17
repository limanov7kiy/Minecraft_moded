package dev.mcmoded.undeadrising.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.undeadrising.entity.flying.VespaEntity;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/// Модель веспы (седло — часть модели, рисуется слоем седла). Текстура 128×64. Геометрия и анимация — как в оригинале.
/// Оригинал: Fish's Undead Rising (Fish), `client/model/entity/VespaModel`.
public class VespaModel<T extends VespaEntity> extends FlyingBaseModel<T> implements HeadedModel {
    public final ModelPart Throax_base;
    public final ModelPart Waist;
    public final ModelPart leg_r_2;
    public final ModelPart Head;
    public final ModelPart Mouth;
    public final ModelPart leg_l_2;
    public final ModelPart Eye_l;
    public final ModelPart Wing_1_l;
    public final ModelPart Wing_0_l;
    public final ModelPart Antennae_r;
    public final ModelPart Cheek_l;
    public final ModelPart Cheek_r;
    public final ModelPart UAbdomen1;
    public final ModelPart UAbdomen2;
    public final ModelPart Eye_r;
    public final ModelPart UAbdomen3;
    public final ModelPart UAbdomen4;
    public final ModelPart Stinger;
    public final ModelPart leg_l_1;
    public final ModelPart leg_r_0;
    public final ModelPart Wing_0_r;
    public final ModelPart Antennae_l;
    public final ModelPart Throax_0;
    public final ModelPart leg_r_1;
    public final ModelPart Wing_1_r;
    public final ModelPart leg_l_0;
    public final ModelPart saddle;
    public final ModelPart saddle_top;
    

    public VespaModel(ModelPart root) {
        this.Throax_base = root.getChild("Throax_base");
        this.Waist = this.Throax_base.getChild("Waist");
        this.leg_r_2 = this.Throax_base.getChild("leg_r_2");
        this.Head = this.Throax_base.getChild("Head");
        this.Mouth = this.Head.getChild("Mouth");
        this.leg_l_2 = this.Throax_base.getChild("leg_l_2");
        this.Eye_l = this.Head.getChild("Eye_l");
        this.Wing_1_l = this.Throax_base.getChild("Wing_1_l");
        this.Wing_0_l = this.Throax_base.getChild("Wing_0_l");
        this.Antennae_r = this.Head.getChild("Antennae_r");
        this.Cheek_l = this.Head.getChild("Cheek_l");
        this.Cheek_r = this.Head.getChild("Cheek_r");
        this.UAbdomen1 = this.Waist.getChild("UAbdomen1");
        this.UAbdomen2 = this.UAbdomen1.getChild("UAbdomen2");
        this.Eye_r = this.Head.getChild("Eye_r");
        this.UAbdomen3 = this.UAbdomen2.getChild("UAbdomen3");
        this.UAbdomen4 = this.UAbdomen3.getChild("UAbdomen4");
        this.Stinger = this.UAbdomen4.getChild("Stinger");
        this.leg_l_1 = this.Throax_base.getChild("leg_l_1");
        this.leg_r_0 = this.Throax_base.getChild("leg_r_0");
        this.Wing_0_r = this.Throax_base.getChild("Wing_0_r");
        this.Antennae_l = this.Head.getChild("Antennae_l");
        this.Throax_0 = this.Throax_base.getChild("Throax_0");
        this.leg_r_1 = this.Throax_base.getChild("leg_r_1");
        this.Wing_1_r = this.Throax_base.getChild("Wing_1_r");
        this.leg_l_0 = this.Throax_base.getChild("leg_l_0");
        this.saddle = this.Throax_0.getChild("saddle");
        this.saddle_top = this.saddle.getChild("saddle_top");
    
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition Throax_base = root.addOrReplaceChild("Throax_base", CubeListBuilder.create().texOffs(38, 14).mirror(true).addBox(-6.0F, -6.0F, -4.0F, 12.0F, 12.0F, 8.0F), PartPose.offset(0.0F, 12.0F, 7.5F));
        PartDefinition Waist = Throax_base.addOrReplaceChild("Waist", CubeListBuilder.create().texOffs(100, 50).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 1.0F));
        Throax_base.addOrReplaceChild("leg_r_2", CubeListBuilder.create().texOffs(56, 0).addBox(-11.0F, -1.0F, -1.0F, 12.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(-4.0F, 6.0F, -2.5F, 0.0F, (float) (Math.PI / 8), -0.58119464F));
        PartDefinition Head = Throax_base.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -12.0F, 8.0F, 8.0F, 12.0F), PartPose.offset(0.0F, 2.0F, -8.0F));
        Head.addOrReplaceChild("Mouth", CubeListBuilder.create().texOffs(24, 22).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 6.0F, 2.0F), PartPose.offset(0.0F, 2.0F, -12.0F));
        Throax_base.addOrReplaceChild("leg_l_2", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, -1.0F, -1.0F, 12.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(4.0F, 6.0F, -2.5F, 0.0F, (float) (-Math.PI / 8), 0.58119464F));
        Head.addOrReplaceChild("Eye_l", CubeListBuilder.create().texOffs(8, 22).addBox(0.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.offset(4.0F, -1.0F, -10.0F));
        Throax_base.addOrReplaceChild("Wing_1_l", CubeListBuilder.create().texOffs(0, 30).mirror(true).addBox(0.0F, 0.0F, -1.0F, 16.0F, 0.0F, 6.0F), PartPose.offsetAndRotation(6.0F, -4.0F, -5.0F, 0.0F, -0.4098033F, 0.0F));
        Throax_base.addOrReplaceChild("Wing_0_l", CubeListBuilder.create().texOffs(0, 36).mirror(true).addBox(0.0F, 0.0F, -1.0F, 24.0F, 0.0F, 8.0F), PartPose.offsetAndRotation(6.0F, -4.0F, -8.0F, 0.0F, 0.22759093F, 0.0F));
        Head.addOrReplaceChild("Antennae_r", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(-2.5F, -3.0F, -10.0F, -0.27314404F, 0.0F, 0.0F));
        Head.addOrReplaceChild("Cheek_l", CubeListBuilder.create().texOffs(40, 0).mirror(true).addBox(0.0F, -2.0F, -4.0F, 4.0F, 6.0F, 8.0F), PartPose.offset(4.0F, 0.0F, -4.0F));
        Head.addOrReplaceChild("Cheek_r", CubeListBuilder.create().texOffs(40, 0).addBox(-4.0F, -2.0F, -4.0F, 4.0F, 6.0F, 8.0F), PartPose.offset(-4.0F, 0.0F, -4.0F));
        PartDefinition UAbdomen1 = Waist.addOrReplaceChild("UAbdomen1", CubeListBuilder.create().texOffs(66, 48).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 1.0F, 5.0F, 0.22759093F, 0.0F, 0.0F));
        PartDefinition UAbdomen2 = UAbdomen1.addOrReplaceChild("UAbdomen2", CubeListBuilder.create().texOffs(88, 0).addBox(-6.0F, -8.0F, 0.0F, 12.0F, 12.0F, 8.0F), PartPose.offsetAndRotation(0.0F, 2.0F, 4.0F, -0.22759093F, 0.0F, 0.0F));
        Head.addOrReplaceChild("Eye_r", CubeListBuilder.create().texOffs(8, 22).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.offset(-4.0F, -1.0F, -10.0F));
        PartDefinition UAbdomen3 = UAbdomen2.addOrReplaceChild("UAbdomen3", CubeListBuilder.create().texOffs(100, 28).addBox(-4.0F, -6.0F, 0.0F, 8.0F, 8.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, -0.22759093F, 0.0F, 0.0F));
        PartDefinition UAbdomen4 = UAbdomen3.addOrReplaceChild("UAbdomen4", CubeListBuilder.create().texOffs(84, 28).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -1.0F, 5.0F, -0.22759093F, 0.0F, 0.0F));
        UAbdomen4.addOrReplaceChild("Stinger", CubeListBuilder.create().texOffs(72, 8).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 12.0F), PartPose.offset(0.0F, 0.0F, -3.0F));
        Throax_base.addOrReplaceChild("leg_l_1", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, -1.0F, -1.0F, 12.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(4.0F, 6.0F, -3.5F, 0.0F, (float) (Math.PI / 8), 0.58119464F));
        Throax_base.addOrReplaceChild("leg_r_0", CubeListBuilder.create().texOffs(56, 0).addBox(-11.0F, -1.0F, -1.0F, 12.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(-4.0F, 6.0F, -4.5F, 0.0F, (float) (-Math.PI / 4), (float) (-Math.PI / 4)));
        Throax_base.addOrReplaceChild("Wing_0_r", CubeListBuilder.create().texOffs(0, 36).addBox(-24.0F, 0.0F, -1.0F, 24.0F, 0.0F, 8.0F), PartPose.offsetAndRotation(-6.0F, -4.0F, -8.0F, 0.0F, -0.22759093F, 0.0F));
        Head.addOrReplaceChild("Antennae_l", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(2.5F, -3.0F, -10.0F, -0.27314404F, 0.0F, 0.0F));
        PartDefinition Throax_0 = Throax_base.addOrReplaceChild("Throax_0", CubeListBuilder.create().texOffs(0, 44).addBox(-6.5F, -6.0F, -4.0F, 13.0F, 12.0F, 8.0F), PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, 0.27314404F, 0.0F, 0.0F));
        Throax_base.addOrReplaceChild("leg_r_1", CubeListBuilder.create().texOffs(56, 0).addBox(-11.0F, -1.0F, -1.0F, 12.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(-4.0F, 6.0F, -3.5F, 0.0F, (float) (-Math.PI / 8), -0.58119464F));
        Throax_base.addOrReplaceChild("Wing_1_r", CubeListBuilder.create().texOffs(0, 30).addBox(-16.0F, 0.0F, -1.0F, 16.0F, 0.0F, 6.0F), PartPose.offsetAndRotation(-6.0F, -4.0F, -5.0F, 0.0F, 0.4098033F, 0.0F));
        Throax_base.addOrReplaceChild("leg_l_0", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, -1.0F, -1.0F, 12.0F, 2.0F, 2.0F), PartPose.offsetAndRotation(4.0F, 6.0F, -4.5F, 0.0F, (float) (Math.PI / 4), (float) (Math.PI / 4)));
        PartDefinition saddle = Throax_0.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(34, 44).addBox(-6.0F, -19.0F, -5.0F, 12.0F, 1.0F, 7.0F), PartPose.offset(0.0F, 12.0F, 1.5F));
        saddle.addOrReplaceChild("saddle_top", CubeListBuilder.create().texOffs(66, 44).addBox(-6.0F, -2.5F, 2.5F, 12.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -18.5F, -1.5F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.Throax_base.render(pose, buffer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float vibrate_rate = 0.5F;
        float i = (float)entityIn.getAttackTimer();
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.Head_Looking(this.Head, 0.0F, 0.0F, netHeadYaw, headPitch);
        this.Wing_0_r.xRot = 0.0F;
        this.Wing_0_r.yRot = -0.22759093F;
        this.Wing_0_r.zRot = 0.5F * Mth.sin(4.0F * ageInTicks);
        this.Wing_1_r.xRot = 0.0F;
        this.Wing_1_r.yRot = 0.4098033F;
        this.Wing_1_r.zRot = 0.5F * Mth.sin(4.0F * ageInTicks + (float) Math.PI);
        this.Wing_0_l.xRot = 0.0F;
        this.Wing_0_l.yRot = 0.22759093F;
        this.Wing_0_l.zRot = 0.5F * Mth.sin(4.0F * ageInTicks + (float) Math.PI);
        this.Wing_1_l.xRot = 0.0F;
        this.Wing_1_l.yRot = -0.4098033F;
        this.Wing_1_l.zRot = 0.5F * Mth.sin(4.0F * ageInTicks);
        this.Throax_base.y = 7.0F + (entityIn.isVehicle() ? 5.0F : 5.0F * Mth.sin(ageInTicks * vibrate_rate));
        this.Stinger.z = -3.0F;
        this.setRotateAngle(
            this.leg_r_0, 0.0F, 0.4553564F + 0.02F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 2)) * (float) Math.PI, -1.6845918F
        );
        this.setRotateAngle(
            this.leg_r_1, 0.0F, 0.7740535F + 0.02F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 10)) * (float) Math.PI, -1.5934856F
        );
        this.setRotateAngle(
            this.leg_r_2, 0.0F, 1.2292354F + 0.02F * Mth.cos(ageInTicks * vibrate_rate + 0.47123894F) * (float) Math.PI, -1.3658947F
        );
        this.setRotateAngle(
            this.leg_l_0, 0.0F, -0.4553564F + 0.02F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 2)) * (float) Math.PI, 1.6845918F
        );
        this.setRotateAngle(
            this.leg_l_1, 0.0F, -0.7740535F + 0.02F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 10)) * (float) Math.PI, 1.5934856F
        );
        this.setRotateAngle(
            this.leg_l_2, 0.0F, -1.2292354F + 0.02F * Mth.cos(ageInTicks * vibrate_rate + 0.47123894F) * (float) Math.PI, 1.3658947F
        );
        if (this.state.equals(State.WAITING)) {
            vibrate_rate = 0.05F;
            this.Throax_base.y = 12.0F;
            this.Waist.xRot = 0.02F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 2)) * (float) Math.PI;
            this.UAbdomen1.xRot = 0.02F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 10)) * (float) Math.PI;
            this.UAbdomen2.xRot = 0.01F * Mth.cos(ageInTicks * vibrate_rate + 0.47123894F) * (float) Math.PI;
            this.UAbdomen3.xRot = 0.01F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 5)) * (float) Math.PI;
            this.UAbdomen4.xRot = 0.01F * Mth.cos(ageInTicks * vibrate_rate + 0.47123894F) * (float) Math.PI;
            this.Stinger.xRot = 0.01F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 10)) * (float) Math.PI;
            this.setRotateAngle(this.Wing_0_r, 0.86009824F, 1.2971287F, -0.50823987F);
            this.setRotateAngle(this.Wing_1_r, 0.07819075F, 1.2699016F, -1.2901473F);
            this.setRotateAngle(this.Wing_0_l, 0.86009824F, -1.2971287F, 0.50823987F);
            this.setRotateAngle(this.Wing_1_l, 0.07819075F, -1.2699016F, 1.2901473F);
            this.setRotateAngle(this.leg_r_0, 0.0F, (float) (-Math.PI / 4), (float) (-Math.PI / 4));
            this.setRotateAngle(this.leg_r_1, 0.0F, (float) (-Math.PI / 8), -0.58119464F);
            this.setRotateAngle(this.leg_r_2, 0.0F, (float) (Math.PI / 8), -0.58119464F);
            this.setRotateAngle(this.leg_l_0, 0.0F, (float) (Math.PI / 4), (float) (Math.PI / 4));
            this.setRotateAngle(this.leg_l_1, 0.0F, (float) (Math.PI / 8), 0.58119464F);
            this.setRotateAngle(this.leg_l_2, 0.0F, (float) (-Math.PI / 8), 0.58119464F);
            this.leg_r_0.zRot = (float) (-Math.PI / 4);
            this.leg_l_0.zRot = (float) (Math.PI / 4);
            this.leg_r_1.zRot = -0.58119464F;
            this.leg_l_1.zRot = 0.58119464F;
            this.leg_r_2.zRot = -0.58119464F;
            this.leg_l_2.zRot = 0.58119464F;
            this.leg_r_0.yRot = (float) (-Math.PI / 4);
            this.leg_l_0.yRot = (float) (Math.PI / 4);
            this.leg_r_1.yRot = (float) (-Math.PI / 8);
            this.leg_l_1.yRot = (float) (Math.PI / 8);
            this.leg_r_2.yRot = (float) (Math.PI / 8);
            this.leg_l_2.yRot = (float) (-Math.PI / 8);
            float f3 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
            float f4 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbSwingAmount;
            float f5 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * limbSwingAmount;
            float f7 = Math.abs(Mth.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
            float f8 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) Math.PI) * 0.4F) * limbSwingAmount;
            float f9 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * limbSwingAmount;
            this.leg_r_0.yRot += f3;
            this.leg_l_0.yRot += -f3;
            this.leg_r_1.yRot += f4;
            this.leg_l_1.yRot += -f4;
            this.leg_r_2.yRot += f5;
            this.leg_l_2.yRot += -f5;
            this.leg_r_0.zRot += f7;
            this.leg_l_0.zRot += -f7;
            this.leg_r_1.zRot += f8;
            this.leg_l_1.zRot += -f8;
            this.leg_r_2.zRot += f9;
            this.leg_l_2.zRot += -f9;
        } else if (this.state.equals(State.FLYING)) {
            this.Waist.xRot = 0.02F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 2)) * (float) Math.PI;
            this.UAbdomen1.xRot = 0.02F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 10)) * (float) Math.PI;
            this.UAbdomen2.xRot = 0.01F * Mth.cos(ageInTicks * vibrate_rate + 0.47123894F) * (float) Math.PI;
            this.UAbdomen3.xRot = 0.01F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 5)) * (float) Math.PI;
            this.UAbdomen4.xRot = 0.01F * Mth.cos(ageInTicks * vibrate_rate + 0.47123894F) * (float) Math.PI;
            this.Stinger.xRot = 0.01F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 10)) * (float) Math.PI;
        } else if (this.state.equals(State.ATTACKING)) {
            if (i > 8.0F) {
                this.Waist.xRot = this.GradientAnimation(0.0F, -1.0472F, (i - 8.0F) / 12.0F);
                this.UAbdomen1.xRot = this.GradientAnimation(0.2276F, -0.7323F, (i - 8.0F) / 12.0F);
                this.UAbdomen2.xRot = this.GradientAnimation(-0.2276F, -0.6203F, (i - 8.0F) / 12.0F);
                this.UAbdomen3.xRot = this.GradientAnimation(-0.2276F, -0.7512F, (i - 8.0F) / 12.0F);
                this.UAbdomen4.xRot = this.GradientAnimation(-0.2276F, -0.0094F, (i - 8.0F) / 12.0F);
                this.Stinger.xRot = 0.0F;
                this.Stinger.z = this.GradientAnimation(-3.0F, 3.0F, (i - 8.0F) / 12.0F);
            } else if (i > 5.0F) {
                this.Waist.xRot = -1.0472F;
                this.UAbdomen1.xRot = -0.7323F;
                this.UAbdomen2.xRot = -0.6203F;
                this.UAbdomen3.xRot = -0.7512F;
                this.UAbdomen4.xRot = -0.0094F;
                this.Stinger.xRot = 0.0F;
                this.Stinger.z = 3.0F;
            } else if (i > 0.0F) {
                this.Waist.xRot = this.GradientAnimation_s(-1.0472F, 0.0F, i / 5.0F);
                this.UAbdomen1.xRot = this.GradientAnimation_s(-0.7323F, 0.2276F, i / 5.0F);
                this.UAbdomen2.xRot = this.GradientAnimation_s(-0.6203F, -0.2276F, i / 5.0F);
                this.UAbdomen3.xRot = this.GradientAnimation_s(-0.7512F, -0.2276F, i / 5.0F);
                this.UAbdomen4.xRot = this.GradientAnimation_s(-0.0094F, -0.2276F, i / 5.0F);
                this.Stinger.xRot = 0.0F;
                this.Stinger.z = this.GradientAnimation_s(3.0F, -3.0F, i / 5.0F);
            }
        } else if (this.state.equals(State.HOVERING)) {
            this.Waist.xRot = -0.014F * (float)(20 - entityIn.getHoverTimer())
                + 0.02F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 2)) * (float) Math.PI;
            this.UAbdomen1.xRot = -0.014F * (float)(20 - entityIn.getHoverTimer())
                + 0.02F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 10)) * (float) Math.PI;
            this.UAbdomen2.xRot = -0.014F * (float)(20 - entityIn.getHoverTimer())
                + 0.01F * Mth.cos(ageInTicks * vibrate_rate + 0.47123894F) * (float) Math.PI;
            this.UAbdomen3.xRot = -0.014F * (float)(20 - entityIn.getHoverTimer())
                + 0.01F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 5)) * (float) Math.PI;
            this.UAbdomen4.xRot = -0.014F * (float)(20 - entityIn.getHoverTimer())
                + 0.01F * Mth.cos(ageInTicks * vibrate_rate + 0.47123894F) * (float) Math.PI;
            this.Stinger.xRot = -0.014F * (float)(20 - entityIn.getHoverTimer())
                + 0.01F * Mth.cos(ageInTicks * vibrate_rate + (float) (Math.PI / 10)) * (float) Math.PI;
        }
    }

    @Override
    public ModelPart getHead() {
        return this.Head;
    }
}
