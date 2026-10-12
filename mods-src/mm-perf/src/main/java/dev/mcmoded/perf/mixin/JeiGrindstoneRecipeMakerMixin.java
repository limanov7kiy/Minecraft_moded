package dev.mcmoded.perf.mixin;

import java.util.stream.Stream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * JEI при КАЖДОМ входе в мир строит рецепты «снятие чар на точиле»: для каждого зачаровываемого предмета и
 * каждого зачарования прогоняет настоящее меню точила. В сборке с сотнями модовых предметов (Iron's Spells,
 * TravelOptics, AzureLib…) каждая копия ItemStack запускает тяжёлые хуки — этот шаг занимал ~1,8 мин
 * из ~3,5 мин запуска JEI на потоке отрисовки (игра «висит» после входа в мир).
 * Категория «Точило» в JEI остаётся (рецепты ремонта), пропадают только строки «снять чары с X».
 * Цель — класс JEI, поэтому remap = false и поиск по имени метода.
 */
@Mixin(targets = "mezz.jei.library.plugins.vanilla.grindstone.GrindstoneRecipeMaker", remap = false)
public abstract class JeiGrindstoneRecipeMakerMixin {
    @Inject(method = "getDisenchantRecipes", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void mm_perf$skipDisenchant(CallbackInfoReturnable<Stream<?>> cir) {
        cir.setReturnValue(Stream.empty());
    }
}
