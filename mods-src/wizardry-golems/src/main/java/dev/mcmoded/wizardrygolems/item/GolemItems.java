package dev.mcmoded.wizardrygolems.item;

import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import dev.mcmoded.wizardrygolems.WizardryGolems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

/// 36 артефактов Wizardry Golems (оригинал — `WizardryGolemsItems`, WinDanesz). Предмет делает Redux
/// (`ArtifactChannel.createArtifact`: Curios-предмет, подсказка `item.<id>.desc`); эффекты — в `GolemArtifacts`
/// и в составе призыва `Golemancies`. Слоты Curios — `data/curios/tags/items/*.json`.
public final class GolemItems {
    private GolemItems() {}

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WizardryGolems.MOD_ID);
    public static final List<RegistryObject<Item>> ARTIFACTS = new ArrayList<>();

    public static final RegistryObject<Item> RING_SANDSTONE = artifact("ring_sandstone", Rarity.UNCOMMON);
    public static final RegistryObject<Item> RING_GRASS = artifact("ring_grass", Rarity.UNCOMMON);
    public static final RegistryObject<Item> RING_ENGRAVED_CONCRETE = artifact("ring_engraved_concrete", Rarity.UNCOMMON);
    public static final RegistryObject<Item> RING_FOREST_GUARDIAN = artifact("ring_forest_guardian", Rarity.RARE);
    public static final RegistryObject<Item> RING_OBSIDIAN = artifact("ring_obsidian", Rarity.RARE);
    public static final RegistryObject<Item> RING_GLISTERING = artifact("ring_glistering", Rarity.RARE);
    public static final RegistryObject<Item> RING_ANCIENT_EMPEROR = artifact("ring_ancient_emperor", Rarity.EPIC);
    public static final RegistryObject<Item> AMULET_SNARE = artifact("amulet_snare", Rarity.RARE);
    public static final RegistryObject<Item> AMULET_GAIA = artifact("amulet_gaia", Rarity.EPIC);
    public static final RegistryObject<Item> AMULET_DEATHWEED = artifact("amulet_deathweed", Rarity.EPIC);
    public static final RegistryObject<Item> CHARM_DRIED_MUSHROOM = artifact("charm_dried_mushroom", Rarity.RARE);
    public static final RegistryObject<Item> RING_CHARCOAL = artifact("ring_charcoal", Rarity.RARE);
    public static final RegistryObject<Item> RING_SMOLDERING = artifact("ring_smoldering", Rarity.RARE);
    public static final RegistryObject<Item> RING_FLAME_TRAIL = artifact("ring_flame_trail", Rarity.UNCOMMON);
    public static final RegistryObject<Item> RING_FIRE_GOLEM_DURATION = artifact("ring_fire_golem_duration", Rarity.RARE);
    public static final RegistryObject<Item> RING_FLAME_GOLEM = artifact("ring_flame_golem", Rarity.EPIC);
    public static final RegistryObject<Item> AMULET_STEAMING_NETHERRACK = artifact("amulet_steaming_netherrack", Rarity.EPIC);
    public static final RegistryObject<Item> CHARM_FIRE_GOLEMANCY_POTENCY = artifact("charm_fire_golemancy_potency", Rarity.RARE);
    public static final RegistryObject<Item> CHARM_IFRIT_BOTTLE = artifact("charm_ifrit_bottle", Rarity.EPIC);
    public static final RegistryObject<Item> RING_PERMANENT_GOLEM = artifact("ring_permanent_golem", Rarity.EPIC);
    public static final RegistryObject<Item> RING_FROSTBITE = artifact("ring_frostbite", Rarity.UNCOMMON);
    public static final RegistryObject<Item> RING_SNOW_GOLEM = artifact("ring_snow_golem", Rarity.UNCOMMON);
    public static final RegistryObject<Item> RING_WINTER_GOLEM = artifact("ring_winter_golem", Rarity.EPIC);
    public static final RegistryObject<Item> AMULET_JAGGED_SAPPHIRE = artifact("amulet_jagged_sapphire", Rarity.UNCOMMON);
    public static final RegistryObject<Item> AMULET_BROKEN_ICE = artifact("amulet_broken_ice", Rarity.UNCOMMON);
    public static final RegistryObject<Item> CHARM_FROZEN_MARK = artifact("charm_frozen_mark", Rarity.UNCOMMON);
    public static final RegistryObject<Item> BODY_FROST_CLOAK = artifact("body_frost_cloak", Rarity.EPIC);
    public static final RegistryObject<Item> CHARM_LIVING_SNOW = artifact("charm_living_snow", Rarity.EPIC);
    public static final RegistryObject<Item> HEAD_PERMAFROST_CROWN = artifact("head_permafrost_crown", Rarity.RARE);
    public static final RegistryObject<Item> BELT_COLDLINK = artifact("belt_coldlink", Rarity.RARE);
    public static final RegistryObject<Item> RING_LODESTONE_GOLEM = artifact("ring_lodestone_golem", Rarity.RARE);
    public static final RegistryObject<Item> HEAD_MAELSTROM_GOLEM = artifact("head_maelstrom_golem", Rarity.EPIC);
    public static final RegistryObject<Item> RING_SPARK_GOLEM = artifact("ring_spark_golem", Rarity.RARE);
    public static final RegistryObject<Item> RING_ELECTRIC_SCATTER = artifact("ring_electric_scatter", Rarity.UNCOMMON);
    public static final RegistryObject<Item> CHARM_STATIC_AURA = artifact("charm_static_aura", Rarity.EPIC);
    public static final RegistryObject<Item> AMULET_RAGING_SKIES = artifact("amulet_raging_skies", Rarity.EPIC);

    private static RegistryObject<Item> artifact(String id, Rarity rarity) {
        RegistryObject<Item> item = ITEMS.register(id, () -> ArtifactChannel.createArtifact(rarity, new IArtifactEffect() {}));
        ARTIFACTS.add(item);
        return item;
    }

    /// Во вкладку артефактов Redux.
    public static void onCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().toString().equals("ebwizardry:ebwizardry_artifacts")) {
            ARTIFACTS.forEach(event::accept);
        }
    }
}
