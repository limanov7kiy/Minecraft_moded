/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/dungeon/RoomBoss.java; missing parts restored from Galacticraft Legacy
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
import dev.mcmoded.gcrevival.block.DungeonSpawnerBlockEntity;
public class RoomBoss extends SizedPiece {
    private BlockPos chestPos;

    public RoomBoss(CompoundTag tag) {
        super(ModRegistry.ROOM_BOSS.get(), tag);
    }

    public RoomBoss(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, Direction entranceDir, int genDepth) {
        this(configuration, rand, blockPosX, blockPosZ, rand.nextInt(6) + 14, rand.nextInt(2) + 8, rand.nextInt(6) + 14, entranceDir, genDepth);
    }

    public RoomBoss(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction entranceDir, int genDepth) {
        super(ModRegistry.ROOM_BOSS.get(), configuration, sizeX, sizeY, sizeZ, entranceDir.getOpposite(), genDepth, new BoundingBox(blockPosX, configuration.getYPosition(), blockPosZ, blockPosX + sizeX, configuration.getYPosition() + sizeY, blockPosZ + sizeZ));
        this.setOrientation(Direction.SOUTH);
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
        this.sizeY = sizeY;
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox chunkBox, ChunkPos pos, BlockPos pivot) {
        for (int x = 0; x <= this.sizeX; x++) {
            for (int y = 0; y <= this.sizeY; y++) {
                for (int z = 0; z <= this.sizeZ; z++) {
                    if (x == 0 || x == this.sizeX || y == 0 || y == this.sizeY || z == 0 || z == this.sizeZ) {
                        boolean placeBlock = true;
                        if (getDirection().getAxis() == Direction.Axis.Z) {
                            int start = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - 1;
                            int end = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 + 1;
                            if (x > start && x <= end && y < 3 && y > 0) {
                                if (getDirection() == Direction.SOUTH && z == 0) {
                                    placeBlock = false;
                                } else if (getDirection() == Direction.NORTH && z == this.sizeZ) {
                                    placeBlock = false;
                                }
                            }
                        } else {
                            int start = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - 1;
                            int end = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 + 1;
                            if (z > start && z <= end && y < 3 && y > 0) {
                                if (getDirection() == Direction.EAST && x == 0) {
                                    placeBlock = false;
                                } else if (getDirection() == Direction.WEST && x == this.sizeX) {
                                    placeBlock = false;
                                }
                            }
                        }
                        if (placeBlock) {
                            this.placeBlock(worldIn, this.configuration.getBrickBlock(), x, y, z, chunkBox);
                        } else {
                            this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), x, y, z, chunkBox);
                        }
                    } else if ((x == 1 && z == 1) || (x == 1 && z == this.sizeZ - 1) || (x == this.sizeX - 1 && z == 1) || (x == this.sizeX - 1 && z == this.sizeZ - 1)) {
                        this.placeBlock(worldIn, Blocks.LAVA.defaultBlockState(), x, y, z, chunkBox);
                    } else if (y % 3 == 0 && y >= 2 && ((x == 1 || x == this.sizeX - 1 || z == 1 || z == this.sizeZ - 1) || (x == 2 && z == 2) || (x == 2 && z == this.sizeZ - 2) || (x == this.sizeX - 2 && z == 2) || (x == this.sizeX - 2 && z == this.sizeZ - 2))) {
                        // Horizontal bars
                        this.placeBlock(worldIn, Blocks.IRON_BARS.defaultBlockState(), x, y, z, chunkBox);
                    } else if ((x == 1 && z == 2) || (x == 2 && z == 1) ||
                            (x == 1 && z == this.sizeZ - 2) || (x == 2 && z == this.sizeZ - 1) ||
                            (x == this.sizeX - 1 && z == 2) || (x == this.sizeX - 2 && z == 1) ||
                            (x == this.sizeX - 1 && z == this.sizeZ - 2) || (x == this.sizeX - 2 && z == this.sizeZ - 1)) {
                        // Vertical bars
                        this.placeBlock(worldIn, Blocks.IRON_BARS.defaultBlockState(), x, y, z, chunkBox);
                    } else {
                        this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), x, y, z, chunkBox);
                    }
                }
            }
        }

        int spawnerX = this.sizeX / 2;
        int spawnerY = 1;
        int spawnerZ = this.sizeZ / 2;
        BlockPos blockpos = new BlockPos(this.getWorldX(spawnerX, spawnerZ), this.getWorldY(spawnerY), this.getWorldZ(spawnerX, spawnerZ));
        //Is this position inside the chunk currently being generated?
        if (chunkBox.isInside(blockpos)) {
            worldIn.setBlock(blockpos, ModRegistry.BOSS_SPAWNER.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            DungeonSpawnerBlockEntity spawner = (DungeonSpawnerBlockEntity) worldIn.getBlockEntity(blockpos);
            if (spawner != null) {
                spawner.setEntityId(ModRegistry.SKELETON_BOSS.get(), random);
                spawner.setRoom(new Vec3i(this.boundingBox.minX() + 1, this.boundingBox.minY() + 1, this.boundingBox.minZ() + 1), new Vec3i(this.sizeX - 1, this.sizeY - 1, this.sizeZ - 1));
                spawner.setChestPos(this.chestPos);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);

        if (this.chestPos != null) {
            tag.putInt("chestX", this.chestPos.getX());
            tag.putInt("chestY", this.chestPos.getY());
            tag.putInt("chestZ", this.chestPos.getZ());
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.chestPos = new BlockPos(tag.getInt("chestX"), tag.getInt("chestY"), tag.getInt("chestZ"));
    }

    @Override
    public DungeonPiece getNextPiece(DungeonStart startPiece, RandomSource rand) {
        return getCorridor(rand, startPiece, 10, true, getGenDepth() + 1);
    }

    public BlockPos getSpawnerPos() {
        return new BlockPos(this.getWorldX(this.sizeX / 2, this.sizeZ / 2), this.getWorldY(1), this.getWorldZ(this.sizeX / 2, this.sizeZ / 2));
    }

    public BlockPos getChestPos() {
        return chestPos;
    }

    public void setChestPos(BlockPos chestPos) {
        this.chestPos = chestPos;
    }
}
