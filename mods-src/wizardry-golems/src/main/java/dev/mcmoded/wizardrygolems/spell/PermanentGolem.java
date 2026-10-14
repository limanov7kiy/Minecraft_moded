package dev.mcmoded.wizardrygolems.spell;

import com.mcmoddev.golems.entity.GolemBase;
import dev.mcmoded.wizardrygolems.item.GolemItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/// Ring of the Golemancer (оригинал — `ItemPermanentGolemRing`): следующий призванный голем живёт без срока;
/// одновременно только один такой голем, откат кольца 5 минут (6000 т.). UUID голема — в persistentData игрока.
final class PermanentGolem {
    private static final String KEY = "wizardrygolems_permanent_golem";

    private PermanentGolem() {}

    static boolean claim(LivingEntity caster, GolemBase golem) {
        if (!(caster instanceof Player p) || !(p.level() instanceof ServerLevel level)) return false;
        var ring = GolemItems.RING_PERMANENT_GOLEM.get();
        if (!Golemancies.has(p, GolemItems.RING_PERMANENT_GOLEM) || p.getCooldowns().isOnCooldown(ring)) return false;
        var data = p.getPersistentData();
        if (data.hasUUID(KEY)) {
            Entity old = level.getEntity(data.getUUID(KEY));
            if (old != null && old.isAlive()) return false;
        }
        data.putUUID(KEY, golem.getUUID());
        p.getCooldowns().addCooldown(ring, 6000);
        return true;
    }
}
