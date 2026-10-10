package net.Gabou.gaboulibs.storage;

import net.minecraft.nbt.CompoundTag;

/** Параметры одной снежной бури: сколько слоёв снега она оставляет (минимум/среднее/максимум). */
public class SnowRecord {
    public float minLayers;
    public float avgLayers;
    public float maxLayers;
    public int[] pattern;

    public SnowRecord() {
        this(0.0F, 0.0F, 0.0F, null);
    }

    public SnowRecord(float minLayers, float avgLayers, float maxLayers, int[] pattern) {
        this.minLayers = minLayers;
        this.avgLayers = avgLayers;
        this.maxLayers = maxLayers;
        this.pattern = pattern;
    }

    // Методы Minecraft вызываются по SRG-именам: библиотека собирается против серверного jar без маппингов.
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.m_128350_("minLayers", minLayers);   // putFloat
        tag.m_128350_("avgLayers", avgLayers);
        tag.m_128350_("maxLayers", maxLayers);
        if (pattern != null) tag.m_128385_("pattern", pattern);   // putIntArray
        return tag;
    }

    public static SnowRecord fromTag(CompoundTag tag) {
        SnowRecord r = new SnowRecord();
        if (tag == null) return r;
        r.minLayers = tag.m_128457_("minLayers");   // getFloat
        r.avgLayers = tag.m_128457_("avgLayers");
        r.maxLayers = tag.m_128457_("maxLayers");
        if (tag.m_128441_("pattern")) r.pattern = tag.m_128465_("pattern");   // contains / getIntArray
        return r;
    }
}
