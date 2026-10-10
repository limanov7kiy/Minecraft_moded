package dev.mcmoded.wizardryrestored.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/// Временные блоки заклинаний (лёд барьеров, магический свет): ставятся только в воздух и убираются по таймеру,
/// если на их месте всё ещё стоит тот же блок. При остановке сервера убираются сразу, чтобы не оставаться в мире.
public final class TempBlocks {
    private record Entry(ResourceKey<Level> dim, BlockPos pos, BlockState state, long expiresAt) {
    }

    private static final List<Entry> ENTRIES = new ArrayList<>();

    private TempBlocks() {
    }

    public static boolean place(ServerLevel level, BlockPos pos, BlockState state, int lifetimeTicks) {
        BlockState current = level.getBlockState(pos);
        if (!current.isAir() && !current.canBeReplaced()) return false;
        if (!current.getFluidState().isEmpty()) return false;
        level.setBlockAndUpdate(pos, state);
        ENTRIES.add(new Entry(level.dimension(), pos.immutable(), state, level.getGameTime() + lifetimeTicks));
        return true;
    }

    private static void remove(MinecraftServer server, Entry e) {
        ServerLevel level = server.getLevel(e.dim());
        if (level != null && level.getBlockState(e.pos()) == e.state()) level.setBlockAndUpdate(e.pos(), Blocks.AIR.defaultBlockState());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ENTRIES.isEmpty()) return;
        MinecraftServer server = event.getServer();
        long now = server.overworld().getGameTime();
        for (Iterator<Entry> it = ENTRIES.iterator(); it.hasNext(); ) {
            Entry e = it.next();
            ServerLevel level = server.getLevel(e.dim());
            long t = level != null ? level.getGameTime() : now;
            if (t >= e.expiresAt()) {
                remove(server, e);
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ENTRIES.forEach(e -> remove(event.getServer(), e));
        ENTRIES.clear();
    }
}
