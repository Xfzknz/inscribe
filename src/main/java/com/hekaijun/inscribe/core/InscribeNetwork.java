package com.hekaijun.inscribe.core;

import java.util.function.Supplier;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.client.ClientHealthShake;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class InscribeNetwork {
    private static final String PROTOCOL = "1";

    @SuppressWarnings("deprecation")
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Inscribe.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    public static void register() {
        CHANNEL.messageBuilder(ShakeHealthPacket.class, 0)
                .encoder(ShakeHealthPacket::encode)
                .decoder(ShakeHealthPacket::decode)
                .consumerMainThread(ShakeHealthPacket::handle)
                .add();
    }

    public static void sendShake(ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ShakeHealthPacket());
    }

    public record ShakeHealthPacket() {
        public static void encode(ShakeHealthPacket message, FriendlyByteBuf buffer) {
        }

        public static ShakeHealthPacket decode(FriendlyByteBuf buffer) {
            return new ShakeHealthPacket();
        }

        public static void handle(ShakeHealthPacket message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() ->
                    DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT,
                            () -> ClientHealthShake::start));
            context.get().setPacketHandled(true);
        }
    }

    private InscribeNetwork() {
    }
}
