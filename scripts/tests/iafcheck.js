// Ice and Fire: здоровье/урон взрослых драконов (у созданного «с нуля» дракона — стадия 1, поэтому bossdump.js показывает 20).
// Призываем драконов с большим AgeTicks в небе над forceload-площадкой, читаем атрибуты. Вывод: IAFCHECK <id> stage=… hp=… dmg=…
ServerEvents.tick(event => {
  var server = event.server
  if (server.tickCount != 200) return
  var level = server.getLevel('minecraft:overworld')
  server.runCommandSilent('forceload add -16 -16 16 16')
  var A = Java.loadClass('net.minecraft.world.entity.ai.attributes.Attributes')
  var Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  var ids = ['fire_dragon', 'ice_dragon', 'lightning_dragon']
  ids.forEach(function (id) {
    server.runCommandSilent('summon iceandfire:' + id + ' 0.5 200 0.5 {NoAI:1b,PersistenceRequired:1b,AgeTicks:3000000}')
  })
  var found = level.getEntitiesOfClass(Entity, new AABB.of(-8, 190, -8, 8, 230, 8))
  found.forEach(function (e) {
    var t = String(e.getType())
    if (t.indexOf('iceandfire:') != 0) return
    var hp = e.getAttribute(A.MAX_HEALTH).getValue(), dmg = e.getAttribute(A.ATTACK_DAMAGE).getValue()
    var armor = e.getAttribute(A.ARMOR).getValue()
    console.info('IAFCHECK ' + t + ' hp=' + hp + ' dmg=' + dmg + ' armor=' + armor + ' h=' + e.getBbHeight() + ' w=' + e.getBbWidth() + ' nbt=' + String(e.nbt).substring(0, 120))
    e.discard()
  })
  console.info('IAFCHECK_DONE ' + found.size())
})
