package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/// Устрашающий рык: существа вокруг бросают цель и разбегаются, ослабленные страхом.
public class Intimidate extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        float radius = ctx.modifiers().get(SpellModifiers.BLAST, property(DefaultProperties.EFFECT_RADIUS));
        int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
        boolean any = false;
        if (!ctx.world().isClientSide) {
            for (LivingEntity e : EntityUtil.getLivingWithinRadius(radius, caster.getX(), caster.getY(), caster.getZ(), ctx.world())) {
                if (!(e instanceof PathfinderMob mob) || !AllyDesignation.isValidTarget(caster, mob)) continue;
                mob.setTarget(null);
                mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, property(DefaultProperties.EFFECT_STRENGTH)));
                Vec3 away = DefaultRandomPos.getPosAway(mob, 16, 7, caster.position());
                if (away != null) mob.getNavigation().moveTo(away.x, away.y, away.z, 1.4);
                any = true;
            }
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return any || ctx.world().isClientSide;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.NECROMANCY, SpellTypes.ALTERATION, SpellAction.POINT_UP, 20, 0, 100)
                .add(DefaultProperties.EFFECT_RADIUS, 8)
                .add(DefaultProperties.EFFECT_DURATION, 600)
                .add(DefaultProperties.EFFECT_STRENGTH, 0)
                .build();
    }
}
