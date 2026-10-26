// Кто держит чанки загруженными (поток 67: без игроков в верхнем мире ~700–1000 чанков). Раз в 600 тиков по каждому измерению
// с загруженными чанками: число тикетов DistanceManager по типам (TicketType.toString = имя типа, у модов — свой тип или forced/unknown/portal…)
// и минимальный уровень тикета. Строки: TICKETS <измерение> loaded=<чанков> <тип>=<число тикетов>(<мин. уровень>) ...
// Пример (поток 67): без игроков в верхнем мире ~700 чанков держат TorchesBecomeSunlight_megastructure_preload/light (мегаструктура у спавна, В67 «а» — так и оставлено).
// Запуск: EXTRA_SERVER_SCRIPTS=<папка с этим файлом> scripts/test-server.sh <папка вне репо>
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  if (T < 200 || T % 600 != 0) return
  server.getAllLevels().forEach(lv => {
    var loaded = lv.getChunkSource().getLoadedChunksCount()
    if (loaded == 0) return
    try {
      var dm = lv.getChunkSource().chunkMap.getDistanceManager()
      var f = null, c = dm.getClass()
      while (c && !f) { c.getDeclaredFields().forEach(x => { if (!f && String(x.getType().getName()).indexOf('Long2ObjectOpenHashMap') >= 0) f = x }); c = c.getSuperclass() }
      f.setAccessible(true)
      var tickets = f.get(dm), types = {}
      // Rhino оборачивает Map в NativeJavaMap (любое свойство = get по ключу), поэтому метод — через рефлексию по типу поля
      var it = f.getType().getMethod('long2ObjectEntrySet').invoke(tickets).iterator()
      while (it.hasNext()) {
        var en = it.next(), it2 = en.getValue().iterator()
        while (it2.hasNext()) {
          var t = it2.next(), k = String(t.getType()), o = types[k] || (types[k] = { n: 0, lvl: 99 })
          o.n++; o.lvl = Math.min(o.lvl, t.getTicketLevel())
        }
      }
      var parts = []
      for (var k in types) parts.push(k + '=' + types[k].n + '(' + types[k].lvl + ')')
      console.info('TICKETS ' + lv.dimension + ' loaded=' + loaded + ' ' + parts.join(' '))
    } catch (e) { console.info('TICKETS ' + lv.dimension + ' loaded=' + loaded + ' FAIL ' + e) }
  })
})
