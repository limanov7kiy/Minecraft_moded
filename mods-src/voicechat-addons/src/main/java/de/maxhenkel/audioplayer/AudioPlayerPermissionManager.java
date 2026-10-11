package de.maxhenkel.audioplayer;

import de.maxhenkel.admiral.permissions.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class AudioPlayerPermissionManager implements PermissionManager<CommandSourceStack> {

    public static final AudioPlayerPermissionManager INSTANCE = new AudioPlayerPermissionManager();

    private static final Permission VOLUME_PERMISSION = new Permission("audioplayer.volume", PermissionType.EVERYONE);
    private static final Permission UPLOAD_PERMISSION = new Permission("audioplayer.upload", PermissionType.EVERYONE);
    private static final Permission APPLY_PERMISSION = new Permission("audioplayer.apply", PermissionType.EVERYONE);
    private static final Permission APPLY_ANNOUNCER_PERMISSION = new AnnouncerPermission("audioplayer.set_static", PermissionType.EVERYONE);
    private static final Permission PLAY_COMMAND_PERMISSION = new Permission("audioplayer.play_command", PermissionType.OPS);

    private static final List<Permission> PERMISSIONS = List.of(
            UPLOAD_PERMISSION,
            APPLY_PERMISSION,
            APPLY_ANNOUNCER_PERMISSION,
            PLAY_COMMAND_PERMISSION,
            VOLUME_PERMISSION
    );

    @Override
    public boolean hasPermission(CommandSourceStack stack, String permission) {
        for (Permission p : PERMISSIONS) {
            if (!p.permission.equals(permission)) {
                continue;
            }
            if (!p.canUse()) {
                return false;
            }
            if (stack.isPlayer()) {
                return p.hasPermission(stack.getPlayer());
            }
            return stack.hasPermission(2);
        }
        return false;
    }

    private static class Permission {
        private final String permission;
        private final PermissionType type;

        public Permission(String permission, PermissionType type) {
            this.permission = permission;
            this.type = type;
        }

        public boolean canUse() {
            return true;
        }

        public boolean hasPermission(ServerPlayer player) {
            return type.hasPermission(player);
        }

        public PermissionType getType() {
            return type;
        }
    }

    private static class AnnouncerPermission extends Permission {

        public AnnouncerPermission(String permission, PermissionType type) {
            super(permission, type);
        }

        @Override
        public boolean canUse() {
            return AudioPlayer.SERVER_CONFIG.allowStaticAudio.get() && super.canUse();
        }
    }

    private static enum PermissionType {

        EVERYONE, NOONE, OPS;

        boolean hasPermission(ServerPlayer player) {
            return switch (this) {
                case EVERYONE -> true;
                case NOONE -> false;
                case OPS -> player != null && player.hasPermissions(player.server.getOperatorUserPermissionLevel());
            };
        }

    }

}
