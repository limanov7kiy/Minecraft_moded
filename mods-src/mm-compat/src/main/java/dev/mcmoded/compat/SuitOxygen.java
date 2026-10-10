package dev.mcmoded.compat;

import earth.terrarium.adastra.api.events.AdAstraEvents;
import earth.terrarium.adastra.api.systems.OxygenApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/// Скафандры Northstar и Creating Space в мирах Ad Astra (одна система окружающей среды — Ad Astra).
///
/// Ad Astra спрашивает «есть ли у существа кислород» через своё событие. Отвечаем «да», если на нём
/// полный скафандр Northstar или Creating Space и в его баллоне есть воздух, — и раз в секунду списываем
/// воздух из баллона так же, как это делают сами моды (1 единица в секунду). Пустой баллон — Ad Astra
/// наносит обычный урон от удушья.
public final class SuitOxygen {
    private static final TagKey<Item> NS_SEALING = item("northstar", "oxygen_sealing");
    private static final TagKey<Item> NS_SOURCES = item("northstar", "oxygen_sources");
    private static final TagKey<Item> CS_SUIT = item("creatingspace", "spacesuit");
    private static final TagKey<Item> CS_SOURCES = item("creatingspace", "oxygen_sources");
    private static final String OXYGEN = "Oxygen";   // у обоих модов запас воздуха лежит в NBT под этим ключом

    private SuitOxygen() {
    }

    private static TagKey<Item> item(String ns, String path) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(ns, path));
    }

    static void register() {
        AdAstraEvents.EntityOxygenEvent.register((entity, hasOxygen) -> hasOxygen || suitTank(entity) != null);
    }

    /// Списание воздуха: раз в секунду, только там, где Ad Astra считает, что воздуха нет.
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || entity.tickCount % 20 != 0) return;
        if (entity instanceof Player p && (p.isCreative() || p.isSpectator())) return;
        if (OxygenApi.API.hasOxygen(entity.level(), BlockPos.containing(entity.getEyePosition()))) return;
        ItemStack tank = suitTank(entity);
        if (tank == null) return;
        CompoundTag tag = tank.getOrCreateTag();
        if (tag.contains(OXYGEN, Tag.TAG_INT)) {                 // Northstar: целое число
            tag.putInt(OXYGEN, Math.max(0, tag.getInt(OXYGEN) - 1));
        } else {                                                  // Creating Space: дробное
            tag.putFloat(OXYGEN, Math.max(0f, tag.getFloat(OXYGEN) - 1f));
        }
    }

    /// Баллон с непустым запасом, если на существе полный скафандр Northstar или Creating Space; иначе null.
    static ItemStack suitTank(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return null;
        ItemStack tank = fullSetTank(living, NS_SEALING, NS_SOURCES);
        return tank != null ? tank : fullSetTank(living, CS_SUIT, CS_SOURCES);
    }

    private static ItemStack fullSetTank(LivingEntity living, TagKey<Item> suit, TagKey<Item> sources) {
        ItemStack tank = null;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = living.getItemBySlot(slot);
            if (stack.isEmpty() || !stack.is(suit)) return null;
            if (stack.is(sources)) tank = stack;
        }
        if (tank == null || !tank.hasTag()) return null;
        CompoundTag tag = tank.getTag();
        float air = tag.contains(OXYGEN, Tag.TAG_INT) ? tag.getInt(OXYGEN) : tag.getFloat(OXYGEN);
        return air > 0 ? tank : null;
    }
}
