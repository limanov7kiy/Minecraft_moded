package dev.mcmoded.compat.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Фриз сервера от Serene Seasons Plus 5.1.4 (Gabou): ActiveSnowUpdateService каждый тик для каждого игрока берёт
 * случайные столбцы в радиусе до 64 блоков (таяние — sampleLocalMeltTarget, накопление — findPlacementTop) и читает
 * высоту/блоки через ServerLevel. Если чанк ещё генерируется (игрок только вошёл, телепорт, портал, полёт), такое
 * чтение синхронно догружает его на серверном потоке: на тестовом сервере (поток 67) тик при появлении 3 игроков
 * в новом месте длился 54 с, и 91 % ожидания getChunkBlocking приходился на этот сервис.
 * Подменяем чтение высоты/поиск верха столбца: если чанк ещё не FULL (ServerChunkCache.getChunkNow == null),
 * столбец пропускается в этом тике (как будто случайная точка не подошла — так мод и делает с неподходящими точками).
 * На готовых чанках поведение не меняется. Имена SRG: getHeight = m_6924_ (remap = false, как у других миксинов mm-compat).
 */
@Pseudo
@Mixin(targets = "com.Gabou.sereneseasonsplus.features.ActiveSnowUpdateService", remap = false)
public abstract class SspSnowChunkMixin {
    @Unique private static boolean mm_compat$logged;

    @Unique
    private static boolean mm_compat$ready(ServerLevel level, int x, int z) {
        if (!mm_compat$logged) {
            mm_compat$logged = true;
            LogUtils.getLogger().info("mm_compat: Serene Seasons Plus — снег/таяние только в готовых чанках (без блокирующей загрузки)");
        }
        return level.getChunkSource().getChunkNow(x >> 4, z >> 4) != null;
    }

    // таяние: высота выше мира — цикл по y ниже сразу отбрасывает точку, getBlockState не вызывается
    @Redirect(method = "sampleLocalMeltTarget", remap = false, require = 1,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;m_6924_(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I", remap = false))
    private int mm_compat$meltHeight(ServerLevel level, Heightmap.Types type, int x, int z) {
        if (!mm_compat$ready(level, x, z)) return level.getMaxBuildHeight() + 8;
        return level.getHeight(type, x, z);
    }

    // накопление: null — мод сам пропускает столбец
    @Redirect(method = "runLocalAccumulation", remap = false, require = 1,
        at = @At(value = "INVOKE", target = "Lcom/Gabou/sereneseasonsplus/features/CommonSnowBlockFeature;findPlacementTop(Lnet/minecraft/server/level/ServerLevel;II)Lnet/minecraft/core/BlockPos;", remap = false))
    private BlockPos mm_compat$placementTop(ServerLevel level, int x, int z) {
        if (!mm_compat$ready(level, x, z)) return null;
        return SspSnowChunkMixin.mm_compat$callFindPlacementTop(level, x, z);
    }

    // CommonSnowBlockFeature.findPlacementTop — protected static в чужом пакете: вызов через рефлексию, метод ищется один раз
    @Unique private static java.lang.reflect.Method mm_compat$find;

    @Unique
    private static BlockPos mm_compat$callFindPlacementTop(ServerLevel level, int x, int z) {
        try {
            if (mm_compat$find == null) {
                java.lang.reflect.Method m = Class.forName("com.Gabou.sereneseasonsplus.features.CommonSnowBlockFeature")
                    .getDeclaredMethod("findPlacementTop", ServerLevel.class, int.class, int.class);
                m.setAccessible(true);
                mm_compat$find = m;
            }
            return (BlockPos) mm_compat$find.invoke(null, level, x, z);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
