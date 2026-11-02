package com.hekaijun.inscribe.core;

import java.util.List;

import com.hekaijun.inscribe.Inscribe;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LootInjection {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (!LootBookHelper.isLootChest(event.getName())) {
            return;
        }

        List<LootPoolEntryContainer.Builder<?>> entries = ModuleLoader.modules().stream()
                .flatMap(module -> module.lootEntries(event.getName()).stream())
                .toList();
        if (entries.isEmpty()) {
            return;
        }

        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
        for (LootPoolEntryContainer.Builder<?> entry : entries) {
            pool.add(entry);
        }
        pool.when(LootItemRandomChanceCondition.randomChance(LootBookHelper.LOOT_CHANCE));
        event.getTable().addPool(pool.build());
        LOGGER.info("Added {} enchanted-book entries to loot table {}", entries.size(), event.getName());
    }

    private LootInjection() {
    }
}
