package dev.mcmoded.undeadrising.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.util.List;

/// Помощники появления мобов (оригинал: Fish's Undead Rising (Fish), `core/SpawnUtil`).
public final class FurSpawnUtil {
    private FurSpawnUtil() {}

    /// Высота земли под сущностью; в измерениях с потолком (Незер) — первый пол под потолком.
    public static BlockPos getHeight(Entity entity) {
        return getHeight(entity.level(), entity.blockPosition());
    }

    public static BlockPos getHeight(Level level, BlockPos at) {
        BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, at);
        if (level.dimensionType().hasCeiling()) {
            int min = level.getMinBuildHeight();
            do {
                pos = pos.below();
            } while (!level.getBlockState(pos).isAir() && pos.getY() > min);
            do {
                pos = pos.below();
            } while (level.getBlockState(pos).isAir() && pos.getY() > min);
        }
        return pos;
    }

    /// Найдена ли земля (в оригинале — проверка `getY() > 0`).
    public static boolean hasGround(Level level, BlockPos ground) {
        return ground.getY() > level.getMinBuildHeight();
    }

    @Nullable
    public static Entity gotRiderEntity(List<Entity> passengers, EntityType<?> type) {
        for (Entity e : passengers) {
            if (e.getType() == type) return e;
        }
        return null;
    }

    /// До 10 попыток поставить моба рядом с точкой (как призыв подкрепления у зомби); null — не вышло.
    @Nullable
    public static <T extends Mob> T trySpawnEntity(EntityType<T> type, ServerLevel level, BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            double dx = i == 0 ? 0.0 : level.random.nextInt(4) - 2;
            double dz = i == 0 ? 0.0 : level.random.nextInt(4) - 2;
            BlockPos at = findSpawnPositionInColumn(level, pos, dx, dz);
            if (at != null) {
                T mob = type.create(level, null, null, at, MobSpawnType.MOB_SUMMONED, false, false);
                if (mob != null) {
                    if (mob.checkSpawnRules(level, MobSpawnType.MOB_SUMMONED) && mob.checkSpawnObstruction(level)) {
                        level.addFreshEntityWithPassengers(mob);
                        return mob;
                    }
                    mob.discard();
                }
            }
        }
        return null;
    }

    @Nullable
    private static BlockPos findSpawnPositionInColumn(ServerLevel level, BlockPos origin, double dx, double dz) {
        BlockPos pos = BlockPos.containing(origin.getX() + dx, origin.getY() + 6.0, origin.getZ() + dz);
        BlockState state = level.getBlockState(pos);
        for (int j = 6; j >= -6; j--) {
            BlockPos above = pos;
            BlockState aboveState = state;
            pos = pos.below();
            state = level.getBlockState(pos);
            if ((aboveState.isAir() || aboveState.liquid()) && state.blocksMotion()) {
                return above.above();
            }
        }
        return null;
    }
}
