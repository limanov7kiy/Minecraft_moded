package dev.mcmoded.necromancersdelight.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.mcmoded.necromancersdelight.NecromancersDelight;
import dev.mcmoded.necromancersdelight.entity.ElementalLeech;
import dev.mcmoded.necromancersdelight.entity.LeechMinion;
import dev.mcmoded.undeadrising.client.ClientSetup;
import dev.mcmoded.undeadrising.client.model.ParasiteModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/// Рендер пиявок (оригинал — `RenderLeech`/`RenderElementalLeech`, WinDanesz): модель паразита Fish's Undead Rising,
/// своя текстура (у стихийной — по стихии); на носителе — крупнее и прижата к спине, как паразит.
public class LeechRenderer<T extends LeechMinion> extends MobRenderer<T, ParasiteModel<T>> {
    private static final ResourceLocation MANA = tex("mana_leech");
    private static final ResourceLocation[] ELEMENTAL = {tex("leech_magic"), tex("leech_fire"), tex("leech_ice"), tex("leech_lightning"),
            tex("leech_necromancy"), tex("leech_earth"), tex("leech_sorcery"), tex("leech_healing")};

    public LeechRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ParasiteModel<>(ctx.bakeLayer(ClientSetup.PARASITE_LAYER)), 0.3F);
    }

    private static ResourceLocation tex(String name) {
        return new ResourceLocation(NecromancersDelight.MOD_ID, "textures/entity/" + name + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity instanceof ElementalLeech e ? ELEMENTAL[e.getElementIndex()] : MANA;
    }

    @Override
    protected float getFlipDegrees(T entity) {
        return 180.0F;
    }

    @Override
    protected void scale(T entity, PoseStack pose, float partialTick) {
        Entity vehicle = entity.getVehicle();
        if (vehicle == null) return;
        pose.scale(1.2F, 1.2F, 1.2F);
        pose.translate(0.0, 0.0, -0.4F);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        if (vehicle instanceof LivingEntity living && !living.isBaby() && living.getBbHeight() > 1.4F) {
            pose.translate(0.0, vehicle instanceof Player ? 0.5 : 0.3F, -0.3F);
            pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
        }
    }
}
