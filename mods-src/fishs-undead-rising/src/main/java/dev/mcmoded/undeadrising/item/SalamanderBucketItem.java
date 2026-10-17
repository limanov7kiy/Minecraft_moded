package dev.mcmoded.undeadrising.item;

import dev.mcmoded.undeadrising.entity.SalamanderEntity;
import dev.mcmoded.undeadrising.registry.FurEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;

/// Ведро с нимфой саламандры: выпускает её обратно (со всеми данными — хозяин, окрас, здоровье).
/// Оригинал: Fish's Undead Rising (Fish), `item/FURFishBucketItem` (ведро без жидкости).
public class SalamanderBucketItem extends Item {
    public static final String DATA = "SalamanderData";

    public SalamanderBucketItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        BlockPos pos = ctx.getLevel().getBlockState(ctx.getClickedPos()).getCollisionShape(ctx.getLevel(), ctx.getClickedPos()).isEmpty()
                ? ctx.getClickedPos() : ctx.getClickedPos().relative(ctx.getClickedFace());
        if (!(ctx.getLevel() instanceof ServerLevel level)) return InteractionResult.SUCCESS;
        ItemStack stack = ctx.getItemInHand();
        SalamanderEntity salamander = FurEntities.SALAMANDER.get().spawn(level, stack, ctx.getPlayer(), pos, MobSpawnType.BUCKET, true, false);
        if (salamander == null) return InteractionResult.FAIL;
        if (stack.getTag() != null && stack.getTag().contains(DATA)) {
            salamander.readAdditionalSaveData(stack.getTag().getCompound(DATA));
        } else {
            salamander.setBaby(true);
        }
        salamander.setPersistenceRequired();
        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
        Player player = ctx.getPlayer();
        if (player == null || !player.getAbilities().instabuild) {
            if (player != null) player.setItemInHand(ctx.getHand(), new ItemStack(Items.BUCKET));
        }
        return InteractionResult.CONSUME;
    }
}
