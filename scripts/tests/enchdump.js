// Выгрузка всех чар сборки (поток 57, ESO): на 100-м тике пишет строку ENCHDUMP {json} на каждую чару реестра —
// id, редкость, макс. уровень, флаги, от которых зависит, найдёт ли игрок книгу обычным путём:
// tradeable (библиотекари), discoverable (enchant_randomly / enchant_with_levels в луте), treasure (только лут/торговля),
// books (Forge isAllowedOnBooks), curse. Разбор и таблица «чара → где взять книгу» — docs/enchanting-table.md.
ServerEvents.tick(event => {
  var server = event.server
  if (server.tickCount != 100) return
  var FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  var n = 0
  FR.ENCHANTMENTS.getEntries().forEach(e => {
    var id = String(e.getKey().location())
    try {
      var en = e.getValue()
      var row = { id: id, cls: String(en.getClass().getName()), rarity: String(en.getRarity()), max: en.getMaxLevel(),
        tradeable: en.isTradeable(), discoverable: en.isDiscoverable(), treasure: en.isTreasureOnly(),
        books: en.isAllowedOnBooks(), curse: en.isCurse(), category: String(en.category) }
      console.info('ENCHDUMP ' + JSON.stringify(row)); n++
    } catch (x) { console.info('ENCHDUMP_ERR ' + id + ' ' + x) }
  })
  console.info('ENCHDUMP_DONE ' + n)
})
