package dev.mcmoded.disasterward.mixin;

import dev.mcmoded.disasterward.WardGuard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Отмечаем, какое существо сейчас тикает (tickNonPassenger = m_8647_, tickPassenger = m_8662_). */
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "m_8647_(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), remap = false)
    private void disasterWard$enter(Entity e, CallbackInfo ci) { WardGuard.enter((ServerLevel) (Object) this, e); }

    @Inject(method = "m_8647_(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"), remap = false)
    private void disasterWard$exit(Entity e, CallbackInfo ci) { WardGuard.exit(); }

    @Inject(method = "m_8662_(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), remap = false)
    private void disasterWard$enterPassenger(Entity vehicle, Entity passenger, CallbackInfo ci) { WardGuard.enter((ServerLevel) (Object) this, passenger); }

    @Inject(method = "m_8662_(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"), remap = false)
    private void disasterWard$exitPassenger(Entity vehicle, Entity passenger, CallbackInfo ci) { WardGuard.exit(); }
}
