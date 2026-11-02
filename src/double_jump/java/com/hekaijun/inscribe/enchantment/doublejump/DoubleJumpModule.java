package com.hekaijun.inscribe.enchantment.doublejump;

import java.util.List;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.core.InscribeModule;
import com.hekaijun.inscribe.core.LootBookHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class DoubleJumpModule implements InscribeModule {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Inscribe.MODID);
    public static final RegistryObject<Enchantment> DOUBLE_JUMP =
            ENCHANTMENTS.register("double_jump", DoubleJumpEnchantment::new);

    @Override
    public String id() {
        return "double_jump";
    }

    @Override
    public void register(IEventBus modBus) {
        ENCHANTMENTS.register(modBus);
        DoubleJumpNetwork.register();
    }

    @Override
    public List<LootPoolEntryContainer.Builder<?>> lootEntries(ResourceLocation table) {
        return List.of(
                LootBookHelper.bookEntry(DOUBLE_JUMP.get(), 1, 3),
                LootBookHelper.bookEntry(DOUBLE_JUMP.get(), 2, 1)
        );
    }
}
