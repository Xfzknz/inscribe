package com.hekaijun.inscribe.core;

import java.util.HashMap;
import java.util.Map;

import com.hekaijun.inscribe.Inscribe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 低于“高位阈值”的操作完全交还原版；高位才接管成本与20条上限。
 */
@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AnvilCostRules {
    private static final int MAX_ENCHANTMENTS = 20;
    private static final int HIGH_COUNT = 8;
    private static final int HIGH_BASE_COST = 30;
    private static final String HIGH_STEP = "inscribe_anvil_high_step";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.isCanceled()) {
            return;
        }

        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.isEmpty() || right.isEmpty()
                || left.is(Items.ENCHANTED_BOOK) || !right.is(Items.ENCHANTED_BOOK)) {
            return;
        }

        ItemStack output = left.copy();
        Map<Enchantment, Integer> existing = new HashMap<>(EnchantmentHelper.getEnchantments(output));
        int added = 0;

        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(right).entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            if (!enchantment.canEnchant(output)) {
                continue;
            }
            boolean compatible = true;
            for (Enchantment old : existing.keySet()) {
                if (old != enchantment && !old.isCompatibleWith(enchantment)) {
                    compatible = false;
                    break;
                }
            }
            if (!compatible) {
                continue;
            }
            int oldLevel = existing.getOrDefault(enchantment, 0);
            if (level > oldLevel) {
                existing.put(enchantment, level);
                added++;
            }
        }

        if (added <= 0) {
            // 同等级/更低等级的书不应再次写入同一条附魔。
            cancel(event);
            return;
        }
        if (existing.size() > MAX_ENCHANTMENTS) {
            cancel(event);
            return;
        }

        // 重写整张表，清除旧版本可能留下的同 ID 重复条目。
        EnchantmentHelper.setEnchantments(existing, output);

        // 低位完全走原版；同等级/更低等级已在上方取消，升级交给原版处理。
        if (existing.size() < HIGH_COUNT && event.getCost() < HIGH_BASE_COST) {
            return;
        }

        boolean renamed = event.getName() != null
                && !event.getName().isBlank()
                && !event.getName().equals(left.getHoverName().getString());
        if (renamed) {
            output.setHoverName(Component.literal(event.getName()));
        }

        int tagStep = Math.max(0, left.getOrCreateTag().getInt(HIGH_STEP));
        int countStep = Math.max(0, existing.size() - HIGH_COUNT);
        int step = Math.max(tagStep, countStep);
        int cost = 41 + 4 * step + (renamed ? 1 : 0);
        output.getOrCreateTag().putInt(HIGH_STEP, step + 1);

        event.setOutput(output);
        event.setCost(cost);
        event.setMaterialCost(0);
    }

    private static void cancel(AnvilUpdateEvent event) {
        event.setOutput(ItemStack.EMPTY);
        event.setCost(0);
        event.setMaterialCost(0);
        event.setCanceled(true);
    }

    private AnvilCostRules() {
    }
}
