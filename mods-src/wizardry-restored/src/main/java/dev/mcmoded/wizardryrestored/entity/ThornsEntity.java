package dev.mcmoded.wizardryrestored.entity;

import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import dev.mcmoded.wizardryrestored.WizardryRestored;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/// Куст шипов из леса шипов: колет врагов, которые к нему прижались, и цепляет их (замедление).
public class ThornsEntity extends AreaConstruct {
    public ThornsEntity(EntityType<? extends ThornsEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || tickCount % 10 != 0) return;
        var spell = WizardryRestored.FOREST_OF_THORNS.get();
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.4), LivingEntity::isAlive)) {
            if (isFriend(e)) continue;
            e.hurt(damage(EBDamageSources.MAGIC), spell.property(DefaultProperties.DAMAGE) * damageMultiplier);
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 2));
        }
    }
}
