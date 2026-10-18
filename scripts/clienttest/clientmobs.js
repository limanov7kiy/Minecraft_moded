// Клиентская проверка рендера мобов (поток 30): кладётся в <папка клиента>/game/kubejs/server_scripts/ ДО запуска (не часть сборки).
// После входа в любой мир (встроенный сервер): режим творчества, день, ровная площадка, и по рядам — по одному мобу КАЖДОГО вида из пространств имён
// spore, srpsystem, chaospersists (без ИИ, неуязвимые), игрок телепортируется вдоль ряда, чтобы клиент отрисовал всех. Логи: CLIENTMOBS … (game/logs/latest.log).
var CM = { t0: 0, ids: null, row: 0, summoned: 0, failed: [] }
var CM_NS = ['spore', 'chaospersists', 'srpsystem']
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  var players = server.players
  if (!players.size()) return
  var p = players.get(0), level = p.level
  if (!CM.t0) { CM.t0 = T; return }
  var rel = T - CM.t0
  if (rel == 100) {
    server.runCommandSilent('gamemode creative ' + p.username); server.runCommandSilent('time set day'); server.runCommandSilent('gamerule doMobSpawning false')
    server.runCommandSilent('gamerule doDaylightCycle false'); server.runCommandSilent('weather clear')
    var FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries'), ids = []
    FR.ENTITY_TYPES.getKeys().forEach(k => { if (CM_NS.indexOf(String(k.getNamespace())) >= 0) ids.push(String(k)) })
    ids.sort(); CM.ids = ids; CM.base = [Math.floor(p.x), Math.floor(p.y) + 20, Math.floor(p.z)]
    console.info('CLIENTMOBS start: ' + ids.length + ' видов (' + CM_NS.join(',') + '), игрок в ' + CM.base + ' dim=' + level.dimension)
  }
  if (!CM.ids || rel < 140) return
  if ((rel - 140) % 60 != 0) return
  var per = 12, r = CM.row, n = CM.ids.length
  if (r * per >= n) { if (!CM.done) { CM.done = true; console.info('CLIENTMOBS DONE summoned=' + CM.summoned + ' failed=' + CM.failed.length + ' ' + CM.failed.join(',')) } return }
  var b = CM.base, z = b[2] + r * 8 + 8
  server.runCommandSilent('fill ' + (b[0] - 30) + ' ' + (b[1] - 1) + ' ' + (z - 6) + ' ' + (b[0] + 30) + ' ' + (b[1] - 1) + ' ' + (z + 6) + ' minecraft:smooth_stone')
  server.runCommandSilent('fill ' + (b[0] - 30) + ' ' + b[1] + ' ' + (z - 6) + ' ' + (b[0] + 30) + ' ' + (b[1] + 12) + ' ' + (z + 6) + ' minecraft:air')
  for (var i = 0; i < per && r * per + i < n; i++) {
    var id = CM.ids[r * per + i], x = b[0] - 26 + i * 4.5
    var res = server.runCommandSilent('summon ' + id + ' ' + x + ' ' + b[1] + ' ' + z + ' {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b,Silent:1b}')
    if (res > 0) CM.summoned++; else CM.failed.push(id)
  }
  server.runCommandSilent('tp ' + p.username + ' ' + b[0] + ' ' + (b[1] + 1) + ' ' + (z - 11) + ' 0 10')
  console.info('CLIENTMOBS row ' + r + ' summoned=' + CM.summoned + ' failed=' + CM.failed.length)
  CM.row++
})
