// Тест: The Lost Cities генерирует города в обычном Overworld по профилю из defaultconfigs (docs/lost-cities.md).
// Считает долю «городских» чанков в сетке ±1500 блоков (шаг 8 чанков) (по API Lost Cities), генерирует до 12 таких чанков
// (level.getChunk — полная генерация) и замеряет время; в городском чанке считает характерные блоки зданий.
ServerEvents.loaded(event => {
  const LC = Java.loadClass('mcjty.lostcities.LostCities')
  const level = event.server.overworld()
  const info = LC.lostCitiesImp.getLostInfo(level)
  let total = 0, city = 0, hits = []
  let tg = Date.now()
  for (let cx = -96; cx <= 96; cx += 8) for (let cz = -96; cz <= 96; cz += 8) {
    total++
    if (info.getChunkInfo(cx, cz).isCity()) { city++; if (hits.length < 12) hits.push([cx, cz]) }
  }
  console.info(`LCCHECK profile-grid: ${city}/${total} city chunks (${(100 * city / total).toFixed(1)}%), ${Date.now() - tg} ms for ${total} samples`)
  let t0 = Date.now(), blocks = 0, buildings = {}
  for (let h of hits) {
    let chunk = level.getChunk(h[0], h[1])
    let b = info.getChunkInfo(h[0], h[1]).getBuildingType()
    buildings[b] = (buildings[b] || 0) + 1
    for (let x = 0; x < 16; x += 2) for (let z = 0; z < 16; z += 2) for (let y = level.minBuildHeight; y < 130; y += 3) {
      let id = String(level.getBlock(h[0] * 16 + x, y, h[1] * 16 + z).id)
      if (/glass|iron_bars|concrete|quartz|bricks|stone_brick|planks|asphalt|lostcities/.test(id)) blocks++
    }
  }
  let dt = Date.now() - t0
  console.info(`LCCHECK generated ${hits.length} city chunks in ${dt} ms (${hits.length ? Math.round(dt / hits.length) : 0} ms/chunk), building blocks sampled: ${blocks}`)
  console.info(`LCCHECK buildings: ${JSON.stringify(buildings)}`)
  console.info(`LCCHECK ${city > 0 && blocks > 100 ? 'OK' : 'FAIL'}`)
})
