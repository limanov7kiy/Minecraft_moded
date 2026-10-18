// Тест (поток 32): ориджиниумная буря Bob's Originium Dream и оберег (mods-src/disaster-ward 1.2, OriStorm.java), правила — oristorm_rules.js.
// Вывод: OSW <проверка> OK|FAIL <подробности>; OSW tick … — средний тик сервера (мс) до бури и во время неё.
// Запуск: EXTRA_SERVER_SCRIPTS=<папка только с этим файлом>, TEST_COMMANDS с «#sleep 420» (буря идёт 3000 тиков). См. docs/compat.md «Ориджиниумная буря».
const OSW = { t0: -1, ticks: [], stormTicks: [] }
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
  const oriBlocks = (x, z, r) => {
    var n = 0, y0 = top(x, z) - 10
    for (var dx = -r; dx <= r; dx++) for (var dz = -r; dz <= r; dz++) for (var y = y0; y < y0 + 70; y++) {
      if (String(level.getBlockState(new BlockPos(x + dx, y, z + dz)).block.id).indexOf('bobsoriginiumdream:') == 0) n++
    }
    return n
  }
  const stormOn = () => { try { return Vars().oristorm } catch (e) { return false } }

  if (T % 100 == 0 && T >= 200) {
    var mspt = server.getAverageTickTime()
    if (stormOn()) OSW.stormTicks.push(mspt); else if (OSW.t0 < 0 && T >= 400) OSW.ticks.push(mspt)
  }
  if (T == 100) {
    server.runCommandSilent('forceload add -128 -128 127 127')
    server.runCommandSilent('forceload add 384 384 447 447')
    ok('rule-oristorm', server.runCommandSilent('gamerule oristorm') == 1, 'oristorm=' + server.runCommandSilent('gamerule oristorm'))
    ok('rule-frequency', server.runCommandSilent('gamerule oristormfrequency') == 100000, '' + server.runCommandSilent('gamerule oristormfrequency'))
    ok('rule-summon', server.runCommandSilent('gamerule oristormsummon') == 200, '' + server.runCommandSilent('gamerule oristormsummon'))
    ok('rule-origrow', server.runCommandSilent('gamerule alloworigrow') == 0, '' + server.runCommandSilent('gamerule alloworigrow'))
    ok('rule-oribreak', server.runCommandSilent('gamerule oribreak') == 0, '' + server.runCommandSilent('gamerule oribreak'))
  }
  if (T == 160) {
    server.runCommandSilent('setblock 0 200 0 disaster_ward:ward')
    // блок бури в зоне и снаружи: через 100 тиков он ставит шип 50×53×57 (Place_spikeProcedure)
    server.runCommandSilent(`setblock 30 ${top(30, 30)} 30 bobsoriginiumdream:oristormblock`)
    server.runCommandSilent(`setblock 416 ${top(416, 416)} 416 bobsoriginiumdream:oristormblock`)
  }
  if (T == 400) {
    ok('spike-inside-blocked', oriBlocks(30, 30, 40) == 0, 'блоков Bob\'s в зоне у (30,30)=' + oriBlocks(30, 30, 40))
    ok('spike-outside-placed', oriBlocks(416, 416, 30) > 100, 'блоков Bob\'s снаружи у (416,416)=' + oriBlocks(416, 416, 30))
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
    server.runCommandSilent(`summon bobsoriginiumdream:evildustoriworm 160 ${top(160, 0)} 0 {Tags:["osw_walk"],PersistenceRequired:1b}`)
    server.runCommandSilent(`summon bobsoriginiumdream:evildustoriworm 20 ${top(20, 20)} 20 {Tags:["osw_cmd"],PersistenceRequired:1b}`)
  }
  if (OSW.t0 > 0 && T == OSW.t0 + 401) server.runCommandSilent(`tp @e[tag=osw_walk] 10 ${top(10, 10)} 10`)
  if (OSW.t0 > 0 && T == OSW.t0 + 700) {
    ok('walked-in-stays', ents(0, 0, 200, (id, e) => e.getTags().contains('osw_walk')).length == 1, '')
    ok('command-in-zone-stays', ents(0, 0, 200, (id, e) => e.getTags().contains('osw_cmd')).length == 1, '')
  }
  if (OSW.t0 > 0 && !OSW.done && T > OSW.t0 + 600 && !stormOn()) {
    OSW.done = true
    var S = Storm()
    console.info(`OSW stat призывов бури при обереге: ${S.spawnsSeen}, отменено в зоне: ${S.spawnsDenied}, длительность ≈ ${T - OSW.t0 - 100} тиков`)
    ok('storm-spawns', S.spawnsSeen > 50, 'всего ' + S.spawnsSeen)
    ok('zone-denied', S.spawnsDenied > 0, 'отменено ' + S.spawnsDenied)
    var stray = ents(0, 0, 96, (id, e) => (id.indexOf('evildustoriworm') >= 0 && !e.getTags().contains('osw_walk') && !e.getTags().contains('osw_cmd')) || id.indexOf('falling_block') >= 0)
    ok('zone-clean', stray.length == 0, 'в зоне червей/падающих блоков бури: ' + stray.length)
    ok('spike-inside-still-blocked', oriBlocks(30, 30, 40) == 0, '' + oriBlocks(30, 30, 40))
    var avg = a => a.length ? (a.reduce((x, y) => x + y, 0) / a.length).toFixed(1) : '-'
    console.info(`OSW tick до бури ${avg(OSW.ticks)} мс (${OSW.ticks.length} замеров), во время бури ${avg(OSW.stormTicks)} мс (${OSW.stormTicks.length} замеров, макс ${OSW.stormTicks.length ? Math.max.apply(null, OSW.stormTicks).toFixed(1) : '-'})`)
    console.info('OSW done')
  }
})
