// Тест портов patches/jar-ports (вместе с ports_commands.txt и EXTRA_MODS): предметы, рецепт-замена.
ServerEvents.loaded(event => {
  for (let id of ['alshanex_familiars:evasion_curio', 'alshanex_familiars:poison_vial', 'traveloptics:eye_of_nothingness', 'cbc_nukes:nuke_shell'])
    console.info(`PORTCHECK item ${id} ${Item.of(id).isEmpty() ? 'MISSING' : 'ok'}`)
  const r = event.server.getRecipeManager().byKey('alshanex_familiars:poison_vial_from_potions')
  console.info(`PORTCHECK recipe poison_vial_from_potions ${r.isPresent() ? 'ok' : 'MISSING'}`)
})
