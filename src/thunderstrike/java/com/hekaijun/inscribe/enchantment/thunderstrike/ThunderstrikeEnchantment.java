package com.hekaijun.inscribe.enchantment.thunderstrike;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import com.hekaijun.inscribe.Inscribe;

public class ThunderstrikeEnchantment extends Enchantment {
    private static final EnchantmentCategory CATEGORY = EnchantmentCategory.create(
            "inscribe_thunderstrike",
            item -> item instanceof SwordItem
    );

    public ThunderstrikeEnchantment() {
        super(Rarity.RARE, CATEGORY, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinCost(int level) {
        return 20 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof EnchantedBookItem;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public String getDescriptionId() {
        return "enchantment." + Inscribe.MODID + ".thunderstrike";
    }
}
