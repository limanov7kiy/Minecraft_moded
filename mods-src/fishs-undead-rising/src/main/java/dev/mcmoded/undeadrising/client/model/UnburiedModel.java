package dev.mcmoded.undeadrising.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.undeadrising.entity.IAggressive;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

/// Модель Unburied-семейства (Unburied, Mummy, Mycosis, Frigid): сгорбленный зомби с челюстью и бинтами на руках.
/// Текстура 64×64. Геометрия и анимация — как в оригинале.
/// Оригинал: Fish's Undead Rising (Fish), `client/model/entity/UnburiedModel`.
public class UnburiedModel<T extends LivingEntity> extends FURBaseModel<T> implements ArmedModel, HeadedModel {
    public final ModelPart Body_base;
    public final ModelPart Body_waist;
    public final ModelPart Leg_l_Seg0;
    public final ModelPart Leg_r_Seg0;
    public final ModelPart Body_chest;
    public final ModelPart Arm_l_Seg0;
    public final ModelPart Arm_r_Seg0;
    public final ModelPart Shroom_base;
    public final ModelPart Neck0;
    public final ModelPart Arm_l_Seg1;
    public final ModelPart Arm_r_Seg1;
    public final ModelPart Neck1;
    public final ModelPart Head;
    public final ModelPart Jaw0;
    public final ModelPart Head_teeth;
    public final ModelPart Jaw1;
    public final ModelPart Jaw1_teeth;
    public final ModelPart Leg_l_Seg1;
    public final ModelPart Leg_r_Seg1;
    public final ModelPart Arm_l_bandage;
    public final ModelPart Arm_r_bandage;
    public final ModelPart Hat;

    public UnburiedModel(ModelPart root) {
        this.Body_base = root.getChild("Body_base");
        this.Body_waist = this.Body_base.getChild("Body_waist");
        this.Leg_l_Seg0 = this.Body_base.getChild("Leg_l_Seg0");
        this.Leg_r_Seg0 = this.Body_base.getChild("Leg_r_Seg0");
        this.Leg_l_Seg1 = this.Leg_l_Seg0.getChild("Leg_l_Seg1");
        this.Leg_r_Seg1 = this.Leg_r_Seg0.getChild("Leg_r_Seg1");
        this.Body_chest = this.Body_waist.getChild("Body_chest");
        this.Neck0 = this.Body_chest.getChild("Neck0");
        this.Arm_l_Seg0 = this.Body_chest.getChild("Arm_l_Seg0");
        this.Arm_r_Seg0 = this.Body_chest.getChild("Arm_r_Seg0");
        this.Shroom_base = this.Body_chest.getChild("Shroom_base");
        this.Arm_l_Seg1 = this.Arm_l_Seg0.getChild("Arm_l_Seg1");
        this.Arm_l_bandage = this.Arm_l_Seg0.getChild("Arm_l_bandage");
        this.Arm_r_Seg1 = this.Arm_r_Seg0.getChild("Arm_r_Seg1");
        this.Arm_r_bandage = this.Arm_r_Seg0.getChild("Arm_r_bandage");
        this.Neck1 = this.Neck0.getChild("Neck1");
        this.Head = this.Neck1.getChild("Head");
        this.Jaw0 = this.Head.getChild("Jaw0");
        this.Head_teeth = this.Head.getChild("Head_teeth");
        this.Hat = this.Head.getChild("Hat");
        this.Jaw1 = this.Jaw0.getChild("Jaw1");
        this.Jaw1_teeth = this.Jaw1.getChild("Jaw1_teeth");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition bodyBase = root.addOrReplaceChild("Body_base", CubeListBuilder.create().texOffs(0, 56)
                .addBox(-4.0F, -4.0F, -2.0F, 8.0F, 4.0F, 4.0F), PartPose.offset(0.0F, 10.0F, 0.0F));
        PartDefinition waist = bodyBase.addOrReplaceChild("Body_waist", CubeListBuilder.create().texOffs(0, 45)
                .addBox(-3.5F, -8.0F, -1.5F, 7.0F, 8.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -3.2F, 0.0F, 0.45535642F, 0.0F, -0.045553092F));
        PartDefinition legL0 = bodyBase.addOrReplaceChild("Leg_l_Seg0", CubeListBuilder.create().texOffs(32, 22)
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(1.9F, 0.0F, 0.1F, -0.273144F, 0.0F, -0.091106184F));
        legL0.addOrReplaceChild("Leg_l_Seg1", CubeListBuilder.create().texOffs(40, 22)
                .addBox(-1.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 8.0F, -1.0F, 0.59184116F, 0.0F, 0.0F));
        PartDefinition legR0 = bodyBase.addOrReplaceChild("Leg_r_Seg0", CubeListBuilder.create().texOffs(32, 36)
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(-1.9F, 0.0F, 0.1F, -0.273144F, -0.5009095F, 0.091106184F));
        legR0.addOrReplaceChild("Leg_r_Seg1", CubeListBuilder.create().texOffs(40, 36)
                .addBox(-1.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 8.0F, -1.0F, 0.59184116F, 0.0F, 0.0F));

        PartDefinition chest = waist.addOrReplaceChild("Body_chest", CubeListBuilder.create().texOffs(0, 35)
                .addBox(-5.0F, -4.0F, -2.5F, 10.0F, 5.0F, 5.0F), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.5009095F, 0.0F, -0.091106184F));
        chest.addOrReplaceChild("Shroom_base", CubeListBuilder.create().texOffs(0, 0)
                .addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(0.0F, -1.0F, 2.2F, -1.1383038F, 0.0F, 0.0F));

