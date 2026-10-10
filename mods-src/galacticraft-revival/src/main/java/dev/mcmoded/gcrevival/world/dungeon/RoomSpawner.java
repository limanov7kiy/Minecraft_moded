/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/dungeon/RoomSpawner.java; missing parts restored from Galacticraft Legacy
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
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.entity.EntityType;
public class RoomSpawner extends RoomEmpty {
    public RoomSpawner(CompoundTag tag) {
        super(ModRegistry.ROOM_SPAWNER.get(), tag);
    }

    public RoomSpawner(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction entranceDir, int genDepth) {
        super(ModRegistry.ROOM_SPAWNER.get(), configuration, rand, blockPosX, blockPosZ, sizeX, sizeY, sizeZ, entranceDir, genDepth);
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos pos, BlockPos pivot) {
        super.postProcess(worldIn, structureManager, chunkGenerator, random, boundingBox, pos, pivot);
//        if (super.postProcess(worldIn, structureManager, chunkGenerator, random, boundingBox, pos, pivot)) {
        for (int i = 1; i <= this.sizeX - 1; ++i) {
            for (int j = 1; j <= this.sizeY - 1; ++j) {
                for (int k = 1; k <= this.sizeZ - 1; ++k) {
                    if (random.nextFloat() < 0.05F) {
                        this.placeBlock(worldIn, Blocks.COBWEB.defaultBlockState(), i, j, k, boundingBox);
                    }
                }
            }
        }

        this.placeBlock(worldIn, Blocks.SPAWNER.defaultBlockState(), 1, 0, 1, boundingBox);
        this.placeBlock(worldIn, Blocks.SPAWNER.defaultBlockState(), this.sizeX - 1, 0, this.sizeZ - 1, boundingBox);

        BlockPos blockpos = new BlockPos(this.getWorldX(1, 1), this.getWorldY(0), this.getWorldZ(1, 1));
        SpawnerBlockEntity spawner = worldIn.getBlockEntity(blockpos) instanceof SpawnerBlockEntity s ? s : null;

        if (spawner != null) {
            spawner.setEntityId(getMob(random), random);
        }

        blockpos = new BlockPos(this.getWorldX(this.sizeX - 1, this.sizeZ - 1), this.getWorldY(0), this.getWorldZ(this.sizeX - 1, this.sizeZ - 1));
        spawner = worldIn.getBlockEntity(blockpos) instanceof SpawnerBlockEntity s2 ? s2 : null;

        if (spawner != null) {
            spawner.setEntityId(getMob(random), random);
        }

//        }

    }

    private static EntityType<?> getMob(RandomSource rand) {
        switch (rand.nextInt(4)) {
            case 0:
                return ModRegistry.EVOLVED_SPIDER.get();
            case 1:
                return ModRegistry.EVOLVED_CREEPER.get();
            case 2:
                return ModRegistry.EVOLVED_SKELETON.get();
            case 3:
            default:
                return ModRegistry.EVOLVED_ZOMBIE.get();
        }
    }
}