/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/dungeon/RoomEntrance.java; missing parts restored from Galacticraft Legacy
 * (micdoodle8, radfast, Team Galacticraft), see CREDITS.md. Adapted to Minecraft 1.20.1 Forge.
 */
package dev.mcmoded.gcrevival.world.dungeon;

import dev.mcmoded.gcrevival.GCRevival;
import dev.mcmoded.gcrevival.ModRegistry;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
public class RoomEntrance extends SizedPiece {
    public RoomEntrance(CompoundTag tag) {
        super(ModRegistry.ROOM_ENTRANCE.get(), tag);
    }

    public RoomEntrance(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int genDepth) {
        super(ModRegistry.ROOM_ENTRANCE.get(), configuration, rand.nextInt(4) + 6, 12, rand.nextInt(4) + 6, Direction.Plane.HORIZONTAL.getRandomDirection(rand), genDepth, null);
        this.setOrientation(Direction.SOUTH);
        int sX = this.sizeX / 2;
        int sZ = this.sizeZ / 2;

        this.boundingBox = new BoundingBox(blockPosX - sX, configuration.getYPosition(), blockPosZ - sZ, blockPosX - sX + this.sizeX, configuration.getYPosition() + this.sizeY, blockPosZ - sZ + this.sizeZ);
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomIn, BoundingBox structureBoundingBoxIn, ChunkPos pos, BlockPos pivot) {
        for (int x = 0; x <= this.sizeX; x++) {
            for (int y = 0; y <= this.sizeY; y++) {
                for (int z = 0; z <= this.sizeZ; z++) {
                    if (x == 0 || x == this.sizeX || y == 0 /*|| j == this.sizeY*/ || z == 0 || z == this.sizeZ) {
                        this.placeBlock(worldIn, this.configuration.getBrickBlock(), x, y, z, boundingBox);
                    } else {
                        this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), x, y, z, boundingBox);
                    }
                }
            }
        }
    }

    @Override
    public DungeonPiece getNextPiece(DungeonStart startPiece, RandomSource rand) {
        return getCorridor(rand, startPiece, 10, false, getGenDepth() + 1);
    }
}