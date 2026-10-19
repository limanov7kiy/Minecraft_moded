// Тест (поток 52): какие «заразные» эффекты и атрибуты снимает Оберег (mods-src/disaster-ward) с игрока в выживании.
// Для каждого эффекта из списка CAND (если он есть в реестре): накладываем снаружи зоны (x=150), переносим в зону (x=10), через 20 тиков смотрим, остался ли;
// затем пробуем наложить в зоне. Атрибуты Bob's Originium Dream (orillness, oridisease) — то же. Вывод: WECHECK row <id> | снаружи | в зоне после входа | наложим в зоне.
// Запуск: EXTRA_SERVER_SCRIPTS=<папка только с этим файлом> TEST_COMMANDS=<файл «#sleep 120»+list> scripts/test-server.sh <папка вне репо> (docs/compat.md «Оберег и заражение»).
var WE = {}
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  var level = server.getLevel('minecraft:overworld')
  var Reg = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  var MEI = Java.loadClass('net.minecraft.world.effect.MobEffectInstance')
  var info = s => console.info('WECHECK ' + s)
  var CAND = ['spore:mycelium_ef', 'spore:biled', 'spore:corrosion', 'spore:frostbite', 'spore:ignitable', 'spore:madness', 'spore:marker', 'spore:starvation', 'spore:stunt', 'spore:symbiosis', 'spore:uneasy',
    'srpsystem:infection', 'srpsystem:reborn', 'srpsystem:nude', 'srpsystem:boom',
    'lycanitesmobs:plague', 'lycanitesmobs:decay', 'cataclysm:ghost_sickness', 'irons_spellbooks:blight', 'ebwizardry:decay', 'necromancersdelight:poisonous_spore', 'clanginghowl:neurotoxin',
    'ars_elemental:venom', 'bobsoriginiumdream:distortion', 'bobsoriginiumdream:oristop', 'bobsoriginiumdream:oristormeffect', 'bobsoriginiumdream:afraid', 'bobsoriginiumdream:bleed',
    'minecraft:poison', 'minecraft:wither', 'minecraft:hunger', 'minecraft:nausea', 'minecraft:weakness']
  var ATTR = ['bobsoriginiumdream:orillness', 'bobsoriginiumdream:oridisease', 'bobsoriginiumdream:orillnessprotect', 'bobsoriginiumdream:oricompletelyprotect']
  var effs = e => { var out = []; e.getActiveEffects().forEach(i => out.push(String(Reg.MOB_EFFECTS.getKey(i.getEffect())))); return out }
  if (T == 40) {
    server.runCommandSilent('forceload add -16 -16 16 16'); server.runCommandSilent('forceload add 140 -16 160 16')
    server.runCommandSilent('fill -6 148 -6 6 149 6 minecraft:stone')
  }
  if (T == 80) {
    server.runCommandSilent('setblock 0 150 0 disaster_ward:ward')
    var Fk = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
    var GP = Java.loadClass('com.mojang.authlib.GameProfile'), UU = Java.loadClass('java.util.UUID')
    var GT = Java.loadClass('net.minecraft.world.level.GameType')
    var fp = Fk.get(level, new GP(UU.fromString('00000000-0000-0000-0000-0000000052bb'), 'we_survivor'))
    fp.gameMode.changeGameModeForPlayer(GT.SURVIVAL)
    fp.setPos(150.5, 150, 0.5); level.addNewPlayer(fp)
    WE.fp = fp; WE.rows = {}
    CAND.forEach(id => {
      var ef = Reg.MOB_EFFECTS.getValue(new RL(id))
      if (!ef) { WE.rows[id] = null; return }
      fp.addEffect(new MEI(ef, 6000, 0))
      WE.rows[id] = { out: effs(fp).includes(id) }
    })
    WE.attr = {}
    ATTR.forEach(id => {
      var a = Reg.ATTRIBUTES.getValue(new RL(id)); var inst = a ? fp.getAttribute(a) : null
      WE.attr[id] = { exists: !!a, inst: !!inst, base0: inst ? inst.getBaseValue() : null }
      if (inst && (id.endsWith('orillness') || id.endsWith('oridisease'))) inst.setBaseValue(id.endsWith('orillness') ? 30 : 2)
    })
    ATTR.forEach(id => { var a = Reg.ATTRIBUTES.getValue(new RL(id)); var inst = a ? fp.getAttribute(a) : null; WE.attr[id].out = inst ? inst.getBaseValue() : null })
    WE.t1 = T + 5
  }
  if (WE.fp && T == WE.t1) { WE.fp.setPos(10.5, 150, 10.5); WE.t2 = T + 25 }
  if (WE.fp && T == WE.t2) {
    var now = effs(WE.fp)
    CAND.forEach(id => { if (WE.rows[id]) WE.rows[id].kept = now.includes(id) })
    ATTR.forEach(id => { var a = Reg.ATTRIBUTES.getValue(new RL(id)); var inst = a ? WE.fp.getAttribute(a) : null; WE.attr[id].inZone = inst ? inst.getBaseValue() : null })
    WE.fp.removeAllEffects()
    CAND.forEach(id => { var ef = Reg.MOB_EFFECTS.getValue(new RL(id)); if (ef && WE.rows[id]) { WE.fp.addEffect(new MEI(ef, 6000, 0)); WE.rows[id].reapply = effs(WE.fp).includes(id) } })
    CAND.forEach(id => info(`row ${id} | ${WE.rows[id] === null ? 'нет в сборке' : `снаружи наложился=${WE.rows[id].out}; в зоне через 25 тиков остался=${WE.rows[id].kept}; в зоне накладывается=${WE.rows[id].reapply}`}`))
    ATTR.forEach(id => info(`attr ${id} | есть=${WE.attr[id].exists} на игроке=${WE.attr[id].inst} исходно=${WE.attr[id].base0} снаружи=${WE.attr[id].out} в зоне=${WE.attr[id].inZone}`))
    level.removePlayerImmediately(WE.fp, Java.loadClass('net.minecraft.world.entity.Entity$RemovalReason').DISCARDED)
    WE.fp = null
    console.info('WECHECK done')
  }
})
