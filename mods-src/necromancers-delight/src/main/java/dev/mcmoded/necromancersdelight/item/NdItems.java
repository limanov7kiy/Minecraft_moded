package dev.mcmoded.necromancersdelight.item;

import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

import static dev.mcmoded.necromancersdelight.NecromancersDelight.ITEMS;

/// Артефакты Necromancer's Delight (оригинал — `NDItems`, WinDanesz): Curios-предметы Redux. Эффекты — `NdArtifacts`
/// (мобы и предметы — из порта Fish's Undead Rising), пиявок — `ManaLeech`/заклинания, амулет кристалла — свой класс.
public final class NdItems {
    private NdItems() {}

    public static final List<RegistryObject<Item>> ARTIFACTS = new ArrayList<>();

    public static final RegistryObject<Item> AMULET_NECROMANTIC_WARD = artifact("amulet_necromantic_ward", Rarity.UNCOMMON);
    public static final RegistryObject<Item> AMULET_SLOWNESS_IMMUNITY = artifact("amulet_slowness_immunity", Rarity.EPIC);
    public static final RegistryObject<Item> AMULET_WEAKNESS_IMMUNITY = artifact("amulet_weakness_immunity", Rarity.EPIC);
    public static final RegistryObject<Item> AMULET_MALICE_WARD = artifact("amulet_malice_ward", Rarity.RARE);
    public static final RegistryObject<Item> AMULET_LEECHLINK = artifact("amulet_leechlink", Rarity.RARE);
    public static final RegistryObject<Item> RING_LEGION = artifact("ring_legion", Rarity.UNCOMMON);
    public static final RegistryObject<Item> RING_NAMELESS = artifact("ring_nameless", Rarity.UNCOMMON);
    public static final RegistryObject<Item> RING_FROZEN_DAGGER = artifact("ring_frozen_dagger", Rarity.RARE);
    public static final RegistryObject<Item> RING_FORSAKEN = artifact("ring_forsaken", Rarity.EPIC);
    public static final RegistryObject<Item> CHARM_BONE_BAG = artifact("charm_bone_bag", Rarity.RARE);
    public static final RegistryObject<Item> CHARM_MUMMY_MINION = artifact("charm_mummy_minion", Rarity.RARE);
    public static final RegistryObject<Item> CHARM_SCARAB = artifact("charm_scarab", Rarity.EPIC);
    public static final RegistryObject<Item> CHARM_MUSHROOM_MINION = artifact("charm_mushroom_minion", Rarity.EPIC);
    public static final RegistryObject<Item> BELT_THORNS = artifact("belt_thorns", Rarity.RARE);
    public static final RegistryObject<Item> HEAD_PEST_CROWN = artifact("head_pest_crown", Rarity.EPIC);
    public static final RegistryObject<Item> CHARM_SPELLVENOM = artifact("charm_spellvenom", Rarity.RARE);
    public static final RegistryObject<Item> AMULET_LEECH_CRYSTAL = register("amulet_leech_crystal", () -> new LeechCrystalAmulet(Rarity.UNCOMMON));

    private static RegistryObject<Item> artifact(String id, Rarity rarity) {
        return register(id, () -> ArtifactChannel.createArtifact(rarity, new IArtifactEffect() {}));
    }

    private static RegistryObject<Item> register(String id, java.util.function.Supplier<Item> factory) {
        RegistryObject<Item> item = ITEMS.register(id, factory);
        ARTIFACTS.add(item);
        return item;
    }

    /// Надет ли артефакт (слот Curios).
    public static boolean has(net.minecraft.world.entity.player.Player player, RegistryObject<Item> artifact) {
        return ArtifactChannel.isEquipped(player, artifact.get());
    }

    public static void init() {}

    public static void onCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().toString().equals("ebwizardry:ebwizardry_artifacts")) ARTIFACTS.forEach(event::accept);
    }
}
