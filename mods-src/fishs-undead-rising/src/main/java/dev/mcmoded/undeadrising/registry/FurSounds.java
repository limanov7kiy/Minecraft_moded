package dev.mcmoded.undeadrising.registry;

import dev.mcmoded.undeadrising.UndeadRising;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/// Звуки порта. Id — как в `sounds.json` оригинала (оригинал: Fish's Undead Rising (Fish), `init/FURSoundRegistry`).
/// Новый звук: строка здесь + запись в `assets/mod_lavacow/sounds.json` + ogg в `sounds/` + субтитр в lang.
public final class FurSounds {
    private FurSounds() {}

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, UndeadRising.MOD_ID);

    public static final RegistryObject<SoundEvent> UNBURIED_AMBIENT = register("unburied_ambient");
    public static final RegistryObject<SoundEvent> UNBURIED_HURT = register("unburied_hurt");
    public static final RegistryObject<SoundEvent> UNBURIED_DEATH = register("unburied_death");
    /// Посох Forsaken (в оригинале — звук заклинания Skeleton King).
    public static final RegistryObject<SoundEvent> SKELETONKING_SPELL_TOSS = register("skeletonking_spell_toss");

    public static final RegistryObject<SoundEvent> PARASITE_AMBIENT = register("parasite_ambient");
    public static final RegistryObject<SoundEvent> PARASITE_HURT = register("parasite_hurt");
    public static final RegistryObject<SoundEvent> PARASITE_DEATH = register("parasite_death");
    /// Паразит плетёт кокон.
    public static final RegistryObject<SoundEvent> PARASITE_WEAVE = register("parasite_weave");
    public static final RegistryObject<SoundEvent> VESPA_AMBIENT = register("vespa_ambient");
    public static final RegistryObject<SoundEvent> VESPA_HURT = register("vespa_hurt");
    public static final RegistryObject<SoundEvent> VESPA_DEATH = register("vespa_death");
    /// Жужжание крыльев в полёте (без субтитра, как в оригинале).
    public static final RegistryObject<SoundEvent> VESPA_FLYING = register("vespa_flying");
    public static final RegistryObject<SoundEvent> BONEWORM_AMBIENT = register("boneworm_ambient");
    public static final RegistryObject<SoundEvent> BONEWORM_ATTACK = register("boneworm_attack");
    public static final RegistryObject<SoundEvent> BONEWORM_BURROW = register("boneworm_burrow");
    public static final RegistryObject<SoundEvent> BONEWORM_HURT = register("boneworm_hurt");
    public static final RegistryObject<SoundEvent> BONEWORM_DEATH = register("boneworm_death");
    public static final RegistryObject<SoundEvent> BANSHEE_AMBIENT = register("banshee_ambient");
    /// Крик Банши.
    public static final RegistryObject<SoundEvent> BANSHEE_ATTACK = register("banshee_attack");
    public static final RegistryObject<SoundEvent> BANSHEE_HURT = register("banshee_hurt");
    public static final RegistryObject<SoundEvent> BANSHEE_DEATH = register("banshee_death");

    public static final RegistryObject<SoundEvent> SALAMANDER_AMBIENT = register("salamander_ambient");
    public static final RegistryObject<SoundEvent> SALAMANDER_HURT = register("salamander_hurt");
    public static final RegistryObject<SoundEvent> SALAMANDER_DEATH = register("salamander_death");
    public static final RegistryObject<SoundEvent> WETA_AMBIENT = register("weta_ambient");
    public static final RegistryObject<SoundEvent> WETA_HURT = register("weta_hurt");
    public static final RegistryObject<SoundEvent> WETA_DEATH = register("weta_death");
    public static final RegistryObject<SoundEvent> MIMIC_AMBIENT = register("mimic_ambient");
    public static final RegistryObject<SoundEvent> MIMIC_HURT = register("mimic_hurt");
    public static final RegistryObject<SoundEvent> MIMIC_DEATH = register("mimic_death");
    /// Щелчок клешни (в оригинале — общий звук укуса Swarmer, им щёлкает мимикраб).
    public static final RegistryObject<SoundEvent> SWARMER_ATTACK = register("swarmer_attack");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(UndeadRising.MOD_ID, name)));
    }
}
