# Чек-лист игрока: проверка клиента

Облачный тест (поток 5, `scripts/test-client.sh`) запускает клиент без видеокарты, без звука и микрофона.
Что можно проверить только на настоящем ПК — здесь. Займёт ~30 минут вместе с хостом.
Установка сборки — `docs/friends.md`. Память в Prism — **не меньше 6144 МБ, лучше 8192** (с 5 ГБ в мире занято 94 %).

## Если что-то пошло не так — что прислать
- Вылет: `<экземпляр>/minecraft/crash-reports/crash-….txt` (самый новый) **и** `logs/latest.log`.
  В Prism: *экземпляр → Папка → minecraft*.
- Игра просто закрылась без окна вылета: `logs/latest.log` и файл `hs_err_pid….log` из папки `minecraft`
  (если есть). Плюс что делали в этот момент.
- Не тот вид/текстура: скриншот (F2, лежат в `minecraft/screenshots`) и название предмета/моба (наведите и
  посмотрите подсказку Jade вверху экрана).
- Присылать хосту; хост кладёт находки в `docs/compat.md`.

## 1. Запуск (у всех)
- [ ] Игра доходит до главного меню. Первый запуск — до 5–10 минут, это нормально.
- [ ] Нет окна «Warning while loading mods». Если есть — скриншот окна (в облаке там был только YSM,
      ругающийся на виртуальную машину; на обычном ПК его быть не должно).
- [ ] *Mods* в меню: около 250 модов, в списке нет красных.

## 2. Вход на сервер
- [ ] Подключение без «Mismatched mod channel list» / «registry mismatch».
- [ ] Окна выбора Origin и Originium Arts открываются, выбор сохраняется после перезахода.
- [ ] Окно «This update renamed the configuration settings» (Serene Seasons Plus) **не появляется**
      (исправлено в сборке; если появилось — скриншот).
- [ ] Если вход обрывается «Timed out» на загрузке — повторить; повторяется — сообщить (на слабых ПК
      загрузка рецептов при входе долгая, сервер ждёт 15 с).

## 3. Графика: Embeddium + Oculus, Distant Horizons
- [ ] *Настройки → Видео → Пакеты шейдеров*: включить любой шейдер (Complementary, BSL). Мир рисуется,
      нет чёрного экрана, вылета; FPS приемлемый.
- [ ] С шейдером: Ad Astra (планеты/небо), Aether (небо, облака), воду, сущности Wizardry (ниже).
- [ ] Distant Horizons: дальний ландшафт за пределами прорисовки появляется через минуту-две; с шейдером тоже.
- [ ] F3: строка `Embeddium Renderer` есть, видеокарта — ваша, а не `llvmpipe`.

## 4. Модели: Yes Steve Model, AzureLib, GeckoLib
- [ ] **YSM**: меню моделей (клавиша — в *Управление*, раздел Yes Steve Model) — выбрать модель, она видна вам (F5) и другу. macOS: YSM не работает.
- [ ] Предметы Arknights Weapon (cello, huge_sword, ocean_sword…) в руке и в инвентаре — не фиолетово-чёрные
      (в логе было «Missing textures in model»).
- [ ] Броня/оружие Cataclysm Spellbooks — **сейчас мод вызывает вылет клиента**, ждём порт (поток 4);
      после него проверить броню (AzureLib) и заклинания.
- [ ] **Origin Furs / Arknights Furs** (порт под AzureLib 3.1): выбрать расу Arknights (например Vulpo) — в F5 видны
  уши/хвост, у друга тоже; анимации хвоста идут. Вылет или «пустая» модель — скриншот и latest.log (compat.md К3).

## 5. Wizardry Restored 0.6.0 (нужен оператор: `/op <ник>`)
Скастовать (или попросить хоста) и посмотреть, что всё видно и звучит:
- [ ] тотемы (radiant totem), чёрная дыра, силок (snare), шипы (forest of thorns), молот (lightning hammer) —
      модели из ванильных блоков;
- [ ] двойник (decoy) и мираж — со скином кастующего; феникс (summon phoenix) — огненная текстура;
- [ ] статуи (petrify, ice statue) — модель существа камнем/льдом; лоза (grapple) — линия до крюка;
- [ ] частицы силового поля (forcefield), грозовой тучи (stormcloud), диска (lightning disc);
- [ ] иконки заклинаний в книге/HUD, иконка эффекта «Метка жертвы»; звуки Redux при касте.

## 6. Galacticraft Revival 0.1.0
- [ ] Скелет-босс (`/summon gcrevival:skeleton_boss`): модель (в облаке рисуется), полоса здоровья, звуки,
      захват и бросок игрока.
