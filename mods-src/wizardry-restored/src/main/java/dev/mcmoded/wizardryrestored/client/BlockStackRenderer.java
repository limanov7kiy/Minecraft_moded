package dev.mcmoded.wizardryrestored.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;

/// Упрощённая модель конструкции: столбик ванильных блоков (уменьшенный; тотемы и чёрная дыра медленно вращаются).
/// Своих текстур нет — модели собраны из ванильных частей.
public class BlockStackRenderer<T extends Entity> extends EntityRenderer<T> {
    private final float scale;
    private final float yOffset;
    private final BlockState[] blocks;
    private final boolean spin;

    public BlockStackRenderer(EntityRendererProvider.Context ctx, float scale, float yOffset, BlockState... blocks) {
        this(ctx, scale, yOffset, true, blocks);
    }

    public BlockStackRenderer(EntityRendererProvider.Context ctx, float scale, float yOffset, boolean spin, BlockState... blocks) {
        super(ctx);
        this.spin = spin;
        this.scale = scale;
        this.yOffset = yOffset;
        this.blocks = blocks;
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.translate(0, yOffset, 0);
        pose.mulPose(Axis.YP.rotationDegrees(spin ? (entity.tickCount + partialTick) * 2 : -entity.getYRot()));
        pose.scale(scale, scale, scale);
        pose.translate(-0.5, 0, -0.5);
        for (BlockState state : blocks) {
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, pose, buffers, light, OverlayTexture.NO_OVERLAY);
            pose.translate(0, 1, 0);
        }
        pose.popPose();
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
