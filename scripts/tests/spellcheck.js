// Тест: есть ли заклинания Wizardry Restored в реестре Redux и читаются ли их свойства
ServerEvents.loaded(event => {
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const RM = Java.loadClass('net.minecraftforge.registries.RegistryManager')
  const reg = RM.ACTIVE.getRegistry(new RL('ebwizardry', 'spells'))
  console.info(`SPELLCHECK total spells: ${reg.getKeys().size()}`)
  for (let id of ['lightning_bolt', 'sixth_sense', 'shockwave', 'ebwizardry:fireball', 'ebwizardry:arc']) {
    let rl = id.includes(':') ? new RL(id) : new RL('wizardry_restored', id)
    let s = reg.getValue(rl)
    if (s == null) { console.error(`SPELLCHECK MISSING ${id}`); continue }
    console.info(`SPELLCHECK ${id}: class=${s.getClass().getSimpleName()} ihc=${s.hashCode()} key=${reg.getKey(s)} loc=${s.getLocation()} tier=${s.getTier()} cost=${s.getCost()} cd=${s.getCooldown()}`)
  }
})
