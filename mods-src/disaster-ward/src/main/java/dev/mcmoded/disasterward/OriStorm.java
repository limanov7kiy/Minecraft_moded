package dev.mcmoded.disasterward;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Ориджиниумная буря Bob's Originium Dream (мягкая связь, рефлексия).
 * Буря тикает в OristormbeginProcedure (LevelTickEvent, фаза END, обычный приоритет) и призывает в квадрате ±300 вокруг своего центра
 * червей evildustoriworm, огненные шары, молнии и падающие блоки (ядро ориджиниума и блок бури, который через 100 тиков ставит шип 50×53×57).
 * Мы окружаем её тик своим контекстом (HIGHEST … LOWEST): то, что появляется в это время в зоне, отменяется, а блоки в зоне не меняются.
 * Запуск — OristormpredictProcedure по счётчику получений опыта; вместо него бросаем шанс по времени (см. WardConfig.ORI_ROLL).
 */
public final class OriStorm {
    private OriStorm() {}

    /** Сколько призывов бури было за время тика бури и сколько из них отменено в зоне (для теста). */
    public static long spawnsSeen, spawnsDenied;
    /** Сколько бурь запущено броском (для теста). */
    public static long rolled;

    private static boolean broken, ready;
    private static Method varsGet, varsSync, predict;
    private static Field fStorm, fBegin, fCounter;
    private static GameRules.Key<GameRules.IntegerValue> freqKey;
    private static boolean inTick;
    private static List<Block> stormBlocks;

    @SuppressWarnings("unchecked")
    private static boolean init() {
        if (ready) return true;
        if (broken) return false;
        try {
            Class<?> vars = Class.forName("bobsoriginiumdream.network.BobsoriginiumdreamModVariables$WorldVariables");
            varsGet = vars.getMethod("get", LevelAccessor.class);
            varsSync = vars.getMethod("syncData", LevelAccessor.class);
            fStorm = vars.getField("oristorm");
            fBegin = vars.getField("oristorm_begin");
            fCounter = vars.getField("ori_storm");
            predict = Class.forName("bobsoriginiumdream.procedures.OristormpredictProcedure").getMethod("execute", LevelAccessor.class, Entity.class);
            freqKey = (GameRules.Key<GameRules.IntegerValue>) Class.forName("bobsoriginiumdream.init.BobsoriginiumdreamModGameRules")
                    .getField("ORISTORMFREQUENCY").get(null);
            ready = true;
        } catch (ClassNotFoundException e) {
            broken = true;   // Bob's Originium Dream не установлен
        } catch (ReflectiveOperationException | LinkageError | ClassCastException e) {
            broken = true;
            WardMod.LOGGER.warn("Bob's Originium Dream: не удалось подключиться к буре, защита и броски отключены: {}", e.toString());
        }
        return ready;
    }

    private static Object vars(ServerLevel level) throws ReflectiveOperationException {
        return varsGet.invoke(null, level);
    }

    static void resetConfig() { stormBlocks = null; }

