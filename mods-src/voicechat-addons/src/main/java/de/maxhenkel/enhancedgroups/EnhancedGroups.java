package de.maxhenkel.enhancedgroups;

import de.maxhenkel.admiral.MinecraftAdmiral;
import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.enhancedgroups.command.*;
import de.maxhenkel.enhancedgroups.config.AutoJoinGroupStore;
import de.maxhenkel.enhancedgroups.config.CommonConfig;
import de.maxhenkel.enhancedgroups.config.PersistentGroupStore;
import de.maxhenkel.enhancedgroups.events.GroupSummaryEvents;
import de.maxhenkel.voicechat.api.Group;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

// Порт на Forge (Minecraft_moded): события Fabric API заменены событиями Forge.
@Mod(EnhancedGroups.MOD_ID)
public class EnhancedGroups {

    public static final String MOD_ID = "enhancedgroups";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static CommonConfig CONFIG;
    public static PersistentGroupStore PERSISTENT_GROUP_STORE;
    public static AutoJoinGroupStore AUTO_JOIN_GROUP_STORE;

    public static EnhancedGroupPermissionManager PERMISSION_MANAGER;

    public EnhancedGroups() {
        Path configFolder = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
        CONFIG = ConfigBuilder.builder(CommonConfig::new).path(configFolder.resolve("%s.properties".formatted(MOD_ID))).build();
        PERSISTENT_GROUP_STORE = new PersistentGroupStore(configFolder.resolve("persistent-groups.json").toFile());
        AUTO_JOIN_GROUP_STORE = new AutoJoinGroupStore(configFolder.resolve("auto-join-groups.json").toFile());
        PERMISSION_MANAGER = new EnhancedGroupPermissionManager();

        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> MinecraftAdmiral.builder(event.getDispatcher(), event.getBuildContext())
                .addCommandClasses(
                        AutoJoinGroupCommands.class,
                        AutoJoinGroupGlobalCommands.class,
                        ForceJoinCommands.class,
                        InstantGroupCommands.class,
                        PersistentGroupCommands.class
                )
                .setPermissionManager(PERMISSION_MANAGER)
                .addArgumentTypes(argumentTypeRegistry -> argumentTypeRegistry.register(Group.Type.class, new GroupTypeArgumentSupplier(), new GroupTypeArgumentTypeSupplier()))
                .build());
        GroupSummaryEvents.init();
        MinecraftForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> {
            PERSISTENT_GROUP_STORE.clearCache();
        });
    }
}
