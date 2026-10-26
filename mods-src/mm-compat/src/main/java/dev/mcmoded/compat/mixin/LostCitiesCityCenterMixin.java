package dev.mcmoded.compat.mixin;

import dev.mcmoded.compat.ReusableRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

/**
 * The Lost Cities 1.20-7.5.7 (McJty, MIT): City.isCityCenter вызывается для каждого чанка из соседних
 * (getCityFactor перебирает окрестность), и каждый раз создаёт new Random(seed) ради одного nextDouble().
 * На тестовом сервере (поток 67, полёт с генерацией) это ~26 % времени каждого рабочего потока генерации:
 * больше половины — атомарные операции AtomicLong внутри Random, плюс ~110 МБ/с мусора.
 * Подменяем создание на ReusableRandom текущего потока с тем же сидом: тот же алгоритм Random без атомиков,
 * числа совпадают бит-в-бит — города и мир генерируются так же, как раньше.
 */
@Pseudo
@Mixin(targets = "mcjty.lostcities.worldgen.lost.City", remap = false)
public abstract class LostCitiesCityCenterMixin {
    @Redirect(method = "isCityCenter", remap = false, require = 1,
        at = @At(value = "NEW", target = "(J)Ljava/util/Random;", remap = false))
    private static Random mm_compat$reusableRandom(long seed) {
        return ReusableRandom.of(seed);
    }
}
