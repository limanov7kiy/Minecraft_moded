package dev.mcmoded.wizardrygolems.block;

import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.core.platform.Services;
import com.mcmoddev.golems.entity.GolemBase;
import dev.mcmoded.wizardrygolems.WizardryGolems;
import dev.mcmoded.wizardrygolems.spell.Golemancy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

import java.util.UUID;

/// Living Snow (оригинал — `BlockLivingSnow` + `TileEntityLivingSnow`, WinDanesz): ночью, если рядом (8 блоков)
/// враг хозяина (или любое существо, если хозяина нет), через 2 с из снега встаёт снежный голем.
/// Со своим хозяином — миньон на 400 т., перезарядка 400 т.; «дикий» снег после призыва становится обычным.
public class LivingSnow extends SnowLayerBlock implements EntityBlock {
    public LivingSnow(Properties props) {
        super(props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Tile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == GolemBlocks.LIVING_SNOW_TILE.get() ? (l, p, s, t) -> ((Tile) t).tick(l, p, s) : null;
    }

    public static class Tile extends BlockEntity {
        private static final int COOLDOWN = 400;
        private UUID owner;
        private int cooldown = 20;
        private int timeToSpawn = -1;

        public Tile(BlockPos pos, BlockState state) {
            super(GolemBlocks.LIVING_SNOW_TILE.get(), pos, state);
        }

        public void setOwner(Player p) {
            owner = p.getUUID();
            setChanged();
        }

        void tick(Level level, BlockPos pos, BlockState state) {
            if (cooldown > 0) cooldown--;
            if (timeToSpawn >= 0) {
                if (level instanceof ServerLevel sl && level.getGameTime() % 4 == 0)
                    sl.sendParticles(ParticleTypes.SNOWFLAKE, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 4, 0.4, 0.1, 0.4, 0.02);
                if (--timeToSpawn == 0 && level instanceof ServerLevel sl) spawn(sl, pos, state);
                return;
            }
            if (level.isClientSide || level.isDay() || cooldown > 0 || level.getGameTime() % 41 != 0) return;
            Player p = owner == null ? null : level.getPlayerByUUID(owner);
            boolean target = !level.getEntitiesOfClass(LivingEntity.class, new net.minecraft.world.phys.AABB(pos).inflate(8), e -> {
                if (e instanceof GolemBase || e.isSpectator()) return false;
                if (p == null) return !(e instanceof Player pl && pl.isCreative());
                return e != p && AllyDesignation.isValidTarget(p, e);
            }).isEmpty();
            if (target) {
                timeToSpawn = 40;
                setChanged();
            }
        }

        private void spawn(ServerLevel level, BlockPos pos, BlockState state) {
            GolemBase golem = GolemBase.create(level, new ResourceLocation(WizardryGolems.MOD_ID, "snow"));
            golem.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            Player p = owner == null ? null : level.getPlayerByUUID(owner);
            if (p != null) {
                MinionData data = Services.OBJECT_DATA.getMinionData(golem);
                data.setSummoned(true);
                data.setOwnerUUID(owner);
                data.setLifetime(400);
                data.setShouldFollowOwner(true);
                data.setSearchNearbyTargets(true);
                golem.getPersistentData().putString(Golemancy.ELEMENT_TAG, "ice");
            }
            level.addFreshEntity(golem);
            cooldown = COOLDOWN;
            timeToSpawn = -1;
            setChanged();
            if (owner == null) level.setBlock(pos, Blocks.SNOW.defaultBlockState().setValue(LAYERS, state.getValue(LAYERS)), 3);
        }

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            if (owner != null) tag.putUUID("owner", owner);
            tag.putInt("cooldown", cooldown);
            tag.putInt("timeToSpawn", timeToSpawn);
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            owner = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
            cooldown = tag.getInt("cooldown");
            timeToSpawn = tag.contains("timeToSpawn") ? tag.getInt("timeToSpawn") : -1;
        }
    }
}
