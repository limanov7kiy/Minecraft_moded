package dev.mcmoded.disasterward.mixin;

import dev.mcmoded.disasterward.OriGrowth;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Bob's Originium Dream, ориджиниевый мутант ломает 8 блоков вокруг (OriGrowth). Без мода миксин не применяется (@Pseudo). */
@Pseudo
@Mixin(targets = "bobsoriginiumdream.procedures.Originiutant_breakProcedure", remap = false)
public abstract class OriMutantBreakMixin {
    @Inject(method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void disasterWard$execute(LevelAccessor world, double x, double y, double z, net.minecraft.world.entity.Entity entity, CallbackInfo ci) {
        if (OriGrowth.denyInZone(world, x, z)) ci.cancel();
    }
}
