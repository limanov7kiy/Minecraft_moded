package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// Обычный снежок туда, куда смотрит заклинатель.
public class Snowball extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        return throwAt(ctx.world(), ctx.caster(), ctx.caster().getLookAngle(), ctx.castingTicks());
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if (ctx.target() == null) return false;
        Vec3 dir = ctx.target().getEyePosition(1).subtract(ctx.caster().getEyePosition(1)).normalize();
        return throwAt(ctx.world(), ctx.caster(), dir, ctx.castingTicks());
    }

    @Override
    public boolean canCastByEntity() {
        return true;
    }

    private boolean throwAt(Level world, LivingEntity caster, Vec3 dir, int ticks) {
        if (!world.isClientSide) {
            net.minecraft.world.entity.projectile.Snowball ball = new net.minecraft.world.entity.projectile.Snowball(world, caster);
            ball.shoot(dir.x, dir.y, dir.z, 1.5f, 1.0f);
            world.addFreshEntity(ball);
        }
        this.playSound(world, caster, ticks, -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.ICE, SpellTypes.PROJECTILE, SpellAction.POINT, 1, 0, 1)
                .add(DefaultProperties.RANGE, 15f)
                .build();
    }
}
