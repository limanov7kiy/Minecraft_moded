/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/dungeon/RoomTreasure.java; missing parts restored from Galacticraft Legacy
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
import dev.mcmoded.gcrevival.block.TreasureChestBlock;
import dev.mcmoded.gcrevival.block.TreasureChestBlockEntity;
public class RoomTreasure extends SizedPiece {

    /** Обычные сундуки данжа (комнаты с сундуком). */
    public static final ResourceLocation MOONCHEST = GCRevival.id("chests/moon_dungeon");
    /** Сокровищница босса: заполняется при его смерти (Legacy EntityBossBase). */
    public static final ResourceLocation TREASURE_T1 = GCRevival.id("chests/dungeon_tier_1");

    public RoomTreasure(CompoundTag tag) {
        super(ModRegistry.ROOM_TREASURE.get(), tag);
    }

    public RoomTreasure(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, Direction entranceDir, int genDepth) {
        this(configuration, rand, blockPosX, blockPosZ, rand.nextInt(4) + 6, configuration.getRoomHeight(), rand.nextInt(4) + 6, entranceDir, genDepth);
    }

    public RoomTreasure(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction entranceDir, int genDepth) {
        super(ModRegistry.ROOM_TREASURE.get(), configuration, sizeX, sizeY, sizeZ, entranceDir.getOpposite(), genDepth, new BoundingBox(blockPosX, configuration.getYPosition(), blockPosZ, blockPosX + sizeX, configuration.getYPosition() + sizeY, blockPosZ + sizeZ));
        this.setOrientation(Direction.SOUTH);
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
                        this.placeBlock(worldIn, Blocks.GLOWSTONE.defaultBlockState(), x, y, z, chunkBox);
                    } else if (x == this.sizeX / 2 && y == 1 && z == this.sizeZ / 2) {
                        BlockPos blockpos = new BlockPos(this.getWorldX(x, z), this.getWorldY(y), this.getWorldZ(x, z));
                        if (chunkBox.isInside(blockpos)) {
                            // Сундук сокровищ T1 (Legacy RoomTreasure: treasureChestTier1 + TABLE_TIER_1_DUNGEON)
                            worldIn.setBlock(blockpos, ModRegistry.TREASURE_CHEST_T1.get().defaultBlockState()
                                    .setValue(TreasureChestBlock.FACING, this.getDirection().getOpposite()), Block.UPDATE_CLIENTS);
                            if (worldIn.getBlockEntity(blockpos) instanceof TreasureChestBlockEntity chest) {
                                chest.setLootTable(TREASURE_T1, random.nextLong());
                            }
                        }
                    } else {
                        this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), x, y, z, chunkBox);
                    }
                }
            }
        }
    }

    @Override
    public DungeonPiece getNextPiece(DungeonStart startPiece, RandomSource rand) {
        if (startPiece.attachedComponents.size() > 2) {
            StructurePiece component = startPiece.attachedComponents.get(startPiece.attachedComponents.size() - 3);
            if (component instanceof RoomBoss) {
                BlockPos blockpos = new BlockPos(this.getWorldX(this.sizeX / 2, this.sizeZ / 2), this.getWorldY(1), this.getWorldZ(this.sizeX / 2, this.sizeZ / 2));
                ((RoomBoss) component).setChestPos(new BlockPos(blockpos));
            }
        }
        return null;
    }
}
