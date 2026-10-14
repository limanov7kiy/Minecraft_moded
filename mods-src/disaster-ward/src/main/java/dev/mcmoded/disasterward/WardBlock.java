package dev.mcmoded.disasterward;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Оберег: без рецепта и лута (noLootTable), взрывоустойчив, свет 7. Берётся только из творческой вкладки / командой. */
public class WardBlock extends Block {
    public WardBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(5.0F, 3600000.0F)
                .sound(SoundType.AMETHYST).lightLevel(s -> 7).noLootTable());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving) {
        super.onPlace(state, level, pos, old, moving);
        if (level instanceof ServerLevel sl && !old.is(this)) {
            WardData.get(sl).add(pos);
            WardApi.weatherRegister(level, pos, true);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (level instanceof ServerLevel sl && !state.is(newState.getBlock())) {
            WardData.get(sl).remove(pos);
            WardApi.weatherRegister(level, pos, false);
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    public static class Item extends BlockItem {
        public Item(Block block, Properties props) { super(block, props); }

        @Override
        public void appendHoverText(ItemStack stack, Level level, List<Component> tip, TooltipFlag flag) {
            tip.add(Component.translatable("tooltip.disaster_ward.ward", WardConfig.RADIUS.get()));
        }
    }
}
