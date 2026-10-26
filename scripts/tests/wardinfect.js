// Тест (поток 52): Оберег (mods-src/disaster-ward) против заражения Fungal Infection: Spore / SRP System — мицелий, игроки в выживании, мобы, измерения, чанки.
// Дополняет sporeward.js (там — появление паразитов, взрывы, бури). Вывод: WICHECK <проверка> OK|FAIL <подробности>; «WICHECK info …» — справочные цифры.
// Оберег в (0,150,0), радиус 96 (WardConfig.RADIUS) → внутри x,z ∈ [-96;96], запас edgeMargin 16 → паразиты не появляются до 112.
// Запуск (docs/compat.md «Оберег и заражение»):
//   mkdir -p $T/cfg $T/js && cp scripts/tests/wardinfect.js $T/js/
//   sed -e 's/"Sets Mound Infection Cooldown" = 120/"Sets Mound Infection Cooldown" = 1/' -e 's/"Sets Mound default range" = 4.0/"Sets Mound default range" = 30.0/' pack/config/sporeconfig.toml > $T/cfg/sporeconfig.toml
//   printf '#sleep 90\nlist\n' > $T/cmds.txt
//   EXTRA_SERVER_SCRIPTS=$T/js EXTRA_CONFIG=$T/cfg TEST_COMMANDS=$T/cmds.txt scripts/test-server.sh <папка вне репо>
// (кулдаун кургана 1 с и радиус 30 — чтобы настоящий тик кургана успел заразить за время теста; без EXTRA_CONFIG проверки «настоящего кургана» FAIL.)
var WI = {}
ServerEvents.tick(event => {
  var server = event.server, T = server.tickCount
  var level = server.getLevel('minecraft:overworld')
  var BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  var Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  var AABB = Java.loadClass('net.minecraft.world.phys.AABB')
  var Api = () => Java.loadClass('dev.mcmoded.disasterward.WardApi')
  var Guard = () => Java.loadClass('dev.mcmoded.disasterward.WardGuard')
  var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  var Reg = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  var ok = (name, cond, info) => console.info(`WICHECK ${name} ${cond ? 'OK' : 'FAIL'} ${info || ''}`)
  var info = s => console.info('WICHECK info ' + s)
  var lv = dim => server.getLevel(dim)
  var ents = (lvl, id, x, z, r) => { var out = []; lvl.getEntitiesOfClass(Entity, new AABB(x - r, -64, z - r, x + r, 320, z + r))
    .forEach(e => { if (String(e.getType().toString()).includes(id) && !e.isRemoved()) out.push(e) }); return out }
  var blk = (lvl, x, y, z) => String(lvl.getBlockState(new BlockPos(x, y, z)).block.id)
  var infected = b => b.startsWith('spore:') || b == 'minecraft:mycelium'
  var effId = i => String(Reg.MOB_EFFECTS.getKey(i.getEffect()))
  var effects = e => { var out = []; e.getActiveEffects().forEach(i => out.push(effId(i))); return out.sort() }
  var count = (lvl, x0, x1, z0, z1, y0, y1, pred) => { let n = 0; for (let x = x0; x <= x1; x++) for (let z = z0; z <= z1; z++) for (let y = y0; y <= y1; y++) if (pred(x, blk(lvl, x, y, z))) n++; return n }
  var R = 96

  if (T == 40) {
    server.runCommandSilent('forceload add -16 -16 16 16')
    server.runCommandSilent('forceload add 60 -16 140 16')           // площадка у края зоны
    server.runCommandSilent('forceload add 60 184 140 216')          // контрольная площадка: те же x, но z=200 — вне зоны оберега
    // платформы: камень + воздух над ним
    for (let zc of [0, 200]) {
      server.runCommandSilent(`fill 60 148 ${zc - 14} 140 149 ${zc + 14} minecraft:stone`)
      server.runCommandSilent(`fill 60 150 ${zc - 14} 140 160 ${zc + 14} minecraft:air`)
    }
  }
  // курганы ставим ДО оберега (рядом с ним появиться нельзя), потом ставим оберег; курган на z=0 в полосе у края отталкивается, поэтому берём x=113 (> 96 + 16)
  if (T == 60) {
    server.runCommandSilent('summon spore:mound 113 150 0 {PersistenceRequired:1b}')
    server.runCommandSilent('summon spore:mound 113 150 200 {PersistenceRequired:1b}')   // контроль (без оберега поблизости)
    WI.t0 = T
  }
  if (T == 62) {
    server.runCommandSilent('setblock 0 150 0 disaster_ward:ward')
    ok('ward-placed', blk(level, 0, 150, 0) == 'disaster_ward:ward')
    ok('radius', Java.loadClass('dev.mcmoded.disasterward.WardConfig').RADIUS.get() == R)
    info('курганов после постановки оберега: z=0 ' + ents(level, 'spore:mound', 113, 0, 8).length + ', z=200 ' + ents(level, 'spore:mound', 113, 200, 8).length)
    WI.cure = Java.loadClass('dev.mcmoded.disasterward.WardConfig').INFECTION_EFFECTS.get().toString()
    info('infectionEffects (конфиг оберега) = ' + WI.cure)
    // справочник: все эффекты Spore и SRP System в реестре
    var all = []
    Reg.MOB_EFFECTS.getKeys().forEach(k => { if (k.getNamespace() == 'spore' || k.getNamespace() == 'srpsystem') all.push(String(k)) })
    WI.allEffects = all.sort()
    info('эффекты в реестре: ' + WI.allEffects.join(' '))
  }

  // ---- 1. Блоки: ручной вызов SpreadInfection в контексте паразита (как в его тике) ----
  if (T == 70) {
    var m = ents(level, 'spore:mound', 113, 200, 6)[0] || ents(level, 'spore:mound', 113, 0, 6)[0]
    WI.mound = m
    ok('mound-class', m && m.SpreadInfection, 'курган найден и умеет SpreadInfection: ' + !!(m && m.SpreadInfection))
    // существующее заражение внутри: ставим командой (не в контексте паразита), потом «зараживаем» вокруг него
    server.runCommandSilent('setblock 70 148 5 spore:infested_stone')
    server.runCommandSilent('setblock 96 149 -3 spore:infested_stone')
    WI.preBlocks = [blk(level, 70, 148, 5), blk(level, 96, 149, -3)]
    info('заражённые блоки, поставленные командой внутри: ' + WI.preBlocks.join(', '))
    var sets = [[100, 0, 'band'], [97, 0, 'edge+1'], [96, 0, 'edge'], [60, 0, 'deep'], [100, 200, 'control-band'], [96, 200, 'control-edge']]
    sets.forEach(s => {
      var mm = ents(level, 'spore:mound', 113, s[1], 8)[0] || m
      for (let i = 0; i < 8; i++) {
        Guard().enter(level, mm)
        try { mm.SpreadInfection(level, 8.0, new BlockPos(s[0], 149, s[1])) } finally { Guard().exit() }
      }
    })
  }
  if (T == 72) {
    // зона по x: центр 100 радиус 8 → x 92..108 (внутри ≤96, снаружи ≥97); центр 97 → 89..105; центр 96 → 88..104; центр 60 → 52..68
    var inside = count(level, 60, 96, -12, 12, 148, 151, (x, b) => infected(b)), outside = count(level, 97, 140, -12, 12, 148, 151, (x, b) => infected(b))
    var preKept = blk(level, 70, 148, 5) == 'spore:infested_stone'
    var cIn = count(level, 60, 96, 188, 212, 148, 151, (x, b) => infected(b)), cOut = count(level, 97, 140, 188, 212, 148, 151, (x, b) => infected(b))
    info(`вручную: заражённых блоков внутри=${inside} снаружи=${outside}; контроль (z=200, без оберега): внутри-полосы x≤96=${cIn} x≥97=${cOut}`)
    ok('manual-spread-inside-zero', inside == 2, `внутри зоны (x 60..96) заражённых блоков=${inside} (2 — блоки, поставленные командой)`)
    ok('manual-spread-outside-works', outside > 0, `снаружи (x 97..140)=${outside}`)
    ok('manual-control-spreads', cIn > 0 && cOut > 0, 'без оберега заражение идёт и на x≤96 (иначе тест не показателен)')
    // край: x=97 заражается (за границей зоны, но внутри полосы +16), x=96 — нет
    var e96 = count(level, 96, 96, -12, 12, 148, 151, (x, b) => infected(b)) - (blk(level, 96, 149, -3) == 'spore:infested_stone' ? 1 : 0)
    var e97 = count(level, 97, 97, -12, 12, 148, 151, (x, b) => infected(b))
    var band = count(level, 97, 112, -12, 12, 148, 151, (x, b) => infected(b))
    ok('edge-x96-clean', e96 == 0, `столбец x=96 (последний внутри) заражённых=${e96}`)
    ok('edge-x97-infected', e97 > 0, `столбец x=97 (первый снаружи) заражённых=${e97}; вся полоса 97..112: ${band}`)
    ok('preexisting-kept', preKept && WI.preBlocks[1] == 'spore:infested_stone', 'заражённый блок внутри, поставленный до этого, остался: ' + blk(level, 70, 148, 5))
    // биомы: заражение их не трогает
    var b1 = String(level.getBiome(new BlockPos(100, 149, 0)).unwrapKey().get().location())
    WI.biome = b1
    info('биом под заражением (100,149,0): ' + b1)
  }

  // ---- 2. Настоящий курган (кулдаун 1 с, радиус 30 из EXTRA_CONFIG), x=113: дотянулся бы до x=83 ----
  if (T == 400) {
    var inR = count(level, 60, 96, -12, 12, 148, 151, (x, b) => infected(b)), outR = count(level, 97, 140, -12, 12, 148, 151, (x, b) => infected(b))
    var cR = count(level, 60, 96, 188, 212, 148, 151, (x, b) => infected(b)), cO = count(level, 97, 140, 188, 212, 148, 151, (x, b) => infected(b))
    var mm = ents(level, 'spore:mound', 113, 0, 20), cm = ents(level, 'spore:mound', 113, 200, 20)
    info(`настоящие курганы: у оберега живо ${mm.length} (x=${mm.length ? mm[0].getX().toFixed(1) : '-'}), контрольных ${cm.length}`)
    info(`настоящий тик: зона (x≤96)=${inR}, снаружи (x≥97)=${outR}; контроль x≤96=${cR}, x≥97=${cO}`)
    var wasInit = count(level, 60, 96, -12, 12, 148, 151, (x, b) => infected(b))
    ok('real-mound-control-spreads', cR > 0, `контрольный курган вне оберега заразил x≤96: ${cR} (тест показателен)`)
    ok('real-mound-inside-zero', cR > 0 && inR <= 2, `курган снаружи, радиус 30: заражённых в зоне=${inR} (2 — блоки, поставленные командой)`)
    ok('real-mound-outside-works', outR > 0, `снаружи=${outR}`)
    ok('preexisting-kept-late', blk(level, 70, 148, 5) == 'spore:infested_stone', 'поставленное командой заражение внутри через ~17 с: ' + blk(level, 70, 148, 5))
    var b2 = String(level.getBiome(new BlockPos(100, 149, 0)).unwrapKey().get().location())
    ok('biome-unchanged', b2 == WI.biome, `${WI.biome} → ${b2}`)
    ents(level, 'spore:mound', 113, 0, 30).forEach(e => e.discard()); ents(level, 'spore:mound', 113, 200, 30).forEach(e => e.discard())
  }

  // ---- 3. Игрок в выживании: заражение снаружи, вход в зону, время лечения, что остаётся ----
  if (T == 410) {
    var Fk = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
    var GP = Java.loadClass('com.mojang.authlib.GameProfile'), UU = Java.loadClass('java.util.UUID')
    var GT = Java.loadClass('net.minecraft.world.level.GameType')
    var MEI = Java.loadClass('net.minecraft.world.effect.MobEffectInstance')
    try {
      var fp = Fk.get(level, new GP(UU.fromString('00000000-0000-0000-0000-0000000052aa'), 'wi_survivor'))
      fp.gameMode.changeGameModeForPlayer(GT.SURVIVAL)
      fp.setPos(150.5, 150, 0.5)
      level.addNewPlayer(fp)
      WI.fp = fp
      info('игрок: режим=' + fp.gameMode.getGameModeForPlayer() + ' в списке мира=' + level.players.contains(fp) + ' x=' + fp.getX())
      var applied = [], refused = []
      WI.allEffects.forEach(id => {
        var ef = Reg.MOB_EFFECTS.getValue(new RL(id))
        var r = fp.addEffect(new MEI(ef, 2400, 0))
        ;(effects(fp).includes(id) ? applied : refused).push(id)
      })
      WI.appliedOut = applied
      info('снаружи (x=150) наложились: ' + applied.join(' ') + '; не наложились: ' + refused.join(' '))
      ok('player-infected-outside', applied.includes('spore:mycelium_ef') && applied.includes('srpsystem:infection'), 'мицелий и заражение SRP на игроке снаружи')
      WI.enterTick = T + 5
    } catch (e) { ok('player-setup', false, 'ошибка ' + e) }
  }
  if (WI.fp && T == WI.enterTick) {
    WI.fp.setPos(10.5, 150, 10.5)
    WI.tIn = T
    WI.before = effects(WI.fp)
    WI.firstClean = null
  }
  if (WI.fp && WI.tIn && T > WI.tIn && T <= WI.tIn + 12) {
    var left = effects(WI.fp)
    var gone = WI.before.filter(x => !left.includes(x))
    if (WI.firstClean === null && !left.includes('spore:mycelium_ef') && !left.includes('srpsystem:infection')) WI.firstClean = T - WI.tIn
    WI.after = left; WI.gone = gone
  }
  if (WI.fp && WI.tIn && T == WI.tIn + 13) {
    ok('player-cured-on-enter', WI.firstClean !== null, `заражение снято через ${WI.firstClean} тиков после входа в зону (проверка раз в 5 тиков)`)
    info('при входе в зону СНЯТЫ: ' + WI.gone.join(' '))
    info('при входе в зону ОСТАЛИСЬ: ' + WI.after.join(' '))
    // в зоне заражение не накладывается заново
    var MEI = Java.loadClass('net.minecraft.world.effect.MobEffectInstance')
    var re = [], refusedIn = []
    WI.allEffects.forEach(id => { var ef = Reg.MOB_EFFECTS.getValue(new RL(id)); if (WI.after.includes(id)) return; WI.fp.addEffect(new MEI(ef, 2400, 0)); (effects(WI.fp).includes(id) ? re : refusedIn).push(id) })
    info('в зоне (x=10) наложились: ' + re.join(' ') + '; отклонены: ' + refusedIn.join(' '))
    ok('player-reinfect-denied', !re.includes('spore:mycelium_ef') && !re.includes('srpsystem:infection'), 'в зоне мицелий и заражение SRP не накладываются')
    WI.reinf = re
    WI.fp.removeAllEffects()
    WI.edgeTick = T + 3
  }
  // край зоны: x=96.4 — внутри (снимается), x=97.4 — снаружи (не снимается)
  if (WI.fp && WI.edgeTick && T == WI.edgeTick) {
    var MEI = Java.loadClass('net.minecraft.world.effect.MobEffectInstance')
    WI.fp.setPos(97.4, 150, 0.5)
    WI.fp.addEffect(new MEI(Reg.MOB_EFFECTS.getValue(new RL('spore:mycelium_ef')), 2400, 0))
    WI.has97 = effects(WI.fp).includes('spore:mycelium_ef')
    WI.edgeTick2 = T + 12
  }
  if (WI.fp && WI.edgeTick2 && T == WI.edgeTick2) {
    var still97 = effects(WI.fp).includes('spore:mycelium_ef')
    ok('edge-x97-not-cured', WI.has97 && still97, `x=97.4: наложился=${WI.has97}, через 12 тиков остался=${still97} (за границей зоны лечения нет)`)
    var MEI = Java.loadClass('net.minecraft.world.effect.MobEffectInstance')
    WI.fp.setPos(96.4, 150, 0.5)
    WI.edgeTick3 = T + 12
  }
  if (WI.fp && WI.edgeTick3 && T == WI.edgeTick3) {
    var still96 = effects(WI.fp).includes('spore:mycelium_ef')
    ok('edge-x96-cured', !still96, `x=96.4: заражение после входа снято=${!still96}`)
    // грибная костная мука (Spore) в руке игрока: это действие игрока, а не тик паразита — оберег его не блокирует
    try {
      var UOC = Java.loadClass('net.minecraft.world.item.context.UseOnContext'), BHR = Java.loadClass('net.minecraft.world.phys.BlockHitResult'), V3 = Java.loadClass('net.minecraft.world.phys.Vec3')
      var Dir = Java.loadClass('net.minecraft.core.Direction'), IS = Java.loadClass('net.minecraft.world.item.ItemStack'), Hand = Java.loadClass('net.minecraft.world.InteractionHand')
      var bm = Reg.ITEMS.getValue(new RL('spore:fungal_bonemeal'))
      var useAt = x => { var st = new IS(bm, 16); WI.fp.setItemInHand(Hand.MAIN_HAND, st)
        for (let i = 0; i < 6; i++) bm.useOn(new UOC(level, WI.fp, Hand.MAIN_HAND, st, new BHR(new V3(x + 0.5, 150, -9.5), Dir.UP, new BlockPos(x, 149, -9), false)))
        return count(level, x - 6, x + 6, -15, -3, 148, 152, (xx, b) => infected(b)) }
      var inB = useAt(70), outB = useAt(125)
      info(`костная мука игрока: в зоне (x=70) заражено ${inB}, снаружи (x=125) ${outB} (оберег действие игрока не блокирует)`)
    } catch (e) { info('костная мука: ошибка ' + e) }
    level.removePlayerImmediately(WI.fp, Java.loadClass('net.minecraft.world.entity.Entity$RemovalReason').DISCARDED)
    WI.fp = null
  }

  // ---- 4. Мобы: заражённые мобы заходят в зону; паразиты разных видов телепортом внутрь ----
  if (T == 470) {
    var MEI = Java.loadClass('net.minecraft.world.effect.MobEffectInstance')
    server.runCommandSilent('summon minecraft:villager 150 150 -8 {NoAI:1b,PersistenceRequired:1b,CustomName:\'"wi_vil"\'}')
    server.runCommandSilent('summon minecraft:zombie 150 150 -10 {NoAI:1b,PersistenceRequired:1b,CustomName:\'"wi_zom"\'}')
    ;['wi_vil', 'wi_zom'].forEach(n => { server.runCommandSilent(`effect give @e[name=${n}] spore:mycelium_ef 600 0`); server.runCommandSilent(`effect give @e[name=${n}] srpsystem:infection 600 0`) })
    var types = ['spore:inf_human', 'spore:inf_villager', 'spore:knight', 'spore:leaper', 'spore:mound', 'spore:inf_husk']
    WI.types = types.filter(t => Reg.ENTITY_TYPES.containsKey(new RL(t)))
    WI.types.forEach((t, i) => server.runCommandSilent(`summon ${t} 150 150 ${20 + i * 3} {PersistenceRequired:1b,CustomName:'"wi_p${i}"'}`))
    info('виды паразитов для теста: ' + WI.types.join(' '))
  }
  if (T == 472) {
    var v = ents(level, 'minecraft:villager', 150, -8, 3), z = ents(level, 'minecraft:zombie', 150, -10, 3)
    ok('mob-infected-outside', v.length == 1 && z.length == 1 && effects(v[0]).includes('spore:mycelium_ef') && effects(z[0]).includes('srpsystem:infection'), `житель ${v.length && effects(v[0])}; зомби ${z.length && effects(z[0])}`)
    v.forEach(e => e.teleportTo(20.5, 150, 20.5)); z.forEach(e => e.teleportTo(24.5, 150, 20.5))
    WI.types.forEach((t, i) => server.runCommandSilent(`tp @e[name=wi_p${i}] ${30 + i * 3} 150 30`))
    WI.tm = T
  }
  if (T == 490) {
    var v = ents(level, 'minecraft:villager', 20, 20, 4), z = ents(level, 'minecraft:zombie', 24, 20, 4)
    ok('mob-cured-in-zone', v.length == 1 && z.length == 1 && !effects(v[0]).includes('spore:mycelium_ef') && !effects(z[0]).includes('srpsystem:infection') && !effects(z[0]).includes('spore:mycelium_ef'), `после входа: житель ${v.length && effects(v[0])}; зомби ${z.length && effects(z[0])}`)
    v.forEach(e => e.discard()); z.forEach(e => e.discard())
    var alive = []; WI.types.forEach((t, i) => { var f = ents(level, t, 30 + i * 3, 30, 2); if (f.length) alive.push(t) })
    ok('parasites-removed-in-zone', alive.length == 0, `осталось внутри зоны: ${alive.join(' ') || 'никого'} (из ${WI.types.length})`)
  }

  // ---- 5. Другие измерения: оберег действует только в том измерении, где стоит (ждём готовности чанков, а не фиксированные тики) ----
  if (T == 500) {
    ;['minecraft:the_nether', 'minecraft:the_end'].forEach(d => {
      server.runCommandSilent(`execute in ${d} run forceload add 0 -16 16 16`)
      server.runCommandSilent(`execute in ${d} run fill 0 98 -8 16 99 8 minecraft:stone`)
      server.runCommandSilent(`execute in ${d} run fill 0 100 -8 16 110 8 minecraft:air`)
    })
    WI.nStage = 1; WI.nTries = 0
  }
  var nether = lv('minecraft:the_nether'), end = lv('minecraft:the_end')
  var spreadN = () => { for (let i = 0; i < 6; i++) { Guard().enter(nether, WI.m2); try { WI.m2.SpreadInfection(nether, 8.0, new BlockPos(8, 99, 0)) } finally { Guard().exit() } }
    return count(nether, 0, 16, -8, 8, 98, 101, (x, b) => infected(b)) }
  if (WI.nStage == 1 && T % 10 == 0) {
    var m2 = ents(nether, 'spore:mound', 8, 0, 6)[0]
    if (!m2 && WI.nTries++ < 30) server.runCommandSilent('execute in minecraft:the_nether run summon spore:mound 8 100 0 {PersistenceRequired:1b,NoAI:1b}')
    else {
      WI.m2 = m2
      ok('nether-mound-spawns-no-ward', !!m2, 'в Нижнем мире на координатах оберега Верхнего (x=8,z=0) курган появился (попыток ' + WI.nTries + '): оберег Верхнего мира Нижний не закрывает')
      ok('ward-index-per-dimension', Api().isProtected(level, 8, 0) && !Api().isProtected(nether, 8, 0) && !Api().isProtected(end, 8, 0), 'isProtected: Верхний=' + Api().isProtected(level, 8, 0) + ' Нижний=' + Api().isProtected(nether, 8, 0) + ' Энд=' + Api().isProtected(end, 8, 0))
      if (m2) { WI.netherNo = spreadN(); ok('nether-no-ward-spreads', WI.netherNo > 0, 'Нижний мир без оберега: заражённых блоков=' + WI.netherNo) }
      server.runCommandSilent('execute in minecraft:the_nether run setblock 8 100 -4 disaster_ward:ward')   // оберег в Нижнем (над платформой)
      WI.nStage = 2; WI.nT = T
    }
  }
  if (WI.nStage == 2 && T >= WI.nT + 20) {
    ok('nether-ward-placed', blk(nether, 8, 100, -4) == 'disaster_ward:ward')
    server.runCommandSilent('execute in minecraft:the_nether run fill 0 98 -8 16 99 8 minecraft:stone')   // сбросить заражённое (и траву над платформой)
    server.runCommandSilent('execute in minecraft:the_nether run fill 0 100 -8 16 110 8 minecraft:air')
    server.runCommandSilent('execute in minecraft:the_nether run setblock 8 100 -4 disaster_ward:ward')
    if (WI.m2) { WI.netherYes = spreadN(); ok('nether-ward-blocks', WI.netherYes == 0, `Нижний мир с оберегом: заражённых блоков=${WI.netherYes}`) }   // курган уже убран оберегом, но SpreadInfection вызываем в том же контексте
    ok('nether-mound-removed', ents(nether, 'spore:mound', 8, 0, 6).length == 0, 'курган внутри зоны оберега Нижнего мира убран')
    server.runCommandSilent('execute in minecraft:the_end run setblock 8 100 -4 disaster_ward:ward')
    WI.nStage = 3; WI.nT = T
  }
  if (WI.nStage == 3 && T >= WI.nT + 10) {
    ok('end-ward', Api().isProtected(end, 8, 0), 'оберег ставится и в Энде')
    server.runCommandSilent('execute in minecraft:the_nether run setblock 8 100 -4 minecraft:air')
    server.runCommandSilent('execute in minecraft:the_end run setblock 8 100 -4 minecraft:air')
    WI.nStage = 4
    WI.fStage = 1; WI.fT = T; WI.fTries = 0
    server.runCommandSilent('forceload add 3000 3000')
    server.runCommandSilent('forceload add 3088 3000 3095 3010')     // угол зоны (x=3088..3095, внутри: |x-3000| ≤ 96)
    server.runCommandSilent('forceload add 3300 3000 3305 3010')     // площадка снаружи
  }

  // ---- 6. Выгруженные чанки: оберег далеко (3000,3000), его чанк без загрузки; заражённые заходят в загруженный угол зоны ----
  var fv = () => ents(level, 'minecraft:villager', 3300, 3002, 3)[0], fh = () => ents(level, 'spore:inf_human', 3300, 3006, 3)[0]
  if (WI.fStage == 1 && T >= WI.fT + 20 && T % 5 == 0) {
    if (blk(level, 3000, 150, 3000) != 'disaster_ward:ward') server.runCommandSilent('setblock 3000 150 3000 disaster_ward:ward')
    if (!fv() && WI.fTries < 40) server.runCommandSilent('summon minecraft:villager 3300 150 3002 {NoAI:1b,PersistenceRequired:1b}')
    if (!fh() && WI.fTries < 40) server.runCommandSilent('summon spore:inf_human 3300 150 3006 {NoAI:1b,PersistenceRequired:1b}')
    WI.fTries++
    if (fv() && fh()) {
      var MEI = Java.loadClass('net.minecraft.world.effect.MobEffectInstance')
      fv().addEffect(new MEI(Reg.MOB_EFFECTS.getValue(new RL('spore:mycelium_ef')), 6000, 0))
      if (effects(fv()).includes('spore:mycelium_ef') && blk(level, 3000, 150, 3000) == 'disaster_ward:ward') { WI.farOk = true; WI.fStage = 2; server.runCommandSilent('forceload remove 3000 3000') }
    }
    if (WI.fTries >= 40 && WI.fStage == 1) { ok('far-ward-setup', false, 'не удалось подготовить площадку (жители/ward)'); WI.fStage = 9; WI.finished = true }
  }
  if (WI.fStage == 2 && T % 2 == 0 && !level.isLoaded(new BlockPos(3000, 150, 3000))) {
    WI.farUnloaded = T; WI.fStage = 3
    fv().teleportTo(3090.5, 150, 3002.5); fh().teleportTo(3092.5, 150, 3004.5)
  }
  if (WI.fStage == 3 && T == WI.farUnloaded + 100) {
    var far = ents(level, 'minecraft:villager', 3090, 3002, 3)[0], farp = ents(level, 'spore:inf_human', 3092, 3004, 3)
    var loadedNow = level.isLoaded(new BlockPos(3000, 150, 3000))
    info(`далёкий оберег: стоит=${WI.farOk}, чанк оберега выгружен на тике ${WI.farUnloaded}, через 100 тиков загружен=${loadedNow}; житель в зоне: ${far ? effects(far).filter(x => x.startsWith('spore') || x.startsWith('srp')).join(',') || 'без заражения' : 'нет'}; паразитов в зоне: ${farp.length}`)
    ok('far-ward-chunk-unloaded-cure', far && !effects(far).includes('spore:mycelium_ef'), `чанк оберега загружен=${loadedNow}; житель вылечен=${far ? !effects(far).includes('spore:mycelium_ef') : 'нет жителя'}`)
    ok('far-ward-chunk-unloaded-parasite', farp.length == 0, `паразитов в зоне при выгруженном чанке оберега: ${farp.length}`)
    ok('far-ward-chunk-unloaded-block-guard', Api().isProtected(level, 3090, 3002), 'защита блоков по таблице оберегов от загруженности чанка оберега не зависит')
    if (far) far.discard(); farp.forEach(e => e.discard())
    server.runCommandSilent('setblock 3000 150 3000 minecraft:air')
    WI.finished = true
  }
  if (WI.fStage == 2 && T > WI.fT + 800) { ok('far-ward-chunk-unloaded-cure', false, 'чанк оберега так и не выгрузился'); WI.finished = true; WI.fStage = 9 }
  if (WI.finished && !WI.said) { WI.said = true; console.info('WICHECK done') }
})
