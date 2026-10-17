# Fish's Undead Rising — частичный порт на 1.20.1 Forge

Оригинал: **Fish's Undead Rising** — автор **Fish** (CurseForge: https://www.curseforge.com/minecraft/mc-mods/fishs-undead-rising,
проект 321139). Порт сделан по jar 1.12.2 (1.6.2, файл 8253053) и 1.16.5 (2.4.1, файл 7327600); исходников автор не публиковал.
Лицензия оригинала — All Rights Reserved; условия автора разрешают декомпиляцию и изменения только приватно.
Порт сделан по решению владельца сборки (некоммерческая сборка для друзей, см. CLAUDE.md «Назначение и лицензии»);
**по требованию автора будет удалён**. Все ассеты (текстуры, звуки, переводы) и механики — © Fish.

Портируется только то, что нужно дополнению Necromancer's Delight (WinDanesz): эффекты, нужные мобы, снаряды и предметы.
Namespace оригинала `mod_lavacow` сохранён.

## Что перенесено (0.2.0, ND-2 партия 1)
- Эффекты: Soiled, Corroded, Infested, Fragile («Смертное касание»).
- Мобы: Unburied, Mummy, Mycosis, Frigid (семейство `entities/tameable/unburied`, база `FURTameableEntity`), Forsaken;
  снаряд Death Coil; частицы locust_swarm, wither_flame; узор флага/щита «Король-скелет».
- Предметы-добыча: hyphae, poisonspore, cordy_shroom и glowshroom (в оригинале — блоки-растения, здесь простые предметы),
  cursed_fabric, ushabti, acidicheart, shattered_ice, frozenthigh, hatred_shard, forsaken_staff; яйца призыва.
- Появление — как в 1.16.5 (значения конфига по умолчанию): Mycosis — влажные/речные биомы Верхнего мира (вес 40, 8–16),
  Frigid — холодные (20, 8–16), Forsaken — песчаные (10, 4–8), Mummy — пустынные храмы (40, 4–8, structure modifier).
  Unburied сам не появляется (надгробия/Гробовщик/оружие призыва оригинала не перенесены).
