/*
 * Ported from Galacticraft Legacy (micdoodle8, radfast, Team Galacticraft):
 * micdoodle8/mods/galacticraft/core/tile/TileEntityTreasureChest.java (locked flag, tier, findClosest). See CREDITS.md.
 */
package dev.mcmoded.gcrevival.block;

import dev.mcmoded.gcrevival.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

public class TreasureChestBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private boolean locked = true;

    public TreasureChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.TREASURE_CHEST_BE.get(), pos, state);
    }

    public int getTier() {
        return this.getBlockState().getBlock() instanceof TreasureChestBlock b ? b.getTier() : 1;
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        this.locked = true;
        this.sync();
    }

    public void unlock() {
        this.locked = false;
        this.sync();
    }

    private void sync() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.gcrevival.treasure_chest", this.getTier());
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return ChestMenu.threeRows(id, inventory, this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.locked = !tag.contains("isLocked") || tag.getBoolean("isLocked");
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isLocked", this.locked);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putBoolean("isLocked", this.locked);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /** Ближайший сундук нужного тира в загруженных чанках (Legacy TileEntityTreasureChest.findClosest). */
    @Nullable
    public static TreasureChestBlockEntity findClosest(Entity entity, int tier) {
        Level level = entity.level();
        ChunkPos center = entity.chunkPosition();
        TreasureChestBlockEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                if (!level.hasChunk(center.x + dx, center.z + dz)) continue;
                for (BlockEntity be : level.getChunk(center.x + dx, center.z + dz).getBlockEntities().values()) {
                    if (be instanceof TreasureChestBlockEntity chest && chest.getTier() == tier) {
                        double d = entity.distanceToSqr(be.getBlockPos().getCenter());
                        if (d < bestDist) {
                            bestDist = d;
                            best = chest;
                        }
                    }
                }
            }
        }
        return best;
    }
}
