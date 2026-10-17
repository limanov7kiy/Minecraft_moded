// Тест «исследование нового мира» (поток 30, жалоба: в обычном новом мире сервер почти не тикает, /summon и яйца не отвечают).
// Имитирует игрока, летящего по спирали над новыми чанками (forceload кольцами: чанки генерируются асинхронно, как для игрока),
// мерит время тика (tickTimes), число тиков дольше 100/500 мс и каждые 30 с проверяет /summon cow (результат команды и наличие через 1 тик).
// Вывод: EXPLORE T0 <epoch мс> (для scripts/jfr/JfrTop.java), EXPLORE t+… avg/max/slow, EXPLORE summon OK|FAIL, EXPLORE DONE.
// Запуск: EXTRA_SERVER_SCRIPTS=<папка> TEST_COMMANDS=<«#sleep 420»> SERVER_JVM_EXTRA='-XX:StartFlightRecording=filename=…,settings=profile' scripts/test-server.sh <папка вне репо>
var EX = { s: [], slow100: 0, slow500: 0, ring: 0, pending: null, sumOk: 0, sumBad: 0, t0: 0 }
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  var level = server.getLevel('minecraft:overworld')
  if (T < 300) return
  if (!EX.t0) { EX.t0 = T; console.info('EXPLORE T0 ' + Date.now()) }
  var rel = T - EX.t0
  var own = null
  try { var tt = server.tickTimes; if (tt) own = tt[(T - 1) % 100] / 1e6 } catch (e) {}
  if (own !== null) { EX.s.push(own); if (own > 100) EX.slow100++; if (own > 500) EX.slow500++ }
  // кольцо новых чанков каждые 2 с: центр на спирали (шаг 48 блоков), загружается область 5x5 чанков
  if (rel % 40 == 0 && rel < 4800) {
    var a = EX.ring * 0.5, r = 40 + EX.ring * 6, cx = Math.floor(r * Math.cos(a)), cz = Math.floor(r * Math.sin(a))
    server.runCommandSilent('forceload add ' + (cx - 40) + ' ' + (cz - 40) + ' ' + (cx + 40) + ' ' + (cz + 40))
    if (EX.ring > 6) { var b = (EX.ring - 6) * 0.5, br = 40 + (EX.ring - 6) * 6
      server.runCommandSilent('forceload remove ' + (Math.floor(br * Math.cos(b)) - 40) + ' ' + (Math.floor(br * Math.sin(b)) - 40) + ' ' + (Math.floor(br * Math.cos(b)) + 40) + ' ' + (Math.floor(br * Math.sin(b)) + 40)) }
    EX.ring++
  }
  if (rel % 600 == 100 && rel < 4800) {
    var res = server.runCommandSilent('summon minecraft:cow 5 ' + (level.getHeight(Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types').MOTION_BLOCKING_NO_LEAVES, 5, 5) + 1) + ' 5 {CustomName:\'"exc"\'}')
    EX.pending = T + 1; EX.sumRes = res
  }
  if (EX.pending && T == EX.pending) { EX.pending = null
    var n = 0; level.getAllEntities().forEach(e => { if (String(e.getType()) == 'minecraft:cow' && e.customName && String(e.customName.getString()) == 'exc') n++ })
    if (n > 0) EX.sumOk++; else EX.sumBad++
    console.info('EXPLORE summon ' + (n > 0 ? 'OK' : 'FAIL') + ' result=' + EX.sumRes + ' cows=' + n)
    level.getAllEntities().forEach(e => { if (e.customName && String(e.customName.getString()) == 'exc') e.discard() })
  }
  if (rel % 600 == 0 && rel > 0) {
    var s = EX.s.slice().sort((p, q) => p - q), avg = s.reduce((p, q) => p + q, 0) / Math.max(s.length, 1)
    console.info('EXPLORE t+' + (rel / 20) + 's tick avg=' + avg.toFixed(1) + ' p99=' + (s[Math.floor(s.length * 0.99)] || 0).toFixed(0) + ' max=' + (s[s.length - 1] || 0).toFixed(0) + ' slow>100ms=' + EX.slow100 + ' slow>500ms=' + EX.slow500 + ' ring=' + EX.ring + ' entities=' + level.getAllEntities().size() + ' summon ok/bad=' + EX.sumOk + '/' + EX.sumBad)
    EX.s = []
  }
  if (rel == 4900) console.info('EXPLORE DONE')
})
