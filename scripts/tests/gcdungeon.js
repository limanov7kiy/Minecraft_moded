// Тест Galacticraft Revival (mods-src/galacticraft-revival): лунный данж GC на Луне Ad Astra.
// Ставит данж командой мода, проверяет призыватель босса и запертый сундук сокровищ, призывает скелета-босса
// у призывателя (тот должен «подхватить» босса), убивает его, ждёт конца анимации смерти (200 тиков) и проверяет:
// сундук наполнен и содержит схему, выпал ключ T1, чужой предмет сундук не открывает, ключ — открывает.
// Вывод: GCTEST <проверка> OK|FAIL <подробности>. Нужен сервер, живущий ~2 мин после Done (в новом контейнере тики идут медленно)
// (TEST_COMMANDS=<абсолютный путь>/scripts/tests/gcdungeon_commands.txt). Наши классы — только через их
// собственные методы (Rhino не видит унаследованные SRG-имена), ванильные — как обычно.
let gc = null
const gcReport = (id, ok, info) => console.info(`GCTEST ${id} ${ok ? 'OK' : 'FAIL'} ${info}`)

function gcPlace(server, level, moon) {
  const n = moon('gcrevival place_dungeon 0 64 0')
  const cmds = Java.loadClass('dev.mcmoded.gcrevival.DungeonCommands')
  const sp = cmds.lastBossSpawner, chestPos = cmds.lastTreasureChest
  gcReport('place', sp != null && chestPos != null, `result=${n} spawner=${sp} chest=${chestPos}`)
  if (sp == null || chestPos == null) return null
  const spawner = level.getBlockEntity(sp)
  gcReport('spawner', spawner != null && String(spawner.bossType) == 'entity.gcrevival.skeleton_boss' && String(spawner.getChestPos()) == String(chestPos),
    `be=${spawner} boss=${spawner ? spawner.bossType : '-'} chestPos=${spawner ? spawner.getChestPos() : '-'}`)
  const floor = String(level.getBlockState(sp.below()).getBlock().getDescriptionId())
  gcReport('bricks', floor == 'block.gcrevival.moon_dungeon_brick', `floor=${floor}`)
  const chest = level.getBlockEntity(chestPos)
  gcReport('chest_locked', chest != null && chest.isLocked(), `chest=${chest} contents=${chest ? chest.describeContents() : '-'}`)
  // ванильный /locate в сборке асинхронный (Async Locator) — ищем напрямую
  const loc = cmds.findNearest(level, new (Java.loadClass('net.minecraft.core.BlockPos'))(1000, 64, 1000), 100)
  gcReport('locate', loc != null, `natural dungeon near (1000, 1000): ${loc}`)
  if (loc != null) {
    // пол входной комнаты (y=25) в центре стартового чанка — после генерации чанка там должен быть кирпич данжа
    const bp = new (Java.loadClass('net.minecraft.core.BlockPos'))(((loc.getX() >> 4) << 4) + 2, 25, ((loc.getZ() >> 4) << 4) + 2)
    level.getChunk(bp)
    const b = String(level.getBlockState(bp).getBlock().getDescriptionId())
    gcReport('natural_gen', b == 'block.gcrevival.moon_dungeon_brick', `block at ${bp}: ${b}`)
  }
  // чанки комнаты босса и сокровищницы должны тикать, иначе смерть босса не доиграет
  moon(`forceload add ${sp.getX()} ${sp.getZ()}`)
  moon(`forceload add ${chestPos.getX()} ${chestPos.getZ()}`)
  moon(`summon gcrevival:skeleton_boss ${sp.getX() + 0.5} ${sp.getY() + 1} ${sp.getZ() + 0.5}`)
  return { sp: sp, chestPos: chestPos, spawner: spawner }
}

function gcKill(moon) {
  const boss = gc.spawner.boss
  gcReport('boss_spawn', boss != null && String(boss.describe()).startsWith('150.0/150.0/1 bow'), `spawned=${gc.spawner.spawned} boss=${boss ? boss.describe() : '-'}`)
  moon('kill @e[type=gcrevival:skeleton_boss]')
}

function gcCheck(level) {
  const chest = level.getBlockEntity(gc.chestPos)
  const c = String(chest.describeContents()).split('/')
  gcReport('treasure', chest.isLocked() && c[1] == 'true' && Number(c[0]) >= 3, `locked=${chest.isLocked()} stacks=${c[0]} schematic=${c[1]}`)
  gcReport('boss_defeated', gc.spawner.isBossDefeated && gc.spawner.boss == null, `defeated=${gc.spawner.isBossDefeated}`)
  const keys = level.getEntitiesOfClass(Java.loadClass('net.minecraft.world.entity.item.ItemEntity'),
    Java.loadClass('net.minecraft.world.phys.AABB').ofSize(gc.sp.getCenter(), 40, 20, 40))
    .stream().filter(e => String(e.getItem().getItem().getDescriptionId()) == 'item.gcrevival.key_t1').count()
  gcReport('key_drop', keys == 1, `keys=${keys}`)

  const fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory').getMinecraft(level)
  const hand = Java.loadClass('net.minecraft.world.InteractionHand').MAIN_HAND
  const hitType = Java.loadClass('net.minecraft.world.phys.BlockHitResult')
  const up = Java.loadClass('net.minecraft.core.Direction').UP
  fake.setPos(gc.chestPos.getX() + 0.5, gc.chestPos.getY() + 1, gc.chestPos.getZ() + 0.5)
  const hit = new hitType(gc.chestPos.getCenter(), up, gc.chestPos, false)
  fake.setItemInHand(hand, Item.of('minecraft:stick'))
  level.getBlockState(gc.chestPos).use(level, fake, hand, hit)
  gcReport('wrong_key', chest.isLocked(), `locked=${chest.isLocked()}`)
  fake.setItemInHand(hand, Item.of('gcrevival:key_t1'))
  level.getBlockState(gc.chestPos).use(level, fake, hand, hit)
  gcReport('unlock', !chest.isLocked() && fake.getMainHandItem().isEmpty(), `locked=${chest.isLocked()} hand=${fake.getMainHandItem()}`)
}

ServerEvents.tick(event => {
  const server = event.server
  const t = server.tickCount
  if (t != 100 && t != 140 && t != 420) return
  if (!Platform.isLoaded('gcrevival')) return
  const level = server.getLevel('ad_astra:moon')
  const moon = (cmd) => server.runCommandSilent('execute in ad_astra:moon run ' + cmd)
  if (t == 100) gc = gcPlace(server, level, moon)
  else if (gc != null && t == 140) gcKill(moon)
  else if (gc != null) gcCheck(level)
})
