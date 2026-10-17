package dev.mcmoded.undeadrising.registry;

import dev.mcmoded.undeadrising.UndeadRising;
import dev.mcmoded.undeadrising.entity.FURTameableEntity;
import dev.mcmoded.undeadrising.entity.ForsakenEntity;
import dev.mcmoded.undeadrising.entity.BoneWormEntity;
import dev.mcmoded.undeadrising.entity.MimicEntity;
import dev.mcmoded.undeadrising.entity.ParasiteEntity;
import dev.mcmoded.undeadrising.entity.SalamanderEntity;
import dev.mcmoded.undeadrising.entity.WetaEntity;
import dev.mcmoded.undeadrising.entity.projectile.WarSmallFireballEntity;
import net.minecraft.world.level.block.Blocks;
import dev.mcmoded.undeadrising.entity.VespaCocoonEntity;
import dev.mcmoded.undeadrising.entity.floating.BansheeEntity;
import dev.mcmoded.undeadrising.entity.floating.FloatingMobEntity;
import dev.mcmoded.undeadrising.entity.flying.FlyingMobEntity;
import dev.mcmoded.undeadrising.entity.flying.VespaEntity;
import dev.mcmoded.undeadrising.entity.projectile.AcidJetEntity;
import dev.mcmoded.undeadrising.entity.projectile.DeathCoilEntity;
import dev.mcmoded.undeadrising.entity.projectile.FlameJetEntity;
import dev.mcmoded.undeadrising.entity.unburied.FrigidEntity;
import dev.mcmoded.undeadrising.entity.unburied.MummyEntity;
import dev.mcmoded.undeadrising.entity.unburied.MycosisEntity;
import dev.mcmoded.undeadrising.entity.unburied.UnburiedEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/// Типы сущностей порта (оригинал: Fish's Undead Rising (Fish), `init/FUREntityRegistry`): размеры, атрибуты, правила
/// появления. Новый моб: тип здесь → атрибуты в {@link #onAttributes} → правило в {@link #onSpawnPlacements} (если
/// появляется сам) → рендер в `client/ClientSetup` → яйцо в {@link FurItems} → biome modifier в
/// `data/mod_lavacow/forge/biome_modifier/` (или structure modifier в `forge/structure_modifier/`) → добыча
/// `data/mod_lavacow/loot_tables/entities/<id>.json` → lang.
public final class FurEntities {
    private FurEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, UndeadRising.MOD_ID);

    // --- Семейство Unburied (entities/tameable/unburied) ---
    public static final RegistryObject<EntityType<UnburiedEntity>> UNBURIED = ENTITIES.register("unburied",
            () -> EntityType.Builder.<UnburiedEntity>of(UnburiedEntity::new, MobCategory.MONSTER).sized(1.0F, 1.95F).build(id("unburied")));
    public static final RegistryObject<EntityType<MummyEntity>> MUMMY = ENTITIES.register("mummy",
            () -> EntityType.Builder.<MummyEntity>of(MummyEntity::new, MobCategory.MONSTER).sized(1.0F, 1.95F).build(id("mummy")));
    public static final RegistryObject<EntityType<MycosisEntity>> MYCOSIS = ENTITIES.register("mycosis",
            () -> EntityType.Builder.<MycosisEntity>of(MycosisEntity::new, MobCategory.MONSTER).sized(1.0F, 1.95F).build(id("mycosis")));
    public static final RegistryObject<EntityType<FrigidEntity>> FRIGID = ENTITIES.register("frigid",
            () -> EntityType.Builder.<FrigidEntity>of(FrigidEntity::new, MobCategory.MONSTER).sized(1.0F, 1.95F).build(id("frigid")));

    // --- Прочие мобы ---
    public static final RegistryObject<EntityType<ForsakenEntity>> FORSAKEN = ENTITIES.register("forsaken",
            () -> EntityType.Builder.<ForsakenEntity>of(ForsakenEntity::new, MobCategory.MONSTER).sized(0.6F, 1.99F).fireImmune().build(id("forsaken")));

    public static final RegistryObject<EntityType<ParasiteEntity>> PARASITE = ENTITIES.register("parasite",
            () -> EntityType.Builder.<ParasiteEntity>of(ParasiteEntity::new, MobCategory.MONSTER).sized(0.8F, 0.3F).build(id("parasite")));
    public static final RegistryObject<EntityType<VespaEntity>> VESPA = ENTITIES.register("vespa",
            () -> EntityType.Builder.<VespaEntity>of(VespaEntity::new, MobCategory.MONSTER).sized(1.6F, 1.0F).build(id("vespa")));
    public static final RegistryObject<EntityType<VespaCocoonEntity>> VESPACOCOON = ENTITIES.register("vespacocoon",
            () -> EntityType.Builder.<VespaCocoonEntity>of(VespaCocoonEntity::new, MobCategory.MONSTER).sized(0.8F, 1.0F).build(id("vespacocoon")));
    public static final RegistryObject<EntityType<BoneWormEntity>> BONEWORM = ENTITIES.register("boneworm",
            () -> EntityType.Builder.<BoneWormEntity>of(BoneWormEntity::new, MobCategory.MONSTER).sized(0.8F, 2.0F).build(id("boneworm")));
    public static final RegistryObject<EntityType<BansheeEntity>> BANSHEE = ENTITIES.register("banshee",
            () -> EntityType.Builder.<BansheeEntity>of(BansheeEntity::new, MobCategory.MONSTER).sized(0.75F, 1.75F).build(id("banshee")));

    public static final RegistryObject<EntityType<SalamanderEntity>> SALAMANDER = ENTITIES.register("salamander",
            () -> EntityType.Builder.<SalamanderEntity>of(SalamanderEntity::new, MobCategory.MONSTER).sized(1.95F, 1.6F).fireImmune().build(id("salamander")));
    public static final RegistryObject<EntityType<WetaEntity>> WETA = ENTITIES.register("weta",
            () -> EntityType.Builder.<WetaEntity>of(WetaEntity::new, MobCategory.MONSTER).sized(0.8F, 0.5F)
                    .immuneTo(Blocks.SWEET_BERRY_BUSH, Blocks.CACTUS).build(id("weta")));
    public static final RegistryObject<EntityType<MimicEntity>> MIMIC = ENTITIES.register("mimic",
            () -> EntityType.Builder.<MimicEntity>of(MimicEntity::new, MobCategory.MONSTER).sized(1.0F, 1.0F).build(id("mimic")));

    // --- Снаряды ---
    public static final RegistryObject<EntityType<DeathCoilEntity>> DEATHCOIL = ENTITIES.register("deathcoil",
            () -> EntityType.Builder.<DeathCoilEntity>of(DeathCoilEntity::new, MobCategory.MISC).sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4).updateInterval(1).build(id("deathcoil")));
    public static final RegistryObject<EntityType<AcidJetEntity>> ACIDJET = ENTITIES.register("acidjet",
            () -> EntityType.Builder.<AcidJetEntity>of(AcidJetEntity::new, MobCategory.MISC).sized(0.25F, 0.25F)
                    .clientTrackingRange(4).updateInterval(10).build(id("acidjet")));
    public static final RegistryObject<EntityType<FlameJetEntity>> FLAMEJET = ENTITIES.register("flamejet",
            () -> EntityType.Builder.<FlameJetEntity>of(FlameJetEntity::new, MobCategory.MISC).sized(0.25F, 0.25F)
                    .clientTrackingRange(4).updateInterval(10).build(id("flamejet")));

    public static final RegistryObject<EntityType<WarSmallFireballEntity>> WARSMALLFIREBALL = ENTITIES.register("warsmallfireball",
            () -> EntityType.Builder.<WarSmallFireballEntity>of(WarSmallFireballEntity::new, MobCategory.MISC).sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4).updateInterval(1).build(id("warsmallfireball")));

    private static String id(String name) {
        return UndeadRising.MOD_ID + ":" + name;
    }

    /// Атрибуты (MOD-шина).
    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(UNBURIED.get(), UnburiedEntity.createAttributes().build());
        event.put(MUMMY.get(), MummyEntity.createAttributes().build());
        event.put(MYCOSIS.get(), MycosisEntity.createAttributes().build());
        event.put(FRIGID.get(), FrigidEntity.createAttributes().build());
        event.put(FORSAKEN.get(), ForsakenEntity.createAttributes().build());
        event.put(PARASITE.get(), ParasiteEntity.createAttributes().build());
        event.put(VESPA.get(), VespaEntity.createAttributes().build());
        event.put(VESPACOCOON.get(), VespaCocoonEntity.createAttributes().build());
        event.put(BONEWORM.get(), BoneWormEntity.createAttributes().build());
        event.put(BANSHEE.get(), BansheeEntity.createAttributes().build());
        event.put(SALAMANDER.get(), SalamanderEntity.createAttributes().build());
        event.put(WETA.get(), WetaEntity.createAttributes().build());
        event.put(MIMIC.get(), MimicEntity.createAttributes().build());
    }

    /// Правила естественного появления (MOD-шина) — как `EntitySpawnPlacementRegistry` оригинала.
    @SubscribeEvent
    public static void onSpawnPlacements(SpawnPlacementRegisterEvent event) {
        var replace = SpawnPlacementRegisterEvent.Operation.REPLACE;
        event.register(UNBURIED.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FURTameableEntity::checkMonsterSpawnRules, replace);
        event.register(MUMMY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FURTameableEntity::checkMonsterSpawnRules, replace);
        event.register(MYCOSIS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, FURTameableEntity::checkMonsterSpawnRules, replace);
        event.register(FRIGID.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FURTameableEntity::checkMonsterSpawnRules, replace);
        event.register(FORSAKEN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, replace);
        event.register(PARASITE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, replace);
        event.register(VESPA.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, FlyingMobEntity::checkFlyerSpawnRules, replace);
        event.register(BONEWORM.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, replace);
        event.register(BANSHEE.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, replace);
        event.register(SALAMANDER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FURTameableEntity::checkMonsterSpawnRules, replace);
        event.register(WETA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FURTameableEntity::checkMonsterSpawnRules, replace);
        event.register(MIMIC.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MimicEntity::checkMimicSpawnRules, replace);
    }
}
