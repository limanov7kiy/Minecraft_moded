package dev.mcmoded.undeadrising.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.UUID;

/// Базовый класс приручаемых мобов FUR: состояния «бродит / сидит / следует» по клику владельца, владельцем может быть
/// не только игрок (миньоны мобов), миньоны мобов умирают вместе с хозяином («suicidal minion», в оригинале — опция
/// конфига, по умолчанию включена), урон от мобов вдвое меньше. Наследники: Unburied-семейство; дальше — Salamander,
/// Weta, Mimic и т. п. (переопределяют {@link #isFood}, {@link #isCommandable}, {@link #wanderGoal}, {@link #followGoal}).
/// Оригинал: Fish's Undead Rising (Fish), `entities/tameable/FURTameableEntity`.
public class FURTameableEntity extends TamableAnimal {
    protected State state = State.WANDERING;
    @Nullable protected Goal wander;
    @Nullable protected Goal follow;
    @Nullable protected SitWhenOrderedToGoal aiSit;

    public FURTameableEntity(EntityType<? extends FURTameableEntity> type, Level level) {
        super(type, level);
        this.setTame(false);
    }

    @Override
    protected void registerGoals() {
        this.wander = this.wanderGoal();
        this.follow = this.followGoal();
        this.aiSit = new SitWhenOrderedToGoal(this);
        this.goalSelector.addGoal(7, this.wander);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 10.0F;
    }

    /// Правило появления в темноте (как у монстров). В 1.16.5 FUR проверял свет по-старому (≤ rand(8)); здесь —
    /// ванильная проверка 1.20.1 `Monster.isDarkEnoughToSpawn` (учитывает настройки измерения).
    public static boolean checkMonsterSpawnRules(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && (reason == MobSpawnType.SPAWNER || Monster.isDarkEnoughToSpawn(level, pos, random))
                && Mob.checkMobSpawnRules(type, level, reason, pos, random);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return !this.isTame() || !(this.getOwner() instanceof Player);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return !this.isTame() || !(this.getOwner() instanceof Player);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return this.isTame() && this.getOwner() instanceof Player || super.requiresCustomPersistence();
    }

    /// Ограниченное время жизни (призванные миньоны). Базовый класс — без ограничения.
    public void setLimitedLife(int ticks) {}

    protected boolean isCommandable() {
        return true;
    }

    protected boolean canSitCondition() {
        return true;
    }

    public void doSitCommand(@Nullable Player player) {
        if (this.wander != null) this.goalSelector.removeGoal(this.wander);
        if (this.follow != null) this.goalSelector.removeGoal(this.follow);
        this.jumping = false;
        this.getNavigation().stop();
        this.state = State.SITTING;
        this.setInSittingPose(true);
        if (player != null) player.displayClientMessage(Component.translatable("command.mod_lavacow.sitting", this.getName()), true);
    }

    public void doFollowCommand(@Nullable Player player) {
        if (this.wander != null) this.goalSelector.removeGoal(this.wander);
        this.follow = this.followGoal();
        this.goalSelector.addGoal(6, this.follow);
        this.getNavigation().stop();
        this.state = State.FOLLOWING;
        this.setInSittingPose(false);
        if (player != null) player.displayClientMessage(Component.translatable("command.mod_lavacow.following", this.getName()), true);
    }

    public void doWanderCommand(@Nullable Player player) {
        if (this.follow != null) this.goalSelector.removeGoal(this.follow);
        this.wander = this.wanderGoal();
        this.goalSelector.addGoal(7, this.wander);
        this.getNavigation().stop();
        this.state = State.WANDERING;
        this.setInSittingPose(false);
        if (player != null) player.displayClientMessage(Component.translatable("command.mod_lavacow.wandering", this.getName()), true);
    }

    protected Goal wanderGoal() {
        return new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F);
    }

    protected Goal followGoal() {
        return new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false);
    }

    /// Шанс приручения: 1 к N за кормление.
    protected int tameRate(ItemStack stack) {
        return 3;
    }

    public boolean isWandering() {
        return this.state == State.WANDERING;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty()) {
            InteractionResult result = stack.interactLivingEntity(player, this, hand);
            if (result == InteractionResult.SUCCESS) return result;
        }

        if (!this.level().isClientSide) {
            if (this.isFood(stack) && this.canTameCondition()) {
                if (!player.getAbilities().instabuild) {
                    if (stack.getItem() instanceof BucketItem) {
                        stack.shrink(1);
                        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                    } else {
                        // В оригинале ещё NetherStewItem → миска; суп-предметы FUR не перенесены.
                        stack.shrink(1);
                    }
                }
                if (this.random.nextInt(this.tameRate(stack)) == 0 && !ForgeEventFactory.onAnimalTame(this, player)) {
                    this.tame(player);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.setHealth(this.getMaxHealth());
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
                return InteractionResult.CONSUME;
            }

            if (this.isTame() && this.isOwnedBy(player) && this.isCommandable() && hand == InteractionHand.MAIN_HAND
                    && !this.isFood(stack) && this.getPassengers().isEmpty()) {
                switch (this.state) {
                    case WANDERING -> {
                        if (this.canSitCondition()) this.doSitCommand(player);
                    }
                    case SITTING -> this.doFollowCommand(player);
                    case FOLLOWING -> this.doWanderCommand(player);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    protected boolean canTameCondition() {
        return !this.isTame();
    }

    @Override
    public void tame(Player player) {
        super.tame(player);
        this.setPersistenceRequired();
        this.doSitCommand(null);
        this.doFollowCommand(null);
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity owner = this.level().isClientSide ? null : this.getOwner();
        if (owner != null && !(owner instanceof Player) && !owner.isAlive()) {
            // Suicidal_Minion = true (значение по умолчанию оригинала): миньон моба гибнет вместе с хозяином, без добычи.
            this.hurt(this.damageSources().mobAttack(this), this.getMaxHealth());
            this.addTag("FUR_noLoot");
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        Entity attacker = source.getEntity();
        this.setOrderedToSit(false);
        if (attacker != null && !(attacker instanceof Player) && !(attacker instanceof AbstractArrow)) {
            amount = (amount + 1.0F) / 2.0F;
        }
        return super.hurt(source, amount);
    }

    /// Владелец — любая живая сущность по UUID (не только игрок), как в оригинале.
    @Nullable
    @Override
    public LivingEntity getOwner() {
        UUID uuid = this.getOwnerUUID();
        if (uuid == null) return null;
        LivingEntity owner = this.level().getPlayerByUUID(uuid);
        if (owner == null && this.level() instanceof ServerLevel server && server.getEntity(uuid) instanceof LivingEntity living) {
            owner = living;
        }
        return owner;
    }

    @Override
    public int getMaxHeadXRot() {
        return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return null;
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof Creeper || target instanceof Ghast) return false;
        if (target instanceof Player tp && owner instanceof Player op && !op.canHarmPlayer(tp)) return false;
        return !(target instanceof TamableAnimal t) || !t.isTame();
    }

    public int getSkin() {
        return 0;
    }

    public void setSkin(int skin) {}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("state", (byte) switch (this.state) {
            case WANDERING -> 0;
            case SITTING -> 1;
            case FOLLOWING -> 2;
        });
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        switch (tag.getByte("state")) {
            case 0 -> this.doWanderCommand(null);
            case 1 -> this.doSitCommand(null);
            case 2 -> this.doFollowCommand(null);
            default -> {}
        }
    }

    public enum State {
        SITTING,
        WANDERING,
        FOLLOWING
    }
}
