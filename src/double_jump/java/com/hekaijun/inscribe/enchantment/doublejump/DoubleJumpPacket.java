package com.hekaijun.inscribe.enchantment.doublejump;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public final class DoubleJumpPacket {
    private final float motionX;
    private final float motionZ;

    public DoubleJumpPacket(float motionX, float motionZ) {
        this.motionX = motionX;
        this.motionZ = motionZ;
    }

    public static void encode(DoubleJumpPacket message, FriendlyByteBuf buffer) {
        buffer.writeFloat(message.motionX);
        buffer.writeFloat(message.motionZ);
    }

    public static DoubleJumpPacket decode(FriendlyByteBuf buffer) {
        return new DoubleJumpPacket(buffer.readFloat(), buffer.readFloat());
    }

    public static void handle(DoubleJumpPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                DoubleJumpEvents.tryDoubleJump(player, message.motionX, message.motionZ);
            }
        });
        context.setPacketHandled(true);
    }
}
