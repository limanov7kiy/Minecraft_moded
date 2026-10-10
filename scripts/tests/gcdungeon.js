// Тест Galacticraft Revival (mods-src/galacticraft-revival): лунный данж GC на Луне Ad Astra.
// Ставит данж командой мода, проверяет призыватель босса и запертый сундук сокровищ, призывает и убивает
// скелета-босса, ждёт конца анимации смерти (200 тиков) и проверяет: сундук наполнен и содержит схему,
// выпал ключ T1, ключ открывает сундук. Вывод: GCTEST <проверка> OK|FAIL <подробности>.
// Нужен сервер, живущий ~40 с после Done (TEST_COMMANDS=scripts/tests/gcdungeon_commands.txt).
let gc = null
ServerEvents.tick(event => {
  const server = event.server
  const t = server.tickCount
  if (t != 100 && t != 400) return
  if (!Platform.isLoaded('gcrevival')) return
  const report = (id, ok, info) => console.info(`GCTEST ${id} ${ok ? 'OK' : 'FAIL'} ${info}`)
  const Cmd = Java.loadClass('dev.mcmoded.gcrevival.DungeonCommands')
  const Reg = Java.loadClass('dev.mcmoded.gcrevival.ModRegistry')
  const level = server.getLevel('ad_astra:moon')

  if (t == 100) {
    const n = server.runCommandSilent('execute in ad_astra:moon run gcrevival place_dungeon 0 64 0')
    const sp = Cmd.lastBossSpawner, chestPos = Cmd.lastTreasureChest
    report('place', sp != null && chestPos != null, `result=${n} spawner=${sp} chest=${chestPos}`)
    if (sp == null || chestPos == null) return
    const spawner = level.getBlockEntity(sp)
    report('spawner', spawner != null && String(spawner.bossType) == 'entity.gcrevival.skeleton_boss' && String(spawner.getChestPos()) == String(chestPos),
      `be=${spawner} boss=${spawner ? spawner.bossType : '-'} chestPos=${spawner ? spawner.getChestPos() : '-'}`)
    const floor = String(level.getBlockState(sp.below()).getBlock().getDescriptionId())
    report('bricks', floor == 'block.gcrevival.moon_dungeon_brick', `floor=${floor}`)
    const chest = level.getBlockEntity(chestPos)
    report('chest_locked', chest != null && chest.isLocked(), `chest=${chest}`)
    const loc = server.runCommandSilent('execute in ad_astra:moon run locate structure gcrevival:moon_dungeon')
    report('locate', loc > 0, `locate result=${loc}`)

    // чанки комнаты босса и сокровищницы должны тикать, иначе смерть босса не доиграет
    server.runCommandSilent(`execute in ad_astra:moon run forceload add ${sp.getX()} ${sp.getZ()}`)
    server.runCommandSilent(`execute in ad_astra:moon run forceload add ${chestPos.getX()} ${chestPos.getZ()}`)
    const boss = Reg.SKELETON_BOSS.get().create(level)
    boss.setPos(sp.getX() + 0.5, sp.getY() + 1, sp.getZ() + 0.5)
    boss.finalizeSpawn(level, level.getCurrentDifficultyAt(sp), Java.loadClass('net.minecraft.world.entity.MobSpawnType').SPAWNER, null, null)
    level.addFreshEntity(boss)
    boss.onBossSpawned(spawner)
    report('boss_spawn', boss.isAlive() && boss.getMaxHealth() == 150, `hp=${boss.getHealth()}/${boss.getMaxHealth()} weapon=${boss.getMainHandItem()}`)
    boss.kill()
    gc = { sp: sp, chestPos: chestPos, spawner: spawner }
    return
  }

  if (gc == null) return
  const chest = level.getBlockEntity(gc.chestPos)
  let items = 0, schematic = false
  for (let i = 0; i < chest.getContainerSize(); i++) {
    const st = chest.getItem(i)
    if (!st.isEmpty()) { items++; if (String(st.getItem().getDescriptionId()).startsWith('item.gcrevival.schematic')) schematic = true }
  }
  report('treasure', chest.isLocked() && schematic && items >= 3, `locked=${chest.isLocked()} stacks=${items} schematic=${schematic}`)
  report('boss_defeated', gc.spawner.isBossDefeated, `defeated=${gc.spawner.isBossDefeated}`)
  const keys = level.getEntitiesOfClass(Java.loadClass('net.minecraft.world.entity.item.ItemEntity'),
    Java.loadClass('net.minecraft.world.phys.AABB').ofSize(gc.sp.getCenter(), 40, 20, 40))
    .stream().filter(e => String(e.getItem().getItem().getDescriptionId()) == 'item.gcrevival.key_t1').count()
  report('key_drop', keys == 1, `keys=${keys}`)

  const Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const Hand = Java.loadClass('net.minecraft.world.InteractionHand')
  const Hit = Java.loadClass('net.minecraft.world.phys.BlockHitResult')
  const Dir = Java.loadClass('net.minecraft.core.Direction')
  const fake = Fake.getMinecraft(level)
  fake.setPos(gc.chestPos.getX() + 0.5, gc.chestPos.getY() + 1, gc.chestPos.getZ() + 0.5)
  const hit = new Hit(gc.chestPos.getCenter(), Dir.UP, gc.chestPos, false)
  fake.setItemInHand(Hand.MAIN_HAND, Item.of('minecraft:stick'))
  level.getBlockState(gc.chestPos).use(level, fake, Hand.MAIN_HAND, hit)
  report('wrong_key', chest.isLocked(), `locked=${chest.isLocked()}`)
  fake.setItemInHand(Hand.MAIN_HAND, Item.of('gcrevival:key_t1'))
  level.getBlockState(gc.chestPos).use(level, fake, Hand.MAIN_HAND, hit)
  report('unlock', !chest.isLocked() && fake.getMainHandItem().isEmpty(), `locked=${chest.isLocked()} hand=${fake.getMainHandItem()}`)
})
