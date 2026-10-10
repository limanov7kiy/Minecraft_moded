package dev.mcmoded.wizardryrestored.util;

import com.binaris.wizardry.api.content.util.CastItemDataHelper;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.entity.construct.FireRingConstruct;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import dev.mcmoded.wizardryrestored.WizardryRestored;
import dev.mcmoded.wizardryrestored.entity.LightningHammerEntity;
import dev.mcmoded.wizardryrestored.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Supplier;

/// Артефакты Redux, которые в 0.8.9 есть как предметы, но без действия (`NotImplementedItems`), — те, что связаны
/// с заклинаниями дополнения. Проверка ношения — `ArtifactChannel` Redux (Curios/Accessories или хотбар).
/// Здесь — эффекты на событиях; остальные встроены в сами заклинания и сущности (поле, туча, дыра, молот, свет,
/// добыча, замедление времени, лоза, перемещение).
public final class Artifacts {
    public static final String RESURRECT_READY = "amulet_resurrection_ready";
    public static final String HAMMER_EXPIRES = "wizardry_restored_expires";
    public static final int RESURRECT_COOLDOWN = 24000;

    private Artifacts() {
    }

    public static boolean has(Entity e, Supplier<? extends Item> artifact) {
        return e instanceof Player p && ArtifactChannel.isEquipped(p, artifact.get());
    }

    /// Амулет якоря: магия не двигает носителя.
    public static boolean anchored(Entity e) {
        return has(e, EBItems.AMULET_ANCHORING);
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel level)) return;
        var source = event.getSource();

        // Амулет Хьелет: 25% — на 3 секунды встаёт щит
        if (has(victim, EBItems.AMULET_AUTO_SHIELD) && level.random.nextFloat() < 0.25f) {
            SpellStates.data(victim).putLong(SpellStates.SHIELD_UNTIL, level.getGameTime() + 60);
            victim.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, false, false, true));
        }
        // Амулет плаща пламени: внутри своего (или союзного) огненного кольца — урон ×0,25
        if (has(victim, EBItems.AMULET_FIRE_CLOAKING) && !level.getEntitiesOfClass(FireRingConstruct.class, victim.getBoundingBox(),
                r -> r.getCaster() == victim || r.getCaster() != null && AllyDesignation.isAllied(victim, r.getCaster())).isEmpty())
            event.setAmount(event.getAmount() * 0.25f);
        // Амулет Края: 15% — напавший вблизи переносится в случайное место рядом
        if (has(victim, EBItems.AMULET_BANISHING) && source.getDirectEntity() instanceof LivingEntity attacker
                && source.getEntity() == attacker && !attacker.getType().is(Tags.EntityTypes.BOSSES) && level.random.nextFloat() < 0.15f) {
            for (int i = 0; i < 16; i++) {
                double x = attacker.getX() + (level.random.nextDouble() - 0.5) * 16, z = attacker.getZ() + (level.random.nextDouble() - 0.5) * 16;
                double y = attacker.getY() + level.random.nextInt(8) - 4;
                if (attacker.randomTeleport(x, y, z, true)) {
                    level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENDERMAN_TELEPORT, attacker.getSoundSource(), 1, 1);
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel level)) return;
        var source = event.getSource();

        // Амулет Бессмертного: воскрешение себя, если на палочке в инвентаре есть «Воскрешение» (раз в 20 минут)
        if (victim instanceof Player p && has(p, EBItems.AMULET_RESURRECTION) && knowsResurrection(p)) {
            CompoundTag d = SpellStates.data(p);
            if (d.getLong(RESURRECT_READY) <= level.getGameTime()) {
                event.setCanceled(true);
                d.putLong(RESURRECT_READY, level.getGameTime() + RESURRECT_COOLDOWN);
                p.setHealth(p.getMaxHealth() / 2);
                p.removeAllEffects();
                level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, p.getX(), p.getY() + 1, p.getZ(), 60, 0.4, 0.8, 0.4, 0.3);
                level.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.TOTEM_USE, p.getSoundSource(), 1, 1);
                return;
            }
        }
        // Кольцо тлеющих углей: жертвы огненных заклинаний рассыпаются пеплом, поджигая соседей
        if (source instanceof MagicDamageSource && source.is(EBDamageSources.FIRE) && has(source.getEntity(), EBItems.RING_DISINTEGRATION)) {
            level.sendParticles(ParticleTypes.FLAME, victim.getX(), victim.getY() + 0.5, victim.getZ(), 60, 0.5, 0.6, 0.5, 0.08);
            level.sendParticles(ParticleTypes.LAVA, victim.getX(), victim.getY() + 0.5, victim.getZ(), 12, 0.4, 0.4, 0.4, 0);
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, victim.getBoundingBox().inflate(2.5)))
                if (e != source.getEntity() && e != victim && AllyDesignation.isValidTarget(source.getEntity(), e)) e.setSecondsOnFire(5);
        }
    }

    private static boolean knowsResurrection(Player p) {
        for (ItemStack stack : p.getInventory().items)
            if (!stack.isEmpty() && CastItemDataHelper.getSpells(stack).contains(WizardryRestored.RESURRECTION.get())) return true;
        return CastItemDataHelper.getSpells(p.getOffhandItem()).contains(WizardryRestored.RESURRECTION.get());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player p = event.player;
        if (event.phase != TickEvent.Phase.END || !(p.level() instanceof ServerLevel level) || p.tickCount % 20 != 0) return;
        // Амулет Эсундора: рядом со стеной льда — защитный эффект Redux
        if (has(p, EBItems.AMULET_FROST_WARDING)) {
            BlockPos base = p.blockPosition();
            for (BlockPos q : BlockPos.betweenClosed(base.offset(-2, 0, -2), base.offset(2, 1, 2))) {
                var s = level.getBlockState(q);
                if (s.is(Blocks.ICE) || s.is(Blocks.PACKED_ICE)) {
                    p.addEffect(new MobEffectInstance(EBMobEffects.WARD.get(), 40, 0, false, false, true));
                    break;
                }
            }
        }
        // Подобранный молот молний живёт не дольше, чем жил бы на земле
        var inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(EBItems.LIGHTNING_HAMMER.get()) && s.hasTag() && s.getTag().getLong(HAMMER_EXPIRES) <= level.getGameTime())
                inv.setItem(i, ItemStack.EMPTY);
        }
    }

    /// Кольцо Талека: подобранный молот молний бросается правым кликом.
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Player p = event.getEntity();
        if (!stack.is(EBItems.LIGHTNING_HAMMER.get()) || !(p.level() instanceof ServerLevel level)) return;
        LightningHammerEntity hammer = new LightningHammerEntity(ModEntities.LIGHTNING_HAMMER.get(), level);
        hammer.setPos(p.getEyePosition(1).add(p.getLookAngle()));
        hammer.setDeltaMovement(p.getLookAngle().scale(1.5));
        hammer.setCaster(p);
        long left = stack.hasTag() ? stack.getTag().getLong(HAMMER_EXPIRES) - level.getGameTime() : 600;
        hammer.setLifetime((int) Math.max(20, left));
        level.addFreshEntity(hammer);
        level.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.TRIDENT_THROW, p.getSoundSource(), 1, 0.6f);
        stack.shrink(1);
        event.setCanceled(true);
        event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
    }
}
