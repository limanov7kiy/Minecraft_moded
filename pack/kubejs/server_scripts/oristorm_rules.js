// Ориджиниумная буря Bob's Originium Dream — редкая (поток 32, решение владельца 2026-10-18; подробности — docs/compat.md «Ориджиниумная буря»).
// Игровые правила хранятся в мире, поэтому ставим их один раз на мир (флаг в persistentData сервера) — потом админ может менять их вручную.
// Запуск бури — бросок по времени в моде-обереге (config/disaster_ward-common.toml, [oriStorm]: в среднем раз в ~3 часа игры);
// свой счётчик мода (oristormfrequency, +1 за каждое получение опыта) поднимаем до 100000, чтобы фермы опыта бурю не вызывали.
// oristormsummon: чем больше, тем реже призывы и короче буря (длительность 5000 − 10·S тиков; черви и огненные шары — раз в S/4 тиков;
// молнии — раз в S; ядра ориджиниума — раз в S/2; блоки бури с шипом 50×57 — раз в 2·S). S = 200: 2,5 минуты, ~60 червей, ~7 шипов.
const ORISTORM_RULES = [
  ['oristorm', 'true'],
  ['oristormfrequency', '100000'],
  ['oristormsummon', '200'],
  ['alloworigrow', 'false'],   // рост ориджиниума портит постройки (решение владельца)
  ['oribreak', 'false'],       // снаряды сарказа-заклинателя ломают блоки, куда попали
]
ServerEvents.loaded(event => {
  const server = event.server
  const data = server.persistentData
  if (data.getBoolean('mm_oristorm_rules')) return
  ORISTORM_RULES.forEach(r => server.runCommandSilent(`gamerule ${r[0]} ${r[1]}`))
  data.putBoolean('mm_oristorm_rules', true)
  console.info('oristorm_rules: правила бури Bob\'s Originium Dream выставлены: ' + ORISTORM_RULES.map(r => r.join('=')).join(' '))
})
