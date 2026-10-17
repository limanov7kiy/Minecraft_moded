package dev.mcmoded.undeadrising.client.renderer;

import dev.mcmoded.undeadrising.client.ClientSetup;
import dev.mcmoded.undeadrising.client.model.VespaModel;
import dev.mcmoded.undeadrising.entity.flying.VespaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

/// Рендер веспы: два окраса (обычная и «из пчелы») и седло. Оригинал: Fish's Undead Rising (Fish), `client/renderer/entity/VespaRenderer`.
public class VespaRenderer extends MobRenderer<VespaEntity, VespaModel<VespaEntity>> {
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation("mod_lavacow", "textures/mobs/vespa/vespa.png"),
            new ResourceLocation("mod_lavacow", "textures/mobs/vespa/vespa1.png")};
    private static final ResourceLocation SADDLE = new ResourceLocation("mod_lavacow", "textures/mobs/vespa/vespa_saddle.png");

    public VespaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new VespaModel<>(ctx.bakeLayer(ClientSetup.VESPA_LAYER)), 0.5F);
        this.addLayer(new SaddleLayer<>(this, new VespaModel<>(ctx.bakeLayer(ClientSetup.VESPA_LAYER)), SADDLE));
    }

    @Override
    public ResourceLocation getTextureLocation(VespaEntity entity) {
        return TEXTURES[Math.floorMod(entity.getSkin(), TEXTURES.length)];
    }
}
