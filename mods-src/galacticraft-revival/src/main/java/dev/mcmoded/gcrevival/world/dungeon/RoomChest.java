/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/dungeon/RoomChest.java; missing parts restored from Galacticraft Legacy
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
import net.minecraft.world.level.block.entity.ChestBlockEntity;
public class RoomChest extends RoomEmpty {
    public RoomChest(CompoundTag tag) {
        super(ModRegistry.ROOM_CHEST.get(), tag);
    }

    public RoomChest(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction entranceDir, int genDepth) {
        super(ModRegistry.ROOM_CHEST.get(), configuration, rand, blockPosX, blockPosZ, sizeX, sizeY, sizeZ, entranceDir, genDepth);
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource rand, BoundingBox boundingBox, ChunkPos pos, BlockPos pivot) {
        super.postProcess(worldIn, structureManager, chunkGenerator, rand, boundingBox, pos, pivot);
//        if (super.postProcess(worldIn, structureManager, chunkGenerator, rand, boundingBox, pos, pivot)) {
        int chestX = this.sizeX / 2;
        int chestY = 1;
        int chestZ = this.sizeZ / 2;
        this.placeBlock(worldIn, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, this.getDirection().getOpposite()), chestX, chestY, chestZ, boundingBox);

        BlockPos blockpos = new BlockPos(this.getWorldX(chestX, chestZ), this.getWorldY(chestY), this.getWorldZ(chestX, chestZ));
        ChestBlockEntity chest = (ChestBlockEntity) worldIn.getBlockEntity(blockpos);

        if (chest != null) {
            ResourceLocation chesttype = RoomTreasure.MOONCHEST;
//                if (worldIn.provider instanceof IGalacticraftWorldProvider) {
//                    chesttype = ((IGalacticraftWorldProvider) worldIn.provider).getDungeonChestType();
//                }
            chest.setLootTable(chesttype, rand.nextLong());
        }

//        }
    }
}