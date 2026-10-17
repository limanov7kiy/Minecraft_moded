package dev.mcmoded.undeadrising.item;

import dev.mcmoded.undeadrising.registry.FurItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import javax.annotation.Nullable;

import java.util.List;
import java.util.function.Supplier;

/// Оружие ближнего боя (оригинал: Fish's Undead Rising (Fish), `item/FURWeaponItem` + бонусы урона в
/// `events/EventHandler`): кинжал изо льда сам чинится в воде/под дождём, костяной меч бьёт сверх урона на 5% от
/// максимального здоровья цели (настройки оригинала: 5%, предел 10000), призрачный кинжал не ломается и наносит +2
/// живым (не нежити). Подсказка — `tooltip.mod_lavacow.<id>`.
public class FurWeaponItem extends SwordItem {
    public static final int BONESWORD_PERCENT = 5;
    public static final int BONESWORD_CAP = 10000;

    /// Призрачный материал (оригинал — `FURItemTier.SPECTRAL`): прочность 0 = вечный.
    public static final Tier SPECTRAL = new Tier() {
        public int getUses() { return 0; }
        public float getSpeed() { return 6.0F; }
        public float getAttackDamageBonus() { return 2.0F; }
        public int getLevel() { return 2; }
        public int getEnchantmentValue() { return 14; }
        public Ingredient getRepairIngredient() { return Ingredient.of(FurItems.ECTOPLASM.get()); }
    };

    private final Supplier<Ingredient> repair;

    public FurWeaponItem(Tier tier, int damage, float speed, Supplier<Ingredient> repair, Properties props) {
        super(tier, damage, speed, props);
        this.repair = repair;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack material) {
        return repair.get().test(material);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (stack.is(FurItems.FROZEN_DAGGER.get()) && entity instanceof LivingEntity && entity.isInWaterRainOrBubble()
                && level.random.nextInt(50) < 2) {
            stack.setDamageValue(Math.max(stack.getDamageValue() - 1, 0));
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (stack.is(FurItems.SPECTRAL_DAGGER.get()) && enchantment.category == EnchantmentCategory.BREAKABLE) return false;
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return !stack.is(FurItems.SPECTRAL_DAGGER.get()) || net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantments(book)
                .keySet().stream().noneMatch(e -> e.category == EnchantmentCategory.BREAKABLE);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String key = "tooltip." + getDescriptionId().substring("item.".length());
        tooltip.add((stack.is(FurItems.BONESWORD.get()) ? Component.translatable(key, BONESWORD_PERCENT, BONESWORD_CAP)
                : Component.translatable(key)).withStyle(ChatFormatting.YELLOW));
    }

    /// Бонусный урон костяного меча и призрачного кинжала (вызывается из `FurEntityEvents.onHurt`).
    public static void onHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker) || event.getSource().getDirectEntity() != attacker) return;
        ItemStack held = attacker.getMainHandItem();
        LivingEntity target = event.getEntity();
        if (held.is(FurItems.BONESWORD.get())) {
            event.setAmount(event.getAmount() + Math.min(BONESWORD_CAP, target.getMaxHealth() * BONESWORD_PERCENT * 0.01F));
        } else if (held.is(FurItems.SPECTRAL_DAGGER.get()) && target.getMobType() != MobType.UNDEAD) {
            event.setAmount(event.getAmount() + 2.0F);
        }
    }
}
