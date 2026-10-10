/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/dungeon/RoomEmpty.java; missing parts restored from Galacticraft Legacy
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
public class RoomEmpty extends SizedPiece {
    public RoomEmpty(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
    }

    public RoomEmpty(StructurePieceType type, DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction entranceDir, int genDepth) {
        super(type, configuration, sizeX, sizeY, sizeZ, entranceDir.getOpposite(), genDepth, new BoundingBox(blockPosX, configuration.getYPosition(), blockPosZ, blockPosX + sizeX, configuration.getYPosition() + sizeY, blockPosZ + sizeZ));
        this.setOrientation(Direction.SOUTH);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public RoomEmpty(CompoundTag tag) {
        this(ModRegistry.ROOM_EMPTY.get(), tag);
    }

    public RoomEmpty(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction entranceDir, int genDepth) {
        this(ModRegistry.ROOM_EMPTY.get(), configuration, rand, blockPosX, blockPosZ, sizeX, sizeY, sizeZ, entranceDir, genDepth);
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox boundingBox, ChunkPos pos, BlockPos pivot) {
        for (int x = 0; x <= this.sizeX; x++) {
            for (int y = 0; y <= this.sizeY; y++) {
                for (int z = 0; z <= this.sizeZ; z++) {
                    if (x == 0 || x == this.sizeX || y == 0 || y == this.sizeY || z == 0 || z == this.sizeZ) {
                        boolean placeBlock = true;
                        if (getDirection().getAxis() == Direction.Axis.Z) {
                            int start = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - 1;
                            int end = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 + 1;
                            if (x > start && x <= end && y < this.configuration.getHallwayHeight() && y > 0) {
                                if (getDirection() == Direction.SOUTH && z == 0) {
                                    placeBlock = false;
                                } else if (getDirection() == Direction.NORTH && z == this.sizeZ) {
                                    placeBlock = false;
                                }
                            }
                        } else {
                            int start = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - 1;
                            int end = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 + 1;
                            if (z > start && z <= end && y < this.configuration.getHallwayHeight() && y > 0) {
                                if (getDirection() == Direction.EAST && x == 0) {
                                    placeBlock = false;
                                } else if (getDirection() == Direction.WEST && x == this.sizeX) {
                                    placeBlock = false;
                                }
                            }
                        }
                        if (placeBlock) {
                            this.placeBlock(worldIn, this.configuration.getBrickBlock(), x, y, z, boundingBox);
                        } else {
                            this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), x, y, z, boundingBox);
                        }
                    } else {
                        this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), x, y, z, boundingBox);
                    }
                }
            }
        }
    }

    @Override
    public DungeonPiece getNextPiece(DungeonStart startPiece, RandomSource rand) {
        if (Math.abs(startPiece.getBoundingBox().maxZ() - boundingBox.minZ()) > 200) {
            return null;
        }

        if (Math.abs(startPiece.getBoundingBox().maxX() - boundingBox.minX()) > 200) {
            return null;
        }

        return getCorridor(rand, startPiece, 10, false, getGenDepth() + 1);
    }
}