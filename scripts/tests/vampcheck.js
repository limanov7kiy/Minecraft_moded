// Проверка Vampirism в сборке (поток 75, docs/vampirism.md «Итог установки»). Запускается вместе с perfload.js или отдельно:
// через VC.start секунд после первого тика (по умолчанию — после конца perfload) 1) считает сущности Vampirism в Overworld,
// 2) ищет ближайшие постройки Vampirism от (0,0), 3) делает фейкового игрока расы Arknights «Саркас-вампир» (Origins) и вампиром
// Vampirism 5-го уровня, ставит его днём под открытое небо и тикает 600 тиков: здоровье, еда, кровь, урон от солнца, исключения.
// Вывод: строки VAMPCHECK …; итог — VAMPCHECK DONE (или VAMPCHECK FAIL <этап> <ошибка>).
// Запуск: EXTRA_SERVER_SCRIPTS=<папка с файлом> TEST_COMMANDS=<«#sleep N»> scripts/test-server.sh <папка вне репо>
var VC = { start: 1420, t0: 0, step: 0, fp: null, n: 0 }
function vcEntities(server) {
  var m = {}, all = 0, v = 0
  server.overworld().getAllEntities().forEach(e => {
    all++; var id = String(e.getType().builtInRegistryHolder().key().location())
    if (id.indexOf('vampirism:') == 0) { v++; m[id] = (m[id] || 0) + 1 }
  })
  console.info('VAMPCHECK entities all=' + all + ' vampirism=' + v + ' ' + JSON.stringify(m))
}
function vcStructures(server) {
  var Reg = Java.loadClass('net.minecraft.core.registries.Registries'), RK = Java.loadClass('net.minecraft.resources.ResourceKey')
  var RL = Java.loadClass('net.minecraft.resources.ResourceLocation'), HS = Java.loadClass('net.minecraft.core.HolderSet')
  var BP = Java.loadClass('net.minecraft.core.BlockPos')
  var level = server.overworld(), reg = server.registryAccess().registryOrThrow(Reg.STRUCTURE)
  ;['crypt', 'vampire_hut', 'vampire_altar', 'mother', 'hunter_camp', 'hunter_outpost_plains', 'hunter_outpost_desert',
    'hunter_outpost_badlands', 'hunter_outpost_vampire_forest'].forEach(id => {
    try {
      var h = reg.getHolderOrThrow(RK.create(Reg.STRUCTURE, new RL('vampirism', id)))
      var r = level.getChunkSource().getGenerator().findNearestMapStructure(level, HS.direct([h]), new BP(0, 64, 0), 40, false)
      console.info('VAMPCHECK structure vampirism:' + id + ' ' + (r ? r.getFirst().getX() + ' ' + r.getFirst().getZ() : 'none'))
    } catch (e) { console.info('VAMPCHECK structure vampirism:' + id + ' error ' + e) }
  })
}
function vcPlayer(server) {
  var level = server.overworld()
  var Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory'), GP = Java.loadClass('com.mojang.authlib.GameProfile')
  var UUID = Java.loadClass('java.util.UUID'), RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  var fp = Fake.get(level, new GP(UUID.fromString('9e7f0000-0000-4000-8000-0000000000a1'), 'vamp0'))
  var sp = level.getSharedSpawnPos(), y = level.getHeight(Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types').MOTION_BLOCKING, sp.getX(), sp.getZ())
  fp.setPos(sp.getX() + 0.5, y + 0.0, sp.getZ() + 0.5)
  fp.setGameMode(Java.loadClass('net.minecraft.world.level.GameType').SURVIVAL)
  level.addFreshEntity(fp)
  try {
    var OA = Java.loadClass('io.github.edwinmindcraft.origins.api.OriginsAPI'), IOC = Java.loadClass('io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer')
    var layer = OA.getLayersRegistry(server).get(new RL('origins', 'origin')), origin = OA.getOriginsRegistry(server).get(new RL('arks', 'sarkaz_vampire'))
    var c = IOC.get(fp).orElse(null)
    c.setOrigin(layer, origin); c.onChosen(false); c.synchronize()
    console.info('VAMPCHECK origin set ' + c.getOrigins())
  } catch (e) { console.info('VAMPCHECK FAIL origin ' + e) }
  try {
    var FPH = Java.loadClass('de.teamlapen.vampirism.entity.factions.FactionPlayerHandler'), VR = Java.loadClass('de.teamlapen.vampirism.api.VReference')
    var ok = FPH.get(fp).setFactionAndLevel(VR.VAMPIRE_FACTION, 5)
    console.info('VAMPCHECK vampire set=' + ok + ' level=' + FPH.get(fp).getCurrentLevel())
  } catch (e) { console.info('VAMPCHECK FAIL vampire ' + e) }
  return fp
}
function vcState(fp, t) {
  var s = 'VAMPCHECK tick ' + t + ' hp=' + fp.getHealth().toFixed(1) + ' food=' + fp.getFoodData().getFoodLevel() + ' alive=' + fp.isAlive()
  try {
    var VP = Java.loadClass('de.teamlapen.vampirism.entity.player.vampire.VampirePlayer'), vp = VP.get(fp)
    s += ' vlevel=' + vp.getLevel() + ' blood=' + vp.getBloodStats().getBloodLevel() + ' sun=' + vp.isGettingSundamage(fp.level(), true)
  } catch (e) { s += ' vp-error ' + e }
  s += ' effects=' + fp.getActiveEffects().size() + ' sky=' + fp.level().canSeeSky(fp.blockPosition().above())
  console.info(s)
}
ServerEvents.tick(event => {
  var server = event.server
  if (VC.step < 0 || server.tickCount < 100) return
  if (!VC.t0) { VC.t0 = Date.now(); return }
  if (VC.step == 0 && Date.now() - VC.t0 >= VC.start * 1000) {
    VC.step = 1
    try { vcEntities(server) } catch (e) { console.info('VAMPCHECK FAIL entities ' + e) }
    try { vcStructures(server) } catch (e) { console.info('VAMPCHECK FAIL structures ' + e) }
    server.runCommandSilent('time set 6000'); server.runCommandSilent('weather clear')
    try { VC.fp = vcPlayer(server); vcState(VC.fp, 0) } catch (e) { console.info('VAMPCHECK FAIL player ' + e); VC.step = -1 }
    return
  }
  if (VC.step == 1) {
    VC.n++
    try { VC.fp.doTick() } catch (e) { console.info('VAMPCHECK FAIL tick ' + VC.n + ' ' + e); VC.step = -1; return }
    if (VC.n % 100 == 0) vcState(VC.fp, VC.n)
    if (VC.n >= 600) { try { vcEntities(server) } catch (e) {} ; console.info('VAMPCHECK DONE'); VC.step = -1 }
  }
})
