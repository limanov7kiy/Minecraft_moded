package dev.mcmoded.undeadrising.entity;

import dev.mcmoded.undeadrising.entity.ai.EntityAITargetItem;
import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurItems;
import dev.mcmoded.undeadrising.registry.FurSounds;
import dev.mcmoded.undeadrising.registry.FurTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/// Мимикраб — краб, живущий в сундуке. Сидит в засаде у сундуков в строениях, неотличимый от сундука (может быть
/// открыт — и тогда атакует), набит добычей сундука подземелья (в Незере — адской крепости); подбирает лежащие
/// предметы. Приручается крыльями Ptera; прирученный носит 27 ячеек (Shift+ПКМ — открыть), в беде ест мясо из своего
/// инвентаря, тотем бессмертия внутри спасает его от смерти. Око Края превращает его в «пустотного» (вместо
/// инвентаря — эндер-сундук хозяина), расплавленное сердце — в адского (огнестойкий, поджигает).
/// Оригинал: Fish's Undead Rising (Fish), `entities/tameable/MimicEntity`.
public class MimicEntity extends FURTameableEntity implements IAggressive {
    private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(MimicEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> CHEST_TEXTURE = SynchedEntityData.defineId(MimicEntity.class, EntityDataSerializers.STRING);
    private static final Component CONTAINER_TITLE = Component.translatable("container.enderchest");
    /// Текстуры сундуков, под которые маскируется мимикраб (в оригинале пул пополнялся совместимостью с модами сундуков).
    public static final List<String> TEXTURE_POOL = new ArrayList<>(List.of("textures/entity/chest/normal.png"));

    /// Mimic_Health, Mimic_Attack по умолчанию.
    public static final double HEALTH = 10.0, ATTACK = 8.0;
    public static final int VOID_SKIN = 3, NETHER_SKIN = 6;

    private int attackTimer;
    private int aggressiveTimer = 40;
    public float rotationAngle = 0.0F;
    public int idleTimer;
    public int sitTimer;
    public final SimpleContainer inventory = new SimpleContainer(27);
    private EntityAITargetItem targetItem;

    public MimicEntity(EntityType<? extends MimicEntity> type, Level level) {
        super(type, level);
        this.setCanPickUpLoot(true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, this.aiSit);
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetItem = new EntityAITargetItem(this, true);
        this.targetSelector.addGoal(4, this.targetItem);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKIN_TYPE, (4 + this.random.nextInt(5)) % 6);
        this.entityData.define(CHEST_TEXTURE, TEXTURE_POOL.get(this.random.nextInt(TEXTURE_POOL.size())));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.MAX_HEALTH, HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK)
                .add(Attributes.ARMOR, 20.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected Goal followGoal() {
        return new FollowOwnerGoal(this, 1.5, 10.0F, 2.0F, false);
    }

    /// Не исчезает, пока в нём что-то лежит.
    @Override
    public boolean removeWhenFarAway(double distance) {
        return this.inventory.isEmpty() && super.removeWhenFarAway(distance);
    }

    /// Появляется только рядом с сундуком (в радиусе 4) и — при естественном появлении — внутри строений из тега
    /// `mod_lavacow:mimic_habitat` (в 1.16.5 это делал список спавна строений).
    public static boolean checkMimicSpawnRules(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        if ((reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION)
                && !level.getLevel().structureManager().getStructureWithPieceAt(pos, FurTags.MIMIC_HABITAT).isValid()) {
            return false;
        }
        if (!isNearChest(level, pos, 4)) return false;
        return checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    private static boolean isNearChest(LevelReader level, BlockPos pos, int r) {
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-r, -r, -r), pos.offset(r - 1, r - 1, r - 1))) {
            if (level.getBlockState(p).is(Blocks.CHEST)) return true;
        }
        return false;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    /// Приручённый — втрое крепче и быстрее, но вдвое слабее.
    @Override
    public void setTame(boolean tamed) {
        super.setTame(tamed);
        if (tamed) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH * 3.0);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK * 0.5);
            this.setSilent(false);
            this.setHealth(this.getHealth() * 3.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.22);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK);
            this.setHealth(this.getHealth() * 0.334F);
        }
    }

    private boolean canPickupItems() {
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            if (this.inventory.getItem(i).isEmpty()) return true;
        }
        return false;
    }

    private void stash(ItemStack stack) {
        if (this.level().isClientSide) return;
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            if (this.inventory.getItem(i).isEmpty()) {
                this.inventory.setItem(i, stack.copy());
                stack.shrink(stack.getCount());
                return;
            }
        }
    }

    /// Номер ячейки с предметом или −1.
    public int containsItem(Item item) {
        if (this.level().isClientSide) return -1;
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            if (this.inventory.getItem(i).is(item)) return i;
        }
        return -1;
    }

    private void emergencyFood() {
        if (this.level().isClientSide) return;
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (this.isFood(stack)) {
                FoodProperties food = stack.getFoodProperties(this);
                this.playSound(SoundEvents.GENERIC_EAT, 0.4F, 1.0F);
                if (food != null) this.heal(food.getNutrition());
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 160, 0));
                stack.shrink(1);
                this.inventory.setChanged();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.aggressiveTimer > 0) this.aggressiveTimer--;
        if (this.attackTimer > 0) this.attackTimer--;

        if (!this.level().isClientSide) {
            if (!this.isAggressive() && !this.isTame()) {
                // Засада: встаёт ровно в центр блока, замирает и молчит.
                if (!this.isSilent()) {
                    this.setInSittingPose(true);
                    this.level().broadcastEntityEvent(this, (byte) (41 + this.random.nextInt(4)));
                }
                this.setPos(Mth.floor(this.getX()) + 0.5, Mth.floor(this.getY()), Mth.floor(this.getZ()) + 0.55);
                this.setYRot(this.rotationAngle);
                this.yRotO = this.rotationAngle;
                this.yBodyRot = this.yBodyRotO = 0.0F;
                if (this.level().getBlockState(this.blockPosition().below()).isAir()) {
                    this.setPos(this.getX(), this.getY() - 1.0, this.getZ());
                }
                this.setSilent(true);
                this.setSpeed(0.0F);
            } else if (this.getTarget() != null) {
                this.aggressiveTimer = 200;
                this.setSilent(false);
                this.setSpeed(0.19F);
            }
        }

        if (!this.isTame() && this.getTarget() != null && this.distanceTo(this.getTarget()) > this.getAttributeValue(Attributes.FOLLOW_RANGE)) {
            this.setTarget(null);
        }

        for (ItemStack held : this.getHandSlots()) {
            if (!held.isEmpty()) this.stash(held);
        }

        if (this.isTame() && this.getHealth() <= this.getMaxHealth() * 0.5F) this.emergencyFood();

        if (this.getSkin() == VOID_SKIN && this.tickCount % 100 == 0) {
            for (int i = 0; i < 8; i++) {
                int j = this.random.nextInt(2) * 2 - 1;
                int k = this.random.nextInt(2) * 2 - 1;
                this.level().addParticle(ParticleTypes.PORTAL, this.getX() + 0.5 + 0.25 * j, this.getY() + this.random.nextFloat(),
                        this.getZ() + 0.5 + 0.25 * k, this.random.nextFloat() * j, (this.random.nextFloat() - 0.5) * 0.125, this.random.nextFloat() * k);
            }
        }

        if (this.level().isClientSide) {
            if (this.isInSittingPose() && this.sitTimer > 0) this.sitTimer--;
            if (!this.isInSittingPose() && this.sitTimer < 20) this.sitTimer++;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.idleTimer > 0) this.idleTimer--;
        if (!this.isAggressive() && !this.isTame() && this.tickCount % 100 == 0 && this.random.nextInt(5) == 0) {
            this.idleTimer = 30 + this.random.nextInt(30);
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.sitTimer > 0 && this.sitTimer < 20) {
            this.setDeltaMovement(Vec3.ZERO);
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        Entity attacker = source.getDirectEntity();
        this.setInSittingPose(false);
        this.aggressiveTimer = 200;
        this.setSilent(false);
        this.setSpeed(0.19F);
        // Как в оригинале: урон не от игрока/стрелы делится пополам здесь (по прямому источнику) и ещё раз в FURTameableEntity#hurt.
        if (attacker != null && !(attacker instanceof Player) && !(attacker instanceof AbstractArrow)) {
            amount = (amount + 1.0F) / 2.0F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            this.playSound(FurSounds.SWARMER_ATTACK.get(), 1.0F, 1.0F);
            this.attackTimer = 5;
            this.level().broadcastEntityEvent(this, (byte) 40);
            if (this.getSkin() == NETHER_SKIN && this.random.nextInt(4) == 0) target.setSecondsOnFire(4);
        }
        return hit;
    }

    @Override
    public void doSitCommand(@Nullable Player player) {
        super.doSitCommand(player);
        this.level().broadcastEntityEvent(this, (byte) (41 + this.random.nextInt(4)));
        this.setInSittingPose(true);
    }

    @Override
    public void doFollowCommand(@Nullable Player player) {
        super.doFollowCommand(player);
        if (this.getSkin() == VOID_SKIN) {
            this.setCanPickUpLoot(false);
            this.dropInventory();
        }
        this.setInSittingPose(false);
    }

    /// Развернуться «лицом» как сундук (для засад в строениях).
    public void doMimicChest(Direction facing) {
        this.level().broadcastEntityEvent(this, (byte) (41 + facing.get2DDataValue()));
    }

    private void dropInventory() {
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
                this.spawnAtLocation(stack.copy(), 0.2F);
                stack.setCount(0);
            }
        }
        this.inventory.setChanged();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        if (item instanceof SpawnEggItem) return super.mobInteract(player, hand);

        if (this.isTame() && player.equals(this.getOwner())) {
            if (player.isCrouching() && !this.level().isClientSide) {
                if (this.getSkin() == VOID_SKIN) {
                    var ender = player.getEnderChestInventory();
                    player.openMenu(new SimpleMenuProvider((id, inv, p) -> ChestMenu.threeRows(id, inv, ender), CONTAINER_TITLE));
                    this.playSound(SoundEvents.ENDER_CHEST_OPEN, 1.0F, 1.0F);
                } else {
                    this.openGUI(player, this.getDisplayName());
                    this.playSound(SoundEvents.CHEST_OPEN, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (!stack.isEmpty()) {
                if (stack.isEdible()) {
                    FoodProperties food = stack.getFoodProperties(this);
                    if (food != null && food.isMeat() && this.getHealth() < this.getMaxHealth()) {
                        if (!player.getAbilities().instabuild) stack.shrink(1);
                        this.playSound(SoundEvents.GENERIC_EAT, 0.4F, 1.0F);
                        this.heal(food.getNutrition());
                        return InteractionResult.SUCCESS;
                    }
                } else if (this.getSkin() != VOID_SKIN && item == Items.ENDER_EYE) {
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    this.setSkin(VOID_SKIN);
                    this.setCanPickUpLoot(false);
                    this.dropInventory();
                    this.transformEffects();
                    return InteractionResult.SUCCESS;
                } else if (this.getSkin() != VOID_SKIN && item == FurItems.MOOTENHEART.get()) {
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    this.setSkin(NETHER_SKIN);
                    this.transformEffects();
                    return InteractionResult.SUCCESS;
                }
            }
            return super.mobInteract(player, hand);
        }

        if (!this.isTame() && this.distanceToSqr(player) < 2.0) {
            // Попытка «открыть сундук» — засада раскрыта.
            this.playSound(SoundEvents.CHEST_OPEN, 1.0F, 1.0F);
            this.playSound(FurSounds.MIMIC_AMBIENT.get(), 0.4F, 1.0F);
            this.setTarget(player);
            this.setInSittingPose(false);
        }
        return super.mobInteract(player, hand);
    }

    private void transformEffects() {
        this.playSound(SoundEvents.AMBIENT_CAVE.value(), 1.0F, 1.0F);
        for (int i = 0; i < 16; i++) {
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT,
                    this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth(),
                    this.getY() + this.random.nextFloat() * this.getBbHeight(),
                    this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth(),
                    this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02);
        }
    }

    /// Начинка — добыча сундука подземелья (в Незере — адской крепости), из каждой ячейки 95% теряют до 3 предметов.
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK);
        this.setHealth(this.getMaxHealth());
        if (level.getBiome(this.blockPosition()).is(BiomeTags.IS_NETHER)) this.setSkin(NETHER_SKIN);
        // Скин 7 (гробница в пустыне) — строение не перенесено.
        this.unpackLootTable(level.getLevel(), this.getSkin() == NETHER_SKIN ? BuiltInLootTables.NETHER_BRIDGE : BuiltInLootTables.SIMPLE_DUNGEON);
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            if (this.random.nextFloat() >= 0.05F) this.inventory.removeItem(i, 3);
        }
        return data;
    }

    private void unpackLootTable(ServerLevel level, ResourceLocation id) {
        LootTable table = level.getServer().getLootData().getLootTable(id);
        LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.blockPosition()))
                .withLuck(-5.0F).create(LootContextParamSets.CHEST);
        table.fill(this.inventory, params, this.random.nextLong());
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(FurItems.PTERA_WING.get()) || stack.is(FurItems.PTERA_WING_COOKED.get());
    }

    /// Ведёт себя агрессивно, пока есть цель / недавно били; иначе изредка «засыпает» сундуком.
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.getTarget() != null || this.aggressiveTimer > 0 || this.lastHurtByPlayerTime > 58) {
            this.setAggressive(true);
            this.level().broadcastEntityEvent(this, (byte) 11);
        } else if (!this.targetItem.canUse() || !this.canPickupItems()) {
            if (this.random.nextInt(1000) < 100) {
                this.setAggressive(false);
                this.level().broadcastEntityEvent(this, (byte) 34);
            }
        } else if (!this.isAggressive() && this.random.nextInt(1000) < 10) {
            this.setAggressive(true);
            this.level().broadcastEntityEvent(this, (byte) 11);
        }
    }

    public void openGUI(Player player, Component name) {
        if (!this.level().isClientSide && !this.hasPassenger(player)) {
            player.openMenu(new SimpleMenuProvider((id, inv, p) -> ChestMenu.threeRows(id, inv, this.inventory), name));
        }
    }

    @Override
    public boolean fireImmune() {
        return this.getSkin() == NETHER_SKIN || super.fireImmune();
    }

    public String getChestTexture() {
        return this.entityData.get(CHEST_TEXTURE);
    }

    public void setChestTexture(String texture) {
        this.entityData.set(CHEST_TEXTURE, texture);
    }

    @Override
    public int getSkin() {
        return this.entityData.get(SKIN_TYPE);
    }

    @Override
    public void setSkin(int skin) {
        this.entityData.set(SKIN_TYPE, skin);
    }

    public int getVoidSkin() {
        return VOID_SKIN;
    }

    public int getSitTimer() {
        return this.sitTimer;
    }

    @Override
    public int getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    public void setAttackTimer(int ticks) {
        this.attackTimer = ticks;
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case 40 -> this.attackTimer = 5;
            case 41 -> this.rotationAngle = (float) Math.PI;
            case 42 -> this.rotationAngle = (float) (Math.PI * 3.0 / 2.0);
            case 43 -> this.rotationAngle = 0.0F;
            case 44 -> this.rotationAngle = (float) (Math.PI / 2);
            case 11 -> this.setAggressive(true);
            case 34 -> this.setAggressive(false);
            default -> super.handleEntityEvent(id);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 0.7F;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.inventory.fromTag(tag.getList("Items", 10));
        this.setSkin(tag.getInt("Variant"));
        if (tag.contains("Chest")) this.setChestTexture(tag.getString("Chest"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Items", this.inventory.createTag());
        tag.putInt("Variant", this.getSkin());
        tag.putString("Chest", this.getChestTexture());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return FurSounds.MIMIC_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return FurSounds.MIMIC_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return FurSounds.MIMIC_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    /// Не толкается (стоит сундуком).
    @Override
    public void push(Entity entity) {}

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public boolean shouldDropExperience() {
        return !this.isTame() && super.shouldDropExperience();
    }

    @Nullable
    @Override
    public MimicEntity getBreedOffspring(ServerLevel level, AgeableMob other) {
        MimicEntity child = FurEntities.MIMIC.get().create(level);
        if (child == null) return null;
        UUID owner = this.getOwnerUUID();
        if (owner != null) {
            child.setOwnerUUID(owner);
            child.setTame(true);
            child.setHealth(this.getMaxHealth());
            child.setSkin(this.random.nextBoolean() || !(other instanceof MimicEntity m) ? this.getSkin() : m.getSkin());
            if (child.getSkin() == VOID_SKIN) child.setSkin(0);
        }
        return child;
    }

    /// При смерти высыпает инвентарь (кроме предметов с проклятием утраты).
    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (!this.level().isClientSide) {
            for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                ItemStack stack = this.inventory.getItem(i);
                if (!stack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(stack)) this.spawnAtLocation(stack);
            }
        }
        this.inventory.clearContent();
    }

    /// Предметы руками мимикраб не держит — сразу складывает в инвентарь (см. {@link #tick}).
    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return this.canPickUpLoot() && this.canPickupItems();
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        return this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && this.canPickupItems();
    }
}
