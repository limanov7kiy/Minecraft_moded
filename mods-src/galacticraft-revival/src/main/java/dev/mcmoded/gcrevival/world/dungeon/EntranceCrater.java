/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/dungeon/EntranceCrater.java; missing parts restored from Galacticraft Legacy
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
public class EntranceCrater extends SizedPiece {

    private static final int RANGE = 16;

    public EntranceCrater(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
    }

    public EntranceCrater(StructurePieceType type, DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int genDepth) {
        super(type, configuration, rand.nextInt(4) + 6, 12, rand.nextInt(4) + 6, Direction.Plane.HORIZONTAL.getRandomDirection(rand), genDepth, new BoundingBox(blockPosX - RANGE, configuration.getYPosition() + 11, blockPosZ - RANGE, blockPosX + RANGE, 150, blockPosZ + RANGE));
        this.setOrientation(Direction.SOUTH);
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox structureBoundingBoxIn, ChunkPos chunkPos, BlockPos pivot) {
        BlockState block1;

        int maxLevel = 0;

        for (int i = -RANGE; i <= RANGE; i++) {
            for (int k = -RANGE; k <= RANGE; k++) {
                int j = 150;
                int x = this.getWorldX(i + RANGE, k + RANGE);
                int z = this.getWorldZ(i + RANGE, k + RANGE);

                while (j >= 0) {
                    j--;

                    int y = this.getWorldY(j);
                    BlockPos blockpos = new BlockPos(x, y, z);
                    Block block = worldIn.getBlockState(blockpos).getBlock();

                    if (Blocks.AIR != block) {
                        break;
                    }
                }

                maxLevel = Math.max(maxLevel, j + 3);
            }
        }

        Mirror mirror = Mirror.NONE;
        Rotation rotation = Rotation.NONE;
        if (this.getOrientation() != null) {
            switch (this.getOrientation()) {
                case SOUTH:
                    mirror = Mirror.LEFT_RIGHT;
                    break;
                case WEST:
                    mirror = Mirror.LEFT_RIGHT;
                    rotation = Rotation.CLOCKWISE_90;
                    break;
                case EAST:
                    rotation = Rotation.CLOCKWISE_90;
                    break;
                default:
                    break;
            }
        }

        for (int i = -RANGE; i < RANGE; i++) {
            for (int k = -RANGE; k < RANGE; k++) {
                final double xDev = i / 20D;
                final double zDev = k / 20D;
                final double distance = xDev * xDev + zDev * zDev;
                final int depth = (int) Math.abs(0.5 / (distance + .00001D));
                int helper = 0;
                for (int j = maxLevel; j > 1 && helper <= depth; j--) {
                    block1 = this.getBlock(worldIn, i + RANGE, j, k + RANGE, boundingBox);
//                    if (block1 == this.configuration.getBrickBlock() || j != this.sizeY)
                    {
                        BlockPos blockpos = new BlockPos(this.getWorldX(i + RANGE, k + RANGE), this.getWorldY(j), this.getWorldZ(i + RANGE, k + RANGE));
                        BlockState state = Blocks.AIR.defaultBlockState();

                        if (mirror != Mirror.NONE) {
                            state = state.mirror(mirror);
                        }

                        if (rotation != Rotation.NONE) {
                            state = state.rotate(rotation);
                        }

                        if (structureBoundingBoxIn.isInside(blockpos)) {
                            worldIn.setBlock(blockpos, state, Block.UPDATE_CLIENTS);
                        }
//                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i + range, j, k + range, boundingBox);
                        helper++;
                    }
                }
            }
        }
    }

    @Override
    public DungeonPiece getNextPiece(DungeonStart startPiece, RandomSource random) {
        return new RoomEntrance(this.configuration, random, this.boundingBox.minX() + this.boundingBox.getXSpan() / 2, this.boundingBox.minZ() + this.boundingBox.getZSpan() / 2, getGenDepth() + 1);
    }
}
