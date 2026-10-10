package dev.mcmoded.wizardryrestored.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mcmoded.wizardryrestored.entity.DecoyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/// Приманка выглядит как её заклинатель: модель игрока (обычная или «тонкая») со скином владельца.
public class DecoyRenderer extends LivingEntityRenderer<DecoyEntity, PlayerModel<DecoyEntity>> {
    private final PlayerModel<DecoyEntity> wide;
    private final PlayerModel<DecoyEntity> slim;

    public DecoyRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
        wide = model;
        slim = new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), ctx.getModelManager()));
        addLayer(new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()));
    }

    private static PlayerInfo info(DecoyEntity decoy) {
        var conn = Minecraft.getInstance().getConnection();
        return conn == null ? null : decoy.owner().map(conn::getPlayerInfo).orElse(null);
    }

    @Override
    public void render(DecoyEntity decoy, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        PlayerInfo info = info(decoy);
        UUID id = decoy.owner().orElse(decoy.getUUID());
        String type = info != null ? info.getModelName() : DefaultPlayerSkin.getSkinModelName(id);
        model = "slim".equals(type) ? slim : wide;
        super.render(decoy, yaw, partialTick, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(DecoyEntity decoy) {
        PlayerInfo info = info(decoy);
        return info != null ? info.getSkinLocation() : DefaultPlayerSkin.getDefaultSkin(decoy.owner().orElse(decoy.getUUID()));
    }

    @Override
    protected void scale(DecoyEntity decoy, PoseStack pose, float partialTick) {
        pose.scale(0.9375f, 0.9375f, 0.9375f);
    }
}
