// Замер сохранения мира (поток 64, отчёт R-012: долгая пауза при автосейве/выходе, 52 измерения).
// Фазы по времени после старта: A — через 2400 тиков после старта (первые минуты тики и так медленные); B — «игрок побывал в разных измерениях»
// (forceload 9×9 чанков по маршруту в верхнем мире, Незере, Энде и 3×3 у (0,0) в каждом прочем измерении),
// C — через 60 с после снятия forceload. В каждой фазе: по каждому измерению загружено чанков / forceload,
// затем время сохранения: SavedData измерения (data/*.dat), чанки измерения (level.save без flush, как автосейв),
// и полное saveEverything (как автосейв: игроки, level.dat). Отдельно — тики дольше 1 с (ловит ванильный автосейв раз в 6000 тиков).
// Вывод: SAVECHECK <фаза> ..., SAVECHECK DIM <фаза> <измерение> chunks=<n> forced=<n> data=<мс> chunks_save=<мс>, SAVECHECK GAP tick=<n> ms=<n>.
var SC = { phase: 0, t: 0, last: 0, held: [] }
function dimId(l) { try { return String(l.dimension.location()) } catch (e) { return String(l.dimension) } }
function nPlayers(l) { try { return l.players.size() } catch (e) { return '?' } }
function nano() { return Java.loadClass('java.lang.System').nanoTime() }
function stats(server, tag) {
  var tot = 0, dims = 0, withChunks = 0
  server.getAllLevels().forEach(l => {
    dims++
    var n = l.getChunkSource().getLoadedChunksCount(), f = l.getForcedChunks().size()
    tot += n; if (n > 0) withChunks++
    if (n > 0 || f > 0) console.info('SAVECHECK LOADED ' + tag + ' ' + dimId(l) + ' chunks=' + n + ' forced=' + f + ' players=' + nPlayers(l))
  })
  console.info('SAVECHECK STATS ' + tag + ' dims=' + dims + ' dimsWithChunks=' + withChunks + ' chunks=' + tot)
}
function timeSave(server, tag) {
  var rows = [], sumData = 0, sumChunks = 0
  server.getAllLevels().forEach(l => {
    var t0 = nano()
    l.getDataStorage().save()
    var t1 = nano()
    l.save(null, false, false)
    var t2 = nano()
    var d = (t1 - t0) / 1e6, c = (t2 - t1) / 1e6
    sumData += d; sumChunks += c
    rows.push([dimId(l), d, c, l.getChunkSource().getLoadedChunksCount()])
  })
  rows.sort((a, b) => (b[1] + b[2]) - (a[1] + a[2]))
  rows.slice(0, 15).forEach(r => console.info('SAVECHECK DIM ' + tag + ' ' + r[0] + ' chunks=' + r[3] + ' data=' + r[1].toFixed(1) + ' chunks_save=' + r[2].toFixed(1)))
  var t0 = nano()
  server.saveEverything(true, false, true)
  var all = (nano() - t0) / 1e6
  console.info('SAVECHECK SAVE ' + tag + ' dims=' + rows.length + ' data_sum=' + sumData.toFixed(0) + ' chunks_sum=' + sumChunks.toFixed(0) + ' saveEverything_after=' + all.toFixed(0))
  var t1 = nano()
  server.saveEverything(true, true, true)
  console.info('SAVECHECK FLUSH ' + tag + ' saveEverything_flush=' + ((nano() - t1) / 1e6).toFixed(0))
}
function fl(server, dim, x0, z0, x1, z1, add) {
  var a = x0 + ' ' + z0 + ' ' + x1 + ' ' + z1
  server.runCommandSilent('execute in ' + dim + ' run forceload ' + (add ? 'add ' : 'remove ') + a)
  if (add) SC.held.push([dim, x0, z0, x1, z1])
}
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount, now = Date.now()
  if (SC.last && now - SC.last > 1000 && SC.phase != 1) console.info('SAVECHECK GAP tick=' + T + ' ms=' + (now - SC.last) + ' phase=' + SC.phase + ' tick%6000=' + (T % 6000))
  SC.last = now
  if (SC.phase == 0 && T >= 2400) {
    stats(server, 'A'); timeSave(server, 'A'); SC.phase = 1; SC.last = Date.now(); return
  }
  if (SC.phase == 1) {
    var t0 = Date.now()
    for (var i = 0; i < 12; i++) fl(server, 'minecraft:overworld', i * 128 - 64, -64, i * 128 + 64, 64, true)
    for (var i = 0; i < 4; i++) fl(server, 'minecraft:the_nether', i * 128 - 64, -64, i * 128 + 64, 64, true)
    fl(server, 'minecraft:the_end', -64, -64, 64, 64, true)
    server.getAllLevels().forEach(l => {
      var id = dimId(l)
      if (id.startsWith('minecraft:')) return
      try { fl(server, id, -16, -16, 16, 16, true) } catch (e) { console.info('SAVECHECK FLFAIL ' + id + ' ' + e) }
    })
    console.info('SAVECHECK LOADTIME ms=' + (Date.now() - t0))
    stats(server, 'B'); timeSave(server, 'B')
    SC.held.forEach(h => fl(server, h[0], h[1], h[2], h[3], h[4], false))
    SC.phase = 2; SC.t = Date.now(); SC.last = Date.now(); return
  }
  if (SC.phase == 2 && now - SC.t > 60000) {
    stats(server, 'C'); timeSave(server, 'C'); SC.phase = 3; SC.t = Date.now(); SC.last = Date.now(); return
  }
  if (SC.phase == 3 && now - SC.t > 30000) { stats(server, 'D'); SC.phase = 4; console.info('SAVECHECK DONE') }
})
