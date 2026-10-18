// Тест: сколько кучи занимает загруженный шаблон структуры (поток 34). Каждые 20 с грузит следующий шаблон из списка
// через StructureTemplateManager.get (как jigsaw при генерации) и пишет TEMPLLOAD <id> blocks=<N> ms=<время>.
// Живую кучу снимать снаружи после каждой строки: jcmd <pid> GC.class_histogram | tail -1.
var TL = { ids: ['bobsoriginiumdream:orilabortory_key', 'caerula_arbor:iberiaeye_looted_1', 'bobsoriginiumdream:kingtemple',
  'caerula_arbor:abyssal_lab_2', 'caerula_arbor:izumik_island', 'aether_villages:olympic_citadel/olympic_citadel_main_0'], i: 0 }
ServerEvents.tick(event => {
  var T = event.server.tickCount
  if (T < 600 || (T - 600) % 400 != 0 || TL.i > TL.ids.length) return
  if (TL.i == TL.ids.length) { console.info('TEMPLLOAD DONE'); TL.i++; return }
  var id = TL.ids[TL.i++], t = Date.now()
  var tpl = event.server.getStructureManager().get(new ResourceLocation(id)).orElse(null)
  var n = -1
  try { n = tpl.palettes.get(0).blocks().size() } catch (e) {}
  console.info('TEMPLLOAD ' + id + ' ' + (tpl ? 'ok' : 'missing') + ' blocks=' + n + ' ms=' + (Date.now() - t))
})
