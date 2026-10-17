package dev.mcmoded.undeadrising.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mcmoded.undeadrising.client.ClientSetup;
import dev.mcmoded.undeadrising.client.model.WetaModel;
import dev.mcmoded.undeadrising.entity.WetaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/// Рендер веты: обычная, детёныш (своя текстура, вдвое меньше), больная.
/// Оригинал: Fish's Undead Rising (Fish), `client/renderer/entity/WetaRenderer`.
public class WetaRenderer extends MobRenderer<WetaEntity, WetaModel<WetaEntity>> {
    private static final ResourceLocation[] TEXTURES = {tex("weta"), tex("weta1"), tex("weta2")};

    public WetaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WetaModel<>(ctx.bakeLayer(ClientSetup.WETA_LAYER)), 0.0F);
    }

    private static ResourceLocation tex(String name) {
        return new ResourceLocation("mod_lavacow", "textures/mobs/weta/" + name + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(WetaEntity entity) {
        return TEXTURES[entity.isBaby() ? 1 : Math.floorMod(entity.getSkin(), TEXTURES.length)];
    }

    @Override
    protected void scale(WetaEntity entity, PoseStack pose, float partialTick) {
        if (entity.isBaby()) pose.scale(0.5F, 0.5F, 0.5F);
    }
}
