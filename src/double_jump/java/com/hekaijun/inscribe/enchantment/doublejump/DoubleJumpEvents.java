package com.hekaijun.inscribe.enchantment.doublejump;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.core.BookOfferHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DoubleJumpEvents {
    private static final Set<UUID> USED = new HashSet<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        if (player.onGround()) {
            USED.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        USED.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        USED.remove(event.getOriginal().getUUID());
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        USED.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }

        int level = player.getItemBySlot(EquipmentSlot.FEET)
                .getEnchantmentLevel(DoubleJumpModule.DOUBLE_JUMP.get());
        if (level <= 0) {
            return;
        }

        float distance = event.getDistance();
        if (distance <= 5.0F) {
            event.setDamageMultiplier(0.0F);
        } else {
            // 5 格以内免伤，超过 5 格按 (distance - 5) 计算。
            event.setDistance(distance - 2.0F);
        }
    }

    public static void tryDoubleJump(ServerPlayer player, double motionX, double motionZ) {
        UUID id = player.getUUID();
        if (USED.contains(id)) {
            return;
        }
        if (player.onGround() || player.isInWater() || player.isInLava() || player.isPassenger()
                || player.getAbilities().flying || player.isSpectator()
                || player.onClimbable() || player.isDeadOrDying()) {
            return;
        }

        int level = player.getItemBySlot(EquipmentSlot.FEET)
                .getEnchantmentLevel(DoubleJumpModule.DOUBLE_JUMP.get());
        if (level <= 0) {
            return;
        }

        // 二段跳优先于鞘翅：如果同 tick 原版已先开始滑翔，先停下再跳。
        if (player.isFallFlying()) {
            player.stopFallFlying();
        }

        double velocity = level >= 2 ? 0.58D : 0.42D;
        if (!Double.isFinite(motionX) || !Double.isFinite(motionZ)) {
            return;
        }

        // 客户端发来的水平速度做上限保护，再保留动量并额外 +30%。
        double speed = Math.sqrt(motionX * motionX + motionZ * motionZ);
        double maxSpeed = 1.0D;
        if (speed > maxSpeed) {
            double scale = maxSpeed / speed;
            motionX *= scale;
            motionZ *= scale;
        }

        player.setDeltaMovement(motionX * 1.30D, velocity, motionZ * 1.30D);
        player.hurtMarked = true;
        player.resetFallDistance();
        USED.add(id);
    }

    private DoubleJumpEvents() {
    }
}
