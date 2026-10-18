// Тест для модели YSM «Priestess» (docs/ysm-models.md): теги форм осколка mm:ysm_form/* (ysm_priestess_forms.js)
// и команда «/ysm molang execute» (её шлёт ysm_chat_glow.js). Первый тик после Done ≈ 2 мин — TEST_COMMANDS «#sleep 300».
// Вывод: YSMCHECK tag <форма> <число> [примеры] | YSMCHECK item <id> -> формы | YSMCHECK cmd <команда> -> <результат>
let ysmDone = false
ServerEvents.tick(event => {
  const server = event.server
  if (ysmDone || server.tickCount < 200) return
  ysmDone = true
  const say = s => console.info('YSMCHECK ' + s)
  const forms = ['sword', 'axe', 'pickaxe', 'shovel', 'hoe', 'bow', 'crossbow', 'shield', 'spear', 'staff', 'tome', 'food']
  const idsOf = f => { const out = []; Ingredient.of('#mm:ysm_form/' + f).itemIds.forEach(x => out.push(String(x))); return out }
  forms.forEach(f => {
    try { say('tag ' + f + ' ' + idsOf(f).length + ' ' + idsOf(f).slice(0, 8).join(' ')) } catch (e) { say('tag ' + f + ' ERR ' + e) }
  })
  ;['irons_spellbooks:blood_staff', 'ebwizardry:wand_master_fire', 'ars_nouveau:novice_spell_book', 'botania:fire_rod',
    'minecraft:bow', 'minecraft:cooked_beef', 'minecraft:diamond_sword', 'minecraft:stone', 'cataclysm_spellbooks:void_staff'].forEach(id => {
    try { say('item ' + id + ' -> ' + (forms.filter(f => idsOf(f).indexOf(id) >= 0).join(',') || '-')) } catch (e) { say('item ' + id + ' ERR ' + e) }
  })
  ;['ysm molang execute @a v.chat_glow=1', 'ysm molang execute @a v.chat_glow=0'].forEach(c => {
    try { say('cmd ' + c + ' -> ' + server.runCommand(c)) } catch (e) { say('cmd ' + c + ' ERR ' + e) }
  })
})
