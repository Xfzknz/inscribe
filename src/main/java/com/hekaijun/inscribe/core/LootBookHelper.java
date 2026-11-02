package com.hekaijun.inscribe.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraftforge.registries.ForgeRegistries;

public final class LootBookHelper {
    public static final ResourceLocation END_CITY = new ResourceLocation("minecraft", "chests/end_city_treasure");
    public static final float LOOT_CHANCE = 0.15F;

    public static boolean isLootChest(ResourceLocation table) {
        return "minecraft".equals(table.getNamespace())
                && table.getPath().startsWith("chests/")
                && !table.equals(END_CITY);
    }

    @SuppressWarnings("deprecation") // 1.20.1 仍用 NBT；SetNbtFunction.setTag 是唯一现成的 StoredEnchantments 写法
    public static LootPoolEntryContainer.Builder<?> bookEntry(Enchantment enchantment, int level, int weight) {
        return LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                .apply(SetNbtFunction.setTag(bookTag(enchantment, level)))
                .setWeight(weight);
    }

    private static CompoundTag bookTag(Enchantment enchantment, int level) {
        CompoundTag entry = new CompoundTag();
        entry.putString("id", ForgeRegistries.ENCHANTMENTS.getKey(enchantment).toString());
        entry.putShort("lvl", (short) level);

        ListTag list = new ListTag();
        list.add(entry);

        CompoundTag tag = new CompoundTag();
        tag.put("StoredEnchantments", list);
        return tag;
    }

    private LootBookHelper() {
    }
}
