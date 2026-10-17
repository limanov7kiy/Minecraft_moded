// Тест Falling Tree + FallingTreeDamageMixin (mods-src/mm-compat): режим FALL_BLOCK, прочность «всё или ничего».
// Рубим дубы железным топором фейковым игроком Forge через ServerPlayerGameMode.destroyBlock (то же событие,
// что при игре). Вывод: FALLINGTREE <случай> OK|FAIL <подробности>
// Запускать в ЧИСТОМ мире (rm -rf <сервер>/world): упавшие брёвна прошлых прогонов остаются в мире, попадают в дерево и искажают счёт.
let st = null
ServerEvents.tick(event => {
  const server = event.server
  const level = server.getLevel('minecraft:overworld')
  if (st) {
    if (server.tickCount != st.at) return
    st.cases.forEach(c => {
      console.info(`FALLINGTREE ${c.name} ${c.check(c) ? 'OK' : 'FAIL'} left=${c.left} damage=${c.dmg} destroyBlock=${c.ok}${c.name == 'natural_leaves_info' ? ' leaves_left=' + st.leaves(c.x) : ''}`)
    })
    st = null
    return
  }
  if (server.tickCount != 200) return
  const BP = Java.loadClass('net.minecraft.core.BlockPos')
  const Fk = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const GT = Java.loadClass('net.minecraft.world.level.GameType')
  const Pose = Java.loadClass('net.minecraft.world.entity.Pose')
  const Hnd = Java.loadClass('net.minecraft.world.InteractionHand')
  const fake = Fk.getMinecraft(level)
  fake.gameMode.changeGameModeForPlayer(GT.SURVIVAL)
  server.runCommandSilent('forceload add -20 -20 220 40')
  // площадка в небе, у каждого случая свой участок по X
  server.runCommandSilent('fill -20 199 -20 220 199 40 minecraft:stone')
  server.runCommandSilent('fill -20 200 -20 220 260 40 minecraft:air')
  const tree = (x, n, persistent) => {   // ствол из n брёвен + крона (persistent=true: листва детерминированно не в счёт)
    server.runCommandSilent(`fill ${x} 200 0 ${x} ${199 + n} 0 minecraft:oak_log`)
    server.runCommandSilent(`fill ${x - 2} ${200 + n} -2 ${x + 2} ${201 + n} 2 minecraft:oak_leaves[persistent=${persistent !== false}]`)
  }
  const axe = dmg => { const s = Item.of('minecraft:iron_axe').withNBT({ Damage: dmg }); fake.setItemInHand(Hnd.MAIN_HAND, s); return s }
  const logs = (x1, y1, z1, x2, y2, z2) => {
    let n = 0
    for (let x = x1; x <= x2; x++) for (let y = y1; y <= y2; y++) for (let z = z1; z <= z2; z++)
      if (String(level.getBlock(x, y, z).id) == 'minecraft:oak_log') n++
    return n
  }
  const cases = []
  let slot = 0
  const run = (name, _x, shape, dmg, sneak, check) => {
      const x = 20 * (slot++)
    shape(x)
    axe(dmg)
    fake.setShiftKeyDown(sneak)
      fake.setPose(sneak ? Pose.CROUCHING : Pose.STANDING)
    const before = level.getBlock(x, 200, 0).id
    const ok = fake.gameMode.destroyBlock(new BP(x, 200, 0))
    // считаем сразу: упавшие брёвна (FALL_BLOCK) через несколько тиков могут приземлиться обратно блоками
      const left = name == 'over_300' ? logs(x, 200, 0, x + 7, 205, 7) : logs(x, 200, 0, x, 205, 0)
      cases.push({ name: name, x: x, check: check, ok: ok, before: before, left: left, dmg: fake.getMainHandItem().getDamageValue() })
  }
  // 1. полная прочность: 6 брёвен, прочность снимается за каждое бревно (множитель 1)
  run('full_durability', 0, x => tree(x, 6), 0, false, c => c.left == 0 && c.dmg == 6)
  // 2. прочности (осталось 4) не хватает на 6 брёвен: ломается один блок, ствол стоит
  run('low_durability', 30, x => tree(x, 6), 247, false, c => c.left == 5 && c.dmg == 248)   // осталось 3 из 250
  // 3. присед: один блок
  run('sneak', 60, x => tree(x, 6), 0, true, c => c.left == 5)
  // 4. больше 300 брёвен (куб 8x6x8 = 384): один блок
  run('over_300', 90, x => {
    server.runCommandSilent(`fill ${x} 200 0 ${x + 7} 205 7 minecraft:oak_log`)
    server.runCommandSilent(`fill ${x} 206 0 ${x + 7} 207 7 minecraft:oak_leaves[persistent=true]`)
  }, 0, false, c => c.left == 383)
  // 5. прочности ровно хватает с запасом в 1 (осталось 7 из 250 на 6 брёвен): дерево падает целиком, топор остаётся с 1
  run('exact_plus_one', 120, x => tree(x, 6), 243, false, c => c.left == 0 && c.dmg == 249)
  // 6. прочности хватает ровно (осталось 6 на 6 брёвен): preserve не даёт добить топор, один блок
  run('exact_no_spare', 150, x => tree(x, 6), 244, false, c => c.left == 5 && c.dmg == 245)
  // повторы полной рубки на том же месте: прочность должна сниматься одинаково
  for (let i = 1; i <= 3; i++) run('full_repeat_' + i, 0, x => tree(x, 6), 0, false, c => c.left == 0 && c.dmg == 6)
  // 7. справочно, без проверки: живая листва (persistent=false) — сколько прочности уходит и сколько листьев остаётся
  run('natural_leaves_info', 180, x => tree(x, 6, false), 0, false, c => true)
  fake.setShiftKeyDown(false)
  fake.setPose(Pose.STANDING)
  const leaves = x => { let n = 0; for (let ix = x - 2; ix <= x + 2; ix++) for (let iy = 206; iy <= 207; iy++) for (let iz = -2; iz <= 2; iz++) if (String(level.getBlock(ix, iy, iz).id) == 'minecraft:oak_leaves') n++; return n }
  st = { cases: cases, leaves: leaves, logs: logs, fake: fake, at: server.tickCount + 5 }
})
