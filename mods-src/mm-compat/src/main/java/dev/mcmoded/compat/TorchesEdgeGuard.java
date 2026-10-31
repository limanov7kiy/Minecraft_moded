package dev.mcmoded.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;

/**
 * Torches Become Sunlight (FreeFish и др.) строит мегаструктуры в тике сервера прямо в загруженных чанках (поток 76).
 * Блок с блок-сущностью (воронка, сундук…) он ставит через LevelChunk.setBlockState, а это зовёт onPlace, и onPlace
 * воронки читает редстоун-сигнал соседей. Если блок стоит на краю чанка, а соседний чанк ещё не FULL, Level.getBlockState
 * синхронно грузит его из тика мегаструктуры и падает «Chunk not there when requested» — сервер останавливается.
 * Здесь — проверка «все чанки в радиусе {@link #MARGIN} блоков от позиции уже FULL» (без загрузки, только getChunkNow).
 */
public final class TorchesEdgeGuard {
    private static final Logger LOG = LogUtils.getLogger();
    /** onPlace/обновления соседей дотягиваются на 1–2 блока (сосед и сосед соседа). */
    public static final int MARGIN = 2;
    private static long placed, repaired;

    private TorchesEdgeGuard() {}

    public static boolean neighboursLoaded(ServerLevel level, BlockPos pos) {
        int x = pos.getX(), z = pos.getZ(), cx = x >> 4, cz = z >> 4;
        int x0 = (x - MARGIN) >> 4, x1 = (x + MARGIN) >> 4, z0 = (z - MARGIN) >> 4, z1 = (z + MARGIN) >> 4;
        if (x0 == cx && x1 == cx && z0 == cz && z1 == cz) return true;
        ServerChunkCache cache = level.getChunkSource();
        for (int i = x0; i <= x1; i++)
            for (int k = z0; k <= z1; k++)
                if ((i != cx || k != cz) && cache.getChunkNow(i, k) == null) return false;
        return true;
    }

    public static void notePlaced(BlockPos pos) {
        if (placed++ % 500 == 0)
            LOG.info("[mm_compat] Torches Become Sunlight: блок мегаструктуры у незагруженного соседнего чанка поставлен без onPlace ({} {} {}), всего {}",
                pos.getX(), pos.getY(), pos.getZ(), placed);
    }

    public static void noteRepaired(BlockPos pos) {
        if (repaired++ % 500 == 0)
            LOG.info("[mm_compat] Torches Become Sunlight: пост-обработка у незагруженного соседнего чанка без updateNeighborsAt ({} {} {}), всего {}",
                pos.getX(), pos.getY(), pos.getZ(), repaired);
    }
}
