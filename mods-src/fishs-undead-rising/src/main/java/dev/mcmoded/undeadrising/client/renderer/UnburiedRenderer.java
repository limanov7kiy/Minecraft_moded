package dev.mcmoded.undeadrising.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mcmoded.undeadrising.client.ClientSetup;
import dev.mcmoded.undeadrising.client.layer.LayerGenericGlowing;
import dev.mcmoded.undeadrising.client.layer.LayerGenericHeldItem;
import dev.mcmoded.undeadrising.client.layer.LayerUnburiedArmor;
import dev.mcmoded.undeadrising.client.model.UnburiedModel;
import dev.mcmoded.undeadrising.entity.unburied.UnburiedEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/// Рендер Unburied-семейства: модель {@link UnburiedModel}, светящиеся глаза (если есть текстура), предмет в руке,
/// броня. Unburied, Mummy и Frigid — этот класс с разными текстурами (в оригинале — UnburiedRenderer, MummyRenderer,
/// FrigidRenderer с одинаковым кодом); Mycosis — {@link MycosisRenderer}.
/// Оригинал: Fish's Undead Rising (Fish), `client/renderer/entity/UnburiedRenderer` (+ Mummy/FrigidRenderer).
public class UnburiedRenderer<T extends UnburiedEntity> extends MobRenderer<T, UnburiedModel<T>> {
    public static final ResourceLocation UNBURIED = tex("unburied"), UNBURIED_EYES = tex("unburied_eyes");
    public static final ResourceLocation FRIGID = tex("unburied1"), FRIGID_EYES = tex("unburied1_eyes");
    public static final ResourceLocation MUMMY = tex("unburied4");

    private final ResourceLocation texture;

    public UnburiedRenderer(EntityRendererProvider.Context ctx, ResourceLocation texture, @Nullable ResourceLocation eyes) {
        super(ctx, new UnburiedModel<>(ctx.bakeLayer(ClientSetup.UNBURIED_LAYER)), 0.5F);
        this.texture = texture;
        if (eyes != null) this.addLayer(new LayerGenericGlowing<>(this, eyes));
        this.addCommonLayers(ctx);
    }

    protected void addCommonLayers(EntityRendererProvider.Context ctx) {
        this.addLayer(new LayerGenericHeldItem<>(this, ctx.getItemInHandRenderer(), 0.0F, 0.15F, -0.6F, 1.0F));
        this.addLayer(new LayerUnburiedArmor<>(this, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR))));
    }

    public static ResourceLocation tex(String name) {
        return new ResourceLocation("mod_lavacow", "textures/mobs/unburied/" + name + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return this.texture;
    }

    @Override
    protected void scale(T entity, PoseStack pose, float partialTick) {
        if (entity.isBaby()) pose.scale(0.6F, 0.6F, 0.6F);
        // «Вылезает из земли»: пока идёт подъём, модель опущена.
        pose.translate(0.0, entity.getSpellTicks() / 20.0, 0.0);
    }
}
