package dev.mcmoded.port.familiars;

import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.effect.OakskinEffect;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Переходники для Alshanex's Familiars (Alshanex) под Iron's Spells 3.16.3 (Iron431): изменившиеся методы
 * и поля Iron's (см. patches/jar-ports/familiars/rules.txt).
 */
public final class FamiliarsPort {
    /** ParticleRegistry.FIREFLY_PARTICLE стал Supplier — даём RegistryObject той же частицы. */
    public static final RegistryObject<ParticleType<?>> FIREFLY_PARTICLE =
        RegistryObject.create(new ResourceLocation("irons_spellbooks", "firefly"), ForgeRegistries.PARTICLE_TYPES);
    /** MobEffectRegistry.RAISE_DEAD_TIMER убран (призывы считает SummonManager); значение больше не используется. */
    public static final RegistryObject<MobEffect> RAISE_DEAD_TIMER =
        RegistryObject.create(new ResourceLocation("minecraft", "luck"), ForgeRegistries.MOB_EFFECTS);

    private static net.minecraft.core.HolderLookup.Provider registries() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server == null ? null : server.registryAccess();
    }

    /** SyncedSpellData.saveNBTData(CompoundTag) → (CompoundTag, HolderLookup.Provider) */
    public static void saveNBTData(SyncedSpellData data, CompoundTag tag) { data.saveNBTData(tag, registries()); }

    public static void loadNBTData(SyncedSpellData data, CompoundTag tag) { data.loadNBTData(tag, registries()); }

    /** OakskinEffect.getReductionAmount(int): теперь зависит от сущности; прежняя формула — 0.05 + 0.1 за уровень, не больше 0.75. */
    public static float oakskinReduction(int amplifier) {
        return Math.min(0.75F, OakskinEffect.BASE_REDUCTION + OakskinEffect.REDUCTION_PER_LEVEL * amplifier);
    }

    /** MagicSummon.getSummoner(): LivingEntity → Entity */
    public static LivingEntity summoner(IMagicSummon summon) {
        return summon.getSummoner() instanceof LivingEntity owner ? owner : null;
    }

    /** onRemovedHelper(Entity, SummonTimer) → onRemovedHelper(Entity): таймеры призыва убраны. */
    public static void onRemovedHelper(IMagicSummon self, Entity entity, Object timer) { self.onRemovedHelper(entity); }

    private FamiliarsPort() {}
}
