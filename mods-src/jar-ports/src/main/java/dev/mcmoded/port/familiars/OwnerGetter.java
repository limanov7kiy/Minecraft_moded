package dev.mcmoded.port.familiars;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

/**
 * Переходник: io.redspace.ironsspellbooks.entity.mobs.goals.OwnerGetter удалён из Iron's Spells 3.16, цели
 * питомцев принимают Supplier&lt;Entity&gt;. Мост get()Object → get()LivingEntity добавляет JarPort (правило bridge),
 * чтобы лямбды Familiars, собранные под старый интерфейс, работали как Supplier.
 */
@FunctionalInterface
public interface OwnerGetter extends Supplier<Entity> {
    LivingEntity get();
}
