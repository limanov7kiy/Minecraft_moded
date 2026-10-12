package dev.mcmoded.compat.jei;

import com.binaris.wizardry.content.recipe.ImbuementAltarRecipe;
import dev.mcmoded.compat.MmCompat;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Центральный предмет + 4 приёмника → результат (как в мире: предмет на алтаре, материалы на приёмниках). */
public class ImbuementAltarCategory implements IRecipeCategory<ImbuementAltarRecipe> {
    public static final RecipeType<ImbuementAltarRecipe> TYPE =
            RecipeType.create(MmCompat.MOD_ID, "imbuement_altar", ImbuementAltarRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ImbuementAltarCategory(IGuiHelper gui) {
        background = gui.createBlankDrawable(130, 70);
        var altar = ForgeRegistries.ITEMS.getValue(new ResourceLocation("ebwizardry", "imbuement_altar"));
        icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(altar != null ? altar : net.minecraft.world.item.Items.ENCHANTING_TABLE));
    }

    @Override
    public RecipeType<ImbuementAltarRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.ebwizardry.imbuement_altar");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ImbuementAltarRecipe recipe, IFocusGroup focuses) {
        // крест: приёмники сверху, снизу, слева, справа от центра
        int[][] pos = {{27, 1}, {27, 49}, {3, 25}, {51, 25}};
        // у рецептов с розыгрышем (fix_ruined_spell_book) приёмников/центра может не быть
        Ingredient center = recipe.getCenterIngredient();
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 25).addIngredients(center != null ? center : Ingredient.EMPTY);
        List<Ingredient> rec = recipe.getReceptacleIngredients();
        if (rec == null) rec = List.of();
        for (int i = 0; i < rec.size() && i < pos.length; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, pos[i][0], pos[i][1]).addIngredients(rec.get(i));
        }
        var out = builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 25);
        var level = Minecraft.getInstance().level;
        ItemStack result = level != null ? recipe.getResultItem(level.registryAccess()) : ItemStack.EMPTY;
        if (!result.isEmpty()) {
            out.addItemStack(result);
        } else {
            out.addItemStacks(pollResults(recipe)); // рецепты с розыгрышем: результат зависит от стихии приёмников
        }
    }

    @SuppressWarnings("unchecked")
    private static List<ItemStack> pollResults(ImbuementAltarRecipe recipe) {
        try {
            Field f = ImbuementAltarRecipe.class.getDeclaredField("results");
            f.setAccessible(true);
            return new ArrayList<>(((Map<?, ItemStack>) f.get(recipe)).values());
        } catch (ReflectiveOperationException | RuntimeException e) {
            return List.of();
        }
    }

    @Override
    public void draw(ImbuementAltarRecipe recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView slots,
                     net.minecraft.client.gui.GuiGraphics g, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        g.drawString(font, "→", 77, 29, 0xFF808080, false);
    }
}
