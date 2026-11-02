package com.hekaijun.inscribe.enchantment.doublejump;

import com.hekaijun.inscribe.Inscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DoubleJumpClient {
    private static boolean used;
    private static boolean suppressElytra;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && suppressElytra) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.options.keyJump.setDown(false);
            }
            suppressElytra = false;
        }
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.onGround()) {
            used = false;
        }
    }

    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        if (event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null) {
            return;
        }
        if (event.getKey() != mc.options.keyJump.getKey().getValue()) {
            return;
        }
        if (mc.player.onGround()) {
            used = false;
            return;
        }
        if (used || !canDoubleJump(mc.player)) {
            return;
        }

        int level = mc.player.getItemBySlot(EquipmentSlot.FEET)
                .getEnchantmentLevel(DoubleJumpModule.DOUBLE_JUMP.get());
        if (level <= 0) {
            return;
        }

        boolean hasElytra = mc.player.getItemBySlot(EquipmentSlot.CHEST).canElytraFly(mc.player);
        var motion = mc.player.getDeltaMovement();
        used = true;
        if (hasElytra) {
            // 阻止原版在同一 tick 发 START_FALL_FLYING，二段跳优先。
            suppressElytra = true;
            mc.options.keyJump.setDown(false);
        }

        // 本地先给一次 30% 水平加速，服务端包随后以同一数值权威同步。
        mc.player.setDeltaMovement(motion.x * 1.30D, motion.y, motion.z * 1.30D);
        DoubleJumpNetwork.CHANNEL.sendToServer(new DoubleJumpPacket((float) motion.x, (float) motion.z));
    }

    private static boolean canDoubleJump(LocalPlayer player) {
        return !player.isInWater() && !player.isInLava() && !player.isPassenger()
                && !player.getAbilities().flying && !player.isSpectator()
                && !player.onClimbable() && !player.isDeadOrDying();
    }

    private DoubleJumpClient() {
    }
}
