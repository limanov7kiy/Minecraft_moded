/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/dungeon/DirectionalPiece.java; missing parts restored from Galacticraft Legacy
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
public abstract class DirectionalPiece extends DungeonPiece {

    private Direction direction;

    public DirectionalPiece(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
    }

    public DirectionalPiece(StructurePieceType type, DungeonConfiguration configuration, Direction direction, int genDepth, BoundingBox boundingBox) {
        super(type, configuration, genDepth, boundingBox);
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);

        tag.putInt("direction", this.direction.get3DDataValue());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("direction")) {
            this.direction = Direction.from3DDataValue(tag.getInt("direction"));
        } else {
            this.direction = Direction.NORTH;
        }
    }

    public DungeonPiece getCorridor(RandomSource rand, DungeonStart startPiece, int maxAttempts, boolean small, int genDepth) {
        Direction randomDir;
        int blockX;
        int blockZ;
        int sizeX;
        int sizeZ;
        boolean valid;
        int attempts = maxAttempts;
        int randDir = rand.nextInt(3);
        do {
            randomDir = Direction.from2DDataValue((getDirection().getOpposite().get2DDataValue() + 1 + randDir) % 4);
            BoundingBox extension =
                    getExtension(randomDir, this.configuration.getHallwayLengthMin() + rand.nextInt(this.configuration.getHallwayLengthMax() - this.configuration.getHallwayLengthMin()), 3);
            blockX = extension.minX();
            blockZ = extension.minZ();
            sizeX = extension.maxX() - extension.minX();
            sizeZ = extension.maxZ() - extension.minZ();
            valid = !startPiece.checkIntersection(extension);
            attempts--;
            randDir++;
        } while (!valid && attempts > 0);

        if (!valid) {
            return null;
        }

        return new Corridor(this.configuration, rand, blockX, blockZ, sizeX, small ? 3 : this.configuration.getHallwayHeight(), sizeZ, randomDir, genDepth);
    }
}