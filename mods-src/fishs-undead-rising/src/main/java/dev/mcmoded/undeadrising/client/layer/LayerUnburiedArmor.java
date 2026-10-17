package dev.mcmoded.undeadrising.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.mcmoded.undeadrising.client.model.UnburiedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/// Броня на сгорбленной модели Unburied: каждая часть брони крепится к своей кости модели. Отделка (trims) не рисуется.
/// Оригинал: Fish's Undead Rising (Fish), `client/layer/LayerUnburiedArmor`.
public class LayerUnburiedArmor<T extends LivingEntity, M extends UnburiedModel<T>> extends RenderLayer<T, M> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = new HashMap<>();
    private final HumanoidModel<T> armorModel;

    /// @param armorModel модель брони (внешний слой, раздутие 1.0 — как `BipedModel(1.0F)` оригинала)
    public LayerUnburiedArmor(RenderLayerParent<T, M> parent, HumanoidModel<T> armorModel) {
        super(parent);
        this.armorModel = armorModel;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.CHEST, light);
        this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.LEGS, light);
        this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.FEET, light);
        this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.HEAD, light);
    }

    private void renderArmorPiece(PoseStack pose, MultiBufferSource buffers, T entity, EquipmentSlot slot, int light) {
        ItemStack stack = entity.getItemBySlot(slot);
        if (!(stack.getItem() instanceof ArmorItem armor) || armor.getEquipmentSlot() != slot) return;
        pose.pushPose();
        this.translateToSlot(pose, slot);
        this.getParentModel().copyPropertiesTo(this.armorModel);
        this.setPartVisibility(this.armorModel, slot);
        Model model = ForgeHooksClient.getArmorModel(entity, stack, slot, this.armorModel);
        boolean foil = stack.hasFoil();
        if (armor instanceof DyeableLeatherItem dyeable) {
            int c = dyeable.getColor(stack);
            float r = (c >> 16 & 0xFF) / 255.0F, g = (c >> 8 & 0xFF) / 255.0F, b = (c & 0xFF) / 255.0F;
            renderModel(pose, buffers, light, foil, model, r, g, b, this.getArmorResource(entity, stack, slot, null));
            renderModel(pose, buffers, light, foil, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, stack, slot, "overlay"));
        } else {
            renderModel(pose, buffers, light, foil, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, stack, slot, null));
        }
        pose.popPose();
    }

    private void translateToSlot(PoseStack pose, EquipmentSlot slot) {
        UnburiedModel<T> m = this.getParentModel();
        switch (slot) {
            case HEAD -> {
                m.Body_base.translateAndRotate(pose);
                m.Body_waist.translateAndRotate(pose);
                m.Body_chest.translateAndRotate(pose);
                m.Neck0.translateAndRotate(pose);
                m.Neck1.translateAndRotate(pose);
                m.Head.translateAndRotate(pose);
            }
            case CHEST -> {
                m.Body_base.translateAndRotate(pose);
                m.Body_waist.translateAndRotate(pose);
                pose.scale(1.1F, 1.1F, 1.1F);
                pose.translate(0.0, -0.15F, -0.1F);
            }
            case LEGS -> {
                m.Body_base.translateAndRotate(pose);
                pose.translate(0.0, -0.1F, 0.0);
                pose.scale(0.8F, 0.8F, 0.8F);
            }
            case FEET -> {
                m.Body_base.translateAndRotate(pose);
                pose.translate(0.0, 0.2F, -0.2F);
                pose.scale(1.1F, 1.1F, 1.1F);
                pose.mulPose(Axis.XP.rotationDegrees(30.0F));
            }
            default -> {}
        }
    }

    protected void setPartVisibility(HumanoidModel<T> model, EquipmentSlot slot) {
        model.setAllVisible(false);
        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }
            case CHEST -> model.body.visible = true;
            case LEGS, FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            default -> {}
        }
    }

    private static void renderModel(PoseStack pose, MultiBufferSource buffers, int light, boolean foil, Model model,
                                    float r, float g, float b, ResourceLocation texture) {
        VertexConsumer vc = ItemRenderer.getArmorFoilBuffer(buffers, RenderType.armorCutoutNoCull(texture), false, foil);
        model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
    }

    public ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem) stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String path = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture,
                slot == EquipmentSlot.LEGS ? 2 : 1, type == null ? "" : "_" + type);
        path = ForgeHooksClient.getArmorTexture(entity, stack, path, slot, type);
        return ARMOR_LOCATION_CACHE.computeIfAbsent(path, ResourceLocation::new);
    }
}
