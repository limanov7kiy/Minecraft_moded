package dev.mcmoded.wizardryrestored.entity;

import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import dev.mcmoded.wizardryrestored.WizardryRestored;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

/// Чёрная дыра: висит в воздухе и затягивает к себе врагов, предметы, опыт и снаряды; кто у самого центра —
/// получает урон. Заклинатель и союзники не затягиваются. Блоки не вырывает (в отличие от оригинала —
/// ради безопасности построек на сервере).
public class BlackHoleEntity extends AreaConstruct {
    public static final double PULL = 0.09;

    public BlackHoleEntity(EntityType<? extends BlackHoleEntity> type, Level level) {
        super(type, level);
    }

    /// Пустотный опал (`charm_black_hole`): дыра поглощает блоки рядом с собой — если заклинатель носит его,
    /// разрешён mobGriefing и защита территорий не против (`BlockEvent.BreakEvent` от имени заклинателя).
    private void devourBlock(double r) {
        LivingEntity caster = getCaster();
        if (!(caster instanceof Player player) || !dev.mcmoded.wizardryrestored.util.Artifacts.has(player, com.binaris.wizardry.setup.registries.EBItems.CHARM_BLACK_HOLE)
                || !net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level(), player)) return;
        double d = r / 2;
        net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.containing(center().add((random.nextDouble() * 2 - 1) * d, (random.nextDouble() * 2 - 1) * d, (random.nextDouble() * 2 - 1) * d));
        var state = level().getBlockState(pos);
        float hardness = state.getDestroySpeed(level(), pos);
        if (state.isAir() || hardness < 0 || hardness >= 50 || state.hasBlockEntity() || !state.getFluidState().isEmpty()) return;
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.level.BlockEvent.BreakEvent(level(), pos, state, player))) return;
        level().removeBlock(pos, false);
        ((net.minecraft.server.level.ServerLevel) level()).sendParticles(new net.minecraft.core.particles.BlockParticleOption(ParticleTypes.BLOCK, state),
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 12, 0.3, 0.3, 0.3, 0.1);
    }

    @Override
    public void tick() {
        super.tick();
        var spell = WizardryRestored.BLACK_HOLE.get();
        double r = spell.property(DefaultProperties.EFFECT_RADIUS);
        Vec3 c = center();

        if (level().isClientSide) {
            for (int i = 0; i < 4; i++) {
                Vec3 d = new Vec3(random.nextGaussian(), random.nextGaussian() * 0.3, random.nextGaussian()).normalize().scale(r * random.nextDouble());
                Vec3 v = d.scale(-0.08).add(d.cross(new Vec3(0, 1, 0)).normalize().scale(0.1));
                level().addParticle(ParticleTypes.PORTAL, c.x + d.x, c.y + d.y, c.z + d.z, -d.x, -d.y, -d.z);
                level().addParticle(ParticleTypes.SQUID_INK, c.x + d.x * 0.4, c.y + d.y * 0.4, c.z + d.z * 0.4, v.x, v.y, v.z);
            }
            return;
        }
        if (tickCount % 40 == 1) playSound(SoundEvents.PORTAL_AMBIENT, 1.2f, 0.5f);
        if (tickCount % 4 == 0) devourBlock(r);

        for (Entity e : level().getEntities(this, getBoundingBox().inflate(r))) {
            if (e instanceof com.binaris.wizardry.api.content.entity.construct.MagicConstructEntity
                    || e instanceof net.minecraft.world.entity.decoration.HangingEntity || e.isSpectator() || !e.isPushable() && !(e instanceof net.minecraft.world.entity.item.ItemEntity)
                    && !(e instanceof net.minecraft.world.entity.ExperienceOrb) && !(e instanceof net.minecraft.world.entity.projectile.Projectile) || (e instanceof Player p && p.getAbilities().flying)) continue;
            Vec3 to = c.subtract(e.position().add(0, e.getBbHeight() / 2, 0));
            double dist = to.length();
            if (dist > r) continue;
            if (e instanceof LivingEntity living) {
                if (isFriend(living)) continue;
                boolean boss = living.getType().is(Tags.EntityTypes.BOSSES);
                double strength = (boss ? 0.3 : 1) * PULL * (1.2 - dist / r);
                push(e, e.getDeltaMovement().scale(0.8).add(to.normalize().scale(strength)).add(0, 0.04, 0));
                if (dist < 1.5 && tickCount % 10 == 0)
                    living.hurt(damage(EBDamageSources.SORCERY), spell.property(DefaultProperties.DAMAGE) * damageMultiplier);
            } else {
                push(e, e.getDeltaMovement().scale(0.8).add(to.normalize().scale(PULL)).add(0, 0.04, 0));
            }
        }
    }
}
