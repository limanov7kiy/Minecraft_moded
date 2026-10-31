package dev.mcmoded.compat.mixin;

import com.google.common.reflect.Reflection;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The Aether 1.20.1-1.5.2 (The Aether Team), поток 76: Aether.commonSetup в параллельной фазе загрузки модов вызывает
 * Reflection.initialize(...) для AetherPlacementModifiers (статическая регистрация в ванильный реестр PLACEMENT_MODIFIER_TYPE),
 * AetherRecipeBookTypes, AetherBlockPathTypes, AetherMobCategory (расширение enum) и др. Ванильные реестры и enum не потокобезопасны:
 * при одновременной записи другого мода — ConcurrentModificationException в MappedRegistry, «Mod loading error», сервер не стартует
 * (плавающее). Здесь эти инициализации переносятся в event.enqueueWork — на главный поток после параллельной фазы, в том же порядке.
 */
@Pseudo
@Mixin(targets = "com.aetherteam.aether.Aether", remap = false)
public abstract class AetherSetupRaceMixin {
    @Redirect(method = "commonSetup", remap = false, require = 1,
        at = @At(value = "INVOKE", target = "Lcom/google/common/reflect/Reflection;initialize([Ljava/lang/Class;)V", remap = false))
    private void mm_compat$initializeOnMainThread(Class<?>[] classes, FMLCommonSetupEvent event) {
        event.enqueueWork(() -> Reflection.initialize(classes));
    }
}
