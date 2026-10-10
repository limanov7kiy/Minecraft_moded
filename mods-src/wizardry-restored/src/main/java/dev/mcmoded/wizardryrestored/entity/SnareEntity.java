package dev.mcmoded.wizardryrestored.entity;

import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import dev.mcmoded.wizardryrestored.WizardryRestored;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/// Силок: ловушка на земле. Первый наступивший враг получает урон и на время почти обездвижен, силок пропадает.
public class SnareEntity extends AreaConstruct {
    public SnareEntity(EntityType<? extends SnareEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || tickCount % 2 != 0) return;
        var spell = WizardryRestored.SNARE.get();
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.1, 0.5, 0.1), LivingEntity::isAlive)) {
            if (isFriend(e)) continue;
            e.hurt(damage(EBDamageSources.MAGIC), spell.property(DefaultProperties.DAMAGE) * damageMultiplier);
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, spell.property(DefaultProperties.EFFECT_DURATION), 5));
            ((ServerLevel) level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()),
                    getX(), getY() + 0.2, getZ(), 15, 0.3, 0.1, 0.3, 0.1);
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.TRIPWIRE_CLICK_ON, getSoundSource(), 1, 0.7f);
            discard();
            return;
        }
    }
}
