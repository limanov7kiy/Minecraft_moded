package dev.mcmoded.port.familiars;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;

/**
 * Переходник: рецепты котла алхимика в Iron's Spells 3.16 задаются данными и варят только жидкости.
 * Кодовый рецепт Familiars (2 зелья яда + клык паука → флакон яда) заменён крафтом
 * data/alshanex_familiars/recipes/poison_vial_from_potions.json; этот класс лишь принимает вызовы.
 */
public class AlchemistCauldronRecipe {
    public AlchemistCauldronRecipe(Potion base, Item input, Item result) {}

    public AlchemistCauldronRecipe setBaseRequirement(int count) { return this; }

    public AlchemistCauldronRecipe setResultLimit(int count) { return this; }
}
