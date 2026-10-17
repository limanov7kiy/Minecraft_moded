package dev.mcmoded.compat.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Фриз сервера от Mana and Artifice 3.1.11 (Mithion): CommonEventHandler.onWorldTick каждый тик для каждого
 * игрока обходит чанки 11×11 вокруг него и гасит в них остаточную магию. Проверка Level.hasChunk пропускает
 * чанки, у которых уже есть тикет, но которые ещё генерируются (не FULL), и следующий Level.getChunk синхронно
 * догружает их на серверном потоке — у края загруженной области тик висел минутами.
 * Подменяем hasChunk на «чанк уже FULL» (ServerChunkCache.getChunkNow): недогруженные чанки пропускаются
 * в этом тике, а getChunk вызывается только для готовых и не блокирует. На клиенте — без изменений.
 * Имена SRG: hasChunk = m_7232_ (remap = false, как у других миксинов mm-compat).
 */
@Pseudo
@Mixin(targets = "com.mna.events.CommonEventHandler", remap = false)
public abstract class MnaChunkTickMixin {
    @Unique private static boolean mm_compat$logged;

    @Redirect(method = "onWorldTick", remap = false, require = 1,   // в конфиге defaultRequire 0 — пусть неудача будет видна в логе
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_7232_(II)Z", remap = false))
    private static boolean mm_compat$hasFullChunk(Level level, int x, int z) {
        if (!(level instanceof ServerLevel server)) return level.hasChunk(x, z);
        if (!mm_compat$logged) {
            mm_compat$logged = true;
            LogUtils.getLogger().info("mm_compat: MnA onWorldTick — только готовые чанки (без блокирующей загрузки)");
        }
        return server.getChunkSource().getChunkNow(x, z) != null;
    }
}
