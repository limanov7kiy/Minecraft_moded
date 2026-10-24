// Contingency Contract Lite (Arknights): выключаем «бонусных» мобов (поток 60: «из любого моба падают железные/алмазные
// блоки, звёзды Незера» — ломает прогрессию). Механика мода: при появлении каждого моба, кроме игрока, с шансом 10 %
// он получает атрибут contigency_contract:bonusable = 1 (ZbHbFuncProcedure, рядом с ним частицы bonus_singal), и при
// убийстве игроком такой моб выбрасывает таблицу contigency_contract:gameplay/chronicle_bonus — алмазы, алмазный и
// железные блоки, незерит, звезда Незера, зачарованные яблоки (DeadFuncProcedure). Включено по умолчанию
// (MapVariables.enableBonus = true), у мода есть свой выключатель — команда /enableBonus <true|false>.
// Здесь: на старте сервера выключаем его штатной командой и снимаем метку с уже помеченных мобов из старых чанков
// (животные не деспавнятся и иначе остались бы «бонусными» навсегда). Остальные механики мода не трогаем.
// Вернуть бонус: удалить этот файл и выполнить /enableBonus true. Проверка: scripts/tests/mobdrops.js.
ServerEvents.loaded(event => {
  event.server.runCommandSilent('enableBonus false')
  console.info('cc_no_mob_bonus: бонусные мобы Contingency Contract выключены (enableBonus false)')
})
const CC_BONUS = Java.loadClass('net.minecraftforge.registries.ForgeRegistries').ATTRIBUTES
  .getValue(Java.loadClass('net.minecraft.resources.ResourceLocation').tryParse('contigency_contract:bonusable'))
EntityEvents.spawned(event => {
  const e = event.entity
  if (CC_BONUS == null || !e.isLiving()) return
  const a = e.getAttribute(CC_BONUS)
  if (a != null && a.getBaseValue() != 0) a.setBaseValue(0)
})
