package dev.mcmoded.compat.mixin;

import com.mojang.logging.LogUtils;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Create: Northstar — Structures 1.0.0: обработчик onEntityJoinLevel удаляет (discard + cancel) каждое не-игровое живое существо,
 * чьи координаты попадают в «защищённый кратер» (ProtectedCraterAreas.isInsideProtectedCrater(seed, chunkPos)). Кратеры считаются
 * только по зерну мира и чанку — измерение не проверяется, поэтому в Обычном мире (Overworld) и во всех прочих измерениях вокруг
 * «кратеров» (круги радиусом 150–250 блоков с шагом 1024, плюс бассейн Эйткена радиусом 2400) не появляется никто: ни /summon,
 * ни яйцо (слышен звук, моба нет), ни естественный спавн. Кратеры есть только на Луне — обработчик пропускаем во всех
 * измерениях, кроме луны (путь измерения «moon»: northstar:moon и ad_astra:moon). Найдено потоком 30 (docs/compat.md «Northstar Structures»).
 */
@Pseudo
@Mixin(targets = "com.northstar_structures.NorthstarStructures", remap = false)
public abstract class NorthstarCraterJoinMixin {
    @Unique private static boolean mm_compat$logged;

    @Inject(method = "onEntityJoinLevel", at = @At("HEAD"), cancellable = true, remap = false, require = 1)   // defaultRequire 0 в конфиге — неудача будет видна в логе
    private void mm_compat$onlyOnMoon(EntityJoinLevelEvent event, CallbackInfo ci) {
        if (event.getLevel().dimension().location().getPath().equals("moon")) return;
        if (!mm_compat$logged) {
            mm_compat$logged = true;
            LogUtils.getLogger().info("mm_compat: Northstar Structures onEntityJoinLevel — только на Луне (в остальных измерениях удалял существ в «кратерах»)");
        }
        ci.cancel();
    }
}
