/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/GCStructurePieceTypes.java (GCRoomPieceType). See CREDITS.md.
 */
package dev.mcmoded.gcrevival.world.dungeon;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.function.Function;

/** Тип комнаты, которую коридор может создать по ссылке из DungeonConfiguration (комната босса, сокровищница). */
public class RoomPieceType implements StructurePieceType {
    public interface Factory {
        SizedPiece create(DungeonConfiguration configuration, RandomSource random, int blockPosX, int blockPosZ, Direction direction, int genDepth);
    }

    public interface SizedFactory {
        SizedPiece create(DungeonConfiguration configuration, RandomSource random, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction direction, int genDepth);
    }

    private final Function<CompoundTag, StructurePiece> loader;
    private final Factory factory;
    private final SizedFactory sizedFactory;

    public RoomPieceType(Function<CompoundTag, StructurePiece> loader, Factory factory, SizedFactory sizedFactory) {
        this.loader = loader;
        this.factory = factory;
        this.sizedFactory = sizedFactory;
    }

    public SizedPiece create(DungeonConfiguration configuration, RandomSource random, int blockPosX, int blockPosZ, Direction direction, int genDepth) {
        return factory.create(configuration, random, blockPosX, blockPosZ, direction, genDepth);
    }

    public SizedPiece create(DungeonConfiguration configuration, RandomSource random, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction direction, int genDepth) {
        return sizedFactory.create(configuration, random, blockPosX, blockPosZ, sizeX, sizeY, sizeZ, direction, genDepth);
    }

    @Override
    public StructurePiece load(StructurePieceSerializationContext context, CompoundTag tag) {
        return loader.apply(tag);
    }
}
