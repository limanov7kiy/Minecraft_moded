// Замер нагрузки (поток 22): вспышка заражения Spore + SRP System на поверхности (без оберега).
// Вывод: SLOAD baseline|outbreak avg=.. p95=.. max=.. ms/tick; SLOAD infected blocks; SLOAD entities. Запуск — отдельный прогон, папка только с этим файлом.
const SL = { last: 0, base: [], load: [] }
ServerEvents.tick(event => {
  const server = event.server, T = server.tickCount
  const level = server.getLevel('minecraft:overworld')
  const BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  const now = Date.now(), dt = SL.last ? now - SL.last : 0
  SL.last = now
  const stat = (a) => { const s = a.slice().sort((p, q) => p - q); return `avg=${(a.reduce((p, q) => p + q, 0) / a.length).toFixed(1)} p95=${s[Math.floor(s.length * 0.95)]} max=${s[s.length - 1]} ms/tick (n=${a.length})` }
  const C = [-300, -300]
  const H = (x, z) => level.getHeight(Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types').MOTION_BLOCKING_NO_LEAVES, x, z)
  const infected = () => { let n = 0; for (let x = C[0] - 40; x <= C[0] + 40; x++) for (let z = C[1] - 40; z <= C[1] + 40; z++) { const h = H(x, z)
    for (let y = h - 3; y <= h + 1; y++) { const b = String(level.getBlockState(new BlockPos(x, y, z)).block.id); if (b.startsWith('spore:') || b == 'minecraft:mycelium') n++ } } return n }
  const count = () => { let sp = 0, srp = 0; level.getAllEntities().forEach(e => { if (String(e.getType()).startsWith('spore:')) sp++
    if (e.getActiveEffects && e.getActiveEffects().stream().anyMatch(i => String(i.getEffect().getDescriptionId()).includes('srpsystem.infection'))) srp++ }); return `spore=${sp} srpInfected=${srp}` }

  if (T == 300) {
    server.runCommandSilent(`forceload add ${C[0] - 48} ${C[1] - 48} ${C[0] + 48} ${C[1] + 48}`)
    for (let dx = -3; dx <= 3; dx++) for (let dz = -3; dz <= 3; dz++) level.getChunk((C[0] >> 4) + dx, (C[1] >> 4) + dz)
  }
  if (T > 330 && T <= 530 && dt) SL.base.push(dt)
  if (T == 531) {
    console.info('SLOAD baseline ' + stat(SL.base))
    SL.inf0 = infected()
    const rnd = (r) => Math.floor(Math.random() * 2 * r) - r
    const at = (id, n, r, nbt) => { for (let i = 0; i < n; i++) { const x = C[0] + rnd(r), z = C[1] + rnd(r); server.runCommandSilent(`summon ${id} ${x} ${H(x, z) + 1} ${z} ${nbt || '{PersistenceRequired:1b}'}`) } }
    at('spore:inf_human', 20, 30); at('spore:inf_villager', 10, 30); at('spore:inf_pillager', 5, 30)
    at('spore:mound', 5, 25); at('spore:gastgaber', 2, 20); at('spore:proto', 1, 5)
    at('minecraft:cow', 20, 30); at('minecraft:villager', 10, 30)
    server.runCommandSilent(`effect give @e[type=minecraft:cow,x=${C[0]},y=0,z=${C[1]},distance=..64] spore:mycelium_ef 1200 1`)
    console.info(`SLOAD outbreak spawned at ${C} ${count()} infectedBlocks=${SL.inf0}`)
  }
  if (T > 531 && T <= 1731 && dt) SL.load.push(dt)
  if (T > 531 && T % 200 == 0) console.info(`SLOAD progress T=${T} lastTick=${dt} ms ${count()}`)
  if (T == 1731) {
    console.info('SLOAD outbreak ' + stat(SL.load))
    console.info(`SLOAD infected blocks after 60 s=${infected()} (before=${SL.inf0}) ${count()}`)
    console.info('SLOAD DONE')
  }
})
