// Прячем из JEI убранные логические дубли (startup_scripts/dedup_list.js)
JEIEvents.hideItems(event => {
  Object.keys(global.mmDedup).forEach(id => event.hide(id))
})
