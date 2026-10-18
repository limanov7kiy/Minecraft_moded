package dev.mcmoded.disasterward;

import java.lang.reflect.Field;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class WardEvents {
    private WardEvents() {}

    private static final String TIMER = "NaturalSpawningNaturalDisastersTimer";   // таймер бросков Natural Disasters! в данных игрока
    private static final String DUE = "dw_next_roll";                              // наш срок следующего броска (игровое время)
    private static final String WAIT = "dw_waiting";                               // бросок разрешён, ждём его

    /** Существо-бедствие, которое нельзя спавнить нигде / в зоне оберега; паразит, которому нельзя появиться в зоне и у её края. */
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
        if (OriStorm.denySpawn(level, e.getEntity())) { e.setCanceled(true); return; }
        if (WardGuard.kind(e.getEntity().getType()) != WardGuard.Kind.NONE
                && WardApi.isProtected(level, e.getEntity().getX(), e.getEntity().getZ(), WardConfig.EDGE_MARGIN.get())) {
            e.setCanceled(true);
        }
    }

    /** В зоне не накладываются эффекты заражения; боссам и существам из тега infection_immune — нигде. */
    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable e) {
        LivingEntity en = e.getEntity();
        if (!(en.level() instanceof ServerLevel level) || !isInfection(e.getEffectInstance().getEffect())) return;
        if (WardApi.isProtected(level, en.getX(), en.getZ()) || immune(en)) e.setResult(Event.Result.DENY);
    }

    private static final TagKey<EntityType<?>> BOSSES = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", "bosses"));
    private static final TagKey<EntityType<?>> IMMUNE = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(WardMod.MOD_ID, "infection_immune"));

    static boolean immune(LivingEntity en) {
        if (!WardConfig.IMMUNE_BOSSES.get() || en instanceof Player) return false;
        EntityType<?> t = en.getType();
        return t.is(BOSSES) || t.is(IMMUNE);
    }

    private static boolean isInfection(MobEffect effect) {
        ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        return id != null && WardConfig.INFECTION_EFFECTS.get().contains(id.toString());
    }

    /** Взрыв, вызванный бедствием (в том числе «ничейный» взрыв из тика метеора Natural Disasters!) или паразитом, не рушит блоки в зоне. */
    @SubscribeEvent
    public static void onDetonate(ExplosionEvent.Detonate e) {
        if (!(e.getLevel() instanceof ServerLevel level) || !WardIndex.has(level) || !WardConfig.PROTECT_BLOCKS.get()) return;
        Entity src = e.getExplosion().getDirectSourceEntity();
        Entity cause = e.getExplosion().getIndirectSourceEntity();
        boolean hostile = WardGuard.active()
                || (src != null && WardGuard.kind(src.getType()) != WardGuard.Kind.NONE)
                || (cause != null && WardGuard.kind(cause.getType()) != WardGuard.Kind.NONE);
        if (!hostile) return;
        BlockPos[] wards = WardIndex.get(level);
        int before = e.getAffectedBlocks().size();
        e.getAffectedBlocks().removeIf(p -> WardIndex.inZone(wards, p.getX() + 0.5, p.getZ() + 0.5, 0));
        WardGuard.denied += before - e.getAffectedBlocks().size();
    }

    /** Торнадо Weather2 рвёт блоки от имени «weather2FakePlayer» (нужно blockBreakingInvokesCancellableEvent = true в config/Weather2/Misc.toml). */
    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent e) {
        if (!(e.getLevel() instanceof ServerLevel level) || !(e.getPlayer() instanceof FakePlayer fp) || !WardConfig.PROTECT_BLOCKS.get()) return;
        if (!W2_FAKE.equals(fp.getGameProfile().getName())) return;
        if (WardApi.isProtected(level, e.getPos().getX() + 0.5, e.getPos().getZ() + 0.5)) { e.setCanceled(true); WardGuard.denied++; }
    }

    private static final String W2_FAKE = "weather2FakePlayer";

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load e) {
        if (e.getLevel() instanceof ServerLevel level) WardData.get(level);   // заполнить WardIndex
    }

    @SubscribeEvent
    public static void onStopped(ServerStoppedEvent e) {
        WardIndex.clear();
        WardGuard.reset();
        WEATHER_CLEANED.clear();
    }

    private static final java.util.Set<net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level>> WEATHER_CLEANED = new java.util.HashSet<>();

    /**
     * Раз в 5 тиков: бедствия Natural Disasters! в зоне и у края убираются; паразиты Spore в полосе у края отталкиваются, внутри — убираются;
     * с заражённых в зоне снимаются эффекты заражения. Раз в секунду — разворот бурь Weather2.
     */
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent e) {
        if (!(e.level instanceof ServerLevel level)) return;
        if (e.phase == TickEvent.Phase.START) { WardGuard.reset(); return; }
        long t = level.getGameTime();
        if (t % 5 != 0) return;
        BlockPos[] wards = WardIndex.get(level);
        if (wards.length == 0) return;
        if (WEATHER_CLEANED.add(level.dimension())) for (BlockPos p : wards) WardApi.weatherUnregister(level, p);
        if (t % 20 == 0 && WardConfig.STEER_STORMS.get()) WardApi.steerStorms(level, wards);
        int r = WardConfig.RADIUS.get(), m = WardConfig.EDGE_MARGIN.get(), reach = r + m;
        for (BlockPos p : wards) {
            if (!level.isLoaded(p)) continue;
            AABB box = new AABB(p.getX() - reach, level.getMinBuildHeight(), p.getZ() - reach,
                    p.getX() + reach + 1, level.getMaxBuildHeight(), p.getZ() + reach + 1);
            for (Entity en : level.getEntities((Entity) null, box, x -> true)) {
                if (en.isRemoved()) continue;
                WardGuard.Kind k = WardGuard.kind(en.getType());
                if (k == WardGuard.Kind.DISASTER) { if (t % 10 == 0) en.discard(); continue; }
                boolean inside = WardIndex.inZone(wards, en.getX(), en.getZ(), 0);
                if (k == WardGuard.Kind.PARASITE) {
                    if (inside) { en.discard(); WardMod.LOGGER.debug("Оберег: убран паразит {} в зоне", en); }
                    else if (en instanceof Mob mob && WardConfig.PUSH_PARASITES.get()) push(mob, p, wards);
                    continue;
                }
                if (inside && WardConfig.CURE_IN_ZONE.get() && en instanceof LivingEntity le) cure(le);
            }
        }
    }

    /** Отталкивание от зоны: толчок наружу от ближайшей стороны квадрата, сброс пути и цели, если цель внутри зоны. */
    private static void push(Mob mob, BlockPos ward, BlockPos[] wards) {
        double dx = mob.getX() - (ward.getX() + 0.5), dz = mob.getZ() - (ward.getZ() + 0.5);
        double vx = 0, vz = 0;
        if (Math.abs(dx) >= Math.abs(dz)) vx = Math.signum(dx == 0 ? 1 : dx) * 0.6; else vz = Math.signum(dz == 0 ? 1 : dz) * 0.6;
        mob.setDeltaMovement(vx, Math.max(mob.getDeltaMovement().y, 0.1), vz);
        mob.hurtMarked = true;
        mob.getNavigation().stop();
        LivingEntity target = mob.getTarget();
        if (target != null && WardIndex.inZone(wards, target.getX(), target.getZ(), WardConfig.EDGE_MARGIN.get())) mob.setTarget(null);
    }

    private static void cure(LivingEntity le) {
        if (le.getActiveEffects().isEmpty()) return;
        java.util.List<MobEffect> bad = new java.util.ArrayList<>();
        for (MobEffectInstance i : le.getActiveEffects()) if (isInfection(i.getEffect())) bad.add(i.getEffect());
        for (MobEffect ef : bad) le.removeEffect(ef);
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
