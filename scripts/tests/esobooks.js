// Проверка книг модовых чар для ESO (поток 57): на 100-м тике катает каждую таблицу из
// pack/kubejs/server_scripts/eso_enchant_books.js (global.mmEsoBookPools) по 1000 раз и считает книги с чарами
// из её списков (ESOBOOKS ...); затем фейковый игрок «читает» книгу Alex's Caves (правый клик, как в игре) —
// ESO должен записать чару в изученные (ESOLEARN ...). Плюс есть ли команда /eso. Итог — ESOBOOKS_DONE.
ServerEvents.tick(event => {
  var server = event.server
  if (server.tickCount != 100) return
  var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  var LootParams = Java.loadClass('net.minecraft.world.level.storage.loot.LootParams$Builder')
  var LCP = Java.loadClass('net.minecraft.world.level.storage.loot.parameters.LootContextParams')
  var Sets = Java.loadClass('net.minecraft.world.level.storage.loot.parameters.LootContextParamSets')
  var Vec3 = Java.loadClass('net.minecraft.world.phys.Vec3')
  var EH = Java.loadClass('net.minecraft.world.item.enchantment.EnchantmentHelper')
  var FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  var level = server.getLevel('minecraft:overworld')
  var bad = 0
  global.mmEsoBookPools.forEach(p => {
    var table = server.getLootData().getLootTable(RL.tryParse(p.table))
    var want = {}; p.normal.concat(p.rare).forEach(e => want[e] = 0)
    var books = 0, other = 0
    for (var i = 0; i < 1000; i++) {
      var params = new LootParams(level).withParameter(LCP.ORIGIN, new Vec3(0, 64, 0)).create(Sets.CHEST)
      table.getRandomItems(params).forEach(st => {
        if (String(st.getItem().builtInRegistryHolder().key().location()) != 'minecraft:enchanted_book') return
        EH.getEnchantments(st).keySet().forEach(en => {
          var id = String(FR.ENCHANTMENTS.getKey(en))
          if (id in want) { want[id]++; books++ } else other++
        })
      })
    }
    var missing = Object.keys(want).filter(k => want[k] == 0)
    if (books == 0) bad++
    console.info('ESOBOOKS ' + p.table + ' books=' + books + ' other=' + other + ' unseen=' + missing.length + (missing.length ? ' ' + missing.join(',') : ''))
  })
  // изучение: правый клик книгой, как игрок
  try {
    var FakePlayerFactory = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
    var GameProfile = Java.loadClass('com.mojang.authlib.GameProfile')
    var UUID = Java.loadClass('java.util.UUID')
    var Hand = Java.loadClass('net.minecraft.world.InteractionHand')
    var Items = Java.loadClass('net.minecraft.world.item.Items')
    var EBI = Java.loadClass('net.minecraft.world.item.EnchantedBookItem')
    var EI = Java.loadClass('net.minecraft.world.item.enchantment.EnchantmentInstance')
    var fp = FakePlayerFactory.get(level, new GameProfile(UUID.fromString('5e1d0b7e-0000-4000-8000-00000000e50b'), 'esobooks'))
    var en = FR.ENCHANTMENTS.getValue(RL.tryParse('alexscaves:gamma_ray'))
    var book = EBI.createForEnchantment(new EI(en, 1))
    fp.setItemInHand(Hand.MAIN_HAND, book)
    var res = book.getItem().use(level, fp, Hand.MAIN_HAND)
    var learned = fp['enchantment_overhaul$getUnlockedEnchantments']()
    console.info('ESOLEARN result=' + res.getResult() + ' learned=' + learned.getInt(en) + ' hand=' + fp.getMainHandItem())
    if (learned.getInt(en) < 1) bad++
  } catch (x) { console.info('ESOLEARN_ERR ' + x); bad++ }
  var root = server.getCommands().getDispatcher().getRoot()
  console.info('ESOCMD eso=' + (root.getChild('eso') != null))
  console.info('ESOBOOKS_DONE bad=' + bad)
})
