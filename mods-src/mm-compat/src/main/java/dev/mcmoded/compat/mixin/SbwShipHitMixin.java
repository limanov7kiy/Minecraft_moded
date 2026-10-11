package dev.mcmoded.compat.mixin;

import com.atsuishio.superbwarfare.compat.valkyrienskies.ValkyrienSkiesCompat;
import kotlin.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

/**
 * Superb Warfare 0.8.9.x сам трассирует снаряды по кораблям Valkyrien Skies, но в попадании по кораблю
 * ставит не блок корабля (он живёт на верфи VS), а точку вылета или мировые координаты попадания, где
 * воздух: снаряд останавливается где надо, а эффекты по блоку (стекло, частицы, урон блоку) уходят в воздух.
 * Подменяем блок на блок корабля из ванильной трассировки (VS дополняет её кораблями).
 */
@Pseudo
@Mixin(targets = "com.atsuishio.superbwarfare.entity.projectile.IAdvancedHitDetection$Companion", remap = false)
public abstract class SbwShipHitMixin {
    @Inject(method = "rayTraceBlocks", at = @At("RETURN"), cancellable = true, remap = false)
    private void mm_compat$shipBlock(Level level, ClipContext clip, Predicate<BlockState> ignore,
                                     CallbackInfoReturnable<BlockHitResult> cir) {
        cir.setReturnValue(mm_compat$fix(level, clip, ignore, cir.getReturnValue()));
    }

    @Inject(method = "rayTraceBlocksWithFluid", at = @At("RETURN"), cancellable = true, remap = false)
    private void mm_compat$shipBlockWithFluid(Level level, ClipContext clip, Predicate<BlockState> ignore,
                                              CallbackInfoReturnable<Pair<BlockHitResult, BlockHitResult>> cir) {
        Pair<BlockHitResult, BlockHitResult> r = cir.getReturnValue();
        BlockHitResult block = mm_compat$fix(level, clip, ignore, r.getFirst());
        if (block != r.getFirst()) cir.setReturnValue(new Pair<>(block, r.getSecond()));
    }

    private static BlockHitResult mm_compat$fix(Level level, ClipContext clip, Predicate<BlockState> ignore, BlockHitResult hit) {
        if (hit == null || hit.getType() != HitResult.Type.BLOCK || !ValkyrienSkiesCompat.hasMod()) return hit;
        if (!level.getBlockState(hit.getBlockPos()).isAir()) return hit;                 // обычное попадание
        // ванильная трассировка с миксином VS возвращает блок корабля на верфи
        BlockHitResult ship = level.clip(new ClipContext(clip.getFrom(), clip.getTo(),
            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        if (ship.getType() != HitResult.Type.BLOCK || ship.getLocation().distanceToSqr(hit.getLocation()) > 1.0E-4) return hit;
        BlockPos pos = ship.getBlockPos().immutable();
        if (ignore.test(level.getBlockState(pos))) return hit;
        return new BlockHitResult(hit.getLocation(), ship.getDirection(), pos, hit.isInside());
    }
}
