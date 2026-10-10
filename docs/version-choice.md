# Выбор версии (данные Modrinth на 2026-10-10)

`+` — версия под эту связку есть, `-` — нет. Twilight Forest в таблице отсутствует: он публикуется
только на CurseForge (есть и под 1.20.1, и под 1.21.1).

| Мод | Тема | 1.20.1 Forge | 1.21.1 NeoForge |
|---|---|---|---|
| Caerula Arbor | Arknights | + | - |
| Bob's Originium Dream | Arknights | + | - |
| Originium Arts | Arknights | + | - |
| Endfield Industry | Arknights | + | + |
| Contingency Contract Lite | Arknights | + | + |
| Create: Originium Industry | Arknights | - | + |
| Ad Astra | космос/ракеты | + | + |
| Create: Northstar | космос/ракеты | + | - |
| Creating Space | космос/ракеты | + | + |
| Create Rockets | космос/ракеты | - | + |
| Create Big Cannons | ракеты/артиллерия | + | + |
| Valkyrien Skies | корабли | + | - |
| Create Aeronautics (+ Sable) | корабли | - | + |
| Ars Nouveau | магия | + | + |
| Iron's Spells 'n Spellbooks | магия | + | + |
| Botania | магия | + | - |
| Occultism | магия | + | + |
| Alex's Caves | измерения | + | - |
| Aether | измерения | + | + |
| Blue Skies | измерения | + | - |
| Undergarden | измерения | + | + |
| Deeper and Darker | измерения | + | + |
| L_Ender's Cataclysm | боссы/измерения | + | + |
| Alex's Mobs | мобы | + | - |
| Tough As Nails | реализм | + | + |
| Serene Seasons | реализм | + | + |
| Legendary Survival Overhaul | реализм | + | + |
| TerraFirmaCraft | реализм (полная переделка) | + | + |
| Embeddium / Sodium | оптимизация (клиент) | Embeddium + | оба + |
| ModernFix, FerriteCore | оптимизация | + | + |
| Radium / Canary (порты Lithium) | оптимизация (сервер) | оба + | только Radium + |
| Distant Horizons | дальность прорисовки | + | + |

Версии 26.x пока почти пусты: Create и большинство крупных контентных модов — максимум 1.21.1.

**Вывод:** 1.20.1 Forge — у неё самый большой выбор контента, и на ней почти все моды по Arknights.
Потери: Create Aeronautics и Create Rockets (есть только под 1.21.1). Небольшие моды можно
бэкпортировать, но только если лицензия позволяет распространять изменённую сборку.

## Почему нельзя «соединить версии»
Сервер и клиент — это один процесс игры одной версии; моды компилируются под её внутренности.
Мод для 1.21.1 не загрузится в 1.20.1. Реальные варианты:
- **Бэкпорт / порт отдельного мода** — переписать его под нашу версию. Для небольших модов это реально,
  для больших (Create Aeronautics) — нет.
- **Sinytra Connector** — запускает Fabric-моды на Forge, но только в пределах одной версии Minecraft.
- **Velocity-прокси с несколькими серверами разных версий** — это переходы между отдельными мирами,
  а не одна объединённая игра; у клиента должна быть своя сборка под каждый.
- Проекты вроде «Minecraft + Skyrim» запускали две игры рядом и обменивались состоянием.
  Для одной модовой сборки такой подход не годится.
