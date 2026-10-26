// Тест логических дублей (docs/dedup.md): для каждого убранного предмета из global.mmDedup (startup_scripts/dedup_list.js)
// — нет рецептов с ним на выходе, ни один рецепт не требует именно его (ингредиент, который принимает и оставшийся предмет —
// например тег «любой топор», — не считается), оставшийся предмет существует.
// Вывод: DEDUPCHECK <убранный> → <оставшийся> out=<N> in=<N> → OK|FAIL; итог DEDUPCHECK total ok=<N> fail=<N>.
// Запуск: EXTRA_SERVER_SCRIPTS=<папка с файлом> TEST_COMMANDS=<«#sleep 60»> scripts/test-server.sh <папка>.
ServerEvents.tick(event => {
  const server = event.server
  if (server.tickCount != 200) return
  const IR = Java.loadClass('net.minecraft.core.registries.BuiltInRegistries').ITEM
  const reg = server.registryAccess()
  const out = {}, inn = {}
  const gone = Object.keys(global.mmDedup)
  const stacks = {}
  const keptSt = {}
  gone.forEach(id => { out[id] = 0; inn[id] = 0; stacks[id] = Item.of(id); keptSt[id] = Item.of(global.mmDedup[id]) })
  server.getRecipeManager().getRecipes().forEach(r => {
    let o = null
    try { o = String(IR.getKey(r.getResultItem(reg).getItem())) } catch (e) {}
    if (o && out[o] !== undefined) out[o]++
    try {
      r.getIngredients().forEach(ing => {
        gone.forEach(id => { try { if (!ing.isEmpty() && ing.test(stacks[id]) && !ing.test(keptSt[id])) inn[id]++ } catch (e) {} })
      })
    } catch (e) {}
  })
  let ok = 0, fail = 0
  gone.forEach(id => {
    const kept = global.mmDedup[id]
    const good = out[id] == 0 && inn[id] == 0 && !Item.of(kept).isEmpty()
    if (good) ok++; else fail++
    console.info(`DEDUPCHECK ${id} → ${kept} out=${out[id]} in=${inn[id]} → ${good ? 'OK' : 'FAIL'}`)
  })
  console.info(`DEDUPCHECK total ok=${ok} fail=${fail}`)
})
