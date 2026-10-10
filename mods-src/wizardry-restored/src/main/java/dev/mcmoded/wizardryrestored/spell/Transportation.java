package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.wizardryrestored.block.ModBlocks;
import dev.mcmoded.wizardryrestored.util.SpellStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/// Перемещение по кругам из камней перемещения: четыре камня (`wizardry_restored:transportation_stone`) в двух
/// блоках от центра по сторонам света. Shift в центре круга — запомнить его; обычное сотворение — перенестись
/// к нему (и в другое измерение). Разрушенный круг забывается. С «Древним компасом» (`charm_transportation`)
/// помнится до 4 кругов, Shift вне круга переключает выбранный.
public class Transportation extends Spell {
    public static final String CIRCLES = "transport_circles";
    public static final String SELECTED = "transport_selected";

    public static boolean isCircle(Level level, BlockPos center) {
        for (Direction d : Direction.Plane.HORIZONTAL)
            if (!level.getBlockState(center.relative(d, 2)).is(ModBlocks.TRANSPORTATION_STONE.get())) return false;
        return true;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        CompoundTag data = SpellStates.data(caster);
        ListTag circles = data.getList(CIRCLES, Tag.TAG_COMPOUND);
        int max = ArtifactChannel.isEquipped(caster, EBItems.CHARM_TRANSPORTATION.get()) ? 4 : 1;
        BlockPos here = caster.blockPosition();

        if (caster.isShiftKeyDown()) {
            if (isCircle(server, here)) {
                CompoundTag c = new CompoundTag();
                c.putString("dim", server.dimension().location().toString());
                c.putLong("pos", here.asLong());
                circles.removeIf(t -> t.equals(c));
                circles.add(c);
                while (circles.size() > max) circles.remove(0);
                data.put(CIRCLES, circles);
                data.putInt(SELECTED, circles.size() - 1);
                caster.displayClientMessage(Component.translatable("spell.wizardry_restored.transportation.marked", circles.size(), max), true);
                server.sendParticles(ParticleTypes.END_ROD, caster.getX(), caster.getY() + 0.1, caster.getZ(), 40, 1.2, 0.05, 1.2, 0.01);
                return true;
            }
            if (circles.size() > 1) {
                int sel = (data.getInt(SELECTED) + 1) % circles.size();
                data.putInt(SELECTED, sel);
                BlockPos p = BlockPos.of(circles.getCompound(sel).getLong("pos"));
                caster.displayClientMessage(Component.translatable("spell.wizardry_restored.transportation.selected",
                        sel + 1, p.getX(), p.getY(), p.getZ(), circles.getCompound(sel).getString("dim")), true);
                return true;
            }
            caster.displayClientMessage(Component.translatable("spell.wizardry_restored.transportation.no_circle"), true);
            return false;
        }

        if (circles.isEmpty()) {
            caster.displayClientMessage(Component.translatable("spell.wizardry_restored.transportation.no_circle"), true);
            return false;
        }
        int sel = Math.min(data.getInt(SELECTED), circles.size() - 1);
        CompoundTag c = circles.getCompound(sel);
        ServerLevel target = server.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(c.getString("dim"))));
        BlockPos dest = BlockPos.of(c.getLong("pos"));
        if (target == null || !isCircle(target, dest)) {
            circles.remove(sel);
            data.putInt(SELECTED, 0);
            caster.displayClientMessage(Component.translatable("spell.wizardry_restored.transportation.broken"), true);
            return false;
        }
        server.sendParticles(ParticleTypes.PORTAL, caster.getX(), caster.getY() + 1, caster.getZ(), 60, 0.4, 0.8, 0.4, 0.3);
        double x = dest.getX() + 0.5, y = dest.getY(), z = dest.getZ() + 0.5;
        if (target != server && caster instanceof ServerPlayer sp) sp.teleportTo(target, x, y, z, caster.getYRot(), caster.getXRot());
        else {
            caster.setPos(x, y, z);
            caster.teleportTo(x, y, z);
        }
        caster.fallDistance = 0;
        target.sendParticles(ParticleTypes.PORTAL, x, y + 1, z, 60, 0.4, 0.8, 0.4, 0.3);
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
