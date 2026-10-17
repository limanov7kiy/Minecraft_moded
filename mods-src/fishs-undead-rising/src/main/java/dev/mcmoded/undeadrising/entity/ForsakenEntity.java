package dev.mcmoded.undeadrising.entity;

import dev.mcmoded.undeadrising.UndeadRising;
import dev.mcmoded.undeadrising.effect.FurEffects;
import dev.mcmoded.undeadrising.entity.ai.EntityChargeAttackGoal;
import dev.mcmoded.undeadrising.entity.projectile.DeathCoilEntity;
import dev.mcmoded.undeadrising.registry.FurBannerPatterns;
import dev.mcmoded.undeadrising.registry.FurItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

/// Отрёкшийся — скелет пустынь в четырёх обличьях (NBT `Variant`): 0 — щит с гербом Короля-скелета, меч и таран;
/// 1 — меч; 2 — лук; 3 — посох с «Кольцом смерти». Удары и стрелы накладывают Смертное касание III. Не горит на солнце,
/// союзник Короля-скелета (кроме прирученных — тег `FUR_tameSkeleton`).
/// Оригинал: Fish's Undead Rising (Fish), `entities/ForsakenEntity`.
public class ForsakenEntity extends AbstractSkeleton {
    /// Forsaken_Health / Forsaken_Attack по умолчанию.
    public static final double HEALTH = 30.0, ATTACK = 4.0;
    private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(ForsakenEntity.class, EntityDataSerializers.INT);
    private static final ResourceLocation SKELETON_KING = new ResourceLocation(UndeadRising.MOD_ID, "skeletonking");

    // Конструктор AbstractSkeleton вызывает reassessWeaponGoal() раньше инициализации этих полей — там проверка на null.
    private final EntityChargeAttackGoal chargeGoal = new EntityChargeAttackGoal(this);
    private final RangedAttackGoal staffGoal = new RangedAttackGoal(this, 1.25, 40, 20.0F) {
        @Override
        public void stop() {
            super.stop();
            ForsakenEntity.this.setAggressive(false);
        }

        @Override
        public void start() {
            super.start();
            ForsakenEntity.this.setAggressive(true);
        }
    };

    public ForsakenEntity(EntityType<? extends ForsakenEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKIN_TYPE, this.random.nextInt(4));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSkeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK);
    }

    /// Во время тарана толкает и бьёт всех, в кого врезается.
    @Override
    public void push(Entity other) {
        super.push(other);
        if (this.chargeGoal != null && this.isCharging() && !this.isAlliedTo(other)) {
            this.doHurtTarget(other);
            other.setDeltaMovement(other.getDeltaMovement().add(this.getLookAngle().normalize().multiply(0.8, 1.6, 0.8)));
        }
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (source.getEntity() instanceof Creeper creeper && creeper.canDropMobsSkull()) {
            creeper.increaseDroppedSkulls();
            this.spawnAtLocation(Items.SKELETON_SKULL);
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        // снаряжение выдаётся в finalizeSpawn по обличью
    }

    @Override
    public void reassessWeaponGoal() {
        if (this.staffGoal == null) {
            super.reassessWeaponGoal();
            return;
        }
        if (this.level() != null && !this.level().isClientSide) {
            this.goalSelector.removeGoal(this.staffGoal);
            this.goalSelector.removeGoal(this.chargeGoal);
            if (this.getMainHandItem().is(FurItems.FORSAKEN_STAFF.get())) {
                this.goalSelector.addGoal(4, this.staffGoal);
            } else {
                super.reassessWeaponGoal();
                if (this.getOffhandItem().is(Items.SHIELD)) this.goalSelector.addGoal(2, this.chargeGoal);
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        if (this.getMainHandItem().is(FurItems.FORSAKEN_STAFF.get())) {
            DeathCoilEntity coil = new DeathCoilEntity(this.level(), this, 0.0, 0.0, 0.0);
            coil.setPos(
                    this.getX() - (this.getBbWidth() + 1.0F) * 0.5 * Mth.sin(this.yBodyRot * Mth.DEG_TO_RAD),
                    this.getEyeY() - 0.1F,
                    this.getZ() + (this.getBbWidth() + 1.0F) * 0.5 * Mth.cos(this.yBodyRot * Mth.DEG_TO_RAD));
            double dx = target.getX() - this.getX();
            double dy = target.getY(0.3333333333333333) - coil.getY();
            double dz = target.getZ() - this.getZ();
            float f = Mth.sqrt((float) (dx * dx + dz * dz)) * 0.2F;
            coil.shoot(dx, dy + f, dz, 1.5F, 10.0F);
            if (!this.isSilent()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLAZE_SHOOT, this.getSoundSource(),
                        1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }
            this.level().addFreshEntity(coil);
        } else {
            super.performRangedAttack(target, power);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data, tag);
        RandomSource random = level.getRandom();
        switch (this.getSkin()) {
            case 0:
                this.setItemSlot(EquipmentSlot.OFFHAND, kingShield());
                this.getAttribute(Attributes.ARMOR).setBaseValue(4.0);
                this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.4);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.22);
                this.getOffhandItem().setDamageValue(random.nextInt(this.getOffhandItem().getMaxDamage()));
                // дальше как обличье 1 — меч
            case 1:
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                break;
            case 3:
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(FurItems.FORSAKEN_STAFF.get()));
                break;
            case 2:
            default:
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                break;
        }
        ItemStack main = this.getMainHandItem();
        if (main.isDamageableItem()) main.setDamageValue(random.nextInt(main.getMaxDamage()));
        this.populateDefaultEquipmentEnchantments(random, difficulty);
        this.reassessWeaponGoal();
        return result;
    }

    /// Чёрный щит с белым гербом Короля-скелета.
    private static ItemStack kingShield() {
        ItemStack shield = new ItemStack(Items.SHIELD);
        CompoundTag pattern = new CompoundTag();
        pattern.putString("Pattern", FurBannerPatterns.SKELETONKING_HASH);
        pattern.putInt("Color", DyeColor.WHITE.getId());
        ListTag patterns = new ListTag();
        patterns.add(pattern);
        CompoundTag bet = shield.getOrCreateTagElement("BlockEntityTag");
        bet.put("Patterns", patterns);
        bet.putInt("Base", DyeColor.BLACK.getId());
        return shield;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!super.doHurtTarget(target)) return false;
        if (target instanceof LivingEntity living) living.addEffect(new MobEffectInstance(FurEffects.FRAGILE.get(), 200, 2));
        return true;
    }

    @Override
    public boolean isAlliedTo(Entity other) {
        if (other == null) return false;
        if (other == this || super.isAlliedTo(other)) return true;
        boolean kin = other instanceof ForsakenEntity || SKELETON_KING.equals(EntityType.getKey(other.getType()));
        return kin && !this.getTags().contains("FUR_tameSkeleton");
    }

    public boolean isCharging() {
        return this.chargeGoal != null && this.chargeGoal.isCharging();
    }

    @Override
    protected AbstractArrow getArrow(ItemStack arrowStack, float velocity) {
        AbstractArrow arrow = super.getArrow(arrowStack, velocity);
        if (arrow instanceof Arrow a) a.addEffect(new MobEffectInstance(FurEffects.FRAGILE.get(), 200, 2));
        return arrow;
    }

    public int getSkin() {
        return this.entityData.get(SKIN_TYPE);
    }

    public void setSkin(int skin) {
        this.entityData.set(SKIN_TYPE, skin);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSkin(tag.getInt("Variant"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getSkin());
    }
}
