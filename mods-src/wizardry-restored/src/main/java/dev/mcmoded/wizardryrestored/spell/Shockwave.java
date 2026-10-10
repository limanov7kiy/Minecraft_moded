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
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/// Ударная волна: ранит и отбрасывает всех вокруг заклинателя; чем ближе к центру, тем сильнее.
public class Shockwave extends Spell {
    private static final SpellProperty<Float> MAX_REPULSION = SpellProperty.floatProperty("max_repulsion_velocity", 3f);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        float radius = ctx.modifiers().get(SpellModifiers.BLAST, property(DefaultProperties.EFFECT_RADIUS));
        float damage = ctx.modifiers().get(SpellModifiers.POTENCY, property(DefaultProperties.DAMAGE));

        if (ctx.world() instanceof ServerLevel server) {
            for (LivingEntity target : EntityUtil.getLivingWithinRadius(radius, caster.getX(), caster.getY(), caster.getZ(), server)) {
                if (target == caster || !AllyDesignation.isValidTarget(caster, target)) continue;
                Vec3 away = target.position().subtract(caster.position());
                double dist = Math.max(0.5, away.length());
                double strength = property(MAX_REPULSION) * (1 - dist / (radius + 1));
                Vec3 push = away.normalize().scale(strength).add(0, 0.4 + strength * 0.3, 0);

                target.hurt(MagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.BLAST), damage);
                if (!dev.mcmoded.wizardryrestored.util.Artifacts.anchored(target)) target.setDeltaMovement(push);
                target.hurtMarked = true;
                if (target instanceof ServerPlayer p) p.connection.send(new ClientboundSetEntityMotionPacket(target));
            }
            server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, caster.getX(), caster.getY() + 0.1, caster.getZ(), 1, 0, 0, 0, 0);
            server.sendParticles(ParticleTypes.CLOUD, caster.getX(), caster.getY() + 0.1, caster.getZ(), 60, radius / 2, 0.1, radius / 2, 0.2);
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.SORCERY, SpellTypes.ATTACK, SpellAction.POINT_DOWN, 65, 20, 150)
                .add(DefaultProperties.EFFECT_RADIUS, 5)
                .add(DefaultProperties.DAMAGE, 8f)
                .add(MAX_REPULSION)
                .build();
    }
}
