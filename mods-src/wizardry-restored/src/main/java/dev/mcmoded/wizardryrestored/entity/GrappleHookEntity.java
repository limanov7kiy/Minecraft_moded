package dev.mcmoded.wizardryrestored.entity;

import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.setup.registries.EBItems;
import dev.mcmoded.wizardryrestored.util.SpellStates;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

/// Лоза захвата: летит по взгляду; зацепившись за блок — подтягивает заклинателя, за существо — подтягивает его
/// к заклинателю (тяжёлое — наоборот). Держится, пока заклинание удерживается. С «Зачарованной бечевой»
/// (`charm_abseiling`) при Shift лоза не тянет, а потихоньку стравливается — можно спускаться.
public class GrappleHookEntity extends AreaConstruct {
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(GrappleHookEntity.class, EntityDataSerializers.INT);
    public static final int MAX_FLIGHT = 20;
    private Vec3 velocity = Vec3.ZERO;
    private Vec3 anchor;
    private Entity hooked;
    private double rope;
    /// Заклинатель — прямой ссылкой (лоза не сохраняется в мир, искать его по UUID каждый тик незачем).
    private LivingEntity owner;

    public GrappleHookEntity(EntityType<? extends GrappleHookEntity> type, Level level) {
        super(type, level);
    }

    public void launch(LivingEntity owner, Vec3 velocity) {
        this.owner = owner;
        setCaster(owner);
        entityData.set(OWNER_ID, owner.getId());
        this.velocity = velocity;
        setPos(owner.getEyePosition(1).add(velocity.normalize().scale(0.5)));
    }

    public Entity ownerClient() {
        return level().getEntity(entityData.get(OWNER_ID));
    }

    public boolean isAttached() {
        return anchor != null || hooked != null;
    }

    /// Лоза живёт только пока заклинание удерживается — в мир не сохраняется.
    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_ID, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (owner == null || !owner.isAlive() || owner.level() != level() || owner.distanceToSqr(this) > 40 * 40
                || SpellStates.data(owner).getLong(SpellStates.GRAPPLE_UNTIL) < level().getGameTime()) {
            discard();
            return;
        }
        if (!isAttached()) fly(owner);
        else if (hooked != null) pullEntity(owner);
        else pullOwner(owner);
    }

    private void fly(LivingEntity owner) {
        if (tickCount > MAX_FLIGHT) {
            discard();
            return;
        }
        Vec3 from = position(), to = from.add(velocity);
        HitResult hit = level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hit.getType() != HitResult.Type.MISS) to = hit.getLocation();
        EntityHitResult eHit = ProjectileUtil.getEntityHitResult(level(), this, from, to, getBoundingBox().expandTowards(velocity).inflate(1),
                e -> e != owner && e instanceof LivingEntity && e.isAlive() && !e.isSpectator());
        if (eHit != null) {
            hooked = eHit.getEntity();
            setPos(hooked.position().add(0, hooked.getBbHeight() / 2, 0));
        } else if (hit instanceof BlockHitResult b && hit.getType() == HitResult.Type.BLOCK) {
            anchor = b.getLocation();
            setPos(anchor);
        } else {
            setPos(to);
            return;
        }
        rope = owner.getEyePosition(1).distanceTo(position());
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.VINE_STEP, getSoundSource(), 1, 0.8f);
        ((ServerLevel) level()).sendParticles(ParticleTypes.COMPOSTER, getX(), getY(), getZ(), 8, 0.2, 0.2, 0.2, 0);
    }

    private void pullOwner(LivingEntity owner) {
        Vec3 to = anchor.subtract(owner.getEyePosition(1));
        double dist = to.length();
        owner.fallDistance = 0;
        boolean abseil = owner instanceof Player p && p.isShiftKeyDown() && ArtifactChannel.isEquipped(p, EBItems.CHARM_ABSEILING.get());
        if (abseil) {
            rope = Math.min(rope + 0.1, 40);
            if (dist > rope) push(owner, owner.getDeltaMovement().add(to.normalize().scale((dist - rope) * 0.3)));
            return;
        }
        rope = Math.min(rope, dist);
        if (dist < 1.5) {
            push(owner, owner.getDeltaMovement().scale(0.5).add(0, 0.08, 0));
            return;
        }
        Vec3 v = owner.getDeltaMovement().scale(0.8).add(to.normalize().scale(0.3));
        if (v.length() > 1.2) v = v.normalize().scale(1.2);
        push(owner, v);
    }

    private void pullEntity(LivingEntity owner) {
        if (!hooked.isAlive() || hooked.level() != level()) {
            discard();
            return;
        }
        setPos(hooked.position().add(0, hooked.getBbHeight() / 2, 0));
        Vec3 to = owner.position().subtract(hooked.position());
        if (to.length() < 2) return;
        boolean heavy = hooked.getType().is(Tags.EntityTypes.BOSSES) || hooked.getBbWidth() * hooked.getBbHeight() > 4 * owner.getBbWidth() * owner.getBbHeight();
        if (heavy) {
            owner.fallDistance = 0;
            push(owner, owner.getDeltaMovement().scale(0.8).add(to.normalize().scale(-0.3)));
        } else {
            push(hooked, to.normalize().scale(Math.min(1.2, 0.15 * to.length())).add(0, 0.12, 0));
        }
    }
}
