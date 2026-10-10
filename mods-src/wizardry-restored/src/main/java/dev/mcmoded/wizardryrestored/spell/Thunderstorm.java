package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.wizardryrestored.util.SpellStates;
import dev.mcmoded.wizardryrestored.util.TempBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Гроза: молнии бьют до 10 врагов вокруг, каждый удар перескакивает на соседей.
public class Thunderstorm extends Spell {
    private static final SpellProperty<Integer> BOLTS = SpellProperty.intProperty("lightning_bolts", 10);
    private static final SpellProperty<Float> CHAIN_DAMAGE = SpellProperty.floatProperty("secondary_damage", 10f);
    private static final SpellProperty<Float> CHAIN_RANGE = SpellProperty.floatProperty("secondary_range", 10f);
    private static final SpellProperty<Integer> CHAIN_TARGETS = SpellProperty.intProperty("secondary_max_targets", 3);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        float radius = ctx.modifiers().get(SpellModifiers.BLAST, property(DefaultProperties.EFFECT_RADIUS));
        java.util.List<LivingEntity> targets = new java.util.ArrayList<>(EntityUtil.getLivingWithinRadius(radius, caster.getX(), caster.getY(), caster.getZ(), server));
        targets.removeIf(t -> t == caster || !AllyDesignation.isValidTarget(caster, t));
        targets.sort(java.util.Comparator.comparingDouble(t -> t.distanceToSqr(caster)));
        int bolts = property(BOLTS);
        for (int i = 0; i < bolts; i++) {
            Vec3 at = i < targets.size() ? targets.get(i).position()
                    : caster.position().add((server.random.nextDouble() - 0.5) * 2 * radius, 0, (server.random.nextDouble() - 0.5) * 2 * radius);
            net.minecraft.world.entity.LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(server);
            if (bolt == null) continue;
            bolt.moveTo(server.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, BlockPos.containing(at)).getCenter());
            if (caster instanceof net.minecraft.server.level.ServerPlayer sp) bolt.setCause(sp);
            server.addFreshEntity(bolt);
            if (i < targets.size()) {
                LivingEntity t = targets.get(i);
                EntityUtil.getLivingWithinRadius(property(CHAIN_RANGE), t.getX(), t.getY(), t.getZ(), server).stream()
                        .filter(n -> n != t && n != caster && AllyDesignation.isValidTarget(caster, n))
                        .limit(property(CHAIN_TARGETS))
                        .forEach(n -> n.hurt(MagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.SHOCK),
                                property(CHAIN_DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY)));
            }
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.LIGHTNING, SpellTypes.ATTACK, SpellAction.POINT_UP, 100, 20, 250)
                .add(DefaultProperties.EFFECT_RADIUS, 10)
                .add(BOLTS).add(CHAIN_DAMAGE).add(CHAIN_RANGE).add(CHAIN_TARGETS)
                .build();
    }
}
