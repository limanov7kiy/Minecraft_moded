// Тест: есть ли заклинания Wizardry Restored в реестре Redux и читаются ли их свойства
ServerEvents.loaded(event => {
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const RM = Java.loadClass('net.minecraftforge.registries.RegistryManager')
  const reg = RM.ACTIVE.getRegistry(new RL('ebwizardry', 'spells'))
  console.info(`SPELLCHECK total spells: ${reg.getKeys().size()}`)
  for (let id of ['lightning_bolt', 'sixth_sense', 'shockwave', 'snowball', 'mine', 'mind_trick', 'blink', 'intimidate', 'glide', 'ironflesh', 'diamondflesh', 'greater_fireball', 'chain_lightning', 'paralysis', 'summon_iron_golem', 'lightning_ray', 'disintegration', 'transience', 'mind_control', 'metamorphosis', 'frost_barrier', 'wall_of_frost', 'thunderstorm', 'earthquake', 'celestial_smite', 'slow_time', 'light', 'shadow_ward', 'ice_shroud', 'summon_spirit_wolf', 'summon_spirit_horse', 'freezing_weapon', 'empowering_presence', 'muffle', 'grapple', 'transportation', 'ice_statue', 'petrify', 'lightning_pulse', 'summon_ice_giant', 'conjure_armor', 'silverfish_swarm', 'forcefield', 'radiant_totem', 'withering_totem', 'black_hole', 'stormcloud', 'decoy', 'summon_ice_wraith', 'summon_shadow_wraith', 'summon_storm_elemental', 'shield', 'lightning_web', 'imbue_weapon', 'mark_sacrifice', 'mirage', 'snare', 'forest_of_thorns', 'lightning_disc', 'lightning_hammer', 'clairvoyance', 'divination', 'summon_phoenix', 'resurrection', 'possession', 'ebwizardry:fireball']) {
    let rl = id.includes(':') ? new RL(id) : new RL('wizardry_restored', id)
    let s = reg.getValue(rl)
    if (s == null) { console.error(`SPELLCHECK MISSING ${id}`); continue }
    console.info(`SPELLCHECK ${id}: class=${s.getClass().getSimpleName()} ihc=${s.hashCode()} key=${reg.getKey(s)} loc=${s.getLocation()} tier=${s.getTier()} cost=${s.getCost()} cd=${s.getCooldown()}`)
  }
})
