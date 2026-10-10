// Тест: сезоны Serene Seasons меняют температуру Legendary Survival Overhaul (встроенная интеграция LSO).
// Запуск: TEST_COMMANDS=scripts/tests/season_commands.txt EXTRA_SERVER_SCRIPTS=scripts/tests
// Команда /seasontemp печатает в лог сезон, вклад сезона и итоговую температуру мира у точки появления.
ServerEvents.commandRegistry(event => {
  const { commands: Commands } = event
  event.register(Commands.literal('seasontemp').executes(ctx => {
    const TU = Java.loadClass('sfiomn.legendarysurvivaloverhaul.api.temperature.TemperatureUtil')
    const SSM = Java.loadClass('sfiomn.legendarysurvivaloverhaul.common.integration.sereneseasons.SereneSeasonsModifier')
    const SH = Java.loadClass('sereneseasons.api.season.SeasonHelper')
    const level = ctx.source.server.overworld()
    const spawn = level.getSharedSpawnPos()
    const pos = level.getHeightmapPos('motion_blocking', spawn)
    const st = SH.getSeasonState(level)
    console.info(`SEASONTEMP ${st.getSubSeason()} biome=${level.getBiome(pos).unwrapKey().get().location()} ` +
      `season=${new SSM().getUncaughtWorldInfluence(level, pos)} world=${TU.getWorldTemperature(level, pos)}`)
    return 1
  }))
})
