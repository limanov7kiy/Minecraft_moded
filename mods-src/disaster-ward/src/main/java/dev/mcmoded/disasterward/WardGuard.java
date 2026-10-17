package dev.mcmoded.disasterward;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * «Контекст бедствия»: пока тикает существо-бедствие (Natural Disasters!) или паразит (Spore), оно не меняет блоки внутри зоны.
 * Вход/выход — миксин на ServerLevel.tickNonPassenger/tickPassenger, проверка — миксин на Level.setBlock/destroyBlock.
 * Работает только в серверном потоке и только в измерениях, где стоит хоть один оберег.
 */
public final class WardGuard {
    public enum Kind { NONE, DISASTER, PARASITE }

    private static final Map<EntityType<?>, Kind> KINDS = new IdentityHashMap<>();
    private static final Entity[] STACK = new Entity[64];
    private static int depth;
    private static Entity current;
    private static Thread thread;
    /** Сколько изменений блоков в зоне отклонено (для теста). */
    public static long denied;

    private WardGuard() {}

    public static Kind kind(EntityType<?> type) {
        Kind k = KINDS.get(type);
        if (k == null) {
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
            k = Kind.NONE;
            if (id != null) {
                if (WardConfig.PROTECTED_NAMESPACES.get().contains(id.getNamespace())) k = Kind.DISASTER;
                else if (WardConfig.PARASITE_NAMESPACES.get().contains(id.getNamespace())) k = Kind.PARASITE;
            }
            synchronized (KINDS) { KINDS.put(type, k); }
        }
        return k;
    }

    static void resetKinds() { synchronized (KINDS) { KINDS.clear(); } }

    public static void enter(ServerLevel level, Entity e) {
        if (depth >= STACK.length) { depth++; return; }
        Entity next = current;
        if (next == null && WardIndex.has(level) && kind(e.getType()) != Kind.NONE) next = e;
        STACK[depth++] = next;
        current = next;
        if (next != null) thread = Thread.currentThread();
    }

    public static void exit() {
        if (depth == 0) return;
        depth--;
        if (depth < STACK.length) STACK[depth] = null;
        current = depth == 0 ? null : STACK[Math.min(depth, STACK.length) - 1];
    }

    /** Сброс на случай, если тик сущности упал с исключением и выход не вызвался (мод-«страховщик» поймал исключение). */
    static void reset() {
        for (int i = 0; i < Math.min(depth, STACK.length); i++) STACK[i] = null;
        depth = 0;
        current = null;
    }

    /** Активен ли контекст бедствия/паразита в этом потоке. */
    public static boolean active() {
        return current != null && Thread.currentThread() == thread;
    }

    /** Отклонить ли изменение блока: идёт тик бедствия/паразита, а блок — в зоне оберега. */
    public static boolean deny(Level level, BlockPos pos) {
        if (current == null || Thread.currentThread() != thread || level.isClientSide()) return false;
        if (!WardIndex.inZone(WardIndex.get(level), pos.getX() + 0.5, pos.getZ() + 0.5, 0) || !WardConfig.PROTECT_BLOCKS.get()) return false;
        denied++;
        return true;
    }
}
