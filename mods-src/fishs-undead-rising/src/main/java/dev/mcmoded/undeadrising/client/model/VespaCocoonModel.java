package dev.mcmoded.undeadrising.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.undeadrising.entity.VespaCocoonEntity;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/// Модель кокона веспы (кокон и две плоские «нити»). Текстура 64×32.
/// Оригинал: Fish's Undead Rising (Fish), `client/model/entity/VespaCocoonModel`.
public class VespaCocoonModel<T extends VespaCocoonEntity> extends FURBaseModel<T> implements HeadedModel {
    public final ModelPart Cocoon_tangle1;
    public final ModelPart Cocoon_base;
    public final ModelPart Cocoon_top;
    public final ModelPart Cocoon_tangle0;
    

    public VespaCocoonModel(ModelPart root) {
        this.Cocoon_tangle1 = root.getChild("Cocoon_tangle1");
        this.Cocoon_base = root.getChild("Cocoon_base");
        this.Cocoon_top = this.Cocoon_base.getChild("Cocoon_top");
        this.Cocoon_tangle0 = root.getChild("Cocoon_tangle0");
    
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("Cocoon_tangle1", CubeListBuilder.create().texOffs(0, -4).addBox(0.0F, -4.0F, -12.0F, 0.0F, 4.0F, 24.0F), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, (float) (-Math.PI / 4), 0.0F));
        PartDefinition Cocoon_base = root.addOrReplaceChild("Cocoon_base", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        Cocoon_base.addOrReplaceChild("Cocoon_top", CubeListBuilder.create().texOffs(34, 0).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 2.0F, 6.0F), PartPose.offset(0.0F, -8.0F, 0.0F));
        root.addOrReplaceChild("Cocoon_tangle0", CubeListBuilder.create().texOffs(0, -4).addBox(0.0F, -4.0F, -12.0F, 0.0F, 4.0F, 24.0F), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, (float) (Math.PI / 4), 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.Cocoon_base.render(pose, buffer, light, overlay, red, green, blue, alpha);
        this.Cocoon_tangle0.render(pose, buffer, light, overlay, red, green, blue, alpha);
        this.Cocoon_tangle1.render(pose, buffer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public ModelPart getHead() {
        return this.Cocoon_top;
    }
}
