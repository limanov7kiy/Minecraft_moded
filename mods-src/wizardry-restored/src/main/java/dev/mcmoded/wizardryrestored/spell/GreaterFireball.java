package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// Большой огненный шар (как у гаста), взрывается при попадании. Разрушение блоков — по правилу mobGriefing.
public class GreaterFireball extends Spell {
    private static final SpellProperty<Integer> EXPLOSION_POWER = SpellProperty.intProperty("explosion_power", 1);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        return launch(ctx.world(), ctx.caster(), ctx.caster().getLookAngle(), ctx.modifiers(), ctx.castingTicks());
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if (ctx.target() == null) return false;
        Vec3 dir = ctx.target().position().subtract(ctx.caster().getEyePosition(1)).normalize();
        return launch(ctx.world(), ctx.caster(), dir, ctx.modifiers(), ctx.castingTicks());
    }

    @Override
    public boolean canCastByEntity() {
        return true;
    }

    private boolean launch(Level world, LivingEntity caster, Vec3 dir, SpellModifiers modifiers, int ticks) {
        if (!world.isClientSide) {
            int power = Math.max(1, Math.round(property(EXPLOSION_POWER) * modifiers.get(SpellModifiers.BLAST)));
            LargeFireball ball = new LargeFireball(world, caster, dir.x, dir.y, dir.z, power);
            Vec3 eye = caster.getEyePosition(1).add(dir.scale(1.5));
            ball.setPos(eye.x, eye.y, eye.z);
            world.addFreshEntity(ball);
        }
        this.playSound(world, caster, ticks, -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.FIRE, SpellTypes.PROJECTILE, SpellAction.POINT, 20, 5, 30)
                .add(DefaultProperties.RANGE, 20f)
                .add(DefaultProperties.DAMAGE, 6f)
                .add(EXPLOSION_POWER)
                .build();
    }
}
