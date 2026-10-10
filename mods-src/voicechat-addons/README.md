# voicechat-addons — порт аддонов Simple Voice Chat на Forge 1.20.1

Неофициальный порт трёх аддонов **Max Henkel** (автор Simple Voice Chat), которые выходят только под Fabric.
Код — оригинальный, взят из веток `outdated/1.20.1` репозиториев автора; изменено только то, что
связано с загрузчиком. Лицензия оригиналов — All Rights Reserved: порт сделан для частной сборки
Minecraft_moded и раздаётся только её игрокам (см. «Назначение и лицензии» в `CLAUDE.md`). Не публиковать.

| Мод (modId) | Оригинал | Версия оригинала |
|---|---|---|
| Voice Chat Interaction (`vcinteraction`) | https://github.com/henkelmax/voicechat-interaction | 1.20.1-1.0.6 |
| AudioPlayer (`audioplayer`) | https://github.com/henkelmax/audio-player | 1.20.1-1.13.2 |
| Simple Voice Chat Enhanced Groups (`enhancedgroups`) | https://github.com/henkelmax/enhanced-groups | 1.20.1-1.7.0 |

Все три мода — в одном jar `voicechat-addons-forge-<версия>.jar` (сторона `both`: событие игры
`vcinteraction:voice` регистрируется на обеих сторонах). Нужны Simple Voice Chat и Admiral (Forge).

## Что изменено при переносе
- `ModInitializer` → `@Mod`; плагины SVC помечены `@ForgeVoicechatPlugin`.
- События Fabric API → Forge: `RegisterCommandsEvent`, `ServerStartedEvent`/`ServerStoppingEvent`/
  `ServerStoppedEvent`, `PlayerLoggedInEvent`; пути — `FMLPaths`.
- Voice Chat Interaction: событие регистрируется `DeferredRegister`, частота вибрации пишется в
  `VibrationSystem.VIBRATION_FREQUENCY_FOR_EVENT` (вместо `SculkSensorFrequencyRegistry` и Polymer).
- AudioPlayer: access widener убран (закрытый конструктор `LevelResource` — через
  `ObfuscationReflectionHelper`); веб-интерфейс читается из файла мода (`ModFile.findResource`).
  Миксины — те же, с refmap.
- Fabric Permissions API на Forge нет: права — по уровням из оригинала (все / операторы).
- Библиотеки (ConfigBuilder, mp3spi, microhttp) переупакованы внутрь jar с переносом пакетов, как у оригиналов.

## Сборка
```
cd mods-src/voicechat-addons && ./gradlew build
cp build/libs/voicechat-addons-forge-*.jar ../../pack/mods/   # не *-reobf.jar
```
Веб-интерфейс AudioPlayer (`src/main/resources/web/`) собран из `web/` оригинала: `npm install && npx vite build`.

Проверка на сервере: `scripts/tests/voicecheck.js` (через `EXTRA_SERVER_SCRIPTS`).
