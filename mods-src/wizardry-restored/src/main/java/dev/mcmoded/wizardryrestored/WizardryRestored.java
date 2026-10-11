package dev.mcmoded.wizardryrestored;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.core.registry.EBRegistries;
import dev.mcmoded.wizardryrestored.spell.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/// Заклинания Electroblob's Wizardry, которых пока нет в Redux. Регистрируются в реестре заклинаний Redux
/// под своим namespace, так что книги, свитки, палочки, добыча и торговля волшебников подхватывают их сами.
@Mod(WizardryRestored.MOD_ID)
public class WizardryRestored {
    public static final String MOD_ID = "wizardry_restored";

    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(EBRegistries.SPELL, MOD_ID);
    public static final DeferredRegister<net.minecraft.world.effect.MobEffect> EFFECTS =
            DeferredRegister.create(net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS, MOD_ID);
    public static final RegistryObject<net.minecraft.world.effect.MobEffect> MARK_SACRIFICE_EFFECT = EFFECTS.register("mark_sacrifice",
            () -> new net.minecraft.world.effect.MobEffect(net.minecraft.world.effect.MobEffectCategory.HARMFUL, 0xE8C34A) {
            });

    public static final RegistryObject<Spell> LIGHTNING_BOLT = SPELLS.register("lightning_bolt", LightningBolt::new);
    public static final RegistryObject<Spell> SIXTH_SENSE = SPELLS.register("sixth_sense", SixthSense::new);
    public static final RegistryObject<Spell> SHOCKWAVE = SPELLS.register("shockwave", Shockwave::new);
    public static final RegistryObject<Spell> SNOWBALL = SPELLS.register("snowball", Snowball::new);
    public static final RegistryObject<Spell> MINE = SPELLS.register("mine", Mine::new);
    public static final RegistryObject<Spell> MIND_TRICK = SPELLS.register("mind_trick", MindTrick::new);
    public static final RegistryObject<Spell> BLINK = SPELLS.register("blink", Blink::new);
    public static final RegistryObject<Spell> INTIMIDATE = SPELLS.register("intimidate", Intimidate::new);
    public static final RegistryObject<Spell> GLIDE = SPELLS.register("glide", Glide::new);
    public static final RegistryObject<Spell> IRONFLESH = SPELLS.register("ironflesh", BuffSpells::ironflesh);
    public static final RegistryObject<Spell> DIAMONDFLESH = SPELLS.register("diamondflesh", BuffSpells::diamondflesh);
    public static final RegistryObject<Spell> GREATER_FIREBALL = SPELLS.register("greater_fireball", GreaterFireball::new);
    public static final RegistryObject<Spell> CHAIN_LIGHTNING = SPELLS.register("chain_lightning", ChainLightning::new);
    public static final RegistryObject<Spell> PARALYSIS = SPELLS.register("paralysis", Paralysis::new);
    public static final RegistryObject<Spell> SUMMON_IRON_GOLEM = SPELLS.register("summon_iron_golem", BuffSpells::summonIronGolem);

    public static final RegistryObject<Spell> LIGHTNING_RAY = SPELLS.register("lightning_ray", LightningRay::new);
    public static final RegistryObject<Spell> DISINTEGRATION = SPELLS.register("disintegration", Disintegration::new);
    public static final RegistryObject<Spell> TRANSIENCE = SPELLS.register("transience", BuffSpells::transience);
    public static final RegistryObject<Spell> MIND_CONTROL = SPELLS.register("mind_control", MindControl::new);
    public static final RegistryObject<Spell> METAMORPHOSIS = SPELLS.register("metamorphosis", Metamorphosis::new);
    public static final RegistryObject<Spell> FROST_BARRIER = SPELLS.register("frost_barrier", FrostBarrier::new);
    public static final RegistryObject<Spell> WALL_OF_FROST = SPELLS.register("wall_of_frost", WallOfFrost::new);
    public static final RegistryObject<Spell> THUNDERSTORM = SPELLS.register("thunderstorm", Thunderstorm::new);
    public static final RegistryObject<Spell> EARTHQUAKE = SPELLS.register("earthquake", Earthquake::new);
    public static final RegistryObject<Spell> CELESTIAL_SMITE = SPELLS.register("celestial_smite", CelestialSmite::new);
    public static final RegistryObject<Spell> SLOW_TIME = SPELLS.register("slow_time", SlowTime::new);
    public static final RegistryObject<Spell> LIGHT = SPELLS.register("light", Light::new);
    public static final RegistryObject<Spell> SHADOW_WARD = SPELLS.register("shadow_ward", ShadowWard::new);
    public static final RegistryObject<Spell> ICE_SHROUD = SPELLS.register("ice_shroud", IceShroud::new);
    public static final RegistryObject<Spell> SUMMON_SPIRIT_WOLF = SPELLS.register("summon_spirit_wolf", BuffSpells::summonSpiritWolf);
    public static final RegistryObject<Spell> SUMMON_SPIRIT_HORSE = SPELLS.register("summon_spirit_horse", SummonSpiritHorse::new);

