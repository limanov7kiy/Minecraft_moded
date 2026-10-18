// Тест урона от падения в выживании (поток 35: «урона от падения нет»). Фейковый игрок Forge, survival, сложность normal:
// LivingEntity.causeFallDamage(20, 1, fall) — то, что вызывает движок при приземлении с ~23 блоков (20 блоков + 3 безопасных).
// Случаи: plain (без расы), затем расы Arknights Races (Origins) через IOriginContainer, затем эффект «Глубинный охотник».
// Вывод: FALLDAMAGE <случай> dmg=<потеряно здоровья> hp=<после> event=dist/mult (множитель события LivingFallEvent: 0 = урона нет) causeFallDamage=<true: урон проходит>.
// Надёжные признаки — event mult и causeFallDamage: dmg у «пустого» ServerPlayer без соединения шумит (его hurt частично глушится).
ServerEvents.tick(event => {
  var server = event.server
  if (server.tickCount != 300) return
  var level = server.getLevel('minecraft:overworld')
  var UU = Java.loadClass('java.util.UUID')
  var GP = Java.loadClass('com.mojang.authlib.GameProfile')
  var Fk = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  var GT = Java.loadClass('net.minecraft.world.level.GameType')
  var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  server.runCommandSilent('difficulty normal')
  var cases = ['plain', 'arks:cautus', 'arks:feline', 'arks:aslan', 'arks:seahunter', 'arks:agir', 'origins:human', 'origins:feline']
  cases.forEach((c, i) => {
    // FakePlayer Forge неуязвим (isInvulnerableTo=true), поэтому берём обычный ServerPlayer без соединения
    var SPL = Java.loadClass('net.minecraft.server.level.ServerPlayer')
    var p = new SPL(server, level, new GP(UU.fromString('00000000-0000-0000-0000-00000000f0' + (10 + i)), 'falltest' + i))
    p.setGameMode(GT.SURVIVAL)
    // новый ServerPlayer 60 тиков неуязвим (spawnInvulnerableTime) — обнуляем поле, найдя его по значению 60
    var reset = 0
    p.getClass().getDeclaredFields().forEach(f => { if (f.getType() == Java.loadClass('java.lang.Integer').TYPE) { f.setAccessible(true); if (f.getInt(p) == 60) { f.setInt(p, 0); reset++ } } })
    p.setPos(0.5 + i * 4, 200, 0.5)
    var note = ''
    if (c != 'plain') {
      try {
        var Cont = Java.loadClass('io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer')
        var Reg = Java.loadClass('io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries')
        var RK = Java.loadClass('net.minecraft.resources.ResourceKey')
        var cont = Cont.get(p).resolve().get()
        var layerKey = RK.create(Reg.LAYERS_REGISTRY, new RL('origins:origin'))
        var key = RK.create(Reg.ORIGINS_REGISTRY, new RL(c))
        cont.setOrigin(layerKey, key)
        cont.onChosen(key, false)
        cont.validateSynchronization()
        note = ' origin=' + cont.getOrigin(layerKey)
      } catch (err) { note = ' origin-set-error=' + err }
    }
    var ev = Java.loadClass('net.minecraftforge.common.ForgeHooks').onLivingFall(p, 20, 1.0)
    var evInfo = ev == null ? ' event=cancelled' : ' event=dist:' + ev[0] + ',mult:' + ev[1]
    p.setHealth(20)
    var took = p.causeFallDamage(20, 1.0, level.damageSources().fall())
    console.info('FALLDAMAGE ' + c + ' dmg=' + (20 - p.getHealth()) + ' hp=' + p.getHealth() + evInfo + ' causeFallDamage=' + took + ' reset=' + reset + note)
  })
})
