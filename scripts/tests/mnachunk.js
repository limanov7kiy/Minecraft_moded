// Тест фриза Mana and Artifice (mm-compat MnaChunkTickMixin): MnA в onWorldTick обходит чанки 11×11 вокруг
// каждого игрока и для «загруженных» (есть тикет) вызывает блокирующий getChunk. Ставим фейкового игрока Forge
// в мир далеко от спавна и одновременно region-тикет вокруг него — чанки с тикетом, но ещё не сгенерированные
// (forceload не годится: команда сама ждёт загрузку чанков). Без исправления серверный поток ждёт генерацию прямо в тике.
// Вывод: MNACHUNK OK|FAIL maxTick=<мс> фон=<макс. тик за 60 с до теста> ticks=<n> full=<готовых чанков из 121>
let mna = { start: -1, last: 0, max: 0, n: 0, fake: null, first: 0, pre: 0 }
ServerEvents.tick(event => {
  const server = event.server
  const level = server.getLevel('minecraft:overworld')
  const now = Date.now()
  // первые минуты после Done тики и так медленные (спавн, Distant Horizons) — ждём 90 с и меряем фон
  if (mna.first == 0) mna.first = now
  if (mna.start < 0 && mna.fake == null && mna.n == 0) {
    if (now - mna.first > 30000 && mna.last > 0 && now - mna.last > mna.pre) mna.pre = now - mna.last
    mna.last = now
  }
  if (mna.start < 0 && mna.n == 0 && now - mna.first > 90000) {
    mna.start = server.tickCount
    const Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
    mna.fake = Fake.getMinecraft(level)
    mna.fake.setPos(320008.5, 120, 320008.5)   // чанк 20000, 20000
    mna.pos = new (Java.loadClass('net.minecraft.world.level.ChunkPos'))(20000, 20000)
    mna.ticket = Java.loadClass('net.minecraft.server.level.TicketType').FORCED
    level.getChunkSource().addRegionTicket(mna.ticket, mna.pos, 6, mna.pos)   // уровень ≤ 33 (FULL) в радиусе 6
    // level.players в KubeJS — копия списка; входим в мир как игрок (как телепорт в новое место)
    level.addNewPlayer(mna.fake)
    mna.last = Date.now()
    console.info(`MNACHUNK start players=${level.players.size()}`)
    return
  }
  if (mna.start < 0 || mna.fake == null) return
  const dt = now - mna.last
  mna.last = now; mna.n++
  if (dt > mna.max) mna.max = dt
  if (server.tickCount < mna.start + 100) return
  level.removePlayerImmediately(mna.fake, Java.loadClass('net.minecraft.world.entity.Entity$RemovalReason').DISCARDED)
  let full = 0
  for (let x = 19995; x <= 20005; x++) for (let z = 19995; z <= 20005; z++) if (level.getChunkSource().getChunkNow(x, z) != null) full++
  level.getChunkSource().removeRegionTicket(mna.ticket, mna.pos, 6, mna.pos)
  console.info(`MNACHUNK ${mna.max < 2000 ? 'OK' : 'FAIL'} maxTick=${mna.max} фон=${mna.pre} ticks=${mna.n} full=${full}`)
  mna.fake = null
})
