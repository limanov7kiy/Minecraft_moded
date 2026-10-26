// Логические дубли между модами (решение владельца В54, 2026-10-26: «пусть будет один, с самым сложным крафтом»).
// Ключ — убранный предмет (без рецептов, скрыт в JEI и креативе), значение — оставшийся предмет, которым он заменяется
// в чужих рецептах. Из реестра ничего не удаляется: предметы, уже лежащие в мирах, остаются рабочими.
// Цифры сложности крафта, группы и откат — docs/dedup.md; костюм — docs/compat.md «Один защитный костюм».
global.mmDedup = {
  // Защитный костюм: остаётся Create Nuclear (защищает и от радиации Mekanism — mm-compat RadiationSuitShielding)
  'mekanism:hazmat_mask': 'createnuclear:default_anti_radiation_helmet',
  'mekanism:hazmat_gown': 'createnuclear:default_anti_radiation_chestplate',
  'mekanism:hazmat_pants': 'createnuclear:default_anti_radiation_leggings',
  'mekanism:hazmat_boots': 'createnuclear:default_anti_radiation_boots',
  // О1 (dedup-1): медные инструменты — остаётся Create Stuff Additions (у него и медная броня: полный комплект одного мода)
  'iceandfire:copper_sword': 'create_sa:copper_sword',
  'iceandfire:copper_pickaxe': 'create_sa:copper_pickaxe',
  'iceandfire:copper_axe': 'create_sa:copper_axe',
  'iceandfire:copper_shovel': 'create_sa:copper_shovel',
  'iceandfire:copper_hoe': 'create_sa:copper_hoe',
  // О2 (dedup-1): лазуритовая броня — остаётся Chaos Persists (из блоков лазурита); нагрудника у него нет, нагрудник Mekanism Tools остаётся
  'mekanismtools:lapis_lazuli_helmet': 'chaospersists:lapis_helmet',
  'mekanismtools:lapis_lazuli_leggings': 'chaospersists:lapis_leggings',
  'mekanismtools:lapis_lazuli_boots': 'chaospersists:lapis_boots',
  // О3 (dedup-1): насос воды — остаётся электронасос Mekanism (качает любую жидкость)
  'ad_astra:water_pump': 'mekanism:electric_pump'
}
// Item-теги убранного, которых нет у оставшегося: оставшийся получает их (server_scripts/dedup.js), чтобы работать во всех
// связанных механиках (например, топор Create SA без minecraft:axes не годился бы для рецептов «любой топор»).
// Генерируется: MM_AUDIT=<папка аудита> python3 -I scripts/dedup-audit/tagcopy.py (docs/dedup.md «Как считали»).
global.mmDedupTags = {
  'chaospersists:lapis_boots': ['forge:armors', 'forge:armors/boots', 'forge:armors/boots/lapis_lazuli'],
  'chaospersists:lapis_helmet': ['forge:armors', 'forge:armors/helmets', 'forge:armors/helmets/lapis_lazuli'],
  'chaospersists:lapis_leggings': ['forge:armors', 'forge:armors/leggings', 'forge:armors/leggings/lapis_lazuli'],
  'create_sa:copper_axe': ['amendments:goes_in_tripwire_hook', 'caerula_arbor:enchantable/sanity', 'caerula_arbor:enchantable/seaborn_killer', 'forge:tools', 'minecraft:axes', 'minecraft:breaks_decorated_pots', 'minecraft:enchantable/sharp_weapon', 'minecraft:tools', 'traveloptics:can_cast_reversal', 'yes_steve_model:axes'],
  'create_sa:copper_hoe': ['amendments:goes_in_tripwire_hook', 'forge:tools', 'minecraft:breaks_decorated_pots', 'minecraft:hoes', 'minecraft:tools', 'traveloptics:can_cast_reversal', 'yes_steve_model:hoes'],
  'create_sa:copper_pickaxe': ['aether:slider_damaging_items', 'amendments:goes_in_tripwire_hook', 'forge:tools', 'minecraft:breaks_decorated_pots', 'minecraft:pickaxes', 'minecraft:tools', 'traveloptics:can_cast_reversal', 'yes_steve_model:pickaxes'],
  'create_sa:copper_shovel': ['amendments:goes_in_tripwire_hook', 'forge:tools', 'minecraft:breaks_decorated_pots', 'minecraft:shovels', 'minecraft:tools', 'traveloptics:can_cast_reversal', 'yes_steve_model:shovels'],
  'create_sa:copper_sword': ['amendments:goes_in_tripwire_hook', 'caerula_arbor:enchantable/sanity', 'caerula_arbor:enchantable/seaborn_killer', 'forge:tools', 'minecraft:breaks_decorated_pots', 'minecraft:enchantable/sharp_weapon', 'minecraft:enchantable/sword', 'minecraft:swords', 'minecraft:tools', 'the_bumblezone:enchantable/comb_cutter', 'traveloptics:can_cast_reversal', 'yes_steve_model:swords'],
  'createnuclear:default_anti_radiation_boots': ['forge:armors/boots/hazmat'],
  'createnuclear:default_anti_radiation_chestplate': ['forge:armors/chestplates/hazmat'],
  'createnuclear:default_anti_radiation_helmet': ['forge:armors/hazmat'],
  'createnuclear:default_anti_radiation_leggings': ['forge:armors/leggings/hazmat']
}
// Вкладка креатива мода, если она не «мод:мод» (id вкладок — реестр minecraft:creative_mode_tab)
const MM_DEDUP_TABS = { iceandfire: 'iceandfire:items', ad_astra: 'ad_astra:main' }
// Убираем дубли из вкладок креатива
Object.keys(global.mmDedup).forEach(id => {
  const ns = id.split(':')[0]
  StartupEvents.modifyCreativeTab(MM_DEDUP_TABS[ns] || ns + ':' + ns, event => event.remove(id))
})
