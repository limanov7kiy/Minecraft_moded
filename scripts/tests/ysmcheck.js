// Тест для модели YSM «Priestess» (docs/ysm-models.md): теги форм осколка mm:ysm_form/* (ysm_priestess_forms.js)
// и команда «/ysm molang execute» на игроке (её шлёт ysm_chat_glow.js). Фейковый игрок Forge, тик 200.
// Вывод: YSMCHECK tag <форма> <число> [примеры] | YSMCHECK cmd <команда> -> <результат> | YSMCHECK item <id> -> формы
ServerEvents.tick(event => {
  const server = event.server
  if (server.tickCount != 200) return
  const forms = ['sword', 'axe', 'pickaxe', 'shovel', 'hoe', 'bow', 'crossbow', 'shield', 'spear', 'staff', 'tome', 'food']
  forms.forEach(f => {
    const ids = Ingredient.of(`#mm:ysm_form/${f}`).itemIds
    console.info(`YSMCHECK tag ${f} ${ids.length} ${ids.slice(0, 8).join(' ')}`)
  })
  const probe = ['irons_spellbooks:blood_staff', 'ebwizardry:wand_master_fire', 'ars_nouveau:novice_spell_book', 'botania:fire_rod',
    'minecraft:bow', 'minecraft:cooked_beef', 'minecraft:diamond_sword', 'minecraft:stone', 'cataclysm_spellbooks:void_staff']
  probe.forEach(id => {
    const tags = forms.filter(f => Ingredient.of(`#mm:ysm_form/${f}`).test(Item.of(id)))
    console.info(`YSMCHECK item ${id} -> ${tags.join(',') || '-'}`)
  })
  const level = server.getLevel('minecraft:overworld')
  const fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory').getMinecraft(level)
  const name = fake.getGameProfile().getName()
  ;[`ysm molang execute ${name} v.chat_glow=1`, `ysm molang execute ${name} v.chat_glow=0`].forEach(c => {
    let r
    try { r = server.runCommand(c) } catch (e) { r = 'ERR ' + e }
    console.info(`YSMCHECK cmd ${c} -> ${r}`)
  })
})
