package com.hekaijun.inscribe.enchantment.doublejump;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import com.hekaijun.inscribe.Inscribe;

public class DoubleJumpEnchantment extends Enchantment {
    public DoubleJumpEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinCost(int level) {
        return 30 + (level - 1) * 15;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return isBoots(stack) || stack.getItem() instanceof EnchantedBookItem;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return isBoots(stack);
    }

    private static boolean isBoots(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armor
                && armor.getEquipmentSlot() == EquipmentSlot.FEET;
    }

    @Override
    public String getDescriptionId() {
        return "enchantment." + Inscribe.MODID + ".double_jump";
    }
}
