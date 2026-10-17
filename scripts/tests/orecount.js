// Тест: сколько руд Chaos Persists и ванильных руд-добавок генерируется на новых чанках Overworld.
// Берёт квадрат 7x7 чанков вокруг (cx0, cz0) — для нового прогона поменять числа на ещё не сгенерированные
// (по умолчанию 2500, 2500 — далеко от спавна), считает блоки по id. Вывод в logs/kubejs/server.log: ORECOUNT <id> <число> и ORECOUNT_TOTAL.
ServerEvents.tick(event => {
  const server = event.server
  const cx0 = 2500, cz0 = 2500   // чанковые координаты центра квадрата
  if (server.tickCount == 100) {
    console.info("ORECOUNT start")
    server.runCommandSilent(`forceload add ${(cx0 - 3) * 16} ${(cz0 - 3) * 16} ${(cx0 + 3) * 16 + 15} ${(cz0 + 3) * 16 + 15}`)
  }
  if (server.tickCount != 1200) return
  const level = server.getLevel('minecraft:overworld')
  const BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  const counts = {}
  let chunks = 0
  const want = /chaospersists:.*(ore|spawn|egg|salt|ruby|uranium|titanium|amethyst)|^minecraft:(deepslate_)?(diamond|emerald|gold)_ore$|^minecraft:(diamond|emerald|gold)_block$/
  for (let cx = cx0 - 3; cx <= cx0 + 3; cx++) for (let cz = cz0 - 3; cz <= cz0 + 3; cz++) {
    if (level.getChunkSource().getChunkNow(cx, cz) == null) continue
    chunks++
    for (let x = 0; x < 16; x++) for (let z = 0; z < 16; z++) for (let y = -64; y < 130; y++) {
      let id = String(level.getBlockState(new BlockPos(cx * 16 + x, y, cz * 16 + z)).block.id)
      if (want.test(id)) counts[id] = (counts[id] || 0) + 1
    }
  }
  Object.keys(counts).sort().forEach(k => console.info(`ORECOUNT ${k} ${counts[k]}`))
  console.info(`ORECOUNT_TOTAL chunks=${chunks}`)
})
