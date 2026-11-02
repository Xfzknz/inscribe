package com.hekaijun.inscribe.client;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.enchantment.lifesiphon.LifeSiphonModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 网络包失效时的客户端兜底：手持生命汲取武器且生命值上升时触发原版心形抖动。
 */
@Mod.EventBusSubscriber(modid = Inscribe.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientLifeSiphonShake {
    private static float lastHealth = -1.0F;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            lastHealth = -1.0F;
            return;
        }

        float health = player.getHealth();
        if (lastHealth >= 0.0F && health > lastHealth + 1.0E-4F
                && player.getMainHandItem()
                .getEnchantmentLevel(LifeSiphonModule.LIFE_SIPHON.get()) > 0) {
            ClientHealthShake.start();
        }
        lastHealth = health;
    }

    private ClientLifeSiphonShake() {
    }
}
