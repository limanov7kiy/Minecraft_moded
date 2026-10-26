// Contingency Contract Lite (Arknights): «бонусные» мобы — редкие (решение владельца В49 «б», поток 61; до этого поток 60 /
// PR #154 выключал их совсем: из любого моба падали железные/алмазные блоки, звёзды Незера).
// Механика мода: при появлении каждого моба, кроме игрока, с шансом 10 % (Math.random() < 0.1 в ZbHbFuncProcedure, зашито
// в код, конфига нет) он получает атрибут contigency_contract:bonusable = 1 и эффект contigency_contract:bonus_sign (частицы),
// а при убийстве игроком бросает таблицу contigency_contract:gameplay/chronicle_bonus (DeadFuncProcedure).
// Здесь: оставляем бонус включённым (/enableBonus true) и после появления моба один раз «перебрасываем» метку —
// оставляем её с шансом KEEP = 1 %, итого 10 % × 1 % = 0,1 % мобов. Бросок один на моба (флаг в persistentData), поэтому
// перезагрузка чанка шанс не увеличивает. Награду задаёт перекрытие таблицы
// pack/kubejs/data/contigency_contract/loot_tables/gameplay/chronicle_bonus.json (слитки и алмазы, без блоков; звезда Незера ~0,2 %).
// Порядок нашего обработчика EntityJoinLevelEvent и обработчика мода не задан: если метки ещё нет — проверяем через тик
// и ещё раз при смерти (на случай, если моба убили в тот же тик).
// Выключить совсем: вернуть из git cc_no_mob_bonus.js (PR #154) вместо этого файла и /enableBonus false.
// Проверка: scripts/tests/ccbonus.js.
const CC_KEEP = 0.01
const CC_ROLLED = 'mm_cc_bonus_roll'
ServerEvents.loaded(event => {
  event.server.runCommandSilent('enableBonus true')
  console.info('cc_rare_mob_bonus: бонусные мобы Contingency Contract — 0,1 % (enableBonus true, метка остаётся с шансом ' + CC_KEEP + ')')
})
const CC_RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
const CC_FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
const CC_BONUS = CC_FR.ATTRIBUTES.getValue(CC_RL.tryParse('contigency_contract:bonusable'))
const CC_SIGN = CC_FR.MOB_EFFECTS.getValue(CC_RL.tryParse('contigency_contract:bonus_sign'))
// Решение «оставить метку или снять» — один раз, как только метка мода видна (bonusable > 0); final — мод уже точно отработал.
function ccRoll(e, final) {
  const data = e.getPersistentData()
  if (data.getBoolean(CC_ROLLED)) return
  const a = e.getAttribute(CC_BONUS)
  const marked = a != null && a.getBaseValue() != 0
  if (!marked && !final) return
  data.putBoolean(CC_ROLLED, true)
  if (!marked || Math.random() < CC_KEEP) return
  a.setBaseValue(0)
  if (CC_SIGN != null) e.removeEffect(CC_SIGN)
}
EntityEvents.spawned(event => {
  const e = event.entity
  if (CC_BONUS == null || !e.isLiving() || e.isPlayer()) return
  ccRoll(e, false)
  if (!e.getPersistentData().getBoolean(CC_ROLLED)) e.getServer().scheduleInTicks(1, () => { if (!e.isRemoved()) ccRoll(e, true) })
})
// моб убит в тот же тик, что появился (до отложенной проверки)
EntityEvents.death(event => {
  const e = event.entity
  if (CC_BONUS != null && !e.isPlayer()) ccRoll(e, true)
})
