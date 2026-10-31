// «Клинок Возвышения» (mm:ascended_blade) — меч из «Возвышенного сердца» (поток 80; В75 «а», В85 «А» по умолчанию: 1 сердце на меч).
// Рецепты — server_scripts/mm_ascension.js; для игроков — docs/encyclopedia/own/ascension-sigil.md; тест — scripts/tests/ascended_blade.js.
//
// ЧИСЛА МЕЧА — здесь, в одном месте (меняются здесь; нужен перезапуск игры/сервера, /reload не хватит: это startup-скрипт).
global.mmAscendedBlade = {
  damage: 30,              // урон в подсказке (1 рука + меч)
  speed: 1.6,              // скорость атаки в подсказке
  durability: 4000,        // прочность
  enchantability: 22,      // зачаровываемость (как у золота)
  fireSeconds: 5,          // поджог цели при ударе, секунд
  bossBonusPercent: 0.05,  // по боссам (тег forge:bosses): + доля их максимального здоровья за удар…
  bossBonusCap: 60         // …но не больше стольких единиц урона
}

const mmBlade = global.mmAscendedBlade

ItemEvents.toolTierRegistry(event => {
  event.add('mm_ascended', tier => {
    tier.uses = mmBlade.durability
    tier.speed = 10
    tier.attackDamageBonus = 0      // весь урон — в attackDamageBaseline меча
    tier.level = 5
    tier.enchantmentValue = mmBlade.enchantability
    tier.repairIngredient = 'mm:ascended_heart'
  })
})

StartupEvents.registry('item', event => {
  // этап 1: Наковальня слияния Cataclysm — The Incinerator + Возвышенное сердце
  event.create('mm:ascended_blade_blank').displayName('Ascended Blade Blank').maxStackSize(1).rarity('rare').fireResistant(true)
    .tooltip(Text.translate('item.mm.ascended_blade_blank.tooltip').gray())
  // этап 2: Кузня огненного дракона (Ice and Fire)
  event.create('mm:ascended_blade_forged').displayName('Searing Blade').maxStackSize(1).rarity('rare').fireResistant(true)
    .tooltip(Text.translate('item.mm.ascended_blade_forged.tooltip').gray())
  // этап 3: Terra Plate (Botania) — готовый меч
  event.create('mm:ascended_blade', 'sword').displayName('Blade of Ascension')
    .tier('mm_ascended')
    .attackDamageBaseline(mmBlade.damage - 1)   // 1 (рука) + 0 (бонус тира) + baseline
    .speedBaseline(mmBlade.speed - 4)           // 4 + baseline
    .rarity('epic').glow(true).fireResistant(true)
    .tooltip(Text.translate('item.mm.ascended_blade.tooltip', String(Math.round(mmBlade.bossBonusPercent * 100)), String(mmBlade.bossBonusCap)).gray())
})

// Свойство меча. LivingHurtEvent — до брони, поэтому прибавка тоже режется бронёй цели, как обычный урон.
// У боссов Cataclysm кап урона срабатывает раньше (в их hurt), прибавка идёт поверх капа — в этом и смысл «меча для боссов».
// global.mmAscendedBladeLast — последний расчёт (для теста scripts/tests/ascended_blade.js).
// Обработчик зовётся на КАЖДЫЙ урон на сервере: сначала дешёвая проверка «в руке меч», всё — в try/catch
// (исключение в обработчике Forge-события роняет сервер: так было в первом прогоне — горящий на солнце зомби).
// Грабли Rhino (KubeJS 6): const/let внутри блока (try, if) в функции, которая зовётся много раз, → «redeclaration of var» — поэтому var;
// null в global (это Java-Map) читать нельзя (NPE) — поэтому mmAscendedBladeLast/Err всегда объекты/строки.
// Часть методов переименована для скриптов (@RemapForJS): DamageSource.getEntity → actual, getDirectEntity → immediate, Entity.hurt → attack.
global.mmAscendedBladeLast = { target: '', bonus: 0, amount: 0 }   // последний расчёт (для теста scripts/tests/ascended_blade.js)
global.mmAscendedBladeErr = ''
function mmAscendedBladeHurt(event) {
  var src = event.getSource()
  var attacker = src.actual
  if (attacker == null || src.immediate == null || !attacker.equals(src.immediate) || !attacker.isLiving()) return   // только ближний бой
  if (String(attacker.getMainHandItem().getId()) != 'mm:ascended_blade') return
  var cfg = global.mmAscendedBlade
  var target = event.getEntity()
  target.setSecondsOnFire(cfg.fireSeconds)
  var bonus = 0
  // KubeJS подменяет entity.type/getType() строкой id — тип берём из реестра Forge
  var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  var FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  var BOSSES = Java.loadClass('net.minecraftforge.common.Tags$EntityTypes').BOSSES
  var typeId = String(target.type)
  if (FR.ENTITY_TYPES.tags().getTag(BOSSES).contains(FR.ENTITY_TYPES.getValue(new RL(typeId)))) {
    bonus = Math.min(target.getMaxHealth() * cfg.bossBonusPercent, cfg.bossBonusCap)
    event.setAmount(event.getAmount() + bonus)
  }
  global.mmAscendedBladeLast = { target: typeId, bonus: bonus, amount: event.getAmount() }
}
var mmBladeErrLogged = false
ForgeEvents.onEvent('net.minecraftforge.event.entity.living.LivingHurtEvent', event => {
  try {
    mmAscendedBladeHurt(event)
  } catch (err) {
    global.mmAscendedBladeErr = String(err)
    if (!mmBladeErrLogged) { mmBladeErrLogged = true; console.error(`[mm_ascended_blade] ошибка в свойстве меча (дальше не пишем): ${err}`) }
  }
})
