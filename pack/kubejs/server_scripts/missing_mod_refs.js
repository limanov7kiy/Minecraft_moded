// Теги модов ссылаются на предметы/биомы модов, которых в сборке нет (или которые мод сам не
// регистрирует, как мороженое Twilight Delight). Ванилла тогда выбрасывает тег ЦЕЛИКОМ
// («Couldn't load tag ... missing following references»): пропадали сладости Farmer's Delight, сахара
// Diet, желе свитов Aether Redux и т.п. Убираем только эти ссылки — тег грузится с остальным.
// Список генерирует scripts/missing_tag_refs.py из лога сервера (не править руками).
// Отключённые рецепты для отсутствующих модов — pack/kubejs/data/*/recipes (условие forge:false).
const REFS = {
  "entity_type": {
    "traveloptics:aerial_collapse_dr": [
      "cataclysm:ancient_ancient_remnant"
    ],
    "traveloptics:element_fire": [
      "cataclysm:old_netherite_monstrosity"
    ],
    "traveloptics:spectral_blink_blacklist": [
      "cataclysm:ancient_ancient_remnant"
    ]
  },
  "item": {
    "aether_redux:swet_jelly": [
      "aether_genesis:blue_swet_jelly",
      "aether_genesis:dark_swet_jelly",
      "aether_genesis:golden_swet_jelly"
    ],
    "diet:sugars": [
      "twilightdelight:aurora_cake_slice",
      "twilightdelight:aurora_ice_cream",
      "twilightdelight:aurora_milkshake",
      "twilightdelight:glacier_cake_slice",
      "twilightdelight:glacier_ice_cream",
      "twilightdelight:glacier_milkshake",
      "twilightdelight:phytochemical_cake_slice",
      "twilightdelight:phytochemical_ice_cream",
      "twilightdelight:phytochemical_milkshake",
      "twilightdelight:rainbow_ice_cream",
      "twilightdelight:refreshing_ice_cream",
      "twilightdelight:torchberry_cake_slice",
      "twilightdelight:torchberry_ice_cream",
      "twilightdelight:torchberry_milkshake",
      "twilightdelight:twilight_ice_cream"
    ],
    "farmersdelight:snacks": [
      "twilightdelight:aurora_cake_slice",
      "twilightdelight:glacier_cake_slice",
      "twilightdelight:phytochemical_cake_slice",
      "twilightdelight:torchberry_cake_slice"
    ],
    "farmersdelight:sweets": [
      "twilightdelight:aurora_cake_slice",
      "twilightdelight:aurora_ice_cream",
      "twilightdelight:aurora_milkshake",
      "twilightdelight:glacier_cake_slice",
      "twilightdelight:glacier_ice_cream",
      "twilightdelight:glacier_milkshake",
      "twilightdelight:phytochemical_cake_slice",
      "twilightdelight:phytochemical_ice_cream",
      "twilightdelight:phytochemical_milkshake",
      "twilightdelight:rainbow_ice_cream",
      "twilightdelight:refreshing_ice_cream",
      "twilightdelight:torchberry_cake_slice",
      "twilightdelight:torchberry_ice_cream",
      "twilightdelight:torchberry_milkshake",
      "twilightdelight:twilight_ice_cream"
    ],
    "forge:nuggets/reinforced_copper": [
      "creatingspace:reinforced_copper_nickel"
    ]
  },
  "worldgen/biome": {
    "aether_villages:collections/ancient_aether_biomes": [
      "#ancient_aether:is_ancient_aether_biome"
    ]
  }
}


ServerEvents.tags('entity_type', event => {
  const tags = REFS['entity_type']
  for (let tag in tags) event.remove(tag, tags[tag])
})

ServerEvents.tags('item', event => {
  const tags = REFS['item']
  for (let tag in tags) event.remove(tag, tags[tag])
})

ServerEvents.tags('worldgen/biome', event => {
  const tags = REFS['worldgen/biome']
  for (let tag in tags) event.remove(tag, tags[tag])
})
