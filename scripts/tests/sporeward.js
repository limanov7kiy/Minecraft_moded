// Тест (поток 22): Оберег (mods-src/disaster-ward) против паразитов Spore / SRP System и бедствий у края зоны.
// Вывод: SWCHECK <проверка> OK|FAIL <подробности>. Оберег в (0,150,0), радиус из конфига (96), запас edgeMargin 16 → паразиты не появляются ближе 112.
// Запуск: EXTRA_SERVER_SCRIPTS=<папка только с этим файлом> TEST_COMMANDS=<файл с «#sleep 120»> scripts/test-server.sh <папка> (docs/compat.md «Паразиты»).
var SW = {}
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  var level = server.getLevel('minecraft:overworld')
  var BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  var Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  var AABB = Java.loadClass('net.minecraft.world.phys.AABB')
  var Api = () => Java.loadClass('dev.mcmoded.disasterward.WardApi')
  var Guard = () => Java.loadClass('dev.mcmoded.disasterward.WardGuard')
  var ok = (name, cond, info) => console.info(`SWCHECK ${name} ${cond ? 'OK' : 'FAIL'} ${info || ''}`)
  var ents = (id, x, z, r) => { var out = []; level.getEntitiesOfClass(Entity, new AABB(x - r, -64, z - r, x + r, 320, z + r))
    .forEach(e => { if (String(e.getType().toString()).includes(id) && !e.isRemoved()) out.push(e) }); return out }
  var block = (x, y, z) => String(level.getBlockState(new BlockPos(x, y, z)).block.id)
  var has = (e, eff) => { let f = false; e.getActiveEffects().forEach(i => { if (String(i.getEffect().getDescriptionId()).includes(eff)) f = true }); return f }
  var R = 96
  if (T % 100 == 0) console.info('SWCHECK debug T=' + T)

  if (T == 60) {
    server.runCommandSilent('forceload add -16 -16 16 16')
    server.runCommandSilent('forceload add 64 -16 176 176')
  }
  if (T == 100) {
    server.runCommandSilent('setblock 0 150 0 disaster_ward:ward')
    ok('ward-placed', block(0, 150, 0) == 'disaster_ward:ward', '')
    ok('radius-unified', Java.loadClass('dev.mcmoded.disasterward.WardConfig').RADIUS.get() == R, 'ward.radius=' + Java.loadClass('dev.mcmoded.disasterward.WardConfig').RADIUS.get())
    ok('zone-edge', Api().isProtected(level, 96, 0) && !Api().isProtected(level, 98, 0), 'x=96 внутри, x=98 снаружи')
    // площадка для заражения (камень с воздухом над ним) поперёк края зоны и диагональ для землетрясения
    server.runCommandSilent('fill 80 149 -8 140 149 8 minecraft:stone')
    server.runCommandSilent('fill 80 150 -8 140 160 8 minecraft:air')
    for (let x = 80; x <= 130; x++) server.runCommandSilent(`fill ${x} 130 ${x} ${x} 165 ${x} minecraft:stone`)
    // появление паразитов: внутри, в полосе у края — нельзя; снаружи — можно
    server.runCommandSilent('summon spore:inf_human 10 150 0')
    server.runCommandSilent('summon spore:mound 105 150 0')
    server.runCommandSilent('summon spore:inf_human 140 150 3 {PersistenceRequired:1b}')
    // заражение: корова в зоне не получает мицелий; корова снаружи получает
    server.runCommandSilent('summon minecraft:cow 5 150 5 {NoAI:1b,PersistenceRequired:1b,CustomName:\'"sw_in"\'}')
    server.runCommandSilent('summon minecraft:cow 150 150 -5 {NoAI:1b,PersistenceRequired:1b,CustomName:\'"sw_out"\'}')
    server.runCommandSilent('effect give @e[type=minecraft:cow,name=sw_in] spore:mycelium_ef 600 0')
    server.runCommandSilent('effect give @e[type=minecraft:cow,name=sw_out] spore:mycelium_ef 600 0')
    server.runCommandSilent('effect give @e[type=minecraft:cow,name=sw_out] srpsystem:infection 600 0')
    // босс (тег forge:bosses) не заражается нигде — иначе SRP System превратит его при 30 % HP
    server.runCommandSilent('summon minecraft:wither 150 155 -5 {NoAI:1b,PersistenceRequired:1b}')
    server.runCommandSilent('effect give @e[type=minecraft:wither] srpsystem:infection 600 0')
    server.runCommandSilent('effect give @e[type=minecraft:wither] spore:mycelium_ef 600 0')
  }
  if (T == 102) {
    ok('spawn-inside-blocked', ents('spore:', 0, 0, 60).length == 0, 'паразитов в центре=' + ents('spore:', 0, 0, 60).length)
    ok('spawn-edge-blocked', ents('spore:mound', 105, 0, 4).length == 0, 'курганов у края=' + ents('spore:mound', 105, 0, 4).length)
    ok('spawn-outside-allowed', ents('spore:inf_human', 140, 3, 4).length == 1, 'снаружи=' + ents('spore:inf_human', 140, 3, 4).length)
    var cin = ents('minecraft:cow', 5, 5, 3), cout = ents('minecraft:cow', 150, -5, 3)
    ok('no-infection-in-zone', cin.length == 1 && !has(cin[0], 'mycelium'), 'корова в зоне: мицелий=' + (cin.length && has(cin[0], 'mycelium')))
    ok('infection-outside', cout.length == 1 && has(cout[0], 'mycelium') && has(cout[0], 'srpsystem.infection'), 'корова снаружи: мицелий/SRP=' + (cout.length && has(cout[0], 'mycelium')) + '/' + (cout.length && has(cout[0], 'srpsystem.infection')))
    var w = ents('minecraft:wither', 150, -5, 8)
    ok('boss-immune', w.length == 1 && !has(w[0], 'srpsystem.infection') && !has(w[0], 'mycelium'), 'визер: SRP=' + (w.length && has(w[0], 'srpsystem.infection')) + ' мицелий=' + (w.length && has(w[0], 'mycelium')))
    w.forEach(e => e.discard())
    // заражённая корова заходит в зону — эффекты снимаются (проверка раз в 5 тиков)
    cout.forEach(c => c.teleportTo(10.5, 150, 10.5))
    // землетрясение Natural Disasters! снаружи у угла зоны: рвёт блоки по диагонали ±50 от себя (x = z от 80 до 180)
    server.runCommandSilent('summon natural_disasters:earthquake 130 150 130')
    SW.denied0 = Guard().denied
  }
  if (T == 115) {
    var c = ents('minecraft:cow', 10, 10, 3)
    ok('cured-on-enter', c.length == 1 && !has(c[0], 'mycelium') && !has(c[0], 'srpsystem.infection'), 'корова в зоне после входа: ' + (c.length ? has(c[0], 'mycelium') + '/' + has(c[0], 'srpsystem.infection') : 'нет'))
    c.forEach(e => e.discard()); ents('minecraft:cow', 5, 5, 3).forEach(e => e.discard())
    ok('earthquake-alive', ents('natural_disasters:earthquake', 130, 130, 8).length == 1, 'землетрясение снаружи у угла')
    // заражение блоков Spore в контексте тика паразита (как его делает курган): центр у края зоны снаружи и далеко снаружи
    try {
      server.runCommandSilent('summon spore:mound 150 150 0 {NoAI:1b,PersistenceRequired:1b}')
      var m = ents('spore:mound', 150, 0, 3)[0]
      SW.spreadIn = 0; SW.spreadOut = 0
      if (m && m.SpreadInfection) {
        for (let i = 0; i < 6; i++) {
          Guard().enter(level, m)
          try { m.SpreadInfection(level, 8.0, new BlockPos(92, 150, 0)); m.SpreadInfection(level, 8.0, new BlockPos(128, 150, 0)) } finally { Guard().exit() }
        }
        SW.spreadOk = true
      } else console.info('SWCHECK debug курган не найден или не FoliageSpread: ' + m)
    } catch (e) { console.info('SWCHECK debug spread error ' + e) }
  }
  if (T == 117) {
    let inside = 0, outside = 0
    for (let x = 84; x <= 140; x++) for (let z = -8; z <= 8; z++) for (let y = 149; y <= 151; y++) {
      var b = block(x, y, z)
      if (b.startsWith('spore:') || b == 'minecraft:mycelium') { if (x <= R) inside++; else outside++ }
    }
    ok('spore-blocks-inside-zero', SW.spreadOk && inside == 0, `заражённых блоков внутри=${inside}`)
    ok('spore-blocks-outside-spread', SW.spreadOk && outside > 0, `заражённых блоков снаружи=${outside}`)
    // взрыв из тика бедствия (как «ничейный» взрыв метеора Natural Disasters!) на самом краю зоны
    var quake = ents('natural_disasters:earthquake', 130, 130, 8)[0]
    if (quake) {
      Guard().enter(level, quake)
      try { level.explode(null, 97.0, 149.5, 0.5, 4.0, Java.loadClass('net.minecraft.world.level.Level$ExplosionInteraction').TNT) } finally { Guard().exit() }
    }
    let inAir = 0, outAir = 0
    for (let x = 93; x <= 101; x++) for (let z = -2; z <= 2; z++) { if (block(x, 149, z) == 'minecraft:air') { if (x <= R) inAir++; else outAir++ } }
    ok('explosion-inside-intact', quake && inAir == 0, `разрушено внутри=${inAir}`)
    ok('explosion-outside-broken', quake && outAir > 0, `разрушено снаружи=${outAir}`)
    // паразит, оказавшийся внутри (телепорт), убирается; в полосе у края его отталкивает наружу
    server.runCommandSilent('summon spore:inf_human 150 150 6 {PersistenceRequired:1b,CustomName:\'"sw_tp"\'}')
    server.runCommandSilent('summon spore:inf_human 150 150 -6 {PersistenceRequired:1b,CustomName:\'"sw_push"\'}')
    server.runCommandSilent('tp @e[type=spore:inf_human,name=sw_tp] 20 150 6')
    server.runCommandSilent('tp @e[type=spore:inf_human,name=sw_push] 104 150 -6')
  }
  if (T >= 118 && T <= 160) {
    ents('spore:inf_human', 104, -6, 20).forEach(e => { if (String(e.getName().getString()) == 'sw_push') { SW.pushMin = Math.min(SW.pushMin || 999, e.getX()); SW.pushLast = e.getX() } })
  }
  if (T == 160) {
    ok('teleported-inside-removed', ents('spore:inf_human', 20, 6, 10).length == 0, 'внутри осталось=' + ents('spore:inf_human', 20, 6, 10).length)
    ok('edge-pushed-out', SW.pushMin !== undefined && SW.pushMin > R && SW.pushLast > 104, `мин. x=${SW.pushMin} последний x=${SW.pushLast}`)
    // Weather2: торнадо в полосе у края разворачивается (единый радиус), оберег больше не «Отражатель бурь» с зашитым радиусом 128
    try {
      var w2Tick = Java.loadClass('weather2.ServerTickHandler')
      var w2Storm = Java.loadClass('weather2.weathersystem.storm.StormObject')
      var w2Vec = Java.loadClass('net.minecraft.world.phys.Vec3')
      var mgr = w2Tick.getWeatherManagerFor(level)
      var anchor = level.createEntity('minecraft:cow'); anchor.setPosition(108, 150, 0)
      var so = new w2Storm(mgr); so.setupStorm(anchor)
      so.levelCurIntensityStage = 6; so.maxIntensityStage = 6
      so.initPositions(new w2Vec(108.5, 150, 0.5))
      mgr.addStormObject(so); mgr.syncStormNew(so)
      SW.storm = so; SW.steer0 = Api().stormsSteered
    } catch (e) { console.info('SWCHECK debug weather2 error ' + e) }
    // разрыв блока торнадо Weather2 идёт через BreakEvent от weather2FakePlayer — в зоне отменяется
    try {
      var Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
      var GP = Java.loadClass('com.mojang.authlib.GameProfile')
      var UUID = Java.loadClass('java.util.UUID')
      var BE = Java.loadClass('net.minecraftforge.event.level.BlockEvent$BreakEvent')
      var bus = Java.loadClass('net.minecraftforge.common.MinecraftForge').EVENT_BUS
      var fp = Fake.get(level, new GP(UUID.fromString('1396b887-2570-4948-86e9-0633d1d22946'), 'weather2FakePlayer'))
      var evIn = new BE(level, new BlockPos(90, 149, 0), level.getBlockState(new BlockPos(90, 149, 0)), fp); bus.post(evIn)
      var evOut = new BE(level, new BlockPos(130, 149, 0), level.getBlockState(new BlockPos(130, 149, 0)), fp); bus.post(evOut)
      ok('weather2-break-inside-cancelled', evIn.isCanceled() && !evOut.isCanceled(), `в зоне отменён=${evIn.isCanceled()} снаружи отменён=${evOut.isCanceled()}`)
      ok('weather2-break-event-enabled', Java.loadClass('weather2.config.ConfigMisc').blockBreakingInvokesCancellableEvent, 'Misc.toml blockBreakingInvokesCancellableEvent')
    } catch (e) { ok('weather2-break-inside-cancelled', false, 'ошибка ' + e) }
  }
  if (T == 220) {
    if (SW.storm) {
      ok('weather2-steered', Api().stormsSteered > SW.steer0 && SW.storm.angleIsOverridden, `разворотов=${Api().stormsSteered - SW.steer0} pos=${SW.storm.posGround}`)
      SW.storm.remove()
    } else ok('weather2-steered', false, 'буря не создана')
    ok('weather2-not-deflector', !Api().weatherDeflected(level, new BlockPos(0, 150, 0), 128), 'оберег не в списке отражателей Weather2')
  }
  if (T == 400) {
    let inAir = 0, outAir = 0
    for (let x = 80; x <= 130; x++) for (let y = 130; y <= 165; y++) { if (block(x, y, x) != 'minecraft:stone') { if (x <= R) inAir++; else outAir++ } }
    ok('earthquake-inside-intact', inAir == 0, `разрушено на диагонали внутри=${inAir}`)
    ok('earthquake-outside-broken', outAir > 0, `разрушено снаружи=${outAir}`)
    ok('guard-denied-count', Guard().denied > SW.denied0, `отклонено изменений блоков=${Guard().denied - SW.denied0}`)
    server.runCommandSilent('kill @e[type=natural_disasters:earthquake]')
    console.info('SWCHECK done')
  }
})
