// Состояния мода лежат в Forge-NBT существа (ForgeData), а не в persistentData KubeJS — читаем полное NBT.
// Тест: заклинания Wizardry Restored действительно срабатывают. Кастуем от имени фейкового игрока Forge
// на площадке в небе и проверяем результат в мире. Вывод: CASTCHECK <заклинание> OK|FAIL <подробности>
ServerEvents.tick(event => {
  const server = event.server
  if (server.tickCount != 200) return
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const RM = Java.loadClass('net.minecraftforge.registries.RegistryManager')
  const Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const Ctx = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.PlayerCastContext')
  const Mods = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.SpellModifiers')
  const Hand = Java.loadClass('net.minecraft.world.InteractionHand')
  const BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  const reg = RM.ACTIVE.getRegistry(new RL('ebwizardry', 'spells'))
  const level = server.getLevel('minecraft:overworld')
  server.runCommandSilent('forceload add -16 -16 16 16')
  server.runCommandSilent('fill -12 199 -12 12 199 12 minecraft:stone')
  server.runCommandSilent('fill -12 200 -12 12 210 12 minecraft:air')
  const fake = Fake.getMinecraft(level)
  // Углы задаём как свойства (Rhino вызывает setYRot/setXRot/setYHeadRot)
  const place = (pitch) => { fake.setPos(0.5, 200, 0.5); fake.yRot = 0; fake.xRot = pitch; fake.yHeadRot = 0; fake.setOnGround(true) }   // yaw 0 = смотрим на юг (+Z)
  const cast = (id) => reg.getValue(new RL('wizardry_restored', id)).cast(new Ctx(level, fake, Hand.MAIN_HAND, 0, new Mods()))
  const report = (id, ok, info) => console.info(`CASTCHECK ${id} ${ok ? 'OK' : 'FAIL'} ${info}`)
  const count = (block) => { let n = 0; for (let x = -6; x <= 6; x++) for (let y = 200; y < 206; y++) for (let z = -6; z <= 6; z++)
    if (String(level.getBlockState(new BlockPos(x, y, z)).getBlock().getDescriptionId()) == 'block.minecraft.' + block) n++; return n }
  const near = (type, r, out) => (out = []) && level.getEntitiesOfClass(Java.loadClass('net.minecraft.world.entity.Entity'), fake.getBoundingBox().inflate(r)).stream()
    .filter(e => String(e.getType()) == (type.includes(':') ? type : 'minecraft:' + type)).forEach(e => out.push(e)) || out
  const spawn = (type, x, z) => { server.runCommandSilent(`summon ${type} ${x} 200 ${z} {NoAI:1b,PersistenceRequired:1b}`); return near(type, 12)[near(type, 12).length - 1] }

  place(0); let r = cast('frost_barrier'); report('frost_barrier', r && count('packed_ice') == 15, `cast=${r} ice=${count('packed_ice')}`)
  server.runCommandSilent('fill -12 200 -12 12 210 12 minecraft:air')
  place(0); r = cast('light'); report('light', r && count('light') == 1, `cast=${r} light=${count('light')}`)
  place(0); r = cast('summon_spirit_horse'); let horses = near('horse', 6)
  report('summon_spirit_horse', r && horses.length == 1 && String(horses[0].nbt).includes('spirit_horse:1b'), `cast=${r} horses=${horses.length}`)
  horses.forEach(h => h.discard())
  place(0); r = cast('summon_spirit_wolf'); report('summon_spirit_wolf', r && near('wolf', 6).length >= 1, `cast=${r} wolves=${near('wolf', 6).length}`)
  near('wolf', 8).forEach(w => w.discard())

  let cow = spawn('cow', 0.5, 3.5); place(20); r = cast('metamorphosis')
  report('metamorphosis', r && near('mooshroom', 8).length == 1 && near('cow', 8).length == 0, `cast=${r} mooshroom=${near('mooshroom', 8).length} cow=${near('cow', 8).length}`)
  near('mooshroom', 8).forEach(e => e.discard())

  let z = spawn('zombie', 0.5, 3.5); place(10); r = cast('mind_control')
  report('mind_control', r && String(z.nbt).includes('mind_control_until'), `cast=${r}`)
  place(0); r = cast('slow_time'); report('slow_time', r && z.hasEffect(Java.loadClass('net.minecraft.world.effect.MobEffects').MOVEMENT_SLOWDOWN), `cast=${r}`)
  z.setOnGround(true); let hp = z.getHealth(); place(0); r = cast('earthquake'); report('earthquake', r && z.getHealth() < hp && z.getDeltaMovement().y() > 0, `cast=${r} hp=${hp}->${z.getHealth()} vy=${z.getDeltaMovement().y()}`)
  z.discard()

  place(0); r = cast('ice_shroud'); report('ice_shroud', r && String(fake.nbt).includes('ice_shroud_until'), `cast=${r}`)
  place(0); r = cast('shadow_ward'); report('shadow_ward', r && String(fake.nbt).includes('shadow_ward_until'), `cast=${r}`)
  place(0); r = cast('transience'); report('transience', r && fake.hasEffect(Java.loadClass('net.minecraft.world.effect.MobEffects').INVISIBILITY), `cast=${r}`)

  let z2 = spawn('zombie', 0.5, 6.5); hp = z2.getHealth(); place(25); r = cast('celestial_smite')
  report('celestial_smite', r && (z2.getHealth() < hp || !z2.isAlive()), `cast=${r} hp=${hp}->${z2.getHealth()}`)
  z2.discard()
  let z3 = spawn('zombie', 3.5, 3.5); place(0); r = cast('thunderstorm')
  report('thunderstorm', r && near('lightning_bolt', 14).length >= 1, `cast=${r} bolts=${near('lightning_bolt', 14).length}`)
  z3.discard()

  // ---- прошлые партии ----
  const Eff = Java.loadClass('net.minecraft.world.effect.MobEffects')
  const effId = (e, id) => { let ok = false; e.getActiveEffects().forEach(i => { if (String(i.getEffect().getDescriptionId()).includes(id)) ok = true }); return ok }
  const clear = () => { place(0); server.runCommandSilent('fill -12 200 -12 12 210 12 minecraft:air'); server.runCommandSilent('kill @e[type=!minecraft:player,distance=..0]') ;
    level.getEntitiesOfClass(Java.loadClass('net.minecraft.world.entity.Entity'), fake.getBoundingBox().inflate(30)).forEach(e => { if (e != fake) e.discard() }) }
  const mob = (x, z) => { let m = spawn('zombie', x, z); m.setOnGround(true); return m }

  clear(); server.runCommandSilent('setblock 0 201 3 minecraft:stone'); place(0); r = cast('mine')
  report('mine', r && count('stone') == 0, `cast=${r} stone_left=${count('stone')}`)
  clear(); place(0); r = cast('blink'); report('blink', r && fake.getZ() > 5, `cast=${r} z=${fake.getZ()}`)
  clear(); let a = mob(2.5, 0.5); place(0); r = cast('shockwave'); report('shockwave', r && a.getHealth() < 20, `cast=${r} hp=${a.getHealth()}`)
  clear(); a = mob(0.5, 3.5); let b = mob(2.5, 3.5); place(10); r = cast('chain_lightning')
  report('chain_lightning', r && a.getHealth() < 20 && b.getHealth() < 20, `cast=${r} hp=${a.getHealth()},${b.getHealth()}`)
  clear(); a = mob(0.5, 3.5); place(10); r = cast('paralysis'); report('paralysis', r && effId(a, 'paralysis'), `cast=${r}`)
  clear(); a = mob(0.5, 3.5); place(10); r = cast('mind_trick'); report('mind_trick', r && a.hasEffect(Eff.WEAKNESS), `cast=${r}`)
  clear(); a = mob(0.5, 3.5); place(10); r = cast('disintegration'); report('disintegration', r && a.getHealth() < 20 && a.isOnFire(), `cast=${r} hp=${a.getHealth()} fire=${a.isOnFire()}`)
  clear(); a = mob(0.5, 3.5); place(10); r = cast('lightning_ray'); report('lightning_ray', r && a.getHealth() < 20, `cast=${r} hp=${a.getHealth()}`)
  clear(); a = mob(3.5, 0.5); place(0); r = cast('intimidate'); report('intimidate', r && a.hasEffect(Eff.WEAKNESS), `cast=${r}`)
  clear(); a = mob(3.5, 0.5); place(0); r = cast('sixth_sense'); report('sixth_sense', r && a.hasEffect(Eff.GLOWING), `cast=${r}`)
  clear(); place(0); r = cast('snowball'); report('snowball', r && near('snowball', 6).length == 1, `cast=${r} n=${near('snowball', 6).length}`)
  clear(); place(0); r = cast('greater_fireball'); report('greater_fireball', r && near('fireball', 6).length == 1, `cast=${r} n=${near('fireball', 6).length}`)
  clear(); place(30); r = cast('lightning_bolt'); report('lightning_bolt', r && near('lightning_bolt', 10).length == 1, `cast=${r} n=${near('lightning_bolt', 10).length}`)
  clear(); place(30); r = cast('wall_of_frost'); report('wall_of_frost', r && count('ice') == 1, `cast=${r} ice=${count('ice')}`)
  clear(); place(0); r = cast('summon_iron_golem'); report('summon_iron_golem', r && near('iron_golem', 6).length == 1, `cast=${r}`)
  clear(); fake.removeAllEffects(); place(0); r = cast('ironflesh'); report('ironflesh', r && fake.hasEffect(Eff.DAMAGE_RESISTANCE), `cast=${r}`)
  fake.removeAllEffects(); place(0); r = cast('diamondflesh'); report('diamondflesh', r && fake.getEffect(Eff.DAMAGE_RESISTANCE).getAmplifier() == 2, `cast=${r}`)
  fake.removeAllEffects(); place(0); fake.setPos(0.5, 205, 0.5); fake.setOnGround(false); fake.setDeltaMovement(new (Java.loadClass('net.minecraft.world.phys.Vec3'))(0, -1, 0))
  r = cast('glide'); report('glide', r && fake.getDeltaMovement().y() >= -0.11, `cast=${r} vy=${fake.getDeltaMovement().y()}`)

  // ---- партия 4 (0.4.0) ----
  const Slot = Java.loadClass('net.minecraft.world.entity.EquipmentSlot')
  clear(); place(0); r = cast('freezing_weapon'); report('freezing_weapon', r && String(fake.nbt).includes('freezing_weapon_until'), `cast=${r}`)
  clear(); place(0); r = cast('empowering_presence'); report('empowering_presence', r && String(fake.nbt).includes('empower_until'), `cast=${r}`)
  clear(); place(0); r = cast('muffle'); report('muffle', r && String(fake.nbt).includes('muffle_until'), `cast=${r}`)
  clear(); a = mob(2.5, 0.5); place(0); r = cast('lightning_pulse'); report('lightning_pulse', r && a.getHealth() < 20, `cast=${r} hp=${a.getHealth()}`)
  clear(); place(0); r = cast('summon_ice_giant'); report('summon_ice_giant', r && near('ebwizardry:ice_giant', 8).length == 1, `cast=${r} n=${near('ebwizardry:ice_giant', 8).length}`)
  clear(); place(0); [Slot.HEAD, Slot.CHEST, Slot.LEGS, Slot.FEET].forEach(sl => fake.setItemSlot(sl, Item.of('minecraft:air')))
  r = cast('conjure_armor'); report('conjure_armor', r && !fake.getItemBySlot(Slot.CHEST).isEmpty(), `cast=${r} chest=${fake.getItemBySlot(Slot.CHEST)}`)
  clear(); place(0); r = cast('silverfish_swarm'); report('silverfish_swarm', r && near('silverfish', 8).length >= 10, `cast=${r} n=${near('silverfish', 8).length}`)
  // ---- партия 5 (0.5.0): сущности этапа 3. Тест идёт в одном тике, поэтому tick() конструкций вызываем сами;
  // фейковый игрок не в мире — конструкция его не находит и всех считает врагами (это и проверяем) ----
  const one = (type) => near(type, 16)[0]
  const tickAt = (e, t) => e.tickAt(t)
  clear(); a = mob(2.5, 0.5); place(0); r = cast('forcefield'); let ff = one('wizardry_restored:forcefield')
  server.runCommandSilent('summon minecraft:arrow 0.5 201 2.0 {Motion:[0.0,0.0,-0.5]}'); let arrow = near('arrow', 6)
  if (ff) tickAt(ff, 1)
  report('forcefield', r && ff && a.getDeltaMovement().x() > 0 && arrow.length == 1 && !arrow[0].isAlive(), `cast=${r} ff=${!!ff} vx=${a.getDeltaMovement().x()} arrow=${arrow.length && !arrow[0].isAlive()}`)
  clear(); a = mob(0.5, 6.5); place(30); r = cast('radiant_totem'); let tt = one('wizardry_restored:radiant_totem')
  if (tt) tickAt(tt, 5)
  report('radiant_totem', r && tt && a.getHealth() < 20, `cast=${r} totem=${!!tt} hp=${a.getHealth()}`)
  clear(); a = mob(0.5, 6.5); place(30); r = cast('withering_totem'); tt = one('wizardry_restored:withering_totem')
  if (tt) tickAt(tt, 20)
  report('withering_totem', r && tt && a.hasEffect(Eff.WITHER), `cast=${r} totem=${!!tt} hp=${a.getHealth()}`)
  clear(); a = mob(0.5, 7.5); place(0); r = cast('black_hole'); let bh = one('wizardry_restored:black_hole')
  if (bh) tickAt(bh, 2)
  report('black_hole', r && bh && a.getDeltaMovement().z() > 0, `cast=${r} hole=${bh ? bh.position() : null} vz=${a.getDeltaMovement().z()}`)
  clear(); a = mob(0.5, 3.5); place(0); r = cast('stormcloud'); let sc = one('wizardry_restored:stormcloud')
  if (sc) tickAt(sc, 10)
  report('stormcloud', r && sc && a.getHealth() < 20 && sc.getZ() > 3.5, `cast=${r} cloud=${sc ? sc.position() : null} hp=${a.getHealth()}`)
  clear(); fake.removeAllEffects(); a = mob(0.5, 3.5); a.setTarget(fake); place(0); r = cast('decoy')
  let tgt = a.getTarget()
  report('decoy', r && tgt && String(tgt.getType()) == 'wizardry_restored:decoy' && fake.hasEffect(Eff.INVISIBILITY), `cast=${r} target=${tgt ? tgt.getType() : null}`)
  for (const [id, type] of [['summon_ice_wraith', 'ebwizardry:ice_wraith'], ['summon_shadow_wraith', 'ebwizardry:shadow_wraith'], ['summon_storm_elemental', 'ebwizardry:storm_elemental']]) {
    clear(); place(0); r = cast(id); report(id, r && near(type, 8).length == 1, `cast=${r} n=${near(type, 8).length}`)
  }
  // ---- партия 6 (0.5.0) ----
  const States = Java.loadClass('dev.mcmoded.wizardryrestored.util.SpellStates')
  const AttackEvt = Java.loadClass('net.minecraftforge.event.entity.living.LivingAttackEvent')
  clear(); fake.removeAllEffects(); place(0); r = cast('shield')
  server.runCommandSilent('summon minecraft:arrow 0.5 201.5 3.5 {NoGravity:1b}'); let arr = near('arrow', 6)[0]
  let evt = new AttackEvt(fake, level.damageSources().arrow(arr, null), 5); States.onAttack(evt)
  report('shield', r && String(fake.nbt).includes('shield_until') && evt.isCanceled(), `cast=${r} blocked=${evt.isCanceled()}`)
  clear(); a = mob(0.5, 3.5); b = mob(2.5, 4.5); place(10); r = cast('lightning_web')
  report('lightning_web', r && a.getHealth() < 20 && b.getHealth() < 20, `cast=${r} hp=${a.getHealth()},${b.getHealth()}`)
  clear(); fake.setItemInHand(Hand.MAIN_HAND, Item.of('minecraft:diamond_sword')); place(0); r = cast('imbue_weapon')
  report('imbue_weapon', r && String(fake.getMainHandItem().getEnchantmentTags()).includes('sharpness'), `cast=${r} ench=${fake.getMainHandItem().getEnchantmentTags()}`)
  fake.setItemInHand(Hand.MAIN_HAND, Item.of('minecraft:air'))
  clear(); a = mob(0.5, 3.5); place(10); r = cast('mark_sacrifice'); let marked = effId(a, 'mark_sacrifice'); cast('lightning_ray')
  // луч бьёт на 3: без метки зомби (броня 2) остаётся с 17.5 (формула брони Apothic Attributes), с меткой ×1.6 — 16
  report('mark_sacrifice', r && marked && a.getHealth() < 17 && !effId(a, 'mark_sacrifice'), `cast=${r} marked=${marked} hp=${a.getHealth()}`)
  clear(); fake.removeAllEffects(); place(0); r = cast('mirage'); let img = near('wizardry_restored:decoy', 8)
  report('mirage', r && img.length == 1 && String(img[0].nbt).includes('mirage:1b') && fake.hasEffect(Eff.INVISIBILITY), `cast=${r} n=${img.length}`)
  clear(); place(30); r = cast('snare'); let sn = one('wizardry_restored:snare')
  if (sn) { a = mob(sn.getX(), sn.getZ()); tickAt(sn, 2) }
  report('snare', r && sn && a.getHealth() < 20 && a.hasEffect(Eff.MOVEMENT_SLOWDOWN) && !sn.isAlive(), `cast=${r} snare=${!!sn} hp=${a.getHealth()}`)
  clear(); place(0); r = cast('forest_of_thorns'); let th = near('wizardry_restored:thorns', 8)
  if (th.length) { a = mob(th[0].getX(), th[0].getZ()); tickAt(th[0], 10) }
  report('forest_of_thorns', r && th.length >= 8 && a.getHealth() < 20, `cast=${r} n=${th.length} hp=${a.getHealth()}`)
  clear(); a = mob(0.5, 6.5); place(5); r = cast('lightning_disc'); let disc = one('wizardry_restored:lightning_disc')
  for (let i = 1; disc && i <= 12 && disc.isAlive(); i++) tickAt(disc, i)
  report('lightning_disc', r && disc && a.getHealth() < 20, `cast=${r} disc=${!!disc} hp=${a.getHealth()}`)
  clear(); a = mob(0.5, 4.5); place(30); r = cast('lightning_hammer'); let hm = near('wizardry_restored:lightning_hammer', 20)[0]
  for (let i = 1; hm && i <= 60 && !hm.hasLanded(); i++) tickAt(hm, i)
  report('lightning_hammer', r && hm && hm.hasLanded() && a.getHealth() < 20, `cast=${r} landed=${hm && hm.hasLanded()} y=${hm ? hm.getY() : null} hp=${a.getHealth()}`)
  clear(); place(30); fake.setShiftKeyDown(true); let r1 = cast('clairvoyance'); fake.setShiftKeyDown(false)
  fake.setPos(-6.5, 200, -6.5); r = cast('clairvoyance'); let len = Java.loadClass('dev.mcmoded.wizardryrestored.spell.Clairvoyance').trailLength(fake)
  report('clairvoyance', r1 && r && len > 3, `mark=${r1} go=${r} path=${len}`)
  clear(); server.runCommandSilent('setblock 3 201 3 minecraft:diamond_ore'); place(0); r = cast('divination')
  report('divination', r && String(fake.nbt).includes('divination_found'), `cast=${r}`)
  // ---- партия 7 (0.5.0) ----
  clear(); place(0); r = cast('summon_phoenix'); let ph = near('wizardry_restored:phoenix', 10)
  server.runCommandSilent('damage @e[type=wizardry_restored:phoenix,limit=1] 1000 minecraft:magic')
  report('summon_phoenix', r && ph.length == 1 && ph[0].isAlive() && ph[0].isReborn(), `cast=${r} n=${ph.length} reborn=${ph.length && ph[0].isReborn()}`)
  clear(); place(0); r = cast('resurrection'); report('resurrection', r == false, `cast=${r} (погибших нет — заклинание должно отказать)`)
  clear(); fake.removeAllEffects(); a = mob(0.5, 3.5); place(10); r = cast('possession'); let poss = String(a.nbt).includes('possessed_by') && a.isNoAi()
  fake.setShiftKeyDown(true); r1 = cast('possession'); fake.setShiftKeyDown(false)
  // тестовый зомби изначально NoAI — после выхода он должен вернуться в исходное состояние (NoAI, уязвим)
  report('possession', r && poss && r1 && a.isNoAi() && !a.isInvulnerable() && !String(a.nbt).includes('possessed_by'), `cast=${r} possessed=${poss} release=${r1} inv=${a.isInvulnerable()}`)
  // ---- этап 4 (0.6.0): статуи, лоза, камни перемещения, артефакты ----
  const statues = () => near('wizardry_restored:statue', 10)
  clear(); a = mob(0.5, 3.5); place(10); r = cast('petrify'); let st = statues()
  let gone = near('zombie', 10).length == 0; if (st.length) st[0].tickAt(100000)
  report('petrify', r && st.length == 1 && gone && !st[0].isAlive() && near('zombie', 10).length == 1, `cast=${r} statue=${st.length} back=${near('zombie', 10).length}`)
  clear(); a = mob(0.5, 3.5); place(10); r = cast('ice_statue'); st = statues()
  let isIce = st.length == 1 && st[0].isIce(); server.runCommandSilent('damage @e[type=wizardry_restored:statue,limit=1] 100 minecraft:magic')
  let alive = near('zombie', 10).filter(e => e.isAlive()).length
  report('ice_statue', r && isIce && statues().length == 0 && alive == 0, `cast=${r} ice=${isIce} left=${statues().length} alive=${alive}`)
  clear(); a = mob(0.5, 6.5); place(10); r = cast('grapple'); let hk = one('wizardry_restored:grapple_hook')
  for (let i = 1; hk && i <= 20 && hk.isAlive(); i++) { cast('grapple'); hk.tickAt(i); if (hk.isAttached()) { hk.tickAt(i + 1); break } }
  report('grapple', r && hk && hk.isAttached() && a.getDeltaMovement().z() < 0, `cast=${r} hook=${!!hk} attached=${hk && hk.isAttached()} vz=${a.getDeltaMovement().z()}`)
  const stones = (x, z) => [[2, 0], [-2, 0], [0, 2], [0, -2]].forEach(([dx, dz]) => server.runCommandSilent(`setblock ${x + dx} 200 ${z + dz} wizardry_restored:transportation_stone`))
  clear(); stones(0, 0); place(0); fake.setShiftKeyDown(true); r1 = cast('transportation'); fake.setShiftKeyDown(false)
  fake.setPos(6.5, 200, 6.5); r = cast('transportation'); let back = Math.abs(fake.getX() - 0.5) < 0.01 && Math.abs(fake.getZ() - 0.5) < 0.01
  server.runCommandSilent('setblock 2 200 0 minecraft:air'); fake.setPos(6.5, 200, 6.5); let r2 = cast('transportation')
  report('transportation', r1 && r && back && !r2, `mark=${r1} go=${r} back=${back} broken_refused=${!r2}`)

  // Артефакты: фейковому игроку надеваем предмет в слот Curios
  const Curios = Java.loadClass('top.theillusivec4.curios.api.CuriosApi')
  const unequip = () => Curios.getCuriosInventory(fake).ifPresent(inv => inv.getCurios().values().forEach(h => { for (let i = 0; i < h.getSlots(); i++) h.getStacks().setStackInSlot(i, Item.of('minecraft:air')) }))
  const equip = (item) => { let ok = false; unequip(); Curios.getCuriosInventory(fake).ifPresent(inv => { for (const slot of ['ring', 'necklace', 'charm', 'belt', 'curio']) { let h = inv.getStacksHandler(slot); if (!ok && h.isPresent()) { h.get().getStacks().setStackInSlot(0, Item.of(item)); ok = true } } }); return ok }
  const AC = Java.loadClass('com.binaris.wizardry.core.integrations.ArtifactChannel')
  const worn = (item) => AC.isEquipped(fake, Item.of(item).getItem())
  clear(); equip('ebwizardry:charm_silk_touch'); server.runCommandSilent('setblock 0 201 3 minecraft:stone'); place(0); r = cast('mine')
  let drops = near('item', 8).map(e => String(e.getItem().getItem())); report('charm_silk_touch', r && worn('ebwizardry:charm_silk_touch') && drops.includes('stone'), `cast=${r} worn=${worn('ebwizardry:charm_silk_touch')} drops=${drops}`)
  clear(); equip('ebwizardry:charm_light'); place(0); r = cast('light'); let lit = count('light'); fake.setShiftKeyDown(true); r1 = cast('light'); fake.setShiftKeyDown(false)
  report('charm_light', r && lit == 1 && r1 && count('light') == 0, `cast=${r} light=${lit} dispel=${r1} left=${count('light')}`)
  clear(); equip('ebwizardry:charm_stop_time'); a = mob(2.5, 0.5); place(0); r = cast('slow_time'); report('charm_stop_time', r && effId(a, 'paralysis'), `cast=${r}`)
  clear(); equip('ebwizardry:charm_transportation'); stones(0, 0); stones(6, 6); place(0); fake.setShiftKeyDown(true); cast('transportation')
  fake.setPos(6.5, 200, 6.5); cast('transportation'); fake.setPos(-6.5, 200, -6.5); r1 = cast('transportation'); fake.setShiftKeyDown(false); r = cast('transportation')
  report('charm_transportation', r1 && r && Math.abs(fake.getX() - 0.5) < 0.01 && Math.abs(fake.getZ() - 0.5) < 0.01, `cycle=${r1} go=${r} pos=${fake.getX()},${fake.getZ()}`)
  unequip(); clear()
})
