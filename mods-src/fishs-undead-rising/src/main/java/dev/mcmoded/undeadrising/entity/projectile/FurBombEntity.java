package dev.mcmoded.undeadrising.entity.projectile;

import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurItems;
import dev.mcmoded.undeadrising.registry.FurSounds;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/// Бомбы (оригинал: Fish's Undead Rising (Fish), `entities/projectiles/BasicBombEntity` + обработчик урона в
/// `events/EventHandler`): взрыв радиуса 4 без разрушения блоков, урон взрыва меняется по виду бомбы —
/// святая: нежити ×0,45 и поджог на 8 с (остальным ×0,15); призрачная: ×0,2, гасит горизонтальную скорость и даёт
/// Левитацию на 1 с; звуковая: ×0,33 и Страх III на 4 с. В 1.16.5 проверка святой гранаты сравнивала имя
/// `holygrenade` с id `holy_grenade` и не срабатывала — здесь сделано как задумано (и как в 1.12.2).
/// Взрыв задевает и бросившего — как в оригинале.
public class FurBombEntity extends ThrowableItemProjectile {
    public enum Kind { HOLY, GHOST, SONIC }

    public static final float RADIUS = 4.0F;

    public FurBombEntity(EntityType<? extends FurBombEntity> type, Level level) {
        super(type, level);
    }

    public FurBombEntity(EntityType<? extends FurBombEntity> type, LivingEntity thrower, Level level) {
        super(type, thrower, level);
    }

    public Kind kind() {
        EntityType<?> t = getType();
        if (t == FurEntities.GHOSTBOMB.get()) return Kind.GHOST;
        if (t == FurEntities.SONICBOMB.get()) return Kind.SONIC;
        return Kind.HOLY;
    }

    @Override
    protected Item getDefaultItem() {
        return switch (kind()) {
            case HOLY -> FurItems.HOLY_GRENADE.get();
            case GHOST -> FurItems.GHOSTBOMB.get();
            case SONIC -> FurItems.SONICBOMB.get();
        };
    }

    private SoundEvent explodeSound() {
        return switch (kind()) {
            case HOLY -> FurSounds.RANDOM_HOLY_GRENADE.get();
            case GHOST -> FurSounds.BANSHEE_HURT.get();
            case SONIC -> FurSounds.BANSHEE_ATTACK.get();
        };
    }

    @Override
    protected float getGravity() {
        return 0.075F;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; i++) {
                level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, getItem()), getX(), getY(), getZ(),
                        (random.nextFloat() - 0.5) * 0.08, (random.nextFloat() - 0.5) * 0.08, (random.nextFloat() - 0.5) * 0.08);
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide) return;
        level().explode(this, getX(), getY(), getZ(), RADIUS, Level.ExplosionInteraction.NONE);
        level().playSound(null, blockPosition(), explodeSound(), SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        level().broadcastEntityEvent(this, (byte) 3);
        discard();
    }

    /// Урон взрыва по виду бомбы (вызывается из `FurEntityEvents.onHurt`).
    public static void onExplosionHurt(LivingHurtEvent event) {
        if (!event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)
                || !(event.getSource().getDirectEntity() instanceof FurBombEntity bomb)) return;
        LivingEntity target = event.getEntity();
        switch (bomb.kind()) {
            case HOLY -> {
                if (target.getMobType() == MobType.UNDEAD) {
                    event.setAmount(event.getAmount() * 0.45F);
                    target.setSecondsOnFire(8);
                } else {
                    event.setAmount(event.getAmount() * 0.15F);
                }
            }
            case GHOST -> {
                target.setDeltaMovement(0, target.getDeltaMovement().y, 0);
                target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 0));
                event.setAmount(event.getAmount() * 0.2F);
            }
            case SONIC -> {
                target.addEffect(new MobEffectInstance(FurEffects.FEAR.get(), 80, 2, false, false, true));
                event.setAmount(event.getAmount() * 0.33F);
            }
        }
    }

    /// Звук броска (как у снежка).
    public static void playThrowSound(Level level, LivingEntity thrower) {
        level.playSound(null, thrower.getX(), thrower.getY(), thrower.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL,
                0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
    }
}
