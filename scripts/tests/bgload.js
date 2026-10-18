// Фон тика сервера без нагрузки от игрока (поток 33): чанки у спавна загружены, один фейковый игрок, естественный спавн включён.
// Каждые 20 с пишет «BGLOAD t=… avg=… max_wall=…» (tickTimes сервера и самый долгий промежуток между тиками за окно) и «BGLOAD T0 <epoch мс>» для разбора JFR
// (scripts/jfr/JfrTop.java, scripts/jfr/JfrHandlers.java). Запуск: EXTRA_SERVER_SCRIPTS=<папка с этим файлом>, TEST_COMMANDS=«#sleep 330».
var BG = { last: 0, wall: [], spikes: [] }
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  var level = server.getLevel('minecraft:overworld')
  var now = Date.now(), dt = BG.last ? now - BG.last : 0
  BG.last = now
  if (T == 200) {
    var sp = level.getSharedSpawnPos(), cx = sp.x >> 4, cz = sp.z >> 4
    server.runCommandSilent('forceload add ' + (sp.x - 64) + ' ' + (sp.z - 64) + ' ' + (sp.x + 64) + ' ' + (sp.z + 64))
    for (var dx = -5; dx <= 5; dx++) for (var dz = -5; dz <= 5; dz++) level.getChunk(cx + dx, cz + dz)
    console.info('BGLOAD chunks loaded around ' + cx + ',' + cz)
  }
  if (T == 260) {
    try {
      var Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory'), GP = Java.loadClass('com.mojang.authlib.GameProfile'), UUID = Java.loadClass('java.util.UUID')
      var sp2 = level.getSharedSpawnPos()
      var fp = Fake.get(level, new GP(UUID.fromString('5a0e10ad-0000-4000-8000-0000000000bb'), 'bgload'))
      fp.setPos(sp2.x + 0.5, level.getHeight(Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types').MOTION_BLOCKING_NO_LEAVES, sp2.x, sp2.z) + 1, sp2.z + 0.5)
      level.addFreshEntity(fp)
      console.info('BGLOAD fakeplayer level.players=' + level.players.size())
    } catch (e) { console.info('BGLOAD fakeplayer FAIL ' + e) }
  }
  if (T == 400) console.info('BGLOAD T0 ' + now)
  if (T < 400) return
  if (dt > 0) BG.wall.push(dt)
  if (dt > 150) BG.spikes.push('T=' + T + ':' + dt + 'ms')
  if (T % 400 == 0) {
    var w = BG.wall.slice().sort((a, b) => a - b), n = w.length
    var avg = w.reduce((a, b) => a + b, 0) / n
    var tt = server.tickTimes, s = 0; for (var i = 0; i < tt.length; i++) s += tt[i]
    var ent = 0; level.getAllEntities().forEach(e => { ent++ })
    console.info('BGLOAD t=' + (T - 400) + ' wall_avg=' + avg.toFixed(1) + ' p95=' + w[Math.floor(n * 0.95)] + ' p99=' + w[Math.floor(n * 0.99)] + ' max=' + w[n - 1] + ' tickTime=' + (s / tt.length / 1e6).toFixed(1) + ' entities=' + ent + ' spikes>150ms=[' + BG.spikes.join(',') + ']')
    BG.wall = []; BG.spikes = []
  }
})
