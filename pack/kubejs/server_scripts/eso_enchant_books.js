// Книги модовых чар для Enchanting System Overhaul (поток 57, решение владельца по В47). В ESO чару можно
// накладывать только после изучения из зачарованной книги, а у части модовых чар книги обычным путём не найти:
// нет флагов торговли/лута (tradeable/discoverable) или запрет на книги (isAllowedOnBooks = false).
// Разбор всех 227 чар и таблица «чара → где взять книгу» — docs/enchanting-table.md.
// Здесь только ДОБАВЛЯЕМ отдельный пул (1 бросок: пусто или книга) в сундуки подходящих структур; лут боссов не трогаем.
// Уровень на книге случайный; в ESO по умолчанию изучение открывает все уровни (enableEnchantmentsLeveling = false).
// Проверка: scripts/tests/esobooks.js (катает эти таблицы и считает книги).

// Alex's Caves: чары оружия пещер — по биому, где добывается само оружие
const AC = 'alexscaves:'
const AC_BIOMES = {
  magnetic: { // перчатка из галенита, щит-резистор
    normal: ['crystallization', 'ferrous_haste', 'field_extension', 'arrow_inducting', 'heavy_slam'], rare: [],
    chests: ['magnetic_ruins'], cabins: ['underground_cabin_magnetic_caves'] },
  toxic: { // лучемёт
    normal: ['energy_efficiency', 'gamma_ray', 'solar', 'x_ray'], rare: [],
    chests: ['toxic_ruins'], cabins: ['underground_cabin_toxic_caves'] },
  primordial: { // первобытная дубина, копьё вымирания
    normal: ['dazing_sweep', 'swiftwood', 'chomping_spirit', 'herd_phalanx', 'plummeting_flight'], rare: ['bonking'],
    chests: ['caveman_house'], cabins: ['underground_cabin_primordial_caves'] },
  abyssal: { // морской посох, ортоланс, волшебная раковина
    normal: ['bouncing_bolt', 'enveloping_bubble', 'soak_seeking', 'triple_splash', 'flinging', 'sea_swing', 'second_wave',
      'charting_call', 'lasting_morale', 'taxing_bellow'], rare: ['seapairing', 'tsunami'],
    chests: ['abyssal_ruins'], cabins: ['underground_cabin_abyssal_chasm'] },
  forlorn: { // лук ужаса, кинжал запустения, тотем одержимости
    normal: ['dark_nock', 'precise_volley', 'twilight_perfection', 'double_stab', 'impending_stab', 'sated_blade',
      'astral_transferring', 'detonating_death', 'rapid_possession', 'sightless'], rare: ['relentless_darkness', 'shaded_respite'],
    chests: ['forlorn_ruins'], cabins: ['underground_cabin_forlorn_hollows'] },
  candy: { // сахарный посох, жвачкострел, крюк-леденец
    normal: ['humungous_hex', 'multiple_mint', 'peppermint_punting', 'seekcandy', 'spell_lasting', 'bouncy_ball',
      'targeted_ricochet', 'triple_split', 'far_flung', 'sharp_cane', 'straight_hook'], rare: ['explosive_flavor'],
    chests: ['gingerbread_town', 'licowitch_tower', 'licowitch_tower_secret'], cabins: ['underground_cabin_candy_cavity'] }
}

// Mana and Artifice (+ M&A Witchcraft): у мода свой путь — руны manaweaving накладывают чару на предмет, книг нет.
// Книга для ESO — редкая находка в «учёных» местах ванили; путь через руны M&A остаётся как был.
const MNA_NORMAL = ['mna:aura-bulwark', 'mna:aura-depths', 'mna:aura-jumping', 'mna:aura-mining', 'mna:aura-running',
  'mna:bludgeoning', 'mna:bouncing', 'mna:cloudstep', 'mna:duration_modifier', 'mna:fireproof', 'mna:gilded',
  'mna:mana-repair', 'mna:range_modifier', 'mna:returning', 'mna:soulbound', 'mna:speed_modifier', 'mna:xp_to_souls']
