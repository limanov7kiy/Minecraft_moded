package dev.mcmoded.undeadrising.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/// Колотый лёд (добыча Frigid): пьётся как напиток, гасит горящего.
/// Оригинал: Fish's Undead Rising (Fish), `item/FURItem` (ветка SHATTERED_ICE).
public class ShatteredIceItem extends Item {
    public ShatteredIceItem(Properties props) {
        super(props);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity.isOnFire()) entity.clearFire();
        return super.finishUsingItem(stack, level, entity);
    }
}
