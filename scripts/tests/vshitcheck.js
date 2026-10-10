// Тест: снаряды Superb Warfare попадают по кораблям Valkyrien Skies (встроенная связка SBW 0.8.9+, Superb VS не нужен).
// Команды: scripts/tests/vshit_commands.txt. /vshit assemble — корабль 3×3 из железа на высоте 200;
// (верфь VS — блоки корабля на координатах за ±1 000 000) /vshit trace — луч сверху вниз через корабль трассировкой снарядов SBW и ванильной для сравнения.
ServerEvents.commandRegistry(event => {
  const { commands: Commands } = event
  const BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  event.register(Commands.literal('vshit')
    .then(Commands.literal('assemble').executes(ctx => {
      const level = ctx.source.server.overworld()
      const Assembler = Java.loadClass('org.valkyrienskies.mod.common.assembly.ShipAssembler')
      const HashSet = Java.loadClass('java.util.HashSet')
      const blocks = new HashSet()
      for (let x = 0; x < 3; x++) for (let z = 0; z < 3; z++) blocks.add(new BlockPos(x, 200, z))
      const ship = Assembler.assembleToShip(level, blocks, 1.0)
      console.info(`VSHIT assembled ship id=${ship.getId()} pos=${ship.getTransform().getPositionInWorld()}`)
      return 1
    }))
    .then(Commands.literal('trace').executes(ctx => {
      const level = ctx.source.server.overworld()
      const Vec3 = Java.loadClass('net.minecraft.world.phys.Vec3')
      const ClipContext = Java.loadClass('net.minecraft.world.level.ClipContext')
      const Hit = Java.loadClass('com.atsuishio.superbwarfare.entity.projectile.IAdvancedHitDetection')
      const clip = new ClipContext(new Vec3(1.5, 260, 1.5), new Vec3(1.5, 100, 1.5),
        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)
      // так ищет попадание ProjectileEntity: пара (блок, жидкость)
      const sbw = Hit.rayTraceBlocksWithFluid(level, clip, state => false).getFirst()
      const plain = Hit.rayTraceBlocks(level, clip, state => false)
      console.info(`VSHIT sbw-plain=${plain.getType()} at ${plain.getLocation()} state=${level.getBlockState(plain.getBlockPos()).getBlock().getId()}`)
      const vanilla = level.clip(clip)
      console.info(`VSHIT sbw=${sbw.getType()} at ${sbw.getLocation()} block=${sbw.getBlockPos()} state=${level.getBlockState(sbw.getBlockPos()).getBlock().getId()} onShip=${Math.abs(sbw.getBlockPos().getX()) > 1000000}`)
      console.info(`VSHIT vanilla=${vanilla.getType()} at ${vanilla.getLocation()} block=${vanilla.getBlockPos()} state=${level.getBlockState(vanilla.getBlockPos()).getBlock().getId()}`)
      return 1
    })))
})
