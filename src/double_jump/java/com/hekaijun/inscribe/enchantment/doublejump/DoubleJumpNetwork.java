package com.hekaijun.inscribe.enchantment.doublejump;

import com.hekaijun.inscribe.Inscribe;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class DoubleJumpNetwork {
    private static final String PROTOCOL = "2";

    @SuppressWarnings("deprecation") // 1.20.1 的 SimpleChannel 入口
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Inscribe.MODID, "double_jump"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    public static void register() {
        CHANNEL.messageBuilder(DoubleJumpPacket.class, 0)
                .encoder(DoubleJumpPacket::encode)
                .decoder(DoubleJumpPacket::decode)
                .consumerMainThread(DoubleJumpPacket::handle)
                .add();
    }

    private DoubleJumpNetwork() {
    }
}
