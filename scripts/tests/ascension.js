// Тест (поток 80): «Печать Возвышения» → «Возвышенное чудовище» → «Возвышенное сердце» (pack/kubejs/server_scripts/mm_ascension.js).
// 1) предметы mm:* зарегистрированы, три рецепта загружены (weapon_fusion, enchanting_apparatus, occultism:ritual);
// 2) вызов как в Occultism SummonRitual: create → finalizeSpawn → saveWithoutId + merge(entity_nbt из рецепта) → load → addFreshEntity;
//    рядом — обычное Незеритовое чудовище (контроль);
// 3) через 5 с: HP = 6000 (AttributeFix), урон = 50 (×2 от 25), тег, имя; затем оба убиваются;
// 4) ещё через 3 с: после убийства в мир вошло ровно одно сердце — рядом с вызванным боссом, и оно цело; у обычного — ни одного.
//    Проверка по самой сущности (EntityEvents.spawned), а не поиском по области: предмет разлетается и падает, босс к смерти мог сдвинуться
//    (у Ревью поиск AABB ±12 дал hearts_near=0 при hearts_spawned=1).
// Вывод: строки ASCENSION … → OK|FAIL. Запуск: EXTRA_SERVER_SCRIPTS=<папка с файлом> + TEST_COMMANDS «#sleep 60».
let mmAsc = null
let mmAscHearts = []   // сердца, вошедшие в мир после убийства (EntityJoinLevel): [{e, pos}]; старые из сохранённого мира не считаются
EntityEvents.spawned(event => {
  let e = event.entity
  if (mmAsc && mmAsc.killed && String(e.type) == 'minecraft:item' && String(e.getItem().getId()) == 'mm:ascended_heart') mmAscHearts.push({ e: e, pos: e.position() })
})
function ascLog(name, ok, info) { console.info(`ASCENSION ${name} ${info} → ${ok ? 'OK' : 'FAIL'}`) }

function ascStart(server) {
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const EntityType = Java.loadClass('net.minecraft.world.entity.EntityType')
  const MobSpawnType = Java.loadClass('net.minecraft.world.entity.MobSpawnType')
  const CompoundTag = Java.loadClass('net.minecraft.nbt.CompoundTag')
  const Heightmap = Java.loadClass('net.minecraft.world.level.levelgen.Heightmap$Types')
  const level = server.getLevel('minecraft:overworld')
  const recipes = server.getRecipeManager()
  let ritual = null
  ;['mm:ascension_sigil_blank', 'mm:ascension_sigil', 'mm:ascended_heart'].forEach(id => ascLog('item ' + id, !Item.of(id).isEmpty(), ''))
  ;['mm:ascension_sigil_blank', 'mm:ascension_sigil', 'mm:ritual/ascended_monstrosity'].forEach(id => {
    let r = recipes.byKey(new RL(id)).orElse(null)
    ascLog('recipe ' + id, r != null, r ? String(r.getType()) : 'нет рецепта')
    if (id.startsWith('mm:ritual/')) ritual = r
  })
  if (!ritual) return
  let nbt = ritual.getEntityNbt()
  ascLog('ritual entity_nbt', nbt != null && nbt.contains('Attributes'), String(nbt))
  let type = EntityType.byString('cataclysm:netherite_monstrosity').orElse(null)
  let sp = level.getSharedSpawnPos()
  let spawn = (dx, merge) => {
    let x = sp.getX() + dx, z = sp.getZ()
    server.runCommandSilent(`forceload add ${x} ${z}`)
    level.getChunk(x >> 4, z >> 4)   // полностью сгенерировать чанк, иначе карта высот пуста (y = дно мира)
    let y = level.getHeight(Heightmap.MOTION_BLOCKING, x, z) + 1
    let e = type.create(level)
    e.setPos(x + 0.5, y + 0.0, z + 0.5)
    e.finalizeSpawn(level, level.getCurrentDifficultyAt(e.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null)
    if (merge) { let tag = e.saveWithoutId(new CompoundTag()); tag.merge(nbt); e.load(tag) }
    level.addFreshEntity(e)
    return e
  }
  mmAsc = { boss: spawn(-24, true), plain: spawn(24, false) }
  ascLog('spawn', mmAsc.boss.isAlive() && mmAsc.plain.isAlive(), `boss=${mmAsc.boss.blockPosition()} plain=${mmAsc.plain.blockPosition()}`)
}

function ascCheckAndKill(server) {
  const Attributes = Java.loadClass('net.minecraft.world.entity.ai.attributes.Attributes')
  const level = server.getLevel('minecraft:overworld')
  let b = mmAsc.boss, p = mmAsc.plain
  let mh = b.getAttribute(Attributes.MAX_HEALTH), ad = b.getAttribute(Attributes.ATTACK_DAMAGE)
  ascLog('boss max_health', Math.abs(b.getMaxHealth() - 6000) < 1, `max=${b.getMaxHealth()} base=${mh.getBaseValue()} hp=${b.getHealth()}`)
  ascLog('boss health_full', b.getHealth() > 5900, `hp=${b.getHealth()}`)
  ascLog('boss attack_damage', Math.abs(ad.getBaseValue() - 50) < 0.01, `base=${ad.getBaseValue()} value=${ad.getValue()}`)
  ascLog('boss tag+name', b.getTags().contains('mm_ascended') && b.hasCustomName(), `tags=${b.getTags()} name=${b.getCustomName() ? b.getCustomName().getString() : null} persistent=${b.isPersistenceRequired()}`)
  ascLog('plain max_health', p.getMaxHealth() < 1024 && !p.getTags().contains('mm_ascended'), `max=${p.getMaxHealth()} attack=${p.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue()}`)
  mmAsc.pos = { b: b.position(), p: p.position() }
  mmAsc.killed = true   // считать только сердца, появившиеся после убийства (в старом мире могут лежать сердца прошлых прогонов)
  ;[b, p].forEach(e => {
    e.kill()   // hurt(genericKill, MAX); если урон срезан капом Cataclysm — добиваем die()
    if (e.isAlive() && e.getHealth() > 0) { e.die(level.damageSources().genericKill()); e.setHealth(0) }
  })
  ascLog('killed', !b.isAlive() || b.getHealth() <= 0, `boss_hp=${b.getHealth()} plain_hp=${p.getHealth()}`)
}

// сердца, появившиеся не дальше 16 блоков по горизонтали от точки (позиция босса в момент убийства)
function ascHeartsNear(v) {
  return mmAscHearts.filter(h => Math.hypot(h.pos.x() - v.x(), h.pos.z() - v.z()) <= 16)
}

ServerEvents.tick(event => {
  let t = event.server.tickCount
  if (t == 200) ascStart(event.server)
  else if (t == 300 && mmAsc) ascCheckAndKill(event.server)
  else if (t == 360 && mmAsc && mmAsc.pos) {
    let hb = ascHeartsNear(mmAsc.pos.b), hp = ascHeartsNear(mmAsc.pos.p)
    let info = mmAscHearts.map(h => `spawn=${h.pos} now=${h.e.position()} alive=${h.e.isAlive()} removed=${h.e.getRemovalReason()}`).join('; ')
    ascLog('heart from ascended', mmAscHearts.length == 1 && hb.length == 1 && hb[0].e.isAlive(), `hearts_spawned=${mmAscHearts.length} near_boss=${hb.length} [${info}]`)
    ascLog('no heart from plain', hp.length == 0, `near_plain=${hp.length}`)
  }
})
