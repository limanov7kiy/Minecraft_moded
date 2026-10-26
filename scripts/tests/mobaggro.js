// Тест агрессии мобов (поток 35: «Крейки/Пугало/Большая Летучая Мышь не бьют человека в выживании»).
// Для каждого моба из пространств имён NS: призывается рядом с фейковым игроком Forge (survival, сложность normal, ночь),
// через 400 тиков (20 с) фиксируется: выбрал ли моб игрока целью (getTarget) и сколько здоровья потерял игрок.
// Вывод: MOBAGGRO <id> <ATTACK|TARGET|IDLE|NOMOB> dmg=<урон> target=<да/нет> cat=<MobCategory>. Каждая «ячейка» — своя площадка.
// Фейковый игрок Forge неуязвим (dmg всегда 0), поэтому признак агрессии — TARGET (моб выбрал игрока целью). IDLE не всегда «не агрессивен»:
// водные/дневные/биомные моб-условия, «peaceful: true» у Lycanites. Игроки не удаляются между партиями (удалённого заново не добавить).
// Запуск: EXTRA_SERVER_SCRIPTS=<папка с этим файлом> scripts/test-server.sh <папка вне репо>; TEST_COMMANDS с «#sleep» ~ 400 с.
// Переменные: NS_FILTER (через запятую) — меняйте в константе ниже; DAY=true — тест днём.
var NS = ['lycanitesmobs', 'mod_lavacow', 'alexsmobs', 'alexscaves', 'spore', 'chaospersists']
var DAY = false
// ONLY — явный список типов (для контрольного прогона); пусто = все мобы из NS
var ONLY = []
var BATCH = 36, SPACING = 32, WAIT = 400
var phase = null
ServerEvents.tick(event => {
  var server = event.server, t = server.tickCount
  var level = server.getLevel('minecraft:overworld')
  var FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  var Fk = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  var UU = Java.loadClass('java.util.UUID')
  var GP = Java.loadClass('com.mojang.authlib.GameProfile')
  var Mob = Java.loadClass('net.minecraft.world.entity.Mob')
  var GT = Java.loadClass('net.minecraft.world.level.GameType')
  if (t == 200) {
    server.runCommandSilent('difficulty normal')
    server.runCommandSilent('gamerule doDaylightCycle false')
    server.runCommandSilent('gamerule doMobSpawning false')
    server.runCommandSilent('time set ' + (DAY ? 6000 : 18000))
    var ids = []
    FR.ENTITY_TYPES.getKeys().forEach(k => { if (NS.indexOf(String(k.getNamespace())) >= 0 && String(FR.ENTITY_TYPES.getValue(k).getCategory()) != 'MISC') ids.push(String(k)) })
    if (ONLY.length) { ids.length = 0; ONLY.forEach(i => ids.push(i)) }
    ids.sort()
    phase = { ids: ids, i: 0, cells: [], next: 210, added: {} }
    console.info('MOBAGGRO total ' + ids.length)
    return
  }
  if (!phase || t < phase.next) return
  if (phase.cells.length) {
    // замер предыдущей партии
    phase.cells.forEach(c => {
      var dmg = c.fake.getMaxHealth() - c.fake.getHealth()
      var mob = c.mob
      var tgt = mob != null && mob.isAlive() && mob.getTarget() != null && String(mob.getTarget().getName().getString()).indexOf('mobaggro') == 0
      var res = mob == null ? 'NOMOB' : (dmg > 0 ? 'ATTACK' : (tgt ? 'TARGET' : 'IDLE'))
      console.info('MOBAGGRO ' + c.id + ' ' + res + ' dmg=' + dmg + ' target=' + (tgt ? 'да' : 'нет') + ' cat=' + c.cat + ' alive=' + (mob != null && mob.isAlive()))
      if (mob != null) mob.discard()
    })
    phase.cells = []
  }
  if (phase.i >= phase.ids.length) { console.info('MOBAGGRO done'); phase = null; return }
  for (var n = 0; n < BATCH && phase.i < phase.ids.length; n++, phase.i++) {
    var id = phase.ids[phase.i]
    var type = FR.ENTITY_TYPES.getValue(new (Java.loadClass('net.minecraft.resources.ResourceLocation'))(id))
    var x = (n % 6) * SPACING, z = Math.floor(n / 6) * SPACING + 1000
    server.runCommandSilent('forceload add ' + (x - 8) + ' ' + (z - 8) + ' ' + (x + 8) + ' ' + (z + 8))
    server.runCommandSilent('fill ' + (x - 12) + ' 199 ' + (z - 12) + ' ' + (x + 12) + ' 199 ' + (z + 12) + ' minecraft:stone')
    server.runCommandSilent('fill ' + (x - 12) + ' 200 ' + (z - 12) + ' ' + (x + 12) + ' 215 ' + (z + 12) + ' minecraft:air')
    var fake = Fk.get(level, new GP(UU.fromString('00000000-0000-0000-0000-00000000a0' + (10 + n)), 'mobaggro' + n))
    fake.setGameMode(GT.SURVIVAL)
    fake.setPos(x + 0.5, 200, z + 0.5)
    fake.setHealth(fake.getMaxHealth())
    fake.removeAllEffects()
    if (!phase.added[n]) { level.addFreshEntity(fake); phase.added[n] = true }   // удалённого игрока заново добавить нельзя — остаются в мире
    var mob = null, cat = '?'
    try {
      cat = String(type.getCategory())
      var e = type.create(level)
      if (e != null && (e instanceof Mob)) {
        e.setPos(x + 6.5, 200, z + 0.5)
        e.setPersistenceRequired()
        if (e.finalizeSpawn) e.finalizeSpawn(level, level.getCurrentDifficultyAt(e.blockPosition()), Java.loadClass('net.minecraft.world.entity.MobSpawnType').COMMAND, null, null)
        level.addFreshEntity(e)
        mob = e
      } else if (e != null) { cat = cat + '/nonmob'; e.discard() }
    } catch (err) { cat = cat + '/err:' + err }
    phase.cells.push({ id: id, fake: fake, mob: mob, cat: cat })
  }
  phase.next = t + WAIT
})
