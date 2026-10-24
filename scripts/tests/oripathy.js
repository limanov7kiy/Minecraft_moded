// Поток 62: орипатия Bob's Originium Dream у каждой расы слоя origins:origin (и без расы).
// Фейковый ServerPlayer (как racethought.js), раса через IOriginContainer, затем N ударов уроном bobsoriginiumdream:oridamage
// (как от кристаллов/бури). Вывод: ORIPATHY <раса> hurt=<сколько ударов прошло> orillness=<индекс> oridisease=<тип 0-3>
// protect=<orillnessprotect> complete=<oricompletelyprotect> — заражение (oridisease) у Bob's идёт только при complete != 0;
// затем 120 тиков игрока (doTick) и те же значения — сброс силой arks:mm_ori_immune / arks:human/ori раз в 100 тиков.
ServerEvents.tick(event => {
  var server = event.server
  if (server.tickCount != 300) return
  var log = s => console.info('ORIPATHY ' + s)
  var level = server.getLevel('minecraft:overworld')
  var UU = Java.loadClass('java.util.UUID')
  var GP = Java.loadClass('com.mojang.authlib.GameProfile')
  var SPL = Java.loadClass('net.minecraft.server.level.ServerPlayer')
  var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  var RK = Java.loadClass('net.minecraft.resources.ResourceKey')
  var Regs = Java.loadClass('net.minecraft.core.registries.Registries')
  var DS = Java.loadClass('net.minecraft.world.damagesource.DamageSource')
  var REG = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  var OA = Java.loadClass('io.github.edwinmindcraft.origins.api.OriginsAPI')
  var Cont = Java.loadClass('io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer')
  var Reg = Java.loadClass('io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries')
  var attr = n => REG.ATTRIBUTES.getValue(new RL('bobsoriginiumdream', n))
  var A = { ill: attr('orillness'), dis: attr('oridisease'), prot: attr('orillnessprotect'), comp: attr('oricompletelyprotect') }
  var v = (p, a) => { var i = p.getAttribute(a); return i == null ? 'n/a' : i.getValue() }
  var conn = p => { var C = Java.loadClass('net.minecraft.network.Connection'), PF = Java.loadClass('net.minecraft.network.protocol.PacketFlow'), G = Java.loadClass('net.minecraft.server.network.ServerGamePacketListenerImpl'); new G(server, new C(PF.SERVERBOUND), p); return p }
  var layer = RK.create(Reg.LAYERS_REGISTRY, new RL('origins:origin'))
  var setO = (p, o) => { var c = Cont.get(p).resolve().get(); var k = RK.create(Reg.ORIGINS_REGISTRY, new RL(o)); c.setOrigin(layer, k); c.onChosen(k, false); c.validateSynchronization(); return '' + c.getOrigin(layer).location() }
  var dmg = () => new DS(server.registryAccess().registryOrThrow(Regs.DAMAGE_TYPE).getHolderOrThrow(RK.create(Regs.DAMAGE_TYPE, new RL('bobsoriginiumdream:oridamage'))))
  log('gamerule oriillness=' + server.getGameRules().getRule(Java.loadClass('bobsoriginiumdream.init.BobsoriginiumdreamModGameRules').ORIILLNESS).get())
  var races = ['plain']
  OA.getLayersRegistry(server).get(new RL('origins:origin')).origins().forEach(h => races.push('' + h.unwrapKey().get().location()))
  races = races.filter((r, i) => races.indexOf(r) == i)
  var HITS = 40
  races.forEach((o, i) => {
    try {
      var hex = ('000' + i.toString(16)).slice(-4)
      var p = new SPL(server, level, new GP(UU.fromString('00000000-0000-0000-0000-00000062' + hex), 'ori' + i))
      p.setPos(0.5, 200, 0.5); conn(p)
      var got = o == 'plain' ? 'plain' : setO(p, o)
      // у нового ServerPlayer 60 тиков неуязвимости после появления (spawnInvulnerableTime) — отжидаем
      for (var t0 = 0; t0 < 70; t0++) { p.setHealth(p.getMaxHealth()); p.doTick() }
      var hurt = 0
      for (var k = 0; k < HITS; k++) {
        p.setHealth(p.getMaxHealth()); p.invulnerableTime = 0
        var h0 = p.getHealth()
        p.attack(dmg(), 0.5)
        if (p.getHealth() < h0) hurt++
      }
      p.setHealth(p.getMaxHealth())
      var hit = 'orillness=' + v(p, A.ill) + ' oridisease=' + v(p, A.dis)
      // 120 тиков игрока: силы с таймером (сброс орипатии у иммунных рас раз в 100 тиков)
      var ticked = 'ok'
      try { for (var t = 0; t < 120; t++) { p.setHealth(p.getMaxHealth()); p.doTick() } } catch (err) { ticked = 'tick-error=' + err }
      log(o + ' got=' + got + ' hurt=' + hurt + '/' + HITS + ' ' + hit + ' | after120t(' + ticked + ') orillness=' + v(p, A.ill) + ' oridisease=' + v(p, A.dis) + ' protect=' + v(p, A.prot) + ' complete=' + v(p, A.comp))
    } catch (err) { log(o + ' error=' + err) }
  })
  log('done ' + races.length)
})
