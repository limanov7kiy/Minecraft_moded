package dev.mcmoded.compat.mixin;

import dev.mcmoded.compat.TorchesEdgeGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Torches Become Sunlight 0.5.0-hotfix2 (FreeFish и др.), поток 76: пост-обработка мегаструктуры (MegaStructureManager.runPostProcessRepair)
 * после постановки блока зовёт level.blockUpdated(pos) (updateNeighborsAt — без проверки загрузки) и level.updateNeighbourForOutputSignal(pos)
 * (Forge читает блок через два от позиции без проверки), а затем сама обходит 6 соседей с проверкой level.isLoaded. На краю загруженной
 * области первые два вызова — та же синхронная загрузка соседнего чанка, что и в onPlace («Chunk not there when requested»).
 * Если в 2 блоках от позиции есть чанк, который ещё не FULL, эти два вызова пропускаются; обход соседей с проверкой у TBS остаётся,
 * так что соседи в загруженных чанках обновляются как раньше (blockUpdated их и так дублирует). Вдали от края — без изменений.
 */
@Pseudo
@Mixin(targets = "com.freefish.torchesbecomesunlight.server.gameframework.mega.MegaStructureManager", remap = false)
public abstract class TorchesPostProcessEdgeMixin {
    @Redirect(method = "runPostProcessRepair", remap = false, require = 1,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;m_6289_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V", remap = false))
    private void mm_compat$blockUpdatedIfLoaded(ServerLevel level, BlockPos pos, Block block) {
        if (TorchesEdgeGuard.neighboursLoaded(level, pos)) level.blockUpdated(pos, block);
        else TorchesEdgeGuard.noteRepaired(pos);
    }

    @Redirect(method = "runPostProcessRepair", remap = false, require = 1,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;m_46672_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V", remap = false))
    private void mm_compat$outputSignalIfLoaded(ServerLevel level, BlockPos pos, Block block) {
        if (TorchesEdgeGuard.neighboursLoaded(level, pos)) level.updateNeighbourForOutputSignal(pos, block);
    }
}
