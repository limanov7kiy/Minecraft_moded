package dev.mcmoded.compat.mixin;

import fr.rakambda.fallingtree.common.wrapper.IBlockPos;
import fr.rakambda.fallingtree.common.wrapper.IChunk;
import fr.rakambda.fallingtree.common.wrapper.IServerLevel;
import dev.mcmoded.compat.FallingTreeNotReadyChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Falling Tree 4.3.4 (RakSrinaNa, LGPL-3.0): LeafBreakingHandler.onBlockUpdate на каждое обновление соседа берёт
 * level.getChunk(сосед) и только потом проверяет level.hasChunk(x, z). getChunk синхронно грузит/генерирует чанк, если
 * блок обновился на краю загруженной области (на тестовом сервере — Chaos Persists ставит блоки у края генерации,
 * поток 67: половина синхронных загрузок в тике входа игроков). Если чанк соседа ещё не FULL, отдаём «пустой» IChunk
 * с теми же координатами: следующая проверка hasChunk даёт false, и мод пропускает соседа — как и задумано.
 */
@Pseudo
@Mixin(targets = "fr.rakambda.fallingtree.common.leaf.LeafBreakingHandler", remap = false)
public abstract class FallingTreeLeafChunkMixin {
    @Redirect(method = "onBlockUpdate", remap = false, require = 1,
        at = @At(value = "INVOKE", target = "Lfr/rakambda/fallingtree/common/wrapper/IServerLevel;getChunk(Lfr/rakambda/fallingtree/common/wrapper/IBlockPos;)Lfr/rakambda/fallingtree/common/wrapper/IChunk;", remap = false))
    private IChunk mm_compat$chunkIfReady(IServerLevel level, IBlockPos pos) {
        if (level.getRaw() instanceof ServerLevel server && pos.getRaw() instanceof BlockPos raw) {
            int cx = raw.getX() >> 4, cz = raw.getZ() >> 4;
            if (server.getChunkSource().getChunkNow(cx, cz) == null) return new FallingTreeNotReadyChunk(cx, cz);
        }
        return level.getChunk(pos);
    }
}
