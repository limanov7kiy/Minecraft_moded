package dev.mcmoded.compat.jei;

import com.binaris.wizardry.content.recipe.ImbuementAltarRecipe;
import com.binaris.wizardry.setup.registries.EBRecipeTypes;
import dev.mcmoded.compat.MmCompat;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/** JEI-плагин сборки: категория Imbuement Altar (Electroblob's Wizardry Redux). Только клиент, JEI — необязательная зависимость. */
@JeiPlugin
public class MmJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(MmCompat.MOD_ID, "jei_plugin");
    private static final ResourceLocation ALTAR = new ResourceLocation("ebwizardry", "imbuement_altar");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ImbuementAltarCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        List<ImbuementAltarRecipe> recipes = level.getRecipeManager().getAllRecipesFor(EBRecipeTypes.IMBUEMENT_ALTAR);
        registration.addRecipes(ImbuementAltarCategory.TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        var altar = ForgeRegistries.ITEMS.getValue(ALTAR);
        if (altar != null) registration.addRecipeCatalyst(new ItemStack(altar), ImbuementAltarCategory.TYPE);
    }
}
