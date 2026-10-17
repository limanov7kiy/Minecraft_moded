package dev.mcmoded.undeadrising.registry;

import dev.mcmoded.undeadrising.UndeadRising;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/// Теги сущностей (оригинал: Fish's Undead Rising (Fish), `init/FURTagRegistry`); содержимое —
/// `data/mod_lavacow/tags/entity_types/*.json`.
public final class FurTags {
    private FurTags() {}

    /// Нежить-зомби: цели паразитов и весп.
    public static final TagKey<EntityType<?>> ZOMBIES = entity("zombies");
    /// Не-игроки, на которых охотятся и цепляются паразиты; с них же при смерти могут вылезти паразиты.
    public static final TagKey<EntityType<?>> PARASITE_TARGETS = entity("parasite_targets");
    /// Не-игроки, на которых охотятся веспы (удвоенный урон по ним, половинный — от них).
    public static final TagKey<EntityType<?>> VESPA_TARGETS = entity("vespa_targets");

    private static TagKey<EntityType<?>> entity(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(UndeadRising.MOD_ID, name));
    }
}
