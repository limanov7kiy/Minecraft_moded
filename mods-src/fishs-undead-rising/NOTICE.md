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

## ND-2 партия 3 (0.2.0)
- Мобы: Salamander (нимфа → 3 стадии роста, приручение нимфы, ведро, седло и залп всадника, раздувает печи),
  Emperor Weta (объедает/собирает урожай, больная вета), Mimicrab (засада сундуком, инвентарь 27 ячеек, пустотный/адский);
  снаряд War Small Fireball; эффект Immolation (зелья не перенесены).
- Предметы: moltenbeef, burntovipositor, salamander_bucket, imp_horn, kung_pao_chicken, chitin, weta_jaw, diseased_wheat,
  diseased_bread, plagued_porkchop, green_bacon_and_eggs, mimic_claw(_cooked), ptera_wing(_cooked), mootenheart; яйца.
- Появление: Salamander — Пустоши Незера (10, 4–8) и Долина песка душ (1, 4–8); Weta — саванны (30, 4–8);
  Mimicrab — у сундуков внутри строений (тег `mod_lavacow:mimic_habitat`, вес 10; в аванпосте — 1), а также с шансом 25%
  у места гибели игрока. В 1.16.5 мимикраб добавлялся в списки спавна строений, что заменяло там всех прочих монстров;
  здесь — общий biome modifier + проверка строения в правиле появления (зомби в деревнях и т. п. не пропадают).
