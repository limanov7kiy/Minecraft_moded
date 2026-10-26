// Аудит логических дублей (поток 66, docs/dedup.md): выгрузка предметов и рецептов в kubejs/exported/mm_dump_{items,recipes}.json.
// Запуск: mkdir -p <сервер>/kubejs/exported; EXTRA_SERVER_SCRIPTS=<папка с файлом> TEST_COMMANDS=<«#sleep 120»> scripts/test-server.sh <сервер>.
// Теги предметов отсюда не берутся (Rhino не может обойти Java Stream) — их собирает scripts/dedup-audit/tags.py из jar.
ServerEvents.tick(event => {
  const server = event.server
  if (server.tickCount != 100) return
  const reg = server.registryAccess()
  const IR = Java.loadClass('net.minecraft.core.registries.BuiltInRegistries').ITEM
  const items = []
  Item.list.forEach(st => {
    let tags = []
    try { st.getItem().builtInRegistryHolder().tags().forEach(t => tags.push(String(t.location()))) } catch (e) { if (!global.mmTagErr) { global.mmTagErr = 1; console.info('MMDUMP tag error: ' + e) } }
    items.push({ id: String(st.getId()), name: String(st.getHoverName().getString()), tags: tags })
  })
  JsonIO.write('kubejs/exported/mm_dump_items.json', { items: items })
  const recipes = []
  server.getRecipeManager().getRecipes().forEach(r => {
    const o = { id: String(r.getId()), type: String(r.getType()) }
    try { let outSt = r.getResultItem(reg); o.out = String(IR.getKey(outSt.getItem())); o.n = outSt.getCount() } catch (e) { o.out = '?'; if (!global.mmDumpErr) { global.mmDumpErr = 1; console.info('MMDUMP out error: ' + e) } }
    const ins = []
    try {
      r.getIngredients().forEach(i => {
        try { ins.push(String(i.toJson())) } catch (e) {
          try { const its = i.getItems(); ins.push(its.length ? '[' + Array.from(its).slice(0, 3).map(x => String(IR.getKey(x.getItem()))).join('|') + ']' : '{}') } catch (e2) { ins.push('?') }
        }
      })
    } catch (e) {}
    o.in = ins
    recipes.push(o)
  })
  JsonIO.write('kubejs/exported/mm_dump_recipes.json', { recipes: recipes })
  console.info(`MMDUMP items=${items.length} recipes=${recipes.length}`)
})
