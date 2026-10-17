package dev.mcmoded.undeadrising.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.mcmoded.undeadrising.client.ClientSetup;
import dev.mcmoded.undeadrising.client.model.ParasiteModel;
import dev.mcmoded.undeadrising.entity.ParasiteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;

/// Рендер паразита: четыре окраса (обычный, пустынный, джунглей, Beelzebub), ползает по стенам и потолку
/// (поворот по прилипшей грани), на носителе — увеличен и прижат к спине/груди.
/// Оригинал: Fish's Undead Rising (Fish), `client/renderer/entity/ParasiteRenderer`.
public class ParasiteRenderer extends MobRenderer<ParasiteEntity, ParasiteModel<ParasiteEntity>> {
    private static final ResourceLocation[] TEXTURES = {
            tex("parasite"), tex("parasite1"), tex("parasite2"), tex("parasite3")};

    public ParasiteRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ParasiteModel<>(ctx.bakeLayer(ClientSetup.PARASITE_LAYER)), 0.3F);
    }

    private static ResourceLocation tex(String name) {
        return new ResourceLocation("mod_lavacow", "textures/mobs/parasite/" + name + ".png");
    }

    @Override
    protected float getFlipDegrees(ParasiteEntity entity) {
        return 180.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(ParasiteEntity entity) {
        return TEXTURES[Math.floorMod(entity.getSkin(), TEXTURES.length)];
    }

    @Override
    protected void scale(ParasiteEntity entity, PoseStack pose, float partialTick) {
        Entity vehicle = entity.getVehicle();
        if (vehicle == null) return;
        pose.scale(1.2F, 1.2F, 1.2F);
        pose.translate(0.0, 0.0, -0.4F);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        boolean humanoid = vehicle instanceof Player || vehicle instanceof Zombie || vehicle instanceof AbstractVillager
                || vehicle instanceof Raider || vehicle instanceof AbstractSkeleton;
        if (humanoid && !((LivingEntity) vehicle).isBaby()) {
            if (!(vehicle instanceof Player)) {
                pose.translate(0.0, 0.3F, -0.3F);
            } else {
                pose.translate(0.0, 0.5, -0.3F);
            }
            pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
        }
    }

    @Override
    protected void setupRotations(ParasiteEntity entity, PoseStack pose, float ageInTicks, float rotationYaw, float partialTicks) {
        Direction attached = entity.getAttachedBlock();
        if (attached == Direction.UP) {
            pose.mulPose(Axis.YP.rotationDegrees(180.0F - rotationYaw));
            pose.mulPose(Axis.XP.rotationDegrees(180.0F));
            pose.mulPose(Axis.YP.rotationDegrees(180.0F));
            pose.translate(0.0, -0.25, 0.0);
        } else if (attached != Direction.DOWN) {
            pose.translate(0.0, 0.25, 0.0);
            switch (attached) {
                case NORTH -> {
                    pose.mulPose(Axis.XP.rotationDegrees(90.0F));
                    pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
                }
                case SOUTH -> {
                    pose.mulPose(Axis.YP.rotationDegrees(180.0F));
                    pose.mulPose(Axis.XP.rotationDegrees(90.0F));
                }
                case WEST -> {
                    pose.mulPose(Axis.XP.rotationDegrees(90.0F));
                    pose.mulPose(Axis.ZP.rotationDegrees(-90.0F));
                }
                case EAST -> {
                    pose.mulPose(Axis.XP.rotationDegrees(90.0F));
                    pose.mulPose(Axis.ZP.rotationDegrees(90.0F));
                }
                default -> {}
            }
            if (entity.getDeltaMovement().y > -0.001F) pose.mulPose(Axis.YP.rotationDegrees(-180.0F));
            pose.translate(0.0, -0.25, 0.0);
        }
        super.setupRotations(entity, pose, ageInTicks, rotationYaw, partialTicks);
    }
}
