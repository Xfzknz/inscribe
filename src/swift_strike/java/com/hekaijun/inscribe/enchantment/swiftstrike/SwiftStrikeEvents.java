package com.hekaijun.inscribe.enchantment.swiftstrike;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.core.BookOfferHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SwiftStrikeEvents {
    private static final UUID ATTACK_SPEED_ID = UUID.fromString("5b1f8c3e-2a9d-4f6b-8e7c-1d3a5f7b9c20");
    private static final String ATTACK_SPEED_NAME = "inscribe:swift_strike";
    private static final Map<UUID, Float> DRAW_PROGRESS = new HashMap<>();

    private SwiftStrikeEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed == null) {
            return;
        }

        ItemStack held = player.getMainHandItem();
        int level = isMelee(held)
                ? held.getEnchantmentLevel(SwiftStrikeModule.SWIFT_STRIKE.get())
                : 0;
        AttributeModifier current = attackSpeed.getModifier(ATTACK_SPEED_ID);

        if (level <= 0) {
            if (current != null) {
                attackSpeed.removeModifier(ATTACK_SPEED_ID);
            }
            return;
        }

        double amount = level * 0.10D;
        if (current == null || current.getAmount() != amount) {
            if (current != null) {
                attackSpeed.removeModifier(ATTACK_SPEED_ID);
            }
            attackSpeed.addTransientModifier(new AttributeModifier(
                    ATTACK_SPEED_ID,
                    ATTACK_SPEED_NAME,
                    amount,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }
    }

    @SubscribeEvent
    public static void onStartUse(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player) {
            DRAW_PROGRESS.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onUseItemTick(LivingEntityUseItemEvent.Tick event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack stack = event.getItem();
        if (!(stack.getItem() instanceof BowItem) && !(stack.getItem() instanceof CrossbowItem)) {
            DRAW_PROGRESS.remove(player.getUUID());
            return;
        }

        int level = stack.getEnchantmentLevel(SwiftStrikeModule.SWIFT_STRIKE.get());
        if (level <= 0) {
            DRAW_PROGRESS.remove(player.getUUID());
            return;
        }

        UUID id = player.getUUID();
        float progress = DRAW_PROGRESS.getOrDefault(id, 0.0F) + 0.10F * level;
        if (progress >= 1.0F) {
            progress -= 1.0F;
            int duration = event.getDuration();
            if (duration > 1) {
                event.setDuration(duration - 1);
            }
        }
        DRAW_PROGRESS.put(id, progress);
    }

    @SubscribeEvent
    public static void onStopUse(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof Player player) {
            DRAW_PROGRESS.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onFinishUse(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            DRAW_PROGRESS.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        DRAW_PROGRESS.remove(event.getEntity().getUUID());
    }

    private static boolean isMelee(ItemStack stack) {
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof TridentItem;
    }
}
