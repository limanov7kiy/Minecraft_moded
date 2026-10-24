// Редкие бонусные мобы Contingency Contract (поток 61, pack/kubejs/server_scripts/cc_rare_mob_bonus.js + перекрытие
// pack/kubejs/data/contigency_contract/loot_tables/gameplay/chronicle_bonus.json). Этапы:
//   CCBONUS_ROLL  — прямой бросок таблицы chronicle_bonus ROLLS раз: что и как часто, есть ли блоки/незерит;
//   CCBONUS_MARK  — MARK_N мобов появляются (addFreshEntity + finalizeSpawn), через 3 тика: сколько помечено, у скольких стоит
//                   флаг броска, у скольких остался эффект bonus_sign без метки;
//   CCBONUS_KILL  — KILL_N мобов убивает фейковый игрок через 2 тика после появления; что выпало;
//   CCBONUS_SAME  — SAME_N мобов убиты в тик появления (проверка EntityEvents.death): сколько осталось помеченными;
//   CCBONUS_FORCE — FORCE_N мобов с меткой (флаг броска поставлен заранее) убивает игрок: настоящий путь DeadFuncProcedure.
// BAD — блоки железа/золота/алмазов и прочее позднее в любом этапе. Итог — CCBONUS_DONE bad=<N>.
// Запуск: EXTRA_SERVER_SCRIPTS=<папка только с этим файлом> TEST_COMMANDS=<файл «#sleep 600»> scripts/test-server.sh <папка вне репо>.
var MOBS = ['minecraft:zombie', 'minecraft:skeleton', 'minecraft:spider', 'minecraft:creeper', 'minecraft:cow', 'minecraft:chicken']
var ROLLS = 100000, MARK_N = 20000, KILL_N = 2000, SAME_N = 1000, FORCE_N = 300, BATCH = 250
var BAD = ['minecraft:iron_block', 'minecraft:gold_block', 'minecraft:diamond_block', 'minecraft:netherite_scrap', 'minecraft:netherite_ingot',
  'minecraft:netherite_sword', 'minecraft:netherite_upgrade_smithing_template', 'minecraft:enchanted_golden_apple', 'minecraft:totem_of_undying',
  'minecraft:trident', 'minecraft:heart_of_the_sea', 'minecraft:sniffer_egg', 'minecraft:echo_shard']
var BONUS_ONLY = ['minecraft:gold_ingot', 'minecraft:diamond', 'minecraft:lapis_lazuli', 'minecraft:coal', 'minecraft:golden_carrot',
  'minecraft:golden_apple', 'minecraft:amethyst_shard', 'minecraft:experience_bottle', 'minecraft:nautilus_shell', 'contigency_contract:crisis_coin']