    public static final RegistryObject<Spell> FREEZING_WEAPON = SPELLS.register("freezing_weapon", FreezingWeapon::new);
    public static final RegistryObject<Spell> EMPOWERING_PRESENCE = SPELLS.register("empowering_presence", EmpoweringPresence::new);
    public static final RegistryObject<Spell> MUFFLE = SPELLS.register("muffle", Muffle::new);
    public static final RegistryObject<Spell> GRAPPLE = SPELLS.register("grapple", Grapple::new);
    public static final RegistryObject<Spell> TRANSPORTATION = SPELLS.register("transportation", Transportation::new);
    public static final RegistryObject<Spell> ICE_STATUE = SPELLS.register("ice_statue", IceStatue::new);
    public static final RegistryObject<Spell> PETRIFY = SPELLS.register("petrify", Petrify::new);
    // Этап 3: новые сущности (0.5.0)
    public static final RegistryObject<Spell> FORCEFIELD = SPELLS.register("forcefield", Constructs::forcefield);
    public static final RegistryObject<Spell> RADIANT_TOTEM = SPELLS.register("radiant_totem", Constructs::radiantTotem);
    public static final RegistryObject<Spell> WITHERING_TOTEM = SPELLS.register("withering_totem", Constructs::witheringTotem);
    public static final RegistryObject<Spell> BLACK_HOLE = SPELLS.register("black_hole", Constructs::blackHole);
    public static final RegistryObject<Spell> STORMCLOUD = SPELLS.register("stormcloud", Stormcloud::new);
    public static final RegistryObject<Spell> DECOY = SPELLS.register("decoy", Decoy::new);
    public static final RegistryObject<Spell> SUMMON_ICE_WRAITH = SPELLS.register("summon_ice_wraith", Constructs::summonIceWraith);
    public static final RegistryObject<Spell> SUMMON_SHADOW_WRAITH = SPELLS.register("summon_shadow_wraith", Constructs::summonShadowWraith);
    public static final RegistryObject<Spell> SUMMON_STORM_ELEMENTAL = SPELLS.register("summon_storm_elemental", Constructs::summonStormElemental);
    public static final RegistryObject<Spell> SHIELD = SPELLS.register("shield", Shield::new);
    public static final RegistryObject<Spell> LIGHTNING_WEB = SPELLS.register("lightning_web", LightningWeb::new);
    public static final RegistryObject<Spell> IMBUE_WEAPON = SPELLS.register("imbue_weapon", ImbueWeapon::new);
    public static final RegistryObject<Spell> MARK_SACRIFICE = SPELLS.register("mark_sacrifice", MarkSacrifice::new);
    public static final RegistryObject<Spell> MIRAGE = SPELLS.register("mirage", Mirage::new);
    public static final RegistryObject<Spell> SNARE = SPELLS.register("snare", Constructs::snare);
    public static final RegistryObject<Spell> FOREST_OF_THORNS = SPELLS.register("forest_of_thorns", Thrown.ForestOfThorns::new);
    public static final RegistryObject<Spell> LIGHTNING_DISC = SPELLS.register("lightning_disc", Thrown.LightningDisc::new);
    public static final RegistryObject<Spell> LIGHTNING_HAMMER = SPELLS.register("lightning_hammer", Thrown.LightningHammer::new);
    public static final RegistryObject<Spell> CLAIRVOYANCE = SPELLS.register("clairvoyance", Clairvoyance::new);
    public static final RegistryObject<Spell> DIVINATION = SPELLS.register("divination", Divination::new);
    public static final RegistryObject<Spell> SUMMON_PHOENIX = SPELLS.register("summon_phoenix", Constructs::summonPhoenix);
    public static final RegistryObject<Spell> RESURRECTION = SPELLS.register("resurrection", Resurrection::new);
    public static final RegistryObject<Spell> POSSESSION = SPELLS.register("possession", Possession::new);
    // «Тестовые» заклинания Redux, которые он убирает на выделенном сервере
    public static final RegistryObject<Spell> LIGHTNING_PULSE = SPELLS.register("lightning_pulse", ReduxTestSpells::lightningPulse);
    public static final RegistryObject<Spell> SUMMON_ICE_GIANT = SPELLS.register("summon_ice_giant", ReduxTestSpells::summonIceGiant);
    public static final RegistryObject<Spell> CONJURE_ARMOR = SPELLS.register("conjure_armor", ReduxTestSpells::conjureArmor);
    public static final RegistryObject<Spell> SILVERFISH_SWARM = SPELLS.register("silverfish_swarm", ReduxTestSpells::silverfishSwarm);

    public WizardryRestored() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        SPELLS.register(modBus);
        EFFECTS.register(modBus);
        dev.mcmoded.wizardryrestored.block.ModBlocks.BLOCKS.register(modBus);
        dev.mcmoded.wizardryrestored.block.ModBlocks.ITEMS.register(modBus);
        modBus.addListener(dev.mcmoded.wizardryrestored.block.ModBlocks::onCreativeTab);
        dev.mcmoded.wizardryrestored.entity.ModEntities.ENTITY_TYPES.register(modBus);
        modBus.addListener(dev.mcmoded.wizardryrestored.entity.ModEntities::onAttributes);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(dev.mcmoded.wizardryrestored.entity.DecoyEntity.class);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(Clairvoyance.class);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(Possession.class);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(dev.mcmoded.wizardryrestored.util.Artifacts.class);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(dev.mcmoded.wizardryrestored.util.TempBlocks.class);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(dev.mcmoded.wizardryrestored.util.SpellStates.class);
    }
}
