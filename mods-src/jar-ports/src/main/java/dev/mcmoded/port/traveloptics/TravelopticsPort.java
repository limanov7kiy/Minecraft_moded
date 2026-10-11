package dev.mcmoded.port.traveloptics;

import com.github.L_Ender.cataclysm.client.particle.Options.CircleLightningParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.RingParticle;
import com.github.L_Ender.cataclysm.entity.projectile.Laser_Beam_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Wither_Homing_Missile_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Переходники для T.O Magic 'n Extras 6.3.0 (gametechbc) под L_Ender's Cataclysm 3.31: частицы
 * Cataclysm (L_Ender) перешли с классов *Data на record'ы Options. Совпадающие по конструктору
 * переименованы правилами, здесь — только те, где конструктор изменился.
 */
public final class TravelopticsPort {
    /** CMConfig.HarbingerLightFire («лазеры Харбингера поджигают при mobGriefing») убран; было true по умолчанию. */
    public static boolean HARBINGER_LIGHT_FIRE = true;

    /** CircleLightningParticle.CircleData(r, g, b): размер стал отдельным полем, у самого Cataclysm он 0.1. */
    public static CircleLightningParticleOptions circle(int r, int g, int b) {
        return new CircleLightningParticleOptions(0.1F, r, g, b);
    }

    /** RingParticle.RingData: цвет был 0..1 (float), стал 0..255 (int); поведение — номер в EnumRingBehavior. */
    public static RingParticleOptions ring(float yaw, float pitch, int duration, float r, float g, float b, float a,
                                           float scale, boolean facesCamera, RingParticle.EnumRingBehavior behavior) {
        return new RingParticleOptions(yaw, pitch, duration, Math.round(r * 255), Math.round(g * 255), Math.round(b * 255),
            a, scale, facesCamera, behavior.ordinal());
    }

    /** ModParticle.SANDSTORM больше не регистрируется (текстура осталась) — пыль: minecraft:white_ash. */
    public static final RegistryObject<ParticleType<?>> SANDSTORM =
        RegistryObject.create(new ResourceLocation("minecraft", "white_ash"), ForgeRegistries.PARTICLE_TYPES);

    /** Laser_Beam_Entity(type, owner, x, y, z, dx, dy, dz, damage, level): направление стало Vec3. */
    @SuppressWarnings("unchecked")
    public static Laser_Beam_Entity laser(EntityType type, LivingEntity owner, double x, double y, double z,
                                          double dx, double dy, double dz, float damage, Level level) {
        return new Laser_Beam_Entity((EntityType<? extends Laser_Beam_Entity>) type, owner, x, y, z, new Vec3(dx, dy, dz), damage, level);
    }

    /** Wither_Homing_Missile_Entity(level, owner) убран; был: тип ракеты Cataclysm + владелец. */
    public static Wither_Homing_Missile_Entity witherMissile(Level level, LivingEntity owner) {
        Wither_Homing_Missile_Entity missile = new Wither_Homing_Missile_Entity(ModEntities.WITHER_HOMING_MISSILE.get(), level);
        missile.setOwner(owner);
        return missile;
    }

    private TravelopticsPort() {}
}
