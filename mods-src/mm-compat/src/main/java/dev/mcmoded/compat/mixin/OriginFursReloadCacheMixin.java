package dev.mcmoded.compat.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Фризы клиента раз в ~25 с (поток 33, spark-профиль игрока): Origin Furs (автор pandaismyname1, MIT; наш порт origin_visuals-1.0.5-mmport-client.jar)
 * в ClientModEvents.tick — без проверки фазы тика, т.е. дважды за тик — каждые 1000 вызовов (≈25 с) зовёт OriginalFurClient.reload, а тот
 * делает ResourceManager.listResources("furs", …): обход ВСЕХ записей всех ресурс-паков и zip-модов (ZipFile.getZipEntry/setExtra0, 4 с на каждый вызов
 * на ПК игрока, 13 % потока рендера). Список ресурсов меняется только при перезагрузке ресурсов, поэтому результат кэшируется, пока не сменились
 * объекты паков (после перезагрузки ресурсов Minecraft создаёт новые PackResources). Остальной reload (реестр слоёв Origins) дешёвый и идёт как раньше.
 * Имя SRG: ResourceManager.listResources = m_214159_ (remap = false, как у других миксинов mm-compat).
 */
@Pseudo
@Mixin(targets = "com.pandaismyname1.origin_visuals.client.OriginalFurClient", remap = false)
public abstract class OriginFursReloadCacheMixin {
    @Unique private static WeakReference<PackResources> mm_compat$packKey;
    @Unique private static Map<ResourceLocation, Resource> mm_compat$cached;
    @Unique private static boolean mm_compat$logged;

    @Redirect(method = "reload", remap = false,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ResourceManager;m_214159_(Ljava/lang/String;Ljava/util/function/Predicate;)Ljava/util/Map;", remap = false))
    private static Map<ResourceLocation, Resource> mm_compat$cachedList(ResourceManager manager, String path, Predicate<ResourceLocation> filter) {
        PackResources last = manager.listPacks().reduce((a, b) -> b).orElse(null);
        PackResources key = mm_compat$packKey == null ? null : mm_compat$packKey.get();
        if (mm_compat$cached != null && last != null && key == last) return mm_compat$cached;
        Map<ResourceLocation, Resource> result = manager.listResources(path, filter);
        mm_compat$cached = result;
        mm_compat$packKey = last == null ? null : new WeakReference<>(last);
        if (!mm_compat$logged) {
            mm_compat$logged = true;
            LogUtils.getLogger().info("mm_compat: Origin Furs reload — список ресурсов 'furs' кэшируется до перезагрузки ресурсов (без обхода всех паков каждые 25 с)");
        }
        return result;
    }
}
