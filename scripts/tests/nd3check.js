// Тест ND-3 порта Necromancer's Delight (поток 29): 15 заклинаний на мобах/предметах Fish's Undead Rising и 12 артефактов.
// Каст — фейковым игроком (как ndcheck.js); результат проверяется в мире. Вывод: ND3CHECK <проверка> OK|FAIL <подробности>
// Фейковый игрок добавлен в мир (addFreshEntity), иначе MinionData.getOwner() не находит хозяина миньонов по UUID.
// Он не тикает, поэтому Crown of Pests и Thornbelt вызываются напрямую (NdArtifacts.pestCrown / thornBurst),
// а Bone Bag и мана-пиявка — через события Redux SpellCastEvent (каст напрямую их не шлёт).
const ND3 = { phase: 0, cast: 0, prep: 0, seen: {} }
// гибель мобов FUR/ND в зоне теста — для диагностики (кто и отчего)
EntityEvents.death(event => { try { let e = event.entity; let id = String(e.getType()); if (e.getX() < -350 && e.getX() > -770 && (id.includes('mod_lavacow') || id.includes('necromancersdelight')))
  console.info(`ND3DIAG death ${id} source=${event.source.getMsgId()} by=${event.source.getEntity()} at=${Math.round(e.getX())},${Math.round(e.getZ())}`) } catch (err) {} })
