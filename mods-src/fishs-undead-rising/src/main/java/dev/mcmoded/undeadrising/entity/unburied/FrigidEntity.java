package dev.mcmoded.undeadrising.entity.unburied;

import dev.mcmoded.undeadrising.registry.FurItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/// Обмороженный: зомби холодных биомов. Удар сильно замедляет, огонь наносит двойной урон, иногда держит мёрзлое бедро.
/// Оригинал: Fish's Undead Rising (Fish), `entities/tameable/unburied/FrigidEntity`.
public class FrigidEntity extends UnburiedEntity {
    /// ZombieFrozen_Health / ZombieFrozen_Attack по умолчанию.
    public static final double HEALTH = 30.0, ATTACK = 3.0;

    public FrigidEntity(EntityType<? extends FrigidEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new FleeSunGoal(this, 1.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK)
                .add(Attributes.ARMOR, 2.0);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!super.doHurtTarget(target)) return false;
        if (target instanceof LivingEntity living) {
            if (this.isTame()) {
                int duration = this.baneOfArthropods > 0 && living.getMobType() == MobType.ARTHROPOD
                        ? 20 + this.random.nextInt(10 * this.baneOfArthropods) : 40;
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 4));
            } else {
                float localDifficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40 * (int) localDifficulty, 4));
            }
        }
        return true;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        if (random.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(FurItems.FROZENTHIGH.get()));
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE) ? 2.0F * amount : amount);
    }
}
