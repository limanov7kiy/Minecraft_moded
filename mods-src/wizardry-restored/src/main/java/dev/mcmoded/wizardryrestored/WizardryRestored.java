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

    public WizardryRestored() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        SPELLS.register(modBus);
    }
}
