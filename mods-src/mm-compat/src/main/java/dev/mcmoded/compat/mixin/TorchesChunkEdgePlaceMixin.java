package dev.mcmoded.compat.mixin;

import dev.mcmoded.compat.TorchesEdgeGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Torches Become Sunlight 0.5.0-hotfix2 (FreeFish и др.): падение сервера «Chunk not there when requested» (поток 76, нашёл поток 73).
 * MegaStructureTask$ChunkPlacementWriter.placeBlockInLevelChunk ставит блоки с блок-сущностью через LevelChunk.setBlockState(pos, state, false);
 * тот зовёт onPlace, и onPlace воронки (checkPoweredState → hasNeighborSignal) читает соседний блок. На краю чанка сосед — в чанке,
 * который ещё не FULL, и getBlockState синхронно грузит его прямо из тика мегаструктуры → исключение, сервер падает.
 * Обычные блоки TBS и так пишет в секцию без onPlace (как генерация мира); здесь то же для блоков с блок-сущностью, но ТОЛЬКО
 * если в 2 блоках от позиции есть незагруженный чанк. Сам блок и его блок-сущность (с NBT из шаблона) ставятся как обычно —
 * ничего не теряется; onPlace Forge пропускает, пока поднят level.captureBlockSnapshots (так же Forge ставит блоки при снимках).
 * Обновление соседей TBS делает позже в своей пост-обработке (см. TorchesPostProcessEdgeMixin).
 */
@Pseudo
@Mixin(targets = "com.freefish.torchesbecomesunlight.server.gameframework.mega.MegaStructureTask$ChunkPlacementWriter", remap = false)
public abstract class TorchesChunkEdgePlaceMixin {
    @Redirect(method = "placeBlockInLevelChunk", remap = false, require = 1,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;m_6978_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;", remap = false))
    private BlockState mm_compat$placeWithoutOnPlaceAtEdge(LevelChunk chunk, BlockPos pos, BlockState state, boolean moving) {
        Level level = chunk.getLevel();
        if (!(level instanceof ServerLevel server) || level.captureBlockSnapshots || TorchesEdgeGuard.neighboursLoaded(server, pos))
            return chunk.setBlockState(pos, state, moving);
        int snapshots = level.capturedBlockSnapshots.size();
        level.captureBlockSnapshots = true;
        try {
            return chunk.setBlockState(pos, state, moving);
        } finally {
            level.captureBlockSnapshots = false;
            // если onRemove старого блока успел позвать level.setBlock, Forge записал снимок — убираем, снимки тут никто не применит
            while (level.capturedBlockSnapshots.size() > snapshots) level.capturedBlockSnapshots.remove(level.capturedBlockSnapshots.size() - 1);
            TorchesEdgeGuard.notePlaced(pos);
        }
    }
}
