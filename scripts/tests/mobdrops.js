// Статистика дропа обычных мобов (поток 60: «из любого моба выпадают железные/алмазные блоки, звёзды Незера»).
// Для каждого типа из MOBS и каждого способа смерти призывается N мобов на каменной площадке и убивается сразу:
//   player — урон от фейкового игрока (killed_by_player, LAST_DAMAGE_PLAYER), kill — entity.kill() как у /kill.
// Через 10 тиков собираются все предметы на площадке. Вывод: MOBDROPS <моб> <способ> n=<N> <предмет>=<штук>/... ;
// MOBDROPS_BAD — «ценные» предметы (VALUABLE) в дропе мобов, у которых их быть не должно. Итог — MOBDROPS_DONE.
// ccbonus=<сколько из N помечены «бонусными» Contingency Contract>.
// Плюс LOOTROLL: прямой бросок таблицы entities/<моб> 1000 раз (с GLM Forge) — видно, добавляет ли таблица/модификатор.
// Запуск: EXTRA_SERVER_SCRIPTS=<папка с этим файлом> TEST_COMMANDS=<файл с «#sleep 200»> scripts/test-server.sh <папка вне репо>.
var MOBS = ['minecraft:zombie', 'minecraft:skeleton', 'minecraft:spider', 'minecraft:chicken', 'minecraft:cow']
var MODES = ['player', 'kill']
var N = 100
var VALUABLE = ['minecraft:iron_block', 'minecraft:gold_block', 'minecraft:diamond_block', 'minecraft:emerald_block',
  'minecraft:netherite_block', 'minecraft:nether_star', 'minecraft:diamond', 'minecraft:netherite_ingot', 'minecraft:netherite_scrap',
  'minecraft:emerald', 'minecraft:beacon', 'minecraft:elytra', 'minecraft:totem_of_undying', 'minecraft:enchanted_golden_apple',
  'minecraft:dragon_egg', 'minecraft:ancient_debris', 'minecraft:lapis_block', 'minecraft:redstone_block']
