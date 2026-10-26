// Create Nuclear: взрыв реактора при расплаве не ломает блоки (решение владельца: радиация не должна разрушать мир).
// Мод ставит сущности взрыва noGriefing = !mobGriefing, своего выключателя нет (ключ explosion.type в конфиге
// мод не читает — поток 66). Выключать mobGriefing целиком нельзя (криперы, мобы других модов), поэтому
// включаем noGriefing только этой сущности. Урон и радиация по существам, облучённый биом вокруг (лечится
// Biome Restore Cell мода) и разрушение самого контроллера реактора остаются.
EntityEvents.spawned('createnuclear:nuclear_explosion', event => {
  event.entity.setNoGriefing(true)
  console.info(`createnuclear_no_grief: взрыв реактора в ${event.level.dimension} ${event.entity.blockPosition()} без разрушения блоков`)
})
