// Модель YSM «Priestess» (docs/ysm-models.md): глаза-ромбы вспыхивают на 2,5 с, когда игрок пишет в чат.
// Сервер ставит переменную модели v.chat_glow через «/ysm molang execute <игрок> …» (YSM рассылает её клиентам),
// модель плавно гасит свечение, когда переменная снова 0. Для игроков с другими моделями переменная ни на что не влияет.
const CHAT_GLOW_TICKS = 50
ServerEvents.loaded(() => { global.ysmChatGlow = {} })
PlayerEvents.chat(event => {
  const player = event.player
  const server = event.server
  const name = player.username
  const until = server.tickCount + CHAT_GLOW_TICKS
  if (!global.ysmChatGlow) global.ysmChatGlow = {}
  global.ysmChatGlow[name] = until
  server.runCommandSilent(`ysm molang execute ${name} v.chat_glow=1`)
  server.scheduleInTicks(CHAT_GLOW_TICKS, () => {
    if (global.ysmChatGlow[name] != until) return   // за это время было новое сообщение — гасит его таймер
    delete global.ysmChatGlow[name]
    server.runCommandSilent(`ysm molang execute ${name} v.chat_glow=0`)
  })
})
