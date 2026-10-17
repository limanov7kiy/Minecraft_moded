package dev.mcmoded.necromancersdelight.effect;

import com.binaris.wizardry.core.AllyDesignation;
import dev.mcmoded.necromancersdelight.NecromancersDelight;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/// Эффекты Necromancer's Delight (оригинал — `PotionPoisonAura`, `PotionLocusts`, WinDanesz).
public final class NdEffects {
    private NdEffects() {}

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, NecromancersDelight.MOD_ID);

    /// Poisonous Spore: раз в 19 тиков отравляет (Poison 4 с, сила = уровень) не-союзников в радиусе 2.
    public static final RegistryObject<MobEffect> POISONOUS_SPORE = EFFECTS.register("poisonous_spore", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xD4BA80) {
        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide || entity.tickCount % 19 != 0) return;
            for (LivingEntity t : entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(2))) {
                if (t != entity && !AllyDesignation.isAllied(entity, t)) t.addEffect(new MobEffectInstance(MobEffects.POISON, 80, amplifier));
            }
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return true;
        }
    });

    /// Locusts: рой саранчи закрывает от солнца — горящий на солнце (нежить) гаснет.
    public static final RegistryObject<MobEffect> LOCUSTS = EFFECTS.register("locusts", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x4A5A1E) {
        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.isOnFire() && !entity.isInLava() && entity.level().isDay() && entity.level().canSeeSky(entity.blockPosition())) entity.clearFire();
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return true;
        }
    });
}
