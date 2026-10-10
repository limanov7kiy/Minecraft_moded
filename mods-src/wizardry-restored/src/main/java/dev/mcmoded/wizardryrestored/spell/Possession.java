package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.wizardryrestored.util.SpellStates;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

/// Одержимость: заклинатель вселяется в существо — сам становится невидимым, а тело существа (без собственного
/// разума, неуязвимое) повторяет каждое его движение. Если тело враждебное, другие монстры принимают
/// заклинателя за своего. Длится 30 секунд; Shift + сотворение — выйти раньше. Без подмены камеры и способностей
/// существа (упрощение «чистой комнаты»; в оригинале игрок управляет телом полностью).
public class Possession extends Spell {
    public static final String HOST = "possession_host";
    public static final String UNTIL = "possession_until";
    public static final String HOSTILE = "possession_hostile";
    public static final String POSSESSED_BY = "possessed_by";
    public static final String HOST_NO_AI = "possessed_no_ai";

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        CompoundTag data = SpellStates.data(caster);
        if (caster.isShiftKeyDown() && data.contains(HOST)) {
            if (ctx.world() instanceof ServerLevel server) release(server, caster);
            return true;
        }
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        LivingEntity target = Targeting.lookTarget(caster, range);
        if (!(target instanceof Mob mob) || data.contains(HOST) || !AllyDesignation.isValidTarget(caster, mob)
                || mob.getType().is(Tags.EntityTypes.BOSSES) || mob.getMaxHealth() > property(MAX_HEALTH)
                || SpellStates.data(mob).contains(POSSESSED_BY)) return false;
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        int duration = (int) (property(DefaultProperties.DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
        CompoundTag host = SpellStates.data(mob);
        host.putUUID(POSSESSED_BY, caster.getUUID());
        host.putBoolean(HOST_NO_AI, mob.isNoAi());
        mob.setNoAi(true);
        mob.setInvulnerable(true);
        mob.setTarget(null);
        mob.noPhysics = true;
        data.putUUID(HOST, mob.getUUID());
        data.putLong(UNTIL, server.getGameTime() + duration);
        data.putBoolean(HOSTILE, mob instanceof Enemy);
        server.sendParticles(ParticleTypes.SOUL, mob.getX(), mob.getY() + mob.getBbHeight() / 2, mob.getZ(), 30, 0.3, 0.5, 0.3, 0.05);
        caster.teleportTo(mob.getX(), mob.getY(), mob.getZ());
        caster.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false, true));
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    /// Вернуть существу собственную волю и снять невидимость с заклинателя.
    public static void release(ServerLevel level, Player player) {
        CompoundTag data = SpellStates.data(player);
        if (data.hasUUID(HOST) && level.getEntity(data.getUUID(HOST)) instanceof Mob mob) freeHost(mob);
        data.remove(HOST);
        data.remove(UNTIL);
        data.remove(HOSTILE);
        player.removeEffect(MobEffects.INVISIBILITY);
    }

    private static void freeHost(Mob mob) {
        CompoundTag host = SpellStates.data(mob);
        mob.setNoAi(host.getBoolean(HOST_NO_AI));
        mob.setInvulnerable(false);
        mob.noPhysics = false;
        host.remove(POSSESSED_BY);
        host.remove(HOST_NO_AI);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || !(player.level() instanceof ServerLevel level)) return;
        CompoundTag data = player.getPersistentData().getCompound("wizardry_restored");
        if (!data.hasUUID(HOST)) return;
        Entity host = level.getEntity(data.getUUID(HOST));
        if (!(host instanceof Mob mob) || !mob.isAlive() || !player.isAlive() || data.getLong(UNTIL) <= level.getGameTime()) {
            release(level, player);
            return;
        }
        // Тело — чуть позади глаз заклинателя, иначе прицел упирается в него самого
        Vec3 back = Vec3.directionFromRotation(0, player.getYRot()).scale(-(mob.getBbWidth() / 2 + 0.4));
        mob.moveTo(player.getX() + back.x, player.getY(), player.getZ() + back.z, player.getYRot(), player.getXRot());
        mob.setYHeadRot(player.getYHeadRot());
        mob.yBodyRot = player.yBodyRot;
        mob.setDeltaMovement(player.getDeltaMovement());
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity().level() instanceof ServerLevel level) release(level, event.getEntity());
    }

    /// Тело, оставшееся «одержимым» после перезапуска или выхода заклинателя, освобождается.
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || mob.level().isClientSide || mob.tickCount % 20 != 0) return;
        CompoundTag host = mob.getPersistentData().getCompound("wizardry_restored");
        if (!host.hasUUID(POSSESSED_BY)) return;
        Player owner = mob.level().getPlayerByUUID(host.getUUID(POSSESSED_BY));
        CompoundTag data = owner == null ? null : owner.getPersistentData().getCompound("wizardry_restored");
        if (data == null || !data.hasUUID(HOST) || !data.getUUID(HOST).equals(mob.getUUID())) freeHost(mob);
    }

    /// Монстры не трогают заклинателя, вселившегося в монстра.
    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getNewTarget() instanceof Player p) || !(event.getEntity() instanceof Enemy)) return;
        CompoundTag data = p.getPersistentData().getCompound("wizardry_restored");
        if (data.hasUUID(HOST) && data.getBoolean(HOSTILE)) event.setCanceled(true);
    }

    private static final com.binaris.wizardry.api.content.spell.properties.SpellProperty<Float> MAX_HEALTH =
            com.binaris.wizardry.api.content.spell.properties.SpellProperty.floatProperty("max_target_health", 60f);

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.NECROMANCY, SpellTypes.UTILITY, SpellAction.POINT, 100, 30, 600)
                .add(DefaultProperties.RANGE, 10f)
                .add(DefaultProperties.DURATION, 600)
                .add(MAX_HEALTH)
                .build();
    }
}
