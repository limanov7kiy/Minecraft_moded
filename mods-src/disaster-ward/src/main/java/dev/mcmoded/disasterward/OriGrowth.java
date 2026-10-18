package dev.mcmoded.disasterward;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

/**
 * Ориджиниум Bob's Originium Dream вне бури (миксины на процедуры мода, mixin/Ori*Mixin): рост «потихоньку» и зона оберега.
 * Рост: блок oriblockb раз в 2400 тиков (если рядом ≤50 блоков игрок) превращается в oldoriblock, ставит новый oriblockb на соседнюю клетку
 * и с шансом 30 % — ядро над собой (OrigrowProcedure). Ядро oriblock_core на каждом случайном тике (~раз в 68 с) бьёт всё живое в радиусе 4 на 8
 * и призывает монстров (Origrow2Procedure). Пропущенный шаг роста не теряется: блок сам переназначает себе тик через 2400.
 */
public final class OriGrowth {
    private OriGrowth() {}

    /** Счётчики для теста: попытки роста и ядер, пропущено броском, отменено в зоне. */
    public static long growTried, growSkipped, coreTried, coreSkipped, zoneDenied;

    private static boolean inZone(LevelAccessor world, double x, double z) {
        return world instanceof ServerLevel level && WardConfig.ORI_GROWTH_PROTECT.get()
                && WardApi.isProtected(level, x, z, WardConfig.EDGE_MARGIN.get() + 2);
    }

    /** Шаг роста oriblockb: в зоне (+ edgeMargin + 2 на соседнюю клетку) — нет; вне — с шансом growChance. */
    public static boolean denyGrow(LevelAccessor world, double x, double z) {
        if (world.isClientSide()) return false;
        growTried++;
        if (inZone(world, x, z)) { zoneDenied++; return true; }
        if (ThreadLocalRandom.current().nextDouble() >= WardConfig.ORI_GROW_CHANCE.get()) { growSkipped++; return true; }
        return false;
    }

    /** Случайный тик ядра (урон рядом и призыв монстров): в зоне — нет; вне — с шансом coreChance. */
    public static boolean denyCore(LevelAccessor world, double x, double z) {
        if (world.isClientSide()) return false;
        coreTried++;
        if (inZone(world, x, z)) { zoneDenied++; return true; }
        if (ThreadLocalRandom.current().nextDouble() >= WardConfig.ORI_CORE_CHANCE.get()) { coreSkipped++; return true; }
        return false;
    }

    /** Прочие процедуры, меняющие блоки (жидкость червя, ядро на месте заражённого, ломание блоков ориджиниевым мутантом): в зоне — нет. */
    public static boolean denyInZone(LevelAccessor world, double x, double z) {
        if (world.isClientSide() || !inZone(world, x, z)) return false;
        zoneDenied++;
        return true;
    }
}
