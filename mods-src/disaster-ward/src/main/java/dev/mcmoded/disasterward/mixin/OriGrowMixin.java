package dev.mcmoded.disasterward.mixin;

import dev.mcmoded.disasterward.OriGrowth;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Bob's Originium Dream, рост oriblockb (OriGrowth). Без мода миксин не применяется (@Pseudo). */
@Pseudo
@Mixin(targets = "bobsoriginiumdream.procedures.OrigrowProcedure", remap = false)
public abstract class OriGrowMixin {
    @Inject(method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDD)V", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void disasterWard$execute(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (OriGrowth.denyGrow(world, x, z)) ci.cancel();
    }
}
