// Поток 76: падение Torches Become Sunlight «Chunk not there when requested» (воронка мегаструктуры у края загруженной области).
// Повторяет условие потока 73: поиск структур с большим радиусом подгружает чанки вокруг строящейся sankta_holy_city до статуса
// STRUCTURE_STARTS (не FULL). Здесь — то же напрямую: волны getChunk(STRUCTURE_STARTS) по квадрату ±26 чанков вокруг центра.
// Запуск (seed 12345, docs/compat.md «Torches Become Sunlight: Chunk not there»): EXTRA_SERVER_SCRIPTS=<папка с файлом>
//   EXTRA_CONFIG=<папка с torchesbecomesunlight-common.toml, где generate = false у plain_rodes_office, snow_rodes_office, snow_guerrilla_camp —
//   иначе TBS строит естественные мегаструктуры по одной и sankta третья> TEST_COMMANDS=<«torchesbecomesunlight megastructure status» + «#sleep 60»>
//   scripts/test-server.sh <папка вне репо>. Смотреть: в логе нет «Chunk not there», строки «[mm_compat] Torches Become Sunlight: …», status до
//   «Natural mega-structure plan: 1/1 completed».
var TE = { wave: 0, i: 0, list: null }
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  if (T < 200) return
  if (TE.list == null) {
    TE.list = []
    for (var dx = -26; dx <= 26; dx++) for (var dz = -26; dz <= 26; dz++) TE.list.push([-38 + dx, 228 + dz])
    TE.CS = Java.loadClass('net.minecraft.world.level.chunk.ChunkStatus')
  }
  if (TE.wave >= 6) return
  if (TE.i == 0 && (T - 200) % 1200 != 0) return
  var level = server.overworld()
  for (var k = 0; k < 60 && TE.i < TE.list.length; k++, TE.i++) {
    var p = TE.list[(TE.i * 7919) % TE.list.length]
    try { level.getChunk(p[0], p[1], TE.CS.STRUCTURE_STARTS, true) } catch (e) { console.info('TBSEDGE error ' + e) }
  }
  if (TE.i >= TE.list.length) { TE.wave++; TE.i = 0; console.info('TBSEDGE wave ' + TE.wave + ' done, chunks=' + level.getChunkSource().getLoadedChunksCount()) }
})
