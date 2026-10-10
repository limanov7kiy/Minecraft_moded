package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// Сеть молний (непрерывное): дуга от заклинателя к цели под взглядом, от неё — к соседям, от тех — дальше.
/// Сеть перестраивается каждые полсекунды, пока заклинание удерживается.
public class LightningWeb extends Spell {
    private static final SpellProperty<Float> SECONDARY_DAMAGE = SpellProperty.floatProperty("secondary_damage", 3f);
    private static final SpellProperty<Float> TERTIARY_DAMAGE = SpellProperty.floatProperty("tertiary_damage", 2f);
    private static final SpellProperty<Float> CHAIN_RANGE = SpellProperty.floatProperty("chain_range", 5f);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        if (ctx.castingTicks() % 10 != 0) return true;
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        LivingEntity first = Targeting.lookTarget(caster, range);
        if (first == null || !AllyDesignation.isValidTarget(caster, first)) {
            Vec3 eye = caster.getEyePosition(1);
            arc(server, eye, eye.add(caster.getLookAngle().scale(range)));
            return true;
        }
        float potency = ctx.modifiers().get(SpellModifiers.POTENCY);
        Set<LivingEntity> hit = new HashSet<>(List.of(caster, first));
        zap(server, caster, caster.getEyePosition(1), first, property(DefaultProperties.DAMAGE) * potency);
        for (LivingEntity second : chain(server, caster, first, 3, property(SECONDARY_DAMAGE) * potency, hit))
            chain(server, caster, second, 2, property(TERTIARY_DAMAGE) * potency, hit);
        return true;
    }

    private List<LivingEntity> chain(ServerLevel server, Player caster, LivingEntity from, int max, float damage, Set<LivingEntity> hit) {
        List<LivingEntity> near = new ArrayList<>(EntityUtil.getLivingWithinRadius(property(CHAIN_RANGE), from.getX(), from.getY(), from.getZ(), server));
        near.removeIf(e -> hit.contains(e) || !AllyDesignation.isValidTarget(caster, e));
        near.sort(Comparator.comparingDouble(e -> e.distanceToSqr(from)));
        List<LivingEntity> picked = new ArrayList<>(near.subList(0, Math.min(max, near.size())));
        hit.addAll(picked);
        for (LivingEntity e : picked) zap(server, caster, from.getEyePosition(1), e, damage);
        return picked;
    }

    private static void zap(ServerLevel server, Player caster, Vec3 from, LivingEntity target, float damage) {
        target.hurt(MagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.SHOCK), damage);
        arc(server, from, target.getEyePosition(1));
    }

    private static void arc(ServerLevel server, Vec3 from, Vec3 to) {
        Vec3 step = to.subtract(from);
        int n = (int) Math.max(4, step.length() * 3);
        for (int i = 0; i <= n; i++) {
            Vec3 p = from.add(step.scale(i / (double) n));
            server.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 1, 0.08, 0.08, 0.08, 0);
        }
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected void playSound(Level world, LivingEntity entity, int castTicks, int duration) {
        if (castTicks % 20 == 0) super.playSound(world, entity, castTicks, duration);
    }

    @Override
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
        if (ticksInUse % 20 == 0) super.playSound(world, x, y, z, ticksInUse, duration);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.LIGHTNING, SpellTypes.ATTACK, SpellAction.POINT, 15, 0, 0)
                .add(DefaultProperties.RANGE, 10f)
                .add(DefaultProperties.DAMAGE, 5f)
                .add(SECONDARY_DAMAGE).add(TERTIARY_DAMAGE).add(CHAIN_RANGE)
                .build();
    }
}
