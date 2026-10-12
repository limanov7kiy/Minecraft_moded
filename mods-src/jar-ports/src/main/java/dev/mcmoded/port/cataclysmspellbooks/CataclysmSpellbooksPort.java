package dev.mcmoded.port.cataclysmspellbooks;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.RingParticle;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Ancient_Remnant.Ancient_Remnant_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Wither_Missile_Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Переходники для Cataclysm: Spellbooks 1.2.9 (Ace The Eldritch King & StarOcean, MIT) под L_Ender's Cataclysm 3.31.
 * Частицы с тем же конструктором и рендерер Древнего Остатка переименованы правилами, здесь — изменившиеся API.
 */
public final class CataclysmSpellbooksPort {
    /** RingParticle.RingData: цвет был 0..1 (float), стал 0..255 (int); поведение — номер в EnumRingBehavior. */
    public static RingParticleOptions ring(float yaw, float pitch, int duration, float r, float g, float b, float a,
                                           float scale, boolean facesCamera, RingParticle.EnumRingBehavior behavior) {
        return new RingParticleOptions(yaw, pitch, duration, Math.round(r * 255), Math.round(g * 255), Math.round(b * 255),
            a, scale, facesCamera, behavior.ordinal());
    }

    /** Wither_Missile_Entity(type, owner, x, y, z, dx, dy, dz, damage, level): направление стало Vec3. */
    @SuppressWarnings("unchecked")
    public static Wither_Missile_Entity witherMissile(EntityType type, LivingEntity owner, double x, double y, double z,
                                                      double dx, double dy, double dz, float damage, Level level) {
        return new Wither_Missile_Entity((EntityType<? extends Wither_Missile_Entity>) type, owner, x, y, z, new Vec3(dx, dy, dz), damage, level);
    }

    /**
     * Ancient_Remnant_Entity.setSleep убран: в 3.31 Остаток «спит», пока на нём нет ожерелья (isSleep =
     * состояние сна/пробуждения или нет ожерелья). Призванный заклинанием фантом будится как ожерельем, но без
     * анимации пробуждения — сразу в бой, как было со setSleep(false).
     */
    public static void setSleep(Ancient_Remnant_Entity remnant, boolean sleep) {
        remnant.setNecklace(!sleep);
        if (!sleep && remnant.getAttackState() == 1) remnant.setAttackState(0);
    }

    private CataclysmSpellbooksPort() {}
}
