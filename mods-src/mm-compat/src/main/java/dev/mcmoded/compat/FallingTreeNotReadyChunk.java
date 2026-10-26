package dev.mcmoded.compat;

import fr.rakambda.fallingtree.common.wrapper.IChunk;
import fr.rakambda.fallingtree.common.wrapper.IChunkPos;

/** Чанк Falling Tree, который ещё не готов: только координаты, hasChunk по ним — false (см. FallingTreeLeafChunkMixin). */
public record FallingTreeNotReadyChunk(int x, int z) implements IChunk, IChunkPos {
    @Override public IChunkPos getPos() { return this; }
    @Override public int getX() { return x; }
    @Override public int getZ() { return z; }
    @Override public Object getRaw() { return null; }
}
