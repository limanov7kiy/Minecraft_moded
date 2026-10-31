// Тест (поток 80): «Печать Возвышения» → «Возвышенное чудовище» → «Возвышенное сердце» (pack/kubejs/server_scripts/mm_ascension.js).
// 1) предметы mm:* зарегистрированы, три рецепта загружены (weapon_fusion, enchanting_apparatus, occultism:ritual);
// 2) вызов как в Occultism SummonRitual: create → finalizeSpawn → saveWithoutId + merge(entity_nbt из рецепта) → load → addFreshEntity;
//    рядом — обычное Незеритовое чудовище (контроль);
// 3) через 5 с: HP = 6000 (AttributeFix), урон = 50 (×2 от 25), тег, имя; затем оба убиваются;
// 4) ещё через 3 с: сердце лежит у вызванного босса и не лежит у обычного.
// Вывод: строки ASCENSION … → OK|FAIL. Запуск: EXTRA_SERVER_SCRIPTS=<папка с файлом> + TEST_COMMANDS «#sleep 60».
let mmAsc = null
ServerEvents.tick(event => {
  const server = event.server
  const t = server.tickCount
  if (t != 200 && t != 300 && t != 360) return
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const Attributes = Java.loadClass('net.minecraft.world.entity.ai.attributes.Attributes')
  const AABB = Java.loadClass('net.minecraft.world.phys.AABB')
  const level = server.getLevel('minecraft:overworld')
  const log = (name, ok, info) => console.info(`ASCENSION ${name} ${info} → ${ok ? 'OK' : 'FAIL'}`)

  if (t == 200) {
    ['mm:ascension_sigil_blank', 'mm:ascension_sigil', 'mm:ascended_heart'].forEach(id => log('item ' + id, !Item.of(id).isEmpty(), ''))
    const rm = server.getRecipeManager()
    let ritual = null
    ;['mm:ascension_sigil_blank', 'mm:ascension_sigil', 'mm:ritual/ascended_monstrosity'].forEach(id => {
      const r = rm.byKey(new RL(id)).orElse(null)
      log('recipe ' + id, r != null, r ? String(r.getType()) : 'нет рецепта')
      if (id.startsWith('mm:ritual/')) ritual = r
    })
    if (!ritual) return
    const nbt = ritual.getEntityNbt()
    log('ritual entity_nbt', nbt != null && nbt.contains('Attributes'), String(nbt))
    const EntityType = Java.loadClass('net.minecraft.world.entity.EntityType')
    const MobSpawnType = Java.loadClass('net.minecraft.world.entity.MobSpawnType')
    const CompoundTag = Java.loadClass('net.minecraft.nbt.CompoundTag')
    const Heightmap = Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types')
    const type = EntityType.byString('cataclysm:netherite_monstrosity').orElse(null)
    const sp = level.getSharedSpawnPos()
    const spawn = (dx, merge) => {
      const x = sp.getX() + dx, z = sp.getZ()
      const y = level.getHeight(Heightmap.MOTION_BLOCKING, x, z) + 1
      const e = type.create(level)
      e.moveTo(x + 0.5, y, z + 0.5, 0, 0)
      e.finalizeSpawn(level, level.getCurrentDifficultyAt(e.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null)
      if (merge) { const tag = e.saveWithoutId(new CompoundTag()); tag.merge(nbt); e.load(tag) }
      level.addFreshEntity(e)
      return e
    }
    mmAsc = { boss: spawn(-24, true), plain: spawn(24, false) }
    log('spawn', mmAsc.boss.isAlive() && mmAsc.plain.isAlive(), `boss=${mmAsc.boss.blockPosition()} plain=${mmAsc.plain.blockPosition()}`)
    return
  }
  if (!mmAsc) return
  const b = mmAsc.boss, p = mmAsc.plain
  if (t == 300) {
    const mh = b.getAttribute(Attributes.MAX_HEALTH), ad = b.getAttribute(Attributes.ATTACK_DAMAGE)
    log('boss max_health', Math.abs(b.getMaxHealth() - 6000) < 1, `max=${b.getMaxHealth()} base=${mh.getBaseValue()} hp=${b.getHealth()}`)
    log('boss health_full', b.getHealth() > 5900, `hp=${b.getHealth()}`)
    log('boss attack_damage', Math.abs(ad.getBaseValue() - 50) < 0.01, `base=${ad.getBaseValue()} value=${ad.getValue()}`)
    log('boss tag+name', b.getTags().contains('mm_ascended') && b.hasCustomName(), `tags=${b.getTags()} name=${b.getCustomName() ? b.getCustomName().getString() : null} persistent=${b.isPersistenceRequired()}`)
    log('plain max_health', p.getMaxHealth() < 1024 && !p.getTags().contains('mm_ascended'), `max=${p.getMaxHealth()}`)
    mmAsc.pos = { b: b.position(), p: p.position() }
    ;[b, p].forEach(e => {
      e.kill()   // hurt(genericKill, MAX); если урон срезан капом Cataclysm — добиваем die()
      if (e.isAlive() && e.getHealth() > 0) { e.die(level.damageSources().genericKill()); e.setHealth(0) }
    })
    log('killed', !b.isAlive() || b.getHealth() <= 0, `boss_hp=${b.getHealth()} plain_hp=${p.getHealth()}`)
    return
  }
  // t == 360
  const hearts = v => {
    let n = 0
    level.getEntitiesOfClass(Java.loadClass('net.minecraft.world.entity.item.ItemEntity'), new AABB(v.x() - 12, v.y() - 12, v.z() - 12, v.x() + 12, v.y() + 12, v.z() + 12))
      .forEach(ie => { if (ie.getItem().getId() == 'mm:ascended_heart') n += ie.getItem().getCount() })
    return n
  }
  const hb = hearts(mmAsc.pos.b), hp = hearts(mmAsc.pos.p)
  log('heart from ascended', hb == 1, `hearts=${hb}`)
  log('no heart from plain', hp == 0, `hearts=${hp}`)
})
