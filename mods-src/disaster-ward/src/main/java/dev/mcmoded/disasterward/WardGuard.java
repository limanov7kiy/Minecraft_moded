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
    /** Внешний контекст бедствия без сущности (тик бури и блоков бури Bob's Originium Dream), только серверный поток. */
    private static int scope;
    private static Thread scopeThread;
    /** Клетка, которую контекст может менять и в зоне (сам блок бури убирает себя перед шипом). */
    private static BlockPos scopeOwn;
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
        scope = 0;
        scopeOwn = null;
        for (int i = 0; i < Math.min(depth, STACK.length); i++) STACK[i] = null;
        depth = 0;
        current = null;
    }

    public static void enterScope(BlockPos own) {
        if (scope++ == 0) { scopeThread = Thread.currentThread(); scopeOwn = own; }
    }

    public static void exitScope() {
        if (scope > 0 && --scope == 0) scopeOwn = null;
    }

    private static boolean inScope() {
        return scope > 0 && Thread.currentThread() == scopeThread;
    }

    /** Активен ли контекст бедствия/паразита в этом потоке. */
    public static boolean active() {
        return (current != null && Thread.currentThread() == thread) || inScope();
    }

    /** Отклонить ли изменение блока: идёт тик бедствия/паразита, а блок — в зоне оберега. */
    public static boolean deny(Level level, BlockPos pos) {
        if (!active() || level.isClientSide()) return false;
        if (scopeOwn != null && current == null && scopeOwn.equals(pos)) return false;
        int margin = current == null ? WardConfig.EDGE_MARGIN.get() : 0;   // буря Bob's (контекст без сущности) — зона с полосой у края
        if (!WardIndex.inZone(WardIndex.get(level), pos.getX() + 0.5, pos.getZ() + 0.5, margin) || !WardConfig.PROTECT_BLOCKS.get()) return false;
        denied++;
        return true;
    }
}
