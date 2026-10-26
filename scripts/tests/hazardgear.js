// Тест dedup-2 (поток 66, В70): защитное снаряжение после объединения дублей. Фейковый игрок Forge:
// — костюм Create Nuclear засчитывается Alex's Caves как Hazmat (HazmatArmorItem.getWornAmount = 4; mm-compat AlexsCavesHazmatMixin);
// — противогаз Spore — рабочая маска для газа Create Big Cannons (GasMaskItem.isWearingWorkingMask; mm-compat GasMaskCompat);
// — без снаряжения — 0 / false; старые Hazmat AC и маска CBC (могут лежать в мирах) работают как раньше.
// Вывод: HAZARDGEAR <проверка> <значение> → OK|FAIL. Запуск: EXTRA_SERVER_SCRIPTS=<папка с файлом> + TEST_COMMANDS «#sleep 60».
ServerEvents.tick(event => {
  const server = event.server
  if (server.tickCount != 200) return
  const Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const Slot = Java.loadClass('net.minecraft.world.entity.EquipmentSlot')
  const ItemStack = Java.loadClass('net.minecraft.world.item.ItemStack')
  const AC = Java.loadClass('com.github.alexmodguy.alexscaves.server.item.HazmatArmorItem')
  const CBC = Java.loadClass('rbasamoyai.createbigcannons.equipment.gas_mask.GasMaskItem')
  const fake = Fake.getMinecraft(server.getLevel('minecraft:overworld'))
  const slots = [Slot.HEAD, Slot.CHEST, Slot.LEGS, Slot.FEET]
  const wear = ids => slots.forEach((s, i) => fake.setItemSlot(s, ids[i] ? Item.of(ids[i]) : ItemStack.EMPTY))
  const report = (name, val, ok) => console.info(`HAZARDGEAR ${name} ${val} → ${ok ? 'OK' : 'FAIL'}`)
  const cn = ['helmet', 'chestplate', 'leggings', 'boots'].map(p => 'createnuclear:default_anti_radiation_' + p)
  const ac = ['mask', 'chestplate', 'leggings', 'boots'].map(p => 'alexscaves:hazmat_' + p)
  wear([]); let n = AC.getWornAmount(fake); report('ac_none', n, n == 0)
  wear(cn); n = AC.getWornAmount(fake); report('ac_createnuclear_suit', n, n == 4)
  wear([cn[0], null, null, null]); n = AC.getWornAmount(fake); report('ac_createnuclear_helmet_only', n, n == 1)
  wear(ac); n = AC.getWornAmount(fake); report('ac_old_hazmat', n, n == 4)
  wear([]); let m = CBC.isWearingWorkingMask(fake); report('cbc_none', m, !m)
  wear(['spore:gas_mask']); m = CBC.isWearingWorkingMask(fake); report('cbc_spore_mask', m, m)
  wear(['createbigcannons:gas_mask']); m = CBC.isWearingWorkingMask(fake); report('cbc_old_mask', m, m)
  wear([])
})
