package net.Gabou.gaboulibs.util;

import net.Gabou.gaboulibs.storage.SnowRecord;
import net.minecraft.util.RandomSource;

/** Случайная снежная буря: от 1 до 6 слоёв снега (слой — 1/8 блока). */
public final class SnowGenerator {
    private SnowGenerator() {
    }

    public static SnowRecord generateStormRecord(RandomSource random) {
        float min = 1.0F + random.m_188501_();                 // nextFloat: 1..2
        float max = min + 1.0F + random.m_188501_() * 3.0F;     // ещё 1..4 сверху, не больше 6
        float avg = min + (max - min) * (0.3F + random.m_188501_() * 0.4F);
        return new SnowRecord(min, avg, max, null);
    }
}
