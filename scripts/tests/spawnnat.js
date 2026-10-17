// Тест спавна мобов (поток 30, жалоба «мобы не спавнятся ни яйцами, ни в мире»): внутри зоны Оберега и снаружи.
// Два настоящих (добавленных в level.players()) фейковых игрока на поверхности: A в зоне оберега (0,0), B снаружи (600,600).
// Проверки (строки SPAWNNAT … OK|FAIL): /summon (ваниль, мод), яйцо призыва фейковым игроком (ваниль, мод) — моб появился и жив через 10 с;
// естественный спавн ночью: через ~2 мин число мобов в 80 блоках вокруг игрока (все / ванильные / модовые) в зоне и снаружи.
// Запуск: EXTRA_SERVER_SCRIPTS=<папка только с этим файлом> TEST_COMMANDS=<«#sleep 600»> scripts/test-server.sh <папка вне репо>
var SN = { A: null, B: null, mid: {} }
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  var level = server.getLevel('minecraft:overworld')
  var Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  var Mob = Java.loadClass('net.minecraft.world.entity.Mob')
  var AABB = Java.loadClass('net.minecraft.world.phys.AABB')
  var Heightmap = Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types')
  var rep = (n, ok, info) => console.info('SPAWNNAT ' + n + ' ' + (ok ? 'OK' : 'FAIL') + ' ' + info)
  var H = (x, z) => level.getHeight(Heightmap.MOTION_BLOCKING_NO_LEAVES, x, z)
  var near = (p, r) => { var l = []; level.getEntitiesOfClass(Mob, new AABB(p.getX() - r, -64, p.getZ() - r, p.getX() + r, 320, p.getZ() + r)).forEach(e => l.push(e)); return l }
  var summary = (p) => { var all = near(p, 80), van = 0, mod = 0, types = {}
    all.forEach(e => { var t = String(e.getType()); if (t.indexOf('entity.minecraft.') == 0) van++; else mod++; types[t] = (types[t] || 0) + 1 })
    return 'total=' + all.length + ' vanilla=' + van + ' modded=' + mod + ' types=' + JSON.stringify(types) }

  if (T == 200) {
    server.runCommandSilent('difficulty normal'); server.runCommandSilent('gamerule doMobSpawning true'); server.runCommandSilent('gamerule doDaylightCycle false')
    server.runCommandSilent('time set 18000')
    var pts = { A: [0, 0], B: [600, 600] }
    for (var k in pts) { var c = pts[k]
      server.runCommandSilent('forceload add ' + (c[0] - 64) + ' ' + (c[1] - 64) + ' ' + (c[0] + 64) + ' ' + (c[1] + 64))
      for (var dx = -4; dx <= 4; dx++) for (var dz = -4; dz <= 4; dz++) level.getChunk((c[0] >> 4) + dx, (c[1] >> 4) + dz) }
    try {
      var Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory'), GP = Java.loadClass('com.mojang.authlib.GameProfile'), UUID = Java.loadClass('java.util.UUID')
      var mk = (name, id, c) => { var fp = Fake.get(level, new GP(UUID.fromString(id), name)); fp.setPos(c[0] + 0.5, H(c[0], c[1]), c[1] + 0.5); level.addFreshEntity(fp); return fp }
      SN.A = mk('snA', '5a0e10ad-0000-4000-8000-0000000000a1', pts.A); SN.B = mk('snB', '5a0e10ad-0000-4000-8000-0000000000b1', pts.B)
      console.info('SPAWNNAT players=' + level.players.size() + ' diff=' + level.getDifficulty() + ' A=' + SN.A.blockPosition() + ' B=' + SN.B.blockPosition())
    } catch (e) { console.info('SPAWNNAT fakeplayers FAIL ' + e); return }
    // оберег рядом с игроком A
    var ap = SN.A.blockPosition()
    server.runCommandSilent('setblock ' + ap.getX() + ' ' + (ap.getY() + 3) + ' ' + ap.getZ() + ' disaster_ward:ward')
    console.info('SPAWNNAT ward placed: ' + level.getBlockState(ap.above(3)).block.id)
  }

  // диагностика: создаём корову в разных точках мира и смотрим, жива ли через 1 и 40 тиков (причина удаления — getRemovalReason)
  if (T == 205) {
    var ETc = Java.loadClass('net.minecraft.world.entity.EntityType')
    SN.diag = []
    var pts2 = [[0, 30], [100, 100], [300, 300], [600, 600], [-600, -600], [1000, 0], [0, 1000]]
    pts2.forEach(p => { var x = p[0], z = p[1]
      server.runCommandSilent('forceload add ' + x + ' ' + z + ' ' + (x + 16) + ' ' + (z + 16))
      level.getChunk(x >> 4, z >> 4); var y = H(x, z)
      var cow = ETc.COW.create(level); cow.setPos(x + 0.5, y, z + 0.5); var added = level.addFreshEntity(cow)
      SN.diag.push({ p: p, y: y, cow: cow, added: added, biome: String(level.getBiome(cow.blockPosition()).unwrapKey().get().location()),
        tick: String(level.isPositionEntityTicking(cow.blockPosition())), loaded: String(level.isLoaded(cow.blockPosition())) }) })
  }

  if (T == 207) {   // кто отклоняет добавление сущности: вызываем слушателей EntityJoinLevelEvent по одному на корове в проблемной точке
    try {
      var MF = Java.loadClass('net.minecraftforge.common.MinecraftForge'), bus = MF.EVENT_BUS
      var f = bus.getClass().getDeclaredField('busID'); f.setAccessible(true); var id = f.get(bus)
      var ELH = Java.loadClass('net.minecraftforge.eventbus.api.EventListenerHelper')
      var EJ = Java.loadClass('net.minecraftforge.event.entity.EntityJoinLevelEvent')
      var ls = ELH.getListenerList(EJ).getListeners(id), ETj = Java.loadClass('net.minecraft.world.entity.EntityType')
      var tests = [[600, 600], [-600, -600], [100, 100]]
      tests.forEach(t => { var y = H(t[0], t[1]); var cow = ETj.COW.create(level); cow.setPos(t[0] + 3.5, y, t[1] + 3.5)
        var ev = new EJ(cow, level, false), culprit = null
        for (var i = 0; i < ls.length && !culprit; i++) { try { ls[i].invoke(ev) } catch (e) { }
          if (ev.isCanceled() || cow.isRemoved()) culprit = i + ' ' + ls[i] + ' canceled=' + ev.isCanceled() + ' removed=' + cow.isRemoved() }
        console.info('SPAWNNAT culprit at ' + t + ': ' + culprit + ' (слушателей ' + ls.length + ')') })
    } catch (e) { console.info('SPAWNNAT listeners FAIL ' + e) }
  }
  if (T == 206 || T == 260) SN.diag && SN.diag.forEach(d => console.info('SPAWNNAT diag t=' + T + ' at ' + d.p + ' y=' + d.y + ' biome=' + d.biome + ' added=' + d.added + ' entityTicking=' + d.tick + ' removed=' + d.cow.isRemoved() + ' reason=' + d.cow.getRemovalReason()))
  if (!SN.A) return
  if (T == 220) {
    var Items = Java.loadClass('net.minecraft.world.item.Items'), SE = Java.loadClass('net.minecraft.world.item.SpawnEggItem')
    var ET = Java.loadClass('net.minecraft.world.entity.EntityType'), FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
    var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
    var BlockPos = Java.loadClass('net.minecraft.core.BlockPos'), Vec3 = Java.loadClass('net.minecraft.world.phys.Vec3'), Direction = Java.loadClass('net.minecraft.core.Direction')
    var Hit = Java.loadClass('net.minecraft.world.phys.BlockHitResult'), Ctx = Java.loadClass('net.minecraft.world.item.context.UseOnContext'), Hand = Java.loadClass('net.minecraft.world.InteractionHand')
    var types = ['minecraft:sheep', 'minecraft:zombie', 'alexsmobs:crow', 'iceandfire:troll', 'lycanitesmobs:ent', 'chaospersists:ant']
    var who = { in: SN.A, out: SN.B }
    SN.eggs = []
    for (var w in who) { var fp = who[w], bp = fp.blockPosition()
      types.forEach((t, i) => {
        var et = FR.ENTITY_TYPES.getValue(new RL(t)); if (!et || String(FR.ENTITY_TYPES.getKey(et)) != t) { console.info('SPAWNNAT skip ' + t + ' (нет типа)'); return }
        var egg = SE.byId(et); if (!egg) { console.info('SPAWNNAT skip egg ' + t + ' (нет яйца)'); return }
        var x = bp.getX() + 6 + i * 3, z = bp.getZ() + 8, y = H(x, z)
        server.runCommandSilent('summon ' + t + ' ' + (x + 0.5) + ' ' + y + ' ' + (z - 12) + ' {PersistenceRequired:1b}')   // /summon
        var res = egg.useOn(new Ctx(level, fp, Hand.MAIN_HAND, egg.getDefaultInstance(), new Hit(new Vec3(x + 0.5, y - 1, z + 0.5), Direction.UP, new BlockPos(x, y - 1, z), false)))
        SN.eggs.push([w, t, String(res)]) })
    }
  }
  if (T == 220 + 240) {   // 12 с спустя (Persistence не задан у яичных — живут, пока рядом игрок)
    SN.eggs.forEach(e => { var fp = e[0] == 'in' ? SN.A : SN.B, n = 0, nm = 0
      near(fp.blockPosition(), 40).forEach(m => { if (String(m.getType()).replace('entity.', '').replace('.', ':') == e[1]) { n++ } })
      rep(e[0] + '_egg_or_summon_' + e[1], n >= 2, 'alive=' + n + ' (ждём 2: summon + яйцо) eggUseResult=' + e[2]) })
  }
  if (T == 500) {   // освобождаем зону: убираем все Mob вокруг игроков, чтобы считать только естественный спавн
    [SN.A, SN.B].forEach(p => near(p.blockPosition(), 80).forEach(m => m.discard()))
    SN.t0 = T; server.runCommandSilent('time set 18000')
    console.info('SPAWNNAT cleared; natural spawn window starts T=' + T)
  }
  if (SN.t0 && (T - SN.t0) % 1200 == 0 && T > SN.t0 && T <= SN.t0 + 3600) {
    server.runCommandSilent('time set 18000')
    console.info('SPAWNNAT natural t+' + ((T - SN.t0) / 20) + 's  IN-ward: ' + summary(SN.A.blockPosition()))
    console.info('SPAWNNAT natural t+' + ((T - SN.t0) / 20) + 's OUTSIDE : ' + summary(SN.B.blockPosition()))
  }
  if (T == 500 + 3600 + 5) console.info('SPAWNNAT DONE')
})
