#!/usr/bin/env python3
"""Сокращение подписей клавиш («Управление → Назначение клавиш») в ru-lang: на масштабе интерфейса x3+ строки длиннее
~28 символов уезжают за левый край экрана. Префиксы вида «(Книга заклинаний)», «[Elemental]» зашиты в сами ключи
key.* модов (Ars Nouveau/Elemental/Additions/Instrumentum), клиентского мода-«приклеивателя» нет.
Правила — таблица OVR ниже (ns, ключ → строка); скрипт идемпотентен: дописывает/правит ru_ru.json в
pack/resourcepacks-src/russian-moded. Запуск: scripts/lang_shorten_keys.py  (затем lang_validate.py, build-ru-pack.sh)."""
import json, pathlib, sys
root = pathlib.Path(__file__).resolve().parent.parent / "pack/resourcepacks-src/russian-moded/assets"
OVR = {
 "ars_nouveau": {
  "key.ars_nouveau.next_slot": "Книга: следующий слот",
  "key.ars_nouveau.previous_slot": "Книга: предыдущий слот",
  "key.ars_nouveau.open_book": "Книга: открыть",
  **{f"key.ars_nouveau.qc{i}": f"Быстрое заклинание {i:02d}" for i in range(1, 11)},
 },
 "ars_instrumentum": {
  "key.ars_nouveau.choose_armarium_slot": "Армариум: HUD выбора",
  "key.ars_nouveau.switch_armarium_slot": "Переключить армариум",
 },
 "ars_elemental": {"key.ars_elemental.open_pouch": "Открыть мешочек безделушек"},
 "ars_additions": {"key.ars_additions.open_lectern": "Активировать варп-индекс"},
 "mna": {
  "key.cast-any-slot": "Заклинание: любой слот",
  "key.cast-back-slot": "Заклинание: слот спины",
  "key.cast-belt-slot": "Заклинание: слот пояса",
  "key.cast-hand-slot": "Заклинание: слот Curios",
  "key.gui-key-modifier": "Модификатор открытия GUI",
  "key.spellbookopen": "Быстрый выбор заклинаний",
 },
 "dmnr": {
  "dmnr.key.use_spell_storing_ring": "DM&R: Браслет заклинаний",
  "dmnr.key.togglenightvision": "DM&R: Ночное зрение",
 },
 "fdlib": {"fdlib.key.end_cutscene": "Завершить кат-сцену"},
 "iris": {"iris.keybind.wireframe": "Каркас (только одиночная)"},
 "jade": {"key.jade.show_details_alternative": "Показать детали (альтерн.)"},
 "jei": {
  "jei.key.category.hover.config.button": "JEI (на кнопке настроек)",
  "key.jei.toggleBookmarkOverlay": "Показать/скрыть закладки",
  "key.jei.maxTransferRecipeBookmark": "Рецепт закладки (макс.)",
  "key.jei.transferRecipeBookmark": "Рецепт закладки (один)",
  "key.jei.copy.recipe.id": "Копировать ID рецепта",
  "key.jei.pauseRecipeCycling": "Пауза смены ингредиентов",
  "key.jei.quickMove": "Быстрое перемещение призрака",
  "key.jei.toggleEditMode": "Режим скрытия ингредиентов",
  "key.jei.toggleOverlay": "Показать/скрыть наложения",
  "key.jei.toggleWildcardHideIngredient": "Скрыть ингредиент (шаблон)",
 },
 "ae2": {
  "key.ae2.portable_fluid_cell": "Переносная жидкостная ячейка",
  "key.ae2.portable_item_cell": "Переносная предметная ячейка",
  "key.ae2.guide": "Руководство по предметам",
 },
 "guideme": {"key.guideme.guide": "Руководство по предметам"},
 "alshanex_familiars": {"key.alshanex_familiars.trinket_keys": "Alshanex Familiars"},
 "bobsoriginiumdream": {"key.arkcraft.oriemergrncy": "Экстренные арты Ориджиниума"},
 "cataclysm": {
  "key.cataclysm.boots_ability": "Способность ботинок",
  "key.cataclysm.chestplate_ability": "Способность нагрудника",
  "key.cataclysm.helmet_ability": "Способность шлема",
 },
 "createbigcannons": {"key.createbigcannons.pitch_mode": "Сменить режим лафета"},
 "create_sa": {
  "key.create_sa.decrease_reach": "Уменьшить радиус дрона",
  "key.create_sa.increase_reach": "Увеличить радиус дрона",
 },
 "deep_aether": {"key.deep_aether.toggle_skyjade_transparency": "Прозрачность небесного камня"},
 "iceandfire": {
  "key.dragon_change_view": "Камера дракона (3-е лицо)",
  "key.dragon_down": "Слезть/снять малыша дракона",
 },
 "entityculling": {"key.entityculling.toggle": "Отладка Entity Culling"},
 "gtbcs_spell_lib": {
  "key.gtbcs_spell_lib.lore_item_next": "Лор: следующая страница",
  "key.gtbcs_spell_lib.lore_item_previous": "Лор: предыдущая страница",
 },
 "voicechat": {"key.voice_chat": "Меню голосового чата"},
 "irons_spellbooks": {
  "key.irons_spellbooks.group_1": "Iron's Spells: основное",
  "key.irons_spellbooks.group_2": "Iron's Spells: быстрый вызов",
  "key.irons_spellbooks.spell_bar_modifier": "Прокрутка панели заклинаний",
  "key.irons_spellbooks.spell_wheel_toggle": "Колесо заклинаний",
  "key.irons_spellbooks.spellbook_cast": "Применить заклинание",
 },
 "ironsarms": {"key.ironsarms.irons_mode": "Режим книги Iron's Spells"},
 "occultengineering": {"key.occultengineering.toggle_combined_goggles": "Потусторонние очки инженера"},
 "occultism": {
  "key.occultism.storage_remote": "Доступ к хранилищу",
  **{f"key.occultism.familiar.{k}": f"Перстень: {v}" for k, v in {
    "bat_familiar": "летучая мышь", "beaver_familiar": "бобёр", "beholder_familiar": "созерцатель",
    "blacksmith_familiar": "кузнец", "chimera_familiar": "химера", "cthulhu_familiar": "Ктулху",
    "deer_familiar": "олень", "devil_familiar": "дьявол", "dragon_familiar": "дракон", "fairy_familiar": "фея",
    "goat_familiar": "коза", "greedy_familiar": "алчный", "guardian_familiar": "страж",
    "headless_familiar": "безголовый ратман", "mummy_familiar": "мумия", "otherworld_bird": "дрикрыл",
    "shub_niggurath_familiar": "Шаб-Ниггурат"}.items()},
 },
 "origins": {"key.origins.secondary_active": "Активная сила (вторая)"},
 "refinedstorage": {
  "key.refinedstorage.openWirelessCraftingMonitor": "Монитор крафта (беспр.)",
  "key.refinedstorage.openWirelessFluidGrid": "Жидкостный терминал (беспр.)",
 },
 "sophisticatedbackpacks": {
  "key.sophisticatedbackpacks.inventory_interaction": "Запустить обмен с инвентарём",
  **{f"key.sophisticatedbackpacks.toggle_upgrade_{i}": f"Вкл/выкл улучшение в слоте {i}" for i in range(1, 6)},
 },
 "sophisticatedcore": {
  "key.sophisticatedcore.sort": "Сортировать хранилище",
  "key.sophisticatedcore.transfer_to_inventory": "Перенести в инвентарь",
  "key.sophisticatedcore.transfer_to_storage": "Перенести в хранилище",
 },
 "alexscaves": {"key.special_ability": "Способность Alex's Caves"},
 "structure_gel": {"key.structure_gel.open_building_tool_gui": "Инструмент строительства"},
 "superbwarfare": {
  "key.superbwarfare.move_left": "Поворот / крен влево",
  "key.superbwarfare.move_right": "Поворот / крен вправо",
 },
 "torchesbecomesunlight": {"key.torchesbecomesunlight.general": "Факелы становятся солнцем"},
 "quark": {"quark.keybind.narrator_full_readout": "Полный вывод диктора"},
}
n = 0
for ns, d in OVR.items():
    f = root / ns / "lang" / "ru_ru.json"
    cur = json.loads(f.read_text("utf-8")) if f.exists() else {}
    for k, v in d.items():
        if cur.get(k) != v:
            cur[k] = v; n += 1
    f.parent.mkdir(parents=True, exist_ok=True)
    f.write_text(json.dumps(cur, ensure_ascii=False, indent=2) + "\n", "utf-8")
print("изменено строк:", n)
