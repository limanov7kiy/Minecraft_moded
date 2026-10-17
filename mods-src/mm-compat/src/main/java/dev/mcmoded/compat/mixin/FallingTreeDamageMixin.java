package dev.mcmoded.compat.mixin;

import fr.rakambda.fallingtree.common.config.enums.DamageRounding;
import fr.rakambda.fallingtree.common.config.enums.MaxSizeAction;
import fr.rakambda.fallingtree.common.tree.breaking.BreakTreeTooBigException;
import fr.rakambda.fallingtree.common.tree.breaking.ToolDamageHandler;
import fr.rakambda.fallingtree.common.wrapper.IItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Falling Tree (автор RakSrinaNa, LGPL-3.0) при {@code tools.preserve = true} рубит столько брёвен, на сколько
 * хватает прочности топора. Нам нужно «всё или ничего»: если прочности не хватает на ВСЕ брёвна дерева,
 * массовой рубки нет, ломается один блок, как в ванили. Для этого в конце конструктора бросаем то же
 * {@link BreakTreeTooBigException}, что мод бросает при превышении maxSize в режиме ABORT: TreeHandler ловит его
 * и возвращает AbortedResult.TREE_TOO_BIG_BREAK (cancel = false), то есть событие не отменяется и игра
 * ломает один блок. Конструктор вызывают все три обработчика (Instantaneous, FallingAnimation, ShiftDown).
 * Здесь breakableCount — число брёвен дерева, maxSize — предел из конфига.
 */
@Pseudo
@Mixin(value = ToolDamageHandler.class, remap = false)
public abstract class FallingTreeDamageMixin {
    @Shadow private int maxBreakCount;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void mm_compat$allOrNothing(IItemStack tool, double damageMultiplicand, boolean preserve,
                                        int breakableCount, int maxSize, MaxSizeAction action,
                                        DamageRounding rounding, CallbackInfo ci) throws BreakTreeTooBigException {
        if (preserve && tool.isDamageable() && maxBreakCount < breakableCount) {
            throw new BreakTreeTooBigException();
        }
    }
}
