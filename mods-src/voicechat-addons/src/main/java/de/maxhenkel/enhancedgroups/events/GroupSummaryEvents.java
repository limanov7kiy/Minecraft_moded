package de.maxhenkel.enhancedgroups.events;

import de.maxhenkel.enhancedgroups.EnhancedGroups;
import de.maxhenkel.enhancedgroups.EnhancedGroupsVoicechatPlugin;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GroupSummaryEvents {

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                onJoin(player, player.server);
            }
        });
    }

    private static void onJoin(ServerPlayer joined, MinecraftServer server) {
        if (!EnhancedGroups.CONFIG.groupSummary.get()) {
            return;
        }
        if (EnhancedGroupsVoicechatPlugin.SERVER_API == null) {
            return;
        }

        Map<UUID, Group> groups = new HashMap<>();
        int playersInGroups = 0;

        for (Player player : server.getPlayerList().getPlayers()) {
            VoicechatConnection connection = EnhancedGroupsVoicechatPlugin.SERVER_API.getConnectionOf(player.getUUID());
            if (connection == null) {
                continue;
            }
            Group group = connection.getGroup();
            if (group == null) {
                continue;
            }
            playersInGroups++;
            groups.put(group.getId(), group);
        }

        if (playersInGroups <= 0) {
            return;
        }

        MutableComponent component = Component.literal("There are currently %s player(s) in voice groups:".formatted(playersInGroups));

        for (Group group : groups.values()) {
            component.append("\n- ");
            component.append(Component.literal(group.getName()).withStyle(ChatFormatting.GRAY)).append(" ");
            if (group.hasPassword()) {
                continue;
            }

            component.append(
                    ComponentUtils.wrapInSquareBrackets(
                            Component.literal("Join").withStyle(style -> {
                                return style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/voicechat join " + group.getId()))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Join group")));
                            })
                    ).withStyle(ChatFormatting.GREEN)
            );
        }


        joined.sendSystemMessage(component);
    }

}
