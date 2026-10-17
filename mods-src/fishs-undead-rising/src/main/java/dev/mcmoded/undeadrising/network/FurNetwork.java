package dev.mcmoded.undeadrising.network;

import dev.mcmoded.undeadrising.UndeadRising;
import dev.mcmoded.undeadrising.entity.SalamanderEntity;
import dev.mcmoded.undeadrising.entity.flying.RidableFlyingMobEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;
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
/// Пока один пакет — «особая способность ездового» (клиент → сервер): жало Vespa, залп саламандры.
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
                if (player.level().getEntity(msg.entityId) instanceof SalamanderEntity salamander && salamander.isRidingPlayer(player)
                        && salamander.barrageCD == 0) {
                    salamanderBarrage(salamander, player);
                    return;
                }
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

        /// Залп саламандры: веер из 8 малых огненных шаров по взгляду всадника (перезарядка 4 с).
        private static void salamanderBarrage(SalamanderEntity salamander, ServerPlayer player) {
            salamander.barrageCD = 80;
            Vec3 look = player.getLookAngle();
            var random = salamander.getRandom();
            for (int i = 0; i < 8; i++) {
                SmallFireball ball = new SmallFireball(salamander.level(), salamander,
                        look.x * (7.0 + random.nextGaussian() * 2.0),
                        look.y * (-1.0 + random.nextGaussian() * 3.0) - 0.25,
                        look.z * (7.0 + random.nextGaussian() * 2.0));
                ball.setPos(salamander.getX() + look.x * 2.0, salamander.getY() + salamander.getBbHeight() / 2.0F + 1.5, salamander.getZ() + look.z * 2.0);
                salamander.level().addFreshEntity(ball);
            }
            salamander.level().playSound(null, salamander.getX(), salamander.getY(), salamander.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS,
                    1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F));
        }
    }
}
