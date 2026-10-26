package dev.mcmoded.disasterward.mixin;

import dev.mcmoded.disasterward.OriGrowth;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Bob's Originium Dream, превращение заражённого в монстров и ядро (OriGrowth). Без мода миксин не применяется (@Pseudo). */
@Pseudo
@Mixin(targets = "bobsoriginiumdream.procedures.InfectorimanProcedure", remap = false)
public abstract class OriInfectManMixin {
    @Inject(method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void disasterWard$execute(LevelAccessor world, double x, double y, double z, net.minecraft.world.entity.Entity entity, net.minecraft.world.entity.Entity source, CallbackInfo ci) {
        if (OriGrowth.denyInZone(world, x, z)) ci.cancel();
    }
}
