package dev.mcmoded.undeadrising.entity.projectile;

import dev.mcmoded.undeadrising.registry.FurEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;

/// Огненная струя — плевок огненного Osvermis (Долина песка душ): урон, поджог цели, поджигает блок при попадании
/// (у мобов — только при разрешённом mobGriefing). Оригинал: Fish's Undead Rising (Fish), `entities/projectiles/FlameJetEntity`.
public class FlameJetEntity extends ThrowableItemProjectile {
    private float damage = 6.0F;

    public FlameJetEntity(EntityType<? extends FlameJetEntity> type, Level level) {
        super(type, level);
    }

    public FlameJetEntity(LivingEntity shooter, Level level) {
        super(FurEntities.FLAMEJET.get(), shooter, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            for (int i = 0; i < 8 + this.random.nextInt(8); i++) {
                this.level().addParticle(this.particleType(), this.getX() + this.random.nextDouble() * 0.5,
                        this.getY() + 0.5 + this.random.nextDouble() * 0.5, this.getZ() + this.random.nextDouble() * 0.5, 0.0, 0.0, 0.0);
            }
        }
    }

    protected ParticleOptions particleType() {
        return ParticleTypes.SOUL_FIRE_FLAME;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        if (!this.level().isClientSide && this.getOwner() instanceof LivingEntity owner && target instanceof LivingEntity && target != owner) {
            this.damage = (float) owner.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
            if (target.hurt(this.damageSources().thrown(this, owner), this.damage)) {
                float localDifficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
                target.setRemainingFireTicks(40 * (int) localDifficulty);
                this.doEnchantDamageEffects(owner, target);
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            Entity owner = this.getOwner();
            if (!(owner instanceof Mob) || ForgeEventFactory.getMobGriefingEvent(this.level(), owner)) {
                BlockPos pos = result.getBlockPos().relative(result.getDirection());
                if (this.level().isEmptyBlock(pos)) this.level().setBlockAndUpdate(pos, BaseFireBlock.getState(this.level(), pos));
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) this.discard();
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FIRE_CHARGE;
    }
}
