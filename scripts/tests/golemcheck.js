// Тест порта Wizardry Golems (поток 12): каждое заклинание Golemancy от фейкового игрока призывает нужных
// големов Extra Golems и они — миньоны Redux. Вывод: GOLEMCHECK <заклинание> OK|FAIL <подробности>
ServerEvents.tick(event => {
  const server = event.server
  if (server.tickCount != 220 && server.tickCount != 420) return
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const RM = Java.loadClass('net.minecraftforge.registries.RegistryManager')
  const Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const Ctx = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.PlayerCastContext')
  const Mods = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.SpellModifiers')
  const Hand = Java.loadClass('net.minecraft.world.InteractionHand')
  const Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  const level = server.getLevel('minecraft:overworld')
  const fake = Fake.getMinecraft(level)
  const golems = () => { let out = []; level.getEntitiesOfClass(Entity, fake.getBoundingBox().inflate(16)).forEach(e => {
    if (String(e.getType()) == 'golems:golem') out.push(e) }); return out }
  if (server.tickCount == 420) {
    // через 200 тиков миньоны живы (жизнь 600) — не исчезают сразу
    console.info(`GOLEMCHECK lifetime ${golems().length >= 3 ? 'OK' : 'FAIL'} alive=${golems().length}`)
    return
  }
  const reg = RM.ACTIVE.getRegistry(new RL('ebwizardry', 'spells'))
  server.runCommandSilent('forceload add -32 -32 32 32')
  server.runCommandSilent('fill -14 199 -14 14 199 14 minecraft:stone')
  server.runCommandSilent('fill -14 200 -14 14 212 14 minecraft:air')
  fake.setPos(0.5, 200, 0.5); fake.setOnGround(true)
  const cases = [['earth_golemancy', 'golems:clay', 3, false], ['fire_golemancy', 'golems:nether_brick', 2, false],
    ['ice_golemancy', 'golems:ice', 2, false], ['lightning_golemancy', 'wizardrygolems:thunderstone', 2, false],
    ['healing_golemancy', 'golems:gold', 1, false]]
  cases.forEach(c => {
    const spell = reg.getValue(new RL('wizardrygolems', c[0]))
    if (!spell) { console.info(`GOLEMCHECK ${c[0]} FAIL not_registered`); return }
    golems().forEach(g => g.discard())
    const r = spell.cast(new Ctx(level, fake, Hand.MAIN_HAND, 0, new Mods()))
    const gs = golems()
    const typed = gs.filter(g => String(g.nbt).includes(`Golem:"${c[1]}"`))
    const minions = gs.filter(g => String(g.getCustomName() ? g.getCustomName().getString() : '').length > 0)
    const baby = gs.filter(g => g.isBaby()).length
    const ok = r && gs.length == c[2] && typed.length == c[2] && minions.length == c[2] && (!c[3] || baby == c[2])
    console.info(`GOLEMCHECK ${c[0]} ${ok ? 'OK' : 'FAIL'} cast=${r} golems=${gs.length} typed=${typed.length} named=${minions.length} baby=${baby} hp=${gs.length ? gs[0].getHealth() : -1}`)
  })
  // оставить последних (золотой) + призвать ещё раз грозовых для проверки времени жизни
  reg.getValue(new RL('wizardrygolems', 'lightning_golemancy')).cast(new Ctx(level, fake, Hand.MAIN_HAND, 0, new Mods()))
})

