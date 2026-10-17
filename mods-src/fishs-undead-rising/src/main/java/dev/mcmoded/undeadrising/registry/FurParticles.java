package dev.mcmoded.undeadrising.registry;

import dev.mcmoded.undeadrising.UndeadRising;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/// Частицы (оригинал: Fish's Undead Rising (Fish), `init/FURParticleRegistry`).
/// Провайдеры — `client/ClientSetup#onParticles`, описание спрайтов — `assets/mod_lavacow/particles/*.json`.
public final class FurParticles {
    private FurParticles() {}

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, UndeadRising.MOD_ID);

    /// Рой саранчи вокруг мумии.
    public static final RegistryObject<SimpleParticleType> LOCUST_SWARM = PARTICLES.register("locust_swarm", () -> new SimpleParticleType(false));
    /// След «Кольца смерти» (снаряд посоха Forsaken).
    public static final RegistryObject<SimpleParticleType> WITHER_FLAME = PARTICLES.register("wither_flame", () -> new SimpleParticleType(false));
    /// Брызги желудочного сока (снаряд Osvermis).
    public static final RegistryObject<SimpleParticleType> GASTRO_ACID = PARTICLES.register("gastro_acid", () -> new SimpleParticleType(false));
    /// Над существом под эффектом «Страх».
    public static final RegistryObject<SimpleParticleType> FEAR = PARTICLES.register("fear", () -> new SimpleParticleType(false));
}
