// Модель YSM «Priestess» (pack/config/yes_steve_model/custom/priestess, docs/ysm-models.md): осколок Originium
// принимает форму предмета в руке. Модель (functions/pr_form_*.molang) проверяет теги mm:ysm_form/<форма>,
// поэтому здесь собираем их из ванильных/forge-тегов и id предметов магических модов сборки.
// Теги — серверные данные, клиенты получают их при входе; на чужом сервере без этого скрипта модель
// всё равно узнаёт мечи/топоры/кирки/лопаты/мотыги/лук/арбалет/щит/трезубец по ванильным тегам.
// Предмет работает как обычно — меняется только отрисовка в руке.
ServerEvents.tags('item', event => {
  const T = f => `mm:ysm_form/${f}`
  event.add(T('sword'), ['#minecraft:swords', '#forge:tools/swords'])
  event.add(T('axe'), ['#minecraft:axes', '#forge:tools/axes'])
  event.add(T('pickaxe'), ['#minecraft:pickaxes', '#forge:tools/pickaxes'])
  event.add(T('shovel'), ['#minecraft:shovels', '#forge:tools/shovels'])
  event.add(T('hoe'), ['#minecraft:hoes', '#forge:tools/hoes'])
  event.add(T('bow'), ['#forge:tools/bows', 'minecraft:bow'])
  event.add(T('crossbow'), ['#forge:tools/crossbows', 'minecraft:crossbow'])
  event.add(T('shield'), ['#forge:tools/shields', 'minecraft:shield'])
  event.add(T('spear'), ['#forge:tools/tridents', 'minecraft:trident'])
  // посохи, жезлы, палочки: Iron's Spells, Cataclysm Spellbooks, Wizardry (Redux/Restored), Mana and Artifice,
  // Ars Nouveau, Botania, Darker Magic и др. — по имени; апгрейды/сферы и «стержни» как материал не берём
  event.add(T('staff'), [/:[a-z_]*(staff|staves|wand|scepter|sceptre|cane)[a-z_0-9]*$/, '#botania:rods'])
  event.remove(T('staff'), [/upgrade/, /_casting$/, /dowsing/, /:wand_point$/])
  // книги заклинаний, которые держат в руке (Ars Nouveau, Ars Magica: Legacy, Wizardry, Iron's, Ars Elemental…)
  event.add(T('tome'), [/:[a-z_]*(spell_book|spellbook|caster_tome|arcane_tome|grimoire|codex)[a-z_0-9]*$/])
  // еда и питьё: всё съедобное + зелья и бутылки
  Item.list.forEach(stack => { if (stack.item.isEdible()) event.add(T('food'), stack.id) })
  event.add(T('food'), ['minecraft:potion', 'minecraft:honey_bottle', 'minecraft:milk_bucket', 'minecraft:splash_potion', 'minecraft:lingering_potion'])
})
