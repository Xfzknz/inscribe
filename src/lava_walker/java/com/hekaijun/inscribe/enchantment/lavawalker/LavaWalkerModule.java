package com.hekaijun.inscribe.enchantment.lavawalker;

import java.util.List;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.core.InscribeModule;
import com.hekaijun.inscribe.core.LootBookHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class LavaWalkerModule implements InscribeModule {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Inscribe.MODID);
    public static final RegistryObject<Enchantment> LAVA_WALKER =
            ENCHANTMENTS.register("lava_walker", LavaWalkerEnchantment::new);

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Inscribe.MODID);
    public static final RegistryObject<Block> LAVA_SHELL = BLOCKS.register(
            "lava_shell",
            () -> new LavaShellBlock(BlockBehaviour.Properties.of()
                    .strength(0.5F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .noLootTable())
    );

    @Override
    public String id() {
        return "lava_walker";
    }

    @Override
    public void register(IEventBus modBus) {
        ENCHANTMENTS.register(modBus);
        BLOCKS.register(modBus);
    }

    @Override
    public List<LootPoolEntryContainer.Builder<?>> lootEntries(ResourceLocation table) {
        return List.of(
                LootBookHelper.bookEntry(LAVA_WALKER.get(), 1, 3),
                LootBookHelper.bookEntry(LAVA_WALKER.get(), 2, 1)
        );
    }
}
