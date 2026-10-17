package dev.mcmoded.disasterward.mixin;

import dev.mcmoded.disasterward.WardGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Бедствие/паразит во время своего тика не ставит и не ломает блоки в зоне оберега (имена SRG: в собранной игре Forge 1.20.1 методы называются так). */
@Mixin(Level.class)
public abstract class LevelMixin {
    @Inject(method = "m_6933_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void disasterWard$setBlock(BlockPos pos, BlockState state, int flags, int depth, CallbackInfoReturnable<Boolean> cir) {
        if (WardGuard.deny((Level) (Object) this, pos)) cir.setReturnValue(false);
    }

    @Inject(method = "m_7740_(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;I)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void disasterWard$destroyBlock(BlockPos pos, boolean drop, Entity breaker, int depth, CallbackInfoReturnable<Boolean> cir) {
        if (WardGuard.deny((Level) (Object) this, pos)) cir.setReturnValue(false);
    }
}
