// Логические дубли между модами: убираем рецепты убранного предмета и подставляем оставшийся в чужие рецепты
// (список — startup_scripts/dedup_list.js). Пример: модуль MekaSuit «Inhalation Purification Unit» делается
// со шлемом Create Nuclear вместо маски Hazmat.
ServerEvents.recipes(event => {
  Object.entries(global.mmDedup).forEach(([gone, kept]) => {
    event.remove({ output: gone })
    event.replaceInput({ input: gone }, gone, kept)
  })
})
// Оставшийся предмет получает item-теги убранного (список — global.mmDedupTags в dedup_list.js)
ServerEvents.tags('item', event => {
  Object.entries(global.mmDedupTags).forEach(([kept, tags]) => tags.forEach(tag => event.add(tag, kept)))
})
