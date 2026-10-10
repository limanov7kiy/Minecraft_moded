// Тест: порт аддонов голосового чата на Forge (mods-src/voicechat-addons) — моды загружены,
// событие vcinteraction:voice зарегистрировано, есть в тегах вибраций, частота сенсора выставлена
ServerEvents.loaded(event => {
  const Registries = Java.loadClass('net.minecraft.core.registries.BuiltInRegistries')
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const Vibration = Java.loadClass('net.minecraft.world.level.gameevent.vibrations.VibrationSystem')
  const Tags = Java.loadClass('net.minecraft.tags.GameEventTags')
  for (let id of ['voicechat', 'vcinteraction', 'audioplayer', 'enhancedgroups', 'admiral']) {
    console.info(`VOICECHECK mod ${id}: ${Platform.isLoaded(id)}`)
  }
  let ev = Registries.GAME_EVENT.get(new RL('vcinteraction', 'voice'))
  if (ev == null) {
    console.info('VOICECHECK event vcinteraction:voice: MISSING')
    return
  }
  console.info(`VOICECHECK event vcinteraction:voice: frequency=${Vibration.getGameEventFrequency(ev)} ` +
    `vibrations=${ev.is(Tags.VIBRATIONS)} warden=${ev.is(Tags.WARDEN_CAN_LISTEN)}`)
})