var X0 = 0, Z0 = 4000, Y = 200
var st = null
function fmt(cnt) { return Object.keys(cnt).sort((a, b) => cnt[b] - cnt[a]).map(k => k + '=' + cnt[k]).join(' ') }
ServerEvents.tick(event => {
  var server = event.server, t = server.tickCount
  var level = server.getLevel('minecraft:overworld')
  var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  var FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  var AABB = Java.loadClass('net.minecraft.world.phys.AABB')
  var ItemEntity = Java.loadClass('net.minecraft.world.entity.item.ItemEntity')
  var Mob = Java.loadClass('net.minecraft.world.entity.Mob')
  var CCB = FR.ATTRIBUTES.getValue(RL.tryParse('contigency_contract:bonusable'))
  var SIGN = FR.MOB_EFFECTS.getValue(RL.tryParse('contigency_contract:bonus_sign'))
  function itemId(s) { return String(FR.ITEMS.getKey(s.getItem())) }
  function box() { return new AABB(X0 - 8, Y - 2, Z0 - 8, X0 + 60, Y + 10, Z0 + 60) }
  function marked(e) { var a = e.getAttribute(CCB); return a != null && a.getBaseValue() > 0 }
  function fake() {
    var Fk = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
    var GP = Java.loadClass('com.mojang.authlib.GameProfile')
    var fp = Fk.get(level, new GP(Java.loadClass('java.util.UUID').fromString('00000000-0000-4000-8000-0000000cc0b1'), 'ccbonus'))
    fp.setPos(X0 - 4, Y, Z0 - 4); return fp
  }
  function spawn(n, k) {
    var type = FR.ENTITY_TYPES.getValue(RL.tryParse(MOBS[k % MOBS.length]))
    var e = type.create(level)
    e.setPos(X0 + (n % 20) * 3 + 0.5, Y, Z0 + Math.floor(n / 20) * 3 + 0.5)
    if (e instanceof Mob) e.finalizeSpawn(level, level.getCurrentDifficultyAt(e.blockPosition()), Java.loadClass('net.minecraft.world.entity.MobSpawnType').NATURAL, null, null)
    return e
  }
  function kill(e, fp) { e.setLastHurtByPlayer(fp); e.attack(level.damageSources().playerAttack(fp), 100000) }
  function checkBad(stage, cnt) {
    var b = Object.keys(cnt).filter(k => BAD.indexOf(k) >= 0)
    if (b.length) { st.bad++; console.info('CCBONUS_BAD ' + stage + ' ' + b.map(k => k + '=' + cnt[k]).join(' ')) }
  }
  if (t == 200) {
    server.runCommandSilent('gamerule doMobSpawning false')
    server.runCommandSilent('gamerule doDaylightCycle false')
    server.runCommandSilent('time set 6000')
    server.runCommandSilent('difficulty normal')
    server.runCommandSilent('forceload add ' + (X0 - 16) + ' ' + (Z0 - 16) + ' ' + (X0 + 64) + ' ' + (Z0 + 64))
    server.runCommandSilent('fill ' + (X0 - 8) + ' ' + (Y - 1) + ' ' + (Z0 - 8) + ' ' + (X0 + 60) + ' ' + (Y - 1) + ' ' + (Z0 + 60) + ' minecraft:stone')
    server.runCommandSilent('fill ' + (X0 - 8) + ' ' + Y + ' ' + (Z0 - 8) + ' ' + (X0 + 60) + ' ' + (Y + 8) + ' ' + (Z0 + 60) + ' minecraft:air')
    st = { bad: 0, stage: 'roll', i: 0, next: 210, pending: null, s: {} }
    console.info('CCBONUS_START attr=' + (CCB != null) + ' sign=' + (SIGN != null))
    return
  }
  if (!st || t < st.next) return
  var fp = fake()
  if (st.stage == 'roll') {
    var LootParams = Java.loadClass('net.minecraft.world.level.storage.loot.LootParams$Builder')
    var LCP = Java.loadClass('net.minecraft.world.level.storage.loot.parameters.LootContextParams')
    var Sets = Java.loadClass('net.minecraft.world.level.storage.loot.parameters.LootContextParamSets')
    var Vec3 = Java.loadClass('net.minecraft.world.phys.Vec3')
    var table = server.getLootData().getLootTable(RL.tryParse('contigency_contract:gameplay/chronicle_bonus'))
    var cnt = {}, hits = {}
    for (var i = 0; i < ROLLS; i++) {
      var p = new LootParams(level).withParameter(LCP.ORIGIN, new Vec3(X0, Y, Z0)).withOptionalParameter(LCP.THIS_ENTITY, fp).create(Sets.EMPTY)
      table.getRandomItems(p).forEach(s => { var k = itemId(s); cnt[k] = (cnt[k] || 0) + s.getCount(); hits[k] = (hits[k] || 0) + 1 })
    }
    console.info('CCBONUS_ROLL x' + ROLLS + ' ' + fmt(cnt))
    console.info('CCBONUS_ROLL_HITS x' + ROLLS + ' ' + fmt(hits) + ' nether_star_per_1000=' + ((hits['minecraft:nether_star'] || 0) * 1000 / ROLLS))
    checkBad('roll', cnt)
    st.stage = 'mark'; st.s = { n: 0, marked: 0, rolled: 0, signOnly: 0 }; st.next = t + 2
    return
  }
  if (st.stage == 'mark') {
    if (st.pending) {
      st.pending.forEach(e => {
        if (marked(e)) st.s.marked++
        else if (SIGN != null && e.hasEffect(SIGN)) st.s.signOnly++
        if (e.getPersistentData().getBoolean('mm_cc_bonus_roll')) st.s.rolled++
        e.discard()
      })
      st.s.n += st.pending.length; st.pending = null
    }
    if (st.s.n >= MARK_N) {
      console.info('CCBONUS_MARK n=' + st.s.n + ' marked=' + st.s.marked + ' (' + (st.s.marked * 100 / st.s.n).toFixed(3) + ' %) rolled=' + st.s.rolled + ' sign_without_mark=' + st.s.signOnly)
      if (st.s.rolled != st.s.n || st.s.signOnly > 0 || st.s.marked > st.s.n * 0.004) { st.bad++; console.info('CCBONUS_BAD mark') }
      st.stage = 'kill'; st.s = { n: 0, marked: 0, cnt: {} }; st.next = t + 2
      return
    }
    st.pending = []
    for (var n = 0; n < BATCH; n++) { var e = spawn(n, st.s.n + n); level.addFreshEntity(e); st.pending.push(e) }
    st.next = t + 3
    return
  }
  if (st.stage == 'kill' || st.stage == 'force') {
    var s = st.s
    if (s.phase == 'hit') {
      s.pending.forEach(e => { if (marked(e)) s.marked++; kill(e, fp) })
      s.n += s.pending.length; s.phase = 'collect'; st.next = t + 10
      return
    }
    if (s.phase == 'collect') {
      level.getEntitiesOfClass(ItemEntity, box()).forEach(e => { var it = e.getItem(), k = itemId(it); s.cnt[k] = (s.cnt[k] || 0) + it.getCount(); e.discard() })
      level.getEntitiesOfClass(Mob, box()).forEach(e => e.discard())
      s.phase = null
    }
    var total = st.stage == 'kill' ? KILL_N : FORCE_N
    if (s.n >= total) {
      console.info('CCBONUS_' + st.stage.toUpperCase() + ' n=' + s.n + ' marked_at_death=' + s.marked + ' ' + fmt(s.cnt))
      checkBad(st.stage, s.cnt)
      if (st.stage == 'kill') { st.stage = 'same'; st.s = { n: 0, marked: 0, cnt: {} } }
      else { console.info('CCBONUS_DONE bad=' + st.bad); st = null; return }
      st.next = t + 2
      return
    }
    s.pending = []
    var m = Math.min(100, total - s.n)
    for (var n = 0; n < m; n++) {
      var e = spawn(n, s.n + n)
      if (st.stage == 'force') e.getPersistentData().putBoolean('mm_cc_bonus_roll', true)
      level.addFreshEntity(e)
      if (st.stage == 'force') e.getAttribute(CCB).setBaseValue(1)
      s.pending.push(e)
    }
    s.phase = 'hit'; st.next = t + 2
    return
  }
  if (st.stage == 'same') {
    var s = st.s
    level.getEntitiesOfClass(ItemEntity, box()).forEach(e => { var it = e.getItem(), k = itemId(it); s.cnt[k] = (s.cnt[k] || 0) + it.getCount(); e.discard() })
    level.getEntitiesOfClass(Mob, box()).forEach(e => e.discard())
    if (s.n >= SAME_N) {
      // предметы, которых нет в дропе этих мобов, но есть в chronicle_bonus — признак сработавшей награды
      var bonusHits = BONUS_ONLY.reduce((a, k) => a + (s.cnt[k] ? 1 : 0), 0)
      console.info('CCBONUS_SAME n=' + s.n + ' marked_after_death=' + s.marked + ' bonus_only_items=' + bonusHits + ' ' + fmt(s.cnt))
      checkBad('same', s.cnt)
      if (s.marked > SAME_N * 0.01) { st.bad++; console.info('CCBONUS_BAD same') }
      st.stage = 'force'; st.s = { n: 0, marked: 0, cnt: {} }; st.next = t + 2
      return
    }
    for (var n = 0; n < 100; n++) { var e = spawn(n, s.n + n); level.addFreshEntity(e); kill(e, fp); if (marked(e)) s.marked++ }
    s.n += 100; st.next = t + 2
  }
})
