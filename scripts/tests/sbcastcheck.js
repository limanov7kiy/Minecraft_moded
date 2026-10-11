// Тест порта Cataclysm: Spellbooks (patches/jar-ports/cataclysm-spellbooks): заклинания и эффекты, в которых порт
// переписал ссылки на Cataclysm 3.31 (частицы Options, ракета Иссушителя, урон лазером, сон Древнего Остатка),
// кастуются фейковым игроком без исключений и дают результат в мире. Вывод: SBCHECK <что> OK|FAIL <подробности>
ServerEvents.tick(event => {
  const server = event.server
  const t = server.tickCount
  if (t != 220 && t != 280) return   // после castcheck (тик 200, тот же фейковый игрок)
  const Spells = Java.loadClass('io.redspace.ironsspellbooks.api.registry.SpellRegistry')
  const MagicData = Java.loadClass('io.redspace.ironsspellbooks.api.magic.MagicData')
  const CastSource = Java.loadClass('io.redspace.ironsspellbooks.api.spells.CastSource')
  const Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  const Vec3 = Java.loadClass('net.minecraft.world.phys.Vec3')
  const level = server.getLevel('minecraft:overworld')
  const fake = Fake.getMinecraft(level)
  const report = (id, ok, info) => console.info(`SBCHECK ${id} ${ok ? 'OK' : 'FAIL'} ${info}`)
  // перегрузка getSpell(String|ResourceLocation) неоднозначна для Rhino — берём явную; const внутри блоков Rhino не любит
  const sbCast = (id) => Spells['getSpell(java.lang.String)']('cataclysm_spellbooks:' + id).onCast(level, 3, fake,
    CastSource.SPELLBOOK, MagicData.getPlayerMagicData(fake))
  const sbAll = (pred) => { const out = []; level.getEntitiesOfClass(Entity, fake.getBoundingBox().inflate(40))
    .forEach(e => { if (pred(e)) out.push(e) }); return out }
  const sbMissiles = (n) => report('aerial_assault_missiles', n > 0, `wither_missile=${n}`)
  if (t == 220) {
    server.runCommandSilent('forceload add -48 -48 48 48')
    server.runCommandSilent('fill -16 229 -16 16 229 16 minecraft:stone')
    server.runCommandSilent('fill -16 230 -16 16 240 16 minecraft:air')
    fake.setPos(0.5, 230, 0.5); fake.yRot = 0; fake.xRot = 0; fake.yHeadRot = 0; fake.setOnGround(true)
    server.runCommandSilent('summon minecraft:zombie 0.5 230 8.5 {NoAI:1b,PersistenceRequired:1b}')   // мишень заклинаний
    server.runCommandSilent('summon minecraft:zombie -12.5 230 -12.5 {NoAI:1b,PersistenceRequired:1b,Invulnerable:1b,Tags:["sbcheck"]}')
    for (let id of ['gravitation_pull', 'thoths_witness', 'aerial_assault', 'parting_shot', 'cryopiercer', 'scorched_earth', 'laserbolt']) {
      try { sbCast(id); report(id, true, 'cast') } catch (e) { report(id, false, `cast: ${e}`) }
    }
    // Aerial Assault — длительное заклинание, ракеты летят в onServerCastTick: вызываем залп напрямую
    try {
      Spells['getSpell(java.lang.String)']('cataclysm_spellbooks:aerial_assault').shootMissiles(level, fake, 3, new Vec3(0.5, 230, 8.5))
      sbMissiles(sbAll(e => String(e.getType()) == 'cataclysm:wither_missile').length)
    } catch (e) { report('aerial_assault_missiles', false, `${e}`) }
    // эффекты с частицами Cataclysm (RingParticle / StormParticle) — тикают на зомби
    for (let id of ['cursed_frenzy', 'kings_wrath'])
      server.runCommandSilent(`effect give @e[tag=sbcheck] cataclysm_spellbooks:${id} 10 0`)
    return
  }
  // через 3 с игрового времени: призванные заклинаниями сущности на месте, фантом Остатка не спит
  const ph = sbAll(e => String(e.getType()) == 'cataclysm_spellbooks:phantom_ancient_remnant')
  report('thoths_witness_phantom', ph.length > 0 && !ph[0].isSleep(),
    `phantoms=${ph.length} sleep=${ph.length ? ph[0].isSleep() : '-'} necklace=${ph.length ? ph[0].getNecklace() : '-'} state=${ph.length ? ph[0].getAttackState() : '-'}`)
  const zb = sbAll(e => e.getTags().contains('sbcheck'))
  report('effects', zb.length > 0 && zb[0].getActiveEffects().size() >= 2, `zombie=${zb.length} effects=${zb.length ? zb[0].getActiveEffects().size() : '-'}`)
})
