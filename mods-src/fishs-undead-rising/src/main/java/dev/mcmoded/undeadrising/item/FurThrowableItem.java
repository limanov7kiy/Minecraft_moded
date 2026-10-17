package dev.mcmoded.undeadrising.item;

import dev.mcmoded.undeadrising.entity.projectile.FurBombEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.List;
import java.util.function.Supplier;

/// Метательная бомба (оригинал: Fish's Undead Rising (Fish), `item/FURThrowableItem`): бросок дугой вверх (−20°),
/// скорость 0,75; стак 16.
public class FurThrowableItem extends Item {
    private final Supplier<EntityType<FurBombEntity>> type;

    public FurThrowableItem(Supplier<EntityType<FurBombEntity>> type, Properties props) {
        super(props);
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        FurBombEntity.playThrowSound(level, player);
        if (!level.isClientSide) {
            FurBombEntity bomb = new FurBombEntity(type.get(), player, level);
            bomb.setItem(stack);
            bomb.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.75F, 1.0F);
            level.addFreshEntity(bomb);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip." + getDescriptionId().substring("item.".length())).withStyle(ChatFormatting.YELLOW));
    }
}
