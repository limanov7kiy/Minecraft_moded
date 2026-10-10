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

/// Призрачный конь: осёдланный, приручённый, быстрый; исчезает через 30 секунд без всадника, ничего не роняет.
public class SummonSpiritHorse extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        net.minecraft.world.entity.animal.horse.Horse horse = EntityType.HORSE.create(server);
        if (horse == null) return false;
        Vec3 at = EntityUtil.findSpaceForTeleport(horse, caster.position().add(caster.getLookAngle().multiply(2, 0, 2)), false);
        if (at == null) return false;
        horse.moveTo(at.x, at.y, at.z, caster.getYRot(), 0);
        horse.tameWithName(caster);
        horse.equipSaddle(null);
        horse.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(0.33);
        horse.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.JUMP_STRENGTH).setBaseValue(0.9);
        horse.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(30);
        horse.setHealth(30);
        horse.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
        SpellStates.data(horse).putBoolean(SpellStates.SPIRIT_HORSE, true);
        server.addFreshEntity(horse);
        server.sendParticles(ParticleTypes.SOUL, at.x, at.y + 1, at.z, 30, 0.5, 0.6, 0.5, 0.02);
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.EARTH, SpellTypes.MINION, SpellAction.SUMMON, 50, 10, 150)
                .add(DefaultProperties.SUMMON_RADIUS, 2)
                .build();
    }
}
