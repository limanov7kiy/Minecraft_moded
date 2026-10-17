package dev.mcmoded.disasterward;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/** Быстрый доступ к позициям оберегов по измерению — для проверок на горячем пути (setBlock, тик сущностей). */
public final class WardIndex {
    private static final BlockPos[] NONE = new BlockPos[0];
    private static final Map<ResourceKey<Level>, BlockPos[]> BY_DIM = new ConcurrentHashMap<>();

    private WardIndex() {}

    static void set(ResourceKey<Level> dim, Iterable<Long> wards) {
        java.util.List<BlockPos> out = new java.util.ArrayList<>();
        for (long l : wards) out.add(BlockPos.of(l));
        if (out.isEmpty()) BY_DIM.remove(dim); else BY_DIM.put(dim, out.toArray(NONE));
    }

    static void clear() { BY_DIM.clear(); }

    public static BlockPos[] get(Level level) {
        BlockPos[] w = BY_DIM.get(level.dimension());
        return w == null ? NONE : w;
    }

    public static boolean has(Level level) { return BY_DIM.containsKey(level.dimension()); }

    /** Лежит ли точка в квадрате radius + margin вокруг какого-либо из оберегов. */
    public static boolean inZone(BlockPos[] wards, double x, double z, int margin) {
        if (wards.length == 0) return false;
        double r = WardConfig.RADIUS.get() + margin;
        for (BlockPos p : wards) {
            if (Math.abs(x - (p.getX() + 0.5)) <= r && Math.abs(z - (p.getZ() + 0.5)) <= r) return true;
        }
        return false;
    }

    /** Ближайший оберег, в чей квадрат radius + margin попадает точка, или null. */
    public static BlockPos owner(BlockPos[] wards, double x, double z, int margin) {
        double r = WardConfig.RADIUS.get() + margin;
        for (BlockPos p : wards) {
            if (Math.abs(x - (p.getX() + 0.5)) <= r && Math.abs(z - (p.getZ() + 0.5)) <= r) return p;
        }
        return null;
    }
}
