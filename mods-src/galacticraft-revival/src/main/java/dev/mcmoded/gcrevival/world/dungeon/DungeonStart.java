/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/dungeon/DungeonStart.java; missing parts restored from Galacticraft Legacy
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
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import com.google.common.collect.Lists;
import java.util.List;
public class DungeonStart extends EntranceCrater {

    public List<StructurePiece> attachedComponents = Lists.newArrayList();
    public List<BoundingBox> componentBounds = Lists.newArrayList();

    public DungeonStart(CompoundTag tag) {
        super(ModRegistry.DUNGEON_START.get(), tag);
    }

    public DungeonStart(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int genDepth) {
        super(ModRegistry.DUNGEON_START.get(), configuration, rand, blockPosX, blockPosZ, genDepth);
    }

    @Override
    public void addChildren(StructurePiece componentIn, StructurePieceAccessor listIn, RandomSource rand) {
        boolean validAttempt = false;
        final int maxAttempts = 10;
        int attempts = 0;
        while (!validAttempt && attempts < maxAttempts) {
            attachedComponents.clear();
            componentBounds.clear();
            componentBounds.add(this.boundingBox);
            DungeonPiece next = getNextPiece(this, rand);
            while (next != null) {
                attachedComponents.add(next);
                componentBounds.add(next.getBoundingBox());
                next = next.getNextPiece(this, rand);
            }
            if (attachedComponents.size() >= 3 && attachedComponents.get(attachedComponents.size() - 1) instanceof RoomTreasure
                    && attachedComponents.get(attachedComponents.size() - 3) instanceof RoomBoss) {
                validAttempt = true;
            }
            attempts++;
        }

        GCRevival.LOGGER.debug("Dungeon generation took {} attempt(s)", attempts);

        if (!validAttempt) {
            int xPos = this.boundingBox.minX() + (this.boundingBox.maxX() - this.boundingBox.minX()) / 2;
            int zPos = this.boundingBox.minZ() + (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2;
            GCRevival.LOGGER.warn("Could not find a valid moon dungeon layout at ({}, {}), dungeon skipped", xPos, zPos);
            attachedComponents.clear();
            return;
        }
        for (StructurePiece piece : attachedComponents) {
            listIn.addPiece(piece);
        }
    }

    public boolean checkIntersection(BoundingBox bounds) {
        for (int i = 0; i < componentBounds.size() - 1; ++i) {
            BoundingBox boundingBox = componentBounds.get(i);
            if (boundingBox.intersects(bounds)) {
                return true;
            }
        }

        return false;
    }
}
