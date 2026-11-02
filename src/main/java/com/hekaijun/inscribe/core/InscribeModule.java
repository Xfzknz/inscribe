package com.hekaijun.inscribe.core;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.eventbus.api.IEventBus;

public interface InscribeModule {
    String id();

    void register(IEventBus modBus);

    default List<LootPoolEntryContainer.Builder<?>> lootEntries(ResourceLocation table) {
        return List.of();
    }
}
