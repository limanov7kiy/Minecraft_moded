package dev.mcmoded.wizardryrestored;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.core.registry.EBRegistries;
import dev.mcmoded.wizardryrestored.spell.LightningBolt;
import dev.mcmoded.wizardryrestored.spell.Shockwave;
import dev.mcmoded.wizardryrestored.spell.SixthSense;
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

    public WizardryRestored() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        SPELLS.register(modBus);
    }
}
