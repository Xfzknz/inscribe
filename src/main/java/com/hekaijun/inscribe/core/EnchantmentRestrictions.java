package com.hekaijun.inscribe.core;

import java.util.HashMap;
import java.util.Map;

import com.hekaijun.inscribe.Inscribe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 统一限制所有 inscribe 附魔的目标物品。
 *
 * <p>原因：铁砧处理附魔书时不会可靠地按目标物品调用 canEnchant，导致书本可以贴到裤子等
 * 非法装备上。这里在铁砧事件和玩家背包层各兜一次底，避免每个附魔模块单独写守卫。</p>
 */
@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EnchantmentRestrictions {
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.isEmpty() || right.isEmpty() || left.getItem() instanceof EnchantedBookItem) {
            return;
        }

        if (hasInvalidOn(left) || hasInvalidFor(left, right)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END
                || !(event.player instanceof ServerPlayer)
                || event.player.tickCount % 20 != 0) {
            return;
        }
        ServerPlayer player = (ServerPlayer) event.player;
        sanitize(player.getInventory().items);
        sanitize(player.getInventory().armor);
        sanitize(player.getInventory().offhand);
    }

    private static boolean hasInvalidOn(ItemStack stack) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments(stack).entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (isInscribe(enchantment) && !enchantment.canEnchant(stack)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasInvalidFor(ItemStack target, ItemStack source) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments(source).entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (isInscribe(enchantment) && !enchantment.canEnchant(target)) {
                return true;
            }
        }
        return false;
    }

    private static void sanitize(Iterable<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (stack.isEmpty() || stack.getItem() instanceof EnchantedBookItem) {
                continue;
            }

            Map<Enchantment, Integer> enchantments = new HashMap<>(enchantments(stack));
            boolean removed = enchantments.entrySet().removeIf(entry ->
                    isInscribe(entry.getKey()) && !entry.getKey().canEnchant(stack));
            if (removed) {
                EnchantmentHelper.setEnchantments(enchantments, stack);
            }
        }
    }

    private static Map<Enchantment, Integer> enchantments(ItemStack stack) {
        Map<Enchantment, Integer> result = new HashMap<>(EnchantmentHelper.getEnchantments(stack));
        if (stack.getItem() instanceof EnchantedBookItem) {
            ListTag list = EnchantedBookItem.getEnchantments(stack);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag tag = list.getCompound(i);
                ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
                if (id == null) {
                    continue;
                }
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(id);
                if (enchantment != null) {
                    result.put(enchantment, (int) tag.getShort("lvl"));
                }
            }
        }
        return result;
    }

    private static boolean isInscribe(Enchantment enchantment) {
        ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        return id != null && Inscribe.MODID.equals(id.getNamespace());
    }

    private EnchantmentRestrictions() {
    }
}
