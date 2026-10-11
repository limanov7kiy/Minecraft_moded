package dev.mcmoded.port.akweapon;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

/**
 * Переходник для Arknights_Weapon 1.5.6 (MCreator, AFL 3.0): процедура RiseProcedure на каждом
 * EntityJoinLevelEvent ставила любому не-игроку базу max_health = текущий максимум × 2. Событие приходит
 * и при каждой загрузке чанка, поэтому здоровье мобов росло вдвое при каждой подгрузке. Других вызовов
 * у процедуры нет — отключаем её целиком.
 */
public final class AkWeaponPort {
    public static void noRise(Event event, Entity entity) {}

    private AkWeaponPort() {}
}
