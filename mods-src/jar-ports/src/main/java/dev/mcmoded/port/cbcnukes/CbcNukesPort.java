package dev.mcmoded.port.cbcnukes;

import com.simibubi.create.AllTags;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.world.level.block.Block;

/**
 * Переходник для CBC: Nuclear 2.1.1 (snackpirate, MIT) под Create Big Cannons 5.11.4: у CBC
 * CBCBuilderTransformers.safeNbt() остался, а его Forge-реализацию удалили (NoSuchMethodError при
 * регистрации блоков). Поведение — как в CBC 1.20 (rbasamoyai): блоку ставится тег create:safe_nbt.
 */
public final class CbcNukesPort {
    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> safeNbt() {
        return b -> b.tag(AllTags.AllBlockTags.SAFE_NBT.tag);
    }

    private CbcNukesPort() {}
}
