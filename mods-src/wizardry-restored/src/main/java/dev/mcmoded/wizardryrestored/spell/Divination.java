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
import dev.mcmoded.wizardryrestored.util.SpellStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.List;

/// Прорицание: ищет руды вокруг (тег `forge:ores`) и указывает на одну из них — искрами в её сторону и подсказкой
/// с названием, расстоянием и «выше/ниже». Обычно — на ближайшую; с шансом, растущим от силы заклинания, — на
/// самую ценную из найденных.
public class Divination extends Spell {
    public static final String FOUND = "divination_found";
    private static final List<List<TagKey<Block>>> VALUE = List.of(
            List.of(Tags.Blocks.ORES_COAL),
            List.of(Tags.Blocks.ORES_IRON, Tags.Blocks.ORES_COPPER, Tags.Blocks.ORES_QUARTZ),
            List.of(Tags.Blocks.ORES_GOLD, Tags.Blocks.ORES_LAPIS, Tags.Blocks.ORES_REDSTONE),
            List.of(Tags.Blocks.ORES_DIAMOND, Tags.Blocks.ORES_EMERALD, Tags.Blocks.ORES_NETHERITE_SCRAP));

    private static int value(BlockState state) {
        for (int i = VALUE.size() - 1; i >= 0; i--) for (TagKey<Block> tag : VALUE.get(i)) if (state.is(tag)) return i + 1;
        return 2; // руды других модов — как железо
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        int r = (int) (property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.RANGE));
        BlockPos center = caster.blockPosition();
        BlockPos nearest = null, best = null;
        int bestValue = 0;
        double nearestDist = Double.MAX_VALUE, bestDist = Double.MAX_VALUE;
        for (BlockPos p : BlockPos.betweenClosed(center.offset(-r, -r, -r), center.offset(r, r, r))) {
            BlockState state = server.getBlockState(p);
            if (!state.is(Tags.Blocks.ORES)) continue;
            double d = p.distSqr(center);
            if (d < nearestDist) {
                nearestDist = d;
                nearest = p.immutable();
            }
            int v = value(state);
            if (v > bestValue || v == bestValue && d < bestDist) {
                bestValue = v;
                bestDist = d;
                best = p.immutable();
            }
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        if (nearest == null) {
            caster.displayClientMessage(Component.translatable("spell.wizardry_restored.divination.nothing"), true);
            return true;
        }
        float potency = ctx.modifiers().get(SpellModifiers.POTENCY);
        float chance = Math.min(1, property(BEST_CHANCE) + (potency - 1) * 2);
        BlockPos found = server.random.nextFloat() < chance ? best : nearest;
        SpellStates.data(caster).putLong(FOUND, found.asLong());

        Vec3 eye = caster.getEyePosition(1);
        Vec3 dir = Vec3.atCenterOf(found).subtract(eye).normalize();
        if (caster instanceof ServerPlayer sp)
            for (int i = 1; i <= 8; i++) {
                Vec3 p = eye.add(dir.scale(0.6 * i));
                server.sendParticles(sp, ParticleTypes.ENCHANT, true, p.x, p.y, p.z, 2, 0.02, 0.02, 0.02, 0);
            }
        int dy = found.getY() - caster.blockPosition().getY();
        String vert = dy > 2 ? "above" : dy < -2 ? "below" : "level";
        caster.displayClientMessage(Component.translatable("spell.wizardry_restored.divination.found",
                server.getBlockState(found).getBlock().getName(), (int) Math.sqrt(found.distSqr(caster.blockPosition())),
                Component.translatable("spell.wizardry_restored.divination." + vert)), true);
        return true;
    }

    private static final com.binaris.wizardry.api.content.spell.properties.SpellProperty<Float> BEST_CHANCE =
            com.binaris.wizardry.api.content.spell.properties.SpellProperty.floatProperty("best_ore_chance", 0.3f);

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.SORCERY, SpellTypes.UTILITY, SpellAction.POINT_DOWN, 20, 0, 100)
                .add(DefaultProperties.EFFECT_RADIUS, 16)
                .add(BEST_CHANCE)
                .build();
    }
}
