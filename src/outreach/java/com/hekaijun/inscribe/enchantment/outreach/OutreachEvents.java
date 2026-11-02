package com.hekaijun.inscribe.enchantment.outreach;

import java.util.List;
import java.util.UUID;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.core.BookOfferHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class OutreachEvents {
    private static final UUID REACH_MODIFIER_ID = UUID.fromString("8f2f1d3a-6c1b-4e5a-9d7c-2b8a4f6e1c90");
    private static final String REACH_MODIFIER_NAME = "inscribe:outreach";

    private OutreachEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        AttributeInstance reach = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (reach == null) {
            return;
        }

        ItemStack held = player.getMainHandItem();
        int level = held.getEnchantmentLevel(OutreachModule.OUTREACH.get());
        AttributeModifier current = reach.getModifier(REACH_MODIFIER_ID);

        if (level <= 0) {
            if (current != null) {
                reach.removeModifier(REACH_MODIFIER_ID);
            }
            return;
        }

        if (current == null || current.getAmount() != level) {
            if (current != null) {
                reach.removeModifier(REACH_MODIFIER_ID);
            }
            reach.addTransientModifier(new AttributeModifier(
                    REACH_MODIFIER_ID,
                    REACH_MODIFIER_NAME,
                    level,
                    AttributeModifier.Operation.ADDITION
            ));
        }
    }

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        // 旧钓鱼获取途径已移除：统一走战利品箱 + 附魔台。
    }
}
