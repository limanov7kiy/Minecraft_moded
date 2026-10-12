// Тест порта Wizardry Golems (поток 12): каждое заклинание Golemancy от фейкового игрока призывает нужных
// големов Extra Golems и они — миньоны Redux. Вывод: GOLEMCHECK <заклинание> OK|FAIL <подробности>
ServerEvents.tick(event => {
  const server = event.server
  if (server.tickCount != 220 && server.tickCount != 420) return
  const RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
  const RM = Java.loadClass('net.minecraftforge.registries.RegistryManager')
  const Fake = Java.loadClass('net.minecraftforge.common.util.FakePlayerFactory')
  const Ctx = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.PlayerCastContext')
  const Mods = Java.loadClass('com.binaris.wizardry.api.content.spell.internal.SpellModifiers')
  const Hand = Java.loadClass('net.minecraft.world.InteractionHand')
  const Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  const level = server.getLevel('minecraft:overworld')
  const fake = Fake.getMinecraft(level)
  const golems = () => { let out = []; level.getEntitiesOfClass(Entity, fake.getBoundingBox().inflate(16)).forEach(e => {
    if (String(e.getType()) == 'golems:golem') out.push(e) }); return out }
  if (server.tickCount == 420) {
    // через 200 тиков миньоны живы (жизнь 600) — не исчезают сразу
    console.info(`GOLEMCHECK lifetime ${golems().length >= 5 ? 'OK' : 'FAIL'} alive=${golems().length}`)
    return
  }
  const reg = RM.ACTIVE.getRegistry(new RL('ebwizardry', 'spells'))
  server.runCommandSilent('forceload add -32 -32 32 32')
  server.runCommandSilent('fill -14 199 -14 14 199 14 minecraft:stone')
  server.runCommandSilent('fill -14 200 -14 14 212 14 minecraft:air')
  fake.setPos(0.5, 200, 0.5); fake.setOnGround(true)
  const cases = [['earth_golemancy', 'golems:clay', 3, false], ['fire_golemancy', 'golems:nether_brick', 2, false],
    ['ice_golemancy', 'golems:ice', 2, false], ['lightning_golemancy', 'wizardrygolems:thunderstone', 5, true],
    ['healing_golemancy', 'golems:gold', 1, false]]
  cases.forEach(c => {
    const spell = reg.getValue(new RL('wizardrygolems', c[0]))
    if (!spell) { console.info(`GOLEMCHECK ${c[0]} FAIL not_registered`); return }
    golems().forEach(g => g.discard())
    const r = spell.cast(new Ctx(level, fake, Hand.MAIN_HAND, 0, new Mods()))
    const gs = golems()
    const typed = gs.filter(g => String(g.nbt).includes(`Golem:"${c[1]}"`))
    const minions = gs.filter(g => String(g.getCustomName() ? g.getCustomName().getString() : '').length > 0)
    const baby = gs.filter(g => g.isBaby()).length
    const ok = r && gs.length == c[2] && typed.length == c[2] && minions.length == c[2] && (!c[3] || baby == c[2])
    console.info(`GOLEMCHECK ${c[0]} ${ok ? 'OK' : 'FAIL'} cast=${r} golems=${gs.length} typed=${typed.length} named=${minions.length} baby=${baby} hp=${gs.length ? gs[0].getHealth() : -1}`)
  })
  // оставить последних (золотой) + призвать ещё раз грозовых для проверки времени жизни
  reg.getValue(new RL('wizardrygolems', 'lightning_golemancy')).cast(new Ctx(level, fake, Hand.MAIN_HAND, 0, new Mods()))
})
