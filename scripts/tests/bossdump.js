// Выгрузка живых существ сборки для таблицы боссов (scripts/boss_table.py). На 100-м тике создаёт (не добавляя в мир)
// каждое существо из реестра и пишет строку BOSSDUMP {json}: id, класс и предки, здоровье/урон/броня, размер хитбокса,
// поля-«боссбары» в классе. Числа — после всех конфигов и патчей сборки (то есть реальные, а не из памяти).
ServerEvents.tick(event => {
  var server = event.server
  if (server.tickCount != 100) return
  var FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  var Attributes = Java.loadClass('net.minecraft.world.entity.ai.attributes.Attributes')
  var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  var level = server.getLevel('minecraft:overworld')
  var attr = (e, a) => { try { var i = e.getAttribute(a); return i == null ? null : Math.round(i.getValue() * 100) / 100 } catch (x) { return null } }
  var keys = FR.ENTITY_TYPES.getKeys().toArray().map(k => String(k)).sort()
  var n = 0
  keys.forEach(id => {
    try {
      var et = FR.ENTITY_TYPES.getValue(RL.tryParse(id))
      var e = et.create(level)
      if (e == null) return
      var row = { id: id, cls: String(e.getClass().getName()), living: false }
      var chain = []; var bars = []
      for (var c = e.getClass(); c != null && String(c.getName()) != 'java.lang.Object'; c = c.getSuperclass()) {
        chain.push(String(c.getName()))
        try { c.getDeclaredFields().forEach(f => { if (String(f.getType().getName()).includes('BossEvent') || String(f.getType().getName()).includes('BossInfo')) bars.push(String(f.getType().getName())) }) } catch (x) {}
      }
      row.living = chain.indexOf('net.minecraft.world.entity.LivingEntity') >= 0
      row.chain = chain.slice(0, 8); row.bar = bars.length > 0
      row.w = Math.round(et.getWidth() * 100) / 100; row.h = Math.round(et.getHeight() * 100) / 100
      if (row.living) {
        row.hp = attr(e, Attributes.MAX_HEALTH); row.dmg = attr(e, Attributes.ATTACK_DAMAGE)
        row.armor = attr(e, Attributes.ARMOR); row.kbres = attr(e, Attributes.KNOCKBACK_RESISTANCE)
      }
      console.info('BOSSDUMP ' + JSON.stringify(row)); n++
    } catch (x) { console.info('BOSSDUMP_ERR ' + id + ' ' + x) }
  })
  console.info('BOSSDUMP_DONE ' + n + ' of ' + keys.length)
})
