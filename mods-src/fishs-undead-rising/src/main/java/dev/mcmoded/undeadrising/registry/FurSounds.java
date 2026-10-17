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

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(UndeadRising.MOD_ID, name)));
    }
}
