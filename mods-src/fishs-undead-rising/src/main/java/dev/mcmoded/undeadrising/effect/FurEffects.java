package dev.mcmoded.undeadrising.effect;

import dev.mcmoded.undeadrising.UndeadRising;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/// Эффекты Fish's Undead Rising (оригинал — `effect/Effect*` и `events/EventHandler` 1.16.5, © Fish).
/// Цвета и правила — как в оригинале.
public final class FurEffects {
    private FurEffects() {}

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, UndeadRising.MOD_ID);

    /// Soiled: нельзя есть и пить зелья; лечение −25% за уровень.
    public static final RegistryObject<MobEffect> SOILED = EFFECTS.register("soiled", () -> new Plain(MobEffectCategory.HARMFUL, 7298108));
    /// Corroded: входящий урон +10% за уровень (I — +10%, II — +20%…).
    public static final RegistryObject<MobEffect> CORRODED = EFFECTS.register("corroded", () -> new Plain(MobEffectCategory.HARMFUL, 2361095));
    /// Infested: из существа вылезают паразиты (мобы — этап 2 порта; пока эффект без появления паразитов).
    public static final RegistryObject<MobEffect> INFESTED = EFFECTS.register("infested", () -> new Plain(MobEffectCategory.HARMFUL, 10037074));

    static final class Plain extends MobEffect {
        Plain(MobEffectCategory category, int color) {
            super(category, color);
        }
    }

    @SubscribeEvent
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {
        LivingEntity e = event.getEntity();
        if (e.hasEffect(SOILED.get()) && (event.getItem().isEdible() || event.getItem().getItem() instanceof PotionItem)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        var soiled = event.getEntity().getEffect(SOILED.get());
        if (soiled != null) event.setAmount(event.getAmount() * Math.max(0, 1 - 0.25f * (1 + soiled.getAmplifier())));
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        var corroded = event.getEntity().getEffect(CORRODED.get());
        if (corroded != null) event.setAmount(event.getAmount() * (1 + 0.1f * (1 + corroded.getAmplifier())));
    }
}
