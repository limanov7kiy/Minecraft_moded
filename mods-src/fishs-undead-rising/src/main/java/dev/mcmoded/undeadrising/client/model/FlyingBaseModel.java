package dev.mcmoded.undeadrising.client.model;

import dev.mcmoded.undeadrising.entity.flying.FlyingMobEntity;
import net.minecraft.world.entity.Entity;

/// Базовая модель летунов: состояние анимации (летит / зависает / атакует / сидит на земле) по таймерам сущности.
/// Таймер зависания ведётся здесь, на клиенте, как в оригинале.
/// Оригинал: Fish's Undead Rising (Fish), `client/model/entity/FlyingBaseModel`.
public abstract class FlyingBaseModel<T extends Entity> extends FURBaseModel<T> {
    protected State state = State.FLYING;

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity instanceof FlyingMobEntity flyer) {
            if (flyer.getLandTimer() > 10) {
                this.state = State.WAITING;
            } else if (flyer.getAttackTimer() > 0) {
                this.state = State.ATTACKING;
            } else if (flyer.getHoverTimer() < 20) {
                this.state = State.HOVERING;
            } else {
                this.state = State.FLYING;
            }

            if (flyer.onGround()) {
                flyer.setHoverTimer(30);
            } else if (!(flyer.getDeltaMovement().x <= 0.05) || !(flyer.getDeltaMovement().z <= 0.05)) {
                if (flyer.getHoverTimer() < 60) flyer.setHoverTimer(flyer.getHoverTimer() + 1);
            } else if (flyer.getHoverTimer() > 0) {
                flyer.setHoverTimer(flyer.getHoverTimer() - 1);
            }
        }
    }

    protected enum State {
        FLYING,
        HOVERING,
        ATTACKING,
        WAITING
    }
}
