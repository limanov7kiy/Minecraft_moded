package dev.mcmoded.wizardryrestored.util;

import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Comparator;
import java.util.UUID;

/// Длительные состояния заклинаний, которые хранятся в NBT существа (переживают перезапуск сервера):
/// теневой заслон, ледяной покров, контроль разума, призрачный конь.
public final class SpellStates {
    private static final String ROOT = "wizardry_restored";
    public static final String SHADOW_WARD_UNTIL = "shadow_ward_until";
    public static final String ICE_SHROUD_UNTIL = "ice_shroud_until";
    public static final String ICE_SHROUD_FROST = "ice_shroud_frost";
    public static final String MIND_CONTROL_UNTIL = "mind_control_until";
    public static final String MIND_CONTROLLER = "mind_controller";
    public static final String SPIRIT_HORSE = "spirit_horse";
    public static final String SPIRIT_HORSE_IDLE = "spirit_horse_idle";
    public static final float SHADOW_WARD_FRACTION = 0.5f;

    private SpellStates() {
    }

    public static CompoundTag data(Entity e) {
        CompoundTag p = e.getPersistentData();
        if (!p.contains(ROOT)) p.put(ROOT, new CompoundTag());
        return p.getCompound(ROOT);
    }

    private static boolean active(Entity e, String key) {
        CompoundTag d = data(e);
        return d.contains(key) && d.getLong(key) > e.level().getGameTime();
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide || !(event.getSource().getEntity() instanceof LivingEntity attacker) || attacker == victim) return;
        // Теневой заслон: половина урона возвращается нападающему
        if (active(victim, SHADOW_WARD_UNTIL)) {
            float reflected = event.getAmount() * SHADOW_WARD_FRACTION;
            event.setAmount(event.getAmount() - reflected);
            attacker.hurt(MagicDamageSource.causeDirectMagicDamage(victim, EBDamageSources.MAGIC), reflected);
        }
        // Ледяной покров: нападающий обмерзает
        if (active(victim, ICE_SHROUD_UNTIL)) {
            attacker.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), data(victim).getInt(ICE_SHROUD_FROST), 0));
            if (victim.level() instanceof ServerLevel s)
                s.sendParticles(ParticleTypes.SNOWFLAKE, attacker.getX(), attacker.getEyeY(), attacker.getZ(), 15, 0.3, 0.4, 0.3, 0.02);
        }
    }

    /// Подконтрольное существо не нападает на хозяина и его союзников.
    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity mob = event.getEntity();
        LivingEntity target = event.getNewTarget();
        if (target == null || !active(mob, MIND_CONTROL_UNTIL)) return;
        UUID owner = data(mob).getUUID(MIND_CONTROLLER);
        Player o = mob.level().getPlayerByUUID(owner);
        if (target.getUUID().equals(owner) || (o != null && target instanceof Player p && AllyDesignation.isAllied(o, p)))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity e = event.getEntity();
        if (e.level().isClientSide || e.tickCount % 20 != 0) return;
        CompoundTag d = e.getPersistentData().getCompound(ROOT);
        if (d.isEmpty()) return;

        // Контроль разума: каждую секунду ищем врага поближе; по окончании — снимаем метку
        if (d.contains(MIND_CONTROL_UNTIL) && e instanceof Mob mob) {
            if (d.getLong(MIND_CONTROL_UNTIL) <= e.level().getGameTime()) {
                d.remove(MIND_CONTROL_UNTIL);
                d.remove(MIND_CONTROLLER);
                mob.setTarget(null);
            } else if (mob.getTarget() == null || !mob.getTarget().isAlive()) {
                UUID owner = d.getUUID(MIND_CONTROLLER);
                e.level().getEntitiesOfClass(LivingEntity.class, e.getBoundingBox().inflate(16),
                                x -> x != e && x instanceof Enemy && !x.getUUID().equals(owner) && x.isAlive())
                        .stream().min(Comparator.comparingDouble(x -> x.distanceToSqr(e))).ifPresent(mob::setTarget);
            }
        }

        // Призрачный конь: исчезает через 30 секунд без всадника
        if (d.getBoolean(SPIRIT_HORSE) && e instanceof AbstractHorse horse) {
            int idle = horse.isVehicle() ? 0 : d.getInt(SPIRIT_HORSE_IDLE) + 1;
            d.putInt(SPIRIT_HORSE_IDLE, idle);
            if (idle >= 30) {
                if (e.level() instanceof ServerLevel s)
                    s.sendParticles(ParticleTypes.CLOUD, e.getX(), e.getY() + 1, e.getZ(), 20, 0.4, 0.5, 0.4, 0.02);
                horse.discard();
            }
        }
    }

    /// Призванные существа ничего не роняют.
    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (event.getEntity().getPersistentData().getCompound(ROOT).getBoolean(SPIRIT_HORSE)) event.setCanceled(true);
    }
}
