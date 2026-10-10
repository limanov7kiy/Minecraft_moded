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

/// Превращение существа в «родственное»: корова ↔ грибная корова, зомби ↔ кадавр, скелет ↔ зимогор,
/// паук ↔ пещерный паук, слизень ↔ магмовый куб, житель ↔ ведьма, лошадь → зомби-лошадь → лошадь-скелет → лошадь.
public class Metamorphosis extends Spell {
    private static final java.util.Map<EntityType<?>, EntityType<? extends Mob>> NEXT = java.util.Map.ofEntries(
            java.util.Map.entry(EntityType.COW, EntityType.MOOSHROOM), java.util.Map.entry(EntityType.MOOSHROOM, EntityType.COW),
            java.util.Map.entry(EntityType.ZOMBIE, EntityType.HUSK), java.util.Map.entry(EntityType.HUSK, EntityType.ZOMBIE),
            java.util.Map.entry(EntityType.SKELETON, EntityType.STRAY), java.util.Map.entry(EntityType.STRAY, EntityType.SKELETON),
            java.util.Map.entry(EntityType.SPIDER, EntityType.CAVE_SPIDER), java.util.Map.entry(EntityType.CAVE_SPIDER, EntityType.SPIDER),
            java.util.Map.entry(EntityType.SLIME, EntityType.MAGMA_CUBE), java.util.Map.entry(EntityType.MAGMA_CUBE, EntityType.SLIME),
            java.util.Map.entry(EntityType.VILLAGER, EntityType.WITCH), java.util.Map.entry(EntityType.WITCH, EntityType.VILLAGER),
            java.util.Map.entry(EntityType.HORSE, EntityType.ZOMBIE_HORSE), java.util.Map.entry(EntityType.ZOMBIE_HORSE, EntityType.SKELETON_HORSE),
            java.util.Map.entry(EntityType.SKELETON_HORSE, EntityType.HORSE), java.util.Map.entry(EntityType.PIG, EntityType.HOGLIN),
            java.util.Map.entry(EntityType.HOGLIN, EntityType.PIG));

    @Override
    public boolean cast(PlayerCastContext ctx) {
        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        LivingEntity target = Targeting.lookTarget(ctx.caster(), range);
        if (!(target instanceof Mob mob) || !NEXT.containsKey(mob.getType())) return false;
        if (ctx.world() instanceof ServerLevel server) {
            Mob result = mob.convertTo(NEXT.get(mob.getType()), true);
            if (result == null) return false;
            server.sendParticles(ParticleTypes.WITCH, result.getX(), result.getY() + 1, result.getZ(), 40, 0.4, 0.6, 0.4, 0.05);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.NECROMANCY, SpellTypes.UTILITY, SpellAction.POINT, 15, 0, 30)
                .add(DefaultProperties.RANGE, 10f)
                .build();
    }
}
