package dev.mcmoded.undeadrising.client.renderer;

import dev.mcmoded.undeadrising.client.ClientSetup;
import dev.mcmoded.undeadrising.client.model.BansheeModel;
import dev.mcmoded.undeadrising.entity.floating.BansheeEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/// Рендер банши: полупрозрачная, без тени. Оригинал: Fish's Undead Rising (Fish), `client/renderer/entity/BansheeRenderer`.
public class BansheeRenderer extends MobRenderer<BansheeEntity, BansheeModel<BansheeEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("mod_lavacow", "textures/mobs/banshee.png");

    public BansheeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BansheeModel<>(ctx.bakeLayer(ClientSetup.BANSHEE_LAYER)), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(BansheeEntity entity) {
        return TEXTURE;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(BansheeEntity entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        return RenderType.entityTranslucent(this.getTextureLocation(entity));
    }
}
