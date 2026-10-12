package dev.mcmoded.wizardrygolems;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.core.registry.EBRegistries;
import dev.mcmoded.wizardrygolems.spell.Golemancies;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/// Порт Wizardry Golems (WinDanesz, 1.12.2, GPL-3.0) на 1.20.1: заклинания Golemancy призывают големов
/// Extra Golems как миньонов Electroblob's Wizardry Redux. Namespace оригинала сохранён — `wizardrygolems`.
@Mod(WizardryGolems.MOD_ID)
public class WizardryGolems {
    public static final String MOD_ID = "wizardrygolems";

    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(EBRegistries.SPELL, MOD_ID);

    public static final RegistryObject<Spell> EARTH_GOLEMANCY = SPELLS.register("earth_golemancy", Golemancies::earth);
    public static final RegistryObject<Spell> FIRE_GOLEMANCY = SPELLS.register("fire_golemancy", Golemancies::fire);
    public static final RegistryObject<Spell> ICE_GOLEMANCY = SPELLS.register("ice_golemancy", Golemancies::ice);
    public static final RegistryObject<Spell> LIGHTNING_GOLEMANCY = SPELLS.register("lightning_golemancy", Golemancies::lightning);
    public static final RegistryObject<Spell> HEALING_GOLEMANCY = SPELLS.register("healing_golemancy", Golemancies::healing);

    public WizardryGolems() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        SPELLS.register(modBus);
    }
}
