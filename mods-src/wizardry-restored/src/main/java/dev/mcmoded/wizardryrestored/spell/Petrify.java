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

/// Окаменение: цель превращается в «камень» на 45 секунд — парализована, почти неуязвима, покрыта каменной пылью.
public class Petrify extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        LivingEntity target = Targeting.lookTarget(ctx.caster(), range);
        if (target == null || !AllyDesignation.isValidTarget(ctx.caster(), target)
                || target.getType().is(net.minecraftforge.common.Tags.EntityTypes.BOSSES)) return false;
        if (ctx.world() instanceof ServerLevel server) {
            int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            target.addEffect(new MobEffectInstance(com.binaris.wizardry.setup.registries.EBMobEffects.PARALYSIS.get(), duration, 0));
            target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 3));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 6));
            server.sendParticles(new net.minecraft.core.particles.BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
                    target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(), 60, 0.3, 0.6, 0.3, 0.1);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellTypes.ATTACK, SpellAction.POINT, 40, 20, 100)
                .add(DefaultProperties.RANGE, 10f)
                .add(DefaultProperties.EFFECT_DURATION, 900)
                .build();
    }
}
