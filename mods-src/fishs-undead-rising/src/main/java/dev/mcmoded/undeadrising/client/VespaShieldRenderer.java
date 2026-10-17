package dev.mcmoded.undeadrising.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mcmoded.undeadrising.UndeadRising;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/// Рисует щит Vespa — брюшко осы (оригинал: Fish's Undead Rising (Fish), `client/model/item/ModelVespaShield` +
/// `FURItemstackRenderer`), текстура моба 128×64. Положение в руке/GUI задают `models/item/vespa_shield*.json`.
public class VespaShieldRenderer extends BlockEntityWithoutLevelRenderer {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(UndeadRising.MOD_ID, "vespa_shield"), "main");
    private static final ResourceLocation TEXTURE = new ResourceLocation(UndeadRising.MOD_ID, "textures/mobs/vespa/vespa.png");
    private static VespaShieldRenderer instance;
    private ModelPart model;

    private VespaShieldRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public static VespaShieldRenderer get() {
        if (instance == null) instance = new VespaShieldRenderer();
        return instance;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition a1 = root.addOrReplaceChild("UAbdomen1", CubeListBuilder.create().texOffs(66, 48).addBox(-5.0F, -1.5F, 0.0F, 10.0F, 3.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 5.0F, -1.0927507F, (float) Math.PI, 0.0F));
        PartDefinition a2 = a1.addOrReplaceChild("UAbdomen2", CubeListBuilder.create().texOffs(88, 0).addBox(-6.0F, -2.0F, 0.0F, 12.0F, 4.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, -0.22759093F, 0.0F, 0.0F));
        a1.addOrReplaceChild("Stinger1", CubeListBuilder.create().texOffs(84, 36).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 3.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
        PartDefinition a3 = a2.addOrReplaceChild("UAbdomen3", CubeListBuilder.create().texOffs(100, 28).addBox(-4.0F, -1.5F, 0.0F, 8.0F, 3.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, -0.22759093F, 0.0F, 0.0F));
        a2.addOrReplaceChild("Stinger2", CubeListBuilder.create().texOffs(84, 36).mirror().addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 4.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
        a3.addOrReplaceChild("Stinger3", CubeListBuilder.create().texOffs(84, 36).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.5F, 3.6F, (float) (Math.PI / 2), 0.0F, 0.0F));
        a3.addOrReplaceChild("UAbdomen4", CubeListBuilder.create().texOffs(84, 28).addBox(-2.0F, -0.5F, 0.0F, 4.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 5.0F, -0.22759093F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void onResourceManagerReload(net.minecraft.server.packs.resources.ResourceManager manager) {
        model = Minecraft.getInstance().getEntityModels().bakeLayer(LAYER);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        if (model == null) model = Minecraft.getInstance().getEntityModels().bakeLayer(LAYER);
        pose.pushPose();
        pose.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer vc = ItemRenderer.getFoilBufferDirect(buffers, RenderType.entityCutoutNoCull(TEXTURE), false, stack.hasFoil());
        model.render(pose, vc, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        pose.popPose();
    }
}
