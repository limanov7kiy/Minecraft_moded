package dev.mcmoded.compat.mixin;

import mcjty.lostcities.worldgen.lost.BuildingInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Утечка кучи в The Lost Cities 1.20-7.5.7 (McJty, MIT; поток 34, OOM у игрока при полёте над новыми чанками).
 * BuildingInfo.getXmin/getXmax/getZmin/getZmax запоминают соседа в полях xmin/xmax/zmin/zmax. Все BuildingInfo
 * сгенерированных чанков сцепляются в одну сетку, и любой ещё живой (недавний чанк, кэш ChunkPlanner) удерживает
 * её целиком — TTL-кэш ChunkPlanner (cacheCleanupSeconds) ничего не освобождает. Замер: за ~10 минут полёта
 * 47 тыс. → 705 тыс. BuildingInfo (~3 КБ каждый) и полная куча 6 ГБ.
 * Не запоминаем соседа: геттер каждый раз берёт его через getBuildingInfo (тот же TimedCache, дёшево),
 * а вытесненные из кэша записи освобождаются. Названия — собственные имена Lost Cities (remap = false).
 */
@Pseudo
@Mixin(value = BuildingInfo.class, remap = false)
public abstract class LostCitiesNeighborMixin {
    @Redirect(method = "getXmin", remap = false, require = 1, at = @At(value = "FIELD", opcode = org.objectweb.asm.Opcodes.PUTFIELD,
        target = "Lmcjty/lostcities/worldgen/lost/BuildingInfo;xmin:Lmcjty/lostcities/worldgen/lost/BuildingInfo;", remap = false))
    private void mm_compat$noXmin(BuildingInfo self, BuildingInfo neighbour) {}

    @Redirect(method = "getXmax", remap = false, require = 1, at = @At(value = "FIELD", opcode = org.objectweb.asm.Opcodes.PUTFIELD,
        target = "Lmcjty/lostcities/worldgen/lost/BuildingInfo;xmax:Lmcjty/lostcities/worldgen/lost/BuildingInfo;", remap = false))
    private void mm_compat$noXmax(BuildingInfo self, BuildingInfo neighbour) {}

    @Redirect(method = "getZmin", remap = false, require = 1, at = @At(value = "FIELD", opcode = org.objectweb.asm.Opcodes.PUTFIELD,
        target = "Lmcjty/lostcities/worldgen/lost/BuildingInfo;zmin:Lmcjty/lostcities/worldgen/lost/BuildingInfo;", remap = false))
    private void mm_compat$noZmin(BuildingInfo self, BuildingInfo neighbour) {}

    @Redirect(method = "getZmax", remap = false, require = 1, at = @At(value = "FIELD", opcode = org.objectweb.asm.Opcodes.PUTFIELD,
        target = "Lmcjty/lostcities/worldgen/lost/BuildingInfo;zmax:Lmcjty/lostcities/worldgen/lost/BuildingInfo;", remap = false))
    private void mm_compat$noZmax(BuildingInfo self, BuildingInfo neighbour) {}
}
