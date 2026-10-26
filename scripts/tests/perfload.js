// Нагрузочный замер сервера «как у игроков» (поток 67): покой без игроков → N фейковых игроков в покое → полёт с генерацией
// новых чанков → сохранение (save-all, затем save-all flush) → покой после. Фейковые игроки Forge добавлены в мир
// (level.players, тикеты чанков по view-distance сервера, естественный спавн мобов вокруг них), двигаются setPos + ChunkSource.move.
// Вывод (строки PERFLOAD, epoch-мс для разбора JFR по фазам — scripts/jfr/JfrMods.java <jfr> <от> <до>):
//   PERFLOAD phase <имя> start=<мс>                 — начало фазы
//   PERFLOAD stat <фаза> mspt avg/p50/p95/max tps chunks entities dims pos — раз в 200 тиков (за последние 200 тиков)
//   PERFLOAD save <вид> ms=<время команды>          — длительность save-all / save-all flush на главном потоке
//   PERFLOAD summary <фаза> ...                     — итог фазы
// Параметры — константы ниже (KubeJS не даёт читать переменные окружения: java.lang.System закрыт фильтром классов).
// Куча — снаружи (jstat/jcmd): java.lang.Runtime тоже закрыт.
// Запуск: EXTRA_SERVER_SCRIPTS=<папка только с этим файлом> TEST_COMMANDS=<файл с «#sleep 1450»> scripts/test-server.sh <папка вне репо>
var PL = { phase: null, t0: 0, ph0: 0, players: [], ticks: [], all: {}, done: false }
// игроков, покой без игроков (с), покой с игроками, полёт, скорость (блоков/с), покой после, расстояние игроков от (0,0)
PL.n = 3; PL.idle0 = 600; PL.idle = 300; PL.fly = 240; PL.speed = 10; PL.after = 180; PL.spread = 1000
function plStats(arr) {
  if (!arr.length) return 'n=0'
  var s = arr.slice().sort((a, b) => a - b), sum = 0
  s.forEach(v => sum += v)
  var q = p => s[Math.min(s.length - 1, Math.floor(p * s.length))]
  return 'avg=' + (sum / s.length).toFixed(1) + ' p50=' + q(0.5).toFixed(1) + ' p95=' + q(0.95).toFixed(1) + ' max=' + s[s.length - 1].toFixed(0) +
    ' over50=' + Math.round(100 * s.filter(v => v > 50).length / s.length) + '%'
}
function plCounts(server) {
  var ch = 0, ent = 0
  server.getAllLevels().forEach(lv => { ch += lv.getChunkSource().getLoadedChunksCount(); var c = 0; lv.getAllEntities().forEach(e => c++); ent += c })
  return 'chunks=' + ch + ' entities=' + ent
}
function plDims(server) {
  var r = []
  server.getAllLevels().forEach(lv => { var c = lv.getChunkSource().getLoadedChunksCount(); if (c > 0) r.push(lv.dimension + '=' + c) })
  return r.join(' ')
}
function plPhase(server, name) {
  if (PL.phase) console.info('PERFLOAD summary ' + PL.phase + ' dur=' + Math.round((Date.now() - PL.ph0) / 1000) + 's mspt ' + plStats(PL.all[PL.phase] || []) + ' ' + plCounts(server) + ' dims: ' + plDims(server))
  PL.phase = name; PL.ph0 = Date.now(); PL.all[name] = []
  console.info('PERFLOAD phase ' + name + ' start=' + PL.ph0)
}
function plPlayers(server) {
  var level = server.getLevel('minecraft:overworld')
  var Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory'), GP = Java.loadClass('com.mojang.authlib.GameProfile'), UUID = Java.loadClass('java.util.UUID')
  for (var i = 0; i < PL.n; i++) {
    // игроки по кругу радиусом spread от (0,0), каждый летит наружу
    // направления по кругу без Math.PI/cos (в Rhino KubeJS дают NaN): 0°, 120°, 240°, 60°, 180°, 300°
    var dirs = [[1, 0], [-0.5, 0.866], [-0.5, -0.866], [0.5, 0.866], [-1, 0], [0.5, -0.866]], dv = dirs[i % dirs.length]
    var x = Math.round(PL.spread * dv[0]), z = Math.round(PL.spread * dv[1])
    var px = 0.0 + x + 0.5, pz = 0.0 + z + 0.5
    var fp = Fake.get(level, new GP(UUID.fromString('9e7f0000-0000-4000-8000-00000000000' + i), 'perf' + i))
    fp.setPos(px, 140.0, pz)
    console.info('PERFLOAD player ' + i + ' want ' + px + ',' + pz + ' got ' + fp.getX() + ',' + fp.getZ())
    level.addFreshEntity(fp)
    level.getChunkSource().move(fp)
    PL.players.push({ p: fp, x: x, z: z, dx: dv[0], dz: dv[1] })
  }
  console.info('PERFLOAD players=' + level.players.size() + ' first=' + PL.players[0].p.blockPosition() + ' at ' + PL.players.map(o => o.x + ',' + o.z).join(' '))
}
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  if (PL.done || T < 100) return
  if (!PL.t0) {
    PL.t0 = Date.now()
    server.runCommandSilent('gamerule doDaylightCycle true'); server.runCommandSilent('difficulty normal')
    plPhase(server, 'idle0')
  }
  // время тика: MinecraftServer.tickTimes (нс, кольцевой буфер на 100 тиков) — берём значение прошлого тика
  var tt = server.tickTimes, prev = (T - 1) % 100
  var ms = tt[prev] / 1e6
  PL.ticks.push(ms); PL.all[PL.phase].push(ms)
  if (PL.ticks.length >= 200) {
    var wall = (Date.now() - (PL.wall || PL.t0)) / 1000; PL.wall = Date.now()
    console.info('PERFLOAD stat ' + PL.phase + ' t=' + Math.round((Date.now() - PL.t0) / 1000) + ' mspt ' + plStats(PL.ticks) + ' tps=' + (200 / wall).toFixed(1) + ' ' + plCounts(server) + ' dims: ' + plDims(server) + ' pos: ' + PL.players.map(o => Math.round(o.p.getX()) + ',' + Math.round(o.p.getZ())).join(' '))
    PL.ticks = []
  }
  var el = (Date.now() - PL.ph0) / 1000
  if (PL.phase == 'idle0' && el >= PL.idle0) { try { plPlayers(server) } catch (e) { console.info('PERFLOAD players FAIL ' + e) } plPhase(server, 'players_idle') }
  else if (PL.phase == 'players_idle' && el >= PL.idle) plPhase(server, 'fly')
  else if (PL.phase == 'fly') {
    if (el >= PL.fly) {
      plPhase(server, 'save')
      var t1 = Date.now(); server.runCommandSilent('save-all'); console.info('PERFLOAD save save-all ms=' + (Date.now() - t1))
      t1 = Date.now(); server.runCommandSilent('save-all flush'); console.info('PERFLOAD save save-all-flush ms=' + (Date.now() - t1))
      plPhase(server, 'after')
    } else {
      // скорость в блоках/с по реальному времени (а не по тикам): при «Can't keep up» игрок всё равно летит
      PL.players.forEach(o => {
        var d = PL.speed * el
        o.p.setPos(0.0 + o.x + o.dx * d + 0.5, 140.0, 0.0 + o.z + o.dz * d + 0.5)
        server.getLevel('minecraft:overworld').getChunkSource().move(o.p)
      })
    }
  } else if (PL.phase == 'after' && el >= PL.after) {
    plPhase(server, 'end'); PL.done = true; console.info('PERFLOAD DONE')
  }
})
