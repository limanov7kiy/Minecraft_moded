// Логические дубли между модами (решение владельца В54, 2026-10-26: «пусть будет один, с самым сложным крафтом»).
// Ключ — убранный предмет (без рецептов, скрыт в JEI и креативе), значение — оставшийся предмет, которым он заменяется
// в чужих рецептах. Из реестра ничего не удаляется: предметы, уже лежащие в мирах, остаются рабочими.
// Подробности, цифры сложности крафта и откат — docs/compat.md «Один защитный костюм».
global.mmDedup = {
  // Защитный костюм: остаётся Create Nuclear (защищает и от радиации Mekanism — mm-compat RadiationSuitShielding)
  'mekanism:hazmat_mask': 'createnuclear:default_anti_radiation_helmet',
  'mekanism:hazmat_gown': 'createnuclear:default_anti_radiation_chestplate',
  'mekanism:hazmat_pants': 'createnuclear:default_anti_radiation_leggings',
  'mekanism:hazmat_boots': 'createnuclear:default_anti_radiation_boots'
}
// Убираем дубли из вкладок креатива (вкладка — по моду предмета)
Object.keys(global.mmDedup).forEach(id => {
  StartupEvents.modifyCreativeTab(id.split(':')[0] + ':' + id.split(':')[0], event => event.remove(id))
})
