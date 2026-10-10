/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/content/entity/boss/AbstractBossEntity.java; the death sequence (explosions, XP,
 * filling the treasure chest with loot + guaranteed schematic, dropping the key) restored from
 * Galacticraft Legacy (micdoodle8, radfast, Team Galacticraft) core/entities/EntityBossBase.java. See CREDITS.md.
 */
package dev.mcmoded.gcrevival.entity;

import dev.mcmoded.gcrevival.block.DungeonSpawnerBlockEntity;
import dev.mcmoded.gcrevival.block.TreasureChestBlockEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class AbstractBossEntity extends Monster {
    protected DungeonSpawnerBlockEntity spawner;

    public int entitiesWithin;
    public int entitiesWithinLast;

    private final ServerBossEvent bossEvent;

    protected AbstractBossEntity(EntityType<? extends AbstractBossEntity> type, Level level) {
        super(type, level);
        this.bossEvent = new ServerBossEvent(this.getDisplayName(), this.getHealthBarColor(), BossEvent.BossBarOverlay.PROGRESS);
        this.xpReward = 0;
        this.setPersistenceRequired();
    }

    public abstract int getChestTier();

    /** Схема, которая гарантированно кладётся в сундук сокровищ. */
    public abstract ItemStack getGuaranteedLoot(RandomSource random);

    /** Таблица лута сундука сокровищ. */
    public abstract ResourceLocation getTreasureLootTable();

    public abstract void dropKey();

    public abstract BossEvent.BossBarColor getHealthBarColor();

    @Override
    protected void customServerAiStep() {
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        super.customServerAiStep();
    }

    @Override
    public void checkDespawn() {
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.level().isClientSide) {
            if (this.deathTime >= 180 && this.deathTime <= 200) {
                float x = (this.random.nextFloat() - 0.5F) * this.getBbWidth();
                float y = (this.random.nextFloat() - 0.5F) * (this.getBbHeight() / 2.0F);
                float z = (this.random.nextFloat() - 0.5F) * this.getBbWidth();
                this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX() + x, this.getY() + 2.0D + y, this.getZ() + z, 0, 0, 0);
            }
            return;
        }

        ServerLevel level = (ServerLevel) this.level();
        if (this.deathTime >= 180 && this.deathTime % 5 == 0) {
            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 4.0F,
                    (1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F) * 0.7F);
        }
        if (this.deathTime > 150 && this.deathTime % 5 == 0) {
            ExperienceOrb.award(level, this.position(), 30);
        }

        if (this.deathTime == 200) {
            ExperienceOrb.award(level, this.position(), 20);
            this.fillTreasureChest();
            this.dropKey();
            if (this.spawner != null) {
                this.spawner.onBossKilled();
                this.spawner = null;
            }
            this.remove(RemovalReason.KILLED);
        }
    }

    private void fillTreasureChest() {
        TreasureChestBlockEntity chest = null;
        if (this.spawner != null && this.spawner.getChestPos() != null
                && this.level().getBlockEntity(this.spawner.getChestPos()) instanceof TreasureChestBlockEntity found) {
            chest = found;
        }
        if (chest == null) {
            chest = TreasureChestBlockEntity.findClosest(this, this.getChestTier());
        }
        if (chest == null || this.distanceToSqr(chest.getBlockPos().getCenter()) >= 1000 * 1000) {
            return;
        }
        chest.lock();
        chest.clearContent();
        chest.setLootTable(this.getTreasureLootTable(), this.random.nextLong());
        chest.unpackLootTable(null);
        chest.setItem(this.random.nextInt(chest.getContainerSize()), this.getGuaranteedLoot(this.random));
        chest.setChanged();
    }

    @Override
    public void aiStep() {
        if (this.spawner != null && !this.level().isClientSide && this.deathTime == 0) {
            List<Player> playersWithin = this.level().getEntitiesOfClass(Player.class, this.spawner.getRangeBounds());
            this.entitiesWithin = playersWithin.size();

            if (this.entitiesWithin == 0 && this.entitiesWithinLast != 0) {
                for (Player p : this.level().getEntitiesOfClass(Player.class, this.spawner.getRangeBoundsPlus11())) {
                    p.sendSystemMessage(Component.translatable("gui.gcrevival.skeleton_boss.despawn"));
                }
                // Победы не было: босс появится снова, когда игрок вернётся в комнату
                this.spawner.onBossDiscarded();
                this.spawner = null;
                this.discard();
                return;
            }
            this.entitiesWithinLast = this.entitiesWithin;
        }
        super.aiStep();
    }

    /** Для серверного теста: «здоровье/максимум/оружие». */
    public String describe() {
        return this.getHealth() + "/" + this.getMaxHealth() + "/" + this.getMainHandItem();
    }

    public void onBossSpawned(DungeonSpawnerBlockEntity spawner) {
        this.spawner = spawner;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }
}
