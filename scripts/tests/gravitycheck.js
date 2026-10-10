// Тест: гравитацию на планетах задаёт одна система (Northstar — реальная, Ad Astra — нейтральная; Глацио — Ad Astra)
ServerEvents.loaded(event => {
  const Grav = Java.loadClass('earth.terrarium.adastra.api.systems.GravityApi')
  for (let p of ['moon', 'mars', 'venus', 'mercury', 'earth_orbit', 'moon_orbit', 'glacio']) {
    let level = event.server.getLevel('ad_astra:' + p)
    let ns = 'n/a'
    try { ns = level['northstar$gravity']() } catch (e) { ns = 'err ' + e }
    console.info(`GRAVCHECK ${p}: ad_astra=${Grav.API.getGravity(level)} northstar=${ns}`)
  }
})
