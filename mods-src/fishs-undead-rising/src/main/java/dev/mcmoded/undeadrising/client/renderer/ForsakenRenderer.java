package dev.mcmoded.undeadrising.client.renderer;

import dev.mcmoded.undeadrising.client.ClientSetup;
import dev.mcmoded.undeadrising.client.layer.LayerForsakenArmor;
import dev.mcmoded.undeadrising.client.model.ForsakenModel;
import dev.mcmoded.undeadrising.entity.ForsakenEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

/// Рендер Forsaken: текстура ванильного скелета + слой лохмотьев по обличью + обычная броня.
/// Оригинал: Fish's Undead Rising (Fish), `client/renderer/entity/ForsakenRenderer`.
public class ForsakenRenderer extends HumanoidMobRenderer<ForsakenEntity, ForsakenModel<ForsakenEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/skeleton/skeleton.png");

    public ForsakenRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ForsakenModel<>(ctx.bakeLayer(ClientSetup.FORSAKEN_LAYER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.SKELETON_INNER_ARMOR)),
                new HumanoidModel<>(ctx.bakeLayer(ModelLayers.SKELETON_OUTER_ARMOR)), ctx.getModelManager()));
        this.addLayer(new LayerForsakenArmor<>(this, new SkeletonModel<>(ctx.bakeLayer(ModelLayers.STRAY_OUTER_LAYER))));
    }

    @Override
    public ResourceLocation getTextureLocation(ForsakenEntity entity) {
        return TEXTURE;
    }
}
