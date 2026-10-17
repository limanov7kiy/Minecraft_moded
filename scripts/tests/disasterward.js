// Тест (поток 15): оберег от катаклизмов (mods-src/disaster-ward) + Weather2 + Natural Disasters!.
// Вывод: DWCHECK <проверка> OK|FAIL <подробности>. Фаза 2 (DWLOAD) — замер тиков при торнадо Weather2 и бедствиях Natural Disasters!
// над застройкой Lost Cities (без оберега и с ним). Запуск: см. docs/compat.md «Weather2»; EXTRA_SERVER_SCRIPTS — папка только с этим файлом.
const DW = {}
ServerEvents.tick(event => {
  const server = event.server, T = server.tickCount
  const level = server.getLevel('minecraft:overworld')
  const Api = () => Java.loadClass('dev.mcmoded.disasterward.WardApi')
  const BlockPos = Java.loadClass('net.minecraft.core.BlockPos')
  const Entity = Java.loadClass('net.minecraft.world.entity.Entity')
  const ok = (name, cond, info) => console.info(`DWCHECK ${name} ${cond ? 'OK' : 'FAIL'} ${info || ''}`)
  const near = (id, x, z, r) => { let n = 0; level.getEntitiesOfClass(Entity, new (Java.loadClass('net.minecraft.world.phys.AABB'))(x - r, -64, z - r, x + r, 320, z + r))
    .forEach(e => { if (String(e.getType().toString()).includes(id)) n++ }); return n }

  if (T == 60) {
    server.runCommandSilent('forceload add -32 -32 32 32')
    server.runCommandSilent('forceload add 140 140 160 160')
  }
  if (T == 100) {
    server.runCommandSilent('setblock 0 200 0 disaster_ward:ward')
    ok('ward-placed', String(level.getBlockState(new BlockPos(0, 200, 0)).block.id) == 'disaster_ward:ward', '')
    ok('zone-in', Api().isProtected(level, 30, 30), 'x=30')
    ok('zone-out', !Api().isProtected(level, 150, 150), 'x=150')
    // внутри зоны и в запасе у края (64+16) — не должны появиться; снаружи — должны (кроме отключённых глобально)
    server.runCommandSilent('summon natural_disasters:acidrain 10 200 10')
    server.runCommandSilent('summon natural_disasters:meteor 70 200 0')
    console.info('DWCHECK debug summon-outside rc=' + server.runCommandSilent('summon natural_disasters:acidrain 150 200 150'))
    server.runCommandSilent('summon natural_disasters:tornado 152 200 152')
    server.runCommandSilent('summon natural_disasters:sandstorm 154 200 154')
  }
  if (T == 101) {
    var all = []; level.getAllEntities().forEach(e => { if (String(e.getType()).includes('natural_disasters')) all.push(`${e.getType()}@${Math.round(e.x)},${Math.round(e.z)}`) })
    console.info('DWCHECK debug nd-entities@101 ' + all.join(' '))
  }
  if (T == 140) {
    var all2 = []; level.getAllEntities().forEach(e => { if (String(e.getType()).includes('natural_disasters')) all2.push(`${e.getType()}@${Math.round(e.x)},${Math.round(e.z)}`) })
    console.info('DWCHECK debug nd-entities@140 ' + all2.join(' '))
    ok('inside-blocked', near('natural_disasters', 0, 0, 90) == 0, `entities in zone=${near('natural_disasters', 0, 0, 90)}`)
    ok('outside-allowed', near('acidrain', 150, 150, 30) == 1, `acidrain outside=${near('acidrain', 150, 150, 30)}`)
    ok('tornado-disabled', near('tornado', 152, 152, 30) == 0, `nd tornado=${near('tornado', 152, 152, 30)}`)
    ok('sandstorm-disabled', near('sandstorm', 154, 154, 30) == 0, `nd sandstorm=${near('sandstorm', 154, 154, 30)}`)
    // бедствие, зашедшее в зону уже после появления: ставим снаружи и телепортируем внутрь — должно исчезнуть за ≤10 тиков
    server.runCommandSilent('tp @e[type=natural_disasters:acidrain,x=150,y=200,z=150,distance=..30] 20 200 20')
  }
  if (T == 170) {
    ok('entered-zone-removed', near('acidrain', 20, 20, 10) == 0, `acidrain in zone=${near('acidrain', 20, 20, 10)}`)
    // с потока 22 оберег не регистрируется «Отражателем бурь» Weather2 (там радиус зашит, 128): бури разворачивает сам оберег по своему радиусу (sporeward.js)
    ok('weather2-not-deflector', !Api().weatherDeflected(level, new BlockPos(0, 200, 0), 128), 'findWeatherDeflector(0,200,0,128) пуст')
    try {
      var V = Java.loadClass('net.mcreator.naturaldisasters.network.NaturalDisastersModVariables$WorldVariables')
      ok('natural-enabled', V.get(level).NaturalDisastersSpawningNaturalCheckBox, 'чекбокс случайных бедствий Natural Disasters!')
    } catch (e) { ok('natural-enabled', false, 'ошибка: ' + e) }
    server.runCommandSilent('kill @e[type=natural_disasters:acidrain]')
    // ломаем оберег командой (destroy = как в выживании): предметов быть не должно, зона пропадает
    console.info('DWCHECK debug destroy rc=' + server.runCommandSilent('setblock 0 200 0 minecraft:air destroy'))
    console.info('DWCHECK debug block now=' + level.getBlockState(new BlockPos(0, 200, 0)).block.id)
  }
  if (T == 180) {
    ok('ward-no-drop', near('item', 0, 0, 8) == 0, `items=${near('item', 0, 0, 8)}`)
    ok('zone-gone', !Api().isProtected(level, 30, 30), '')
  }
})
