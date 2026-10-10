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
import dev.mcmoded.wizardryrestored.entity.GrappleHookEntity;
import dev.mcmoded.wizardryrestored.entity.ModEntities;
import dev.mcmoded.wizardryrestored.util.SpellStates;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/// Захват (непрерывное): выпускает лозу (`GrappleHookEntity`); пока заклинание удерживается, лоза подтягивает
/// заклинателя к блоку или существо к заклинателю. Отпустили — лоза исчезает.
public class Grapple extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        SpellStates.data(caster).putLong(SpellStates.GRAPPLE_UNTIL, server.getGameTime() + 3);
        boolean hasHook = !server.getEntitiesOfClass(GrappleHookEntity.class, caster.getBoundingBox().inflate(45),
                h -> h.getOwnerUUID() != null && h.getOwnerUUID().equals(caster.getUUID())).isEmpty();
        if (hasHook) return true;
        if (ctx.castingTicks() > 0) return true;
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        GrappleHookEntity hook = new GrappleHookEntity(ModEntities.GRAPPLE_HOOK.get(), server);
        hook.launch(caster, caster.getLookAngle().scale(range / GrappleHookEntity.MAX_FLIGHT));
        hook.setLifetime(-1);
        server.addFreshEntity(hook);
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected void playSound(Level world, LivingEntity entity, int castTicks, int duration) {
        if (castTicks == 0) super.playSound(world, entity, castTicks, duration);
    }

    @Override
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellTypes.UTILITY, SpellAction.GRAPPLE, 2, 0, 10)
                .add(DefaultProperties.RANGE, 20f)
                .build();
    }
}
