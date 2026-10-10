package dev.mcmoded.wizardryrestored.entity;

import com.binaris.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/// Общая основа наших конструкций поверх `MagicConstructEntity` Redux (заклинатель, время жизни и множитель урона
/// хранятся там): поиск существ в радиусе, урон от имени заклинателя, толчок с синхронизацией игрокам.
public abstract class AreaConstruct extends MagicConstructEntity {
    protected AreaConstruct(EntityType<?> type, Level level) {
        super(type, level);
    }

    /// Центр действия — середина сущности по высоте.
    protected Vec3 center() {
        return position().add(0, getBbHeight() / 2, 0);
    }

    protected List<LivingEntity> livingWithin(double radius) {
        Vec3 c = center();
        return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius),
                e -> e.isAlive() && e.position().add(0, e.getBbHeight() / 2, 0).distanceToSqr(c) <= radius * radius);
    }

    /// Свои: заклинатель и его союзники (в том числе его миньоны).
    protected boolean isFriend(Entity e) {
        return e == getCaster() || !isValidTarget(e);
    }

    protected DamageSource damage(ResourceKey<DamageType> type) {
        LivingEntity caster = getCaster();
        return caster != null ? MagicDamageSource.causeIndirectMagicDamage(this, caster, type)
                : MagicDamageSource.causeDirectMagicDamage(this, type);
    }

    protected static void push(Entity e, Vec3 velocity) {
        if (dev.mcmoded.wizardryrestored.util.Artifacts.anchored(e)) return;
        e.setDeltaMovement(velocity);
        e.hurtMarked = true;
        if (e instanceof ServerPlayer p) p.connection.send(new ClientboundSetEntityMotionPacket(e));
    }

    /// Один тик конструкции с заданным счётчиком тиков — для серверных тестов (`castcheck.js`), где всё
    /// происходит внутри одного тика мира.
    public void tickAt(int tick) {
        tickCount = tick;
        tick();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
