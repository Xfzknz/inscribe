package com.hekaijun.inscribe.client;

import com.hekaijun.inscribe.Inscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 服务端已经允许 ≥40 级的线性成本；客户端把原版“过于昂贵”文字覆盖成正常成本。
 */
@Mod.EventBusSubscriber(modid = Inscribe.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AnvilCostOverlay {
    private static final int IMAGE_WIDTH = 176;
    private static final int IMAGE_HEIGHT = 166;

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof AnvilScreen screen)) {
            return;
        }
        if (!screen.getMenu().getSlot(2).hasItem()) {
            return;
        }

        int cost = screen.getMenu().getCost();
        if (cost < 40) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        int left = (screen.width - IMAGE_WIDTH) / 2;
        int top = (screen.height - IMAGE_HEIGHT) / 2;
        int y = top + 69;
        Component text = Component.translatable("container.repair.cost", cost);

        int right = left + IMAGE_WIDTH - 8;
        int textX = right - minecraft.font.width(text) - 2;

        // 成本可能远大于原“过于昂贵”文本；整行擦除，避免左侧残留旧底框。
        event.getGuiGraphics().fill(left + 8, top + 67, right, top + 79, 0xFFC6C6C6);
        event.getGuiGraphics().fill(textX - 2, top + 67, right, top + 79, 0x4F000000);
        event.getGuiGraphics().drawString(
                minecraft.font,
                text,
                textX,
                y,
                player.experienceLevel >= cost ? 0xFF80FF20 : 0xFFFF5555,
                false
        );
    }

    private AnvilCostOverlay() {
    }
}
