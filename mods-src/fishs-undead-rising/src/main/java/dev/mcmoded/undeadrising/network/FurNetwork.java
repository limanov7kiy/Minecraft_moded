package dev.mcmoded.undeadrising.network;

import dev.mcmoded.undeadrising.UndeadRising;
import dev.mcmoded.undeadrising.entity.flying.RidableFlyingMobEntity;
import dev.mcmoded.undeadrising.entity.flying.VespaEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Supplier;

/// Сетевой канал мода (оригинал: Fish's Undead Rising (Fish), `mod_LavaCow.NETWORK` + `message/MessageMountSpecial`).
/// Пока один пакет — «особая способность ездового» (клиент → сервер).
public final class FurNetwork {
    private FurNetwork() {}

    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(UndeadRising.MOD_ID, "main"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    public static void register() {
        CHANNEL.registerMessage(0, MountSpecial.class, MountSpecial::encode, MountSpecial::decode, MountSpecial::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    /// Всадник нажал «особую способность». Сервер проверяет, что отправитель управляет этим ездовым.
    public record MountSpecial(int entityId) {
        public static void encode(MountSpecial msg, FriendlyByteBuf buf) {
            buf.writeVarInt(msg.entityId);
        }

        public static MountSpecial decode(FriendlyByteBuf buf) {
            return new MountSpecial(buf.readVarInt());
        }

        public static void handle(MountSpecial msg, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player == null) return;
                if (player.level().getEntity(msg.entityId) instanceof RidableFlyingMobEntity mount && mount.isRidingPlayer(player)
                        && mount.abilityCooldown == 0) {
                    if (mount instanceof VespaEntity vespa) {
                        // Удар жалом: сам удар — в VespaEntity#tick на 14-м тике анимации.
                        vespa.setAttackTimer(20);
                        vespa.abilityCooldown = vespa.abilityCooldown();
                        vespa.level().broadcastEntityEvent(vespa, (byte) 4);
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
