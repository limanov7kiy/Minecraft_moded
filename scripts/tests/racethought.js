// Поток 48: силы рас Arknights «Thought Load +N» (arks:human|sarkaz|sarkaz_nephesh/thought) → предел нагрузки мыслей Sarkaz's Furnace.
// Фейковый ServerPlayer, раса через IOriginContainer, затем снятие расы (origins:empty). Вывод: RACETHOUGHT <случай> limit=... sfsf=... tmt=...
ServerEvents.tick(event => {
  var server = event.server
  if (server.tickCount != 300) return
  var log = s => console.info('RACETHOUGHT ' + s)
  var level = server.getLevel('minecraft:overworld')
  var UU = Java.loadClass('java.util.UUID')
  var GP = Java.loadClass('com.mojang.authlib.GameProfile')
  var SPL = Java.loadClass('net.minecraft.server.level.ServerPlayer')
  var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  var RK = Java.loadClass('net.minecraft.resources.ResourceKey')
  var REG = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  var Cont = Java.loadClass('io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer')
  var Reg = Java.loadClass('io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries')
  var TLS = Java.loadClass('com.zhixu.themassestravels.content.empathy.common.thought.ThoughtLoadService')
  var sfsf = REG.ATTRIBUTES.getValue(new RL('sarkazs_furnace_side_fables', 'thought_load_limit'))
  var tmt = REG.ATTRIBUTES.getValue(new RL('the_massestravels', 'thought_load_limit'))
  var val = (p, a) => { if (a == null) return 'none'; var i = p.getAttribute(a); return i == null ? 'n/a' : i.getValue() + i.getModifiers() }
  var st = p => 'limit=' + TLS.getFinalLoadLimit(p) + ' sfsf=' + val(p, sfsf) + ' tmt=' + val(p, tmt)
  var conn = p => { var C = Java.loadClass('net.minecraft.network.Connection'), PF = Java.loadClass('net.minecraft.network.protocol.PacketFlow'), G = Java.loadClass('net.minecraft.server.network.ServerGamePacketListenerImpl'); new G(server, new C(PF.SERVERBOUND), p); return p }
  var layer = RK.create(Reg.LAYERS_REGISTRY, new RL('origins:origin'))
  var setO = (p, o) => { try { var c = Cont.get(p).resolve().get(); var k = RK.create(Reg.ORIGINS_REGISTRY, new RL(o)); c.setOrigin(layer, k); c.onChosen(k, false); c.validateSynchronization(); return 'origin=' + c.getOrigin(layer) } catch (err) { return 'origin-error=' + err } }
  ;['plain', 'arks:human', 'arks:sarkaz_lich', 'arks:sarkaz_nephesh', 'arks:feline'].forEach((o, i) => {
    var p = new SPL(server, level, new GP(UU.fromString('00000000-0000-0000-0000-00000000d0' + (10 + i)), 'race' + i))
    p.setPos(0.5, 200, 0.5); conn(p)
    var note = o == 'plain' ? '' : setO(p, o)
    log(o + ' ' + st(p) + ' ' + note)
    if (o != 'plain') { var n2 = setO(p, 'origins:empty'); log(o + ' -> removed ' + st(p) + ' ' + n2) }
  })
})
