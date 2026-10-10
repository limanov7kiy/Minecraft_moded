package dev.mcmoded.port.familiars;

import net.minecraft.resources.ResourceLocation;

/** Переходник: см. {@link AlchemistCauldronRecipe}. */
public final class AlchemistCauldronRecipeRegistry {
    public static AlchemistCauldronRecipe registerRecipe(ResourceLocation id, AlchemistCauldronRecipe recipe) { return recipe; }

    private AlchemistCauldronRecipeRegistry() {}
}
