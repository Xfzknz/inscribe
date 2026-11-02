package com.hekaijun.inscribe.enchantment.unscathed;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import com.hekaijun.inscribe.Inscribe;

public class UnscathedEnchantment extends Enchantment {
    public UnscathedEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinCost(int level) {
        return 20;
    }

    @Override
    public int getMaxCost(int level) {
        return 40;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return isChestplate(stack) || stack.getItem() instanceof EnchantedBookItem;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return isChestplate(stack);
    }

    @Override
    public String getDescriptionId() {
        return "enchantment." + Inscribe.MODID + ".unscathed";
    }

    private static boolean isChestplate(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armor
                && armor.getEquipmentSlot() == EquipmentSlot.CHEST;
    }
}
