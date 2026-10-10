// Прячем из JEI предметы ракет Ad Astra, рецепты которых убраны (server_scripts/space_rockets.js)
JEIEvents.hideItems(event => {
  global.mmRemovedRocketItems.forEach(id => event.hide(id))
})
