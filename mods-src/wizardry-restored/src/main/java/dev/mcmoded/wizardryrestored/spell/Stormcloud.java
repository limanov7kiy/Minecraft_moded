package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.wizardryrestored.entity.ModEntities;
import dev.mcmoded.wizardryrestored.entity.StormcloudEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/// Грозовая туча: появляется над заклинателем и медленно плывёт туда, куда он смотрел, поражая врагов под собой.
public class Stormcloud extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (ctx.world() instanceof ServerLevel server) {
            Vec3 look = caster.getLookAngle().multiply(1, 0, 1);
            if (look.lengthSqr() < 1.0e-4) look = Vec3.directionFromRotation(0, caster.getYRot()).multiply(1, 0, 1);
            look = look.normalize();
            StormcloudEntity cloud = new StormcloudEntity(ModEntities.STORMCLOUD.get(), server);
            cloud.setPos(caster.position().add(look.scale(3)).add(0, 3.5, 0));
            cloud.setDrift(look.scale(property(DefaultProperties.SPEED)));
            Constructs.configure(cloud, ctx, (int) (property(DefaultProperties.DURATION) * ctx.modifiers().get(SpellModifiers.DURATION)));
            server.addFreshEntity(cloud);
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.LIGHTNING, SpellTypes.CONSTRUCT, SpellAction.POINT, 40, 0, 200)
                .add(DefaultProperties.DURATION, 300)
                .add(DefaultProperties.DAMAGE, 4f)
                .add(DefaultProperties.SPEED, 0.12f)
                .build();
    }
}
