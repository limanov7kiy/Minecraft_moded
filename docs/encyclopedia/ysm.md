# Модели и косметика (YSM)

Yes Steve Model — свои модели игроков. Подробности — docs/ysm-models.md.

<!-- auto:start toc -->
Модов в категории: **1**.

- [Yes Steve Model](#yes-steve-model)

Вернуться: [оглавление справочника](INDEX.md).
<!-- auto:end toc -->

## Общее по категории

Одна страница — Yes Steve Model: свои анимированные модели игроков. Наборы моделей (Arknights, Endfield, Punishing: Gray Raven, Azur Lane, своя Priestess)
раздаются через `pack/config/yes_steve_model/custom/`; всё о выборе, лицензиях и проверке — [ysm-models.md](../ysm-models.md).

---

<!-- cards:start -->
## Yes Steve Model

<!-- auto:start card:yes-steve-model -->
<a id="yes-steve-model"></a>
- **Версия:** 2.6.5-forge+mc1.20.1 · **Сторона:** both · **Источник:** [Modrinth](https://modrinth.com/mod/yes-steve-model)
- **Что это:** Подменяет ванильную модель игрока своими (анимированными) моделями. _(источник: docs сборки)_
- **В сборке:** модели игроков (нативный код: Windows/Linux x64, macOS не поддерживается)
- **В наших docs:** [ysm-models.md](../ysm-models.md) (76), [compat.md](../compat.md) (22), [roadmap.md](../roadmap.md) (19), [friends.md](../friends.md) (9), [client-checklist.md](../client-checklist.md) (7), [issue-history.md](../issue-history.md) (7)
<!-- auto:end card:yes-steve-model -->

### Как играть

Подменяет модель игрока анимированной (Bedrock-модели `.ysm`). Сервер раздаёт модели всем клиентам сам. Платформы: Windows/Linux x64; **macOS не поддерживается**. Подробно — [ysm-models.md](../ysm-models.md) (выбор модели, раздача, авторство и лицензии, 18+ пакеты).
1. В «Настройки → Управление» назначьте клавиши раздела **Yes Steve Model** (по умолчанию не назначены): «Open Player Model GUI» (меню моделей), «Open Animation Roulette» (колесо анимаций), «Lock Roulette» (закрепить позу), `Play Extra Animation 0…7`, «Extra Player Render Config».
2. Меню моделей: слева пакеты (Arknights, Arknights: Endfield, Punishing: Gray Raven, Azur Lane, Azur Lane FREE-YSM-AUTHOR, встроенные, наша **Priestess**); выберите модель и текстуру. Вид от третьего лица — F5; друзья видят модель сразу.
3. Колесо анимаций — позы и настройки модели (у Priestess — пункт «Настройки»).
4. Часть моделей из пакета Azur Lane FREE-YSM-AUTHOR может быть 18+ — предупреждение и способ убрать в [ysm-models.md](../ysm-models.md).

### Команды/гамерулы

- `/ysm model reload` (оператор) — перечитать модели после правки на сервере.
- `/ysm export <модель>` — сохранить модель в `config/yes_steve_model/export/<модель>.ysm` (так проверяется загрузка).
- `auth_model` (add/remove/all/clear) — выдача моделей игрокам (по `lang`: «Add '%1$s' model for player …»).
- `/ysm molang execute <игрок> v.<переменная>=<значение>` — переменные Molang для модели; сборка использует это в `ysm_chat_glow.js` (свечение глаз Priestess при сообщении в чат).

### Конфиги

`pack/config/yes_steve_model/custom/<пакет>/` — модели: готовые лежат как packwiz-метафайлы со ссылкой на репозиторий Elaina69/Yes-Steve-Model-Repo, **Priestess** — папка-модель из `mods-src/ysm-priestess`. Какие пакеты и модели раздаются, как обновлять — [ysm-models.md](../ysm-models.md), клиентская проверка — [client-checklist.md](../client-checklist.md). Совместимость (нативный код, облачный клиент) — [compat.md](../compat.md).

<!-- cards:end -->
