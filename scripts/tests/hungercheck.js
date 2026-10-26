// Замер расхода голода (поток 67, баг владельца «быстрый голод»). ServerPlayer без соединения, добавлен в мир; тик игрока
// (Player.tick → FoodData.tick, PlayerTickEvent модов) вызывается вручную через doTick(). Для каждой расы и каждой фазы
// по 30 с (числа в выводе пересчитаны на 60 с): сытость сбрасывается в 20/5, в конце считается потраченная «усталость» (exhaustion):
//   потрачено = (20 + 5 − голод − насыщение) × 4 + текущий exhaustion (4 единицы усталости = 1 деление голода/насыщения).
// Фазы: idle (стоит), walk (4,3 бл/с по земле), sprint (5,6 бл/с с бегом), jump (прыжок раз в 10 тиков, на месте).
// Ваниль 1.20.1 для сравнения: idle 0, walk 0, sprint 0,1 на блок (≈34 за 60 с), jump 0,05 за прыжок (6 за 60 с).
// Вывод: HUNGER <раса> <фаза> exhaustion=<за 60 с> food=<голод> sat=<насыщение> biome=<биом>
// Запуск: EXTRA_SERVER_SCRIPTS=<папка с файлом> TEST_COMMANDS=<«#sleep 1400»> scripts/test-server.sh <папка вне репо>
var HG = { list: ['none', 'arks:human', 'arks:feline', 'arks:draco', 'arks:hippogryph', 'arks:sarkaz'], phases: ['idle', 'walk', 'sprint', 'jump'], ri: 0, pi: 0, t: 0, p: null, done: false }
ServerEvents.tick(event => {
  var server = event.server
  if (HG.done || server.tickCount < 300) return
  var level = server.getLevel('minecraft:overworld')
  if (!HG.p) {
    var UU = Java.loadClass('java.util.UUID'), GP = Java.loadClass('com.mojang.authlib.GameProfile'), GT = Java.loadClass('net.minecraft.world.level.GameType')
    var SPL = Java.loadClass('net.minecraft.server.level.ServerPlayer'), HM = Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types')
    server.runCommandSilent('difficulty normal'); server.runCommandSilent('gamerule doDaylightCycle false'); server.runCommandSilent('time set 6000')
    server.runCommandSilent('weather clear')
    var p = new SPL(server, level, new GP(UU.fromString('00000000-0000-0000-0000-0000000067a1'), 'hunger67'))
    // соединение-заглушка Forge: моды шлют игроку пакеты (без него NPE в PacketDistributor); сам игрок — обычный ServerPlayer, не FakePlayer
    // (FakePlayer$FakePlayerNetHandler не публичный — берём готовый у FakePlayerFactory: пакеты уходят в его фиктивное соединение)
    p.connection = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory').get(level, new GP(UU.fromString('00000000-0000-0000-0000-0000000067a2'), 'hunger67net')).connection
    p.setGameMode(GT.SURVIVAL)
    // точка на суше (не океан/река/пляж, выше уровня моря) — иначе игрок плавает и движение считается плаванием
    var sx = 0, sz = 0
    for (var i = 0; i < 64; i++) {
      var tx = (i % 8) * 256, tz = Math.floor(i / 8) * 256
      level.getChunk(tx >> 4, tz >> 4)
      var b = String(level.getBiome(new (Java.loadClass('net.minecraft.core.BlockPos'))(tx, 64, tz)).unwrapKey().get().location())
      if (b.indexOf('ocean') < 0 && b.indexOf('river') < 0 && b.indexOf('beach') < 0 && level.getHeight(HM.MOTION_BLOCKING_NO_LEAVES, tx, tz) > 64) { sx = tx; sz = tz; break }
    }
    p.setPos(sx + 0.5, level.getHeight(HM.MOTION_BLOCKING_NO_LEAVES, sx, sz) + 0.0, sz + 0.5)
    level.addFreshEntity(p)
    HG.p = p
    HG.biome = String(level.getBiome(p.blockPosition()).unwrapKey().get().location())
    console.info('HUNGER player at ' + p.blockPosition() + ' biome=' + HG.biome)
  }
  var p = HG.p, race = HG.list[HG.ri], phase = HG.phases[HG.pi]
  if (HG.t == 0) {
    if (HG.pi == 0 && race != 'none') {
      try {
        var Cont = Java.loadClass('io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer')
        var Reg = Java.loadClass('io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries')
        var RK = Java.loadClass('net.minecraft.resources.ResourceKey'), RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
        var cont = Cont.get(p).resolve().get(), lk = RK.create(Reg.LAYERS_REGISTRY, new RL('origins:origin')), key = RK.create(Reg.ORIGINS_REGISTRY, new RL(race))
        cont.setOrigin(lk, key); cont.onChosen(key, false); cont.validateSynchronization()
      } catch (e) { console.info('HUNGER origin-set-error ' + race + ' ' + e) }
    }
    var fd = p.getFoodData(); fd.setFoodLevel(20); fd.setSaturation(5.0); fd.setExhaustion(0.0)
    p.setHealth(p.getMaxHealth()); p.setSprinting(phase == 'sprint')
  }
  // движение: учёт статистики/усталости как у настоящего игрока (ServerGamePacketListenerImpl → checkMovementStatistics)
  p.setOnGround(true)
  // ходьба/бег: и статистика (ванильная усталость бега), и реальное смещение (LSO считает усталость, только если игрок сдвинулся > 1 блока за 10 тиков);
  // ходим туда-обратно по 20 блоков вдоль X, чтобы не уходить в незагруженные чанки
  if (phase == 'walk' || phase == 'sprint') {
    var v = phase == 'walk' ? 0.215 : 0.28, dir = Math.floor(HG.t / 80) % 2 == 0 ? 1 : -1
    if (phase == 'sprint') p.setSprinting(true)
    p.setPos(p.getX() + dir * v, p.getY(), p.getZ())
    p.checkMovementStatistics(dir * v, 0.0, 0.0)
  }
  if (phase == 'jump' && HG.t % 10 == 0) p.jumpFromGround()
  p.doTick()
  HG.t++
  if (HG.t >= 600) {
    var fd2 = p.getFoodData()
    var spent = 2 * ((25 - fd2.getFoodLevel() - fd2.getSaturationLevel()) * 4 + fd2.getExhaustionLevel())
    console.info('HUNGER ' + race + ' ' + phase + ' exhaustion=' + spent.toFixed(2) + ' food=' + fd2.getFoodLevel() + ' sat=' + fd2.getSaturationLevel().toFixed(2) + ' biome=' + HG.biome)
    HG.t = 0; HG.pi++
    if (HG.pi >= HG.phases.length) { HG.pi = 0; HG.ri++ }
    if (HG.ri >= HG.list.length) { HG.done = true; console.info('HUNGER DONE') }
  }
})
