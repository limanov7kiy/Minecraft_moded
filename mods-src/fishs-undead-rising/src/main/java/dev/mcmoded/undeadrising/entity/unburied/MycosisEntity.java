package dev.mcmoded.undeadrising.entity.unburied;

import dev.mcmoded.undeadrising.UndeadRising;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.joml.Vector3f;

import javax.annotation.Nullable;

/// Микоз (в Necromancer's Delight и конфиге FUR — «ZombieMushroom»): зомби, проросший грибом. Отравляет всех рядом,
/// на Hard после смерти оставляет ядовитое облако. Вариант 1 — светящийся (глубоко под землёй, свои глаза и добыча
/// `entities/mycosis1`), вариант 0 — с кордицепсом.
/// Оригинал: Fish's Undead Rising (Fish), `entities/tameable/unburied/MycosisEntity`.
public class MycosisEntity extends UnburiedEntity {
    /// ZombieMushroom_Health / ZombieMushroom_Attack по умолчанию.
    public static final double HEALTH = 20.0, ATTACK = 3.0;
    private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(MycosisEntity.class, EntityDataSerializers.INT);
    private static final Vector3f[] SPORE_COLOR = {new Vector3f(0.83F, 0.73F, 0.5F), new Vector3f(0.0F, 0.98F, 0.93F)};

    public MycosisEntity(EntityType<? extends MycosisEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKIN_TYPE, 0);
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
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount % 20 == 0) {
            for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0))) {
                if (e != this && !this.isOwnedBy(e) && !e.hasEffect(MobEffects.POISON)) {
                    float localDifficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
                    e.addEffect(new MobEffectInstance(MobEffects.POISON, 40 * (int) localDifficulty, 0));
                }
            }
        }
        Vector3f c = SPORE_COLOR[Math.min(this.getSkin(), SPORE_COLOR.length - 1)];
        this.level().addParticle(new DustParticleOptions(c, 0.6F),
                this.getX() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(),
                this.getY() + this.random.nextFloat() * this.getBbHeight(),
                this.getZ() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(),
                0.0, 0.0, 0.0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        // Оригинал: светящийся вариант рядом с блоками светящегося гриба или глубже y=50 без неба. Блоков гриба в порте
        // пока нет — остаётся только условие глубины.
        if (this.getY() < 50.0 && !this.level().canSeeSky(this.blockPosition())) this.setSkin(1);
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!this.level().isClientSide && this.level().getDifficulty() == Difficulty.HARD && !this.isOnFire()) {
            AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            float localDifficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            cloud.setOwner(this);
            cloud.setRadius(3.0F);
            cloud.setRadiusOnUse(-0.5F);
            cloud.setWaitTime(10);
            cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
            cloud.setPotion(Potions.POISON);
            cloud.addEffect(new MobEffectInstance(MobEffects.POISON, 40 * (int) localDifficulty, 0));
            this.level().addFreshEntity(cloud);
        }
    }

    @Override
    public int getSkin() {
        return this.entityData.get(SKIN_TYPE);
    }

    @Override
    public void setSkin(int skin) {
        this.entityData.set(SKIN_TYPE, skin);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getSkin());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSkin(tag.getInt("Variant"));
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return this.getSkin() == 1 ? new ResourceLocation(UndeadRising.MOD_ID, "entities/mycosis1") : super.getDefaultLootTable();
    }
}
