package dev.mcmoded.disasterward;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** Позиции оберегов в мире (переживают перезапуск) и флаг «случайные бедствия уже включены». */
public class WardData extends SavedData {
    private static final String NAME = "disaster_ward";
    private final Set<Long> wards = new HashSet<>();
    public boolean naturalInit;

    public static WardData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(WardData::load, WardData::new, NAME);
    }

    public static WardData load(CompoundTag tag) {
        WardData d = new WardData();
        for (long p : tag.getLongArray("wards")) d.wards.add(p);
        d.naturalInit = tag.getBoolean("naturalInit");
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLongArray("wards", wards.stream().mapToLong(Long::longValue).toArray());
        tag.putBoolean("naturalInit", naturalInit);
        return tag;
    }

    public void add(BlockPos pos) { if (wards.add(pos.asLong())) setDirty(); }
    public void remove(BlockPos pos) { if (wards.remove(pos.asLong())) setDirty(); }
    public boolean isEmpty() { return wards.isEmpty(); }

    public Set<BlockPos> positions() {
        Set<BlockPos> out = new HashSet<>();
        for (long l : wards) out.add(BlockPos.of(l));
        return out;
    }
}
