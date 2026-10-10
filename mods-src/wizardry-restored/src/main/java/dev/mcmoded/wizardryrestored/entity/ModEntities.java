package dev.mcmoded.wizardryrestored.entity;

import dev.mcmoded.wizardryrestored.WizardryRestored;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/// Сущности заклинаний этапа 3: конструкции (силовое поле, тотемы, чёрная дыра, грозовая туча) и приманка.
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, WizardryRestored.MOD_ID);

    public static final RegistryObject<EntityType<ForcefieldEntity>> FORCEFIELD = construct("forcefield", ForcefieldEntity::new, 6f, 6f, 5);
    public static final RegistryObject<EntityType<RadiantTotemEntity>> RADIANT_TOTEM = construct("radiant_totem", RadiantTotemEntity::new, 0.6f, 1.5f, 10);
    public static final RegistryObject<EntityType<WitheringTotemEntity>> WITHERING_TOTEM = construct("withering_totem", WitheringTotemEntity::new, 0.6f, 1.5f, 10);
    public static final RegistryObject<EntityType<BlackHoleEntity>> BLACK_HOLE = construct("black_hole", BlackHoleEntity::new, 1f, 1f, 10);
    public static final RegistryObject<EntityType<StormcloudEntity>> STORMCLOUD = construct("stormcloud", StormcloudEntity::new, 4f, 1f, 1);
    public static final RegistryObject<EntityType<SnareEntity>> SNARE = construct("snare", SnareEntity::new, 0.8f, 0.1f, 20);
    public static final RegistryObject<EntityType<ThornsEntity>> THORNS = construct("thorns", ThornsEntity::new, 1f, 1f, 20);
    public static final RegistryObject<EntityType<LightningDiscEntity>> LIGHTNING_DISC = construct("lightning_disc", LightningDiscEntity::new, 1f, 0.2f, 1);
    public static final RegistryObject<EntityType<LightningHammerEntity>> LIGHTNING_HAMMER = construct("lightning_hammer", LightningHammerEntity::new, 0.8f, 1.6f, 2);
    public static final RegistryObject<EntityType<GrappleHookEntity>> GRAPPLE_HOOK = construct("grapple_hook", GrappleHookEntity::new, 0.25f, 0.25f, 1);
    public static final RegistryObject<EntityType<DecoyEntity>> DECOY = ENTITY_TYPES.register("decoy", () ->
            EntityType.Builder.of(DecoyEntity::new, MobCategory.MISC).sized(0.6f, 1.8f).clientTrackingRange(10)
                    .build(WizardryRestored.MOD_ID + ":decoy"));

    public static final RegistryObject<EntityType<StatueEntity>> STATUE = ENTITY_TYPES.register("statue", () ->
            EntityType.Builder.<StatueEntity>of(StatueEntity::new, MobCategory.MISC).sized(0.6f, 1.8f).fireImmune().clientTrackingRange(10)
                    .build(WizardryRestored.MOD_ID + ":statue"));
    public static final RegistryObject<EntityType<PhoenixEntity>> PHOENIX = ENTITY_TYPES.register("phoenix", () ->
            EntityType.Builder.of(PhoenixEntity::new, MobCategory.MONSTER).sized(0.6f, 1.8f).fireImmune().clientTrackingRange(8)
                    .build(WizardryRestored.MOD_ID + ":phoenix"));

    private ModEntities() {
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> construct(String id, EntityType.EntityFactory<T> factory,
                                                                              float width, float height, int updateInterval) {
        return ENTITY_TYPES.register(id, () -> EntityType.Builder.of(factory, MobCategory.MISC).sized(width, height)
                .fireImmune().clientTrackingRange(10).updateInterval(updateInterval).build(WizardryRestored.MOD_ID + ":" + id));
    }

    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(DECOY.get(), DecoyEntity.createAttributes().build());
        event.put(PHOENIX.get(), PhoenixEntity.createAttributes().build());
    }
}
