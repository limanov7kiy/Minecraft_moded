/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/dungeon/DungeonPiece.java; missing parts restored from Galacticraft Legacy
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
public abstract class DungeonPiece extends StructurePiece {
    protected DungeonConfiguration configuration;

    public DungeonPiece(StructurePieceType type, DungeonConfiguration configuration, int genDepth, BoundingBox boundingBox) {
        super(type, genDepth, boundingBox);
        this.configuration = configuration;
    }

    public DungeonPiece(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
        readAdditionalSaveData(tag);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        this.configuration.write(tag);
    }

    protected void readAdditionalSaveData(CompoundTag tag) {
        if (this.configuration == null) {
            this.configuration = new DungeonConfiguration();
            this.configuration.read(tag);
        }
    }

    protected BoundingBox getExtension(Direction direction, int length, int width) {
        int blockX, blockZ, sizeX, sizeZ;
        switch (direction) {
            case NORTH:
                sizeX = width;
                sizeZ = length;
                blockX = this.boundingBox.minX() + (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - sizeX / 2;
                blockZ = this.boundingBox.minZ() - sizeZ;
                break;
            case EAST:
                sizeX = length;
                sizeZ = width;
                blockX = this.boundingBox.maxX();
                blockZ = this.boundingBox.minZ() + (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - sizeZ / 2;
                break;
            case SOUTH:
                sizeX = width;
                sizeZ = length;
                blockX = this.boundingBox.minX() + (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - sizeX / 2;
                blockZ = this.boundingBox.maxZ();
                break;
            case WEST:
            default:
                sizeX = length;
                sizeZ = width;
                blockX = this.boundingBox.minX() - sizeX;
                blockZ = this.boundingBox.minZ() + (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - sizeZ / 2;
                break;
        }
        return new BoundingBox(blockX, this.configuration.getYPosition(), blockZ, blockX + sizeX, this.configuration.getYPosition() + this.configuration.getHallwayHeight(), blockZ + sizeZ);
    }

    public DungeonPiece getNextPiece(DungeonStart startPiece, RandomSource random) {
        return null;
    }
}
