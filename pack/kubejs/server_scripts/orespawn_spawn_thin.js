// OreSpawn (Chaos Persists) в Overworld: естественный спавн реже (решение владельца: «чтобы это происходило не часто»).
// У мода вес спавна зашит в код (EntityRegistry.addSpawn), в chaospersists.cfg его нет: есть только «выкл. всё» и
// «выкл. моба». Поэтому отсекаем долю попыток естественного спавна мобов chaospersists:* в minecraft:overworld.
// Отсечённая попытка не занимает лимит мобов, обычный спавн Minecraft и других модов идёт как раньше.
// Не трогает: измерения Chaos (в т.ч. Utopia — там мобы как в оригинале), Незер/Энд, яйца, спавнеры, /summon.
const ORESPAWN_KEEP = 0.25   // доля сохраняемых попыток: 0.25 = спавн в 4 раза реже
ServerEvents.loaded(() => { console.info(`orespawn_spawn_thin: оставляем ${ORESPAWN_KEEP * 100}% естественных спавнов chaospersists:* в Overworld`) })
EntityEvents.checkSpawn(event => {
  const t = String(event.type)
  if (t != 'NATURAL' && t != 'CHUNK_GENERATION') return
  if (String(event.level.dimension) != 'minecraft:overworld') return
  if (!String(event.entity.type).startsWith('chaospersists:')) return
  global.orespawnSeen = (global.orespawnSeen || 0) + 1   // счётчики для строки статистики ниже
  if (Math.random() >= ORESPAWN_KEEP) { global.orespawnCut = (global.orespawnCut || 0) + 1; event.cancel() }
})
// Раз в 10 минут пишем в лог, сколько попыток увидели и сколько отсекли (чтобы на живом сервере видеть, что фильтр работает).
ServerEvents.tick(event => {
  if (event.server.tickCount % 12000 != 0 || !global.orespawnSeen) return
  console.info(`orespawn_spawn_thin: попыток спавна chaospersists:* в Overworld ${global.orespawnSeen}, отсечено ${global.orespawnCut || 0}`)
})
