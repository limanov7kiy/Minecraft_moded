package dev.mcmoded.undeadrising.entity.floating;

import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurSounds;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

/// Банши — парящий призрак гор и холмов. Вплотную к цели кричит: урон магией всем живым (кроме нежити) в радиусе
/// {@link #SCREAM_RADIUS} и «Страх» III. Оставляет тёмный шлейф.
/// Оригинал: Fish's Undead Rising (Fish), `entities/floating/BansheeEntity`.
public class BansheeEntity extends FloatingMobEntity {
    /// Banshee_Health / Banshee_Attack / Banshee_Ability_Radius по умолчанию.
    public static final double HEALTH = 34.0, ATTACK = 7.0, SCREAM_RADIUS = 3.0;

    public BansheeEntity(EntityType<? extends BansheeEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AIUseSpell());
        this.goalSelector.addGoal(3, new AIChargeAttack());
    }

    @Override
    protected void applyEntityAI() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK);
    }

    @Nullable
    @Override
    protected ParticleOptions particleType() {
        return new DustParticleOptions(new Vector3f(0.2F, 0.21F, 0.23F), 0.6F);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getType() == FurEntities.BANSHEE.get() && this.getSpellTicks() > 8 && this.getSpellTicks() < 13) {
            this.level().addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY() + this.getBbHeight(), this.getZ(), 0.0, 1.0, 0.0);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        if (this.getType() == FurEntities.BANSHEE.get()) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK);
            this.setHealth(this.getMaxHealth());
        }
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return size.height * 0.8F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return FurSounds.BANSHEE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return FurSounds.BANSHEE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return FurSounds.BANSHEE_DEATH.get();
    }

    protected SoundEvent getSpellSound() {
        return FurSounds.BANSHEE_ATTACK.get();
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    /// Крик: подготовка 20 тиков (на 15-м — сам крик), перезарядка 320 тиков, только вплотную (< 3 блоков).
    public class AIUseSpell extends Goal {
        protected int spellWarmup;
        protected int spellCooldown;

        @Override
        public boolean canUse() {
            BansheeEntity self = BansheeEntity.this;
            if (self.getTarget() == null || self.isSpellcasting()) return false;
            return self.tickCount >= this.spellCooldown && self.distanceTo(self.getTarget()) < 3.0F;
        }

        @Override
        public boolean canContinueToUse() {
            return BansheeEntity.this.getTarget() != null && this.spellWarmup > 0;
        }

        @Override
        public void start() {
            BansheeEntity self = BansheeEntity.this;
            this.spellWarmup = 20;
            self.spellTicks = 30;
            self.level().broadcastEntityEvent(self, (byte) 10);
            this.spellCooldown = self.tickCount + 320;
        }

        @Override
        public void tick() {
            this.spellWarmup--;
            if (this.spellWarmup == 5) {
                this.castSpell();
                BansheeEntity.this.playSound(BansheeEntity.this.getSpellSound(), 4.0F, 1.2F);
            }
        }

        protected void castSpell() {
            BansheeEntity self = BansheeEntity.this;
            List<Entity> list = self.level().getEntities(self, self.getBoundingBox().inflate(SCREAM_RADIUS));
            self.level().broadcastEntityEvent(self, (byte) 11);
            float localDifficulty = self.level().getCurrentDifficultyAt(self.blockPosition()).getEffectiveDifficulty();
            for (Entity e : list) {
                if (e instanceof LivingEntity living && living.getMobType() != MobType.UNDEAD
                        && living.hurt(self.damageSources().indirectMagic(self, self), (float) self.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                    living.addEffect(new MobEffectInstance(FurEffects.FEAR.get(), 40 * (int) localDifficulty, 2, false, false, true));
                }
            }
        }
    }
}
