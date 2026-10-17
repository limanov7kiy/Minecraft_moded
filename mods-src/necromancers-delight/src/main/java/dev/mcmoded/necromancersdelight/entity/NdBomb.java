package dev.mcmoded.necromancersdelight.entity;

import com.binaris.wizardry.api.content.entity.projectile.BombEntity;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBSounds;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3f;

import java.util.function.Supplier;

/// Бомбы Necromancer's Delight (оригинал — `EntityStinkBomb`, `EntityFrostBomb`, WinDanesz): прямое попадание —
/// урон `DIRECT_DAMAGE` и эффект на `EFFECT_DURATION`; всплеск в `EFFECT_RADIUS` — `SPLASH_DAMAGE` и эффект на 2/3
/// длительности (в оригинале 300/200 у вонючей и 150/100 у морозной). Без ограничения времени полёта, как в оригинале.
public class NdBomb extends BombEntity {
    private final Supplier<Spell> spell;
    private final Supplier<MobEffect> effect;
    private final ResourceKey<DamageType> damageType;
    private final Supplier<Item> item;
    private final Vector3f color;

    public NdBomb(EntityType<? extends ThrowableProjectile> type, Level level, Supplier<Spell> spell, Supplier<MobEffect> effect,
                  ResourceKey<DamageType> damageType, Supplier<Item> item, int rgb) {
        super(type, level);
        this.spell = spell;
        this.effect = effect;
        this.damageType = damageType;
        this.item = item;
        this.color = new Vector3f((rgb >> 16 & 255) / 255f, (rgb >> 8 & 255) / 255f, (rgb & 255) / 255f);
    }

    @Override
    public int getLifeTime() {
        return -1;
    }

    @Override
    public float getDamage(EntityHitResult hit) {
        return spell.get().property(DefaultProperties.DIRECT_DAMAGE) * damageMultiplier;
    }

    @Override
    public ResourceKey<DamageType> getDamageType(EntityHitResult hit) {
        return damageType;
    }

    @Override
    protected Item getDefaultItem() {
        return item.get();
    }

    @Override
    public void onHitTargetExtraEffects(EntityHitResult hit) {
        if (hit.getEntity() instanceof LivingEntity living) applyEffect(living, 1f);
        splash(hit.getEntity());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        splash(null);
    }

    private void applyEffect(LivingEntity target, float durationFactor) {
        MobEffect e = effect.get();
        if (e == null) return;
        int duration = (int) (spell.get().property(DefaultProperties.EFFECT_DURATION) * durationFactor);
        target.addEffect(new MobEffectInstance(e, duration, spell.get().property(DefaultProperties.EFFECT_STRENGTH)));
    }

    private void splash(Entity direct) {
        if (level().isClientSide) return;
        double range = spell.get().property(DefaultProperties.EFFECT_RADIUS) * blastMultiplier;
        float splash = spell.get().property(DefaultProperties.SPLASH_DAMAGE) * damageMultiplier;
        for (LivingEntity t : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range))) {
            if (t == direct || t == getOwner() || !isValidTarget(t) || t.distanceToSqr(this) > range * range) continue;
            MagicDamageSource.causeMagicDamage(this, t, splash, damageType);
            applyEffect(t, 2f / 3f);
        }
    }

    @Override
    protected void playHitSound(HitResult result) {
        playSound(EBSounds.ENTITY_POISON_BOMB_SMASH.get(), 1.5f, random.nextFloat() * 0.4f + 0.6f);
        playSound(EBSounds.ENTITY_POISON_BOMB_POISON.get(), 1.2f, 1f);
    }

    @Override
    protected void spawnHitParticles(HitResult.Type type) {
        if (level() instanceof ServerLevel sl) {
            sl.sendParticles(new DustParticleOptions(color, 2f), getX(), getY(), getZ(), (int) (60 * blastMultiplier), blastMultiplier, blastMultiplier, blastMultiplier, 0.1);
            sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
        }
    }
}