    // --- защита зоны во время тика бури ---

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTickFirst(TickEvent.LevelTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.level instanceof ServerLevel level) || !WardConfig.ORI_PROTECT.get()) return;
        if (!WardIndex.has(level) || !init()) return;
        try {
            if (!fStorm.getBoolean(vars(level))) return;
        } catch (ReflectiveOperationException e2) {
            return;
        }
        inTick = true;
        WardGuard.enterScope(null);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTickLast(TickEvent.LevelTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !inTick) return;
        inTick = false;
        WardGuard.exitScope();
    }

    /** Отменить ли появление сущности: идёт тик бури, это её призыв, и он в зоне или у её края. */
    static boolean denySpawn(ServerLevel level, Entity en) {
        if (!inTick) return false;
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(en.getType());
        if (id == null || !WardConfig.ORI_ENTITIES.get().contains(id.toString())) return false;
        spawnsSeen++;
        if (!WardApi.isProtected(level, en.getX(), en.getZ(), WardConfig.EDGE_MARGIN.get())) return false;
        spawnsDenied++;
        return true;
    }

    /** Блок бури тикает (миксин на ServerLevel.tickBlock): пока он ставит шип, блоки в зоне не меняются. */
    public static boolean isStormBlock(ServerLevel level, Block block) {
        if (!WardConfig.ORI_PROTECT.get() || !WardIndex.has(level)) return false;
        List<Block> list = stormBlocks;
        if (list == null) {
            list = new ArrayList<>();
            for (String s : WardConfig.ORI_BLOCKS.get()) {
                ResourceLocation id = ResourceLocation.tryParse(s);
                if (id != null && ForgeRegistries.BLOCKS.containsKey(id)) list.add(ForgeRegistries.BLOCKS.getValue(id));
            }
            stormBlocks = list;
        }
        return list.contains(block);
    }

    // --- запуск ---

    /** Раз в rollTicks: в измерении с игроком в выживании — бросок; удача — запуск бури у случайного такого игрока (предупреждение, через oriwait — буря). */
    @SubscribeEvent
    public static void onTickRoll(TickEvent.LevelTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.level instanceof ServerLevel level) || !WardConfig.ORI_ROLL.get()) return;
        if (level.getGameTime() % WardConfig.ORI_ROLL_TICKS.get() != 0) return;
        if (!WardConfig.ORI_DIMENSIONS.get().contains(level.dimension().location().toString()) || !init()) return;
        List<ServerPlayer> players = new ArrayList<>();
        for (ServerPlayer p : level.players()) if (p.gameMode.isSurvival() && p.isAlive()) players.add(p);
        if (players.isEmpty() || level.random.nextDouble() >= WardConfig.ORI_ROLL_CHANCE.get()) return;
        start(level, players.get(level.random.nextInt(players.size())));
    }

    /** Запустить бурю у игрока, как это делает сам мод (вызывается и тестом). false — уже идёт/ждёт или буря выключена правилом oristorm. */
    public static boolean start(ServerLevel level, ServerPlayer player) {
        if (!init()) return false;
        try {
            Object v = vars(level);
            if (fStorm.getBoolean(v) || fBegin.getBoolean(v)) return false;
            double counter = fCounter.getDouble(v);
            fCounter.setDouble(v, level.getGameRules().getInt(freqKey) - 1);   // следующий шаг счётчика — запуск
            predict.invoke(null, level, player);
            boolean begun = fBegin.getBoolean(v);
            if (!begun) fCounter.setDouble(v, counter);   // правило oristorm выключено — мод ничего не сделал, счётчик возвращаем
            varsSync.invoke(v, level);
            if (begun) {
                rolled++;
                WardMod.LOGGER.info("Ориджиниумная буря: запуск у {} ({}), начнётся через oriwait тиков", player.getGameProfile().getName(), level.dimension().location());
            }
            return begun;
        } catch (ReflectiveOperationException | LinkageError e) {
            WardMod.LOGGER.warn("Bob's Originium Dream: не удалось запустить бурю: {}", e.toString());
            return false;
        }
    }

    /** Снять «зависший» запуск: мод ждёт oriwait в памяти, после перезапуска сервера oristorm_begin остаётся true и буря больше не начинается. */
    @SubscribeEvent
    public static void onStarted(ServerStartedEvent e) {
        if (!WardConfig.ORI_FIX_STUCK.get() || !init()) return;
        for (ServerLevel level : e.getServer().getAllLevels()) {
            try {
                Object v = vars(level);
                if (fBegin.getBoolean(v) && !fStorm.getBoolean(v)) {
                    fBegin.setBoolean(v, false);
                    varsSync.invoke(v, level);
                    WardMod.LOGGER.info("Ориджиниумная буря: снят зависший запуск в {}", level.dimension().location());
                }
            } catch (ReflectiveOperationException | LinkageError ex) {
                WardMod.LOGGER.warn("Bob's Originium Dream: не удалось проверить запуск бури: {}", ex.toString());
                return;
            }
        }
    }
}
