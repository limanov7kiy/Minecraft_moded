// Тест: исправления шума в логе (поток 4). Категория лучей Cataclysm Spellbooks (mm_compat) и теги,
// которые раньше выбрасывались целиком из-за ссылок на отсутствующие предметы (missing_mod_refs.js).
ServerEvents.loaded(event => {
  for (let id of ['cataclysm_spellbooks:extended_laser_beam', 'cataclysm_spellbooks:extended_death_laser_beam'])
    console.info(`NOISECHECK ${id} category=${Utils.getRegistry('entity_type').getValue(id).getCategory()}`)
  for (let tag of ['farmersdelight:snacks', 'farmersdelight:sweets', 'diet:sugars', 'aether_redux:swet_jelly', 'forge:nuggets/reinforced_copper'])
    console.info(`NOISECHECK #${tag} items=${Ingredient.of('#' + tag).getItemIds().length}`)
})
// силы «Thought Load» рас Arknights: атрибут есть (mm_compat), сила — скрытая заглушка
ServerEvents.loaded(event => {
  console.info(`NOISECHECK attribute the_massestravels:thought_load_limit=${Java.loadClass('net.minecraftforge.registries.ForgeRegistries').ATTRIBUTES.containsKey(Java.loadClass('net.minecraft.resources.ResourceLocation').tryParse('the_massestravels:thought_load_limit'))}`)
})
