package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Мгновенный короткий телепорт туда, куда смотрит заклинатель.
public class Blink extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        HitResult hit = RayTracer.standardBlockRayTrace(ctx.world(), caster, range, false, true, false);
        // Не нашли блок — прыгаем на всю дальность по взгляду
        Vec3 dest = hit.getType() == HitResult.Type.MISS
                ? caster.getEyePosition(1).add(caster.getLookAngle().scale(range))
                : hit.getLocation().subtract(caster.getLookAngle().scale(0.5));
        Vec3 safe = EntityUtil.findSpaceForTeleport(caster, dest, false);
        if (safe == null) return false;

        if (ctx.world() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.PORTAL, caster.getX(), caster.getY() + 1, caster.getZ(), 30, 0.3, 0.6, 0.3, 0.2);
            if (caster.isPassenger()) caster.stopRiding();
            caster.setPos(safe.x, safe.y, safe.z);        // сервер — сразу; клиенту — пакет телепорта ниже
            caster.teleportTo(safe.x, safe.y, safe.z);
            caster.fallDistance = 0;
            server.sendParticles(ParticleTypes.PORTAL, safe.x, safe.y + 1, safe.z, 30, 0.3, 0.6, 0.3, 0.2);
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.SORCERY, SpellTypes.UTILITY, SpellAction.POINT, 15, 0, 25)
                .add(DefaultProperties.RANGE, 25f)
                .build();
    }
}
