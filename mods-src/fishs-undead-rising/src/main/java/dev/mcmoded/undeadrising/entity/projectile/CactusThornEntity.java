package dev.mcmoded.undeadrising.entity.projectile;

import dev.mcmoded.undeadrising.registry.FurEntities;
import dev.mcmoded.undeadrising.registry.FurItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/// Кактусовый шип (оригинал: Fish's Undead Rising (Fish), `entities/projectiles/CactusThornEntity`): стрела с
/// уроном стрелы и следом частиц «crit». Им стреляют Thorn Shooter и пояс шипов Necromancer's Delight.
public class CactusThornEntity extends AbstractArrow {
    public CactusThornEntity(EntityType<? extends CactusThornEntity> type, Level level) {
        super(type, level);
    }

    public CactusThornEntity(Level level, LivingEntity shooter) {
        super(FurEntities.CACTUS_THORN.get(), shooter, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && !this.inGround) {
            level().addParticle(ParticleTypes.CRIT, getX(), getY(), getZ(), 0, 0, 0);
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(FurItems.CACTUS_THORN.get());
    }
}
