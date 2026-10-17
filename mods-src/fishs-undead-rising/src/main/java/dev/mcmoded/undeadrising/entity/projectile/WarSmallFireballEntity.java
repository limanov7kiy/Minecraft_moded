package dev.mcmoded.undeadrising.entity.projectile;

import dev.mcmoded.undeadrising.entity.SalamanderEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

/// Малый боевой огненный шар (выстрел саламандры): не замедляется, живёт 10 с, урон 5 (у саламандры — её сила атаки),
/// поджигает цель на 5 с и блок при попадании (у мобов — при разрешённом mobGriefing).
/// Оригинал: Fish's Undead Rising (Fish), `entities/projectiles/WarSmallFireballEntity` + `EnchantableFireBallEntity`.
public class WarSmallFireballEntity extends Fireball {
    private float damage = 5.0F;
    protected int knockbackStrength;
    protected int flame;

    public WarSmallFireballEntity(EntityType<? extends WarSmallFireballEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        if (this.tickCount < 3) {
            this.level().addParticle(ParticleTypes.LAVA, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
        }
        super.tick();
        if (this.tickCount >= 200) this.discard();
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    @Override
    protected boolean shouldBurn() {
        return this.flame > 0;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return this.damage;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide) return;
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if (owner instanceof SalamanderEntity salamander) this.damage = (float) salamander.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        int fire = target.getRemainingFireTicks();
        target.setSecondsOnFire(5 + this.flame);
        DamageSource source = this.damageSources().fireball(this, owner);
        if (!target.hurt(source, this.damage)) {
            target.setRemainingFireTicks(fire);
        } else if (owner instanceof LivingEntity living) {
            if (this.knockbackStrength > 0) {
                Vec3 push = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(this.knockbackStrength * 0.6);
                if (push.lengthSqr() > 0.0) target.push(push.x, 0.1, push.z);
            }
            this.doEnchantDamageEffects(living, target);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide) return;
        Entity owner = this.getOwner();
        if (!(owner instanceof Mob) || ForgeEventFactory.getMobGriefingEvent(this.level(), owner)) {
            BlockPos pos = result.getBlockPos().relative(result.getDirection());
            if (this.level().isEmptyBlock(pos)) this.level().setBlockAndUpdate(pos, BaseFireBlock.getState(this.level(), pos));
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) this.discard();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.FIRE_CHARGE);
    }
}
