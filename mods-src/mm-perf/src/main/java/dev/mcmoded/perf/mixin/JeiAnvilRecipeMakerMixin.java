package dev.mcmoded.perf.mixin;

import java.util.stream.Stream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * То же, что {@link JeiGrindstoneRecipeMakerMixin}, для наковальни: JEI для каждого зачаровываемого предмета
 * и зачарования гоняет настоящее меню наковальни («предмет + книга»). Рецепты ремонта остаются,
 * пропадают только строки «предмет + зачарованная книга».
 */
@Mixin(targets = "mezz.jei.library.plugins.vanilla.anvil.AnvilRecipeMaker", remap = false)
public abstract class JeiAnvilRecipeMakerMixin {
    @Inject(method = "getBookEnchantmentRecipes", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void mm_perf$skipBookEnchant(CallbackInfoReturnable<Stream<?>> cir) {
        cir.setReturnValue(Stream.empty());
    }
}
