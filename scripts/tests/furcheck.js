// Тест порта мобов Fish's Undead Rising (поток 27, этап ND-2): каждый моб призывается на своей площадке, через
// 100 тиков жив и на месте; у каждого есть яйцо призыва и таблица добычи. Вывод: FURCHECK <проверка> OK|FAIL <подробности>
// Запуск: TEST_COMMANDS=<файл «#sleep 420»> EXTRA_SERVER_SCRIPTS=<папка с копией> scripts/test-server.sh <папка вне репо>
const FUR = { phase: 0, at: 0 }
const FUR_MOBS = ['unburied', 'mummy', 'mycosis', 'frigid', 'forsaken', 'vespa', 'vespacocoon', 'banshee', 'parasite',
  'boneworm', 'salamander', 'weta', 'mimic']
ServerEvents.tick(event => { try { furTick(event) } catch (err) { console.info(`FURCHECK script FAIL error=${err}`) } })
const furTick = (event) => {
  const server = event.server
  const t = server.tickCount
  if (t < 200) return
  const level = server.getLevel('minecraft:overworld')
  const BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  const Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  const AABB = Java.loadClass('net.minecraft.world.phys.AABB')
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const FRegs = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  const report = (id, ok, info) => console.info(`FURCHECK ${id} ${ok ? 'OK' : 'FAIL'} ${info}`)
  // площадки по X: 300, 316, 332 … (по одной на моба), z = 0; стеклянная коробка 5×5, моб внутри
  const px = (i) => 300 + i * 16
  if (FUR.phase == 0) {
    server.runCommandSilent(`forceload add 290 -10 ${px(FUR_MOBS.length) + 10} 10`)
    server.runCommandSilent('time set midnight')
    FUR.phase = 1
    return
  }
  if (FUR.phase == 1) {
    if (t < 600) return
    if (!FUR_MOBS.every((m, i) => level.isPositionEntityTicking(new BlockPos(px(i), 200, 0)))) return
    // мир сохраняется между прогонами — убрать остатки прошлых запусков
    server.runCommandSilent(`kill @e[type=!minecraft:player,x=290,y=190,z=-10,dx=${FUR_MOBS.length * 16 + 20},dy=30,dz=20]`)
    FUR_MOBS.forEach((m, i) => {
      let x = px(i)
      server.runCommandSilent(`fill ${x - 3} 199 -3 ${x + 3} 206 3 minecraft:glass hollow`)
    })
    FUR.phase = 2; FUR.at = t
    return
  }
  if (FUR.phase == 2) {
    if (t < FUR.at + 40) return
    FUR_MOBS.forEach((m, i) => {
      let type = FRegs.ENTITY_TYPES.getValue(new RL('mod_lavacow', m))
      if (!type || String(FRegs.ENTITY_TYPES.getKey(type)) != `mod_lavacow:${m}`) { report(m, false, 'not_registered'); return }
      server.runCommandSilent(`summon mod_lavacow:${m} ${px(i) + 0.5} 200 0.5 {PersistenceRequired:1b}`)
    })
    FUR.phase = 3; FUR.at = t
    return
  }
  if (FUR.phase == 3 && t == FUR.at + 100) {
    FUR_MOBS.forEach((m, i) => {
      let x = px(i)
      let found = []
      level.getEntitiesOfClass(Entity, new AABB(x - 3, 198, -3, x + 4, 207, 4)).forEach(e => { if (String(e.getType()) == `mod_lavacow:${m}`) found.push(e) })
      let egg = FRegs.ITEMS.getValue(new RL('mod_lavacow', `spawn_egg_${m}`))
      let eggOk = egg && String(FRegs.ITEMS.getKey(egg)) != 'minecraft:air'
      let loot = server.getLootData().getLootTable(new RL('mod_lavacow', `entities/${m}`))
      let lootOk = loot && loot != Java.loadClass('net.minecraft.world.level.storage.loot.LootTable').EMPTY
      let e = found.length ? found[0] : null
      report(m, found.length == 1 && e.isAlive() && eggOk && lootOk,
        `alive=${found.length} hp=${e ? e.getHealth().toFixed(1) + '/' + e.getMaxHealth().toFixed(1) : '-'} egg=${eggOk} loot=${lootOk}`)
    })
    FUR.phase = 4
  }
}
