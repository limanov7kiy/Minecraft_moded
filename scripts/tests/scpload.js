// Нагрузка рядом с постройками SCP: Ultimate (поток 73, docs/compat.md «SCP»): ищет ближайший заброшенный комплекс
// и базу SCP Site от (0,0), держит чанки вокруг каждой загруженными (forceload, сущности тикают) и сравнивает время тика
// с покоем до этого. Вывод: SCPLOAD found <структура> <x> <z> | SCPLOAD stat <фаза> mspt avg/p95/max entities scp=<N> | SCPLOAD done.
// Запуск: EXTRA_SERVER_SCRIPTS=<папка с файлом> TEST_COMMANDS=<«#sleep 420»> scripts/test-server.sh <папка вне репо>
var SL = { t: [], phase: 'idle', found: [], start: 0 }
function slStat(server, name) {
  var s = SL.t.slice().sort((a, b) => a - b), sum = 0
  s.forEach(v => sum += v)
  var scp = 0, all = 0
  server.overworld().getAllEntities().forEach(e => { all++; if (String(e.getType()).indexOf('scp') == 0) scp++ })
  console.info('SCPLOAD stat ' + name + ' mspt avg=' + (sum / Math.max(1, s.length)).toFixed(1) + ' p95=' + (s[Math.floor(0.95 * s.length)] || 0).toFixed(1) +
    ' max=' + (s[s.length - 1] || 0).toFixed(0) + ' entities=' + all + ' scp=' + scp + ' chunks=' + server.overworld().getChunkSource().getLoadedChunksCount())
  SL.t = []
}
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  if (T < 200) return
  var tt = server.tickTimes
  SL.t.push(tt[(T - 1) % 100] / 1e6)
  if (T == 1400) {
    slStat(server, 'idle')
    var Reg = Java.loadClass('net.minecraft.core.registries.Registries')
    var RK = Java.loadClass('net.minecraft.resources.ResourceKey')
    var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
    var HS = Java.loadClass('net.minecraft.core.HolderSet')
    var BP = Java.loadClass('net.minecraft.core.BlockPos')
    var level = server.overworld(), reg = server.registryAccess().registryOrThrow(Reg.STRUCTURE)
    ;['scp_ultimate:facility_natural_1', 'scp_ultimate:facility_natural_2', 'scp_ultimate:facility_natural_3',
      'scp_ultimate:scp_site_1', 'scp_ultimate:scp_site_2', 'scp_ultimate:scp_site_3'].forEach(id => {
      try {
        var h = reg.getHolderOrThrow(RK.create(Reg.STRUCTURE, new RL(id)))
        var r = level.getChunkSource().getGenerator().findNearestMapStructure(level, HS.direct([h]), new BP(0, 64, 0), 3, false)
        if (r) { var p = r.getFirst(); SL.found.push(p); console.info('SCPLOAD found ' + id + ' ' + p.getX() + ' ' + p.getZ()) }
        else console.info('SCPLOAD found ' + id + ' none')
      } catch (e) { console.info('SCPLOAD error ' + id + ' ' + e) }
    })
    // по одному комплексу и одной базе: 9×9 чанков вокруг
    SL.found.slice(0, 6).forEach(p => {
      var cx = p.getX() >> 4, cz = p.getZ() >> 4
      server.runCommandSilent('forceload add ' + ((cx - 4) * 16) + ' ' + ((cz - 4) * 16) + ' ' + ((cx + 4) * 16) + ' ' + ((cz + 4) * 16))
    })
    SL.phase = 'bases'; SL.start = T
  }
  if (SL.phase == 'bases' && (T - SL.start) % 1200 == 0 && T > SL.start) {
    slStat(server, 'bases' + ((T - SL.start) / 1200))
    if (T - SL.start >= 3600) { SL.phase = 'done'; console.info('SCPLOAD done') }
  }
})
