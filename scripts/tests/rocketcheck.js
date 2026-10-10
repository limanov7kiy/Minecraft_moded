// Тест: рецептов ракет Ad Astra не осталось; рецепты ракетных деталей Northstar/Creating Space на месте
ServerEvents.loaded(event => {
  const server = event.server
  let counts = {}
  server.getRecipeManager().getRecipes().forEach(r => {
    let item = r.getResultItem(server.registryAccess())
    if (item.isEmpty()) return
    let id = String(item.getItem().builtInRegistryHolder().key().location())
    if (/^ad_astra:(tier_\d_rocket|nasa_workbench|launch_pad|rocket_fin|rocket_nose_cone)$/.test(id) || /rocket|engine|thruster/.test(id))
      counts[id] = (counts[id] || 0) + 1
  })
  console.info('ROCKETCHECK ' + Object.keys(counts).sort().map(k => `${k}=${counts[k]}`).join(' '))
})
