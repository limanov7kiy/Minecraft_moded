// Тест: взрыв реактора Create Nuclear не ломает блоки (pack/kubejs/server_scripts/createnuclear_no_grief.js, поток 66).
// Задаёт вызванному взрыву размер 3 (у /summon размер 0 — взрыва нет) и пишет в лог size и noGriefing.
// Запуск: EXTRA_SERVER_SCRIPTS=<папка только с этим файлом> TEST_COMMANDS=<файл> scripts/test-server.sh <папка>, команды:
//   #sleep 60 / forceload add 0 0 / #sleep 10 / fill 0 100 0 2 110 2 minecraft:stone /
//   summon createnuclear:nuclear_explosion 1 111 1 / #sleep 60 /
//   execute if block 1 105 1 minecraft:stone run say NOGRIEF_STONE_INTACT / forceload remove all
// Успех: в логе «noGriefing=true» и «NOGRIEF_STONE_INTACT».
EntityEvents.spawned('createnuclear:nuclear_explosion', event => {
  event.entity.setSize(3)
  console.info(`cn_nogrief_test: size=${event.entity.getSize()} noGriefing=${event.entity.isNoGriefing()}`)
})
