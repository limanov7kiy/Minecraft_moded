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

/// Перемещение: с зажатым Shift запоминает текущую точку; обычное применение переносит к ней
/// (в том же измерении). Замена кругу из камней перемещения оригинала, пока их нет (этап 4).
public class Transportation extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        var d = SpellStates.data(caster);
        if (caster.isShiftKeyDown()) {
            net.minecraft.nbt.CompoundTag mark = new net.minecraft.nbt.CompoundTag();
            mark.putString("dim", server.dimension().location().toString());
            mark.putDouble("x", caster.getX()); mark.putDouble("y", caster.getY()); mark.putDouble("z", caster.getZ());
            d.put(SpellStates.MARK, mark);
            caster.displayClientMessage(net.minecraft.network.chat.Component.translatable("spell.wizardry_restored.transportation.marked"), true);
            server.sendParticles(ParticleTypes.END_ROD, caster.getX(), caster.getY() + 0.1, caster.getZ(), 40, 0.6, 0.05, 0.6, 0.01);
            return true;
        }
        if (!d.contains(SpellStates.MARK)) return false;
        var mark = d.getCompound(SpellStates.MARK);
        if (!mark.getString("dim").equals(server.dimension().location().toString())) {
            caster.displayClientMessage(net.minecraft.network.chat.Component.translatable("spell.wizardry_restored.transportation.other_dimension"), true);
            return false;
        }
        server.sendParticles(ParticleTypes.PORTAL, caster.getX(), caster.getY() + 1, caster.getZ(), 60, 0.4, 0.8, 0.4, 0.3);
        caster.setPos(mark.getDouble("x"), mark.getDouble("y"), mark.getDouble("z"));
        caster.teleportTo(mark.getDouble("x"), mark.getDouble("y"), mark.getDouble("z"));
        caster.fallDistance = 0;
        server.sendParticles(ParticleTypes.PORTAL, caster.getX(), caster.getY() + 1, caster.getZ(), 60, 0.4, 0.8, 0.4, 0.3);
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellTypes.UTILITY, SpellAction.POINT_UP, 100, 20, 100)
                .build();
    }
}