- [ ] Сундук сокровищ после победы: модель не перевёрнута (UV под 1.12 могла «перевернуться»).
- [ ] Потухший факел — выглядит как факел без огня.

## 7. Голос (у двоих одновременно)
- [ ] Первый вход: окно настройки микрофона, клавиша V; значок микрофона не перечёркнут.
- [ ] Голос друга слышно рядом и не слышно далеко; сквозь стену глуше (Sound Physics), в пещере — эхо.
      То же с включённым шейдером (Oculus).
- [ ] Группы (G), `/instantgroup` — слышно на любом расстоянии.
- [ ] Simple Radio: две рации на одной частоте — слышно далеко, с помехами.
- [ ] `/audioplayer url <ссылка на mp3>`, затем с пластинкой в руке `/audioplayer apply <имя>` → звук в
      проигрывателе слышат все рядом; то же с козьим рогом. Загрузка через браузер (ссылка из чата).
- [ ] Talking Heads: голова говорящего покачивается; Let Them Talk: музыка тише, когда кто-то говорит.

## 8. JEI: рецепты и использование (R — рецепт, U — использование)
Подробности и таблица покрытия — `docs/jei.md`. В мире откройте инвентарь, наведите курсор на предмет в списке JEI:
**R** — как скрафтить, **U** — где используется, **A** — добавить в закладки, **Shift+A** — закладки отдельно
(правый край экрана), `?` — поиск по моду (`@create`). Все пункты — «открылось, рецепт виден, ничего не вылетело»:
- [ ] Create (+ Steam 'n' Rails, Create Additions, Big Cannons, Creating Space, Northstar): дробилка, смешивание,
      прессование, сборочная линия (sequenced assembly), «Спаутинг/Haunting»; предмет из аддона (напр. рельса Steam 'n' Rails).
- [ ] Mekanism (+ Generators, Tools, Additions, mekmm): обогащение, плавка в инфузер, химический окислитель; Just Enough Mekanism
      Multiblocks — страница мультиблоков.
- [ ] Ars Nouveau (+ Elemental, Technica, Additions…): стол зачарования (Enchanting Apparatus), письменный стол глифов,
      Imbuement chamber; любой глиф (U — что из него делается).
- [ ] Ars Magica: Legacy — алтарь/заклинания; Mana and Artifice (mna) — Eldrin-алтарь, Manaweaving, ритуалы.
- [ ] Botania (+ Mythic Botany, ExtraBotany): лепестковый аптекарь, руны, чародейский котёл, Orechid; книга Lexica — отдельно (Patchouli).
- [ ] Occultism: ритуалы (Pentacle), дробилка духов, пробирка; Blood Magic: алтарь, Alchemy Array, Hellfire Forge; Animus.
- [ ] Iron's Spells: Scroll Forge, Alchemist Cauldron, Inscription Table, Arcane Anvil (улучшение/слияние).
- [ ] Ad Astra: NASA Workbench (ракета), Compressor, Alloy furnace, Cryo freezer, Oxygen loader, Refinery;
      Northstar / Creating Space: ракетные детали, топливо, химсинтез.
- [ ] Electroblob's Wizardry Redux: Arcane Workbench (обычный крафт с рецептом в сетке), **Imbuement Altar** — в JEI
      пока не отображается (см. `docs/jei.md`, «Пробелы»); Wizardry Restored: Transportation Stone.
- [ ] Cataclysm: Weapon Fusion Anvil, Amethyst Bless; Lycanites: JEI-плагина нет (мобы/дроп — через JER, `U` на предмете).
- [ ] Farmer's Delight (+ аддоны Delight): Cooking Pot, доска (cutting); Aether (+ Redux, Deep Aether): Altar, Freezer, Incubator.
- [ ] Прочее: Twilight Forest, Blue Skies, Alex's Caves/Mobs, Sophisticated Backpacks (улучшения), AE2, Refined Storage, TaCZ.
- [ ] Дополнения: **JEBr** (кнопка «разведение» на мобе/яйце спавна), **JEP** (рабочие места жителей — «Professions»),
      **JEA** (поиск достижений), **Just Enough Beacons** (маяк в списке рецептов блока), **JER** (дроп мобов, генерация руд, торговля),
      **JEED** (эффекты зелий), **Jade** (подсказка наведения).
- [ ] Закладки (A) переживают перезаход; история поиска — стрелка вверх в строке поиска JEI.
- [ ] Консоль: в `logs/latest.log` нет строк `[jei]` с уровнем ERROR ("Recipe registration failed", "Failed to register").

## 9. Итог
Напишите хосту: ОС, видеокарта, сколько памяти дали, какие пункты прошли, какие нет (+ файлы из начала).