// ---- Golems-2/3: особые големы, артефакты, Living Snow, добыча ----
ServerEvents.tick(event => {
  const server = event.server
  const t = server.tickCount
  if (t != 520 && t != 760) return
  const Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  const BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  const level = server.getLevel('minecraft:overworld')
  const at = (x, z, r) => { let out = []; level.getEntitiesOfClass(Entity, new (Java.loadClass('net.minecraft.world.phys.AABB'))(x - r, 190, z - r, x + r, 215, z + r)).forEach(e => out.push(e)); return out }
  const report = (id, ok, info) => console.info(`GOLEMCHECK ${id} ${ok ? 'OK' : 'FAIL'} ${info}`)
  if (t == 520) {
    // арены вдали от первой части: x=60 (аура), x=90 (каст огня), x=120 (след мерзлоты)
    server.runCommandSilent('forceload add 40 -20 170 20')
    server.runCommandSilent('time set midnight')   // зомби в небе не горят на солнце
    ;[60, 90, 120].forEach(x => { server.runCommandSilent(`fill ${x - 8} 199 -8 ${x + 8} 199 8 minecraft:stone`); server.runCommandSilent(`fill ${x - 8} 200 -8 ${x + 8} 210 8 minecraft:air`) })
    server.runCommandSilent('summon golems:golem 60.5 200 0.5 {Golem:"wizardrygolems:thunderstone",PersistenceRequired:1b,NoAI:1b}')  // без ИИ — урон только от ауры
    server.runCommandSilent('summon minecraft:zombie 61.5 200 0.5 {PersistenceRequired:1b,NoAI:1b}')
    server.runCommandSilent('summon golems:golem 90.5 200 0.5 {Golem:"wizardrygolems:flame",PersistenceRequired:1b}')
    server.runCommandSilent('summon minecraft:zombie 90.5 200 7.5 {PersistenceRequired:1b,NoAI:1b,Health:200f,Attributes:[{Name:"generic.max_health",Base:200}]}')
    server.runCommandSilent('summon golems:golem 120.5 200 0.5 {Golem:"wizardrygolems:permafrost",PersistenceRequired:1b}')
    server.runCommandSilent('setblock 60 200 6 wizardrygolems:living_snow')
    const types = ['flame', 'winter', 'spark', 'lodestone', 'maelstrom', 'packed_ice', 'permafrost', 'snow', 'concrete', 'strawthorns', 'thunderstone']
    let loaded = 0
    types.forEach((ty, i) => { server.runCommandSilent(`summon golems:golem ${130 + (i % 4) * 2}.5 200 ${-6 + Math.floor(i / 4) * 3}.5 {Golem:"wizardrygolems:${ty}",PersistenceRequired:1b,NoAI:1b}`) })
    return
  }
  // t == 760: результаты через 12 с
  const zs = at(60.5, 0.5, 3).filter(e => String(e.getType()) == 'minecraft:zombie')
  report('thunderstone_aura', zs.length == 0 || zs[0].getHealth() < 20, `zombie_hp=${zs.length ? zs[0].getHealth() : 'dead'}`)
  const z2 = at(90.5, 7.5, 6).filter(e => String(e.getType()) == 'minecraft:zombie')
  // огненные заклинания поджигают; ближний удар голема — нет
  report('flame_casts', z2.length == 0 || z2[0].getRemainingFireTicks() > 0 || z2[0].isOnFire(), `zombie_hp=${z2.length ? z2[0].getHealth() : 'dead'} fire=${z2.length ? z2[0].getRemainingFireTicks() : '-'}`)
  let perma = 0
  for (let x = 112; x <= 128; x++) for (let z = -8; z <= 8; z++)
    if (String(level.getBlockState(new BlockPos(x, 200, z)).getBlock().getDescriptionId()).includes('permafrost')) perma++
  const pg = at(120.5, 0.5, 8).filter(e => String(e.getType()) == 'golems:golem')
  report('permafrost_trail', pg.length == 1 && (perma > 0 || pg[0].getDeltaMovement().length() == 0), `blocks=${perma} golem=${pg.length}`)
  const all = at(134, -2, 8).filter(e => String(e.getType()) == 'golems:golem')
  const typed = all.filter(e => String(e.nbt).includes('Golem:"wizardrygolems:')).length
  report('custom_types', typed == 11, `golems=${all.length} typed=${typed}`)
  const be = level.getBlockEntity(new BlockPos(60, 200, 6))
  report('living_snow', be != null && String(level.getBlockState(new BlockPos(60, 200, 6)).getBlock().getDescriptionId()).includes('living_snow'), `be=${be}`)
  // добыча: 300 бросков эпической выборки Redux — среди них должны быть артефакты wizardrygolems
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const Params = Java.loadClass('net.minecraft.world.level.storage.loot.LootParams$Builder')
  const Sets = Java.loadClass('net.minecraft.world.level.storage.loot.parameters.LootContextParamSets')
  const table = server.getLootData().getLootTable(new RL('ebwizardry', 'subsets/epic_artifacts'))
  let ours = 0, total = 0
  for (let i = 0; i < 300; i++) table.getRandomItems(new Params(level).create(Sets.EMPTY)).forEach(s => { total++; if (String(s.getItem().getDescriptionId()).includes('wizardrygolems')) ours++ })
  report('loot_epic', ours > 0, `ours=${ours}/${total}`)
  // артефакт в слот Curios фейкового игрока: Engraved Concrete Ring → Earth Golemancy даёт 2 бетонных голема
  try {
    const Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
    const Ctx = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.PlayerCastContext')
    const Mods = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.SpellModifiers')
    const Hand = Java.loadClass('net.minecraft.world.InteractionHand')
    const RM = Java.loadClass('net.minecraftforge.registries.RegistryManager')
    const Curios = Java.loadClass('top.theillusivec4.curios.api.CuriosApi')
    const fake = Fake.getMinecraft(level)
    const inv = Curios.getCuriosInventory(fake).resolve()
    if (!inv.isPresent()) { report('artifact_concrete', false, 'no_curios_inventory'); return }
    const ring = inv.get().getStacksHandler('ring')
    if (!ring.isPresent()) { report('artifact_concrete', false, 'no_ring_slot'); return }
    ring.get().getStacks().setStackInSlot(0, Item.of('wizardrygolems:ring_engraved_concrete'))
    server.runCommandSilent('fill 152 199 -8 168 199 8 minecraft:stone'); server.runCommandSilent('fill 152 200 -8 168 210 8 minecraft:air')
    fake.setPos(160.5, 200, 0.5); fake.setOnGround(true)
    const r = RM.ACTIVE.getRegistry(new RL('ebwizardry', 'spells')).getValue(new RL('wizardrygolems', 'earth_golemancy')).cast(new Ctx(level, fake, Hand.MAIN_HAND, 0, new Mods()))
    const gs = at(160.5, 0.5, 6).filter(e => String(e.getType()) == 'golems:golem')
    const conc = gs.filter(e => String(e.nbt).includes('Golem:"wizardrygolems:concrete"')).length
    report('artifact_concrete', r && conc == 2 && gs.length == 2, `cast=${r} golems=${gs.length} concrete=${conc}`)
    ring.get().getStacks().setStackInSlot(0, Item.of('minecraft:air'))
  } catch (err) { report('artifact_concrete', false, `error=${err}`) }
})
