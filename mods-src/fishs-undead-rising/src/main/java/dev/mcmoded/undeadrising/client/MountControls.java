package dev.mcmoded.undeadrising.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.mcmoded.undeadrising.entity.SalamanderEntity;
import dev.mcmoded.undeadrising.entity.flying.RidableFlyingMobEntity;
import dev.mcmoded.undeadrising.network.FurNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/// Управление ездовыми летунами (оригинал: Fish's Undead Rising (Fish), `init/FURKeybindRegistry` +
/// `RidableFlyingMobEntity#ClientControl`): прыжок — вверх, «вниз» (C) — вниз, «особая способность» (G).
public final class MountControls {
    private MountControls() {}

    private static final String CATEGORY = "key.categories.mod_lavacow";
    public static final KeyMapping MOUNT_SPECIAL = new KeyMapping("key.mod_lavacow.special", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping MOUNT_DOWN = new KeyMapping("key.mod_lavacow.down", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY);

    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(MOUNT_SPECIAL);
        event.register(MOUNT_DOWN);
    }

    /// Саламандра (наземная, оригинал `SalamanderEntity#ClientControl`): только «особая способность» — залп.
    public static void tickSalamander(SalamanderEntity mount) {
        Minecraft game = Minecraft.getInstance();
        if (game.player == null || !mount.isRidingPlayer(game.player)) return;
        if (mount.barrageCD == 0 && MOUNT_SPECIAL.isDown()) {
            mount.barrageCD = 80;
            FurNetwork.CHANNEL.sendToServer(new FurNetwork.MountSpecial(mount.getId()));
        }
    }

    /// Вызывается из `aiStep` ездового на клиенте.
    public static void tick(RidableFlyingMobEntity mount) {
        Minecraft game = Minecraft.getInstance();
        if (game.player == null || !mount.isRidingPlayer(game.player)) return;
        if (mount.abilityCooldown == 0 && MOUNT_SPECIAL.isDown() && mount.getLandTimer() <= 10) {
            mount.abilityCooldown = mount.abilityCooldown();
            FurNetwork.CHANNEL.sendToServer(new FurNetwork.MountSpecial(mount.getId()));
        }
        mount.setControlState(0, game.options.keyJump.isDown());
        mount.setControlState(1, MOUNT_DOWN.isDown());
    }
}
