// Поток 35: «игрок не получает урон». Пропускаем события урона через EVENT_BUS для новорождённого ServerPlayer (без соединения, не в мире)
// и смотрим, кто их отменяет или режет: LivingAttackEvent / LivingHurtEvent / LivingDamageEvent (generic, fall, мобовый удар зомби).
// Дополнительно: эффекты, атрибуты с модификаторами, броня. Вывод: HURTCHECK ...
ServerEvents.tick(event => {
  var server = event.server
  if (server.tickCount != 300) return   // первый тик после Done ≈2 мин — sleep ≥ 240
  var level = server.getLevel('minecraft:overworld')
  var UU = Java.loadClass('java.util.UUID')
  var GP = Java.loadClass('com.mojang.authlib.GameProfile')
  var SPL = Java.loadClass('net.minecraft.server.level.ServerPlayer')
  var MF = Java.loadClass('net.minecraftforge.common.MinecraftForge')
  var LAE = Java.loadClass('net.minecraftforge.event.entity.living.LivingAttackEvent')
  var LHE = Java.loadClass('net.minecraftforge.event.entity.living.LivingHurtEvent')
  var LDE = Java.loadClass('net.minecraftforge.event.entity.living.LivingDamageEvent')
  server.runCommandSilent('difficulty hard')
  var p = new SPL(server, level, new GP(UU.fromString('00000000-0000-0000-0000-00000000c001'), 'hurtcheck'))
  p.setPos(0.5, 200, 0.5)
  var zombie = Java.loadClass('net.minecraft.world.entity.EntityType').ZOMBIE.create(level)
  var DS = level.damageSources()
  var sources = { generic: DS.generic(), fall: DS.fall(), zombie: DS.mobAttack(zombie), magic: DS.magic(), onFire: DS.onFire() }
  var post = (label, ev) => {
    try { return MF.EVENT_BUS.post(ev) } catch (err) {
      var j = err.javaException ? err.javaException : err
      var st = j.getStackTrace ? Array.from(j.getStackTrace()).slice(0, 8).join(' | ') : ''
      console.info('HURTCHECK ' + label + ' EXCEPTION ' + err + ' ' + st)
      return null
    }
  }
  Object.keys(sources).forEach(k => {
    var src = sources[k]
    var a = new LAE(p, src, 5.0), h = new LHE(p, src, 5.0), d = new LDE(p, src, 5.0)
    var ca = post(k + '/attack', a), ch = post(k + '/hurt', h), cd = post(k + '/damage', d)
    console.info('HURTCHECK ' + k + ' attack.canceled=' + ca + ' hurt.canceled=' + ch + ' hurt.amount=' + h.getAmount() + ' damage.canceled=' + cd + ' damage.amount=' + d.getAmount())
  })
  console.info('HURTCHECK effects=' + p.getActiveEffects() + ' armor=' + p.getArmorValue() + ' absorption=' + p.getAbsorptionAmount() + ' invulnerable=' + p.isInvulnerable() + ' abilitiesInvulnerable=' + p.getAbilities().invulnerable)
  var AttrReg = Java.loadClass('net.minecraftforge.registries.ForgeRegistries').ATTRIBUTES
  AttrReg.getValues().forEach(a => {
    var inst = p.getAttribute(a)
    if (inst != null && inst.getModifiers().size() > 0) console.info('HURTCHECK attr ' + AttrReg.getKey(a) + ' value=' + inst.getValue() + ' base=' + inst.getBaseValue() + ' mods=' + inst.getModifiers())
  })
})
