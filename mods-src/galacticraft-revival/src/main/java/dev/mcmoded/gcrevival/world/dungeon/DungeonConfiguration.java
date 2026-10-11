/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/dungeon/DungeonConfiguration.java. See CREDITS.md.
 */
package dev.mcmoded.gcrevival.world.dungeon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mcmoded.gcrevival.GCRevival;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class DungeonConfiguration {
    public static final Codec<DungeonConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("brickBlock").forGetter(DungeonConfiguration::getBrickBlock),
            Codec.INT.fieldOf("yPosition").forGetter(DungeonConfiguration::getYPosition),
            Codec.INT.fieldOf("hallwayLengthMin").forGetter(DungeonConfiguration::getHallwayLengthMin),
            Codec.INT.fieldOf("hallwayLengthMax").forGetter(DungeonConfiguration::getHallwayLengthMax),
            Codec.INT.fieldOf("hallwayHeight").forGetter(DungeonConfiguration::getHallwayHeight),
            Codec.INT.fieldOf("roomHeight").forGetter(DungeonConfiguration::getRoomHeight),
            BuiltInRegistries.STRUCTURE_PIECE.byNameCodec().fieldOf("bossRoom").forGetter(DungeonConfiguration::getBossRoom),
            BuiltInRegistries.STRUCTURE_PIECE.byNameCodec().fieldOf("treasureRoom").forGetter(DungeonConfiguration::getTreasureRoom)
    ).apply(instance, DungeonConfiguration::new));

    private BlockState brickBlock;
    private int yPosition;
    private int hallwayLengthMin;
    private int hallwayLengthMax;
    private int hallwayHeight;
    private int roomHeight;
    private StructurePieceType bossRoom;
    private StructurePieceType treasureRoom;

    public DungeonConfiguration() {
    }

    public DungeonConfiguration(BlockState brickBlock, int yPosition, int hallwayLengthMin, int hallwayLengthMax, int hallwayHeight, int roomHeight, StructurePieceType bossRoom, StructurePieceType treasureRoom) {
        this.brickBlock = brickBlock;
        this.yPosition = yPosition;
        this.hallwayLengthMin = hallwayLengthMin;
        this.hallwayLengthMax = hallwayLengthMax;
        this.hallwayHeight = hallwayHeight;
        this.roomHeight = roomHeight;
        this.bossRoom = bossRoom;
        this.treasureRoom = treasureRoom;
    }

    public CompoundTag write(CompoundTag tag) {
        BlockState.CODEC.encodeStart(NbtOps.INSTANCE, this.brickBlock).result().ifPresent(t -> tag.put("brickBlock", t));
        tag.putInt("yPosition", this.yPosition);
        tag.putInt("hallwayLengthMin", this.hallwayLengthMin);
        tag.putInt("hallwayLengthMax", this.hallwayLengthMax);
        tag.putInt("hallwayHeight", this.hallwayHeight);
        tag.putInt("roomHeight", this.roomHeight);
        tag.putString("bossRoom", String.valueOf(BuiltInRegistries.STRUCTURE_PIECE.getKey(this.bossRoom)));
        tag.putString("treasureRoom", String.valueOf(BuiltInRegistries.STRUCTURE_PIECE.getKey(this.treasureRoom)));
        return tag;
    }

    public void read(CompoundTag tag) {
        this.brickBlock = BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("brickBlock")).result().orElseGet(() -> {
            GCRevival.LOGGER.error("Failed to read dungeon brick block from NBT");
            return Blocks.STONE_BRICKS.defaultBlockState();
        });
        this.yPosition = tag.getInt("yPosition");
        this.hallwayLengthMin = tag.getInt("hallwayLengthMin");
        this.hallwayLengthMax = tag.getInt("hallwayLengthMax");
        this.hallwayHeight = tag.getInt("hallwayHeight");
        this.roomHeight = tag.getInt("roomHeight");
        this.bossRoom = BuiltInRegistries.STRUCTURE_PIECE.get(ResourceLocation.tryParse(tag.getString("bossRoom")));
        this.treasureRoom = BuiltInRegistries.STRUCTURE_PIECE.get(ResourceLocation.tryParse(tag.getString("treasureRoom")));
    }

    public BlockState getBrickBlock() { return brickBlock; }
    public int getYPosition() { return yPosition; }
    public int getHallwayLengthMin() { return hallwayLengthMin; }
    public int getHallwayLengthMax() { return hallwayLengthMax; }
    public int getHallwayHeight() { return hallwayHeight; }
    public int getRoomHeight() { return roomHeight; }
    public StructurePieceType getBossRoom() { return bossRoom; }
    public StructurePieceType getTreasureRoom() { return treasureRoom; }
}
