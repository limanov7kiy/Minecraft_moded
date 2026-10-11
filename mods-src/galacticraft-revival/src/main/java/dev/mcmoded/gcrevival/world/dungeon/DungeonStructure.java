/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/structure/dungeon/DungeonStructure.java. See CREDITS.md. Adapted to Minecraft 1.20.1 Forge.
 */
package dev.mcmoded.gcrevival.world.dungeon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mcmoded.gcrevival.ModRegistry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

/** Процедурный данж GC: воронка-вход, коридоры, комнаты со спаунерами и сундуками, комната босса, сокровищница. */
public class DungeonStructure extends Structure {
    public static final Codec<DungeonStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            settingsCodec(instance),
            DungeonConfiguration.CODEC.fieldOf("dungeon_configuration").forGetter(s -> s.configuration)
    ).apply(instance, DungeonStructure::new));

    private final DungeonConfiguration configuration;

    public DungeonStructure(StructureSettings settings, DungeonConfiguration configuration) {
        super(settings);
        this.configuration = configuration;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.OCEAN_FLOOR_WG, builder -> generatePieces(builder, context));
    }

    private void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        WorldgenRandom random = context.random();
        DungeonStart start = new DungeonStart(configuration, random, chunkPos.getMinBlockX() + 2, chunkPos.getMinBlockZ() + 2, 0);
        start.addChildren(start, builder, random);
        if (!start.attachedComponents.isEmpty()) {
            builder.addPiece(start);
        }
    }

    @Override
    public StructureType<?> type() {
        return ModRegistry.MOON_DUNGEON.get();
    }
}
