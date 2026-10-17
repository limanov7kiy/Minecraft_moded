// Замер нагрузки Spore + SRP System (поток 30, продолжение замера потока 22): вспышки заражения нарастающего масштаба на поверхности (без оберега).
// Фазы: base (фон, ничего не заспавнено) → x1 (1 очаг) → x3 (3 очага) → x6 (6 очагов); очаги накапливаются, фаза длится SL_PHASE тиков.
// В каждом очаге: заражённые (inf_human/villager/pillager), курганы, Гастгебер, Протомозг, коровы с мицелием, жители, фейковый игрок (его лечат раз в секунду).
// Вывод: «SLOAD phase=<фаза> …» — tickTimes сервера (мс, свои тайминги тика), wall-период между тиками, число сущностей Spore/SRP/всего, заражённые блоки;
// «SLOAD T0 <фаза> <epoch мс>» — границы фаз для разбора профиля JFR (scripts/jfr/JfrTop.java).
// Запуск — отдельный прогон, папка только с этим файлом; профиль: SERVER_JVM_EXTRA='-XX:StartFlightRecording=filename=<путь>.jfr,settings=profile'.
var SL_PHASE = 4800            // тиков на фазу (4 мин)
var SL_BASE = 2400             // фон (2 мин)
var SL = { last: 0, samples: [], wall: [], phases: [], phase: 'pre', fakes: [], centers: [], blocksBase: {} }
// 6 очагов 3x2 с шагом 160 блоков (чанки подгружаются заранее — иначе генерация попадёт в замер)
var SL_C = []
for (var i = 0; i < 3; i++) for (var j = 0; j < 2; j++) SL_C.push([-400 + 160 * i, -400 + 160 * j])
var SL_PLAN = [['base', 0], ['x1', 1], ['x3', 3], ['x6', 6]]

ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  var level = server.getLevel('minecraft:overworld')
  var BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  var Heightmap = Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types')
  var now = Date.now(), dt = SL.last ? now - SL.last : 0
  SL.last = now
  var H = (x, z) => level.getHeight(Heightmap.MOTION_BLOCKING_NO_LEAVES, x, z)
  var pct = (s, q) => s[Math.min(s.length - 1, Math.floor(s.length * q))]
  var stat = (a) => { if (!a.length) return 'n=0'; var s = a.slice().sort((p, q) => p - q)
    return 'avg=' + (a.reduce((p, q) => p + q, 0) / a.length).toFixed(1) + ' p95=' + pct(s, 0.95).toFixed(1) + ' p99=' + pct(s, 0.99).toFixed(1) + ' max=' + s[s.length - 1].toFixed(1) + ' (n=' + a.length + ')' }
  var isInfFx = (e) => { var r = false; e.getActiveEffects().forEach(i => { if (String(i.getEffect().getDescriptionId()).indexOf('srpsystem') >= 0 || String(i.getEffect().getDescriptionId()).indexOf('mycelium') >= 0) r = true }); return r }
  var count = () => { var sp = 0, srp = 0, all = 0, hostile = 0
    level.getAllEntities().forEach(e => { all++; if (String(e.getType()).indexOf('spore.') >= 0 || String(e.getType()).indexOf('spore:') >= 0) sp++
      else if (e.getActiveEffects && isInfFx(e)) srp++ })
    return 'spore=' + sp + ' fxInfected=' + srp + ' entities=' + all }
  var blocks = (n) => { var cnt = 0
    for (var k = 0; k < n; k++) { var c = SL_C[k]
      for (var x = c[0] - 40; x <= c[0] + 40; x++) for (var z = c[1] - 40; z <= c[1] + 40; z++) { var h = H(x, z)
        for (var y = h - 3; y <= h + 1; y++) { var b = String(level.getBlockState(new BlockPos(x, y, z)).block.id); if (b.indexOf('spore:') == 0) cnt++ } } }
    return cnt }

  if (T == 300) {
    for (var k = 0; k < SL_C.length; k++) { var c = SL_C[k]
      server.runCommandSilent('forceload add ' + (c[0] - 48) + ' ' + (c[1] - 48) + ' ' + (c[0] + 48) + ' ' + (c[1] + 48))
      for (var dx = -3; dx <= 3; dx++) for (var dz = -3; dz <= 3; dz++) level.getChunk((c[0] >> 4) + dx, (c[1] >> 4) + dz) }
    console.info('SLOAD chunks loaded at T=' + T)
  }
  // фейковые игроки: добавляем в мир как настоящих (попадают в level.players() → мобы их видят)
  if (T == 330) {
    try {
      var Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory'), GP = Java.loadClass('com.mojang.authlib.GameProfile'), UUID = Java.loadClass('java.util.UUID')
      for (var k = 0; k < SL_C.length; k++) { var c = SL_C[k]
        var fp = Fake.get(level, new GP(UUID.fromString('5a0e10ad-0000-4000-8000-00000000000' + k), 'sload' + k))
        fp.setPos(c[0] + 0.5, H(c[0], c[1]) + 1, c[1] + 0.5); level.addFreshEntity(fp); SL.fakes.push(fp) }
      console.info('SLOAD fakeplayers=' + SL.fakes.length + ' level.players=' + level.players().size())
    } catch (e) { console.info('SLOAD fakeplayers FAIL ' + e) }
  }
  if (T > 330 && T % 20 == 0) SL.fakes.forEach(fp => { try { fp.setHealth(fp.getMaxHealth()); fp.removeAllEffects(); fp.getFoodData().setFoodLevel(20) } catch (e) {} })

  // чистый замер: без естественного спавна и без посторонних мобов (иначе 6 игроков с мобкапами дают сотни мобов и ~160 мс/тик фона)
  if (T == 330) server.runCommandSilent('gamerule doMobSpawning false')
  if (T == 590) { var Mob = Java.loadClass('net.minecraft.world.entity.Mob'); var n0 = 0; level.getAllEntities().forEach(e => { if (e instanceof Mob) { e.discard(); n0++ } }); console.info('SLOAD cleared mobs=' + n0) }
  if (T < 600) return
  // номер фазы по тику
  var rel = T - 600, idx = rel < SL_BASE ? 0 : 1 + Math.floor((rel - SL_BASE) / SL_PHASE)
  if (idx >= SL_PLAN.length) {
    if (!SL.done) { SL.done = true; report(true); console.info('SLOAD DONE') }
    return
  }
  var ph = SL_PLAN[idx]
  if (ph[0] != SL.phase) {
    if (SL.phase != 'pre') report(true)
    SL.phase = ph[0]; SL.samples = []; SL.wall = []; SL.phaseStart = T
    console.info('SLOAD T0 ' + ph[0] + ' ' + Date.now() + ' T=' + T)
    // добавляем очаги до нужного числа
    for (var k = (SL.spawned || 0); k < ph[1]; k++) spawnCenter(k)
    SL.spawned = ph[1]
    if (ph[0] == 'base') SL.blocksBase = { n0: blocks(SL_C.length) }
  }
  // свой тайминг тика: время предыдущего тика из кольца tickTimes (нс); запасной вариант — wall-период
  var own = null
  try { var tt = server.tickTimes; if (tt) own = tt[(T - 1) % 100] / 1e6 } catch (e) {}
  if (own === null || own === undefined) try { own = server.getAverageTickTime() } catch (e) { own = null }
  if (own !== null && own !== undefined && T > SL.phaseStart + 2) SL.samples.push(Number(own))
  if (dt && T > SL.phaseStart + 2) SL.wall.push(dt)
  if ((T - SL.phaseStart) % 1200 == 0 && T > SL.phaseStart) report(false)

  function report(final) {
    var nb = SL.phase == 'base' ? 0 : SL.spawned
    console.info('SLOAD phase=' + SL.phase + (final ? ' FINAL' : ' t+' + ((T - SL.phaseStart) / 20) + 's') + ' mspt{' + stat(SL.samples) + '} wall{' + stat(SL.wall) + '} ' + count() + ' sporeBlocks=' + blocks(SL.spawned || 0))
  }
  function spawnCenter(k) {
    var c = SL_C[k]
    var rnd = (r) => Math.floor(Math.random() * 2 * r) - r
    var at = (id, n, r, nbt) => { for (var i = 0; i < n; i++) { var x = c[0] + rnd(r), z = c[1] + rnd(r); server.runCommandSilent('summon ' + id + ' ' + x + ' ' + (H(x, z) + 1) + ' ' + z + ' ' + (nbt || '{PersistenceRequired:1b}')) } }
    at('spore:inf_human', 20, 30); at('spore:inf_villager', 10, 30); at('spore:inf_pillager', 5, 30)
    at('spore:mound', 5, 25); at('spore:gastgaber', 2, 20); at('spore:proto', 1, 5)
    at('minecraft:cow', 20, 30); at('minecraft:villager', 10, 30)
    server.runCommandSilent('effect give @e[type=minecraft:cow,x=' + c[0] + ',y=0,z=' + c[1] + ',distance=..64] spore:mycelium_ef 1200 1')
    console.info('SLOAD outbreak #' + (k + 1) + ' at ' + c)
  }
})
