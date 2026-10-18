package dev.mcmoded.undeadrising.registry;

import dev.mcmoded.undeadrising.UndeadRising;
import dev.mcmoded.undeadrising.block.FurShroomBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/// Блоки порта (оригинал: Fish's Undead Rising (Fish), `init/FURBlockRegistry`): пока только грибы — их едят/держат
/// в руке заклинание Trial of Mushrooms и талисман грибного миньона Necromancer's Delight. Генерация —
/// `data/mod_lavacow/worldgen/` + biome modifiers `add_*_shroom.json` (биомы и частоты — из оригинала 1.16.5).
public final class FurBlocks {
    private FurBlocks() {}

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, UndeadRising.MOD_ID);

    public static final RegistryObject<Block> GLOWSHROOM = shroom("glowshroom", MapColor.COLOR_CYAN, 15, true);
    public static final RegistryObject<Block> BLOODTOOTH_SHROOM = shroom("bloodtooth_shroom", MapColor.COLOR_RED, 1, false);
    public static final RegistryObject<Block> CORDY_SHROOM = shroom("cordy_shroom", MapColor.COLOR_ORANGE, 1, false);
    public static final RegistryObject<Block> VEIL_SHROOM = shroom("veil_shroom", MapColor.COLOR_ORANGE, 1, false);

    private static RegistryObject<Block> shroom(String name, MapColor color, int light, boolean glowing) {
        return BLOCKS.register(name, () -> new FurShroomBlock(BlockBehaviour.Properties.of().mapColor(color).noCollission()
                .randomTicks().instabreak().sound(SoundType.GRASS).lightLevel(s -> light).pushReaction(PushReaction.DESTROY)
                .offsetType(BlockBehaviour.OffsetType.NONE), glowing));
    }
}
