package dev.mcmoded.undeadrising.client.model;

import dev.mcmoded.undeadrising.entity.ForsakenEntity;
import dev.mcmoded.undeadrising.registry.FurItems;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Items;

/// Модель Forsaken — ванильный скелет; в бою посох держится «как арбалет», щит поднят.
/// Оригинал: Fish's Undead Rising (Fish), `client/model/entity/ForsakenModel`.
public class ForsakenModel<T extends ForsakenEntity> extends SkeletonModel<T> {
    public ForsakenModel(ModelPart root) {
        super(root);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        this.rightArmPose = ArmPose.EMPTY;
        this.leftArmPose = ArmPose.EMPTY;
        if (entity.getMainHandItem().is(FurItems.FORSAKEN_STAFF.get()) && entity.isAggressive()) {
            this.rightArmPose = ArmPose.CROSSBOW_HOLD;
        } else if (entity.getOffhandItem().is(Items.SHIELD) && entity.isAggressive()) {
            if (entity.getMainArm() == HumanoidArm.RIGHT) this.leftArmPose = ArmPose.BLOCK;
            else this.rightArmPose = ArmPose.BLOCK;
        } else {
            super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        }
    }
}
