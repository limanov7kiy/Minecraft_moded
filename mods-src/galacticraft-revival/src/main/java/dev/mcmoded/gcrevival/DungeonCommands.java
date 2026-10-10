package dev.mcmoded.gcrevival;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.mcmoded.gcrevival.world.dungeon.RoomBoss;
import dev.mcmoded.gcrevival.world.dungeon.RoomTreasure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

/**
 * /gcrevival place_dungeon [pos] — ставит лунный данж GC в текущем измерении, сама прогружая нужные чанки
 * (ванильный /place structure требует, чтобы все чанки уже были загружены, а данж тянется на ~200 блоков).
 * Пишет координаты призывателя босса и сундука сокровищ; их же читает серверный тест scripts/tests/gcdungeon.js.
 */
public final class DungeonCommands {
    public static final ResourceKey<Structure> MOON_DUNGEON = ResourceKey.create(Registries.STRUCTURE, GCRevival.id("moon_dungeon"));
    private static final SimpleCommandExceptionType FAILED = new SimpleCommandExceptionType(Component.literal("Dungeon generation failed"));

    /** Последний поставленный данж (для серверного теста). */
    public static BlockPos lastBossSpawner;
    public static BlockPos lastTreasureChest;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("gcrevival").requires(s -> s.hasPermission(2))
                .then(Commands.literal("place_dungeon")
                        .executes(ctx -> place(ctx.getSource(), BlockPos.containing(ctx.getSource().getPosition())))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> place(ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "pos"))))));
    }

    private static int place(CommandSourceStack source, BlockPos pos) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerLevel level = source.getLevel();
        Structure structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(MOON_DUNGEON);
        if (structure == null) throw FAILED.create();
        var generator = level.getChunkSource().getGenerator();
        StructureStart start = structure.generate(level.registryAccess(), generator, generator.getBiomeSource(),
                level.getChunkSource().randomState(), level.getStructureManager(), level.getSeed(), new ChunkPos(pos), 0, level, b -> true);
        if (!start.isValid()) throw FAILED.create();

        BoundingBox box = start.getBoundingBox();
        ChunkPos min = new ChunkPos(box.minX() >> 4, box.minZ() >> 4);
        ChunkPos max = new ChunkPos(box.maxX() >> 4, box.maxZ() >> 4);
        ChunkPos.rangeClosed(min, max).forEach(cp -> {
            level.getChunk(cp.x, cp.z);
            start.placeInChunk(level, level.structureManager(), generator, level.getRandom(),
                    new BoundingBox(cp.getMinBlockX(), level.getMinBuildHeight(), cp.getMinBlockZ(), cp.getMaxBlockX(), level.getMaxBuildHeight(), cp.getMaxBlockZ()), cp);
        });

        lastBossSpawner = null;
        lastTreasureChest = null;
        for (StructurePiece piece : start.getPieces()) {
            if (piece instanceof RoomBoss boss) {
                lastBossSpawner = boss.getSpawnerPos();
                lastTreasureChest = boss.getChestPos();
            }
        }
        BlockPos spawner = lastBossSpawner, chest = lastTreasureChest;
        source.sendSuccess(() -> Component.literal("Moon dungeon placed: " + start.getPieces().size() + " pieces, boss spawner " + spawner
                + ", treasure chest " + chest), true);
        return start.getPieces().size();
    }

    /** Ближайший естественный данж (синхронно; ванильный /locate в сборке асинхронный из-за Async Locator). */
    @javax.annotation.Nullable
    public static BlockPos findNearest(ServerLevel level, BlockPos from, int radiusChunks) {
        var holder = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolder(MOON_DUNGEON);
        if (holder.isEmpty()) return null;
        var found = level.getChunkSource().getGenerator().findNearestMapStructure(level, net.minecraft.core.HolderSet.direct(holder.get()), from, radiusChunks, false);
        return found == null ? null : found.getFirst();
    }

    private DungeonCommands() {
    }
}
