package dev.mcmoded.wizardryrestored.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.wizardryrestored.entity.StatueEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

/// Статуя рисуется моделью самого существа (созданного из NBT, неподвижного), но все её слои перекрашены
/// текстурой камня или льда — подменой RenderType у буфера.
public class StatueRenderer extends EntityRenderer<StatueEntity> {
    private static final ResourceLocation STONE = new ResourceLocation("minecraft", "textures/block/stone.png");
    private static final ResourceLocation ICE = new ResourceLocation("minecraft", "textures/block/ice.png");

    public StatueRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    private record Retextured(MultiBufferSource base, RenderType type) implements MultiBufferSource {
        @Override
        public VertexConsumer getBuffer(RenderType ignored) {
            return base.getBuffer(type);
        }
    }

    @Override
    public void render(StatueEntity statue, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        Entity model = statue.clientModel;
        if (model == null && !statue.creature().isEmpty()) {
            model = EntityType.create(statue.creature(), statue.level()).orElse(null);
            if (model instanceof LivingEntity living) {
                living.yBodyRot = living.yBodyRotO = statue.getYRot();
                living.yHeadRot = living.yHeadRotO = statue.getYRot();
                living.hurtTime = 0;
                living.deathTime = 0;
            }
            if (model != null) model.setYRot(statue.getYRot());
            statue.clientModel = model;
        }
        if (model != null) {
            model.setPos(statue.getX(), statue.getY(), statue.getZ());
            RenderType type = statue.isIce() ? RenderType.entityTranslucent(ICE) : RenderType.entityCutoutNoCull(STONE);
            Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(model)
                    .render(model, statue.getYRot(), 0, pose, new Retextured(buffers, type), light);
        }
        super.render(statue, yaw, partialTick, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(StatueEntity statue) {
        return STONE;
    }
}
