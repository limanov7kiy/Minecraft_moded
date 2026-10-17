package dev.mcmoded.undeadrising.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mcmoded.undeadrising.entity.ForsakenEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

/// Лохмотья/доспех Forsaken поверх скелета — по обличью (`Variant`).
/// Оригинал: Fish's Undead Rising (Fish), `client/layer/LayerForsakenArmor`.
public class LayerForsakenArmor<T extends ForsakenEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation[] ARMOR_TEXTURES = {
            new ResourceLocation("mod_lavacow:textures/mobs/forsaken/forsaken_overlay2.png"),
            new ResourceLocation("mod_lavacow:textures/mobs/forsaken/forsaken_overlay2.png"),
            new ResourceLocation("mod_lavacow:textures/mobs/forsaken/forsaken_overlay.png"),
            new ResourceLocation("mod_lavacow:textures/mobs/forsaken/forsaken_overlay3.png")
    };
    private final SkeletonModel<T> layerModel;

    /// @param layerModel скелет с раздутием 0.25 (как `SkeletonModel(0.25F, true)` оригинала — слой одежды зимогора)
    public LayerForsakenArmor(RenderLayerParent<T, M> parent, SkeletonModel<T> layerModel) {
        super(parent);
        this.layerModel = layerModel;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        int skin = Math.floorMod(entity.getSkin(), ARMOR_TEXTURES.length);
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, ARMOR_TEXTURES[skin], pose, buffers, light, entity,
                limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, 1.0F, 1.0F, 1.0F);
    }
}
