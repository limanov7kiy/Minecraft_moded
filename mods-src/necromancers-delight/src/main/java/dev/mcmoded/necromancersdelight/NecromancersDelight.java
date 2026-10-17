package dev.mcmoded.necromancersdelight;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.core.registry.EBRegistries;
import dev.mcmoded.necromancersdelight.entity.NdBombs;
import dev.mcmoded.necromancersdelight.spell.NdSpells;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/// Порт Wizardry - Necromancer's Delight (WinDanesz, 1.12.2, GPL-3.0) на 1.20.1 поверх Redux и нашего порта
/// Fish's Undead Rising (`mod_lavacow`). Namespace оригинала — `necromancersdelight`.
@Mod(NecromancersDelight.MOD_ID)
public class NecromancersDelight {
    public static final String MOD_ID = "necromancersdelight";

    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(EBRegistries.SPELL, MOD_ID);
    public static final DeferredRegister<net.minecraft.world.item.Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<net.minecraft.world.entity.EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<Spell> STINK_BOMB = SPELLS.register("stink_bomb", NdSpells::stinkBomb);
    public static final RegistryObject<Spell> FROST_BOMB = SPELLS.register("frost_bomb", NdSpells::frostBomb);
    public static final RegistryObject<Spell> CORRODE = SPELLS.register("corrode", dev.mcmoded.necromancersdelight.spell.Corrode::new);
    public static final RegistryObject<Spell> GLOWSHROOM = SPELLS.register("glowshroom", dev.mcmoded.necromancersdelight.spell.Glowshroom::new);
    // ND-3 (порядок — как в NDSpells оригинала)
    public static final RegistryObject<Spell> BOUND_FROZEN_DAGGER = SPELLS.register("bound_frozen_dagger", NdSpells::boundFrozenDagger);
    public static final RegistryObject<Spell> BOUND_BONE_SWORD = SPELLS.register("bound_bone_sword", NdSpells::boundBoneSword);
    public static final RegistryObject<Spell> HOLY_HAND_GRENADE = SPELLS.register("holy_hand_grenade", NdSpells::holyHandGrenade);
    public static final RegistryObject<Spell> GHOST_BOMB = SPELLS.register("ghost_bomb", NdSpells::ghostBomb);
    public static final RegistryObject<Spell> WAIL_OF_THE_BANSHEE = SPELLS.register("wail_of_the_banshee", NdSpells::wailOfTheBanshee);
    public static final RegistryObject<Spell> SUMMON_VESPA = SPELLS.register("summon_vespa", NdSpells::summonVespa);
    public static final RegistryObject<Spell> CONJURE_BANSHEE = SPELLS.register("conjure_banshee", NdSpells::conjureBanshee);
    public static final RegistryObject<Spell> CONJURE_MYCOSIS = SPELLS.register("conjure_mycosis", NdSpells::conjureMycosis);
    public static final RegistryObject<Spell> SUMMON_SALAMANDER = SPELLS.register("summon_salamander", NdSpells::summonSalamander);
    public static final RegistryObject<Spell> TRIAL_OF_MUSHROOMS = SPELLS.register("trial_of_mushrooms", dev.mcmoded.necromancersdelight.spell.TrialOfMushrooms::new);
    public static final RegistryObject<Spell> BOUND_SPECTRAL_DAGGER = SPELLS.register("bound_spectral_dagger", NdSpells::boundSpectralDagger);
    public static final RegistryObject<Spell> BOUND_VESPA_SHIELD = SPELLS.register("bound_vespa_shield", NdSpells::boundVespaShield);
    public static final RegistryObject<Spell> SUMMON_MANA_LEECH = SPELLS.register("summon_mana_leech", NdSpells::summonManaLeech);
    public static final RegistryObject<Spell> SUMMON_ELEMENTAL_LEECH = SPELLS.register("summon_elemental_leech", NdSpells::summonElementalLeech);
    public static final RegistryObject<Spell> CARCINIZE = SPELLS.register("carcinize", dev.mcmoded.necromancersdelight.spell.Carcinize::new);

    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final RegistryObject<net.minecraft.world.level.block.Block> TEMP_GLOWSHROOM = BLOCKS.register("temporary_glowshroom",
            () -> new dev.mcmoded.necromancersdelight.block.TempGlowshroom(net.minecraft.world.level.block.state.BlockBehaviour.Properties
                    .copy(net.minecraft.world.level.block.Blocks.BROWN_MUSHROOM).lightLevel(s -> 15).noLootTable().noCollission().instabreak()));

    public NecromancersDelight() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        NdBombs.init();
        dev.mcmoded.necromancersdelight.entity.NdEntities.init();
        modBus.addListener(dev.mcmoded.necromancersdelight.entity.NdEntities::onAttributes);
        var redux = com.binaris.wizardry.core.event.WizardryEventBus.getInstance();
        redux.register(com.binaris.wizardry.api.content.event.SpellCastEvent.Pre.class, dev.mcmoded.necromancersdelight.item.NdArtifacts::onSpellPreCast);
        redux.register(com.binaris.wizardry.api.content.event.SpellCastEvent.Post.class, dev.mcmoded.necromancersdelight.item.NdArtifacts::onSpellPostCast);
        dev.mcmoded.necromancersdelight.item.NdItems.init();
        SPELLS.register(modBus);
        BLOCKS.register(modBus);
        dev.mcmoded.necromancersdelight.effect.NdEffects.EFFECTS.register(modBus);
        ITEMS.register(modBus);
        ENTITIES.register(modBus);
        modBus.addListener(NdBombs::onCreativeTab);
        modBus.addListener(dev.mcmoded.necromancersdelight.item.NdItems::onCreativeTab);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(dev.mcmoded.necromancersdelight.item.NdArtifacts.class);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(dev.mcmoded.necromancersdelight.item.NdLoot.class);
    }
}
