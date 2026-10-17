package dev.mcmoded.undeadrising.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.undeadrising.entity.ParasiteEntity;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/// Модель паразита: шесть сегментов-колец и жвалы; на носителе изгибается. Текстура 64×32.
/// Оригинал: Fish's Undead Rising (Fish), `client/model/entity/ParasiteModel`.
public class ParasiteModel<T extends ParasiteEntity> extends FURBaseModel<T> implements HeadedModel {
    public final ModelPart Parasite_Seg_4;
    public final ModelPart Parasite_Seg_0;
    public final ModelPart Mandible0;
    public final ModelPart Mandible2;
    public final ModelPart Mandible2_2;
    public final ModelPart Parasite_Seg_3;
    public final ModelPart Mandible0_2;
    public final ModelPart Mandible1;
    public final ModelPart Parasite_Seg_5;
    public final ModelPart Parasite_Seg_1;
    public final ModelPart Parasite_Seg_2;
    public final ModelPart Mandible1_2;
    
    public final ModelPart[] Parasite_Seg;
    private State state = State.NORMAL;

    public ParasiteModel(ModelPart root) {
        this.Parasite_Seg_4 = root.getChild("Parasite_Seg_4");
        this.Parasite_Seg_0 = root.getChild("Parasite_Seg_0");
        this.Mandible0 = this.Parasite_Seg_0.getChild("Mandible0");
        this.Mandible2 = this.Parasite_Seg_0.getChild("Mandible2");
        this.Mandible2_2 = this.Mandible2.getChild("Mandible2_2");
        this.Parasite_Seg_3 = root.getChild("Parasite_Seg_3");
        this.Mandible0_2 = this.Mandible0.getChild("Mandible0_2");
        this.Mandible1 = this.Parasite_Seg_0.getChild("Mandible1");
        this.Parasite_Seg_5 = root.getChild("Parasite_Seg_5");
        this.Parasite_Seg_1 = root.getChild("Parasite_Seg_1");
        this.Parasite_Seg_2 = root.getChild("Parasite_Seg_2");
        this.Mandible1_2 = this.Mandible1.getChild("Mandible1_2");
    
        this.Parasite_Seg = new ModelPart[]{this.Parasite_Seg_0, this.Parasite_Seg_1, this.Parasite_Seg_2, this.Parasite_Seg_3, this.Parasite_Seg_4, this.Parasite_Seg_5};
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("Parasite_Seg_4", CubeListBuilder.create().texOffs(0, 24).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F), PartPose.offset(0.0F, 22.0F, 5.5F));
        PartDefinition Parasite_Seg_0 = root.addOrReplaceChild("Parasite_Seg_0", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 2.0F, 2.0F), PartPose.offset(0.0F, 22.0F, -7.0F));
        PartDefinition Mandible0 = Parasite_Seg_0.addOrReplaceChild("Mandible0", CubeListBuilder.create().texOffs(20, 0).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.5F, -0.5F, -0.7285004F, 0.0F, 0.0F));
        PartDefinition Mandible2 = Parasite_Seg_0.addOrReplaceChild("Mandible2", CubeListBuilder.create().texOffs(20, 0).mirror(true).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(-1.0F, 1.0F, -1.0F, 0.0F, 0.13665928F, 0.0F));
        Mandible2.addOrReplaceChild("Mandible2_2", CubeListBuilder.create().texOffs(20, 2).mirror(true).addBox(-0.5F, -0.5F, -0.8F, 1.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.5F, -1.0F, 0.0F, -0.5462881F, 0.0F));
        root.addOrReplaceChild("Parasite_Seg_3", CubeListBuilder.create().texOffs(0, 18).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 3.0F, 3.0F), PartPose.offset(0.0F, 21.0F, 2.5F));
        Mandible0.addOrReplaceChild("Mandible0_2", CubeListBuilder.create().texOffs(20, 2).addBox(-0.5F, -0.5F, -0.8F, 1.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.59184116F, 0.0F, 0.0F));
        PartDefinition Mandible1 = Parasite_Seg_0.addOrReplaceChild("Mandible1", CubeListBuilder.create().texOffs(20, 0).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(1.0F, 1.0F, -1.0F, 0.0F, -0.13665928F, 0.0F));
        root.addOrReplaceChild("Parasite_Seg_5", CubeListBuilder.create().texOffs(11, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F), PartPose.offset(0.0F, 23.0F, 8.0F));
        root.addOrReplaceChild("Parasite_Seg_1", CubeListBuilder.create().texOffs(0, 4).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 3.0F, 2.0F), PartPose.offset(0.0F, 21.0F, -5.0F));
        root.addOrReplaceChild("Parasite_Seg_2", CubeListBuilder.create().texOffs(0, 9).addBox(-3.0F, 0.0F, -2.5F, 6.0F, 4.0F, 5.0F), PartPose.offset(0.0F, 20.0F, -1.5F));
        Mandible1.addOrReplaceChild("Mandible1_2", CubeListBuilder.create().texOffs(20, 2).addBox(-0.5F, -0.5F, -0.8F, 1.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.5F, -1.0F, 0.0F, 0.5462881F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        for (ModelPart seg : this.Parasite_Seg) seg.render(pose, buffer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        for (int i = 1; i < this.Parasite_Seg.length; i++) {
            if (this.state == State.RIDING) {
                this.Parasite_Seg[i].y = 20.0F
                    + Math.abs((float)i - 2.0F)
                    + Mth.sin(ageInTicks * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.2F * (float)Math.abs(i - 2);
                this.Parasite_Seg[i].yRot = 0.0F;
            } else {
                this.Parasite_Seg[i].y = 20.0F + Math.abs((float)i - 2.0F);
                this.Parasite_Seg[i].yRot = Mth.cos(ageInTicks * 0.9F + (float)i * 0.15F * (float) Math.PI)
                    * (float) Math.PI
                    * 0.05F
                    * (float)(1 + Math.abs(i - 2));
                this.Parasite_Seg[i].x = Mth.sin(ageInTicks * 0.9F + (float)i * 0.15F * (float) Math.PI)
                    * (float) Math.PI
                    * 0.2F
                    * (float)Math.abs(i - 2);
            }
        }

        if (this.state == State.RIDING) {
            this.Parasite_Seg[0].xRot = 1.0F;
        } else {
            this.Parasite_Seg[0].xRot = 0.0F;
        }

        this.Mandible0.xRot = -0.7285004F + 0.35F * Mth.sin(ageInTicks * 0.9F);
        this.Mandible1.yRot = 0.26F * Mth.sin(ageInTicks * 0.9F);
        this.Mandible2.yRot = -0.26F * Mth.sin(ageInTicks * 0.9F);
        if (entityIn.getVehicle() != null) {
            this.state = State.RIDING;
        } else {
            this.state = State.NORMAL;
        }
    }

    @Override
    public ModelPart getHead() {
        return this.Parasite_Seg[0];
    }

    private enum State {
        RIDING,
        NORMAL
    }
}
