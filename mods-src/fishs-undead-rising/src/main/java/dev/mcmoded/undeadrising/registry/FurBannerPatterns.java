package dev.mcmoded.undeadrising.registry;

import dev.mcmoded.undeadrising.UndeadRising;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/// Узоры флагов (оригинал: Fish's Undead Rising (Fish), `FURItemRegistry.addBanner`). Текстуры —
/// `textures/entity/{banner,shield}/<имя>.png` (атласы ванили подхватывают их из любого namespace).
public final class FurBannerPatterns {
    private FurBannerPatterns() {}

    public static final DeferredRegister<BannerPattern> PATTERNS = DeferredRegister.create(Registries.BANNER_PATTERN, UndeadRising.MOD_ID);

    /// Хэш узора — как в оригинале; им же записан узор в NBT щита Forsaken.
    public static final String SKELETONKING_HASH = "mod_lavacow.skeletonking";
    public static final RegistryObject<BannerPattern> SKELETONKING = PATTERNS.register("skeletonking", () -> new BannerPattern(SKELETONKING_HASH));
}
