package dev.mcmoded.disasterward;

import java.lang.reflect.Field;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class WardEvents {
    private WardEvents() {}

    private static final String TIMER = "NaturalSpawningNaturalDisastersTimer";   // таймер бросков Natural Disasters! в данных игрока
    private static final String DUE = "dw_next_roll";                              // наш срок следующего броска (игровое время)
    private static final String WAIT = "dw_waiting";                               // бросок разрешён, ждём его

    /** Существо-бедствие, которое нельзя спавнить нигде / в зоне оберега. */
    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent e) {
        if (!(e.getLevel() instanceof ServerLevel level)) return;
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(e.getEntity().getType());
        if (id == null) return;
        if (WardConfig.DISABLED_ENTITIES.get().contains(id.toString())) { e.setCanceled(true); return; }
        int max = WardConfig.MAX_CONCURRENT.get();
        if (max > 0 && WardConfig.ROOT_DISASTERS.get().contains(id.toString())) {
            int[] n = {0};
            level.getAllEntities().forEach(x -> {
                ResourceLocation xid = ForgeRegistries.ENTITY_TYPES.getKey(x.getType());
                if (x != e.getEntity() && xid != null && WardConfig.ROOT_DISASTERS.get().contains(xid.toString())) n[0]++;
            });
            if (n[0] >= max) { e.setCanceled(true); return; }
        }
        if (isDisaster(id) && WardApi.isProtected(level, e.getEntity().getX(), e.getEntity().getZ(), WardConfig.EDGE_MARGIN.get())) {
            e.setCanceled(true);
        }
    }

    private static boolean isDisaster(ResourceLocation id) {
        return WardConfig.PROTECTED_NAMESPACES.get().contains(id.getNamespace());
    }

    /** Раз в 10 тиков вычищаем бедствия, зашедшие в зону; раз в 5 с повторно регистрируем отражатели в Weather2. */
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.level instanceof ServerLevel level)) return;
        long t = level.getGameTime();
        if (t % 10 != 0) return;
        WardData data = WardData.get(level);
        if (data.isEmpty()) return;
        int reach = WardConfig.RADIUS.get() + WardConfig.EDGE_MARGIN.get();
        for (BlockPos p : data.positions()) {
            if (!level.isLoaded(p)) continue;
            if (t % 100 == 0) WardApi.weatherRegister(level, p, true);
            AABB box = new AABB(p.getX() - reach, level.getMinBuildHeight(), p.getZ() - reach,
                    p.getX() + reach + 1, level.getMaxBuildHeight(), p.getZ() + reach + 1);
            for (Entity en : level.getEntities((Entity) null, box, x -> {
                ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(x.getType());
                return id != null && isDisaster(id);
            })) {
                en.discard();
            }
        }
    }

    /** Включаем случайные бедствия Natural Disasters! один раз на мир (игрок может выключить их в меню мода — мы не вернём). */
    @SubscribeEvent
    public static void onStarted(ServerStartedEvent e) {
        ServerLevel over = e.getServer().overworld();
        WardData data = WardData.get(over);
        if (data.naturalInit || !WardConfig.NATURAL.get()) return;
        try {
            Class<?> vars = Class.forName("net.mcreator.naturaldisasters.network.NaturalDisastersModVariables$WorldVariables");
            Object wv = vars.getMethod("get", net.minecraft.world.level.LevelAccessor.class).invoke(null, over);
            Field f = vars.getField("NaturalDisastersSpawningNaturalCheckBox");
            f.setBoolean(wv, true);
            vars.getMethod("syncData", net.minecraft.world.level.LevelAccessor.class).invoke(wv, over);
            data.naturalInit = true;
            data.setDirty();
            WardMod.LOGGER.info("Natural Disasters!: случайные бедствия включены (паузы {}–{} тиков на игрока)",
                    WardConfig.NATURAL_MIN_TICKS.get(), WardConfig.NATURAL_MAX_TICKS.get());
        } catch (ReflectiveOperationException | LinkageError ex) {
            WardMod.LOGGER.warn("Natural Disasters!: не удалось включить случайные бедствия: {}", ex.toString());
        }
    }

    /**
     * Natural Disasters! бросает кубик каждые ~100 тиков на игрока (таймер в данных игрока). Не даём таймеру дойти до нуля,
     * пока не прошла наша пауза; после броска (таймер снова около 100) назначаем следующую паузу.
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.player instanceof ServerPlayer p) || !WardConfig.NATURAL.get()) return;
        CompoundTag d = p.getPersistentData();
        long now = p.level().getGameTime();
        double timer = d.getDouble(TIMER);
        if (!d.contains(DUE)) { schedule(d, now); }
        else if (d.getBoolean(WAIT)) {
            if (timer >= 99) { schedule(d, now); d.putBoolean(WAIT, false); }   // бросок состоялся
        }
        if (!d.getBoolean(WAIT)) {
            if (now >= d.getLong(DUE)) { d.putBoolean(WAIT, true); }
            else if (timer < 50) { d.putDouble(TIMER, 50); }
        }
    }

    private static void schedule(CompoundTag d, long now) {
        int min = WardConfig.NATURAL_MIN_TICKS.get(), max = Math.max(min, WardConfig.NATURAL_MAX_TICKS.get());
        d.putLong(DUE, now + min + (long) (Math.random() * (max - min)));
    }

}
