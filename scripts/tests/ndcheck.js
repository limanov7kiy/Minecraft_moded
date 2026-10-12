// Тест порта Necromancer's Delight (поток 12, этап ND-1): заклинания без мобов Fish's Undead Rising и артефакты.
// Вывод: NDCHECK <проверка> OK|FAIL <подробности>
ServerEvents.tick(event => {
  const server = event.server
  const t = server.tickCount
  if (t != 200 && t != 240 && t != 245 && t != 255 && t != 300 && t != 400) return
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const RM = Java.loadClass('net.minecraftforge.registries.RegistryManager')
  const NdFake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const NdCtx = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.PlayerCastContext')
  const NdMods = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.SpellModifiers')
  const NdHand = Java.loadClass('net.minecraft.world.InteractionHand')
  const BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  const Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  const AABB = Java.loadClass('net.minecraft.world.phys.AABB')
  const level = server.getLevel('minecraft:overworld')
  const fake = NdFake.getMinecraft(level)
  const report = (id, ok, info) => console.info(`NDCHECK ${id} ${ok ? 'OK' : 'FAIL'} ${info}`)
  const reg = RM.ACTIVE.getRegistry(new RL('ebwizardry', 'spells'))
  const cast = (id) => { let s = reg.getValue(new RL('necromancersdelight', id)); return s ? s.cast(new NdCtx(level, fake, NdHand.MAIN_HAND, 0, new NdMods())) : 'missing' }
  const mobsAt = (x, z, r) => { let out = []; level.getEntitiesOfClass(Entity, new AABB(x - r, 195, z - r, x + r, 210, z + r)).forEach(e => { if (String(e.getType()) == 'minecraft:husk') out.push(e) }); return out }
  const hasEff = (e, id) => { let ok = false; e.getActiveEffects().forEach(i => { if (String(i.getEffect().getDescriptionId()) == id) ok = true }); return ok }
  // арены по X: -200 (вонючая), -230 (морозная), -260 (разъедание), -290 (светогриб, споры, амулет)
  const arenas = [-200, -230, -260, -290]
  if (t == 200) {
    // чанки арен грузим заранее: в том же тике снаряды и луч не находят сущности
    server.runCommandSilent('forceload add -310 -20 -180 20')
    server.runCommandSilent('time set midnight')
    return
  }
  if (t == 240) {
    arenas.forEach(x => { server.runCommandSilent(`fill ${x - 8} 199 -8 ${x + 8} 199 8 minecraft:stone`); server.runCommandSilent(`fill ${x - 8} 200 -8 ${x + 8} 210 8 minecraft:air`) })
    ;[-200, -230, -260].forEach(x => server.runCommandSilent(`summon minecraft:husk ${x}.5 200 4.5 {NoAI:1b,PersistenceRequired:1b}`))
    let aim = (x, pitch) => { fake.setPos(x + 0.5, 200, 0.5); fake.yRot = 0; fake.xRot = pitch; fake.yHeadRot = 0; fake.setOnGround(true) }
    aim(-200, 10); report('stink_bomb_cast', cast('stink_bomb') === true, '')
    aim(-230, 10); report('frost_bomb_cast', cast('frost_bomb') === true, '')
    aim(-260, 10); report('corrode_cast', cast('corrode') === true, '')
    // светогриб: луч в пол перед собой
    aim(-290, 60); report('glowshroom_cast', cast('glowshroom') === true, '')
    // контроль: как в castcheck — у точки появления
    server.runCommandSilent('fill -8 199 -8 8 199 8 minecraft:stone'); server.runCommandSilent('fill -8 200 -8 8 210 8 minecraft:air')
    server.runCommandSilent('summon minecraft:husk 0.5 200 3.5 {NoAI:1b,PersistenceRequired:1b}')
    aim(0, 10); report('corrode_cast_origin', cast('corrode') === true, `look=${fake.getLookAngle()}`)
    return
  }
  if (t == 245 || t == 255) {
    // диагностика: где бомбы и куда смотрит фейковый игрок
    level.getEntitiesOfClass(Entity, new AABB(-320, 0, -40, -170, 320, 40)).forEach(e => {
      let ty = String(e.getType()); if (ty.startsWith('necromancersdelight:')) console.info(`NDDIAG t=${t} ${ty} pos=${e.getX().toFixed(2)},${e.getY().toFixed(2)},${e.getZ().toFixed(2)} v=${e.getDeltaMovement()}`) })
    console.info(`NDDIAG fake pos=${fake.getX()},${fake.getY()},${fake.getZ()} look=${fake.getLookAngle()} eye=${fake.getEyeY()}`)
    return
  }
  if (t == 400) {
    let hs = mobsAt(-289.5, 3.5, 2); report('poisonous_spore', hs.length >= 1 && hasEff(hs[0], 'effect.minecraft.poison'), `husks=${hs.length}`)
    return
  }
  // t == 300
  let h = mobsAt(-199.5, 4.5, 4); report('stink_bomb', h.length == 1 && hasEff(h[0], 'effect.mod_lavacow.soiled'), `husks=${h.length} hp=${h.length ? h[0].getHealth() : '-'}`)
  h = mobsAt(-229.5, 4.5, 4); report('frost_bomb', h.length == 1 && hasEff(h[0], 'effect.ebwizardry.frost'), `husks=${h.length} hp=${h.length ? h[0].getHealth() : '-'}`)
  h = mobsAt(-259.5, 4.5, 4); report('corrode', h.length == 1 && hasEff(h[0], 'effect.mod_lavacow.corroded'), `husks=${h.length}`)
  let shrooms = 0
  for (let x = -298; x <= -282; x++) for (let z = -8; z <= 8; z++)
    if (String(level.getBlockState(new BlockPos(x, 200, z)).getBlock().getDescriptionId()).includes('temporary_glowshroom')) shrooms++
  report('glowshroom', shrooms == 1, `blocks=${shrooms}`)
  // Corroded: урон +10%
  // Rhino не видит SRG-имя hurt — урон командой /damage
  if (h.length == 1) { let before = h[0].getHealth(); server.runCommandSilent('execute positioned -259.5 200 4.5 run damage @e[type=minecraft:husk,distance=..1.5,limit=1] 4 minecraft:magic'); report('corroded_damage', Math.abs((before - h[0].getHealth()) - 4.4) < 0.05, `dmg=${before - h[0].getHealth()}`) }
  // Poisonous Spore: стойка для брони с эффектом отравляет хаска рядом (проверка на t=400)
  server.runCommandSilent('summon minecraft:husk -289.5 200 3.5 {NoAI:1b,PersistenceRequired:1b}')
  server.runCommandSilent('summon minecraft:armor_stand -289.5 200 2.5 {PersistenceRequired:1b}')
  server.runCommandSilent('effect give @e[type=minecraft:armor_stand,x=-290,y=200,z=2,distance=..2] necromancersdelight:poisonous_spore 60 0')
  let Eff = Java.loadClass('net.minecraft.world.effect.MobEffectInstance')
  // Амулет стойкости в слоте Curios: замедление не накладывается
  try {
    let Curios = Java.loadClass('top.theillusivec4.curios.api.CuriosApi')
    let inv = Curios.getCuriosInventory(fake).resolve()
    let neck = inv.isPresent() ? inv.get().getStacksHandler('necklace') : null
    if (!neck || !neck.isPresent()) { report('amulet_slowness_immunity', false, 'no_necklace_slot') }
    else {
      neck.get().getStacks().setStackInSlot(0, Item.of('necromancersdelight:amulet_slowness_immunity'))
      let MobEffects = Java.loadClass('net.minecraft.world.effect.MobEffects')
      fake.removeAllEffects()
      fake.addEffect(new Eff(MobEffects.MOVEMENT_SLOWDOWN, 200, 1))
      report('amulet_slowness_immunity', !fake.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), '')
      neck.get().getStacks().setStackInSlot(0, Item.of('minecraft:air'))
    }
  } catch (err) { report('amulet_slowness_immunity', false, `error=${err}`) }
})
