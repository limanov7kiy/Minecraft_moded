// Поток 58: сохраняется ли раса Origins (и силы) у игрока: выбор → смерть → возрождение → другое измерение → выход,
// затем (второй запуск того же сервера) загрузка игрока после перезапуска. Фейковый ServerPlayer, как racethought.js.
// Первый запуск: нет playerdata → ставим расу Человек + класс + ветку + искусство, проверяем, затем PlayerList.remove (сохраняет).
// Второй запуск: PlayerList.load читает playerdata → проверяем. Вывод: ORIGINCHECK <шаг> ...
ServerEvents.tick(event => {
  var server = event.server
  if (server.tickCount != 300) return
  var log = s => console.info('ORIGINCHECK ' + s)
  var UU = Java.loadClass('java.util.UUID')
  var GP = Java.loadClass('com.mojang.authlib.GameProfile')
  var SPL = Java.loadClass('net.minecraft.server.level.ServerPlayer')
  var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  var RK = Java.loadClass('net.minecraft.resources.ResourceKey')
  var Regs = Java.loadClass('net.minecraft.core.registries.Registries')
  var DS = Java.loadClass('net.minecraft.world.damagesource.DamageSource')
  var Cont = Java.loadClass('io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer')
  var Reg = Java.loadClass('io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries')
  var PC = Java.loadClass('io.github.edwinmindcraft.apoli.api.component.IPowerContainer')
  var Inv = Java.loadClass('io.github.edwinmindcraft.apoli.common.power.InvulnerablePower')
  var TLS = Java.loadClass('com.zhixu.themassestravels.content.empathy.common.thought.ThoughtLoadService')
  var level = server.getLevel('minecraft:overworld')
  var plist = server.getPlayerList()
  // Xaero's lib заводит свои данные игрока при входе; у фейкового их нет, и respawn падает NPE — заводим вручную
  var xaero = p => { try { Java.loadClass('xaero.lib.common.player.ServerPlayerData').get(p) } catch (err) { console.info('ORIGINCHECK xaero-skip ' + err) } }
  var conn = p => { var C = Java.loadClass('net.minecraft.network.Connection'), PF = Java.loadClass('net.minecraft.network.protocol.PacketFlow'), G = Java.loadClass('net.minecraft.server.network.ServerGamePacketListenerImpl'); new G(server, new C(PF.SERVERBOUND), p); xaero(p); return p }
  var choice = { 'origins:origin': 'arks:human', 'origins:arkclass_layers': 'arkskills:guard', 'origins:branch_layers': 'arkbranches:guard_lord', 'origins:arkarts': 'arkarts:fire' }
  var lkey = l => RK.create(Reg.LAYERS_REGISTRY, new RL(l))
  var dmg = id => new DS(server.registryAccess().registryOrThrow(Regs.DAMAGE_TYPE).getHolderOrThrow(RK.create(Regs.DAMAGE_TYPE, new RL(id))))
  var oriTypes = ['bobsoriginiumdream:oridamage', 'bobsoriginiumdream:origintantdamage', 'bobsoriginiumdream:oribleed']
  var watch = ['ori', 'ori_melee', 'ori_projectile', 'ori_attribute', 'ori_illness', 'ori_immune', 'ori_immune1', 'thought', 'status', 'modify'].map(w => 'arks:human/' + w)
  var st = p => {
    try {
      var c = Cont.get(p).resolve().get(), pc = PC.get(p).resolve().get()
      var o = Object.keys(choice).map(l => l.replace('origins:', '') + '=' + c.getOrigin(lkey(l)).location()).join(' ')
      var names = pc.getPowerNames(), have = watch.filter(w => names.contains(RK.create(Java.loadClass('io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries').CONFIGURED_POWER_KEY, new RL(w)))).map(w => w.replace('arks:human/', ''))
      var immune = oriTypes.map(t => Inv.isInvulnerableTo(p, dmg(t)) ? 1 : 0).join('')
      return o + ' powers=' + names.size() + ' arks=[' + have.join(',') + '] oriImmune=' + immune + ' thought=' + TLS.getFinalLoadLimit(p) + ' dim=' + p.level.dimension
    } catch (err) { return 'state-error=' + err }
  }
  var uuid = UU.fromString('00000000-0000-0000-0000-0000000058a1')
  var p = conn(new SPL(server, level, new GP(uuid, 'orichk')))
  var saved = plist.load(p)
  if (saved != null) {
    // второй запуск: игрок загружен из playerdata
    log('after-restart ' + st(p))
    return
  }
  p.setPos(0.5, 200, 0.5)
  var preg = Java.loadClass('io.github.edwinmindcraft.apoli.api.ApoliAPI').getPowers(server)
  log('registry ' + ['arks:human/ori_immune', 'arks:human/ori_immune1', 'arks:human/ori_illness', 'arks:human/ori_attribute'].map(k => k.replace('arks:human/', '') + '=' + preg.containsKey(new RL(k))).join(' '))
  var plain = conn(new SPL(server, level, new GP(UU.fromString('00000000-0000-0000-0000-0000000058a0'), 'orichk0')))
  log('plain oriImmune=' + oriTypes.map(t => Inv.isInvulnerableTo(plain, dmg(t)) ? 1 : 0).join(''))
  try {
    var c = Cont.get(p).resolve().get()
    Object.keys(choice).forEach(l => { var k = RK.create(Reg.ORIGINS_REGISTRY, new RL(choice[l])); c.setOrigin(lkey(l), k); c.onChosen(k, false) })
    c.validateSynchronization()
  } catch (err) { log('set-error=' + err) }
  log('chosen ' + st(p))
  p.kill()
  log('dead=' + p.isDeadOrDying())
  var p2
  // PlayerList.respawn у фейкового игрока падает в Xaero (нет его данных игрока) — то же, что делает respawn для Origins:
  // новый ServerPlayer + restoreFrom(старый, false) → Forge PlayerEvent.Clone (там Origins копирует расу)
  try { p2 = plist.respawn(p, false); log('respawned(PlayerList) ' + st(p2)) } catch (err) {
    log('respawn-fallback ' + String(err).substring(0, 120))
    // restoreFrom тоже падает в Xaero — шлём само событие Clone (wasDeath=true), его и обрабатывает Origins
    try { p2 = conn(new SPL(server, level, new GP(uuid, 'orichk'))); Java.loadClass('net.minecraftforge.common.MinecraftForge').EVENT_BUS.post(new (Java.loadClass('net.minecraftforge.event.entity.player.PlayerEvent$Clone'))(p2, p, true)); log('respawned(Clone) ' + st(p2)) } catch (err2) { log('respawn-error=' + err2); return }
  }
  // смена измерения обычной командой /tp (ServerPlayer.teleportTo → PlayerChangedDimensionEvent)
  server.runCommandSilent('execute in minecraft:the_nether run tp ' + uuid + ' 0.5 100 0.5'); log('nether ' + st(p2))
  server.runCommandSilent('execute in minecraft:overworld run tp ' + uuid + ' 0.5 200 0.5'); log('back ' + st(p2))
  try { plist.remove(p2) } catch (err) { log('remove-error=' + String(err).substring(0, 160)) }
  log('logged-out saved=' + (server.getWorldPath(Java.loadClass('net.minecraft.world.level.storage.LevelResource').PLAYER_DATA_DIR) + '/' + uuid + '.dat'))
})
