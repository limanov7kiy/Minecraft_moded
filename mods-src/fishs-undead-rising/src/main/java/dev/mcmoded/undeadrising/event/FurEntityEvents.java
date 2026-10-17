package dev.mcmoded.undeadrising.event;

import dev.mcmoded.undeadrising.entity.unburied.UnburiedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/// Мобовые правила из общего обработчика событий оригинала (оригинал: Fish's Undead Rising (Fish),
/// `events/EventHandler`). Сюда же — такие правила для мобов следующих партий (Scarab, Lil Sludge и т. п.).
/// Шина FORGE.
public final class FurEntityEvents {
    private FurEntityEvents() {}

    /// Доп. урон призванных Unburied (чары оружия призыва).
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof UnburiedEntity unburied) {
            event.setAmount(event.getAmount() + unburied.getBonusDamage(event.getEntity()));
        }
    }

    /// Вампиризм: хозяин призванного Unburied лечится на 5% нанесённого урона за уровень.
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (event.getSource().getDirectEntity() instanceof UnburiedEntity unburied && unburied.getLifestealLevel() > 0) {
            LivingEntity owner = unburied.getOwner();
            if (owner != null) owner.heal(event.getAmount() * unburied.getLifestealLevel() * 0.05F);
        }
    }

    /// Жители убегают от Unburied-семейства.
    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof Villager villager) {
            villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, UnburiedEntity.class, 8.0F, 0.8, 0.8));
        }
    }

    /// Миньоны, погибшие вместе с хозяином-мобом (тег `FUR_noLoot`), ничего не роняют.
    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (event.getEntity().getTags().contains("FUR_noLoot")) event.setCanceled(true);
    }
}