var X0 = 0, Z0 = 2000, Y = 200
var st = null
ServerEvents.tick(event => {
  var server = event.server, t = server.tickCount
  var level = server.getLevel('minecraft:overworld')
  var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  var FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  var AABB = Java.loadClass('net.minecraft.world.phys.AABB')
  var ItemEntity = Java.loadClass('net.minecraft.world.entity.item.ItemEntity')
  var Mob = Java.loadClass('net.minecraft.world.entity.Mob')
  function itemId(s) { return String(FR.ITEMS.getKey(s.getItem())) }
  function box() { return new AABB(X0 - 8, Y - 2, Z0 - 8, X0 + 40, Y + 10, Z0 + 40) }
  if (t == 200) {
    server.runCommandSilent('gamerule doMobSpawning false')
    server.runCommandSilent('gamerule doDaylightCycle false')
    server.runCommandSilent('time set 6000')
    server.runCommandSilent('difficulty normal')
    server.runCommandSilent('forceload add ' + (X0 - 16) + ' ' + (Z0 - 16) + ' ' + (X0 + 48) + ' ' + (Z0 + 48))
    server.runCommandSilent('fill ' + (X0 - 8) + ' ' + (Y - 1) + ' ' + (Z0 - 8) + ' ' + (X0 + 40) + ' ' + (Y - 1) + ' ' + (Z0 + 40) + ' minecraft:stone')
    server.runCommandSilent('fill ' + (X0 - 8) + ' ' + Y + ' ' + (Z0 - 8) + ' ' + (X0 + 40) + ' ' + (Y + 8) + ' ' + (Z0 + 40) + ' minecraft:air')
    // прямой бросок таблиц (как при смерти от игрока; GLM применяются в getRandomItems)
    try {
      var LootParams = Java.loadClass('net.minecraft.world.level.storage.loot.LootParams$Builder')
      var LCP = Java.loadClass('net.minecraft.world.level.storage.loot.parameters.LootContextParams')
      var Sets = Java.loadClass('net.minecraft.world.level.storage.loot.parameters.LootContextParamSets')
      var Vec3 = Java.loadClass('net.minecraft.world.phys.Vec3')
      var Fk = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
      var GP = Java.loadClass('com.mojang.authlib.GameProfile')
      var UU = Java.loadClass('java.util.UUID')
      var fp0 = Fk.get(level, new GP(UU.fromString('00000000-0000-4000-8000-0000000d0b01'), 'mobdrops0'))
      fp0.setPos(X0, Y, Z0)
      MOBS.forEach(id => {
        var type = FR.ENTITY_TYPES.getValue(RL.tryParse(id))
        var e = type.create(level); e.setPos(X0, Y, Z0)
        var table = server.getLootData().getLootTable(type.getDefaultLootTable())
        var cnt = {}
        for (var i = 0; i < 1000; i++) {
          var p = new LootParams(level).withParameter(LCP.THIS_ENTITY, e).withParameter(LCP.ORIGIN, new Vec3(X0, Y, Z0))
            .withParameter(LCP.DAMAGE_SOURCE, level.damageSources().playerAttack(fp0)).withOptionalParameter(LCP.KILLER_ENTITY, fp0)
            .withOptionalParameter(LCP.DIRECT_KILLER_ENTITY, fp0).withParameter(LCP.LAST_DAMAGE_PLAYER, fp0).withLuck(0).create(Sets.ENTITY)
          table.getRandomItems(p).forEach(s => { var k = itemId(s); cnt[k] = (cnt[k] || 0) + s.getCount() })
        }
        console.info('LOOTROLL ' + id + ' x1000 ' + Object.keys(cnt).sort().map(k => k + '=' + cnt[k]).join(' '))
        e.discard()
      })
    } catch (x) { console.info('LOOTROLL_ERR ' + x) }
    // моб из старого чанка с меткой «бонусный» (summon грузит NBT как при загрузке чанка) — метка должна сняться
    server.runCommandSilent('summon minecraft:cow ' + (X0 + 20) + ' ' + Y + ' ' + (Z0 - 6) + ' {Tags:["mobdrops_old"],Attributes:[{Name:"contigency_contract:bonusable",Base:1.0d},{Name:"minecraft:generic.max_health",Base:7.0d}]}')
    level.getEntitiesOfClass(Mob, box()).forEach(e => { if (e.getTags().contains('mobdrops_old')) {
      var a = e.getAttribute(FR.ATTRIBUTES.getValue(RL.tryParse('contigency_contract:bonusable')))
      console.info('MOBDROPS_OLDMOB bonusable=' + (a == null ? 'нет' : a.getBaseValue()) + ' max_health=' + e.getMaxHealth() + ' (7 — NBT применился)'); e.discard() } })
    var jobs = []
    MOBS.forEach(m => MODES.forEach(md => jobs.push({ mob: m, mode: md })))
    st = { jobs: jobs, i: 0, next: 220, collect: -1, bad: 0 }
    level.getEntitiesOfClass(ItemEntity, box()).forEach(e => e.discard())
    return
  }
  if (!st || t < st.next) return
  if (st.collect >= 0) {
    var j = st.jobs[st.collect], cnt = {}
    level.getEntitiesOfClass(ItemEntity, box()).forEach(e => { var s = e.getItem(), k = itemId(s); cnt[k] = (cnt[k] || 0) + s.getCount(); e.discard() })
    level.getEntitiesOfClass(Mob, box()).forEach(e => e.discard())
    var keys = Object.keys(cnt).sort((a, b) => cnt[b] - cnt[a])
    console.info('MOBDROPS ' + j.mob + ' ' + j.mode + ' n=' + N + ' ccbonus=' + (st.ccBonus || 0) + ' ' + keys.map(k => k + '=' + cnt[k]).join(' '))
    var bad = keys.filter(k => VALUABLE.indexOf(k) >= 0)
    if (bad.length) { st.bad++; console.info('MOBDROPS_BAD ' + j.mob + ' ' + j.mode + ' ' + bad.map(k => k + '=' + cnt[k]).join(' ')) }
    st.collect = -1; st.ccBonus = 0
  }
  if (st.i >= st.jobs.length) { console.info('MOBDROPS_DONE bad=' + st.bad); st = null; return }
  var job = st.jobs[st.i]
  var Fk2 = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  var GP2 = Java.loadClass('com.mojang.authlib.GameProfile')
  var UU2 = Java.loadClass('java.util.UUID')
  var fp = Fk2.get(level, new GP2(UU2.fromString('00000000-0000-4000-8000-0000000d0b02'), 'mobdrops'))
  fp.setPos(X0 - 4, Y, Z0 - 4)
  var type = FR.ENTITY_TYPES.getValue(RL.tryParse(job.mob))
  for (var n = 0; n < N; n++) {
    var e = type.create(level)
    e.setPos(X0 + (n % 10) * 3 + 0.5, Y, Z0 + Math.floor(n / 10) * 3 + 0.5)
    if (e instanceof Mob) e.finalizeSpawn(level, level.getCurrentDifficultyAt(e.blockPosition()), Java.loadClass('net.minecraft.world.entity.MobSpawnType').NATURAL, null, null)
    level.addFreshEntity(e)
    // метка «бонусного» моба Contingency Contract (pack/kubejs/server_scripts/cc_no_mob_bonus.js её выключает)
    var ccb = FR.ATTRIBUTES.getValue(RL.tryParse('contigency_contract:bonusable'))
    if (ccb != null && e.isLiving() && e.getAttribute(ccb) != null && e.getAttribute(ccb).getBaseValue() > 0) st.ccBonus = (st.ccBonus || 0) + 1
    if (job.mode == 'player') { e.setLastHurtByPlayer(fp); e.attack(level.damageSources().playerAttack(fp), 100000) }
    else e.kill()
  }
  st.collect = st.i; st.i++; st.next = t + 10
})
