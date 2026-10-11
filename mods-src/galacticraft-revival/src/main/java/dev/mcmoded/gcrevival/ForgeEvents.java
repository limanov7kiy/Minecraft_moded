package dev.mcmoded.gcrevival;

import dev.mcmoded.gcrevival.entity.SkeletonBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ForgeEvents {
    /** Игрок не может спрыгнуть с лунного босса по Shift — только босс его бросает (GC5 IgnoreShift). */
    @SubscribeEvent
    public static void onDismount(EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityMounting() instanceof Player player && player.isAlive() && !player.isCreative()
                && event.getEntityBeingMounted() instanceof SkeletonBoss boss && boss.isAlive() && !boss.allowDismount) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        DungeonCommands.register(event.getDispatcher());
    }

    private ForgeEvents() {
    }
}