// mnaw:dedication без книги: в M&A её накладывает только фракция Ковена (решение координатора; вернуть — дописать сюда)
const MNA_RARE = ['mna:aura-healing', 'mna:aura-pilgrim', 'mna:aura-repair', 'mna:beheading', 'mna:transitorystep',
  'mnaw:potion_immune']

// Fungal Infection: Spore — чары грибного оружия (в т.ч. два проклятия), раньше только у самого стола
const SPORE_NORMAL = ['spore:corrosive_potency', 'spore:cryogenic_aspect', 'spore:gastric_spewage', 'spore:serrated_thorns',
  'spore:voracious_maw', 'spore:mutagenic_reactant', 'spore:unwavering_nature']
const SPORE_RARE = ['spore:symbiotic_reconstitution']

// пул: 1 бросок; wNormal/wRare — шанс (из 100) получить книгу обычной/редкой чары, делится поровну между чарами списка.
// Каждая чара — своя запись set_enchantments (уровень случайный от 1 до максимума): enchant_randomly не годится —
// миксин Alex's Caves подменяет в нём чары AC на случайные чужие (пока enchantments_in_loot = false в его конфиге).
const FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
function addBooks(pool, list, chance) {
  list.forEach(id => {
    const en = FR.ENCHANTMENTS.getValue(id)
    const max = en == null ? 1 : en.getMaxLevel()
    const lvl = {}; lvl[id] = max > 1 ? { type: 'minecraft:uniform', min: 1, max: max } : 1
    pool.addItem('minecraft:book', Math.max(1, Math.round(chance * 10 / list.length)))
      .addFunction({ function: 'minecraft:set_enchantments', enchantments: lvl })
  })
}
function addBookPool(table, normal, rare, wNormal, wRare) {
  table.addPool(pool => {
    pool.rolls = 1
    addBooks(pool, normal, wNormal)
    addBooks(pool, rare, wRare)
    pool.addEmpty(1000 - (normal.length > 0 ? wNormal * 10 : 0) - (rare.length > 0 ? wRare * 10 : 0))
  })
}

// тот же список читает тест scripts/tests/esobooks.js
global.mmEsoBookPools = []
function pool(tableId, normal, rare, wNormal, wRare) { global.mmEsoBookPools.push({ table: tableId, normal: normal, rare: rare, wNormal: wNormal, wRare: wRare }) }

Object.keys(AC_BIOMES).forEach(k => {
  const b = AC_BIOMES[k], n = b.normal.map(e => AC + e), r = b.rare.map(e => AC + e)
  b.chests.forEach(c => pool(AC + 'chests/' + c, n, r, 25, 6))   // руины/постройки биома
  b.cabins.forEach(c => pool(AC + 'chests/' + c, n, [], 12, 0))  // хижины в биоме — только обычные
})
pool('minecraft:chests/stronghold_library', MNA_NORMAL, MNA_RARE, 15, 5)
pool('minecraft:chests/ancient_city', MNA_NORMAL, MNA_RARE, 8, 4)
pool('minecraft:chests/woodland_mansion', MNA_NORMAL, MNA_RARE, 8, 3)
pool('spore:chests/document_chest', SPORE_NORMAL, SPORE_RARE, 25, 5)
pool('spore:chests/equipment_chest', SPORE_NORMAL, SPORE_RARE, 15, 5)
// Sarkaz's Furnace-side Fables: «Аннигиляция» (лук Суйе) — сокровищная, но мод не кладёт её ни в один лут
pool('sarkazs_furnace_side_fables:chests/buried_warehouse_special', [], ['sarkazs_furnace_side_fables:annihilation'], 0, 8)

// generic, а не chestLootTables: там к id дописывается «chests/», а здесь id полные
ServerEvents.genericLootTables(event => {
  global.mmEsoBookPools.forEach(p => event.modify(p.table, table => addBookPool(table, p.normal, p.rare, p.wNormal, p.wRare)))
})
