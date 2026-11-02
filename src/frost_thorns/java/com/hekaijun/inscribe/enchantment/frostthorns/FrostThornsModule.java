package com.hekaijun.inscribe.enchantment.frostthorns;

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

public final class FrostThornsModule implements InscribeModule {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Inscribe.MODID);
    public static final RegistryObject<Enchantment> FROST_THORNS =
            ENCHANTMENTS.register("frost_thorns", FrostThornsEnchantment::new);

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Inscribe.MODID);
    public static final RegistryObject<MobEffect> FROST_THORNS_EFFECT =
            EFFECTS.register("frost_thorns", FrostThornsEffect::new);

    @Override
    public String id() {
        return "frost_thorns";
    }

    @Override
    public void register(IEventBus modBus) {
        ENCHANTMENTS.register(modBus);
        EFFECTS.register(modBus);
    }

    @Override
    public List<LootPoolEntryContainer.Builder<?>> lootEntries(ResourceLocation table) {
        return List.of(
                LootBookHelper.bookEntry(FROST_THORNS.get(), 1, 3),
                LootBookHelper.bookEntry(FROST_THORNS.get(), 2, 2),
                LootBookHelper.bookEntry(FROST_THORNS.get(), 3, 1)
        );
    }
}
