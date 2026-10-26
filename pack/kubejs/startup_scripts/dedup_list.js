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
  'ad_astra:water_pump': 'mekanism:electric_pump',
  // В70 п.1 (dedup-2): Hazmat Alex's Caves — остаётся костюм Create Nuclear (mm-compat AlexsCavesHazmatMixin: защищает от облучения и кислоты AC)
  'alexscaves:hazmat_mask': 'createnuclear:default_anti_radiation_helmet',
  'alexscaves:hazmat_chestplate': 'createnuclear:default_anti_radiation_chestplate',
  'alexscaves:hazmat_leggings': 'createnuclear:default_anti_radiation_leggings',
  'alexscaves:hazmat_boots': 'createnuclear:default_anti_radiation_boots',
  // В70 п.3 (dedup-2): реактивные ранцы — остаются Create Stuff Additions; Jet Suit Ad Astra не трогаем
  'mekanism:jetpack': 'create_sa:brass_jetpack_chestplate',
  'mekanism:jetpack_armored': 'create_sa:netherite_jetpack_chestplate',
  // В70 п.4 (dedup-2): дыхание под водой — остаётся маска Mekanism; бак Create и незеритовый шлем Create остаются
  'create:copper_diving_helmet': 'mekanism:scuba_mask',
  // В70 п.5 (dedup-2): противогаз — остаётся Spore (mm-compat GasMaskCompat: защищает и от газа Create Big Cannons)
  'createbigcannons:gas_mask': 'spore:gas_mask',
  // В70 п.8 (dedup-2): парашюты — остаётся Superb Warfare
  'aether:cold_parachute': 'superbwarfare:parachute',
  'aether:golden_parachute': 'superbwarfare:parachute',
  // В69 (dedup-3, энергия): только явная разница цена ↔ выработка — кабели Ad Astra (150 / 500 FE/т) против универсальных
  // кабелей Mekanism (от 3200 FE/т при той же цене), угольный генератор Ad Astra (20 FE/т, 34 ед.) против теплового Mekanism (80 FE/т, 18 ед.)
  'ad_astra:steel_cable': 'mekanism:basic_universal_cable',
  'ad_astra:desh_cable': 'mekanism:advanced_universal_cable',
  'ad_astra:cable_duct': 'mekanism:basic_universal_cable',
  'ad_astra:coal_generator': 'mekanismgenerators:heat_generator'
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
  'createnuclear:default_anti_radiation_boots': ['caerula_arbor:enchantable/nethersea_walker', 'create_new_age:hazmat_suit', 'forge:armor/boots', 'forge:armors/boots/hazmat'],
  'createnuclear:default_anti_radiation_chestplate': ['create_new_age:hazmat_suit', 'forge:armor/chestplates', 'forge:armors/chestplates/hazmat'],
  'createnuclear:default_anti_radiation_helmet': ['create_new_age:hazmat_suit', 'forge:armor/helmets', 'forge:armors/hazmat'],
  'createnuclear:default_anti_radiation_leggings': ['create_new_age:hazmat_suit', 'forge:armor/leggings', 'forge:armors/leggings/hazmat'],
  'mekanism:scuba_mask': ['caerula_arbor:enchantable/sanity_defend', 'forge:armors', 'forge:armors/helmets'],
  'spore:gas_mask': ['ars_elemental:curio_bag_item', 'createbigcannons:gas_masks']
}
// Вкладка креатива мода, если она не «мод:мод» (id вкладок — реестр minecraft:creative_mode_tab)
const MM_DEDUP_TABS = {
  iceandfire: ['iceandfire:items'], ad_astra: ['ad_astra:main'], alexscaves: ['alexscaves:toxic_caves'], create: ['create:base'],
  createbigcannons: ['createbigcannons:base'], aether: ['aether:equipment_and_utilities', 'aether:armor_and_accessories']
}
// Убираем дубли из вкладок креатива
Object.keys(global.mmDedup).forEach(id => {
  const ns = id.split(':')[0]
  ;(MM_DEDUP_TABS[ns] || [ns + ':' + ns]).forEach(tab => StartupEvents.modifyCreativeTab(tab, event => event.remove(id)))
})
