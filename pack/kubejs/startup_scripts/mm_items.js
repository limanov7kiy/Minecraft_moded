// Свои предметы сборки (пространство имён mm). Пилот «дорогих крафтов» (поток 80):
// docs/expensive-crafts.md, раздел 3, «Пример 2»; рецепты, ритуал и награда — server_scripts/mm_ascension.js.
// Текстуры собраны из ассетов L_Ender's Cataclysm (L_Ender, void_core / monstrous_eye) и нарисованы поверх; названия — assets/mm/lang.
StartupEvents.registry('item', event => {
  // этап 1 → 2: заготовка печати (Cataclysm weapon_fusion), заряжается в Enchanting Apparatus (Ars Nouveau)
  event.create('mm:ascension_sigil_blank').displayName('Dormant Sigil').maxStackSize(1).rarity('rare').fireResistant(true)
    .tooltip(Text.translate('item.mm.ascension_sigil_blank.tooltip').gray())
  // этап 3: ключ ритуала Occultism — вызывает «Возвышенное чудовище»
  event.create('mm:ascension_sigil').displayName('Sigil of Ascension').maxStackSize(1).rarity('epic').glow(true).fireResistant(true)
    .tooltip(Text.translate('item.mm.ascension_sigil.tooltip').gray())
  // награда: выпадает только с босса с тегом mm_ascended; ингредиент будущих меча и брони
  event.create('mm:ascended_heart').displayName('Ascended Heart').maxStackSize(16).rarity('epic').glow(true).fireResistant(true)
    .tooltip(Text.translate('item.mm.ascended_heart.tooltip').gray())
})
