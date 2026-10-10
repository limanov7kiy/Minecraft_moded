/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/client/render/entity/EvolvedSkeletonBossRenderer.java. See CREDITS.md.
 */
package dev.mcmoded.gcrevival.client;

import dev.mcmoded.gcrevival.GCRevival;
import dev.mcmoded.gcrevival.entity.SkeletonBoss;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class SkeletonBossRenderer extends MobRenderer<SkeletonBoss, SkeletonBossModel> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(GCRevival.id("skeleton_boss"), "main");
    private static final ResourceLocation TEXTURE = GCRevival.id("textures/entity/skeleton_boss.png");

    public SkeletonBossRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletonBossModel(context.bakeLayer(LAYER)), 0.9F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonBoss entity) {
        return TEXTURE;
    }
}
