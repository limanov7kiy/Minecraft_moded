package dev.mcmoded.necromancersdelight.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.RegistryObject;

import static dev.mcmoded.necromancersdelight.NecromancersDelight.ENTITIES;

/// Свои мобы Necromancer's Delight (оригинал — `NDEntities`): пиявки-миньоны. Id — как в оригинале.
public final class NdEntities {
    private NdEntities() {}

    public static final RegistryObject<EntityType<ManaLeech>> MANA_LEECH = ENTITIES.register("mana_leech_minion",
            () -> EntityType.Builder.<ManaLeech>of(ManaLeech::new, MobCategory.MISC).sized(0.8F, 0.3F).clientTrackingRange(8)
                    .build("necromancersdelight:mana_leech_minion"));
    public static final RegistryObject<EntityType<ElementalLeech>> ELEMENTAL_LEECH = ENTITIES.register("elemental_leech_minion",
            () -> EntityType.Builder.<ElementalLeech>of(ElementalLeech::new, MobCategory.MISC).sized(0.8F, 0.3F).clientTrackingRange(8)
                    .build("necromancersdelight:elemental_leech_minion"));

    public static void init() {}

    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(MANA_LEECH.get(), LeechMinion.createAttributes().build());
        event.put(ELEMENTAL_LEECH.get(), ElementalLeech.createAttributes().build());
    }
}
