# Звук и голосовой чат

Simple Voice Chat, рации, физика звука.

<!-- auto:start toc -->
Модов в категории: **9**.

- [Admiral](#admiral)
- [Let Them Talk (LTT)](#let-them-talk-ltt)
- [Simple Radio](#simple-radio)
- [Simple Voice Chat](#simple-voice-chat)
- [SimpleVoiceChat Broadcast](#simplevoicechat-broadcast)
- [Sound Physics Remastered](#sound-physics-remastered)
- [Talking Heads](#talking-heads)
- [VC Admin - Voice Chat Moderation Tool](#vc-admin---voice-chat-moderation-tool)
- [Vibrative Voice](#vibrative-voice)

Вернуться: [оглавление справочника](INDEX.md).
<!-- auto:end toc -->

## Общее по категории

Голос внутри игры. Основа — **Simple Voice Chat** (сервер слушает UDP 24454); вокруг него — рации (Simple Radio), физика звука (Sound Physics Remastered),
модерация и вещание (VC Admin, SimpleVoiceChat Broadcast), «голос как вибрация» (Vibrative Voice: скалк и Хранитель слышат игроков) и клиентские украшения
(Talking Heads, Let Them Talk). Наши порты аддонов Max Henkel (AudioPlayer, Enhanced Groups, Voice Chat Interaction) — отдельный jar `voicechat-addons`,
команды — в [friends.md](../friends.md). Открыть порт для голоса — [hosting.md](../hosting.md), состав и тесты — [compat.md](../compat.md).

---

<!-- cards:start -->
## Admiral

<!-- auto:start card:admiral -->
<a id="admiral"></a>
- **Версия:** 0.4.10+1.20.1+forge · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/admiral)
- **Что это:** Библиотека обработки команд на аннотациях для Brigadier. _(источник: docs сборки)_
- **В сборке:** both: библиотека команд Max Henkel, нужна порту AudioPlayer и Enhanced Groups
<!-- auto:end card:admiral -->

### Что даёт игроку

Библиотека команд Max Henkel (для Forge). Нужна портам `voicechat-addons` — Voice Chat Interaction, AudioPlayer, Enhanced Groups: команды `/audioplayer`, `/instantgroup`, `/persistentgroup add <имя>`, `/autojoingroup set <имя>` — см. [friends.md](../friends.md) (раздел 3) и [compat.md](../compat.md).

## Let Them Talk (LTT)

<!-- auto:start card:let-them-talk -->
<a id="let-them-talk"></a>
- **Версия:** 1.0.1+1.20.1+forge · **Сторона:** client · **Источник:** [Modrinth](https://modrinth.com/mod/let-them-talk)
- **Что это:** Клиентский аддон Plasmo Voice и Simple Voice Chat: приглушает музыку и другие приложения, пока кто-то говорит. _(источник: docs сборки)_
- **В сборке:** client: приглушает музыку и другие звуки, пока кто-то говорит
- **В наших docs:** [client-checklist.md](../client-checklist.md) (1), [compat.md](../compat.md) (1), [roadmap.md](../roadmap.md) (1)
<!-- auto:end card:let-them-talk -->

### Что даёт игроку

Клиентский аддон к Simple Voice Chat: когда кто-то говорит, приглушает музыку и звуки других приложений (`DuckTarget`/«ducking»). Настраивается в меню конфигурации (YACL). VoiceChatProfiles по несовместимости убран — [compat.md](../compat.md) (К2).

## Simple Radio

<!-- auto:start card:simple-radio -->
<a id="simple-radio"></a>
- **Версия:** 1.20.1-4.5.7.9 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/simple-radio)
- **Что это:** Простая рация для связи с друзьями на расстоянии. _(источник: docs сборки)_
- **В сборке:** both: рации и радио через SVC; связки с Create и Valkyrien Skies
- **В наших docs:** [compat.md](../compat.md) (3), [client-checklist.md](../client-checklist.md) (1), [friends.md](../friends.md) (1), [roadmap.md](../roadmap.md) (1)
<!-- auto:end card:simple-radio -->

### Как играть

Рации и радио поверх Simple Voice Chat. Рации на одной частоте слышат друг друга далеко за пределами обычного голоса (чем дальше, тем больше помех).
- Предметы: `simpleradio:walkie_talkie`, `spuddie_talkie`, `transceiver`; модули (`transmitting_module`, `receiving_module`, `speaker_module`, `listener_module`, а также железные, золотые, алмазные и незеритовые варианты), `copper_wire`.
- Блоки радиосвязи: `radio`, `antenna`, `speaker`, `microphone`, `transmitter`, `receiver`, `insulator`, `frequencer` (настройка частоты) и `radiosmither` (радиокузнечный стол).
- Рецепты ищите в JEI ([jei.md](../jei.md)). Сами включились связки с Create, Valkyrien Skies, Curios, Vibrative Voice и AudioPlayer ([compat.md](../compat.md)).

## Simple Voice Chat

<!-- auto:start card:simple-voice-chat -->
<a id="simple-voice-chat"></a>
- **Версия:** forge-1.20.1-2.6.24 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/simple-voice-chat)
- **Что это:** Голосовой чат прямо в игре: близкий, группы, на сервере. _(источник: docs сборки)_
- **В сборке:** both: сам голосовой чат, группы
- **В наших docs:** [compat.md](../compat.md) (16), [hosting.md](../hosting.md) (15), [roadmap.md](../roadmap.md) (6), [performance.md](../performance.md) (2), [russification.md](../russification.md) (2)
<!-- auto:end card:simple-voice-chat -->

### Как играть

Голос по близости внутри игры (Max Henkel). Сервер слушает **UDP 24454** (отдельно от TCP 25565); если значок голоса перечёркнут — не открыт именно этот порт: [hosting.md](../hosting.md) («Голосовой чат: UDP-порт 24454»), памятка игроку — [friends.md](../friends.md) (раздел 3).
1. При первом входе откроется окно настройки микрофона; меню голоса — клавиша **V**.
2. Говорить: **Caps Lock** (push-to-talk, по умолчанию; можно включить активацию голосом); **M** — выключить микрофон, **N** — выключить голосовой чат целиком, **G** — группы; шёпот — отдельная клавиша в настройках.
3. Голос слышно на расстоянии 48 блоков (шёпот — 24), настройки сервера — `max_voice_distance`, `whisper_distance`.
4. Группы (`G`): типы **Normal** (посторонние вас не слышат, вы их слышите), **Open** (слышат друг друга все), **Isolated** (изоляция в обе стороны); группу можно защитить паролем и пригласить игрока.
5. Остальное — аддоны ниже: рации, физика звука, модерация и вещание.

### Команды/гамерулы

Клавиши: `key.push_to_talk`, `key.whisper`, `key.mute_microphone`, `key.disable_voice_chat`, `key.hide_icons`, `key.voice_chat_settings`, `key.voice_chat` (меню), `key.voice_chat_group`, `key.voice_chat_toggle_recording` («запись»), `key.voice_chat_adjust_volumes`. Запись голоса на сервере разрешена (`allow_recording=true`).

### Конфиги

`pack/config/voicechat/voicechat-server.properties` — `port=24454`, `max_voice_distance=48.0`, `whisper_distance=24.0`, `codec=VOIP`, `enable_groups=true`, `allow_recording=true`, `force_voice_chat=false`, `use_natives=true`, `voice_host` (для туннелей playit.gg, см. hosting.md). Перенос настроек клиента — [friends.md](../friends.md). Состав голосовых модов — [compat.md](../compat.md), раздел «Голосовой чат».

## SimpleVoiceChat Broadcast

<!-- auto:start card:simplevoicechat-broadcast -->
<a id="simplevoicechat-broadcast"></a>
- **Версия:** 1.0.1 · **Сторона:** server · **Источник:** [Modrinth](https://modrinth.com/mod/simplevoicechat-broadcast)
- **Что это:** Вещание аудио на всех игроков. _(источник: docs сборки)_
- **В сборке:** server (в одиночке не нужен): OP вещает на всех (группа "broadcast")
- **В наших docs:** [compat.md](../compat.md) (2)
<!-- auto:end card:simplevoicechat-broadcast -->

### Что даёт игроку

Серверный плагин Simple Voice Chat (только на сервере): голос игрока в особой группе слышат **все** на сервере (вещание). Параметры в конфиге — `broadcast_group_name` (имя группы-вещателя) и `permission_level` (уровень оператора для вещания). Моды-соседи — [compat.md](../compat.md).

## Sound Physics Remastered

<!-- auto:start card:sound-physics-remastered -->
<a id="sound-physics-remastered"></a>
- **Версия:** forge-1.20.1-1.5.1 · **Сторона:** client · **Источник:** [Modrinth](https://modrinth.com/mod/sound-physics-remastered)
- **Что это:** Реалистичное затухание, реверберация и поглощение звука блоками. _(источник: docs сборки)_
- **В сборке:** client: эхо и заглушение звука, в т.ч. голосов
- **В наших docs:** [roadmap.md](../roadmap.md) (2), [compat.md](../compat.md) (1), [russification-audit.md](../russification-audit.md) (1)
<!-- auto:end card:sound-physics-remastered -->

### Что даёт игроку

Клиентский мод: звуки и голоса получают реверберацию, а стены приглушают их. Настройки сборки — `pack/config/sound_physics_remastered/soundphysics.properties` (`environment_evaluation_ray_count=16`, `environment_evaluation_ray_bounces=2`, `max_sound_processing_distance=128.0`). Сторона `client` исправлена вручную (на Modrinth помечен как «серверно-необязательный») — [compat.md](../compat.md).

## Talking Heads

<!-- auto:start card:talkingheads -->
<a id="talkingheads"></a>
- **Версия:** 1.1.4+1.20.1+forge · **Сторона:** client · **Источник:** [Modrinth](https://modrinth.com/mod/talkingheads)
- **Что это:** Клиентский аддон голосового чата: увеличивает головы говорящих игроков. _(источник: docs сборки)_
- **В сборке:** client: голова говорящего игрока покачивается
- **В наших docs:** [client-checklist.md](../client-checklist.md) (1), [compat.md](../compat.md) (1), [roadmap.md](../roadmap.md) (1)
<!-- auto:end card:talkingheads -->

### Что даёт игроку

Клиентский аддон к Simple Voice Chat: голова говорящего игрока слегка увеличивается (пульсирует) во время речи, работает с моделью игрока и шлемами (миксины для головного слоя и брони). Настраивается в меню конфигурации (YACL). Подробности по моделям игроков — [ysm-models.md](../ysm-models.md).

## VC Admin - Voice Chat Moderation Tool

<!-- auto:start card:vc-admin-voice-chat-moderation-tool -->
<a id="vc-admin-voice-chat-moderation-tool"></a>
- **Версия:** 1 · **Сторона:** server · **Источник:** [Modrinth](https://modrinth.com/mod/vc-admin-voice-chat-moderation-tool)
- **Что это:** Серверная модерация Simple Voice Chat: операторы входят в группы с паролем без пароля; участники видят уведомление. _(источник: docs сборки)_
- **В сборке:** server (в одиночке не нужен — OP-модерация групп): OP заходят в запароленные группы (модерация)
- **В наших docs:** [compat.md](../compat.md) (3), [roadmap.md](../roadmap.md) (2)
<!-- auto:end card:vc-admin-voice-chat-moderation-tool -->

### Команды/гамерулы

Серверный инструмент оператора для групп голосового чата: команды `vcjoin <группа>`, `vcleave`, `vclist` (по именам в классе мода) — заходить в чужую группу, выходить, смотреть список. Нужен активный сервер голоса.

## Vibrative Voice

<!-- auto:start card:vibrative-voice -->
<a id="vibrative-voice"></a>
- **Версия:** 1.2.0 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/vibrative-voice)
- **Что это:** Аддон Simple Voice Chat: голос порождает вибрации, на которые реагируют датчики и мобы Sculk. _(источник: docs сборки)_
- **В сборке:** both: голос — вибрация для скалк-сенсоров и Хранителя; в одиночке нужен на клиенте (встроенный сервер), мод чисто серверной логики, клиентских классов и каналов нет
- **В наших docs:** [compat.md](../compat.md) (9), [roadmap.md](../roadmap.md) (4), [issue-history.md](../issue-history.md) (2), [friends.md](../friends.md) (1)
<!-- auto:end card:vibrative-voice -->

### Как играть

Голос превращается в **вибрации**: скалк-сенсоры, скалк-крикуны и Хранитель его слышат. Говорите громко рядом с сенсором, не приседая и не шёпотом (оба режима глушат голос). После обновления сборки это работает и в одиночной игре через встроенный сервер (поток 31).

### События

Вибрация голоса с частотой 7 (как у Voice Chat Interaction, который мы выключили, чтобы не дублировать).

### Конфиги

`config/vibrativevoice.toml` создаётся мод-библиотекой при первом запуске (в репозитории его нет): `weakVibrationThreshold = 400`, `strongVibrationThreshold = 1500`, `cooldownDuration = 20`, `peakDuration = 20`, `frequency = 7`, `restrictions.whispering`/`whisperFactor = 0.8`, `restrictions.sneaking`/`sneakFactor = 0.7`, `warden.loudnessFactor = 200`. Все значения и способ проверки — [compat.md](../compat.md) («Vibrative Voice: пороги и как проверить»). Наш `vcinteraction.properties` — `enabled=false`.

<!-- cards:end -->
