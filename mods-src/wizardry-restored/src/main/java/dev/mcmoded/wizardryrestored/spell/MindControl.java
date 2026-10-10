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

/// Контроль разума: существо на 30 секунд переходит на сторону заклинателя. Не действует на боссов и сильных существ.
public class MindControl extends Spell {
    private static final float MAX_HEALTH = 60f;

    @Override
    public boolean cast(PlayerCastContext ctx) {
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        LivingEntity target = Targeting.lookTarget(ctx.caster(), range);
        if (!(target instanceof Mob mob) || mob.getMaxHealth() > MAX_HEALTH * ctx.modifiers().get(SpellModifiers.POTENCY)
                || mob.getType().is(net.minecraftforge.common.Tags.EntityTypes.BOSSES)) return false;
        if (ctx.world() instanceof ServerLevel server) {
            int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            var d = SpellStates.data(mob);
            d.putLong(SpellStates.MIND_CONTROL_UNTIL, server.getGameTime() + duration);
            d.putUUID(SpellStates.MIND_CONTROLLER, ctx.caster().getUUID());
            mob.setTarget(null);
            server.sendParticles(ParticleTypes.WITCH, mob.getX(), mob.getEyeY() + 0.3, mob.getZ(), 30, 0.3, 0.3, 0.3, 0.05);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.NECROMANCY, SpellTypes.ATTACK, SpellAction.POINT, 40, 10, 150)
                .add(DefaultProperties.RANGE, 8f)
                .add(DefaultProperties.EFFECT_DURATION, 600)
                .build();
    }
}
