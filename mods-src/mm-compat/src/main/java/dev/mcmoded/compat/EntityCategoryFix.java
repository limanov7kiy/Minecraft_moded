package dev.mcmoded.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Cataclysm Spellbooks регистрирует лучи-сущности (не мобов) с категорией MONSTER. Из-за этого
 * ванильная проверка пишет сотни строк «Entity ... has no attributes», а живые лучи занимают
 * место в лимите враждебных мобов. Ставим им MISC, как у остальных снарядов.
 */
final class EntityCategoryFix {
    private static final String[] MISC = {
        "cataclysm_spellbooks:extended_laser_beam",
        "cataclysm_spellbooks:extended_death_laser_beam",
    };

    static void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for (String id : MISC) {
                ResourceLocation key = ResourceLocation.tryParse(id);
                if (!ForgeRegistries.ENTITY_TYPES.containsKey(key)) continue;   // мод убрали из сборки
                EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(key);
                ObfuscationReflectionHelper.setPrivateValue(EntityType.class, type, MobCategory.MISC, "f_20536_");
            }
        });
    }

    private EntityCategoryFix() {}
}
