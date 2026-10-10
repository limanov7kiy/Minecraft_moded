package dev.mcmoded.wizardryrestored.entity;

import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import dev.mcmoded.wizardryrestored.WizardryRestored;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

/// Молот молний: падает с неба в указанную точку. При ударе о землю — разряд по всем врагам вокруг
/// с отбрасыванием (молния только видимая, без пожаров), затем ещё полминуты бьёт током ближайших врагов.
public class LightningHammerEntity extends AreaConstruct {
    private boolean landed;

    public LightningHammerEntity(EntityType<? extends LightningHammerEntity> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    /// Кольцо Талека (`ring_hammer`): упавший молот можно подобрать (и потом метнуть правым кликом).
    @Override
    public net.minecraft.world.InteractionResult interact(net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        if (!landed || !dev.mcmoded.wizardryrestored.util.Artifacts.has(player, com.binaris.wizardry.setup.registries.EBItems.RING_HAMMER))
            return net.minecraft.world.InteractionResult.PASS;
        if (!level().isClientSide) {
            var stack = new net.minecraft.world.item.ItemStack(com.binaris.wizardry.setup.registries.EBItems.LIGHTNING_HAMMER.get());
            stack.getOrCreateTag().putLong(dev.mcmoded.wizardryrestored.util.Artifacts.HAMMER_EXPIRES, level().getGameTime() + Math.max(20, getLifetime() - tickCount));
            if (!player.addItem(stack)) player.drop(stack, false);
            discard();
        }
        return net.minecraft.world.InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public boolean isPickable() {
        return !isRemoved();
    }

    public boolean hasLanded() {
        return landed;
    }

    @Override
    public void tick() {
        super.tick();
        if (!landed) {
            setDeltaMovement(getDeltaMovement().add(0, -0.08, 0).scale(0.98));
            move(MoverType.SELF, getDeltaMovement());
            if (onGround() && !level().isClientSide) land();
        }
        if (level().isClientSide) {
            if (onGround() && random.nextInt(4) == 0)
                level().addParticle(ParticleTypes.ELECTRIC_SPARK, getX() + random.nextGaussian() * 0.4, getY() + random.nextDouble() * 1.5, getZ() + random.nextGaussian() * 0.4, 0, 0, 0);
            return;
        }
        if (!landed || tickCount % 20 != 0) return;
        var spell = WizardryRestored.LIGHTNING_HAMMER.get();
        livingWithin(spell.property(DefaultProperties.EFFECT_RADIUS)).stream().filter(e -> !isFriend(e))
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(this))).ifPresent(e -> {
                    e.hurt(damage(EBDamageSources.SHOCK), spell.property(SECONDARY_DAMAGE) * damageMultiplier);
                    Vec3 from = position().add(0, 1.2, 0), to = e.position().add(0, e.getBbHeight() / 2, 0);
                    for (int i = 0; i <= 8; i++) {
                        Vec3 p = from.lerp(to, i / 8.0);
                        ((ServerLevel) level()).sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0);
                    }
                });
    }

    public static final com.binaris.wizardry.api.content.spell.properties.SpellProperty<Float> SECONDARY_DAMAGE =
            com.binaris.wizardry.api.content.spell.properties.SpellProperty.floatProperty("secondary_damage", 3f);

    private void land() {
        landed = true;
        ServerLevel server = (ServerLevel) level();
        var spell = WizardryRestored.LIGHTNING_HAMMER.get();
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(server);
        if (bolt != null) {
            bolt.moveTo(getX(), getY(), getZ());
            bolt.setVisualOnly(true);
            server.addFreshEntity(bolt);
        }
        server.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.5, getZ(), 3, 1, 0.2, 1, 0);
        playSound(SoundEvents.ANVIL_LAND, 1.5f, 0.6f);
        for (LivingEntity e : livingWithin(spell.property(DefaultProperties.EFFECT_RADIUS))) {
            if (isFriend(e)) continue;
            e.hurt(damage(EBDamageSources.SHOCK), spell.property(DefaultProperties.DAMAGE) * damageMultiplier);
            Vec3 away = e.position().subtract(position()).multiply(1, 0, 1);
            push(e, (away.lengthSqr() < 1.0e-4 ? new Vec3(1, 0, 0) : away.normalize()).scale(1.2).add(0, 0.5, 0));
        }
    }

    @Override
    protected void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        landed = tag.getBoolean("landed");
    }

    @Override
    protected void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("landed", landed);
    }
}
