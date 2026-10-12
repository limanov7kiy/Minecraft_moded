// Тест порта Necromancer's Delight (поток 12, этап ND-1): заклинания без мобов Fish's Undead Rising и артефакты.
// Вывод: NDCHECK <проверка> OK|FAIL <подробности>
ServerEvents.tick(event => {
  const server = event.server
  const t = server.tickCount
  if (t != 240 && t != 300) return
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
  if (t == 240) {
    server.runCommandSilent('forceload add -310 -20 -180 20')
    server.runCommandSilent('time set midnight')
    arenas.forEach(x => { server.runCommandSilent(`fill ${x - 8} 199 -8 ${x + 8} 199 8 minecraft:stone`); server.runCommandSilent(`fill ${x - 8} 200 -8 ${x + 8} 210 8 minecraft:air`) })
    ;[-200, -230, -260].forEach(x => server.runCommandSilent(`summon minecraft:husk ${x}.5 200 4.5 {NoAI:1b,PersistenceRequired:1b}`))
    const aim = (x, pitch) => { fake.setPos(x + 0.5, 200, 0.5); fake.yRot = 0; fake.xRot = pitch; fake.yHeadRot = 0; fake.setOnGround(true) }
    aim(-200, 10); report('stink_bomb_cast', cast('stink_bomb') === true, '')
    aim(-230, 10); report('frost_bomb_cast', cast('frost_bomb') === true, '')
    aim(-260, 10); report('corrode_cast', cast('corrode') === true, '')
    // светогриб: луч в пол перед собой
    aim(-290, 60); report('glowshroom_cast', cast('glowshroom') === true, '')
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
  if (h.length == 1) { let before = h[0].getHealth(); h[0].hurt(level.damageSources().magic(), 4); report('corroded_damage', Math.abs((before - h[0].getHealth()) - 4.4) < 0.05, `dmg=${before - h[0].getHealth()}`) }
  // Poisonous Spore на фейковом игроке отравляет хаска рядом (раз в 19 т. — даём эффект и тикаем вручную)
  server.runCommandSilent('summon minecraft:husk -289.5 200 1.5 {NoAI:1b,PersistenceRequired:1b}')
  fake.setPos(-289.5, 200, 0.5)
  let Eff = Java.loadClass('net.minecraft.world.effect.MobEffectInstance')
  let spore = Java.loadClass('net.minecraftforge.registries.ForgeRegistries').MOB_EFFECTS.getValue(new RL('necromancersdelight', 'poisonous_spore'))
  fake.addEffect(new Eff(spore, 200, 0))
  for (let i = 0; i < 20; i++) { fake.tickCount = fake.tickCount + 1; spore.applyEffectTick(fake, 0) }
  h = mobsAt(-289.5, 1.5, 2); report('poisonous_spore', h.length >= 1 && hasEff(h[0], 'effect.minecraft.poison'), `husks=${h.length}`)
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
