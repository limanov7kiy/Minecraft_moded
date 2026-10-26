// Тест (поток 66, В54): костюм Create Nuclear защищает от радиации Mekanism и Create Nuclear; без костюма — нет.
// Фейковый игрок Forge получает дозу Mekanism (IRadiationManager.radiate, магнитуда 1.0) и проверяется защита
// Create Nuclear (сумма атрибута createnuclear:irradiated_resistance с надетых вещей — фейк не тикает, поэтому считаем сами).
// Старый Hazmat Mekanism (может лежать в мирах) должен по-прежнему защищать от Mekanism.
// Вывод: RADSUIT <набор> mek_dose=<доза> cn_resist=<0..1> → OK|FAIL. Запуск: EXTRA_SERVER_SCRIPTS=<папка с файлом> + TEST_COMMANDS «#sleep 60».
ServerEvents.tick(event => {
  const server = event.server
  if (server.tickCount != 200) return
  const Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const Slot = Java.loadClass('net.minecraft.world.entity.EquipmentSlot')
  const ItemStack = Java.loadClass('net.minecraft.world.item.ItemStack')
  const RM = Java.loadClass('mekanism.api.radiation.IRadiationManager')
  const Caps = Java.loadClass('mekanism.common.capabilities.Capabilities')
  const CNAttr = Java.loadClass('net.nuclearteam.createnuclear.CNAttributes')
  const level = server.getLevel('minecraft:overworld')
  const fake = Fake.getMinecraft(level)
  fake.setPos(0.5, 200, 0.5)
  const slots = [Slot.HEAD, Slot.CHEST, Slot.LEGS, Slot.FEET]
  const sets = {
    none: [null, null, null, null],
    createnuclear: ['default_anti_radiation_helmet', 'default_anti_radiation_chestplate', 'default_anti_radiation_leggings', 'default_anti_radiation_boots'].map(i => 'createnuclear:' + i),
    hazmat_old: ['hazmat_mask', 'hazmat_gown', 'hazmat_pants', 'hazmat_boots'].map(i => 'mekanism:' + i)
  }
  const attr = CNAttr.IRRADIATED_RESISTANCE.get()
  Object.keys(sets).forEach(name => {
    let known = true
    slots.forEach((s, i) => {
      const id = sets[name][i]
      const st = id ? Item.of(id) : ItemStack.EMPTY
      if (id && st.isEmpty()) known = false
      fake.setItemSlot(s, st)
    })
    if (!known) { console.info(`RADSUIT ${name} SKIP (нет таких предметов)`); return }
    const cap = fake.getCapability(Caps.RADIATION_ENTITY).orElse(null)
    cap.set(0)
    RM.INSTANCE.radiate(fake, 1.0)
    const dose = cap.getRadiation()
    let resist = 0
    slots.forEach(s => fake.getItemBySlot(s).getAttributeModifiers(s).get(attr).forEach(m => { resist += m.getAmount() }))
    const mekProt = dose < 0.01, cnProt = resist >= 0.99
    const ok = mekProt == expect[name][0] && cnProt == expect[name][1]
    console.info(`RADSUIT ${name} mek_dose=${dose.toFixed(3)} cn_resist=${resist.toFixed(2)} → ${ok ? 'OK' : 'FAIL'}`)
  })
  slots.forEach(s => fake.setItemSlot(s, ItemStack.EMPTY))
})
