package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Молния бьёт туда, куда смотрит заклинатель.
public class LightningBolt extends Spell {

    @Override
    public boolean cast(PlayerCastContext ctx) {
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        HitResult hit = RayTracer.standardBlockRayTrace(ctx.world(), ctx.caster(), range, false, true, false);
        if (hit.getType() == HitResult.Type.MISS) return false;
        return strike(ctx.world(), ctx.caster(), hit.getLocation(), ctx.castingTicks());
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        LivingEntity target = ctx.target();
        if (target == null) return false;
        return strike(ctx.world(), ctx.caster(), target.position(), ctx.castingTicks());
    }

    @Override
    public boolean canCastByEntity() {
        return true;
    }

    private boolean strike(Level world, LivingEntity caster, Vec3 pos, int castTicks) {
        BlockPos block = BlockPos.containing(pos);
        // Как и в оригинале, молния нужна открытому небу: под землёй не бьёт.
        if (!world.canSeeSky(block.above())) return false;
        if (world instanceof ServerLevel server) {
            net.minecraft.world.entity.LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(server);
            if (bolt == null) return false;
            bolt.moveTo(pos);
            if (caster instanceof ServerPlayer player) bolt.setCause(player);
            server.addFreshEntity(bolt);
        }
        this.playSound(world, caster, castTicks, -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.LIGHTNING, SpellTypes.ATTACK, SpellAction.POINT, 40, 10, 80)
                .add(DefaultProperties.RANGE, 40f)
                .add(DefaultProperties.DAMAGE, 5f)
                .build();
    }
}
