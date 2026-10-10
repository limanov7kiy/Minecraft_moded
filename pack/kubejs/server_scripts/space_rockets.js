// Ракеты в сборке — только Northstar и Creating Space (их ракеты строятся из блоков и требуют расчёта тяги,
// топлива и Δv). Простые крафтовые ракеты Ad Astra убраны вместе со всем, что нужно только им.
// Двигатели и баки Ad Astra остаются: из них делаются реактивный костюм и ровер.
// Список предметов — в space_rockets_list.js (общий с клиентским скриптом).
ServerEvents.recipes(event => {
  global.mmRemovedRocketItems.forEach(id => event.remove({ output: id }))
})