ServerEvents.tick(event => { try { nd3Tick(event) } catch (err) { console.info(`ND3CHECK script FAIL error=${err}`) } })
const nd3Tick = (event) => {
  const server = event.server
  const t = server.tickCount
  if (t < 200) return
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const RM = Java.loadClass('net.minecraftforge.registries.RegistryManager')
  const Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const Ctx = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.PlayerCastContext')
  const Mods = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.SpellModifiers')
  const Hand = Java.loadClass('net.minecraft.world.InteractionHand')
  const BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  const Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  const AABB = Java.loadClass('net.minecraft.world.phys.AABB')
  const Services = Java.loadClass('com.binaris.wizardry.core.platform.Services')
  const level = server.getLevel('minecraft:overworld')
  const fake = Fake.getMinecraft(level)
  const report = (id, ok, info) => console.info(`ND3CHECK ${id} ${ok ? 'OK' : 'FAIL'} ${info}`)
  const reg = RM.ACTIVE.getRegistry(new RL('ebwizardry', 'spells'))
  const spell = (ns, id) => reg.getValue(new RL(ns, id))
  const ctx = () => new Ctx(level, fake, Hand.MAIN_HAND, 0, new Mods())
  const cast = (id, ns) => { let s = spell(ns || 'necromancersdelight', id); if (!s) return 'missing'; try { return s.cast(ctx()) } catch (err) { console.info(`ND3DIAG cast ${id} error=${err}`); return `error` } }
  const EType = Java.loadClass('net.minecraft.world.entity.EntityType')
  const type = (e) => String(EType.getKey(e.getType()))
  const near = (x, z, r, id) => { let out = []; level.getEntitiesOfClass(Entity, new AABB(x - r, 190, z - r, x + r, 215, z + r)).forEach(e => { if (type(e) == id) out.push(e) }); return out }
  const hasEff = (e, id) => { let ok = false; e.getActiveEffects().forEach(i => { if (String(i.getEffect().getDescriptionId()) == id) ok = true }); return ok }
  const minionOf = (e) => { let d = Services.OBJECT_DATA.getMinionData(e); return d != null && d.isSummoned() && d.getOwner() == fake }
  // миньоны Redux идут (и телепортируются) к хозяину — их ищем по всей зоне теста
  const all = (id) => { let out = []; level.getEntitiesOfClass(Entity, new AABB(-760, 185, -20, -360, 225, 20)).forEach(e => { if (type(e) == id) out.push(e) }); return out }
  const invCount = (id) => { let n = 0; let inv = fake.getInventory(); for (let i = 0; i < inv.getContainerSize(); i++) { let s = inv.getItem(i); if (String(s.getItem().builtInRegistryHolder().key().location()) == id) n += s.getCount() } return n }
  const aim = (x, z, yaw, pitch) => { fake.setPos(x + 0.5, 200, z + 0.5); fake.yRot = yaw; fake.xRot = pitch; fake.yHeadRot = yaw; fake.setOldPosAndRot(); fake.setOnGround(true) }
  // слоты Curios фейкового игрока
  const curios = () => Java.loadClass('top.theillusivec4.curios.api.CuriosApi').getCuriosInventory(fake).resolve().get()
  const slot = (name, i, id) => curios().getStacksHandler(name).get().getStacks().setStackInSlot(i, Item.of(id))
  const unequipAll = () => ['ring', 'necklace', 'charm', 'belt', 'head'].forEach(n => { let h = curios().getStacksHandler(n); if (h.isPresent()) for (let i = 0; i < h.get().getSlots(); i++) h.get().getStacks().setStackInSlot(i, Item.of('minecraft:air')) })
  // арены по X (пол y=199, z −8..8); чанки — forceload
  const A = { conj: -400, holy: -430, ghost: -460, sonic: -490, minion: -520, trial: -550, mummy: -580, legion: -610, forsaken: -640, bag: -670, leech: -700, crown: -730 }
  const xs = Object.keys(A).map(k => A[k])
  if (ND3.phase == 0) {
    server.runCommandSilent('forceload add -750 -20 -380 20')
    server.runCommandSilent('time set midnight')
    ND3.phase = 1
    return
  }
  if (ND3.phase == 1) {
    if (t < 1400) return  // после ndcheck.js (он кастует тем же фейковым игроком около t=700–900)
    let ready = xs.every(x => level.isPositionEntityTicking(new BlockPos(x, 200, 0)) && level.isPositionEntityTicking(new BlockPos(x, 200, 6)))
    if (!ready) { if (t % 100 == 0) console.info(`ND3DIAG waiting chunks t=${t}`); return }
    server.runCommandSilent('kill @e[type=!minecraft:player,x=-760,y=185,z=-20,dx=400,dy=40,dz=40]')
    xs.forEach(x => { server.runCommandSilent(`fill ${x - 8} 199 -8 ${x + 8} 199 8 minecraft:stone`); server.runCommandSilent(`fill ${x - 8} 200 -8 ${x + 8} 212 8 minecraft:air`) })
    ND3.phase = 3; ND3.prep = t
    return
  }
  if (ND3.phase == 3) {
    if (t < ND3.prep + 40) return
    ND3.phase = 2; ND3.cast = t
    if (!fake.isAddedToWorld()) level.addFreshEntity(fake)
    fake.getInventory().clearContent(); fake.removeAllEffects(); unequipAll()

    // --- призыв оружия (4) ---
    try {
    aim(A.conj, 0, 0, 0)
    slot('ring', 0, 'necromancersdelight:ring_frozen_dagger')
    report('bound_frozen_dagger_cast', cast('bound_frozen_dagger') === true, '')
    unequipAll()
    report('bound_bone_sword_cast', cast('bound_bone_sword') === true, '')
    report('bound_spectral_dagger_cast', cast('bound_spectral_dagger') === true, '')
    report('bound_vespa_shield_cast', cast('bound_vespa_shield') === true, '')
    let Conj = Java.loadClass('com.binaris.wizardry.content.spell.abstr.ConjureItemSpell')
    ;['frozen_dagger', 'bonesword', 'spectral_dagger', 'vespa_shield'].forEach(id => {
      let n = invCount(`mod_lavacow:${id}`); let summoned = false
      let inv = fake.getInventory(); for (let i = 0; i < inv.getContainerSize(); i++) if (String(inv.getItem(i).getItem().builtInRegistryHolder().key().location()) == `mod_lavacow:${id}`) summoned = Conj.isSummoned(inv.getItem(i))
      report(`conjured_${id}`, n == 1 && summoned, `count=${n} summoned=${summoned}`)
    })
    // Frigidbone Ring: кинжал с меткой морозит цель при ударе
    let inv = fake.getInventory(); let dagger = -1
    for (let i = 0; i < inv.getContainerSize(); i++) if (String(inv.getItem(i).getItem().builtInRegistryHolder().key().location()) == 'mod_lavacow:frozen_dagger') dagger = i
    let dtag = dagger >= 0 ? inv.getItem(dagger).nbt : null
    let freezing = dtag != null && dtag.getBoolean('necromancersdelight_freezing')
    report('ring_frozen_dagger_tag', freezing, `slot=${dagger}`)
    server.runCommandSilent(`summon minecraft:husk ${A.conj + 0.5} 200 1.5 {NoAI:1b,PersistenceRequired:1b}`)
    try {
      fake.setItemInHand(Hand.MAIN_HAND, inv.getItem(dagger).copy())
      let husk = near(A.conj + 0.5, 1.5, 2, 'minecraft:husk')[0]
      fake.attack(husk)
      report('ring_frozen_dagger_frost', hasEff(husk, 'effect.ebwizardry.frost'), `hp=${husk.getHealth()}`)
    } catch (err) { report('ring_frozen_dagger_frost', false, `error=${err}`) }
    fake.getInventory().clearContent()

    } catch (err) { report('section_1', false, `error=${err}`) }
    // --- бомбы (3): хаск в 4 блоках, бросок под ноги ---
    try {
    ;[['holy_hand_grenade', A.holy], ['ghost_bomb', A.ghost], ['wail_of_the_banshee', A.sonic]].forEach(p => {
      server.runCommandSilent(`summon minecraft:husk ${p[1] + 0.5} 200 4.5 {NoAI:1b,PersistenceRequired:1b}`)
      aim(p[1], 0, 0, 30); report(`${p[0]}_cast`, cast(p[0]) === true, '')
    })

    } catch (err) { report('section_2', false, `error=${err}`) }
    // --- миньоны (6) ---
    try {
    aim(A.minion, 0, 0, 0)
    ;['summon_vespa', 'conjure_banshee', 'conjure_mycosis', 'summon_salamander', 'summon_mana_leech'].forEach(id => report(`${id}_cast`, cast(id) === true, ''))
    // стихийная пиявка: огненная палочка в руке → огонь
    fake.setItemInHand(Hand.MAIN_HAND, Item.of('ebwizardry:wand_novice_fire'))
    report('summon_elemental_leech_cast', cast('summon_elemental_leech') === true, '')
    fake.setItemInHand(Hand.MAIN_HAND, Item.of('minecraft:air'))

    } catch (err) { report('section_3', false, `error=${err}`) }
    // --- Trial of Mushrooms, Carcinize ---
    try {
    aim(A.trial, 0, 0, 0)
    fake.setItemInHand(Hand.OFF_HAND, Item.of('mod_lavacow:veil_shroom', 2))
    report('trial_of_mushrooms', cast('trial_of_mushrooms') === true && hasEff(fake, 'effect.minecraft.invisibility') && fake.getOffhandItem().getCount() == 1, `left=${fake.getOffhandItem().getCount()}`)
    fake.setItemInHand(Hand.OFF_HAND, Item.of('mod_lavacow:bloodtooth_shroom'))
    report('trial_bloodtooth', cast('trial_of_mushrooms') === true && hasEff(fake, 'effect.minecraft.regeneration'), '')
    fake.setItemInHand(Hand.OFF_HAND, Item.of('minecraft:air')); fake.removeAllEffects()
    server.runCommandSilent(`setblock ${A.trial} 200 3 minecraft:chest`)
    server.runCommandSilent(`data merge block ${A.trial} 200 3 {Items:[{Slot:0b,id:"minecraft:diamond",Count:3b}]}`)
    fake.getInventory().add(Item.of('ebwizardry:magic_crystal_earth', 2))
    aim(A.trial, 0, 0, 20)
    report('carcinize_cast', cast('carcinize') === true && invCount('ebwizardry:magic_crystal_earth') == 1, `crystals=${invCount('ebwizardry:magic_crystal_earth')}`)
    fake.getInventory().clearContent()

    } catch (err) { report('section_4', false, `error=${err}`) }
    // --- артефакты подмены (Redux summon_zombie / summon_skeleton) ---
    try {
    aim(A.mummy, 0, 0, 0); slot('charm', 0, 'necromancersdelight:charm_mummy_minion')
    report('mummy_cast', cast('summon_zombie', 'ebwizardry') === true, ''); unequipAll()
    aim(A.legion, 0, 0, 0); slot('ring', 0, 'necromancersdelight:ring_legion'); slot('ring', 1, 'necromancersdelight:ring_nameless')
    report('legion_cast', cast('summon_zombie', 'ebwizardry') === true, ''); unequipAll()
    aim(A.forsaken, 0, 0, 0); slot('ring', 0, 'necromancersdelight:ring_forsaken')
    report('forsaken_cast', cast('summon_skeleton', 'ebwizardry') === true, ''); unequipAll()
    // Bone Bag (полный) + Book of Witchcraft (светогриб во второй руке)
    aim(A.bag, 0, 0, 0); slot('charm', 0, 'necromancersdelight:charm_bone_bag')
    let bag = curios().getStacksHandler('charm').get().getStacks().getStackInSlot(0); let bt = new (Java.loadClass('net.minecraft.nbt.CompoundTag'))(); bt.putInt('progress', 100); bag.nbt = bt
    let Post = Java.loadClass('com.binaris.wizardry.api.content.event.SpellCastEvent$Post')
    let Src = Java.loadClass('com.binaris.wizardry.api.content.event.SpellCastEvent$Sources')
    let Bus = Java.loadClass('com.binaris.wizardry.core.event.WizardryEventBus')
    let c = ctx(); let sz = spell('ebwizardry', 'summon_zombie')
    report('bone_bag_cast', sz.cast(c) === true, ''); Bus.fireEvent(new Post(Src.COMMAND, sz, c))
    report('bone_bag_reset', bag.nbt != null && bag.nbt.getInt('progress') == 0, `progress=${bag.nbt ? bag.nbt.getInt('progress') : '-'}`)
    unequipAll(); slot('charm', 0, 'necromancersdelight:charm_mushroom_minion')
    fake.setItemInHand(Hand.OFF_HAND, Item.of('mod_lavacow:glowshroom', 2))
    aim(A.bag, 6, 0, 0)
    report('mushroom_minion_cast', cast('summon_zombie', 'ebwizardry') === true && fake.getOffhandItem().getCount() == 1, `left=${fake.getOffhandItem().getCount()}`)
    fake.setItemInHand(Hand.OFF_HAND, Item.of('minecraft:air')); unequipAll()

    } catch (err) { report('section_5', false, `error=${err}`) }
    // --- пиявки: Leechlink, Spell-Sapping Venom ---
    try {
    aim(A.leech, 0, 0, 0)
    slot('necklace', 0, 'necromancersdelight:amulet_leechlink'); slot('charm', 0, 'necromancersdelight:charm_spellvenom')
    report('venom_leech_cast', cast('summon_mana_leech') === true, '')
    unequipAll(); slot('necklace', 0, 'necromancersdelight:amulet_leechlink')
    report('leechlink_leech_cast', cast('summon_mana_leech') === true, '')
    // Leech Crystal Amulet: привязка стихии в Arcane Workbench (льдом) — пиявка изо льда
    let amulet = Item.of('necromancersdelight:amulet_leech_crystal')
    let Cont = Java.loadClass('net.minecraft.world.SimpleContainer'); let Slot = Java.loadClass('net.minecraft.world.inventory.Slot')
    let cont = new Cont(amulet, Item.of('ebwizardry:magic_crystal_ice', 3))  // new Cont(2) Rhino принимает за varargs ItemStack
    let applied = amulet.getItem().onApplyButtonPressed(fake, new Slot(cont, 0, 0, 0), new Slot(cont, 1, 0, 0), null, null)
    let bound = cont.getItem(0)
    report('leech_crystal_bind', applied && bound.nbt != null && String(bound.nbt.getString('element')) == 'ice' && cont.getItem(1).getCount() == 2, `element=${bound.nbt != null ? bound.nbt.getString('element') : '-'}`)
    curios().getStacksHandler('necklace').get().getStacks().setStackInSlot(0, bound)
    aim(A.leech, 6, 0, 0)
    report('crystal_leech_cast', cast('summon_elemental_leech') === true, '')
    unequipAll(); slot('necklace', 0, 'necromancersdelight:amulet_leechlink')
    // мана: хаск-носитель с палочкой (1000 маны), у хозяина палочка с 0
    server.runCommandSilent(`summon minecraft:husk ${A.leech + 4.5} 200 0.5 {NoAI:1b,PersistenceRequired:1b,HandItems:[{id:"ebwizardry:wand_novice",Count:1b},{}]}`)
    fake.setItemInHand(Hand.MAIN_HAND, Item.of('ebwizardry:wand_novice'))
    let w = fake.getMainHandItem(); w.getItem().setMana(w, 0)
    let host = near(A.leech + 4.5, 0.5, 1, 'minecraft:husk')[0]
    let hw = host.getMainHandItem(); hw.getItem().setMana(hw, hw.getItem().getManaCapacity(hw))
    ND3.hostMana = hw.getItem().getMana(hw)

    } catch (err) { report('section_6', false, `error=${err}`) }
    // --- Crown of Pests, Thornbelt ---
    try {
    aim(A.crown, 0, 0, 0)
    let Arts = Java.loadClass('dev.mcmoded.necromancersdelight.item.NdArtifacts')
    fake.getPersistentData().remove('necromancersdelight_weta'); fake.getCooldowns().removeCooldown(Item.of('necromancersdelight:head_pest_crown').getItem())
    slot('head', 0, 'necromancersdelight:head_pest_crown'); Arts.pestCrown(fake); Arts.pestCrown(fake)
    slot('belt', 0, 'necromancersdelight:belt_thorns'); fake.getCooldowns().removeCooldown(Item.of('necromancersdelight:belt_thorns').getItem())
    Arts.thornBurst(fake)
    report('belt_thorns', near(A.crown + 0.5, 0.5, 3, 'mod_lavacow:cactus_thorn').length == 16, `thorns=${near(A.crown + 0.5, 0.5, 3, 'mod_lavacow:cactus_thorn').length}`)
    unequipAll(); slot('necklace', 0, 'necromancersdelight:amulet_leechlink')
    // генерация грибов: светящийся на земле
    server.runCommandSilent(`fill ${A.crown - 8} 199 -8 ${A.crown + 8} 199 8 minecraft:grass_block`)
    server.runCommandSilent(`execute positioned ${A.crown} 200 -4 run place feature mod_lavacow:glowshroom`)
    } catch (err) { report('section_7', false, `error=${err}`) }
    return
  }
  let since = t - ND3.cast
  // бомбы: эффект виден недолго (левитация 1 с) — смотрим каждый тик
  if (since > 0 && since < 80) {
    let h = near(A.holy + 0.5, 4.5, 4, 'minecraft:husk'); if (h.length && h[0].isOnFire()) ND3.seen.holy = true
    h = near(A.ghost + 0.5, 4.5, 4, 'minecraft:husk'); if (h.length && hasEff(h[0], 'effect.minecraft.levitation')) ND3.seen.ghost = true
    h = near(A.sonic + 0.5, 4.5, 4, 'minecraft:husk'); if (h.length && hasEff(h[0], 'effect.mod_lavacow.fear')) ND3.seen.sonic = true
  }
  if (since == 20) {
    // мана-пиявку сажаем на хаска с палочкой
    let leech = all('necromancersdelight:mana_leech_minion').filter(e => !e.weakensSpells() && !e.isPassenger())
    let host = near(A.leech + 4.5, 0.5, 1, 'minecraft:husk')[0]
    if (leech.length && host) leech[0].startRiding(host, true)
    // Spell-Sapping Venom / цена ×2: пиявка (с ядом) на кастующем-хаске
    let Pre = Java.loadClass('com.binaris.wizardry.api.content.event.SpellCastEvent$Pre')
    let Src = Java.loadClass('com.binaris.wizardry.api.content.event.SpellCastEvent$Sources')
    let Bus = Java.loadClass('com.binaris.wizardry.core.event.WizardryEventBus')
    let ECtx = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.EntityCastContext')
    let venom = all('necromancersdelight:mana_leech_minion').filter(e => e.weakensSpells())
    report('charm_spellvenom', venom.length >= 1, `venom_leeches=${venom.length}`)
    if (venom.length && host) {
      server.runCommandSilent(`summon minecraft:husk ${A.leech - 3.5} 200 0.5 {NoAI:1b,PersistenceRequired:1b}`)
      let caster = near(A.leech - 3.5, 0.5, 1, 'minecraft:husk')[0]
      venom[0].startRiding(caster, true)
      let ev = new Pre(Src.NPC, spell('necromancersdelight', 'corrode'), new ECtx(level, caster, Hand.MAIN_HAND, 0, null, new Mods()))
      Bus.fireEvent(ev)
      let m = ev.getModifiers()
      report('mana_leech_cost', Math.abs(m.getFactor(Mods.COST) - 2) < 0.01 && Math.abs(m.getFactor(Mods.POTENCY) - 0.6) < 0.01, `cost=${m.getFactor(Mods.COST)} potency=${m.getFactor(Mods.POTENCY)}`)
    }
    return
  }
  if (since == 100) {
    report('holy_hand_grenade', ND3.seen.holy === true, '')
    report('ghost_bomb', ND3.seen.ghost === true, '')
    report('wail_of_the_banshee', ND3.seen.sonic === true, '')
    // миньоны: тип, хозяин (поиск по всей зоне); саламандра приручена; стихийные — огонь (палочка) и лёд (амулет)
    ;[['mod_lavacow:vespa', 'summon_vespa'], ['mod_lavacow:banshee', 'conjure_banshee'], ['mod_lavacow:mycosis', 'conjure_mycosis'],
      ['mod_lavacow:salamander', 'summon_salamander']].forEach(p => {
      let m = all(p[0]).filter(minionOf)
      let ok = m.length == 1 && (p[1] != 'summon_salamander' || m[0].isTame())
      report(p[1], ok, `found=${m.length}${p[1] == 'summon_salamander' && m.length ? ' tame=' + m[0].isTame() : ''}`)
    })
    ;['mod_lavacow:vespa', 'mod_lavacow:banshee', 'mod_lavacow:mycosis', 'mod_lavacow:mummy'].forEach(id => all(id).forEach(e => {
      let d = Services.OBJECT_DATA.getMinionData(e); console.info(`ND3DIAG ${id} at=${Math.round(e.getX())},${Math.round(e.getY())},${Math.round(e.getZ())} summoned=${d ? d.isSummoned() : '-'} owner=${d ? d.getOwner() : '-'}`) }))
    let ml = all('necromancersdelight:mana_leech_minion').filter(minionOf)
    report('summon_mana_leech', ml.length == 3, `found=${ml.length}`)
    let el = all('necromancersdelight:elemental_leech_minion').filter(minionOf); let els = el.map(e => e.getElementIndex()).sort()
    report('summon_elemental_leech', els.indexOf(1) >= 0, `elements=${els}`)
    report('amulet_leech_crystal', els.indexOf(2) >= 0, `elements=${els}`)
    let mim = all('mod_lavacow:mimic')
    report('carcinize', mim.length == 1 && mim[0].isTame() && String(level.getBlockState(new BlockPos(A.trial, 200, 3)).getBlock().getDescriptionId()) != 'block.minecraft.chest',
      `mimics=${mim.length} tame=${mim.length ? mim[0].isTame() : '-'} `)
    let vanilla = all('minecraft:zombie').concat(all('minecraft:husk')).concat(all('minecraft:skeleton')).concat(all('minecraft:stray')).filter(minionOf)
    let swap = (id, ok2, label) => { let got = all(id).filter(minionOf); report(label, got.length == 1 && (!ok2 || ok2(got[0])), `found=${got.length}`) }
    swap('mod_lavacow:mummy', null, 'charm_mummy_minion')
    swap('mod_lavacow:unburied', e => { let n = 0; e.getArmorSlots().forEach(s => { if (!s.isEmpty()) n++ }); return n + (e.getMainHandItem().isEmpty() ? 0 : 1) + (e.getOffhandItem().isEmpty() ? 0 : 1) >= 1 }, 'ring_legion_nameless')
    swap('mod_lavacow:forsaken', null, 'ring_forsaken')
    // ванильные миньоны — только 2 зомби (Bone Bag и Book of Witchcraft), подменённые не появились
    report('no_vanilla_leftovers', vanilla.length == 2, `vanilla_minions=${vanilla.length}`)
    report('charm_bone_bag', all('mod_lavacow:boneworm').filter(minionOf).length == 1, `boneworms=${all('mod_lavacow:boneworm').length}`)
    let glow = vanilla.filter(e => hasEff(e, 'effect.minecraft.glowing') && hasEff(e, 'effect.minecraft.speed'))
    report('charm_mushroom_minion', glow.length == 1, `glowing=${glow.length}`)
    let host = near(A.leech + 4.5, 0.5, 1, 'minecraft:husk')[0]
    let hw = host ? host.getMainHandItem() : null; let w = fake.getMainHandItem()
    let IMana = Java.loadClass('com.binaris.wizardry.api.content.item.IManaItem')
    let hm = hw && hw.getItem() instanceof IMana ? hw.getItem().getMana(hw) : -1; let fm = w.getItem() instanceof IMana ? w.getItem().getMana(w) : -1
    report('amulet_leechlink', host && host.getPassengers().size() >= 1 && hm < ND3.hostMana && fm > 0, `host=${hm}/${ND3.hostMana} owner=${fm}`)
    let weta = all('mod_lavacow:weta').filter(minionOf)
    report('head_pest_crown', weta.length == 1 && weta[0].isTame(), `wetas=${weta.length}`)
    let shrooms = 0
    for (let x = A.crown - 8; x <= A.crown + 8; x++) for (let z = -8; z <= 8; z++) if (String(level.getBlockState(new BlockPos(x, 200, z)).getBlock().getDescriptionId()) == 'block.mod_lavacow.glowshroom') shrooms++
    report('glowshroom_feature', shrooms >= 1, `blocks=${shrooms}`)
    // фейкового игрока — из мира (CHANGED_DIMENSION: не сохраняется в чанк)
    fake.remove(Java.loadClass('net.minecraft.world.entity.Entity$RemovalReason').CHANGED_DIMENSION)
    console.info('ND3CHECK done')
  }
}
