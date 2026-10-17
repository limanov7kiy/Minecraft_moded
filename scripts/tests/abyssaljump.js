// Тест патча patches/abyssal-jump: усиление Прыгучести у «Охотников Бездны» (arknights_weapon) = 2 (≈3,3 блока).
// Фейковый игрок: эффект «Глубинный охотник» (arknights_weapon:abyssal_hunters) через applyEffectTick (через 120 тиков мод
// выдаёт Прыгучесть) и процедуры ботинок/поножей образца «Агор» с надетыми предметами. Вывод: ABYSSALJUMP <случай> OK|FAIL amp=<усиление>
let st = null
ServerEvents.tick(event => {
  const server = event.server
  const level = server.getLevel('minecraft:overworld')
  const UU = Java.loadClass('java.util.UUID')
  const Fk = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  const Slot = Java.loadClass('net.minecraft.world.entity.EquipmentSlot')
  const JUMP = Java.loadClass('net.minecraft.world.effect.MobEffects').JUMP
  const amp = p => { const e = p.getEffect(JUMP); return e == null ? -1 : e.getAmplifier() }
  if (server.tickCount == 200) {
    var pl1 = Fk.getMinecraft(level)
    var eff = FR.MOB_EFFECTS.getValue(new RL('arknights_weapon', 'abyssal_hunters'))
    st = { pl1: pl1, eff: eff, at: 200 + 140, cases: [] }
    // 1. эффект «Глубинный охотник» уровня 0: через 120 тиков мод вешает Прыгучесть (раньше усиление 10)
    eff.applyEffectTick(pl1, 0)
    // 2. ботинки (раньше 6): пустой игрок-двойник, чтобы эффекты не смешивались
    var f2 = Fk.get(level, new (Java.loadClass('com.mojang.authlib.GameProfile'))(UU.fromString('00000000-0000-0000-0000-0000000000a2'), 'abyssal_boots'))
    f2.setItemSlot(Slot.FEET, Item.of('arknights_weapon:abyssal_hunters_suit_boots'))
    Java.loadClass('net.mcreator.arknightsweaopn.procedures.AbyssalHuntersSuitboots1Procedure').execute(f2)
    st.cases.push({ name: 'boots', p: f2, now: amp(f2) })   // эффект ботинок живёт 20 тиков — читаем сразу
    // 3. поножи (раньше 3)
    var f3 = Fk.get(level, new (Java.loadClass('com.mojang.authlib.GameProfile'))(UU.fromString('00000000-0000-0000-0000-0000000000a3'), 'abyssal_legs'))
    f3.setItemSlot(Slot.LEGS, Item.of('arknights_weapon:abyssal_hunters_suit_leggings'))
    Java.loadClass('net.mcreator.arknightsweaopn.procedures.AbyssalHuntersSuitleggings1Procedure').execute(f3)
    st.cases.push({ name: 'leggings', p: f3, now: amp(f3) })
    st.cases.unshift({ name: 'effect', p: pl1 })
    return
  }
  if (st && server.tickCount == st.at) {
    st.cases.forEach(c => { const a = c.now !== undefined ? c.now : amp(c.p); console.info(`ABYSSALJUMP ${c.name} ${a == 2 ? 'OK' : 'FAIL'} amp=${a}`) })
    st = null
  }
})
