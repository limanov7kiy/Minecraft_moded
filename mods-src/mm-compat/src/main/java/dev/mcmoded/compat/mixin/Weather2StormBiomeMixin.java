package dev.mcmoded.compat.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Weather2 2.8.3 (Corosus): StormObject.initFirstTime при появлении новой тучи у игрока читает биом в точке тучи
 * (Level.getBiome), а точка случайная и часто вне загруженной области — getBiome синхронно грузит/генерирует чанк на
 * серверном потоке (поток 67: треть синхронных загрузок в тике входа игроков). Нужна только температура биома.
 * Если чанк ещё не FULL, берём биом прямо из источника биомов генератора (как его посчитает генерация, без загрузки
 * чанка); отличие от getBiome — только «размытие» границы биома на несколько блоков. В готовых чанках — как раньше.
 */
@Pseudo
@Mixin(targets = "weather2.weathersystem.storm.StormObject", remap = false)
public abstract class Weather2StormBiomeMixin {
    @Redirect(method = "initFirstTime", remap = false, require = 1,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_204166_(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;", remap = false))
    private Holder<Biome> mm_compat$biomeNoLoad(Level level, BlockPos pos) {
        if (level instanceof ServerLevel server) {
            ServerChunkCache cache = server.getChunkSource();
            if (cache.getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) == null)
                return cache.getGenerator().getBiomeSource().getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()),
                    QuartPos.fromBlock(pos.getZ()), cache.randomState().sampler());
        }
        return level.getBiome(pos);
    }
}
