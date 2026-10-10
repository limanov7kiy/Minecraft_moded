/*
 * Ported from Galacticraft 5 (Copyright (c) 2019-2026 Team Galacticraft, MIT):
 * dev/galacticraft/mod/content/block/entity/DungeonSpawnerBlockEntity.java
 * (same logic as Galacticraft Legacy TileEntityDungeonSpawner). See CREDITS.md. Adapted to Minecraft 1.20.1 Forge.
 */
package dev.mcmoded.gcrevival.block;

import dev.mcmoded.gcrevival.ModRegistry;
import dev.mcmoded.gcrevival.entity.AbstractBossEntity;
import dev.mcmoded.gcrevival.entity.evolved.EvolvedCreeper;
import dev.mcmoded.gcrevival.entity.evolved.EvolvedSkeleton;
import dev.mcmoded.gcrevival.entity.evolved.EvolvedSpider;
import dev.mcmoded.gcrevival.entity.evolved.EvolvedZombie;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class DungeonSpawnerBlockEntity extends BlockEntity {
    /** Через сколько после убийства босс может появиться снова (как в GC: 15 минут). */
    private static final long RESPAWN_MILLIS = 900_000L;

    public EntityType<? extends AbstractBossEntity> bossType;
    public AbstractBossEntity boss;
    public boolean spawned;
    public boolean isBossDefeated;
    public boolean playerInRange;
    public boolean lastPlayerInRange;
    private Vec3i roomCoords;
    private Vec3i roomSize;
    public long lastKillTime;
    private BlockPos chestPos;
    private AABB range15;
    private AABB rangeBounds;
    private AABB rangeBoundsPlus3;
    private AABB rangeBoundsPlus11;

    public DungeonSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.DUNGEON_SPAWNER_BE.get(), pos, state);
    }

    @SuppressWarnings("unchecked")
    public void setEntityId(EntityType<?> type, RandomSource random) {
        this.bossType = (EntityType<? extends AbstractBossEntity>) type;
        this.setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DungeonSpawnerBlockEntity be) {
        be.tick((ServerLevel) level, pos);
    }

    public void tick(ServerLevel level, BlockPos pos) {
        if (this.roomCoords == null || this.bossType == null) {
            return;
        }

        if (this.range15 == null) {
            this.range15 = new AABB(pos).inflate(15);
            this.rangeBoundsPlus3 = this.getRangeBounds().inflate(3);
        }

        if (this.lastKillTime > 0 && Util.getMillis() - this.lastKillTime > RESPAWN_MILLIS) {
            this.lastKillTime = 0;
            this.isBossDefeated = false;
        }

        for (Entity e : level.getEntities(this.bossType, this.range15, Entity::isAlive)) {
            this.boss = (AbstractBossEntity) e;
            this.spawned = true;
            this.isBossDefeated = false;
            this.boss.onBossSpawned(this);
        }

        // Мобы из комнат-спаунеров в комнату босса не допускаются
        for (Monster mob : level.getEntitiesOfClass(Monster.class, this.rangeBoundsPlus3)) {
            if (mob instanceof EvolvedZombie || mob instanceof EvolvedSkeleton || mob instanceof EvolvedSpider || mob instanceof EvolvedCreeper) {
                mob.discard();
            }
        }

        List<Player> playersWithin = level.getEntitiesOfClass(Player.class, this.getRangeBounds(), p -> !p.isSpectator());
        this.playerInRange = !playersWithin.isEmpty();

        if (this.playerInRange && !this.lastPlayerInRange && !this.spawned) {
            if (this.boss == null && !this.isBossDefeated) {
                this.boss = this.bossType.create(level);
                if (this.boss != null) {
                    this.boss.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                }
            }
            if (this.boss != null) {
                this.boss.finalizeSpawn(level, level.getCurrentDifficultyAt(this.boss.blockPosition()), MobSpawnType.SPAWNER, null, null);
                level.addFreshEntity(this.boss);
                this.boss.onBossSpawned(this);
                this.spawned = true;
            }
        }

        this.lastPlayerInRange = this.playerInRange;
    }

    public void setRoom(Vec3i coords, Vec3i size) {
        this.roomCoords = coords;
        this.roomSize = size;
        this.rangeBounds = null;
        this.rangeBoundsPlus11 = null;
        this.range15 = null;
        this.setChanged();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.playerInRange = this.lastPlayerInRange = tag.getBoolean("playerInRange");
        this.isBossDefeated = tag.getBoolean("defeated");
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(tag.getString("bossType")));
        if (type != null && tag.contains("bossType")) {
            this.setEntityId(type, null);
        }
        if (tag.contains("roomCoordsX")) {
            this.roomCoords = new Vec3i(tag.getInt("roomCoordsX"), tag.getInt("roomCoordsY"), tag.getInt("roomCoordsZ"));
            this.roomSize = new Vec3i(tag.getInt("roomSizeX"), tag.getInt("roomSizeY"), tag.getInt("roomSizeZ"));
        }
        long savedTime = tag.getLong("lastKillTimeNew");
        this.lastKillTime = savedTime == 0 ? 0 : savedTime + Util.getMillis();
        if (tag.contains("chestPosNull") && !tag.getBoolean("chestPosNull")) {
            this.chestPos = new BlockPos(tag.getInt("chestX"), tag.getInt("chestY"), tag.getInt("chestZ"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("playerInRange", this.playerInRange);
        tag.putBoolean("defeated", this.isBossDefeated);
        if (this.bossType != null) {
            tag.putString("bossType", BuiltInRegistries.ENTITY_TYPE.getKey(this.bossType).toString());
        }
        if (this.roomCoords != null) {
            tag.putInt("roomCoordsX", this.roomCoords.getX());
            tag.putInt("roomCoordsY", this.roomCoords.getY());
            tag.putInt("roomCoordsZ", this.roomCoords.getZ());
            tag.putInt("roomSizeX", this.roomSize.getX());
            tag.putInt("roomSizeY", this.roomSize.getY());
            tag.putInt("roomSizeZ", this.roomSize.getZ());
        }
        tag.putLong("lastKillTimeNew", this.lastKillTime == 0 ? 0 : this.lastKillTime - Util.getMillis());
        tag.putBoolean("chestPosNull", this.chestPos == null);
        if (this.chestPos != null) {
            tag.putInt("chestX", this.chestPos.getX());
            tag.putInt("chestY", this.chestPos.getY());
            tag.putInt("chestZ", this.chestPos.getZ());
        }
    }

    /** Вызывается боссом при окончательной смерти (Legacy EntityBossBase.onDeathUpdate). */
    public void onBossKilled() {
        this.isBossDefeated = true;
        this.boss = null;
        this.spawned = false;
        this.lastKillTime = Util.getMillis();
        this.setChanged();
    }

    /** Босс ушёл без победы (игроки покинули комнату) — появится снова при следующем входе. */
    public void onBossDiscarded() {
        this.isBossDefeated = false;
        this.boss = null;
        this.spawned = false;
    }

    public BlockPos getChestPos() {
        return chestPos;
    }

    public void setChestPos(BlockPos chestPos) {
        this.chestPos = chestPos;
        this.setChanged();
    }

    public AABB getRangeBounds() {
        if (this.rangeBounds == null) {
            this.rangeBounds = new AABB(this.roomCoords.getX(), this.roomCoords.getY(), this.roomCoords.getZ(),
                    this.roomCoords.getX() + this.roomSize.getX(), this.roomCoords.getY() + this.roomSize.getY(), this.roomCoords.getZ() + this.roomSize.getZ());
        }
        return this.rangeBounds;
    }

    public AABB getRangeBoundsPlus11() {
        if (this.rangeBoundsPlus11 == null) {
            this.rangeBoundsPlus11 = this.getRangeBounds().inflate(11);
        }
        return this.rangeBoundsPlus11;
    }
}
