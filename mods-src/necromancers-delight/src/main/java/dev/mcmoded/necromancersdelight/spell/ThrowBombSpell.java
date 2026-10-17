package dev.mcmoded.necromancersdelight.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import dev.mcmoded.undeadrising.entity.projectile.FurBombEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

/// Holy Hand Grenade / Ghost Bomb / Wail of the Banshee (оригинал — `SpellThrowable` + бомбы Fish's Undead Rising,
/// WinDanesz): бросает бомбу порта FUR (`FurBombEntity`). Дальность `RANGE` задаёт скорость броска (как в Redux
/// `ProjectileSpell`, но с гравитацией бомбы 0,075). Мобы-маги кидают её в цель.
public class ThrowBombSpell extends Spell {
    private final Supplier<EntityType<FurBombEntity>> type;

    public ThrowBombSpell(Supplier<EntityType<FurBombEntity>> type) {
        this.type = type;
        this.soundValues(0.5f, 0.4f, 0.2f);
    }

    @Override
    public boolean canCastByEntity() {
        return true;
    }

    private float velocity(CastContext ctx, LivingEntity caster) {
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        float h = Math.max(0.5F, caster.getEyeHeight() - 0.3F);
        return range / Mth.sqrt(2.0F * h / 0.075F);
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!ctx.world().isClientSide) {
            FurBombEntity bomb = new FurBombEntity(type.get(), ctx.caster(), ctx.world());
            bomb.shootFromRotation(ctx.caster(), ctx.caster().getXRot(), ctx.caster().getYRot(), 0.0F, velocity(ctx, ctx.caster()), 1.0F);
            ctx.world().addFreshEntity(bomb);
        }
        ctx.caster().swing(ctx.hand());
        playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        LivingEntity target = ctx.target();
        if (target == null) return false;
        if (!ctx.world().isClientSide) {
            FurBombEntity bomb = new FurBombEntity(type.get(), ctx.caster(), ctx.world());
            double dx = target.getX() - ctx.caster().getX(), dz = target.getZ() - ctx.caster().getZ();
            double dy = target.getY(0.33) - bomb.getY();
            bomb.shoot(dx, dy + Math.sqrt(dx * dx + dz * dz) * 0.2, dz, velocity(ctx, ctx.caster()), 4.0F);
            ctx.world().addFreshEntity(bomb);
        }
        ctx.caster().swing(ctx.hand());
        playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.empty();
    }
}
