package dev.mcmoded.undeadrising.item;

import dev.mcmoded.undeadrising.entity.projectile.DeathCoilEntity;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/// Посох Forsaken: ПКМ — «Кольцо смерти» в сторону взгляда, перезарядка 2 с. Без боеприпасов; чары лука работают
/// (Сила — урон, Отбрасывание, Горящая стрела — поджигает).
/// Оригинал: Fish's Undead Rising (Fish), `item/FURRangedItem` (ветка DEATHCOIL).
public class ForsakenStaffItem extends BowItem {
    public ForsakenStaffItem(Properties props) {
        super(props);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {}

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            Vec3 look = player.getLookAngle();
            int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
            int punch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
            int flame = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack);
            DeathCoilEntity coil = new DeathCoilEntity(FurEntities.DEATHCOIL.get(), level);
            coil.setPos(player.getX() + look.x, player.getY() + player.getBbHeight(), player.getZ() + look.z);
            coil.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.75F, 1.0F);
            coil.setOwner(player);
            if (power > 0) coil.setDamage(coil.getDamage() * (1.0F + (power + 1) * 0.25F));
            if (punch > 0) coil.setKnockbackStrength(punch);
            if (flame > 0) coil.setSecondsOnFire(100);
            level.addFreshEntity(coil);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), FurSounds.SKELETONKING_SPELL_TOSS.get(), SoundSource.PLAYERS,
                    1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F));
            player.getCooldowns().addCooldown(this, 40 - power * 2);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 320;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.mod_lavacow.forsaken_staff").withStyle(ChatFormatting.YELLOW));
    }
}
