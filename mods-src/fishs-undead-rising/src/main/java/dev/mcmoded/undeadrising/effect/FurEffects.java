package dev.mcmoded.undeadrising.effect;

import dev.mcmoded.undeadrising.UndeadRising;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/// Эффекты Fish's Undead Rising (оригинал — `effect/Effect*` и `events/EventHandler` 1.16.5, © Fish).
/// Цвета и правила — как в оригинале.
public final class FurEffects {
    private FurEffects() {}

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, UndeadRising.MOD_ID);

    /// Soiled: нельзя есть и пить зелья; лечение −25% за уровень.
    public static final RegistryObject<MobEffect> SOILED = EFFECTS.register("soiled", () -> new Plain(MobEffectCategory.HARMFUL, 7298108));
    /// Corroded: входящий урон +10% за уровень (I — +10%, II — +20%…).
    public static final RegistryObject<MobEffect> CORRODED = EFFECTS.register("corroded", () -> new Plain(MobEffectCategory.HARMFUL, 2361095));
    /// Infested: скорость −10%; при смерти из существа вылезают паразиты (`event/FurEntityEvents#onDeath`).
    public static final RegistryObject<MobEffect> INFESTED = EFFECTS.register("infested", () -> new Plain(MobEffectCategory.HARMFUL, 10037074)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.1F, AttributeModifier.Operation.MULTIPLY_TOTAL));
    /// Fear (крик Банши): урон −4, скорость −5%; мобы бросают цель и бегут куда глаза глядят (оригинал: `effect/EffectFear`).
    public static final RegistryObject<MobEffect> FEAR = EFFECTS.register("fear", () -> new Fear()
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", -4.0, AttributeModifier.Operation.ADDITION)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", -0.05F, AttributeModifier.Operation.MULTIPLY_TOTAL));

    /// Fragile («Смертное касание»): пока здоровье ниже 5% × (уровень + 1) от максимума — мгновенная смерть
    /// (оригинал: `effect/EffectFragile`). Накладывают Forsaken и «Кольцо смерти».
    public static final RegistryObject<MobEffect> FRAGILE = EFFECTS.register("fragile", Fragile::new);

    /// Immolation («Самосожжение»): раз в секунду поджигает всех чужих в радиусе 2 блоков (на 2 + уровень с), сам
    /// носитель не получает урона от огня ({@link #onAttack}); над ним — языки пламени. Даёт Kung Pao Chicken
    /// (оригинал: `effect/EffectImmolation` + `events/EventHandler`; зелья Immolation не перенесены).
    public static final RegistryObject<MobEffect> IMMOLATION = EFFECTS.register("immolation", Immolation::new);

    static final class Immolation extends MobEffect {
        Immolation() {
            super(MobEffectCategory.BENEFICIAL, 15371285);
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            for (LivingEntity other : entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(2.0))) {
                if (other != entity && !other.isAlliedTo(entity)) other.setSecondsOnFire(2 + amplifier);
            }
            if (entity.level() instanceof net.minecraft.server.level.ServerLevel level) {
                var r = entity.getRandom();
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME, entity.getRandomX(1.0), entity.getRandomY() + 1.0,
                        entity.getRandomZ(1.0), 15, r.nextGaussian() * 0.02, r.nextGaussian() * 0.02, r.nextGaussian() * 0.02, 0.0);
            }
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return duration % 20 == 0;
        }
    }

    static final class Fragile extends MobEffect {
        Fragile() {
            super(MobEffectCategory.HARMFUL, 3483434);
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            float max = entity.getMaxHealth();
            if (entity.getHealth() / max < 0.05F * (amplifier + 1)) {
                if (entity instanceof net.minecraft.world.entity.player.Player p && (p.isSpectator() || p.isCreative())) return;
                entity.hurt(entity.damageSources().wither(), max);
            }
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return true;
        }
    }

    static final class Fear extends MobEffect {
        Fear() {
            super(MobEffectCategory.HARMFUL, 13457950);
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity instanceof PathfinderMob mob) {
                Vec3 vec = LandRandomPos.getPos(mob, 20, 5);
                mob.setTarget(null);
                mob.setLastHurtByMob(null);
                if (vec != null) mob.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.0);
            }
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return duration % 10 == 0;
        }
    }

    static final class Plain extends MobEffect {
        Plain(MobEffectCategory category, int color) {
            super(category, color);
        }
    }

    @SubscribeEvent
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {
        LivingEntity e = event.getEntity();
        if (e.hasEffect(SOILED.get()) && (event.getItem().isEdible() || event.getItem().getItem() instanceof PotionItem)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        var soiled = event.getEntity().getEffect(SOILED.get());
        if (soiled != null) event.setAmount(event.getAmount() * Math.max(0, 1 - 0.25f * (1 + soiled.getAmplifier())));
    }

    /// Под Immolation урон от огня не проходит.
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        if (event.getSource().is(DamageTypeTags.IS_FIRE) && event.getEntity().hasEffect(IMMOLATION.get())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        var corroded = event.getEntity().getEffect(CORRODED.get());
        if (corroded != null) event.setAmount(event.getAmount() * (1 + 0.1f * (1 + corroded.getAmplifier())));
    }
}
