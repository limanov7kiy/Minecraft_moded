package dev.mcmoded.necromancersdelight.item;

import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.core.platform.Services;
import dev.mcmoded.necromancersdelight.effect.NdEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

/// Эффекты артефактов Necromancer's Delight, не требующие мобов Fish's Undead Rising (оригинал — `NDEventHandler`,
/// `ItemMaliceWard`, WinDanesz). «Нежить» — `MobType.UNDEAD`.
public final class NdArtifacts {
    private NdArtifacts() {}

    static boolean has(Player p, RegistryObject<Item> a) {
        return ArtifactChannel.isEquipped(p, a.get());
    }

    static boolean undead(Object e) {
        return e instanceof LivingEntity l && l.getMobType() == MobType.UNDEAD;
    }

    /// Amulet of Tenacity / Vigour: полный иммунитет к замедлению / слабости.
    @SubscribeEvent
    public static void onEffect(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player p)) return;
        var eff = event.getEffectInstance().getEffect();
        if (eff == MobEffects.MOVEMENT_SLOWDOWN && has(p, NdItems.AMULET_SLOWNESS_IMMUNITY)
                || eff == MobEffects.WEAKNESS && has(p, NdItems.AMULET_WEAKNESS_IMMUNITY)) {
            event.setResult(Event.Result.DENY);
        }
    }

    /// Necromantic Ward: урон от нежити −15%.
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player p && undead(event.getSource().getEntity()) && has(p, NdItems.AMULET_NECROMANTIC_WARD)) {
            event.setAmount(event.getAmount() * 0.85f);
        }
    }

    /// Malice Ward: нежить в радиусе 5 — Поглощение III на 10 с, откат 60 с (настройки оригинала по умолчанию).
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player p = event.player;
        if (event.phase != TickEvent.Phase.END || p.level().isClientSide || p.tickCount % 20 != 0) return;
        Item ward = NdItems.AMULET_MALICE_WARD.get();
        if (p.getCooldowns().isOnCooldown(ward) || !has(p, NdItems.AMULET_MALICE_WARD)) return;
        if (p.level().getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(5), NdArtifacts::undead).isEmpty()) return;
        p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 2));
        p.getCooldowns().addCooldown(ward, 1200);
    }

    /// Cursed Scarab: призванная нежить покрыта роем саранчи (Locusts — не горит на солнце).
    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof Mob mob) || !undead(mob)) return;
        MinionData data = Services.OBJECT_DATA.getMinionData(mob);
        if (data == null || !data.isSummoned() || !(data.getOwner() instanceof Player p) || !has(p, NdItems.CHARM_SCARAB)) return;
        mob.addEffect(new MobEffectInstance(NdEffects.LOCUSTS.get(), Integer.MAX_VALUE, 0, false, false));
    }
}
