// Регрессионный тест спавна мобов (поток 9; поводом стала жалоба «перестали спавниться мобы» и подозрение на общие миксины
// коллизий Cerbon's API). Вывод строк SPAWNCHECK <проверка> OK|FAIL <подробности>:
//   1. /summon ванильного (cow, zombie) и модового (iceandfire:troll, alexsmobs:crow) моба — существует после 20 тиков;
//   2. яйцо призыва, применённое фейковым игроком Forge к блоку — моб появляется;
//   3. естественный спавн: проверка места для моба SpawnPlacements.checkSpawnRules + level.noCollision (правила места и коллизии AABB — то, что ломают миксины коллизий) на тёмной (зомби, тролль IaF) и освещённой (корова) площадке — доля допустимых позиций;
// Запуск: EXTRA_SERVER_SCRIPTS=scripts/tests scripts/test-server.sh <папка вне репо> (нужен только этот файл — копируйте в отдельную папку).
ServerEvents.tick(event => {
  var server = event.server
  if (server.tickCount != 200 && server.tickCount != 260) return
  var level = server.getLevel('minecraft:overworld')
  var report = function (name, ok, info) { console.info('SPAWNCHECK ' + name + ' ' + (ok ? 'OK' : 'FAIL') + ' ' + info) }
  var Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  var AABB = Java.loadClass('net.minecraft.world.phys.AABB')
  var count = function (type) {
    var n = 0
    level.getEntitiesOfClass(Entity, new AABB(-80, 60, -80, 80, 320, 80)).forEach(function (e) { if (String(e.getType()) == type) n++ })
    return n
  }
  if (server.tickCount == 200) {
    server.runCommandSilent('forceload add -32 -32 32 32')
    server.runCommandSilent('fill -12 199 -12 12 199 12 minecraft:stone')
    server.runCommandSilent('fill -12 200 -12 12 210 12 minecraft:air')
    var list = ['minecraft:cow', 'minecraft:zombie', 'iceandfire:troll', 'alexsmobs:crow']
    list.forEach(function (t, i) { server.runCommandSilent('summon ' + t + ' ' + (i * 3 - 4) + ' 200 0 {PersistenceRequired:1b,NoAI:1b}') })
    // яйцо: фейковый игрок кликает по блоку
    var Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
    var fake = Fake.getMinecraft(level)
    var Items = Java.loadClass('net.minecraft.world.item.Items')
    var BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
    var Vec3 = Java.loadClass('net.minecraft.world.phys.Vec3')
    var Direction = Java.loadClass('net.minecraft.core.Direction')
    var Hit = Java.loadClass('net.minecraft.world.phys.BlockHitResult')
    var Ctx = Java.loadClass('net.minecraft.world.item.context.UseOnContext')
    var Hand = Java.loadClass('net.minecraft.world.InteractionHand')
    var stack = Items.SHEEP_SPAWN_EGG.getDefaultInstance()
    fake.setPos(0.5, 200, 6.5)
    var res = Items.SHEEP_SPAWN_EGG.useOn(new Ctx(level, fake, Hand.MAIN_HAND, stack, new Hit(new Vec3(8.5, 200, 8.5), Direction.UP, new BlockPos(8, 199, 8), false)))
    console.info('SPAWNCHECK egg_use_result ' + res)
    // площадки для естественного спавна: тёмная x100..124 и освещённая x0..24 (трава + блоки света)
    server.runCommandSilent('forceload add 96 96 128 128')
    server.runCommandSilent('fill 98 199 98 126 199 126 minecraft:grass_block')
    server.runCommandSilent('fill 98 200 98 126 215 126 minecraft:air')
    server.runCommandSilent('fill -2 199 -2 26 199 26 minecraft:grass_block')
    for (var lx = 0; lx <= 24; lx += 4) for (var lz = 0; lz <= 24; lz += 4) server.runCommandSilent('setblock ' + lx + ' 203 ' + lz + ' minecraft:light[level=15]')
    server.runCommandSilent('time set 18000')
  }
  if (server.tickCount == 260) {
    ['minecraft:cow', 'minecraft:zombie', 'iceandfire:troll', 'alexsmobs:crow'].forEach(function (t) { report('summon_' + t, count(t) >= 1, 'count=' + count(t)) })
    report('spawn_egg_sheep', count('minecraft:sheep') >= 1, 'sheep=' + count('minecraft:sheep'))
    // естественный спавн. Настоящий NaturalSpawner на сервере без игроков ничего не делает (ему нужен ближайший игрок), поэтому
    // вызываем его проверку места для моба напрямую: NaturalSpawner.isValidSpawnPostitionForType (правила спавна, коллизии AABB,
    // ошибка там = «мобы не спавнятся»). Площадки: тёмная (зомби, ночью) и освещённая (корова, блоки света).
    var SP = Java.loadClass('net.minecraft.world.entity.SpawnPlacements')
    var MST = Java.loadClass('net.minecraft.world.entity.MobSpawnType')
    var MC = Java.loadClass('net.minecraft.world.entity.MobCategory')
    var ET = Java.loadClass('net.minecraft.world.entity.EntityType')
    var MPos = Java.loadClass('net.minecraft.core.BlockPos$MutableBlockPos')
    var rate = function (name, cat, type, x0, z0, tries) {
      var ok = 0, errs = 0
      for (var i = 0; i < tries; i++) {
        var pos = new MPos(x0 + Math.floor(Math.random() * 20), 200, z0 + Math.floor(Math.random() * 20))
        try { if (SP.checkSpawnRules(type, level, MST.NATURAL, pos, level.getRandom()) && level.noCollision(type.getAABB(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5))) ok++ } catch (x) { errs++; if (errs == 1) console.info('SPAWNCHECK first_error ' + x) }
      }
      report('natural_' + name, ok >= tries / 10, 'valid=' + ok + '/' + tries + ' errors=' + errs)
    }
    rate('zombie_dark', MC.MONSTER, ET.ZOMBIE, 100, 100, 200)
    rate('cow_lit', MC.CREATURE, ET.COW, 0, 0, 200)
    rate('troll_dark', MC.MONSTER, Java.loadClass('net.minecraftforge.registries.ForgeRegistries').ENTITY_TYPES.getValue(Java.loadClass('net.minecraft.resources.ResourceLocation').tryParse('iceandfire:troll')), 100, 100, 200)
  }
})
