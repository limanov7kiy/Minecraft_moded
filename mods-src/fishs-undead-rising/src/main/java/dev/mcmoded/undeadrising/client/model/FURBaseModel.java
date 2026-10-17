package dev.mcmoded.undeadrising.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/// Общие помощники анимации моделей FUR (синусные покачивания, плавный переход). Методы — как в оригинале, чтобы
/// `setupAnim` переносился построчно.
/// Оригинал: Fish's Undead Rising (Fish), `client/model/entity/FURBaseModel`.
public abstract class FURBaseModel<T extends Entity> extends EntityModel<T> {
    protected void Head_Looking(ModelPart head, float initX, float initY, float netHeadYaw, float headPitch) {
        head.xRot = initX + (float) Math.toRadians(headPitch);
        head.yRot = initY + (float) Math.toRadians(netHeadYaw);
    }

    protected void SwingX_Sin(ModelPart part, float init, float ticks, float amplitude, float frequency, boolean flip, float delay) {
        part.xRot = init + (flip ? -1 : 1) * amplitude * Mth.sin(frequency * ticks + delay);
    }

    protected void SwingY_Sin(ModelPart part, float init, float ticks, float amplitude, float frequency, boolean flip, float delay) {
        part.yRot = init + (flip ? -1 : 1) * amplitude * Mth.sin(frequency * ticks + delay);
    }

    protected void SwingZ_Sin(ModelPart part, float init, float ticks, float amplitude, float frequency, boolean flip, float delay) {
        part.zRot = init + (flip ? -1 : 1) * amplitude * Mth.sin(frequency * ticks + delay);
    }

    protected float GradientAnimation(float start, float end, float tick) {
        float y = (float) (1.0 / Math.pow(1.2 * tick + 1.0, 4.5));
        return start * (1.0F - y) + end * y;
    }

    protected float GradientAnimation_s(float start, float end, float tick) {
        return start * Mth.sin((float) (Math.PI / 2) * tick) + end * (1.0F - Mth.sin((float) (Math.PI / 2) * tick));
    }

    protected void setRotateAngle(ModelPart part, float x, float y, float z) {
        part.xRot = x;
        part.yRot = y;
        part.zRot = z;
    }

    protected float triangleWave(float a, float b) {
        return (Math.abs(a % b - b * 0.5F) - b * 0.25F) / (b * 0.25F);
    }
}
