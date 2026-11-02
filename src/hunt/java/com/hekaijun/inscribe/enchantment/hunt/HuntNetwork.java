package com.hekaijun.inscribe.enchantment.hunt;

import java.util.function.Supplier;

import com.hekaijun.inscribe.Inscribe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class HuntNetwork {
    private static final String PROTOCOL = "1";

    @SuppressWarnings("deprecation")
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Inscribe.MODID, "hunt"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    public static void register() {
        CHANNEL.messageBuilder(HuntMissPacket.class, 0)
                .encoder(HuntMissPacket::encode)
                .decoder(HuntMissPacket::decode)
                .consumerMainThread(HuntMissPacket::handle)
                .add();
    }

    public static void sendMiss() {
        CHANNEL.sendToServer(new HuntMissPacket());
    }

    public record HuntMissPacket() {
        public static void encode(HuntMissPacket message, FriendlyByteBuf buffer) {
        }

        public static HuntMissPacket decode(FriendlyByteBuf buffer) {
            return new HuntMissPacket();
        }

        public static void handle(HuntMissPacket message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                ServerPlayer player = context.get().getSender();
                if (player != null) {
                    HuntEvents.reset(player);
                }
            });
            context.get().setPacketHandled(true);
        }
    }

    private HuntNetwork() {
    }
}
