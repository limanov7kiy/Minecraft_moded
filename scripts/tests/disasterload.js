// Замер нагрузки (поток 15): торнадо Weather2 (F2) + метеоритный дождь, землетрясение и пожар Natural Disasters! над застройкой Lost Cities.
// Вывод: DWLOAD baseline|storm avg=.. max=.. ms/tick; DWLOAD blocks changed; DWLOAD entities. Идёт после disasterward.js (тик ≥ 300).
// Режим замера: 'weather2' — только торнадо Weather2, 'nd' — только бедствия Natural Disasters!, 'both' — всё сразу (меняется sed'ом при запуске)
const DL_MODE = 'both'
const DL = { last: 0, base: [], load: [], center: null }
ServerEvents.tick(event => {
  const server = event.server, T = server.tickCount
  const level = server.getLevel('minecraft:overworld')
  const BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  const now = Date.now()
  const dt = DL.last ? now - DL.last : 0
  DL.last = now
  const sample = () => { let n = 0; const c = DL.center; for (let x = c[0] - 32; x < c[0] + 32; x += 2) for (let z = c[1] - 32; z < c[1] + 32; z += 2) for (let y = 60; y < 120; y += 2)
    if (!level.getBlockState(new BlockPos(x, y, z)).isAir()) n++; return n }
  const countPlanks = () => { let n = 0; const a = DL.planksAt; for (let x = a[0]; x <= a[3]; x++) for (let z = a[2]; z <= a[5]; z++) for (let y = a[1]; y <= a[4]; y++) if (String(level.getBlockState(new BlockPos(x, y, z)).block.id) == 'minecraft:oak_planks') n++; return n }
  const stat = (a) => { const s = a.slice().sort((p, q) => p - q); return `avg=${(a.reduce((p, q) => p + q, 0) / a.length).toFixed(1)} p95=${s[Math.floor(s.length * 0.95)]} max=${s[s.length - 1]} ms/tick (n=${a.length})` }

  if (T == 300) {
    let center = null
    try {
      var lcInfo = Java.loadClass('mcjty.lostcities.LostCities').lostCitiesImp.getLostInfo(level)
      for (let cx = -96; cx <= 96 && !center; cx += 4) for (let cz = -96; cz <= 96 && !center; cz += 4) if (lcInfo.getChunkInfo(cx, cz).isCity()) center = [cx * 16 + 8, cz * 16 + 8]
    } catch (e) { console.info('DWLOAD lostcities API: ' + e) }
    if (!center) { console.info('DWLOAD FAIL нет городского чанка (Lost Cities)'); return }
    DL.center = center
    for (let dx = -2; dx <= 2; dx++) for (let dz = -2; dz <= 2; dz++) level.getChunk((center[0] >> 4) + dx, (center[1] >> 4) + dz)
    server.runCommandSilent(`forceload add ${center[0] - 32} ${center[1] - 32} ${center[0] + 32} ${center[1] + 32}`)
    DL.before = sample()
    console.info(`DWLOAD city center=${center} non-air blocks (sample) before=${DL.before}`)
  }
  if (!DL.center) return
  if (T > 330 && T <= 530 && dt) DL.base.push(dt)
  if (T == 531) {
    console.info('DWLOAD baseline ' + stat(DL.base))
    var c = DL.center
    var Hh = level.getHeight(Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types').MOTION_BLOCKING_NO_LEAVES, c[0], c[1])
    // деревянная «постройка» 13x13x8 прямо под торнадо (проверка, что рвёт; считаем доски до и после)
    DL.planksAt = [c[0] - 6, Hh, c[1] - 6, c[0] + 6, Hh + 7, c[1] + 6]
    server.runCommandSilent(`fill ${c[0] - 6} ${Hh} ${c[1] - 6} ${c[0] + 6} ${Hh + 7} ${c[1] + 6} minecraft:oak_planks`)
    DL.planksBefore = countPlanks()
    console.info('DWLOAD planks before=' + DL.planksBefore)
    var H = level.getHeight(Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types').MOTION_BLOCKING_NO_LEAVES, c[0], c[1])
    // торнадо Weather2 (как команда /weather2 summon), ставим с запада города, он идёт по ветру
    if (DL_MODE != 'nd') {
      var w2Tick = Java.loadClass('weather2.ServerTickHandler')
      var w2Storm = Java.loadClass('weather2.weathersystem.storm.StormObject')
      var w2Vec = Java.loadClass('net.minecraft.world.phys.Vec3')
      var w2mgr = w2Tick.getWeatherManagerFor(level)
      var w2anchor = level.createEntity('minecraft:cow'); w2anchor.setPosition(c[0], H, c[1])
      var w2so = new w2Storm(w2mgr); w2so.setupStorm(w2anchor)
      w2so.levelCurIntensityStage = 6; w2so.maxIntensityStage = 6
      w2so.initPositions(new w2Vec(c[0] + 0.5, H, c[1] + 0.5))
      w2mgr.addStormObject(w2so); w2mgr.syncStormNew(w2so)
      DL.storm = w2so
    }
    if (DL_MODE != 'weather2') {
      server.runCommandSilent(`summon natural_disasters:spawnmeteorshower ${c[0]} ${H} ${c[1]}`)
      server.runCommandSilent(`summon natural_disasters:spawnaearthquake ${c[0] + 20} ${H} ${c[1]}`)
      server.runCommandSilent(`summon natural_disasters:wildfire ${c[0] - 20} ${H} ${c[1]}`)
    }
    console.info(`DWLOAD storm spawned at ${c} y=${H} mode=${DL_MODE}`)
  }
  if (T > 531 && T <= 1131 && dt) DL.load.push(dt)
  if (T > 531 && T % 100 == 0) console.info('DWLOAD progress T=' + T + ' lastTick=' + dt + ' ms')
  if (T == 831 && DL.storm && DL_MODE != 'nd') { var th0 = DL.storm.tornadoHelper; console.info(`DWLOAD t+300 stage=${DL.storm.levelCurIntensityStage} tornado=${DL.storm.isTornadoFormingOrGreater()} tryRip=${th0.tryRipCount} rip=${th0.ripCount}`) }
  if (T == 1131) {
    console.info('DWLOAD storm ' + stat(DL.load))
    console.info('DWLOAD planks after=' + countPlanks() + ' (before=' + DL.planksBefore + ')')
    console.info(`DWLOAD blocks (sample) after=${sample()} (before=${DL.before})`)
    let w2 = 0, nd = 0
    level.getAllEntities().forEach(e => { var tt = String(e.getType()); if (tt.includes('weather2')) w2++; if (tt.includes('natural_disasters')) nd++ })
    if (DL.storm) {
      var th = DL.storm.tornadoHelper
      console.info(`DWLOAD storm stage=${DL.storm.levelCurIntensityStage}/${DL.storm.maxIntensityStage} type=${DL.storm.stormType} tornado=${DL.storm.isTornadoFormingOrGreater()} dead=${DL.storm.isDead} pos=${DL.storm.pos}`)
      console.info(`DWLOAD tornadoHelper tryRip=${th.tryRipCount} rip=${th.ripCount} grabTick=${th.tickGrabCount} blockedCached=${th.isBlockGrabbingBlockedCached}`)
    }
    console.info('DWLOAD DONE')
  }
})
