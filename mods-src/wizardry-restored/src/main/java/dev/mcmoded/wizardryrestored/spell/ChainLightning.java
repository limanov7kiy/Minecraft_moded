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
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// Молния бьёт цель и перескакивает на соседей, затем ещё раз — с убывающим уроном.
public class ChainLightning extends Spell {
    private static final SpellProperty<Float> SECONDARY_DAMAGE = SpellProperty.floatProperty("secondary_damage", 8f);
    private static final SpellProperty<Float> TERTIARY_DAMAGE = SpellProperty.floatProperty("tertiary_damage", 6f);
    private static final SpellProperty<Float> CHAIN_RANGE = SpellProperty.floatProperty("chain_range", 5f);
    private static final SpellProperty<Integer> SECONDARY_TARGETS = SpellProperty.intProperty("secondary_max_targets", 5);
    private static final SpellProperty<Integer> TERTIARY_TARGETS = SpellProperty.intProperty("tertiary_max_targets", 2);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        LivingEntity first = Targeting.lookTarget(caster, range);
        if (first == null || !AllyDesignation.isValidTarget(caster, first)) return false;
        if (!(ctx.world() instanceof ServerLevel server)) return true;

        float potency = ctx.modifiers().get(SpellModifiers.POTENCY);
        Set<LivingEntity> hit = new HashSet<>();
        hit.add(caster);
        zap(server, caster, caster.getEyePosition(1), first, property(DefaultProperties.DAMAGE) * potency, hit);

        List<LivingEntity> second = chain(server, caster, first, property(SECONDARY_TARGETS), property(SECONDARY_DAMAGE) * potency, hit);
        for (LivingEntity e : second) chain(server, caster, e, property(TERTIARY_TARGETS), property(TERTIARY_DAMAGE) * potency, hit);
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    private List<LivingEntity> chain(ServerLevel server, Player caster, LivingEntity from, int max, float damage, Set<LivingEntity> hit) {
        List<LivingEntity> near = new ArrayList<>(EntityUtil.getLivingWithinRadius(property(CHAIN_RANGE), from.getX(), from.getY(), from.getZ(), server));
        near.removeIf(e -> hit.contains(e) || !AllyDesignation.isValidTarget(caster, e));
        near.sort(Comparator.comparingDouble(e -> e.distanceToSqr(from)));
        List<LivingEntity> picked = near.subList(0, Math.min(max, near.size()));
        for (LivingEntity e : picked) zap(server, caster, from.getEyePosition(1), e, damage, hit);
        return new ArrayList<>(picked);
    }

    private void zap(ServerLevel server, Player caster, Vec3 from, LivingEntity target, float damage, Set<LivingEntity> hit) {
        hit.add(target);
        target.hurt(MagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.SHOCK), damage);
        Vec3 to = target.getEyePosition(1);
        Vec3 step = to.subtract(from);
        int n = (int) Math.max(4, step.length() * 3);
        for (int i = 0; i <= n; i++) {
            Vec3 p = from.add(step.scale(i / (double) n));
            server.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0);
        }
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.LIGHTNING, SpellTypes.ATTACK, SpellAction.POINT, 25, 5, 50)
                .add(DefaultProperties.RANGE, 10f)
                .add(DefaultProperties.DAMAGE, 10f)
                .add(SECONDARY_DAMAGE).add(TERTIARY_DAMAGE).add(CHAIN_RANGE)
                .add(SECONDARY_TARGETS).add(TERTIARY_TARGETS)
                .build();
    }
}
