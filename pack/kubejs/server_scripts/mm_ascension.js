// «Печать Возвышения» → «Возвышенное чудовище» → «Возвышенное сердце» (поток 80, пилот дорогих крафтов).
// Спецификация — docs/expensive-crafts.md, раздел 3, «Пример 2»; итоги и проверка — docs/compat.md «Печать Возвышения».
// Предметы — startup_scripts/mm_items.js. Тест — scripts/tests/ascension.js.
// Босс — Незеритовое чудовище из L_Ender's Cataclysm (cataclysm:netherite_monstrosity): база HP 600, урон 25.

// NBT вызванного босса. Occultism (SummonRitual.applyEntityNbt) сливает его с NBT уже созданной сущности
// после finalizeSpawn, поэтому базовые значения атрибутов отсюда перекрывают значения спавна.
// HP > 1024 работает благодаря AttributeFix (потолок generic.max_health 1 000 000, config/attributefix.json).
global.mmAscendedNbt = '{Attributes:[{Name:"minecraft:generic.max_health",Base:6000.0d},' +
  '{Name:"minecraft:generic.attack_damage",Base:50.0d}],Health:6000.0f,PersistenceRequired:1b,' +
  'CustomName:\'{"translate":"entity.mm.ascended_monstrosity","color":"dark_red"}\',Tags:["mm_ascended"]}'

ServerEvents.recipes(event => {
  // 1. Заготовка — Cataclysm, Наковальня слияния (weapon_fusion): Глаз чудовища + Ядро пустоты
  event.custom({
    type: 'cataclysm:weapon_fusion',
    base: { item: 'cataclysm:monstrous_eye' },
    addition: { item: 'cataclysm:void_core' },
    result: { item: 'mm:ascension_sigil_blank' }
  }).id('mm:ascension_sigil_blank')

  // 2. Зарядка — Ars Nouveau, Чародейский аппарат: заготовка + 8 трофеев боссов и редких материалов, 10 000 источника
  event.custom({
    type: 'ars_nouveau:enchanting_apparatus',
    keepNbtOfReagent: false,
    reagent: [{ item: 'mm:ascension_sigil_blank' }],
    pedestalItems: [
      { item: 'cataclysm:essence_of_the_storm' },   // Scylla (Cataclysm)
      { item: 'cataclysm:lacrima' },                // Scylla (Cataclysm)
      { item: 'cataclysm:abyssal_egg' },            // Левиафан (Cataclysm)
      { item: 'botania:life_essence' },             // Дух Гайи (Botania)
      { item: 'bloodmagic:defaultcrystal' },        // Кристалл демонической воли (Blood Magic)
      { item: 'extrabotany:hero_medal' },           // Медаль героя (ExtraBotany)
      { item: 'alexscaves:pure_darkness' },         // Чистая тьма (Alex's Caves)
      { item: 'minecraft:nether_star' }             // Звезда Незера
    ],
    output: { item: 'mm:ascension_sigil' },
    sourceCost: 10000
  }).id('mm:ascension_sigil')

  // 3. Ритуал — Occultism, пентаграмма марида; печать — активирующий предмет (расходуется)
  event.custom({
    type: 'occultism:ritual',
    ritual_type: 'occultism:summon',
    activation_item: { item: 'mm:ascension_sigil' },
    pentacle_id: 'occultism:summon_marid',
    duration: 60,
    entity_to_summon: 'cataclysm:netherite_monstrosity',
    entity_nbt: global.mmAscendedNbt,
    ritual_dummy: { item: 'occultism:ritual_dummy/custom_ritual' },
    ingredients: [
      { tag: 'forge:ingots/netherite' },
      { item: 'cataclysm:monstrous_horn' },   // трофей обычного Незеритового чудовища
      { item: 'cataclysm:ignitium_ingot' },
      { tag: 'forge:ingots/netherite' }
    ],
    // результат ритуала-призыва показывается только в JEI: награда с босса
    result: {
      item: 'mm:ascended_heart',
      nbt: { display: { Lore: ['[{"translate":"ritual.mm.ascended_monstrosity.result","italic":false,"color":"gray"}]'] } }
    }
  }).id('mm:ritual/ascended_monstrosity')
})

// 4. Награда: только босс с тегом mm_ascended (обычное Незеритовое чудовище сердце не даёт)
const ItemEntity = Java.loadClass('net.minecraft.world.entity.item.ItemEntity')
EntityEvents.death(event => {
  const e = event.entity
  if (!e.getTags().contains('mm_ascended')) return
  e.removeTag('mm_ascended')   // не выдать второй раз, если смерть придёт повторно
  // ItemEntity напрямую: у spawnAtLocation перегрузки (ItemLike|ItemStack, int|float), Rhino не может выбрать
  const drop = new ItemEntity(e.level, e.x, e.y + 1, e.z, Item.of('mm:ascended_heart'))
  drop.setDefaultPickUpDelay()
  e.level.addFreshEntity(drop)
  console.info(`[mm_ascension] Возвышенное сердце выпало: ${e.type} @ ${e.blockX} ${e.blockY} ${e.blockZ}`)
})
