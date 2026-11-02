package com.hekaijun.inscribe.enchantment.hunt;

import java.util.List;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.core.InscribeModule;
import com.hekaijun.inscribe.core.LootBookHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class HuntModule implements InscribeModule {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Inscribe.MODID);
    public static final RegistryObject<Enchantment> HUNT =
            ENCHANTMENTS.register("hunt", HuntEnchantment::new);

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Inscribe.MODID);
    public static final RegistryObject<MobEffect> HUNT_EFFECT =
            EFFECTS.register("hunt", HuntEffect::new);

    @Override
    public String id() {
        return "hunt";
    }

    @Override
    public void register(IEventBus modBus) {
        ENCHANTMENTS.register(modBus);
        EFFECTS.register(modBus);
        HuntNetwork.register();
    }

    @Override
    public List<LootPoolEntryContainer.Builder<?>> lootEntries(ResourceLocation table) {
        return List.of(
                LootBookHelper.bookEntry(HUNT.get(), 1, 3),
                LootBookHelper.bookEntry(HUNT.get(), 2, 2),
                LootBookHelper.bookEntry(HUNT.get(), 3, 1)
        );
    }
}
