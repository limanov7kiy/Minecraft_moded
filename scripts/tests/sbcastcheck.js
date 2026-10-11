// Тест порта Cataclysm: Spellbooks (patches/jar-ports/cataclysm-spellbooks): заклинания и эффекты, в которых порт
// переписал ссылки на Cataclysm 3.31 (частицы Options, ракета Иссушителя, урон лазером, сон Древнего Остатка),
// кастуются фейковым игроком без исключений и дают результат в мире. Вывод: SBCHECK <что> OK|FAIL <подробности>
ServerEvents.tick(event => {
  const server = event.server
  const t = server.tickCount
  if (t != 220 && t != 320) return   // после castcheck (тик 200, тот же фейковый игрок)
  const Spells = Java.loadClass('io.redspace.ironsspellbooks.api.registry.SpellRegistry')
  const MagicData = Java.loadClass('io.redspace.ironsspellbooks.api.magic.MagicData')
  const CastSource = Java.loadClass('io.redspace.ironsspellbooks.api.spells.CastSource')
  const Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  const level = server.getLevel('minecraft:overworld')
  const fake = Fake.getMinecraft(level)
  const report = (id, ok, info) => console.info(`SBCHECK ${id} ${ok ? 'OK' : 'FAIL'} ${info}`)
  const near = (type) => level.getEntitiesOfClass(Entity, fake.getBoundingBox().inflate(40)).stream()
    .filter(e => String(e.getType()) == type).count()
  if (t == 220) {
    server.runCommandSilent('forceload add -48 -48 48 48')
    server.runCommandSilent('fill -16 229 -16 16 229 16 minecraft:stone')
    server.runCommandSilent('fill -16 230 -16 16 240 16 minecraft:air')
    fake.setPos(0.5, 230, 0.5); fake.yRot = 0; fake.xRot = 0; fake.yHeadRot = 0; fake.setOnGround(true)
    server.runCommandSilent('summon minecraft:zombie 0.5 230 8.5 {NoAI:1b,PersistenceRequired:1b,Tags:["sbcheck"]}')
    for (let id of ['gravitation_pull', 'thoths_witness', 'aerial_assault', 'parting_shot', 'cryopiercer', 'scorched_earth', 'laserbolt']) {
      const spell = Spells.getSpell('cataclysm_spellbooks:' + id)
      try {
        spell.onCast(level, spell.getMaxLevel(), fake, CastSource.SPELLBOOK, MagicData.getPlayerMagicData(fake))
        report(id, true, 'cast')
      } catch (e) { report(id, false, `cast: ${e}`) }
    }
    // эффекты с частицами Cataclysm (RingParticle / StormParticle) — тикают на зомби
    for (let id of ['cursed_frenzy', 'kings_wrath'])
      server.runCommandSilent(`effect give @e[tag=sbcheck] cataclysm_spellbooks:${id} 10 0`)
    return
  }
  // через 5 с: призванные заклинаниями сущности на месте, фантом Остатка не спит
  const phantoms = level.getEntitiesOfClass(Entity, fake.getBoundingBox().inflate(40)).stream()
    .filter(e => String(e.getType()) == 'cataclysm_spellbooks:phantom_ancient_remnant').toList()
  report('thoths_witness_phantom', phantoms.size() > 0 && !phantoms.get(0).isSleep(),
    `phantoms=${phantoms.size()} sleep=${phantoms.size() > 0 ? phantoms.get(0).isSleep() : '-'} state=${phantoms.size() > 0 ? phantoms.get(0).getAttackState() : '-'}`)
  report('aerial_assault_missiles', true, `wither_missile=${near('cataclysm:wither_missile')}`)
  const zombie = level.getEntitiesOfClass(Entity, fake.getBoundingBox().inflate(40)).stream()
    .filter(e => e.getTags().contains('sbcheck')).toList()
  report('effects', zombie.size() > 0, `zombie=${zombie.size()} hp=${zombie.size() > 0 ? zombie.get(0).getHealth() : '-'}`)
})
