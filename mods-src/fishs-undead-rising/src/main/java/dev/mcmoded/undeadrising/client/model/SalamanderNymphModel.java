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

/// Модель нимфы саламандры (жабры, хвост-плавник), текстура 64×32.
/// Оригинал: Fish's Undead Rising (Fish), `client/model/entity/SalamanderNymphModel`.
public class SalamanderNymphModel<T extends SalamanderEntity> extends FURBaseModel<T> implements HeadedModel {
    public final ModelPart Body_Base;
    public final ModelPart Head;
    public final ModelPart Head_Gill_r2;
    public final ModelPart leg1_r0;
    public final ModelPart Head_Gill_l2;
    public final ModelPart Head_Gill_l1;
    public final ModelPart Tail0;
    public final ModelPart Tail1;
    public final ModelPart Tail2;
    public final ModelPart Head_Gill_r1;
    public final ModelPart Jaw;
    public final ModelPart leg0_r0;
    public final ModelPart leg0_r1;
    public final ModelPart leg1_l0;
    public final ModelPart Head_Gill_l0;
    public final ModelPart Head_Gill_r0;
    public final ModelPart leg0_l0;
    public final ModelPart leg0_l1;
    public final ModelPart leg1_l1;
    public final ModelPart leg1_r1;
    

    public SalamanderNymphModel(ModelPart root) {
        this.Body_Base = root.getChild("Body_Base");
        this.Head = this.Body_Base.getChild("Head");
        this.Head_Gill_r2 = this.Head.getChild("Head_Gill_r2");
        this.leg1_r0 = this.Body_Base.getChild("leg1_r0");
        this.Head_Gill_l2 = this.Head.getChild("Head_Gill_l2");
        this.Head_Gill_l1 = this.Head.getChild("Head_Gill_l1");
        this.Tail0 = this.Body_Base.getChild("Tail0");
        this.Tail1 = this.Tail0.getChild("Tail1");
        this.Tail2 = this.Tail1.getChild("Tail2");
        this.Head_Gill_r1 = this.Head.getChild("Head_Gill_r1");
        this.Jaw = this.Head.getChild("Jaw");
        this.leg0_r0 = this.Body_Base.getChild("leg0_r0");
        this.leg0_r1 = this.leg0_r0.getChild("leg0_r1");
        this.leg1_l0 = this.Body_Base.getChild("leg1_l0");
        this.Head_Gill_l0 = this.Head.getChild("Head_Gill_l0");
        this.Head_Gill_r0 = this.Head.getChild("Head_Gill_r0");
        this.leg0_l0 = this.Body_Base.getChild("leg0_l0");
        this.leg0_l1 = this.leg0_l0.getChild("leg0_l1");
        this.leg1_l1 = this.leg1_l0.getChild("leg1_l1");
        this.leg1_r1 = this.leg1_r0.getChild("leg1_r1");
    
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition Body_Base = root.addOrReplaceChild("Body_Base", CubeListBuilder.create().texOffs(0, 17).addBox(-3.0F, -1.5F, -12.0F, 6.0F, 3.0F, 12.0F), PartPose.offsetAndRotation(0.0F, 21.0F, 5.0F, -0.0710349F, 0.0F, 0.0F));
        PartDefinition Head = Body_Base.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -1.5F, -7.0F, 5.0F, 2.0F, 7.0F), PartPose.offsetAndRotation(0.0F, 0.0F, -12.0F, -0.19547687F, 0.0F, 0.0F));
        Head.addOrReplaceChild("Head_Gill_r2", CubeListBuilder.create().texOffs(20, 0).addBox(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 0.0F), PartPose.offsetAndRotation(-2.0F, -0.5F, -1.0F, 0.18203785F, 0.59184116F, -0.5009095F));
        PartDefinition leg1_r0 = Body_Base.addOrReplaceChild("leg1_r0", CubeListBuilder.create().texOffs(42, 0).addBox(-1.0F, -0.5F, -1.0F, 1.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(-2.5F, 0.0F, -2.0F, 0.4098033F, 0.0F, 0.4098033F));
        Head.addOrReplaceChild("Head_Gill_l2", CubeListBuilder.create().texOffs(20, 0).mirror(true).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 0.0F), PartPose.offsetAndRotation(2.0F, -0.5F, -1.0F, 0.18203785F, -0.59184116F, 0.5009095F));
        Head.addOrReplaceChild("Head_Gill_l1", CubeListBuilder.create().texOffs(20, 0).mirror(true).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 0.0F), PartPose.offsetAndRotation(2.0F, -0.6F, -1.0F, 0.0F, -0.8196066F, 0.0F));
        PartDefinition Tail0 = Body_Base.addOrReplaceChild("Tail0", CubeListBuilder.create().texOffs(25, 0).addBox(-2.0F, -1.5F, 0.0F, 4.0F, 3.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.03543018F, 0.0F, 0.0F));
        PartDefinition Tail1 = Tail0.addOrReplaceChild("Tail1", CubeListBuilder.create().texOffs(24, 7).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 3.5F, 0.27209684F, 0.0F, 0.0F));
        Tail1.addOrReplaceChild("Tail2", CubeListBuilder.create().texOffs(24, 7).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 5.5F, 0.33126348F, 0.0F, 0.0F));
        Head.addOrReplaceChild("Head_Gill_r1", CubeListBuilder.create().texOffs(20, 0).addBox(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 0.0F), PartPose.offsetAndRotation(-2.0F, -0.6F, -1.0F, 0.0F, 0.8196066F, 0.0F));
        Head.addOrReplaceChild("Jaw", CubeListBuilder.create().texOffs(0, 9).addBox(-2.5F, -0.5F, -7.0F, 5.0F, 1.0F, 7.0F), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.43004912F, 0.0F, 0.0F));
        PartDefinition leg0_r0 = Body_Base.addOrReplaceChild("leg0_r0", CubeListBuilder.create().texOffs(42, 0).addBox(-1.0F, -0.5F, -1.0F, 1.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(-2.5F, 0.0F, -10.0F, 0.0F, 0.0F, 0.4098033F));
        leg0_r0.addOrReplaceChild("leg0_r1", CubeListBuilder.create().texOffs(43, 5).addBox(-1.0F, -0.5F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.0F, 0.0F, -0.45535642F));
        PartDefinition leg1_l0 = Body_Base.addOrReplaceChild("leg1_l0", CubeListBuilder.create().texOffs(42, 0).mirror(true).addBox(0.0F, -0.5F, -1.0F, 1.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(2.5F, 0.0F, -2.0F, 0.4098033F, 0.0F, -0.4098033F));
        Head.addOrReplaceChild("Head_Gill_l0", CubeListBuilder.create().texOffs(20, 0).mirror(true).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 0.0F), PartPose.offsetAndRotation(1.9F, -0.8F, -1.5F, -0.13665928F, -0.8651597F, -0.5009095F));
        Head.addOrReplaceChild("Head_Gill_r0", CubeListBuilder.create().texOffs(20, 0).addBox(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 0.0F), PartPose.offsetAndRotation(-1.9F, -0.8F, -1.5F, -0.13665928F, 0.8651597F, 0.5009095F));
        PartDefinition leg0_l0 = Body_Base.addOrReplaceChild("leg0_l0", CubeListBuilder.create().texOffs(42, 0).mirror(true).addBox(0.0F, -0.5F, -1.0F, 1.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(2.5F, 0.0F, -10.0F, 0.0F, 0.0F, -0.4098033F));
        leg0_l0.addOrReplaceChild("leg0_l1", CubeListBuilder.create().texOffs(43, 5).mirror(true).addBox(0.0F, -0.5F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.0F, 0.0F, 0.45535642F));
        leg1_l0.addOrReplaceChild("leg1_l1", CubeListBuilder.create().texOffs(43, 5).mirror(true).addBox(0.0F, -0.5F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.0F, 0.0F, 0.45535642F));
        leg1_r0.addOrReplaceChild("leg1_r1", CubeListBuilder.create().texOffs(43, 5).addBox(-1.0F, -0.5F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.0F, 0.0F, -0.45535642F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.Body_Base.render(pose, buffer, light, overlay, red, green, blue, alpha);
    }
    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entityIn.isInSittingPose()) {
            this.Body_Base.y = 23.4F;
            this.Body_Base.xRot = -0.0710349F;
            if (entityIn.isBoostingFurnace()) {
                this.Head.xRot = -0.4691445F + 0.08F * Mth.sin(0.3F * ageInTicks);
                this.Head.yRot = 0.0F;
                this.Head.zRot = 0.0F;
                if (this.Jaw.xRot < 0.66F) {
                    this.Jaw.xRot += 0.01F;
                } else {
                    this.Jaw.xRot = 0.7F + -0.04F * Mth.sin(0.3F * ageInTicks);
                }
            } else {
                this.Head.xRot = -0.6646214F + headPitch * (float) (Math.PI / 180.0);
                this.Head.yRot = netHeadYaw * (float) (Math.PI / 180.0);
                this.Jaw.xRot = 0.08F - 0.08F * Mth.sin(0.03F * ageInTicks);
            }

            this.Head_Gill_l0.yRot = -0.8651597F + 0.135F * Mth.sin(0.18F * ageInTicks + 0.0F);
            this.Head_Gill_l1.yRot = -0.8196066F + 0.135F * Mth.sin(0.18F * ageInTicks + (float) (Math.PI / 5));
            this.Head_Gill_l2.yRot = -0.59184116F + 0.135F * Mth.sin(0.18F * ageInTicks + (float) (Math.PI * 2.0 / 5.0));
            this.Head_Gill_r0.yRot = 0.8651597F - 0.135F * Mth.sin(0.18F * ageInTicks + 0.0F);
            this.Head_Gill_r1.yRot = 0.8196066F - 0.135F * Mth.sin(0.18F * ageInTicks + (float) (Math.PI / 5));
            this.Head_Gill_r2.yRot = 0.59184116F - 0.135F * Mth.sin(0.18F * ageInTicks + (float) (Math.PI * 2.0 / 5.0));
            this.Tail0.xRot = 0.08F + 0.08F * Mth.sin(0.06F * ageInTicks);
            this.Tail1.xRot = 0.135F + 0.135F * Mth.sin(0.12F * ageInTicks + (float) (Math.PI / 5));
            this.Tail2.xRot = 0.165F + 0.165F * Mth.sin(0.12F * ageInTicks + (float) (Math.PI * 2.0 / 5.0));
            this.leg0_r0.xRot = -1.1728612F;
            this.leg0_l0.xRot = -1.1728612F;
            this.leg1_r0.xRot = 1.5044738F;
            this.leg1_l0.xRot = 1.5044738F;
            this.leg0_r0.zRot = 0.4098033F;
            this.leg0_l0.zRot = -0.4098033F;
            this.leg1_r0.zRot = 0.4098033F;
            this.leg1_l0.zRot = -0.4098033F;
        } else if (entityIn.isAggressive()) {
            this.Body_Base.y = 21.5F;
            this.Body_Base.xRot = -0.68294734F;
            this.Head.xRot = 0.5462881F + headPitch * (float) (Math.PI / 180.0);
            this.Head.yRot = 0.0F;
            this.Jaw.xRot = 0.5F;
            this.Head_Gill_l0.yRot = 0.0F;
            this.Head_Gill_l1.yRot = 0.0F;
            this.Head_Gill_l2.yRot = 0.0F;
            this.Head_Gill_r0.yRot = 0.0F;
            this.Head_Gill_r1.yRot = 0.0F;
            this.Head_Gill_r2.yRot = 0.0F;
            this.Tail0.xRot = 0.5462881F;
            this.Tail1.xRot = 0.27209684F;
            this.Tail2.xRot = 0.33126348F;
            this.leg0_r0.xRot = Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
            this.leg0_l0.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * limbSwingAmount;
            this.leg1_r0.xRot = 0.4098033F + Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * limbSwingAmount;
            this.leg1_l0.xRot = 0.4098033F + Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
            this.leg0_r0.zRot = 0.4098033F + Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
            this.leg0_l0.zRot = -0.4098033F + Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * limbSwingAmount;
            this.leg1_r0.zRot = 0.4098033F + Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * limbSwingAmount;
            this.leg1_l0.zRot = -0.4098033F + Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
        } else {
            this.Body_Base.y = 21.0F;
            this.Body_Base.xRot = -0.0710349F;
            this.Head.xRot = headPitch * (float) (Math.PI / 180.0);
            this.Head.yRot = netHeadYaw * (float) (Math.PI / 180.0);
            this.Jaw.xRot = 0.08F - 0.08F * Mth.sin(0.03F * ageInTicks);
            this.Head_Gill_l0.yRot = -0.8651597F + 0.135F * Mth.sin(0.18F * ageInTicks + 0.0F);
            this.Head_Gill_l1.yRot = -0.8196066F + 0.135F * Mth.sin(0.18F * ageInTicks + (float) (Math.PI / 5));
            this.Head_Gill_l2.yRot = -0.59184116F + 0.135F * Mth.sin(0.18F * ageInTicks + (float) (Math.PI * 2.0 / 5.0));
            this.Head_Gill_r0.yRot = 0.8651597F - 0.135F * Mth.sin(0.18F * ageInTicks + 0.0F);
            this.Head_Gill_r1.yRot = 0.8196066F - 0.135F * Mth.sin(0.18F * ageInTicks + (float) (Math.PI / 5));
            this.Head_Gill_r2.yRot = 0.59184116F - 0.135F * Mth.sin(0.18F * ageInTicks + (float) (Math.PI * 2.0 / 5.0));
            this.Tail0.xRot = 0.08F + 0.08F * Mth.sin(0.06F * ageInTicks);
            this.Tail1.xRot = 0.135F + 0.135F * Mth.sin(0.12F * ageInTicks + (float) (Math.PI / 5));
            this.Tail2.xRot = 0.165F + 0.165F * Mth.sin(0.12F * ageInTicks + (float) (Math.PI * 2.0 / 5.0));
            this.leg0_r0.xRot = Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
            this.leg0_l0.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * limbSwingAmount;
            this.leg1_r0.xRot = 0.4098033F + Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * limbSwingAmount;
            this.leg1_l0.xRot = 0.4098033F + Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
            this.leg0_r0.zRot = 0.4098033F + Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
            this.leg0_l0.zRot = -0.4098033F + Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * limbSwingAmount;
            this.leg1_r0.zRot = 0.4098033F + Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * limbSwingAmount;
            this.leg1_l0.zRot = -0.4098033F + Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
        }
    }

    @Override
    public ModelPart getHead() {
        return this.Head;
    }
}