        PartDefinition armL0 = chest.addOrReplaceChild("Arm_l_Seg0", CubeListBuilder.create().texOffs(48, 22)
                .addBox(0.0F, -1.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(5.0F, -2.0F, 0.0F, -0.7740535F, 0.0F, -0.546288F));
        armL0.addOrReplaceChild("Arm_l_Seg1", CubeListBuilder.create().texOffs(56, 20)
                .addBox(-1.0F, 0.0F, -2.0F, 2.0F, 10.0F, 2.0F), PartPose.offsetAndRotation(1.0F, 7.0F, 1.0F, -0.7740535F, 0.0F, 0.0F));
        armL0.addOrReplaceChild("Arm_l_bandage", CubeListBuilder.create().texOffs(39, 10)
                .addBox(-1.0F, 3.0F, 0.0F, 2.0F, 6.0F, 4.0F), PartPose.offsetAndRotation(1.0F, 7.0F, 1.0F, -0.7740535F, 0.0F, 0.0F));
        PartDefinition armR0 = chest.addOrReplaceChild("Arm_r_Seg0", CubeListBuilder.create().texOffs(48, 36)
                .addBox(-2.0F, -1.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(-5.0F, -2.0F, 0.0F, -0.7740535F, 0.0F, 0.546288F));
        armR0.addOrReplaceChild("Arm_r_Seg1", CubeListBuilder.create().texOffs(56, 34)
                .addBox(-1.0F, 0.0F, -2.0F, 2.0F, 10.0F, 2.0F), PartPose.offsetAndRotation(-1.0F, 7.0F, 1.0F, -0.7740535F, 0.0F, 0.0F));
        armR0.addOrReplaceChild("Arm_r_bandage", CubeListBuilder.create().texOffs(52, 10)
                .addBox(-3.0F, 2.0F, 0.0F, 2.0F, 6.0F, 4.0F), PartPose.offsetAndRotation(1.0F, 7.0F, 1.0F, -0.7740535F, 0.0F, 0.0F));

        PartDefinition neck0 = chest.addOrReplaceChild("Neck0", CubeListBuilder.create().texOffs(0, 26)
                .addBox(-2.0F, -5.0F, -2.0F, 4.0F, 5.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.36425024F, 0.0F, 0.0F));
        PartDefinition neck1 = neck0.addOrReplaceChild("Neck1", CubeListBuilder.create().texOffs(0, 0)
                .addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), PartPose.offsetAndRotation(0.0F, -4.0F, 0.3F, -1.2747885F, -0.22759093F, -0.18203785F));
        PartDefinition head = neck1.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -3.0F, -8.0F, 8.0F, 6.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("Head_teeth", CubeListBuilder.create().texOffs(0, 15)
                .addBox(-3.5F, 3.0F, -7.5F, 7.0F, 1.0F, 4.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("Hat", CubeListBuilder.create().texOffs(32, 48)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 5.0F, -4.0F));
        PartDefinition jaw0 = head.addOrReplaceChild("Jaw0", CubeListBuilder.create().texOffs(24, 0)
                .addBox(-3.5F, -3.5F, -3.0F, 7.0F, 5.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.7285005F, 0.0F, 0.0F));
        PartDefinition jaw1 = jaw0.addOrReplaceChild("Jaw1", CubeListBuilder.create().texOffs(40, 2)
                .addBox(-3.0F, 0.0F, -7.8F, 6.0F, 2.0F, 6.0F), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.091106184F, 0.0F, -0.045553092F));
        jaw1.addOrReplaceChild("Jaw1_teeth", CubeListBuilder.create().texOffs(0, 21)
                .addBox(-2.5F, 0.0F, -7.5F, 5.0F, 1.0F, 4.0F), PartPose.offset(0.0F, -1.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
        this.Body_base.render(pose, buffer, light, overlay, r, g, b, a);
    }

    /// Предмет всегда в левой руке модели (как в оригинале).
    @Override
    public void translateToHand(HumanoidArm side, PoseStack pose) {
        this.Body_base.translateAndRotate(pose);
        this.Body_waist.translateAndRotate(pose);
        this.Body_chest.translateAndRotate(pose);
        this.Arm_l_Seg0.translateAndRotate(pose);
        this.Arm_l_Seg1.translateAndRotate(pose);
    }

    /// Поза для брони (слой `LayerUnburiedArmor`).
    public void copyPropertiesTo(HumanoidModel<T> model) {
        super.copyPropertiesTo(model);
        model.head.copyFrom(this.Hat);
        model.hat.copyFrom(this.Hat);
        model.body.copyFrom(this.Body_chest);
        model.rightLeg.copyFrom(this.Leg_r_Seg0);
        model.leftLeg.copyFrom(this.Leg_l_Seg0);
    }

    @Override
    public ModelPart getHead() {
        return this.Head;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.Head_Looking(this.Head, 0.0F, 0.0F, netHeadYaw, headPitch);
        this.SwingX_Sin(this.Jaw0, 0.7285004F, ageInTicks, -0.04F, 0.06F, false, 0.0F);
        this.SwingX_Sin(this.Jaw1, 0.091106184F, ageInTicks, -0.03F, 0.06F, false, 0.0F);
        this.Head.y = -0.55F * Mth.sin(0.06F * ageInTicks + (float) (Math.PI / 5));
        this.Body_chest.y = -8.0F - 0.55F * Mth.sin(0.06F * ageInTicks + 0.9424779F);
        this.SwingX_Sin(this.Arm_r_Seg1, -0.7740535F, ageInTicks, -0.03F, 0.06F, true, 0.0F);
        this.SwingX_Sin(this.Arm_l_Seg1, -0.7740535F, ageInTicks, -0.03F, 0.06F, false, 0.0F);
        this.Body_base.z = 2.0F - 0.5F * Mth.sin(0.6F * limbSwing);
        if (this.riding) {
            this.Body_base.y = 15.0F;
            this.Body_base.zRot = 0.0F;
            this.setRotateAngle(this.Leg_l_Seg0, -1.6845918F, -0.3642502F, -0.091106184F);
            this.setRotateAngle(this.Leg_l_Seg1, 1.0927507F, 0.0F, 0.0F);
            this.setRotateAngle(this.Leg_r_Seg0, -1.7301449F, 0.18203785F, 0.091106184F);
            this.setRotateAngle(this.Leg_r_Seg1, 0.91053826F, 0.0F, 0.0F);
        } else {
            this.Body_base.y = 10.0F - 0.5F * Mth.sin(0.6F * limbSwing);
            this.SwingZ_Sin(this.Body_base, 0.0F, limbSwing, limbSwingAmount * 0.1F, 0.6F, false, 0.0F);
            this.SwingX_Sin(this.Leg_r_Seg0, -0.27314404F, limbSwing, limbSwingAmount * 0.7F, 0.6F, true, 0.0F);
            this.SwingX_Sin(this.Leg_l_Seg0, -0.27314404F, limbSwing, limbSwingAmount * 0.7F, 0.6F, false, 0.0F);
            this.SwingX_Sin(this.Leg_r_Seg1, 0.59184116F, limbSwing, limbSwingAmount * 0.4F, 0.6F, false, 0.9424779F);
            this.SwingX_Sin(this.Leg_l_Seg1, 0.59184116F, limbSwing, limbSwingAmount * 0.4F, 0.6F, true, 0.9424779F);
            this.Leg_l_Seg0.yRot = 0.0F;
            this.Leg_r_Seg0.yRot = 0.0F;
        }

        float i = entity instanceof IAggressive aggressive ? aggressive.getAttackTimer() / 5.0F : 0.0F;
        boolean isAggressive = entity instanceof Mob mob && mob.isAggressive();
        ItemStack main = entity.getMainHandItem();
        ItemStack off = entity.getOffhandItem();
        if (i > 0.0F) {
            if (!main.isEmpty()) this.Arm_l_Seg0.xRot = this.GradientAnimation((float) -Math.PI, -1.821251F, i);
            if (!off.isEmpty()) this.Arm_r_Seg0.xRot = this.GradientAnimation((float) -Math.PI, -1.9577358F, i);
        } else if (isAggressive) {
            this.setRotateAngle(this.Arm_r_Seg0, -1.9577358F, 0.0F, 0.091106184F);
            this.setRotateAngle(this.Arm_l_Seg0, -1.821251F, 0.0F, 0.045553092F);
        } else {
            this.setRotateAngle(this.Arm_r_Seg0, -0.7740535F, 0.0F, 0.5462881F);
            this.setRotateAngle(this.Arm_l_Seg0, -0.7740535F, 0.0F, -0.5462881F);
            this.Arm_r_Seg0.y = -2.0F - 0.55F * Mth.sin(0.06F * ageInTicks + (float) (Math.PI / 2));
            this.Arm_l_Seg0.y = -2.0F - 0.55F * Mth.sin(0.06F * ageInTicks + (float) (Math.PI / 2));
        }
    }
}
