package dev.mcmoded.wizardryrestored.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.wizardryrestored.entity.GrappleHookEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/// Лоза — провисающая зелёная линия от руки заклинателя до крюка (как леска удочки).
public class GrappleRenderer extends EntityRenderer<GrappleHookEntity> {
    public GrappleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(GrappleHookEntity hook, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        Entity owner = hook.ownerClient();
        if (owner != null) {
            float bodyYaw = owner.getViewYRot(partialTick) * ((float) Math.PI / 180f);
            Vec3 hand = owner.getEyePosition(partialTick).add(new Vec3(-Math.cos(bodyYaw) * 0.35, -0.45, -Math.sin(bodyYaw) * 0.35));
            Vec3 self = hook.getPosition(partialTick);
            float dx = (float) (hand.x - self.x), dy = (float) (hand.y - self.y), dz = (float) (hand.z - self.z);
            VertexConsumer vc = buffers.getBuffer(RenderType.lineStrip());
            PoseStack.Pose last = pose.last();
            Matrix4f m = last.pose();
            Matrix3f n = last.normal();
            int segments = 16;
            for (int i = 0; i <= segments; i++) {
                float t = i / (float) segments;
                // Натянутая лоза — прямая, летящая — провисает
                float x = dx * t, y = dy * t - (hook.isAttached() ? 0 : 0.6f * t * (1 - t)), z = dz * t;
                float nx = dx, ny = dy, nz = dz;
                float len = Math.max(1.0e-4f, (float) Math.sqrt(nx * nx + ny * ny + nz * nz));
                vc.vertex(m, x, y, z).color(40, 140, 30, 255).normal(n, nx / len, ny / len, nz / len).endVertex();
            }
        }
        super.render(hook, yaw, partialTick, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(GrappleHookEntity hook) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
