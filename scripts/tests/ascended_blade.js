// Тест (поток 80): «Клинок Возвышения» (pack/kubejs/startup_scripts/mm_ascended_blade.js, рецепты — server_scripts/mm_ascension.js).
// 1) меч зарегистрирован: урон/скорость в подсказке и прочность = global.mmAscendedBlade, огнестойкий; три рецепта загружены;
// 2) фейковый игрок с мечом бьёт (attack с playerAttack): Возвышенное чудовище (6000 HP, forge:bosses) → прибавка упирается в предел;
//    обычное Незеритовое чудовище (600 HP) → ровно доля от HP (ниже предела); зомби → прибавки нет, цель горит;
// 3) контроль: тот же удар без меча — свойство не срабатывает.
// Вывод: строки BLADE … → OK|FAIL. Запуск вместе с ascension.js: EXTRA_SERVER_SCRIPTS + TEST_COMMANDS «#sleep 60».
function bladeLog(name, ok, info) { console.info(`BLADE ${name} ${info} → ${ok ? 'OK' : 'FAIL'}`) }

function bladeRun(server) {
  const cfg = global.mmAscendedBlade
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const Slot = Java.loadClass('net.minecraft.world.entity.EquipmentSlot')
  const Attributes = Java.loadClass('net.minecraft.world.entity.ai.attributes.Attributes')
  const ItemStack = Java.loadClass('net.minecraft.world.item.ItemStack')
  const EntityType = Java.loadClass('net.minecraft.world.entity.EntityType')
  const CompoundTag = Java.loadClass('net.minecraft.nbt.CompoundTag')
  const TagParser = Java.loadClass('net.minecraft.nbt.TagParser')
  const Heightmap = Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types')
  const Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const level = server.getLevel('minecraft:overworld')

  // 1. предмет и рецепты
  const blade = Item.of('mm:ascended_blade')
  let dmg = 1, spd = 4
  blade.getAttributeModifiers(Slot.MAINHAND).get(Attributes.ATTACK_DAMAGE).forEach(m => { dmg += Number(m.getAmount()) })
  blade.getAttributeModifiers(Slot.MAINHAND).get(Attributes.ATTACK_SPEED).forEach(m => { spd += Number(m.getAmount()) })
  bladeLog('item', !blade.isEmpty() && blade.getItem().isFireResistant(), `fireResistant=${blade.getItem().isFireResistant()}`)
  bladeLog('damage', Math.abs(dmg - cfg.damage) < 0.01, `damage=${dmg} ждали ${cfg.damage}`)
  bladeLog('speed', Math.abs(spd - cfg.speed) < 0.01, `speed=${spd.toFixed(2)} ждали ${cfg.speed}`)
  bladeLog('durability', blade.getMaxDamage() == cfg.durability, `durability=${blade.getMaxDamage()}`)
  const recipes = server.getRecipeManager()
  ;['mm:ascended_blade_blank', 'mm:ascended_blade_forged', 'mm:ascended_blade'].forEach(id => {
    let r = recipes.byKey(new RL(id)).orElse(null)
    bladeLog('recipe ' + id, r != null, r ? String(r.getType()) : 'нет рецепта')
  })

  // 2. цели
  const sp = level.getSharedSpawnPos()
  const spawn = (id, dx, nbt) => {
    let x = sp.getX() + dx, z = sp.getZ() + 48
    server.runCommandSilent(`forceload add ${x} ${z}`)
    level.getChunk(x >> 4, z >> 4)
    let e = EntityType.byString(id).orElse(null).create(level)
    e.setPos(x + 0.5, level.getHeight(Heightmap.MOTION_BLOCKING, x, z) + 1.0, z + 0.5)
    if (nbt) { let tag = e.saveWithoutId(new CompoundTag()); tag.merge(TagParser.parseTag(nbt)); e.load(tag) }
    level.addFreshEntity(e)
    return e
  }
  const ascended = spawn('cataclysm:netherite_monstrosity', -32, global.mmAscendedNbt)
  const plainBoss = spawn('cataclysm:netherite_monstrosity', 0, null)
  const zombie = spawn('minecraft:zombie', 32, null)

  const fake = Fake.getMinecraft(level)
  fake.setPos(sp.getX() + 0.5, 320, sp.getZ() + 0.5)
  const hit = (target, withBlade) => {
    fake.setItemSlot(Slot.MAINHAND, withBlade ? Item.of('mm:ascended_blade') : ItemStack.EMPTY)
    global.mmAscendedBladeLast = null
    target.invulnerableTime = 0
    let hp0 = target.getHealth()
    let hurt = target.attack(level.damageSources().playerAttack(fake), 10)   // KubeJS: attack(source, amount); hurt из Rhino не виден
    return { last: global.mmAscendedBladeLast, hurt: hurt, lost: hp0 - target.getHealth() }
  }
  const expectBonus = t => Math.min(t.getMaxHealth() * cfg.bossBonusPercent, cfg.bossBonusCap)

  let r = hit(ascended, true)
  bladeLog('boss_cap', r.last != null && Math.abs(r.last.bonus - cfg.bossBonusCap) < 0.01,
    `max_hp=${ascended.getMaxHealth()} bonus=${r.last ? r.last.bonus : null} ждали ${cfg.bossBonusCap} (предел) hurt=${r.hurt} hp_lost=${r.lost.toFixed(1)}`)
  r = hit(plainBoss, true)
  bladeLog('boss_percent', r.last != null && Math.abs(r.last.bonus - expectBonus(plainBoss)) < 0.01 && r.last.bonus < cfg.bossBonusCap,
    `max_hp=${plainBoss.getMaxHealth()} bonus=${r.last ? r.last.bonus : null} ждали ${expectBonus(plainBoss)} hurt=${r.hurt} hp_lost=${r.lost.toFixed(1)}`)
  zombie.clearFire()
  r = hit(zombie, true)
  bladeLog('mob_no_bonus', r.last != null && r.last.bonus == 0, `bonus=${r.last ? r.last.bonus : null} hurt=${r.hurt}`)
  bladeLog('mob_fire', zombie.getRemainingFireTicks() > 0, `fire_ticks=${zombie.getRemainingFireTicks()}`)

  // 3. контроль без меча
  zombie.clearFire()
  r = hit(plainBoss, false)
  let r2 = hit(zombie, false)
  bladeLog('no_errors', !global.mmAscendedBladeErr, 'обработчик без исключений (иначе строка [mm_ascended_blade] в логе)')
  bladeLog('no_blade', r.last == null && r2.last == null && zombie.getRemainingFireTicks() <= 0, `boss_last=${r.last} mob_last=${r2.last} fire_ticks=${zombie.getRemainingFireTicks()}`)

  fake.setItemSlot(Slot.MAINHAND, ItemStack.EMPTY)
  ;[ascended, plainBoss, zombie].forEach(e => e.discard())
}

ServerEvents.tick(event => {
  if (event.server.tickCount == 420) bladeRun(event.server)
})
