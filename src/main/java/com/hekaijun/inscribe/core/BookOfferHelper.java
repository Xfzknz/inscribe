package com.hekaijun.inscribe.core;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;

public final class BookOfferHelper {
    public static MerchantOffer create(Enchantment enchantment, RandomSource random) {
        return create(enchantment, random, 2);
    }

    public static MerchantOffer create(Enchantment enchantment, RandomSource random, int maxLevel) {
        ItemStack book = EnchantedBookItem.createForEnchantment(
                new EnchantmentInstance(enchantment, 1 + random.nextInt(maxLevel))
        );
        int price = 20 + random.nextInt(13);
        return new MerchantOffer(
                new ItemStack(Items.EMERALD, price),
                ItemStack.EMPTY,
                book,
                3,
                15,
                0.05F
        );
    }

    private BookOfferHelper() {
    }
}
