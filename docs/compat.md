# Совместимость: журнал

Связка: **Minecraft 1.20.1 + Forge 47.4.26** (Java 17 или 21).
Проверка — `scripts/test-server.sh <папка>`: настоящий запуск выделенного сервера со всеми серверными модами.

## Решения по запрошенным модам

| Мод | Состояние на 1.20.1 Forge | Что сделали |
|---|---|---|
| Electroblob's Wizardry | Оригинал есть только для 1.12.2. На 1.20.1 есть **Electroblob's Wizardry Redux** — пересоздание с нуля (GPL-3.0), статус alpha, заклинания ещё добавляются | Взяли Redux |
| Lycanites Mobs | Официальная версия для 1.20.1 вышла 2026-08-01 (LGPL-3.0) | Взяли официальную |
| Galacticraft | Galacticraft Legacy — только 1.12.2. Galacticraft 5 — пре-альфа, только Fabric 1.21.1, без релиза. Официального порта на 1.20.1 Forge нет | Пока в сборке Ad Astra + Northstar Redux + Creating Space (ракеты, кислород, планеты). Порт Galacticraft — отдельный большой проект, см. ниже |
| Create: Northstar | Оригинал заброшен в 2024 г., собран под Create 0.5.1 и **роняет сервер** с Create 6 (`NoClassDefFoundError: MovementBehaviour`) | Заменили на **Northstar Redux** (MIT, поддерживается) + его дополнения |

## Найдено тестовыми запусками

1. Iron's Spells требует `playeranimator` и `irons_lib` (>=1.20.1-2, <1.20.1-3), Origins (apoli) — `caelus`,
   Originium Arts — `ldlib`. На Modrinth эти зависимости не объявлены — добавлены вручную.
2. Create: Northstar (оригинал) несовместим с Create 6 — заменён (см. выше).
3. Origin Furs (unofficial port) на выделенном сервере падает: в общей инициализации обращается к
   клиентскому классу `GeoObjectRenderer` из AzureLib. Это чисто визуальный мод (шерсть/уши origins),
   поэтому `origin-furs-unofficial-forge-port` и `arknights-furs` вручную помечены
   `side = "client"` (AzureLib остаётся на обеих сторонах — он нужен Cataclysm Spellbooks на сервере). **После `packwiz update` проверить, что пометка не слетела.**
4. Lycanites Mobs: предупреждение `Mixin apply failed lycanitesmobs.mixins.json:StructureAccessor`
   (не находит поле `settings` в `Structure`). Причина: в jar `lycanitesmobs-1.20.1` нет
   `lycanitesmobs.refmap.json`, хотя конфиг миксинов на него ссылается, — миксины к ванильным классам
   (`StructureAccessor`, `ReloadCommandMixin`, клиентский `LightTextureMixin`) в настоящей игре не
   применяются. `StructureAccessor` кодом не используется (сервер не падает); теряются перезагрузка
   JSON Lycanites по `/reload` и, вероятно, эффект освещения на клиенте. Патч на будущее: собрать
   refmap (LGPL-3.0 позволяет распространять изменённый jar) и сообщить автору.
5. Create: Northstar - Structures 1.0.0 ссылается на `northstar:moon_*_blob`, которых нет в
   Northstar Redux 0.6+ (автор сам пишет: «not compatible with Northstar 0.6.0 yet») — генерация мира
   падает с `Unbound values in registry ... placed_feature`. Отключён; вернуть, когда выйдет совместимая версия.
6. Canary × Valkyrien Skies: миксин Canary `ai.poi.PoiManagerMixin` перезаписывает
   `PoiManager.getInRange`, после чего миксин VS `feature.poi.MixinPOIManager` не может встроиться —
   сервер падает при создании мира. Фикс: `pack/config/canary.properties` → `mixin.ai.poi=false`
   (отключена одна оптимизация, остальные работают).
7. Iron's Spells 1.20.1-3.16.3 поставляет таблицы добычи `chests/catacombs/crypt_loot` и
   `chests/citadel/citadel_tomes` в формате 1.21 (`"value"` вместо `"name"`, функция
   `set_written_book_pages`) — на 1.20.1 они не загружаются, сундуки пустые. **Патч:**
   `patches/irons_loot_1_20_1.py` переписывает их в формат 1.20.1 → `pack/kubejs/data/...`
   (KubeJS перекрывает данные модов). Проверено командой `/loot spawn`: книги и добыча выпадают.
   После обновления Iron's Spells перезапустить скрипт или удалить патч, если автор исправил.
8. Serene Seasons Plus требует GabouLibs, которой нет ни на Modrinth, ни на CurseForge, — отключён.
9. Некритичное (в логе, игру не ломает): рецепты/теги для модов, которых нет в сборке
   (Ancient Aether, Aether Genesis и т.п.); спам `cataclysm_spellbooks:*laser_beam has no attributes`;
   способности рас Arknights `arks:*/thought` ссылаются на атрибут `the_massestravels:thought_load_limit`
   из мода, которого нет в открытом доступе, — эта способность у рас не работает.
10. После падения загрузки сторонние потоки держат JVM живой, сервер «висит» без вывода.
   `test-server.sh` теперь это распознаёт.

## Открытые вопросы
- Ad Astra и Northstar Redux добавляют свои Луну и Марс — на одной планете будет две версии. Нужно
  решить: оставить обе или отключить дубли конфигом/датапаком.
- Legendary Survival Overhaul и Serene Seasons: проверить интеграцию температуры с сезонами.
- Серверный тест не проверяет клиент (рендер, шейдеры Oculus + Embeddium): нужен запуск клиента у игрока.

## Порт Galacticraft (если решим делать)
Galacticraft Legacy (1.12.2) → 1.20.1 — это фактически переписывание: между версиями сменились
система блоков (flattening), рендер, сеть, генерация мира и capabilities. Объём — десятки тысяч строк.
Перед началом проверить лицензию исходников Galacticraft Legacy: она должна разрешать распространять
изменённую версию друзьям.
