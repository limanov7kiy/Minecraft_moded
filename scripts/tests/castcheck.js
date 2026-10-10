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
  clear(); a = mob(0.5, 6.5); place(10); r = cast('grapple'); report('grapple', r && a.getDeltaMovement().z() < 0, `cast=${r} vz=${a.getDeltaMovement().z()}`)
  clear(); place(0); fake.setShiftKeyDown(true); let r1 = cast('transportation'); fake.setShiftKeyDown(false); fake.setPos(6.5, 200, 6.5); r = cast('transportation')
  report('transportation', r1 && r && Math.abs(fake.getX() - 0.5) < 0.01 && Math.abs(fake.getZ() - 0.5) < 0.01, `mark=${r1} go=${r} pos=${fake.getX()},${fake.getZ()}`)
  clear(); a = mob(0.5, 3.5); place(10); r = cast('ice_statue'); report('ice_statue', r && count('ice') >= 8 && effId(a, 'paralysis'), `cast=${r} ice=${count('ice')}`)
  clear(); a = mob(0.5, 3.5); place(10); r = cast('petrify'); report('petrify', r && effId(a, 'paralysis') && a.hasEffect(Eff.DAMAGE_RESISTANCE), `cast=${r}`)
  clear(); a = mob(2.5, 0.5); place(0); r = cast('lightning_pulse'); report('lightning_pulse', r && a.getHealth() < 20, `cast=${r} hp=${a.getHealth()}`)
  clear(); place(0); r = cast('summon_ice_giant'); report('summon_ice_giant', r && near('ebwizardry:ice_giant', 8).length == 1, `cast=${r} n=${near('ebwizardry:ice_giant', 8).length}`)
  clear(); place(0); [Slot.HEAD, Slot.CHEST, Slot.LEGS, Slot.FEET].forEach(sl => fake.setItemSlot(sl, Item.of('minecraft:air')))
  r = cast('conjure_armor'); report('conjure_armor', r && !fake.getItemBySlot(Slot.CHEST).isEmpty(), `cast=${r} chest=${fake.getItemBySlot(Slot.CHEST)}`)
  clear(); place(0); r = cast('silverfish_swarm'); report('silverfish_swarm', r && near('silverfish', 8).length >= 10, `cast=${r} n=${near('silverfish', 8).length}`)
  clear()
})
