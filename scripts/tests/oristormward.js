// Тест (поток 32): ориджиниумная буря Bob's Originium Dream и оберег (mods-src/disaster-ward 1.2, OriStorm.java), правила — oristorm_rules.js.
// Вывод: OSW <проверка> OK|FAIL <подробности>; OSW tick … — средний тик сервера (мс) до бури и во время неё.
// Запуск: EXTRA_SERVER_SCRIPTS=<папка только с этим файлом>, TEST_COMMANDS с «#sleep 1080» (буря 3000 тиков, рост до T=7650). См. docs/compat.md «Ориджиниумная буря».
const OSW = { t0: -1, ticks: [], stormTicks: [], after: [] }
// Рост ориджиниума (поток 32, alloworigrow=true, шансы [oriGrowth]): ряд семян oriblockb у края зоны снаружи (x=115; зона+полоса = 112),
// поле из 100 семян вдали (скорость), ядра в зоне и снаружи. redstoneaction=true — рост без игрока рядом. Итог — на T = GROW_END.
const GROW_END = 7650, ORI_MOBS = ['originiutant', 'oripuppt', 'translationbase', 'mutant_giant_rock_spider']
ServerEvents.tick(event => {
  const server = event.server, T = server.tickCount
  const level = server.getLevel('minecraft:overworld')
  const Storm = () => Java.loadClass('dev.mcmoded.disasterward.OriStorm')
  const BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  const Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  const AABB = Java.loadClass('net.minecraft.world.phys.AABB')
  const Heightmap = Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types')
  const Vars = () => Java.loadClass('bobsoriginiumdream.network.BobsoriginiumdreamModVariables$WorldVariables').get(level)
  const ok = (name, cond, info) => console.info(`OSW ${name} ${cond ? 'OK' : 'FAIL'} ${info || ''}`)
  const top = (x, z) => level.getHeight(Heightmap.MOTION_BLOCKING_NO_LEAVES, x, z)
  const ents = (x, z, r, f) => { var out = []; level.getEntitiesOfClass(Entity, new AABB(x - r, -64, z - r, x + r, 320, z + r)).forEach(e => { if (f(String(e.getType().toString()), e)) out.push(e) }); return out }
  // блоки Bob's Originium Dream (шип бури) в квадрате r вокруг x,z от поверхности вверх на 70
  const oriBlocks = (x, z, r, yBase) => {
    var n = 0, y0 = (yBase === undefined ? top(x, z) : yBase) - 10
    for (var dx = -r; dx <= r; dx++) for (var dz = -r; dz <= r; dz++) for (var y = y0; y < y0 + 70; y++) {
      if (String(level.getBlockState(new BlockPos(x + dx, y, z + dz)).block.id).indexOf('bobsoriginiumdream:') == 0) n++
    }
    return n
  }
  const stormOn = () => { try { return Vars().oristorm } catch (e) { return false } }

  if (T % 100 == 0 && T >= 200) {
    var mspt = server.getAverageTickTime()
    if (stormOn()) OSW.stormTicks.push(mspt); else if (OSW.t0 < 0 && T >= 600) OSW.ticks.push(mspt); else if (OSW.done && T > OSW.doneT + 100) OSW.after.push(mspt)
  }
  if (T == 100) {
    server.runCommandSilent('forceload add -128 -128 127 127')
    server.runCommandSilent('forceload add 384 384 447 447')
    server.runCommandSilent('forceload add 128 -48 143 47')
    ok('rule-oristorm', server.runCommandSilent('gamerule oristorm') == 1, 'oristorm=' + server.runCommandSilent('gamerule oristorm'))
    ok('rule-frequency', server.runCommandSilent('gamerule oristormfrequency') == 100000, '' + server.runCommandSilent('gamerule oristormfrequency'))
    ok('rule-summon', server.runCommandSilent('gamerule oristormsummon') == 200, '' + server.runCommandSilent('gamerule oristormsummon'))
    ok('rule-origrow', server.runCommandSilent('gamerule alloworigrow') == 1, '' + server.runCommandSilent('gamerule alloworigrow'))
    ok('rule-oribreak', server.runCommandSilent('gamerule oribreak') == 0, '' + server.runCommandSilent('gamerule oribreak'))
  }
  if (T == 200) {
    server.runCommandSilent('gamerule redstoneaction true')
    server.runCommandSilent('fill 100 149 -40 135 151 40 minecraft:stone')
    for (var z = -38; z <= 38; z += 2) server.runCommandSilent(`setblock 115 150 ${z} bobsoriginiumdream:oriblockb`)
    server.runCommandSilent('fill 390 149 390 440 151 440 minecraft:stone')
    for (var gx = 392; gx <= 437; gx += 5) for (var gz = 392; gz <= 437; gz += 5) server.runCommandSilent(`setblock ${gx} 150 ${gz} bobsoriginiumdream:oriblockb`)
    for (var i = 0; i < 20; i++) server.runCommandSilent(`setblock ${392 + i * 2} 152 445 bobsoriginiumdream:oriblock_core`)
    for (var j = 0; j < 10; j++) server.runCommandSilent(`setblock ${10 + j * 2} 150 -20 bobsoriginiumdream:oriblock_core`)
  }
  if (T == 160) {
    server.runCommandSilent('setblock 0 200 0 disaster_ward:ward')
    // блок бури в зоне и снаружи: через 100 тиков он ставит шип 50×53×57 (Place_spikeProcedure)
    server.runCommandSilent(`setblock 30 ${top(30, 30)} 30 bobsoriginiumdream:oristormblock`)
    OSW.spikeY = top(416, 416)
    server.runCommandSilent(`setblock 416 ${OSW.spikeY} 416 bobsoriginiumdream:oristormblock`)
  }
  if (T == 1400) {
    ok('spike-inside-blocked', oriBlocks(30, 30, 40) == 0, 'блоков Bob\'s в зоне у (30,30)=' + oriBlocks(30, 30, 40))
    ok('spike-outside-placed', oriBlocks(416, 416, 30, OSW.spikeY) > 100, 'блоков Bob\'s снаружи у (416,416)=' + oriBlocks(416, 416, 30, OSW.spikeY))
    // запуск бури у фейкового игрока в центре зоны (центр бури — ±100 от игрока, призывы — ±300 от центра)
    server.runCommandSilent('gamerule oriwait 100')
    var fp = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory').getMinecraft(level)
    fp.setPos(0.5, 100, 0.5)
    ok('storm-start', Storm().start(level, fp), 'OriStorm.start')
    OSW.t0 = T
  }
  if (OSW.t0 > 0 && T == OSW.t0 + 400) {
    ok('storm-active', stormOn(), 'oristorm=' + stormOn())
    // червь, заведённый в зону снаружи, и червь, призванный командой в зоне, не исчезают
    server.runCommandSilent(`summon bobsoriginiumdream:evildustoriworm 120 ${top(120, 0)} 0 {Tags:["osw_walk"],PersistenceRequired:1b}`)
    server.runCommandSilent(`summon bobsoriginiumdream:evildustoriworm 20 ${top(20, 20)} 20 {Tags:["osw_cmd"],PersistenceRequired:1b}`)
  }
  if (OSW.t0 > 0 && T == OSW.t0 + 401) server.runCommandSilent(`tp @e[tag=osw_walk] 10 ${top(10, 10)} 10`)
  if (OSW.t0 > 0 && T == OSW.t0 + 700) {
    ok('walked-in-stays', ents(0, 0, 200, (id, e) => e.getTags().contains('osw_walk')).length == 1, '')
    ok('command-in-zone-stays', ents(0, 0, 200, (id, e) => e.getTags().contains('osw_cmd')).length == 1, '')
  }
  if (OSW.t0 > 0 && !OSW.done && T > OSW.t0 + 600 && !stormOn()) {
    OSW.done = true
    OSW.doneT = T
    var S = Storm()
    console.info(`OSW stat призывов бури при обереге: ${S.spawnsSeen}, отменено в зоне: ${S.spawnsDenied}, пропущено в незагруженных чанках: ${S.spawnsUnloaded}, длительность ≈ ${T - OSW.t0 - 100} тиков`)
    ok('storm-spawns', S.spawnsSeen > 50, 'всего ' + S.spawnsSeen)
    ok('zone-denied', S.spawnsDenied > 0, 'отменено ' + S.spawnsDenied)
    var stray = ents(0, 0, 96, (id, e) => (id.indexOf('evildustoriworm') >= 0 && !e.getTags().contains('osw_walk') && !e.getTags().contains('osw_cmd')) || id.indexOf('falling_block') >= 0)
    ok('zone-clean', stray.length == 0, 'в зоне червей/падающих блоков бури: ' + stray.length)
    ok('spike-inside-still-blocked', oriBlocks(30, 30, 40) == 0, '' + oriBlocks(30, 30, 40))
    var avg = a => a.length ? (a.reduce((x, y) => x + y, 0) / a.length).toFixed(1) : '-'
    console.info(`OSW tick до бури ${avg(OSW.ticks)} мс (${OSW.ticks.length} замеров), во время бури ${avg(OSW.stormTicks)} мс (${OSW.stormTicks.length} замеров, макс ${OSW.stormTicks.length ? Math.max.apply(null, OSW.stormTicks).toFixed(1) : '-'})`)
  }
  // случайные тики ядер без живого игрока не идут — вызываем их сами: 20 ядер снаружи и 10 в зоне, по 10 тиков
  if (T == GROW_END - 300) {
    var RS = Java.loadClass('net.minecraft.util.RandomSource')
    var rnd = RS.create()
    for (var k = 0; k < 10; k++) {
      for (var i2 = 0; i2 < 20; i2++) { var p1 = new BlockPos(392 + i2 * 2, 152, 445); level.getBlockState(p1).randomTick(level, p1, rnd) }
      for (var j2 = 0; j2 < 10; j2++) { var p2 = new BlockPos(10 + j2 * 2, 150, -20); level.getBlockState(p2).randomTick(level, p2, rnd) }
    }
  }
  if (T == GROW_END) {
    var blockAt = (x, y, z) => String(level.getBlockState(new BlockPos(x, y, z)).block.id)
    var inZone = 0, edge = 0, maxX = -1e9
    for (var x = 100; x <= 135; x++) for (var z = -40; z <= 40; z++) for (var y = 149; y <= 152; y++) {
      var b = blockAt(x, y, z)
      if (b.indexOf('bobsoriginiumdream:') != 0) continue
      if (x <= 112) inZone++; else edge++
      if (b != 'bobsoriginiumdream:oriblockb' || x != 115) maxX = Math.max(maxX, -x)
    }
    ok('growth-edge-outside', inZone == 0, `блоков ориджиниума в зоне (x≤112): ${inZone}, у края снаружи: ${edge}, ближе всего к зоне x=${-maxX}`)
    var old = 0, bb = 0, cores = 0
    for (var x2 = 390; x2 <= 440; x2++) for (var z2 = 390; z2 <= 440; z2++) for (var y2 = 149; y2 <= 152; y2++) {
      var b2 = blockAt(x2, y2, z2)
      if (b2 == 'bobsoriginiumdream:oldoriblock') old++; else if (b2 == 'bobsoriginiumdream:oriblockb') bb++; else if (b2 == 'bobsoriginiumdream:oriblock_core') cores++
    }
    var G = Java.loadClass('dev.mcmoded.disasterward.OriGrowth'), hours = (GROW_END - 200) / 72000
    console.info(`OSW grow поле 100 семян за ${(GROW_END - 200) / 1200} мин (${Math.floor((GROW_END - 200) / 2400)} тиков роста): шагов роста (oldoriblock) ${old}, активных oriblockb ${bb}, новых ядер ${cores}; ≈ ${(old / 100 / hours).toFixed(1)} шага на семя в час`)
    console.info(`OSW grow счётчики: попыток роста ${G.growTried}, пропущено броском ${G.growSkipped}, ядро: тиков ${G.coreTried}, пропущено ${G.coreSkipped}, отменено в зоне ${G.zoneDenied}`)
    var zoneMobs = ents(0, 0, 112, (t, e) => ORI_MOBS.some(m => t.indexOf(m) >= 0)).length
    var outMobs = ents(412, 445, 40, (t, e) => ORI_MOBS.some(m => t.indexOf(m) >= 0)).length
    ok('core-zone-quiet', zoneMobs == 0, `монстров ориджиниума в зоне: ${zoneMobs}, у ядер снаружи: ${outMobs}`)
    ok('growth-happens', old > 0, `шагов роста в поле: ${old}`)
    ok('core-throttled', G.coreTried >= 300 && G.coreSkipped > 0 && G.coreSkipped < G.coreTried, `тиков ядер ${G.coreTried}, пропущено ${G.coreSkipped}`)
    ok('core-outside-spawns', outMobs > 0, `монстров у ядер снаружи: ${outMobs}`)
    var avg2 = a => a.length ? (a.reduce((x, y) => x + y, 0) / a.length).toFixed(1) : '-'
    console.info(`OSW tick после бури (рост включён) ${avg2(OSW.after)} мс (${OSW.after.length} замеров, макс ${OSW.after.length ? Math.max.apply(null, OSW.after).toFixed(1) : '-'})`)
    console.info('OSW done')
  }
})
