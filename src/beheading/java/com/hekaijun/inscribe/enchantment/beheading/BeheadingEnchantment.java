package com.hekaijun.inscribe.enchantment.beheading;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.ForgeRegistries;

import com.hekaijun.inscribe.Inscribe;

public class BeheadingEnchantment extends Enchantment {
    private static final ResourceLocation IMMOLATION =
            new ResourceLocation(Inscribe.MODID, "immolation");

    public BeheadingEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
        return stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof EnchantedBookItem;
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
    public boolean checkCompatibility(Enchantment other) {
        ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(other);
        return (id == null || !id.equals(IMMOLATION)) && super.checkCompatibility(other);
    }

    @Override
    public String getDescriptionId() {
        return "enchantment." + Inscribe.MODID + ".beheading";
    }
}
