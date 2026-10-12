# JEI: просмотр крафтов и использования

Требование владельца: рецепты/использование видны для **всего**. Состояние на 2026-10-12 (поток 7).
Горячие клавиши: **R** — рецепт, **U** — использование, **A** — закладка, `@мод` — поиск по моду (по умолчанию JEI
требует префикс `@`), `$` — по тегу. Закладки и история поиска включены по умолчанию (конфиг JEI не менялся —
`pack/config/jei` не нужен).

## Что стоит
JEI 15.62, **JER** (дроп мобов, руды, торговля), **JEED** (эффекты), **Just Enough Mekanism Multiblocks**, Jade (+аддоны, jade-vs),
добавлено в этом потоке (`pack/modlist.txt`, раздел «JEI»): **Just Enough Breeding** (клиент — разведение мобов),
**Just Enough Professions** (клиент — рабочие места жителей), **Just Enough Advancements** (both — поиск достижений).
Just Enough Beacons (+ Cerbon's API) убран (поток 10): под 1.20.1 есть только сборка «1.20.4», ей нужен Cerbon's API, а его
общие миксины коллизий (AABB.intersects, Shapes.create, VoxelShape.collide, Entity.getBoundingBox) в одиночном мире работают на
встроенном сервере; польза (подсказка «что даёт маяк») не стоит риска.

## Как проверить покрытие
```
python3 scripts/fetch-mods.py client <папка вне репо>      # jar по pack/mods/*.pw.toml + свои pack/mods/*.jar
python3 -I scripts/jei_audit.py <папка> --md /tmp/jei-audit.md
```
Скрипт читает `data/*/recipes/*.json` каждого jar (и вложенных jarjar), собирает нестандартные `type`, ищет JEI-плагин
(класс `@JeiPlugin` / `IModPlugin`) и категории JEI. Тип «чужого» мода (например `create:mixing` в аддоне Create)
считается покрытым, если у мода-владельца есть плагин. Ограничение: рецепты, заданные только кодом (не в JSON), и
специальные крафты (`*_special`, dye) аудит не видит.

Итого по 242 jar: 65 модов со своим плагином и своими типами — «OK»; 30 аддонов, чьи типы показывает плагин
основного мода (Create, Mekanism, Farmer's Delight, Ars Nouveau, Aether, Mana and Artifice, Botania, Occultism, Patchouli…);
остальное — в таблице ниже.

OK (свой плагин): ae2, aether, aether_redux, alexs_caves_delight, alexscaves, alexsmobs, almostunified, animus, arknights_endfield, ars_additions, ars_elemental, ars_nouveau, arsmagicalegacy, bloodmagic, blue_skies, botania, botanicalextramachinery, botanicalmachinery, bstweaks, caerula_arbor, cataclysm, cbc_at, copycats, create, createbigcannons, createpropulsion, creatingspace, curios, deep_aether, extrabotany, gtbcs_geomancy_plus, irons_spellbooks, jade, jeed, jei, jei_mekanism_multiblocks, jeresources, kubejs, ldlib, legendarysurvivaloverhaul, lrtactical, mekanism, mekanismgenerators, mekmm, mna, modernfix, modonomicon, mythicbotany, northstar, occultengineering, occultism, ognmarts, patchouli, quark, railways, sophisticatedbackpackscreateintegration, sophisticatedcore, superbwarfare, tacz, torchesbecomesunlight, traveloptics, twilightdelight, twilightforest, waystones, zeta.

## Таблица (всё, кроме «OK (плагин)»)
| Статус | Jar | modId | Плагин | Категорий JEI | Свои типы рецептов | Чужие типы (кто показывает) |
|---|---|---|---|---|---|---|
| ПРОБЕЛ | aether_treasure_reforging-1.20.1-1.0.1-neoforge.jar | aether_treasure_reforging | нет | 0 | phoenix_armor |  |
| ПРОБЕЛ | arcaneadditions-1.20.1-forge-1.10.0.jar | arcaneadditions | нет | 0 | plow_transformation | mna |
| ПРОБЕЛ | ars_artifice-1.20.1-2.0.4.jar | ars_artifice | нет | 0 | no_damage_enchantment | ars_nouveau |
| ПРОБЕЛ | arsdelight-1.2.2.jar | arsdelight | нет | 1 | empty | ars_nouveau, farmersdelight |
| ПРОБЕЛ | electroblobs-wizardry-redux-0.8.9-forge.jar | ebwizardry | нет | 0 | imbuement_altar |  |
| ПРОБЕЛ | vs_sails-1.20.1-0.3.2-forge.jar | vs_sails | нет | 0 | wind_flag_pattern, wind_flag_dye |  |
| проверить | MekanismTools-1.20.1-10.4.16.80.jar | mekanismtools | да | 0 | banner_shield, paxel |  |
| проверить | mnaoverpowered-1.20.1-0.11.1.jar | mnaoverpowered | да | 0 | materia_bottling | mna |
| проверить | refinedstorage-1.12.4.jar | refinedstorage | да | 0 | cover_recipe, upgrade_with_enchanted_book, hollow_cover_recipe |  |
| проверить | sophisticatedbackpacks-1.20.1-3.26.9.2194.jar | sophisticatedbackpacks | да | 0 | basic_backpack, backpack_upgrade, smithing_backpack_upgrade, backpack_dye | sophisticatedcore |
| проверить | supplementaries-1.20-3.1.43-forge.jar | supplementaries | да | 0 | antique_book, bamboo_spikes_tipped, blackboard_duplicate, bubble_blower_charge, flag_from_banner, item_lore, rope_arrow_add, rope_arrow_create, safe, soap_clearing, sus_crafting, trapped_present, weathered_map, present_dye | create, farmersdelight, jeed |
| проверить | taczaddon-1.1.8.3-forge-1.20.1.jar | taczaddon | да | 0 | gun_smithing |  |
| чужой тип без плагина | Ad-Astra-Giselle-Addon-forge-1.20.1-6.20.jar | ad_astra_giselle_addon | да | 2 | can_upgrading | ae2, mekanism, tconstruct ✗, thermal ✗ |
| чужой тип без плагина | FarmersDelight-1.20.1-1.3.4.jar | farmersdelight | да | 4 | cooking, cutting, food_serving, dough | create, immersiveengineering ✗ |
| чужой тип без плагина | SereneSeasons-forge-1.20.1-9.1.0.3.jar | sereneseasons | нет | 0 |  | crafting_shaped ✗ |
| чужой тип без плагина | ad_astra-forge-1.20.1-1.15.21.jar | ad_astra | да | 7 | alloying, compressing, cryo_freezing, nasa_workbench, oxygen_loading, refining, space_station_recipe | create, immersiveengineering ✗, mekanism, patchouli, thermal ✗ |
| чужой тип без плагина | ars_technica-1.20.1-1.4.2.jar | ars_technica | да | 1 | armor_upgrade | ars_nouveau, create, create_enchantment_industry ✗ |
| чужой тип без плагина | create_alexscaves_compat-1.20.1-1.6.2.jar | create_alexscaves_compat | да | 1 | corroding | create, create_aquatic_ambitions ✗, createaddition, farmersdelight, garnished ✗ |
| чужой тип без плагина | createaddition-1.20.1-1.3.3.jar | createaddition | да | 1 | charging, liquid_burning, rolling | create, immersiveengineering ✗, jeed, mekanism |
| чужой тип без плагина | deeperdarker-forge-1.20.1-1.3.3.jar | deeperdarker | нет | 0 |  | create, farmersdelight, jeed, mekanism, pneumaticcraft ✗ |
| чужой тип без плагина | origins-forge-1.20.1-1.10.0.9-all.jar | origins | нет | 0 |  | apoli ✗ |
| чужой тип без плагина | the_bumblezone-7.14.0+1.20.1-forge.jar | the_bumblezone | да | 3 | container_shapeless_recipe_bz, nbt_keeping_shapeless_recipe, potion_candle_recipe | jeed, tokenenchanter ✗ |
| только special | amendments-1.20-2.2.6.jar | amendments | нет | 0 | dye_bottle |  |
| только special | mekanismcovers-1.3-BETA+1.20.jar | mekanismcovers | нет | 0 | crafting_special_cover |  |
| OK (чужой плагин) | MekanismAdditions-1.20.1-10.4.16.80.jar | mekanismadditions | нет | 0 |  | mekanism |
| OK (чужой плагин) | Occultism-Ritual-Satchel-1.0.2.jar | occultism_rs | нет | 0 |  | occultism |
| OK (чужой плагин) | aether_enhanced_extinguishing-1.20.1-1.0.0-neoforge.jar | aether_enhanced_extinguishing | нет | 0 |  | aether |
| OK (чужой плагин) | aethersdelight-0.1.3.1-1.20.1.jar | aethersdelight | нет | 0 |  | aether, farmersdelight |
| OK (чужой плагин) | alexsdelight-1.5.jar | alexsdelight | нет | 0 |  | farmersdelight |
| OK (чужой плагин) | alexsmobsinteraction-6.0-all.jar | alexsmobsinteraction | нет | 0 |  | patchouli |
| OK (чужой плагин) | ancient_aether-0.9.10.jar | ancient_aether | нет | 0 |  | aether |
| OK (чужой плагин) | ars_controle-1.20.1-1.1.1.jar | ars_controle | нет | 0 |  | ars_nouveau |
| OK (чужой плагин) | ars_instrumentum-1.20.1-4.1.0.jar | ars_instrumentum | нет | 0 |  | ars_nouveau |
| OK (чужой плагин) | ars_ocultas-1.20.1-1.2.2-all.jar | ars_ocultas | нет | 0 |  | ars_nouveau, occultism |
| OK (чужой плагин) | cataclysm_spellbooks-1.2.9-1.20.1-mmport.jar | cataclysm_spellbooks | нет | 0 |  | cataclysm |
| OK (чужой плагин) | cbc_nukes-2.1.1-mmport.jar | cbc_nukes | нет | 0 |  | create |
| OK (чужой плагин) | create-stuff-additions1.20.1_v2.1.2.jar | create_sa | нет | 0 |  | create |
| OK (чужой плагин) | create_ad_astra_recipes-1.0.0-forge-1.20.1.jar | create_ad_astra_recipes | нет | 0 |  | create |
| OK (чужой плагин) | create_blue_skies_compat-forge-1.20.1-1.1.0.jar | create_blue_skies_compat | нет | 0 |  | create |
| OK (чужой плагин) | createdeco-2.0.3-1.20.1-forge.jar | createdeco | нет | 0 |  | create |
| OK (чужой плагин) | dmnr-3.2.14.jar | dmnr | нет | 0 |  | mna |
| OK (чужой плагин) | farmers-cutting-blue-skies-1.20.1-1.0.jar | mr_farmers_cuttingblueskies | нет | 0 |  | farmersdelight |
| OK (чужой плагин) | fnc-1.3.1.jar | fnc | нет | 0 |  | mna |
| OK (чужой плагин) | lendersdelight-1.20.1-1.0.10b.jar | lendersdelight | нет | 0 |  | farmersdelight |
| OK (чужой плагин) | lost_aether_content-1.20.1-1.2.3.jar | lost_aether_content | нет | 0 |  | aether |
| OK (чужой плагин) | lycanitesdelight-1.20.1-0.4.0.jar | lycanitesdelight | нет | 0 |  | farmersdelight |
| OK (чужой плагин) | mechanicalbotania-1.0.2.jar | mechanicalbotania | нет | 0 |  | botania |
| OK (чужой плагин) | mek_x_star-1.20.1-1.4.0.jar | mek_x_star | нет | 0 |  | create, mekanism |
| OK (чужой плагин) | mnaw-1.20.1-forge-0.2.1-alpha-mmport.jar | mnaw | нет | 0 |  | mna |
| OK (чужой плагин) | prma-1.20.1-0.4.4-cr6.0-SIMPLE-beta-all.jar | prma | нет | 0 |  | create |
| OK (чужой плагин) | tomeofblood-1.20.1-0.4.5.jar | tomeofblood | нет | 0 |  | ars_nouveau |
| OK (чужой плагин) | toomanyglyphs-1.20.1-2.3.2.12345.jar | toomanyglyphs | нет | 0 |  | ars_nouveau |
| OK (чужой плагин) | undergardendelight-2.0.2-forge-1.20.1.jar | undergardendelight | нет | 0 |  | farmersdelight |
| OK (чужой плагин) | vs-tournament-reforged-1.0.3.jar | vs_tournament | нет | 0 |  | create |

Полная таблица — вывод скрипта.

## Пробелы и решения
| Что | Статус | Решение |
|---|---|---|
| **Electroblob's Wizardry Redux**: Imbuement Altar (`ebwizardry:imbuement_altar`, 120 рецептов) | плагина нет, JEI-аддона нет (Modrinth/CurseForge искали: «wizardry jei», «electroblob jei») | **сделано**: mm-compat 0.5.0, клиентский JEI-плагин (ниже) |
| Arcane Workbench Redux | рецепты — обычный `crafting_shaped`, видны в JEI как крафт 3×3 (без пометки «верстак») | оставлено |
| Lycanites Mobs | JEI-плагина нет; рецептов в data нет (оружие/души/алтари — кодом) | дроп мобов — через JER; плагин не пишем |
| Wizardry Restored (наш мод) | единственный рецепт — обычный крафт (`transportation_stone`) | плагин не нужен |
| mm-compat (наш мод) | рецептов нет | не нужен |
| `vs_sails`, `amendments`, `mekanismcovers`, `aether_treasure_reforging`, `arcaneadditions:plow_transformation`, `ars_artifice:no_damage_enchantment` | особые крафты/механики без своего JEI | мелочь, оставлено |
| **Blue Skies**: плагин падает на `horizonite_forge_fuel` (в JEI нет категории) | собственная ошибка мода (лог клиента) | после этой строки рецепты плагина не регистрируются; ждём обновления мода |
| **mekmm** (Mekanism More Machines): NPE в `registerItemReplicator` (`customRecipeMap` = null) | собственная ошибка мода | рецепты Replicator-а не в JEI |
| JER: «Exception caught when registering villager» (`turnToQuill`, trader=null) | один тип торговли | безвредно |

### Сделано: JEI-категория Imbuement Altar (mm-compat 0.5.0)
`mods-src/mm-compat/.../jei/MmJeiPlugin` + `ImbuementAltarCategory`: центр + 4 приёмника → результат, каталист — алтарь.
JEI и Redux — только `modCompileOnly`; JEI в рантайме необязателен (класс грузит только JEI), сервер без JEI стартует.
Проверено: сервер `Done` без JEI; клиент вошёл на сервер, категория `mm_compat:imbuement_altar` зарегистрирована, ошибок плагина нет.
Рецепт с розыгрышем `fix_ruined_spell_book` (нет приёмников) показывается без них.

## Что смотреть глазами
Чек-лист: `docs/client-checklist.md`, раздел 8 «JEI».

## Проверка клиента (поток 7)
- `scripts/test-client.sh` (главное меню) **не доходит до плагинов JEI** — они грузятся только после входа в мир. Для
  проверки нужен вход на тестовый сервер: клиент + сервер одновременно требуют ~10 ГБ RAM → в облаке понадобился своп 8 ГБ
  (`fallocate -l 8G`, `swapon`), сервер `-Xmx4G`, клиент `-Xmx5G`; YSM убрать с обеих сторон; `xdotool` нужен для кликов.
- Результат: JEI запустился, плагины зарегистрировались (`Registering ingredients/categories/recipes: …`), ошибки — только
  три мод-специфичные (Blue Skies, mekmm, JER-торговля), см. таблицу выше и `docs/compat.md`.
- Строки `Unknown recipe category: …` (≈94: `ognmarts:rlr`, `cataclysm:weapon_fusion`, `deep_aether:poison_recipe`,
  `ae2:charger`, `mythicbotany:infuser`, `arknights_endfield:*`) — это **ванильная** книга рецептов (ClientRecipeBook), не JEI:
  у этих типов нет категории вкладки книги рецептов. Рецепты в JEI показываются.
