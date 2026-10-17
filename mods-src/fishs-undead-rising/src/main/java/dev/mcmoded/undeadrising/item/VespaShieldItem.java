package dev.mcmoded.undeadrising.item;

import dev.mcmoded.undeadrising.client.VespaShieldRenderer;
import dev.mcmoded.undeadrising.registry.FurItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import javax.annotation.Nullable;

import java.util.List;
import java.util.function.Consumer;

/// Щит Vespa (оригинал: Fish's Undead Rising (Fish), `item/VespaShieldItem`): блокируя удар в ближнем бою, колет
/// нападающего (2 урона шипами) и отравляет на 6 с. Чинится панцирем Vespa. Модель — брюшко осы
/// (`client/VespaShieldRenderer`, оригинал — `ModelVespaShield`).
public class VespaShieldItem extends ShieldItem {
    public VespaShieldItem(Properties props) {
        super(props);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return getDescriptionId();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack material) {
        return material.is(FurItems.VESPA_CARAPACE.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.mod_lavacow.vespa_shield").withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return VespaShieldRenderer.get();
            }
        });
    }

    /// Ответный укол (вызывается из `FurEntityEvents`).
    public static void onBlock(ShieldBlockEvent event) {
        LivingEntity defender = event.getEntity();
        if (defender.level().isClientSide || !defender.getUseItem().is(FurItems.VESPA_SHIELD.get())) return;
        if (event.getDamageSource().getDirectEntity() instanceof LivingEntity attacker) {
            attacker.hurt(defender.damageSources().thorns(defender), 2.0F);
            attacker.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0));
        }
    }
}
