// Тест: генерируются ли руды/камень Northstar и Creating Space на планетах Ad Astra (space_unify + ore_crossgen)
ServerEvents.loaded(event => {
  const server = event.server
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  for (let planet of ['moon', 'mars', 'venus', 'mercury']) {
    let level = server.getLevel('ad_astra:' + planet)
    if (level == null) { console.error(`SPACECHECK ${planet}: no level`); continue }
    let counts = {}
    for (let cx = 0; cx < 2; cx++) for (let cz = 0; cz < 2; cz++) {
      let chunk = level.getChunk(cx, cz)
      for (let x = 0; x < 16; x++) for (let z = 0; z < 16; z++) for (let y = -64; y < 160; y++) {
        let id = String(chunk.getBlockState(new BlockPos(cx * 16 + x, y, cz * 16 + z)).getBlock().getDescriptionId())
        if (id.indexOf('northstar') >= 0 || id.indexOf('creatingspace') >= 0) counts[id] = (counts[id] || 0) + 1
      }
    }
    let line = Object.keys(counts).sort().map(k => `${k.replace('block.', '')}=${counts[k]}`).join(' ')
    console.info(`SPACECHECK ${planet}: ${line}`)
  }
})
