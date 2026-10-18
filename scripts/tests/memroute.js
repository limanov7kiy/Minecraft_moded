// Тест «полёт по новым чанкам» для поиска утечки кучи (поток 34, OOM в StructureTemplate.load у (-700, 96, 300)).
// Двигает область forceload 9x9 чанков (как обзор игрока) по маршруту: от (0,0) к (-700,300), дальше по квадрату со стороной MR.side
// блоков вокруг (-700,300); старые области снимаются (держим 3 последних). Следующий шаг — когда центр предыдущей
// области сгенерирован полностью (или через 30 с), то есть скорость ограничена генерацией, как у летящего игрока.
// Вывод: MEMROUTE step N x z t=<с> chunks=<загружено> heap=<МБ занято/макс>, MEMROUTE DONE. Кучу снимать снаружи (jcmd GC.class_histogram).
// Параметры — переменные окружения сервера: MEMROUTE_STEPS (по умолчанию 400), MEMROUTE_SIDE (3000), MEMROUTE_STRIDE (64 блока).
var MR = { step: 0, waitFrom: 0, held: [], done: false, t0: 0, last: null }
function envInt(k, d) { try { var v = Java.loadClass('java.lang.System').getenv(k); return v ? parseInt(v) : d } catch (e) { return d } }
MR.steps = envInt('MEMROUTE_STEPS', 400)
MR.side = envInt('MEMROUTE_SIDE', 3000)
MR.stride = envInt('MEMROUTE_STRIDE', 64)
function routePoint(i) {
  // отрезок (0,0) -> (-700,300), затем квадрат против часовой
  var lead = Math.ceil(Math.hypot(700, 300) / MR.stride)
  if (i <= lead) return [Math.round(-700 * i / lead), Math.round(300 * i / lead)]
  var d = (i - lead) * MR.stride, s = MR.side, p = d % (4 * s), x = -700, z = 300
  if (p < s) return [x + p, z]
  if (p < 2 * s) return [x + s, z + (p - s)]
  if (p < 3 * s) return [x + s - (p - 2 * s), z + s]
  return [x, z + s - (p - 3 * s)]
}
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  if (T < 200 || MR.done) return
  if (!MR.t0) { MR.t0 = Date.now(); console.info('MEMROUTE T0 ' + MR.t0) }
  var level = server.getLevel('minecraft:overworld')
  if (MR.last) {
    var ready = level.getChunkSource().hasChunk(MR.last[0] >> 4, MR.last[1] >> 4)
    if (!ready && Date.now() - MR.waitFrom < 30000) return
  }
  if (MR.step >= MR.steps) {
    MR.held.forEach(h => server.runCommandSilent('forceload remove ' + h))
    MR.done = true; console.info('MEMROUTE DONE t=' + Math.round((Date.now() - MR.t0) / 1000)); return
  }
  var p = routePoint(MR.step)
  var area = (p[0] - 64) + ' ' + (p[1] - 64) + ' ' + (p[0] + 64) + ' ' + (p[1] + 64)
  server.runCommandSilent('forceload add ' + area)
  MR.held.push(area)
  if (MR.held.length > 3) server.runCommandSilent('forceload remove ' + MR.held.shift())
  MR.last = p; MR.waitFrom = Date.now()
  if (MR.step % 10 == 0) {
    var used = '?', max = '?'
    try { var rt = Java.loadClass('java.lang.Runtime').getRuntime(); used = Math.round((rt.totalMemory() - rt.freeMemory()) / 1048576); max = Math.round(rt.maxMemory() / 1048576) } catch (e) {}
    console.info('MEMROUTE step ' + MR.step + ' ' + p[0] + ' ' + p[1] + ' t=' + Math.round((Date.now() - MR.t0) / 1000) +
      ' chunks=' + level.getChunkSource().getLoadedChunksCount() + ' heap=' + used + '/' + max)
  }
  MR.step++
})
