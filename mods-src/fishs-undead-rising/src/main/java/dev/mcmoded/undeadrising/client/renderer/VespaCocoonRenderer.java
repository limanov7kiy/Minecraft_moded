package dev.mcmoded.undeadrising.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mcmoded.undeadrising.client.ClientSetup;
import dev.mcmoded.undeadrising.client.model.VespaCocoonModel;
import dev.mcmoded.undeadrising.entity.VespaCocoonEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

/// Рендер кокона: полупрозрачный, «дышит» по высоте. Оригинал: Fish's Undead Rising (Fish), `client/renderer/entity/VespaCocoonRenderer`.
public class VespaCocoonRenderer extends MobRenderer<VespaCocoonEntity, VespaCocoonModel<VespaCocoonEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("mod_lavacow", "textures/mobs/vespa/vespa_cocoon.png");

    public VespaCocoonRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new VespaCocoonModel<>(ctx.bakeLayer(ClientSetup.VESPA_COCOON_LAYER)), 0.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(VespaCocoonEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(VespaCocoonEntity entity, PoseStack pose, float partialTick) {
        float f = 1.2F + 0.2F * Mth.sin(entity.tickCount * 0.25F);
        if (!entity.isBaby()) pose.scale(1.5F, f, 1.5F);
    }

    @Nullable
    @Override
    protected RenderType getRenderType(VespaCocoonEntity entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        return RenderType.entityTranslucent(this.getTextureLocation(entity));
    }
}
