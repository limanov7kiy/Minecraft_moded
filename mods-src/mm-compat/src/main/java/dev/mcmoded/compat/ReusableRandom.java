package dev.mcmoded.compat;

import java.util.Random;

/**
 * java.util.Random без AtomicLong в каждом шаге: тот же линейный конгруэнтный генератор (те же константы и
 * начальное перемешивание, что в Random), поэтому последовательность чисел совпадает бит-в-бит. Один экземпляр
 * на поток, сид задаётся заново перед каждым использованием (см. LostCitiesCityCenterMixin). Не потокобезопасен.
 */
public final class ReusableRandom extends Random {
    private static final long MULTIPLIER = 0x5DEECE66DL, ADDEND = 0xBL, MASK = (1L << 48) - 1;
    private static final ThreadLocal<ReusableRandom> LOCAL = ThreadLocal.withInitial(ReusableRandom::new);
    private long state;

    private ReusableRandom() { super(0L); }

    /** Генератор текущего потока с сидом, как у {@code new Random(seed)}. */
    public static ReusableRandom of(long seed) {
        ReusableRandom r = LOCAL.get();
        r.state = (seed ^ MULTIPLIER) & MASK;
        return r;
    }

    @Override
    public synchronized void setSeed(long seed) {
        // вызывается из конструктора Random до инициализации полей подкласса — тогда состояние задаёт of()
        state = (seed ^ MULTIPLIER) & MASK;
    }

    @Override
    protected int next(int bits) {
        state = (state * MULTIPLIER + ADDEND) & MASK;
        return (int) (state >>> (48 - bits));
    }
}
